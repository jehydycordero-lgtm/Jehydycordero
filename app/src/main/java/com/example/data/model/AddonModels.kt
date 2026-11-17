package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AddonSource(val label: String) {
    FREE_IN_APP("Gratis en App"),
    MARKETPLACE("Marketplace")
}

enum class AddonCategory(val label: String) {
    ALL("Todos"),
    MOBS_RPG("Mobs y RPG"),
    FURNITURE("Muebles"),
    SURVIVAL("Supervivencia"),
    SHADERS("Shaders y VFX"),
    TECH_REDSTONE("Tech y Redstone"),
    WEAPONS("Armas y Combate"),
    WORLDS("Mundos")
}

enum class PackFormat(val label: String, val extension: String) {
    MCADDON(".MCADDON", "mcaddon"),
    MCPACK(".MCPACK", "mcpack"),
    MCWORLD(".MCWORLD", "mcworld")
}

enum class SortOption(val label: String) {
    FEATURED("Destacados"),
    RATING("Mejor valorados"),
    FREE_FIRST("Gratis primero"),
    MINECOINS_ASC("Menor precio")
}

@Entity(tableName = "addons")
data class AddonEntity(
    @PrimaryKey val id: String,
    val title: String,
    val creator: String,
    val source: AddonSource,
    val category: AddonCategory,
    val packFormat: PackFormat,
    val minecoins: Int,
    val isFreeInApp: Boolean,
    val isFeatured: Boolean,
    val rating: Float,
    val downloadsLabel: String,
    val bedrockVersion: String,
    val fileSizeMb: String,
    val artworkKey: String,
    val shortTagline: String,
    val description: String,
    val featuresCsv: String,
    val installStepsCsv: String,
    val marketplaceUrl: String,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val installedAt: Long? = null
) {
    val featuresList: List<String>
        get() = featuresCsv.split("|").filter { it.isNotBlank() }

    val installStepsList: List<String>
        get() = installStepsCsv.split("|").filter { it.isNotBlank() }
}
