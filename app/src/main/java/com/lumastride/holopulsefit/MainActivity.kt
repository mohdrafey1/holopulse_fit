package com.lumastride.holopulsefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lumastride.holopulsefit.navigation.HoloApp
import com.lumastride.holopulsefit.ui.theme.HoloPulseFitTheme

/**
 * Single activity host for HoloPulse Fit. All 8 screen areas are Compose destinations inside
 * [HoloApp]'s Navigation Compose host.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoloPulseFitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    HoloApp()
                }
            }
        }
    }
}
