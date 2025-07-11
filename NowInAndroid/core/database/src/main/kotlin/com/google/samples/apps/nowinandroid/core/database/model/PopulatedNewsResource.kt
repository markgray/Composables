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

package com.google.samples.apps.nowinandroid.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource

/**
 * External data layer representation of a fully populated NiA news resource. The [Embedded]
 * annotation is used to declare that the [NewsResourceEntity] property [entity] should be treated
 * as if it was directly available in the database when querying. The [Relation] annotation is used
 * to define the relationship between this [PopulatedNewsResource] class and the [TopicEntity] class,
 * in this case a many to many relationship between [NewsResourceEntity] (the parent) and [TopicEntity].
 * The [Relation.parentColumn] column "id" is the column in the parent entity ([NewsResourceEntity])
 * that is used for the relationship. [Relation.entityColumn] is the column in the child entity
 * [TopicEntity] that is used for the relationship. The [Relation.associateBy] annotation is used to
 * define the many to many relationship between [NewsResourceEntity] and [TopicEntity]. It specifies
 * an intermediary table (a junction table) is used to link news resources and topics. The
 * [Junction.value] points to the junction table ([NewsResourceTopicCrossRef]) and the
 * [Junction.parentColumn] "news_resource_id" is the column in the junction table that is used for
 * the relationship with the parent entity ([NewsResourceEntity]). The [Junction.entityColumn]
 * "topic_id" is the column in the junction table that is used for the relationship with the child
 * [TopicEntity].
 *
 * @property entity The [NewsResourceEntity] that is the parent of the relationship.
 * @property topics The list of [TopicEntity] that is the child of the relationship.
 */
data class PopulatedNewsResource(
    @Embedded
    val entity: NewsResourceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NewsResourceTopicCrossRef::class,
            parentColumn = "news_resource_id",
            entityColumn = "topic_id",
        ),
    )
    val topics: List<TopicEntity>,
)

/**
 * Converts a [PopulatedNewsResource] from the database layer to a [NewsResource] in the model layer.
 * This function maps the properties of the [PopulatedNewsResource.entity] and its associated
 * [PopulatedNewsResource.topics] to create a new [NewsResource] object.
 *
 * @return A [NewsResource] object representing the converted news resource.
 */
fun PopulatedNewsResource.asExternalModel(): NewsResource = NewsResource(
    id = entity.id,
    title = entity.title,
    content = entity.content,
    url = entity.url,
    headerImageUrl = entity.headerImageUrl,
    publishDate = entity.publishDate,
    type = entity.type,
    topics = topics.map(transform = TopicEntity::asExternalModel),
)

/**
 * Converts a [PopulatedNewsResource] from the database layer to a [NewsResourceFtsEntity]
 * for Full-Text Search (FTS). This function maps the relevant properties from the
 * [PopulatedNewsResource.entity] to create a new [NewsResourceFtsEntity] object,
 * specifically using the `id`, `title`, and `content` for indexing and searching.
receiver
 * @return A [NewsResourceFtsEntity] object ready for FTS.
 */
fun PopulatedNewsResource.asFtsEntity(): NewsResourceFtsEntity = NewsResourceFtsEntity(
    newsResourceId = entity.id,
    title = entity.title,
    content = entity.content,
)
