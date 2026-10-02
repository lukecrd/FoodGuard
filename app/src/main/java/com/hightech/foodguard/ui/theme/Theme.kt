package com.hightech.foodguard.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CheerfulColorScheme = lightColorScheme(
    primary = SkyDeep,
    onPrimary = Color.White,
    primaryContainer = SkySoft,
    onPrimaryContainer = SkyInk,
    secondary = BerryDeep,
    onSecondary = Color.White,
    secondaryContainer = BerrySoft,
    onSecondaryContainer = BerryInk,
    tertiary = LeafDeep,
    onTertiary = Color.White,
    tertiaryContainer = LeafSoft,
    onTertiaryContainer = LeafInk,
    background = FunBackground,
    onBackground = Ink,
    surface = FunSurface,
    onSurface = Ink,
    surfaceVariant = FunSurfaceSoft,
    onSurfaceVariant = InkSoft,
    outline = InkMuted,
    outlineVariant = FunBorder,
    error = TomatoDeep,
    onError = Color.White,
    errorContainer = TomatoSoft,
    onErrorContainer = TomatoInk
)

// Forme morbide e "paffute"
private val CheerfulShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun FoodGuardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CheerfulColorScheme,
        typography = FoodGuardTypography,
        shapes = CheerfulShapes,
        content = content
    )
}
