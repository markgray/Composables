/*
 * Copyright 2022 The Android Open Source Project
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

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.reply.data.LocalEmailsDataProvider
import com.example.reply.ui.theme.AppTheme
import kotlinx.coroutines.flow.StateFlow

/**
 * This is the main activity of our app and it exists to host our [ReplyHomeViewModel] in order to
 * transmit [ReplyHomeUIState] state from it to down to the [ReplyApp] Composable and to propagate
 * the `closeDetailScreen` and `navigateToDetail` events up to the [ReplyHomeViewModel].
 */
class MainActivity : ComponentActivity() {

    /**
     * The [ReplyHomeViewModel] view model used by our app.
     */
    private val viewModel: ReplyHomeViewModel by viewModels()

    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to
     * edge display, then we call our super's implementation of `onCreate`. Next we call [setContent]
     * to have it compose its `content` Composable lambda argument into the activity. In that
     * lambda we initialize [ReplyHomeUIState] variable `val uiState` by collecting values from the
     * [StateFlow] of [ReplyHomeUIState] that is emitted by the [ReplyHomeViewModel.uiState] property
     * and representing its latest value via a [State] in a lifecycle-aware manner. Then we compose
     * [AppTheme] to supply default values to [MaterialTheme] composables in its `content` composable
     * lambda argument. The `content` of the [AppTheme] Composable is a [Box] whose `modifier`
     * argument is a [Modifier.safeDrawingPadding] to add padding to accommodate the safe drawing
     * insets. In the [BoxScope] `content` composable lambda argument of the [Box] we compose a
     * [Surface] whose `tonalElevation` argument is 5.dp (When color is ColorScheme.surface, a
     * higher elevation will result in a darker color in light theme and lighter color in dark
     * theme). The `content` of the [Surface] is our [ReplyApp] Composable, with its
     * `replyHomeUIState` argument our [ReplyHomeUIState] variable `uiState`, whose
     * `closeDetailScreen` argument is a lambda that calls the [ReplyHomeViewModel.closeDetailScreen]
     * method of our [ReplyHomeViewModel] field [viewModel], and whose `navigateToDetail` argument
     * is a lambda which calls the [ReplyHomeViewModel.setSelectedEmail] method of our
     * [ReplyHomeViewModel] field [viewModel] with the [Long] `emailId` passed to the lambda used
     * for the `emailId` argument.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use it.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {

            /**
             * Collects values from the [StateFlow] of [ReplyHomeUIState] that is emitted by the
             * [ReplyHomeViewModel.uiState] property and represents its latest value via [State] in
             * a lifecycle-aware manner.
             */
            val uiState: ReplyHomeUIState by viewModel.uiState.collectAsStateWithLifecycle()

            AppTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    Surface(tonalElevation = 5.dp) {
                        ReplyApp(
                            replyHomeUIState = uiState,
                            closeDetailScreen = {
                                viewModel.closeDetailScreen()
                            },
                            navigateToDetail = { emailId: Long ->
                                viewModel.setSelectedEmail(emailId = emailId)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * The Previews of our [AppTheme] wrapped [ReplyApp], "DefaultPreviewDark" uses [UI_MODE_NIGHT_YES]
 * as its `uiMode` so that our [darkColorScheme] is used and "DefaultPreviewLight" uses
 * [UI_MODE_NIGHT_NO] as its `uiMode` so that our [lightColorScheme] is used.
 */
@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun ReplyAppPreview() {
    AppTheme {
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(
                emails = LocalEmailsDataProvider.allEmails
            )
        )
    }
}
