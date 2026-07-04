package com.lumastride.holopulsefit.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.DividerBlue
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow
import kotlin.math.min

/**
 * Animated circular progress ring in cyan, used for daily progress, countdown, and target rep
 * progress (design.md section 4). The sweep animates smoothly when [progress] changes and a
 * translucent under stroke provides the neon glow.
 *
 * @param progress fraction from 0f to 1f.
 * @param centerContent optional content drawn in the middle of the ring (for example a rep count).
 */
@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    diameter: Dp = 120.dp,
    strokeWidth: Dp = 10.dp,
    trackColor: Color = DividerBlue,
    progressBrush: Brush = Brush.sweepGradient(listOf(CyanPulse, ElectricBlueGlow, CyanPulse)),
    centerContent: (@Composable () -> Unit)? = null,
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "ringProgress",
    )
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(diameter)) {
        Canvas(modifier = Modifier.size(diameter)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f
            val arcSize = androidx.compose.ui.geometry.Size(
                width = size.width - stroke,
                height = size.height - stroke,
            )
            val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
            // Track.
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            // Soft glow under stroke.
            drawArc(
                brush = progressBrush,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke * 1.8f, cap = StrokeCap.Round),
                alpha = 0.25f,
            )
            // Progress arc.
            drawArc(
                brush = progressBrush,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        if (centerContent != null) {
            centerContent()
        }
    }
}

/** Clamps a raw value into a progress fraction, guarding against a zero or negative target. */
fun progressFraction(current: Int, target: Int): Float =
    if (target <= 0) 0f else min(1f, current.toFloat() / target.toFloat())
