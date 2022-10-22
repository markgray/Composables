/*
 * Copyright 2022 Google LLC
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

package com.example.reply.ui

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.example.reply.data.Email
import com.example.reply.data.local.LocalEmailsDataProvider
import com.example.reply.ui.theme.ReplyTheme
import com.example.reply.ui.utils.DevicePosture
import com.example.reply.ui.utils.isBookPosture
import com.example.reply.ui.utils.isSeparating
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * This is the [MainActivity] of the "Build adaptive apps with Jetpack Compose Codelab" which can
 * be found at: https://codelabs.developers.google.com/jetpack-compose-adaptability
 * It uses the [ExperimentalMaterial3WindowSizeClassApi] to calculate the [WindowSizeClass] for our
 * [Activity] and our Composables adjust the UI that they display to take advantage of the different
 * screen sizes and [DevicePosture]'s that can occur.
 */
class MainActivity : ComponentActivity() {

    /**
     * The [ReplyHomeViewModel] that is used to provide the [ReplyHomeUIState] used as the `uiState`
     * argument of [ReplyApp] as a [StateFlow]. The [Flow.collectAsState] extension function is used
     * to collect values from this [Flow] and supply its latest value to [ReplyApp] as a [State]
     * which will cause it to recompose whenever a new value is emitted. The [ReplyHomeUIState.emails]
     * field is then used by all the children of [ReplyApp] to supply the [List] of [Email] that they
     * display.
     */
    private val viewModel: ReplyHomeViewModel by viewModels()

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`.
     * Then we create a [StateFlow] of [DevicePosture] which is our sealed interface whose three
     * implementors [DevicePosture.NormalPosture] (has no special features that require consideration),
     * [DevicePosture.BookPosture] (device has a feature that describes a fold in its flexible
     * display or a hinge between two physical display panels), and [DevicePosture.Separating] (the
     * device has a [FoldingFeature] that should be thought of as splitting the window into multiple
     * physical areas that can be seen by users as logically separate. Display panels connected by a
     * hinge are always separated). To create this [StateFlow] we use the [WindowInfoTracker.getOrCreate]
     * method to get a [WindowInfoTracker] associated with `this` [Context], which we use to call
     * its [WindowInfoTracker.windowLayoutInfo] to obtain a [Flow] of [WindowLayoutInfo] to which we
     * apply the [flowWithLifecycle] extension function with its `lifecycle` the [Lifecycle] of this
     * (this will cancel the upstream [Flow] when the [Lifecycle] falls below [Lifecycle.State.STARTED]),
     * we then apply the [map] extension function to this [Flow] whose lambda block initializes its
     * [FoldingFeature] variable `val foldingFeature` by filtering the [WindowLayoutInfo.displayFeatures]
     * from the incoming [Flow] of [WindowLayoutInfo] for instances of [FoldingFeature] and uses the
     * [firstOrNull] extension function on the [List] to filter it down to one [FoldingFeature] or
     * `null` if there are none. Then a `when` statement transforms the [FoldingFeature] to a
     * [DevicePosture] like so:
     *  - if the [isBookPosture] method returns `true` for the [FoldingFeature] it creates a
     *  [DevicePosture.BookPosture] with its `hingePosition` field the [Rect] stored in the
     *  [FoldingFeature.bounds] property.
     *  - if the [isSeparating] method returns `true` for the [FoldingFeature] it creates a
     *  [DevicePosture.Separating] with its `hingePosition` field the [Rect] stored in the
     *  [FoldingFeature.bounds] property, and its `orientation` field the [FoldingFeature.Orientation]
     *  found in the [FoldingFeature.orientation] property.
     *  - else it creates a [DevicePosture.NormalPosture]
     *
     * The [Flow] of [DevicePosture] produced by the [map] is then fed to the [stateIn] extension
     * function with its `scope` argument the [CoroutineScope] tied to this [LifecycleOwner]'s
     * [Lifecycle] that is returned by [lifecycleScope], its `started` argument [SharingStarted.Eagerly]
     * (Sharing is started immediately and never stops), and its `initialValue` [DevicePosture.NormalPosture].
     * The resulting output of all of this is a [StateFlow] of [DevicePosture].
     *
     * Finally we call [setContent] with a lambda block which wraps its `content` in our [ReplyTheme]
     * custom [MaterialTheme]. Inside of the [ReplyTheme] we initialize our [ReplyHomeUIState] variable
     * `val uiState` by using the [collectAsState] extension function on the [ReplyHomeViewModel.uiState]
     * field of [viewModel] using the [State.value] of the [State]. We initialize our [WindowSizeClass]
     * variable `val windowSize` to the value returned by the [calculateWindowSizeClass] method for
     * our [Activity] (calculates the [WindowSizeClass] of our [Window]. A new [WindowSizeClass] will
     * be returned whenever a configuration change causes the width or height of the window to cross
     * a breakpoint, such as when the device is rotated or the window is resized). And we initialize
     * our [DevicePosture] variable `val devicePosture` by using the [collectAsState] extension function
     * on the `devicePostureFlow` using the [State.value] of that [State]. We then call our [ReplyApp]
     * Composable with the arguments:
     *  - `replyHomeUIState` = `uiState` the decendents of [ReplyApp] use the [ReplyHomeUIState.emails]
     *  [List] of [Email] for the data they display.
     *  - `windowSize` is the [WindowSizeClass.widthSizeClass] of `windowSize`, [ReplyApp] will use a
     *  `when` statement to determine the value of the arguments it feeds to [ReplyNavigationWrapperUI]
     *  - `foldingDevicePosture` is the latest [DevicePosture] of `devicePosture`
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so ignore it.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Flow of [DevicePosture] that emits every time there's a change in the windowLayoutInfo
         */
        val devicePostureFlow: StateFlow<DevicePosture> = WindowInfoTracker
            .getOrCreate(context = this)
            .windowLayoutInfo(activity = this)
            .flowWithLifecycle(lifecycle = this.lifecycle)
            .map { layoutInfo: WindowLayoutInfo ->
                val foldingFeature: FoldingFeature? =
                    layoutInfo.displayFeatures
                        .filterIsInstance<FoldingFeature>()
                        .firstOrNull()
                when {
                    isBookPosture(foldFeature = foldingFeature) ->
                        DevicePosture.BookPosture(hingePosition = foldingFeature.bounds)

                    isSeparating(foldFeature = foldingFeature) ->
                        DevicePosture.Separating(
                            hingePosition = foldingFeature.bounds,
                            orientation = foldingFeature.orientation
                        )

                    else -> DevicePosture.NormalPosture
                }
            }
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = DevicePosture.NormalPosture
            )

        setContent {
            ReplyTheme {
                val uiState: ReplyHomeUIState = viewModel.uiState.collectAsState().value
                val windowSize: WindowSizeClass = calculateWindowSizeClass(this)
                val devicePosture: DevicePosture = devicePostureFlow.collectAsState().value
                ReplyApp(
                    replyHomeUIState = uiState,
                    windowSize = windowSize.widthSizeClass,
                    foldingDevicePosture = devicePosture
                )
            }
        }
    }
}

/**
 * Preview of our [ReplyApp] for a `windowSize` argument [WindowWidthSizeClass.Compact] (Represents
 * the majority of phones in portrait) whose `foldingDevicePosture` [DevicePosture] argument is
 * [DevicePosture.NormalPosture] (which is defined as just an object the implements the [DevicePosture]
 * sealed interface, whereas its siblings [DevicePosture.BookPosture] and [DevicePosture.Separating]
 * are data classes which implement [DevicePosture] and contain additional fields). The type of the
 * `foldingDevicePosture` parameter is all that is important to [ReplyApp]. This is wrapped inside
 * our [ReplyTheme] custom [MaterialTheme]. The `replyHomeUIState` argument to [ReplyApp] is just
 * a [ReplyHomeUIState] constructed to contain all of the fake `emails` in the [MutableList] of
 * [Email] in the [LocalEmailsDataProvider.allEmails] field.
 */
@Preview(showBackground = true)
@Composable
fun ReplyAppPreview() {
    ReplyTheme {
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowWidthSizeClass.Compact,
            foldingDevicePosture = DevicePosture.NormalPosture,
        )
    }
}

/**
 * Preview of our [ReplyApp] for a `windowSize` argument [WindowWidthSizeClass.Medium] (Represents
 * the majority of tablets in portrait and large unfolded inner displays in portrai) whose
 * `foldingDevicePosture` [DevicePosture] argument is [DevicePosture.NormalPosture] (which is defined
 *  sealed interface, whereas its siblings [DevicePosture.BookPosture] and [DevicePosture.Separating]
 * are data classes which implement [DevicePosture] and contain additional fields). The type of the
 * `foldingDevicePosture` parameter is all that is important to [ReplyApp]. This is wrapped inside
 * our [ReplyTheme] custom [MaterialTheme]. The `replyHomeUIState` argument to [ReplyApp] is just
 * a [ReplyHomeUIState] constructed to contain all of the fake `emails` in the [MutableList] of
 * [Email] in the [LocalEmailsDataProvider.allEmails] field.
 */
@Preview(showBackground = true, widthDp = 700)
@Composable
fun ReplyAppPreviewTablet() {
    ReplyTheme {
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowWidthSizeClass.Medium,
            foldingDevicePosture = DevicePosture.NormalPosture,
        )
    }
}

/**
 * Preview of our [ReplyApp] for a `windowSize` argument [WindowWidthSizeClass.Expanded] (Represents
 * the majority of tablets in landscape and large unfolded inner displays in landscape) whose
 * `foldingDevicePosture` [DevicePosture] argument is [DevicePosture.NormalPosture] (which is defined
 * as just an object the implements the [DevicePosture] sealed interface, whereas its siblings
 * [DevicePosture.BookPosture] and [DevicePosture.Separating] are data classes which implement
 * [DevicePosture] and contain additional fields). The type of the `foldingDevicePosture` parameter
 * is all that is important to [ReplyApp]. This is wrapped inside our [ReplyTheme] custom
 * [MaterialTheme]. The `replyHomeUIState` argument to [ReplyApp] is just a [ReplyHomeUIState]
 * constructed to contain all of the fake `emails` in the [MutableList] of [Email] in the
 * [LocalEmailsDataProvider.allEmails] field.
 */
@Preview(showBackground = true, widthDp = 1000)
@Composable
fun ReplyAppPreviewDesktop() {
    ReplyTheme {
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowWidthSizeClass.Expanded,
            foldingDevicePosture = DevicePosture.NormalPosture,
        )
    }
}