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

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.ui.components.InsetAwareTopAppBar
import com.example.jetnews.ui.theme.JetnewsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * At one time in the distant past this was a Stateful HomeScreen Screen that managed state using
 * `produceUiState` (a class that used to be included in the project's source code and can now only
 * be found in ancient commits of the git repository). We call our overloaded [HomeScreen] Composable
 * with the [List] of [Post] objects returned by the [PostsRepository.getPosts] method of our parameter
 * [postsRepository] as its `posts` argument, our parameter [navigateToArticle] as its `navigateToArticle`
 * argument, our parameter [openDrawer] as its `openDrawer` argument, and our parameter [scaffoldState]
 * as its `scaffoldState` argument,
 *
 * @param postsRepository data source for this screen
 * @param navigateToArticle (event) request navigation to Article screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeScreen(
    postsRepository: PostsRepository,
    navigateToArticle: (String) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    HomeScreen(
        posts = postsRepository.getPosts(),
        navigateToArticle = navigateToArticle,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState
    )
}

/**
 * Responsible for displaying the Home Screen of this application. Stateless composable that is not
 * coupled to any specific state management. We initialize and remember our [CoroutineScope] variable
 * `val coroutineScope` using the [rememberCoroutineScope] method, then call our [Scaffold] root
 * Composable with our [ScaffoldState] parameter [scaffoldState] as its `scaffoldState` argument, and
 * as its `topBar` argument we use the [stringResource] method to retrieve the [String] whose resource
 * ID is [R.string.app_name] ("Jetnews") and call our [InsetAwareTopAppBar] Composable with a [Text]
 * displaying our [String] variable `title` as its `title` argument, and as its `navigationIcon`
 * argument we use a lambda to create an [IconButton] whose `onClick` argument uses the method
 * [CoroutineScope.launch] of our `coroutineScope` variable to launch a new coroutine which calls
 * our [openDrawer] lambda parameter. The `content` of the [IconButton] is an [Icon] whose `painter`
 * argument renders the drawable with resource ID [R.drawable.ic_jetnews_logo], and whose
 * contentDescription` argument is the [String] with resource ID [R.string.cd_open_navigation_drawer]
 * ("Open navigation drawer"). The `content` of the [Scaffold] is a lambda which receives as its
 * argument an [PaddingValues] variable `innerPadding` that should be applied to the content root via
 * [Modifier.padding] to properly offset top and bottom bars. To do this we initialize our [Modifier]
 * variable `val modifier` to a [Modifier.padding] whose `paddingValues` argument is the lambda's
 * `innerPadding` argument. Then we call the [PostList] Composable with our [List] of [Post] parameter
 * [posts] as its `posts` argument, our [navigateToArticle] lambda parameter as its `navigateToArticle`
 * argument, and our [Modifier] variable `modifier` as its `modifier` argument. It will display the
 * [List] of [Post] in [posts] in a [LazyColumn].
 *
 * @param posts (state) the data to show on the screen
 * @param navigateToArticle (event) request navigation to Article screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeScreen(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val title: String = stringResource(id = R.string.app_name)
            InsetAwareTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { openDrawer() } }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_jetnews_logo),
                            contentDescription = stringResource(R.string.cd_open_navigation_drawer)
                        )
                    }
                }
            )
        }
    ) { innerPadding: PaddingValues ->
        val modifier: Modifier = Modifier.padding(paddingValues = innerPadding)
        PostList(posts = posts, navigateToArticle = navigateToArticle, modifier = modifier)
    }
}

/**
 * Display a list of posts. When a post is clicked on, [navigateToArticle] will be called to navigate
 * to the detail screen for that post.
 *
 * @param posts (state) the list to display
 * @param navigateToArticle (event) request navigation to Article screen
 * @param modifier modifier for the root element
 */
@Composable
private fun PostList(
    posts: List<Post>,
    navigateToArticle: (postId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val postsHistory: List<Post> = posts.subList(0, 3)
    val postsPopular: List<Post> = posts.subList(3, 5)
    val contentPadding: PaddingValues = rememberContentPaddingForScreen(additionalTop = 8.dp)
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(postsHistory) { post ->
            PostCardHistory(post = post, navigateToArticle = navigateToArticle)
            PostListDivider()
        }
        item {
            PostListPopularSection(posts = postsPopular, navigateToArticle = navigateToArticle)
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
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_popular_section_title),
            style = MaterialTheme.typography.subtitle1
        )

        LazyRow(contentPadding = PaddingValues(end = 16.dp)) {
            items(posts) { post ->
                PostCardPopular(
                    post,
                    navigateToArticle,
                    Modifier.padding(start = 16.dp, bottom = 16.dp)
                )
            }
        }
        PostListDivider()
    }
}

/**
 * Full-width divider with padding for [PostList]
 */
@Composable
private fun PostListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

/**
 * Determine the content padding to apply to the different screens of the app
 */
@Composable
fun rememberContentPaddingForScreen(additionalTop: Dp = 0.dp): PaddingValues =
    WindowInsets.systemBars
        .only(WindowInsetsSides.Bottom)
        .add(WindowInsets(top = additionalTop))
        .asPaddingValues()

/**
 * Four Previews of our [JetnewsTheme] wrapped [HomeScreen] Composable.
 */
@Preview("Home screen")
@Preview("Home screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Preview("Home screen (big font)", fontScale = 1.5f)
@Preview("Home screen (large screen)", device = Devices.PIXEL_C)
@Composable
fun PreviewHomeScreen() {
    JetnewsTheme {
        HomeScreen(
            posts = PostsRepository().getPosts(),
            navigateToArticle = { /*TODO*/ },
            openDrawer = { /*TODO*/ },
            scaffoldState = rememberScaffoldState()
        )
    }
}
