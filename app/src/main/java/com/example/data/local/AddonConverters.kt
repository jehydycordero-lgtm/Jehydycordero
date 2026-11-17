package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AddonCategory
import com.example.data.model.AddonSource
import com.example.data.model.PackFormat

class AddonConverters {
    @TypeConverter
    fun fromSource(value: AddonSource): String = value.name

    @TypeConverter
    fun toSource(value: String): AddonSource =
        AddonSource.entries.firstOrNull { it.name == value } ?: AddonSource.FREE_IN_APP

    @TypeConverter
    fun fromCategory(value: AddonCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): AddonCategory =
        AddonCategory.entries.firstOrNull { it.name == value } ?: AddonCategory.SURVIVAL

    @TypeConverter
    fun fromPackFormat(value: PackFormat): String = value.name

    @TypeConverter
    fun toPackFormat(value: String): PackFormat =
        PackFormat.entries.firstOrNull { it.name == value } ?: PackFormat.MCADDON
}
