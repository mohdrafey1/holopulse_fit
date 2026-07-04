# HoloPulse Fit: Build Steps

All commands run from the project root.

## Build a debug APK

```
gradlew :app:assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`. A copy for review is kept in `01_apk/HoloPulseFit-debug.apk`.

## Run unit tests

```
gradlew :app:testDebugUnitTest
```

Results are written to `app/build/test-results/testDebugUnitTest/`.

## Install on a connected device

```
gradlew :app:installDebug
```

Or install the APK directly:

```
adb install -r 01_apk/HoloPulseFit-debug.apk
```

## Build a release APK

```
gradlew :app:assembleRelease
```

Release minification is disabled by default (see `app/build.gradle.kts`), so the release APK builds without extra signing setup for review. Configure a signing config before publishing.

## Environment notes

1. JDK 17 is required.
2. The project compiles against the installed android-36.1 platform, targets API 35, and has a minimum of API 26.
3. The Gradle wrapper pins the Gradle version, so no separate Gradle install is needed.
