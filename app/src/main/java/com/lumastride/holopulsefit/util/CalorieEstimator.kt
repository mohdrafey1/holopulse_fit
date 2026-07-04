package com.lumastride.holopulsefit.util

import kotlin.math.roundToInt

/**
 * Estimates calories for a session from a per exercise MET style factor, duration, and rep count
 * (schema.md section 5.2). The estimate is stored on the session at save time.
 *
 * The constants were tuned so the supported exercises land near the provided sample data values
 * (a moderate squat set and an intense jumping jack set). This is a rough estimate for review, not
 * a medical measurement.
 */
object CalorieEstimator {
    /** Scales MET factor and minutes into kcal, roughly modeling an average adult. */
    private const val TIME_FACTOR = 2.0

    /** Extra kcal credited per completed rep. */
    private const val REP_FACTOR = 0.3

    fun estimate(metFactor: Double, durationSeconds: Int, reps: Int): Int {
        val minutes = durationSeconds.coerceAtLeast(0) / 60.0
        val kcal = metFactor * minutes * TIME_FACTOR + reps.coerceAtLeast(0) * REP_FACTOR
        return kcal.roundToInt().coerceAtLeast(0)
    }
}
