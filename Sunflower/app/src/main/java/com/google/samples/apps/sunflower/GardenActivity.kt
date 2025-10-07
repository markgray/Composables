/*
 * Copyright 2018 Google LLC
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

package com.google.samples.apps.sunflower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.samples.apps.sunflower.compose.SunflowerApp
import com.google.samples.apps.sunflower.ui.SunflowerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the Sunflower app.
 * This activity is the entry point of the application and hosts the main UI, which is
 * built using Jetpack Compose.
 *
 * The @[AndroidEntryPoint] annotation Marks an Android component class to be setup for injection
 * with the standard Hilt Dagger Android components. This will generate a base class that the
 * annotated class should extend, either directly or via the Hilt Gradle Plugin (as we do). This
 * base class will take care of injecting members into the Android class as well as handling
 * instantiating the proper Hilt components at the right point in the lifecycle. The name of the
 * base class will be "Hilt_GardenActivity.java".
 */
@AndroidEntryPoint
class GardenActivity : ComponentActivity() {

    /**
     * Called when the activity is first created.
     *
     * This method is responsible for initializing the activity. It sets up the user interface
     * to be displayed edge-to-edge, and then sets the main content view to the [SunflowerApp]
     * composable, which is wrapped in the [SunflowerTheme].
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in
     * [onSaveInstanceState]. We do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Displaying edge-to-edge
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = 0,
                darkScrim = 0
            )
        )
        setContent {
            SunflowerTheme {
                SunflowerApp()
            }
        }

    }
}
