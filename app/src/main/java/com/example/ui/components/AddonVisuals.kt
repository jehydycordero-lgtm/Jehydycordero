package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AddonCategory
import com.example.data.model.AddonEntity
import com.example.data.model.AddonSource
import com.example.ui.theme.LocalBedrockAccents

@DrawableRes
fun addonArtworkRes(artworkKey: String): Int = when (artworkKey) {
    "fantasy" -> R.drawable.img_pack_fantasy
    "tech" -> R.drawable.img_pack_tech
    "shaders" -> R.drawable.img_pack_shaders
    else -> R.drawable.img_hero_bedrock
}

fun categoryIcon(category: AddonCategory): ImageVector = when (category) {
    AddonCategory.ALL -> Icons.Filled.Explore
    AddonCategory.MOBS_RPG -> Icons.Filled.Pets
    AddonCategory.FURNITURE -> Icons.Filled.Chair
    AddonCategory.SURVIVAL -> Icons.Outlined.Bolt
    AddonCategory.SHADERS -> Icons.Filled.AutoAwesome
    AddonCategory.TECH_REDSTONE -> Icons.Filled.Memory
    AddonCategory.WEAPONS -> Icons.Filled.Shield
    AddonCategory.WORLDS -> Icons.Filled.Public
}

@Composable
fun categoryAccentColor(category: AddonCategory): Color {
    val accents = LocalBedrockAccents.current
    return when (category) {
        AddonCategory.ALL -> accents.freeEmerald
        AddonCategory.MOBS_RPG -> accents.amethyst
        AddonCategory.FURNITURE -> accents.minecoinGold
        AddonCategory.SURVIVAL -> accents.freeEmerald
        AddonCategory.SHADERS -> accents.diamondCyan
        AddonCategory.TECH_REDSTONE -> accents.redstone
        AddonCategory.WEAPONS -> accents.minecoinGold
        AddonCategory.WORLDS -> accents.diamondCyan
    }
}

@Composable
fun VoxelGlowBackdrop(modifier: Modifier = Modifier) {
    val accents = LocalBedrockAccents.current
    val bg = MaterialTheme.colorScheme.background
    Canvas(modifier = modifier) {
        drawRect(color = bg)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accents.freeEmerald.copy(alpha = 0.14f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.18f, size.height * 0.08f),
                radius = size.width * 0.7f
            ),
            radius = size.width * 0.7f,
            center = Offset(size.width * 0.18f, size.height * 0.08f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accents.amethyst.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.88f, size.height * 0.18f),
                radius = size.width * 0.6f
            ),
            radius = size.width * 0.6f,
            center = Offset(size.width * 0.88f, size.height * 0.18f)
        )
        // Subtle voxel pixel accents in top-right corner
        val blockSize = 10.dp.toPx()
        val gap = 4.dp.toPx()
        val startX = size.width - (blockSize * 4 + gap * 3) - 16.dp.toPx()
        val startY = 20.dp.toPx()
        val alphaGrid = listOf(0.06f, 0.12f, 0.04f, 0.09f)
        for (i in 0..3) {
            drawRect(
                color = accents.freeEmerald.copy(alpha = alphaGrid[i]),
                topLeft = Offset(startX + i * (blockSize + gap), startY + (i % 2) * (blockSize + gap)),
                size = Size(blockSize, blockSize)
            )
        }
    }
}

@Composable
fun SourcePriceBadge(
    addon: AddonEntity,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    val (bgColor, textColor, label, icon) = when {
        addon.isFreeInApp -> Quad(
            accents.freeEmeraldContainer.copy(alpha = 0.92f),
            accents.freeEmerald,
            "GRATIS",
            Icons.Filled.Download
        )
        addon.minecoins == 0 -> Quad(
            accents.diamondContainer.copy(alpha = 0.92f),
            accents.diamondCyan,
            "GRATIS MKT",
            Icons.Filled.Storefront
        )
        else -> Quad(
            accents.minecoinContainer.copy(alpha = 0.92f),
            accents.minecoinGold,
            "${addon.minecoins} MC",
            Icons.Filled.MonetizationOn
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PackFormatBadge(
    formatLabel: String,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, accents.borderSubtle)
    ) {
        Text(
            text = formatLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun AddonFeaturedCard(
    addon: AddonEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    val borderColor = if (addon.isFreeInApp) {
        accents.freeEmerald.copy(alpha = 0.45f)
    } else {
        accents.minecoinGold.copy(alpha = 0.45f)
    }

    Surface(
        modifier = modifier
            .width(276.dp)
            .height(214.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag("featured_addon_card_${addon.id}"),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.55f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.96f)
                            )
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SourcePriceBadge(addon = addon)
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.48f)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("fav_btn_${addon.id}")
                    ) {
                        Icon(
                            imageVector = if (addon.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorite_content_desc),
                            tint = if (addon.isFavorite) accents.redstone else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PackFormatBadge(formatLabel = addon.packFormat.label)
                    Text(
                        text = addon.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryAccentColor(addon.category),
                        maxLines = 1
                    )
                }
                Text(
                    text = addon.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = addon.creator,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = accents.minecoinGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${addon.rating}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddonCatalogCard(
    addon: AddonEntity,
    isInstalling: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onQuickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    val accent = categoryAccentColor(addon.category)
    val cardScale by animateFloatAsState(
        targetValue = if (isInstalling) 0.98f else 1f,
        label = "cardScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .testTag("addon_card_${addon.id}"),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (addon.isDownloaded) accents.freeEmerald.copy(alpha = 0.65f) else accents.borderSubtle
        ),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
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
                                listOf(
                                    Color.Black.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    SourcePriceBadge(addon = addon)
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.45f)
                    ) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("card_fav_${addon.id}")
                        ) {
                            Icon(
                                imageVector = if (addon.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = stringResource(R.string.favorite_content_desc),
                                tint = if (addon.isFavorite) accents.redstone else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PackFormatBadge(formatLabel = addon.packFormat.label)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accent.copy(alpha = 0.18f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = categoryIcon(addon.category),
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = addon.category.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = accent,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = addon.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = addon.shortTagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = addon.creator,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = accents.minecoinGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${addon.rating} • ${addon.bedrockVersion}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    val btnBg = when {
                        addon.isDownloaded -> accents.freeEmeraldContainer
                        addon.isFreeInApp -> accents.freeEmerald
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val btnFg = when {
                        addon.isDownloaded -> accents.freeEmerald
                        addon.isFreeInApp -> MaterialTheme.colorScheme.onPrimary
                        else -> accents.minecoinGold
                    }

                    Surface(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = if (addon.source == AddonSource.MARKETPLACE) {
                                    accents.minecoinGold.copy(alpha = 0.4f)
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = !isInstalling, onClick = onQuickAction)
                            .testTag("quick_action_${addon.id}"),
                        shape = RoundedCornerShape(12.dp),
                        color = btnBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isInstalling) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = btnFg
                                )
                            } else {
                                Icon(
                                    imageVector = when {
                                        addon.isDownloaded -> Icons.Filled.CheckCircle
                                        addon.isFreeInApp -> Icons.Filled.Download
                                        else -> Icons.Filled.Storefront
                                    },
                                    contentDescription = null,
                                    tint = btnFg,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = when {
                                    isInstalling -> "Creando..."
                                    addon.isDownloaded -> "Abrir"
                                    addon.isFreeInApp -> "Instalar"
                                    else -> "Ficha"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = btnFg
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
