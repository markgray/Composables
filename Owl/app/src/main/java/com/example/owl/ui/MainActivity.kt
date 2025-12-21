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

package com.example.owl.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.owl.R

/**
 * Main activity for the Owl app.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is starting.
     *
     * We start by calling the [enableEdgeToEdge] extension function of the [ComponentActivity] to
     * enable edge-to-edge mode with its `statusBarStyle` parameter set to a [SystemBarStyle.dark]
     * whose `scrim` parameter is the [Color] whose resource ID is `R.color.immersive_sys_ui`. Then
     * we call our super's implementation of `onCreate` and then call the [setContent] method to
     * compose our [OwlApp] composable into this activity's root view with its `finishActivity` a
     * lambda which calls the [ComponentActivity.finish] method, and its `modifier` argument a
     * [Modifier.safeContentPadding] to add padding to accommodate the safe content insets.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in [onSaveInstanceState].
     * We do not override [onSaveInstanceState] so it is not used.
     *
     * @see onStart
     * @see onSaveInstanceState
     * @see onRestoreInstanceState
     * @see onPostCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = ContextCompat.getColor(this, R.color.immersive_sys_ui)
            )
        )
        super.onCreate(savedInstanceState)

        setContent {
            OwlApp(
                modifier = Modifier.safeContentPadding(),
                finishActivity = { finish() }
            )
        }
    }
}
