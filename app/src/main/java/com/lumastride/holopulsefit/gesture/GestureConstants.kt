package com.lumastride.holopulsefit.gesture

/**
 * Threshold and timing values for the gesture engine, in one place per rules.md section 4.6. Every
 * gesture requires a stable hold or a clear direction plus a confidence check, and no gesture fires
 * on a single frame (rules.md section 4.1).
 */
object GestureConstants {
    /** A wrist counts as raised when it is this far above its shoulder (normalized, y up). */
    const val WRIST_ABOVE_MARGIN = 0.03f

    /** Stable hold before a single hand raise fires next or confirm (TRD section 5). */
    const val HAND_RAISE_HOLD_MS = 700L

    /** Stable hold before both hands toggles pause or resume (TRD section 5). */
    const val BOTH_HANDS_HOLD_MS = 1000L

    /** Minimum gap between any two fired gestures, avoiding bursts. */
    const val DEBOUNCE_MS = 900L

    /** Time window over which a side swipe displacement is measured. */
    const val SWIPE_WINDOW_MS = 550L

    /** Minimum normalized horizontal wrist displacement for a swipe. */
    const val SWIPE_MIN_DX = 0.18f
}

/** The four gestures the engine recognizes (PRD section 7). */
enum class GestureType {
    HAND_RAISE,
    BOTH_HANDS_HOLD,
    SWIPE_LEFT,
    SWIPE_RIGHT,
}
