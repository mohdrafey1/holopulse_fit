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

    /**
     * Cooldown after any fired gesture before another can fire. A generous window keeps control
     * deliberate and prevents a single slow motion from triggering twice.
     */
    const val DEBOUNCE_MS = 3000L

    /** Time window over which a side swipe displacement is measured. */
    const val SWIPE_WINDOW_MS = 550L

    /** Minimum normalized horizontal wrist displacement for a swipe. */
    const val SWIPE_MIN_DX = 0.22f

    /**
     * A swipe only counts when the wrist is within this vertical distance of the shoulder, i.e. the
     * arm is held out to the side at shoulder height. This excludes squats (hands low) and jumping
     * jacks (hands overhead), where accidental horizontal motion would otherwise look like a swipe.
     */
    const val SWIPE_HEIGHT_BAND = 0.22f

    /** A swipe must be mostly horizontal: vertical travel below this fraction of horizontal travel. */
    const val SWIPE_MAX_VERTICAL_RATIO = 0.6f
}

/** The four gestures the engine recognizes (PRD section 7). */
enum class GestureType {
    HAND_RAISE,
    BOTH_HANDS_HOLD,
    SWIPE_LEFT,
    SWIPE_RIGHT,
}
