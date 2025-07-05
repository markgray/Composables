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

package com.google.samples.apps.nowinandroid.core.data.model

import com.google.samples.apps.nowinandroid.core.database.model.RecentSearchQueryEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Data layer representation of a recent search query.
 *
 * @property query The search query.
 * @property queriedDate The date when the query was executed.
 */
data class RecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.System.now(),
)

/**
 * Maps a [RecentSearchQueryEntity] to a [RecentSearchQuery]
 */
fun RecentSearchQueryEntity.asExternalModel(): RecentSearchQuery =
    RecentSearchQuery(
        query = query,
        queriedDate = queriedDate,
    )
