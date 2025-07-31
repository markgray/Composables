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

package com.google.samples.apps.nowinandroid.core.testing.repository

import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

/**
 * An empty default [UserData] that one can use to test its properties separately.
 */
val emptyUserData: UserData = UserData(
    bookmarkedNewsResources = emptySet(),
    viewedNewsResources = emptySet(),
    followedTopics = emptySet(),
    themeBrand = ThemeBrand.DEFAULT,
    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    useDynamicColor = false,
    shouldHideOnboarding = false,
)

/**
 * Test implementation for [UserDataRepository]
 */
class TestUserDataRepository : UserDataRepository {
    /**
     * The backing hot flow for the [UserData] user data for testing.
     */
    private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)

    /**
     * The current [UserData] from the backing hot flow.
     */
    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: emptyUserData

    /**
     * The public read-only access to our [MutableSharedFlow] of [UserData] property [_userData].
     */
    override val userData: Flow<UserData> = _userData.filterNotNull()

    /**
     * Sets the [UserData.followedTopics] followed topic IDs property of the current [UserData].
     *
     * @param followedTopicIds The set of topic IDs that are being followed.
     */
    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {
        _userData.tryEmit(value = currentUserData.copy(followedTopics = followedTopicIds))
    }

    /**
     * Sets the followed state for the [Topic] whose [Topic.id] matches the [String] parameter
     * [followedTopicId] by adding it to the [UserData.followedTopics] set if [followed] is `true`,
     * or removing it from the set if it is `false`.
     *
     * @param followedTopicId The topic ID to update.
     * @param followed Whether the topic is followed or not.
     */
    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {
        currentUserData.let { current: UserData ->
            val followedTopics: Set<String> = if (followed) {
                current.followedTopics + followedTopicId
            } else {
                current.followedTopics - followedTopicId
            }

            _userData.tryEmit(value = current.copy(followedTopics = followedTopics))
        }
    }

    /**
     * Sets whether the [NewsResource] whose [NewsResource.id] matches the [String] parameter
     * [newsResourceId] has been bookmarked by adding it to the [UserData.bookmarkedNewsResources]
     * set if [bookmarked] is `true`, or removing it from the set if it is `false`.
     *
     * @param newsResourceId The news resource ID to update.
     * @param bookmarked Whether the news resource has been bookmarked.
     */
    override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        currentUserData.let { current: UserData ->
            val bookmarkedNews: Set<String> = if (bookmarked) {
                current.bookmarkedNewsResources + newsResourceId
            } else {
                current.bookmarkedNewsResources - newsResourceId
            }

            _userData.tryEmit(value = current.copy(bookmarkedNewsResources = bookmarkedNews))
        }
    }

    /**
     * Sets the viewed state of the [NewsResource] whose [NewsResource.id] matches the [String]
     * parameter [newsResourceId] by adding it to the [UserData.viewedNewsResources] set if
     * [viewed] is `true`, or removing it from the set if it is `false`.
     *
     * @param newsResourceId The news resource ID to update.
     * @param viewed Whether the news resource has been viewed.
     */
    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        currentUserData.let { current: UserData ->
            _userData.tryEmit(
                value = current.copy(
                    viewedNewsResources =
                        if (viewed) {
                            current.viewedNewsResources + newsResourceId
                        } else {
                            current.viewedNewsResources - newsResourceId
                        },
                ),
            )
        }
    }

    /**
     * Sets the preferred theme brand.
     *
     * @param themeBrand The [ThemeBrand] to set.
     */
    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        currentUserData.let { current: UserData ->
            _userData.tryEmit(value = current.copy(themeBrand = themeBrand))
        }
    }

    /**
     * Sets the [UserData.darkThemeConfig] dark theme config property of the current [UserData].
     *
     * @param darkThemeConfig The dark theme config to set.
     */
    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        currentUserData.let { current: UserData ->
            _userData.tryEmit(value = current.copy(darkThemeConfig = darkThemeConfig))
        }
    }

    /**
     * Sets the [UserData.useDynamicColor] dynamic color property of the current [UserData].
     *
     * @param useDynamicColor Whether the user prefers a dynamic color.
     */
    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        currentUserData.let { current: UserData ->
            _userData.tryEmit(value = current.copy(useDynamicColor = useDynamicColor))
        }
    }

    /**
     * Sets the value of the [UserData.shouldHideOnboarding] property of the current [UserData].
     *
     * @param shouldHideOnboarding Whether the onboarding screen should be hidden.
     */
    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        currentUserData.let { current: UserData ->
            _userData.tryEmit(value = current.copy(shouldHideOnboarding = shouldHideOnboarding))
        }
    }

    /**
     * A test-only API to allow setting of user data directly.
     *
     * @param userData The [UserData] to set.
     */
    fun setUserData(userData: UserData) {
        _userData.tryEmit(value = userData)
    }
}
