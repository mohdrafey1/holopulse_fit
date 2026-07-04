package com.lumastride.holopulsefit.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow

/**
 * Rounded dark card with a soft neon outer glow border. The core surface component used for
 * dashboard stats, recent sessions, workout library items, and summary tiles (design.md section 4).
 *
 * @param glowColor the neon hue of the glow, always a palette token.
 * @param onClick optional click handler; when set the whole card is a touch target.
 */
@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = ElectricBlueGlow,
    cornerRadius: Dp = 20.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val decorated = modifier
        .neonGlow(glowColor = glowColor, cornerRadius = cornerRadius)
        .clip(shape)
        .background(MaterialTheme.colorScheme.surface)
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .padding(contentPadding)

    Column(modifier = decorated, content = content)
}
