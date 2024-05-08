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

@file:Suppress("Destructure", "DEPRECATION")

package com.example.jetnews.ui.home

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.ui.JetnewsApp
import com.example.jetnews.ui.JetnewsNavGraph
import com.example.jetnews.ui.article.postContentItems
import com.example.jetnews.ui.article.sharePost
import com.example.jetnews.ui.components.JetnewsSnackbarHost
import com.example.jetnews.ui.home.HomeScreenType.FeedWithArticleDetails
import com.example.jetnews.ui.modifiers.interceptKey
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.utils.BookmarkButton
import com.example.jetnews.ui.utils.FavoriteButton
import com.example.jetnews.ui.utils.ShareButton
import com.example.jetnews.ui.utils.TextSettingsButton
import com.example.jetnews.utils.ErrorMessage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

/**
 * The home screen displaying the feed along with an article details. Called by [HomeRoute] when the
 * [HomeScreenType] is [FeedWithArticleDetails] which occurs when `isExpandedScreen` is `true` which
 * happens when the [WindowWidthSizeClass] of the device we are running on is
 * [WindowWidthSizeClass.Expanded] (Represents the majority of tablets in landscape and large unfolded
 * inner displays in landscape). Our root Composable is a [HomeScreenWithList] whose `uiState` argument
 * is our [HomeUiState] parameter [uiState], whose `showTopAppBar` argument is our [Boolean] parameter
 * [showTopAppBar], whose `onRefreshPosts` argument is our lambda parameter [onRefreshPosts], whose
 * `onErrorDismiss` argument is our lambda parameter [onErrorDismiss], whose `openDrawer` argument is
 * our lambda parameter [openDrawer], whose `snackbarHostState` argument is our [SnackbarHostState]
 * parameter [snackbarHostState], and whose `modifier` argument is our [Modifier] parameter [modifier].
 * For the `hasPostsContent` lambda argument of [HomeScreenWithList] we pass a lambda which accepts
 * the [HomeUiState.HasPosts] passed the lambda in variable `hasPostsUiState`, the [PaddingValues]
 * passed the lambda in variable `contentPadding`, and the [Modifier] passed the lambda in variable
 * `contentModifier`. The the root composable of the lambda is a [Row] whose `modifier` argument is
 * the `contentModifier` passed the lambda. In the [RowScope] `content` lambda argument of the [Row]
 * we compose a [PostList] whose `postsFeed` argument is the [HomeUiState.HasPosts.postsFeed] property
 * of the `hasPostsUiState` passed to the `hasPostsContent` lambda, whose `favorites` argument is the
 * [HomeUiState.HasPosts.favorites] property of the `hasPostsUiState` passed to the `hasPostsContent`
 * lambda, whose `showExpandedSearch` argument is the inverse of our [Boolean] parameter [showTopAppBar],
 * whose `onArticleTapped` argument is our lambda parameter [onSelectPost], whose `onToggleFavorite`
 * argument is our lambda parameter [onToggleFavorite], whose `contentPadding` argument is the
 * [PaddingValues] `contentPadding` passed, whose `modifier` argument is a [Modifier.width] that sets
 * the width of 334.dp with a [Modifier.notifyInput] chained to that whose `block` argument is our
 * lambda parameter  [onInteractWithList], whose `state` argument is our [LazyListState] parameter
 * [homeListLazyListState], whose `searchInput` argument is the [HomeUiState.HasPosts.searchInput]
 * property of the `hasPostsUiState` passed, and whose `onSearchInputChanged` argument is our lambda
 * parameter [onSearchInputChanged]. Next to the [PostList] in the [Row] is a [Crossfade] whose
 * `targetState` argument is the [HomeUiState.HasPosts.selectedPost] property of the `hasPostsUiState`
 * passed, and whose `label` argument is "Crossfade". The `content` lambda argument of the [Crossfade]
 * accepts the [Post] passed the lambda as the variable `detailPost`, then initializes and remember
 * its [State] wrapped [LazyListState] using the [derivedStateOf] method with its `calculation` lambda
 * argument fetching the [LazyListState] in the [Map] of [String] to [LazyListState] parameter
 * [articleDetailLazyListStates] whose `key` is the [Post.id] of `detailPost`. We then [key] on the
 * [Post.id] of `detailPost` (to avoid sharing any state between different posts) and compose a
 * [LazyColumn] whose `state` argument is our [State] wrapped [LazyListState] variable `detailLazyListState`,
 * whose `contentPadding` argument is the [PaddingValues] variable `contentPadding` passed the
 * `hasPostsContent` lambda argument of [HomeScreenWithList], and whose `modifier` argument is a
 * [Modifier.padding] that adds 16.dp to both sides, with a [Modifier.fillMaxSize] chained to that
 * which makes it occupy its entire incoming size constraints, and this is followed by a
 * [Modifier.notifyInput] whose `block` lambda argument calls our lambda parameter [onInteractWithDetail]
 * with the [Post.id] of `detailPost`. In the [LazyListScope] lambda argument of the [LazyColumn] we
 * first compose a [LazyListScope.stickyHeader] in whose `content` lambda argument we initialize our
 * [Context] variable `val context` to the `current` [LocalContext] then compose a [PostTopBar] whose
 * `isFavorite` argument is the [Boolean] value returned by the [Set.contains] method of the [Set] of
 * [String] property of the [HomeUiState.HasPosts.favorites] property of `hasPostsUiState` for the
 * `element` of the [Post.id] of `detailPost`, whose `onToggleFavorite` argument is a lambda which
 * calls our [onToggleFavorite] lambda parameter with the [Post.id] of `detailPost`, whose `onSharePost`
 * argument is a lambda which calls our [sharePost] method with its `post` argument `detailPost`, and
 * with its `context` argument our [Context] variable `context`, and the `modifier` argument of the
 * [PostTopBar] is a [Modifier.fillMaxWidth] that causes it to occupy its entire incoming width
 * constraint to which is chained a [Modifier.wrapContentWidth] whose `align` argument of [Alignment.End]
 * causes it to align its content to the end. This is followed in the [LazyColumn] by our
 * [LazyListScope.postContentItems] composable which adds items to the [LazyColumn] constructed from
 * the [Post] `post` argument `detailPost` we pass it.
 *
 * @param uiState the current [HomeUiState]. It is collected using the [StateFlow.collectAsStateWithLifecycle]
 * extension function from the [StateFlow] of [HomeUiState] property [HomeViewModel.uiState] of the
 * [HomeViewModel] in the stateful override of [HomeRoute].
 * @param showTopAppBar this is `true` if `isExpandedScreen` is `false` ie. the [WindowWidthSizeClass]
 * of the device we are running on is not [WindowWidthSizeClass.Expanded]. When `true` the `topBar`
 * argument of the [Scaffold] in [HomeScreenWithList] will compose a [HomeTopAppBar] and the [PostList]
 * will compose a [HomeSearch] in its [LazyColumn] if it is `false`.
 * @param onToggleFavorite a lambda that we can call with the [Post.id] property of the [Post] that
 * the user wishes to "toggle" the "favorite" status of. The call to the stateless override of
 * [HomeRoute] in the stateful override of [HomeRoute] passes down a lambda which calls the
 * [HomeViewModel.toggleFavourite] method with the [String] passed the lambda.
 * @param onSelectPost a lambda that we can call with the [Post.id] property of the [Post] that the
 * user wishes to "select". The call to the stateless override of [HomeRoute] in the stateful override
 * of [HomeRoute] passes down a lambda which calls the [HomeViewModel.selectArticle] method with the
 * [String] passed the lambda.
 * @param onRefreshPosts a lambda we can call when we wish the apps [List] of [Post] to be "refreshed".
 * This is passed down to the `onClick` argument of a [TextButton] labeled "Tap to load content" which
 * appears when the [HomeUiState] is a [HomeUiState.NoPosts] but there are no errors, and it is also
 * passed to a `SwipeRefresh` in [LoadingContent] as its `onRefresh` argument. The call to the stateless
 * override of [HomeRoute] in the stateful override of [HomeRoute] passes down a lambda which calls
 * the [HomeViewModel.refreshPosts] method.
 * @param onErrorDismiss a lambda we should call with the [ErrorMessage.id] of the [ErrorMessage]
 * that has been displayed and dismissed in order to notify the [HomeViewModel]. The call to the stateless
 * override of [HomeRoute] in the stateful override of [HomeRoute] passes down a lambda which calls
 * the [HomeViewModel.errorShown] method with the [Long] passed the lambda.
 * @param openDrawer a lambda we can call when we wish to open the navigation drawer. The call to
 * [JetnewsNavGraph] in [JetnewsApp] passes down a lambda which calls the [CoroutineScope.launch]
 * method to launch a coroutine that calls the [DrawerState.open] method of the `drawerState` of the
 * [ModalNavigationDrawer] to open the drawer.
 * @param snackbarHostState the [SnackbarHostState] that is used as the `hostState` of the
 * [JetnewsSnackbarHost] that is used as the `snackbarHost` argument of the [Scaffold] in
 * [HomeScreenWithList]. It is used for its [SnackbarHostState.showSnackbar] method to show a
 * [Snackbar], and it is called in a [LaunchedEffect] of the [HomeScreenWithList] Composable whenever
 * the current [HomeUiState.errorMessages] property is not empty.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [HomeRoute] does not pass us one so the empty, default, or starter [Modifier]
 * that contains no elements is used.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeFeedWithArticleDetailsScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onInteractWithList: () -> Unit,
    onInteractWithDetail: (String) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    articleDetailLazyListStates: Map<String, LazyListState>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onSearchInputChanged: (String) -> Unit,
) {
    HomeScreenWithList(
        uiState = uiState,
        showTopAppBar = showTopAppBar,
        onRefreshPosts = onRefreshPosts,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { hasPostsUiState: HomeUiState.HasPosts, contentPadding: PaddingValues, contentModifier: Modifier ->
        Row(modifier = contentModifier) {
            PostList(
                postsFeed = hasPostsUiState.postsFeed,
                favorites = hasPostsUiState.favorites,
                showExpandedSearch = !showTopAppBar,
                onArticleTapped = onSelectPost,
                onToggleFavorite = onToggleFavorite,
                contentPadding = contentPadding,
                modifier = Modifier
                    .width(width = 334.dp)
                    .notifyInput(block = onInteractWithList),
                state = homeListLazyListState,
                searchInput = hasPostsUiState.searchInput,
                onSearchInputChanged = onSearchInputChanged,
            )
            // Crossfade between different detail posts
            Crossfade(
                targetState = hasPostsUiState.selectedPost,
                label = "Crossfade"
            ) { detailPost: Post ->
                // Get the lazy list state for this detail view
                val detailLazyListState: LazyListState by remember {
                    derivedStateOf {
                        articleDetailLazyListStates.getValue(key = detailPost.id)
                    }
                }

                // Key against the post id to avoid sharing any state between different posts
                key(detailPost.id) {
                    LazyColumn(
                        state = detailLazyListState,
                        contentPadding = contentPadding,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize()
                            .notifyInput {
                                onInteractWithDetail(detailPost.id)
                            }
                    ) {
                        stickyHeader {
                            val context: Context = LocalContext.current
                            PostTopBar(
                                isFavorite = hasPostsUiState.favorites.contains(element = detailPost.id),
                                onToggleFavorite = { onToggleFavorite(detailPost.id) },
                                onSharePost = { sharePost(post = detailPost, context = context) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(align = Alignment.End)
                            )
                        }
                        postContentItems(post = detailPost)
                    }
                }
            }
        }
    }
}

/**
 * A [Modifier] that tracks all input, and calls [block] every time input is received. We initialize
 * and remember our [State] wrapped lambda variable `val blockState` to our lambda parameter [block].
 * Then we chain a [Modifier.pointerInput] to our [Modifier] receiver keyed to [Unit] and in its
 * [PointerInputScope] lambda argument we loop while the [CoroutineContext.isActive] property of
 * the current [CoroutineContext] that [currentCoroutineContext] returns is `true`. In the loop we
 * call [PointerInputScope.awaitPointerEventScope] to suspend and install a pointer input [block]
 * that will await input events and resume when our [block] returns. In that [block] we call
 * [AwaitPointerEventScope.awaitPointerEvent] to suspend until a [PointerEvent] is reported to the
 * input [PointerEventPass.Initial] (Allows ancestors to consume aspects of PointerInputChange before
 * descendants) and when that happens we call the [State.value] of our [State] wrapped lambda variable
 * `blockState` to execute our lambda parameter [block].
 */
private fun Modifier.notifyInput(block: () -> Unit): Modifier =
    this then composed {
        val blockState: State<() -> Unit> = rememberUpdatedState(newValue = block)
        pointerInput(key1 = Unit) {
            while (currentCoroutineContext().isActive) {
                awaitPointerEventScope {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                    blockState.value()
                }
            }
        }
    }

/**
 * The home screen displaying just the article feed. This is called by [HomeRoute] when the
 * [HomeScreenType] is [HomeScreenType.Feed] which happens when `isExpandedScreen` is `false` (ie.
 * a phone instead of a tablet) and the [HomeUiState] is a [HomeUiState.NoPosts] or it is a
 * [HomeUiState.HasPosts] but [HomeUiState.HasPosts.isArticleOpen] is `false`. Our root composable
 * is a [HomeScreenWithList] whose `uiState` argument is our [HomeUiState] parameter [uiState],
 * whose `showTopAppBar` argument is our [Boolean] parameter [showTopAppBar], whose `onRefreshPosts`
 * argument is our lambda parameter [onRefreshPosts], whose `onErrorDismiss` argument is our lambda
 * parameter [onErrorDismiss], whose `openDrawer` argument is our lambda parameter [openDrawer],
 * whose `snackbarHostState` argument is our [SnackbarHostState] parameter [snackbarHostState], and
 * whose `modifier` argument is our [Modifier] parameter [modifier]. Then in the `hasPostsContent`
 * lambda argument of [HomeScreenWithList] we accept the [HomeUiState.HasPosts] passed the lambda as
 * variable `hasPostsUiState`, the [PaddingValues] passed as variable `contentPadding` and the
 * [Modifier] passed as variable `contentModifier`. Then we compose a [PostList] whose `postsFeed`
 * argument is the [HomeUiState.HasPosts.postsFeed] of `hasPostsUiState`, whose `favorites` argument
 * argument is the [HomeUiState.HasPosts.favorites] of `hasPostsUiState`, whose `showExpandedSearch`
 * argument is the inverse of our [Boolean] parameter [showTopAppBar] (which is always `true` so we
 * pass `false`), whose `onArticleTapped` argument is our lambda parameter [onSelectPost], whose
 * `onToggleFavorite` argument is our lambda parameter [onToggleFavorite], whose `contentPadding`
 * argument is the [PaddingValues] passed our lambda that we accepted as `contentPadding`, whose
 * `modifier` argument is the [Modifier] passed our lambda that we accepted as `contentModifier`,
 * whose `state` argument is our [LazyListState] parameter [homeListLazyListState], whose `searchInput`
 * argument is our [String] parameter [searchInput], and whose `onSearchInputChanged` argument is
 * our lambda parameter [onSearchInputChanged].
 *
 * @param uiState the current [HomeUiState] of the app.
 * @param showTopAppBar if `true` [HomeScreenWithList] should display a [HomeTopAppBar] as the
 * `topBar` of its [Scaffold], and [PostList] should _not_ show a [HomeSearch]. Our caller
 * [HomeRoute] calls us with the inverse of `isExpandedScreen` and since `isExpandedScreen` is
 * `false` when we are chosen to be composed [showTopAppBar] is always `true`.
 * @param onToggleFavorite a lambda which can be called with the [Post.id] of a [Post] to toggle its
 * "favorite" status.
 * @param onSelectPost a lambda that we can call with the [Post.id] of the [Post] that we wish to
 * "select" to view more information about. Sets the [HomeUiState.HasPosts.selectedPost] property
 * to that [Post] (eventually).
 * @param onRefreshPosts a lambda we can call when we want the [HomeViewModel] to refresh its [List]
 * of [Post].
 * @param onErrorDismiss a lambda we should call with an [ErrorMessage.id] to "dismiss" an error
 * [Snackbar] that was shown for that [ErrorMessage]. The lambda that is passed down to us from the
 * stateful [HomeRoute] calls the [HomeViewModel.errorShown] method with the [Long] passed to the
 * lambda
 * @param openDrawer a lambda that we can call to open the [ModalNavigationDrawer] of the app.
 * @param homeListLazyListState the [LazyListState] that the [LazyColumn] in [PostList] should as
 * its `state` argument.
 * @param snackbarHostState the [SnackbarHostState] that is used as the `hostState` of the
 * [JetnewsSnackbarHost] that is used as the `snackbarHost` argument of the [Scaffold] in
 * [HomeScreenWithList]. It is used for its [SnackbarHostState.showSnackbar] method to show a
 * [Snackbar], and it is called in a [LaunchedEffect] of the [HomeScreenWithList] Composable whenever
 * the current [HomeUiState.errorMessages] property is not empty.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller the stateless [HomeRoute] does not pass one so the empty, default, or starter
 * [Modifier] that contains no elements is used instead.
 * @param searchInput passed as the `searchInput` argument of [HomeSearch] in our [PostList] composable
 * it is always the empty [String] (search is not implemented yet in this configuration).
 * @param onSearchInputChanged a lambda that can be called by [HomeSearch] when the search input
 * changes. The stateful [HomeRoute] passes down a lambda that calls [HomeViewModel.onSearchInputChanged]
 * method with the [String] passed the lambda and the method updates the [HomeUiState.HasPosts.searchInput]
 * property with it (which is not read in this configuration).
 */
@Composable
fun HomeFeedScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit,
) {
    HomeScreenWithList(
        uiState = uiState,
        showTopAppBar = showTopAppBar,
        onRefreshPosts = onRefreshPosts,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    ) { hasPostsUiState: HomeUiState.HasPosts, contentPadding: PaddingValues, contentModifier: Modifier ->
        PostList(
            postsFeed = hasPostsUiState.postsFeed,
            favorites = hasPostsUiState.favorites,
            showExpandedSearch = !showTopAppBar,
            onArticleTapped = onSelectPost,
            onToggleFavorite = onToggleFavorite,
            contentPadding = contentPadding,
            modifier = contentModifier,
            state = homeListLazyListState,
            searchInput = searchInput,
            onSearchInputChanged = onSearchInputChanged
        )
    }
}

/**
 * A Composable that displays its [hasPostsContent] lambda parameter if its [HomeUiState] parameter
 * [uiState] is a [HomeUiState.HasPosts] or displays a [TextButton] labeled "Tap to load content"
 * that calls its [onRefreshPosts] lambda parameter when clicked to have the [HomeViewModel] reload
 * its dataset if it is a [HomeUiState.NoPosts]. It sets up a scaffold with the top app bar, and
 * surrounds the [hasPostsContent] with refresh, loading and error handling. This is essentially a
 * helper function that is used by both [HomeFeedWithArticleDetailsScreen] and [HomeFeedScreen]
 * because they are  extremely similar, except for the rendered content when there are posts to
 * display. We start by initializing and remembering our [TopAppBarState] variable `val topAppBarState`
 * to a new instance. Then we initialize our [TopAppBarScrollBehavior] variable `val scrollBehavior`
 * to a [TopAppBarDefaults.pinnedScrollBehavior] whose `state` argument is `topAppBarState`.
 * Our root Composable is a [Scaffold] whose `snackbarHost` argument is a lambda which composes a
 * [JetnewsSnackbarHost] whose `hostState` argument is our [SnackbarHostState] parameter
 * [snackbarHostState], whose `topBar` argument is if our [Boolean] parameter [showTopAppBar] is
 * `true` a [HomeTopAppBar] whose `openDrawer` argument is our lambda parameter [openDrawer], and
 * whose `topAppBarState` argument is our [TopAppBarState] variable `topAppBarState`. The `modifier`
 * argument of the [Scaffold] is our [Modifier] parameter [modifier].
 *
 * The `content` lambda of the [Scaffold] accepts the [PaddingValues] passed it in the variable
 * `innerPadding`. It initializes its [Modifier] variable `val contentModifier` to a
 * [Modifier.nestedScroll] whose `connection` argument is the [TopAppBarScrollBehavior.nestedScrollConnection]
 * of our [TopAppBarScrollBehavior] variable `scrollBehavior`. Then it composes a [LoadingContent]
 * whose [Boolean] argument `empty` is `false` when [HomeUiState] parameter [uiState] is a
 * [HomeUiState.HasPosts] or the value of the [HomeUiState.isLoading] property if [uiState] is a
 * [HomeUiState.NoPosts], whose `emptyContent` argument is a lambda which composes a [FullScreenLoading]
 * Composable, whose `loading` argument is the [HomeUiState.isLoading] property of our [HomeUiState]
 * parameter [uiState], whose `onRefresh` argument is our lambda parameter [onRefreshPosts].
 *
 * The `content` of the [LoadingContent] is when [HomeUiState] parameter [uiState] is a
 * [HomeUiState.HasPosts] just a call to our [hasPostsContent] lambda parameter with its
 * [HomeUiState] argument our [HomeUiState] parameter [uiState], with its [PaddingValues] argument
 * the [PaddingValues] passed by [Scaffold] to its `content` which we accepted as our `innerPadding`
 * variable, and with its [Modifier] argument the [Modifier.nestedScroll] we initialized our variable
 * `contentModifier` to. On the other hand if our [HomeUiState] parameter [uiState] is a
 * [HomeUiState.NoPosts] we check if the [List] of [ErrorMessage] in the [HomeUiState.errorMessages]
 * of [uiState] is empty and if so we compose a [TextButton] with its `onClick` argument our lambda
 * parameter [onRefreshPosts], and with its `modifier` argument our [Modifier] parameter [modifier]
 * with a [Modifier.padding] that adds the `paddingValues` that the [Scaffold] passed to our
 * variable `innerPadding`, and with [Modifier.fillMaxSize] added to that to have the [TextButton]
 * occupy its entire incoming size constraints. The `content` of the [TextButton] is a [Text] that
 * displays the `text` whose resource ID is [R.string.home_tap_to_load_content] ("Tap to load content"),
 * and whose `textAlign` is [TextAlign.Center] to center the `text`. If on the other hand the [List]
 * of [ErrorMessage] in the [HomeUiState.errorMessages] of [uiState] is not empty we compose a [Box]
 * whose `modifier` argument chains to our [Modifier] variable `contentModifier` a [Modifier.padding]
 * that adds our [PaddingValues] variable `innerPadding` as padding, and then chains a
 * [Modifier.fillMaxSize] to that to have it occupy its entire incoming constraits.
 *
 * When done composing our [Scaffold] we check if the [List] of [ErrorMessage] in the
 * [HomeUiState.errorMessages] property of [uiState] is not empty, and if so we initialize and
 * remember keyed on [uiState] our [ErrorMessage] variable `val errorMessage` to the [ErrorMessage]
 * at index 0 in the [HomeUiState.errorMessages] of [uiState]. We initialize our [String] variable
 * `val errorMessageText` to the [String] whose resource ID is the [ErrorMessage.messageId] property
 * of `errorMessage`, and our [String] variable `val retryMessageText` to the [String] with resource
 * ID [R.string.retry] ("Retry"). We use [rememberUpdatedState] to initialize our lambda variable
 * `val onRefreshPostsState` using our lambda parameter [onRefreshPosts] as the `newValue` argument
 * of [rememberUpdatedState] so that it will be updated to the latest value of [onRefreshPosts] should
 * it change, and do the same for our lambda taking a [Long] variable `val onErrorDismissState` with
 * our lambda parameter [onErrorDismiss] as the `newValue` argument of [rememberUpdatedState]. Then
 * we compose a  [LaunchedEffect] keyed on `errorMessageText`, `retryMessageText`, and our
 * [SnackbarHostState] parameter [snackbarHostState] so that if there is a change to any of these
 * any previous effect will be canceled and re-launched with the new values. In the [CoroutineScope]
 * `block` argument of the [LaunchedEffect] we initialize our [SnackbarResult] variable `val snackbarResult`
 * to the value returned when we call the [SnackbarHostState.showSnackbar] method of our
 * [SnackbarHostState] parameter [snackbarHostState] when called with its `message` argument our
 * [String] variable `errorMessageText`, and its `actionLabel` argument our [String] variable
 * `retryMessageText` to have it show a [Snackbar] displaying these [String]'s. Then if our
 * [SnackbarResult] variable `snackbarResult` is [SnackbarResult.ActionPerformed] we call our
 * lambda variable `onRefreshPostsState`. Once the message is displayed and dismissed we call our
 * lambda of [Long] variable `onErrorDismissState` with the [ErrorMessage.id] of `errorMessage`
 * to notify the [HomeViewModel].
 *
 * @param uiState the current [HomeUiState] of the app.
 * @param showTopAppBar if `true` we display a [HomeTopAppBar] as the topBar` of our [Scaffold].
 * This is `true` when we are called by [HomeFeedScreen], and `false` when we are called by
 * [HomeFeedWithArticleDetailsScreen].
 * @param onRefreshPosts a lambda we can call when we want the [HomeViewModel] to refresh its [List]
 * of [Post].
 * @param onErrorDismiss a lambda we should call with an [ErrorMessage.id] to "dismiss" an error
 * [Snackbar] that was shown for that [ErrorMessage]. The lambda that is passed down to us from the
 * stateful [HomeRoute] calls the [HomeViewModel.errorShown] method with the [Long] passed to the
 * lambda.
 * @param openDrawer  a lambda that we can call to open the [ModalNavigationDrawer] of the app.
 * @param snackbarHostState the [SnackbarHostState] that is used as the `hostState` of the
 * [JetnewsSnackbarHost] that is used as the `snackbarHost` argument of our [Scaffold]. It is used
 * for its [SnackbarHostState.showSnackbar] method to show a [Snackbar], and it is called in our
 * [LaunchedEffect] whenever the current [HomeUiState.errorMessages] property is not empty.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our callers just pass us the empty, default, or starter [Modifier] that their own
 * `modifier` parameter defaults to.
 * @param hasPostsContent a Composable lambda that will be composed by [LoadingContent] as its
 * `content` argument when our [HomeUiState] parameter [uiState] is a [HomeUiState.HasPosts].
 * It is called with our [HomeUiState] parameter [uiState], the [PaddingValues] passed the `content`
 * lambda of the [Scaffold] that [LoadingContent] is in, and the [Modifier.nestedScroll] we create
 * whose `connection` argument is the [TopAppBarScrollBehavior.nestedScrollConnection] of the
 * [TopAppBarDefaults.pinnedScrollBehavior] we create from the [TopAppBarState] we use for our
 * [HomeTopAppBar] (when our [Boolean] parameter [showTopAppBar] is `true`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenWithList(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    hasPostsContent: @Composable (
        uiState: HomeUiState.HasPosts,
        contentPadding: PaddingValues,
        modifier: Modifier
    ) -> Unit
) {
    val topAppBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(state = topAppBarState)
    Scaffold(
        snackbarHost = { JetnewsSnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (showTopAppBar) {
                HomeTopAppBar(
                    openDrawer = openDrawer,
                    topAppBarState = topAppBarState
                )
            }
        },
        modifier = modifier
    ) { innerPadding: PaddingValues ->
        val contentModifier =
            Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection)

        LoadingContent(
            empty = when (uiState) {
                is HomeUiState.HasPosts -> false
                is HomeUiState.NoPosts -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = onRefreshPosts,
            content = {
                when (uiState) {
                    is HomeUiState.HasPosts ->
                        hasPostsContent(uiState, innerPadding, contentModifier)

                    is HomeUiState.NoPosts -> {
                        if (uiState.errorMessages.isEmpty()) {
                            // if there are no posts, and no error, let the user refresh manually
                            TextButton(
                                onClick = onRefreshPosts,
                                modifier = modifier
                                    .padding(paddingValues = innerPadding)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.home_tap_to_load_content),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // there's currently an error showing, don't show any content
                            Box(
                                modifier = contentModifier
                                    .padding(innerPadding)
                                    .fillMaxSize()
                            ) { /* empty screen */ }
                        }
                    }
                }
            }
        )
    }

    // Process one error message at a time and show them as Snackbars in the UI
    if (uiState.errorMessages.isNotEmpty()) {
        // Remember the errorMessage to display on the screen
        val errorMessage: ErrorMessage = remember(uiState) { uiState.errorMessages[0] }

        // Get the text to show on the message from resources
        val errorMessageText: String = stringResource(errorMessage.messageId)
        val retryMessageText: String = stringResource(id = R.string.retry)

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState: () -> Unit by rememberUpdatedState(newValue = onRefreshPosts)
        val onErrorDismissState: (Long) -> Unit by rememberUpdatedState(newValue = onErrorDismiss)

        // Effect running in a coroutine that displays the Snackbar on the screen
        // If there's a change to errorMessageText, retryMessageText or snackbarHostState,
        // the previous effect will be cancelled and a new one will start with the new values
        LaunchedEffect(key1 = errorMessageText, key2 = retryMessageText, key3 = snackbarHostState) {
            val snackbarResult: SnackbarResult = snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }
}

/**
 * Display an initial empty state or swipe to refresh content. If our [Boolean] parameter [empty] is
 * `true` we compose our [emptyContent] composable lambda, otherwise we compose a [SwipeRefresh]
 * whose state argument is a remembered [SwipeRefreshState] whose `isRefreshing` argument is our
 * [Boolean] parameter [loading], whose `onRefresh` argument  is our lambda parameter [onRefresh],
 * (Lambda which is invoked when a swipe to refresh gesture is completed) and whose `content` argument
 * is our Composable lambda parameter [content] (the main content to show).
 *
 * @param empty (state) when true, display [emptyContent]
 * @param emptyContent (slot) the content to display for the empty state
 * @param loading (state) when true, display a loading spinner over [content]
 * @param onRefresh (event) event to request refresh
 * @param content (slot) the main content to show
 */
@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        @Suppress("DEPRECATION") // TODO: Switch to Modifier.pullRefresh(). See https://google.github.io/accompanist/swiperefresh/#migration
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = loading),
            onRefresh = onRefresh,
            content = content,
        )
    }
}

/**
 * Display a feed of posts. When a post is clicked on, [onArticleTapped] will be called with the
 * [Post.id] of the [Post].
 *
 * @param postsFeed (state) the [PostsFeed] to display found in the [HomeUiState.HasPosts.postsFeed]
 * property of the current [HomeUiState].
 * @param favorites (state) the [Set] of [String] found in the [HomeUiState.HasPosts.favorites] of
 * the current [HomeUiState], the [String] is the [Post.id] of a [Post] that is a "favorite".
 * @param showExpandedSearch (state) if `true` we should compose a [HomeSearch] as the top item of
 * our [LazyColumn].
 * @param onArticleTapped (event) request navigation to Article screen to display the [Post] whose
 * [Post.id] is the `postId` passed the lambda.
 * @param onToggleFavorite (event) lambda that can be called with the [Post.id] of a [Post] whose
 * presence in the [favorites] we wish to toggle.
 * @param modifier modifier for the [LazyColumn] root element. Our when [HomeFeedScreen] calls
 * [HomeScreenWithList] it passes down the empty, default, or starter [Modifier] that contains no
 * elements, while when [HomeFeedWithArticleDetailsScreen] calls [HomeScreenWithList] passes down a
 * [Modifier.width] that sets the width of the [LazyColumn] to 334.dp with a [Modifier.notifyInput]
 * chained to that ends up calling the [HomeViewModel.interactedWithArticleDetails] method with the
 * [Post.id] of the [Post] that has been selected.
 * @param contentPadding (state) the [PaddingValues] to use as the `contentPadding` argument of
 * our [LazyColumn] (a padding around the whole content of the [LazyColumn])
 * @param state (state) the [LazyListState] that our [LazyColumn] should use.
 * @param searchInput (state) the current search [String] of our [HomeSearch] composable.
 * @param onSearchInputChanged (event) a lambda that [HomeSearch] can call when it wants to update
 * the [searchInput] to the [String] passed the lambda.
 */
@Composable
private fun PostList(
    postsFeed: PostsFeed,
    favorites: Set<String>,
    showExpandedSearch: Boolean,
    onArticleTapped: (postId: String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        state = state
    ) {
        if (showExpandedSearch) {
            item {
                HomeSearch(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    searchInput = searchInput,
                    onSearchInputChanged = onSearchInputChanged,
                )
            }
        }
        item {
            PostListTopSection(
                post = postsFeed.highlightedPost,
                navigateToArticle = onArticleTapped
            )
        }
        if (postsFeed.recommendedPosts.isNotEmpty()) {
            item {
                PostListSimpleSection(
                    posts = postsFeed.recommendedPosts,
                    navigateToArticle = onArticleTapped,
                    favorites = favorites,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
        if (postsFeed.popularPosts.isNotEmpty() && !showExpandedSearch) {
            item {
                PostListPopularSection(
                    posts = postsFeed.popularPosts,
                    navigateToArticle = onArticleTapped
                )
            }
        }
        if (postsFeed.recentPosts.isNotEmpty()) {
            item {
                PostListHistorySection(
                    posts = postsFeed.recentPosts,
                    navigateToArticle = onArticleTapped
                )
            }
        }
    }
}

/**
 * Full screen circular progress indicator
 */
@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Top section of [PostList]
 *
 * @param post (state) highlighted post to display
 * @param navigateToArticle (event) request navigation to Article screen
 */
@Composable
private fun PostListTopSection(post: Post, navigateToArticle: (String) -> Unit) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = stringResource(id = R.string.home_top_section_title),
        style = MaterialTheme.typography.titleMedium
    )
    PostCardTop(
        post = post,
        modifier = Modifier.clickable(onClick = { navigateToArticle(post.id) })
    )
    PostListDivider()
}

/**
 * Full-width list items for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToArticle (event) request navigation to Article screen
 */
@Composable
private fun PostListSimpleSection(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit
) {
    Column {
        posts.forEach { post ->
            PostCardSimple(
                post = post,
                navigateToArticle = navigateToArticle,
                isFavorite = favorites.contains(element = post.id),
                onToggleFavorite = { onToggleFavorite(post.id) }
            )
            PostListDivider()
        }
    }
}

/**
 * Horizontal scrolling cards for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToArticle (event) request navigation to Article screen
 */
@Composable
private fun PostListPopularSection(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(all = 16.dp),
            text = stringResource(id = R.string.home_popular_section_title),
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier
                .horizontalScroll(state = rememberScrollState())
                .height(intrinsicSize = IntrinsicSize.Max)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            for (post in posts) {
                PostCardPopular(
                    post = post,
                    navigateToArticle = navigateToArticle
                )
            }
        }
        Spacer(Modifier.height(height = 16.dp))
        PostListDivider()
    }
}

/**
 * Full-width list items that display "based on your history" for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToArticle (event) request navigation to Article screen
 */
@Composable
private fun PostListHistorySection(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit
) {
    Column {
        posts.forEach { post ->
            PostCardHistory(post = post, navigateToArticle = navigateToArticle)
            PostListDivider()
        }
    }
}

/**
 * Full-width divider with padding for [PostList]
 */
@Composable
private fun PostListDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    )
}

/**
 * Expanded search UI - includes support for enter-to-send on the search field
 */
@Composable
private fun HomeSearch(
    modifier: Modifier = Modifier,
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit,
) {
    val context: Context = LocalContext.current
    val focusManager: FocusManager = LocalFocusManager.current
    val keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = searchInput,
        onValueChange = onSearchInputChanged,
        placeholder = { Text(text = stringResource(id = R.string.home_search)) },
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
        modifier = modifier
            .fillMaxWidth()
            .interceptKey(key = Key.Enter) {
                // submit a search query when Enter is pressed
                submitSearch(onSearchInputChanged = onSearchInputChanged, context = context)
                keyboardController?.hide()
                focusManager.clearFocus(force = true)
            },
        singleLine = true,
        // keyboardOptions change the newline key to a search key on the soft keyboard
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        // keyboardActions submits the search query when the search key is pressed
        keyboardActions = KeyboardActions(
            onSearch = {
                submitSearch(onSearchInputChanged = onSearchInputChanged, context = context)
                keyboardController?.hide()
            }
        )
    )
}

/**
 * Stub helper function to submit a user's search query
 */
private fun submitSearch(
    onSearchInputChanged: (String) -> Unit,
    context: Context
) {
    onSearchInputChanged("")
    Toast.makeText(
        /* context = */ context,
        /* text = */ "Search is not yet implemented",
        /* duration = */ Toast.LENGTH_SHORT
    ).show()
}

/**
 * Top bar for a Post when displayed next to the Home feed
 */
@Composable
fun PostTopBar(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSharePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(size = 8.dp),
        border = BorderStroke(
            width = Dp.Hairline,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
        ),
        modifier = modifier.padding(end = 16.dp)
    ) {
        Row(Modifier.padding(horizontal = 8.dp)) {
            FavoriteButton(onClick = { /* Functionality not available */ })
            BookmarkButton(isBookmarked = isFavorite, onClick = onToggleFavorite)
            ShareButton(onClick = onSharePost)
            TextSettingsButton(onClick = { /* Functionality not available */ })
        }
    }
}

/**
 * TopAppBar for the Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(state = topAppBarState)
) {
    val context = LocalContext.current
    val title = stringResource(id = R.string.app_name)
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.ic_jetnews_wordmark),
                contentDescription = title,
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_jetnews_logo),
                    contentDescription = stringResource(id = R.string.cd_open_navigation_drawer),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = {
                Toast.makeText(
                    /* context = */ context,
                    /* text = */ "Search is not yet implemented in this configuration",
                    /* duration = */ Toast.LENGTH_LONG
                ).show()
            }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.cd_search)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

/**
 * TODO: Add kdoc
 */
@Preview("Home list drawer screen")
@Preview("Home list drawer screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Preview("Home list drawer screen (big font)", fontScale = 1.5f)
@Composable
fun PreviewHomeListDrawerScreen() {
    val postsFeed: PostsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                selectedPost = postsFeed.highlightedPost,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = ""
            ),
            showTopAppBar = false,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {}
        )
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(name = "Home list navrail screen", device = Devices.NEXUS_7_2013)
@Preview(
    name = "Home list navrail screen (dark)",
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.NEXUS_7_2013
)
@Preview(name = "Home list navrail screen (big font)", fontScale = 1.5f, device = Devices.NEXUS_7_2013)
@Composable
fun PreviewHomeListNavRailScreen() {
    val postsFeed: PostsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                selectedPost = postsFeed.highlightedPost,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = ""
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {}
        )
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(name = "Home list detail screen", device = Devices.PIXEL_C)
@Preview(name = "Home list detail screen (dark)", uiMode = UI_MODE_NIGHT_YES, device = Devices.PIXEL_C)
@Preview(name = "Home list detail screen (big font)", fontScale = 1.5f, device = Devices.PIXEL_C)
@Composable
fun PreviewHomeListDetailScreen() {
    val postsFeed: PostsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetnewsTheme {
        HomeFeedWithArticleDetailsScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                selectedPost = postsFeed.highlightedPost,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = ""
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            onInteractWithList = {},
            onInteractWithDetail = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            articleDetailLazyListStates = postsFeed.allPosts.associate { post: Post ->
                key(post.id) {
                    post.id to rememberLazyListState()
                }
            },
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {}
        )
    }
}
