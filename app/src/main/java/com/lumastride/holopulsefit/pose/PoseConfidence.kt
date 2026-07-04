package com.lumastride.holopulsefit.pose

/**
 * Confidence thresholds that gate every pose consumer (rules.md section 3.5). Kept in one place so
 * tuning never requires touching overlay, counting, or gesture logic.
 */
object PoseConfidence {
    /** Below this average landmark likelihood, counting pauses and guidance is shown. */
    const val TRACKING_GATE = 0.5f

    /** Below this the push-up approximation confidence average marks results as estimated. */
    const val ESTIMATED_THRESHOLD = 0.6f

    /** Minimum likelihood for a landmark or bone to be drawn in the overlay. */
    const val OVERLAY_MIN = 0.3f
}
