# HoloPulse Fit: Project Tracker

Status values: Not Started, In Progress, Blocked, Done
Rule: update this file in the same change set as the work it tracks. One task moves to In Progress at a time per person.

## Phase Status Overview

| Phase | Name | Status | Notes |
| --- | --- | --- | --- |
| 0 | Project Setup | Done | Compose toolchain builds a debug APK on device SDK |
| 1 | Navigation Shell and Static Screens | Done | All 8 routes plus bottom navigation |
| 2 | Data Layer | Done | Room, repository, seeding, streak and calorie unit tests green |
| 3 | Camera and Pose Pipeline | Done | Builds; live camera verification pending on a device |
| 4 | Rep Counting Engines | Done | Squat, jumping jack, push-up counters with unit tests |
| 5 | Gesture Controls | Done | Hand raise, both hands, swipe with unit tests; touch fallbacks present |
| 6 | Aura Energy and Ghost Trainer | Done | Aura trails, reduced effects, recorder, replay with sample fallback |
| 7 | Session Loop and History Completion | Done | Full loop saves to Room; history CRUD; dashboard live |
| 8 | Hardening and Delivery | In Progress | Docs, helpers, delivery folders done; screenshots and demo pending on device |

## Task Checklist

### Phase 0: Project Setup
- [x] Android project created (Kotlin, Compose, min SDK 26, target SDK 35)
- [x] Dependencies added (Compose, Navigation, CameraX, ML Kit Pose, Room, serialization, Lottie)
- [x] Package structure created per TRD
- [x] Visual assets copied to 03_visual_assets with subfolders intact
- [x] Sample data copied to 06_sample_data
- [x] App icon and used vectors moved into res folders (branding PNGs in res/drawable-nodpi)
- [x] Dark neon theme tokens built from palette JSON

### Phase 1: Navigation Shell and Static Screens
- [x] Navigation Compose with all 8 routes
- [x] Launch screen with branding
- [x] Dashboard static layout
- [x] Workout Library static layout
- [x] History static layout
- [x] Ghost Trainer static layout
- [x] Settings static layout
- [x] GlowCard, ProgressRing, StreakBadge, GestureHintBar components

### Phase 2: Data Layer
- [x] Room database with 5 entities and DAOs
- [x] Cascade deletes and indices
- [x] Repository layer
- [x] Sample data seeding from app assets (copied from 06_sample_data)
- [x] Streak calculator with unit tests
- [x] Calorie estimator with unit tests
- [x] Settings persistence wired to UI

### Phase 3: Camera and Pose Pipeline
- [x] Permission explainer with privacy wording
- [x] Denial guidance and system settings link
- [x] CameraX preview lifecycle binding
- [x] ImageAnalysis with keep latest and frame throttle
- [x] ML Kit pose detector wrapper emitting frames (StateFlow to consumers)
- [x] PoseOverlay skeleton rendering
- [x] Pause on background, unbind on exit

### Phase 4: Rep Counting Engines
- [x] Counting state machine base with confidence gate
- [x] SquatCounter (down and up phases)
- [x] JumpingJackCounter (open and closed states)
- [x] PushUpCounter with estimated labeling
- [x] Rep counter UI, timer, state text, guidance banner
- [x] Counter unit tests using recorded landmark sequences

### Phase 5: Gesture Controls
- [x] Gesture state machine with debounce and hold timers
- [x] Hand raise: next or confirm after stable hold
- [x] Side swipe with direction and confidence checks
- [x] Both hands hold: single pause or resume per hold cycle
- [x] Idle and step back guidance mode
- [x] Touch fallback verified for every gesture action (device check pending)

### Phase 6: Aura Energy and Ghost Trainer
- [x] AuraTrailLayer with speed scaled glow
- [x] Reduced effects mode wired to settings
- [x] MotionPath recorder with downsampling
- [x] Ghost replay screen with timestamp sync
- [x] In workout ghost overlay with similarity cues
- [x] Guidance only label on replay
- [x] Sample path fallback (from sample-motion-paths.json; see decision log)

### Phase 7: Session Loop and History Completion
- [x] Countdown, tracking, pause, complete flow
- [x] Session Summary with save to Room
- [x] Streak and stats update on save
- [x] History list and detail
- [x] Single delete and clear all with cascade
- [x] Dashboard live data

### Phase 8: Hardening and Delivery
- [x] Low light, partial visibility, revocation, background handling
- [x] Performance pass with reduced effects verification (throttle, bounded buffers, reduced mode)
- [x] Optional Python helper scripts
- [ ] Screenshots of all 8 screen areas (capture on device)
- [ ] Demo video of full session with gestures (capture on device)
- [x] Setup instructions and build steps
- [x] Test report and known limitations
- [x] Em dash and en dash scan on all final documents
- [x] Review ready APK built
- [x] Delivery folders assembled (01 to 06)

## Acceptance Criteria Verification

| # | Criterion | Status |
| --- | --- | --- |
| 1 | APK installs and runs without setup errors | Debug APK builds; on device install and run verification pending |
| 2 | Permission flow blocks tracking until granted | Implemented; tracking is gated behind RequireCameraPermission |
| 3 | Hand raise and side swipe trigger correct actions (or demo video plus notes) | Implemented with unit tests; on device or demo video pending |
| 4 | Squat and jumping jack counters reduce false counts | Implemented with hysteresis; unit tested |
| 5 | Push-up approximation with confidence feedback | Implemented; estimated label when confidence is low; unit tested |
| 6 | Aura effects never block key workout data | Implemented; trails render under the data layers |
| 7 | Ghost Trainer replays at least one saved or sample path | Implemented; sample fallback loads when no saved path exists |
| 8 | History saves, displays, and deletes correctly | Implemented; cascade delete and clear all recompute stats |
| 9 | Dark neon UI, readable contrast, consistent structure | Implemented; palette tokens only, shared components and top bar |
| 10 | All delivery files present | Folders assembled; screenshots and demo recording pending on device |

## Milestone Log

Append-only journal. After each completed milestone or phase, add a dated entry below with: what was built, decisions made, deviations from the docs with reasons, and known issues. Do not edit or delete past entries.

### Entry Template

```
### YYYY-MM-DD: Phase N complete (or milestone name)
Built: short summary of what was implemented
Decisions: choices made that the docs did not already cover
Deviations: any departure from prd, trd, appflow, design, schema, or rules, with reason
Known issues: open bugs, rough edges, or follow-up tasks
```

### Entries

### 2026-07-04: Phases 6 to 8 complete (build and docs)
Built: Phase 6 Aura and Ghost Trainer: an AuraTrailLayer canvas with bounded per joint buffers, speed scaled glow, and a cyan to violet fade, wired to the aura intensity and reduced effects settings; a MotionRecorder that downsamples the session to about 5 frames per second of joint coordinates and timestamps; a pure GhostReplay engine that interpolates and loops a saved or sample path and scores live similarity; a standalone Ghost Trainer replay screen with playback and the guidance only label; an in workout ghost overlay behind the live skeleton with a match cue; and the sample fallback. Phase 7 session loop: the full countdown to tracking to pause to complete flow saves a WorkoutSession, ExerciseSet, and downsampled MotionPath to Room and updates stats and streak, then the Session Summary loads the saved session; history list, detail, single delete, clear all, and live dashboard were already wired in Phase 2 and are confirmed. Phase 8 delivery: optional Python helpers (validate_motion_paths.py, generate_test_data.py), the README, the setup, build, test report, and known limitations docs, the delivery folders 01 to 06, and the em dash and en dash scan (clean). All 26 unit tests pass and the debug APK builds.
Decisions: The in workout ghost overlay and its similarity cue read the ghost frame produced by the same GhostReplay engine used by the replay screen. The session completed status is completed when reps reach the target, otherwise stopped early.
Deviations: Screenshots and the demo recording require a physical device and are pending; placeholders and a capture list are in 04_screenshots. The Python helpers are provided but were not run in the build environment because Python is not installed there; they use only the standard library.
Known issues: See 05_docs/known-limitations.md. The main items are on device threshold tuning, approximate overlay alignment, and the session timer continuing while backgrounded.

### 2026-07-04: Phases 3 to 5 complete
Built: Phase 3 camera and pose pipeline: a camera permission flow with a privacy explainer, denial guidance, and a system settings link; a CameraX front camera preview via LifecycleCameraController bound to the composition lifecycle; a throttled ML Kit streaming pose analyzer (about 18 fps, keep only latest, background executor) mapping landmarks into a PoseFrame; a PoseOverlay skeleton with confidence based opacity; automatic pause and unbind through the lifecycle. Phase 4 rep counting: a two phase finite state machine base with a confidence gate and complete cycle rule, plus SquatCounter (hip, knee, shoulder), JumpingJackCounter (arm raise), and PushUpCounter (elbow flexion, estimated when confidence is low), with the RepCounter pulse UI and eight counter unit tests. Phase 5 gestures: a hold and debounce gesture state machine for hand raise (finish or confirm), both hands hold (pause or resume once per cycle), and side swipe (switch exercise), with six unit tests. Every gesture has a touch fallback on the workout screen (pause, finish, previous, next).
Decisions: Jumping jack detection keys on the reliable front camera arm raise signal; leg spread accompanies the motion visually. The workout screen is wrapped by the shared top bar rather than fully immersive, keeping consistent structure. Camera permission state persists to Settings.
Deviations: None beyond the documented decisions. Physical device verification of live camera, counting accuracy, and gesture triggering is pending (recorded in known issues and the test report to follow in Phase 8).
Known issues: Overlay to preview alignment is approximate because the analysis image crop is not mapped one to one to the FILL_CENTER preview; acceptable for a demo overlay. Counting and gesture thresholds are tuned from synthetic sequences and will need on device tuning.

### 2026-07-03: Phase 0 complete
Built: Converted the scaffolded View based project to a pure Jetpack Compose app. Rewrote the Gradle setup (version catalog, root and app build scripts) to add Compose, Navigation, CameraX, ML Kit Pose Detection, Room with KSP, kotlinx.serialization, and Lottie. Created the full TRD package structure. Copied the Visual Asset Package into 03_visual_assets and sample data into 06_sample_data, and copied Lottie, palette, sample data, and branding assets into the app. Built the dark neon Compose theme (Color, Type, Theme) from the palette JSON. A themed placeholder screen builds into a debug APK.
Decisions: Renamed the base package from com.holopulsefit.app to com.lumastride.holopulsefit to match TRD section 3. compileSdk targets the installed android-36.1 platform while minSdk is 26 and targetSdk is 35 per TRD.
Deviations: See decision log for AGP 9 built-in Kotlin handling, compileSdk 36.1, and in-app iconography.
Known issues: None. Launcher uses the generated adaptive icon; the provided brand logo is shown on the launch screen.

### 2026-07-03: Phase 1 complete
Built: Single activity Navigation Compose host with all 8 routes and a bottom navigation bar for the four primary destinations. Launch screen with brand logo and aura pulse Lottie. Dashboard, Workout Library, History, History Detail, Ghost Trainer, and Settings screens. Camera Workout and Session Summary exist as navigable screens (camera workout is a documented placeholder until Phase 3). Core components: GlowCard, ProgressRing, StreakBadge, GestureHintBar, GuidanceBanner, PrimaryGlowButton, HoloTopBar, GhostSkeleton, plus a neonGlow modifier. Every screen area opens without crashes.
Decisions: UI facing state models (SessionUi, DashboardUi, HistoryUi, SettingsUi, SummaryUi) are kept separate from Room entities so composables never touch persistence.
Deviations: Workout and Summary screens are placeholders in Phase 1, replaced in Phases 3 and 7. Documented here per rules.md.
Known issues: Camera workout screen is a placeholder pending Phase 3.

### 2026-07-03: Phase 2 complete
Built: Room database holopulse_fit.db with the five fixed entities (WorkoutSession, ExerciseSet, MotionPath, UserStats, Settings), DAOs, indices, and foreign key cascade deletes. HoloRepository over the DAOs with first run sample seeding, session save, single delete, clear all, settings updates, and Ghost Trainer replay source resolution. Manual DI via AppContainer and HoloPulseApplication. Dashboard, History, History Detail, and Settings are now wired to live Room data through per screen ViewModels. StreakCalculator and CalorieEstimator with 12 passing unit tests. MotionPathCodec expands the simplified sample series into full skeleton frames.
Decisions: Dashboard and History live data were wired now (part of Phase 7) because the Phase 2 checkpoint requires rendering seeded sessions from Room. First run seeding is detected by an absent Settings row so clearing history does not reseed.
Deviations: The provided ghost-trainer-sample.json carries no coordinate frames, so the demonstrable fallback geometry comes from sample-motion-paths.json (which has a real simplified squat series); ghost-trainer-sample.json is still loaded for its label. See decision log.
Known issues: Camera, counting, gestures, aura, and full session save arrive in Phases 3 to 7.

## Blockers Log

| Date | Blocker | Owner | Resolution |
| --- | --- | --- | --- |
| | | | |

## Decision Log

| Date | Decision | Reason |
| --- | --- | --- |
| 2026-07-02 | UI: Jetpack Compose | Instruction allows any documented approach, Compose chosen for animation heavy dark neon UI |
| 2026-07-02 | Pose: ML Kit Pose Detection | On device, lightweight, streaming support with CameraX |
| 2026-07-02 | Storage: Room (SQLite) | Structured entities, cascade deletes, easy history management |
| 2026-07-03 | Base package renamed to com.lumastride.holopulsefit | Match TRD section 3 exactly; the scaffold used com.holopulsefit.app. Applied to namespace, applicationId, and sources |
| 2026-07-03 | AGP 9.2.1 uses built-in Kotlin (2.2.10); standalone kotlin-android plugin removed | The standalone plugin is incompatible with AGP 9 (casts to the removed BaseExtension). Compose, serialization, and KSP plugins pinned to 2.2.10. Set android.disallowKotlinSourceSets=false so KSP can register Room generated sources |
| 2026-07-03 | compileSdk = android-36.1, targetSdk = 35, minSdk = 26 | Only android-36.1 is installed in the environment; compiling against it avoids an SDK download while honoring the TRD min and target levels |
| 2026-07-03 | In-app icons use Compose Material Icons; provided SVGs kept as reference | The provided icon set is SVG, not Android vector drawables, and Compose renders Material vectors natively. SVGs remain in 03_visual_assets/icons_used per rules 7.4. No new custom icons were created |
| 2026-07-03 | Camera marked not required in the manifest | Lets review devices and emulators without a camera still install and open non tracking screens; camera absence is handled with guidance |
| 2026-07-03 | Ghost fallback geometry from sample-motion-paths.json | The provided ghost-trainer-sample.json has no coordinate frames. sample-motion-paths.json carries a real simplified squat series, so it is the demonstrable fallback; ghost-trainer-sample.json is still loaded for its label. Resolves a docs conflict (schema section 3 vs the actual asset) |
| 2026-07-03 | Deliverable 02_source_code maps to the repo root | The Android Studio project lives at the repo root; restructuring into a nested folder would break the Gradle project. Other delivery folders (03 to 06) are created at the root |
| 2026-07-04 | Optional target reps passed as a workout route query argument | Keeps the library selection stateless and avoids a global session config; the route stays workout/{exerciseType} with an optional target |
| 2026-07-04 | Hand raise gesture maps to finish, side swipe to switch exercise | In a single exercise session there is no card carousel, so the touchless actions map to finish or confirm and to exercise switching, each with a touch fallback on the workout screen |