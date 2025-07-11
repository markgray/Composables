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
import com.google.samples.apps.nowinandroid.core.model.data.Topic

/**
 * Defines a topic a user may follow. It has a many-to-many relationship with [NewsResourceEntity]
 * via [NewsResourceTopicCrossRef]. The [Entity] annotation specifies its table name to be
 * "topics". The [PrimaryKey] annotation specifies the primary key of the table to be "id". The
 * [ColumnInfo] annotation specifies that the default value of the [longDescription] column, the
 * [url] column, and the [imageUrl] column should be empty strings. The properties are:
 *  - [id]: The unique ID of the topic.
 *  - [name]: The name of the topic.
 *  - [shortDescription]: The short description of the topic.
 *  - [longDescription]: The long description of the topic.
 *  - [url]: The URL of the topic.
 *  - [imageUrl]: The URL of the image associated with the topic.
 */
@Entity(
    tableName = "topics",
)
data class TopicEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val shortDescription: String,
    @ColumnInfo(defaultValue = "")
    val longDescription: String,
    @ColumnInfo(defaultValue = "")
    val url: String,
    @ColumnInfo(defaultValue = "")
    val imageUrl: String,
)

/**
 * Maps a [TopicEntity] to a [Topic] external model. It just copies the properties of the
 * [TopicEntity] receiver to the properties of a new instance of [Topic] with the same names.
 */
fun TopicEntity.asExternalModel(): Topic = Topic(
    id = id,
    name = name,
    shortDescription = shortDescription,
    longDescription = longDescription,
    url = url,
    imageUrl = imageUrl,
)
