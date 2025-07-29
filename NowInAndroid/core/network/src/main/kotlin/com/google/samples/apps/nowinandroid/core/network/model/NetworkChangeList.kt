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

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Network representation of a change list for a model.
 *
 * Change lists are a representation of a server-side map like data structure of model ids to
 * metadata about that model. In a single change list, a given model id can only show up once.
 *  - @[OptIn] ([InternalSerializationApi]::class): This annotation indicates that the class uses
 *  experimental or internal features of the Kotlinx Serialization library. It's a way for library
 *  authors to signal that certain APIs might change in the future.
 *  - @[Serializable]: This annotation comes from the Kotlinx Serialization library. It indicates
 *  that instances of this class can be automatically converted to and from a serialized format
 *  (like JSON). This is crucial for network communication, as data needs to be in a format that
 *  can be transmitted over the internet.
 *
 * @property id The id of the model that was changed
 * @property changeListVersion Unique consecutive, monotonically increasing version number in the
 * collection describing the relative point of change between models in the collection
 * @property isDelete Summarizes the update to the model; whether it was deleted or updated.
 */
@OptIn(InternalSerializationApi::class)
@Serializable
data class NetworkChangeList(
    /**
     * The id of the model that was changed
     */
    val id: String,
    /**
     * Unique consecutive, monotonically increasing version number in the collection describing
     * the relative point of change between models in the collection
     */
    val changeListVersion: Int,
    /**
     * Summarizes the update to the model; whether it was deleted or updated.
     * Updates include creations.
     */
    val isDelete: Boolean,
)
