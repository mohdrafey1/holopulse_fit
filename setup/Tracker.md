# HoloPulse Fit: Project Tracker

Status values: Not Started, In Progress, Blocked, Done
Rule: update this file in the same change set as the work it tracks. One task moves to In Progress at a time per person.

## Phase Status Overview

| Phase | Name | Status | Notes |
| --- | --- | --- | --- |
| 0 | Project Setup | Not Started | |
| 1 | Navigation Shell and Static Screens | Not Started | |
| 2 | Data Layer | Not Started | |
| 3 | Camera and Pose Pipeline | Not Started | |
| 4 | Rep Counting Engines | Not Started | |
| 5 | Gesture Controls | Not Started | |
| 6 | Aura Energy and Ghost Trainer | Not Started | |
| 7 | Session Loop and History Completion | Not Started | |
| 8 | Hardening and Delivery | Not Started | |

## Task Checklist

### Phase 0: Project Setup
- [ ] Android project created (Kotlin, Compose, min SDK 26, target SDK 35)
- [ ] Dependencies added (Compose, Navigation, CameraX, ML Kit Pose, Room, serialization, Lottie)
- [ ] Package structure created per TRD
- [ ] Visual assets copied to 03_visual_assets with subfolders intact
- [ ] Sample data copied to 06_sample_data
- [ ] App icon and used vectors moved into res folders
- [ ] Dark neon theme tokens built from palette JSON

### Phase 1: Navigation Shell and Static Screens
- [ ] Navigation Compose with all 8 routes
- [ ] Launch screen with branding
- [ ] Dashboard static layout
- [ ] Workout Library static layout
- [ ] History static layout
- [ ] Ghost Trainer static layout
- [ ] Settings static layout
- [ ] GlowCard, ProgressRing, StreakBadge, GestureHintBar components

### Phase 2: Data Layer
- [ ] Room database with 5 entities and DAOs
- [ ] Cascade deletes and indices
- [ ] Repository layer
- [ ] Sample data seeding from 06_sample_data
- [ ] Streak calculator with unit tests
- [ ] Calorie estimator with unit tests
- [ ] Settings persistence wired to UI

### Phase 3: Camera and Pose Pipeline
- [ ] Permission explainer with privacy wording
- [ ] Denial guidance and system settings link
- [ ] CameraX preview lifecycle binding
- [ ] ImageAnalysis with keep latest and frame throttle
- [ ] ML Kit pose detector wrapper emitting Flow
- [ ] PoseOverlay skeleton rendering
- [ ] Pause on background, unbind on exit

### Phase 4: Rep Counting Engines
- [ ] Counting state machine base with confidence gate
- [ ] SquatCounter (down and up phases)
- [ ] JumpingJackCounter (open and closed states)
- [ ] PushUpCounter with estimated labeling
- [ ] Rep counter UI, timer, state text, guidance banner
- [ ] Counter unit tests using recorded landmark sequences

### Phase 5: Gesture Controls
- [ ] Gesture state machine with debounce and hold timers
- [ ] Hand raise: next or confirm after stable hold
- [ ] Side swipe with direction and confidence checks
- [ ] Both hands hold: single pause or resume per hold cycle
- [ ] Idle and step back guidance mode
- [ ] Touch fallback verified for every gesture action

### Phase 6: Aura Energy and Ghost Trainer
- [ ] AuraTrailLayer with speed scaled glow
- [ ] Reduced effects mode wired to settings
- [ ] MotionPath recorder with downsampling
- [ ] Ghost replay screen with timestamp sync
- [ ] In workout ghost overlay with similarity cues
- [ ] Guidance only label on replay
- [ ] Sample path fallback (ghost-trainer-sample.json)

### Phase 7: Session Loop and History Completion
- [ ] Countdown, tracking, pause, complete flow
- [ ] Session Summary with save to Room
- [ ] Streak and stats update on save
- [ ] History list and detail
- [ ] Single delete and clear all with cascade
- [ ] Dashboard live data

### Phase 8: Hardening and Delivery
- [ ] Low light, partial visibility, revocation, background handling
- [ ] Performance pass with reduced effects verification
- [ ] Optional Python helper scripts
- [ ] Screenshots of all 8 screen areas
- [ ] Demo video of full session with gestures
- [ ] Setup instructions and build steps
- [ ] Test report and known limitations
- [ ] Em dash and en dash scan on all final documents
- [ ] Review ready APK built
- [ ] Delivery folders assembled (01 to 06)

## Acceptance Criteria Verification

| # | Criterion | Status |
| --- | --- | --- |
| 1 | APK installs and runs without setup errors | Not Verified |
| 2 | Permission flow blocks tracking until granted | Not Verified |
| 3 | Hand raise and side swipe trigger correct actions (or demo video plus notes) | Not Verified |
| 4 | Squat and jumping jack counters reduce false counts | Not Verified |
| 5 | Push-up approximation with confidence feedback | Not Verified |
| 6 | Aura effects never block key workout data | Not Verified |
| 7 | Ghost Trainer replays at least one saved or sample path | Not Verified |
| 8 | History saves, displays, and deletes correctly | Not Verified |
| 9 | Dark neon UI, readable contrast, consistent structure | Not Verified |
| 10 | All delivery files present | Not Verified |

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

(no entries yet)

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