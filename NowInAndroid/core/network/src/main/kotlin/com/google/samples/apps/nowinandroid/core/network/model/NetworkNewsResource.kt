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

import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import kotlinx.datetime.Instant
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Network representation of [NewsResource] when fetched from /newsresources
 *  - @[OptIn] ([InternalSerializationApi]::class): This annotation indicates that the class uses
 *  experimental or internal features of the Kotlinx Serialization library. It's a way for library
 *  authors to signal that certain APIs might change in the future.
 *  - @[Serializable]: This annotation comes from the Kotlinx Serialization library. It indicates
 *  that instances of this class can be automatically converted to and from a serialized format
 *  (like JSON). This is crucial for network communication, as data needs to be in a format that
 *  can be transmitted over the internet.
 *
 * @property id The id of the news resource.
 * @property title The title of the news resource.
 * @property content A description of the news resource.
 * @property url The URL to the original content of the news resource.
 * @property headerImageUrl The URL to the header image of the news resource.
 * @property publishDate The date when the news resource was published.
 * @property type The type of the news resource.
 * @property topics A list of topics associated with the news resource.
 */
@OptIn(InternalSerializationApi::class)
@Serializable
data class NetworkNewsResource(
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    val headerImageUrl: String,
    val publishDate: Instant,
    val type: String,
    val topics: List<String> = listOf(),
)
