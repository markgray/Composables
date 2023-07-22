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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * This is the main activity of the demo.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call the [setContent] method to have it compose into our activity the Composable that
     * consists of our [Drag2DComposeTheme] custom [MaterialTheme] wrapping a [Surface] whose
     * `modifier` argument is a [Modifier.fillMaxSize] (causes it to occupy its entire incoming size
     * constraints), and whose `color` argument sets its background [Color] to the
     * [ColorScheme.background] color of the [MaterialTheme.colorScheme]
     * Color(red = 28, green = 27, blue = 31) for dark theme (a shade of black) and
     * Color(red = 255, green = 251, blue = 254) for light theme (a shade of white).
     * The `content` of the [Surface] is our [Material2DMotionPreview] Composable.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Drag2DComposeTheme {
                // A surface container using the 'background' color from the theme
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