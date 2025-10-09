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

package com.google.samples.apps.nowinandroid.feature.topic

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.samples.apps.nowinandroid.core.designsystem.component.DynamicAsyncImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaFilterChip
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaLoadingWheel
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.ScrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.DevicePreviews
import com.google.samples.apps.nowinandroid.core.ui.R
import com.google.samples.apps.nowinandroid.core.ui.TrackScreenViewEvent
import com.google.samples.apps.nowinandroid.core.ui.TrackScrollJank
import com.google.samples.apps.nowinandroid.core.ui.UserNewsResourcePreviewParameterProvider
import com.google.samples.apps.nowinandroid.core.ui.userNewsResourceCardItems
import com.google.samples.apps.nowinandroid.feature.topic.R.string
import kotlinx.coroutines.flow.StateFlow

/**
 * Stateful topic screen.
 *
 * We start by initializing our [State] wrapped [TopicUiState] variable `topicUiState` to the value
 * returned by the [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of [TopicUiState]
 * property [TopicViewModel.topicUiState] of our [TopicViewModel] parameter [viewModel]. Then we
 * initialize our [State] wrapped [NewsUiState] variable `newsUiState` to the value returned by the
 * [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of [NewsUiState] property
 * [TopicViewModel.newsUiState] of our [TopicViewModel] parameter [viewModel].
 *
 * Next we call the [TrackScreenViewEvent] method to record a screen view event for the `screenName`
 * argument "Topic: ${viewModel.topicId}". (the [String] "Topic:" concatenated with the value of the
 * [Topic.id] property of the [Topic] we are to display)
 *
 * Finally we compose the stateless [TopicScreen] overload with the following arguments:
 *  - `topicUiState`: is our [State] wrapped [TopicUiState] variable `topicUiState`.
 *  - `newsUiState`: is our [State] wrapped [NewsUiState] variable `newsUiState`.
 *  - `modifier`: is our [Modifier] parameter [modifier] chained to a [Modifier.testTag] whose
 *  `tag` argument is "topic:${viewModel.topicId}".
 *  - `showBackButton`: is our [Boolean] parameter [showBackButton].
 *  - `onBackClick`: is our lambda parameter [onBackClick].
 *  - `onFollowClick`: is a reference to the [TopicViewModel.followTopicToggle] method of our
 *  [TopicViewModel] parameter [viewModel].
 *  - `onBookmarkChanged`: is a reference to the [TopicViewModel.bookmarkNews] method of our
 *  [TopicViewModel] parameter [viewModel].
 *  - `onNewsResourceViewed`: is a lambda that accepts the [String] passed it in variable
 *  `newsResourceId` then calls the [TopicViewModel.setNewsResourceViewed] method of our
 *  [TopicViewModel] parameter [viewModel] with `newsResourceId` as its `newsResourceId` argument
 *  and `true` as its `viewed` argument.
 *  - `onTopicClick`: is our lambda parameter [onTopicClick].
 *
 * @param showBackButton Whether to show the back button.
 * @param onBackClick Called when the back button is clicked.
 * @param onTopicClick Called when a topic is clicked.
 * @param modifier Modifier to apply to the screen.
 * @param viewModel The view model for the screen.
 */
@Composable
fun TopicScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TopicViewModel = hiltViewModel(
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
        null,
    ),
) {
    val topicUiState: TopicUiState by viewModel.topicUiState.collectAsStateWithLifecycle()
    val newsUiState: NewsUiState by viewModel.newsUiState.collectAsStateWithLifecycle()

    TrackScreenViewEvent(screenName = "Topic: ${viewModel.topicId}")
    TopicScreen(
        topicUiState = topicUiState,
        newsUiState = newsUiState,
        modifier = modifier.testTag(tag = "topic:${viewModel.topicId}"),
        showBackButton = showBackButton,
        onBackClick = onBackClick,
        onFollowClick = viewModel::followTopicToggle,
        onBookmarkChanged = viewModel::bookmarkNews,
        onNewsResourceViewed = { newsResourceId: String ->
            viewModel.setNewsResourceViewed(newsResourceId = newsResourceId, viewed = true)
        },
        onTopicClick = onTopicClick,
    )
}

/**
 * Stateless Topic screen.
 *
 * We start by initializing and remembering our [LazyListState] variable `state` to the value returned
 * by the [rememberLazyListState] method. Then we call the [TrackScrollJank] method to track the jank
 * while scrolling the [ScrollableState] of `state` using the `stateName` argument "topic:screen".
 *
 * Our root composable is [Box] whose `modifier` argument is our [Modifier] parameter [modifier]. In
 * the [BoxScope] `content` composable lambda argument we first compose a [LazyColumn] whose `state`
 * argument is our [LazyListState] variable `state`, and whose `horizontalAlignment` argument is
 * [Alignment.CenterHorizontally]. In the [LazyListScope] `content` lambda argument we first compose:
 *
 * An [LazyListScope.item] whose `content` lambda argument is a [Spacer] whose `modifier` argument
 * is a [Modifier.windowInsetsTopHeight] whose `insets` argument is [WindowInsets.Companion.safeDrawing].
 * The we branch on the type of [TopicUiState] parameter [topicUiState]:
 *  - If [TopicUiState.Loading] we compose a [LazyListScope.item] holding a [NiaLoadingWheel] whose
 *  `modifier` argument is our [Modifier] parameter [modifier] and whose `contentDesc` argument is
 *  the [String] resource with id `string.feature_topic_loading` ("Loading topic").
 *  - If [TopicUiState.Error] we have not decided what to do here.
 *  - If [TopicUiState.Success] we compose a [LazyListScope.item] holding a [TopicToolbar] whose
 *  `showBackButton` argument is our [Boolean] parameter [showBackButton], whose `onBackClick`
 *  argument is our lambda parameter [onBackClick], whose `onFollowClick` is our lambda parameter
 *  [onFollowClick], and whose `uiState` argument is the [TopicUiState.Success.followableTopic]
 *  property of our [TopicUiState] parameter [topicUiState]. Then we compose a [LazyListScope.topicBody]
 *  whose `name` argument is the [Topic.name] property of the [TopicUiState.Success.followableTopic]
 *  property of our [TopicUiState] parameter [topicUiState], whose `description` argument is the
 *  [Topic.longDescription] property of the [TopicUiState.Success.followableTopic] property of our
 *  [TopicUiState] parameter [topicUiState], whose `news` argument is our [NewsUiState] parameter
 *  [newsUiState], whose `imageUrl` argument is the [Topic.imageUrl] property of the
 *  [TopicUiState.Success.followableTopic] property of our [TopicUiState] parameter [topicUiState],
 *  whose `onBookmarkChanged` argument is our lambda parameter [onBookmarkChanged], whose
 *  `onNewsResourceViewed` argument is our lambda parameter [onNewsResourceViewed], and whose
 *  `onTopicClick` argument is our lambda parameter [onTopicClick].
 *
 * At the bottom of the [LazyColumn] we compose a [LazyListScope.item] whose `content` lambda
 * argument is a [Spacer] whose `modifier` argument is a [Modifier.windowInsetsBottomHeight] whose
 * `insets` argument is [WindowInsets.Companion.safeDrawing].
 *
 * Next in the [BoxScope] `content` lambda argument we initialize our [Int] variable `itemsAvailable`
 * to the value returned by the [topicItemsSize] method when called with its `topicUiState` our
 * [TopicUiState] parameter [topicUiState] and its `newsUiState` our [NewsUiState] parameter. Then
 * we initialize our [ScrollbarState] variable `scrollbarState` to the value returned by the
 * [LazyListState.scrollbarState] method of our [LazyListState] variable `state` when called with
 * its `itemsAvailable` argument our [Int] variable `itemsAvailable`. Finally we compose a
 * [DraggableScrollbar] using `state` as its [ScrollableState] receiver with the arguments:
 *  - `modifier`: is a [Modifier.fillMaxHeight] chained to a [Modifier.windowInsetsPadding] whose
 *  `insets` argument is [WindowInsets.Companion.systemBars] chained to a [Modifier.padding] that
 *  adds `2.dp` to the `horizontal` sides, chained to a [BoxScope.align] whose `alignment` argument
 *  is [Alignment.CenterEnd].
 *  - `state`: is our [ScrollbarState] variable `scrollbarState`.
 *  - `orientation`: is [Orientation.Vertical].
 *  - `onThumbMoved`: is the lambda returned by the [LazyListState.rememberDraggableScroller] method
 *  of our [LazyListState] variable `state` when called with its `itemsAvailable` argument our
 *  [Int] variable `itemsAvailable`.
 *
 * @param topicUiState The UI state for the topic.
 * @param newsUiState The UI state for the news.
 * @param showBackButton Whether to show the back button.
 * @param onBackClick Called when the back button is clicked.
 * @param onFollowClick Called when the follow button is clicked.
 * @param onTopicClick Called when a topic is clicked.
 * @param onBookmarkChanged Called when the bookmark button is clicked.
 * @param onNewsResourceViewed Called when a news resource is viewed.
 * @param modifier Modifier to apply to the screen.
 */
@VisibleForTesting
@Composable
internal fun TopicScreen(
    topicUiState: TopicUiState,
    newsUiState: NewsUiState,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onFollowClick: (Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    onBookmarkChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state: LazyListState = rememberLazyListState()
    TrackScrollJank(scrollableState = state, stateName = "topic:screen")
    Box(
        modifier = modifier,
    ) {
        LazyColumn(
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Spacer(modifier = Modifier.windowInsetsTopHeight(insets = WindowInsets.safeDrawing))
            }
            when (topicUiState) {
                TopicUiState.Loading -> item {
                    NiaLoadingWheel(
                        modifier = modifier,
                        contentDesc = stringResource(id = string.feature_topic_loading),
                    )
                }

                TopicUiState.Error -> TODO()
                is TopicUiState.Success -> {
                    item {
                        TopicToolbar(
                            showBackButton = showBackButton,
                            onBackClick = onBackClick,
                            onFollowClick = onFollowClick,
                            uiState = topicUiState.followableTopic,
                        )
                    }
                    topicBody(
                        name = topicUiState.followableTopic.topic.name,
                        description = topicUiState.followableTopic.topic.longDescription,
                        news = newsUiState,
                        imageUrl = topicUiState.followableTopic.topic.imageUrl,
                        onBookmarkChanged = onBookmarkChanged,
                        onNewsResourceViewed = onNewsResourceViewed,
                        onTopicClick = onTopicClick,
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(insets = WindowInsets.safeDrawing))
            }
        }
        val itemsAvailable: Int = topicItemsSize(
            topicUiState = topicUiState,
            newsUiState = newsUiState,
        )
        val scrollbarState: ScrollbarState = state.scrollbarState(
            itemsAvailable = itemsAvailable,
        )
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(insets = WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(alignment = Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
}

/**
 * Returns the number of items in the topic screen for the given [TopicUiState] and [NewsUiState].
 * We branch on the type of [TopicUiState] parameter [topicUiState]:
 *  - [TopicUiState.Error] we return `0` (nothing to display).
 *  - [TopicUiState.Loading] we return `1` (a loading bar).
 *  - [TopicUiState.Success] we branch on the type of [NewsUiState] parameter [newsUiState]:
 *      - [NewsUiState.Error] we return `0` (nothing to display).
 *      - [NewsUiState.Loading] we return `1` (a loading bar).
 *      - [NewsUiState.Success] we return `2 + newsUiState.news.size` (the toolbar, header, and news).
 *
 * @param topicUiState The [TopicUiState] for the topic.
 * @param newsUiState The [NewsUiState] for the news.
 */
private fun topicItemsSize(
    topicUiState: TopicUiState,
    newsUiState: NewsUiState,
) = when (topicUiState) {
    TopicUiState.Error -> 0 // Nothing
    TopicUiState.Loading -> 1 // Loading bar
    is TopicUiState.Success -> when (newsUiState) {
        NewsUiState.Error -> 0 // Nothing
        NewsUiState.Loading -> 1 // Loading bar
        is NewsUiState.Success -> 2 + newsUiState.news.size // Toolbar, header
    }
}

/**
 * Adds the body of the topic screen to the [LazyListScope] of the calling [LazyColumn].
 *
 * It starts with a [LazyListScope.item] whose `content` lambda argument is a [TopicHeader] whose
 * `name` argument is our [String] parameter [name], whose `description` argument is our [String]
 * parameter [description], and whose `imageUrl` argument is our [String] parameter [imageUrl].
 *
 * Then it calls the [LazyListScope.userNewsResourceCards] method with its `news` argument our
 * [NewsUiState] parameter [news], its `onBookmarkChanged` argument our lambda parameter
 * [onBookmarkChanged], its `onNewsResourceViewed` argument our lambda parameter
 * [onNewsResourceViewed], and its `onTopicClick` argument our lambda parameter [onTopicClick].
 *
 * @param name the name of the [Topic] we are displaying.
 * @param description the description of the [Topic] we are displaying.
 * @param news the [NewsUiState] of the news associated with the topic we are displaying.
 * @param imageUrl the URL of the image associated with the [Topic] we are displaying.
 * @param onBookmarkChanged a lambda which is called with the ID of the news resource whose bookmark
 * has been changed, and a [Boolean] indicating whether it is now bookmarked or not.
 * @param onNewsResourceViewed a lambda which is called with the ID of the news resource that has
 * been viewed.
 * @param onTopicClick a lambda which is called with the ID of the topic that has been clicked.
 */
private fun LazyListScope.topicBody(
    name: String,
    description: String,
    news: NewsUiState,
    imageUrl: String,
    onBookmarkChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
) {
    // TODO: Show icon if available
    item {
        TopicHeader(name = name, description = description, imageUrl = imageUrl)
    }

    userNewsResourceCards(
        news = news,
        onBookmarkChanged = onBookmarkChanged,
        onNewsResourceViewed = onNewsResourceViewed,
        onTopicClick = onTopicClick,
    )
}

/**
 * Displays the header for a topic, which consists of the topic image, name and description.
 *
 * This Composable uses a [Column] whose `modifier` argument is a [Modifier.padding] that adds `24.dp`
 * to the `horizontal` sides. Its [ColumnScope] `content` composable lambda argument consists of:
 *  - A [DynamicAsyncImage] whose `imageUrl` argument is our [String] parameter [imageUrl], whose
 *  `contentDescription` is `null`, and whose `modifier` argument is a [ColumnScope.align] whose
 *  `alignment` argument is [Alignment.CenterHorizontally] (to center the image in the [Column])
 *  chained to a [Modifier.size] that sets its `size` to `132.dp`, chained to a [Modifier.padding]
 *  that sets its `bottom` padding to `12.dp`.
 *  - A [Text] displaying our [String] parameter [name] using the `style` [Typography.displayMedium]
 *  of our custom [MaterialTheme.typography].
 *  - If our [String] parameter [description] is not empty we compose a [Text] displaying our
 *  [String] parameter [description] using the `style` [Typography.bodyLarge] of our custom
 *  [MaterialTheme.typography], with its `modifier` argument a [Modifier.padding] that adds `24.dp`
 *  to its `top`.
 *
 * @param name The name of the topic.
 * @param description The description of the topic.
 * @param imageUrl The URL of the topic image.
 */
@Composable
private fun TopicHeader(name: String, description: String, imageUrl: String) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
    ) {
        DynamicAsyncImage(
            imageUrl = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .size(size = 132.dp)
                .padding(bottom = 12.dp),
        )
        Text(text = name, style = MaterialTheme.typography.displayMedium)
        if (description.isNotEmpty()) {
            Text(
                text = description,
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

// TODO: Could/should this be replaced with [LazyGridScope.newsFeed]?
/**
 * Displays the news resources associated with a topic, either as a list of cards, a loading wheel,
 * or an error message.
 *
 * This function branches on the type of its [NewsUiState] parameter [news]:
 *  - [NewsUiState.Success]: it calls the [LazyListScope.userNewsResourceCardItems] method to display
 *  the [List] of [UserNewsResource] objects in its [NewsUiState.Success.news] property as a series
 *  of news resource cards. The `onToggleBookmark` argument is a lambda which calls our lambda
 *  parameter `onBookmarkChanged` with the [UserNewsResource.id] of the [UserNewsResource] whose
 *  bookmark is being toggled and the inverse of its [UserNewsResource.isSaved] property (current
 *  bookmarked state). The `onNewsResourceViewed` argument is our lambda parameter
 *  `onNewsResourceViewed`, the `onTopicClick` argument is our lambda parameter `onTopicClick`, and
 *  the `itemModifier` argument is a [Modifier.padding] that adds `24.dp` to `all` sides of the card.
 *  - [NewsUiState.Loading]: it composes a [LazyListScope.item] which holds a [NiaLoadingWheel] whose
 *  `contentDesc` argument is the [String] "Loading news".
 *  - else branch (ie. [NewsUiState.Error]): it composes a [LazyListScope.item] which holds a [Text]
 *  displaying the [String] "Error".
 *
 * @param news The [NewsUiState] for the news to display.
 * @param onBookmarkChanged Called when the bookmark button for a news resource is clicked.
 * @param onNewsResourceViewed Called when a news resource is viewed.
 * @param onTopicClick Called when a topic is clicked.
 */
private fun LazyListScope.userNewsResourceCards(
    news: NewsUiState,
    onBookmarkChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
) {
    when (news) {
        is NewsUiState.Success -> {
            userNewsResourceCardItems(
                items = news.news,
                onToggleBookmark = { newsResource: UserNewsResource ->
                    onBookmarkChanged(newsResource.id, !newsResource.isSaved)
                },
                onNewsResourceViewed = onNewsResourceViewed,
                onTopicClick = onTopicClick,
                itemModifier = Modifier.padding(all = 24.dp),
            )
        }

        is NewsUiState.Loading -> item {
            NiaLoadingWheel(contentDesc = "Loading news") // TODO
        }

        else -> item {
            Text(text = "Error") // TODO
        }
    }
}

/**
 * This is a Preview of the [LazyListScope.topicBody] method which displays the header for a
 * topic, which consists of the topic image, name and description and the news resources
 * associated with a topic.
 *
 * It is wrapped in our [NiaTheme] custom [MaterialTheme]. Its root composable is a [LazyColumn]
 * which calls the [LazyListScope.topicBody] method with the arguments:
 *  - `name`: the [String] "Jetpack Compose"
 *  - `description`: the [String] "Lorem ipsum maximum"
 *  - `news`: a [NewsUiState.Success] whose `news` argument is an empty list.
 *  - `imageUrl`: an empty [String].
 *  - `onBookmarkChanged`: an empty lambda.
 *  - `onNewsResourceViewed`: an empty lambda.
 *  - `onTopicClick`: an empty lambda.
 */
@Preview
@Composable
private fun TopicBodyPreview() {
    NiaTheme {
        LazyColumn {
            topicBody(
                name = "Jetpack Compose",
                description = "Lorem ipsum maximum",
                news = NewsUiState.Success(news = emptyList()),
                imageUrl = "",
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

/**
 * This Composable displays the toolbar for the Topic screen. The toolbar consists of a "Back" button
 * which is only displayed if its [Boolean] parameter [showBackButton] is `true` (if it is `false` a
 * `1.dp` wide [Spacer] is displayed instead to keep the [NiaFilterChip] aligned to the end of the
 * [Row]), and a [NiaFilterChip] which displays either the [String] "FOLLOWING" or "NOT FOLLOWING"
 * depending on whether the [FollowableTopic.isFollowed] property of our [FollowableTopic] parameter
 * [uiState] is `true` or `false`. When the [NiaFilterChip] is clicked our lambda parameter
 * [onFollowClick] is called with the new `selected` state of the chip. When the "Back" button is
 * clicked our lambda parameter [onBackClick] is called.
 *
 * Our root Composable is a [Row] whose `horizontalArrangement` argument is
 * [Arrangement.SpaceBetween] (space is placed between children), whose `verticalAlignment` argument
 * is [Alignment.CenterVertically] (its children are centered vertically), and whose `modifier`
 * argument is our [Modifier] parameter [modifier] chained to a [Modifier.fillMaxWidth] (the [Row]
 * will occupy its entire incoming width constraint), chained to a [Modifier.padding] that adds
 * `32.dp` to its `bottom`.
 *
 * In the [RowScope] `content` composable lambda argument of the [Row] **if** [showBackButton] is
 * `true` we compose an [IconButton] whose `onClick` argument is a lambda that calls our lambda
 * parameter [onBackClick]. The `content` of the [IconButton] is an [Icon] whose `imageVector` is
 * [NiaIcons.ArrowBack] and whose `contentDescription` is the [String] whose resource id is
 * `R.string.core_ui_back` ("Back").
 *
 * If [showBackButton] is `false` we compose a [Spacer] whose `modifier` argument is a
 * [Modifier.width] that sets its `width` to `1.dp`. (This keeps the [NiaFilterChip] aligned to the
 * end of the [Row].)
 *
 * Next we initialize our [Boolean] variable `selected` to the value of the [FollowableTopic.isFollowed]
 * property of our [FollowableTopic] parameter [uiState]. Then we compose a [NiaFilterChip] whose
 * `selected` argument is our [Boolean] variable `selected`, whose `onSelectedChange` argument is
 * our lambda parameter [onFollowClick], and whose `modifier` argument is a [Modifier.padding]
 * that adds `24.dp` to its `end`. If `selected` is `true` we compose a [Text] displaying the
 * [String] "FOLLOWING". If `selected` is `false` we compose a [Text] displaying the [String]
 * "NOT FOLLOWING".
 *
 * @param uiState The [FollowableTopic] to display.
 * @param modifier The [Modifier] to apply to the Composable.
 * @param showBackButton Whether to show the back button.
 * @param onBackClick Called when the back button is clicked.
 * @param onFollowClick Called when the follow button is clicked.
 */
@Composable
private fun TopicToolbar(
    uiState: FollowableTopic,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    onFollowClick: (Boolean) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
    ) {
        if (showBackButton) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = NiaIcons.ArrowBack,
                    contentDescription = stringResource(
                        id = R.string.core_ui_back,
                    ),
                )
            }
        } else {
            // Keeps the NiaFilterChip aligned to the end of the Row.
            Spacer(modifier = Modifier.width(width = 1.dp))
        }
        val selected: Boolean = uiState.isFollowed
        NiaFilterChip(
            selected = selected,
            onSelectedChange = onFollowClick,
            modifier = Modifier.padding(end = 24.dp),
        ) {
            if (selected) {
                Text(text = "FOLLOWING")
            } else {
                Text(text = "NOT FOLLOWING")
            }
        }
    }
}

/**
 * Preview of the [TopicScreen] when the topic is populated. We are wrapped in our [NiaTheme] custom
 * [MaterialTheme], and we are then wrapped in our [NiaBackground] custom background Composable.
 * We call our [TopicScreen] Composable with its `topicUiState` argument a [TopicUiState.Success]
 * whose `followableTopic` is the first [FollowableTopic] in the [List] of [FollowableTopic] in the
 * `followableTopics` field of the first [UserNewsResource] in our [List] of [UserNewsResource]
 * parameter [userNewsResources]. Its `newsUiState` argument is a [NewsUiState.Success] whose `news`
 * argument is our [List] of [UserNewsResource] parameter [userNewsResources]. Its `showBackButton`
 * argument is `true`, and all of its lambda arguments are empty lambdas.
 *
 * @param userNewsResources the [List] of [UserNewsResource] to use to populate the [TopicScreen].
 * This is provided by our [UserNewsResourcePreviewParameterProvider] custom
 * [PreviewParameterProvider].
 */
@DevicePreviews
@Composable
fun TopicScreenPopulated(
    @PreviewParameter(provider = UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        NiaBackground {
            TopicScreen(
                topicUiState = TopicUiState.Success(
                    followableTopic = userNewsResources[0].followableTopics[0],
                ),
                newsUiState = NewsUiState.Success(news = userNewsResources),
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

/**
 * This is a Preview of the [TopicScreen] when the topic is loading. We are wrapped in our
 * [NiaTheme] custom [MaterialTheme], and we are then wrapped in our [NiaBackground] custom
 * background Composable. We call our [TopicScreen] Composable with its `topicUiState` argument
 * a [TopicUiState.Loading], its `newsUiState` argument is a [NewsUiState.Loading], its
 * `showBackButton` argument is `true`, and all of its lambda arguments are empty lambdas.
 */
@DevicePreviews
@Composable
fun TopicScreenLoading() {
    NiaTheme {
        NiaBackground {
            TopicScreen(
                topicUiState = TopicUiState.Loading,
                newsUiState = NewsUiState.Loading,
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}
