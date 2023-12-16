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
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.components.ReplyEmailListItem
import com.example.reply.ui.components.ReplyEmailThreadItem
import kotlinx.coroutines.flow.MutableStateFlow

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
 *
 */
@Composable
fun ReplyAppContent(
    modifier: Modifier = Modifier,
    replyHomeUIState: ReplyHomeUIState,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit,
) {

    val selectedDestination: MutableState<String> = remember { mutableStateOf(ReplyRoute.INBOX) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        if (selectedDestination.value == ReplyRoute.INBOX) {
            ReplyInboxScreen(
                replyHomeUIState = replyHomeUIState,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                modifier = Modifier.weight(1f)
            )
        } else {
            EmptyComingSoon(modifier = Modifier.weight(weight = 1f))
        }

        NavigationBar(modifier = Modifier.fillMaxWidth()) {
            TOP_LEVEL_DESTINATIONS.forEach { replyDestination ->
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
 *
 */
object ReplyRoute {
    /**
     *
     */
    const val INBOX: String = "Inbox"

    /**
     *
     */
    const val ARTICLES: String = "Articles"

    /**
     *
     */
    const val DM: String = "DirectMessages"

    /**
     *
     */
    const val GROUPS: String = "Groups"
}

/**
 *
 */
data class ReplyTopLevelDestination(
    /**
     *
     */
    val route: String,
    /**
     *
     */
    val selectedIcon: ImageVector,
    /**
     *
     */
    val unselectedIcon: ImageVector,
    /**
     *
     */
    val iconTextId: Int
)

/**
 *
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