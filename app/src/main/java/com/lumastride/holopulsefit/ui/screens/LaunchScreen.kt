package com.lumastride.holopulsefit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.lumastride.holopulsefit.R
import kotlinx.coroutines.delay

/**
 * Launch screen: centered HoloPulse Fit branding on the deep space base with a single aura pulse
 * animation (design.md Launch, appflow.md section 2). It is a branded moment while UserStats and
 * Settings load, then it hands off to the dashboard and is not reachable again through back.
 */
@Composable
fun LaunchScreen(onFinished: () -> Unit) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("animations/lottie-aura-pulse.json"),
    )
    val progress by animateLottieCompositionAsState(composition, iterations = 1)

    // Hand off after a short branded moment regardless of animation availability.
    LaunchedEffect(Unit) {
        delay(1900)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(320.dp),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(R.drawable.lumastride_holopulse_fit_logo),
                contentDescription = "HoloPulse Fit",
                modifier = Modifier.size(180.dp),
            )
            Text(
                text = "HoloPulse Fit",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Move. Pulse. Control.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}
