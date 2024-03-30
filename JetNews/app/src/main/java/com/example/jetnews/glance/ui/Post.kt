/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetnews.glance.ui

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.example.jetnews.JetnewsApplication.Companion.JETNEWS_APP_URI
import com.example.jetnews.R
import com.example.jetnews.glance.ui.theme.JetnewsGlanceTextStyles
import com.example.jetnews.model.Post
import com.example.jetnews.ui.MainActivity

/**
 * TODO: Add kdoc
 */
enum class PostLayout {
    /**
     * TODO: Add kdoc
     */
    HORIZONTAL_SMALL,
    /**
     * TODO: Add kdoc
     */
    HORIZONTAL_LARGE,
    /**
     * TODO: Add kdoc
     */
    VERTICAL
}

/**
 * TODO: Add kdoc
 */
fun DpSize.toPostLayout(): PostLayout {
    return when {
        (this.width <= 300.dp) -> PostLayout.VERTICAL
        (this.width <= 700.dp) -> PostLayout.HORIZONTAL_SMALL
        else -> PostLayout.HORIZONTAL_LARGE
    }
}

private fun Context.authorReadTimeString(author: String, readTimeMinutes: Int) =
    getString(R.string.home_post_min_read)
        .format(author, readTimeMinutes)

private fun openPostDetails(context: Context, post: Post): Action {
    // actionStartActivity is the preferred way to start activities.
    return actionStartActivity(
        Intent(
            /* action = */ Intent.ACTION_VIEW,
            /* uri = */ "$JETNEWS_APP_URI/home?postId=${post.id}".toUri(),
            /* packageContext = */ context,
            /* cls = */ MainActivity::class.java
        )
    )
}

/**
 * TODO: Add kdoc
 */
@Composable
fun Post(
    post: Post,
    bookmarks: Set<String>,
    onToggleBookmark: (String) -> Unit,
    modifier: GlanceModifier,
    postLayout: PostLayout,
) {
    when (postLayout) {
        PostLayout.HORIZONTAL_SMALL -> HorizontalPost(
            post = post,
            bookmarks = bookmarks,
            onToggleBookmark = onToggleBookmark,
            modifier = modifier,
        )

        PostLayout.HORIZONTAL_LARGE -> HorizontalPost(
            post = post,
            bookmarks = bookmarks,
            onToggleBookmark = onToggleBookmark,
            modifier = modifier,
            showImageThumbnail = false
        )

        PostLayout.VERTICAL -> VerticalPost(
            post = post,
            bookmarks = bookmarks,
            onToggleBookmark = onToggleBookmark,
            modifier = modifier,
        )
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
fun HorizontalPost(
    post: Post,
    bookmarks: Set<String>,
    onToggleBookmark: (String) -> Unit,
    modifier: GlanceModifier,
    showImageThumbnail: Boolean = true
) {
    val context: Context = LocalContext.current
    Row(
        verticalAlignment = Alignment.Vertical.CenterVertically,
        modifier = modifier.clickable(onClick = openPostDetails(context = context, post = post))
    ) {
        if (showImageThumbnail) {
            PostImage(
                imageId = post.imageThumbId,
                contentScale = ContentScale.Fit,
                modifier = GlanceModifier.size(size = 80.dp)
            )
        } else {
            PostImage(
                imageId = post.imageId,
                contentScale = ContentScale.Crop,
                modifier = GlanceModifier.width(width = 250.dp)
            )
        }
        PostDescription(
            title = post.title,
            metadata = context.authorReadTimeString(
                author = post.metadata.author.name,
                readTimeMinutes = post.metadata.readTimeMinutes
            ),
            modifier = GlanceModifier.defaultWeight().padding(horizontal = 20.dp)
        )
        BookmarkButton(
            id = post.id,
            isBookmarked = bookmarks.contains(element = post.id),
            onToggleBookmark = onToggleBookmark
        )
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
fun VerticalPost(
    post: Post,
    bookmarks: Set<String>,
    onToggleBookmark: (String) -> Unit,
    modifier: GlanceModifier,
) {
    val context: Context = LocalContext.current
    Column(
        verticalAlignment = Alignment.Vertical.CenterVertically,
        modifier = modifier.clickable(onClick = openPostDetails(context = context, post = post))
    ) {
        PostImage(imageId = post.imageId, modifier = GlanceModifier.fillMaxWidth())
        Spacer(modifier = GlanceModifier.height(height = 4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            PostDescription(
                title = post.title,
                metadata = context.authorReadTimeString(
                    author = post.metadata.author.name,
                    readTimeMinutes = post.metadata.readTimeMinutes
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Spacer(modifier = GlanceModifier.width(width = 10.dp))
            BookmarkButton(
                id = post.id,
                isBookmarked = bookmarks.contains(element = post.id),
                onToggleBookmark = onToggleBookmark
            )
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
fun BookmarkButton(id: String, isBookmarked: Boolean, onToggleBookmark: (String) -> Unit) {
    Image(
        provider = ImageProvider(
            if (isBookmarked) {
                R.drawable.ic_jetnews_bookmark_filled
            } else {
                R.drawable.ic_jetnews_bookmark
            }
        ),
        colorFilter = ColorFilter.tint(colorProvider = GlanceTheme.colors.primary),
        contentDescription = "${if (isBookmarked) R.string.unbookmark else R.string.bookmark}",
        modifier = GlanceModifier.clickable { onToggleBookmark(id) }
    )
}

/**
 * TODO: Add kdoc
 */
@Composable
fun PostImage(
    imageId: Int,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: GlanceModifier = GlanceModifier
) {
    Image(
        provider = ImageProvider(resId = imageId),
        contentScale = contentScale,
        contentDescription = null,
        modifier = modifier.cornerRadius(radius = 5.dp)
    )
}

/**
 * TODO: Add kdoc
 */
@Composable
fun PostDescription(title: String, metadata: String, modifier: GlanceModifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            maxLines = 3,
            style = JetnewsGlanceTextStyles.bodyLarge
                .copy(color = GlanceTheme.colors.onBackground)
        )
        Spacer(modifier = GlanceModifier.height(height = 4.dp))
        Text(
            text = metadata,
            style = JetnewsGlanceTextStyles.bodySmall
                .copy(color = GlanceTheme.colors.onBackground)
        )
    }
}
