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

package com.example.jetnews.ui

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.JetnewsApplication.Companion.JETNEWS_APP_URI
import com.example.jetnews.data.AppContainer

/**
 * This is the main (and only) activity of our app.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when activity is starting. First we call [enableEdgeToEdge] to enable edge-to-edge
     * display for our app, then we call our `super`'s implementation of `onCreate`. Next we
     * initialize our [AppContainer] variable `val appContainer` to the [JetnewsApplication.container]
     * of the application that owns this activity (which is our [JetnewsApplication] custom
     * [Application], which exists solely to allow us to access its [JetnewsApplication.container]
     * field and the constant [JETNEWS_APP_URI]). Next we call [setContent] to have it compose
     * into our activity the `content` lambda argument consisting of the initialization of our
     * [WindowWidthSizeClass] variable `val widthSizeClass` to the [WindowSizeClass.widthSizeClass]
     * of the [WindowSizeClass] returned by [calculateWindowSizeClass] method for this `activity`.
     * Then we compose [JetnewsApp] with its `appContainer` argument our [AppContainer] variable
     * `appContainer`, and its `widthSizeClass` argument our [WindowWidthSizeClass] variable
     * `widthSizeClass`.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState]
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer: AppContainer = (application as JetnewsApplication).container
        setContent {
            val widthSizeClass: WindowWidthSizeClass =
                calculateWindowSizeClass(activity = this).widthSizeClass
            JetnewsApp(appContainer = appContainer, widthSizeClass = widthSizeClass)
        }
    }
}
