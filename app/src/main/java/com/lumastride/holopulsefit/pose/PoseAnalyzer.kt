package com.lumastride.holopulsefit.pose

import android.os.SystemClock
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

/**
 * CameraX [ImageAnalysis.Analyzer] that runs the ML Kit streaming pose detector and forwards a
 * [PoseFrame] for every processed frame (TRD section 4).
 *
 * Runs on the background analysis executor. Frames are throttled to about 18 fps to reduce heat and
 * battery use; frames arriving sooner are dropped and closed immediately, which combined with the
 * keep only latest strategy keeps the pipeline responsive. No frame data is ever stored.
 */
class PoseAnalyzer(
    private val onFrame: (PoseFrame) -> Unit,
) : ImageAnalysis.Analyzer {

    private val detector = PoseDetection.getClient(
        PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build(),
    )

    private var lastProcessedMs = 0L

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastProcessedMs < MIN_INTERVAL_MS) {
            imageProxy.close()
            return
        }
        lastProcessedMs = now

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val rotation = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotation)
        val rotated = rotation == 90 || rotation == 270
        val width = if (rotated) imageProxy.height else imageProxy.width
        val height = if (rotated) imageProxy.width else imageProxy.height

        detector.process(image)
            .addOnSuccessListener { pose ->
                onFrame(MlKitPoseMapper.toPoseFrame(pose, width, height, now))
            }
            .addOnFailureListener {
                onFrame(PoseFrame.EMPTY)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    /** Releases the detector. Call when the analysis use case is torn down. */
    fun close() {
        detector.close()
    }

    companion object {
        /** About 18 processed frames per second (TRD caps analysis near 15 to 20 fps). */
        private const val MIN_INTERVAL_MS = 55L
    }
}
