/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.ui

import android.content.Context
import android.view.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children

/**
 * A [DeviceConfigurationOverride] that overrides the window insets for the contained content.
 *
 * @param windowInsets
 */
@Suppress("ktlint:standard:function-naming", "TestFunctionName")
fun DeviceConfigurationOverride.Companion.WindowInsets(
    windowInsets: WindowInsetsCompat,
): DeviceConfigurationOverride = DeviceConfigurationOverride { contentUnderTest ->
    val currentContentUnderTest: @Composable (() -> Unit) by rememberUpdatedState(contentUnderTest)
    val currentWindowInsets: WindowInsetsCompat by rememberUpdatedState(windowInsets)
    AndroidView(
        factory = { context: Context ->
            object : AbstractComposeView(context) {
                /**
                 * The Jetpack Compose UI content for this view. Subclasses must implement this
                 * method to provide content. Initial composition will occur when the view becomes
                 * attached to a window or when createComposition is called, whichever comes first.
                 */
                @Composable
                override fun Content() {
                    currentContentUnderTest()
                }

                /**
                 * Applies [WindowInsets] parameter [insets] to all of its children
                 */
                override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
                    children.forEach {
                        it.dispatchApplyWindowInsets(
                            WindowInsets(currentWindowInsets.toWindowInsets()),
                        )
                    }
                    return WindowInsetsCompat.CONSUMED.toWindowInsets()!!
                }

                /**
                 * Deprecated, but intercept the `requestApplyInsets` call via the deprecated
                 * method.
                 */
                @Deprecated("Deprecated in Java")
                override fun requestFitSystemWindows() {
                    dispatchApplyWindowInsets(WindowInsets(currentWindowInsets.toWindowInsets()!!))
                }
            }
        },
        update = { with(currentWindowInsets) { it.requestApplyInsets() } },
    )
}
