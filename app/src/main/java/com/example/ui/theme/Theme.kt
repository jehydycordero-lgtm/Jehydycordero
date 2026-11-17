package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class BedrockAccents(
    val freeEmerald: Color,
    val freeEmeraldContainer: Color,
    val minecoinGold: Color,
    val minecoinContainer: Color,
    val amethyst: Color,
    val amethystContainer: Color,
    val diamondCyan: Color,
    val diamondContainer: Color,
    val redstone: Color,
    val redstoneContainer: Color,
    val borderSubtle: Color,
    val cardElevated: Color
)

val LocalBedrockAccents = staticCompositionLocalOf {
    BedrockAccents(
        freeEmerald = EmeraldNeon,
        freeEmeraldContainer = EmeraldDark,
        minecoinGold = MinecoinGold,
        minecoinContainer = MinecoinDark,
        amethyst = AmethystGlow,
        amethystContainer = AmethystDark,
        diamondCyan = DiamondCyan,
        diamondContainer = DiamondDark,
        redstone = RedstoneCoral,
        redstoneContainer = RedstoneDark,
        borderSubtle = ObsidianBorder,
        cardElevated = ObsidianElevated
    )
}

private val BedrockDarkColorScheme = darkColorScheme(
    primary = EmeraldNeon,
    onPrimary = ObsidianDeep,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldSoft,
    secondary = AmethystGlow,
    onSecondary = Color.White,
    secondaryContainer = AmethystDark,
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = DiamondCyan,
    onTertiary = ObsidianDeep,
    tertiaryContainer = DiamondDark,
    onTertiaryContainer = Color(0xFFBAE6FD),
    background = ObsidianDeep,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianBorder,
    error = RedstoneCoral,
    onError = ObsidianDeep
)

private val BedrockLightColorScheme = lightColorScheme(
    primary = LightEmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = AmethystGlow,
    onSecondary = Color.White,
    tertiary = DiamondCyan,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFE11D48),
    onError = Color.White
)

private val BedrockShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun BedrockHubTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BedrockDarkColorScheme else BedrockLightColorScheme
    val accents = BedrockAccents(
        freeEmerald = EmeraldNeon,
        freeEmeraldContainer = EmeraldDark,
        minecoinGold = MinecoinGold,
        minecoinContainer = MinecoinDark,
        amethyst = AmethystGlow,
        amethystContainer = AmethystDark,
        diamondCyan = DiamondCyan,
        diamondContainer = DiamondDark,
        redstone = RedstoneCoral,
        redstoneContainer = RedstoneDark,
        borderSubtle = if (darkTheme) ObsidianBorder else Color(0xFFCBD5E1),
        cardElevated = if (darkTheme) ObsidianElevated else LightSurfaceVariant
    )

    CompositionLocalProvider(LocalBedrockAccents provides accents) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = BedrockShapes,
            content = content
        )
    }
}
