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

package com.google.samples.apps.nowinandroid.core.network.demo

import JvmUnitTestDemoAssetManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.IO
import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkChangeList
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.BufferedReader
import java.io.InputStream
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * [NiaNetworkDataSource] implementation that provides static news resources to aid development.
 *
 * It reads the news resources and topics from JSON files stored in the assets folder.
 * This allows for easy testing and development without requiring a live backend.
 *
 * @property ioDispatcher The coroutine dispatcher used for IO operations, injected by Hilt.
 * @property networkJson The Json instance used to parse the JSON files, injected by Hilt.
 * @property assets The asset manager used to access the assets folder, injected by Hilt.
 */
class DemoNiaNetworkDataSource @Inject constructor(
    @param:Dispatcher(niaDispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json,
    private val assets: DemoAssetManager = JvmUnitTestDemoAssetManager,
) : NiaNetworkDataSource {

    /**
     * Retrieves a list of topics from the static JSON file.
     *
     * @param ids A list of topic IDs to filter by. If null or empty, all topics are returned.
     * Note: In this demo implementation, the `ids` parameter is ignored, and all topics from
     * the JSON file are always returned.
     * @return A [List] of [NetworkTopic] objects.
     */
    override suspend fun getTopics(ids: List<String>?): List<NetworkTopic> =
        getDataFromJsonFile(fileName = TOPICS_ASSET)

    /**
     * Retrieves a list of news resources from a JSON file.
     *
     * @param ids A list of news resource IDs to retrieve. If null, all news resources are returned.
     * This parameter is currently ignored by this implementation, which always returns all
     * news resources from the JSON file.
     * @return A [List] of [NetworkNewsResource] objects.
     */
    override suspend fun getNewsResources(ids: List<String>?): List<NetworkNewsResource> =
        getDataFromJsonFile(fileName = NEWS_ASSET)

    /**
     * Retrieves a list of topic change lists from the static JSON file.
     *
     * This method fetches all topics from the JSON file and then maps them to a list of
     * [NetworkChangeList] objects. Each [NetworkChangeList] represents a change to a topic,
     * with the `id` being the topic's ID and `isDelete` always set to `false` in this demo
     * implementation.
     *
     * @param after The change list version to fetch changes after. This parameter is currently
     * ignored by this implementation, which always returns all topics as changes.
     * @return A list of [NetworkChangeList] objects representing changes to topics.
     */
    override suspend fun getTopicChangeList(after: Int?): List<NetworkChangeList> =
        getTopics().mapToChangeList(idGetter = NetworkTopic::id)

    /**
     * Retrieves a list of news resource change lists from the static JSON file.
     *
     * This method fetches all news resources from the JSON file and then maps them to a list of
     * [NetworkChangeList] objects. Each [NetworkChangeList] represents a change to a news
     * resource, with the `id` being the news resource's ID and `isDelete` always set to `false`
     * in this demo implementation.
     *
     * @param after The change list version to fetch changes after. This parameter is currently
     * ignored by this implementation, which always returns all news resources as changes.
     * @return A list of [NetworkChangeList] objects representing changes to news resources.
     */
    override suspend fun getNewsResourceChangeList(after: Int?): List<NetworkChangeList> =
        getNewsResources().mapToChangeList(idGetter = NetworkNewsResource::id)

    /**
     * Reads and deserializes data from a JSON file stored in the assets folder.
     *
     * This function is a generic helper used to load data of type [T] from a specified
     * JSON file. It handles opening the asset, reading its content, and deserializing it
     * using the [Json] property [networkJson] parser. It also includes a workaround for a
     * deserialization issue on Android API 23 (M) and below.
     *
     * We use the [withContext] method to launch a coroutine with [ioDispatcher] as its
     * [CoroutineContext]. In the [CoroutineScope] `block` lambda argument we call the
     * [DemoAssetManager.open] method of [DemoAssetManager] property [assets] to open the assets
     * file [fileName] and use [use] on the [InputStream] `inputStream` and in the `block` argument
     * we branch on the SDK version:
     *  - On API 23 (M) and below we use the [BufferedReader.readText] method to read the contents
     *  passing it on to [let] to execute the [Json.decodeFromString] method of [networkJson] to
     *  deserialize the JSON string to a list of objects of type [T].
     *  - On API 24 (N) and above we use the [Json.decodeFromStream] method to deserialize the
     *  [InputStream] variable `inputStream` to a list of objects of type [T].
     *
     * @param T The type of data to deserialize from the JSON file.
     * @param fileName The name of the JSON file in the assets folder (e.g., "news.json").
     * @return A list of objects of type [T] deserialized from the JSON file.
     * @see <a href="https://github.com/Kotlin/kotlinx.serialization/issues/2457#issuecomment-1786923342">Workaround for API 23 deserialization issue</a>
     */
    @OptIn(ExperimentalSerializationApi::class)
    private suspend inline fun <reified T> getDataFromJsonFile(fileName: String): List<T> =
        withContext(context = ioDispatcher) {
            assets.open(fileName = fileName).use { inputStream: InputStream ->
                if (SDK_INT <= M) {
                    /**
                     * On API 23 (M) and below we must use a workaround to avoid an exception being
                     * thrown during deserialization. See:
                     * https://github.com/Kotlin/kotlinx.serialization/issues/2457#issuecomment-1786923342
                     */
                    inputStream.bufferedReader().use(block = BufferedReader::readText)
                        .let(block = networkJson::decodeFromString)
                } else {
                    networkJson.decodeFromStream(stream = inputStream)
                }
            }
        }

    companion object {
        /**
         * Name of the JSON file in the assets folder that contains the list of news resources.
         */
        private const val NEWS_ASSET = "news.json"

        /**
         * The name of the JSON file in the assets folder that contains the static list of topics.
         * This file is used by the [DemoNiaNetworkDataSource] to provide a predefined set of
         * topics for development and testing purposes.
         */
        private const val TOPICS_ASSET = "topics.json"
    }
}

/**
 * Converts a list of items of type [T] to a list of [NetworkChangeList] objects.
 *
 * This extension function iterates over the receiver [List] of [T] and transforms each item into a
 * [NetworkChangeList]. The `id` of each [NetworkChangeList] is determined by the
 * [idGetter] lambda parameter, which extracts a unique identifier from the item. The
 * `changeListVersion` is set to the index of the item in the original list, and
 * `isDelete` is always `false`, indicating that these are all additions or updates.
 *
 * @param T The type of items in the list.
 * @param idGetter A function that takes an item of type [T] and returns its String ID.
 * This ID is used for the [NetworkChangeList.id] property.
 * @return A list of [NetworkChangeList] objects, one for each item in the input list.
 */
private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> String,
) = mapIndexed { index: Int, item: T ->
    NetworkChangeList(
        id = idGetter(item),
        changeListVersion = index,
        isDelete = false,
    )
}
