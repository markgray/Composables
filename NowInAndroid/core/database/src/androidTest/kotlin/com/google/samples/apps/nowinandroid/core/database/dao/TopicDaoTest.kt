/*
 * Copyright 2024 The Android Open Source Project
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

import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Data Access Object (DAO) test for the [TopicEntity] access.
 *
 * Note that the database is cleared before and after each test.
 *
 * @see DatabaseTest
 */
internal class TopicDaoTest : DatabaseTest() {

    /**
     * Tests that topics can be retrieved as a stream.
     * - It first inserts a set of predefined topics.
     * - Then, it retrieves all topics as a Flow and takes the first emission.
     * - Finally, it asserts that the IDs of the retrieved topics match the expected IDs
     * ("1", "2", "3").
     */
    @Test
    fun getTopics() = runTest {
        insertTopics()

        val savedTopics: List<TopicEntity> = topicDao.getTopicEntities().first()

        assertEquals(
            expected = listOf("1", "2", "3"),
            actual = savedTopics.map { it.id },
        )
    }

    /**
     * Tests that a specific topic can be retrieved by its ID.
     * - It first inserts a set of predefined topics.
     * - Then, it retrieves the topic with ID "2".
     * - Finally, it asserts that the name of the retrieved topic is "performance".
     */
    @Test
    fun getTopic() = runTest {
        insertTopics()

        val savedTopicEntity: TopicEntity = topicDao.getTopicEntity(topicId = "2").first()

        assertEquals(expected = "performance", actual = savedTopicEntity.name)
    }

    /**
     * Tests that topics can be retrieved as a one-off list.
     * - It first inserts a set of predefined topics.
     * - Then, it retrieves all topics as a List (not a Flow).
     * - Finally, it asserts that the IDs of the retrieved topics match the expected IDs
     * ("1", "2", "3").
     */
    @Test
    fun getTopics_oneOff() = runTest {
        insertTopics()

        val savedTopics: List<TopicEntity> = topicDao.getOneOffTopicEntities()

        assertEquals(
            expected = listOf("1", "2", "3"),
            actual = savedTopics.map { it.id },
        )
    }

    /**
     * Tests that topics can be retrieved by their IDs.
     * - It first inserts a set of predefined topics.
     * - Then, it retrieves topics with specific IDs ("1", "2").
     * - Finally, it asserts that the names of the retrieved topics match the expected names
     * ("compose", "performance").
     */
    @Test
    fun getTopics_byId() = runTest {
        insertTopics()

        val savedTopics: List<TopicEntity> = topicDao.getTopicEntities(ids = setOf("1", "2"))
            .first()

        assertEquals(
            expected = listOf("compose", "performance"),
            actual = savedTopics.map { it.name },
        )
    }

    /**
     * Tests that inserting a topic with an existing ID using [TopicDao.insertOrIgnoreTopics] method
     * is ignored.
     *  - It first inserts a set of predefined topics.
     *  - Then, it attempts to insert a new topic with an ID that already exists ("1").
     *  - Finally, it retrieves all topics and asserts that the total number of topics remains
     *  unchanged (3), indicating that the duplicate insertion was ignored.
     */
    @Test
    fun insertTopic_newEntryIsIgnoredIfAlreadyExists() = runTest {
        insertTopics()
        topicDao.insertOrIgnoreTopics(
            topicEntities = listOf(testTopicEntity(id = "1", name = "compose")),
        )

        val savedTopics: List<TopicEntity> = topicDao.getOneOffTopicEntities()

        assertEquals(expected = 3, actual = savedTopics.size)
    }

    /**
     * Tests that upserting a topic with an existing ID using the [TopicDao.upsertTopics] method
     * updates the existing entry.
     * - It first inserts a set of predefined topics.
     * - Then, it upserts a topic with an existing ID ("1") but a new name ("newName").
     * - Finally, it retrieves all topics and asserts:
     *     - The total number of topics remains unchanged (3).
     *     - The name of the topic with ID "1" is updated to "newName".
     */
    @Test
    fun upsertTopic_existingEntryIsUpdated() = runTest {
        insertTopics()
        topicDao.upsertTopics(
            entities = listOf(testTopicEntity(id = "1", name = "newName")),
        )

        val savedTopics: List<TopicEntity> = topicDao.getOneOffTopicEntities()

        assertEquals(expected = 3, actual = savedTopics.size)
        assertEquals(expected = "newName", actual = savedTopics.first().name)
    }

    /**
     * Tests that topics with specified IDs are deleted.
     * - It first inserts a set of predefined topics.
     * - Then, it deletes topics with specific IDs ("1", "2").
     * - Finally, it retrieves all topics and asserts:
     *     - The total number of topics is reduced to 1.
     *     - The remaining topic has the ID "3".
     */
    @Test
    fun deleteTopics_byId_existingEntriesAreDeleted() = runTest {
        insertTopics()
        topicDao.deleteTopics(ids = listOf("1", "2"))

        val savedTopics: List<TopicEntity> = topicDao.getOneOffTopicEntities()

        assertEquals(expected = 1, actual = savedTopics.size)
        assertEquals(expected = "3", actual = savedTopics.first().id)
    }

    /**
     * Helper function to insert a predefined set of topics into the database.
     * This function is used by various tests to set up initial data.
     * The topics inserted are:
     * - id: "1", name: "compose"
     * - id: "2", name: "performance"
     * - id: "3", name: "headline"
     */
    private suspend fun insertTopics() {
        val topicEntities: List<TopicEntity> = listOf(
            testTopicEntity(id = "1", name = "compose"),
            testTopicEntity(id = "2", name = "performance"),
            testTopicEntity(id = "3", name = "headline"),
        )
        topicDao.insertOrIgnoreTopics(topicEntities = topicEntities)
    }
}

/**
 * Creates a test [TopicEntity] with the given [id] and [name].
 * The other fields ([TopicEntity.shortDescription], [TopicEntity.longDescription],
 * [TopicEntity.url], and [TopicEntity.imageUrl] are initialized with empty strings.
 *
 * @param id The ID of the topic. Defaults to "0".
 * @param name The name of the topic.
 * @return A new [TopicEntity] instance.
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
