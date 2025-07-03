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
     * Gets a list of [NetworkTopic]s from the backing [allTopics] list. We return the [List] of
     * [NetworkTopic]s that results from calling the [List.matchIds] extension function on the
     * [List] of [NetworkTopic]s property [allTopics] with its `ids` argument the [List] of [String]
     * parameter [ids] and its `idGetter` argument the [NetworkTopic.id] property of each
     * [NetworkTopic].
     *
     * @param ids A list of topic IDs. If null, returns all topics.
     * @return A list of [NetworkTopic]s that match the given IDs, or all topics if [ids] is null.
     */
    override suspend fun getTopics(ids: List<String>?): List<NetworkTopic> =
        allTopics.matchIds(
            ids = ids,
            idGetter = NetworkTopic::id,
        )

    /**
     * Gets a list of [NetworkNewsResource]s from the backing [allNewsResources] list. We return the
     * [List] of [NetworkNewsResource]s that results from calling the [List.matchIds] extension
     * function on the [List] of [NetworkNewsResource]s property [allNewsResources] with its `ids`
     * argument the [List] of [String] parameter [ids] and its `idGetter` argument the
     * [NetworkNewsResource.id] property of each [NetworkNewsResource].
     *
     * @param ids A list of news resource IDs. If null, returns all news resources.
     * @return A list of [NetworkNewsResource]s that match the given IDs, or all news resources
     * if [ids] is null.
     */
    override suspend fun getNewsResources(ids: List<String>?): List<NetworkNewsResource> =
        allNewsResources.matchIds(
            ids = ids,
            idGetter = NetworkNewsResource::id,
        )

    /**
     * Gets a list of [NetworkChangeList]s for topics from the backing [changeLists] map. The
     * version of each [NetworkChangeList] in the list is its index in the list (0 based). We return
     * the [List] of [NetworkChangeList]s that results from calling the [Map.getValue] extension
     * function on the [Map] of [CollectionType] to [List] of [NetworkChangeList]s property
     * [changeLists] with its `key` argument the [CollectionType.Topics] enum value and feeding
     * the [List] of [NetworkChangeList]s to the [List.after] extension function with its `version`
     * argument the [Int] parameter [after].
     *
     * @param after The version to fetch topics after. If null, fetches all topics.
     * @return A list of [NetworkChangeList]s for topics.
     */
    override suspend fun getTopicChangeList(after: Int?): List<NetworkChangeList> =
        changeLists.getValue(key = CollectionType.Topics).after(version = after)

    /**
     * Gets a list of [NetworkChangeList]s for news resources from the backing [changeLists] map.
     * The version of each [NetworkChangeList] in the list is its index in the list (0 based). We
     * return the [List] of [NetworkChangeList]s that results from calling the [Map.getValue]
     * extension function on the [Map] of [CollectionType] to [List] of [NetworkChangeList]s property
     * [changeLists] with its `key` argument the [CollectionType.NewsResources] enum value and feeding
     * the [List] of [NetworkChangeList]s to the [List.after] extension function with its `version`
     * argument the [Int] parameter [after].
     *
     * @param after The version to fetch news resources after. If null, fetches all news resources.
     * @return A list of [NetworkChangeList]s for news resources.
     */
    override suspend fun getNewsResourceChangeList(after: Int?): List<NetworkChangeList> =
        changeLists.getValue(key = CollectionType.NewsResources).after(version = after)

    /**
     * Returns the [NetworkChangeList.changeListVersion] of the last [NetworkChangeList] in the
     * [List] of [NetworkChangeList]s that the [Map.getValue] method of [changeLists] returns for
     * the [CollectionType] `key` [collectionType].
     *
     * @param collectionType the [CollectionType] of the [NetworkChangeList]s we are interested in.
     * @return the [NetworkChangeList.changeListVersion] of the last [NetworkChangeList] for that
     * [CollectionType].
     */
    fun latestChangeListVersion(collectionType: CollectionType): Int =
        changeLists.getValue(key = collectionType).last().changeListVersion

    /**
     * Gets a list of [NetworkChangeList]s from the backing [changeLists] map. The version of each
     * [NetworkChangeList] in the list is its index in the list (0 based). We return the [List] of
     * [NetworkChangeList]s that results from calling the [Map.getValue] extension function on the
     * [Map] of [CollectionType] to [List] of [NetworkChangeList]s property [changeLists] with its
     * `key` argument the [CollectionType] parameter [collectionType] and feeding the [List] of
     * [NetworkChangeList]s to the [List.after] extension function with its `version` argument the
     * [Int] parameter [version].
     *
     * @param collectionType The type of data to fetch change lists for.
     * @param version The version to fetch change lists after.
     * @return A list of [NetworkChangeList]s.
     */
    fun changeListsAfter(collectionType: CollectionType, version: Int): List<NetworkChangeList> =
        changeLists.getValue(key = collectionType).after(version = version)

    /**
     * Edits the change list for the backing [collectionType] for the given [id] mimicking
     * the server's change list registry. We start by initializing our [List] of [NetworkChangeList]
     * variable `changeList` to the [List] of [NetworkChangeList]s that results from calling the
     * [Map.getValue] extension function on the [Map] of [CollectionType] to [List] of
     * [NetworkChangeList]s property [changeLists] with its `key` argument the [CollectionType]
     * parameter [collectionType]. Then we initialize our [Int] variable `latestVersion` to the
     * [NetworkChangeList.changeListVersion] of the last [NetworkChangeList] in `changeList` or
     * `0` if `changeList` is empty. We then initialize our [NetworkChangeList] variable `change`
     * to a new [NetworkChangeList] with its `id` argument the [String] parameter [id], its
     * `isDelete` argument the [Boolean] parameter [isDelete], and its `changeListVersion` argument
     * `latestVersion` + 1. We then update the [Map] of [CollectionType] to [List] of
     * [NetworkChangeList]s property [changeLists] with its `key` argument the [CollectionType]
     * parameter [collectionType] and its `function` lambda argument capturing the [List] of
     * [NetworkChangeList]s passed to it that results from calling the [Iterable.filterNot] extension
     * function on `changeList` with its `predicate` lambda argument capturing each
     * [NetworkChangeList] in variable `list` selecting only the [NetworkChangeList]s whose
     * [NetworkChangeList.id] property is not equal to the [String] parameter [id] and then
     * appending `change` to that [List].
     *
     * @param collectionType The type of data to edit change lists for.
     * @param id The id of the data to edit change lists for.
     * @param isDelete Whether the data with the given [id] is being deleted.
     */
    fun editCollection(collectionType: CollectionType, id: String, isDelete: Boolean) {
        val changeList: List<NetworkChangeList> = changeLists.getValue(key = collectionType)
        val latestVersion: Int = changeList.lastOrNull()?.changeListVersion ?: 0
        val change = NetworkChangeList(
            id = id,
            isDelete = isDelete,
            changeListVersion = latestVersion + 1,
        )
        changeLists[collectionType] = changeList.filterNot { list: NetworkChangeList ->
            list.id == id
        } + change
    }
}

/**
 * Filters a list of [NetworkChangeList]s to only include items after a given version. If [version]
 * is `null` then we return the [List] of [NetworkChangeList]s unmodified. Otherwise we return the
 * [List] of [NetworkChangeList]s that results from calling the [Iterable.filter] extension function
 * on the [List] of [NetworkChangeList]s with its `predicate` lambda argument comparing the
 * [NetworkChangeList.changeListVersion] of each [NetworkChangeList] in the [List] to see if it is
 * greater than the [Int] parameter [version].
 *
 * @param version The version to filter by.
 * @return A list of [NetworkChangeList]s that are after the given [version].
 */
fun List<NetworkChangeList>.after(version: Int?): List<NetworkChangeList> = when (version) {
    null -> this
    else -> filter { changeList: NetworkChangeList -> changeList.changeListVersion > version }
}

/**
 * Return items from the [List] of [T] receiver whose id defined by lambda parameter [idGetter] is
 * in [List] of [String] parameter [ids] if [ids] is not null, and if [ids] is null return all items.
 *
 * @param ids The ids to filter by.
 * @param idGetter The lambda to get the id of type [String] of type [T].
 * @return A list of [T] filtered by the given [ids].
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
 * We return the [List] of [NetworkChangeList]s that results from calling the [Iterable.mapIndexed]
 * extension function on the [List] of [T] receiver with its `transform` lambda argument capturing
 * the index of each item in variable `index` and the [T] item itself in variable `item` and then
 * calling the [NetworkChangeList] constructor with its `id` argument the [String] returned by
 * calling our lambda parameter `idGetter` with the [T] argument `item` and its
 * `changeListVersion` argument `index + 1` and its `isDelete` argument `false` returning the [List]
 * of [NetworkChangeList]s that results from this to the caller.
 *
 * @param idGetter The lambda to get the id of type [String] of type [T].
 * @param T The type of item to map.
 * @return A list of [NetworkChangeList]s.
 */
private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> String,
): List<NetworkChangeList> = mapIndexed { index: Int, item: T ->
    NetworkChangeList(
        id = idGetter(item),
        changeListVersion = index + 1,
        isDelete = false,
    )
}
