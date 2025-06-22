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

package com.google.samples.apps.niacatalog

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.google.samples.apps.niacatalog.ui.NiaCatalog

/**
 * Main activity for the Now in Android catalog.
 *
 * This activity serves as the entry point for the Now in Android catalog app. It sets up the
 * UI using Jetpack Compose and displays the [NiaCatalog] composable.
 */
class NiaCatalogActivity : ComponentActivity() {
    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides you with
     * a Bundle containing the activity's previously frozen state, if there was one.
     *
     * Always followed by [onStart].
     *
     * First we call our super's implementation of `onCreate`. Then we call the method
     * [WindowCompat.setDecorFitsSystemWindows] with the current [Window] as its `window` argument
     * and `false` as its `decorFitsSystemWindows` argument. (If set to false, the framework will
     * not fit the content view to the insets and will just pass through the WindowInsetsCompat to
     * the content view). Then we call the method [setContent] with the [NiaCatalog] composable as
     * its `content` composable lambda argument.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in [onSaveInstanceState]
     * We do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent { NiaCatalog() }
    }
}
