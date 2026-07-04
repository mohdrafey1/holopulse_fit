package com.lumastride.holopulsefit.counting

import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseGeometry
import com.lumastride.holopulsefit.pose.PoseLandmarkType

/**
 * Counts jumping jacks from arm spread across repeated cycles (PRD section 8). The primary signal is
 * arm raise: the wrists relative to the shoulders, scaled by torso length so it is size invariant.
 * Arms overhead give a large positive signal (open); arms down give a negative signal (closed). A
 * rep completes on open then closed. Leg spread is visible in the overlay and accompanies the motion
 * but the front camera arm raise is the reliable trigger.
 */
class JumpingJackCounter : PhaseRepCounter() {

    override val gateJoints = arrayOf(
        PoseLandmarkType.LEFT_SHOULDER, PoseLandmarkType.RIGHT_SHOULDER,
        PoseLandmarkType.LEFT_WRIST, PoseLandmarkType.RIGHT_WRIST,
        PoseLandmarkType.LEFT_HIP, PoseLandmarkType.RIGHT_HIP,
    )
    override val enterThreshold = CountingConstants.JACK_ENTER
    override val exitThreshold = CountingConstants.JACK_EXIT
    override val activeLabel = "Open"
    override val neutralLabel = "Closed"

    override fun signal(frame: PoseFrame): Float? {
        val shoulder = PoseGeometry.avgY(frame, PoseLandmarkType.LEFT_SHOULDER, PoseLandmarkType.RIGHT_SHOULDER) ?: return null
        val wrist = PoseGeometry.avgY(frame, PoseLandmarkType.LEFT_WRIST, PoseLandmarkType.RIGHT_WRIST) ?: return null
        val hip = PoseGeometry.avgY(frame, PoseLandmarkType.LEFT_HIP, PoseLandmarkType.RIGHT_HIP) ?: return null
        val torso = hip - shoulder
        if (torso <= 0.05f) return null
        // Positive when wrists are above the shoulders (arms raised).
        return (shoulder - wrist) / torso
    }
}
