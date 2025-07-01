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

import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.analytics.NoOpAnalyticsHelper
import com.google.samples.apps.nowinandroid.core.datastore.NiaPreferencesDataSource
import com.google.samples.apps.nowinandroid.core.datastore.UserPreferences
import com.google.samples.apps.nowinandroid.core.datastore.test.InMemoryDataStore
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [OfflineFirstUserDataRepository].
 */
class OfflineFirstUserDataRepositoryTest {
    /**
     * The [TestScope] used in this test. The [UnconfinedTestDispatcher] as its `context` is similar
     * to `Dispatchers.Unconfined`: the tasks that it executes are not confined to any particular
     * thread and form an event loop; it's different in that it skips delays, as all TestDispatchers
     * do. Like `Dispatchers.Unconfined`, this one does not provide guarantees about the execution
     * order when several coroutines are queued in this dispatcher. However, we ensure that the
     * launch and async blocks at the top level of runTest are entered eagerly. This allows
     * launching child coroutines and not calling runCurrent for them to start executing.
     */
    private val testScope = TestScope(context = UnconfinedTestDispatcher())

    /**
     * The [OfflineFirstUserDataRepository] being tested, provides access to user preferences.
     */
    private lateinit var subject: OfflineFirstUserDataRepository

    /**
     * The backing [NiaPreferencesDataSource] used to manage the user preferences data.
     */
    private lateinit var niaPreferencesDataSource: NiaPreferencesDataSource

    /**
     * The [NoOpAnalyticsHelper] used in this test. An [AnalyticsHelper] which does nothing.
     */
    private val analyticsHelper = NoOpAnalyticsHelper()

    /**
     * Sets up the test dependencies before each test.
     * Initializes [niaPreferencesDataSource] with an in-memory data store and
     * creates an instance of [OfflineFirstUserDataRepository] with the mock dependencies.
     */
    @Before
    fun setup() {
        niaPreferencesDataSource = NiaPreferencesDataSource(
            userPreferences = InMemoryDataStore(initialValue = UserPreferences.getDefaultInstance()),
        )

        subject = OfflineFirstUserDataRepository(
            niaPreferencesDataSource = niaPreferencesDataSource,
            analyticsHelper = analyticsHelper,
        )
    }

    /**
     * Tests that the default user data is correctly emitted by the repository.
     * It verifies that all initial user preferences are set to their default values.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - Call [assertEquals] to verify that the `expected` argument a new instance of [UserData]
     *  constructed with the default values of all properties **matches** the `actual` argument
     *  which is the [UserData] that results from using the [Flow.first] function of the
     *  [OfflineFirstUserDataRepository.userData] property of our [OfflineFirstUserDataRepository]
     *  property [subject] to collect the latest [UserData].
     */
    @Test
    fun offlineFirstUserDataRepository_default_user_data_is_correct(): TestResult =
        testScope.runTest {
            assertEquals(
                expected = UserData(
                    bookmarkedNewsResources = emptySet(),
                    viewedNewsResources = emptySet(),
                    followedTopics = emptySet(),
                    themeBrand = ThemeBrand.DEFAULT,
                    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                    useDynamicColor = false,
                    shouldHideOnboarding = false,
                ),
                actual = subject.userData.first(),
            )
        }

    /**
     * Tests that the logic for toggling followed topics is delegated to [NiaPreferencesDataSource].
     * It verifies that when a topic is followed or unfollowed, the change is correctly reflected
     * in both the repository's user data and the underlying data source.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - Call the [OfflineFirstUserDataRepository.setTopicIdFollowed] method of our
     *  [OfflineFirstUserDataRepository] property [subject] with the `followedTopicId` argument "0",
     *  and the `followed` argument set to `true` in order to follow the "0" topic.
     *  - Call [assertEquals] to verify that the `expected` argument a new instance of [Set]
     *  containing the string "0" **matches** the `actual` argument which is the result of feeding
     *  [Flow] of [UserData] property [OfflineFirstUserDataRepository.userData] of our
     *  [OfflineFirstUserDataRepository] property [subject] to the [Flow.map] extension function
     *  with its `transform` argument the [UserData.followedTopics] property to create a [Flow] of
     *  [Set] of [String] which we feed to its [Flow.first] method to collect the latest [Set].
     *  - Call the [OfflineFirstUserDataRepository.setTopicIdFollowed] method of our
     *  [OfflineFirstUserDataRepository] property [subject] with the `followedTopicId` argument "1",
     *  and the `followed` argument set to `true` in order to follow the "1" topic.
     *  - Call [assertEquals] to verify that the `expected` argument a new instance of [Set]
     *  containing the strings "0" and "1" **matches** the `actual` argument which is the result of
     *  feeding [Flow] of [UserData] property [OfflineFirstUserDataRepository.userData] of our
     *  [OfflineFirstUserDataRepository] property [subject] to the [Flow.map] extension function
     *  with its `transform` argument the [UserData.followedTopics] property to create a [Flow] of
     *  [Set] of [String] which we feed to its [Flow.first] method to collect the latest [Set].
     * TODO: Continue here.
     */
    @Test
    fun offlineFirstUserDataRepository_toggle_followed_topics_logic_delegates_to_nia_preferences(): TestResult =
        testScope.runTest {
            subject.setTopicIdFollowed(followedTopicId = "0", followed = true)

            assertEquals(
                expected = setOf("0"),
                actual = subject.userData
                    .map { it.followedTopics }
                    .first(),
            )

            subject.setTopicIdFollowed(followedTopicId = "1", followed = true)

            assertEquals(
                expected = setOf("0", "1"),
                actual = subject.userData
                    .map { it.followedTopics }
                    .first(),
            )

            assertEquals(
                expected = niaPreferencesDataSource.userData
                    .map { it.followedTopics }
                    .first(),
                actual = subject.userData
                    .map { it.followedTopics }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_followed_topics_logic_delegates_to_nia_preferences(): TestResult =
        testScope.runTest {
            subject.setFollowedTopicIds(followedTopicIds = setOf("1", "2"))

            assertEquals(
                expected = setOf("1", "2"),
                actual = subject.userData
                    .map { it.followedTopics }
                    .first(),
            )

            assertEquals(
                expected = niaPreferencesDataSource.userData
                    .map { it.followedTopics }
                    .first(),
                actual = subject.userData
                    .map { it.followedTopics }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_bookmark_news_resource_logic_delegates_to_nia_preferences(): TestResult =
        testScope.runTest {
            subject.setNewsResourceBookmarked(newsResourceId = "0", bookmarked = true)

            assertEquals(
                expected = setOf("0"),
                actual = subject.userData
                    .map { it.bookmarkedNewsResources }
                    .first(),
            )

            subject.setNewsResourceBookmarked(newsResourceId = "1", bookmarked = true)

            assertEquals(
                expected = setOf("0", "1"),
                actual = subject.userData
                    .map { it.bookmarkedNewsResources }
                    .first(),
            )

            assertEquals(
                expected = niaPreferencesDataSource.userData
                    .map { it.bookmarkedNewsResources }
                    .first(),
                actual = subject.userData
                    .map { it.bookmarkedNewsResources }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_update_viewed_news_resources_delegates_to_nia_preferences(): TestResult =
        runTest {
            subject.setNewsResourceViewed(newsResourceId = "0", viewed = true)

            assertEquals(
                expected = setOf("0"),
                actual = subject.userData
                    .map { it.viewedNewsResources }
                    .first(),
            )

            subject.setNewsResourceViewed(newsResourceId = "1", viewed = true)

            assertEquals(
                expected = setOf("0", "1"),
                actual = subject.userData
                    .map { it.viewedNewsResources }
                    .first(),
            )

            assertEquals(
                expected = niaPreferencesDataSource.userData
                    .map { it.viewedNewsResources }
                    .first(),
                actual = subject.userData
                    .map { it.viewedNewsResources }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_theme_brand_delegates_to_nia_preferences(): TestResult =
        testScope.runTest {
            subject.setThemeBrand(themeBrand = ThemeBrand.ANDROID)

            assertEquals(
                expected = ThemeBrand.ANDROID,
                actual = subject.userData
                    .map { it.themeBrand }
                    .first(),
            )
            assertEquals(
                expected = ThemeBrand.ANDROID,
                actual = niaPreferencesDataSource
                    .userData
                    .map { it.themeBrand }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_dynamic_color_delegates_to_nia_preferences(): TestResult =
        testScope.runTest {
            subject.setDynamicColorPreference(useDynamicColor = true)

            assertEquals(
                expected = true,
                actual = subject.userData
                    .map { it.useDynamicColor }
                    .first(),
            )
            assertEquals(
                expected = true,
                actual = niaPreferencesDataSource
                    .userData
                    .map { it.useDynamicColor }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_dark_theme_config_delegates_to_nia_preferences(): TestResult =
        testScope.runTest {
            subject.setDarkThemeConfig(darkThemeConfig = DarkThemeConfig.DARK)

            assertEquals(
                expected = DarkThemeConfig.DARK,
                actual = subject.userData
                    .map { it.darkThemeConfig }
                    .first(),
            )
            assertEquals(
                expected = DarkThemeConfig.DARK,
                actual = niaPreferencesDataSource
                    .userData
                    .map { it.darkThemeConfig }
                    .first(),
            )
        }

    @Test
    fun whenUserCompletesOnboarding_thenRemovesAllInterests_shouldHideOnboardingIsFalse(): TestResult =
        testScope.runTest {
            subject.setFollowedTopicIds(followedTopicIds = setOf("1"))
            subject.setShouldHideOnboarding(shouldHideOnboarding = true)
            assertTrue(actual = subject.userData.first().shouldHideOnboarding)

            subject.setFollowedTopicIds(followedTopicIds = emptySet())
            assertFalse(actual = subject.userData.first().shouldHideOnboarding)
        }
}
