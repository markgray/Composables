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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.data.model.RecentSearchQuery
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.ScrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
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
 * [String] property [SearchViewModel.searchQuery] of our [SearchViewModel] parameter.
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
    searchViewModel: SearchViewModel = hiltViewModel(),
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
                viewed = true
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
 * that transforms it to a [List] of [String] of their [RecentSearchQuery.query] properties.
 *
 * When it is [SearchResultUiState.Success], if its [SearchResultUiState.Success.isEmpty], we
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
 * a [Modifier.padding] that adds `48.dp` to the horizontal sides.
 * TODO: Continue here.
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

@Preview
@Composable
private fun SearchNotReadyBodyPreview() {
    NiaTheme {
        SearchNotReadyBody()
    }
}

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
