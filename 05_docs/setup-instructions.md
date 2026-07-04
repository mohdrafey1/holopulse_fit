# HoloPulse Fit: Setup Instructions

## Requirements

1. Android Studio (a recent version that supports AGP 9.x).
2. JDK 17.
3. Android SDK Platform for API 36 (the project compiles against android-36.1) and build tools.
4. A physical Android device (recommended) or emulator running Android 8.0 (API 26) or newer.
5. A device with a front camera for the tracked workout flow.

## First time setup

1. Open the project root folder in Android Studio (the folder that contains `settings.gradle.kts`).
2. Let Gradle sync. The first sync downloads Compose, CameraX, ML Kit Pose Detection, Room, and the other dependencies, so it needs network access.
3. If prompted, install the missing SDK platform and build tools.
4. Confirm `local.properties` points to your Android SDK. Android Studio writes this automatically.

## Notes

1. The project uses AGP built-in Kotlin, so the standalone Kotlin Android plugin is intentionally not applied. See the decision log in Tracker.md.
2. No API keys, accounts, or network services are required. All processing and storage are on device.
3. The optional Python helper scripts in `tools/` require Python 3 and are not needed to build or run the app.
