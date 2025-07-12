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

package com.google.samples.apps.nowinandroid.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * Data source for user preferences. This class is responsible for updating and exposing user
 * data such as bookmarked news resources, viewed news resources, followed topics, theme brand,
 * dark theme configuration, dynamic color preference, and onboarding status.
 * It uses [DataStore] to persist user preferences.
 *
 * @property userPreferences [DataStore] that stores user preferences.
 */
class NiaPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    /**
     * Stream of [UserData] created from the [Flow] of [UserPreferences] returned by the
     * [DataStore.data] property of our [DataStore]<[UserPreferences]> property [userPreferences].
     *
     * We use the [Flow.map] method of the [Flow] of [UserPreferences] returned by the
     * [DataStore.data] property of our [DataStore]<[UserPreferences]> property [userPreferences]
     * and in its `transform` block we capture the [UserPreferences] in variable `userPreferences`
     * then emit a [UserData] object created from the [UserPreferences] with the following
     * arguments:
     *  - [UserData.bookmarkedNewsResources] is the set of news resource ids that the user has
     *  bookmarked and we initialize this to a [Set] of [String] that are the keys to the
     *  [UserPreferences.bookmarkedNewsResourceIdsMap] map of `userPreferences`.
     *  - [UserData.viewedNewsResources] is the set of news resource ids that the user has viewed
     *  and we initialize this to a [Set] of [String] that are the keys to the
     *  [UserPreferences.viewedNewsResourceIdsMap] map of `userPreferences`.
     *  - [UserData.followedTopics] is the set of topic ids that the user has followed and we
     *  initialize this to a [Set] of [String] that are the keys to the
     *  [UserPreferences.followedTopicIdsMap] map of `userPreferences`.
     *  - [UserData.themeBrand] is the value of the [UserPreferences.themeBrand] property of
     *  `userPreferences` converted to a [ThemeBrand] enum.
     *  - [UserData.darkThemeConfig] is the value of the [UserPreferences.darkThemeConfig] property
     *  of `userPreferences` converted to a [DarkThemeConfig] enum.
     *  - [UserData.useDynamicColor] is the value of the [UserPreferences.useDynamicColor] property
     *  of `userPreferences`.
     *  - [UserData.shouldHideOnboarding] is the value of the [UserPreferences.shouldHideOnboarding]
     *  property of `userPreferences`.
     */
    val userData: Flow<UserData> = userPreferences.data
        .map { userPreferences: UserPreferences ->
            UserData(
                bookmarkedNewsResources = userPreferences.bookmarkedNewsResourceIdsMap.keys,
                viewedNewsResources = userPreferences.viewedNewsResourceIdsMap.keys,
                followedTopics = userPreferences.followedTopicIdsMap.keys,
                themeBrand = when (userPreferences.themeBrand) {
                    null,
                    ThemeBrandProto.THEME_BRAND_UNSPECIFIED,
                    ThemeBrandProto.UNRECOGNIZED,
                    ThemeBrandProto.THEME_BRAND_DEFAULT,
                        -> ThemeBrand.DEFAULT

                    ThemeBrandProto.THEME_BRAND_ANDROID -> ThemeBrand.ANDROID
                },
                darkThemeConfig = when (userPreferences.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                        ->
                        DarkThemeConfig.FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
                        DarkThemeConfig.LIGHT

                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                useDynamicColor = userPreferences.useDynamicColor,
                shouldHideOnboarding = userPreferences.shouldHideOnboarding,
            )
        }

    /**
     * Sets the followed topic IDs. Wrapped in a `try` block intended to catch and Log any
     * [IOException] we call the [DataStore.updateData] method of our [DataStore]<[UserPreferences]>
     * property [userPreferences] capturing the [UserPreferences] in variable `userPreferences` and
     * then we call the [UserPreferences.copy] method of the `userPreferences` object and in its
     * `copy` block we call the `clear` method of the [UserPreferences.followedTopicIdsMap] map of
     * `userPreferences` and then we call the `putAll` method of the
     * [UserPreferences.followedTopicIdsMap] to add all of the [Set] of [String] paramter [topicIds]
     * mapped to `true`. Finally we call the [UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary]
     * method of the `userPreferences` object to update the [UserPreferences.shouldHideOnboarding]
     * property of `userPreferences` if necessary.
     *
     * @param topicIds The set of topic IDs to follow.
     */
    suspend fun setFollowedTopicIds(topicIds: Set<String>) {
        try {
            userPreferences.updateData { userPreferences: UserPreferences ->
                userPreferences.copy {
                    followedTopicIds.clear()
                    followedTopicIds.putAll(map = topicIds.associateWith { true })
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    /**
     * Sets the followed state of a topic.
     * TODO: Continue here.
     *
     * @param topicId The ID of the topic.
     * @param followed Whether the topic should be followed or not.
     */
    suspend fun setTopicIdFollowed(topicId: String, followed: Boolean) {
        try {
            userPreferences.updateData { userPreferences: UserPreferences ->
                userPreferences.copy {
                    if (followed) {
                        followedTopicIds.put(key = topicId, value = true)
                    } else {
                        followedTopicIds.remove(key = topicId)
                    }
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        userPreferences.updateData {
            it.copy {
                this.themeBrand = when (themeBrand) {
                    ThemeBrand.DEFAULT -> ThemeBrandProto.THEME_BRAND_DEFAULT
                    ThemeBrand.ANDROID -> ThemeBrandProto.THEME_BRAND_ANDROID
                }
            }
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM ->
                        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM

                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (bookmarked) {
                        bookmarkedNewsResourceIds.put(newsResourceId, true)
                    } else {
                        bookmarkedNewsResourceIds.remove(newsResourceId)
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        setNewsResourcesViewed(listOf(newsResourceId), viewed)
    }

    suspend fun setNewsResourcesViewed(newsResourceIds: List<String>, viewed: Boolean) {
        userPreferences.updateData { prefs ->
            prefs.copy {
                newsResourceIds.forEach { id ->
                    if (viewed) {
                        viewedNewsResourceIds.put(id, true)
                    } else {
                        viewedNewsResourceIds.remove(id)
                    }
                }
            }
        }
    }

    suspend fun getChangeListVersions(): ChangeListVersions = userPreferences.data
        .map {
            ChangeListVersions(
                topicVersion = it.topicChangeListVersion,
                newsResourceVersion = it.newsResourceChangeListVersion,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    /**
     * Update the [ChangeListVersions] using [update].
     */
    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            userPreferences.updateData { currentPreferences ->
                val updatedChangeListVersions = update(
                    ChangeListVersions(
                        topicVersion = currentPreferences.topicChangeListVersion,
                        newsResourceVersion = currentPreferences.newsResourceChangeListVersion,
                    ),
                )

                currentPreferences.copy {
                    topicChangeListVersion = updatedChangeListVersions.topicVersion
                    newsResourceChangeListVersion = updatedChangeListVersions.newsResourceVersion
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy { this.shouldHideOnboarding = shouldHideOnboarding }
        }
    }
}

private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
    if (followedTopicIds.isEmpty() && followedAuthorIds.isEmpty()) {
        shouldHideOnboarding = false
    }
}
