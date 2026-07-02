# HoloPulse Fit: Design Document

 
Design language: Dark neon, motion first, readable during exercise.
Source of truth: Visual Asset Package (03_visual_assets) and palette JSON.

## 1. Design Principles

1. Dark interface with electric blue, cyan, violet, and soft white highlights.
2. Text stays readable during movement. No heavy effects over key workout data.
3. Glowing cards, animated progress rings, pose overlays, and rep count feedback applied consistently.
4. Aura Energy effects react to movement while keeping the camera preview smooth.
5. A reduced effects option exists for comfort and device performance.

## 2. Color Palette

Taken directly from holopulse-fit-color-palette.json:

| Token | Hex | Usage |
| --- | --- | --- |
| deep_space_base | #0B1020 | App background, screen base |
| electric_blue_glow | #2AE8FF | Primary glow, active states, rep counter accents |
| cyan_pulse | #00D4FF | Progress rings, secondary glow, links and actions |
| violet_energy | #8B5CF6 | Aura trails, Ghost Trainer skeleton, streak accents |
| soft_white_text | #F8FAFC | Primary text and icons |
| warning_pulse | #F97316 | Guidance banners, low confidence, delete confirmations |

Supporting derived tones: card surface at #121a30 range, dividers and outlines as low opacity electric blue, disabled text as soft white at reduced opacity.

## 3. Typography

1. Single sans serif family (system default or a bundled clean sans) to keep labels stable during motion.
2. Rep counter: extra large numeric display, heaviest weight, electric blue glow, largest element on the workout screen.
3. Screen titles: large, soft white.
4. Card titles and stats: medium, high contrast.
5. Hints and captions: small but never below readable size at arm distance, since users stand away from the phone.
6. Short labels only. No long sentences on active workout surfaces.

## 4. Core Components

| Component | Description |
| --- | --- |
| GlowCard | Rounded dark card with soft outer glow border, used for dashboard stats, recent sessions, and workout library items |
| ProgressRing | Animated circular ring in cyan for daily progress, countdown, and target rep progress, backed by the countdown ring Lottie placeholder |
| RepCounter | Large glowing numeral with a short pulse animation on each counted rep |
| PoseOverlay | Skeleton lines and joints over the camera preview in electric blue with confidence based opacity |
| AuraTrailLayer | Canvas layer drawing fading violet and cyan trails behind tracked joints |
| GestureHintBar | Compact strip of icon plus label hints for available gestures in the current state |
| GuidanceBanner | Warning pulse colored banner for tracking loss, low light, and permission guidance |
| GhostSkeleton | Semi transparent violet skeleton for replay, visually distinct from the live overlay |
| StreakBadge | Streak icon with count and subtle glow on the dashboard |

## 5. Screen Design Notes

### Launch
Centered HoloPulse Fit logo from branding assets on deep space base with a single aura pulse animation.

### Dashboard
Greeting, ProgressRing for today, StreakBadge, quick start GlowCard as the dominant action, recent session GlowCards below. Reference: ui-reference-landing-page-layout.png.

### Workout Library
Vertical or paged GlowCards per exercise using pose reference imagery, target rep chips, effort tag, and a bright start button.

### Camera Workout
Full screen camera preview. Overlay zones: top strip for exercise name, timer, and state; large RepCounter anchored to a corner clear of the body; GestureHintBar at the bottom; touch pause and stop kept small but reachable. Aura trails render under text layers so data is never hidden. Reference: ui-reference-camera-workout-layout.png.

### Session Summary
Stacked GlowCards for reps, duration, calories, streak update, and save status with a completion glow moment.

### History
List of compact session cards, progress summary strip, detail view with delete in warning pulse styling.

### Ghost Trainer
Dark stage with GhostSkeleton replay, playback controls, similarity cues, and the persistent guidance only label. Reference: ui-reference-ghost-trainer-layout.png.

### Settings
Simple grouped list: permission status, aura intensity slider, reduced effects toggle, ghost trainer toggle, clear history, privacy notes.

## 6. Aura Energy Visual Specification

1. Trails attach to wrists, elbows, knees, and ankles.
2. Trail length and glow intensity scale with movement speed and rep momentum.
3. Colors blend from cyan pulse to violet energy along the trail fade.
4. Point buffer is bounded so rendering stays lightweight during tracking.
5. Reduced effects mode: shorter trails, lower glow radius, Lottie loops off, pulse animations minimized.

## 7. Iconography and Motion Assets

1. Use the provided icon set: camera, workout, history, settings, replay, timer, energy, gesture, streak, calories.
2. Icons render in soft white or electric blue depending on state, with the contact sheet as the reference.
3. Lottie placeholders map as: lottie-countdown-ring to session countdown, lottie-glow-progress to progress rings, lottie-aura-pulse to launch and summary moments. Placeholders may be replaced with final graphics when named clearly.

## 8. Accessibility

1. Contrast between soft white text and deep space base stays high on every screen.
2. Every gesture action has a touch fallback control.
3. Guidance messages appear as text captions, not color alone.
4. Reduced effects option lowers visual load for comfort.
5. Touch targets meet standard Android minimum sizes.

## 9. Asset Handling in Design

1. Visual Asset Package is the starting point. Do not recreate supplied assets unless a final replacement is needed.
2. App icon and vectors used by the app live in Android res folders. Reference imagery stays in visual asset folders.
3. Placeholder assets may be replaced only when clearly named as placeholders.
