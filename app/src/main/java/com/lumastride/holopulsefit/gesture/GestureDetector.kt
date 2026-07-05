package com.lumastride.holopulsefit.gesture

import com.lumastride.holopulsefit.pose.PoseConfidence
import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseLandmarkType
import kotlin.math.abs
import kotlin.math.sign

/**
 * Frame driven gesture state machine with per gesture hold and debounce timers (TRD section 5).
 * Timing uses each frame's timestamp so it is deterministic and unit testable.
 *
 * Rules enforced (rules.md section 4): a stable hold or a clear directional swipe is required, no
 * gesture fires on a single frame, both hands toggles at most once per hold cycle, and low
 * confidence or a partially visible body suspends detection (guidance is handled by the caller).
 */
class GestureDetector {

    private var handRaiseStartMs: Long? = null
    private var handRaiseLatched = false
    private var bothHandsStartMs: Long? = null
    private var bothHandsLatched = false
    private var lastFireMs = -GestureConstants.DEBOUNCE_MS
    private val swipeHistory = ArrayDeque<SwipeSample>()

    /** Feeds one frame; returns a gesture when one fires, otherwise null. */
    fun onFrame(frame: PoseFrame): GestureType? {
        val t = frame.timestampMs
        val confidence = frame.likelihoodOf(
            PoseLandmarkType.LEFT_SHOULDER, PoseLandmarkType.RIGHT_SHOULDER,
            PoseLandmarkType.LEFT_WRIST, PoseLandmarkType.RIGHT_WRIST,
        )
        val leftShoulder = frame[PoseLandmarkType.LEFT_SHOULDER]
        val rightShoulder = frame[PoseLandmarkType.RIGHT_SHOULDER]
        val leftWrist = frame[PoseLandmarkType.LEFT_WRIST]
        val rightWrist = frame[PoseLandmarkType.RIGHT_WRIST]

        if (confidence < PoseConfidence.TRACKING_GATE ||
            leftShoulder == null || rightShoulder == null || leftWrist == null || rightWrist == null
        ) {
            resetHolds()
            swipeHistory.clear()
            return null
        }

        val leftRaised = leftWrist.y < leftShoulder.y - GestureConstants.WRIST_ABOVE_MARGIN
        val rightRaised = rightWrist.y < rightShoulder.y - GestureConstants.WRIST_ABOVE_MARGIN
        val bothRaised = leftRaised && rightRaised
        val oneRaised = leftRaised != rightRaised

        // Both hands hold: toggle pause or resume, once per hold cycle.
        if (bothRaised) {
            handRaiseStartMs = null
            handRaiseLatched = false
            swipeHistory.clear()
            if (bothHandsStartMs == null) bothHandsStartMs = t
            if (!bothHandsLatched &&
                t - bothHandsStartMs!! >= GestureConstants.BOTH_HANDS_HOLD_MS &&
                canFire(t)
            ) {
                bothHandsLatched = true
                lastFireMs = t
                return GestureType.BOTH_HANDS_HOLD
            }
            return null
        } else {
            bothHandsStartMs = null
            bothHandsLatched = false
        }

        // Single hand raise: next or confirm, after a stable hold.
        if (oneRaised) {
            swipeHistory.clear()
            if (handRaiseStartMs == null) handRaiseStartMs = t
            if (!handRaiseLatched &&
                t - handRaiseStartMs!! >= GestureConstants.HAND_RAISE_HOLD_MS &&
                canFire(t)
            ) {
                handRaiseLatched = true
                lastFireMs = t
                return GestureType.HAND_RAISE
            }
            return null
        } else {
            handRaiseStartMs = null
            handRaiseLatched = false
        }

        // Neither hand raised: look for a deliberate sideways sweep of the right wrist held at
        // shoulder height. The shoulder height reference excludes exercise arm motion.
        val shoulderY = (leftShoulder.y + rightShoulder.y) / 2f
        return detectSwipe(t, rightWrist.x, rightWrist.y, shoulderY)
    }

    private fun detectSwipe(t: Long, x: Float, y: Float, shoulderY: Float): GestureType? {
        // Only a hand held out near shoulder height counts, so squats and jumping jacks are ignored.
        if (abs(y - shoulderY) > GestureConstants.SWIPE_HEIGHT_BAND) {
            swipeHistory.clear()
            return null
        }

        swipeHistory.addLast(SwipeSample(t, x, y))
        while (swipeHistory.isNotEmpty() && t - swipeHistory.first().t > GestureConstants.SWIPE_WINDOW_MS) {
            swipeHistory.removeFirst()
        }
        if (swipeHistory.size < 3 || !canFire(t)) return null

        val start = swipeHistory.first()
        val dx = x - start.x
        val dy = y - start.y
        if (abs(dx) < GestureConstants.SWIPE_MIN_DX) return null
        // Reject exercise motion that is not predominantly horizontal.
        if (abs(dy) > abs(dx) * GestureConstants.SWIPE_MAX_VERTICAL_RATIO) return null

        // Require a consistent horizontal direction across the window to reject jitter.
        val direction = sign(dx)
        var previous = start.x
        for (sample in swipeHistory.drop(1)) {
            val step = sample.x - previous
            if (sign(step) == -direction && abs(step) > 0.02f) return null
            previous = sample.x
        }

        lastFireMs = t
        swipeHistory.clear()
        return if (dx > 0f) GestureType.SWIPE_RIGHT else GestureType.SWIPE_LEFT
    }

    private data class SwipeSample(val t: Long, val x: Float, val y: Float)

    private fun canFire(t: Long): Boolean = t - lastFireMs >= GestureConstants.DEBOUNCE_MS

    private fun resetHolds() {
        handRaiseStartMs = null
        handRaiseLatched = false
        bothHandsStartMs = null
        bothHandsLatched = false
    }

    fun reset() {
        resetHolds()
        swipeHistory.clear()
        lastFireMs = -GestureConstants.DEBOUNCE_MS
    }
}
