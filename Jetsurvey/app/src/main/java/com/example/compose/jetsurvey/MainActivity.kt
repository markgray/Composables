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

package com.example.compose.jetsurvey

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import com.example.compose.jetsurvey.theme.JetsurveyTheme

/**
 * Main activity for the Jetsurvey app.
 * This activity serves as the entry point for the application and sets up the
 * Jetpack Compose UI.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides you with
     * a Bundle containing the activity's previously frozen state, if there was one.
     *
     * Always followed by [onStart].
     *
     * First we call [enableEdgeToEdge] to enable edge-to-edge display mode, then we call our
     * super's implementation of `onCreate`. Next we call [setContent] to have it Compose its
     * Composable `content` lambda into our activity. That lambda consists of our [JetsurveyTheme]
     * custom [MaterialTheme] wrapping our [JetsurveyNavHost] Composable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            JetsurveyTheme {
                JetsurveyNavHost()
            }
        }
    }
}
