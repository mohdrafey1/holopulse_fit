package com.lumastride.holopulsefit.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.Lifecycle
import com.lumastride.holopulsefit.data.entities.Settings as HoloSettings
import com.lumastride.holopulsefit.ui.components.GlowCard
import com.lumastride.holopulsefit.ui.components.GuidanceBanner
import com.lumastride.holopulsefit.ui.components.PrimaryGlowButton
import com.lumastride.holopulsefit.ui.theme.CyanPulse

/** True when the camera runtime permission is currently granted. */
fun hasCameraPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

/**
 * Gates tracking behind the camera permission (appflow.md section 5). Shows a privacy explainer
 * before the first request, guidance with retry on denial, and a system settings link on permanent
 * denial. Tracking cannot start until permission is granted, satisfying acceptance criterion 2.
 *
 * @param onStateChange reports the resolved permission state so it can be persisted to Settings.
 */
@Composable
fun RequireCameraPermission(
    onStateChange: (String) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var granted by remember { mutableStateOf(hasCameraPermission(context)) }
    var permanentlyDenied by remember { mutableStateOf(false) }
    var requestedOnce by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        granted = isGranted
        if (isGranted) {
            onStateChange(HoloSettings.PERMISSION_GRANTED)
        } else {
            val showRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
            } ?: false
            permanentlyDenied = requestedOnce && !showRationale
            onStateChange(
                if (permanentlyDenied) HoloSettings.PERMISSION_PERMANENTLY_DENIED
                else HoloSettings.PERMISSION_DENIED,
            )
        }
    }

    // The user may grant permission in system settings and return, so re-check on resume.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val nowGranted = hasCameraPermission(context)
        if (nowGranted != granted) {
            granted = nowGranted
            if (nowGranted) onStateChange(HoloSettings.PERMISSION_GRANTED)
        }
    }

    if (granted) {
        content()
    } else {
        CameraPermissionRequest(
            permanentlyDenied = permanentlyDenied,
            onRequest = {
                requestedOnce = true
                launcher.launch(Manifest.permission.CAMERA)
            },
            onOpenSettings = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null),
                )
                context.startActivity(intent)
            },
        )
    }
}

@Composable
private fun CameraPermissionRequest(
    permanentlyDenied: Boolean,
    onRequest: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Camera Access",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        GlowCard(glowColor = CyanPulse, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "HoloPulse Fit uses your camera to track your body and count reps. Frames " +
                    "are analyzed on device only. No video or images are ever stored or uploaded.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (permanentlyDenied) {
            GuidanceBanner(
                message = "Camera permission is blocked. Enable it in system settings to start tracking.",
                modifier = Modifier.fillMaxWidth(),
            )
            PrimaryGlowButton(
                text = "Open Settings",
                icon = Icons.Filled.CameraAlt,
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            PrimaryGlowButton(
                text = "Enable Camera",
                icon = Icons.Filled.CameraAlt,
                onClick = onRequest,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
