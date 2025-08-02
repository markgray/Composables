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
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.analytics.LocalAnalyticsHelper
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource

/**
 * An extension on [LazyListScope] defining a feed with news resources.
 * Depending on the [feedState], this might emit no items.
 *
 * @param feedState The state of the feed, determining what is displayed.
 * @param onNewsResourcesCheckedChanged A callback invoked when the user changes the saved state of a
 * news resource.
 * @param onNewsResourceViewed A callback invoked when the user views a news resource.
 * @param onTopicClick A callback invoked when the user clicks on a topic chip.
 * @param onExpandedCardClick A callback invoked when the user clicks on an expanded card.
 */
@SuppressLint("UseKtx")
fun LazyStaggeredGridScope.newsFeed(
    feedState: NewsFeedUiState,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onExpandedCardClick: () -> Unit = {},
) {
    when (feedState) {
        NewsFeedUiState.Loading -> Unit
        is NewsFeedUiState.Success -> {
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "newsFeedItem" },
            ) { userNewsResource: UserNewsResource ->
                val context: Context = LocalContext.current
                val analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current
                val backgroundColor: Int = MaterialTheme.colorScheme.background.toArgb()

                NewsResourceCardExpanded(
                    userNewsResource = userNewsResource,
                    isBookmarked = userNewsResource.isSaved,
                    onClick = {
                        onExpandedCardClick()
                        analyticsHelper.logNewsResourceOpened(
                            newsResourceId = userNewsResource.id,
                        )
                        launchCustomChromeTab(
                            context = context,
                            uri = Uri.parse(userNewsResource.url),
                            toolbarColor = backgroundColor,
                        )

                        onNewsResourceViewed(userNewsResource.id)
                    },
                    hasBeenViewed = userNewsResource.hasBeenViewed,
                    onToggleBookmark = {
                        onNewsResourcesCheckedChanged(
                            userNewsResource.id,
                            !userNewsResource.isSaved,
                        )
                    },
                    onTopicClick = onTopicClick,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(),
                )
            }
        }
    }
}

/**
 * Launches a custom Chrome tab with the specified URI and toolbar color.
 *
 * @param context The context to use for launching the custom tab.
 * @param uri The URI to open in the custom tab.
 * @param toolbarColor The color to use for the custom tab's toolbar.
 */
fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
    val customTabBarColor: CustomTabColorSchemeParams = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor).build()
    val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(customTabBarColor)
        .build()

    customTabsIntent.launchUrl(context, uri)
}

/**
 * A sealed hierarchy describing the state of the feed of news resources.
 */
sealed interface NewsFeedUiState {
    /**
     * The feed is still loading.
     */
    data object Loading : NewsFeedUiState

    /**
     * The feed is loaded with the given list of news resources.
     */
    data class Success(
        /**
         * The list of news resources contained in this feed.
         */
        val feed: List<UserNewsResource>,
    ) : NewsFeedUiState
}

/**
 * Preview of the [newsFeed] loading state.
 */
@Preview
@Composable
private fun NewsFeedLoadingPreview() {
    NiaTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(minSize = 300.dp)) {
            newsFeed(
                feedState = NewsFeedUiState.Loading,
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

/**
 * Preview function for the news feed when it is populated with content.
 *
 * @param userNewsResources The list of user news resources to display.
 */
@Preview
@Preview(device = Devices.TABLET)
@Composable
private fun NewsFeedContentPreview(
    @PreviewParameter(provider = UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(minSize = 300.dp)) {
            newsFeed(
                feedState = NewsFeedUiState.Success(feed = userNewsResources),
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}
