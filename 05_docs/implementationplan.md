# HoloPulse Fit: Implementation Plan

 
Approach: 8 phases, each ending in a runnable checkpoint. Phases map one to one with Tracker.md.

## Phase 0: Project Setup

1. Create Android project: Kotlin, Jetpack Compose, min SDK 26, target SDK 35, Gradle Kotlin DSL.
2. Add dependencies: Compose BOM, Navigation Compose, CameraX, ML Kit Pose Detection, Room with KSP, kotlinx.serialization, Lottie Compose.
3. Set up package structure per TRD section 3.
4. Copy Visual Asset Package into 03_visual_assets keeping subfolders. Copy sample data into 06_sample_data. Move app icon and used vectors into app/src/main/res.
5. Build dark neon theme tokens from the palette JSON.
Checkpoint: empty app builds and launches with themed placeholder screen.

## Phase 1: Navigation Shell and Static Screens

1. Single activity with Navigation Compose and all 8 routes.
2. Launch screen with logo and pulse moment.
3. Dashboard, Workout Library, History, Ghost Trainer, and Settings as static layouts with placeholder data.
4. Core components: GlowCard, ProgressRing, StreakBadge, GestureHintBar shells.
Checkpoint: every screen area opens without crashes.

## Phase 2: Data Layer

1. Room database with the 5 entities, DAOs, indices, and cascade deletes per schema.md.
2. Repository layer plus sample data loader that seeds from 06_sample_data on first run.
3. UserStats streak calculator and calorie estimator utilities with unit tests.
4. Settings persistence wired to the Settings screen controls.
Checkpoint: dashboard and history render seeded sample sessions from Room.

## Phase 3: Camera and Pose Pipeline

1. Camera permission flow with privacy explainer, denial guidance, and settings link.
2. CameraX preview bound to the workout screen lifecycle.
3. ImageAnalysis with keep latest strategy, frame throttle, background executor.
4. ML Kit streaming pose detector wrapper emitting landmark frames as a Flow.
5. PoseOverlay rendering skeleton with confidence based opacity.
6. Pause processing on background, unbind on exit.
Checkpoint: live preview with stable skeleton overlay on a physical device.

## Phase 4: Rep Counting Engines

1. Counting state machine base with confidence gate and complete cycle rule.
2. SquatCounter: hip, knee, shoulder phase detection.
3. JumpingJackCounter: open and closed body state detection.
4. PushUpCounter: shoulder, elbow, torso angle approximation with estimated labeling.
5. RepCounter UI with pulse animation, timer, state text, guidance banner on tracking loss.
6. Unit tests with recorded landmark sequences from sample motion paths.
Checkpoint: squats and jumping jacks count correctly on device, push-up approximation counts with confidence feedback.

## Phase 5: Gesture Controls

1. Gesture state machine with per gesture debounce and hold timers.
2. Hand raise hold: next exercise or confirm.
3. Side swipe with direction and confidence checks: card and panel navigation.
4. Both hands hold: single toggle pause and resume per hold cycle.
5. Idle and step back handling: guidance mode, counting sensitivity reduced, no unexpected session end.
6. Touch fallback controls verified for every gesture action.
Checkpoint: all 4 gesture behaviors work in the camera workout screen under normal indoor lighting.

## Phase 6: Aura Energy and Ghost Trainer

1. AuraTrailLayer canvas with bounded buffers, speed scaled glow, cyan to violet fade.
2. Reduced effects mode wired to settings, verified smooth preview during tracking.
3. MotionPath recorder with downsampling and JSON serialization on session save.
4. Ghost Trainer replay screen and in workout overlay with timestamp sync, similarity cues, and the guidance only label.
5. Fallback load of ghost-trainer-sample.json when no saved path exists.
Checkpoint: effects react to movement without hiding data, replay works from both saved and sample paths.

## Phase 7: Session Loop and History Completion

1. Full session flow: countdown, tracking, pause, complete, summary save.
2. Session Summary screen with reps, duration, calories, streak update, save status.
3. History list, detail, single delete, and clear all with cascade behavior.
4. Dashboard live data: today progress, streak, recent sessions.
Checkpoint: complete workout loop from quick start to saved history entry.

## Phase 8: Hardening and Delivery

1. Handle low light, partial visibility, permission revocation, and background transitions per appflow.md section 11.
2. Performance pass: frame rate during effects, heat and battery observations, animation load.
3. Optional Python helpers: validate_motion_paths.py and generate_test_data.py.
4. Capture screenshots of all 8 screen areas and record the demo video of a full session with gestures.
5. Write setup instructions, build steps, test report, and known limitations.
6. Run the Quality Check list from the instruction document, including the em dash and en dash scan on all final documents.
7. Build the review ready APK and assemble the delivery folders: 01_apk, 02_source_code, 03_visual_assets, 04_screenshots, 05_docs, 06_sample_data.
Checkpoint: delivery checklist fully satisfied.

## Dependency Order

Phase 0 -> 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8. Phases 4 and 5 both depend on Phase 3 and can run in parallel after it. Phase 6 depends on Phases 3 and 2.

## Risk Notes

| Risk | Mitigation |
| --- | --- |
| Gesture false triggers during exercise movement | Hold timers, direction checks, single toggle per hold cycle, tune thresholds on device |
| Push-up landmarks unreliable from front camera | Camera friendly position guidance, estimated labeling, confidence gate |
| Effects hurting frame rate on low end devices | Bounded buffers, throttle, reduced effects mode, measure early in Phase 6 |
| Emulator cannot exercise camera flows | Physical device testing, document any emulator only checks in the test report |
