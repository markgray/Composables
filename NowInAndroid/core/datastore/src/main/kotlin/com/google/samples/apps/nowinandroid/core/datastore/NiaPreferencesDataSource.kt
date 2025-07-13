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
     *  `userPreferences` converted to a [ThemeBrand] enum using a `when` switch.
     *  - [UserData.darkThemeConfig] is the value of the [UserPreferences.darkThemeConfig] property
     *  of `userPreferences` converted to a [DarkThemeConfig] enum using a `when` switch.
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
     *
     * Wrapped in a `try` block intended to catch and Log any [IOException] we call the
     * [DataStore.updateData] method of our [DataStore]<[UserPreferences]> property [userPreferences]
     * and in its `tranform` suspend lambda block we capture the [UserPreferences] in variable
     * `userPreferences` and then we call the [UserPreferences.copy] method of the `userPreferences`
     * object and in its `copy` block if our [Boolean] parameter [followed] is `true` we call the
     * `put` method of the [UserPreferences.followedTopicIdsMap] map of `userPreferences` with the
     * [String] parameter [topicId] mapped to `true` and if our [Boolean] parameter [followed] is
     * `false` we call the `remove` method of the [UserPreferences.followedTopicIdsMap] map of
     * `userPreferences` with the [String] parameter [topicId]. Finally we call the
     * [UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary] method of the `userPreferences`
     * object to update the [UserPreferences.shouldHideOnboarding] property of `userPreferences` if
     * necessary.
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

    /**
     * Sets the preferred theme brand.
     * We call the [DataStore.updateData] method of our [DataStore]<[UserPreferences]> property
     * [userPreferences] and in the `transform` lambda argument we capture the [UserPreferences]
     * passed the lambda in variable `userPreferences` and then we call the [UserPreferences.copy]
     * method of `userPreferences` and in its `copy` block we set the [UserPreferences.themeBrand]
     * property to [ThemeBrandProto.THEME_BRAND_DEFAULT] if our [ThemeBrand] parameter [themeBrand] is
     * [ThemeBrand.DEFAULT], or to [ThemeBrandProto.THEME_BRAND_ANDROID] if it is
     * [ThemeBrand.ANDROID].
     *
     * @param themeBrand The desired [ThemeBrand] to set.
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        userPreferences.updateData { userPreferences: UserPreferences ->
            userPreferences.copy {
                this.themeBrand = when (themeBrand) {
                    ThemeBrand.DEFAULT -> ThemeBrandProto.THEME_BRAND_DEFAULT
                    ThemeBrand.ANDROID -> ThemeBrandProto.THEME_BRAND_ANDROID
                }
            }
        }
    }

    /**
     * Sets the preferred dynamic color preference.
     * We call the [DataStore.updateData] method of our [DataStore]<[UserPreferences]> property
     * [userPreferences] and in the `transform` lambda argument we capture the [UserPreferences]
     * passed the lambda in variable `userPreferences` and then we call the [UserPreferences.copy]
     * method of `userPreferences` and in its `copy` block we set its [UserPreferences.useDynamicColor]
     * property to our [Boolean] parameter [useDynamicColor].
     *
     * @param useDynamicColor Whether to use dynamic color (Material You).
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData { userPreferences: UserPreferences ->
            userPreferences.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    /**
     * Sets the preferred dark theme configuration.
     * We call the [DataStore.updateData] method of our [DataStore]<[UserPreferences]> property
     * [userPreferences] and in the `transform` lambda argument we capture the [UserPreferences]
     * passed the lambda in variable `userPreferences` and then we call the [UserPreferences.copy]
     * method of `userPreferences` and in its `copy` block we set the [UserPreferences.darkThemeConfig]
     * property to [DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM] if our [DarkThemeConfig]
     * parameter [darkThemeConfig] is [DarkThemeConfig.FOLLOW_SYSTEM], to
     * [DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT] if it is [DarkThemeConfig.LIGHT], or to
     * [DarkThemeConfigProto.DARK_THEME_CONFIG_DARK] if it is [DarkThemeConfig.DARK].
     *
     * @param darkThemeConfig The desired [DarkThemeConfig] to set.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData { userPreferences: UserPreferences ->
            userPreferences.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM ->
                        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM

                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    /**
     * Sets the bookmarked state of a news resource.
     *
     * Wrapped in a `try` block intended to catch and Log any [IOException] we call the
     * [DataStore.updateData] method of our [DataStore]<[UserPreferences]> property [userPreferences]
     * and in its `tranform` suspend lambda block we capture the [UserPreferences] in variable
     * `userPreferences` and then we call the [UserPreferences.copy] method of the `userPreferences`
     * object and in its `copy` block if our [Boolean] parameter [bookmarked] is `true` we call the
     * `put` method of the [UserPreferences.bookmarkedNewsResourceIdsMap] map of `userPreferences`
     * with the [String] parameter [newsResourceId] mapped to `true` and if our [Boolean] parameter
     * [bookmarked] is `false` we call the `remove` method of the
     * [UserPreferences.bookmarkedNewsResourceIdsMap] map of `userPreferences` with the [String]
     * parameter [newsResourceId].
     *
     * @param newsResourceId The ID of the news resource.
     * @param bookmarked Whether the news resource should be bookmarked or not.
     */
    suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        try {
            userPreferences.updateData { userPreferences: UserPreferences ->
                userPreferences.copy {
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

    /**
     * Sets the viewed state of a news resource.
     *
     * This method calls the [setNewsResourcesViewed] method with a list containing only the
     * [String] parameter [newsResourceId] for its `newsResourceIds` argument and the [Boolean]
     * parameter [viewed] for its `viewed` argument.
     *
     * @param newsResourceId The ID of the news resource.
     * @param viewed Whether the news resource should be marked as viewed or not.
     */
    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        setNewsResourcesViewed(newsResourceIds = listOf(newsResourceId), viewed = viewed)
    }

    /**
     * Sets the viewed state of a list of news resources.
     *
     * We call the [DataStore.updateData] method of our [DataStore]<[UserPreferences]> property
     * [userPreferences] and in the `transform` lambda argument we capture the [UserPreferences]
     * passed the lambda in variable `prefs` and then we call the [UserPreferences.copy]
     * method of `prefs` and in its `copy` block we iterate over the [List] of [String] parameter
     * [newsResourceIds] using its [Iterable.forEach] method capturing each [String] in variable
     * `id`. If our [Boolean] parameter [viewed] is `true` we call the `put` method of the
     * [UserPreferences.viewedNewsResourceIdsMap] map of `prefs` with the key `id` mapped to `true`,
     * and if our [Boolean] parameter [viewed] is `false` we call the `remove` method of the
     * [UserPreferences.viewedNewsResourceIdsMap] map of `prefs` with the key `id`.
     *
     * @param newsResourceIds The list of news resource IDs.
     * @param viewed Whether the news resources should be marked as viewed or not.
     */
    suspend fun setNewsResourcesViewed(newsResourceIds: List<String>, viewed: Boolean) {
        userPreferences.updateData { prefs: UserPreferences ->
            prefs.copy {
                newsResourceIds.forEach { id: String ->
                    if (viewed) {
                        viewedNewsResourceIds.put(key = id, value = true)
                    } else {
                        viewedNewsResourceIds.remove(key = id)
                    }
                }
            }
        }
    }

    /**
     * Retrieves the current change list versions from user preferences.
     * It maps the [UserPreferences] to [ChangeListVersions] and returns the first emitted value
     * or a default [ChangeListVersions] if no value is emitted.
     *
     * @return The current [ChangeListVersions].
     */
    suspend fun getChangeListVersions(): ChangeListVersions = userPreferences.data
        .map { userPreferences: UserPreferences ->
            ChangeListVersions(
                topicVersion = userPreferences.topicChangeListVersion,
                newsResourceVersion = userPreferences.newsResourceChangeListVersion,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    /**
     * Update the [ChangeListVersions] using the [update] lambda argument. The lambda is called with
     * the current [ChangeListVersions] and the returned [ChangeListVersions] is used to update the
     * [UserPreferences].
     *
     * Wrapped in a `try` block intended to catch and log any [IOException] that occurs, we call
     * the [DataStore.updateData] method of our [DataStore]<[UserPreferences]> property
     * [userPreferences]. In its `transform` lambda block we capture the current [UserPreferences]
     * in our variable `currentPreferences` and then we construct an [ChangeListVersions] variable
     * `updatedChangeListVersions` by calling our [update] lambda parameter with a [ChangeListVersions]
     * argument whose [ChangeListVersions.topicVersion] property is initialized from the
     * [UserPreferences.topicChangeListVersion] of `currentPreferences` and whose
     * [ChangeListVersions.newsResourceVersion] property is initialized from the
     * [UserPreferences.newsResourceChangeListVersion] of `currentPreferences`.
     *
     * Finally we call the [UserPreferences.copy] method of `currentPreferences` and in its `copy`
     * lambda block we set its [UserPreferences.topicChangeListVersion] property to the value of the
     * [ChangeListVersions.topicVersion] of `updatedChangeListVersions` and its
     * [UserPreferences.newsResourceChangeListVersion] property to the value of the
     * [ChangeListVersions.newsResourceVersion] of `updatedChangeListVersions`.
     *
     * @param update A lambda that takes the current [ChangeListVersions] and returns the updated
     * [ChangeListVersions].
     */
    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            userPreferences.updateData { currentPreferences: UserPreferences ->
                val updatedChangeListVersions: ChangeListVersions = update(
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

    /**
     * Sets the currently desired should hide onboarding state to our [Boolean] parameter
     * [shouldHideOnboarding].
     *
     * We call the [DataStore.updateData] method of our [DataStore]<[UserPreferences]> property
     * [userPreferences] and in the `transform` lambda argument we capture the [UserPreferences]
     * passed the lambda in variable `userPreferences` and then we call the [UserPreferences.copy]
     * method of `userPreferences` and in its `copy` block we set its
     * [UserPreferences.shouldHideOnboarding] property to our [Boolean] parameter
     * [shouldHideOnboarding].
     *
     * @param shouldHideOnboarding Whether onboarding should be hidden or not.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData { userPreferences: UserPreferences ->
            userPreferences.copy { this.shouldHideOnboarding = shouldHideOnboarding }
        }
    }
}

/**
 * Updates the `shouldHideOnboarding` preference based on the state of followed topics and authors.
 * If both [UserPreferences.followedTopicIds] and [UserPreferences.followedAuthorIds] are empty,
 * [UserPreferences.shouldHideOnboarding] is set to `false`.
 */
private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
    if (followedTopicIds.isEmpty() && followedAuthorIds.isEmpty()) {
        shouldHideOnboarding = false
    }
}
