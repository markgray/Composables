/*
 * Copyright 2023 The Android Open Source Project
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
 * An entity that holds the search result.
 *
 * This class is used to represent the result of a search query, which can include a list of
 * matching [Topic]s and a list of matching [NewsResource]s.
 *
 * @property topics The list of topics that match the search query.
 * @property newsResources The list of news resources that match the search query.
 */
data class SearchResult(
    val topics: List<Topic> = emptyList(),
    val newsResources: List<NewsResource> = emptyList(),
)
