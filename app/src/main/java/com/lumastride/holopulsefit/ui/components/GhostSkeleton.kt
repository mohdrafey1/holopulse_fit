package com.lumastride.holopulsefit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import com.lumastride.holopulsefit.ui.theme.VioletEnergy

/**
 * Normalized joint positions (0..1) keyed by the joint names used in the MotionPath schema.
 * The Ghost Trainer draws its semi transparent skeleton from a map of these, so both the static
 * Phase 1 stage and the later frame driven replay share one renderer.
 */
typealias GhostFrame = Map<String, Offset>

private val skeletonBones: List<Pair<String, String>> = listOf(
    "left_shoulder" to "right_shoulder",
    "left_shoulder" to "left_elbow",
    "left_elbow" to "left_wrist",
    "right_shoulder" to "right_elbow",
    "right_elbow" to "right_wrist",
    "left_shoulder" to "left_hip",
    "right_shoulder" to "right_hip",
    "left_hip" to "right_hip",
    "left_hip" to "left_knee",
    "left_knee" to "left_ankle",
    "right_hip" to "right_knee",
    "right_knee" to "right_ankle",
)

/**
 * Draws a semi transparent violet skeleton, visually distinct from the live electric blue overlay
 * (design.md GhostSkeleton). Used by the Ghost Trainer replay and the in workout guide overlay.
 */
@Composable
fun GhostSkeleton(
    frame: GhostFrame,
    modifier: Modifier = Modifier,
    alpha: Float = 0.55f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        fun p(name: String): Offset? = frame[name]?.let { Offset(it.x * w, it.y * h) }

        skeletonBones.forEach { (a, b) ->
            val pa = p(a)
            val pb = p(b)
            if (pa != null && pb != null) {
                drawLine(
                    color = VioletEnergy.copy(alpha = alpha),
                    start = pa,
                    end = pb,
                    strokeWidth = 10f,
                    cap = StrokeCap.Round,
                )
            }
        }
        frame.values.forEach { joint ->
            drawCircle(
                color = VioletEnergy.copy(alpha = alpha),
                radius = 9f,
                center = Offset(joint.x * w, joint.y * h),
            )
        }
    }
}
