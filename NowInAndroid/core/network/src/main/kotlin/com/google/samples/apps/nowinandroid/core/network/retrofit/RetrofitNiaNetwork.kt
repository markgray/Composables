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

package com.google.samples.apps.nowinandroid.core.network.retrofit

import androidx.tracing.trace
import com.google.samples.apps.nowinandroid.core.network.BuildConfig
import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkChangeList
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for NIA Network API
 */
private interface RetrofitNiaNetworkApi {
    /**
     * Gets the [List] of [NetworkTopic] topics from the network.
     *  - @[GET] (value = "topics"): This is a Retrofit annotation indicating an HTTP GET request.
     *  The string provided in `value` is the relative path that will be appended to the base URL
     *  configured with Retrofit.
     *  - @[Query] (...): This Retrofit annotation is used to add query parameters to the URL.
     *
     * @param ids The list of topic IDs to query. If null, returns all topics.
     * @return The [NetworkResponse] wrapped [List] of [NetworkTopic] topics whose [NetworkTopic.id]
     * is in [List] of [String] parameter [ids].
     */
    @GET(value = "topics")
    suspend fun getTopics(
        @Query("id") ids: List<String>?,
    ): NetworkResponse<List<NetworkTopic>>

    /**
     * Gets the list of news resources from the network
     *  - @[GET] (value = "newsresources"): This is a Retrofit annotation indicating an HTTP GET
     *  request. The string provided in `value` is the relative path that will be appended to the
     *  base URL configured with Retrofit.
     *  - @[Query] (...): This Retrofit annotation is used to add query parameters to the URL.
     *
     * @param ids The list of news resources ids to retrieve. If null, all news resources will be
     * returned.
     * @return The [NetworkResponse] wrapped [List] of [NetworkNewsResource] news resources whose
     * [NetworkNewsResource.id] is in [List] of [String] parameter [ids].
     */
    @GET(value = "newsresources")
    suspend fun getNewsResources(
        @Query("id") ids: List<String>?,
    ): NetworkResponse<List<NetworkNewsResource>>

    /**
     * Gets the list of topic changes after a certain version.
     *  - @[GET] (value = "changelists/topics"): This is a Retrofit annotation indicating an HTTP
     *  GET request. The string provided in `value` is the relative path that will be appended to
     *  the base URL configured with Retrofit.
     *  - @[Query] (...): This Retrofit annotation is used to add query parameters to the URL.
     *
     * @param after The version after which to get the changes. If `null`, all changes will be returned.
     * @return The [List] of [NetworkChangeList] topic changes.
     */
    @GET(value = "changelists/topics")
    suspend fun getTopicChangeList(
        @Query("after") after: Int?,
    ): List<NetworkChangeList>

    /**
     * Gets the list of news resources changes after a certain version.
     *  - @[GET] (value = "changelists/newsresources"): This is a Retrofit annotation indicating
     *  an HTTP GET request. The string provided in `value` is the relative path that will be
     *  appended to the base URL configured with Retrofit.
     *  - @[Query] (...): This Retrofit annotation is used to add query parameters to the URL.
     *
     * @param after The version after which to get the changes. If `null`, all changes will be
     * returned.
     * @return The [List] of [NetworkChangeList] news resources changes.
     */
    @GET(value = "changelists/newsresources")
    suspend fun getNewsResourcesChangeList(
        @Query("after") after: Int?,
    ): List<NetworkChangeList>
}

/**
 * The base URL for the Now in Android API.
 */
private const val NIA_BASE_URL = BuildConfig.BACKEND_URL

/**
 * Represents a network response from the NIA_BASE_URL.
 *
 * This class is a generic wrapper for data retrieved from the network. It is used to
 * deserialize JSON responses that have a top-level "data" field.
 *
 * @param T The type of the data contained in the response.
 * @property data The actual data payload of the response.
 */
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
private data class NetworkResponse<T>(
    val data: T,
)

/**
 * [Retrofit] backed [NiaNetworkDataSource].
 *
 * This class provides a concrete implementation of [NiaNetworkDataSource] using Retrofit
 * to make network requests. It is responsible for fetching data such as topics,
 * news resources, and change lists from the backend API.
 *
 * The class is annotated with `@Singleton` to ensure that only one instance is created
 * and shared throughout the application. It is injected with dependencies like `Json`
 * for serialization and `okhttpCallFactory` for creating HTTP calls.
 *
 * The `networkApi` property is lazily initialized using Retrofit.Builder. It configures
 * the base URL, call factory (to avoid initializing OkHttp on the main thread), and
 * a converter factory for JSON serialization/deserialization.
 *
 * The methods `getTopics`, `getNewsResources`, `getTopicChangeList`, and
 * `getNewsResourceChangeList` implement the corresponding methods from the
 * [NiaNetworkDataSource] interface by delegating the calls to the `networkApi`.
 *
 * @property networkJson The [Json] instance used for serialization/deserialization injected by Hilt.
 * @property okhttpCallFactory The [Call.Factory] instance used for creating HTTP calls lazily
 * injected by Dagger.
 */
@Singleton
internal class RetrofitNiaNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : NiaNetworkDataSource {

    /**
     * The Retrofit API interface for making network requests.
     *
     * This property is initialized lazily using `trace("RetrofitNiaNetwork")` to measure
     * the initialization time. The Retrofit instance is configured with:
     * - The base URL ([NIA_BASE_URL]).
     * - A call factory that uses `dagger.Lazy<Call.Factory>` to prevent initializing OkHttp
     *   on the main thread.
     * - A converter factory for JSON serialization/deserialization using `networkJson`.
     *
     * The `create(RetrofitNiaNetworkApi::class.java)` method then creates an implementation
     * of the `RetrofitNiaNetworkApi` interface.
     */
    private val networkApi = trace("RetrofitNiaNetwork") {
        Retrofit.Builder()
            .baseUrl(NIA_BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitNiaNetworkApi::class.java)
    }

    /**
     * Retrieves a list of topics from the network.
     *
     * This function makes a network request to fetch topics based on the provided IDs.
     * If no IDs are specified, it fetches all available topics.
     *
     * @param ids A list of topic IDs to filter the results. If `null`, all topics are returned.
     * @return A list of [NetworkTopic] objects representing the fetched topics.
     */
    override suspend fun getTopics(ids: List<String>?): List<NetworkTopic> =
        networkApi.getTopics(ids = ids).data

    /**
     * Retrieves a list of news resources from the network.
     *
     * This function makes a network request to fetch news resources based on the provided IDs.
     * If no IDs are specified, it fetches all available news resources.
     *
     * @param ids A list of news resource IDs to filter the results. If `null`, all news resources
     * are returned.
     * @return A list of [NetworkNewsResource] objects representing the fetched news resources.
     */
    override suspend fun getNewsResources(ids: List<String>?): List<NetworkNewsResource> =
        networkApi.getNewsResources(ids = ids).data

    /**
     * Fetches a list of topic changes from the network.
     *
     * This function retrieves changes related to topics that have occurred after a specified
     * version. If no version is provided, it fetches all topic changes.
     *
     * @param after The version number to fetch changes after. If `null`, all changes are returned.
     * @return A list of [NetworkChangeList] objects representing the topic changes.
     */
    override suspend fun getTopicChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getTopicChangeList(after = after)

    /**
     * Fetches a list of news resource changes from the network.
     *
     * This function retrieves changes related to news resources that have occurred after a
     * specified version. If no version is provided, it fetches all news resource changes.
     *
     * @param after The version number to fetch changes after. If `null`, all changes are returned.
     * @return A list of [NetworkChangeList] objects representing the news resource changes.
     */
    override suspend fun getNewsResourceChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getNewsResourcesChangeList(after = after)
}
