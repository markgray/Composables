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

package com.google.samples.apps.nowinandroid.core.network.model

import com.google.samples.apps.nowinandroid.core.model.data.Topic
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Network representation of [Topic]
 *  - @[OptIn] ([InternalSerializationApi]::class): This annotation indicates that the class uses
 *  experimental or internal features of the Kotlinx Serialization library. It's a way for library
 *  authors to signal that certain APIs might change in the future.
 *  - @[Serializable]: This annotation comes from the Kotlinx Serialization library. It indicates
 *  that instances of this class can be automatically converted to and from a serialized format
 *  (like JSON). This is crucial for network communication, as data needs to be in a format that
 *  can be transmitted over the internet.
 *
 * @property id The id of the topic.
 * @property name The name of the topic.
 * @property shortDescription A short description of the topic.
 * @property longDescription A long description of the topic.
 * @property url The URL to the original content of the topic.
 * @property imageUrl The URL to the image of the topic.
 * @property followed Whether the user is following the topic.
 */
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkTopic(
    val id: String,
    val name: String = "",
    val shortDescription: String = "",
    val longDescription: String = "",
    val url: String = "",
    val imageUrl: String = "",
    val followed: Boolean = false,
)

/**
 * Converts a [NetworkTopic] object to a [Topic] object.
 *
 * This function is an extension function for the [NetworkTopic] class. It takes a [NetworkTopic]
 * object as its receiver and returns a [Topic] object. The [Topic] object is created by copying the
 * properties of the [NetworkTopic] object into a new instance of [Topic].
 *
 * This function is useful for converting data from the network layer to the domain layer. The
 * network layer uses [NetworkTopic] objects to represent topics, while the domain layer uses
 * [Topic] objects. This function allows you to easily convert between these two representations.
 */
fun NetworkTopic.asExternalModel(): Topic =
    Topic(
        id = id,
        name = name,
        shortDescription = shortDescription,
        longDescription = longDescription,
        url = url,
        imageUrl = imageUrl,
    )
