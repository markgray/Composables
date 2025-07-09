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
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceFtsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NewsResourceFtsEntity] access.
 */
@Dao
interface NewsResourceFtsDao {
    /**
     * Inserts [newsResources] into the FTS table. If a [NewsResourceFtsEntity] already exists, it will
     * be replaced.
     *
     * @param newsResources The list of news resources to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(newsResources: List<NewsResourceFtsEntity>)

    /**
     * Searches for news resources that match the [String] parameter [query]. The [Query] annotation
     * specifies the SQL query to be executed. The meaning of the SQL query is:
     *  - `SELECT newsResourceId FROM newsResourcesFts WHERE newsResourcesFts MATCH :query`: This
     *  query selects the `newsResourceId` column from the `newsResourcesFts` table. It applies a
     *  FTS5 match against the `newsResourcesFts` column using the provided [query] parameter.
     *
     * @param query The search query.
     * @return A flow of lists of news resource IDs that match the query.
     */
    @Query("SELECT newsResourceId FROM newsResourcesFts WHERE newsResourcesFts MATCH :query")
    fun searchAllNewsResources(query: String): Flow<List<String>>

    /**
     * Gets the number of FTS news resources. The [Query] annotation specifies the SQL query to be
     * executed. The meaning of the SQL query is:
     *  - `SELECT count(*) FROM newsResourcesFts`: This query counts the number of rows in the
     *  `newsResourcesFts` table.
     *
     * @return The number of FTS news resources.
     */
    @Query("SELECT count(*) FROM newsResourcesFts")
    fun getCount(): Flow<Int>
}
