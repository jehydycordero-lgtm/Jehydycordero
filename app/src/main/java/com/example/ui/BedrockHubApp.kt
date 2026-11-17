package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.data.model.AddonEntity
import com.example.ui.components.VoxelGlowBackdrop
import com.example.ui.screens.AddonDetailScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.ManifestPreviewDialog
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.LocalBedrockAccents
import com.example.ui.viewmodel.AddonHubViewModel
import com.example.ui.viewmodel.CatalogUiState

private const val ROUTE_EXPLORE = "explore"
private const val ROUTE_MARKETPLACE = "marketplace"
private const val ROUTE_VAULT = "vault"
private const val ROUTE_DETAIL = "detail/{addonId}"

private data class TopDestination(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topDestinations = listOf(
    TopDestination(
        route = ROUTE_EXPLORE,
        labelRes = R.string.nav_explore,
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        testTag = "nav_explore"
    ),
    TopDestination(
        route = ROUTE_MARKETPLACE,
        labelRes = R.string.nav_marketplace,
        selectedIcon = Icons.Filled.Storefront,
        unselectedIcon = Icons.Outlined.Storefront,
        testTag = "nav_marketplace"
    ),
    TopDestination(
        route = ROUTE_VAULT,
        labelRes = R.string.nav_vault,
        selectedIcon = Icons.Filled.FolderZip,
        unselectedIcon = Icons.Outlined.FolderZip,
        testTag = "nav_vault"
    )
)

@Composable
fun BedrockHubApp(
    viewModel: AddonHubViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val manifestPreview by viewModel.manifestPreview.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showTopLevelNav = currentRoute in topDestinations.map { it.route }
    val accents = LocalBedrockAccents.current

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    manifestPreview?.let { (title, json) ->
        ManifestPreviewDialog(
            title = title,
            manifestJson = json,
            onDismiss = { viewModel.dismissManifestPreview() }
        )
    }

    val navigateToAddonDetail: (AddonEntity) -> Unit = { addon ->
        navController.navigate("detail/${addon.id}")
    }

    val handleQuickAction: (AddonEntity) -> Unit = { addon ->
        when {
            addon.isFreeInApp && addon.isDownloaded -> viewModel.openInMinecraft(addon)
            addon.isFreeInApp -> viewModel.downloadAddon(addon)
            else -> navigateToAddonDetail(addon)
        }
    }

    val navigateTopLevel: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(ROUTE_EXPLORE) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        VoxelGlowBackdrop(modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showTopLevelNav && !useNavRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                        tonalElevation = 6.dp
                    ) {
                        topDestinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopLevel(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(dest.labelRes),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = accents.freeEmerald,
                                    selectedTextColor = accents.freeEmerald,
                                    indicatorColor = accents.freeEmeraldContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                if (showTopLevelNav && useNavRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        topDestinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopLevel(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(dest.labelRes),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = accents.freeEmerald,
                                    selectedTextColor = accents.freeEmerald,
                                    indicatorColor = accents.freeEmeraldContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                when (val state = uiState) {
                    is CatalogUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = accents.freeEmerald)
                        }
                    }

                    is CatalogUiState.Ready -> {
                        NavHost(
                            navController = navController,
                            startDestination = ROUTE_EXPLORE,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(ROUTE_EXPLORE) {
                                ExploreScreen(
                                    state = state,
                                    onSearchChange = viewModel::updateSearchQuery,
                                    onSelectSource = viewModel::selectSource,
                                    onSelectCategory = viewModel::selectCategory,
                                    onResetFilters = viewModel::resetFilters,
                                    onAddonClick = navigateToAddonDetail,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onQuickAction = handleQuickAction,
                                    onNavigateToMarketplace = { navigateTopLevel(ROUTE_MARKETPLACE) },
                                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                                )
                            }

                            composable(ROUTE_MARKETPLACE) {
                                MarketplaceScreen(
                                    state = state,
                                    onSortChange = viewModel::selectSort,
                                    onAddonClick = navigateToAddonDetail,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onQuickAction = handleQuickAction,
                                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                                )
                            }

                            composable(ROUTE_VAULT) {
                                VaultScreen(
                                    state = state,
                                    onAddonClick = navigateToAddonDetail,
                                    onOpenInMinecraft = viewModel::openInMinecraft,
                                    onShareAddon = viewModel::shareAddon,
                                    onInspectManifest = viewModel::showManifestPreview,
                                    onRemoveDownload = viewModel::removeDownloadedAddon,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onExploreClick = { navigateTopLevel(ROUTE_EXPLORE) },
                                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                                )
                            }

                            composable(
                                route = ROUTE_DETAIL,
                                arguments = listOf(navArgument("addonId") { type = NavType.StringType })
                            ) { entry ->
                                val addonId = entry.arguments?.getString("addonId").orEmpty()
                                val addon by viewModel.observeAddon(addonId)
                                    .collectAsStateWithLifecycle(initialValue = null)
                                addon?.let { item ->
                                    val manifestJson = remember(item.id) {
                                        viewModel.getManifestPreview(item)
                                    }
                                    AddonDetailScreen(
                                        addon = item,
                                        isInstalling = state.installingAddonId == item.id,
                                        manifestPreviewJson = manifestJson,
                                        onBack = { navController.popBackStack() },
                                        onToggleFavorite = { viewModel.toggleFavorite(item) },
                                        onDownload = { viewModel.downloadAddon(item) },
                                        onOpenInMinecraft = { viewModel.openInMinecraft(item) },
                                        onShare = { viewModel.shareAddon(item) },
                                        onOpenMarketplace = { viewModel.openMarketplace(item) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
