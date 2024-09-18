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

package com.example.composeanimateddraganddrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.composeanimateddraganddrop.ui.theme.ComposeAnimatedDragAndDropTheme

/**
 * This is the main activity of the demo, but it immediately hands everything over to the Composable
 * [FlowDragAndDropExample] so there is not much to say here.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call [setContent] to have it Compose the composable which consists of our
     * [ComposeAnimatedDragAndDropTheme] custom [MaterialTheme] wrapping a [Surface] whose
     * `modifier` argument is a [Modifier.fillMaxSize] that causes it to fill its entire
     * incoming constraints and whose `color` argument is the [ColorScheme.background] color of
     * the [MaterialTheme.colorScheme] which sets the background color of the [Surface] to the
     * default `Color(0xFFFFFBFE)` since [ComposeAnimatedDragAndDropTheme] does not override it.
     * The `content` of the [Surface] is our [FlowDragAndDropExample] composable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use it.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAnimatedDragAndDropTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        FlowDragAndDropExample()
                    }
                }
            }
        }
    }
}