/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.google.samples.apps.nowinandroid.core.database.model.RecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [RecentSearchQueryEntity] access
 */
@Dao
interface RecentSearchQueryDao {
    /**
     * Get the most recent search queries up to the limit. The [Query] annotation specifies the SQL
     * query to be executed. The meaning of the SQL query is:
     *  - `SELECT * FROM recentSearchQueries`: This query selects all columns from the
     *  `recentSearchQueries` table.
     *  - `ORDER BY queriedDate DESC`: This keyword is used to sort the results in descending order
     *  based on the `queriedDate` column.
     *  - `LIMIT :limit`: This keyword is used to limit the number of results returned by the query
     *  to our [Int] parameter [limit].
     *
     * @param limit The maximum number of recent search queries to get.
     * @return A flow of a list of recent search queries.
     */
    @Query(value = "SELECT * FROM recentSearchQueries ORDER BY queriedDate DESC LIMIT :limit")
    fun getRecentSearchQueryEntities(limit: Int): Flow<List<RecentSearchQueryEntity>>

    /**
     * Inserts or replaces [RecentSearchQueryEntity] in the db under the Fts tables.
     * If the [RecentSearchQueryEntity] is new, it is inserted.
     * If the [RecentSearchQueryEntity] is already directly in the db, it is replaced.
     * If the last query was the same, the timestamp is updated.
     *
     * @param recentSearchQuery To be inserted or replaced.
     */
    @Upsert
    suspend fun insertOrReplaceRecentSearchQuery(recentSearchQuery: RecentSearchQueryEntity)

    /**
     * Clears all recent search queries. The [Query] annotation specifies the SQL query to be executed.
     * The meaning of the SQL query is:
     *  - `DELETE FROM recentSearchQueries`: This query deletes all rows from the `recentSearchQueries`
     *  table.
     */
    @Query(value = "DELETE FROM recentSearchQueries")
    suspend fun clearRecentSearchQueries()
}
