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

package com.codelab.theming.ui.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.theming.R
import com.codelab.theming.data.Metadata
import com.codelab.theming.data.Post
import com.codelab.theming.data.PostAuthor
import com.codelab.theming.data.PostRepo
import com.codelab.theming.ui.start.theme.JetnewsTheme
import com.codelab.theming.ui.start.theme.JetnewsTypography
import com.codelab.theming.ui.start.theme.Red300
import com.codelab.theming.ui.start.theme.Red700
import java.util.Locale

/**
 * This is the main Composable of the app. We start by initializing and remembering our [Post] variable
 * `val featured` to the [Post] returned by the [PostRepo.getFeaturedPost] method and initializing and
 * remembering our [List] of [Post] variable `val posts` to the [List] of [Post] returned by the
 * [PostRepo.getPosts] method. The root Composable of [Home] is a [Scaffold] that is wrapped in our
 * [JetnewsTheme] custom [MaterialTheme]. The `topBar` argument of the [Scaffold] is a lambda calling
 * our [AppBar] Composable. The `content` of the [Scaffold] is a [LazyColumn] whose `contentPadding`
 * argument is the [PaddingValues] that the [Scaffold] passes to its `content` lambda in the variable
 * `innerPadding`. The first `item` in the [LazyColumn] is a [Header] Composable displaying the `text`
 * "Top Story". The second `item` is a [FeaturedPost] whose `post` argument is our [Post] variable
 * `featured` and whose `modifier` argument is a [Modifier.padding] that adds 16.dp to all sides of
 * the Composable. The third `item` is a [Header] Composable displaying the `text` "Popular". The
 * bottom of the [LazyColumn] is an [items] whose `items` argument is our [List] of [Post] variable
 * `posts` and whose `itemContent` lambda feeds each [Post] that is supplied to the lambda by [items]
 * to a [PostItem] as its `post` argument then adds a [Divider] below the [PostItem] whose `startIndent`
 * argument is 72.dp (start offset of the line).
 */
@Composable
fun Home() {
    val featured: Post = remember { PostRepo.getFeaturedPost() }
    val posts: List<Post> = remember { PostRepo.getPosts() }
    JetnewsTheme {
        Scaffold(
            topBar = { AppBar() }
        ) { innerPadding: PaddingValues ->
            LazyColumn(contentPadding = innerPadding) {
                item {
                    Header(text = stringResource(id = R.string.top))
                }
                item {
                    FeaturedPost(
                        post = featured,
                        modifier = Modifier.padding(all = 16.dp)
                    )
                }
                item {
                    Header(text = stringResource(id = R.string.popular))
                }
                items(items = posts) { post: Post ->
                    PostItem(post = post)
                    Divider(startIndent = 72.dp)
                }
            }
        }
    }
}

/**
 * This is used as the `topBar` argument of the [Scaffold] Composable of the [Home] Composable. Its
 * root Composable is a [TopAppBar]. Its `navigationIcon` argument (icon displayed at the start of
 * the [TopAppBar]) is lambda displaying an [Icon] whose `imageVector` argument ([ImageVector] to
 * draw inside the [Icon]) is the [Icons.Rounded.Palette] system [ImageVector] (a stylized artists
 * palette), and its `modifier` argument is a [Modifier.padding] that adds 12.dp to each `horizontal`
 * side of the [Icon]. The `title` argument of the [TopAppBar] is a lambda that displays a [Text]
 * whose `text` is the [String] "Jetnews". The `backgroundColor` argument of the [TopAppBar] is the
 * [Colors.primarySurface] color of [MaterialTheme.colors] which is [Colors.primary] in Light Theme
 * ([Red700] in our [JetnewsTheme] custom [MaterialTheme]) or [Colors.surface] in Dark Theme (the
 * default value of `Color(0xFF121212)` or almost black in our [JetnewsTheme] custom [MaterialTheme]).
 */
@Composable
private fun AppBar() {
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.Palette,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        },
        title = {
            Text(text = stringResource(id = R.string.app_title))
        },
        backgroundColor = MaterialTheme.colors.primarySurface
    )
}

/**
 * Displays a "header" line, used as an `item` in the [LazyColumn] of [Home] to display the [String]
 * "Top Story" before the [FeaturedPost] Composable and to display the [String] "Popular" before the
 * [items] which display all of the [Post] objects in the [List] of [Post] that [Home] retrieves from
 * the [PostRepo.getPosts] method. Our root Composable is a [Surface] whose `color` argument (the
 * background color) is a copy of the [Colors.onSurface] color of [MaterialTheme.colors] (the default
 * [Color.Black] for light theme, or the default [Color.White] for dark theme) with the `alpha` set
 * to 0.1f, the `contentColor` argument (preferred content color provided by this Surface to its
 * children) is the [Colors.primary] color of [MaterialTheme.colors] ([Red700] for light theme and
 * [Red300] for dark theme, and the `modifier` argument adds a [Modifier.semantics] of [heading] to
 * our [Modifier] parameter [modifier]. The `content` of the [Surface] is a [Text] that displays our
 * [text] parameter using the [Typography.subtitle2] of [MaterialTheme.typography] as its [TextStyle]
 * `style` argument (`fontFamily` of Montserrat, `fontWeight` of [FontWeight.W500] (the [Font] with
 * resource ID [R.font.montserrat_medium]) and a `fontSize` of 14.sp). The `modifier` argument of the
 * [Text] is a [Modifier.fillMaxWidth] to have the [Text] use the entire width of its incoming
 * constraints to which is added a [Modifier.padding] that adds 16.dp to each side of the [Text] and
 * 8.dp to the top and bottom of the [Text].
 *
 * @param text the [String] that our [Text] should display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our two call sites do not pass one so the empty, default, or starter [Modifier] that
 * contains no elements is used instead.
 */
@Composable
fun Header(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier.semantics { heading() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * Displays the [Post] in its [post] parameter. It is called to fill an `item` in the [LazyColumn]
 * of [Home] with the random [Post] that [Home] retrieves from [PostRepo.getFeaturedPost]. Our root
 * Composable is a [Card] whose `modifier` argument is our [modifier] parameter. The root Composable
 * of the [Card] is a [Column] whose `modifier` argument is a [Modifier.fillMaxWidth] that causes it
 * to fill the entire width of its incoming measurement constraints, to which is chained a
 * [Modifier.clickable] with a do nothing lambda. The `content` of the [Column] is:
 *  - an [Image] whose `painter` argument draws the drawable whose resource ID is the [Post.imageId]
 *  of our [post] parameter, whose `contentDescription` argument is `null`, whose `contentScale`
 *  argument is [ContentScale.Crop] (scales the source uniformly, maintaining the source's aspect
 *  ratio), and whose `modifier` argument is a [Modifier.heightIn] that constrains the height of the
 *  content to a minimum of 180.dp, with a [Modifier.fillMaxWidth] to have it fill the entire width
 *  of its incoming measurement constraints.
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] that sets its height to 16.dp
 *  - Since we want a `horizontal` padding of 16.dp for the next three Composables we initialize our
 *  [Modifier] variable `val padding` to a [Modifier.padding] whose `horizontal` argument is 16.dp
 *  - a [Text] comes next in the [Column] whose `text` argument displays the [Post.title] field of
 *  our [post] parameter using as its `style` argument the [TextStyle] specified for [Typography.h6]
 *  by our [JetnewsTypography] custom [Typography] (`fontFamily` is `Montserrat`, `fontWeight` is
 *  [FontWeight.W600], and `fontSize` is 20.sp), and its `modifier` argument is our [Modifier] variable
 *  `padding`.
 *  - this is followed by a [Text] whose `text` argument displays the [PostAuthor.name] of the
 *  [Metadata.author] field of the [Post.metadata] field of our [Post] parameter [post] using as its
 *  `style` argument the [TextStyle] specified for [Typography.body2] by our [JetnewsTypography] custom
 *  [Typography] (`fontFamily` is `Montserrat`, and `fontSize` is 14.sp), and its `modifier` argument
 *  is our [Modifier] variable `padding`.
 *  - next in the [Column] is a [PostMetadata] whose `post` argument is our [post] parameter, and
 *  whose `modifier` argument is our [Modifier] variable `padding`.
 *  - at the end of the [Column] is a [Spacer] whose `modifier` argument is a [Modifier.height] that
 *  sets its height to 16.dp
 *
 * @param post the [Post] we should display. [Home] calls us with the random [Post] that it retrieves
 * from [PostRepo.getFeaturedPost].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [Home] calls us with a [Modifier.padding] that adds 16.dp to all sides of our [Card]
 * root Composable.
 */
@Composable
fun FeaturedPost(
    post: Post,
    modifier: Modifier = Modifier
) {
    Card(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* onClick */ }
        ) {
            Image(
                painter = painterResource(id = post.imageId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .heightIn(min = 180.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(height = 16.dp))

            val padding = Modifier.padding(horizontal = 16.dp)
            Text(
                text = post.title,
                style = MaterialTheme.typography.h6,
                modifier = padding
            )
            Text(
                text = post.metadata.author.name,
                style = MaterialTheme.typography.body2,
                modifier = padding
            )
            PostMetadata(post = post, modifier = padding)
            Spacer(modifier = Modifier.height(height = 16.dp))
        }
    }
}

/**
 * This Composable builds an [AnnotatedString] that displays the fields [Metadata.date], and
 * [Metadata.readTimeMinutes] of the [Post.metadata] of its [Post] parameter [post] as well as its
 * [Post.tags] strings using appropriate [SpanStyle] styling configurations for each field. We
 * initialize our [String] variable `val divider` to a &sdot; character with two spaces on either
 * side of it, and our [String] variable to two spaces. Then we initialize our [AnnotatedString]
 * variable `val text` to a new [AnnotatedString] by populating newly created [AnnotatedString.Builder]
 * provided to the lambda of the [buildAnnotatedString] method. The lambda applies builder methods
 * to the [AnnotatedString.Builder] supplied to it as `this` as follows:
 *  - it uses [AnnotatedString.Builder.append] to append the [Metadata.date] field of the [Post.metadata]
 *  field of our [Post] parameter [post] to the Builder.
 *  - it uses [AnnotatedString.Builder.append] to append our `divider` variable.
 *  - it uses [AnnotatedString.Builder.append] to append the formated [String] created from the
 *  [Metadata.readTimeMinutes] field of the [Post.metadata] field of our [Post] parameter [post]
 *  using the format [String] with the resource ID [R.string.read_time] ("%1$d min read").
 *  - it uses [AnnotatedString.Builder.append] to append our `divider` variable.
 *  - it initializes its [SpanStyle] variable `val tagStyle` to a copy of the [Typography.overline]
 *  custom [TextStyle] of [MaterialTheme.typography] (`fontFamily` = Montserrat, `fontWeight` =
 *  [FontWeight.W500], and `fontSize` = 12.sp) converted to a [SpanStyle] by its [TextStyle.toSpanStyle]
 *  method, then copies that with the `background` color for the text set to a copy of the
 *  [Colors.primary] color of [MaterialTheme.colors] with the `alpha` set to 0.1f ([Red700] for the
 *  light theme, and [Red300] for the dark theme).
 *  - then it uses the [forEachIndexed] method to loop over all of the [String]'s in the [Post.tags]
 *  field of [post] and if the `index` of a entry is not 0 it uses `append` to append our `tagDivider`
 *  variable, then for all the [String]'s it uses the [withStyle] method of our builder to set the
 *  `style` to our `tagStyle` variable betore using `append` to append the `tag` [String] converted
 *  to upper case using the rules of the [Locale.getDefault] default locale.
 *
 * Having built our [AnnotatedString] variable `text` we then wrap in a [CompositionLocalProvider]
 * that has [LocalContentAlpha] provide [ContentAlpha.medium] (medium level of content alpha, used
 * to represent medium emphasis text) a [Text] Composable with its `text` argument displaying
 * our [AnnotatedString] variable `text`, with the `style` argument the [Typography.body2] font style
 * of [MaterialTheme.typography] (`fontFamily` = `Montserrat`, `fontSize` = 14.sp), and with its
 * `modifier` argument our [Modifier] parameter [modifier].
 *
 * @param post the [Post] whose [Post.metadata] and [Post.tags] we are to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [FeaturedPost] calls us with a `horizontal` [Modifier.padding] of 16.dp, and [ListItem]
 * of [PostItem] does not pass a value so the empty, default, or starter [Modifier] that contains no
 * elements is used for it.
 */
@Composable
private fun PostMetadata(
    post: Post,
    modifier: Modifier = Modifier
) {
    val divider = "  •  "
    val tagDivider = "  "
    val text: AnnotatedString = buildAnnotatedString {
        append(text = post.metadata.date)
        append(text = divider)
        append(text = stringResource(R.string.read_time, post.metadata.readTimeMinutes))
        append(text = divider)
        val tagStyle: SpanStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
            background = MaterialTheme.colors.primary.copy(alpha = 0.1f)
        )
        post.tags.forEachIndexed { index: Int, tag: String ->
            if (index != 0) {
                append(text = tagDivider)
            }
            withStyle(style = tagStyle) {
                append(text = " ${tag.uppercase(Locale.getDefault())} ")
            }
        }
    }
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = modifier
        )
    }
}

/**
 * This Composable is used for each of the [Post] `items` in the [List] of [Post] that the [Home]
 * Composable retrieves from the [PostRepo.getPosts] method ([Home] uses the [items] method to in
 * its [LazyColumn] to call us for each [Post] in the [List]). Our root Composable is the system
 * [ListItem] Composable which we call with the `modifier` argument our [Modifier] parameter [modifier]
 * with a [Modifier.clickable] added to it with do nothing lambda and a [Modifier.padding] added to
 * that which sets the `vertical` padding to 8.dp, the `icon` argument is a lambda which calls
 * [Image] with its [Painter] argument `painter` rendering the drawable whose resource ID is the
 * [Post.imageThumbId] field of [post] and whose `contentDescription` argument is `null` and whose
 * `modifier` argument is a [Modifier.clip] that clips the [Image] to the [Shapes.small] shape of
 * [MaterialTheme.shapes] (a [CutCornerShape] whose `topStart` argument is 8.dp (only the top left
 * corner is "cut", the rest are left uncut). The `text` argument of the [ListItem] is a lambda that
 * calls [Text] with its `text` argument the [Post.title] field of [post]. The `secondaryText` argument
 * is a [PostMetadata] Composable that we call with its `post` argument our [Post] parameter [post].
 *
 * @param post the [Post] that we are supposed to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [Home] does not pass any so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostItem(
    post: Post,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .clickable { /* todo */ }
            .padding(vertical = 8.dp),
        icon = {
            Image(
                painter = painterResource(id = post.imageThumbId),
                contentDescription = null,
                modifier = Modifier.clip(shape = MaterialTheme.shapes.small)
            )
        },
        text = {
            Text(text = post.title)
        },
        secondaryText = {
            PostMetadata(post = post)
        }
    )
}

/**
 * This is a Preview of the [PostItem] Composable. We initialize and [remember] our [Post] variable
 * `val post` to the random [Post] returned by the [PostRepo.getFeaturedPost] method, then wrapped
 * in our [JetnewsTheme] custom [MaterialTheme] we call [Surface] with its `content` a [PostItem]
 * with our `post` variable as its `post` argument.
 */
@Preview("Post Item")
@Composable
private fun PostItemPreview() {
    val post = remember { PostRepo.getFeaturedPost() }
    JetnewsTheme {
        Surface {
            PostItem(post = post)
        }
    }
}

/**
 * This is a Preview of the [FeaturedPost] Composable. We initialize and [remember] our [Post] variable
 * `val post` to the random [Post] returned by the [PostRepo.getFeaturedPost] method, then wrapped
 * in our [JetnewsTheme] custom [MaterialTheme] we call [FeaturedPost] with our `post` variable as
 * its `post` argument.
 */
@Preview("Featured Post")
@Composable
private fun FeaturedPostPreview() {
    val post = remember { PostRepo.getFeaturedPost() }
    JetnewsTheme {
        FeaturedPost(post = post)
    }
}

/**
 * This preview is the same as [FeaturedPostPreview] except that it uses the `darkTheme` of our
 * [JetnewsTheme] custom [MaterialTheme] instead of the default light theme. We initialize and
 * [remember] our [Post] variable `val post` to the random [Post] returned by the
 * [PostRepo.getFeaturedPost] method, then wrapped in our [JetnewsTheme] custom [MaterialTheme]
 * with its `darkTheme` argument `true` we call [FeaturedPost] with our `post` variable as its
 * `post` argument.
 */
@Preview("Featured Post • Dark")
@Composable
private fun FeaturedPostDarkPreview() {
    val post = remember { PostRepo.getFeaturedPost() }
    JetnewsTheme(darkTheme = true) {
        FeaturedPost(post = post)
    }
}

/**
 * This is a Preview of the [Home] Composable, and consists of only a call of [Home].
 */
@Preview("Home")
@Composable
private fun HomePreview() {
    Home()
}
