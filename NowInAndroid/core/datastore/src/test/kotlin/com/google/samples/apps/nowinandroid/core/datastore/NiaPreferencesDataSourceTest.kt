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

import com.google.samples.apps.nowinandroid.core.datastore.test.InMemoryDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [NiaPreferencesDataSource].
 */
class NiaPreferencesDataSourceTest {

    /**
     * The scope for testing coroutines.
     *
     * It uses an [UnconfinedTestDispatcher] as its context to run coroutines eagerly.
     */
    private val testScope = TestScope(context = UnconfinedTestDispatcher())

    /**
     * The instance of [NiaPreferencesDataSource] being tested.
     * This is initialized in the [setup] method.
     */
    private lateinit var subject: NiaPreferencesDataSource

    /**
     * Sets up the test environment by initializing the [NiaPreferencesDataSource] property
     * [subject] with an in-memory data store. This ensures that each test runs with a fresh
     * instance of [NiaPreferencesDataSource].
     */
    @Before
    fun setup() {
        subject = NiaPreferencesDataSource(
            userPreferences = InMemoryDataStore(initialValue = UserPreferences.getDefaultInstance()),
        )
    }

    /**
     * Test that checks if the `shouldHideOnboarding` flag is `false` by default.
     * This ensures that the onboarding process is shown to new users.
     */
    @Test
    fun shouldHideOnboardingIsFalseByDefault(): TestResult = testScope.runTest {
        assertFalse(actual = subject.userData.first().shouldHideOnboarding)
    }

    /**
     * Tests that the `shouldHideOnboarding` property is correctly set to `true` when
     * [NiaPreferencesDataSource.setShouldHideOnboarding] is called with `true`. It verifies
     * that the updated value is reflected in the [NiaPreferencesDataSource.userData] flow.
     */
    @Test
    fun userShouldHideOnboardingIsTrueWhenSet(): TestResult = testScope.runTest {
        subject.setShouldHideOnboarding(shouldHideOnboarding = true)
        assertTrue(actual = subject.userData.first().shouldHideOnboarding)
    }

    /**
     * Test case to verify that if a user has completed onboarding by selecting a single topic
     * and then unfollows that topic, the onboarding should be shown again.
     */
    @Test
    fun userShouldHideOnboarding_unfollowsLastTopic_shouldHideOnboardingIsFalse(): TestResult =
        testScope.runTest {
            // Given: user completes onboarding by selecting a single topic.
            subject.setTopicIdFollowed(topicId = "1", followed = true)
            subject.setShouldHideOnboarding(shouldHideOnboarding = true)

            // When: they unfollow that topic.
            subject.setTopicIdFollowed(topicId = "1", followed = false)

            // Then: onboarding should be shown again
            assertFalse(actual = subject.userData.first().shouldHideOnboarding)
        }

    /**
     * Test case to verify that if a user has completed onboarding by selecting several topics
     * and then unfollows all of those topics, the onboarding should be shown again.
     */
    @Test
    fun userShouldHideOnboarding_unfollowsAllTopics_shouldHideOnboardingIsFalse(): TestResult =
        testScope.runTest {
            // Given: user completes onboarding by selecting several topics.
            subject.setFollowedTopicIds(topicIds = setOf("1", "2"))
            subject.setShouldHideOnboarding(shouldHideOnboarding = true)

            // When: they unfollow those topics.
            subject.setFollowedTopicIds(topicIds = emptySet())

            // Then: onboarding should be shown again
            assertFalse(actual = subject.userData.first().shouldHideOnboarding)
        }

    /**
     * Test case to verify that the `useDynamicColor` property is `false` by default.
     * This means that dynamic theming is not enabled when the app is first launched.
     */
    @Test
    fun shouldUseDynamicColorFalseByDefault(): TestResult = testScope.runTest {
        assertFalse(actual = subject.userData.first().useDynamicColor)
    }

    /**
     * Tests that the `useDynamicColor` property is correctly set to `true` when
     * [NiaPreferencesDataSource.setDynamicColorPreference] is called with `true`. It verifies
     * that the updated value is reflected in the [NiaPreferencesDataSource.userData] flow.
     */
    @Test
    fun userShouldUseDynamicColorIsTrueWhenSet(): TestResult = testScope.runTest {
        subject.setDynamicColorPreference(useDynamicColor = true)
        assertTrue(actual = subject.userData.first().useDynamicColor)
    }
}
