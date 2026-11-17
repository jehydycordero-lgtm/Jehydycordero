package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AddonEntity
import com.example.ui.components.PackFormatBadge
import com.example.ui.components.SourcePriceBadge
import com.example.ui.components.addonArtworkRes
import com.example.ui.theme.LocalBedrockAccents
import com.example.ui.viewmodel.CatalogUiState
import java.io.File

@Composable
fun VaultScreen(
    state: CatalogUiState.Ready,
    onAddonClick: (AddonEntity) -> Unit,
    onOpenInMinecraft: (AddonEntity) -> Unit,
    onShareAddon: (AddonEntity) -> Unit,
    onInspectManifest: (AddonEntity) -> Unit,
    onRemoveDownload: (AddonEntity) -> Unit,
    onToggleFavorite: (AddonEntity) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 960.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MI BÓVEDA",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Paquetes .mcaddon generados y favoritos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.FolderZip,
                            contentDescription = null,
                            tint = accents.freeEmerald,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        VaultTabPill(
                            label = "Descargados (${state.downloadedAddons.size})",
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            tag = "vault_tab_downloads",
                            modifier = Modifier.weight(1f)
                        )
                        VaultTabPill(
                            label = "Favoritos (${state.favoriteAddons.size})",
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            tag = "vault_tab_favorites",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                if (state.downloadedAddons.isEmpty()) {
                    item {
                        VaultEmptyCard(
                            icon = Icons.Filled.DownloadDone,
                            title = stringResource(R.string.empty_downloads_title),
                            description = stringResource(R.string.empty_downloads_desc),
                            ctaText = "Explorar addons gratuitos",
                            onCtaClick = onExploreClick
                        )
                    }
                } else {
                    items(state.downloadedAddons, key = { it.id }) { addon ->
                        DownloadedPackCard(
                            addon = addon,
                            onClick = { onAddonClick(addon) },
                            onOpenInMinecraft = { onOpenInMinecraft(addon) },
                            onShare = { onShareAddon(addon) },
                            onInspectManifest = { onInspectManifest(addon) },
                            onDelete = { onRemoveDownload(addon) }
                        )
                    }
                }
            } else {
                if (state.favoriteAddons.isEmpty()) {
                    item {
                        VaultEmptyCard(
                            icon = Icons.Filled.Favorite,
                            title = stringResource(R.string.empty_favorites_title),
                            description = stringResource(R.string.empty_favorites_desc),
                            ctaText = "Ir al catálogo",
                            onCtaClick = onExploreClick
                        )
                    }
                } else {
                    items(state.favoriteAddons, key = { it.id }) { addon ->
                        FavoriteItemCard(
                            addon = addon,
                            onClick = { onAddonClick(addon) },
                            onToggleFavorite = { onToggleFavorite(addon) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VaultTabPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) accents.freeEmeraldContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (selected) accents.freeEmerald else accents.borderSubtle
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) accents.freeEmerald else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DownloadedPackCard(
    addon: AddonEntity,
    onClick: () -> Unit,
    onOpenInMinecraft: () -> Unit,
    onShare: () -> Unit,
    onInspectManifest: () -> Unit,
    onDelete: () -> Unit
) {
    val accents = LocalBedrockAccents.current
    val fileName = addon.localFilePath?.let { File(it).name } ?: "${addon.id}.${addon.packFormat.extension}"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .testTag("vault_downloaded_${addon.id}"),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, accents.freeEmerald.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = addonArtworkRes(addon.artworkKey)),
                    contentDescription = addon.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PackFormatBadge(formatLabel = addon.packFormat.label)
                        Text(
                            text = addon.fileSizeMb,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = addon.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.labelSmall,
                        color = accents.freeEmerald,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("vault_delete_${addon.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Eliminar descarga",
                        tint = accents.redstone
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenInMinecraft,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("vault_open_mc_${addon.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accents.freeEmerald,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir en Minecraft", style = MaterialTheme.typography.labelLarge)
                }
                OutlinedButton(
                    onClick = onInspectManifest,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("vault_manifest_${addon.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, accents.borderSubtle)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Code,
                        contentDescription = stringResource(R.string.cta_inspect_manifest),
                        modifier = Modifier.size(18.dp)
                    )
                }
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("vault_share_${addon.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, accents.borderSubtle)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.cta_share_pack),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteItemCard(
    addon: AddonEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val accents = LocalBedrockAccents.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, accents.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = addonArtworkRes(addon.artworkKey)),
                contentDescription = addon.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SourcePriceBadge(addon = addon)
                Text(
                    text = addon.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${addon.creator} • ${addon.bedrockVersion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(R.string.favorite_content_desc),
                    tint = accents.redstone
                )
            }
        }
    }
}

@Composable
private fun VaultEmptyCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    ctaText: String,
    onCtaClick: () -> Unit
) {
    val accents = LocalBedrockAccents.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, accents.borderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accents.freeEmerald,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onCtaClick,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(ctaText, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun ManifestPreviewDialog(
    title: String,
    manifestJson: String,
    onDismiss: () -> Unit
) {
    val accents = LocalBedrockAccents.current
    val clipboard = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Code,
                    contentDescription = null,
                    tint = accents.freeEmerald
                )
                Text(
                    text = "manifest.json — $title",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, accents.borderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Text(
                    text = manifestJson,
                    style = MaterialTheme.typography.labelMedium,
                    color = accents.freeEmerald,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                clipboard.setText(AnnotatedString(manifestJson))
                onDismiss()
            }) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copiar y cerrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
