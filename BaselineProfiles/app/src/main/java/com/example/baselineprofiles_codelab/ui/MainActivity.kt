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

package com.example.baselineprofiles_codelab.ui

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat

/**
 * Main activity for the Jetsnack app.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is first created.
     *
     * Sets up the main content of the activity using Jetpack Compose, enabling edge-to-edge display
     * and handling system windows.
     *
     * First we call the [enableEdgeToEdge] function to enable edge-to-edge display. Then we call
     * our super's implementation of `onCreate` with our [Bundle] parameter [savedInstanceState].
     * We call the [WindowCompat.setDecorFitsSystemWindows] with its `window` argument the current
     * [Window] and `false` as its `fitSystemWindows` argument to indicate that we want to handle
     * fitting system windows. Finally we call [setContent] with its `content` argument being a
     * [Box] with a [Modifier.safeDrawingPadding] modifier (this modifier is used to apply padding
     * to accommodate the safe drawing insets, insets that include areas where content may be covered
     * by other drawn content. This includes all systemBars, displayCutout, and ime). In its [BoxScope]
     * `content` composable lambda argument we compose our [JetsnackMain] composable.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     * then this Bundle contains the data it most recently supplied in [onSaveInstanceState]. We do
     * not override [onSaveInstanceState] so it is not used.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState = savedInstanceState)

        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Box(modifier = Modifier.safeDrawingPadding()) {
                JetsnackMain()
            }
        }
    }
}
