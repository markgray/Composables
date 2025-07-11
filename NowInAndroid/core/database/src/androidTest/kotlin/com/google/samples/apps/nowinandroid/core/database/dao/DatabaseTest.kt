/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import org.junit.After
import org.junit.Before

/**
 * Abstract class for database tests, providing common setup and teardown logic.
 * This class initializes an in-memory Room database and provides DAOs for testing.
 */
internal abstract class DatabaseTest {

    /**
     * The in-memory Room database instance.
     * Initialized in the [setup] method.
     */
    private lateinit var db: NiaDatabase

    /**
     * The Data Access Object for the [NewsResourceEntity] class.
     * Initialized in the [setup] method.
     */
    protected lateinit var newsResourceDao: NewsResourceDao

    /**
     * The Data Access Object for the [TopicEntity] class.
     * Initialized in the [setup] method.
     */
    protected lateinit var topicDao: TopicDao

    /**
     * Sets up the in-memory Room database and initializes the DAOs before each test.
     * This method is annotated with [Before] to ensure it runs before each test case.
     *
     * We initialize our [NiaDatabase] property [db] to the result returned by the [run]
     * function when called with its [DatabaseTest] `block` lambda argument a lambda wherein we
     * initialize our [Context] variable `context` with the application context returned by
     * [ApplicationProvider.getApplicationContext], and then build an in-memory Room database
     * using the [RoomDatabase.Builder] returned by the [Room.inMemoryDatabaseBuilder] method whose
     * context is `context` and whose database `klass` is [NiaDatabase]. We initialize our
     * [NewsResourceDao] property [newsResourceDao] with the [NiaDatabase.newsResourceDao] of
     * the [NiaDatabase] property [db], and we initialize our [TopicDao] property [topicDao] with
     * the [NiaDatabase.topicDao] of the [NiaDatabase] property [db].
     */
    @Before
    fun setup() {
        db = run {
            val context: Context = ApplicationProvider.getApplicationContext()
            Room.inMemoryDatabaseBuilder(
                context = context,
                klass = NiaDatabase::class.java,
            ).build()
        }
        newsResourceDao = db.newsResourceDao()
        topicDao = db.topicDao()
    }

    /**
     * Closes the in-memory Room database after each test.
     * This method is annotated with [After] to ensure it runs after each test case,
     * releasing database resources.
     */
    @After
    fun teardown() = db.close()
}
