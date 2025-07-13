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
 * Unit test for [IntToStringIdsMigration].
 *
 * This test verifies the migration of topic and author IDs from integer to string format
 * within the user preferences. It ensures that after the migration:
 *  - Integer IDs are correctly converted to their string equivalents.
 *  - The `hasDoneIntToStringIdMigration` flag is set to true, indicating the completion
 *   of the migration process.
 */
class IntToStringIdsMigrationTest {

    /**
     * Test that the [IntToStringIdsMigration] correctly migrates deprecated integer topic IDs
     * to string topic IDs and marks the migration as complete.
     *
     * This test performs the following steps:
     *  1. Sets up initial [UserPreferences] with deprecated integer topic IDs.
     *  2. Asserts that there are no string topic IDs present before the migration.
     *  3. Runs the [IntToStringIdsMigration.migrate] function.
     *  4. Asserts that the deprecated integer topic IDs have been successfully migrated to
     *  string topic IDs in the resulting [UserPreferences].
     *  5. Asserts that the `hasDoneIntToStringIdMigration` flag is set to true, indicating
     *  that the migration has been completed.
     */
    @Test
    fun IntToStringIdsMigration_should_migrate_topic_ids(): TestResult = runTest {
        // Set up existing preferences with topic int ids
        val preMigrationUserPreferences: UserPreferences = userPreferences {
            deprecatedIntFollowedTopicIds.addAll(listOf(1, 2, 3))
        }
        // Assert that there are no string topic ids yet
        assertEquals(
            expected = emptyList<String>(),
            actual = preMigrationUserPreferences.deprecatedFollowedTopicIdsList,
        )

        // Run the migration
        val postMigrationUserPreferences: UserPreferences =
            IntToStringIdsMigration.migrate(currentData = preMigrationUserPreferences)

        // Assert the deprecated int topic ids have been migrated to the string topic ids
        assertEquals(
            expected = userPreferences {
                deprecatedFollowedTopicIds.addAll(listOf("1", "2", "3"))
                hasDoneIntToStringIdMigration = true
            },
            actual = postMigrationUserPreferences,
        )

        // Assert that the migration has been marked complete
        assertTrue(actual = postMigrationUserPreferences.hasDoneIntToStringIdMigration)
    }

    /**
     * Test that the [IntToStringIdsMigration] correctly migrates deprecated integer author IDs
     * to string author IDs and marks the migration as complete.
     *
     * This test performs the following steps:
     *  1. Sets up initial [UserPreferences] with deprecated integer author IDs.
     *  2. Asserts that there are no string author IDs present before the migration.
     *  3. Runs the [IntToStringIdsMigration.migrate] function.
     *  4. Asserts that the deprecated integer author IDs have been successfully migrated to
     *  string author IDs in the resulting [UserPreferences].
     *  5. Asserts that the `hasDoneIntToStringIdMigration` flag is set to true, indicating
     *  that the migration has been completed.
     */
    @Test
    fun IntToStringIdsMigration_should_migrate_author_ids(): TestResult = runTest {
        // Set up existing preferences with author int ids
        val preMigrationUserPreferences: UserPreferences = userPreferences {
            deprecatedIntFollowedAuthorIds.addAll(listOf(4, 5, 6))
        }
        // Assert that there are no string author ids yet
        assertEquals(
            expected = emptyList<String>(),
            actual = preMigrationUserPreferences.deprecatedFollowedAuthorIdsList,
        )

        // Run the migration
        val postMigrationUserPreferences: UserPreferences =
            IntToStringIdsMigration.migrate(currentData = preMigrationUserPreferences)

        // Assert the deprecated int author ids have been migrated to the string author ids
        assertEquals(
            expected = userPreferences {
                deprecatedFollowedAuthorIds.addAll(listOf("4", "5", "6"))
                hasDoneIntToStringIdMigration = true
            },
            actual = postMigrationUserPreferences,
        )

        // Assert that the migration has been marked complete
        assertTrue(actual = postMigrationUserPreferences.hasDoneIntToStringIdMigration)
    }
}
