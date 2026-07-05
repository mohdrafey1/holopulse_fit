# HoloPulse Fit

HoloPulse Fit is a demo ready Android fitness app that turns camera based body tracking into
touchless workout control. It counts bodyweight exercises from pose landmarks, lets you control the
session with body gestures instead of tapping the screen, shows glowing motion feedback, stores a
local workout history, and replays saved movement paths through an AI Ghost Trainer overlay.

Built for LumaStride Technologies as a motion first, dark neon home workout tool.

## Features

1. Dark neon dashboard with today progress, streak, quick start, and recent sessions.
2. Workout library for squats, jumping jacks, and push-up approximation, with selectable target reps.
3. Camera workout session with a live preview, pose skeleton overlay, rep counter, timer, and state.
4. Pose based rep counting that increments only on a complete movement cycle, with a confidence gate.
5. Touchless gesture control: hand raise to finish or confirm, both hands hold to pause or resume, side swipe to switch exercises. Every gesture has a touch fallback.
6. Aura Energy trails that glow behind the tracked joints and react to movement speed.
7. AI Ghost Trainer that replays a saved or sample motion path as a semi transparent guide, with a timing and similarity cue and a persistent guidance only label.
8. Local workout history with detail view, single delete, and clear all, plus streak and calorie tracking.
9. Settings for camera permission guidance, aura intensity, reduced effects, ghost trainer toggle, history management, and privacy notes.
10. A How it works guide, opened from Settings, that explains the gestures, rep counting, Aura Energy, and Ghost Trainer.
11. Privacy first: all processing is on device and no raw camera video or frames are ever stored.

## Architecture

HoloPulse Fit uses MVVM with a single repository over Room, and a single activity Compose UI.

```
Compose UI (screens, components)
        |  observes StateFlow
   ViewModel (per screen)
        |  calls
   HoloRepository
        |
   Room DAOs  +  Camera and Pose layer  +  Sample data loader
```

1. Composables never touch the database or the camera directly. They render immutable UI state from a ViewModel and send events back up.
2. ViewModels expose StateFlow of UI state and call the repository for data and the camera and pose layer for frames.
3. The camera and pose layer (CameraX plus an ML Kit analyzer) emits a normalized PoseFrame. The overlay, the rep counter, and the gesture engine each consume it, and each applies a confidence gate.
4. The repository is the single source of truth over the Room DAOs, seeds sample data on first run, and resolves Ghost Trainer replay sources.

## Tech stack

| Layer | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Camera | CameraX (camera2, lifecycle, view) |
| Pose detection | Google ML Kit Pose Detection, streaming base model |
| Storage | Room (SQLite) |
| Async and state | Kotlin Coroutines and Flow |
| Navigation | Navigation Compose |
| Serialization | kotlinx.serialization |
| Animation | Compose animation APIs and Lottie Compose |
| Build | Gradle Kotlin DSL, AGP 9.2.1, built-in Kotlin 2.2.10 |

## Folder structure

```
HoloPulse_Fit/
  app/                     Android app module
    src/main/java/com/lumastride/holopulsefit/
      ui/                  Compose screens
      ui/components/       GlowCard, ProgressRing, RepCounter, PoseOverlay, AuraTrailLayer, GhostSkeleton, ...
      ui/theme/            Dark neon tokens from the palette JSON
      ui/viewmodel/        One ViewModel per screen
      camera/              CameraX preview and permission flow
      pose/                ML Kit wrapper, landmark models, geometry, confidence
      gesture/             Gesture state machine
      counting/            Rep counting engines
      ghost/               Motion recorder, codec, replay engine
      data/                Room database, entities, DAOs, repository, sample loader
      util/                Calorie estimator, streak calculator, time formatting
      navigation/          Destinations and the Navigation Compose host
    src/main/assets/       Lottie animations, palette JSON, sample data JSON
    src/test/              Unit tests for counters, gestures, and utilities
  setup/                   Source of truth documents and the project tracker
  tools/                   Optional Python helper scripts
  01_apk/                  Review ready APK
  02_source_code/          Pointer to the source at the repository root
  03_visual_assets/        Visual Asset Package (branding, icons, palette, references, animations)
  04_screenshots/          Screenshots and demo recording
  05_docs/                 Documentation set
  06_sample_data/          Sample workout history and motion path JSON
```

## Required SDK versions

1. Minimum SDK: 26 (Android 8.0).
2. Target SDK: 35.
3. Compile SDK: android-36.1 (the platform installed in the build environment).
4. JDK: 17.

## Setup

See `05_docs/setup-instructions.md`. In short: open the repository root in Android Studio, let Gradle
sync (the first sync downloads dependencies and needs network access), and install the SDK platform
if prompted.

## Installation

Install the review APK on a connected device:

```
adb install -r 01_apk/HoloPulseFit-debug.apk
```

## Build and run

From the project root:

```
gradlew :app:assembleDebug        build a debug APK
gradlew :app:installDebug         install on a connected device
gradlew :app:testDebugUnitTest    run unit tests
```

See `05_docs/build-steps.md` for more.

## Permissions

HoloPulse Fit requests one runtime permission:

1. CAMERA. Used only to analyze your body pose on device for tracking and rep counting. The permission is requested with a clear explainer before the first tracked workout, and tracking cannot start until it is granted. No video or images are ever stored or uploaded. If permission is denied, the app shows guidance with a retry action and a link to system settings.

The camera feature is marked not required in the manifest so review devices without a camera can still install and open the non tracking screens.

## ML components

Pose tracking uses Google ML Kit Pose Detection in streaming mode.

1. CameraX binds a front camera preview and an image analysis use case to the workout screen lifecycle.
2. The analyzer runs on a background executor with the keep only latest strategy and throttles to about 18 processed frames per second to reduce heat and battery use.
3. Each frame is passed to the ML Kit streaming detector, and the 33 landmark output is mapped into a normalized PoseFrame.
4. Three consumers read each PoseFrame behind a confidence gate: the pose overlay, the rep counter, and the gesture state machine.
5. The Ghost Trainer records a downsampled skeletal path (about 5 samples per second, joint coordinates and timestamps only) and replays it as an interpolated, looping guide.

## Screenshots

Screenshots of all eight screen areas and a demo recording are placed in `04_screenshots/`. They are
captured on a device and are not produced by the build. See `04_screenshots/README.md` for the list.

## Future improvements

1. On device threshold tuning and a short calibration step for counting and gestures.
2. Exact overlay alignment using the camera image crop and rotation transform.
3. A profile view and body angle guidance to improve push-up counting reliability.
4. More exercises and guided multi exercise sessions.
5. Optional export and import of history and motion paths.
6. Pause the session timer precisely when the app is backgrounded.

## Troubleshooting

1. Gradle sync fails on first open. Ensure network access for the first dependency download and that the API 36 SDK platform is installed.
2. The workout screen shows the permission explainer repeatedly. Grant the camera permission, or if it was permanently denied, use the Open Settings action to enable it.
3. The skeleton or trails look offset from your body. This is expected minor drift from the approximate overlay mapping. Stand centered and a full step back so your whole body is in frame.
4. Reps do not count. Make sure your whole body is visible in good light. Counting pauses and shows guidance when tracking confidence is low.
5. The Ghost Trainer shows a sample path. This is the fallback when no saved motion path exists yet for that exercise. Complete a session to save your own path.
6. The APK is large. The ML Kit pose model and native libraries are bundled for fully on device inference with no network dependency.
