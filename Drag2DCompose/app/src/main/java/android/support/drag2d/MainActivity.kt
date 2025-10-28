/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.drag2d

import android.os.Bundle
import android.support.drag2d.ui.theme.Drag2DComposeTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

/**
 * This is the main activity of the demo.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * First, we call [enableEdgeToEdge] to allow our content to draw behind the system bars.
     * Then, we call our super's implementation of `onCreate`.
     *
     * Finally, we call [setContent] to define our UI using Jetpack Compose. The layout consists of:
     *  - [Drag2DComposeTheme]: Our custom [MaterialTheme].
     *  - [Box]: A container that respects safe drawing areas using [Modifier.safeDrawingPadding],
     *    ensuring content doesn't overlap with system UI like status bars or navigation bars.
     *  - [Surface]: A container that fills the entire screen ([Modifier.fillMaxSize]) and uses the
     *    `background` color from our theme.
     *  - [Material2DMotionPreview]: The main Composable for this demo, which is the content of the
     *    [Surface].
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this [Bundle] contains the data it most recently supplied in
     * [onSaveInstanceState]. **Note: We do not override [onSaveInstanceState] so this is not used.**
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Drag2DComposeTheme {
                // A surface container using the 'background' color from the theme
                Box(modifier = Modifier.safeDrawingPadding()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Material2DMotionPreview()
                    }
                }
            }
        }
    }
}