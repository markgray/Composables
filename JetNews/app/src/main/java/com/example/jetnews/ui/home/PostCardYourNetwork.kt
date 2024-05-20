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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts.impl.post1
import com.example.jetnews.data.posts.impl.post2
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.data.posts.impl.post4
import com.example.jetnews.data.posts.impl.post5
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostAuthor
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.ui.theme.JetnewsTheme

/**
 * Used by the `PostListPopularSection` Composable to display information about each of the [Post]
 * in the [List] of [Post] property [PostsFeed.popularPosts] of the current [PostsFeed] that is passed
 * to it by the `PostList` Composable. Our root Composable is a [Card] whose `onClick` argument is a
 * lambda that calls our lambda parameter [navigateToArticle] with the [Post.id] of our [Post] parameter
 * [post], whose `shape` argument is the [Shapes.medium] of our custom [MaterialTheme.shapes] (which
 * is a [RoundedCornerShape] whose `size` is 4.dp), and whose `modifier` argument chains a [Modifier.width]
 * to our [Modifier] parameter [modifier] that sets its width to 280dp. In the [ColumnScope] `content`
 * lambda of the [Card] we compose: *
 *  - a [Column] that contains an [Image] whose `painter` is the [Painter] returned by [painterResource]
 *  for the drawable whose resource `id` is the [Post.imageId] of our [Post] parameter [post], whose
 *  `contentScale` argument is [ContentScale.Crop] (Scale the source uniformly (maintaining the
 *  source's aspect ratio) so that both dimensions (width and height) of the source will be equal
 *  to or larger than the corresponding dimension of the destination), and whose `modifier` argument
 *  is a [Modifier.height] that sets its `height` to 100.dp with a [Modifier.fillMaxWidth] that has
 *  it fill its entire incoming width constraint chained to that.
 *  - a [Column] whose `modifier` argument is a [Modifier.padding] that adds 16.dp to `all` sides,
 *  and the [ColumnScope] `content` lambda argument composes:
 *  - a [Text] whose `text` argument is the [Post.title] of our [Post] parameter [post], whose [TextStyle]
 *  `style` argument is the [Typography.headlineSmall] of our custom [MaterialTheme.typography]
 *  (`fontSize` = 24.sp, `lineHeight` = 32.sp, `letterSpacing` = 0.sp, `lineBreak` = [LineBreak.Heading]),
 *  whose `maxLines` argument is 2 (maximum number of lines for the text to span), and the `overflow`
 *  argument is [TextOverflow.Ellipsis] (Use an ellipsis to indicate that the text has overflowed.).
 *  - a [Spacer] whose `modifier` argument is a [ColumnScope.weight] whose `weight` is 1f causing it
 *  to take up all the height remaining in the [Column] once its unweighted siblings have been measured
 *  and placed.
 *  - a [Text] whose text is the [PostAuthor.name] of the [Metadata.author] of the [Post.metadata]
 *  of our [Post] parameter [post], whose `maxLines` argument is 1 (maximum number of lines for the
 *  text to span), whose `overflow` argument is [TextOverflow.Ellipsis] (Use an ellipsis to indicate
 *  that the text has overflowed), and whose [TextStyle] style argument is the [Typography.bodyMedium]
 *  of our custom [MaterialTheme.typography] (`fontSize` = 14.sp, `lineHeight` = 20.sp, `letterSpacing`
 *  = 0.25.sp, `lineBreak` = [LineBreak.Paragraph]).
 *  - a [Text] whose `text` is the [String] that is formatted using the [String] whose resource id
 *  is [R.string.home_post_min_read] ("%1$s - %2$d min read") using [Metadata.date] of the [Post.metadata]
 *  of our [Post] parameter [post] and the [Metadata.readTimeMinutes] of the [Post.metadata] of our
 *  [Post] parameter [post] as the `formatArgs`, and whose [TextStyle] `style` argument is the
 *  [Typography.bodySmall] of our custom [MaterialTheme.typography] (`fontSize` = 12.sp, `lineHeight`
 *  = 16.sp, `letterSpacing` = 0.4.sp, `lineBreak` = [LineBreak.Paragraph]).
 *
 * @param post the [Post] whose information we are to display in a clickable [Card].
 * @param navigateToArticle a lambda which when called with the [Post.id] property of a [Post] will
 * navigate to the article screen to have it display the [Post].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller `PostListPopularSection` does not pass us one so the empty, default, or
 * starter [Modifier] that contains no elements is used.
 */
@Composable
fun PostCardPopular(
    post: Post,
    navigateToArticle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { navigateToArticle(post.id) },
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .width(width = 280.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = post.imageId),
                contentDescription = null, // decorative
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(height = 100.dp)
                    .fillMaxWidth()
            )

            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                Text(
                    text = post.metadata.author.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(
                        id = R.string.home_post_min_read,
                        formatArgs = arrayOf(
                            post.metadata.date,
                            post.metadata.readTimeMinutes
                        )
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Previews of our [PostCardPopular]
 */
@Preview(name = "Regular colors")
@Preview(name = "Dark colors", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPostCardPopular(
    @PreviewParameter(PostPreviewParameterProvider::class, limit = 1) post: Post
) {
    JetnewsTheme {
        Surface {
            PostCardPopular(post = post, navigateToArticle = {})
        }
    }
}

/**
 * Preview of our [PostCardPopular] with a [Post] with very long [Post.title] and [PostAuthor.name]
 */
@Preview(name = "Regular colors, long text")
@Composable
fun PreviewPostCardPopularLongText(
    @PreviewParameter(PostPreviewParameterProvider::class, limit = 1) post: Post
) {
    val loremIpsum =
        """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras ullamcorper pharetra massa,
        sed suscipit nunc mollis in. Sed tincidunt orci lacus, vel ullamcorper nibh congue quis.
        Etiam imperdiet facilisis ligula id facilisis. Suspendisse potenti. Cras vehicula neque sed
        nulla auctor scelerisque. Vestibulum at congue risus, vel aliquet eros. In arcu mauris,
        facilisis eget magna quis, rhoncus volutpat mi. Phasellus vel sollicitudin quam, eu
        consectetur dolor. Proin lobortis venenatis sem, in vestibulum est. Duis ac nibh interdum,
        """.trimIndent()
    JetnewsTheme {
        Surface {
            PostCardPopular(
                post = post.copy(
                    title = "Title$loremIpsum",
                    metadata = post.metadata.copy(
                        author = PostAuthor(name = "Author: $loremIpsum"),
                        readTimeMinutes = Int.MAX_VALUE
                    )
                ),
                navigateToArticle = {}
            )
        }
    }
}

/**
 * Provides sample [Post] instances for Composable Previews.
 *
 * When creating a Composable Preview using @Preview, you can pass sample data
 * by annotating a parameter with @PreviewParameter:
 *
 * ```
 * @Preview
 * @Composable
 * fun MyPreview(@PreviewParameter(PostPreviewParameterProvider::class, limit = 2) post: Post) {
 *   MyComposable(post)
 * }
 * ```
 *
 * In this simple app we just return the hard-coded posts. When the app
 * would be more complex - e.g. retrieving the posts from a server - this would
 * be the right place to instantiate dummy instances.
 */
class PostPreviewParameterProvider : PreviewParameterProvider<Post> {
    override val values: Sequence<Post> = sequenceOf(
        post1, post2, post3, post4, post5
    )
}
