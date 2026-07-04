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
    private val swipeHistory = ArrayDeque<Pair<Long, Float>>()

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

        // Neither hand raised: look for a directional side swipe of the right wrist.
        return detectSwipe(t, rightWrist.x)
    }

    private fun detectSwipe(t: Long, x: Float): GestureType? {
        swipeHistory.addLast(t to x)
        while (swipeHistory.isNotEmpty() && t - swipeHistory.first().first > GestureConstants.SWIPE_WINDOW_MS) {
            swipeHistory.removeFirst()
        }
        if (swipeHistory.size < 3 || !canFire(t)) return null

        val startX = swipeHistory.first().second
        val dx = x - startX
        if (abs(dx) < GestureConstants.SWIPE_MIN_DX) return null

        // Require a consistent direction across the window to reject jitter.
        val direction = sign(dx)
        var previous = startX
        for ((_, sampleX) in swipeHistory.drop(1)) {
            val step = sampleX - previous
            if (sign(step) == -direction && abs(step) > 0.02f) return null
            previous = sampleX
        }

        lastFireMs = t
        swipeHistory.clear()
        return if (dx > 0f) GestureType.SWIPE_RIGHT else GestureType.SWIPE_LEFT
    }

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
