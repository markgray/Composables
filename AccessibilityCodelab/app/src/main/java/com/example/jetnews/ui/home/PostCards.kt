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

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.jetnews.R
import com.example.jetnews.data.posts.post1
import com.example.jetnews.data.posts.post3
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostAuthor
import com.example.jetnews.ui.MainActions
import com.example.jetnews.ui.theme.JetnewsShapes
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.theme.JetnewsTypography
import com.example.jetnews.ui.theme.Red300
import com.example.jetnews.ui.theme.Red700
import com.example.jetnews.ui.JetnewsNavGraph
import com.example.jetnews.ui.MainDestinations
import com.example.jetnews.ui.article.ArticleScreen

/**
 * This Composable is used by the `PostList` Composable (file ui/home/HomeScreen.kt) as the [items]
 * of the [LazyColumn] that is uses to display all of the [Post] objects in its `postsHistory` [List]
 * of [Post] (which is arbitrarily chosen to be the first 3 posts in its `posts` [List] of [Post]
 * parameter). We start by `remember`ing our [Boolean] variable `var openDialog` with an initial
 * value of a [mutableStateOf] `false`, and initializing our [String] variable `val showFewerLabel`
 * to the [String] with resource ID `R.string.cd_show_fewer` ("Show fewer like this"). Our root
 * Composable is a [Row] whose `modifier` argument is a [Modifier.clickable] whose `onClickLabel`
 * is the [String] with resource ID `R.string.action_read_article` ("read article") and whose
 * `onClick` lambda argument is a lambda which calls our [navigateToArticle] lambda parameter with
 * the [Post.id] property of our [post] parameter. A [Modifier.semantics] is chained to the
 * [Modifier.clickable] whose Custom actions is a [listOf] a single [CustomAccessibilityAction]
 * whose `label` is our [String] variable `showFewerLabel`, and whose `action` argument is a lambda
 * which sets `openDialog` to `true` and returns `true` to indicate success. The `content` of the
 * [Row] is:
 *  - an [Image] whose `painter` draws the drawable whose resource ID is the [Post.imageThumbId]
 *  of our [post] parameter, whose `contentDescription` argument is `null`, and whose `modifier`
 *  argument is a [Modifier.padding] whose `top` is 16.dp, `start` is 16.dp and whose `end` is
 *  16.dp, to which is chained a [Modifier.size] of 40.dp by 40.dp, followed by a [Modifier.clip]
 *  whose `shape` argument is the `small` [RoundedCornerShape] of [MaterialTheme.shapes] (which the
 *  [JetnewsShapes] used as the `shapes` of our [JetnewsTheme] custom [MaterialTheme] specifies to
 *  be a [RoundedCornerShape] whose `size` is 4.dp).
 *  - The next Composable in the [Row] is a [Column] whose `modifier` argument is a `RowScope`
 *  `Modifier.weight` of 1f (the [Column] will occupy all remaining space after its unweighted
 *  siblings are measured and placed), to which a [Modifier.padding] that adds 16.dp padding to the
 *  `top` of the [Column] and 16.dp to the `bottom` is added.
 *
 * The `content` of the [Column] is:
 *  - A [Text] whose `text` is the [String] in the [Post.title] property of [post] and whose `style`
 *  is the `subtitle1` [TextStyle] of [MaterialTheme.typography] which the [JetnewsTypography] of
 *  our [JetnewsTheme] custom [MaterialTheme] specifies to be the Montserrat` [FontFamily] with a
 *  `fontWeight` of [FontWeight.SemiBold] (the [Font] with resource ID `R.font.montserrat_semibold`)
 *  `fontSize` of 16.sp, and `letterSpacing` of 0.15.sp
 *  - A [Row] whose `modifier` argument is a [Modifier.padding] that adds 4.dp to the `top` of the
 *  [Row]. And the `content` of the [Row] is two [Text] wrapped in a [CompositionLocalProvider] which
 *  causes the value [ContentAlpha.medium] to be supplied as the [LocalContentAlpha]. Within the
 *  [CompositionLocalProvider] block we initialize our [TextStyle] variable `val textStyle` to the
 *  `body2` [TextStyle] of [MaterialTheme.typography] which the [JetnewsTypography] of our
 *  [JetnewsTheme] custom [MaterialTheme] specifies to be the `Montserrat` [FontFamily] with a
 *  `fontWeight` of [FontWeight.Medium] (the [Font] with resource ID `R.font.montserrat_medium`),
 *  `fontSize` of 14.sp, and `letterSpacing` of 0.25.sp. The two [Text] Composables use `textStyle`
 *  as their `style` argument, with the `text` argument of the first [Text] using the property
 *  [PostAuthor.name] of the [Metadata.author] of the [Post.metadata] field of our [Post] parameter
 *  [post], and the `text` argument of the second [Text] using the property the [Metadata.readTimeMinutes]
 *  of the [Post.metadata] field of our [Post] parameter [post] as the number of minutes to read in
 *  the String interpolation: " - ${post.metadata.readTimeMinutes} min read"
 *
 *  - At the end of the root [Row] we have an [IconButton] wrapped in a [CompositionLocalProvider]
 *  which causes the value [ContentAlpha.medium] to be supplied as the [LocalContentAlpha]. The
 *  `modifier` argument of the [IconButton] is a an [Modifier.clearAndSetSemantics] (which Clears
 *  the semantics of all the descendant nodes and sets the new semantics to an empty block), and the
 *  `onClick` argument is a lambda which set `openDialog` to `true`. The `content` of the [IconButton]
 *  is an [Icon] whose `imageVector` argument is the `Close` [ImageVector] of [Icons.Default] (an "X"),
 *  and whose `contentDescription` argument is the [String] with resource ID `R.string.cd_show_fewer`
 *  ("Show fewer like this").
 *
 * At the end of this Composable is an `if` statement which will (if `openDialog` is `true`) call an
 * [AlertDialog] whose arguments are:
 *  - `modifier` a [Modifier.padding] that adds 20.dp to all sides of [AlertDialog] (this is added to
 *  the outside of the [AlertDialog] oddly enough, making it smaller which was a bit of a surprise).
 *  - `onDismissRequest` a lambda which sets `openDialog` to `false`.
 *  - `title` The title of the Dialog (which should specify the purpose of the Dialog) uses a [Text]
 *  Composable whose `text` is the [String] with resource ID `R.string.fewer_stories` ("Show fewer
 *  stories like this?"), and whose `style` is the `h6` [TextStyle] of [MaterialTheme.typography]
 *  which the [JetnewsTypography] of our [JetnewsTheme] custom [MaterialTheme] specifies to be the
 *  `Montserrat` [FontFamily] with a `fontWeight` of [FontWeight.SemiBold] (the [Font] with resource
 *  ID `R.font.montserrat_semibold`), `fontSize` of 20.sp, and `letterSpacing` of 0.sp.
 *  - `text` The text which presents the details regarding the Dialog's purpose uses a [Text]
 *  Composable whose `text` is the [String] with resource ID `R.string.fewer_stories_content`
 *  ("This feature is not yet implemented"), and whose `style` is the `body1` [TextStyle] of
 *  [MaterialTheme.typography] which the [JetnewsTypography] of our [JetnewsTheme] custom
 *  [MaterialTheme] specifies to be the `Domine` [FontFamily] with a `fontWeight` of
 *  [FontWeight.Normal] (the [Font] with resource ID `R.font.domine_regular`), `fontSize` of 16.sp,
 *  and `letterSpacing` of 0.5.sp.
 *  - `confirmButton` the button which is meant to confirm a proposed action, thus resolving what
 *  triggered the dialog uses a [Text] as its lambda argument whose `text` is the [String] with
 *  resource ID `R.string.agree` ("AGREE"), whose `style` argument is the `button` [TextStyle] of
 *  [MaterialTheme.typography] which the [JetnewsTypography] of our [JetnewsTheme] custom
 *  [MaterialTheme] specifies to be the `Montserrat` [FontFamily] with a `fontWeight` of
 *  [FontWeight.SemiBold] (the [Font] with resource ID `R.font.montserrat_semibold`), `fontSize`
 *  of 14.sp, and `letterSpacing` of 1.25.sp, whose `color` argument will apply to the text the
 *  `primary` [Color] of [MaterialTheme.colors] which our [JetnewsTheme] custom [MaterialTheme]
 *  defines to be [Red700] (a bright blood red: 0xffdd0d3c) for light theme, and [Red300] (a light
 *  red: 0xffea6d7e) for dark theme, and its `modifier` argument is a [Modifier.padding] that adds
 *  15.dp padding to all sides of the [Text], to which is chained a [Modifier.clickable] whose lambda
 *  argument sets `openDialog` to `false` when the [Text] is clicked.
 *
 * @param post the [Post] whose basic information we are supposed to display.
 * @param navigateToArticle a lambda which should be called with the [Post.id] field of our [post]
 * parameter when our Composable is clicked. [PostList] passes us its own [navigateToArticle] parameter
 * and [HomeScreen] passed it its own [navigateToArticle] parameter, which [JetnewsNavGraph] passed
 * it, and which is the [MainActions.navigateToArticle] method which uses is [NavHostController] to
 * navigate to the [MainDestinations.ARTICLE_ROUTE] which will use [ArticleScreen] to display the
 * [Post].
 */
@Composable
fun PostCardHistory(post: Post, navigateToArticle: (String) -> Unit) {
    var openDialog: Boolean by remember { mutableStateOf(false) }
    val showFewerLabel: String = stringResource(R.string.cd_show_fewer)
    Row(
        modifier = Modifier
            .clickable(
                onClickLabel = stringResource(id = R.string.action_read_article)
            ) {
                navigateToArticle(post.id)
            }
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = showFewerLabel,
                        // action returns boolean to indicate success
                        action = { openDialog = true; true }
                    )
                )
            }
    ) {

        Image(
            painter = painterResource(post.imageThumbId),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .size(width = 40.dp, height = 40.dp)
                .clip(shape = MaterialTheme.shapes.small)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Text(text = post.title, style = MaterialTheme.typography.subtitle1)
            Row(modifier = Modifier.padding(top = 4.dp)) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    val textStyle: TextStyle = MaterialTheme.typography.body2
                    Text(
                        text = post.metadata.author.name,
                        style = textStyle
                    )
                    Text(
                        text = " - ${post.metadata.readTimeMinutes} min read",
                        style = textStyle
                    )
                }
            }
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            IconButton(
                modifier = Modifier.clearAndSetSemantics { },
                onClick = { openDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cd_show_fewer)
                )
            }
        }

    }
    if (openDialog) {
        AlertDialog(
            modifier = Modifier.padding(all = 20.dp),
            onDismissRequest = { openDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.fewer_stories),
                    style = MaterialTheme.typography.h6
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.fewer_stories_content),
                    style = MaterialTheme.typography.body1
                )
            },
            confirmButton = {
                Text(
                    text = stringResource(id = R.string.agree),
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(all = 15.dp)
                        .clickable { openDialog = false }
                )
            }
        )
    }
}

/**
 * This Composable is used by the [PostListPopularSection] Composable to hold each of the [Post]
 * objects that [PostListPopularSection] is passed by the [PostList] Composable from its
 * `postsPopular` [List] of [Post] (which is arbitrarily chosen to be the posts at index 3 and 4
 * of its `posts` [List] of [Post] parameter). Our parent displays its [PostCardPopular] Composables
 * using the [items] method in a [LazyRow] so we are running in a `LazyListScope`. We initialize our
 * [String] variable `val readArticleLabel` to the string with resource ID `R.string.action_read_article`
 * ("read article"), then call our [Card] root Composable with its `shape` argument the `medium`
 * [Shape] of [MaterialTheme.shapes] (which the [JetnewsShapes] used by our [JetnewsTheme] custom
 * [MaterialTheme] specifies to be a [RoundedCornerShape] with 4.dp rounded corners). For its
 * `modifier` argument we add a [Modifier.size] whose `width` is 280.dp, and whose `height` is 240.dp,
 * then add a [Modifier.semantics] whose lambda argument is an [onClick] whose `label` is the [String]
 * variable `readArticleLabel` and whose `action` is `null`. The `onClick` argument of the [Card] is
 * a lambda that calls our [navigateToArticle] lambda parameter with the [Post.id] field of our [post]
 * parameter.
 *
 * The `content` of the [Card] is a [Column] which contains:
 *  - An [Image] whose `painter` draws the drawable with the resource ID in the [Post.imageId] field
 *  of our [post], whose `contentDescription` is `null`, whose `contentScale` is [ContentScale.Crop]
 *  (scales the source image uniformly to fit its destination), and whose `modifier` is a [Modifier.height]
 *  of 100.dp to which is chained a [Modifier.fillMaxWidth].
 *  - An inner [Column] whose `modifier` is a [Modifier.padding] that adds 16.dp to all sides. The
 *  `content` of this [Column] is three [Text] Composables:
 *
 *  1. A [Text] whose `text` is the [Post.title] field of our [post] parameter, whose `style` is the
 *  `h6` [TextStyle] of [MaterialTheme.typography] which the [JetnewsTypography] custom [Typography]
 *  used by our [JetnewsTheme] custom [MaterialTheme] specifies to be the `Montserrat` [FontFamily]
 *  with a `fontWeight` of [FontWeight.SemiBold] (the [Font] with resource ID `R.font.montserrat_semibold`)
 *  with `fontSize` = 20.sp, and `letterSpacing` = 0.sp. The `maxLines` argument of the [Text] is "2",
 *  and the `overflow` argument is [TextOverflow.Ellipsis] (uses an ellipsis to indicate that the text
 *  has overflowed).
 *  2. A [Text] whose `text` is the [PostAuthor.name] field of the [Metadata.author] field of the
 *  [Post.metadata] field of our parameter [post] (aka the `Post.metadata.author.name` field of [post]),
 *  whose `maxLines` argument is "1", and whose `overflow` argument is [TextOverflow.Ellipsis] (uses
 *  an ellipsis to indicate that the text has overflowed). The `style` argument of this [Text] is the
 *  `body2` [TextStyle] of [MaterialTheme.typography] which the [JetnewsTypography] custom [Typography]
 *  used by our [JetnewsTheme] custom [MaterialTheme] specifies to be the `Montserrat` [FontFamily]
 *  with a `fontWeight` of [FontWeight.Medium] (the [Font] with resource ID `R.font.montserrat_medium`)
 *  with `fontSize` = 14.sp, and `letterSpacing` = 0.25.sp
 *  3. A [Text] whose `text` is a [String] formatted using the format [String] with resource ID
 *  `R.string.home_post_min_read` ("%1$s - %2$d min read") which displays the [Metadata.date] and
 *  [Metadata.readTimeMinutes] fields of the [Post.metadata] field of our [post] parameter, and whose
 *  `style` parameter is the `body2` [TextStyle] of [MaterialTheme.typography] which the [JetnewsTypography]
 *  custom [Typography] used by our [JetnewsTheme] custom [MaterialTheme] specifies to be the `Montserrat`
 *  [FontFamily] with a `fontWeight` of [FontWeight.Medium] (the [Font] with resource ID
 *  `R.font.montserrat_medium`) with `fontSize` = 14.sp, and `letterSpacing` = 0.25.sp
 *
 * @param post the [Post] whose information we are supposed to display.
 * @param navigateToArticle a lambda which we should call with the [Post.id] field of our [post]
 * parameter when our [Card] is clicked.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [PostListPopularSection] calls us in the [items] of its [LazyRow] with a [Modifier.padding]
 * that adds 16.dp of padding to our `start` and our `bottom`.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostCardPopular(
    post: Post,
    navigateToArticle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val readArticleLabel: String = stringResource(id = R.string.action_read_article)
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .size(width = 280.dp, height = 240.dp)
            .semantics { onClick(label = readArticleLabel, action = null) },
        onClick = { navigateToArticle(post.id) }
    ) {
        Column {

            Image(
                painter = painterResource(post.imageId),
                contentDescription = null, // decorative
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(height = 100.dp)
                    .fillMaxWidth()
            )

            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.h6,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = post.metadata.author.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2
                )

                Text(
                    text = stringResource(
                        id = R.string.home_post_min_read,
                        formatArgs = arrayOf<Any>(
                            post.metadata.date,
                            post.metadata.readTimeMinutes
                        )
                    ),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

/**
 * Two Previews of a [Surface] wrapped [PostCardPopular] all wrapped in our [JetnewsTheme] custom
 * [MaterialTheme]. The one whose `name` is "Regular colors" uses the default `LightThemeColors` and
 * the one whose `name` is "Dark colors" sets the `uiMode` to [Configuration.UI_MODE_NIGHT_YES] in
 * order to use the `DarkThemeColors`.
 */
@Preview(name = "Regular colors")
@Preview(name = "Dark colors", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPostCardPopular() {
    JetnewsTheme {
        Surface {
            PostCardPopular(post1, {})
        }
    }
}

/**
 * A Preview of a [Surface] wrapped [PostCardHistory] all wrapped in our [JetnewsTheme] custom
 * [MaterialTheme]. Its `name` is "Post History card"
 */
@Preview(name = "Post History card")
@Composable
fun HistoryPostPreview() {
    JetnewsTheme {
        Surface {
            PostCardHistory(post3) {}
        }
    }
}
