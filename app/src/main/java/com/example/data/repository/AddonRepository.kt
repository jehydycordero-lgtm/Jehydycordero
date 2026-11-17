package com.example.data.repository

import com.example.data.local.AddonDao
import com.example.data.local.AddonSeedData
import com.example.data.model.AddonEntity
import com.example.data.pack.LaunchResult
import com.example.data.pack.McAddonPackager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class AddonRepository(
    private val addonDao: AddonDao,
    private val packager: McAddonPackager
) {
    val allAddons: Flow<List<AddonEntity>> = addonDao.observeAllAddons()

    fun observeAddon(addonId: String): Flow<AddonEntity?> = addonDao.observeAddonById(addonId)

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        if (addonDao.countAddons() == 0) {
            addonDao.insertAll(AddonSeedData.initialAddons)
        }
    }

    suspend fun toggleFavorite(addon: AddonEntity) = withContext(Dispatchers.IO) {
        addonDao.setFavorite(addon.id, !addon.isFavorite)
    }

    suspend fun downloadAndPackageAddon(addon: AddonEntity): File = withContext(Dispatchers.IO) {
        val file = packager.buildPackFile(addon)
        addonDao.updateDownloadState(
            addonId = addon.id,
            isDownloaded = true,
            localFilePath = file.absolutePath,
            installedAt = System.currentTimeMillis()
        )
        file
    }

    suspend fun removeDownloadedAddon(addon: AddonEntity) = withContext(Dispatchers.IO) {
        packager.deletePackFile(addon.localFilePath)
        addonDao.updateDownloadState(
            addonId = addon.id,
            isDownloaded = false,
            localFilePath = null,
            installedAt = null
        )
    }

    fun inspectManifest(addon: AddonEntity): String = packager.inspectManifestPreview(addon)

    fun openInMinecraft(addon: AddonEntity): LaunchResult {
        val path = addon.localFilePath ?: return LaunchResult.FileMissing
        return packager.openPackInMinecraft(File(path))
    }

    fun shareAddon(addon: AddonEntity): Boolean {
        val path = addon.localFilePath ?: return false
        return packager.sharePack(File(path))
    }

    fun openMarketplace(addon: AddonEntity): Boolean =
        packager.openMarketplaceUrl(addon.marketplaceUrl)
}
