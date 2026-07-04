package com.lumastride.holopulsefit.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow

/**
 * The largest element on the workout screen: a big glowing numeral that pulses each time a rep is
 * counted (design.md RepCounter). It stays clear of key data and never covers the timer or state.
 */
@Composable
fun RepCounter(
    count: Int,
    target: Int,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            scale.snapTo(1.3f)
            scale.animateTo(1f, animationSpec = tween(durationMillis = 260))
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = "$count",
            fontSize = 84.sp,
            fontWeight = FontWeight.Black,
            color = ElectricBlueGlow,
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        )
        Text(
            text = "of $target reps",
            style = MaterialTheme.typography.labelLarge,
            color = CyanPulse,
        )
    }
}
