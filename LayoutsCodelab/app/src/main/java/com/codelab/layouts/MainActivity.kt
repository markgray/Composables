/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelab.layouts

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.codelab.layouts.ui.LayoutsCodelabTheme

/**
 * This is the main activity of our "Layouts Codelab" demo app.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to
     * edge display, then we call our super's implementation of `onCreate`. Next we call the
     * [setContent] method to have it Compose our [LayoutsCodelabTheme] custom [MaterialTheme]
     * into our activity (it will supply default values to the compose functions it wraps).
     * The `content` composable lambda argument of the [LayoutsCodelabTheme] holds a [Box]
     * whose `modifier` argument is a [Modifier.safeDrawingPadding] to add padding to accommodate
     * the safe drawing insets, and in its [BoxScope] `content` composable lambda argument we
     * compose our [LayoutsCodelab] composable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        /**
         * Composes the LayoutsCodelabTheme wrapped LayoutsCodelab composable into our activity. The
         * content will become the root view of the activity. This is roughly equivalent to calling
         * ComponentActivity.setContentView with a ComposeView.
         */
        setContent {
            /**
             * This is our custom MaterialTheme located in the file ui/Theme.kt which overrides the
             * default `colors` parameter with our custom palettes DarkColorPalette and LightColorPalette
             * depending on whether `isSystemInDarkTheme` is `true` or not. A MaterialTheme supplies
             * the values for the `colors`, `typography`, and `shapes` attributes of the content
             * Composable ([LayoutsCodelab] in our case).
             */
            LayoutsCodelabTheme {
                /**
                 * This is the main Composable of our app, and holds a [Scaffold] with a Material
                 * Design top app bar and whose content is a `BodyContent` Composable (both of these
                 * are in the file LayoutsCodelab.kt
                 */
                Box(modifier = Modifier.safeDrawingPadding()) {
                    LayoutsCodelab()
                }
            }
        }
    }
}

/**
 * This is just a very simple example Composable which is used by [DefaultPreview] to demonstrate
 * how a Preview is done. It composes a [Text] Composable whose `text` is formed by appending our
 * [String] parameter [name] to the end of the [String] "Hello ".
 */
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

/**
 * This is the Preview for the [Greeting] Composable, the `showBackground` parameter of the
 * Preview annotation when `true` causes the Composable to use a default background color.
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    /**
     * This is our custom MaterialTheme located in the file ui/Theme.kt which overrides the default
     * `colors` parameter with our custom palettes DarkColorPalette and LightColorPalette depending
     * on whether `isSystemInDarkTheme` is `true` or not. A MaterialTheme supplies the values for
     * the `colors`, `typography`, and `shapes` attributes of the content Composable ([Greeting] in
     * our case).
     */
    LayoutsCodelabTheme {
        Greeting("Android")
    }
}
