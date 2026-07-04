package com.lumastride.holopulsefit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lumastride.holopulsefit.pose.PoseConfidence
import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseLandmarkType
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.VioletEnergy
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.hypot

/** Joints that grow Aura Energy trails (design.md section 6.1). */
private val trailJoints = listOf(
    PoseLandmarkType.LEFT_WRIST, PoseLandmarkType.RIGHT_WRIST,
    PoseLandmarkType.LEFT_ELBOW, PoseLandmarkType.RIGHT_ELBOW,
    PoseLandmarkType.LEFT_KNEE, PoseLandmarkType.RIGHT_KNEE,
    PoseLandmarkType.LEFT_ANKLE, PoseLandmarkType.RIGHT_ANKLE,
)

/**
 * Draws glowing Aura Energy trails behind the tracked joints on a single Canvas layer with bounded
 * point buffers (design.md section 6, TRD section 9). Trail length and glow scale with movement
 * speed and the aura intensity; colors blend from cyan to violet along the fade. Reduced effects
 * shortens the trails and lowers the glow. It renders under the data layers so nothing is hidden.
 */
@Composable
fun AuraTrailLayer(
    poseFrames: StateFlow<PoseFrame>,
    auraIntensity: Float,
    reducedEffects: Boolean,
    modifier: Modifier = Modifier,
    mirrorX: Boolean = true,
) {
    val frame by poseFrames.collectAsStateWithLifecycle()
    val buffers = remember { mutableMapOf<PoseLandmarkType, ArrayDeque<Offset>>() }
    var tick by remember { mutableIntStateOf(0) }

    val maxPoints = if (reducedEffects) 6 else 16
    val glowScale = if (reducedEffects) 0.55f else 1f

    LaunchedEffect(frame.timestampMs) {
        for (joint in trailJoints) {
            val buffer = buffers.getOrPut(joint) { ArrayDeque() }
            val lm = frame[joint]
            if (lm != null && lm.likelihood >= PoseConfidence.OVERLAY_MIN) {
                buffer.addLast(Offset(lm.x, lm.y))
                while (buffer.size > maxPoints) buffer.removeFirst()
            } else if (buffer.isNotEmpty()) {
                buffer.removeFirst() // fade the trail out when the joint is lost
            }
        }
        tick++
    }

    Canvas(modifier = modifier) {
        tick // establish a redraw dependency on each new frame
        if (auraIntensity <= 0.01f) return@Canvas
        val w = size.width
        val h = size.height
        fun mapped(p: Offset) = Offset((if (mirrorX) 1f - p.x else p.x) * w, p.y * h)

        for (buffer in buffers.values) {
            if (buffer.size < 2) continue
            val points = buffer.toList()
            val last = points.size - 1
            for (i in 1..last) {
                val f = i.toFloat() / last // 0 oldest, 1 newest
                val p0 = mapped(points[i - 1])
                val p1 = mapped(points[i])
                val speed = hypot((points[i].x - points[i - 1].x).toDouble(), (points[i].y - points[i - 1].y).toDouble()).toFloat()
                val speedBoost = (0.35f + speed * 9f).coerceIn(0.35f, 1.3f)
                val alpha = (f * auraIntensity * speedBoost * glowScale).coerceIn(0f, 1f)
                val stroke = (3f + f * 12f) * glowScale
                drawLine(
                    color = lerp(CyanPulse, VioletEnergy, f).copy(alpha = alpha),
                    start = p0,
                    end = p1,
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
