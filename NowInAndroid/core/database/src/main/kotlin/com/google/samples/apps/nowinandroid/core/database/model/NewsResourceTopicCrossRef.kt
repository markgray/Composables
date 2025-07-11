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
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross reference for many to many relationship between [NewsResourceEntity] and [TopicEntity]. The
 * arguments of the [Entity] annotation are:
 *  - tableName: The name of the table to be created in the database is "news_resources_topics".
 *  - primaryKeys: The primary keys of the table are "news_resource_id" and "topic_id".
 *  - foreignKeys: The foreign keys of the table are a list of two [ForeignKey] annotations. The
 *  first foreign key is a [ForeignKey] annotation that specifies the [ForeignKey.entity] as the
 *  [NewsResourceEntity] class and the [ForeignKey.parentColumns] as the "id" column of the
 *  [NewsResourceEntity] class. The [ForeignKey.childColumns] is set to the "news_resource_id" column
 *  of the [NewsResourceTopicCrossRef] class. The [ForeignKey.onDelete] is set to [ForeignKey.CASCADE]
 *  (propagates the delete or update operation on the parent key to each dependent child key). The
 *  second foreign key is a [ForeignKey] annotation that specifies the [ForeignKey.entity] as the
 *  [TopicEntity] class and the [ForeignKey.parentColumns] as the "id" column of the [TopicEntity]
 *  class. The [ForeignKey.childColumns] is set to the "topic_id" column of the
 *  [NewsResourceTopicCrossRef]  class. The [ForeignKey.onDelete] is set to [ForeignKey.CASCADE]
 *  (propagates the delete or update operation on the parent key to each dependent child key).
 *  - indices: The indices of the table are a list of two [Index] annotations. The first index is a
 *  [Index] annotation that specifies the "news_resource_id" column as the index. The second index is
 *  an [Index] annotation that specifies the "topic_id" column as the index.
 *
 * @property newsResourceId The ID of the news resource is in column "news_resource_id" of the table.
 * @property topicId The ID of the topic is in column "topic_id" of the table.
 */
@Entity(
    tableName = "news_resources_topics",
    primaryKeys = ["news_resource_id", "topic_id"],
    foreignKeys = [
        ForeignKey(
            entity = NewsResourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["news_resource_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["news_resource_id"]),
        Index(value = ["topic_id"]),
    ],
)
data class NewsResourceTopicCrossRef(
    @ColumnInfo(name = "news_resource_id")
    val newsResourceId: String,
    @ColumnInfo(name = "topic_id")
    val topicId: String,
)
