package com.emon.savetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.emon.savetrack.ui.navigation.AppNavHost
import com.emon.savetrack.ui.theme.SaveTrackTheme

/**
 * MainActivity serves as the entry point for the SaveTrack application.
 * Sets edge-to-edge UI, applies SaveTrackTheme, and launches AppNavHost.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaveTrackTheme {
                AppNavHost()
            }
        }
    }
}