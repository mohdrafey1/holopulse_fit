# 02_source_code

The full Android source lives at the repository root, not inside this folder, because the Android
Studio and Gradle project is rooted there (`settings.gradle.kts`, `app/`, `gradle/`). Moving the
module into a nested folder would break the Gradle project layout.

To open the source:

1. Open the repository root folder in Android Studio.
2. The app module is `app/`, with sources under `app/src/main/java/com/lumastride/holopulsefit/`.

Package structure (TRD section 3):

```
com.lumastride.holopulsefit
  ui/            Compose screens
  ui/components/ GlowCard, ProgressRing, RepCounter, PoseOverlay, AuraTrailLayer, GhostSkeleton, ...
  ui/theme/      Dark neon tokens from the palette JSON
  ui/viewmodel/  One ViewModel per screen
  camera/        CameraX preview and permission flow
  pose/          ML Kit detector wrapper, landmark models, geometry, confidence
  gesture/       Gesture state machine
  counting/      Squat, jumping jack, push-up rep counters
  ghost/         Motion recorder, codec, replay engine
  data/          Room database, entities, DAOs, repository, sample loader
  util/          Calorie estimator, streak calculator, time formatting
  navigation/    Destinations and the Navigation Compose host
```

This decision is recorded in the Tracker.md decision log.
