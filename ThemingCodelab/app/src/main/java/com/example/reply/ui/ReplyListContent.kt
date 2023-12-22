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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.components.EmailDetailAppBar
import com.example.reply.ui.components.ReplyEmailListItem
import com.example.reply.ui.components.ReplyEmailThreadItem
import com.example.reply.ui.components.ReplySearchBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * This Composable is composed into the UI by the [ReplyAppContent] Composable when its [MutableState]
 * of [String] variable `selectedDestination` is [ReplyRoute.INBOX]. We start by initializing and
 * remembering our [LazyListState] variable `val emailLazyListState` (which we pass down to our
 * [ReplyEmailListContent] Composable as its `emailLazyListState` argument). Our root Composable is
 * a [Box] whose `modifier` argument is our [Modifier] parameter [modifier] with a [Modifier.fillMaxSize]
 * chained to it so that it will occupy our entire incoming size constraints.
 *
 * @param replyHomeUIState the current [ReplyHomeUIState]. It is passed down the hierarchy from
 * the `onCreate` override of [MainActivity] where it is collected as [State] in a lifecycle-aware
 * manner from the [ReplyHomeViewModel.uiState] property, which is a read-only [StateFlow] of its
 * private [MutableStateFlow] of [ReplyHomeUIState] field `_uiState`.
 * @param closeDetailScreen this lambda is passed down to [ReplyEmailListContent] as its `closeDetailScreen`
 * argument which passes it down to its [ReplyEmailListContent] where it is used as the `onBack` argument
 * of its [BackHandler] as well as the `onBackPressed` argument of its [ReplyEmailDetail]. It is passed
 * down the hierarchy from the `onCreate` override of [MainActivity] where it is a call to the
 * [ReplyHomeViewModel.closeDetailScreen] method, which sets the current value of
 * [ReplyHomeUIState.isDetailOnlyOpen] to `false`, and [ReplyHomeUIState.selectedEmail] to the first
 * [Email] in the [List] of [Email] field [ReplyHomeUIState.emails] (thereby replacing the [ReplyEmailDetail]
 * Composable of [ReplyEmailListContent] with the [ReplyEmailList] Composable).
 * @param navigateToDetail this lambda is passed down to [ReplyEmailListContent] as its `navigateToDetail`
 * argument which passes it down to its [ReplyEmailListContent] Composable where it is used as the
 * `navigateToDetail` argument to its [ReplyEmailList] Composable where it is used as the `navigateToDetail`
 * argument of each of the [ReplyEmailListItem] Composables in its [LazyColumn], where it is used as the
 * `onClick` argument of the `Modifier.clickable` of its root [Card]. It is passed down the hierarchy from
 * the `onCreate` override of [MainActivity] where it is a call to the [ReplyHomeViewModel.setSelectedEmail]
 * method with the `emailId` of the [Email] displayed by the [ReplyEmailListItem]. The method sets the
 * current value of [ReplyHomeUIState.isDetailOnlyOpen] to `true`, and [ReplyHomeUIState.selectedEmail]
 * to the [Email] it finds in the [List] of [Email] in [ReplyHomeUIState.emails] with the same `emailId`
 * as the [Long] passed the lambda (thereby replacing the [ReplyEmailList] Composable of [ReplyEmailListContent]
 * with the [ReplyEmailDetail] Composable).
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or behavior.
 * Our [ReplyAppContent] caller passes us a [ColumnScope] `Modifier.weight` of 1f which causes us to
 * occupy all the remaining space in the incoming vertical constaint after our siblings have been
 * measured and placed.
 */
@Composable
fun ReplyInboxScreen(
    replyHomeUIState: ReplyHomeUIState,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    /**
     * This [LazyListState] is used as the `emailLazyListState` argument of our [ReplyEmailListContent]
     * Composable, which uses it as the `emailLazyListState` argument of its [ReplyEmailList], which
     * uses it as the `state` argument of its [LazyColumn]. It is not used anywhere else for anything.
     */
    val emailLazyListState: LazyListState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        ReplyEmailListContent(
            replyHomeUIState = replyHomeUIState,
            emailLazyListState = emailLazyListState,
            modifier = Modifier.fillMaxSize(),
            closeDetailScreen = closeDetailScreen,
            navigateToDetail = navigateToDetail
        )

        LargeFloatingActionButton(
            onClick = { /*Click Implementation*/ },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit),
                modifier = Modifier.size(28.dp)
            )
        }

    }
}

/**
 *
 */
@Composable
fun ReplyEmailListContent(
    replyHomeUIState: ReplyHomeUIState,
    emailLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit
) {
    if (replyHomeUIState.selectedEmail != null && replyHomeUIState.isDetailOnlyOpen) {
        BackHandler {
            closeDetailScreen()
        }
        ReplyEmailDetail(email = replyHomeUIState.selectedEmail) {
            closeDetailScreen()
        }
    } else {
        ReplyEmailList(
            emails = replyHomeUIState.emails,
            emailLazyListState = emailLazyListState,
            modifier = modifier,
            navigateToDetail = navigateToDetail
        )
    }
}

/**
 *
 */
@Composable
fun ReplyEmailList(
    emails: List<Email>,
    emailLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    selectedEmail: Email? = null,
    navigateToDetail: (Long) -> Unit
) {
    LazyColumn(modifier = modifier, state = emailLazyListState) {
        item {
            ReplySearchBar(modifier = Modifier.fillMaxWidth())
        }
        items(items = emails, key = { it.id }) { email ->
            ReplyEmailListItem(
                email = email,
                isSelected = email.id == selectedEmail?.id
            ) { emailId: Long ->
                navigateToDetail(emailId)
            }
        }
    }
}

/**
 *
 */
@Composable
fun ReplyEmailDetail(
    email: Email,
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = true,
    onBackPressed: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        item {
            EmailDetailAppBar(email = email, isFullScreen = isFullScreen) {
                onBackPressed()
            }
        }
        items(items = email.threads, key = { it.id }) { email ->
            ReplyEmailThreadItem(email = email)
        }
    }
}
