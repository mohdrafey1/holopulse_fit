package com.lumastride.holopulsefit.pose

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

/**
 * Maps a raw ML Kit [Pose] into the app's [PoseFrame], converting pixel positions into normalized
 * 0..1 coordinates using the analyzed image dimensions. Only the landmarks the app uses are kept.
 */
object MlKitPoseMapper {

    private val mlKitTypeByLandmark: Map<PoseLandmarkType, Int> = mapOf(
        PoseLandmarkType.NOSE to PoseLandmark.NOSE,
        PoseLandmarkType.LEFT_SHOULDER to PoseLandmark.LEFT_SHOULDER,
        PoseLandmarkType.RIGHT_SHOULDER to PoseLandmark.RIGHT_SHOULDER,
        PoseLandmarkType.LEFT_ELBOW to PoseLandmark.LEFT_ELBOW,
        PoseLandmarkType.RIGHT_ELBOW to PoseLandmark.RIGHT_ELBOW,
        PoseLandmarkType.LEFT_WRIST to PoseLandmark.LEFT_WRIST,
        PoseLandmarkType.RIGHT_WRIST to PoseLandmark.RIGHT_WRIST,
        PoseLandmarkType.LEFT_HIP to PoseLandmark.LEFT_HIP,
        PoseLandmarkType.RIGHT_HIP to PoseLandmark.RIGHT_HIP,
        PoseLandmarkType.LEFT_KNEE to PoseLandmark.LEFT_KNEE,
        PoseLandmarkType.RIGHT_KNEE to PoseLandmark.RIGHT_KNEE,
        PoseLandmarkType.LEFT_ANKLE to PoseLandmark.LEFT_ANKLE,
        PoseLandmarkType.RIGHT_ANKLE to PoseLandmark.RIGHT_ANKLE,
    )

    /**
     * @param imageWidth width of the analyzed image after rotation.
     * @param imageHeight height of the analyzed image after rotation.
     */
    fun toPoseFrame(pose: Pose, imageWidth: Int, imageHeight: Int, timestampMs: Long): PoseFrame {
        if (imageWidth <= 0 || imageHeight <= 0) return PoseFrame.EMPTY
        val landmarks = HashMap<PoseLandmarkType, Landmark>(mlKitTypeByLandmark.size)
        for ((type, mlKitType) in mlKitTypeByLandmark) {
            val lm = pose.getPoseLandmark(mlKitType) ?: continue
            val position = lm.position
            landmarks[type] = Landmark(
                x = (position.x / imageWidth).coerceIn(0f, 1f),
                y = (position.y / imageHeight).coerceIn(0f, 1f),
                likelihood = lm.inFrameLikelihood,
            )
        }
        return PoseFrame(landmarks, timestampMs)
    }
}
