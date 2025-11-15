package com.example.examplecomposegrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.examplecomposegrid.ui.theme.ExampleComposeGridTheme

/**
 * The main entry point of the application.
 *
 * This activity sets up the Compose content for the entire app. It enables edge-to-edge
 * display and hosts the main Composable, which currently showcases a demo for a
 * grid layout ([ColumnWeightsJsonDemo]).
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     *
     * This method initializes the activity, enables edge-to-edge display for a modern UI,
     * and sets the main content view to be a Compose UI. It uses the [ExampleComposeGridTheme]
     * and displays the [ColumnWeightsJsonDemo] composable, which serves as the primary
     * content for the application.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [onSaveInstanceState].  **Note: Otherwise it is null.**
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ExampleComposeGridTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        // TODO: Allow choice of different demos
                        ColumnWeightsJsonDemo()
                    }
                }
            }
        }
    }
}
