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
 * TODO: Add kdoc
 */
class MainActivity : ComponentActivity() {
    /**
     * TODO: Add kdoc
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExampleComposeGridTheme {
                // A surface container using the 'background' color from the theme
                Box(modifier = Modifier.safeDrawingPadding()) {
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

/**
 * TODO: Add kdoc
 */
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

/**
 * TODO: Add kdoc
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ExampleComposeGridTheme {
        Greeting("Android")
    }
}