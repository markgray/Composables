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

package com.google.samples.apps.nowinandroid.core.data.repository

import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceDao
import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceFtsDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicFtsDao
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceFtsEntity
import com.google.samples.apps.nowinandroid.core.database.model.PopulatedNewsResource
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.database.model.TopicFtsEntity
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.database.model.asFtsEntity
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.SearchResult
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of [SearchContentsRepository] that uses FTS5 ("Full Text Search") on a Room
 * database.
 *
 * The `searchContents` query has been split into two queries because it's not possible to use
 * `INNER JOIN` in a FTS virtual table.
 *
 * @see <a href="https://www.sqlite.org/fts5.html#fts5_restrictions">FTS5 restrictions</a>
 *
 * @property newsResourceDao the DAO for news resources injected by HILT.
 * @property newsResourceFtsDao the DAO for news resources FTS injected by HILT.
 * @property topicDao the DAO for topics injected by HILT.
 * @property topicFtsDao the DAO for topics FTS injected by HILT.
 * @property ioDispatcher the coroutine dispatcher to use for IO operations injected by HILT.
 */
internal class DefaultSearchContentsRepository @Inject constructor(
    private val newsResourceDao: NewsResourceDao,
    private val newsResourceFtsDao: NewsResourceFtsDao,
    private val topicDao: TopicDao,
    private val topicFtsDao: TopicFtsDao,
    @param:Dispatcher(niaDispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
) : SearchContentsRepository {

    /**
     * Populates the FTS tables for news resources and topics.
     * This method is called when the FTS tables are empty or outdated.
     * It retrieves all news resources and topics from the corresponding DAOs,
     * transforms them into FTS entities, and inserts them into the FTS tables.
     *
     * We call [withContext] to launch a coroutine with its `context` our [CoroutineDispatcher]
     * property [ioDispatcher]. In its [CoroutineScope] `block` suspend lambda argument we first
     * call the [NewsResourceFtsDao.insertAll] method of [NewsResourceFtsDao] property
     * [newsResourceFtsDao] to insert the `newsResources` [List] of [PopulatedNewsResource]
     * that results from feeding the [Flow] of [List] of [PopulatedNewsResource] returned by the
     * [NewsResourceDao.getNewsResources] method of [NewsResourceDao] property [newsResourceDao]
     * to the [Flow.first] method then feeding the [List] of [PopulatedNewsResource] to the
     * [Iterable.map] method to transform each [PopulatedNewsResource] to a [NewsResourceFtsEntity].
     * Then we call the [TopicFtsDao.insertAll] method of [TopicFtsDao] property [topicFtsDao]
     * to insert the `topics` [List] of [TopicEntity] that results from feeding the [List] of
     * [TopicEntity] returned by the [TopicDao.getOneOffTopicEntities] method of [TopicDao]
     * property [topicDao] to the [Iterable.map] method to transform each [TopicEntity] to a
     * [TopicFtsEntity].
     */
    override suspend fun populateFtsData() {
        withContext(context = ioDispatcher) {
            @Suppress("RedundantValueArgument")
            newsResourceFtsDao.insertAll(
                newsResources = newsResourceDao.getNewsResources(
                    useFilterTopicIds = false,
                    useFilterNewsIds = false,
                )
                    .first()
                    .map(transform = PopulatedNewsResource::asFtsEntity),
            )
            topicFtsDao.insertAll(
                topics = topicDao.getOneOffTopicEntities().map { it.asFtsEntity() },
            )
        }
    }

    /**
     * Searches the FTS tables for news resources and topics that match the given query.
     *
     * The Flow combines the results from two separate queries: one for news resources and one for
     * topics. The results are then combined into a [SearchResult] object and emitted as a [Flow].
     *
     * We start by initializing our [Flow] of [List] of [String] variable `newsResourceIds` with the
     * result of calling the [NewsResourceFtsDao.searchAllNewsResources] method of [NewsResourceFtsDao]
     * property [newsResourceFtsDao] with the argument `query = "*$searchQuery*"` (the query is
     * surrounded by asterisks to match the query when it's in the middle of a word). We initialize
     * our [Flow] of [List] of [String] variable `topicIds` with the result of calling the
     * [TopicFtsDao.searchAllTopics] method of [TopicFtsDao] property [topicFtsDao] with the argument
     * `query = "*$searchQuery*"`. We initialize our [Flow] of [List] of [PopulatedNewsResource]
     * variable `newsResourcesFlow` with the result of feeding the [Flow] of [List] of [String]
     * variable `newsResourceIds` to its [Flow.mapLatest] method with the `transform` suspend lambda
     * argument a lambda that converts the [List] of [String] to a [Set] of [String] and then we
     * feed that to its [Flow.distinctUntilChanged] method filter out duplicate values, and that
     * we feed that to its [Flow.flatMapLatest] method with the `transform` suspend lambda argument
     * a lambda that calls the [NewsResourceDao.getNewsResources] method of [NewsResourceDao] property
     * with its `useFilterNewsIds` argument set to `true` and its `filterNewsIds` argument set to
     * the [Set] of [String] passed the lambda.
     *
     * We initialize our [Flow] of [List] of [TopicEntity] variable `topicsFlow` with the result of
     * feeding the [Flow] of [List] of [String] variable `topicIds` to its [Flow.mapLatest] method
     * with the `transform` suspend lambda argument a lambda converts the [List] of [String] to a
     * [Set] of [String] and then we feed that to its [Flow.distinctUntilChanged] method filter out
     * duplicate values, and that we feed that to its [Flow.flatMapLatest] method with the
     * `transform` suspend lambda argument a lambda that calls the [TopicDao.getTopicEntities] method
     * of [TopicDao] property [topicDao].
     *
     * We then use the [combine] function to combine the `newsResourcesFlow` and `topicsFlow` [Flow]s
     * and in its `transform` suspend lambda argument we acccept the [List] of [PopulatedNewsResource]
     * passed the lambda in variable `newsResources` and the [List] of [TopicEntity] passed the lambda
     * in variable `topics` and return a [SearchResult] object with its `topics` property set to the
     * result of calling the [Iterable.map] method of [List] of [TopicEntity] variable `topics` with
     * the `transform` lambda argument a lambda that converts each [TopicEntity] to a [Topic] and its
     * `newsResources` argument set to the result of calling the [Iterable.map] method of [List] of
     * [PopulatedNewsResource] variable `newsResources` with the `transform` lambda argument a lambda
     * that converts each [PopulatedNewsResource] to a [NewsResource].
     *
     * @param searchQuery the query to search for.
     * @return a Flow of [SearchResult] objects that contains the search results.
     */
    override fun searchContents(searchQuery: String): Flow<SearchResult> {
        // Surround the query by asterisks to match the query when it's in the middle of
        // a word
        val newsResourceIds: Flow<List<String>> =
            newsResourceFtsDao.searchAllNewsResources(query = "*$searchQuery*")
        val topicIds: Flow<List<String>> = topicFtsDao.searchAllTopics(query = "*$searchQuery*")

        val newsResourcesFlow: Flow<List<PopulatedNewsResource>> = newsResourceIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest {
                newsResourceDao.getNewsResources(useFilterNewsIds = true, filterNewsIds = it)
            }
        val topicsFlow: Flow<List<TopicEntity>> = topicIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest(transform = topicDao::getTopicEntities)
        return combine(
            flow = newsResourcesFlow,
            flow2 = topicsFlow,
        ) { newsResources: List<PopulatedNewsResource>, topics: List<TopicEntity> ->
            SearchResult(
                topics = topics.map { it.asExternalModel() },
                newsResources = newsResources.map { it.asExternalModel() },
            )
        }
    }

    /**
     * Returns the total count of documents in the FTS tables for news resources and topics.
     *
     * This method combines the counts from two separate DAOs: the [NewsResourceFtsDao.getCount]
     * of [NewsResourceFtsDao] property [newsResourceFtsDao] and the [TopicFtsDao.getCount] of
     * [TopicFtsDao] property [topicFtsDao].
     *
     * @return a Flow that emits the total count of search contents.
     */
    override fun getSearchContentsCount(): Flow<Int> =
        combine(
            flow = newsResourceFtsDao.getCount(),
            flow2 = topicFtsDao.getCount(),
        ) { newsResourceCount: Int, topicsCount: Int ->
            newsResourceCount + topicsCount
        }
}
