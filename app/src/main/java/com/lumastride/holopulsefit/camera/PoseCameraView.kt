package com.lumastride.holopulsefit.camera

import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.lumastride.holopulsefit.pose.PoseAnalyzer
import com.lumastride.holopulsefit.pose.PoseFrame
import java.util.concurrent.Executors

/**
 * Hosts the CameraX front camera preview and binds an ML Kit pose [PoseAnalyzer] to the composition
 * lifecycle (TRD section 4). Uses [LifecycleCameraController], which applies the keep only latest
 * strategy, pauses processing when the lifecycle stops, and unbinds automatically. Only the image
 * analysis use case is enabled; no capture or recording use case exists, so no frame is ever stored.
 *
 * @param onFrame invoked on the analysis executor thread for every processed frame.
 */
@Composable
fun PoseCameraView(
    onFrame: (PoseFrame) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnFrame = rememberUpdatedState(onFrame)

    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val analyzer = remember { PoseAnalyzer { frame -> latestOnFrame.value(frame) } }

    val controller = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(analysisExecutor, analyzer)
        }
    }

    DisposableEffect(lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
        onDispose {
            controller.unbind()
            analyzer.close()
            analysisExecutor.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                this.controller = controller
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
    )
}
