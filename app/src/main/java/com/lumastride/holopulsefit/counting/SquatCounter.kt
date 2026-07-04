package com.lumastride.holopulsefit.counting

import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseGeometry
import com.lumastride.holopulsefit.pose.PoseLandmarkType

/**
 * Counts squats from hip, knee, and shoulder vertical positions (PRD section 8). The signal is the
 * hip height expressed between the shoulders (0) and the knees (1), which is invariant to where the
 * body sits in the frame and to its scale. Standing keeps the hips high (low signal); a deep squat
 * drops the hips toward knee level (high signal). A rep completes on down then up.
 */
class SquatCounter : PhaseRepCounter() {

    override val gateJoints = arrayOf(
        PoseLandmarkType.LEFT_HIP, PoseLandmarkType.RIGHT_HIP,
        PoseLandmarkType.LEFT_KNEE, PoseLandmarkType.RIGHT_KNEE,
        PoseLandmarkType.LEFT_SHOULDER, PoseLandmarkType.RIGHT_SHOULDER,
    )
    override val enterThreshold = CountingConstants.SQUAT_ENTER
    override val exitThreshold = CountingConstants.SQUAT_EXIT
    override val activeLabel = "Down"
    override val neutralLabel = "Up"

    override fun signal(frame: PoseFrame): Float? {
        val shoulder = PoseGeometry.avgY(frame, PoseLandmarkType.LEFT_SHOULDER, PoseLandmarkType.RIGHT_SHOULDER) ?: return null
        val hip = PoseGeometry.avgY(frame, PoseLandmarkType.LEFT_HIP, PoseLandmarkType.RIGHT_HIP) ?: return null
        val knee = PoseGeometry.avgY(frame, PoseLandmarkType.LEFT_KNEE, PoseLandmarkType.RIGHT_KNEE) ?: return null
        val denom = knee - shoulder
        if (denom <= 0.05f) return null
        return (hip - shoulder) / denom
    }
}
