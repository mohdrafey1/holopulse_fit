package com.lumastride.holopulsefit.counting

/**
 * Threshold values for the rep counters, kept in one place per rules.md section 4.6 so tuning never
 * requires logic edits. Each pair uses hysteresis (enter above, exit below) to avoid double counts
 * from small jitter around a single threshold.
 */
object CountingConstants {

    // Squat: signal is the hip height between shoulders (0) and knees (1). Higher means deeper.
    const val SQUAT_ENTER = 0.80f // hips dropped toward knee level -> Down
    const val SQUAT_EXIT = 0.66f  // hips back up -> Up (completes the rep)

    // Jumping jack: signal is arm raise, wrists relative to shoulders scaled by torso length.
    const val JACK_ENTER = 0.35f  // arms up -> Open
    const val JACK_EXIT = 0.05f   // arms down -> Closed (completes the rep)

    // Push-up approximation: signal is elbow flexion, 0 extended to 1 fully bent.
    const val PUSHUP_ENTER = 0.38f // elbows bent -> Down
    const val PUSHUP_EXIT = 0.22f  // elbows extended -> Up (completes the rep)
}
