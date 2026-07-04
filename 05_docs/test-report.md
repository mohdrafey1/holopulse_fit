# HoloPulse Fit: Test Report

Date: 2026-07-04

## Automated unit tests

Run with `gradlew :app:testDebugUnitTest`. All pass.

| Suite | Tests | Focus |
| --- | --- | --- |
| CalorieEstimatorTest | 4 | Calorie estimate lands near sample values, scales with reps and duration, never negative |
| StreakCalculatorTest | 8 | Streak increments on consecutive days, holds same day, resets on a gap, best streak tracks max, run computation from a full date set |
| RepCountersTest | 8 | Squat, jumping jack, and push-up counters count complete cycles only, reject half reps, gate on low confidence, average per rep confidence, reset |
| GestureDetectorTest | 6 | Hand raise fires after a stable hold, both hands toggles once per hold cycle, side swipe requires consistent direction, low confidence and jitter produce no gesture |

Total: 26 tests, 0 failures, 0 errors.

The counting and gesture suites use synthetic landmark frame sequences that model the down and up, open and closed, and raised hand poses, so the state machine logic is verified without a camera.

## Build verification

`gradlew :app:assembleDebug` produces a debug APK at each phase checkpoint. The full dependency set (Compose, CameraX, ML Kit Pose Detection, Room with KSP, kotlinx.serialization, Lottie) resolves and compiles on AGP 9.2.1 with the bundled Kotlin 2.2.10.

## Manual and on device checks

The following require a physical Android device with a front camera and are to be completed on device. They are listed here so a reviewer can reproduce them, and can be captured in a short screen recording per the instruction document.

1. Camera permission flow: the explainer appears before the first tracked workout, and tracking cannot start until permission is granted.
2. Live preview with the pose skeleton overlay is smooth and stable.
3. Squat and jumping jack counters increment on visible movement phases and avoid repeated false counts.
4. Push-up approximation counts with an estimated label when confidence is low.
5. Hand raise, both hands hold, and side swipe gestures trigger the correct actions under normal indoor lighting. Each has a touch fallback that is also verified.
6. Aura Energy trails appear during movement and do not cover the rep count, timer, or state text.
7. Ghost Trainer replays a saved path, and falls back to the bundled sample path when none exists.
8. History saves, displays, and deletes local records, and clear all resets stats.

## Emulator only note

Emulator camera input is a synthetic scene, so pose tracking, counting, and gestures are best verified on a physical device. Any check performed only on an emulator is noted as such when recorded.
