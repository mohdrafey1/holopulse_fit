package com.lumastride.holopulsefit.pose

/**
 * The subset of body landmarks HoloPulse Fit tracks. Keeping our own enum (rather than raw ML Kit
 * ints) means the rep counters and gesture engine can be unit tested with synthetic frames and have
 * no dependency on ML Kit.
 */
enum class PoseLandmarkType {
    NOSE,
    LEFT_SHOULDER, RIGHT_SHOULDER,
    LEFT_ELBOW, RIGHT_ELBOW,
    LEFT_WRIST, RIGHT_WRIST,
    LEFT_HIP, RIGHT_HIP,
    LEFT_KNEE, RIGHT_KNEE,
    LEFT_ANKLE, RIGHT_ANKLE,
}

/**
 * A single detected landmark. Coordinates are normalized 0..1 relative to the analyzed frame, with
 * y increasing downward (screen convention). [likelihood] is the ML Kit inFrameLikelihood.
 */
data class Landmark(
    val x: Float,
    val y: Float,
    val likelihood: Float,
)

/**
 * One detected pose. Empty landmarks means no person was found in the frame.
 */
data class PoseFrame(
    val landmarks: Map<PoseLandmarkType, Landmark>,
    val timestampMs: Long,
) {
    operator fun get(type: PoseLandmarkType): Landmark? = landmarks[type]

    /** True when the frame carries no detected landmarks. */
    val isEmpty: Boolean get() = landmarks.isEmpty()

    /** Mean inFrameLikelihood across detected landmarks, 0 when empty. Drives the confidence gate. */
    val averageLikelihood: Float
        get() = if (landmarks.isEmpty()) 0f else landmarks.values.map { it.likelihood }.average().toFloat()

    /**
     * Mean likelihood across a specific set of landmarks, used by each counter to gate on just the
     * joints it depends on. Missing landmarks count as zero.
     */
    fun likelihoodOf(vararg types: PoseLandmarkType): Float {
        if (types.isEmpty()) return 0f
        return types.map { landmarks[it]?.likelihood ?: 0f }.average().toFloat()
    }

    companion object {
        val EMPTY = PoseFrame(emptyMap(), 0L)
    }
}
