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
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import com.google.samples.apps.nowinandroid.core.network.model.asExternalModel
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Tests for mapping functions between network and database models.
 */
class NetworkEntityTest {

    /**
     * Test mapping from [NetworkTopic] to [TopicEntity].
     *
     * We start by initializing our [NetworkTopic] variable `networkModel` to a new instance whose
     * properties are:
     *  - `id`: "0"
     *  - `name`: "Test"
     *  - `shortDescription`: "short description"
     *  - `longDescription`: "long description"
     *  - `url`: "URL"
     *  - `imageUrl`: "image URL"
     *
     * Then we intitialize our [TopicEntity] to the result of calling the [NetworkTopic.asEntity]
     * method of our [NetworkTopic] variable `networkModel`. Then we use the [assertEquals] method
     * to compare the values of the properties of our [TopicEntity] variable `entity` with the
     * values of the properties of our [NetworkTopic] variable `networkModel`.
     */
    @Test
    fun networkTopicMapsToDatabaseModel() {
        val networkModel = NetworkTopic(
            id = "0",
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "image URL",
        )
        val entity: TopicEntity = networkModel.asEntity()

        assertEquals("0", entity.id)
        assertEquals("Test", entity.name)
        assertEquals("short description", entity.shortDescription)
        assertEquals("long description", entity.longDescription)
        assertEquals("URL", entity.url)
        assertEquals("image URL", entity.imageUrl)
    }

    /**
     * Test mapping from [NetworkNewsResource] to [NewsResourceEntity].
     *
     * We start by initializing our [NetworkNewsResource] variable `networkModel` to a new instance
     * whose properties are:
     *  - `id`: "0"
     *  - `title`: "title"
     *  - `content`: "content"
     *  - `url`: "url"
     *  - `headerImageUrl`: "headerImageUrl"
     *  - `publishDate`: Instant.fromEpochMilliseconds(1)
     *  - `type`: "Article 📚"
     *
     * Then we initialize our [NewsResourceEntity] to the result of calling the
     * [NetworkNewsResource.asEntity] method of our [NetworkNewsResource] variable `networkModel`.
     * Then we use the [assertEquals] method to compare the values of the properties of our
     * [NewsResourceEntity] variable `entity` with the values of the properties of our
     * [NetworkNewsResource] variable `networkModel`.
     */
    @Test
    fun networkNewsResourceMapsToDatabaseModel() {
        val networkModel =
            NetworkNewsResource(
                id = "0",
                title = "title",
                content = "content",
                url = "url",
                headerImageUrl = "headerImageUrl",
                publishDate = Instant.fromEpochMilliseconds(epochMilliseconds = 1),
                type = "Article 📚",
            )
        val entity: NewsResourceEntity = networkModel.asEntity()

        assertEquals("0", entity.id)
        assertEquals("title", entity.title)
        assertEquals("content", entity.content)
        assertEquals("url", entity.url)
        assertEquals("headerImageUrl", entity.headerImageUrl)
        assertEquals(Instant.fromEpochMilliseconds(1), entity.publishDate)
        assertEquals("Article 📚", entity.type)
    }

    /**
     * Test mapping from [NetworkTopic] to [Topic].
     *
     * We initialize our [NetworkTopic] variable `networkTopic` to a new instance whose properties
     * are:
     *  - `id`: "0"
     *  - `name`: "Test"
     *  - `shortDescription`: "short description"
     *  - `longDescription`: "long description"
     *  - `url`: "URL"
     *  - `imageUrl`: "imageUrl"
     *
     * Then we initialize our [Topic] variable `expected` to a new instance whose properties are:
     *  - `id`: "0"
     *  - `name`: "Test"
     *  - `shortDescription`: "short description"
     *  - `longDescription`: "long description"
     *  - `url`: "URL"
     *  - `imageUrl`: "imageUrl"
     *
     * Then we use the [assertEquals] method to compare the values of the properties of our
     * [Topic] variable `expected` with the values of the properties of the [Topic] returned by
     * the [NetworkTopic.asExternalModel] method of our [NetworkTopic] variable `networkTopic`.
     */
    @Test
    fun networkTopicMapsToExternalModel() {
        val networkTopic = NetworkTopic(
            id = "0",
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "imageUrl",
        )

        val expected = Topic(
            id = "0",
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "imageUrl",
        )

        assertEquals(expected, networkTopic.asExternalModel())
    }

    /**
     * Test mapping from [NetworkNewsResource] to [NewsResource].
     *
     * We initialize our [NetworkNewsResource] variable `networkNewsResource` to a new instance
     * whose properties are:
     *  - `id`: "0"
     *  - `title`: "title"
     *  - `content`: "content"
     *  - `url`: "url"
     *  - `headerImageUrl`: "headerImageUrl"
     *  - `publishDate`: Instant.fromEpochMilliseconds(1)
     *  - `type`: "Article 📚"
     *  - `topics`: listOf("1", "2")
     *
     * Then we initialize our [List] of [NetworkTopic] variable `networkTopics` to a new instance
     * whose elements are a new instance of [NetworkTopic] whose properties are:
     *  - `id`: "1"
     *  - `name`: "Test 1"
     *  - `shortDescription`: "short description 1"
     *  - `longDescription`: "long description 1"
     *  - `url`: "url 1"
     *  - `imageUrl`: "imageUrl 1"
     *
     * And another new instance of [NetworkTopic] whose properties are:
     *  - `id`: "2"
     *  - `name`: "Test 2"
     *  - `shortDescription`: "short description 2"
     *  - `longDescription`: "long description 2"
     *  - `url`: "url 2"
     *  - `imageUrl`: "imageUrl 2"
     *
     * We then initialize our [NewsResource] variable `expected` to a new instance whose properties
     * are:
     *  - `id`: "0"
     *  - `title`: "title"
     *  - `content`: "content"
     *  - `url`: "url"
     *  - `headerImageUrl`: "headerImageUrl"
     *  - `publishDate`: Instant.fromEpochMilliseconds(1)
     *  - `type`: "Article 📚"
     *  - `topics`: the [List] of [Topic] that the [Iterable.map] method of the [List] of
     *  [NetworkTopic] variable `networkTopics` returns when it applies the
     *  [NetworkTopic.asExternalModel] method to each element.
     *
     * Then we use the [assertEquals] method to compare the values of the properties of our
     * [NewsResource] variable `expected` with the values of the properties of the [NewsResource]
     * returned by the [NetworkNewsResource.asExternalModel] method of our
     * [NetworkNewsResource] variable `networkNewsResource`.
     */
    @Test
    fun networkNewsResourceMapsToExternalModel() {
        val networkNewsResource = NetworkNewsResource(
            id = "0",
            title = "title",
            content = "content",
            url = "url",
            headerImageUrl = "headerImageUrl",
            publishDate = Instant.fromEpochMilliseconds(1),
            type = "Article 📚",
            topics = listOf("1", "2"),
        )

        val networkTopics: List<NetworkTopic> = listOf(
            NetworkTopic(
                id = "1",
                name = "Test 1",
                shortDescription = "short description 1",
                longDescription = "long description 1",
                url = "url 1",
                imageUrl = "imageUrl 1",
            ),
            NetworkTopic(
                id = "2",
                name = "Test 2",
                shortDescription = "short description 2",
                longDescription = "long description 2",
                url = "url 2",
                imageUrl = "imageUrl 2",
            ),
        )

        val expected = NewsResource(
            id = "0",
            title = "title",
            content = "content",
            url = "url",
            headerImageUrl = "headerImageUrl",
            publishDate = Instant.fromEpochMilliseconds(1),
            type = "Article 📚",
            topics = networkTopics.map(NetworkTopic::asExternalModel),
        )
        assertEquals(expected, networkNewsResource.asExternalModel(networkTopics))
    }
}
