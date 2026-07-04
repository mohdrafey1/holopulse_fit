// Top-level build file where you can add configuration options common to all sub-projects/modules.
// AGP 9.x provides built-in Kotlin, so the standalone kotlin-android plugin is intentionally absent.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
}
