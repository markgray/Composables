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

import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Test double for [TopicDao]
 */
class TestTopicDao : TopicDao {

    /**
     * The backing hot [MutableStateFlow] wrapped [List] of [TopicEntity]s.
     */
    private val entitiesStateFlow: MutableStateFlow<List<TopicEntity>> =
        MutableStateFlow(value = emptyList())

    /**
     * Gets a topic by ID. We just throw a [NotImplementedError] because we don't have a need for
     * this in tests.
     *
     * @param topicId The ID of the topic.
     * @return A flow that emits the topic with the given ID.
     */
    override fun getTopicEntity(topicId: String): Flow<TopicEntity> =
        throw NotImplementedError(message = "Unused in tests")

    /**
     * Publicly accessible read-only version of the backing hot [MutableStateFlow] wrapped [List] of
     * [TopicEntity]s property [entitiesStateFlow].
     *
     * @return all topic entities.
     */
    override fun getTopicEntities(): Flow<List<TopicEntity>> = entitiesStateFlow

    /**
     * Gets a list of topics by a set of topic ids. We return the [Flow] of [List] of [TopicEntity]s
     * that results from calling the [Flow.map] extension function on the [Flow] of [List] of
     * [TopicEntity]s returned by the [getTopicEntities] method with its `transform` lambda argument
     * capturing the [List] of [TopicEntity]s in variable `topics` selecting only the [TopicEntity]s
     * whose [TopicEntity.id] property is in the [Set] of [String] parameter [ids].
     *
     * @param ids The set of topic ids.
     * @return A list of topics.
     */
    override fun getTopicEntities(ids: Set<String>): Flow<List<TopicEntity>> =
        getTopicEntities().map { topics: List<TopicEntity> -> topics.filter { it.id in ids } }

    /**
     * Gets a list of topic entities from the local database that are not followed.
     * **This method is not used in tests, so it returns an empty list.**
     *
     * @return A list of [TopicEntity]
     */
    override suspend fun getOneOffTopicEntities(): List<TopicEntity> = emptyList()

    /**
     * Inserts or ignores a list of topic entities into the local database. We call the
     * [MutableStateFlow.update] extension function on the [MutableStateFlow] wrapped [List] of
     * [TopicEntity]s property [entitiesStateFlow] with its `function` lambda argument capturing the
     * [List] of [TopicEntity]s in passed the lambda in variable `oldValues` then appending the
     * [List] of [TopicEntity]s parameter [topicEntities] to that [List] and feeding the result to
     * the [Iterable.distinctBy] extension function with its `selector` lambda argument using the
     * [TopicEntity.id] property of each [TopicEntity] to determine if two [TopicEntity]s are
     * considered equal. We then return the [List] of row IDs that were inserted by calling the
     * [Iterable.map] extension function with its `transform` lambda argument capturing each
     * [TopicEntity] in variable `topic` and returning the [TopicEntity.id] property of those
     * [TopicEntity] in a [List] of [Long].
     *
     * @param topicEntities The list of [TopicEntity]s to insert.
     * @return A list of row IDs that were inserted.
     */
    override suspend fun insertOrIgnoreTopics(topicEntities: List<TopicEntity>): List<Long> {
        // Keep old values over new values
        entitiesStateFlow.update { oldValues: List<TopicEntity> ->
            (oldValues + topicEntities).distinctBy(selector = TopicEntity::id)
        }
        return topicEntities.map { topic: TopicEntity -> topic.id.toLong() }
    }

    /**
     * Updates or inserts a list of topic entities in the local database. We call the
     * [MutableStateFlow.update] extension function on the [MutableStateFlow] wrapped [List] of
     * [TopicEntity]s property [entitiesStateFlow] with its `function` lambda argument capturing the
     * [List] of [TopicEntity]s in passed the lambda in variable `oldValues` then appending the
     * `oldValues` [List] of [TopicEntity]s to the [List] of [TopicEntity]s parameter [entities] and
     * feeding the result to the [Iterable.distinctBy] extension function with its `selector` lambda
     * argument using the [TopicEntity.id] property of each [TopicEntity] to determine if two
     * [TopicEntity]s are considered equal.
     *
     * @param entities The list of [TopicEntity]s to update or insert.
     */
    override suspend fun upsertTopics(entities: List<TopicEntity>) {
        // Overwrite old values with new values
        entitiesStateFlow.update { oldValues: List<TopicEntity> ->
            (entities + oldValues).distinctBy(
                selector = TopicEntity::id,
            )
        }
    }

    /**
     * Deletes a list of topics from the local database. We first initialize the [Set] of [String]
     * variable `val idSet` to the [Set] that results from calling the [Iterable.toSet] extension
     * function on our [List] of [String] parameter [ids]. Then we call the [MutableStateFlow.update]
     * extension function on the [MutableStateFlow] wrapped [List] of [TopicEntity]s property
     * [entitiesStateFlow] with its `function` lambda argument capturing the [List] of [TopicEntity]s
     * in variable `entities` then filtering that [List] for all [TopicEntity]s that are not in the
     * `idSet` [Set] of [String]s using the [Iterable.filterNot] extension function (those that are
     * in the set are effectively deleted).
     *
     * @param ids The list of topic IDs to delete.
     */
    override suspend fun deleteTopics(ids: List<String>) {
        val idSet: Set<String> = ids.toSet()
        entitiesStateFlow.update { entities: List<TopicEntity> ->
            entities.filterNot { topic: TopicEntity -> topic.id in idSet }
        }
    }
}
