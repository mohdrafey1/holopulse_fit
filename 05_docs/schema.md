# HoloPulse Fit: Data Schema Document

 
Storage: Room (SQLite), single database `holopulse_fit.db`, all data local to the device.
Entity names are fixed by the instruction document: WorkoutSession, ExerciseSet, MotionPath, UserStats, Settings.

## 1. Entity Relationship Overview

```
WorkoutSession 1 ---- * ExerciseSet
WorkoutSession 1 ---- * MotionPath
UserStats  (single row aggregate)
Settings   (single row preferences)
```

## 2. Tables

### 2.1 WorkoutSession

| Column | Type | Constraints | Notes |
| --- | --- | --- | --- |
| id | TEXT | PRIMARY KEY | UUID string |
| date | TEXT | NOT NULL | ISO date, example 2026-06-13 |
| duration | INTEGER | NOT NULL | Seconds |
| totalReps | INTEGER | NOT NULL | Sum of counted reps |
| exerciseType | TEXT | NOT NULL | squats, jumping_jacks, pushup_approximation |
| caloriesEstimate | INTEGER | NOT NULL | Estimated kcal |
| completedStatus | TEXT | NOT NULL | completed, stopped_early |

### 2.2 ExerciseSet

| Column | Type | Constraints | Notes |
| --- | --- | --- | --- |
| id | TEXT | PRIMARY KEY | UUID string |
| sessionId | TEXT | NOT NULL, FK -> WorkoutSession.id, ON DELETE CASCADE | |
| exerciseName | TEXT | NOT NULL | |
| targetReps | INTEGER | NOT NULL | User selected target |
| countedReps | INTEGER | NOT NULL | Actual counted reps |
| confidenceAverage | REAL | NOT NULL | 0.0 to 1.0, low values mark results as estimated |

Index: `index_exerciseset_sessionId` on sessionId.

### 2.3 MotionPath

| Column | Type | Constraints | Notes |
| --- | --- | --- | --- |
| id | TEXT | PRIMARY KEY | UUID string |
| sessionId | TEXT | NOT NULL, FK -> WorkoutSession.id, ON DELETE CASCADE | |
| landmarkSeries | TEXT | NOT NULL | JSON array of sampled landmark frames, schema in section 3 |
| timestamps | TEXT | NOT NULL | JSON array of sample timestamps in ms, aligned with landmarkSeries |
| exerciseName | TEXT | NOT NULL | Used to match replay to the same workout |
| replayLabel | TEXT | NOT NULL | Guidance label shown during replay |

Index: `index_motionpath_sessionId` on sessionId.

### 2.4 UserStats (single row, fixed id 1)

| Column | Type | Constraints | Notes |
| --- | --- | --- | --- |
| id | INTEGER | PRIMARY KEY, always 1 | |
| totalSessions | INTEGER | NOT NULL DEFAULT 0 | |
| currentStreak | INTEGER | NOT NULL DEFAULT 0 | Consecutive workout days |
| bestStreak | INTEGER | NOT NULL DEFAULT 0 | |
| totalCalories | INTEGER | NOT NULL DEFAULT 0 | |
| lastWorkoutDate | TEXT | NULLABLE | ISO date, drives streak update logic |

### 2.5 Settings (single row, fixed id 1)

| Column | Type | Constraints | Notes |
| --- | --- | --- | --- |
| id | INTEGER | PRIMARY KEY, always 1 | |
| cameraPermissionState | TEXT | NOT NULL DEFAULT unknown | unknown, granted, denied, permanently_denied |
| auraIntensity | REAL | NOT NULL DEFAULT 1.0 | 0.0 to 1.0 slider value |
| ghostTrainerEnabled | INTEGER | NOT NULL DEFAULT 1 | Boolean |
| reducedEffectsEnabled | INTEGER | NOT NULL DEFAULT 0 | Boolean |

## 3. MotionPath landmarkSeries JSON Schema

Simplified skeletal samples only. No raw camera video is ever stored. Format matches the provided sample-motion-paths.json shape and extends it with the joints needed for replay:

```json
[
  {
    "timestamp_ms": 0,
    "joints": {
      "left_shoulder":  { "x": 0.42, "y": 0.25 },
      "right_shoulder": { "x": 0.58, "y": 0.25 },
      "left_elbow":     { "x": 0.38, "y": 0.35 },
      "right_elbow":    { "x": 0.62, "y": 0.35 },
      "left_wrist":     { "x": 0.36, "y": 0.45 },
      "right_wrist":    { "x": 0.64, "y": 0.45 },
      "left_hip":       { "x": 0.45, "y": 0.50 },
      "right_hip":      { "x": 0.55, "y": 0.50 },
      "left_knee":      { "x": 0.44, "y": 0.70 },
      "right_knee":     { "x": 0.56, "y": 0.70 },
      "left_ankle":     { "x": 0.44, "y": 0.90 },
      "right_ankle":    { "x": 0.56, "y": 0.90 }
    },
    "confidence": 0.92
  }
]
```

Rules:

1. Coordinates are normalized 0.0 to 1.0 relative to the analyzed frame.
2. Sampling rate near 5 samples per second, downsampled before save.
3. The bundled ghost-trainer-sample.json (simplified hip, knee, shoulder series) must also load through the same reader with defaults for missing joints.

## 4. DAO Operations

| DAO | Key operations |
| --- | --- |
| WorkoutSessionDao | insert, getAllByDateDesc (Flow), getById, getRecent(limit), deleteById, deleteAll |
| ExerciseSetDao | insert, getBySessionId |
| MotionPathDao | insert, getBySessionId, getLatestByExercise(exerciseName), deleteBySessionId |
| UserStatsDao | get (Flow), upsert |
| SettingsDao | get (Flow), upsert |

Cascade deletes remove ExerciseSet and MotionPath rows when a WorkoutSession is deleted. Clear history in settings runs deleteAll plus a UserStats recalculation.

## 5. Derived Logic Bound to Schema

1. Streak update: on session save, if lastWorkoutDate is yesterday then currentStreak increments, if today it stays, otherwise it resets to 1. bestStreak takes the max.
2. Calorie estimate: per exercise MET style factor times duration and rep count, stored on the session at save time.
3. Estimated flag in UI: confidenceAverage below 0.6 marks push-up results as estimated.

## 6. Sample Data Mapping

Provided sample files map into the schema for review seeding:

| Sample file | Target |
| --- | --- |
| sample-workout-history.json | WorkoutSession rows (session_id, date, exercise_type, duration_seconds, reps, calories_estimate, completed_status map to matching columns) |
| sample-motion-paths.json | MotionPath row with simplified series |
| ghost-trainer-sample.json | Ghost Trainer fallback replay source, loaded from assets, not required to insert into the database |
