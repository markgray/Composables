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

package com.example.jetnews.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.PostTopBar
import com.example.jetnews.ui.home.HomeFeedWithArticleDetailsScreen
import com.example.jetnews.R

/**
 * If the display is not expanded this is shown in the [ArticleScreen] in a [BottomAppBar] along
 * with a [BookmarkButton], a [ShareButton], and a [TextSettingsButton]. If it is expanded it is
 * shown in a [PostTopBar] with them, which is the top bar for a Post when it is displayed next to
 * the Home feed by [HomeFeedWithArticleDetailsScreen]. It consists of an [IconButton] whose `onClick`
 * argument is our [onClick] lambda parameter and its `content` lambda argument is an [Icon] whose
 * `imageVector` argument causes it to display the [ImageVector] drawn by [Icons.Filled.ThumbUpOffAlt]
 * (a stylized hand with its thumb pointing up) and whose `contentDescription` arugment is the
 * [String] with resource ID [R.string.cd_add_to_favorites] ("Add to favorites").
 *
 * @param onClick a lambda that our [IconButton] should call when it is clicked
 */
@Composable
fun FavoriteButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.ThumbUpOffAlt,
            contentDescription = stringResource(id = R.string.cd_add_to_favorites)
        )
    }
}

/**
 * If the display is not expanded this is shown in the [ArticleScreen] in a [BottomAppBar] along
 * with a [FavoriteButton], a [ShareButton], and a [TextSettingsButton]. If it is expanded it is
 * shown in a [PostTopBar] with them, which is the top bar for a Post when it is displayed next to
 * the Home feed by [HomeFeedWithArticleDetailsScreen]. It consists of an [IconToggleButton] whose
 * `checked` argument is our [Boolean] parameter [isBookmarked], `onCheckedChange` argument is a
 * lambda which calls our [onClick] lambda parameter and its content lambda argument is an [Icon]
 * whose `imageVector` argument causes it to display the [ImageVector] drawn by [Icons.Filled.Bookmark]
 * if [isBookmarked] is `true`, or [Icons.Filled.BookmarkBorder] if it is `false`. The `modifier`
 * argument of the [IconToggleButton] chains a [Modifier.semantics] to our [modifier] parament whose
 * [SemanticsPropertyReceiver.onClick] overrides the `label` of the [IconToggleButton] to supply
 * a custom click label that accessibility services can communicate to the user which is the
 * [String] with resource ID [R.string.unbookmark] ("unbookmark") if [isBookmarked] is `true` or
 * the [String] with resource ID [R.string.bookmark] ("bookmark") if [isBookmarked] is `false`.
 *
 * @param isBookmarked if `true` the article has already been bookmarked
 * @param onClick a lambda that our [IconToggleButton] should call when it is toggled.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior.
 */
@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickLabel = stringResource(
        if (isBookmarked) R.string.unbookmark else R.string.bookmark
    )
    IconToggleButton(
        checked = isBookmarked,
        onCheckedChange = { onClick() },
        modifier = modifier.semantics {
            // Use a custom click label that accessibility services can communicate to the user.
            // We only want to override the label, not the actual action, so for the action we pass null.
            this.onClick(label = clickLabel, action = null)
        }
    ) {
        Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
            contentDescription = null // handled by click label of parent
        )
    }
}

/**
 * If the display is not expanded this is shown in the [ArticleScreen] in a [BottomAppBar] along with
 * a [FavoriteButton], a [BookmarkButton], and a [TextSettingsButton]. If it is expanded it is shown
 * in a [PostTopBar] with them, which is the top bar for a Post when it is displayed next to the Home
 * feed by [HomeFeedWithArticleDetailsScreen]. It consists of an [IconButton] whose `onClick` argument
 * is our [onClick] lambda parameter and its content lambda argument is an [Icon] whose `imageVector`
 * argument causes it to display the [ImageVector] drawn by [Icons.Filled.Share] (a sideways "V")
 * and whose contentDescription arugment is the String with resource ID [R.string.cd_share] ("Share").
 *
 * @param onClick a lambda which our [IconButton] should call if it is clicked.
 */
@Composable
fun ShareButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = stringResource(id = R.string.cd_share)
        )
    }
}

/**
 * If the display is not expanded this is shown in the [ArticleScreen] in a [BottomAppBar] along with
 * a [FavoriteButton], a [BookmarkButton], and a [ShareButton]. If it is expanded it is shown in a
 * [PostTopBar] with them, which is the top bar for a Post when it is displayed next to the Home feed
 * by [HomeFeedWithArticleDetailsScreen]. It consists of an [IconButton] whose `onClick` argument is
 * our [onClick] lambda parameter and its `content` lambda argument is an [Icon] whose `imageVector`
 * argument causes it to display the [ImageVector] drawn by [R.drawable.ic_text_settings] ( (a bold
 * capital "A") and whose `contentDescription` arugment is the [String] with resource ID
 * [R.string.cd_text_settings] ("Text settings").
 *
 * @param onClick a lambda which our [IconButton] should call if it is clicked.
 */
@Composable
fun TextSettingsButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            painter = painterResource(id = R.drawable.ic_text_settings),
            contentDescription = stringResource(id = R.string.cd_text_settings)
        )
    }
}
