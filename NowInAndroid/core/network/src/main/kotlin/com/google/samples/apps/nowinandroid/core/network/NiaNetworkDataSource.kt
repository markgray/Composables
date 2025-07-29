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

package com.google.samples.apps.nowinandroid.core.network

import com.google.samples.apps.nowinandroid.core.network.model.NetworkChangeList
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic

/**
 * Interface representing network calls to the NIA backend
 */
interface NiaNetworkDataSource {
    /**
     * Fetches the topics from the network
     *
     * @param ids The list of topic ids to fetch. If `null`, fetches all topics.
     * @return The list of topics that match the [ids]
     */
    suspend fun getTopics(ids: List<String>? = null): List<NetworkTopic>

    /**
     * Fetches the news resources from the network
     *
     * @param ids The list of news resource ids to fetch. If `null`, fetches all news resources.
     * @return The list of news resources that match the [ids]
     */
    suspend fun getNewsResources(ids: List<String>? = null): List<NetworkNewsResource>

    /**
     * Fetches the change list for topics from the network
     *
     * @param after The change list version to fetch after. If `null`, it fetches the initial change
     * list.
     * @return The list of change lists for topics
     */
    suspend fun getTopicChangeList(after: Int? = null): List<NetworkChangeList>

    /**
     * Fetches the change list for news resources from the network
     *
     * @param after The change list version to fetch after. If `null`, fetches the initial change list.
     * @return The list of change lists for news resources
     */
    suspend fun getNewsResourceChangeList(after: Int? = null): List<NetworkChangeList>
}
