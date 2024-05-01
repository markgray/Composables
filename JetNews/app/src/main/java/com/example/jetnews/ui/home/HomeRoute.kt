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

package com.example.jetnews.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import com.example.jetnews.model.Post
import com.example.jetnews.ui.JetnewsDestinations
import com.example.jetnews.ui.JetnewsNavGraph
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.HomeScreenType.ArticleDetails
import com.example.jetnews.ui.home.HomeScreenType.Feed
import com.example.jetnews.ui.home.HomeScreenType.FeedWithArticleDetails
import kotlinx.coroutines.flow.StateFlow

/**
 * Displays the Home route. This is composed by the [NavHost] in [JetnewsNavGraph] for the route
 * [JetnewsDestinations.HOME_ROUTE]. We start by initializing our [HomeUiState] variable `val uiState`
 * using the [StateFlow.collectAsStateWithLifecycle] extension function to values from the [StateFlow]
 * of [HomeUiState] property [HomeViewModel.uiState] of our [HomeViewModel] parameter [homeViewModel]
 * and representing its latest value via [State] in a lifecycle-aware manner. Then our root composable
 * is a call to our [HomeRoute] stateless (more or less) override with its `uiState` argument our
 * [State] wrapped [HomeUiState] variable `uiState`, its `isExpandedScreen` argument is our [Boolean]
 * parameter [isExpandedScreen], its `onToggleFavorite` argument is a lambda which calls the
 * [HomeViewModel.toggleFavourite] method of [homeViewModel] with the [String] passed the lambda,
 * its `onSelectPost` argument is a lambda which calls the [HomeViewModel.selectArticle] method of
 * [homeViewModel] with the [String] passed the lambda, its `onRefreshPosts` argument is a lambda
 * which calls the [HomeViewModel.refreshPosts] method of [homeViewModel], its `onErrorDismiss`
 * argument is a lambda which calls the [HomeViewModel.errorShown] method of [homeViewModel] with
 * the [Long] passed the lambda, its `onInteractWithFeed` argument is a lambda which calls the
 * [HomeViewModel.interactedWithFeed] method of [homeViewModel], its `onInteractWithArticleDetails`
 * argument is a lambda which calls the [HomeViewModel.interactedWithArticleDetails] method of
 * [homeViewModel] with the [String] passed the lambda, its `onSearchInputChanged` argument is
 * a lambda which calls the [HomeViewModel.onSearchInputChanged] method of [homeViewModel] with the
 * [String] passed the lambda, its `openDrawer` argument is our [openDrawer] lambda parameter, and
 * its `snackbarHostState` argument is our [SnackbarHostState] parameter [snackbarHostState].
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 * @param isExpandedScreen (state) whether the screen is expanded
 * @param openDrawer (event) request opening the app drawer
 * @param snackbarHostState (state) [SnackbarHostState] for the [Scaffold] component on this screen
 */
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    // UiState of the HomeScreen
    val uiState: HomeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeRoute(
        uiState = uiState,
        isExpandedScreen = isExpandedScreen,
        onToggleFavorite = { homeViewModel.toggleFavourite(postId = it) },
        onSelectPost = { homeViewModel.selectArticle(postId = it) },
        onRefreshPosts = { homeViewModel.refreshPosts() },
        onErrorDismiss = { homeViewModel.errorShown(errorId = it) },
        onInteractWithFeed = { homeViewModel.interactedWithFeed() },
        onInteractWithArticleDetails = { homeViewModel.interactedWithArticleDetails(postId = it) },
        onSearchInputChanged = { homeViewModel.onSearchInputChanged(searchInput = it) },
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
    )
}

/**
 * Displays the Home route, with the [HomeUiState] and [HomeViewModel] hoisted to its "stateful"
 * [HomeRoute] override.
 *
 * This composable is not coupled to any specific state management.
 *
 * @param uiState (state) the data to show on the screen
 * @param isExpandedScreen (state) whether the screen is expanded
 * @param onToggleFavorite (event) toggles favorite for a post
 * @param onSelectPost (event) indicate that a post was selected
 * @param onRefreshPosts (event) request a refresh of posts
 * @param onErrorDismiss (event) error message was shown
 * @param onInteractWithFeed (event) indicate that the feed was interacted with
 * @param onInteractWithArticleDetails (event) indicate that the article details were interacted
 * with
 * @param openDrawer (event) request opening the app drawer
 * @param snackbarHostState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeRoute(
    uiState: HomeUiState,
    isExpandedScreen: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onInteractWithFeed: () -> Unit,
    onInteractWithArticleDetails: (String) -> Unit,
    onSearchInputChanged: (String) -> Unit,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    // Construct the lazy list states for the list and the details outside of deciding which one to
    // show. This allows the associated state to survive beyond that decision, and therefore
    // we get to preserve the scroll throughout any changes to the content.
    val homeListLazyListState = rememberLazyListState()

    @Suppress("Destructure")
    val articleDetailLazyListStates: Map<String, LazyListState> = when (uiState) {
        is HomeUiState.HasPosts -> uiState.postsFeed.allPosts
        is HomeUiState.NoPosts -> emptyList()
    }.associate { post: Post ->
        key(post.id) {
            post.id to rememberLazyListState()
        }
    }

    val homeScreenType: HomeScreenType = getHomeScreenType(
        isExpandedScreen = isExpandedScreen,
        uiState = uiState
    )
    when (homeScreenType) {
        FeedWithArticleDetails -> {
            HomeFeedWithArticleDetailsScreen(
                uiState = uiState,
                showTopAppBar = !isExpandedScreen,
                onToggleFavorite = onToggleFavorite,
                onSelectPost = onSelectPost,
                onRefreshPosts = onRefreshPosts,
                onErrorDismiss = onErrorDismiss,
                onInteractWithList = onInteractWithFeed,
                onInteractWithDetail = onInteractWithArticleDetails,
                openDrawer = openDrawer,
                homeListLazyListState = homeListLazyListState,
                articleDetailLazyListStates = articleDetailLazyListStates,
                snackbarHostState = snackbarHostState,
                onSearchInputChanged = onSearchInputChanged,
            )
        }
        Feed -> {
            HomeFeedScreen(
                uiState = uiState,
                showTopAppBar = !isExpandedScreen,
                onToggleFavorite = onToggleFavorite,
                onSelectPost = onSelectPost,
                onRefreshPosts = onRefreshPosts,
                onErrorDismiss = onErrorDismiss,
                openDrawer = openDrawer,
                homeListLazyListState = homeListLazyListState,
                snackbarHostState = snackbarHostState,
                onSearchInputChanged = onSearchInputChanged,
            )
        }
        ArticleDetails -> {
            // Guaranteed by above condition for home screen type
            check(uiState is HomeUiState.HasPosts)

            ArticleScreen(
                post = uiState.selectedPost,
                isExpandedScreen = isExpandedScreen,
                onBack = onInteractWithFeed,
                isFavorite = uiState.favorites.contains(element = uiState.selectedPost.id),
                onToggleFavorite = {
                    onToggleFavorite(uiState.selectedPost.id)
                },
                lazyListState = articleDetailLazyListStates.getValue(
                    uiState.selectedPost.id
                )
            )

            // If we are just showing the detail, have a back press switch to the list.
            // This doesn't take anything more than notifying that we "interacted with the list"
            // since that is what drives the display of the feed
            BackHandler {
                onInteractWithFeed()
            }
        }
    }
}

/**
 * A precise enumeration of which type of screen to display at the home route.
 *
 * There are 3 options:
 * - [FeedWithArticleDetails], which displays both a list of all articles and a specific article.
 * - [Feed], which displays just the list of all articles
 * - [ArticleDetails], which displays just a specific article.
 */
enum class HomeScreenType {
    /**
     * Displays both a list of all articles and a specific article
     */
    FeedWithArticleDetails,
    /**
     * Displays just the list of all articles
     */
    Feed,
    /**
     * Displays just a specific article
     */
    ArticleDetails
}

/**
 * Returns the current [HomeScreenType] to display, based on whether or not the screen is expanded
 * and the [HomeUiState].
 */
@Composable
private fun getHomeScreenType(
    isExpandedScreen: Boolean,
    uiState: HomeUiState
): HomeScreenType = when (isExpandedScreen) {
    false -> {
        when (uiState) {
            is HomeUiState.HasPosts -> {
                if (uiState.isArticleOpen) {
                    ArticleDetails
                } else {
                    Feed
                }
            }
            is HomeUiState.NoPosts -> Feed
        }
    }
    true -> FeedWithArticleDetails
}
