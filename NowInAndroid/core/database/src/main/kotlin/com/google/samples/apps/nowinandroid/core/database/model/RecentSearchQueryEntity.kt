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

package com.google.samples.apps.nowinandroid.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

/**
 * Defines an database entity that stores recent search queries. The [Entity] annotation
 * specifies its table name to be "recentSearchQueries". The [PrimaryKey] annotation specifies
 * the primary key of the table to be "query". The [ColumnInfo] annotation specifies the name
 * of the column in the table "queriedDate" for the [queriedDate] property.
 *
 * Its properties are:
 *  - [query]: The unique ID of the news resource.
 *  - [queriedDate]: The date when the news resource was published.
 */
@Entity(
    tableName = "recentSearchQueries",
)
data class RecentSearchQueryEntity(
    @PrimaryKey
    val query: String,
    @ColumnInfo
    val queriedDate: Instant,
)
