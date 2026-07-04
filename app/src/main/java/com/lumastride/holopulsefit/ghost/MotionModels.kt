package com.lumastride.holopulsefit.ghost

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A single normalized joint position (0..1 relative to the analyzed frame).
 */
@Serializable
data class JointXY(val x: Float, val y: Float)

/**
 * One sampled skeletal frame in the MotionPath landmarkSeries (schema.md section 3). Only
 * normalized joint coordinates and a confidence value are stored, never raw camera data.
 */
@Serializable
data class MotionFrame(
    @SerialName("timestamp_ms") val timestampMs: Long,
    val joints: Map<String, JointXY> = emptyMap(),
    val confidence: Float = 1f,
)

/** Canonical joint names used across the MotionPath schema, ghost skeleton, and recorder. */
object Joints {
    const val LEFT_SHOULDER = "left_shoulder"
    const val RIGHT_SHOULDER = "right_shoulder"
    const val LEFT_ELBOW = "left_elbow"
    const val RIGHT_ELBOW = "right_elbow"
    const val LEFT_WRIST = "left_wrist"
    const val RIGHT_WRIST = "right_wrist"
    const val LEFT_HIP = "left_hip"
    const val RIGHT_HIP = "right_hip"
    const val LEFT_KNEE = "left_knee"
    const val RIGHT_KNEE = "right_knee"
    const val LEFT_ANKLE = "left_ankle"
    const val RIGHT_ANKLE = "right_ankle"

    /** The full ordered set stored per frame. */
    val ALL = listOf(
        LEFT_SHOULDER, RIGHT_SHOULDER, LEFT_ELBOW, RIGHT_ELBOW, LEFT_WRIST, RIGHT_WRIST,
        LEFT_HIP, RIGHT_HIP, LEFT_KNEE, RIGHT_KNEE, LEFT_ANKLE, RIGHT_ANKLE,
    )
}
