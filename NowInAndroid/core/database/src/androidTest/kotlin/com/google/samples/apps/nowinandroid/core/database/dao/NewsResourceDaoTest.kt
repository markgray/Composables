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

@file:Suppress("RedundantValueArgument")

package com.google.samples.apps.nowinandroid.core.database.dao

import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceTopicCrossRef
import com.google.samples.apps.nowinandroid.core.database.model.PopulatedNewsResource
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Tests for [NewsResourceDao]
 */
internal class NewsResourceDaoTest : DatabaseTest() {

    /**
     * When the [NewsResourceDao.getNewsResources] query is called, all entries in the table
     * are returned, and they are ordered by their publish date, with the most recent first.
     *
     * We initialize our [List] of [NewsResourceEntity] variable `newsResourceEntities` `4` instances
     * of [testNewsResource] whose `millisSinceEpoch` values are `0`, `3`, `1`, and `2` then use the
     * [NewsResourceDao.upsertNewsResources] method to insert them into the "news_resources" table
     * of the [NiaDatabase] database. Then we initialize our [List] of [PopulatedNewsResource]
     * variable `savedNewsResourceEntities` by collecting the first emission of the
     * [NewsResourceDao.getNewsResources] method. Finally, we call [assertEquals] to verify that
     * the`expected` [List] of `3L`, `2L`, `1L`, and `0L` is equal to the `actual` [List] of [Long]
     * that results when we call the [Iterable.map] method of `savedNewsResourceEntities` and in its
     * `transform` lambda argument we call the [asExternalModel] method of each [PopulatedNewsResource]
     * of each [PopulatedNewsResource] in `savedNewsResourceEntities` to convert it to an [NewsResource]
     * and then call the [Instant.toEpochMilliseconds] method on its [NewsResource.publishDate]
     * property to convert them to a [List] of [Long].
     */
    @Test
    fun getNewsResources_allEntries_areOrderedByPublishDateDesc() = runTest {
        val newsResourceEntities: List<NewsResourceEntity> = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities = newsResourceEntities,
        )

        val savedNewsResourceEntities: List<PopulatedNewsResource> =
            newsResourceDao.getNewsResources().first()

        assertEquals(
            expected = listOf(3L, 2L, 1L, 0L),
            actual = savedNewsResourceEntities.map { newsResource: PopulatedNewsResource ->
                newsResource.asExternalModel().publishDate.toEpochMilliseconds()
            },
        )
    }

    /**
     * When the [NewsResourceDao.getNewsResources] query is called with a `filterNewsIds`
     * specified, only entries whose ID is in the filter are returned, and they are
     * ordered by their publish date, with the most recent first.
     */
    @Test
    fun getNewsResources_filteredById_areOrderedByDescendingPublishDate() = runTest {
        val newsResourceEntities: List<NewsResourceEntity> = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )

        val savedNewsResourceEntities: List<PopulatedNewsResource> =
            newsResourceDao.getNewsResources(
                useFilterNewsIds = true,
                filterNewsIds = setOf("3", "0"),
            ).first()

        assertEquals(
            expected = listOf("3", "0"),
            actual = savedNewsResourceEntities.map { newsResource: PopulatedNewsResource ->
                newsResource.entity.id
            },
        )
    }

    /**
     * When the [NewsResourceDao.getNewsResources] query is called with a `filterTopicIds`
     * specified, only entries whose ID is associated with a topic whose ID is in the filter
     * are returned, and they are ordered by their publish date, with the most recent first.
     */
    @Test
    fun getNewsResources_filteredByTopicId_areOrderedByDescendingPublishDate() = runTest {
        val topicEntities: List<TopicEntity> = listOf(
            testTopicEntity(
                id = "1",
                name = "1",
            ),
            testTopicEntity(
                id = "2",
                name = "2",
            ),
        )
        val newsResourceEntities: List<NewsResourceEntity> = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        val newsResourceTopicCrossRefEntities: List<NewsResourceTopicCrossRef> =
            topicEntities.mapIndexed { index: Int, topicEntity: TopicEntity ->
                NewsResourceTopicCrossRef(
                    newsResourceId = index.toString(),
                    topicId = topicEntity.id,
                )
            }

        topicDao.insertOrIgnoreTopics(
            topicEntities = topicEntities,
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities = newsResourceEntities,
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossReferences = newsResourceTopicCrossRefEntities,
        )

        val filteredNewsResources: List<PopulatedNewsResource> =
            newsResourceDao.getNewsResources(
                useFilterTopicIds = true,
                filterTopicIds = topicEntities
                    .map(transform = TopicEntity::id)
                    .toSet(),
            ).first()

        assertEquals(
            expected = listOf("1", "0"),
            actual = filteredNewsResources.map { it.entity.id },
        )
    }

    /**
     * When the [NewsResourceDao.getNewsResources] query is called with a `filterNewsIds` and
     * `filterTopicIds` specified, only entries whose ID is in the `filterNewsIds` and associated
     * with a topic whose ID is in the `filterTopicIds` are returned, and they are ordered by
     * their publish date, with the most recent first.
     */
    @Test
    fun getNewsResources_filteredByIdAndTopicId_areOrderedByDescendingPublishDate() = runTest {
        val topicEntities: List<TopicEntity> = listOf(
            testTopicEntity(
                id = "1",
                name = "1",
            ),
            testTopicEntity(
                id = "2",
                name = "2",
            ),
        )
        val newsResourceEntities: List<NewsResourceEntity> = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        val newsResourceTopicCrossRefEntities: List<NewsResourceTopicCrossRef> =
            topicEntities.mapIndexed { index: Int, topicEntity: TopicEntity ->
                NewsResourceTopicCrossRef(
                    newsResourceId = index.toString(),
                    topicId = topicEntity.id,
                )
            }

        topicDao.insertOrIgnoreTopics(
            topicEntities = topicEntities,
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities = newsResourceEntities,
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossReferences = newsResourceTopicCrossRefEntities,
        )

        val filteredNewsResources: List<PopulatedNewsResource> =
            newsResourceDao.getNewsResources(
                useFilterTopicIds = true,
                filterTopicIds = topicEntities
                    .map(transform = TopicEntity::id)
                    .toSet(),
                useFilterNewsIds = true,
                filterNewsIds = setOf("1"),
            ).first()

        assertEquals(
            expected = listOf("1"),
            actual = filteredNewsResources.map { it.entity.id },
        )
    }

    /**
     * When the [NewsResourceDao.deleteNewsResources] method is called with a [List] of
     * [NewsResourceEntity.id] values, the entries that have those `id` values are
     * deleted from the "news_resources" table.
     */
    @Test
    fun deleteNewsResources_byId() =
        runTest {
            val newsResourceEntities: List<NewsResourceEntity> = listOf(
                testNewsResource(
                    id = "0",
                    millisSinceEpoch = 0,
                ),
                testNewsResource(
                    id = "1",
                    millisSinceEpoch = 3,
                ),
                testNewsResource(
                    id = "2",
                    millisSinceEpoch = 1,
                ),
                testNewsResource(
                    id = "3",
                    millisSinceEpoch = 2,
                ),
            )
            newsResourceDao.upsertNewsResources(newsResourceEntities = newsResourceEntities)

            val (toDelete: List<NewsResourceEntity>, toKeep: List<NewsResourceEntity>) =
                newsResourceEntities.partition { entity: NewsResourceEntity ->
                    entity.id.toInt() % 2 == 0
                }

            newsResourceDao.deleteNewsResources(
                ids = toDelete.map(transform = NewsResourceEntity::id),
            )

            assertEquals(
                expected = toKeep.map(transform = NewsResourceEntity::id)
                    .toSet(),
                actual = newsResourceDao.getNewsResources().first()
                    .map { it.entity.id }
                    .toSet(),
            )
        }
}

/**
 * Helper function to create a [TopicEntity] for testing purposes.
 *
 * @param id The ID of the topic. Defaults to "0".
 * @param name The name of the topic.
 * @return A [TopicEntity] instance with the specified [id] and [name], and empty strings for
 * other properties.
 */
private fun testTopicEntity(
    id: String = "0",
    name: String,
) = TopicEntity(
    id = id,
    name = name,
    shortDescription = "",
    longDescription = "",
    url = "",
    imageUrl = "",
)

/**
 * Helper function to create a [NewsResourceEntity] for testing purposes.
 *
 * @param id The ID of the news resource. Defaults to "0".
 * @param millisSinceEpoch The publish date of the news resource, in milliseconds since the epoch.
 * Defaults to 0.
 * @return A [NewsResourceEntity] instance with the specified [id] and [millisSinceEpoch], and
 * empty strings for other properties, with the type defaulted to "Article 📚".
 */
private fun testNewsResource(
    id: String = "0",
    millisSinceEpoch: Long = 0,
) = NewsResourceEntity(
    id = id,
    title = "",
    content = "",
    url = "",
    headerImageUrl = "",
    publishDate = Instant.fromEpochMilliseconds(epochMilliseconds = millisSinceEpoch),
    type = "Article 📚",
)
