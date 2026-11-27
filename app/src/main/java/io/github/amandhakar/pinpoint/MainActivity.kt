package io.github.amandhakar.pinpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.amandhakar.pinpoint.ui.MapScreen
import io.github.amandhakar.pinpoint.ui.PinpointTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PinpointTheme {
                MapScreen(hasMapsKey = BuildConfig.HAS_MAPS_KEY)
            }
        }
    }
}
