package com.lumastride.holopulsefit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import com.lumastride.holopulsefit.pose.Landmark
import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseLandmarkType
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow

/** Skeleton bones drawn between landmark pairs. */
private val bones: List<Pair<PoseLandmarkType, PoseLandmarkType>> = listOf(
    PoseLandmarkType.LEFT_SHOULDER to PoseLandmarkType.RIGHT_SHOULDER,
    PoseLandmarkType.LEFT_SHOULDER to PoseLandmarkType.LEFT_ELBOW,
    PoseLandmarkType.LEFT_ELBOW to PoseLandmarkType.LEFT_WRIST,
    PoseLandmarkType.RIGHT_SHOULDER to PoseLandmarkType.RIGHT_ELBOW,
    PoseLandmarkType.RIGHT_ELBOW to PoseLandmarkType.RIGHT_WRIST,
    PoseLandmarkType.LEFT_SHOULDER to PoseLandmarkType.LEFT_HIP,
    PoseLandmarkType.RIGHT_SHOULDER to PoseLandmarkType.RIGHT_HIP,
    PoseLandmarkType.LEFT_HIP to PoseLandmarkType.RIGHT_HIP,
    PoseLandmarkType.LEFT_HIP to PoseLandmarkType.LEFT_KNEE,
    PoseLandmarkType.LEFT_KNEE to PoseLandmarkType.LEFT_ANKLE,
    PoseLandmarkType.RIGHT_HIP to PoseLandmarkType.RIGHT_KNEE,
    PoseLandmarkType.RIGHT_KNEE to PoseLandmarkType.RIGHT_ANKLE,
)

/**
 * Draws the live pose skeleton over the camera preview in electric blue with confidence based
 * opacity (design.md PoseOverlay, rules.md section 3.5). Landmark coordinates are normalized, so the
 * overlay maps them onto its own size; the front camera preview is mirrored, so X is mirrored to
 * match.
 *
 * @param minLikelihood bones and joints below this confidence are hidden.
 */
@Composable
fun PoseOverlay(
    frame: PoseFrame,
    modifier: Modifier = Modifier,
    mirrorX: Boolean = true,
    minLikelihood: Float = 0.3f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        fun toOffset(landmark: Landmark): Offset {
            val x = if (mirrorX) (1f - landmark.x) else landmark.x
            return Offset(x * w, landmark.y * h)
        }

        bones.forEach { (a, b) ->
            val la = frame[a]
            val lb = frame[b]
            if (la != null && lb != null) {
                val confidence = minOf(la.likelihood, lb.likelihood)
                if (confidence >= minLikelihood) {
                    drawLine(
                        color = ElectricBlueGlow.copy(alpha = confidence.coerceIn(0.3f, 1f)),
                        start = toOffset(la),
                        end = toOffset(lb),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }

        frame.landmarks.values.forEach { landmark ->
            if (landmark.likelihood >= minLikelihood) {
                drawCircle(
                    color = CyanPulse.copy(alpha = landmark.likelihood.coerceIn(0.3f, 1f)),
                    radius = 7f,
                    center = toOffset(landmark),
                )
            }
        }
    }
}
