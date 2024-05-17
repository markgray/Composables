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

package com.example.jetnews.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostAuthor
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.ui.home.PostCardHistory
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.utils.BookmarkButton
import com.example.jetnews.ui.home.PostCardHistory as PostCardHistory1

/**
 * Used by [PostCardHistory] and [PostCardSimple] to display the [PostAuthor.name] of the
 * [Metadata.author] of the [Post.metadata] property of our [Post] parameter [post], and the
 * [Metadata.readTimeMinutes] of the [Post.metadata] property. Our root Composable is a [Row]
 * whose `modifier` argument is our [Modifier] parameter [modifier]. In the [RowScope] `content`
 * lambda argument of the [Row] we have a [Text] whose `text` is a [String] that is formatted by
 * the format string whose resource `id` is [R.string.home_post_min_read] "%1$s - %2$d min read"
 * with the `formatArgs` for the format string the [PostAuthor.name] of the [Metadata.author] of
 * the [Post.metadata] property of our [Post] parameter [post], and the [Metadata.readTimeMinutes]
 * of the [Post.metadata] property. The [TextStyle] `style` argument of the [Text] is the
 * [Typography.bodyMedium] of our custom [MaterialTheme.typography] (`fontSize` = 14.sp, `lineHeight`
 * = 20.sp, `letterSpacing` = 0.25.sp, and `lineBreak` = [LineBreak.Paragraph])
 *
 * @param post the [Post] whose author and read time we are to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [PostCardHistory] passes us a [Modifier.padding] that adds 4.dp to the `top` of our
 * Composable, and [PostCardSimple] passes us none so empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
fun AuthorAndReadTime(
    post: Post,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(
                id = R.string.home_post_min_read,
                formatArgs = arrayOf(
                    post.metadata.author.name,
                    post.metadata.readTimeMinutes
                )
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Displays the drawable whose resource `id` is the [Post.imageThumbId] property of our [Post]
 * parameter [post]. Our root Composable is an [Image] whose `painter` argument is the [Painter]
 * that is returned by [painterResource] for the `id` argument of the [Post.imageThumbId] of our
 * [Post] parameter [post], and whose `modifier` argument chains a [Modifier.size] to our [Modifier]
 * parameter [modifier] that sets the size of the [Image] to `width` 40.dp, and `height` 40.dp, and
 * to that is chained a [Modifier.clip] that clips the [Image] to the `shape` [Shapes.small] of our
 * custom [MaterialTheme.shapes] (a [RoundedCornerShape] whose `size` if 4.dp).
 *
 * @param post the [Post] whose [Post.imageThumbId] we should use as the resource `id` of the
 * [Image] to draw (they are all png's).
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [PostCardHistory] passes us a [Modifier.padding] that adds 16.dp to the `all` of our
 * sides, and [PostCardSimple] passes us none so empty, default, or starter [Modifier] that contains
 * no elements is used.
 */
@Composable
fun PostImage(post: Post, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = post.imageThumbId),
        contentDescription = null, // decorative
        modifier = modifier
            .size(width = 40.dp, height = 40.dp)
            .clip(shape = MaterialTheme.shapes.small)
    )
}

/**
 * Used by [PostCardHistory] and [PostCardSimple] to display the [Post.title] property of our [Post]
 * parameter [post]. Our root (and only) Composable is a [Text] whose `text` argument is the [String]
 * property [Post.title] or our [Post] parameter [post]. The [TextStyle] `style` argument of the
 * [Text] is the [Typography.titleMedium] of our custom [MaterialTheme.typography] (`fontSize` = 16.sp,
 * `lineHeight` = 24.sp, `letterSpacing` = 0.15.sp, `fontWeight` = [FontWeight.Medium], and `lineBreak`
 * = [LineBreak.Heading]). The `maxLines` argument of the [Text] is 3 (maximum number of lines for
 * the text to span, wrapping if necessary. If the text exceeds the given number of lines, it will
 * be truncated according to overflow), and the `overflow` argument is [TextOverflow.Ellipsis] (Uses
 * an ellipsis to indicate that the text has overflowed).
 *
 * @param post the [Post] whose [Post.title] we are supposed to display.
 */
@Composable
fun PostTitle(post: Post) {
    Text(
        text = post.title,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
    )
}

/**
 * Used by the `PostListSimpleSection` to display a [PostTitle] and [AuthorAndReadTime] for one of
 * the [Post] in the [List] of [Post] found in the [PostsFeed.recommendedPosts] of the current
 * [PostsFeed]. We start by initializing our [String] variable `val bookmarkAction` to the [String]
 * with resource ID [R.string.unbookmark] ("unbookmark") if our [Boolean] parameter [isFavorite] is
 * `true` or the the [String] with resource ID [R.string.bookmark] ("bookmark") if it is `false`.
 * Then our root Composable is a [Row] whose `modifier` argument is a [Modifier.clickable] whose
 * `onClick` argument is a lambda that calls our lambda parameter [navigateToArticle] with the
 * [Post.id] of our [Post] parameter [post] to navigate to the article screen to have it display the
 * contents of [post]. Chained to the [Modifier.clickable] is a [Modifier.semantics] whose
 * [customActions] argument is a [CustomAccessibilityAction] whose `label` argument is our [String]
 * variable `bookmarkAction` and whose `action` argument is a lambda which calls our lambda parameter
 * [onToggleFavorite] and returns `true` to indicate that the action was successfully handled. In
 * the [RowScope] lambda `content` argument of the [Row] we compose a [PostImage] whose `post`
 * argument is our [Post] parameter [post] and whose `modifier` argument is a [Modifier.padding] that
 * adds 16.dp to all of its sides. Next in the [Row] is a [Column] whose `modifier` argument is a
 * [RowScope.weight] that sets its `weight` to 1f (to have it take up all remaining width after its
 * unweighted siblings are measured and placed), with a [Modifier.padding] that adds 16.dp to its
 * `top` and `bottom` chained to that. In the [ColumnScope] `content` lambda of the [Column] we
 * compose a [PostTitle] whose `post` argument is our [Post] parameter [post], and below that in the
 * [Column] is an [AuthorAndReadTime] whose `post` argument is our [Post] parameter [post].
 *
 * At the end of the [Row] is a [BookmarkButton] whose `isBookmarked` argument is our [Boolean]
 * parameter [isFavorite], whose `onClick` argument is our lambda parameter [onToggleFavorite], and
 * whose `modifier` argument is a [Modifier.clearAndSetSemantics] (to remove button semantics so the
 * action can be handled at [Row] level), with a [Modifier.padding] that adds 2.dp to `top` and `bottom`
 * and add 6.dp to each side.
 *
 * @param post the [Post] we are supposed to display.
 * @param navigateToArticle a lambda we can call with the [Post.id] property of our [Post] parameter
 * to request navigation to the Article screen to have it display the [Post] contents.
 * @param isFavorite if `true` the [Post.id] of [post] is in the [Set] of [String] of
 * [HomeUiState.HasPosts.favorites].
 * @param onToggleFavorite lambda that can be called to toggle the presence of the [Post.id] of our
 * [Post] parameter [post] in the [Set] of [String] of [HomeUiState.HasPosts.favorites]. Used as the
 * `onClick` argument of our [BookmarkButton].
 */
@Composable
fun  PostCardSimple(
    post: Post,
    navigateToArticle: (String) -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val bookmarkAction: String = stringResource(if (isFavorite) R.string.unbookmark else R.string.bookmark)
    Row(
        modifier = Modifier
            .clickable(onClick = { navigateToArticle(post.id) })
            .semantics {
                // By defining a custom action, we tell accessibility services that this whole
                // composable has an action attached to it. The accessibility service can choose
                // how to best communicate this action to the user.
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = bookmarkAction,
                        action = { onToggleFavorite(); true }
                    )
                )
            }
    ) {
        PostImage(post = post, modifier = Modifier.padding(all = 16.dp))
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(vertical = 10.dp)
        ) {
            PostTitle(post = post)
            AuthorAndReadTime(post = post)
        }
        BookmarkButton(
            isBookmarked = isFavorite,
            onClick = onToggleFavorite,
            // Remove button semantics so action can be handled at row level
            modifier = Modifier
                .clearAndSetSemantics {}
                .padding(vertical = 2.dp, horizontal = 6.dp)
        )
    }
}

/**
 * This Composable is called by `PostListHistorySection` to display each of the [Post] in its [List]
 * of [Post] parameter `posts` (which is the [PostsFeed.recentPosts] property of the current
 * [PostsFeed] that it is called with by `PostList`). We start by initializing and remembering our
 * [MutableState] wrapped [Boolean] variable `var openDialog` to an initial value of `false`. Then
 * our root Composable is a [Row] whose `modifier` is a [Modifier.clickable] whose `onClick` argument
 * is a lambda that calls our lambda parameter [navigateToArticle] with the [Post.id] of our [Post]
 * parameter [post] (to have the article screen display the [Post]'s contents). In the [RowScope]
 * `content` lambda argument of the [Row] we compose:
 *  - a [PostImage] whose `post` argument is our [Post] parameter [post], and whose `modifier`
 *  parameter is a [Modifier.padding] that adds 16.dp padding to all of its sides.
 *  - a [Column] whose `modifier` argument is a [RowScope.weight] whose `weight` argument is 1f
 *  causing it to take up all remaining space after its siblings have been measured and placed.
 *  Chained to that [Modifier] is a [Modifier.padding] that adds 12.dp padding to its top and
 *  bottom.
 *  - the [ColumnScope] `content` lambda of the [Column] contains a [Text] whose `text` argument is
 *  the [String] with resource ID [R.string.home_post_based_on_history] ("BASED ON YOUR HISTORY"),
 *  and whose [TextStyle] `style` argument is the [Typography.labelMedium] of our custom
 *  [MaterialTheme.typography]. Below that in the [Column] is a [PostTitle] whose `post` argument
 *  is our [Post] parameter [post], and that is followed by an [AuthorAndReadTime] whose `post`
 *  argument is our [Post] parameter [post], and whose `modifier` argument is a [Modifier.padding]
 *  that adds 4.dp to its `top`.
 *  - At the end of the [Row] is an [IconButton] whose `onClick` argument is a lambda that sets our
 *  [MutableState] wrapped [Boolean] variable `openDialog` to `true` (this causes an [AlertDialog]
 *  to be displayed that advises "This feature is not yet implemented"). The `content` lambda of
 *  the [AlertDialog] is an [Icon] whose `imageVector` argument causes it to display the [ImageVector]
 *  drawn by [Icons.Filled.MoreVert] (three verical dots), and whose `contentDescription` argument is
 *  the [String] with resource ID [R.string.cd_more_actions] ("More actions").
 *
 * When done composing our content we check if our [MutableState] wrapped [Boolean] variable `openDialog`
 * is now `true` (the user clicked the [IconButton] in our [Row]) and if it is we compose an [AlertDialog]
 * whose arguments are:
 *  - `modifier` is a [Modifier.padding] that adds 20.dp to all sides of he [AlertDialog]
 *  - `onDismissRequest` is a lambda that sets `openDialog` back to `false` (causing us to disappear
 *  on the next recomposition) This is called when the user tries to dismiss the Dialog by clicking
 *  outside or pressing the back button.
 *  - `title` is a lambda that composes a [Text] whose `text` is the [String] with resourse ID
 *  [R.string.fewer_stories] ("Show fewer stories like this?"), and whose [TextStyle] `style` argument
 *  is the [Typography.titleLarge] of our custom [MaterialTheme.typography] (`fontSize` = 22.sp,
 *  `lineHeight` = 28.sp, `letterSpacing` = 0.sp, `lineBreak` = [LineBreak.Heading])
 *  - `text` is a lambda that composes a [Text] whose `text` argument is the [String] with resource
 *  ID [R.string.fewer_stories_content] ("This feature is not yet implemented")
 *
 */
@Composable
fun PostCardHistory(post: Post, navigateToArticle: (String) -> Unit) {
    var openDialog: Boolean by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.clickable(onClick = { navigateToArticle(post.id) })
    ) {
        PostImage(
            post = post,
            modifier = Modifier.padding(all = 16.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.home_post_based_on_history),
                style = MaterialTheme.typography.labelMedium
            )
            PostTitle(post = post)
            AuthorAndReadTime(
                post = post,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        IconButton(onClick = { openDialog = true }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(id = R.string.cd_more_actions)
            )
        }
    }
    if (openDialog) {
        AlertDialog(
            modifier = Modifier.padding(all = 20.dp),
            onDismissRequest = { openDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.fewer_stories),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.fewer_stories_content),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Text(
                    text = stringResource(id = R.string.agree),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(all = 15.dp)
                        .clickable { openDialog = false }
                )
            }
        )
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(name = "Bookmark Button")
@Composable
fun BookmarkButtonPreview() {
    JetnewsTheme {
        Surface {
            BookmarkButton(isBookmarked = false, onClick = { })
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(name = "Bookmark Button Bookmarked")
@Composable
fun BookmarkButtonBookmarkedPreview() {
    JetnewsTheme {
        Surface {
            BookmarkButton(isBookmarked = true, onClick = { })
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(name = "Simple post card")
@Preview(name = "Simple post card (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SimplePostPreview() {
    JetnewsTheme {
        Surface {
            PostCardSimple(
                post = post3,
                navigateToArticle = {},
                isFavorite = false,
                onToggleFavorite = {})
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(name = "Post History card")
@Composable
fun HistoryPostPreview() {
    JetnewsTheme {
        Surface {
            PostCardHistory1(post = post3) {}
        }
    }
}
