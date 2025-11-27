/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.example.composemail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.example.composemail.ui.compositionlocal.FoldableInfo
import com.example.composemail.ui.compositionlocal.LocalFoldableInfo
import com.example.composemail.ui.compositionlocal.LocalHeightSizeClass
import com.example.composemail.ui.compositionlocal.LocalWidthSizeClass
import com.example.composemail.ui.home.ComposeMailHome
import com.example.composemail.ui.theme.ComposeMailTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main activity for the Compose Mail application.
 *
 * This activity sets up the Jetpack Compose UI, collects window size and foldable
 * state information, and provides this information to the composable hierarchy
 * through [CompositionLocalProvider]. It serves as the main entry point for the app.
 */
@ExperimentalMaterial3WindowSizeClassApi
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     *
     * This function initializes the activity, enables edge-to-edge display, and sets up the
     * Jetpack Compose content. It calculates the [WindowSizeClass] and collects information
     * about the foldable state of the device. This information is then provided down to the
     * composable tree using a [CompositionLocalProvider] to enable adaptive layouts.
     *
     * First we call [enableEdgeToEdge] to enable edge to edge display, then we call our super's
     * implementation of `onCreate`. Then we call [setContent] to have it compose its `content`
     * composable lambda argument into our activity. In that lambda we iniitialize our
     * [WindowSizeClass] variable `windowSizeClass` to the value calculated by the
     * [calculateWindowSizeClass] method, and initialize our [State] wrapped [FoldableInfo] to
     * the value returned by our [collectFoldableInfoAsState] method. Then we compose a
     * [CompositionLocalProvider] that has [LocalWidthSizeClass] provide the
     * [WindowSizeClass.widthSizeClass] of `windowSizeClass`, [LocalHeightSizeClass] provide the
     * [WindowSizeClass.heightSizeClass] of `windowSizeClass`, and [LocalFoldableInfo] provide
     * [State] wrapped [FoldableInfo] variable `foldableInfo`. In the `content` composable lambda
     * argument of the [CompositionLocalProvider] we compose [ComposeMailTheme] wrapping a [Box]
     * whose `modifier` argument is a [Modifier.safeDrawingPadding] to add padding to accommodate
     * the safe drawing insets, and in its [BoxScope] `content` composable lambda argument we
     * compose a [Surface] whose `modifier` argument is a [Modifier.fillMaxSize] to have it occupy
     * its entire incoming size constraints, and whose `color` argument is the [Colors.background]
     * of our custom [MaterialTheme.colors]. Finally the `content` composable lambda argument
     * of the [Surface] composes our [ComposeMailHome] composable with its `modifier` argument also
     * a [Modifier.fillMaxSize].
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down, this Bundle contains the data it most recently supplied in [onSaveInstanceState].
     * We do not override [onSaveInstanceState] so it is not used.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(activity = this)
            val foldableInfo: FoldableInfo by collectFoldableInfoAsState(activity = this)
            CompositionLocalProvider(
                LocalWidthSizeClass provides windowSizeClass.widthSizeClass,
                LocalHeightSizeClass provides windowSizeClass.heightSizeClass,
                LocalFoldableInfo provides foldableInfo
            ) {
                ComposeMailTheme {
                    Box(modifier = Modifier.safeDrawingPadding()) {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            ComposeMailHome(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

/**
 * Collects information about the foldable state of the device and returns it as a [State].
 *
 * This composable function uses [WindowInfoTracker] to listen for changes in the window layout.
 * It specifically checks for the presence of a [FoldingFeature] and whether its state is
 * [FoldingFeature.State.HALF_OPENED]. This information is encapsulated in a [FoldableInfo] object.
 *
 * The collection is managed within a [LaunchedEffect] and is tied to the lifecycle of the
 * composable, starting when the lifecycle reaches the [Lifecycle.State.STARTED] state and
 * stopping when it goes below it. This ensures that the collection is only active when the
 * UI is visible to the user.
 *
 * @param activity The [ComponentActivity] used to access the [WindowInfoTracker] and lifecycle.
 * @return A [State] holding the current [FoldableInfo], which updates whenever the foldable
 * state changes. The initial value is [FoldableInfo.Default].
 */
@Composable
private fun collectFoldableInfoAsState(activity: ComponentActivity): State<FoldableInfo> {
    val lifecycleScope: LifecycleCoroutineScope = LocalLifecycleOwner.current.lifecycleScope
    val foldableInfo: MutableState<FoldableInfo> =
        remember { mutableStateOf(FoldableInfo.Default) }

    LaunchedEffect(key1 = lifecycleScope, key2 = activity) {
        lifecycleScope.launch(context = Dispatchers.Main) {
            activity.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(context = activity)
                    .windowLayoutInfo(activity = activity)
                    .collect { newLayoutInfo: WindowLayoutInfo ->
                        foldableInfo.value = FoldableInfo(
                            isHalfOpen = newLayoutInfo.displayFeatures
                                .filterIsInstance<FoldingFeature>()
                                .map { it.state }
                                .contains(FoldingFeature.State.HALF_OPENED)
                        )
                    }
            }
        }
    }
    return foldableInfo
}