package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AddonCategory
import com.example.data.model.AddonEntity
import com.example.data.model.AddonSource
import com.example.ui.components.AddonCatalogCard
import com.example.ui.components.AddonFeaturedCard
import com.example.ui.components.categoryAccentColor
import com.example.ui.components.categoryIcon
import com.example.ui.theme.LocalBedrockAccents
import com.example.ui.viewmodel.CatalogUiState

@Composable
fun ExploreScreen(
    state: CatalogUiState.Ready,
    onSearchChange: (String) -> Unit,
    onSelectSource: (AddonSource?) -> Unit,
    onSelectCategory: (AddonCategory) -> Unit,
    onResetFilters: () -> Unit,
    onAddonClick: (AddonEntity) -> Unit,
    onToggleFavorite: (AddonEntity) -> Unit,
    onQuickAction: (AddonEntity) -> Unit,
    onNavigateToMarketplace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = when {
            maxWidth >= 900.dp -> 3
            maxWidth >= 560.dp -> 2
            else -> 1
        }
        val chunkedAddons = state.filteredAddons.chunked(columns)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1100.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BEDROCKHUB",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Marketplace & Addons Gratis 1.21+",
                                style = MaterialTheme.typography.bodyMedium,
                                color = accents.freeEmerald
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, accents.borderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(accents.freeEmerald)
                                )
                                Text(
                                    text = "${state.allAddons.size} Packs",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("explore_search_input"),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = accents.freeEmerald
                            )
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchChange("") },
                                    modifier = Modifier.testTag("clear_search_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = stringResource(R.string.clear_search_desc)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = accents.freeEmerald,
                            unfocusedBorderColor = accents.borderSubtle
                        )
                    )
                }
            }

            if (state.searchQuery.isBlank() && state.selectedSource == null && state.selectedCategory == AddonCategory.ALL) {
                item {
                    HeroPortalBanner(
                        freeCount = state.allAddons.count { it.isFreeInApp || it.minecoins == 0 },
                        marketplaceCount = state.marketplaceAddons.size,
                        onExploreFree = { onSelectSource(AddonSource.FREE_IN_APP) },
                        onOpenMarketplace = onNavigateToMarketplace,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SourcePillChip(
                            label = "Todos",
                            selected = state.selectedSource == null,
                            accent = accents.freeEmerald,
                            onClick = { onSelectSource(null) },
                            tag = "source_chip_all",
                            modifier = Modifier.weight(1f)
                        )
                        SourcePillChip(
                            label = "Gratis App",
                            selected = state.selectedSource == AddonSource.FREE_IN_APP,
                            accent = accents.freeEmerald,
                            onClick = { onSelectSource(AddonSource.FREE_IN_APP) },
                            tag = "source_chip_free",
                            modifier = Modifier.weight(1f)
                        )
                        SourcePillChip(
                            label = "Marketplace",
                            selected = state.selectedSource == AddonSource.MARKETPLACE,
                            accent = accents.minecoinGold,
                            onClick = { onSelectSource(AddonSource.MARKETPLACE) },
                            tag = "source_chip_marketplace",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(AddonCategory.entries) { category ->
                            val selected = state.selectedCategory == category
                            val accent = categoryAccentColor(category)
                            Surface(
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSelectCategory(category) }
                                    .testTag("category_chip_${category.name}"),
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) accent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (selected) accent else accents.borderSubtle
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = categoryIcon(category),
                                        contentDescription = null,
                                        tint = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = category.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state.searchQuery.isBlank() && state.selectedCategory == AddonCategory.ALL && state.selectedSource == null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.section_featured),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Top 1.21+",
                                style = MaterialTheme.typography.labelMedium,
                                color = accents.amethyst
                            )
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.featuredAddons, key = { it.id }) { addon ->
                                AddonFeaturedCard(
                                    addon = addon,
                                    onClick = { onAddonClick(addon) },
                                    onToggleFavorite = { onToggleFavorite(addon) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.section_catalog),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${state.filteredAddons.size} resultados",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (chunkedAddons.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, accents.borderSubtle)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SearchOff,
                                contentDescription = null,
                                tint = accents.amethyst,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = stringResource(R.string.empty_search_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.empty_search_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = onResetFilters,
                                modifier = Modifier.testTag("reset_filters_btn")
                            ) {
                                Text("Mostrar todos los addons")
                            }
                        }
                    }
                }
            } else {
                items(chunkedAddons, key = { row -> row.joinToString("_") { it.id } }) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { addon ->
                            AddonCatalogCard(
                                addon = addon,
                                isInstalling = state.installingAddonId == addon.id,
                                onClick = { onAddonClick(addon) },
                                onToggleFavorite = { onToggleFavorite(addon) },
                                onQuickAction = { onQuickAction(addon) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(columns - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroPortalBanner(
    freeCount: Int,
    marketplaceCount: Int,
    onExploreFree: () -> Unit,
    onOpenMarketplace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, accents.freeEmerald.copy(alpha = 0.5f)),
        tonalElevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(216.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_bedrock),
                contentDescription = "Banner de BedrockHub",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.72f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .offset(y = 2.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, accents.amethyst.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = accents.amethyst,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "$freeCount GRATIS • $marketplaceCount MKT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(0.84f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Todos tus Addons de Bedrock en un solo portal",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = "Instala paquetes .mcaddon gratuitos al instante o explora el catálogo oficial del Marketplace.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onExploreFree,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("hero_free_cta"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accents.freeEmerald,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gratis en App", style = MaterialTheme.typography.labelLarge)
                    }
                    Surface(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onOpenMarketplace)
                            .testTag("hero_marketplace_cta"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, accents.minecoinGold.copy(alpha = 0.65f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = null,
                                tint = accents.minecoinGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Marketplace",
                                style = MaterialTheme.typography.labelLarge,
                                color = accents.minecoinGold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourcePillChip(
    label: String,
    selected: Boolean,
    accent: Color,
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
        color = if (selected) accent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) accent else accents.borderSubtle)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
