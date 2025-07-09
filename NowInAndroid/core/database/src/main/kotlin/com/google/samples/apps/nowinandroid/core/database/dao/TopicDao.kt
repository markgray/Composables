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

package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [TopicEntity] access
 */
@Dao
interface TopicDao {
    /**
     * Fetches the topic with the given ID. The [Query] annotation specifies the SQL query to be
     * executed. The meaning of the SQL query is:
     *  - `SELECT * FROM topics`: This query selects all columns from the `topics` table.
     *  - `WHERE id = :topicId`: This keyword is used to filter the results based on the value of the
     *  `id` column. The [String] parameter [topicId] is used to specify the value of the `id` column.
     *
     * @param topicId The ID of the topic.
     * @return A [Flow] of the topic with the given ID.
     */
    @Query(
        value = """
        SELECT * FROM topics
        WHERE id = :topicId
    """,
    )
    fun getTopicEntity(topicId: String): Flow<TopicEntity>

    /**
     * Get all topics from the db. The [Query] annotation specifies the SQL query to be executed.
     * The meaning of the SQL query is:
     *  - `SELECT * FROM topics`: This query selects all columns from the `topics` table.
     *
     * @return A [Flow] of a list of all topics.
     */
    @Query(value = "SELECT * FROM topics")
    fun getTopicEntities(): Flow<List<TopicEntity>>

    /**
     * Fetches all topic entities from the db. The [Query] annotation specifies the SQL query to be
     * executed. The meaning of the SQL query is:
     *  - `SELECT * FROM topics`: This query selects all columns from the `topics` table.
     *
     * @return A [List] of [TopicEntity] of all topics in the `topics` table..
     */
    @Query(value = "SELECT * FROM topics")
    suspend fun getOneOffTopicEntities(): List<TopicEntity>

    /**
     * Fetches the topics with the given IDs. The [Query] annotation specifies the SQL query to be
     * executed. The meaning of the SQL query is:
     *  - `SELECT * FROM topics`: This query selects all columns from the `topics` table.
     *  - `WHERE id IN (:ids)`: This keyword is used to filter the results based on the values in the
     *  `id` column. The [Set] of [String]s parameter [ids] is used to specify the values of the
     *  `id` column.
     *
     * @param ids The IDs of the topics.
     * @return A [Flow] of the topics with the given IDs.
     */
    @Query(
        value = """
        SELECT * FROM topics
        WHERE id IN (:ids)
    """,
    )
    fun getTopicEntities(ids: Set<String>): Flow<List<TopicEntity>>

    /**
     * Inserts [topicEntities] into the db if they don't exist, and ignores those that do.
     * The [Insert] annotation specifies that this method should insert data into the database.
     * The `onConflict` parameter is set to `OnConflictStrategy.IGNORE`, which means that if a
     * topic with the same primary key already exists in the database, the insert operation will be
     * ignored.
     *
     * @param topicEntities The list of topics to insert.
     * @return A list of row IDs that were inserted. If a topic was ignored, the corresponding value
     * in the list will be -1.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTopics(topicEntities: List<TopicEntity>): List<Long>

    /**
     * Inserts or updates [List] of [TopicEntity] parameter [entities] in the database. If a row
     * with the same primary key already exists, it will be updated. Otherwise, a new row will be
     * inserted.
     *
     * @param entities The list of [TopicEntity] objects to be upserted.
     */
    @Upsert
    suspend fun upsertTopics(entities: List<TopicEntity>)

    /**
     * Deletes rows in the db matching the [List] of [String] parameter [ids]. The [Query] annotation
     * specifies the SQL query to be executed. The meaning of the SQL query is:
     * - `DELETE FROM topics`: This query deletes rows from the `topics` table.
     * - `WHERE id IN (:ids)`: This keyword is used to filter the rows to be deleted based on the
     * values in the `id` column. The [List] of [String]s parameter [ids] is used to specify the
     * values of the `id` column.
     *
     * @param ids The list of topic IDs to delete.
     */
    @Query(
        value = """
            DELETE FROM topics
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteTopics(ids: List<String>)
}
