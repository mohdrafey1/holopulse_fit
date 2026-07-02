# HoloPulse Fit: Project Rules

 
Scope: binding rules for all code, assets, data, and documents in this project. Applies to human contributors and AI coding assistants.

## 1. Document Rules

1. No em dash and no en dash characters anywhere in final documents. Use commas, colons, or the word "to" for ranges.
2. prd.md mirrors HoloPulse_Fit_Instruction.md. Any scope change updates both the PRD and the Tracker decision log.
3. Entity names in every document and in code match exactly: WorkoutSession, ExerciseSet, MotionPath, UserStats, Settings.
4. Chosen technology approaches must be documented in trd.md, never left implicit.
5. Tracker.md updates in the same change set as the work it tracks.

## 2. Language and Code Rules

1. Kotlin is the primary language. Java only for interop or bridge code, and any such file needs a comment stating why.
2. Jetpack Compose for all UI. No new XML layouts except resources Compose cannot express (launcher icon adaptive XML, vector drawables).
3. MVVM: composables never touch DAOs or the camera directly. Flow of data is UI -> ViewModel -> Repository -> DAO or camera and pose layers.
4. Coroutines and Flow for async work. No callbacks bridged without a suspend or Flow wrapper.
5. Package placement follows TRD section 3. New features get a package, not a utils dump.
6. Names are descriptive and match domain terms from the instruction document (Aura, Ghost Trainer, MotionPath, rep, streak).
7. No dead code, no commented out blocks in committed source.

## 3. Camera and Pose Rules

1. All frame analysis runs on a background executor with keep latest strategy.
2. Analysis frame rate is throttled near 15 to 20 fps. No unthrottled inference.
3. Camera processing pauses on background and unbinds when the workout screen closes. No camera use outside the workout and replay contexts.
4. Raw camera frames or video are never written to disk, logs, or sample data folders. This rule has no exceptions.
5. Landmark confidence gates every consumer: overlay opacity, gesture triggers, and rep counting.

## 4. Gesture and Counting Rules

1. Every gesture trigger requires a stable hold or a clear direction plus confidence check. No single frame triggers.
2. Both hands hold toggles pause or resume at most once per hold cycle.
3. Rep counters increment only after a complete movement cycle.
4. Tracking loss switches to guidance mode. It never ends the session, never switches screens, and never counts reps.
5. Every gesture action must have a touch fallback control on the same screen.
6. Threshold values live in one constants file per engine so tuning never requires logic edits.

## 5. Data and Privacy Rules

1. Only these persist: session summaries, exercise results, settings, user stats, and simplified motion paths.
2. Motion paths store normalized joint coordinates and timestamps only, downsampled before save.
3. Users can delete single records and clear all history. Deletes cascade to ExerciseSet and MotionPath.
4. Camera permission wording explains on device processing and no video storage before the first tracked workout.
5. No analytics, no network calls, no cloud account requirement for core review.

## 6. Visual and UX Rules

1. Colors come from the palette JSON tokens. No hardcoded hex values outside the theme files.
2. Key workout data (rep count, timer, state, guidance) is never covered by glow effects, trails, or animations.
3. Reduced effects mode must visibly lower trail length, glow radius, and animation load.
4. Ghost Trainer replay always shows the guidance only label. No wording that implies medical or professional correction.
5. Provided assets are used as supplied. Placeholders may be replaced only when the file name marks them as placeholders.
6. Icons come from the provided icon set before any new icon is created.

## 7. Asset Handling Rules

1. Visual Asset Package lives in 03_visual_assets with original subfolder names.
2. Sample data JSON lives in 06_sample_data with original file names.
3. Only icons and vectors the app actually uses move into app/src/main/res.
4. UI reference and pose reference images stay in asset folders, never inside source packages.
5. No personal camera video in any folder of the deliverable.

## 8. Testing and Quality Rules

1. Counting engines and streak or calorie utilities require unit tests with recorded or sample landmark sequences.
2. Gesture behavior is verified on a physical device. Emulator only checks are documented in the test report.
3. Every screen area must open without a crash before any submission.
4. At least one complete workout session runs end to end, or a screen recording plus test notes covers it.
5. Before submission, run the full Quality Check list from the instruction document, including the em dash and en dash scan.

## 9. Delivery Rules

1. Folder layout: 01_apk, 02_source_code, 03_visual_assets, 04_screenshots, 05_docs, 06_sample_data.
2. 05_docs contains: prd.md, trd.md, appflow.md, design.md, schema.md, implementationplan.md, Tracker.md, rules.md, setup instructions, build steps, test report, known limitations.
3. Screenshots cover all 8 screen areas. Demo media shows a full session with gesture control.
4. The APK must install and run on a supported Android device without extra setup.
