package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AddonEntity
import com.example.ui.components.PackFormatBadge
import com.example.ui.components.SourcePriceBadge
import com.example.ui.components.addonArtworkRes
import com.example.ui.components.categoryAccentColor
import com.example.ui.theme.LocalBedrockAccents

@Composable
fun AddonDetailScreen(
    addon: AddonEntity,
    isInstalling: Boolean,
    manifestPreviewJson: String,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDownload: () -> Unit,
    onOpenInMinecraft: () -> Unit,
    onShare: () -> Unit,
    onOpenMarketplace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    var showManifestCode by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            DetailBottomActionBar(
                addon = addon,
                isInstalling = isInstalling,
                onDownload = onDownload,
                onOpenInMinecraft = onOpenInMinecraft,
                onShare = onShare,
                onOpenMarketplace = onOpenMarketplace,
                onToggleFavorite = onToggleFavorite
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 900.dp)
                    .align(Alignment.TopCenter),
                contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding() + 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        Image(
                            painter = painterResource(id = addonArtworkRes(addon.artworkKey)),
                            contentDescription = addon.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.45f),
                                            Color.Black.copy(alpha = 0.25f),
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.55f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                            ) {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("detail_back_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back_content_desc),
                                        tint = Color.White
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.55f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                            ) {
                                IconButton(
                                    onClick = onToggleFavorite,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("detail_fav_btn")
                                ) {
                                    Icon(
                                        imageVector = if (addon.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = stringResource(R.string.favorite_content_desc),
                                        tint = if (addon.isFavorite) accents.redstone else Color.White
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SourcePriceBadge(addon = addon)
                                PackFormatBadge(formatLabel = addon.packFormat.label)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = categoryAccentColor(addon.category).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = addon.category.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = categoryAccentColor(addon.category),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = addon.title,
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Por ${addon.creator} • ${addon.downloadsLabel} descargas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecTile(
                            label = "Bedrock",
                            value = addon.bedrockVersion,
                            accent = accents.freeEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        SpecTile(
                            label = "Valoración",
                            value = "${addon.rating} ★",
                            accent = accents.minecoinGold,
                            modifier = Modifier.weight(1f)
                        )
                        SpecTile(
                            label = "Tamaño",
                            value = addon.fileSizeMb,
                            accent = accents.diamondCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = addon.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "¿Qué incluye este Addon?",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        addon.featuresList.forEach { feature ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, accents.borderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = accents.freeEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = feature,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, accents.borderSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Guía de instalación en Bedrock",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            addon.installStepsList.forEachIndexed { index, step ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = accents.amethystContainer,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                if (addon.isFreeInApp) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { showManifestCode = !showManifestCode },
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, accents.borderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Code,
                                            contentDescription = null,
                                            tint = accents.diamondCyan
                                        )
                                        Text(
                                            text = "Inspeccionar manifest.json del paquete",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Icon(
                                        imageVector = if (showManifestCode) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                AnimatedVisibility(visible = showManifestCode) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.background,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp)
                                    ) {
                                        Text(
                                            text = manifestPreviewJson,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = accents.freeEmerald,
                                            modifier = Modifier.padding(12.dp)
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
}

@Composable
private fun SpecTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, accents.borderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = accent
            )
        }
    }
}

@Composable
private fun DetailBottomActionBar(
    addon: AddonEntity,
    isInstalling: Boolean,
    onDownload: () -> Unit,
    onOpenInMinecraft: () -> Unit,
    onShare: () -> Unit,
    onOpenMarketplace: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val accents = LocalBedrockAccents.current
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, accents.borderSubtle),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (addon.isFreeInApp) {
                if (addon.isDownloaded) {
                    Button(
                        onClick = onOpenInMinecraft,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("detail_open_mc_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accents.freeEmerald,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.cta_open_minecraft), style = MaterialTheme.typography.labelLarge)
                    }
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("detail_share_btn"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, accents.borderSubtle)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(R.string.cta_share_pack)
                        )
                    }
                } else {
                    Button(
                        onClick = onDownload,
                        enabled = !isInstalling,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("detail_download_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accents.freeEmerald,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isInstalling) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generando ${addon.packFormat.label}...", style = MaterialTheme.typography.labelLarge)
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Descargar ${addon.packFormat.label} Gratis",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            } else {
                Button(
                    onClick = onOpenMarketplace,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("detail_marketplace_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (addon.minecoins == 0) accents.diamondCyan else accents.minecoinGold,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Storefront,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (addon.minecoins == 0) {
                            "Reclamar Gratis en Marketplace"
                        } else {
                            "Ver en Marketplace (${addon.minecoins} MC)"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                OutlinedButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, accents.borderSubtle)
                ) {
                    Icon(
                        imageVector = if (addon.isFavorite) Icons.Filled.Favorite else Icons.Filled.Star,
                        contentDescription = stringResource(R.string.favorite_content_desc),
                        tint = if (addon.isFavorite) accents.redstone else accents.minecoinGold
                    )
                }
            }
        }
    }
}
