# HoloPulse Fit: Technical Requirements Document (TRD)

 
Platform: Android
Minimum SDK: 26 (Android 8.0)
Target SDK: 35
Document status: Locked tech stack, aligned with HoloPulse_Fit_Instruction.md

## 1. Technology Stack Summary

| Layer | Technology | Reason |
| --- | --- | --- |
| Language | Kotlin | Required as primary language by the instruction document. Java allowed only for interop or bridge code. |
| UI framework | Jetpack Compose (Material 3) | Modern declarative UI, fast iteration for glow effects, animated counters, and progress rings. Documented as the chosen approach per instruction rules. |
| Camera | CameraX (camera-camera2, camera-lifecycle, camera-view) | Lifecycle aware live preview plus ImageAnalysis use case for per frame pose inference. |
| Pose detection | Google ML Kit Pose Detection (base model) | On device, lightweight, 33 landmark output, streams frames from CameraX ImageAnalysis. |
| Local storage | Room (SQLite) | Structured storage for WorkoutSession, ExerciseSet, MotionPath, UserStats, and Settings with DAO based queries and easy deletion support. |
| Async and state | Kotlin Coroutines and Flow | Frame pipelines, DB reads, and reactive UI state through StateFlow. |
| Architecture | MVVM with repository layer | ViewModel per screen, single repository over Room DAOs, clean separation of camera, detection, and UI. |
| Navigation | Navigation Compose | Single activity, composable destinations for all 8 screen areas. |
| Animations | Compose animation APIs plus Lottie Compose | Countdown ring, glow progress, and Aura pulse use the provided Lottie placeholders where suitable. |
| JSON | kotlinx.serialization | Encodes landmark series for MotionPath storage and reads bundled sample data. |
| Build | Gradle (Kotlin DSL), AGP 8.x | Standard Android build with debug and release APK output. |

## 2. Key Dependencies

```
androidx.core:core-ktx
androidx.lifecycle:lifecycle-runtime-ktx
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.activity:activity-compose
androidx.compose:compose-bom
androidx.compose.material3:material3
androidx.navigation:navigation-compose
androidx.camera:camera-camera2
androidx.camera:camera-lifecycle
androidx.camera:camera-view
com.google.mlkit:pose-detection            (base streaming model)
androidx.room:room-runtime
androidx.room:room-ktx                     (with room-compiler via KSP)
org.jetbrains.kotlinx:kotlinx-serialization-json
com.airbnb.android:lottie-compose
```

## 3. Module and Package Structure

Single app module with feature oriented packages:

```
com.lumastride.holopulsefit
  ui/            Compose screens: launch, dashboard, library, workout, summary, history, ghost, settings
  ui/components/ GlowCard, ProgressRing, RepCounter, PoseOverlay, AuraTrailLayer, GestureHintBar
  ui/theme/      Color, Type, Theme (dark neon tokens from palette JSON)
  camera/        CameraX setup, PreviewView bridge, ImageAnalysis analyzer
  pose/          ML Kit detector wrapper, landmark models, confidence utilities
  gesture/       GestureDetector: hand raise, side swipe, both hands hold, idle state machine
  counting/      RepCounter engines: SquatCounter, JumpingJackCounter, PushUpCounter
  ghost/         MotionPath recorder, downsampler, GhostTrainer replay renderer
  data/          Room database, entities, DAOs, repository, sample data loader
  util/          Calorie estimator, streak calculator, time formatting
```

## 4. Camera and Pose Pipeline

1. CameraX binds Preview and ImageAnalysis to the workout screen lifecycle.
2. ImageAnalysis uses STRATEGY_KEEP_ONLY_LATEST so slow frames drop instead of queueing.
3. Frames pass to the ML Kit streaming pose detector on a background executor.
4. Landmark output (normalized coordinates plus inFrameLikelihood) feeds three consumers: pose overlay renderer, gesture state machine, and the active rep counter.
5. Frame throttling caps analysis near 15 to 20 processed frames per second to reduce heat and battery use.
6. Processing pauses in onPause and resumes in onResume. Camera is unbound when the workout screen closes.

## 5. Gesture Engine Requirements

1. Implement a state machine with debounce timers per gesture.
2. Hand raise: wrist landmark above shoulder landmark held stable for about 700 ms before triggering next or confirm.
3. Side swipe: horizontal wrist displacement past a normalized threshold with consistent direction across consecutive frames.
4. Both hands hold: both wrists above shoulders held for about 1 second, toggles pause or resume once per hold cycle.
5. Idle or step back: low landmark confidence or body partially out of frame switches to guidance mode and suspends counting.
6. Every gesture action must also be reachable through an on screen touch control.

## 6. Rep Counting Requirements

1. Each counter is a two phase (or open and closed) finite state machine that increments only on a complete cycle.
2. Squats: hip and shoulder vertical displacement relative to knee position defines down and up phases.
3. Jumping jacks: arm spread and leg spread angles define open and closed states.
4. Push-up approximation: shoulder and elbow vertical change with torso angle from a camera friendly side or high angle position, results labeled estimated when confidence average is low.
5. A confidence gate pauses counting and shows a message when landmark confidence drops below threshold.
6. Per rep confidence values average into ExerciseSet.confidenceAverage.

## 7. Ghost Trainer Requirements

1. During a session the recorder samples key landmark positions at a reduced rate (about 5 samples per second) into a landmark series with timestamps.
2. The series is downsampled and serialized to JSON, stored in the MotionPath entity.
3. Replay renders the saved series as a semi transparent skeleton overlay synchronized to timestamps, looped for the matching exercise.
4. Timing and path similarity cues compare live landmarks against replay positions.
5. Replay UI carries a persistent guidance only label. The bundled ghost-trainer-sample.json must load when no saved path exists.

## 8. Data and Privacy Requirements

1. Camera permission requested at first tracked workout with clear privacy wording. Tracking is blocked until granted.
2. No raw camera frames or video are written to storage at any point.
3. Only session summaries, exercise results, settings, and simplified motion paths persist.
4. History records are deletable individually and in bulk from settings.
5. Entity names in code match: WorkoutSession, ExerciseSet, MotionPath, UserStats, Settings.

## 9. Performance Requirements

1. Frame throttling and keep latest strategy in the analysis pipeline.
2. Aura Energy trails render on a single Compose Canvas layer with a bounded point buffer.
3. Reduced effects setting lowers trail length, glow radius, and disables Lottie loops.
4. Camera processing pauses when the app is backgrounded.
5. Low light, partial visibility, and permission denial states each show specific guidance UI.
6. Test on a physical Android device where possible. Emulator only checks must be documented in the test report.

## 10. Optional Python Helper Tools

Python scripts are allowed only as helper tools, not as any part of the app:

1. validate_motion_paths.py: checks sample and exported motion path JSON structure.
2. generate_test_data.py: produces extra sample workout history entries for review.

## 11. Build and Delivery Requirements

1. Gradle project builds a review ready debug or release APK without extra setup.
2. Source bundle keeps the package structure in section 3 with clear naming.
3. Deliverable folders: 01_apk, 02_source_code, 03_visual_assets, 04_screenshots, 05_docs, 06_sample_data.
4. Visual asset package copies into 03_visual_assets keeping group subfolders. Sample data JSON copies into 06_sample_data. App icons and vectors used by the app move into 02_source_code/app/src/main/res.
5. Documentation set includes setup instructions, build steps, test report, known limitations, and this document set.
