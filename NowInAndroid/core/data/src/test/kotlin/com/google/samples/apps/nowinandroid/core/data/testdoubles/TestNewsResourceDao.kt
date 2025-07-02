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

package com.google.samples.apps.nowinandroid.core.data.testdoubles

import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceDao
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceTopicCrossRef
import com.google.samples.apps.nowinandroid.core.database.model.PopulatedNewsResource
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Set of topic ids to filter by.
 */
val filteredInterestsIds: Set<String> = setOf("1")

/**
 * Set of news resource ids to filter by.
 */
val nonPresentInterestsIds: Set<String> = setOf("2")

/**
 * Test double for [NewsResourceDao]
 */
class TestNewsResourceDao : NewsResourceDao {

    /**
     * The backing hot flow for the list of news resources. All lists are sorted by when the
     * resource was published, descending.
     */
    private val entitiesStateFlow: MutableStateFlow<List<NewsResourceEntity>> =
        MutableStateFlow(value = emptyList())

    /**
     * The backing list of topic cross references.
     */
    internal var topicCrossReferences: List<NewsResourceTopicCrossRef> = listOf()

    /**
     * Gets a filtered list of news resources.
     *
     * We return the [Flow] of [List] of [PopulatedNewsResource] that results from feeding the
     * [MutableSharedFlow] wrapped [List] of [NewsResourceEntity] property [entitiesStateFlow]
     * to the  [Flow.map] extension function with its `transform` lambda capturing the
     * [List] of [NewsResourceEntity] passed the lambda in variable `newsResourceEntities` and
     * then feeding that to an [Iterable.map] whose lambda captures each [NewsResourceEntity]
     * in variable `entity` and uses the [NewsResourceEntity.asPopulatedNewsResource] extension
     * function to convert it to a [PopulatedNewsResource] producing a [Flow] of [List] of
     * [PopulatedNewsResource] which it feeds to the [Flow.map] extension function whose lambda
     * captures the [List] of [PopulatedNewsResource] passed to the lambda in variable
     * `resources` and in the `transform` lambda of the second [Flow.map] extension function
     * it initializes its variable `result` to the [List] of [PopulatedNewsResource] passed
     * the lambda in variable `resources` and then if [useFilterTopicIds] is `true` it
     * sets `result` to the result of calling the [Iterable.filter] extension function on `result`
     * and in its `predicate` lambda captures each [PopulatedNewsResource] in variable `resource`
     * then uses the [Iterable.any] extension function of `result` to select only the
     * [PopulatedNewsResource]'s whose [PopulatedNewsResource.topics] property contains an element
     * whose [TopicEntity.id] property is in the [Set] of [String] property [filterTopicIds].
     * Then if [useFilterNewsIds] is `true` it sets `result` to the result of calling the
     * [Iterable.filter] extension function of `result` and in its `predicate` lambda captures each
     * [PopulatedNewsResource] in variable `resource` selecting only the [PopulatedNewsResource]
     * whose [NewsResourceEntity.id] of [PopulatedNewsResource.entity] property is in the [Set] of
     * [String] property [filterNewsIds]. Then it returns the possibly filtered [List] of
     * [PopulatedNewsResource] `result` to have it emitted as a [Flow].
     *
     * @param useFilterTopicIds Whether to filter by topic ids.
     * @param filterTopicIds The set of topic ids to filter by.
     * @param useFilterNewsIds Whether to filter by news ids.
     * @param filterNewsIds The set of news ids to filter by.
     * @return A flow of a list of populated news resources.
     */
    override fun getNewsResources(
        useFilterTopicIds: Boolean,
        filterTopicIds: Set<String>,
        useFilterNewsIds: Boolean,
        filterNewsIds: Set<String>,
    ): Flow<List<PopulatedNewsResource>> =
        entitiesStateFlow
            .map { newsResourceEntities: List<NewsResourceEntity> ->
                newsResourceEntities.map { entity: NewsResourceEntity ->
                    entity.asPopulatedNewsResource(topicCrossReferences = topicCrossReferences)
                }
            }
            .map { resources: List<PopulatedNewsResource> ->
                var result: List<PopulatedNewsResource> = resources
                if (useFilterTopicIds) {
                    result = result.filter { resource: PopulatedNewsResource ->
                        resource.topics.any { it.id in filterTopicIds }
                    }
                }
                if (useFilterNewsIds) {
                    result = result.filter { resource: PopulatedNewsResource ->
                        resource.entity.id in filterNewsIds
                    }
                }
                result
            }

    /**
     * Gets a filtered list of news resource ids.
     *
     * We return the [Flow] of [List] of [String] that results from feeding the [MutableSharedFlow]
     * wrapped [List] of [NewsResourceEntity] property [entitiesStateFlow] to the [Flow.map]
     * extension function with its `transform` lambda capturing the [List] of [NewsResourceEntity]
     * passed the lambda in variable `newsResourceEntities` and using its
     * [NewsResourceEntity.asPopulatedNewsResource] with its `topicCrossReferences` argument the
     * [List] of [NewsResourceTopicCrossRef] property [topicCrossReferences] and then feeding that
     * [Flow] of [List] of [PopulatedNewsResource] to the [Flow.map] extension function in whose
     * `transform` lambda it captures the [List] of [PopulatedNewsResource] passed the lambda in
     * variable `resources` and initializes its variable `result` to that [List] of
     * [PopulatedNewsResource]. Then if [useFilterTopicIds] is `true` it sets `result` to the
     * result of calling the [Iterable.filter] extension function on `result` and in its
     * `predicate` lambda captures each [PopulatedNewsResource] in variable `resource` theb
     * using the [Iterable.any] extension function of `resource` selects only the
     * [PopulatedNewsResource] whose [PopulatedNewsResource.topics] property contains an element
     * whose [TopicEntity.id] property is in the [Set] of [String] parameter [filterTopicIds].
     * Then if [useFilterNewsIds] is `true` it sets `result` to the result of calling the
     * [Iterable.filter] extension function on `result` and in its `predicate` lambda captures each
     * [PopulatedNewsResource] in variable `resource` then selects only the [PopulatedNewsResource]
     * whose [NewsResourceEntity.id] of [PopulatedNewsResource.entity] property is in the [Set] of
     * [String] parameter [filterNewsIds]. Then it returns the possibly filtered [List] of [String]
     * that results using the [Iterable.map] extension function on `result` with its `transform`
     * lambda method emitting the [NewsResourceEntity.id] of each [PopulatedNewsResource.entity] in
     * `result` to have it emitted as a [Flow] of [List] of [String].
     *
     * @param useFilterTopicIds Whether to filter by topic ids.
     * @param filterTopicIds The set of topic ids to filter by.
     * @param useFilterNewsIds Whether to filter by news ids.
     * @param filterNewsIds The set of news ids to filter by.
     * @return A flow of a list of news resource ids.
     */
    override fun getNewsResourceIds(
        useFilterTopicIds: Boolean,
        filterTopicIds: Set<String>,
        useFilterNewsIds: Boolean,
        filterNewsIds: Set<String>,
    ): Flow<List<String>> =
        entitiesStateFlow
            .map { newsResourceEntities: List<NewsResourceEntity> ->
                newsResourceEntities.map { entity: NewsResourceEntity ->
                    entity.asPopulatedNewsResource(topicCrossReferences = topicCrossReferences)
                }
            }
            .map { resources: List<PopulatedNewsResource> ->
                var result: List<PopulatedNewsResource> = resources
                if (useFilterTopicIds) {
                    result = result.filter { resource: PopulatedNewsResource ->
                        resource.topics.any { it.id in filterTopicIds }
                    }
                }
                if (useFilterNewsIds) {
                    result = result.filter { resource: PopulatedNewsResource ->
                        resource.entity.id in filterNewsIds
                    }
                }
                result.map { it.entity.id }
            }

    /**
     * Inserts or updates the given [newsResourceEntities] in the backing [entitiesStateFlow] flow.
     * The [entitiesStateFlow] is updated by concatenating the new [newsResourceEntities] with the
     * existing values, then removing duplicates based on the `id` property, and finally sorting
     * the result by `publishDate` in descending order.
     * TODO: Continue here.
     *
     * @param newsResourceEntities The list of news resources to be upserted.
     */
    override suspend fun upsertNewsResources(newsResourceEntities: List<NewsResourceEntity>) {
        entitiesStateFlow.update { oldValues: List<NewsResourceEntity> ->
            // New values come first so they overwrite old values
            (newsResourceEntities + oldValues)
                .distinctBy(selector = NewsResourceEntity::id)
                .sortedWith(
                    comparator = compareBy(selector = NewsResourceEntity::publishDate).reversed(),
                )
        }
    }

    override suspend fun insertOrIgnoreTopicCrossRefEntities(
        newsResourceTopicCrossReferences: List<NewsResourceTopicCrossRef>,
    ) {
        // Keep old values over new ones
        topicCrossReferences = (topicCrossReferences + newsResourceTopicCrossReferences)
            .distinctBy { it.newsResourceId to it.topicId }
    }

    override suspend fun deleteNewsResources(ids: List<String>) {
        val idSet: Set<String> = ids.toSet()
        entitiesStateFlow.update { entities: List<NewsResourceEntity> ->
            entities.filterNot { it.id in idSet }
        }
    }
}

private fun NewsResourceEntity.asPopulatedNewsResource(
    topicCrossReferences: List<NewsResourceTopicCrossRef>,
) = PopulatedNewsResource(
    entity = this,
    topics = topicCrossReferences
        .filter { it.newsResourceId == id }
        .map { newsResourceTopicCrossRef: NewsResourceTopicCrossRef ->
            TopicEntity(
                id = newsResourceTopicCrossRef.topicId,
                name = "name",
                shortDescription = "short description",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            )
        },
)
