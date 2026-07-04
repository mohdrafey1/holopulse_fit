package com.lumastride.holopulsefit.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies the signature HoloPulse Fit neon edge: a soft colored outer glow (via a tinted shadow)
 * plus a thin gradient border in the same hue. Colored shadows tint on API 28+, and fall back to a
 * neutral elevation shadow below that, so the effect degrades gracefully.
 *
 * @param glowColor hue of the glow and border, always a palette token.
 * @param cornerRadius corner radius of the surface this decorates.
 * @param glowRadius shadow elevation that controls how far the glow spreads.
 */
fun Modifier.neonGlow(
    glowColor: Color,
    cornerRadius: Dp = 20.dp,
    glowRadius: Dp = 14.dp,
    borderWidth: Dp = 1.dp,
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .shadow(
            elevation = glowRadius,
            shape = shape,
            ambientColor = glowColor.copy(alpha = 0.6f),
            spotColor = glowColor.copy(alpha = 0.9f),
        )
        .border(
            width = borderWidth,
            brush = Brush.linearGradient(
                listOf(glowColor.copy(alpha = 0.7f), glowColor.copy(alpha = 0.12f)),
            ),
            shape = shape,
        )
}
