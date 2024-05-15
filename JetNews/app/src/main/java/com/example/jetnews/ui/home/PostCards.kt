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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
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
 * TODO: Add kdoc
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
 * TODO: Add kdoc
 */
@Composable
fun PostCardHistory(post: Post, navigateToArticle: (String) -> Unit) {
    var openDialog: Boolean by remember { mutableStateOf(false) }

    Row(
        Modifier
            .clickable(onClick = { navigateToArticle(post.id) })
    ) {
        PostImage(
            post = post,
            modifier = Modifier.padding(all = 16.dp)
        )
        Column(
            Modifier
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
