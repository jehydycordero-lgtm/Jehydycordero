package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AddonDatabase
import com.example.data.model.AddonCategory
import com.example.data.model.AddonEntity
import com.example.data.model.AddonSource
import com.example.data.model.SortOption
import com.example.data.pack.LaunchResult
import com.example.data.pack.McAddonPackager
import com.example.data.repository.AddonRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    data class Ready(
        val allAddons: List<AddonEntity>,
        val filteredAddons: List<AddonEntity>,
        val featuredAddons: List<AddonEntity>,
        val marketplaceAddons: List<AddonEntity>,
        val downloadedAddons: List<AddonEntity>,
        val favoriteAddons: List<AddonEntity>,
        val searchQuery: String,
        val selectedSource: AddonSource?,
        val selectedCategory: AddonCategory,
        val sortOption: SortOption,
        val installingAddonId: String?
    ) : CatalogUiState
}

class AddonHubViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AddonRepository by lazy {
        val db = AddonDatabase.getInstance(application)
        AddonRepository(db.addonDao(), McAddonPackager(application))
    }

    private val _searchQuery = MutableStateFlow("")
    private val _selectedSource = MutableStateFlow<AddonSource?>(null)
    private val _selectedCategory = MutableStateFlow(AddonCategory.ALL)
    private val _sortOption = MutableStateFlow(SortOption.FEATURED)
    private val _installingAddonId = MutableStateFlow<String?>(null)
    private val _isSeeded = MutableStateFlow(false)

    private val _snackbarEvents = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    private val _manifestPreview = MutableStateFlow<Pair<String, String>?>(null)
    val manifestPreview: StateFlow<Pair<String, String>?> = _manifestPreview.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            _isSeeded.value = true
        }
    }

    private data class FilterParams(
        val query: String,
        val source: AddonSource?,
        val category: AddonCategory,
        val sort: SortOption,
        val installingId: String?,
        val seeded: Boolean
    )

    private val filterParams = combine(
        _searchQuery,
        _selectedSource,
        _selectedCategory,
        _sortOption,
        _installingAddonId
    ) { query, source, category, sort, installingId ->
        FilterParams(query, source, category, sort, installingId, true)
    }.combine(_isSeeded) { params, seeded ->
        params.copy(seeded = seeded)
    }

    val uiState: StateFlow<CatalogUiState> = combine(
        repository.allAddons,
        filterParams
    ) { addons, params ->
        if (!params.seeded && addons.isEmpty()) {
            return@combine CatalogUiState.Loading
        }

        val queryLower = params.query.trim().lowercase()
        val filtered = addons.filter { addon ->
            val matchesQuery = queryLower.isEmpty() ||
                addon.title.lowercase().contains(queryLower) ||
                addon.creator.lowercase().contains(queryLower) ||
                addon.shortTagline.lowercase().contains(queryLower) ||
                addon.category.label.lowercase().contains(queryLower)
            val matchesSource = params.source == null || addon.source == params.source
            val matchesCategory = params.category == AddonCategory.ALL || addon.category == params.category
            matchesQuery && matchesSource && matchesCategory
        }.let { list ->
            when (params.sort) {
                SortOption.FEATURED -> list.sortedWith(
                    compareByDescending<AddonEntity> { it.isFeatured }.thenByDescending { it.rating }
                )
                SortOption.RATING -> list.sortedByDescending { it.rating }
                SortOption.FREE_FIRST -> list.sortedWith(
                    compareByDescending<AddonEntity> { it.isFreeInApp || it.minecoins == 0 }
                        .thenBy { it.minecoins }
                )
                SortOption.MINECOINS_ASC -> list.sortedWith(
                    compareBy<AddonEntity> { it.minecoins }.thenByDescending { it.rating }
                )
            }
        }

        CatalogUiState.Ready(
            allAddons = addons,
            filteredAddons = filtered,
            featuredAddons = addons.filter { it.isFeatured },
            marketplaceAddons = addons.filter { it.source == AddonSource.MARKETPLACE },
            downloadedAddons = addons.filter { it.isDownloaded }.sortedByDescending { it.installedAt ?: 0L },
            favoriteAddons = addons.filter { it.isFavorite },
            searchQuery = params.query,
            selectedSource = params.source,
            selectedCategory = params.category,
            sortOption = params.sort,
            installingAddonId = params.installingId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState.Loading
    )

    fun observeAddon(addonId: String): Flow<AddonEntity?> = repository.observeAddon(addonId)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectSource(source: AddonSource?) {
        _selectedSource.value = source
    }

    fun selectCategory(category: AddonCategory) {
        _selectedCategory.value = category
    }

    fun selectSort(sortOption: SortOption) {
        _sortOption.value = sortOption
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedSource.value = null
        _selectedCategory.value = AddonCategory.ALL
        _sortOption.value = SortOption.FEATURED
    }

    fun toggleFavorite(addon: AddonEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(addon)
            val msg = if (!addon.isFavorite) {
                "${addon.title} guardado en Favoritos"
            } else {
                "${addon.title} eliminado de Favoritos"
            }
            _snackbarEvents.tryEmit(msg)
        }
    }

    fun downloadAddon(addon: AddonEntity) {
        if (_installingAddonId.value != null) return
        viewModelScope.launch {
            _installingAddonId.value = addon.id
            delay(550)
            runCatching {
                repository.downloadAndPackageAddon(addon)
            }.onSuccess { file ->
                _snackbarEvents.tryEmit("Paquete ${file.name} listo en Mi Bóveda")
            }.onFailure {
                _snackbarEvents.tryEmit("No se pudo generar el archivo .mcaddon")
            }
            _installingAddonId.value = null
        }
    }

    fun openInMinecraft(addon: AddonEntity) {
        when (repository.openInMinecraft(addon)) {
            LaunchResult.OpenedMinecraft ->
                _snackbarEvents.tryEmit("Abriendo ${addon.title} en Minecraft Bedrock...")
            LaunchResult.OpenedChooser ->
                _snackbarEvents.tryEmit("Selecciona Minecraft Bedrock para importar el paquete")
            LaunchResult.NoHandler ->
                _snackbarEvents.tryEmit("Instala Minecraft Bedrock para importar archivos .mcaddon")
            LaunchResult.FileMissing ->
                _snackbarEvents.tryEmit("Archivo no encontrado. Descarga el paquete nuevamente.")
        }
    }

    fun shareAddon(addon: AddonEntity) {
        if (!repository.shareAddon(addon)) {
            _snackbarEvents.tryEmit("No hay aplicación disponible para compartir el archivo")
        }
    }

    fun removeDownloadedAddon(addon: AddonEntity) {
        viewModelScope.launch {
            repository.removeDownloadedAddon(addon)
            _snackbarEvents.tryEmit("${addon.title} eliminado de descargas")
        }
    }

    fun openMarketplace(addon: AddonEntity) {
        if (!repository.openMarketplace(addon)) {
            _snackbarEvents.tryEmit("No se pudo abrir el enlace del Marketplace")
        }
    }

    fun getManifestPreview(addon: AddonEntity): String = repository.inspectManifest(addon)

    fun showManifestPreview(addon: AddonEntity) {
        val json = repository.inspectManifest(addon)
        _manifestPreview.value = addon.title to json
    }

    fun dismissManifestPreview() {
        _manifestPreview.value = null
    }
}
