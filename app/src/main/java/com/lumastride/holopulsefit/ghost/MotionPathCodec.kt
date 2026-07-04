package com.lumastride.holopulsefit.ghost

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** A simplified sample frame carrying only vertical hip, knee, and shoulder positions. */
data class SimplifiedSample(
    val timestampMs: Long,
    val hipY: Float?,
    val kneeY: Float?,
    val shoulderY: Float?,
)

/**
 * Serializes and deserializes MotionPath series, and expands the simplified hip, knee, shoulder
 * sample format into full skeleton frames using a standing template for the joints not provided
 * (schema.md section 3, rule 3). Shared by the sample seeder, the session recorder, and replay.
 */
object MotionPathCodec {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encodeSeries(frames: List<MotionFrame>): String = json.encodeToString(frames)

    fun encodeTimestamps(frames: List<MotionFrame>): String =
        json.encodeToString(frames.map { it.timestampMs })

    fun decodeSeries(seriesJson: String): List<MotionFrame> = try {
        json.decodeFromString<List<MotionFrame>>(seriesJson)
    } catch (_: Exception) {
        emptyList()
    }

    // Fixed horizontal template so the ghost reads as an upright figure.
    private const val SHOULDER_L_X = 0.42f
    private const val SHOULDER_R_X = 0.58f
    private const val ELBOW_L_X = 0.37f
    private const val ELBOW_R_X = 0.63f
    private const val WRIST_L_X = 0.35f
    private const val WRIST_R_X = 0.65f
    private const val HIP_L_X = 0.45f
    private const val HIP_R_X = 0.55f
    private const val KNEE_L_X = 0.44f
    private const val KNEE_R_X = 0.56f
    private const val ANKLE_L_X = 0.44f
    private const val ANKLE_R_X = 0.56f

    /**
     * Expands simplified samples into full frames. Missing values fall back to a neutral standing
     * pose, and arm and ankle joints are derived from the shoulder and knee heights so the guide
     * still animates.
     */
    fun fromSimplified(samples: List<SimplifiedSample>): List<MotionFrame> = samples.map { s ->
        val shoulderY = s.shoulderY ?: 0.25f
        val hipY = s.hipY ?: 0.5f
        val kneeY = s.kneeY ?: 0.7f
        val elbowY = (shoulderY + 0.13f).coerceAtMost(0.98f)
        val wristY = (shoulderY + 0.26f).coerceAtMost(0.98f)
        val ankleY = (kneeY + 0.18f).coerceAtMost(0.98f)
        MotionFrame(
            timestampMs = s.timestampMs,
            joints = mapOf(
                Joints.LEFT_SHOULDER to JointXY(SHOULDER_L_X, shoulderY),
                Joints.RIGHT_SHOULDER to JointXY(SHOULDER_R_X, shoulderY),
                Joints.LEFT_ELBOW to JointXY(ELBOW_L_X, elbowY),
                Joints.RIGHT_ELBOW to JointXY(ELBOW_R_X, elbowY),
                Joints.LEFT_WRIST to JointXY(WRIST_L_X, wristY),
                Joints.RIGHT_WRIST to JointXY(WRIST_R_X, wristY),
                Joints.LEFT_HIP to JointXY(HIP_L_X, hipY),
                Joints.RIGHT_HIP to JointXY(HIP_R_X, hipY),
                Joints.LEFT_KNEE to JointXY(KNEE_L_X, kneeY),
                Joints.RIGHT_KNEE to JointXY(KNEE_R_X, kneeY),
                Joints.LEFT_ANKLE to JointXY(ANKLE_L_X, ankleY),
                Joints.RIGHT_ANKLE to JointXY(ANKLE_R_X, ankleY),
            ),
            confidence = 0.9f,
        )
    }
}
