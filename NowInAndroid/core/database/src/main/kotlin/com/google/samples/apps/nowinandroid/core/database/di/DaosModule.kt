/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.database.di

import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceDao
import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceFtsDao
import com.google.samples.apps.nowinandroid.core.database.dao.RecentSearchQueryDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicFtsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module that provides DAO instances.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    /**
     * Provides a [TopicDao] instance. We return the [TopicDao] instance returned from the
     * [NiaDatabase.topicDao] method of our [NiaDatabase] parameter [database].
     *
     * @param database The [NiaDatabase] instance.
     * @return A [TopicDao] instance.
     */
    @Provides
    fun providesTopicsDao(
        database: NiaDatabase,
    ): TopicDao = database.topicDao()

    /**
     * Provides a [NewsResourceDao] instance. We return the [NewsResourceDao] instance returned
     * from the [NiaDatabase.newsResourceDao] method of our [NiaDatabase] parameter [database].
     *
     * @param database The [NiaDatabase] instance.
     * @return A [NewsResourceDao] instance.
     */
    @Provides
    fun providesNewsResourceDao(
        database: NiaDatabase,
    ): NewsResourceDao = database.newsResourceDao()

    /**
     * Provides a [TopicFtsDao] instance. We return the [TopicFtsDao] instance returned from the
     * [NiaDatabase.topicFtsDao] method of our [NiaDatabase] parameter [database].
     *
     * @param database The [NiaDatabase] instance.
     * @return A [TopicFtsDao] instance.
     */
    @Provides
    fun providesTopicFtsDao(
        database: NiaDatabase,
    ): TopicFtsDao = database.topicFtsDao()

    /**
     * Provides a [NewsResourceFtsDao] instance. This DAO is used for full-text search on
     * `news_resources` table. We return the [NewsResourceFtsDao] instance returned from the
     * [NiaDatabase.newsResourceFtsDao] method of our [NiaDatabase] parameter [database].
     *
     * @param database The [NiaDatabase] instance.
     * @return A [NewsResourceFtsDao] instance.
     */
    @Provides
    fun providesNewsResourceFtsDao(
        database: NiaDatabase,
    ): NewsResourceFtsDao = database.newsResourceFtsDao()

    /**
     * Provides a [RecentSearchQueryDao] instance. This DAO is used for interacting with the
     * `recent_search_queries` table. We return the [RecentSearchQueryDao] instance returned
     * from the [NiaDatabase.recentSearchQueryDao] method of our [NiaDatabase] parameter [database].
     *
     * @param database The [NiaDatabase] instance.
     * @return A [RecentSearchQueryDao] instance.
     */
    @Provides
    fun providesRecentSearchQueryDao(
        database: NiaDatabase,
    ): RecentSearchQueryDao = database.recentSearchQueryDao()
}
