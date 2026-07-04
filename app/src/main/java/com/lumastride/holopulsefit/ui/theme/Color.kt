package com.lumastride.holopulsefit.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * HoloPulse Fit dark neon palette.
 *
 * These are the single source of truth for color in the app. Per rules.md section 6.1 no other
 * file may declare a raw hex color: every screen and component references these named tokens or
 * the Material [androidx.compose.material3.ColorScheme] built from them in [HoloPulseFitTheme].
 *
 * Core tokens are copied verbatim from holopulse-fit-color-palette.json.
 */

// Core palette tokens (holopulse-fit-color-palette.json).
val DeepSpaceBase = Color(0xFF0B1020)     // App background, screen base
val ElectricBlueGlow = Color(0xFF2AE8FF)  // Primary glow, active states, rep counter accents
val CyanPulse = Color(0xFF00D4FF)         // Progress rings, secondary glow, links and actions
val VioletEnergy = Color(0xFF8B5CF6)      // Aura trails, Ghost Trainer skeleton, streak accents
val SoftWhiteText = Color(0xFFF8FAFC)     // Primary text and icons
val WarningPulse = Color(0xFFF97316)      // Guidance banners, low confidence, delete confirmations

// Supporting derived tones (design.md section 2).
val CardSurface = Color(0xFF121A30)       // GlowCard base surface
val CardSurfaceElevated = Color(0xFF18223D)
val DividerBlue = Color(0x332AE8FF)       // Electric blue at low opacity, dividers and outlines
val DisabledText = Color(0x99F8FAFC)      // Soft white at reduced opacity
val ScrimDark = Color(0xCC0B1020)         // Overlay scrim over camera preview for readable data
val GlowShadowBlue = Color(0x662AE8FF)    // Soft outer glow for cards and rings
val GlowShadowViolet = Color(0x668B5CF6)  // Aura / ghost glow
