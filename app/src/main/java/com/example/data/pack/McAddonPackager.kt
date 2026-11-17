package com.example.data.pack

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.model.AddonEntity
import com.example.data.model.PackFormat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class McAddonPackager(private val context: Context) {

    fun buildPackFile(addon: AddonEntity): File {
        val addonsDir = File(context.filesDir, "addons").apply { mkdirs() }
        val safeName = addon.id.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val ext = addon.packFormat.extension
        val outputFile = File(addonsDir, "${safeName}.$ext")

        val bpHeaderUuid = deterministicUuid("${addon.id}_bp_header")
        val bpModuleUuid = deterministicUuid("${addon.id}_bp_module")
        val rpHeaderUuid = deterministicUuid("${addon.id}_rp_header")
        val rpModuleUuid = deterministicUuid("${addon.id}_rp_module")
        val iconBytes = loadPackIconPngBytes()

        ZipOutputStream(FileOutputStream(outputFile)).use { zip ->
            if (addon.packFormat == PackFormat.MCPACK) {
                writeZipEntry(
                    zip,
                    "manifest.json",
                    buildResourceManifestJson(addon, rpHeaderUuid, rpModuleUuid, null)
                )
                writeZipBytes(zip, "pack_icon.png", iconBytes)
                writeZipEntry(zip, "texts/es_ES.lang", buildLangFile(addon))
                writeZipEntry(zip, "texts/en_US.lang", buildLangFile(addon))
            } else {
                // Standard .mcaddon bundle with Behavior Pack + Resource Pack
                writeZipEntry(
                    zip,
                    "behavior_pack/manifest.json",
                    buildBehaviorManifestJson(addon, bpHeaderUuid, bpModuleUuid, rpHeaderUuid)
                )
                writeZipBytes(zip, "behavior_pack/pack_icon.png", iconBytes)
                writeZipEntry(
                    zip,
                    "behavior_pack/recipes/bedrockhub_token_recipe.json",
                    buildStarterRecipeJson(addon)
                )

                writeZipEntry(
                    zip,
                    "resource_pack/manifest.json",
                    buildResourceManifestJson(addon, rpHeaderUuid, rpModuleUuid, bpHeaderUuid)
                )
                writeZipBytes(zip, "resource_pack/pack_icon.png", iconBytes)
                writeZipEntry(zip, "resource_pack/texts/es_ES.lang", buildLangFile(addon))
                writeZipEntry(zip, "resource_pack/texts/en_US.lang", buildLangFile(addon))
            }
        }
        return outputFile
    }

    fun inspectManifestPreview(addon: AddonEntity): String {
        val bpHeaderUuid = deterministicUuid("${addon.id}_bp_header")
        val bpModuleUuid = deterministicUuid("${addon.id}_bp_module")
        val rpHeaderUuid = deterministicUuid("${addon.id}_rp_header")
        val rpModuleUuid = deterministicUuid("${addon.id}_rp_module")
        return if (addon.packFormat == PackFormat.MCPACK) {
            buildResourceManifestJson(addon, rpHeaderUuid, rpModuleUuid, null)
        } else {
            buildBehaviorManifestJson(addon, bpHeaderUuid, bpModuleUuid, rpHeaderUuid)
        }
    }

    fun openPackInMinecraft(file: File): LaunchResult {
        if (!file.exists()) return LaunchResult.FileMissing
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/octet-stream")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setPackage("com.mojang.minecraftpe")
        }
        return try {
            context.startActivity(viewIntent)
            LaunchResult.OpenedMinecraft
        } catch (_: ActivityNotFoundException) {
            val genericChooser = Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/octet-stream"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                },
                "Importar ${file.name} en Minecraft"
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(genericChooser)
                LaunchResult.OpenedChooser
            } catch (_: ActivityNotFoundException) {
                LaunchResult.NoHandler
            }
        }
    }

    fun sharePack(file: File): Boolean {
        if (!file.exists()) return false
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            },
            "Compartir ${file.name}"
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(shareIntent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    fun openMarketplaceUrl(url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    fun deletePackFile(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching { File(path).delete() }
    }

    private fun buildBehaviorManifestJson(
        addon: AddonEntity,
        headerUuid: String,
        moduleUuid: String,
        linkedRpUuid: String
    ): String {
        val cleanDesc = addon.shortTagline.replace("\"", "'")
        return """
            {
              "format_version": 2,
              "header": {
                "name": "${addon.title} [BP]",
                "description": "$cleanDesc",
                "uuid": "$headerUuid",
                "version": [1, 0, 0],
                "min_engine_version": [1, 21, 50]
              },
              "modules": [
                {
                  "description": "BedrockHub Behavior Module",
                  "type": "data",
                  "uuid": "$moduleUuid",
                  "version": [1, 0, 0]
                }
              ],
              "dependencies": [
                {
                  "uuid": "$linkedRpUuid",
                  "version": [1, 0, 0]
                }
              ]
            }
        """.trimIndent()
    }

    private fun buildResourceManifestJson(
        addon: AddonEntity,
        headerUuid: String,
        moduleUuid: String,
        linkedBpUuid: String?
    ): String {
        val cleanDesc = addon.shortTagline.replace("\"", "'")
        val dependencyBlock = if (linkedBpUuid != null) {
            """,
              "dependencies": [
                {
                  "uuid": "$linkedBpUuid",
                  "version": [1, 0, 0]
                }
              ]"""
        } else {
            ""
        }
        return """
            {
              "format_version": 2,
              "header": {
                "name": "${addon.title} [RP]",
                "description": "$cleanDesc",
                "uuid": "$headerUuid",
                "version": [1, 0, 0],
                "min_engine_version": [1, 21, 50]
              },
              "modules": [
                {
                  "description": "BedrockHub Resource Module",
                  "type": "resources",
                  "uuid": "$moduleUuid",
                  "version": [1, 0, 0]
                }
              ]$dependencyBlock
            }
        """.trimIndent()
    }

    private fun buildStarterRecipeJson(addon: AddonEntity): String {
        val safeId = addon.id.replace(Regex("[^a-z0-9_]"), "_")
        return """
            {
              "format_version": "1.20.10",
              "minecraft:recipe_shaped": {
                "description": {
                  "identifier": "bedrockhub:${safeId}_guide_emerald"
                },
                "tags": ["crafting_table"],
                "pattern": [
                  " G ",
                  "GBG",
                  " G "
                ],
                "key": {
                  "G": { "item": "minecraft:emerald" },
                  "B": { "item": "minecraft:book" }
                },
                "unlock": [
                  { "item": "minecraft:book" }
                ],
                "result": {
                  "item": "minecraft:written_book"
                }
              }
            }
        """.trimIndent()
    }

    private fun buildLangFile(addon: AddonEntity): String {
        return "pack.name=${addon.title}\npack.description=${addon.shortTagline}\n"
    }

    private fun loadPackIconPngBytes(): ByteArray {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_art)
        val scaled = Bitmap.createScaledBitmap(bitmap, 128, 128, true)
        val stream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.PNG, 90, stream)
        if (scaled != bitmap) scaled.recycle()
        bitmap.recycle()
        return stream.toByteArray()
    }

    private fun writeZipEntry(zip: ZipOutputStream, name: String, content: String) {
        writeZipBytes(zip, name, content.toByteArray(Charsets.UTF_8))
    }

    private fun writeZipBytes(zip: ZipOutputStream, name: String, bytes: ByteArray) {
        zip.putNextEntry(ZipEntry(name))
        zip.write(bytes)
        zip.closeEntry()
    }

    private fun deterministicUuid(seed: String): String =
        UUID.nameUUIDFromBytes(seed.toByteArray(Charsets.UTF_8)).toString()
}

enum class LaunchResult {
    OpenedMinecraft,
    OpenedChooser,
    NoHandler,
    FileMissing
}
