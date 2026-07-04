package com.lumastride.holopulsefit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * HoloPulse Fit is an always dark, neon themed app. There is no light variant: the design system
 * is built around the deep space base with electric blue, cyan, and violet glow accents.
 */
private val HoloDarkColorScheme = darkColorScheme(
    primary = ElectricBlueGlow,
    onPrimary = DeepSpaceBase,
    primaryContainer = CardSurfaceElevated,
    onPrimaryContainer = SoftWhiteText,
    secondary = CyanPulse,
    onSecondary = DeepSpaceBase,
    tertiary = VioletEnergy,
    onTertiary = SoftWhiteText,
    background = DeepSpaceBase,
    onBackground = SoftWhiteText,
    surface = CardSurface,
    onSurface = SoftWhiteText,
    surfaceVariant = CardSurfaceElevated,
    onSurfaceVariant = DisabledText,
    error = WarningPulse,
    onError = DeepSpaceBase,
    outline = DividerBlue,
    outlineVariant = DividerBlue,
    scrim = ScrimDark,
)

/**
 * Root theme wrapper. Every screen is hosted inside this so Material components pick up the neon
 * color scheme and [HoloTypography]. Neon specific tokens (glow shadows, aura colors) are imported
 * directly from [Color.kt].
 */
@Composable
fun HoloPulseFitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HoloDarkColorScheme,
        typography = HoloTypography,
        content = content,
    )
}
