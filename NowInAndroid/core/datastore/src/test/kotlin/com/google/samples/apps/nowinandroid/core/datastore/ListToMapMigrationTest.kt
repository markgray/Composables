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

@file:Suppress("TestFunctionName")

package com.google.samples.apps.nowinandroid.core.datastore

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [ListToMapMigration]
 */
class ListToMapMigrationTest {

    /**
     * Tests that the [ListToMapMigration] migrates topic ids from the deprecated list to the new map
     * and sets the `has_done_list_to_map_migration` flag to true.
     * TODO: Continue here.
     */
    @Test
    fun ListToMapMigration_should_migrate_topic_ids(): TestResult = runTest {
        // Set up existing preferences with topic ids
        val preMigrationUserPreferences: UserPreferences = userPreferences {
            deprecatedFollowedTopicIds.addAll(listOf("1", "2", "3"))
        }
        // Assert that there are no topic ids in the map yet
        assertEquals(
            expected = emptyMap<String, Boolean>(),
            actual = preMigrationUserPreferences.followedTopicIdsMap,
        )

        // Run the migration
        val postMigrationUserPreferences: UserPreferences =
            ListToMapMigration.migrate(currentData = preMigrationUserPreferences)

        // Assert the deprecated topic ids have been migrated to the topic ids map
        assertEquals(
            expected = mapOf("1" to true, "2" to true, "3" to true),
            actual = postMigrationUserPreferences.followedTopicIdsMap,
        )

        // Assert that the migration has been marked complete
        assertTrue(actual = postMigrationUserPreferences.hasDoneListToMapMigration)
    }

    @Test
    fun ListToMapMigration_should_migrate_author_ids(): TestResult = runTest {
        // Set up existing preferences with author ids
        val preMigrationUserPreferences: UserPreferences = userPreferences {
            deprecatedFollowedAuthorIds.addAll(listOf("4", "5", "6"))
        }
        // Assert that there are no author ids in the map yet
        assertEquals(
            expected = emptyMap<String, Boolean>(),
            actual = preMigrationUserPreferences.followedAuthorIdsMap,
        )

        // Run the migration
        val postMigrationUserPreferences: UserPreferences =
            ListToMapMigration.migrate(currentData = preMigrationUserPreferences)

        // Assert the deprecated author ids have been migrated to the author ids map
        assertEquals(
            expected = mapOf("4" to true, "5" to true, "6" to true),
            actual = postMigrationUserPreferences.followedAuthorIdsMap,
        )

        // Assert that the migration has been marked complete
        assertTrue(actual = postMigrationUserPreferences.hasDoneListToMapMigration)
    }

    @Test
    fun ListToMapMigration_should_migrate_bookmarks(): TestResult = runTest {
        // Set up existing preferences with bookmarks
        val preMigrationUserPreferences: UserPreferences = userPreferences {
            deprecatedBookmarkedNewsResourceIds.addAll(listOf("7", "8", "9"))
        }
        // Assert that there are no bookmarks in the map yet
        assertEquals(
            expected = emptyMap<String, Boolean>(),
            actual = preMigrationUserPreferences.bookmarkedNewsResourceIdsMap,
        )

        // Run the migration
        val postMigrationUserPreferences: UserPreferences =
            ListToMapMigration.migrate(currentData = preMigrationUserPreferences)

        // Assert the deprecated bookmarks have been migrated to the bookmarks map
        assertEquals(
            expected = mapOf("7" to true, "8" to true, "9" to true),
            actual = postMigrationUserPreferences.bookmarkedNewsResourceIdsMap,
        )

        // Assert that the migration has been marked complete
        assertTrue(actual = postMigrationUserPreferences.hasDoneListToMapMigration)
    }
}
