/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.samples.apps.nowinandroid.core.data.model.RecentSearchQuery
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.Scrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.ScrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.DevicePreviews
import com.google.samples.apps.nowinandroid.core.ui.InterestsItem
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState.Success
import com.google.samples.apps.nowinandroid.core.ui.R.string
import com.google.samples.apps.nowinandroid.core.ui.TrackScreenViewEvent
import com.google.samples.apps.nowinandroid.core.ui.newsFeed
import kotlinx.coroutines.flow.StateFlow
import com.google.samples.apps.nowinandroid.feature.search.R as searchR

/**
 * Displays the search screen. Holds all the UI state for the stateless [SearchScreen]. We start by
 * initializing our [State] wrapped [RecentSearchQueriesUiState] variable `recentSearchQueriesUiState`
 * using the [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of
 * [RecentSearchQueriesUiState] property [SearchViewModel.recentSearchQueriesUiState] of our
 * [SearchViewModel] parameter [searchViewModel] to collect the [StateFlow] as a [State] wrapped
 * [RecentSearchQueriesUiState]. Next we initialize our [State] wrapped [SearchResultUiState]
 * variable `searchResultUiState` using the [StateFlow.collectAsStateWithLifecycle] method of the
 * [StateFlow] of [SearchResultUiState] property [SearchViewModel.searchResultUiState] of our
 * [SearchViewModel] parameter [searchViewModel] to collect the [StateFlow] as a [State] wrapped
 * [SearchResultUiState]. Finally, we initialize our [State] wrapped [String] variable
 * `searchQuery` using the [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of
 * [String] property [SearchViewModel.searchQuery] of our [SearchViewModel] parameter to collect the
 * [StateFlow] as a [State] wrapped [String].
 *
 * Then we compose a [SearchScreen] with the arguments:
 *  - `modifier`: Our [Modifier] parameter [modifier].
 *  - `searchQuery`: Our [State] wrapped [String] variable `searchQuery`.
 *  - `recentSearchesUiState`: Our [State] wrapped [RecentSearchQueriesUiState] variable
 *  `recentSearchQueriesUiState`.
 *  - `searchResultUiState`: Our [State] wrapped [SearchResultUiState] variable
 *  `searchResultUiState`.
 *  - `onSearchQueryChanged`: A reference to the [SearchViewModel.onSearchQueryChanged] method
 *  of our [SearchViewModel] parameter [searchViewModel].
 *  - `onSearchTriggered`: A reference to the [SearchViewModel.onSearchTriggered] method of our
 *  [SearchViewModel] parameter [searchViewModel].
 *  - `onClearRecentSearches`: A reference to the [SearchViewModel.clearRecentSearches] method of
 *  our [SearchViewModel] parameter [searchViewModel].
 *  - `onNewsResourcesCheckedChanged`: A reference to the [SearchViewModel.setNewsResourceBookmarked]
 *  method of our [SearchViewModel] parameter [searchViewModel].
 *  - `onNewsResourceViewed`: A lambda that accepts the [String] passed the lamba in variable `newsId`
 *  then calls the [SearchViewModel.setNewsResourceViewed] method of our [SearchViewModel] parameter
 *  [searchViewModel] with its `newsResourceId` argument set to `newsId` and its `viewed` argument
 *  set to `true`.
 *  - `onFollowButtonClick`: A reference to the [SearchViewModel.followTopic] method of our
 *  [SearchViewModel] parameter [searchViewModel].
 *  - `onBackClick`: Our lambda parameter [onBackClick].
 *  - `onInterestsClick`: Our lambda parameter [onInterestsClick].
 *  - `onTopicClick`: Our lambda parameter [onTopicClick].
 *
 * @param onBackClick Called when the user clicks the back button.
 * @param onInterestsClick Called when the user clicks the interests button.
 * @param onTopicClick Called when the user clicks a topic.
 * @param modifier The modifier to be applied to the screen.
 * @param searchViewModel The view model for the search screen.
 */
@Composable
internal fun SearchRoute(
    onBackClick: () -> Unit,
    onInterestsClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
        null,
    ),
) {
    /**
     * The recent search queries state. It uses the [StateFlow.collectAsStateWithLifecycle] method
     * of the [StateFlow] of [RecentSearchQueriesUiState] property
     * [SearchViewModel.recentSearchQueriesUiState] of our [SearchViewModel] parameter
     * [searchViewModel] to collect the [StateFlow] as a [State] wrapped [RecentSearchQueriesUiState].
     */
    val recentSearchQueriesUiState:
        RecentSearchQueriesUiState by searchViewModel
        .recentSearchQueriesUiState.collectAsStateWithLifecycle()

    /**
     * The search result state. It uses the [StateFlow.collectAsStateWithLifecycle] method of the
     * [StateFlow] of [SearchResultUiState] property [SearchViewModel.searchResultUiState] of our
     * [SearchViewModel] parameter [searchViewModel] to collect the [StateFlow] as a [State] wrapped
     * [SearchResultUiState].
     */
    val searchResultUiState:
        SearchResultUiState by searchViewModel
        .searchResultUiState.collectAsStateWithLifecycle()

    /**
     * The current search string. It uses the [StateFlow.collectAsStateWithLifecycle] method of the
     * [StateFlow] of [String] property [SearchViewModel.searchQuery] of our [SearchViewModel]
     * parameter [searchViewModel] to collect the [StateFlow] as a [State] wrapped [String].
     */
    val searchQuery: String by searchViewModel.searchQuery.collectAsStateWithLifecycle()

    SearchScreen(
        modifier = modifier,
        searchQuery = searchQuery,
        recentSearchesUiState = recentSearchQueriesUiState,
        searchResultUiState = searchResultUiState,
        onSearchQueryChanged = searchViewModel::onSearchQueryChanged,
        onSearchTriggered = searchViewModel::onSearchTriggered,
        onClearRecentSearches = searchViewModel::clearRecentSearches,
        onNewsResourcesCheckedChanged = searchViewModel::setNewsResourceBookmarked,
        onNewsResourceViewed = { newsId: String ->
            searchViewModel.setNewsResourceViewed(
                newsResourceId = newsId,
                viewed = true,
            )
        },
        onFollowButtonClick = searchViewModel::followTopic,
        onBackClick = onBackClick,
        onInterestsClick = onInterestsClick,
        onTopicClick = onTopicClick,
    )
}

/**
 * This is the stateless version of the screen that is shown when the user wants to search for
 * something.
 *
 * We start by calling the [TrackScreenViewEvent] composable with its `screenName` argument set to
 * the string "Search" in order to trigget a side-effect that records the screen view event.
 *
 * Our root composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In the [ColumnScope] `content` composable lambda argument we compose:
 *
 * A [Spacer] whose `modifier` argument is [Modifier.windowInsetsTopHeight] with its `insets` argument
 * set to [WindowInsets.Companion.safeDrawing].
 *
 * A [SearchToolbar] whose `onBackClick` argument is our lambda parameter [onBackClick], whose
 * `onSearchQueryChanged` argument is our lambda parameter [onSearchQueryChanged], whose
 * `onSearchTriggered` argument is our lambda parameter [onSearchTriggered], and whose
 * `searchQuery` argument is our [String] parameter [searchQuery].
 *
 * Next we switch on the type of our [SearchResultUiState] parameter [searchResultUiState]:
 *
 * When it is [SearchResultUiState.Loading], or [SearchResultUiState.LoadFailed], we do nothing.
 *
 * When it is [SearchResultUiState.SearchNotReady], we compose a [SearchNotReadyBody] composable.
 *
 * When it is [SearchResultUiState.EmptyQuery], if our [RecentSearchQueriesUiState] parameter
 * [recentSearchesUiState] is [RecentSearchQueriesUiState.Success], we compose a
 * [RecentSearchesBody] composable whose `onClearRecentSearches` argument is our lambda parameter
 * [onClearRecentSearches], whose `onRecentSearchClicked` argument a lambda that accepts the
 * [String] passed the lamba in variable `query` then calls our [onSearchQueryChanged] lambda
 * parameter [onSearchQueryChanged] with `query`, then calls our [onSearchTriggered] lambda
 * parameter with `query`, and whose `recentSearchQueries` uses the [Iterable.map] method of the
 * [List] of [RecentSearchQuery] property [RecentSearchQueriesUiState.Success.recentQueries]
 * to transform it to a [List] of [String] of their [RecentSearchQuery.query] properties.
 *
 * When it is [SearchResultUiState.Success], if it is [SearchResultUiState.Success.isEmpty], we
 * compose a [EmptySearchResultBody] composable whose `searchQuery` argument is our [String]
 * parameter [searchQuery], and whose `onInterestsClick` argument is our lambda parameter
 * [onInterestsClick], and if our [RecentSearchQueriesUiState] parameter [recentSearchesUiState]
 * is [RecentSearchQueriesUiState.Success], we compose a [RecentSearchesBody] composable
 * whose `onClearRecentSearches` argument is our lambda parameter [onClearRecentSearches],
 * whose `onRecentSearchClicked` argument a lambda that accepts the [String] passed the lamba
 * in variable `query` then calls our [onSearchQueryChanged] lambda parameter [onSearchQueryChanged]
 * with `query`, then calls our [onSearchTriggered] lambda parameter with `query`, and whose
 * `recentSearchQueries` uses the [Iterable.map] method of the [List] of [RecentSearchQuery]
 * property [RecentSearchQueriesUiState.Success.recentQueries] that transforms it to a [List] of
 * [String] of their [RecentSearchQuery.query] properties. And if the [SearchResultUiState.Success]
 * is not empty, we compose a [SearchResultBody] composable whose `searchQuery` argument is our
 * [String] parameter [searchQuery], whose `topics` argument is our [List] of [FollowableTopic]
 * property [SearchResultUiState.Success.topics], whose `newsResources` argument is our
 * [List] of [UserNewsResource] property [SearchResultUiState.Success.newsResources], whose
 * `onSearchTriggered` argument is our lambda parameter [onSearchTriggered], whose
 * `onTopicClick` argument is our lambda parameter [onTopicClick], whose
 * `onNewsResourcesCheckedChanged` argument is our lambda parameter [onNewsResourcesCheckedChanged],
 * whose `onNewsResourceViewed` argument is our lambda parameter [onNewsResourceViewed], and whose
 * `onFollowButtonClick` argument is our lambda parameter [onFollowButtonClick].
 *
 * At the end of the [ColumnScope] `content` composable lambda argument we compose a [Spacer] whose
 * `modifier` argument is [Modifier.windowInsetsBottomHeight] with its `insets` argument set to
 * [WindowInsets.Companion.safeDrawing].
 *
 * @param modifier The modifier to be applied to the screen.
 * @param searchQuery The current search query string.
 * @param recentSearchesUiState The recent search queries state.
 * @param searchResultUiState The search result state.
 * @param onSearchQueryChanged Called when the search query changes.
 * @param onSearchTriggered Called when the user submits the search query.
 * @param onClearRecentSearches Called when the user clicks the clear recent searches button.
 * @param onNewsResourcesCheckedChanged Called when the user checks or unchecks a news resource.
 * @param onNewsResourceViewed Called when the user views a news resource.
 * @param onFollowButtonClick Called when the user clicks the follow button.
 * @param onBackClick Called when the user clicks the back button.
 * @param onInterestsClick Called when the user clicks the interests button.
 * @param onTopicClick Called when the user clicks a topic.
 */
@Composable
internal fun SearchScreen(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    recentSearchesUiState: RecentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
    searchResultUiState: SearchResultUiState = SearchResultUiState.Loading,
    onSearchQueryChanged: (String) -> Unit = {},
    onSearchTriggered: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit = { _, _ -> },
    onNewsResourceViewed: (String) -> Unit = {},
    onFollowButtonClick: (String, Boolean) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
    onInterestsClick: () -> Unit = {},
    onTopicClick: (String) -> Unit = {},
) {
    TrackScreenViewEvent(screenName = "Search")
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(insets = WindowInsets.safeDrawing))
        SearchToolbar(
            onBackClick = onBackClick,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = onSearchTriggered,
            searchQuery = searchQuery,
        )
        when (searchResultUiState) {
            SearchResultUiState.Loading,
            SearchResultUiState.LoadFailed,
                -> Unit

            SearchResultUiState.SearchNotReady -> SearchNotReadyBody()
            SearchResultUiState.EmptyQuery,
                -> {
                if (recentSearchesUiState is RecentSearchQueriesUiState.Success) {
                    RecentSearchesBody(
                        onClearRecentSearches = onClearRecentSearches,
                        onRecentSearchClicked = { query: String ->
                            onSearchQueryChanged(query)
                            onSearchTriggered(query)
                        },
                        recentSearchQueries = recentSearchesUiState.recentQueries.map { it.query },
                    )
                }
            }

            is SearchResultUiState.Success -> {
                if (searchResultUiState.isEmpty()) {
                    EmptySearchResultBody(
                        searchQuery = searchQuery,
                        onInterestsClick = onInterestsClick,
                    )
                    if (recentSearchesUiState is RecentSearchQueriesUiState.Success) {
                        RecentSearchesBody(
                            onClearRecentSearches = onClearRecentSearches,
                            onRecentSearchClicked = { query: String ->
                                onSearchQueryChanged(query)
                                onSearchTriggered(query)
                            },
                            recentSearchQueries = recentSearchesUiState.recentQueries.map { it.query },
                        )
                    }
                } else {
                    SearchResultBody(
                        searchQuery = searchQuery,
                        topics = searchResultUiState.topics,
                        newsResources = searchResultUiState.newsResources,
                        onSearchTriggered = onSearchTriggered,
                        onTopicClick = onTopicClick,
                        onNewsResourcesCheckedChanged = onNewsResourcesCheckedChanged,
                        onNewsResourceViewed = onNewsResourceViewed,
                        onFollowButtonClick = onFollowButtonClick,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.windowInsetsBottomHeight(insets = WindowInsets.safeDrawing))
    }
}

/**
 * Composable that displays when a search result is empty. Our root composable is a [Column] whose
 * `horizontalAlignment` argument is [Alignment.CenterHorizontally] and whose `modifier` argument is
 * a [Modifier.padding] that adds `48.dp` to the horizontal sides. In the [ColumnScope] `content`
 * composable lambda argument of the [Column] we start by initializing our [String] variable
 * `message` to the formatted string that is formatted using the [String] with resouce ID
 * `searchR.string.feature_search_result_not_found` and the [String] parameter [searchQuery]
 * ("Sorry, there is no content found for your search "%1$s""), and we initialize our [Int] variable
 * `start` to the index of the first occurrence of the [String] parameter [searchQuery] in the
 * [String] variable `message`. Next we compose a [Text] whose arguments are:
 *  - `text`: An [AnnotatedString] whose `text` argument is the [String] variable `message` and
 *  which has a [SpanStyle] whose `spanStyles` argument is a [List] of [AnnotatedString.Range]
 *  that applies [FontWeight.Bold] to the range between the [Int] variable `start` and the variable
 *  `start` plus the length of the [String] parameter [searchQuery].
 *  - `style`: The [TextStyle] of the [Text] is the [Typography.bodyLarge] of our custom
 *  [MaterialTheme.typography]
 *  - `textAlign`: The [TextAlign] of the [Text] is [TextAlign.Center].
 *  - `modifier`: The [Modifier] of the [Text] is a [Modifier.padding] that adds `24.dp` to the
 *  `vertical` sides.
 *
 * Next we initialize our [AnnotatedString] variable `tryAnotherSearchString` using the method
 * [buildAnnotatedString] to build one which contains a fancy clickable link to [onInterestsClick].
 * (TODO: Refer to this when replacing deprecated ClickableText composables.)
 *
 * Finally, we compose a [Text] whose arguments are:
 *  - `text`: The [AnnotatedString] variable `tryAnotherSearchString`.
 *  - `style`: The [TextStyle] of the [Text] is the [Typography.bodyLarge] of our custom
 *  [MaterialTheme.typography] `merge`d with a [TextStyle] whose `color` argument is the
 *  [ColorScheme.secondary] of our custom [MaterialTheme.colorScheme], and whose `textAlign`
 *  argument is [TextAlign.Center].
 *  - `modifier`: The [Modifier] of the [Text] is a [Modifier.padding] that adds `36.dp` to the
 *  `start` and `end` sides, and `24.dp` to the `bottom`.
 *
 * @param searchQuery the search query that was used.
 * @param onInterestsClick the callback that is called when the "Interests" link is clicked.
 */
@Composable
fun EmptySearchResultBody(
    searchQuery: String,
    onInterestsClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 48.dp),
    ) {
        val message: String =
            stringResource(id = searchR.string.feature_search_result_not_found, searchQuery)
        val start: Int = message.indexOf(string = searchQuery)
        Text(
            text = AnnotatedString(
                text = message,
                spanStyles = listOf(
                    AnnotatedString.Range(
                        item = SpanStyle(fontWeight = FontWeight.Bold),
                        start = start,
                        end = start + searchQuery.length,
                    ),
                ),
            ),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp),
        )
        val tryAnotherSearchString: AnnotatedString = buildAnnotatedString {
            append(text = stringResource(id = searchR.string.feature_search_try_another_search))
            append(text = " ")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "",
                    linkInteractionListener = {
                        onInterestsClick()
                    },
                ),
            ) {
                withStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    append(text = stringResource(id = searchR.string.feature_search_interests))
                }
            }

            append(text = " ")
            append(text = stringResource(id = searchR.string.feature_search_to_browse_topics))
        }
        Text(
            text = tryAnotherSearchString,
            style = MaterialTheme.typography.bodyLarge.merge(
                other = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                ),
            ),
            modifier = Modifier
                .padding(start = 36.dp, end = 36.dp, bottom = 24.dp),
        )
    }
}

/**
 * Search not ready screen, which is displayed when the [SearchResultUiState] is a
 * [SearchResultUiState.SearchNotReady] which happens when the *Fts tables are not populated yet.
 *
 * Our root composable is a [Column] whose `horizontalAlignment` argument is
 * [Alignment.CenterHorizontally] and whose `modifier` argument is a [Modifier.padding] that adds
 * `48.dp` to the horizontal sides. In the [ColumnScope] `content` composable lambda argument of the
 * [Column] we compose a [Text] whose arguments are:
 *  - `text`: The [String] with resource ID `searchR.string.feature_search_not_ready`. ("Sorry, we
 *  are still processing the search index. Please come back later")
 *  - `style`: The [TextStyle] of the [Text] is the [Typography.bodyLarge] of our custom
 *  [MaterialTheme.typography]
 *  - `textAlign`: The [TextAlign] of the [Text] is [TextAlign.Center].
 *  - `modifier`: The [Modifier] of the [Text] is a [Modifier.padding] that adds `24.dp` to the
 *  `vertical` sides.
 */
@Composable
private fun SearchNotReadyBody() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 48.dp),
    ) {
        Text(
            text = stringResource(id = searchR.string.feature_search_not_ready),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp),
        )
    }
}

/**
 * Displays the search results.
 *
 * We start by initializing our [LazyStaggeredGridState] variable `state` to the instance returned
 * by [rememberLazyStaggeredGridState]. Our root composable is a [Box] whose `modifier` argument is
 * a [Modifier.fillMaxSize]. In the [BoxScope] `content` composable lambda argument of the [Box] we
 * compose a [LazyVerticalStaggeredGrid] whose arguments are:
 *  - `columns`: is a [StaggeredGridCells.Adaptive] whose `minSize` argument is `300.dp`.
 *  - `contentPadding`: is a [PaddingValues] that adds `16.dp` to `all` sides.
 *  - `horizontalArrangement`: is a [Arrangement.spacedBy] that causes adjacent children to be placed
 *  `space`d by `16.dp` horizontally.
 *  - `verticalItemSpacing`: is `24.dp`.
 *  - `modifier`: is a [Modifier.fillMaxSize] chained to a [Modifier.testTag] whose `tag` argument
 *  is the [String] "search:newsResources".
 *  - `state`: is our [LazyStaggeredGridState] variable `state`.
 *
 * In the [LazyStaggeredGridScope] `content` composable lambda argument, if our [List] of
 * [FollowableTopic] parameter [topics] is not empty, we compose a [LazyStaggeredGridScope.item]
 * whose `span` argument is [StaggeredGridItemSpan.FullLine]. In its [LazyStaggeredGridItemScope]
 * `content` composable lambda argument we compose a [Text] whose `text` argument is an
 * [AnnotatedString] displaying the string "Topics" using [FontWeight.Bold], and whose `modifier`
 * argument is a [Modifier.padding] that adds `16.dp` to the `horizontal` sides and `8.dp` to the
 * `vertical` sides.
 *
 * Then we use the [Iterable.forEach] method of the [List] of [FollowableTopic] parameter [topics] to
 * iterate over each [FollowableTopic] capturing the [FollowableTopic] in variable `followableTopic`,
 * and initalizing our [String] variable `topicId` to the [Topic.id] of the [FollowableTopic.topic]
 * of `followableTopic`. We then compose a [LazyStaggeredGridScope.item] whose `key` argument is
 * the string "topic-$topicId" and whose `span` argument is [StaggeredGridItemSpan.FullLine]. In the
 * [LazyStaggeredGridItemScope] `content` composable lambda argument we compose an [InterestsItem]
 * whose arguments are:
 *  - `name`: is the [Topic.name] of the [FollowableTopic.topic] of `followableTopic`.
 *  - `following`: is the [FollowableTopic.isFollowed] of `followableTopic`.
 *  - `description`: is the [Topic.shortDescription] of the [FollowableTopic.topic] of
 *  `followableTopic`.
 *  - `topicImageUrl`: is the [Topic.imageUrl] of the [FollowableTopic.topic] of `followableTopic`.
 *  - `onClick`: is a lambda that calls our [onSearchTriggered] lambda parameter with our [String]
 *  parameter [searchQuery] then calls our [onTopicClick] lambda parameter with `topicId`.
 *  - `onFollowButtonClick`: is a lambda that calls our [onFollowButtonClick] lambda parameter with
 *  `topicId` and the [Boolean] passed the lambda in variable `it`.
 *
 * If our [List] of [UserNewsResource] parameter [newsResources] is not empty, we compose a
 * [LazyStaggeredGridScope.item] whose `span` argument is [StaggeredGridItemSpan.FullLine]. In the
 * [LazyStaggeredGridItemScope] `content` composable lambda argument we compose a [Text] whose `text`
 * argument is an [AnnotatedString] displaying the string "Updates" using [FontWeight.Bold], and
 * whose `modifier` argument is a [Modifier.padding] that adds `16.dp` to the `horizontal` sides and
 * `8.dp` to the `vertical` sides. Below that we compose a [newsFeed] composable whose arguments are:
 *  - `feedState`: is a [Success] whose `feed` argument is our [List] of [UserNewsResource] parameter
 *  [newsResources].
 *  - `onNewsResourcesCheckedChanged`: is our [onNewsResourcesCheckedChanged] lambda parameter.
 *  - `onNewsResourceViewed`: is our [onNewsResourceViewed] lambda parameter.
 *  - `onTopicClick`: is our [onTopicClick] lambda parameter.
 *  - `onExpandedCardClick`: is a lambda that calls our [onSearchTriggered] lambda parameter with
 *  our [String] parameter [searchQuery].
 *
 * Continuing on in the [BoxScope] `content` composable lambda argument of the [Box] we initialize
 * our [Int] variable `itemsAvailable` to the length of our [List] of [FollowableTopic] parameter
 * [topics] plus the length of our [List] of [UserNewsResource] parameter [newsResources]. We
 * initialize our [ScrollbarState] variable `scrollbarState` to the instance returned by the
 * [LazyStaggeredGridState.scrollbarState] method of our [LazyStaggeredGridState] variable `state`
 * when called with its `itemsAvailable` argument set to our [Int] variable `itemsAvailable`. We
 * then compose a [DraggableScrollbar] whose receiver is our [LazyStaggeredGridState] variable
 * `state` amd whose arguments are:
 *  - `modifier`: is a [Modifier.fillMaxHeight] chained to a [Modifier.windowInsetsPadding] whose
 *  `insets` argument is [WindowInsets.Companion.systemBars], chained to a [Modifier.padding] that
 *  adds `2.dp` padding to the `horizontal` sides, and chained to a [BoxScope.align] whose `alignment`
 *  argument is [Alignment.CenterEnd].
 *  - `state`: is our [ScrollbarState] variable `scrollbarState`.
 *  - `orientation`: is [Orientation.Vertical].
 *  - `onThumbMoved`: is the [LazyStaggeredGridState.rememberDraggableScroller] created from our
 *  [LazyStaggeredGridState] variable `state` when called with its `itemsAvailable` argument set
 *  to our [Int] variable `itemsAvailable` (it is a Generic function that reacts to [Scrollbar]
 *  thumb displacements in a lazy layout by calling the [LazyStaggeredGridState.scroll] method of its
 *  receiver inside a [LaunchedEffect]).
 *
 * @param searchQuery The current search query.
 * @param topics The list of topics that match the search query.
 * @param newsResources The list of news resources that match the search query.
 * @param onSearchTriggered Called when the search query is submitted.
 * @param onTopicClick Called when a topic is clicked.
 * @param onNewsResourcesCheckedChanged Called when a news resource is bookmarked or unbookmarked.
 * @param onNewsResourceViewed Called when a news resource is viewed.
 * @param onFollowButtonClick Called when a topic is followed or unfollowed.
 */
@Composable
private fun SearchResultBody(
    searchQuery: String,
    topics: List<FollowableTopic>,
    newsResources: List<UserNewsResource>,
    onSearchTriggered: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onFollowButtonClick: (String, Boolean) -> Unit,
) {
    val state: LazyStaggeredGridState = rememberLazyStaggeredGridState()
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(all = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .fillMaxSize()
                .testTag(tag = "search:newsResources"),
            state = state,
        ) {
            if (topics.isNotEmpty()) {
                item(
                    span = StaggeredGridItemSpan.FullLine,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(text = stringResource(id = searchR.string.feature_search_topics))
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
                topics.forEach { followableTopic: FollowableTopic ->
                    val topicId: String = followableTopic.topic.id
                    item(
                        // Append a prefix to distinguish a key for news resources
                        key = "topic-$topicId",
                        span = StaggeredGridItemSpan.FullLine,
                    ) {
                        InterestsItem(
                            name = followableTopic.topic.name,
                            following = followableTopic.isFollowed,
                            description = followableTopic.topic.shortDescription,
                            topicImageUrl = followableTopic.topic.imageUrl,
                            onClick = {
                                // Pass the current search query to ViewModel to save it as recent searches
                                onSearchTriggered(searchQuery)
                                onTopicClick(topicId)
                            },
                            onFollowButtonClick = { onFollowButtonClick(topicId, it) },
                        )
                    }
                }
            }

            if (newsResources.isNotEmpty()) {
                item(
                    span = StaggeredGridItemSpan.FullLine,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(text = stringResource(id = searchR.string.feature_search_updates))
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                newsFeed(
                    feedState = Success(feed = newsResources),
                    onNewsResourcesCheckedChanged = onNewsResourcesCheckedChanged,
                    onNewsResourceViewed = onNewsResourceViewed,
                    onTopicClick = onTopicClick,
                    onExpandedCardClick = {
                        onSearchTriggered(searchQuery)
                    },
                )
            }
        }
        val itemsAvailable: Int = topics.size + newsResources.size
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
 * Displays a list of recent search queries.
 *
 * Our root composable is a [Column]. In its [ColumnScope] `content` composable lambda argument we
 * first compose a [Row] whose `horizontalArrangement` argument is [Arrangement.SpaceBetween],
 * whose `verticalAlignment` argument is [Alignment.CenterVertically], and whose `modifier` argument
 * is a [Modifier.fillMaxWidth]. In the [RowScope] `content` composable lambda argument we compose
 * a [Text] whose `text` argument is an [AnnotatedString] displaying the string "Recent searches"
 * using [FontWeight.Bold], and whose `modifier` argument is a [Modifier.padding] that adds  `16.dp`
 * to the `horizontal` sides and `8.dp` to the `vertical` sides. Below that if our [List] of [String]
 * parameter [recentSearchQueries] is not empty, we compose an [IconButton] whose `onClick` argument
 * is a lambda that calls our [onClearRecentSearches] lambda parameter, and whose `modifier` argument
 * is a [Modifier.padding] that adds `16.dp` to the `horizontal` sides. In its `content` composable
 * lambda argument we compose an [Icon] whose arguments are:
 *  - `imageVector`: is [NiaIcons.Close].
 *  - `contentDescription`: is the string "Clear searches".
 *  - `tint`: is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *
 * Below the [Row] we compose a [LazyColumn] whose `modifier` argument is a [Modifier.padding] that
 * adds `16.dp` to the `horizontal` sides. In the [LazyListScope] `content` composable lambda
 * argument we compose an [LazyListScope.items] whose `items` argument is our [List] of [String]
 * parameter [recentSearchQueries]. In the [LazyItemScope] `itemContent` composable lambda argument
 * we capture the [String] passed the lambda in variable `recentSearch` then we compose a [Text]
 * whose arguments are:
 *  - `text`: is the [String] variable `recentSearch`.
 *  - `style`: is the [Typography.headlineSmall] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` to the `vertical` sides chained to a
 *  [Modifier.clickable] whose `onClick` argument is a lambda that calls our [onRecentSearchClicked]
 *  lambda parameter with the [String] variable `recentSearch`, and to that is chained a
 *  [Modifier.fillMaxWidth].
 *
 * @param recentSearchQueries The list of recent search queries.
 * @param onClearRecentSearches Called when the clear recent searches button is clicked.
 * @param onRecentSearchClicked Called when a recent search query is clicked.
 */
@Composable
private fun RecentSearchesBody(
    recentSearchQueries: List<String>,
    onClearRecentSearches: () -> Unit,
    onRecentSearchClicked: (String) -> Unit,
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text = stringResource(id = searchR.string.feature_search_recent_searches))
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            if (recentSearchQueries.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onClearRecentSearches()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Icon(
                        imageVector = NiaIcons.Close,
                        contentDescription = stringResource(
                            id = searchR.string.feature_search_clear_recent_searches_content_desc,
                        ),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(items = recentSearchQueries) { recentSearch: String ->
                Text(
                    text = recentSearch,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .clickable { onRecentSearchClicked(recentSearch) }
                        .fillMaxWidth(),
                )
            }
        }
    }
}

/**
 * This composable is a Row that displays the top app bar for the search screen. It displays a
 * back arrow [IconButton] on the left and a [SearchTextField] on the right. Our root composable
 * is a [Row] whose `verticalAlignment` argument is [Alignment.CenterVertically] (to center its
 * children vertically), and whose `modifier` argument is our [Modifier] parameter [modifier]
 * chained to a [Modifier.fillMaxWidth] (so that the [Row] occupies its entire incoming width
 * constraint). The [RowScope] `content` composable lambda argument of the [Row] contains:
 *  - An [IconButton] whose `onClick` argument is a lambda that calls our [onBackClick] lambda
 *  parameter. In the `content` lambda argument of the [IconButton] is an [Icon] displaying
 *  [NiaIcons.ArrowBack] (a left pointing arrow vector graphic) with a `contentDescription` which
 *  is the string with resource ID [string.core_ui_back] ("Back").
 *  - A [SearchTextField] whose `onSearchQueryChanged` argument is our [onSearchQueryChanged] lambda
 *  parameter, whose `onSearchTriggered` argument is our [onSearchTriggered] lambda parameter, and
 *  whose `searchQuery` argument is our [String] parameter [searchQuery].
 *
 * @param searchQuery The current search query.
 * @param onSearchQueryChanged Called when the search query changes.
 * @param onSearchTriggered Called when the search query is submitted.
 * @param onBackClick Called when the back button is clicked.
 * @param modifier The modifier to be applied to the search toolbar.
 */
@Composable
private fun SearchToolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = { onBackClick() }) {
            Icon(
                imageVector = NiaIcons.ArrowBack,
                contentDescription = stringResource(
                    id = string.core_ui_back,
                ),
            )
        }
        SearchTextField(
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = onSearchTriggered,
            searchQuery = searchQuery,
        )
    }
}

/**
 * This is a text input field used for searching for topics or news resources.
 *
 * We start by initializing and remembering our [FocusRequester] variable `focusRequester` to the
 * instance returned by the [remember] method when called with a new instance of [FocusRequester].
 * We then initialize our [SoftwareKeyboardController] to the `current` value of
 * [LocalSoftwareKeyboardController]. We initialize our lambda variable `onSearchExplicitlyTriggered`
 * to a lambda that calls the [SoftwareKeyboardController.hide] method of our
 * [SoftwareKeyboardController] variable `keyboardController` then calls our [onSearchTriggered]
 * lambda parameter with our [String] parameter [searchQuery]. Our root composable is a [TextField]
 * whose arguments are:
 *  - `colors`: is a [TextFieldDefaults.colors] with its `focusedIndicatorColor` [Color] set to
 *  [Color.Transparent], its `unfocusedIndicatorColor` [Color] set to [Color.Transparent], and its
 *  `disabledIndicatorColor` [Color] set to [Color.Transparent].
 *  - `leadingIcon`: is a lambda that composes an [Icon] whose `imageVector` argument is
 *  [NiaIcons.Search], whose `contentDescription` argument the string "Search", and whose `tint`
 *  argument is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *  - `trailingIcon`: is a lambda that, if our [String] parameter [searchQuery] is not empty,
 *  composes an [IconButton] whose `onClick` argument is a lambda that calls our [onSearchQueryChanged]
 *  lambda parameter with an empty string, and whose `content` composable lambda argument composes
 *  an [Icon] whose `imageVector` argument is [NiaIcons.Close], whose `contentDescription` argument
 *  is the string "Clear search text", and whose `tint` argument is the [ColorScheme.onSurface] of
 *  our custom [MaterialTheme.colorScheme].
 *  - `onValueChange`: is a lambda that accepts the [String] passed the lambda in variable `it` then
 *  calls our [onSearchQueryChanged] lambda parameter with `it` if there is no newline character in
 *  `it`.
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `6.dp` to
 *  `all` sides, chained to a [Modifier.focusRequester] whose `focusRequester` argument is our
 *  [FocusRequester] variable `focusRequester`, chained to a [Modifier.onKeyEvent] that accepts the
 *  [KeyEvent] passed its lambda argument in variable `keyEvent` then if the [KeyEvent.key] is
 *  [Key.Enter] calls our `onSearchExplicitlyTriggered` lambda variable if `searchQuery` is not
 *  empty, and returns `true`. Otherwise it returns `false`. And this is chained to a
 *  [Modifier.testTag] whose `tag` argument is the string "searchTextField".
 *  - `shape`: is a [RoundedCornerShape] with its `size` argument set to `32.dp`.
 *  - `value`: is our [String] parameter [searchQuery].
 *  - `keyboardOptions`: is a [KeyboardOptions] with its `imeAction` argument set to [ImeAction.Search].
 *  - `keyboardActions`: is a [KeyboardActions] with its `onSearch` argument set to a lambda that
 *  does nothing if our [String] parameter [searchQuery] is empty, otherwise calls our
 *  `onSearchExplicitlyTriggered` lambda variable.
 *  - `maxLines`: is `1`.
 *  - `singleLine`: is `true`.
 *
 * Below this is a [LaunchedEffect] that calls the [FocusRequester.requestFocus] method of our
 * [FocusRequester] variable `focusRequester` when this composable is first composed (because its
 * `key1` argument is `Unit` it will not be called again).
 *
 * @param searchQuery The current search query.
 * @param onSearchQueryChanged Called when the search query changes.
 * @param onSearchTriggered Called when the search query is submitted.
 */
@Composable
private fun SearchTextField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
) {
    val focusRequester: FocusRequester = remember { FocusRequester() }
    val keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
        onSearchTriggered(searchQuery)
    }

    TextField(
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = NiaIcons.Search,
                contentDescription = stringResource(
                    id = searchR.string.feature_search_title,
                ),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onSearchQueryChanged("")
                    },
                ) {
                    Icon(
                        imageVector = NiaIcons.Close,
                        contentDescription = stringResource(
                            id = searchR.string.feature_search_clear_search_text_content_desc,
                        ),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        onValueChange = {
            if ("\n" !in it) onSearchQueryChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .focusRequester(focusRequester = focusRequester)
            .onKeyEvent { keyEvent: KeyEvent ->
                if (keyEvent.key == Key.Enter) {
                    if (searchQuery.isBlank()) return@onKeyEvent false
                    onSearchExplicitlyTriggered()
                    true
                } else {
                    false
                }
            }
            .testTag(tag = "searchTextField"),
        shape = RoundedCornerShape(size = 32.dp),
        value = searchQuery,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                if (searchQuery.isBlank()) return@KeyboardActions
                onSearchExplicitlyTriggered()
            },
        ),
        maxLines = 1,
        singleLine = true,
    )
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
}

/**
 * Preview of the [SearchToolbar] composable. We compose it inside our [NiaTheme] custom
 * [MaterialTheme].
 */
@Preview
@Composable
private fun SearchToolbarPreview() {
    NiaTheme {
        SearchToolbar(
            searchQuery = "",
            onBackClick = {},
            onSearchQueryChanged = {},
            onSearchTriggered = {},
        )
    }
}

/**
 * This is a Preview of the [EmptySearchResultBody] composable which is displayed when a search result
 * is empty. We compose it inside our [NiaTheme] custom [MaterialTheme] with its `onInterestsClick`
 * argument a do nothing lambda, and its `searchQuery` argument the string "C++".
 */
@Preview
@Composable
private fun EmptySearchResultColumnPreview() {
    NiaTheme {
        EmptySearchResultBody(
            onInterestsClick = {},
            searchQuery = "C++",
        )
    }
}

/**
 * This is a preview of the [RecentSearchesBody] composable. We compose it inside our [NiaTheme]
 * custom [MaterialTheme].
 */
@Preview
@Composable
private fun RecentSearchesBodyPreview() {
    NiaTheme {
        RecentSearchesBody(
            onClearRecentSearches = {},
            onRecentSearchClicked = {},
            recentSearchQueries = listOf("kotlin", "jetpack compose", "testing"),
        )
    }
}

/**
 * This is a Preview of the [SearchNotReadyBody] composable. We compose it inside our [NiaTheme]
 * custom [MaterialTheme]. The [SearchNotReadyBody] is used when the Fts tables are not populated yet.
 */
@Preview
@Composable
private fun SearchNotReadyBodyPreview() {
    NiaTheme {
        SearchNotReadyBody()
    }
}

/**
 * This is a Preview of the [SearchScreen] composable that is annotated with our [DevicePreviews]
 * custom Preview annotation to cause it to be rendered on four different device sizes.
 *
 * We compose our [SearchScreenPreview] inside our [NiaTheme] custom [MaterialTheme], with its
 * `searchResultUiState` argument our [SearchResultUiState] parameter [searchResultUiState].
 *
 * @param searchResultUiState the [SearchResultUiState] to use to render our [SearchScreen]. The
 * [PreviewParameter] annotation causes our [SearchUiStatePreviewParameterProvider] to provide this
 * parameter.
 */
@DevicePreviews
@Composable
private fun SearchScreenPreview(
    @PreviewParameter(provider = SearchUiStatePreviewParameterProvider::class)
    searchResultUiState: SearchResultUiState,
) {
    NiaTheme {
        SearchScreen(searchResultUiState = searchResultUiState)
    }
}
