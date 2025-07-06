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

package com.google.samples.apps.nowinandroid.core.data.repository

import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent.Param
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper

/**
 * Logs a news resource "bookmark toggled event".
 *
 * @param newsResourceId the ID of the news resource.
 * @param isBookmarked whether the news resource is bookmarked.
 */
internal fun AnalyticsHelper.logNewsResourceBookmarkToggled(
    newsResourceId: String,
    isBookmarked: Boolean,
) {
    val eventType: String = if (isBookmarked) "news_resource_saved" else "news_resource_unsaved"
    val paramKey: String =
        if (isBookmarked) "saved_news_resource_id" else "unsaved_news_resource_id"
    logEvent(
        event = AnalyticsEvent(
            type = eventType,
            extras = listOf(
                Param(key = paramKey, value = newsResourceId),
            ),
        ),
    )
}

/**
 * Logs a topic followed/unfollowed event.
 *
 * @param followedTopicId the ID of the topic.
 * @param isFollowed whether the topic is followed.
 */
internal fun AnalyticsHelper.logTopicFollowToggled(followedTopicId: String, isFollowed: Boolean) {
    val eventType: String = if (isFollowed) "topic_followed" else "topic_unfollowed"
    val paramKey: String = if (isFollowed) "followed_topic_id" else "unfollowed_topic_id"
    logEvent(
        event = AnalyticsEvent(
            type = eventType,
            extras = listOf(
                Param(key = paramKey, value = followedTopicId),
            ),
        ),
    )
}

/**
 * Logs a theme changed event.
 *
 * @param themeName the name of the theme.
 */
internal fun AnalyticsHelper.logThemeChanged(themeName: String) =
    logEvent(
        event = AnalyticsEvent(
            type = "theme_changed",
            extras = listOf(
                Param(key = "theme_name", value = themeName),
            ),
        ),
    )

/**
 * Logs a dark theme config changed event.
 *
 * @param darkThemeConfigName the name of the dark theme config.
 */
internal fun AnalyticsHelper.logDarkThemeConfigChanged(darkThemeConfigName: String) =
    logEvent(
        event = AnalyticsEvent(
            type = "dark_theme_config_changed",
            extras = listOf(
                Param(key = "dark_theme_config", value = darkThemeConfigName),
            ),
        ),
    )

/**
 * Logs a dynamic color preference changed event.
 *
 * @param useDynamicColor whether the dynamic color is used.
 */
internal fun AnalyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor: Boolean) =
    logEvent(
        event = AnalyticsEvent(
            type = "dynamic_color_preference_changed",
            extras = listOf(
                Param(key = "dynamic_color_preference", value = useDynamicColor.toString()),
            ),
        ),
    )

/**
 * Logs an onboarding state changed event.
 *
 * @param shouldHideOnboarding whether the onboarding screen should be hidden.
 */
internal fun AnalyticsHelper.logOnboardingStateChanged(shouldHideOnboarding: Boolean) {
    val eventType: String = if (shouldHideOnboarding) "onboarding_complete" else "onboarding_reset"
    logEvent(
        event = AnalyticsEvent(type = eventType),
    )
}
