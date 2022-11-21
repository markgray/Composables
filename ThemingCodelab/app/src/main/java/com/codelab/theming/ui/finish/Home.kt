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

package com.codelab.theming.ui.finish

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
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.theming.R
import com.codelab.theming.data.Post
import com.codelab.theming.data.PostRepo
import com.codelab.theming.ui.finish.theme.JetnewsTheme
import java.util.Locale

/**
 * This is the main Composable of the app. We start by initializing and remembering our [Post] variable
 * `val featured` to the [Post] returned by the [PostRepo.getFeaturedPost] method and initializing and
 * remembering our [List] of [Post] variable `val posts` to the [List] of [Post] returned by the
 * [PostRepo.getPosts] method. The root Composable [Home] is a [Scaffold] that is wrapped in our
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
                    Header(text = stringResource(R.string.top))
                }
                item {
                    FeaturedPost(
                        post = featured,
                        modifier = Modifier.padding(all = 16.dp)
                    )
                }
                item {
                    Header(text = stringResource(R.string.popular))
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
 * TODO: Add kdoc
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
            Text(text = stringResource(R.string.app_title))
        },
        backgroundColor = MaterialTheme.colors.primarySurface
    )
}

/**
 * TODO: Add kdoc
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
 * TODO: Add kdoc
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

            val padding: Modifier = Modifier.padding(horizontal = 16.dp)
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
            withStyle(tagStyle) {
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
 * TODO: Add kdoc
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

@Preview("Post Item")
@Composable
private fun PostItemPreview() {
    val post: Post = remember { PostRepo.getFeaturedPost() }
    JetnewsTheme {
        Surface {
            PostItem(post = post)
        }
    }
}

@Preview("Featured Post")
@Composable
private fun FeaturedPostPreview() {
    val post: Post = remember { PostRepo.getFeaturedPost() }
    JetnewsTheme {
        FeaturedPost(post = post)
    }
}

@Preview("Featured Post • Dark")
@Composable
private fun FeaturedPostDarkPreview() {
    val post: Post = remember { PostRepo.getFeaturedPost() }
    JetnewsTheme(darkTheme = true) {
        FeaturedPost(post = post)
    }
}

@Preview("Home")
@Composable
private fun HomePreview() {
    Home()
}
