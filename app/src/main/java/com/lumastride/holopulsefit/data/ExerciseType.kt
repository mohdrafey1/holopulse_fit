package com.lumastride.holopulsefit.data

/**
 * The three supported bodyweight exercises.
 *
 * [id] is the stable string persisted in [WorkoutSession.exerciseType] and used in navigation
 * routes, so it must never change. The remaining fields drive the workout library cards, the rep
 * counters, and the calorie estimator, keeping exercise metadata in one place.
 */
enum class ExerciseType(
    val id: String,
    val displayName: String,
    val shortInstruction: String,
    val defaultTargetReps: Int,
    val targetRepOptions: List<Int>,
    val effort: String,
    /** MET style intensity factor used by the calorie estimator (util/CalorieEstimator). */
    val metFactor: Double,
    /** True when landmark tracking is less reliable and results should be labeled estimated. */
    val approximate: Boolean = false,
) {
    SQUATS(
        id = "squats",
        displayName = "Squats",
        shortInstruction = "Stand tall, bend knees and hips to lower down, then drive back up.",
        defaultTargetReps = 20,
        targetRepOptions = listOf(10, 20, 30),
        effort = "Moderate",
        metFactor = 5.0,
    ),
    JUMPING_JACKS(
        id = "jumping_jacks",
        displayName = "Jumping Jacks",
        shortInstruction = "Jump feet wide while raising arms overhead, then return to center.",
        defaultTargetReps = 30,
        targetRepOptions = listOf(20, 30, 40),
        effort = "Intense",
        metFactor = 8.0,
    ),
    PUSHUP_APPROXIMATION(
        id = "pushup_approximation",
        displayName = "Push-up (approx)",
        shortInstruction = "From a camera friendly angle, bend elbows to lower, then press up.",
        defaultTargetReps = 12,
        targetRepOptions = listOf(8, 12, 16),
        effort = "Intense",
        metFactor = 7.0,
        approximate = true,
    );

    /** The next exercise in the list, wrapping around, for touchless swipe navigation. */
    fun next(): ExerciseType = entries[(ordinal + 1) % entries.size]

    /** The previous exercise in the list, wrapping around. */
    fun previous(): ExerciseType = entries[(ordinal - 1 + entries.size) % entries.size]

    companion object {
        /** Resolves a persisted or route id back to the enum, defaulting to squats if unknown. */
        fun fromId(id: String?): ExerciseType = entries.firstOrNull { it.id == id } ?: SQUATS
    }
}
