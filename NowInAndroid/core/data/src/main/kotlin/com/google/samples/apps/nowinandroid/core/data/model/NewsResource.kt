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

package com.google.samples.apps.nowinandroid.core.data.model

import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceTopicCrossRef
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import com.google.samples.apps.nowinandroid.core.network.model.asExternalModel

/**
 * Converts a [NetworkNewsResource] to a [NewsResourceEntity]. This function maps the fields from
 * the network model receiver to the corresponding fields in the database entity.
 */
fun NetworkNewsResource.asEntity(): NewsResourceEntity = NewsResourceEntity(
    id = id,
    title = title,
    content = content,
    url = url,
    headerImageUrl = headerImageUrl,
    publishDate = publishDate,
    type = type,
)

/**
 * A shell [TopicEntity] to fulfill the foreign key constraint when inserting
 * a [NewsResourceEntity] into the DB
 */
fun NetworkNewsResource.topicEntityShells(): List<TopicEntity> =
    topics.map { topicId: String ->
        TopicEntity(
            id = topicId,
            name = "",
            url = "",
            imageUrl = "",
            shortDescription = "",
            longDescription = "",
        )
    }

/**
 * Converts a [NetworkNewsResource] to a list of [NewsResourceTopicCrossRef] objects.
 * This function iterates over the topics associated with the network news resource
 * and creates a [NewsResourceTopicCrossRef] cross-reference object for each, linking
 * the news resource ID with the topic ID.
 */
fun NetworkNewsResource.topicCrossReferences(): List<NewsResourceTopicCrossRef> =
    topics.map { topicId: String ->
        NewsResourceTopicCrossRef(
            newsResourceId = id,
            topicId = topicId,
        )
    }

/**
 * Converts a [NetworkNewsResource] to a [NewsResource] object.
 * This function maps the fields from the network model receiver to the corresponding
 * fields in the external model, including transforming associated topics.
 *
 * @param topics A list of [NetworkTopic] objects that are candidates for association with this
 * news resource.
 * @return A [NewsResource] object representing the external model.
 */
fun NetworkNewsResource.asExternalModel(topics: List<NetworkTopic>): NewsResource =
    NewsResource(
        id = id,
        title = title,
        content = content,
        url = url,
        headerImageUrl = headerImageUrl,
        publishDate = publishDate,
        type = type,
        topics = topics
            .filter { networkTopic: NetworkTopic -> this.topics.contains(networkTopic.id) }
            .map(transform = NetworkTopic::asExternalModel),
    )
