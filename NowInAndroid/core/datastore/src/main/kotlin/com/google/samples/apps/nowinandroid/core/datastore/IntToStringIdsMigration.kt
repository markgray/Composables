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

import androidx.datastore.core.DataMigration

/**
 * Migrates saved ids from [Int] to [String] types.
 *
 * The `*_ids` fields were initially stored as `Int` types. This migration
 * changes them to `String` types.
 */
internal object IntToStringIdsMigration : DataMigration<UserPreferences> {

    /**
     * This migration does not have any clean up logic as it is a one time migration.
     */
    override suspend fun cleanUp() = Unit

    /**
     * Migrates the user preferences from using integer IDs for followed topics and authors
     * to using string IDs.
     *
     * This migration involves the following steps:
     *  1. Clears the existing string-based followed topic IDs.
     *  2. Converts the integer-based followed topic IDs to strings and adds them to the
     *  string-based list.
     *  3. Clears the integer-based followed topic IDs.
     *  4. Clears the existing string-based followed author IDs.
     *  5. Converts the integer-based followed author IDs to strings and adds them to the
     *  string-based list.
     *  6. Clears the integer-based followed author IDs.
     *  7. Sets a flag indicating that the migration has been completed.
     *
     * @param currentData The current user preferences data.
     * @return The updated user preferences data after migration.
     */
    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate topic ids
            deprecatedFollowedTopicIds.clear()
            deprecatedFollowedTopicIds.addAll(
                values = currentData.deprecatedIntFollowedTopicIdsList.map(transform = Int::toString),
            )
            deprecatedIntFollowedTopicIds.clear()

            // Migrate author ids
            deprecatedFollowedAuthorIds.clear()
            deprecatedFollowedAuthorIds.addAll(
                values = currentData.deprecatedIntFollowedAuthorIdsList.map(transform = Int::toString),
            )
            deprecatedIntFollowedAuthorIds.clear()

            // Mark migration as complete
            hasDoneIntToStringIdMigration = true
        }

    /**
     * Determines whether the migration should be performed.
     *
     * The migration should be performed if the [UserPreferences.hasDoneIntToStringIdMigration]
     * flag is false, indicating that the migration has not yet been completed.
     *
     * @param currentData The current user preferences data.
     * @return `true` if the migration should be performed, `false` otherwise.
     */
    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneIntToStringIdMigration
}
