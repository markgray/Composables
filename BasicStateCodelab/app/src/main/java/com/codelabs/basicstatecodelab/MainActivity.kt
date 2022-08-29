package com.codelabs.basicstatecodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.codelabs.basicstatecodelab.ui.theme.BasicStateCodelabTheme

/**
 * This is the main activity of our BasicStateCodelab code lab. It extends [ComponentActivity]
 * instead of AppCompatActivity because [ComponentActivity] has all you need for a Compose-only
 * app. If you need AppCompat APIs, an AndroidView which works with AppCompat or MaterialComponents
 * theme, or you need Fragments then extend AppCompatActivity. Note: AppCompatActivity extends
 * FragmentActivity which extends ComponentActivity.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call [setContent] to have it Compose its Composable lambda argument into our activity.
     * That lambda consists of our [BasicStateCodelabTheme] custom [MaterialTheme] wrapping a [Surface]
     * whose `modifier` parameter uses [Modifier.fillMaxSize] to have its content fill the incoming
     * measurement constraints, and uses the `background` of our [MaterialTheme.colors] ([Color.White]
     * in our case as its background [Color]. The `content` Composable of the [Surface] is our main
     * screen the [WellnessScreen] Composable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicStateCodelabTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WellnessScreen()
                }
            }
        }
    }
}
