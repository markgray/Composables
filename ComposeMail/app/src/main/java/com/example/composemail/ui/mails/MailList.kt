/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composemail.ui.mails

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.composemail.model.data.MailInfoPeek
import kotlinx.coroutines.flow.Flow

/**
 * The duration of the slide-in/slide-out transition when the mail list is shown after loading.
 */
private const val TRANSITION_DURATION_MS = 600

/**
 * A [LazyColumn] that displays a list of emails.
 *
 * This composable uses [PagingData] to display a list of [MailInfoPeek]s. It animates
 * between a loading state and the list of mails. It also uses a custom [MailListState]
 * to manage the state of individual mail items.
 *
 * We start by initializing our [LazyPagingItems] of [MailInfoPeek] variable `lazyMailItems`
 * to the result of calling the [collectAsLazyPagingItems] extension function on our [Flow] of
 * [PagingData] of [MailInfoPeek] parameter [observableConversations].
 *
 * Our root composable is an [AnimatedContent] whose arguments are:
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `targetState`: is `true` when the list is empty, `false` otherwise.
 *  - `transitionSpec`: is a [AnimatedContentTransitionScope] of [Boolean] lambda in which we call
 *  [slideInVertically] with the `animationSpec` argument a [tween] with a duration of
 *  [TRANSITION_DURATION_MS], and an `initialOffsetY` a lambda that accepts the [Int] passed it
 *  in the variable `fullHeight` and returns it, then we use [togetherWith] to call
 *  [slideOutVertically] with the `animationSpec` argument a [tween] with a duration of
 *  [TRANSITION_DURATION_MS], and an `targetOffsetY` a lambda that accepts the [Int] passed it
 *  in the variable `fullHeight` and returns the negative value of it.
 *  - `label`: is an empty string.
 *
 * In the [AnimatedContentScope] `content` composable lambda argument of the [AnimatedContent]
 * we accept the [Boolean] passed the lambda in variable `isListEmpty` and if it is true we compose
 * a [MailItem] whose arguments are:
 *  - `info`: is `null`
 *  - `state`: is the result of calling the [MailListState.stateFor] function on our [MailListState]
 *  parameter [listState] with an `id` of `null`.
 *  - `onMailOpen`: is our [onMailOpen] lambda parameter.
 *
 * If `isListEmpty` is false, we compose a [LazyColumn] whose arguments are:
 *  - `modifier`: is [Modifier.fillMaxSize()]
 *  - `verticalArrangement`: is [Arrangement.spacedBy]
 *
 * In the [LazyListScope] `content` lambda argument of the [LazyColumn] we call [LazyListScope.items]
 * with its `items` argument our [LazyPagingItems] of [MailInfoPeek] variable `lazyMailItems` and in
 * its [LazyItemScope] `itemContent` composable lambda argument we accept the [MailInfoPeek] passed
 * the lambda in variable `mailInfo` and compose a [MailItem] whose arguments are:
 *  - `info`: is the value of the variable `mailInfo`
 *  - `state`: is the result of calling the [MailListState.stateFor] function on our [MailListState]
 *  parameter [listState] with an `id` of the [MailInfoPeek.id] of `mailInfo`.
 *  - `onMailOpen`: is our [onMailOpen] lambda parameter.
 *
 * @param modifier The [Modifier] to be applied to this Composable.
 * @param listState The state of the mail list, used to manage individual item states.
 * @param observableConversations A [Flow] of [PagingData] of [MailInfoPeek] that contains the mail
 * items to be displayed.
 * @param onMailOpen A lambda to be called with the mail's ID when a mail item is clicked.
 */
@Composable
fun MailList(
    modifier: Modifier = Modifier,
    listState: MailListState,
    observableConversations: Flow<PagingData<MailInfoPeek>>,
    onMailOpen: (id: Int) -> Unit
) {
    // The items provided through the model using Pager through a Flow
    val lazyMailItems: LazyPagingItems<MailInfoPeek> =
        observableConversations.collectAsLazyPagingItems()

    // Since there's currently no good way to have initial placeholders from the PagingItems, we'll
    // just animate between a loading indicator and the LazyColumn
    AnimatedContent(
        modifier = modifier,
        targetState = lazyMailItems.itemCount == 0,
        transitionSpec = {
            // Equal duration for a pushing in/out effect
            slideInVertically(
                animationSpec = tween(durationMillis = TRANSITION_DURATION_MS),
                initialOffsetY = { fullHeight: Int -> fullHeight }
            ) togetherWith slideOutVertically(
                animationSpec = tween(durationMillis = TRANSITION_DURATION_MS),
                targetOffsetY = { fullHeight: Int -> -fullHeight }
            )
        },
        label = ""
    ) { isListEmpty: Boolean ->
        if (isListEmpty) {
            // MailItem with null info will act as a loading indicator
            MailItem(
                info = null,
                state = listState.stateFor(id = null),
                onMailOpen = onMailOpen
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(space = 4.dp)
            ) {
                items(items = lazyMailItems) { mailInfo: MailInfoPeek? ->
                    // The Pager, configured with placeholders may initially provide
                    // a null mailInfo when it reaches the current end of the list,
                    // it will then provide a non-null mailInfo for the same Composable,
                    // MailItem animates the transition from those two values
                    MailItem(
                        info = mailInfo,
                        state = listState.stateFor(mailInfo?.id),
                        onMailOpen = onMailOpen
                    )
                }
            }
        }
    }
}