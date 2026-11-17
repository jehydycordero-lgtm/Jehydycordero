package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.AddonEntity
import com.example.data.model.SortOption
import com.example.ui.components.AddonCatalogCard
import com.example.ui.theme.LocalBedrockAccents
import com.example.ui.viewmodel.CatalogUiState

private enum class MarketplaceTierFilter(val label: String) {
    ALL("Todos"),
    FREE_OFFICIAL("Gratis Oficial (0 MC)"),
    PREMIUM("Populares Minecoins")
}

@Composable
fun MarketplaceScreen(
    state: CatalogUiState.Ready,
    onSortChange: (SortOption) -> Unit,
    onAddonClick: (AddonEntity) -> Unit,
    onToggleFavorite: (AddonEntity) -> Unit,
    onQuickAction: (AddonEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val accents = LocalBedrockAccents.current
    var tierFilter by remember { mutableStateOf(MarketplaceTierFilter.ALL) }
    var selectedStudio by remember { mutableStateOf<String?>(null) }

    val studios = remember(state.marketplaceAddons) {
        state.marketplaceAddons.map { it.creator }.distinct()
    }

    val displayedAddons = remember(state.marketplaceAddons, tierFilter, selectedStudio, state.sortOption) {
        state.marketplaceAddons.filter { addon ->
            val matchesTier = when (tierFilter) {
                MarketplaceTierFilter.ALL -> true
                MarketplaceTierFilter.FREE_OFFICIAL -> addon.minecoins == 0
                MarketplaceTierFilter.PREMIUM -> addon.minecoins > 0
            }
            val matchesStudio = selectedStudio == null || addon.creator == selectedStudio
            matchesTier && matchesStudio
        }.let { list ->
            when (state.sortOption) {
                SortOption.FEATURED -> list.sortedWith(
                    compareByDescending<AddonEntity> { it.isFeatured }.thenByDescending { it.rating }
                )
                SortOption.RATING -> list.sortedByDescending { it.rating }
                SortOption.FREE_FIRST -> list.sortedBy { it.minecoins }
                SortOption.MINECOINS_ASC -> list.sortedBy { it.minecoins }
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = when {
            maxWidth >= 900.dp -> 3
            maxWidth >= 560.dp -> 2
            else -> 1
        }
        val chunked = displayedAddons.chunked(columns)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1100.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MARKETPLACE OFICIAL",
                                style = MaterialTheme.typography.headlineMedium,
                                color = accents.minecoinGold
                            )
                            Text(
                                text = "Estudios verificados y lanzamientos gratis oficiales",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.Storefront,
                            contentDescription = null,
                            tint = accents.minecoinGold,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = accents.minecoinContainer.copy(alpha = 0.42f),
                        border = BorderStroke(1.dp, accents.minecoinGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = null,
                                tint = accents.minecoinGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Colección oficial Bedrock",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Consulta precios en Minecoins, reclama Add-Ons gratis de Mojang y estudios asociados, o abre su ficha oficial.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MarketplaceTierFilter.entries.forEach { tier ->
                            val selected = tierFilter == tier
                            val accent = if (tier == MarketplaceTierFilter.FREE_OFFICIAL) {
                                accents.diamondCyan
                            } else {
                                accents.minecoinGold
                            }
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { tierFilter = tier }
                                    .testTag("mkt_tier_${tier.name}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) accent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, if (selected) accent else accents.borderSubtle)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                ) {
                                    Text(
                                        text = tier.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Icon(
                                imageVector = Icons.Filled.Sort,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        items(
                            listOf(
                                SortOption.FEATURED,
                                SortOption.RATING,
                                SortOption.MINECOINS_ASC
                            )
                        ) { sort ->
                            val selected = state.sortOption == sort
                            Surface(
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSortChange(sort) }
                                    .testTag("mkt_sort_${sort.name}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) accents.amethystContainer else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) accents.amethyst else accents.borderSubtle
                                )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                ) {
                                    Text(
                                        text = sort.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            StudioFilterChip(
                                name = "Todos los estudios",
                                selected = selectedStudio == null,
                                onClick = { selectedStudio = null }
                            )
                        }
                        items(studios) { studio ->
                            StudioFilterChip(
                                name = studio,
                                selected = selectedStudio == studio,
                                onClick = {
                                    selectedStudio = if (selectedStudio == studio) null else studio
                                }
                            )
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
                        text = "${displayedAddons.size} Add-Ons del Marketplace",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MonetizationOn,
                            contentDescription = null,
                            tint = accents.minecoinGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "MC = Minecoins",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(chunked, key = { row -> row.joinToString("_") { it.id } }) { rowItems ->
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

@Composable
private fun StudioFilterChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accents = LocalBedrockAccents.current
    Surface(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) accents.minecoinGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) accents.minecoinGold else accents.borderSubtle)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) accents.minecoinGold else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
