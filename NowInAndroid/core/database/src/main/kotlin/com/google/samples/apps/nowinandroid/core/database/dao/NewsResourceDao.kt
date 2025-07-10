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
import androidx.room.Transaction
import androidx.room.Upsert
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceTopicCrossRef
import com.google.samples.apps.nowinandroid.core.database.model.PopulatedNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NewsResource] and [NewsResourceEntity] access
 */
@Dao
interface NewsResourceDao {

    /**
     * Fetches news resources that match the query parameters. The list is ordered by publish date.
     *
     * The [Transaction] annotation ensures that the database operation is performed within a single
     * atomic transaction. If any part of the operation fails, the entire transaction is rolled back,
     * maintaining data consistency. The [Query] annotation specifies the SQL query to be executed.
     * The meaning of the SQL query is:
     *  - `SELECT * FROM news_resources`: Select all columns from the `news_resources` table.
     *  - `WHERE` clause: This clause filters the results based on certain conditions. It uses two
     *  CASE statements to conditionally apply filters.
     *  - `CASE WHEN :useFilterNewsIds THEN id IN (:filterNewsIds) ELSE 1 END`: This CASE statement
     *  filters the results based on the [useFilterNewsIds] parameter. If [useFilterNewsIds] is `true`,
     *  it filters the results by checking if the `id` of the news resource is present in the
     *  [filterNewsIds] set. If [useFilterNewsIds] is `false`, it always returns 1, meaning no filtering.
     *  - `AND`: This keyword is used to combine multiple conditions in the WHERE clause.
     *  - `CASE WHEN :useFilterTopicIds THEN id IN (SELECT news_resource_id FROM news_resources_topics
     *  WHERE topic_id IN (:filterTopicIds)) ELSE 1 END`: This CASE statement filters the results
     *  based on the [useFilterTopicIds] parameter. If [useFilterTopicIds] is `true`, it filters the
     *  results by checking if the `id` of the news resource is present in the `news_resources_topics`
     *  table and the `topic_id` is present in the [filterTopicIds] set. If [useFilterTopicIds] is
     *  `false`, it always returns 1, meaning no filtering.
     *  - `ORDER BY publish_date DESC`: This keyword is used to sort the results in descending order
     *  based on the `publish_date` column.
     *
     * @param useFilterTopicIds Whether to filter by topic IDs.
     * @param filterTopicIds The set of topic IDs to filter by. If [useFilterTopicIds] is false,
     * this parameter is ignored.
     * @param useFilterNewsIds Whether to filter by news resource IDs.
     * @param filterNewsIds The set of news resource IDs to filter by. If [useFilterNewsIds] is
     * false, this parameter is ignored.
     * @return A [Flow] of a [List] of [PopulatedNewsResource] objects that match the query
     * parameters. The list is ordered by publish date in descending order.
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM news_resources
            WHERE 
                CASE WHEN :useFilterNewsIds
                    THEN id IN (:filterNewsIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterTopicIds
                    THEN id IN
                        (
                            SELECT news_resource_id FROM news_resources_topics
                            WHERE topic_id IN (:filterTopicIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    fun getNewsResources(
        useFilterTopicIds: Boolean = false,
        filterTopicIds: Set<String> = emptySet(),
        useFilterNewsIds: Boolean = false,
        filterNewsIds: Set<String> = emptySet(),
    ): Flow<List<PopulatedNewsResource>>

    /**
     * Fetches ids of news resources that match the query parameters. The list is ordered by publish
     * date.
     *
     * The [Transaction] annotation ensures that the database operation is performed within a single
     * atomic transaction. If any part of the operation fails, the entire transaction is rolled back,
     * maintaining data consistency. The [Query] annotation specifies the SQL query to be executed.
     * The meaning of the SQL query is:
     *  - `SELECT id FROM news_resources`: Select the `id` column from the `news_resources` table.
     *  - `WHERE` clause: This clause filters the results based on certain conditions. It uses two
     *  CASE statements to conditionally apply filters.
     *  - `CASE WHEN :useFilterNewsIds THEN id IN (:filterNewsIds) ELSE 1 END`: This CASE statement
     *  filters the results based on the [useFilterNewsIds] parameter. If [useFilterNewsIds] is `true`,
     *  it filters the results by checking if the `id` of the news resource is present in the
     *  [filterNewsIds] set. If [useFilterNewsIds] is `false`, it always returns 1, meaning no filtering.
     *  - `AND`: This keyword is used to combine multiple conditions in the WHERE clause.
     *  - `CASE WHEN :useFilterTopicIds THEN id IN (SELECT news_resource_id FROM news_resources_topics
     *  WHERE topic_id IN (:filterTopicIds)) ELSE 1 END`: This CASE statement filters the results
     *  based on the [useFilterTopicIds] parameter. If [useFilterTopicIds] is `true`, it filters the
     *  results by checking if the `id` of the news resource is present in the `news_resources_topics`
     *  table and the `topic_id` is present in the [filterTopicIds] set. If [useFilterTopicIds] is
     *  `false`, it always returns 1, meaning no filtering.
     *  - `ORDER BY publish_date DESC`: This keyword is used to sort the results in descending order
     *  based on the `publish_date` column.
     *
     * @param useFilterTopicIds Whether to filter by topic IDs.
     * @param filterTopicIds The set of topic IDs to filter by. If [useFilterTopicIds] is false,
     * this parameter is ignored.
     * @param useFilterNewsIds Whether to filter by news resource IDs.
     * @param filterNewsIds The set of news resource IDs to filter by. If [useFilterNewsIds] is
     * false, this parameter is ignored.
     * @return A [Flow] of a list of [String] objects that match the query parameters. The list is
     * ordered by publish date in descending order.
     */
    @Transaction
    @Query(
        value = """
            SELECT id FROM news_resources
            WHERE 
                CASE WHEN :useFilterNewsIds
                    THEN id IN (:filterNewsIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterTopicIds
                    THEN id IN
                        (
                            SELECT news_resource_id FROM news_resources_topics
                            WHERE topic_id IN (:filterTopicIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    fun getNewsResourceIds(
        useFilterTopicIds: Boolean = false,
        filterTopicIds: Set<String> = emptySet(),
        useFilterNewsIds: Boolean = false,
        filterNewsIds: Set<String> = emptySet(),
    ): Flow<List<String>>

    /**
     * Inserts or updates [newsResourceEntities] in the database.
     *
     * The @[Upsert] annotation efficiently handles both insertion of new entities and updates
     * to existing ones based on their primary keys. If an entity with a matching primary key
     * already exists, it will be updated; otherwise, a new entity will be inserted.
     * This operation is performed as a suspend function, making it suitable for execution
     * within a coroutine scope without blocking the main thread.
     *
     * @param newsResourceEntities A list of [NewsResourceEntity] objects to be inserted or updated.
     */
    @Upsert
    suspend fun upsertNewsResources(newsResourceEntities: List<NewsResourceEntity>)

    /**
     * Inserts [newsResourceTopicCrossReferences] into the db if they don't exist, otherwise ignores
     * them.
     * The @[Insert] annotation, with its `onConflict` strategy set to `OnConflictStrategy.IGNORE`,
     * ensures that if a cross-reference attempting to be inserted already exists in the database
     * (based on its primary key), the insertion operation for that specific cross-reference is
     * skipped, and no error is thrown. This is useful for preventing duplicate entries while
     * efficiently adding new associations. This operation is performed as a suspend function,
     * making it suitable for execution within a coroutine scope without blocking the main thread.
     *
     * @param newsResourceTopicCrossReferences A list of [NewsResourceTopicCrossRef] objects
     * representing the associations between news resources and topics to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTopicCrossRefEntities(
        newsResourceTopicCrossReferences: List<NewsResourceTopicCrossRef>,
    )

    /**
     * Deletes rows in the `news_resources` table that match the specified list of [ids].
     *
     * The [Query] annotation specifies the SQL query to be executed.
     * The meaning of the SQL query is:
     *  - `DELETE FROM news_resources`: This clause specifies that rows should be deleted from the
     *  `news_resources` table.
     *  - `WHERE id in (:ids)`: This clause filters the rows to be deleted. It selects only those
     *  rows where the `id` column matches any of the values in the provided [ids] list.
     *
     * This operation is performed as a suspend function, making it suitable for execution
     * within a coroutine scope without blocking the main thread.
     *
     * @param ids A list of [String] representing the IDs of the news resources to be deleted.
     */
    @Query(
        value = """
            DELETE FROM news_resources
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteNewsResources(ids: List<String>)
}
