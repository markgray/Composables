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
import androidx.room.Fts4

/**
 * Fts entity for the topic. See https://developer.android.com/reference/androidx/room/Fts4.
 *
 * This class represents a table that will be used by Room to store topics for full-text search.
 * It is referenced by [TopicEntity] to enable searching for topics based on their name, short
 * description, and long description. The [Entity] annotation specifies its table name to be
 * "topicsFts". The [Fts4] annotation enables full-text search for the entity. The [ColumnInfo]
 * just specifies the name of the column in the table to be the same as the property name.
 *
 * @property topicId The unique identifier of the topic.
 * @property name The name of the topic.
 * @property shortDescription A short description of the topic.
 * @property longDescription A long description of the topic.
 */
@Entity(tableName = "topicsFts")
@Fts4
data class TopicFtsEntity(

    @ColumnInfo(name = "topicId")
    val topicId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "shortDescription")
    val shortDescription: String,

    @ColumnInfo(name = "longDescription")
    val longDescription: String,
)

/**
 * Constructs a [TopicFtsEntity] from its [TopicEntity] receiver. The [TopicFtsEntity.topicId] is
 * the [TopicEntity.id], the [TopicFtsEntity.name] is the [TopicEntity.name], the
 * [TopicFtsEntity.shortDescription] is the [TopicEntity.shortDescription], and the
 * [TopicFtsEntity.longDescription] is the [TopicEntity.longDescription].
 *
 * @return A [TopicFtsEntity] object created from the [TopicEntity] receiver.
 */
fun TopicEntity.asFtsEntity(): TopicFtsEntity = TopicFtsEntity(
    topicId = id,
    name = name,
    shortDescription = shortDescription,
    longDescription = longDescription,
)
