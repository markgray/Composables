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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
 * chained to it so that it will occupy our entire incoming size constraints. The `content` of the [Box]
 * is a [ReplyEmailListContent] Composable whose `replyHomeUIState` argument is our [ReplyHomeUIState]
 * parameter [replyHomeUIState], whose `emailLazyListState` argument is our [LazyListState] variable
 * `emailLazyListState`, whose `modifier` argument is a [Modifier.fillMaxSize] to have it take up the
 * entire incoming size constraints, whose `closeDetailScreen` argument is our lambda parameter
 * [closeDetailScreen], and  whose `navigateToDetail` argument is our lambda parameter [navigateToDetail].
 * It share the [Box] with a [LargeFloatingActionButton] whose `onClick` argument is a do-nothing lambda,
 * whose `containerColor` is the [ColorScheme.tertiaryContainer] color of our [MaterialTheme], whose
 * `contentColor` argument is the [ColorScheme.onTertiaryContainer] color of our [MaterialTheme], and
 * whose `modifier` argument is a [BoxScope] `Modifier.align` with an `alignment` argument of
 * [Alignment.BottomEnd] to align the [LargeFloatingActionButton] at the bottom end of the [Box], to
 * which is chained a  [Modifier.padding] that adds 16.dp to all its sides. The `content` of the
 * [LargeFloatingActionButton] is an [Icon] displaying as its `imageVector` argument the [ImageVector]
 * [Icons.Filled.Edit], its `contentDescription` argument is the [String] with resource ID `R.string.edit`
 * ("Edit"), and its `modifier` argument is a [Modifier.size] that sets its size to 28.dp.
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
                .align(alignment = Alignment.BottomEnd)
                .padding(all = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit),
                modifier = Modifier.size(size = 28.dp)
            )
        }

    }
}

/**
 * This is the actual main screen of the app. An if statement decides whether to display the
 * [ReplyHomeUIState.selectedEmail] in a [ReplyEmailDetail] if the [ReplyHomeUIState.selectedEmail]
 * property of its [ReplyHomeUIState] parameter [replyHomeUIState] is not `null` and its
 * [ReplyHomeUIState.isDetailOnlyOpen] property is `true`, otherwise it displays the [List] of [Email]
 * in the [ReplyHomeUIState.emails] property of [replyHomeUIState] using a [ReplyEmailList] Composable.
 * When [ReplyHomeUIState.selectedEmail] is not `null` and [ReplyHomeUIState.isDetailOnlyOpen] property
 * is `true` it composes a [BackHandler] whose `onBack` argument is a lambda which calls our [closeDetailScreen]
 * lambda parameter, and a [ReplyEmailDetail] whose `email` argument is the [ReplyHomeUIState.selectedEmail]
 * property of our [ReplyHomeUIState] parameter [replyHomeUIState], and whose `onBackPressed` argument
 * is a lambda that calls our [closeDetailScreen] lambda parameter. The `else` of the `if` composes a
 * [ReplyEmailList] whose `emails` argument is the [List] of [Email] in the [ReplyHomeUIState.emails]
 * property of our [ReplyHomeUIState] parameter [replyHomeUIState], whose `emailLazyListState` argument
 * is our [LazyListState] parameter [emailLazyListState], whose `modifier` argument is our [modifier]
 * parameter, and whose `navigateToDetail` argument is our [navigateToDetail] lambda parameter.
 *
 * @param replyHomeUIState the current [ReplyHomeUIState] maintained by the [ReplyHomeViewModel] of the
 * app. [MainActivity] collects as state the [StateFlow] of [ReplyHomeUIState] property
 * [ReplyHomeViewModel.uiState] to keep this up to date as the [ReplyHomeViewModel] modifies it in
 * reponse to events reported to it.
 * @param emailLazyListState a [LazyListState] passed down from [ReplyInboxScreen] which could be used
 * to monitor and control the [LazyColumn] in [ReplyEmailList] but is not used except by the [LazyColumn].
 * @param modifier a [Modifier] instance that our caller can use to modifiy our appearance and/or
 * behavior. Our caller [ReplyInboxScreen] passes us a [Modifier.fillMaxSize] to have use use the entire
 * incoming size constraints.
 * @param closeDetailScreen a lambda to call to close the [ReplyEmailDetail]. It is passed down the
 * hierarchy from the `onCreate` override of [MainActivity] where it is a call to the
 * [ReplyHomeViewModel.closeDetailScreen] method, which sets the current value of
 * [ReplyHomeUIState.isDetailOnlyOpen] to `false`, and [ReplyHomeUIState.selectedEmail] to the first
 * [Email] in the [List] of [Email] field [ReplyHomeUIState.emails].
 * @param navigateToDetail this lambda should be called to have the [ReplyEmailList] Composable be
 * replaced by a [ReplyEmailDetail] display the [Email] whose [Long] property [Email.id] is the argument
 * passed the lambda. It is passed down the hierarchy from the `onCreate` override of [MainActivity]
 * where it is a call to the [ReplyHomeViewModel.setSelectedEmail] method with the `emailId` of the
 * [Email] displayed by the [ReplyEmailListItem]. The method sets the current value of
 * [ReplyHomeUIState.isDetailOnlyOpen] to `true`, and [ReplyHomeUIState.selectedEmail] to the [Email]
 * it finds in the [List] of [Email] in [ReplyHomeUIState.emails] with the same `emailId` as the [Long]
 * passed the lambda (thereby replacing the [ReplyEmailList] Composable of [ReplyEmailListContent]
 * with the [ReplyEmailDetail] Composable).
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
 * This Composable displays its [List] of [Email] parameter in a [LazyColumn] with each [Email]
 * rendered in a [ReplyEmailListItem]. The `modifier` argument of the [LazyColumn] is our [Modifier]
 * parmeter [modifier], and the `state` argument if our [LazyListState] parameter [emailLazyListState].
 * The first `item` in the [LazyColumn] is a [ReplySearchBar] whose `modifier` argument is a
 * [Modifier.fillMaxWidth] to have it occupy the entire incoming width constraint. There follows an
 * `items` whose whose `items` argument is our [List] of [Email] parameter [emails] and whose `key`
 * argument is the [Email.id] of each [Email] (used as factory of stable and unique keys representing
 * the items). And the Composable used to display each [Email] (its `itemContent` argument) is a
 * [ReplyEmailListItem] whose `email` argument is the [Email] that it is passed it by the `items`,
 * whose `isSelected` argument is `true` iff the [Email.id] of the `email` is the same as the
 * [Email.id] of our [Email] parameter [selectedEmail], and its `navigateToDetail` argument is a
 * lambda that calls our lambda parameter [navigateToDetail] with the [Email.id] passed it.
 *
 * @param emails the [List] of [Email] that we are supposed to display in our [LazyColumn].
 * @param emailLazyListState the [LazyListState] that we use as the `state` argument of our
 * [LazyColumn] which our caller could use to monitor and/or control the [LazyColumn], but our
 * caller does not use the one it passes us for anything.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [ReplyEmailListContent] passes us the [Modifier] that it is passed as its
 * `modifier` argument by [ReplyInboxScreen], which is a [Modifier.fillMaxSize] which causes any
 * Composable that uses it to occupy the entire incoming size constraints.
 * @param selectedEmail the currently "selected" [Email] instance. Its [Email.id] is compared to
 * the [Email.id] of the particular [Email] that each [ReplyEmailListItem] is rendering to generate
 * its [Boolean] argument `isSelected`. [ReplyEmailListContent] passes us the
 * [ReplyHomeUIState.selectedEmail] property of the current [ReplyHomeUIState] that it is passed
 * as its `replyHomeUIState` argument.
 * @param navigateToDetail a lambda that we pass to each [ReplyEmailListItem] that it can call
 * with the [Email.id] of the [Email] it holds in order for it to become the currently "selected"
 * [Email] instance. [ReplyEmailListContent] passes us the `navigateToDetail` argument that it is
 * passed as its `navigateToDetail` argument by [ReplyInboxScreen]. It is passed down the hierarchy
 * from the `onCreate` override of [MainActivity] where it is a call to the
 * [ReplyHomeViewModel.setSelectedEmail] method with the [Email.id] passed the lambda as its argument.
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
        items(items = emails, key = { it.id }) { email: Email ->
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
 * This Composable is composed into the UI by [ReplyEmailListContent] when [ReplyHomeUIState.selectedEmail]
 * propery of its [ReplyHomeUIState] parameter `replyHomeUIState` is not `null` and its
 * [ReplyHomeUIState.isDetailOnlyOpen] property is `true`. Its root Composable is a [LazyColumn] whose
 * `modifier` argument chains a [Modifier.fillMaxSize] to our [Modifier] parameter [modifier] to have
 * the [LazyColumn] occupy its entire incoming size constraints, followed by a [Modifier.padding] that
 * sets the `top` padding to 16.dp. The content of the [LazyColumn] is an `item` holding an
 * [EmailDetailAppBar] whose `email` argument is our [Email] parameter [email], which it uses to supply
 * the text for two [Text] Composables in its [TopAppBar] that display the [Email.subject] and the
 * [List.size] of the [Email.threads] list of the `email`. This is followed by a `items` displaying
 * all of the [Email] in the [List] of [Email] property [Email.threads] of our [Email] parameter [email]
 * in a [ReplyEmailThreadItem] Composable.
 *
 * @param email the [Email] that we are to display the details of. [ReplyEmailListContent] passes us
 * the [ReplyHomeUIState.selectedEmail] of the current [ReplyHomeUIState].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass one so the empty, default, or starter [Modifier] that contains
 * no elements is used.
 * @param isFullScreen we pass this as the `isFullScreen` argument of our [EmailDetailAppBar], our
 * caller does not pass a value so `true` is always used. [EmailDetailAppBar] uses in that case
 * a [Alignment.CenterHorizontally] as the `horizontalAlignment` argument of the [Column] in its
 * [TopAppBar] `title` argument.
 * @param onBackPressed the lambda that we pass as the `onBackPressed` argument of our
 * [EmailDetailAppBar]. [ReplyEmailListContent] passes us its `closeDetailScreen` lambda parameter,
 * which is is passed down the hierarchy from the `onCreate` override of [MainActivity] where it is
 * a call to the [ReplyHomeViewModel.closeDetailScreen] method, which sets the current value of
 * [ReplyHomeUIState.isDetailOnlyOpen] to `false`, and [ReplyHomeUIState.selectedEmail] to the first
 * [Email] in the [List] of [Email] field [ReplyHomeUIState.emails].
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
