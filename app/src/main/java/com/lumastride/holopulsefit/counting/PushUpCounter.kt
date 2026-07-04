package com.lumastride.holopulsefit.counting

import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseGeometry
import com.lumastride.holopulsefit.pose.PoseLandmarkType

/**
 * Approximate push-up counter from elbow flexion (PRD section 8). The signal is the average elbow
 * bend across both arms, 0 when the arms are extended and 1 when fully bent. Bending past the enter
 * threshold is the down phase; extending back past the exit threshold completes the rep. Because a
 * front or high camera angle makes this less reliable, results are labeled estimated when the
 * confidence average is low (schema.md section 5.3).
 */
class PushUpCounter : PhaseRepCounter() {

    override val gateJoints = arrayOf(
        PoseLandmarkType.LEFT_SHOULDER, PoseLandmarkType.RIGHT_SHOULDER,
        PoseLandmarkType.LEFT_ELBOW, PoseLandmarkType.RIGHT_ELBOW,
        PoseLandmarkType.LEFT_WRIST, PoseLandmarkType.RIGHT_WRIST,
    )
    override val enterThreshold = CountingConstants.PUSHUP_ENTER
    override val exitThreshold = CountingConstants.PUSHUP_EXIT
    override val activeLabel = "Down"
    override val neutralLabel = "Up"

    override fun signal(frame: PoseFrame): Float? {
        val left = PoseGeometry.angleDegrees(
            frame, PoseLandmarkType.LEFT_SHOULDER, PoseLandmarkType.LEFT_ELBOW, PoseLandmarkType.LEFT_WRIST,
        )
        val right = PoseGeometry.angleDegrees(
            frame, PoseLandmarkType.RIGHT_SHOULDER, PoseLandmarkType.RIGHT_ELBOW, PoseLandmarkType.RIGHT_WRIST,
        )
        val angle = when {
            left != null && right != null -> (left + right) / 2f
            left != null -> left
            right != null -> right
            else -> return null
        }
        // 180 degrees extended maps to 0, roughly 30 degrees fully bent maps to 1.
        return ((180f - angle) / 150f).coerceIn(0f, 1f)
    }
}
