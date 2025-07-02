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

import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.demo.DemoNiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkChangeList
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * The type of data that can be queried from the backend
 */
enum class CollectionType {
    /**
     * The [NetworkTopic] asset
     */
    Topics,

    /**
     * The [NetworkNewsResource] asset
     */
    NewsResources,
}

/**
 * Test double for [NiaNetworkDataSource]
 */
class TestNiaNetworkDataSource : NiaNetworkDataSource {

    /**
     * The [DemoNiaNetworkDataSource] that this [TestNiaNetworkDataSource] delegates to.
     * This is used to ensure that the data returned by this test double is realistic.
     * We use [UnconfinedTestDispatcher] as its `ioDispatcher` argument which is
     * similar to `Dispatchers.Unconfined`: the tasks that it executes are not confined to any
     * particular thread and form an event loop; it's different from `Dispatchers.Unconfined` in
     * that it skips delays, as all TestDispatchers do. We use a [Json] instance whose
     * `ignoreUnknownKeys` is set to `true` so that encounters of unknown properties in the input
     * JSON will be ignored instead of throwing [SerializationException].
     */
    private val source = DemoNiaNetworkDataSource(
        ioDispatcher = UnconfinedTestDispatcher(),
        networkJson = Json { ignoreUnknownKeys = true },
    )

    /**
     * A backing store for all topics that are available from the [DemoNiaNetworkDataSource].
     */
    private val allTopics: List<NetworkTopic> = runBlocking { source.getTopics() }

    /**
     * A backing store for all news resources that are available from the [NiaNetworkDataSource].
     */
    private val allNewsResources: List<NetworkNewsResource> =
        runBlocking { source.getNewsResources() }

    /**
     * A map from [CollectionType] to a list of [NetworkChangeList]s, where the version of each
     * [NetworkChangeList] in the list is its index in the list (0 based).
     */
    private val changeLists: MutableMap<CollectionType, List<NetworkChangeList>> = mutableMapOf(
        CollectionType.Topics to allTopics
            .mapToChangeList(idGetter = NetworkTopic::id),
        CollectionType.NewsResources to allNewsResources
            .mapToChangeList(idGetter = NetworkNewsResource::id),
    )

    /**
     * Gets a list of [NetworkTopic]s from the backing [allTopics] list.
     * TODO: Continue here.
     *
     * @param ids A list of topic IDs. If null, returns all topics.
     * @return A list of [NetworkTopic]s that match the given IDs, or all topics if [ids] is null.
     */
    override suspend fun getTopics(ids: List<String>?): List<NetworkTopic> =
        allTopics.matchIds(
            ids = ids,
            idGetter = NetworkTopic::id,
        )

    override suspend fun getNewsResources(ids: List<String>?): List<NetworkNewsResource> =
        allNewsResources.matchIds(
            ids = ids,
            idGetter = NetworkNewsResource::id,
        )

    override suspend fun getTopicChangeList(after: Int?): List<NetworkChangeList> =
        changeLists.getValue(CollectionType.Topics).after(after)

    override suspend fun getNewsResourceChangeList(after: Int?): List<NetworkChangeList> =
        changeLists.getValue(CollectionType.NewsResources).after(after)

    fun latestChangeListVersion(collectionType: CollectionType): Int =
        changeLists.getValue(collectionType).last().changeListVersion

    fun changeListsAfter(collectionType: CollectionType, version: Int): List<NetworkChangeList> =
        changeLists.getValue(collectionType).after(version)

    /**
     * Edits the change list for the backing [collectionType] for the given [id] mimicking
     * the server's change list registry
     */
    fun editCollection(collectionType: CollectionType, id: String, isDelete: Boolean) {
        val changeList = changeLists.getValue(collectionType)
        val latestVersion = changeList.lastOrNull()?.changeListVersion ?: 0
        val change = NetworkChangeList(
            id = id,
            isDelete = isDelete,
            changeListVersion = latestVersion + 1,
        )
        changeLists[collectionType] = changeList.filterNot { it.id == id } + change
    }
}

fun List<NetworkChangeList>.after(version: Int?): List<NetworkChangeList> = when (version) {
    null -> this
    else -> filter { it.changeListVersion > version }
}

/**
 * Return items from [this] whose id defined by [idGetter] is in [ids] if [ids] is not null
 */
private fun <T> List<T>.matchIds(
    ids: List<String>?,
    idGetter: (T) -> String,
) = when (ids) {
    null -> this
    else -> ids.toSet().let { idSet -> filter { idGetter(it) in idSet } }
}

/**
 * Maps items to a change list where the change list version is denoted by the index of each item.
 * [after] simulates which models have changed by excluding items before it
 */
private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> String,
) = mapIndexed { index: Int, item: T ->
    NetworkChangeList(
        id = idGetter(item),
        changeListVersion = index + 1,
        isDelete = false,
    )
}
