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

package androidx.compose.samples.crane.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.details.launchDetailsActivity
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the application. The AndroidEntryPoint annotation Marks an Android component
 * class to be setup for injection with the standard Hilt Dagger Android components. This will
 * generate a base class that the annotated class should extend, either directly or via the Hilt
 * Gradle Plugin (as we do). This base class will take care of injecting members into the Android
 * class as well as handling instantiating the proper Hilt components at the right point in the
 * lifecycle. The name of the base class will be "Hilt_MainActivity.java".
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is starting. First we call our super's implementatin of `onCreate`,
     * then we call [WindowCompat.setDecorFitsSystemWindows] with `false` so the framework will not
     * fit the content view to the insets and will just pass through the [WindowInsetsCompat] to the
     * content view. Finally we call [setContent] to have it Compose the composable we pass as its
     * `content` lambda argument into the our activity. The content will become the root view of our
     * activity. That lambda Composable is a [MainScreen] whose `onExploreItemClicked` argument is
     * a lambda which calls [launchDetailsActivity] with the [ExploreModel] of the item that
     * was clicked. The [MainScreen] composable is wrapped in our [CraneTheme] custom [MaterialTheme]
     * which will cause our custom [Colors] and [Typography] to be used by material savy widgets.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CraneTheme {
                MainScreen(
                    onExploreItemClicked = { launchDetailsActivity(context = this, item = it) }
                )
            }
        }
    }
}

/**
 * This is the main screen of our activity. It consists of a [Surface] whose `color` argument sets
 * its background [Color] to the `primary` [Color] of [MaterialTheme.colors], which comes from our
 * [CraneTheme] and is `crane_purple_800` which is `Color(0xFF5D1049)`. The Composable content of
 * the [Surface] depends on the remembered [Boolean] variable `var showLandingScreen` which starts
 * out `true`:
 *  - `true` it displays a [LandingScreen] whose `onTimeout` lambda argument sets `showLandingScreen`
 *  to `false` when the 2000 millisecond delay in [LandingScreen] finishes.
 *  - `false` it displays a [CraneHome] whose `onExploreItemClicked` argument is our [onExploreItemClicked]
 *  parameter (in our case this will be a lambda which calls [launchDetailsActivity] with the [ExploreModel]
 *  of the item that was clicked.
 *
 * @param onExploreItemClicked the [OnExploreItemClicked] lambda that we should pass to [CraneHome].
 */
@Composable
private fun MainScreen(onExploreItemClicked: OnExploreItemClicked) {
    Surface(color = MaterialTheme.colors.primary) {
        var showLandingScreen: Boolean by remember { mutableStateOf(true) }
        if (showLandingScreen) {
            LandingScreen(onTimeout = { showLandingScreen = false })
        } else {
            CraneHome(onExploreItemClicked = onExploreItemClicked)
        }
    }
}
