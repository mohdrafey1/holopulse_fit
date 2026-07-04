package com.lumastride.holopulsefit.ghost

import com.lumastride.holopulsefit.pose.PoseFrame

/**
 * Records a simplified skeletal motion path during a session for Ghost Trainer replay
 * (TRD section 7). Samples at about 5 frames per second, storing only normalized joint coordinates
 * with timestamps relative to the first sample. No raw camera data is ever captured.
 */
class MotionRecorder(private val intervalMs: Long = 200L) {

    private val frames = ArrayList<MotionFrame>()
    private var lastSampleMs = Long.MIN_VALUE
    private var startMs = 0L

    /** Samples a frame if enough time has passed and a body is present. */
    fun maybeSample(frame: PoseFrame) {
        if (frame.isEmpty) return
        val t = frame.timestampMs
        if (frames.isEmpty()) {
            startMs = t
        } else if (t - lastSampleMs < intervalMs) {
            return
        }
        lastSampleMs = t
        frames.add(frame.toMotionFrame(t - startMs))
    }

    /** Snapshot of the recorded frames. */
    fun build(): List<MotionFrame> = frames.toList()

    fun reset() {
        frames.clear()
        lastSampleMs = Long.MIN_VALUE
        startMs = 0L
    }
}
