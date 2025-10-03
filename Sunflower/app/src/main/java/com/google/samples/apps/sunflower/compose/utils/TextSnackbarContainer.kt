/*
 * Copyright 2020 Google LLC
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

package com.google.samples.apps.sunflower.compose.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

/**
 * A container that manages the display of a text-based [Snackbar].
 *
 * This composable wraps the provided [content] and shows a [Snackbar] with the given [snackbarText]
 * when [showSnackbar] is true. The snackbar is automatically dismissed after a short duration,
 * at which point [onDismissSnackbar] is called.
 *
 * Our root composable is a [Box] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In the [BoxScope] `content` composable lambda argument of the [Box] we first compose our
 * composable lambda parameter [content]. Then we initialize and remember our [State] wrapped lambda
 * variable `onDismissState` with our lambda parameter [onDismissSnackbar]. We compose a
 * [LaunchedEffect] whose `key1` is our [Boolean] parameter [showSnackbar] and `key2` is our [String]
 * parameter [snackbarText]. In the [CoroutineScope] `block` lambda argument of the [LaunchedEffect]
 * if [showSnackbar] is `true` inside a `try-finally` we call the [SnackbarHostState.showSnackbar]
 * method of our [SnackbarHostState] parameter [snackbarHostState] with the given [snackbarText]
 * as its `message` argument and [SnackbarDuration.Short] as its `duration` argument, and in the
 * `finally` block we call our lambda variable `onDismissState`.
 *
 * Finally wrapped in a [MaterialTheme] whose `shapes` argument is a new instance of [Shapes] we
 * compose a [SnackbarHost] whose `hostState` argument is our [SnackbarHostState] parameter,
 * and whose `modifier` argument chains to our [Modifier] parameter [modifier] a [BoxScope.align]
 * whose `alignment` argument is [Alignment.BottomCenter], chained to a [Modifier.systemBarsPadding]
 * to add padding to accommodate the system bars insets, and chained to that is a [Modifier.padding]
 * that adds 8.dp padding all sides. In the `snackbar` composable lambda argument of the [SnackbarHost]
 * we accept the [SnackbarData] passed the lambda in variable `snackbarData` and compose a [Snackbar]
 * whose `snackbarData` argument is our [SnackbarData] variable `snackbarData`.
 *
 * @param snackbarText The text to display in the snackbar.
 * @param showSnackbar A boolean flag that triggers the display of the snackbar when `true`.
 * @param onDismissSnackbar A lambda to be invoked when the snackbar is dismissed. This should
 * typically reset the state that caused the snackbar to be shown.
 * @param modifier The [Modifier] to be applied to the container.
 * @param snackbarHostState The [SnackbarHostState] used to manage the snackbar. A new instance
 * is created and remembered by default.
 * @param content The main content to be displayed within the container. The snackbar will be
 * shown on top of this content.
 */
@Composable
fun TextSnackbarContainer(
    snackbarText: String,
    showSnackbar: Boolean,
    onDismissSnackbar: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        val onDismissState: () -> Unit by rememberUpdatedState(newValue = onDismissSnackbar)
        LaunchedEffect(key1 = showSnackbar, key2 = snackbarText) {
            if (showSnackbar) {
                try {
                    snackbarHostState.showSnackbar(
                        message = snackbarText,
                        duration = SnackbarDuration.Short
                    )
                } finally {
                    onDismissState()
                }
            }
        }

        // Override shapes to not use the ones coming from the MdcTheme
        MaterialTheme(shapes = Shapes()) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = modifier
                    .align(alignment = Alignment.BottomCenter)
                    .systemBarsPadding()
                    .padding(all = 8.dp),
            ) { snackbarData: SnackbarData ->
                Snackbar(snackbarData = snackbarData)
            }
        }
    }
}
