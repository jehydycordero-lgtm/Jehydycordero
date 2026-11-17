package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AddonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddonDao {
    @Query("SELECT * FROM addons ORDER BY isFeatured DESC, rating DESC")
    fun observeAllAddons(): Flow<List<AddonEntity>>

    @Query("SELECT * FROM addons WHERE id = :addonId LIMIT 1")
    fun observeAddonById(addonId: String): Flow<AddonEntity?>

    @Query("SELECT * FROM addons WHERE id = :addonId LIMIT 1")
    suspend fun getAddonById(addonId: String): AddonEntity?

    @Query("SELECT COUNT(*) FROM addons")
    suspend fun countAddons(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(addons: List<AddonEntity>)

    @Query("UPDATE addons SET isFavorite = :isFavorite WHERE id = :addonId")
    suspend fun setFavorite(addonId: String, isFavorite: Boolean)

    @Query(
        "UPDATE addons SET isDownloaded = :isDownloaded, localFilePath = :localFilePath, installedAt = :installedAt WHERE id = :addonId"
    )
    suspend fun updateDownloadState(
        addonId: String,
        isDownloaded: Boolean,
        localFilePath: String?,
        installedAt: Long?
    )
}
