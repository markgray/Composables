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

package com.google.samples.apps.nowinandroid.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import kotlinx.datetime.Instant

/**
 * Defines an NiA news resource that can be stored in a database. The [Entity] annotation specifies
 * its table name to be "news_resources". The [PrimaryKey] annotation specifies the primary key
 * of the table to be [id]. The [ColumnInfo] annotation specifies the name of the column in the table
 * "header_image_url" for the [headerImageUrl] property, and "publish_date" for the [publishDate]
 * property.
 *
 * Its properties are:
 *  - [id]: The unique ID of the news resource.
 *  - [title]: The title of the news resource.
 *  - [content]: The content of the news resource.
 *  - [url]: The URL of the news resource.
 *  - [headerImageUrl]: The URL of the header image of the news resource.
 *  - [publishDate]: The date when the news resource was published.
 *  - [type]: The type of the news resource.
 */
@Entity(
    tableName = "news_resources",
)
data class NewsResourceEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    @ColumnInfo(name = "header_image_url")
    val headerImageUrl: String?,
    @ColumnInfo(name = "publish_date")
    val publishDate: Instant,
    val type: String,
)

/**
 * Converts the database [NewsResourceEntity] to the external model [NewsResource].
 * This function is an extension function on the [NewsResourceEntity] class.
 * It creates a new [NewsResource] object and populates its properties using the corresponding
 * properties from the [NewsResourceEntity] object which share the same names.
 * The `topics` property of the resulting [NewsResource] is initialized as an empty list,
 * as this information is not directly available in the [NewsResourceEntity] and needs to be
 * fetched separately or joined from another table.
 */
fun NewsResourceEntity.asExternalModel(): NewsResource = NewsResource(
    id = id,
    title = title,
    content = content,
    url = url,
    headerImageUrl = headerImageUrl,
    publishDate = publishDate,
    type = type,
    topics = listOf(),
)
