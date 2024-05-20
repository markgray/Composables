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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts.impl.posts
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostAuthor
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.utils.CompletePreviews

/**
 * Used by `PostListTopSection` to display the [Post.imageId], [Post.title], and [Post.metadata]
 * information of the [Post] found in the [PostsFeed.highlightedPost] property of the current
 * [PostsFeed]. We start by initialzing our [Typography] variable to our custom
 * [MaterialTheme.typography]. Then our root Composable is a [Column] whose `modifier` argument
 * chains a [Modifier.fillMaxWidth] to our [Modifier] parameter [modifier] to have it fill its
 * entire incoming width constraint, and to this is chained a [Modifier.padding] that adds 16.dp to
 * all sides of the [Column]. Its [ColumnScope] `content` lambda argument starts by initializing its
 * [Modifier] variable to a [Modifier.heightIn] that sets its `min` minimum height to 180.dp, with
 * a [Modifier.fillMaxWidth] chained to that to make the Composable using the [Modifier] occupy its
 * entire width constraint, and to this is chained a [Modifier.clip] that clips its [Shape] to the
 * [Shapes.medium] of our custom [MaterialTheme.shapes] (a [RoundedCornerShape] whose `size` is 4.dp).
 * The children Composables of the [Column] are:
 *  - an [Image] whose `painter` argument is the [Painter] returned by [painterResource] for the
 *  drawable whose resource ID is the [Post.imageId] property of our [Post] parameter [post], whose
 *  `modifier` argument is our [Modifier] variable `imageModifier`, and whose `contentScale` argument
 *  is [ContentScale.Crop] (Scale the source uniformly (maintaining the source's aspect ratio) so
 *  that both dimensions (width and height) of the source will be equal to or larger than the
 *  corresponding dimension of the destination).
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] that sets its `height` to 16.dp
 *  - a [Text] whose `text` is the [Post.title] of our [Post] parameter [post], whose [TextStyle]
 *  `style` argument is the [Typography.titleLarge] of our [Typography] variable `typography`
 *  (`fontSize` = 22.sp, `lineHeight` = 28.sp, `letterSpacing` = 0.sp, `lineBreak` = [LineBreak.Heading]),
 *  and whose `modifier` argument is a [Modifier.padding] that adds 8.dp to its `bottom`
 *  - a [Text] whose `text` is the [PostAuthor.name] of the [Metadata.author] of the [Post.metadata]
 *  of our [Post] parameter [post], whose [TextStyle] `style` argument is the [Typography.labelLarge]
 *  of our [Typography] variable `typography` (`fontSize` = 14.sp, `lineHeight` = 20.sp, `letterSpacing`
 *  = 0.1.sp, `fontWeight` = [FontWeight.Medium]), and whose `modifier` argument is a [Modifier.padding]
 *  that adds 4.dp to its `bottom`
 *  - a [Text] whose `text` is the [String] that is formatted using the [String] whose resource `id`
 *  is [R.string.home_post_min_read] ("%1$s - %2$d min read") using [Metadata.date] of the
 *  [Post.metadata] of our [Post] parameter [post] and the [Metadata.readTimeMinutes] of the
 *  [Post.metadata] of our [Post] parameter [post] as the `formatArgs`, and whose [TextStyle] `style`
 *  argument is the [Typography.bodySmall] of our [Typography] variable `typography` (`fontSize` =
 *  12.sp, `lineHeight` = 16.sp, `letterSpacing` = 0.4.sp, `lineBreak` = [LineBreak.Paragraph])
 *
 * @param post the [Post] whose [Post.imageId], [Post.title], and [Post.metadata] information we are
 * to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us a [Modifier.clickable] whose `onClick` lambda argument causes us
 * to navigate to have the article screen to display the content of our [Post] parameter [post].
 */
@Composable
fun PostCardTop(post: Post, modifier: Modifier = Modifier) {
    // TUTORIAL CONTENT STARTS HERE
    val typography: Typography = MaterialTheme.typography
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {
        val imageModifier: Modifier = Modifier
            .heightIn(min = 180.dp)
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
        Image(
            painter = painterResource(id = post.imageId),
            contentDescription = null, // decorative
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(height = 16.dp))

        Text(
            text = post.title,
            style = typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = post.metadata.author.name,
            style = typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = stringResource(
                id = R.string.home_post_min_read,
                formatArgs = arrayOf(
                    post.metadata.date,
                    post.metadata.readTimeMinutes
                )
            ),
            style = typography.bodySmall
        )
    }
}
// TUTORIAL CONTENT ENDS HERE

/**
 * Preview of the [PostCardTop] composable. Fake data is passed into the composable.
 *
 * Learn more about Preview features in the [documentation](https://d.android.com/jetpack/compose/tooling#preview)
 */
@Preview
@Composable
fun PostCardTopPreview() {
    JetnewsTheme {
        Surface {
            PostCardTop(post = posts.highlightedPost)
        }
    }
}

/**
 * These previews will only show up on Android Studio Dolphin and later.
 * They showcase a feature called Multipreview Annotations.
 *
 * Read more in the [documentation](https://d.android.com/jetpack/compose/tooling#preview-multipreview)
*/
@CompletePreviews
@Composable
fun PostCardTopPreviews() {
    JetnewsTheme {
        Surface {
            PostCardTop(post = posts.highlightedPost)
        }
    }
}
