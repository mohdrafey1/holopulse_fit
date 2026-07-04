package com.lumastride.holopulsefit.ghost

import androidx.compose.ui.geometry.Offset
import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseLandmarkType

/** Single mapping from tracked landmark types to the MotionPath joint names. */
object PoseJoints {
    val nameByType: Map<PoseLandmarkType, String> = mapOf(
        PoseLandmarkType.LEFT_SHOULDER to Joints.LEFT_SHOULDER,
        PoseLandmarkType.RIGHT_SHOULDER to Joints.RIGHT_SHOULDER,
        PoseLandmarkType.LEFT_ELBOW to Joints.LEFT_ELBOW,
        PoseLandmarkType.RIGHT_ELBOW to Joints.RIGHT_ELBOW,
        PoseLandmarkType.LEFT_WRIST to Joints.LEFT_WRIST,
        PoseLandmarkType.RIGHT_WRIST to Joints.RIGHT_WRIST,
        PoseLandmarkType.LEFT_HIP to Joints.LEFT_HIP,
        PoseLandmarkType.RIGHT_HIP to Joints.RIGHT_HIP,
        PoseLandmarkType.LEFT_KNEE to Joints.LEFT_KNEE,
        PoseLandmarkType.RIGHT_KNEE to Joints.RIGHT_KNEE,
        PoseLandmarkType.LEFT_ANKLE to Joints.LEFT_ANKLE,
        PoseLandmarkType.RIGHT_ANKLE to Joints.RIGHT_ANKLE,
    )
}

/** Live pose as normalized joint offsets keyed by MotionPath joint name (for similarity scoring). */
fun PoseFrame.toGhostOffsets(): Map<String, Offset> = buildMap {
    for ((type, name) in PoseJoints.nameByType) {
        this@toGhostOffsets[type]?.let { put(name, Offset(it.x, it.y)) }
    }
}

/** Live pose as a MotionFrame for recording, with a relative timestamp. */
fun PoseFrame.toMotionFrame(relativeTimestampMs: Long): MotionFrame {
    val joints = HashMap<String, JointXY>(PoseJoints.nameByType.size)
    for ((type, name) in PoseJoints.nameByType) {
        this[type]?.let { joints[name] = JointXY(it.x, it.y) }
    }
    return MotionFrame(
        timestampMs = relativeTimestampMs,
        joints = joints,
        confidence = averageLikelihood,
    )
}
