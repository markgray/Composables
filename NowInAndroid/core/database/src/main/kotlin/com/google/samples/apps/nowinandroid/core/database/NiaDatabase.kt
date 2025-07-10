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

package com.google.samples.apps.nowinandroid.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceDao
import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceFtsDao
import com.google.samples.apps.nowinandroid.core.database.dao.RecentSearchQueryDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicFtsDao
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceFtsEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceTopicCrossRef
import com.google.samples.apps.nowinandroid.core.database.model.RecentSearchQueryEntity
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.database.model.TopicFtsEntity
import com.google.samples.apps.nowinandroid.core.database.util.InstantConverter

/**
 * Nia Room Database.
 *
 * This database stores information about the topics, news resources, and recent searches.
 * It is used by the various DAOs to access and manipulate the data.
 *
 * The database is versioned, and migrations are provided to update the schema when necessary.
 * The schema is also exported to a JSON file, which can be used to verify the schema.
 *
 * The database uses a custom type converter to store and retrieve `Instant` objects. The
 * arguments of the [Database] annotation are:
 *  - [Database.entities]: An array of classes that represent the tables in the database. They
 *  are all data classes anootsted with [Entity]. [NewsResourceEntity] creates the table
 *  "news_resources" in the database, [NewsResourceTopicCrossRef] creates the table
 *  "news_resources_topics" in the database, [NewsResourceFtsEntity] creates the table
 *  "newsResourcesFts" in the database, [TopicEntity] creates the table "topics" in the database,
 *  [TopicFtsEntity] creates the table "topicsFts" in the database, and [RecentSearchQueryEntity]
 *  creates the table "recent_search_queries" in the database.
 *  - [Database.version]: The version of the database. This is used to determine if the database
 *  needs to be upgraded or downgraded. Today it is set to `14`.
 *  - [Database.autoMigrations]: An array of [AutoMigration] objects that represent the
 *  migrations that will be performed when the database is upgraded. The [AutoMigration] annotation
 *  is used to specify the version range of the migration and the migration class. The migration
 *  classes are `SchemaXtoY` where `X` is the version of the database that the migration is
 *  from and `Y` is the version of the database that the migration is to. The migration classes
 *  are in the [DatabaseMigrations] object.
 *  - [Database.exportSchema]: A boolean that indicates whether the database schema should be
 *  exported to a JSON file. This is set to `true`.
 *
 * The [TypeConverters] annotation is used to specify the type converters that will be used by the
 * database which in our case is the [InstantConverter] class.
 */
@Database(
    entities = [
        NewsResourceEntity::class,
        NewsResourceTopicCrossRef::class,
        NewsResourceFtsEntity::class,
        TopicEntity::class,
        TopicFtsEntity::class,
        RecentSearchQueryEntity::class,
    ],
    version = 14,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = DatabaseMigrations.Schema2to3::class),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11, spec = DatabaseMigrations.Schema10to11::class),
        AutoMigration(from = 11, to = 12, spec = DatabaseMigrations.Schema11to12::class),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
    ],
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class NiaDatabase : RoomDatabase() {
    /**
     * The Data Access Object for the [TopicEntity] class.
     *
     * @return The [TopicDao] Data Access Object for the [TopicEntity] class.
     */
    abstract fun topicDao(): TopicDao

    /**
     * The Data Access Object for the [NewsResourceEntity] class.
     *
     * @return The [NewsResourceDao] Data Access Object for the [NewsResourceEntity] class.
     */
    abstract fun newsResourceDao(): NewsResourceDao

    /**
     * The Data Access Object for the [TopicFtsEntity] class.
     *
     * @return The [TopicFtsDao] Data Access Object for the [TopicFtsEntity] class.
     */
    abstract fun topicFtsDao(): TopicFtsDao

    /**
     * The Data Access Object for the [NewsResourceFtsEntity] class.
     *
     * @return The [NewsResourceFtsDao] Data Access Object for the [NewsResourceFtsEntity] class.
     */
    abstract fun newsResourceFtsDao(): NewsResourceFtsDao

    /**
     * The Data Access Object for the [RecentSearchQueryEntity] class.
     *
     * @return The [RecentSearchQueryDao] Data Access Object for the [RecentSearchQueryEntity] class.
     */
    abstract fun recentSearchQueryDao(): RecentSearchQueryDao
}
