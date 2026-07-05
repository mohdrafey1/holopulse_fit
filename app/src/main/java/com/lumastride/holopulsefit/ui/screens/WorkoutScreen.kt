package com.lumastride.holopulsefit.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import com.lumastride.holopulsefit.camera.PoseCameraView
import com.lumastride.holopulsefit.camera.RequireCameraPermission
import com.lumastride.holopulsefit.pose.PoseConfidence
import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.ui.components.AuraTrailLayer
import com.lumastride.holopulsefit.ui.components.GestureHint
import com.lumastride.holopulsefit.ui.components.GestureHintBar
import com.lumastride.holopulsefit.ui.components.GhostSkeleton
import com.lumastride.holopulsefit.ui.components.GuidanceBanner
import com.lumastride.holopulsefit.ui.components.PoseOverlay
import com.lumastride.holopulsefit.ui.components.neonGlow
import com.lumastride.holopulsefit.ui.components.ProgressRing
import com.lumastride.holopulsefit.ui.components.RepCounter
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow
import com.lumastride.holopulsefit.ui.theme.ScrimDark
import com.lumastride.holopulsefit.ui.theme.WarningPulse
import com.lumastride.holopulsefit.ui.viewmodel.WorkoutPhase
import com.lumastride.holopulsefit.ui.viewmodel.WorkoutUiState
import com.lumastride.holopulsefit.util.TimeFormat

/**
 * Camera Workout Session (design.md Camera Workout, appflow.md section 6). Live camera preview with
 * a pose skeleton overlay, a top strip for timer and state, a large rep counter clear of the body, a
 * countdown ring, guidance on tracking loss, and touch pause and stop controls. Tracking is gated
 * behind the camera permission.
 */
@Composable
fun WorkoutScreen(
    state: WorkoutUiState,
    poseFrames: StateFlow<PoseFrame>,
    ghostFrames: StateFlow<Map<String, Offset>>,
    onPoseFrame: (PoseFrame) -> Unit,
    onTogglePause: () -> Unit,
    onStop: () -> Unit,
    onNextExercise: () -> Unit,
    onPrevExercise: () -> Unit,
    onPermissionState: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    RequireCameraPermission(onStateChange = onPermissionState) {
        Box(modifier = modifier.fillMaxSize()) {
            PoseCameraView(onFrame = onPoseFrame, modifier = Modifier.fillMaxSize())
            // Ghost Trainer guide behind the live skeleton.
            if (state.ghostEnabled) {
                GhostReplayLayer(ghostFrames)
            }
            // Aura Energy trails render under the data layers so nothing is hidden.
            AuraTrailLayer(
                poseFrames = poseFrames,
                auraIntensity = state.auraIntensity,
                reducedEffects = state.reducedEffects,
                modifier = Modifier.fillMaxSize(),
            )
            PoseOverlayLayer(poseFrames)

            // Top strip: timer and state.
            TopStrip(
                state = state,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(12.dp),
            )

            // Rep counter, anchored top end away from the body center.
            if (state.phase != WorkoutPhase.COUNTDOWN) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 64.dp, end = 20.dp),
                ) {
                    RepCounter(count = state.reps, target = state.targetReps)
                }
            }

            // Brief confirmation that a gesture registered (and is in its cooldown).
            GestureFeedbackPill(
                text = state.gestureFeedback,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp),
            )

            // Countdown ring while the user steps into frame.
            if (state.phase == WorkoutPhase.COUNTDOWN) {
                ProgressRing(
                    progress = 1f - (state.countdown - 1) / 5f,
                    diameter = 180.dp,
                    modifier = Modifier.align(Alignment.Center),
                    centerContent = {
                        Text(
                            text = "${state.countdown}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = ElectricBlueGlow,
                            fontWeight = FontWeight.Black,
                        )
                    },
                )
            }

            // Bottom controls and guidance.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.guidanceMessage != null) {
                    GuidanceBanner(message = state.guidanceMessage, modifier = Modifier.fillMaxWidth())
                } else {
                    Text(
                        text = state.nextActionHint,
                        style = MaterialTheme.typography.labelLarge,
                        color = CyanPulse,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ScrimDark)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }

                // Touchless gesture hints; every action also has a touch control below.
                GestureHintBar(
                    hints = listOf(
                        GestureHint(Icons.Filled.PanTool, "Both hands: pause"),
                        GestureHint(Icons.Filled.BackHand, "Raise hand: finish"),
                        GestureHint(Icons.Filled.Swipe, "Swipe: switch"),
                    ),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Touch fallback for the swipe gesture: previous exercise.
                    IconButton(onClick = onPrevExercise) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous exercise", tint = CyanPulse)
                    }
                    Button(
                        onClick = onTogglePause,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPulse,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                    ) {
                        val paused = state.phase == WorkoutPhase.PAUSED
                        Icon(
                            imageVector = if (paused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = if (paused) "Resume" else "Pause",
                        )
                        Text(if (paused) "  Resume" else "  Pause")
                    }
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningPulse,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                    ) {
                        Icon(Icons.Filled.Stop, contentDescription = "Stop")
                        Text("  Finish")
                    }
                    // Touch fallback for the swipe gesture: next exercise.
                    IconButton(onClick = onNextExercise) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Next exercise", tint = CyanPulse)
                    }
                }
            }
        }
    }
}

/** A short lived glowing pill confirming the gesture that just fired. */
@Composable
private fun GestureFeedbackPill(text: String?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = text != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .neonGlow(glowColor = ElectricBlueGlow, cornerRadius = 20.dp, glowRadius = 10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ScrimDark)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = ElectricBlueGlow,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = text.orEmpty(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/** Collects pose frames in an isolated composable so only the overlay recomposes per frame. */
@Composable
private fun PoseOverlayLayer(poseFrames: StateFlow<PoseFrame>) {
    val frame by poseFrames.collectAsStateWithLifecycle()
    PoseOverlay(
        frame = frame,
        modifier = Modifier.fillMaxSize(),
        mirrorX = true,
        minLikelihood = PoseConfidence.OVERLAY_MIN,
    )
}

/** Renders the semi transparent Ghost Trainer guide behind the live skeleton. */
@Composable
private fun GhostReplayLayer(ghostFrames: StateFlow<Map<String, Offset>>) {
    val frame by ghostFrames.collectAsStateWithLifecycle()
    if (frame.isNotEmpty()) {
        // The live overlay is mirrored, so mirror the ghost to match.
        val mirrored = frame.mapValues { Offset(1f - it.value.x, it.value.y) }
        GhostSkeleton(frame = mirrored, modifier = Modifier.fillMaxSize(), alpha = 0.4f)
    }
}

@Composable
private fun TopStrip(state: WorkoutUiState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ScrimDark)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LabeledValue(label = "Time", value = TimeFormat.duration(state.elapsedSeconds))
        LabeledValue(label = "State", value = state.stateText)
        if (state.ghostEnabled) {
            LabeledValue(label = "Match", value = "${(state.similarity * 100).toInt()}%")
        } else {
            LabeledValue(label = "Confidence", value = "${(state.confidenceAverage * 100).toInt()}%")
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = ElectricBlueGlow)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
