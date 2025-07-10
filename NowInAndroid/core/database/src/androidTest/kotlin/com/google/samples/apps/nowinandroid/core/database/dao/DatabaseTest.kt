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
     * TODO: Continue here.
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

    @After
    fun teardown() = db.close()
}
