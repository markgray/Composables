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

package com.google.samples.apps.nowinandroid.core.data.repository

import androidx.annotation.VisibleForTesting
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.datastore.NiaPreferencesDataSource
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * [UserDataRepository] implementation that uses [NiaPreferencesDataSource] to store the data.
 * It also uses [AnalyticsHelper] to log the changes to the user data.
 *
 * @property niaPreferencesDataSource the data source for the user data injected by HILT
 * @property analyticsHelper the analytics helper injected by HILT
 */
internal class OfflineFirstUserDataRepository @Inject constructor(
    private val niaPreferencesDataSource: NiaPreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper,
) : UserDataRepository {

    /**
     * The user data retrieved from the [NiaPreferencesDataSource.userData] property of our
     * [NiaPreferencesDataSource] property [niaPreferencesDataSource].
     */
    override val userData: Flow<UserData> =
        niaPreferencesDataSource.userData

    /**
     * Sets the followed topic IDs, delegates to the [NiaPreferencesDataSource.setFollowedTopicIds]
     * method of our [NiaPreferencesDataSource] property [niaPreferencesDataSource] with its
     * `followedTopicIds` argument set to the [Set] of [String] parameter [followedTopicIds].
     *
     * @param followedTopicIds the new set of followed topic IDs.
     */
    @VisibleForTesting
    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) =
        niaPreferencesDataSource.setFollowedTopicIds(topicIds = followedTopicIds)

    /**
     * Sets the followed state for a topic. We delegate to the
     * [NiaPreferencesDataSource.setTopicIdFollowed] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] with its `followedTopicId` argument set to the
     * [String] parameter [followedTopicId] and its `followed` argument set to the [Boolean]
     * parameter [followed]. Then we call the [AnalyticsHelper.logTopicFollowToggled] method of
     * our [AnalyticsHelper] property [analyticsHelper] with its `followedTopicId` argument set
     * to the [String] parameter [followedTopicId] and its `isFollowed` argument set to the
     * [Boolean] parameter [followed].
     *
     * @param followedTopicId the ID of the topic to set the followed state for.
     * @param followed whether the topic is followed or not.
     */
    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {
        niaPreferencesDataSource.setTopicIdFollowed(topicId = followedTopicId, followed = followed)
        analyticsHelper.logTopicFollowToggled(
            followedTopicId = followedTopicId,
            isFollowed = followed,
        )
    }

    /**
     * Sets the bookmarked state of a news resource. We delegate to the
     * [NiaPreferencesDataSource.setNewsResourceBookmarked] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] with its `newsResourceId` argument set to the
     * [String] parameter [newsResourceId] and its `bookmarked` argument set to the [Boolean]
     * parameter [bookmarked]. Then we call the [AnalyticsHelper.logNewsResourceBookmarkToggled]
     * method of our [AnalyticsHelper] property [analyticsHelper] with its `newsResourceId`
     * argument set to the [String] parameter [newsResourceId] and its `isBookmarked` argument
     * set to the [Boolean] parameter [bookmarked].
     *
     * @param newsResourceId the ID of the news resource.
     * @param bookmarked whether the news resource is bookmarked.
     */
    override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        niaPreferencesDataSource.setNewsResourceBookmarked(
            newsResourceId = newsResourceId,
            bookmarked = bookmarked,
        )
        analyticsHelper.logNewsResourceBookmarkToggled(
            newsResourceId = newsResourceId,
            isBookmarked = bookmarked,
        )
    }

    /**
     * Sets the viewed state for a news resource. We delegate to the
     * [NiaPreferencesDataSource.setNewsResourceViewed] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] with its `newsResourceId` argument set to the [String]
     * parameter [newsResourceId] and its `viewed` argument set to the [Boolean] parameter
     * [viewed].
     *
     * @param newsResourceId the ID of the news resource to set the viewed state for.
     * @param viewed whether the news resource has been viewed or not.
     */
    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) =
        niaPreferencesDataSource.setNewsResourceViewed(
            newsResourceId = newsResourceId,
            viewed = viewed,
        )

    /**
     * Sets the preferred theme brand. We delegate to the
     * [NiaPreferencesDataSource.setThemeBrand] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] with its `themeBrand` argument set to the
     * [ThemeBrand] parameter [themeBrand]. Then we call the [AnalyticsHelper.logThemeChanged]
     * method of our [AnalyticsHelper] property [analyticsHelper] with its `themeName` argument
     * set [ThemeBrand.name] of the [ThemeBrand] parameter [themeBrand].
     *
     * @param themeBrand the [ThemeBrand] to set.
     */
    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        niaPreferencesDataSource.setThemeBrand(themeBrand = themeBrand)
        analyticsHelper.logThemeChanged(themeName = themeBrand.name)
    }

    /**
     * Sets the preferred dark theme configuration. WE delegate to the
     * [NiaPreferencesDataSource.setDarkThemeConfig] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] with its `darkThemeConfig` argument set to the
     * [DarkThemeConfig] parameter [darkThemeConfig]. Then we call the
     * [AnalyticsHelper.logDarkThemeConfigChanged] method of our [AnalyticsHelper] property
     * [analyticsHelper] with its `darkThemeConfigName` argument set to the [DarkThemeConfig.name]
     * of the [DarkThemeConfig] parameter [darkThemeConfig].
     *
     * @param darkThemeConfig the [DarkThemeConfig] to set.
     */
    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        niaPreferencesDataSource.setDarkThemeConfig(darkThemeConfig = darkThemeConfig)
        analyticsHelper.logDarkThemeConfigChanged(darkThemeConfigName = darkThemeConfig.name)
    }

    /**
     * Sets the preferred dynamic color policy. We delegate to the
     * [NiaPreferencesDataSource.setDynamicColorPreference] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] with its `useDynamicColor` argument set to the [Boolean]
     * parameter [useDynamicColor]. Then we call the [AnalyticsHelper.logDynamicColorPreferenceChanged]
     * method of our [AnalyticsHelper] property [analyticsHelper] with its `useDynamicColor` argument
     * set to the [Boolean] parameter [useDynamicColor].
     *
     * @param useDynamicColor whether to use dynamic color.
     */
    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        niaPreferencesDataSource.setDynamicColorPreference(useDynamicColor = useDynamicColor)
        analyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor = useDynamicColor)
    }

    /**
     * Sets whether the onboarding should be hidden. We delegate to the
     * [NiaPreferencesDataSource.setShouldHideOnboarding] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] with its `shouldHideOnboarding` argument set to the [Boolean]
     * parameter [shouldHideOnboarding]. Then we call the [AnalyticsHelper.logOnboardingStateChanged]
     * method of our [AnalyticsHelper] property [analyticsHelper] with its `shouldHideOnboarding` argument
     * set to the [Boolean] parameter [shouldHideOnboarding].
     *
     * @param shouldHideOnboarding whether the onboarding should be hidden.
     */
    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        niaPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding = shouldHideOnboarding)
        analyticsHelper.logOnboardingStateChanged(shouldHideOnboarding = shouldHideOnboarding)
    }
}
