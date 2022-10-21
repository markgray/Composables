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
import android.os.Bundle
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
 * screen sizes and [DevicePosture]'s that.
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
     * Called when the activity is starting.
     *
     * @param savedInstanceState we do not implement [onSaveInstanceState] so ignore it.
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