/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetnews.ui.article

import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.model.Post
import com.example.jetnews.model.Publication
import com.example.jetnews.ui.home.HomeScreenType
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.utils.BookmarkButton
import com.example.jetnews.ui.utils.FavoriteButton
import com.example.jetnews.ui.utils.ShareButton
import com.example.jetnews.ui.utils.TextSettingsButton
import kotlinx.coroutines.runBlocking

/**
 * Stateless Article Screen that displays a single post adapting the UI to different screen sizes.
 * Composed by [HomeRoute] when the [HomeScreenType] is [HomeScreenType.ArticleDetails]. We start
 * by initializing and remembering our [MutableState] wrapped [Boolean] variable
 * `var showUnimplementedActionDialog` to `false`. Then if on this recomposition it is `true`
 * we compose our [FunctionalityNotAvailablePopup] with its `onDismiss` lambda argument a lambda
 * which sets `showUnimplementedActionDialog` to `false` (this is used as the lambda that the
 * [AlertDialog] of [FunctionalityNotAvailablePopup] should call to dismiss itself). Then our root
 * Composable is a [Row] whose `modifier` argument chains a [Modifier.fillMaxSize] to our [Modifier]
 * parameter [modifier]. Inside the `content` [RowScope] lambda argument of [Row] we start by
 * initializing our [Context] variable `val context` to the `current` [LocalContext] and then we
 * compose an [ArticleScreenContent] Composable whose `post` is our [Post] parameter [post], whose
 * `navigationIconContent` lambda argument is a lambda which if our [Boolean] parameter
 * [isExpandedScreen] is `false` will compose an [IconButton] whose `onClick` argument is our
 * lambda parameter [onBack] and whose `content` is an [Icon] whose `imageVector` argument causes
 * it to display the [ImageVector] drawn by [Icons.AutoMirrored.Filled.ArrowBack], whose
 * `contentDescription` is the [String] with resource ID [R.string.cd_navigate_up] "Navigate up",
 * and whose `tint` is the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme]. The
 * `bottomBarContent` lambda argument of [ArticleScreenContent] is a lambda which if our [Boolean]
 * parameter [isExpandedScreen] is `false` composes a [BottomAppBar] Composable whose `actions`
 * [RowScope] lambda parameter composes:
 *  - a [FavoriteButton] whose `onClick` lambda argument is a lambda which sets our [MutableState]
 *  wrapped [Boolean] variable `showUnimplementedActionDialog` to `true` which causes our
 *  [FunctionalityNotAvailablePopup] to pop up its "Functionality not available" [AlertDialog].
 *  - a [BookmarkButton] whose `isBookmarked` argument is our [Boolean] parameter [isFavorite], and
 *  whose `onClick` lambda argument is our lambda parameter [onToggleFavorite].
 *  - a [ShareButton] whose `lambda` argument is a lambda which calls our [sharePost] method with
 *  its `post` argument our [Post] parameter [post] and its `context` argument our [Context] variable
 *  `context` to have it launch a chooser to allow the user to share share our [Post] parameter [post].
 *  - a [TextSettingsButton] whose `onClick` lambda argument is a lambda which sets our [MutableState]
 *  wrapped [Boolean] variable `showUnimplementedActionDialog` to `true` which causes our
 *  [FunctionalityNotAvailablePopup] to pop up its "Functionality not available" [AlertDialog].
 *
 * The `lazyListState` argument of the [ArticleScreenContent] is our [LazyListState] parameter
 * [lazyListState].
 *
 * @param post (state) item to display
 * @param isExpandedScreen (state) whether the screen is expanded
 * @param onBack (event) request navigate back
 * @param isFavorite (state) is this item currently a favorite
 * @param onToggleFavorite (event) request that this post toggle it's favorite state
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [HomeRoute] passes us none so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 * @param lazyListState (state) the [LazyListState] for the article content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    post: Post,
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    var showUnimplementedActionDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    if (showUnimplementedActionDialog) {
        FunctionalityNotAvailablePopup { showUnimplementedActionDialog = false }
    }

    Row(modifier = modifier.fillMaxSize()) {
        val context: Context = LocalContext.current
        ArticleScreenContent(
            post = post,
            // Allow opening the Drawer if the screen is not expanded
            navigationIconContent = {
                if (!isExpandedScreen) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_navigate_up),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            // Show the bottom bar if the screen is not expanded
            bottomBarContent = {
                if (!isExpandedScreen) {
                    BottomAppBar(
                        actions = {
                            FavoriteButton(onClick = { showUnimplementedActionDialog = true })
                            BookmarkButton(isBookmarked = isFavorite, onClick = onToggleFavorite)
                            ShareButton(onClick = { sharePost(post = post, context = context) })
                            TextSettingsButton(onClick = { showUnimplementedActionDialog = true })
                        }
                    )
                }
            },
            lazyListState = lazyListState
        )
    }
}

/**
 * Stateless Article Screen that displays a single post. We start by initializing and remembering
 * our [TopAppBarState] variable `val topAppBarState` to a new instance and initializing our
 * [TopAppBarScrollBehavior] variable `val scrollBehavior` to the [TopAppBarScrollBehavior] returned
 * by [TopAppBarDefaults.enterAlwaysScrollBehavior] with its `state` argument our [TopAppBarState]
 * variable `topAppBarState` (a top app bar that is set up with this [TopAppBarScrollBehavior] will
 * immediately collapse when the content is pulled up, and will immediately appear when the content
 * is pulled down). Our root Composable is a [Scaffold] whose `topBar` lambda argument is a lambda
 * which composes a [TopAppBar] whose `title` argument is the [Publication.name] of the
 * [Post.publication] of our [Post] parameter [post] or the empty [String] if it is `null`, whose
 * `navigationIconContent` lambda argument argument is our [navigationIconContent] lambda parameter,
 * and whose `scrollBehavior` argument is our [TopAppBarScrollBehavior] variable `scrollBehavior`,
 * and whose `bottomBar` argument is our [bottomBarContent] lambda parameter. In the `content` lambda
 * argument of the [Scaffold] we compose a [PostContent] Composable whose `post` argument is our
 * [Post] parameter [post], whose `contentPadding` argument is the [PaddingValues] passed the lambda
 * by [Scaffold], whose `modifier` argument is a [Modifier.nestedScroll] is the
 * [TopAppBarScrollBehavior.nestedScrollConnection] of our [TopAppBarScrollBehavior] variable
 * `scrollBehavior` (a [NestedScrollConnection] that should be attached to a [Modifier.nestedScroll]
 * in order to keep track of the scroll events). And the `state` argument of the [PostContent] is
 * our [LazyListState] parameter [lazyListState].
 *
 * @param post (state) item to display
 * @param navigationIconContent (UI) content to show for the navigation icon
 * @param bottomBarContent (UI) content to show for the bottom bar
 * @param lazyListState a [LazyListState] that can be used to control or observe the list's state
 * in the [LazyColumn] used by [PostContent].
 */
@ExperimentalMaterial3Api
@Composable
private fun ArticleScreenContent(
    post: Post,
    navigationIconContent: @Composable () -> Unit = { },
    bottomBarContent: @Composable () -> Unit = { },
    lazyListState: LazyListState = rememberLazyListState()
) {
    val topAppBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(state = topAppBarState)
    Scaffold(
        topBar = {
            TopAppBar(
                title = post.publication?.name.orEmpty(),
                navigationIconContent = navigationIconContent,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = bottomBarContent
    ) { innerPadding: PaddingValues ->
        PostContent(
            post = post,
            contentPadding = innerPadding,
            modifier = Modifier
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection),
            state = lazyListState,
        )
    }
}

/**
 * Used as the `topBar` argument of the [Scaffold] used by [ArticleScreenContent]. Our root
 * Composable is a [CenterAlignedTopAppBar] (which is a Material Design center-aligned small top app
 * bar that displays information and actions at the top of a screen. This small top app bar has a
 * header `title` that is horizontally aligned to the center. It has slots for a title, navigation
 * icon, and actions). The `title` lambda argument of the [CenterAlignedTopAppBar] is a lambda which
 * composes a [Row] which in its [RowScope] `content` lambda argument holds an [Image] whose `painter`
 * argument causes it to draw the drawable with resource ID [R.drawable.icon_article_background]
 * (a `vector` which draws the upper half of an "android" face), and the `modifier` argument of the
 * [Image] is a [Modifier.clip] which clips it to the `shape` [CircleShape], with a [Modifier.size]
 * which sets the size of the [Image] to 36.dp. The [Image] is followed in the [Row] by a [Text]
 * whose `text` argument is the [String] returned by the [stringResource] when it uses the format
 * [String] with resource ID [R.string.published_in] to format our [String] parameter [title]
 * (prefixes the [title] with the [String] "Published in:").
 *
 * @param title the [Publication.name] of the [Publication] that the [Post] was published in.
 * @param navigationIconContent the navigation icon displayed at the start of the top app bar.
 * @param scrollBehavior the [TopAppBarScrollBehavior] which holds various offset values that will
 * be applied by our [CenterAlignedTopAppBar] to set up its height and colors. A scroll behavior is
 * designed to work in conjunction with a scrolled content to change the top app bar appearance as
 * the content scrolls. In our case we are passed [TopAppBarScrollBehavior] which when used as the
 * `scrollBehavior` argument of our [CenterAlignedTopAppBar] will cause it to immediately collapse
 * when the content is pulled up, and immediately appear when the content is pulled down.
 * @param modifier a [Modifier] instance which our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    title: String,
    navigationIconContent: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.icon_article_background),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .size(size = 36.dp)
                )
                Text(
                    text = stringResource(id = R.string.published_in, title),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        navigationIcon = navigationIconContent,
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

/**
 * Display a popup explaining functionality not available.
 *
 * @param onDismiss (event) request the popup be dismissed
 */
@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(id = R.string.article_functionality_not_available),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.close))
            }
        }
    )
}

/**
 * Show a share sheet for a post
 *
 * @param post to share
 * @param context Android context to show the share sheet in
 */
fun sharePost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(
        Intent.createChooser(
            /* target = */ intent,
            /* title = */ context.getString(R.string.article_share_post)
        )
    )
}

/**
 * TODO: Add kdoc
 */
@Preview("Article screen")
@Preview("Article screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Preview("Article screen (big font)", fontScale = 1.5f)
@Composable
fun PreviewArticleDrawer() {
    JetnewsTheme {
        val post = runBlocking {
            (BlockingFakePostsRepository().getPost(postId = post3.id) as Result.Success).data
        }
        ArticleScreen(
            post = post,
            isExpandedScreen = false,
            onBack = {},
            isFavorite = false,
            onToggleFavorite = {})
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(name = "Article screen navrail", device = Devices.PIXEL_C)
@Preview(
    name = "Article screen navrail (dark)",
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_C
)
@Preview(name = "Article screen navrail (big font)", fontScale = 1.5f, device = Devices.PIXEL_C)
@Composable
fun PreviewArticleNavRail() {
    JetnewsTheme {
        val post = runBlocking {
            (BlockingFakePostsRepository().getPost(post3.id) as Result.Success).data
        }
        ArticleScreen(
            post = post,
            isExpandedScreen = true,
            onBack = {},
            isFavorite = false,
            onToggleFavorite = {})
    }
}
