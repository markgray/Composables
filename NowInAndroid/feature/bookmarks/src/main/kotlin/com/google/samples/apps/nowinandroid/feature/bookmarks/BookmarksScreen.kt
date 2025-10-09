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

package com.google.samples.apps.nowinandroid.feature.bookmarks

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaLoadingWheel
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.ScrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LocalTintTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.TintTheme
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState.Loading
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState.Success
import com.google.samples.apps.nowinandroid.core.ui.TrackScreenViewEvent
import com.google.samples.apps.nowinandroid.core.ui.TrackScrollJank
import com.google.samples.apps.nowinandroid.core.ui.UserNewsResourcePreviewParameterProvider
import com.google.samples.apps.nowinandroid.core.ui.newsFeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

/**
 * Displays the Bookmark screen.
 *
 * We start by initializing our [State] wrapped [NewsFeedUiState] variable `feedState` using the
 * [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of [NewsFeedUiState] property
 * [BookmarksViewModel.feedUiState] of our [BookmarksViewModel] parameter [viewModel]. Then we
 * compose a [BookmarksScreen] whose arguments are:
 *  - `feedState`: our [State] wrapped [NewsFeedUiState] variable `feedState`.
 *  - `onShowSnackbar`: our [onShowSnackbar] suspend lambda parameter.
 *  - `removeFromBookmarks`: a reference to the [BookmarksViewModel.removeFromSavedResources] method
 *  of our [BookmarksViewModel] parameter [viewModel].
 *  - `onNewsResourceViewed`: a lambda which accepts the [String] passed it in variable `newsResourceId`
 *  then calls the [BookmarksViewModel.setNewsResourceViewed] method of our [BookmarksViewModel]
 *  parameter [viewModel] with its `newsResourceId` argument the [String] variable `newsResourceId`
 *  and `viewed` argument `true`.
 *  - `onTopicClick`: our lambda parameter [onTopicClick].
 *  - `modifier`: our [Modifier] parameter [modifier].
 *  - `shouldDisplayUndoBookmark`: the [BookmarksViewModel.shouldDisplayUndoBookmark] property of
 *  our [BookmarksViewModel] parameter [viewModel].
 *  - `undoBookmarkRemoval`: a reference to the [BookmarksViewModel.undoBookmarkRemoval] method
 *  of our [BookmarksViewModel] parameter [viewModel].
 *  - `clearUndoState`: a reference to the [BookmarksViewModel.clearUndoState] method of our
 *  [BookmarksViewModel] parameter [viewModel].
 *
 * @param onTopicClick The callback to be invoked when a topic is clicked.
 * @param onShowSnackbar The callback to be invoked when a snackbar is to be shown.
 * @param modifier The modifier to be applied to the screen.
 * @param viewModel The view model to be used by the screen.
 */
@Composable
internal fun BookmarksRoute(
    onTopicClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel(
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
        null,
    ),
) {
    val feedState: NewsFeedUiState by viewModel.feedUiState.collectAsStateWithLifecycle()
    BookmarksScreen(
        feedState = feedState,
        onShowSnackbar = onShowSnackbar,
        removeFromBookmarks = viewModel::removeFromSavedResources,
        onNewsResourceViewed = { newsResourceId: String ->
            viewModel.setNewsResourceViewed(
                newsResourceId = newsResourceId,
                viewed = true,
            )
        },
        onTopicClick = onTopicClick,
        modifier = modifier,
        shouldDisplayUndoBookmark = viewModel.shouldDisplayUndoBookmark,
        undoBookmarkRemoval = viewModel::undoBookmarkRemoval,
        clearUndoState = viewModel::clearUndoState,
    )
}

/**
 * Displays the user's bookmarked articles.
 *
 * This composable uses a [LaunchedEffect] to show a snackbar when a bookmark is removed. It also
 * uses a [LifecycleEventEffect] to clear the undo state when the composable is stopped.
 *
 * The composable displays a loading state if [feedState] is [Loading]. If [feedState] is
 * [Success] and the feed is not empty, it displays a [BookmarksGrid]. Otherwise, it displays an
 * [EmptyState].
 *
 * We start by initializing our [String] variable `bookmarkRemovedMessage` to the [String]
 * "Bookmark removed", and initializing our [String] variable `undoText` to the [String] "UNDO".
 * Then we compose a [LaunchedEffect] with our [Boolean] parameter [shouldDisplayUndoBookmark]
 * as its `key1`. In its [CoroutineScope] `block` lambda argument if [shouldDisplayUndoBookmark] is
 * `true` we initialize our [Boolean] variable `snackBarResult` to the result of calling the
 * [onShowSnackbar] suspend lambda with the [String] variable `bookmarkRemovedMessage` and the
 * [String] variable `undoText` as its `message` and `actionLabel` arguments respectively. If
 * `snackBarResult` is `true` we call our lambda parameter [undoBookmarkRemoval], and if it is
 * `false` we call our lambda parameter [clearUndoState].
 *
 * Next we call the [LifecycleEventEffect] method with [Lifecycle.Event.ON_STOP] as its `event`
 * argument and in its `onEvent` lambda argument we call our lambda parameter [clearUndoState]
 * (this will run when the composable is stopped).
 *
 * Next we branch on the value of [feedState]:
 *  - If [feedState] is [Loading] we compose a [LoadingState] with our [Modifier] parameter [modifier]
 *  as its `modifier` argument.
 *  - If [feedState] is [Success] and the `feed` is not empty we compose a [BookmarksGrid] with our
 *  [NewsFeedUiState] parameter [feedState] as its `feedState` argument, our lambda parameter
 *  [removeFromBookmarks] as its `removeFromBookmarks` argument, our lambda parameter
 *  [onNewsResourceViewed] as its `onNewsResourceViewed` argument, our lambda parameter
 *  [onTopicClick] as its `onTopicClick` argument, and our [Modifier] parameter [modifier] as its
 *  `modifier` argument.
 *  - If [feedState] is [Success] and the feed is empty we compose an [EmptyState] with our
 *  [Modifier] parameter [modifier] as its `modifier` argument.
 *
 * Finally we call the [TrackScreenViewEvent] method with the [String] "Saved" as its `screenName`
 * argument.
 *
 * @param feedState The state of the news feed.
 * @param onShowSnackbar The callback to be invoked when a snackbar is to be shown.
 * @param removeFromBookmarks The callback to be invoked when a bookmark is removed.
 * @param onNewsResourceViewed The callback to be invoked when a news resource is viewed.
 * @param onTopicClick The callback to be invoked when a topic is clicked.
 * @param modifier The modifier to be applied to the screen.
 * @param shouldDisplayUndoBookmark Whether to display the undo bookmark snackbar.
 * @param undoBookmarkRemoval The callback to be invoked when a bookmark removal is undone.
 * @param clearUndoState The callback to be invoked when the undo state is cleared.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
@Composable
internal fun BookmarksScreen(
    feedState: NewsFeedUiState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    removeFromBookmarks: (String) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    shouldDisplayUndoBookmark: Boolean = false,
    undoBookmarkRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    val bookmarkRemovedMessage: String = stringResource(id = R.string.feature_bookmarks_removed)
    val undoText: String = stringResource(id = R.string.feature_bookmarks_undo)

    LaunchedEffect(key1 = shouldDisplayUndoBookmark) {
        if (shouldDisplayUndoBookmark) {
            val snackBarResult: Boolean = onShowSnackbar(bookmarkRemovedMessage, undoText)
            if (snackBarResult) {
                undoBookmarkRemoval()
            } else {
                clearUndoState()
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP) {
        clearUndoState()
    }

    when (feedState) {
        Loading -> LoadingState(modifier = modifier)
        is Success -> if (feedState.feed.isNotEmpty()) {
            BookmarksGrid(
                feedState = feedState,
                removeFromBookmarks = removeFromBookmarks,
                onNewsResourceViewed = onNewsResourceViewed,
                onTopicClick = onTopicClick,
                modifier = modifier,
            )
        } else {
            EmptyState(modifier = modifier)
        }
    }

    TrackScreenViewEvent(screenName = "Saved")
}

/**
 * Displays a loading wheel in the center of the screen when the user's bookmarked articles are
 * loading.
 *
 * Our root composable is a [NiaLoadingWheel] whose arguments are:
 *  - `modifier`: our [Modifier] parameter [modifier] chained to a [Modifier.fillMaxWidth], chained
 *  to a [Modifier.wrapContentSize], chained to a [Modifier.testTag] whose `tag` argument is the
 *  [String] "forYou:Loading"
 *  - `contentDesc`: the [String] with resourse ID `R.string.feature_bookmarks_loading`
 *  ("Loading saved…")
 *
 * @param modifier The modifier to be applied to the loading wheel.
 */
@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    NiaLoadingWheel(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize()
            .testTag(tag = "forYou:loading"),
        contentDesc = stringResource(id = R.string.feature_bookmarks_loading),
    )
}

/**
 * An adaptive grid of news resources. Displays a [LazyVerticalStaggeredGrid] of news resources
 * when the screen is not empty.
 *
 * We start by initializing our [LazyStaggeredGridState] variable `val scrollableState` to the value
 * returned by [rememberLazyStaggeredGridState]. Then we call [TrackScrollJank] with `scrollableState`
 * as its `scrollableState` argument and "bookmarks:grid" as its `stateName`.
 *
 * The root Composable is a [Box] whose `modifier` argument is our [Modifier] parameter [modifier]
 * with a [Modifier.fillMaxSize] chained to it. The [BoxScope] `content` lambda argument of the [Box]
 * contains a [LazyVerticalStaggeredGrid] and a [DraggableScrollbar]. The arguments to the
 * [LazyVerticalStaggeredGrid] are:
 *  - `columns`: a [StaggeredGridCells.Adaptive] whose `minSize` is 300.dp (The grid will have as
 *  many columns as possible such that each column has at least 300.dp of width).
 *  - `contentPadding`: a [PaddingValues] whose `all` sides are 16.dp
 *  - `horizontalArrangement`: an [Arrangement.spacedBy] whose `space` is 16.dp (Place children such
 *  that they are spaced by 16.dp apart along the main axis).
 *  - `verticalItemSpacing`: 24.dp (The vertical spacing between items of the grid).
 *  - `state`: our [LazyStaggeredGridState] variable `scrollableState`.
 *  - `modifier`: a [Modifier.fillMaxSize] chained to a [Modifier.testTag] whose `tag` is
 *  "bookmarks:feed".
 *
 * In the [LazyStaggeredGridScope] `content` lambda argument we compose a [newsFeed] with the
 * arguments:
 *  - `feedState`: our [NewsFeedUiState] parameter [feedState].
 *  - `onNewsResourcesCheckedChanged`: a lambda which accepts a [String] in variable `id` and calls
 *  our lambda parameter [removeFromBookmarks] with `id`.
 *  - `onNewsResourceViewed`: our lambda parameter [onNewsResourceViewed].
 *  - `onTopicClick`: our lambda parameter [onTopicClick].
 *
 * Then we compose a [LazyStaggeredGridScope.item] whose `span` is [StaggeredGridItemSpan.FullLine]
 * and in its `content` lambda argument we compose a [Spacer] with a [Modifier.windowInsetsBottomHeight]
 * whose `insets` argument is [WindowInsets.Companion.safeDrawing].
 *
 * Next we work on creating a [DraggableScrollbar] to occupy the [Box] alongside the
 * [LazyVerticalStaggeredGrid]. We start by initializing our [Int] variable `itemsAvailable` to `1`
 * if [feedState] is [Loading] or to the [List.size] of the [List] of [UserNewsResource] property
 * [Success.feed] of [feedState] if it is a [Success]. Then we initialize our [ScrollbarState]
 * variable `val scrollbarState` to the value returned by the [LazyStaggeredGridState.scrollbarState]
 * method of our [LazyStaggeredGridState] variable `scrollableState` with its `itemsAvailable`
 * argument set to `itemsAvailable`. Finally we call the [ScrollableState.DraggableScrollbar]
 * extension function of our [ScrollbarState] variable `scrollbarState` with the arguments:
 *  - `modifier`: a [Modifier.fillMaxHeight] chained to a [Modifier.windowInsetsPadding] whose
 *  `insets` argument is [WindowInsets.Companion.systemBars], chained to a [Modifier.padding] whose
 *  `horizontal` argument is 2.dp, chained to a [BoxScope.align] whose `alignment` argument is
 *  [Alignment.CenterEnd].
 *  - `state`: our [ScrollbarState] variable `scrollbarState`.
 *  - `orientation`: [Orientation.Vertical] (The scrollbar will be vertical).
 *  - `onThumbMoved`: calls the [LazyStaggeredGridState.rememberDraggableScroller] method of our
 *  [LazyStaggeredGridState] variable `scrollableState` with its `itemsAvailable` argument our [Int]
 *  variable `itemsAvailable`.
 *
 * @param feedState The state of the news feed.
 * @param removeFromBookmarks The callback to be invoked when a bookmark is removed.
 * @param onNewsResourceViewed The callback to be invoked when a news resource is viewed.
 * @param onTopicClick The callback to be invoked when a topic is clicked.
 * @param modifier The modifier to be applied to the screen.
 */
@Composable
private fun BookmarksGrid(
    feedState: NewsFeedUiState,
    removeFromBookmarks: (String) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollableState: LazyStaggeredGridState = rememberLazyStaggeredGridState()
    TrackScrollJank(scrollableState = scrollableState, stateName = "bookmarks:grid")
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(all = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
            verticalItemSpacing = 24.dp,
            state = scrollableState,
            modifier = Modifier
                .fillMaxSize()
                .testTag(tag = "bookmarks:feed"),
        ) {
            newsFeed(
                feedState = feedState,
                onNewsResourcesCheckedChanged = { id: String, _ -> removeFromBookmarks(id) },
                onNewsResourceViewed = onNewsResourceViewed,
                onTopicClick = onTopicClick,
            )
            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(insets = WindowInsets.safeDrawing))
            }
        }
        val itemsAvailable: Int = when (feedState) {
            Loading -> 1
            is Success -> feedState.feed.size
        }
        val scrollbarState: ScrollbarState = scrollableState.scrollbarState(
            itemsAvailable = itemsAvailable,
        )
        scrollableState.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(insets = WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(alignment = Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = scrollableState.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
}

/**
 * An empty state for the bookmarks screen.
 *
 * This composable displays an image, a title, and a description to inform the user that they have
 * no bookmarks. It also provides a test tag for UI testing.
 *
 * Our root composable is a [Column] whose arguments are:
 *  - `modifier`: our [Modifier] parameter [modifier] chained to a [Modifier.padding] whose `all`
 *  argument is 16.dp, chained to a [Modifier.fillMaxSize], chained to a [Modifier.testTag] whose
 *  `tag` argument is "bookmarks:empty".
 *  - `verticalArrangement`: an [Arrangement.Center] (The children are placed vertically in the center
 *  of the screen).
 *  - `horizontalAlignment`: an [Alignment.CenterHorizontally] (The children are placed horizontally
 *  in the center of the screen).
 *
 * In the [ColumnScope] `content` composable lambda argument we initialize our [Color] variable
 * `iconTint` to the current [TintTheme.iconTint] property of our [LocalTintTheme] composition local,
 * then we compose an [Image] whose arguments are:
 *  - `modifier`: a [Modifier.fillMaxWidth].
 *  - `painter`: is the [Painter] returned by [painterResource] for the resource `id` argument
 *  `R.drawable.feature_bookmarks_img_empty_bookmarks`
 *  - `colorFilter`: a [ColorFilter.tint] whose `color` argument is our [Color] variable `iconTint`
 *  if it is not [Color.Unspecified], otherwise it is `null`.
 *  - `contentDescription`: is `null`.
 *
 * Next in the [ColumnScope] `content` composable lambda argument we compose a [Spacer] whose
 * `modifier` argument is a [Modifier.height] whose `height` argument is 48.dp.
 *
 * Then we compose a [Text] whose arguments are:
 *  - `text`: the [String] with resourse ID `R.string.feature_bookmarks_empty_error` ("No saved updates")
 *  - `modifier`: is a [Modifier.fillMaxWidth].
 *  - `textAlign`: [TextAlign.Center] (The text will be aligned to the center of the screen).
 *  - `style`: the [Typography.titleMedium] of our custom [MaterialTheme.typography].
 *  - `fontWeight`: [FontWeight.Bold] (The text will be bold).
 *
 * Next we compose a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height`
 * argument is 8.dp.
 *
 * Finally we compose a [Text] whose arguments are:
 *  - `text`: the [String] with resourse ID `R.string.feature_bookmarks_empty_description`
 *  ("Updates you save will be stored here to read later")
 *  - `modifier`: a [Modifier.fillMaxWidth]
 *  - `textAlign`: [TextAlign.Center] (The text will be aligned to the center of the screen).
 *  - `style`: the [Typography.bodyMedium] of our custom [MaterialTheme.typography].
 *
 * @param modifier The modifier to be applied to the screen.
 */
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(all = 16.dp)
            .fillMaxSize()
            .testTag(tag = "bookmarks:empty"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val iconTint: Color = LocalTintTheme.current.iconTint
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.feature_bookmarks_img_empty_bookmarks),
            colorFilter = if (iconTint != Color.Unspecified) {
                ColorFilter.tint(color = iconTint)
            } else {
                null
            },
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(height = 48.dp))

        Text(
            text = stringResource(id = R.string.feature_bookmarks_empty_error),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(height = 8.dp))

        Text(
            text = stringResource(id = R.string.feature_bookmarks_empty_description),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

/**
 * Previews the loading state [LoadingState] Composable of the Bookmarks screen.
 *
 * This preview is displayed within the NiaTheme.
 */
@Preview
@Composable
private fun LoadingStatePreview() {
    NiaTheme {
        LoadingState()
    }
}

/**
 * A preview of the [BookmarksGrid] composable that is used for displaying a grid of bookmarked
 * news resources.
 *
 * This composable uses a [PreviewParameter] to provide a list of [UserNewsResource] objects to the
 * [BookmarksGrid] composable. The [BookmarksGrid] composable is then displayed in a [NiaTheme]
 * composable.
 *
 * It composes a [NiaTheme] which wraps a [BookmarksGrid] whose arguments are:
 *  - `feedState`: a [Success] whose `feed` argument is our [List] of [UserNewsResource] parameter
 *  [userNewsResources].
 *  - `removeFromBookmarks`: an empty lambda.
 *  - `onNewsResourceViewed`: an empty lambda.
 *  - `onTopicClick`: an empty lambda.
 *
 * @param userNewsResources The list of user news resources to display. This parameter is provided
 * by a [UserNewsResourcePreviewParameterProvider].
 */
@Preview
@Composable
private fun BookmarksGridPreview(
    @PreviewParameter(provider = UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        BookmarksGrid(
            feedState = Success(feed = userNewsResources),
            removeFromBookmarks = {},
            onNewsResourceViewed = {},
            onTopicClick = {},
        )
    }
}

/**
 * Previews the empty state [EmptyState] Composable of the Bookmarks screen.
 *
 * This preview is displayed within the NiaTheme.
 */
@Preview
@Composable
private fun EmptyStatePreview() {
    NiaTheme {
        EmptyState()
    }
}
