/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.analytics.LocalAnalyticsHelper
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource

/**
 * Extension function for displaying a [List] of [NewsResourceCardExpanded] backed by a list of
 * [UserNewsResource]s.
 *
 * [onToggleBookmark] defines the action invoked when a user wishes to bookmark an item
 * When a news resource card is tapped it will open the news resource URL in a Chrome Custom Tab.
 *
 * TODO: Continue here.
 *
 * @param items (state) the [List] of [UserNewsResource] news resources to display in the UI.
 * @param onToggleBookmark (event) the callback invoked when a user wishes to bookmark an item.
 * @param onNewsResourceViewed (event) callback invoked when a news resource is viewed.
 * @param onTopicClick (event) callback invoked when an item topic is clicked.
 * @param itemModifier the modifier to apply to each list item.
 */
@SuppressLint("UseKtx")
fun LazyListScope.userNewsResourceCardItems(
    items: List<UserNewsResource>,
    onToggleBookmark: (item: UserNewsResource) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    itemModifier: Modifier = Modifier,
): Unit = items(
    items = items,
    key = { it.id },
    itemContent = { userNewsResource: UserNewsResource ->
        val resourceUrl: Uri = Uri.parse(userNewsResource.url)
        val backgroundColor: Int = MaterialTheme.colorScheme.background.toArgb()
        val context: Context = LocalContext.current
        val analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current

        NewsResourceCardExpanded(
            userNewsResource = userNewsResource,
            isBookmarked = userNewsResource.isSaved,
            hasBeenViewed = userNewsResource.hasBeenViewed,
            onToggleBookmark = { onToggleBookmark(userNewsResource) },
            onClick = {
                analyticsHelper.logNewsResourceOpened(
                    newsResourceId = userNewsResource.id,
                )
                launchCustomChromeTab(
                    context = context,
                    uri = resourceUrl,
                    toolbarColor = backgroundColor,
                )
                onNewsResourceViewed(userNewsResource.id)
            },
            onTopicClick = onTopicClick,
            modifier = itemModifier,
        )
    },
)
