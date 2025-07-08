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

package com.google.samples.apps.nowinandroid.core.data.test.repository

import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.datastore.NiaPreferencesDataSource
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fake implementation of the [UserDataRepository] that returns hardcoded user data.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 *
 * @property niaPreferencesDataSource Data source for user preferences injected by HILT.
 */
class FakeUserDataRepository @Inject constructor(
    private val niaPreferencesDataSource: NiaPreferencesDataSource,
) : UserDataRepository {

    /**
     * [Flow] of [UserData] that will by emitted by the data source. We delegate to the
     * [NiaPreferencesDataSource.userData] property of our [NiaPreferencesDataSource] property
     * [niaPreferencesDataSource] to get the data.
     *
     * @return [Flow] of [UserData]
     */
    override val userData: Flow<UserData> =
        niaPreferencesDataSource.userData

    /**
     * Sets the followed topic IDs. We delegate to the [NiaPreferencesDataSource.setFollowedTopicIds]
     * property of our [NiaPreferencesDataSource] property [niaPreferencesDataSource] to set the
     * followed topics.
     *
     * @param followedTopicIds The set of topic IDs to follow.
     */
    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>): Unit =
        niaPreferencesDataSource.setFollowedTopicIds(topicIds = followedTopicIds)

    /**
     * Sets the followed state for a topic. We delegate to the
     * [NiaPreferencesDataSource.setTopicIdFollowed] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] to set the followed state for a topic.
     *
     * @param followedTopicId The ID of the topic.
     * @param followed `true` to follow the topic, `false` to unfollow it.
     */
    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean): Unit =
        niaPreferencesDataSource.setTopicIdFollowed(topicId = followedTopicId, followed = followed)

    /**
     * Sets the bookmarked state for a news resource. We delegate to the
     * [NiaPreferencesDataSource.setNewsResourceBookmarked] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] to set the bookmarked state for a news resource.
     *
     * @param newsResourceId The ID of the news resource.
     * @param bookmarked `true` to bookmark the news resource, `false` to unbookmark it.
     */
    override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        niaPreferencesDataSource.setNewsResourceBookmarked(
            newsResourceId = newsResourceId,
            bookmarked = bookmarked,
        )
    }

    /**
     * Sets the viewed state for a news resource. We delegate to the
     * [NiaPreferencesDataSource.setNewsResourceViewed] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] to set the viewed state for a news resource.
     *
     * @param newsResourceId The ID of the news resource.
     * @param viewed `true` to mark the news resource as viewed, `false` to mark it as not viewed.
     */
    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean): Unit =
        niaPreferencesDataSource.setNewsResourceViewed(
            newsResourceId = newsResourceId,
            viewed = viewed,
        )

    /**
     * Sets the theme brand. We delegate to the [NiaPreferencesDataSource.setThemeBrand] method of
     * our [NiaPreferencesDataSource] property [niaPreferencesDataSource] to set the theme brand.
     *
     * @param themeBrand The theme brand to set.
     */
    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        niaPreferencesDataSource.setThemeBrand(themeBrand = themeBrand)
    }

    /**
     * Sets the dark theme config. We delegate to the
     * [NiaPreferencesDataSource.setDarkThemeConfig] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] to set the dark theme config.
     *
     * @param darkThemeConfig The dark theme config to set.
     */
    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        niaPreferencesDataSource.setDarkThemeConfig(darkThemeConfig = darkThemeConfig)
    }

    /**
     * Sets the dynamic color preference. We delegate to the
     * [NiaPreferencesDataSource.setDynamicColorPreference] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] to set the dynamic color preference.
     *
     * @param useDynamicColor `true` to use dynamic color, `false` to use the default color.
     */
    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        niaPreferencesDataSource.setDynamicColorPreference(useDynamicColor = useDynamicColor)
    }

    /**
     * Sets the value of the flag indicating whether onboarding should be hidden or not. We delegate
     * to the [NiaPreferencesDataSource.setShouldHideOnboarding] method of our [NiaPreferencesDataSource]
     * property [niaPreferencesDataSource] to set the value.
     *
     * @param shouldHideOnboarding `true` to hide onboarding, `false` to show it.
     */
    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        niaPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding = shouldHideOnboarding)
    }
}
