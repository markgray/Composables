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

import kotlinx.datetime.Instant

/**
 * External data layer representation of a fully populated NiA news resource.
 * This is the model that is used to represent a news resource in the UI.
 *
 * @property id The unique identifier of the news resource.
 * @property title The title of the news resource.
 * @property content The content of the news resource.
 * @property url The URL of the news resource.
 * @property headerImageUrl The URL of the header image for the news resource.
 * @property publishDate The date the news resource was published.
 * @property type The type of the news resource.
 * @property topics The list of topics associated with the news resource.
 */
data class NewsResource(
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    val headerImageUrl: String?,
    val publishDate: Instant,
    val type: String,
    val topics: List<Topic>,
)
