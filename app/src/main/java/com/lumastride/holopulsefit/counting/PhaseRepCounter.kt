package com.lumastride.holopulsefit.counting

import com.lumastride.holopulsefit.pose.PoseConfidence
import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseLandmarkType

/**
 * Base two phase finite state machine for rep counting (TRD section 6). A subclass provides a scalar
 * [signal] derived from the pose plus enter and exit thresholds. The machine only increments on a
 * complete cycle: neutral to active (enter) then active back to neutral (exit), which counts the rep
 * (rules.md section 4.3). Hysteresis between the thresholds removes double counts from jitter.
 *
 * A confidence gate suspends counting when the tracked joints drop below the threshold
 * (rules.md section 3.5). Per rep confidence is averaged into [confidenceAverage] for the
 * ExerciseSet and the estimated flag.
 */
abstract class PhaseRepCounter {

    var reps: Int = 0
        private set

    var stateLabel: String = "Ready"
        private set

    private val repConfidences = ArrayList<Float>()
    private var active = false
    private var cycleConfSum = 0f
    private var cycleConfCount = 0

    /** Mean of the per rep confidence averages, 0 before any rep completes. */
    val confidenceAverage: Float
        get() = if (repConfidences.isEmpty()) 0f else repConfidences.average().toFloat()

    /** Landmarks whose average likelihood gates this counter. */
    protected abstract val gateJoints: Array<PoseLandmarkType>
    protected abstract val enterThreshold: Float
    protected abstract val exitThreshold: Float
    protected abstract val activeLabel: String
    protected abstract val neutralLabel: String

    /** The scalar signal for this exercise, or null when required landmarks are missing. */
    protected abstract fun signal(frame: PoseFrame): Float?

    /**
     * Feeds one frame. Returns true when a rep was completed on this frame.
     */
    fun update(frame: PoseFrame): Boolean {
        val confidence = frame.likelihoodOf(*gateJoints)
        if (confidence < PoseConfidence.TRACKING_GATE) {
            stateLabel = "Low confidence"
            return false
        }
        val s = signal(frame)
        if (s == null) {
            stateLabel = "Low confidence"
            return false
        }

        if (active) {
            cycleConfSum += confidence
            cycleConfCount++
        }

        return when {
            !active && s >= enterThreshold -> {
                active = true
                stateLabel = activeLabel
                cycleConfSum = confidence
                cycleConfCount = 1
                false
            }
            active && s <= exitThreshold -> {
                active = false
                reps++
                repConfidences.add(if (cycleConfCount > 0) cycleConfSum / cycleConfCount else confidence)
                stateLabel = neutralLabel
                true
            }
            else -> {
                stateLabel = if (active) activeLabel else neutralLabel
                false
            }
        }
    }

    fun reset() {
        reps = 0
        stateLabel = "Ready"
        active = false
        cycleConfSum = 0f
        cycleConfCount = 0
        repConfidences.clear()
    }
}
