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

package com.example.jetnews.ui.article

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.model.Post
import com.example.jetnews.model.Publication
import com.example.jetnews.ui.components.InsetAwareTopAppBar
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.utils.supportWideScreen

/**
 * At one time in the distant past this was a Stateful Article Screen that managed state using
 * `produceUiState` (a class that used to be included in the project's source code and can now only
 * be found in ancient commits of the git repository). Now there are no state variables involved, it
 * simply fetchs the [Post] whose ID is its [String] parameter [postId] to initialize its [Post]
 * variable `val postData` using the [PostsRepository.getPost] method of its parameter [postsRepository]
 * and calls the "Stateless Article Screen" with `postData` as its `post` argument and our [onBack]
 * lambda parameter as its `onBack` argument.
 *
 * @param postId (state) the post to show
 * @param postsRepository data source for this screen
 * @param onBack (event) request back navigation
 */
@Composable
fun ArticleScreen(
    postId: String?,
    postsRepository: PostsRepository,
    onBack: () -> Unit
) {
    val postData: Post = postsRepository.getPost(postId) ?: return

    ArticleScreen(
        post = postData,
        onBack = onBack
    )
}

/**
 * Stateless Article Screen that displays a single [Post]. First we use the [rememberSaveable] method
 * to remember the [Boolean] variable `var showDialog` starting with a [MutableState] of `false`.
 * Then if `showDialog` is `true we call the [FunctionalityNotAvailablePopup] Composable to show
 * an [AlertDialog] informing the user that "Functionality not available" (`showDialog` is never set
 * to `true` so this is obviously another left over from an ancient version of the sample, the latest
 * JetNews sample has a `BottomAppBar` which has unimplemented features and uses the variable
 * `showUnimplementedActionDialog` to control the display of [FunctionalityNotAvailablePopup]).
 * The root Composable is a [Scaffold] whose `topBar` argument is a [InsetAwareTopAppBar] whose
 * `title` argument is a [Text] displaying the [Publication.name] of the [Post.publication] field
 * of our [post] parameter, and whose `navigationIcon` argument is an [IconButton] whose `onClick`
 * is our [onBack] parameter and whose `content` is an [Icon] whose `imageVector` is the drawable
 * [Icons.Filled.ArrowBack], and whose `contentDescription` is the [String] with resource ID
 * [R.string.cd_navigate_up] ("Navigate up"). The `content` of the [Scaffold] is a [PostContent]
 * Composable whose `post` argument is our [post] parameter, and whose `modifier` argument is a
 * [Modifier.padding] of the [PaddingValues] passed the `content` in `innerPadding` with a
 * [Modifier.supportWideScreen] added to it to center content in landscape mode.
 *
 * @param post (state) [Post] to display
 * @param onBack (event) request navigate back
 */
@Composable
fun ArticleScreen(
    post: Post,
    onBack: () -> Unit
) {

    var showDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    if (showDialog) {
        FunctionalityNotAvailablePopup { showDialog = false }
    }

    Scaffold(
        topBar = {
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = "Published in: ${post.publication?.name}",
                        style = MaterialTheme.typography.subtitle2,
                        color = LocalContentColor.current
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.cd_navigate_up
                            )
                        )
                    }
                }
            )
        }
    ) { innerPadding: PaddingValues ->
        PostContent(
            post = post,
            modifier = Modifier
                // innerPadding takes into account the top and bottom bar
                .padding(innerPadding)
                // center content in landscape mode
                .supportWideScreen()
        )
    }
}

/**
 * Display a popup explaining functionality not available. Our root Composable is an [AlertDialog]
 * whose `onDismissRequest` argument is our [onDismiss] lambda parameter, whose `text` argument is
 * a [Text] displaying the message "Functionality not available" using the `body2` [TextStyle] of
 * [MaterialTheme.typography] (in our case the `Montserrat` [FontFamily] with a `fontWeight` of
 * [FontWeight.Medium], `fontSize` of 14.sp, and `letterSpacing` of 0.25.sp), and the `confirmButton`
 * argument of the [AlertDialog] is a [TextButton] whose `onClick` argument is our [onDismiss]
 * parameter, and whose `content` is a [Text] displaying the `text` "CLOSE".
 *
 * @param onDismiss (event) request the popup be dismissed
 */
@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
}

/**
 * Previews of a [JetnewsTheme] custom [MaterialTheme] wrapped [ArticleScreen] using different settings:
 *  - "Article screen" uses default settings
 *  - "Article screen (dark)" uses [UI_MODE_NIGHT_YES] for the `uiMode` which shows the [ArticleScreen]
 *  in night mode.
 *  - "Article screen (big font)" uses 1.5f as the `fontScale` -- the scaling factor for fonts,
 *  relative to the base density scaling
 *  - "Article screen (large screen)" uses [Devices.PIXEL_C] as the `device` to use in the preview.
 */
@Preview("Article screen")
@Preview("Article screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Preview("Article screen (big font)", fontScale = 1.5f)
@Preview("Article screen (large screen)", device = Devices.PIXEL_C)
@Composable
fun PreviewArticle() {
    JetnewsTheme {
        ArticleScreen(PostsRepository().getPost(post3.id) ?: return@JetnewsTheme) {}
    }
}
