/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui.components

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * [SnackbarHost] that is configured for insets and large screens. Used as the `snackbarHost` argument
 * of the [Scaffold] used by `HomeScreenWithList` (component to host [Snackbar]'s that are pushed to be
 * shown via [SnackbarHostState.showSnackbar] as is done in a [LaunchedEffect] of `HomeScreenWithList`
 * when there is a change to errorMessageText, retryMessageText or snackbarHostState). Our root
 * Composable is a [SnackbarHost] whose `hostState` argument is our [SnackbarHostState] parameter
 * [hostState], whose `modifier` argument chains a [Modifier.systemBarsPadding] to our [Modifier]
 * argument [modifier] (adds padding to accommodate the system bars insets), with a [Modifier.wrapContentWidth]
 * whose `align` argument is [Alignment.Start] (Allow the content to measure at its desired width without
 * regard for the incoming measurement minimum, with the `align` argument causing a call to
 *`WrapContentElement.width` with its `align` argument [Alignment.Start]), and a [Modifier.widthIn]
 * that sets its maximum width to 550.dp (limits the [Snackbar] width for large screens). The `snackbar`
 * argument of the [SnackbarHost] is our lambda parameter [snackbar].
 *
 * @param hostState the [SnackbarHostState] whose [SnackbarHostState.showSnackbar] method will cause
 * us to display our [Snackbar].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller `HomeScreenWithList` does not pass us one so the empty, default, or starter
 * [Modifier] that contains no elements is used.
 * @param snackbar a lambda which will compose a [Snackbar] using the [SnackbarData] passed it for its
 * [SnackbarVisuals] (contains the `message` argument passed to [SnackbarHostState.showSnackbar] in
 * its [SnackbarVisuals.message] field, and the `actionLabel` argument passed to
 * [SnackbarHostState.showSnackbar] in in its [SnackbarVisuals.actionLabel] field. Our caller does
 * not override our default which is just the basic lambda which composes a [Snackbar] using the
 * [SnackbarData] passed the lambda as the `snackbarData` argument of the [Snackbar]
 */
@Composable
fun JetnewsSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(snackbarData = it) }
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .systemBarsPadding()
            // Limit the Snackbar width for large screens
            .wrapContentWidth(align = Alignment.Start)
            .widthIn(max = 550.dp),
        snackbar = snackbar
    )
}
