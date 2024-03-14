/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.jetlagged

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import com.example.jetlagged.ui.theme.JetLaggedTheme

/**
 * This is the main [ComponentActivity] of our JetLagged app.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when activity is starting. First we call [enableEdgeToEdge] to enable edge-to-edge
     * display for our app, then we call our `super`'s implementation of `onCreate`. Finally we call
     * [setContent] to have it compose into our activity the `content` lambda argument consisting of
     * our [JetLaggedTheme] custom [MaterialTheme] wrapping our [HomeScreenDrawer] root Composable,
     * with [JetLaggedTheme] supplying custom values for [ColorScheme], [Typography], and [Shapes]
     * used by [MaterialTheme] Composables.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            JetLaggedTheme {
                HomeScreenDrawer()
            }
        }
    }
}
