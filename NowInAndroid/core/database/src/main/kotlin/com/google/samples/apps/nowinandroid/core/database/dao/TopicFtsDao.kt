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
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.samples.apps.nowinandroid.core.database.model.TopicFtsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [TopicFtsEntity] access.
 */
@Dao
interface TopicFtsDao {
    /**
     * Inserts [TopicFtsEntity] instances into the db. If a [TopicFtsEntity] instance has the same
     * primary key as a [TopicFtsEntity] already in the db, it replaces it.
     *
     * @param topics The list of [TopicFtsEntity] to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicFtsEntity>)

    /**
     * Searches all topics for a query. The [Query] annotation specifies the SQL query to be executed.
     * The meaning of the SQL query is:
     *  - `SELECT topicId FROM topicsFts WHERE topicsFts MATCH :query`: This query selects the
     *  `topicId` column from the `topicsFts` table. It applies a FTS5 match against the `topicsFts`
     *  column using the provided [String] parameter [query].
     *
     * @param query The query to search for.
     * @return A list of topic IDs that match the query.
     */
    @Query("SELECT topicId FROM topicsFts WHERE topicsFts MATCH :query")
    fun searchAllTopics(query: String): Flow<List<String>>

    /**
     * Gets the count of all topics in the FTS table. The [Query] annotation specifies the SQL query
     * to be executed. The meaning of the SQL query is:
     *  - `SELECT count(*) FROM topicsFts`: This query counts the number of rows in the `topicsFts`
     *  table.
     *
     * @return The count of all topics.
     */
    @Query("SELECT count(*) FROM topicsFts")
    fun getCount(): Flow<Int>
}
