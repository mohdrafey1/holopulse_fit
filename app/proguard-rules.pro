# HoloPulse Fit ProGuard rules.
# Release builds keep minification disabled per app/build.gradle.kts, so these rules are a
# safety net for future release hardening.

# kotlinx.serialization: keep generated serializers.
-keepclassmembers class **$$serializer { *; }
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# ML Kit pose detection models.
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
