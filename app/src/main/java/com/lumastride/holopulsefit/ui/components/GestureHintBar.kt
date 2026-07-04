package com.lumastride.holopulsefit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ScrimDark

/** A single gesture hint: an icon and a short label describing an available touchless action. */
data class GestureHint(
    val icon: ImageVector,
    val label: String,
)

/**
 * Compact strip of icon plus label hints for the gestures available in the current state
 * (design.md section 4). Rendered over a dark scrim so it stays readable above the camera preview.
 */
@Composable
fun GestureHintBar(
    hints: List<GestureHint>,
    modifier: Modifier = Modifier,
) {
    if (hints.isEmpty()) return
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ScrimDark)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        hints.forEach { hint ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = hint.icon,
                    contentDescription = null,
                    tint = CyanPulse,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = hint.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
