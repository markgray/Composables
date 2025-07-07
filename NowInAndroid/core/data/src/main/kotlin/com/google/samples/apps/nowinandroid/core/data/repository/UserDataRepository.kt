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

import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing user data.
 */
interface UserDataRepository {

    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Sets the user's currently followed topics
     *
     * @param followedTopicIds set of topic ids that the user is following.
     */
    suspend fun setFollowedTopicIds(followedTopicIds: Set<String>)

    /**
     * Sets the user's newly followed/unfollowed topic.
     *
     * @param followedTopicId id of the topic that was followed/unfollowed.
     * @param followed `true` if the topic should be followed, `false` if it should be unfollowed.
     */
    suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean)

    /**
     * Updates the bookmarked status for a news resource.
     *
     * @param newsResourceId id of the news resource.
     * @param bookmarked `true` if the news resource should be bookmarked, `false` if it should be
     * unbookmarked.
     */
    suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean)

    /**
     * Updates the viewed status for a news resource.
     *
     * @param newsResourceId id of the news resource.
     * @param viewed `true` if the news resource should be marked as viewed, `false` if it should be
     *
     */
    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean)

    /**
     * Sets the desired theme brand.
     *
     * @param themeBrand new theme brand to use.
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand)

    /**
     * Sets the desired dark theme config.
     *
     * @param darkThemeConfig new dark theme config to use.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets the preferred dynamic color config.
     *
     * @param useDynamicColor whether to use a dynamic color scheme.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    /**
     * Sets whether the user has completed the onboarding process.
     *
     * @param shouldHideOnboarding `true` if the user has completed onboarding, `false` otherwise.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
}
