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
 * Migrates from using lists to using maps for user data.
 *
 * The `*_ids` fields were initially lists of strings. They are now maps of strings to booleans,
 * where the boolean value indicates whether the item is followed (true) or not (false).
 *
 * This migration iterates through the old list fields and populates the new map fields. It also
 * clears the old list fields and sets a flag to indicate that the migration has been completed.
 */
internal object ListToMapMigration : DataMigration<UserPreferences> {

    /**
     * This migration does not require any clean up because it is clearing the deprecated fields
     * as part of the migration.
     */
    override suspend fun cleanUp() = Unit

    /**
     * Migrates the user data from using lists to maps.
     *
     * This function takes the current user preferences as input and returns the migrated user
     * preferences.
     *
     * The migration process involves the following steps:
     *  1. Clear the existing map fields for followed topic IDs, followed author IDs, and bookmarked
     *  news resource IDs.
     *  2. Populate the map fields by iterating through the corresponding deprecated list fields and
     *  associating each ID with `true`.
     *  3. Clear the deprecated list fields.
     *  4. Set the `hasDoneListToMapMigration` flag to `true` to indicate that the migration has
     *  been completed.
     *
     * @param currentData The current user preferences.
     * @return The migrated user preferences.
     */
    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate topic id lists
            followedTopicIds.clear()
            followedTopicIds.putAll(
                currentData.deprecatedFollowedTopicIdsList.associateWith { true },
            )
            deprecatedFollowedTopicIds.clear()

            // Migrate author ids
            followedAuthorIds.clear()
            followedAuthorIds.putAll(
                currentData.deprecatedFollowedAuthorIdsList.associateWith { true },
            )
            deprecatedFollowedAuthorIds.clear()

            // Migrate bookmarks
            bookmarkedNewsResourceIds.clear()
            bookmarkedNewsResourceIds.putAll(
                currentData.deprecatedBookmarkedNewsResourceIdsList.associateWith { true },
            )
            deprecatedBookmarkedNewsResourceIds.clear()

            // Mark migration as complete
            hasDoneListToMapMigration = true
        }

    /**
     * Returns true if the migration has not been completed yet.
     *
     * The migration should be run if the `hasDoneListToMapMigration` flag is false.
     *
     * @param currentData The current user preferences.
     * @return True if the migration should be run, false otherwise.
     */
    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneListToMapMigration
}
