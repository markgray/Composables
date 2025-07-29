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

package com.google.samples.apps.nowinandroid.core.model.data

/**
 * External data layer representation of a NiA Topic.
 *
 * @property id The unique identifier of the topic.
 * @property name The name of the topic.
 * @property shortDescription A brief description of the topic.
 * @property longDescription A more detailed description of the topic.
 * @property url An URL associated with the topic, for example, a link to a relevant webpage.
 * @property imageUrl An URL for an image representing the topic.
 */
data class Topic(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val url: String,
    val imageUrl: String,
)
