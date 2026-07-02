# AD-0401: HoloPulse Fit Instructions

Category: App Development
Company: LumaStride Technologies
Domain: Gesture controlled mobile fitness and camera based motion interaction

## Project Intent

Build a demo ready Android fitness app package that turns camera based body tracking into touchless workout control, exercise counting, glow based motion feedback, workout history, and AI Ghost Trainer replay.

## Company Direction

LumaStride Technologies creates motion first mobile wellness tools, camera assisted interaction flows, and readable dark neon training interfaces for home workout use. HoloPulse Fit should be clear during active movement and should let users control workouts without stopping to tap the screen often.

## Product Summary

HoloPulse Fit is a demo ready Android fitness app package that lets users control workout sessions with body gestures instead of constant screen taps. Camera based pose tracking supports exercise counting, touchless workout navigation, glowing motion feedback, local workout history, and guided replay through an AI Ghost Trainer overlay. The build should prove the main camera workout flow through an APK, source bundle, screenshots, sample data, and test notes.

Touch controls may exist as fallback actions, but the main demonstration should show workout flow controlled by stable gesture holds, pose checkpoints, and visual feedback that remains readable during exercise.

## Product Goals

1. Create a camera based Android workout experience that is different from standard tap only workout trackers.
2. Let users control workout flow with simple body gestures detected through the device camera.
3. Count selected bodyweight exercises with pose based movement checkpoints.
4. Show real time feedback through animated counters, glow progress, pose overlays, and movement trails.
5. Store workout history so users can review sessions, streaks, calories, and progress summaries.
6. Replay saved movement paths through a transparent AI Ghost Trainer guide.

## Target Users

1. Home fitness users who prefer guided workouts without touching the phone during exercise.
2. Tech friendly users interested in gesture control and camera based tracking.
3. Beginners who need simple visual guidance for bodyweight exercises.
4. Review audiences who need to understand touchless fitness interaction through APK, screenshots, demo video, and sample data.

## Core User Flow

1. User opens the app and lands on a dark neon dashboard with today status, streak, quick start, and recent activity.
2. Workout selection displays animated cards for available exercises and guided sessions.
3. Camera permission is requested with clear privacy wording before the first tracked workout.
4. Workout session opens with live camera preview, pose overlay, rep counter, timer, and gesture hints.
5. User performs exercises while the app counts reps, detects simple gestures, and shows Aura Energy effects.
6. Session ends with summary details, estimated calories, completed reps, duration, and saved motion data.
7. Later sessions can load AI Ghost Trainer replay as a transparent guide based on saved skeletal paths.

## Provided Assets And Inputs

A Visual Asset Package is provided with this task and should be used as the starting point. Do not recreate assets that are already supplied unless a final replacement is needed. When a provided file is named clearly as a placeholder, it may be replaced with final custom graphics during development.

Visual Asset Package contents:

1. `branding`: LumaStride logo (PNG and SVG) and HoloPulse Fit app icon.
2. `ui_reference`: landing page, camera workout, and Ghost Trainer layout references.
3. `exercise_pose_reference`: squat, jumping jack, push-up, and gesture pose references.
4. `icons_used`: camera, workout, history, settings, replay, timer, energy, gesture, streak, and calories icons, plus a contact sheet.
5. `animations`: Lottie style JSON placeholders for countdown ring, glow progress, and Aura pulse.
6. `palette`: color palette JSON and swatch image.
7. `sample_data`: sample workout history, sample motion paths, and Ghost Trainer sample JSON.

Asset placement when building the deliverable:

1. Copy the visual asset package into the project `03_visual_assets/` folder, keeping the same group subfolders (`branding`, `ui_reference`, `exercise_pose_reference`, `icons_used`, `animations`, `palette`).
2. Copy the provided sample data JSON files into the project `06_sample_data/` folder. These already match the required sample data file names.
3. Move app icons and Android vector resources that the app actually uses into the Android resource folders under `02_source_code/app/src/main/res/`.

## Core Work To Complete

1. Build a review ready Android app for HoloPulse Fit.
2. Implement camera permission flow, live preview, and pose landmark tracking.
3. Add gesture controls for hand raise, side swipe movement, pause, resume, and next workout actions.
4. Count squats, jumping jacks, and push-up approximation using pose based movement checkpoints.
5. Create launch, dashboard, workout library, camera workout, session summary, history, Ghost Trainer, and settings screen areas.
6. Add Aura Energy motion trails and reduced effects control.
7. Store workout sessions, streaks, calorie estimates, settings, and simplified motion paths.
8. Prepare APK, source files, visual assets, screenshots, setup notes, build steps, test report, sample data, and documentation.

## Functional Requirements

### Dashboard

1. Show app logo, greeting, daily progress, streak counter, completed sessions, and quick start action.
2. Display recent workout summary cards with duration, reps, calories, and date.
3. Use glowing cards, readable spacing, and short labels that remain clear during active use.

### Workout Library

1. List supported workouts for squats, jumping jacks, and push-up approximation.
2. Include short instructions, target rep options, estimated effort, and start action.
3. Allow users to move between workout cards with touch controls and supported touchless gestures.

### Camera Workout Session

1. Show live camera preview with pose skeleton overlay and landmark based movement feedback.
2. Present rep counter, timer, exercise name, current state, next action, and pause option.
3. Keep touch fallback controls available for safety, accessibility, and failed gesture cases.
4. Guide users when the body is not fully visible or tracking confidence drops.

### Aura Energy Visualization

1. Add glowing trails around tracked joints during movement.
2. Increase glow intensity based on movement speed or rep momentum.
3. Keep visual effects lightweight so camera preview and counting remain smooth.
4. Allow effects to be reduced from settings for comfort and device performance.

### AI Ghost Trainer Replay

1. Save simplified skeletal motion paths from completed sessions.
2. Replay previous motion paths as a transparent guide during matching workouts.
3. Let users compare current movement against the ghost guide through timing and path similarity cues.
4. Clearly label replay as guidance, not medical or professional fitness correction.

### Workout History

1. Store completed sessions with date, duration, exercise type, reps, calories estimate, and streak impact.
2. Show history list, detail view, and simple progress summaries.
3. Allow deletion of local workout records from settings or history detail.

## Gesture Controls

| Gesture | Expected Action | Acceptance Point |
| --- | --- | --- |
| Hand raise | Move to the next exercise or confirm a highlighted action. | Action triggers only after a stable raised hand pose is detected for a short hold. |
| Side swipe movement | Switch workout cards or move between session panels. | Detection reduces accidental triggers by checking direction and movement confidence. |
| Both hands hold | Pause or resume workout session. | Session state changes only once per hold cycle to prevent repeated toggles. |
| Step back or idle state | Show guidance and reduce counting sensitivity. | App gives clear feedback without ending the session unexpectedly. |

### Gesture Detection Rules

1. Hand raise should trigger next exercise or confirm the highlighted action only after a short stable hold.
2. Side swipe movement should move between workout cards or panels only when direction and movement confidence are clear.
3. Both hands hold should pause or resume the active session without repeated toggles.
4. Tracking loss should show guidance instead of counting reps or switching screens.
5. Touch controls should remain available as fallback actions.

## Exercise Counting

| Exercise | Tracking Logic | Expected Output |
| --- | --- | --- |
| Squats | Use hip, knee, and shoulder landmark changes to detect down and up movement phases. | Count one rep after a complete down and up cycle with basic form confidence. |
| Jumping jacks | Track arm spread, leg spread, and return position across repeated cycles. | Count one rep after open and closed body positions are detected. |
| Push-up approximation | Use shoulder, elbow, and torso movement angle changes from a camera friendly position. | Count approximate reps and label low confidence results as estimated. |

### Exercise Tracking Rules

1. Squat counting should detect a down phase and an up phase using hip, knee, and shoulder landmarks.
2. Jumping jack counting should detect open and closed body states using arms and legs.
3. Push-up approximation should use shoulder, elbow, and torso movement changes and show estimated confidence when needed.
4. Rep counts should update only after a complete movement cycle.
5. Low confidence tracking should pause counting and show a clear message.

## Technology Requirements

1. Android source should use Kotlin as the primary language, with Java allowed only for interop or bridge code, when the final source is readable and buildable.
2. UI may use Jetpack Compose, XML views, or a comparable Android UI approach. Chosen approach must be documented.
3. Camera handling may use CameraX or another Android camera approach that supports live preview and frame analysis.
4. Pose detection may use ML Kit, MediaPipe, or a comparable lightweight pose landmark library.
5. Local data may use Room, SQLite, JSON files, or Firebase when the storage choice is documented and reviewable.
6. Python files are optional helper tools for sample data checks, motion path validation, or test data generation. They are not a replacement for the Android app.

## Asset Handling Rules

1. Use the Visual Asset Package for logo, UI reference layouts, exercise pose references, icons, palette files, animation placeholders, and sample data.
2. Keep app icons and Android vector resources inside the Android resource folders when used by the app.
3. Keep UI reference images and pose reference images in review asset or visual asset folders.
4. Do not store personal camera video in source folders or sample data folders.
5. Use placeholder assets only when named clearly and safe to replace.

## Visual Design Rules

1. Use a dark interface with electric blue, cyan, violet, and soft white highlights.
2. Keep text readable during movement and avoid heavy effects over key workout data.
3. Apply glowing cards, animated progress rings, pose overlays, and rep count feedback consistently.
4. Make Aura Energy effects react to movement while keeping the camera preview smooth.
5. Provide a reduced effects option for comfort and device performance.

## Data And Privacy Rules

1. Request camera permission with clear wording before starting the first tracked workout.
2. Do not store raw camera video for normal workout history.
3. Save only session summaries, exercise results, settings, and simplified motion paths needed for Ghost Trainer replay.
4. Allow users to delete local workout records.
5. Keep data names aligned with the entities: WorkoutSession, ExerciseSet, MotionPath, UserStats, and Settings.

## Data Model

| Entity | Key fields |
| --- | --- |
| WorkoutSession | id, date, duration, totalReps, exerciseType, caloriesEstimate, completedStatus |
| ExerciseSet | id, sessionId, exerciseName, targetReps, countedReps, confidenceAverage |
| MotionPath | id, sessionId, landmarkSeries, timestamps, exerciseName, replayLabel |
| UserStats | totalSessions, currentStreak, bestStreak, totalCalories, lastWorkoutDate |
| Settings | cameraPermissionState, auraIntensity, ghostTrainerEnabled, reducedEffectsEnabled |

## Performance Rules

1. Use frame throttling or efficient inference settings to reduce heat and battery use.
2. Pause camera processing when the app is not active.
3. Keep animation layers lightweight during active tracking.
4. Handle low light, partial body visibility, and permission denial with clear guidance.
5. Test the app on a standard Android device when possible, and document any emulator only checks.

## Non Functional Requirements

| Requirement | Target |
| --- | --- |
| Performance | Camera preview and pose overlay should stay smooth on standard Android devices during a workout session. |
| Gesture timing | Gesture actions and rep counter updates should appear after stable detection without long wait times. |
| Battery and heat care | Use frame throttling, efficient inference settings, and pause processing when the app is not active. |
| Privacy | Explain camera use clearly and avoid storing raw camera video for normal workout history. |
| Reliability | Handle low light, partial body visibility, permission denial, and tracking loss with clear guidance. |
| Accessibility | Provide readable contrast, touch fallback controls, captions for guidance, and reduced effect options. |
| Reviewability | Include APK, source files, screenshots, sample data, demo media, and test notes so core behavior can be checked without private footage. |

## Screen Requirements

1. Launch screen with HoloPulse Fit identity.
2. Dashboard with quick start, streaks, and recent sessions.
3. Workout library with exercise cards and session options.
4. Camera workout screen with skeleton overlay, counter, timer, gestures, and Aura Energy visualization.
5. Session summary with rep count, duration, calories estimate, streak update, and save status.
6. History screen with completed sessions, details, and delete action.
7. Ghost Trainer replay screen or overlay mode for matching saved movement paths.
8. Settings screen for camera permission guidance, effects intensity, history management, and privacy notes.

## Required Project Output

1. Review ready Android APK.
2. Android source code with organized modules and clear naming.
3. Gesture detection and pose tracking implementation or documented equivalent approach.
4. Workout counting logic for squats, jumping jacks, and push-up approximation.
5. Dark neon UI screens and optimized visual assets.
6. Workout history storage and summary screens.
7. AI Ghost Trainer replay implementation with sample motion path data.
8. Visual asset package with logo, UI references, exercise pose references, icons, palette, and animation placeholders.
9. Setup instructions, build steps, testing notes, screenshots, demo video or screen recording, and known limitations.

## Acceptance Criteria

1. App installs and runs on supported Android devices through the supplied APK without setup errors.
2. Camera permission flow is clear and workout tracking cannot start until permission is granted.
3. Hand raise and side swipe gestures trigger the correct app actions under normal indoor lighting or are shown through demo video and test notes.
4. Squat and jumping jack counters work with visible movement phases and reduce repeated false counts.
5. Push-up approximation is implemented with confidence feedback.
6. Aura Energy effects appear during tracked movement and do not block important workout information.
7. AI Ghost Trainer can replay at least one saved skeletal motion path from a previous or sample session.
8. Workout history saves, displays, and deletes local records correctly.
9. UI uses dark neon panels, readable contrast, stable labels, and consistent screen structure.
10. Project files include APK, source code, visual assets, setup instructions, sample data, screenshots, and test notes.

## Out of Scope

1. Medical diagnosis, injury risk prediction, or professional fitness certification features.
2. Wearable device integration and external hardware requirements.
3. Real money purchases, paid plans, or commerce flows.
4. Social network features, public leaderboards, or community feeds.
5. Advanced nutrition planning or meal tracking.
6. Mandatory cloud account creation for core review.

## Delivery Checklist

1. Review ready Android APK.
2. Android project source code with clear structure and naming.
3. Gesture detection and pose tracking implementation or documented equivalent approach.
4. Workout counting logic for the required exercises.
5. Dark neon UI screens, animations, and visual effect assets.
6. Local workout history and sample workout data.
7. AI Ghost Trainer replay data flow and visual overlay.
8. Visual asset package with logo, UI references, exercise pose references, icons, palette, and animation placeholders.
9. Screenshots, demo video or short screen recording, setup instructions, build steps, test report, and known limitations.

## Quality Check Before Submission

1. Confirm all core screen areas open without crashes.
2. Run at least one complete workout session or provide a screen recording plus test notes.
3. Test every gesture action in the camera workout screen or document verified behavior with sample data.
4. Confirm Aura Energy effects appear during movement and do not hide key data.
5. Confirm Ghost Trainer can replay a saved or sample skeletal path.
6. Review visuals against the Asset Document and Visual Asset Package.
7. Confirm final documents do not contain em dash or en dash characters.