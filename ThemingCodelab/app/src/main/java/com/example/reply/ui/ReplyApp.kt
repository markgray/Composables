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

@file:Suppress("Destructure")

package com.example.reply.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.components.ReplyEmailListItem
import com.example.reply.ui.components.ReplyEmailThreadItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * This is the root composable of our app. It appears to exist solely to wrap [ReplyAppContent] in
 * a [Surface] whose `tonalElevation` argument of 5.dp causes its background color to be a darker
 * color in light theme and lighter color in dark theme in order to suggest elevation.
 *
 * @param replyHomeUIState the current [ReplyHomeUIState] that is collected as [State] from the
 * [ReplyHomeViewModel.uiState] property by [MainActivity].
 * @param closeDetailScreen a lambda to call in order to close the [ReplyEmailDetail] email detail
 * screen. [MainActivity] passes a lambda which calls the [ReplyHomeViewModel.closeDetailScreen]
 * method of our apps viewmodel. This method updates the value of its [MutableStateFlow] of
 * [ReplyHomeUIState] by setting its [ReplyHomeUIState.isDetailOnlyOpen] property to `false` and
 * its [ReplyHomeUIState.selectedEmail] property to the [List.first] of the [List] of [Email] in
 * the [ReplyHomeUIState.emails] property of the old [ReplyHomeUIState].
 * @param navigateToDetail a lambda for [ReplyEmailListItem] to call with the [Email.id] of its
 * [Email] in order to have [ReplyEmailListContent] have [ReplyEmailDetail] display that [Email]'s
 * [Email.threads] property in [ReplyEmailThreadItem] widgets in a [LazyColumn] ([Email.threads] is
 * a [List] of [Email]). [MainActivity] passes a lambda which calls the [ReplyHomeViewModel.setSelectedEmail]
 * method of [ReplyHomeViewModel] with the [Long] passed to the lambda. This method updates the value
 * of its [MutableStateFlow] of  [ReplyHomeUIState] by setting its [ReplyHomeUIState.isDetailOnlyOpen]
 * property to `true` and its [ReplyHomeUIState.selectedEmail] to the [Email] that the [List.find]
 * method of the [List] of [Email] property [ReplyHomeUIState.emails] finds with that [Email.id].
 */
@Composable
fun ReplyApp(
    replyHomeUIState: ReplyHomeUIState,
    closeDetailScreen: () -> Unit = {},
    navigateToDetail: (Long) -> Unit = {}
) {
    Surface(tonalElevation = 5.dp) {
        ReplyAppContent(
            replyHomeUIState = replyHomeUIState,
            closeDetailScreen = closeDetailScreen,
            navigateToDetail = navigateToDetail
        )
    }
}

/**
 * This is the main screen of the app. We start by initializing and remembering our [MutableState]
 * of [String] variable `val selectedDestination` with an initial value of [ReplyRoute.INBOX]. Our
 * root Composable is a [Column] whose `modifier` argument chains a [Modifier.fillMaxSize] to our
 * [Modifier] parameter [modifier] to have the [Column] occupy the entire incoming size constraints
 * The `content` of the [Column] is an if/else which displays the [ReplyInboxScreen] Composable if
 * the [MutableState.value] of our [MutableState] of [String] variable `selectedDestination` is equal
 * to [ReplyRoute.INBOX], otherwise it displays the [EmptyComingSoon] Composable. At the bottom of
 * the screen is a [NavigationBar] holding a [NavigationBarItem] for each of the [ReplyTopLevelDestination]
 * in [List] of [ReplyTopLevelDestination] field [TOP_LEVEL_DESTINATIONS].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance, and/or
 * behavior. Our [ReplyApp] caller does not pass us one, so the empty, default, or starter [Modifier]
 * that contains no elements is used instead.
 * @param replyHomeUIState the most current value of [ReplyHomeUIState]. [ReplyApp] passes us the
 * value passed it in the `onCreate` override of [MainActivity], and [MainActivity] collects as state
 * the [StateFlow] of [ReplyHomeUIState] property [ReplyHomeViewModel.uiState] to keep this up to
 * date as the [ReplyHomeViewModel] modifies it in reponse to events reported to it.
 * @param closeDetailScreen this lambda is passed as the `closeDetailScreen` argument of our
 * [ReplyInboxScreen] which passes it as the `closeDetailScreen` argument of its [ReplyEmailListContent],
 * which uses it as the [BackHandler] lambda, and as the `onBackPressed` argument passed to its
 * [ReplyEmailDetail] Composable. [ReplyApp] passes us the `closeDetailScreen` passed it in the
 * `onCreate` override of [MainActivity] which is a lambda that calls the [ReplyHomeViewModel.closeDetailScreen]
 * method of the [ReplyHomeViewModel] which sets the [ReplyHomeUIState.isDetailOnlyOpen] property of
 * the current [ReplyHomeUIState] to `false` and sets the [ReplyHomeUIState.selectedEmail] to the first
 * [Email] in the [List] of [Email] property [ReplyHomeUIState.emails].
 * @param navigateToDetail a lambda which will be called with the [Email.id] of the [Email] displayed
 * by the [ReplyEmailListItem] Composable. It traces back up to the `onCreate` override of [MainActivity]
 * which uses a lambda which calls the [ReplyHomeViewModel.setSelectedEmail] method of the [ViewModel]
 * with the [Email.id] passed it, and that method sets the [ReplyHomeUIState.selectedEmail] of the
 * current [ReplyHomeUIState] to the [Email] in the [List] of [Email] property [ReplyHomeUIState.emails]
 * that has the same [Email.id], and sets the [ReplyHomeUIState.isDetailOnlyOpen] property to `true`.
 *
 */
@Composable
fun ReplyAppContent(
    modifier: Modifier = Modifier,
    replyHomeUIState: ReplyHomeUIState,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit,
) {

    /**
     * This variable chooses between the [ReplyTopLevelDestination]'s that are available in
     * [TOP_LEVEL_DESTINATIONS]. The only one that is actually implemented is [ReplyRoute.INBOX],
     * which composes the [ReplyInboxScreen] into the display, the others just compose the
     * [EmptyComingSoon] space holder composable. It is set by the `onClick` lambda of the
     * [NavigationBarItem]'s in the [NavigationBar] at the bottom of the screen.
     */
    val selectedDestination: MutableState<String> = remember { mutableStateOf(ReplyRoute.INBOX) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        if (selectedDestination.value == ReplyRoute.INBOX) {
            ReplyInboxScreen(
                replyHomeUIState = replyHomeUIState,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                modifier = Modifier.weight(weight = 1f)
            )
        } else {
            EmptyComingSoon(modifier = Modifier.weight(weight = 1f))
        }

        NavigationBar(modifier = Modifier.fillMaxWidth()) {
            TOP_LEVEL_DESTINATIONS.forEach { replyDestination: ReplyTopLevelDestination ->
                NavigationBarItem(
                    selected = selectedDestination.value == replyDestination.route,
                    onClick = { selectedDestination.value = replyDestination.route },
                    icon = {
                        Icon(
                            imageVector = replyDestination.selectedIcon,
                            contentDescription = stringResource(id = replyDestination.iconTextId)
                        )
                    }
                )
            }
        }
    }
}


/**
 * These are the different destinations of the app. The only one that has a "meaningful" screen is
 * [ReplyRoute.INBOX], all the others just compose the [EmptyComingSoon] skeleton destination.
 */
object ReplyRoute {
    /**
     * Composes the [ReplyInboxScreen] Composable into the UI.
     */
    const val INBOX: String = "Inbox"

    /**
     * Composables the [EmptyComingSoon] skeleton destination into the UI
     */
    const val ARTICLES: String = "Articles"

    /**
     * Composables the [EmptyComingSoon] skeleton destination into the UI
     */
    const val DM: String = "DirectMessages"

    /**
     * Composables the [EmptyComingSoon] skeleton destination into the UI
     */
    const val GROUPS: String = "Groups"
}

/**
 * This class holds values that the [NavigationBar] uses to feed to its [NavigationBarItem] for each
 * of the [ReplyRoute]'s that the [NavigationBarItem]'s represent.
 */
data class ReplyTopLevelDestination(
    /**
     * The [ReplyRoute] of this [ReplyTopLevelDestination]. It is used by the [NavigationBarItem] to
     * determine if it is the `selected` [NavigationBarItem] by comparing it to the `value` of the
     * [MutableState] of [String] variable `selectedDestination` of [ReplyAppContent], and its
     * `onClick` lambda sets `selectedDestination` to it when the [NavigationBarItem] is clicked.
     */
    val route: String,
    /**
     * This is the resource ID of an [ImageVector] for the [NavigationBarItem] to display in the
     * [Icon] used as its `icon` argument.
     */
    val selectedIcon: ImageVector,
    /**
     * This is the same resource ID as our [selectedIcon] property, and is unused at present.
     */
    val unselectedIcon: ImageVector,
    /**
     * This is the resource ID of a [String] that will be used as the `contentDescription`
     */
    val iconTextId: Int
)

/**
 * This is a list of all of the [ReplyTopLevelDestination] used to populate the [NavigationBarItem]'s
 * displayed in the [NavigationBar].
 */
val TOP_LEVEL_DESTINATIONS: List<ReplyTopLevelDestination> = listOf(
    ReplyTopLevelDestination(
        route = ReplyRoute.INBOX,
        selectedIcon = Icons.Default.Inbox,
        unselectedIcon = Icons.Default.Inbox,
        iconTextId = R.string.tab_inbox
    ),
    ReplyTopLevelDestination(
        route = ReplyRoute.ARTICLES,
        selectedIcon = Icons.Default.Article,
        unselectedIcon = Icons.Default.Article,
        iconTextId = R.string.tab_article
    ),
    ReplyTopLevelDestination(
        route = ReplyRoute.DM,
        selectedIcon = Icons.Outlined.ChatBubbleOutline,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        iconTextId = R.string.tab_inbox
    ),
    ReplyTopLevelDestination(
        route = ReplyRoute.GROUPS,
        selectedIcon = Icons.Default.People,
        unselectedIcon = Icons.Default.People,
        iconTextId = R.string.tab_article
    )
)