/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * A database that stores SleepNight information.
 * And a global method to get access to the database.
 *
 * This pattern is pretty much the same for any database,
 * so you can reuse it.
 *
 * The `@Database` annotation marks this class as a [RoomDatabase], the `entities` annotation
 * processor argument specifies one table to be generated from the [SleepNight] class (it has an
 * `@Entity(tableName = "daily_sleep_quality_table")` which overrides the default class name table
 * name), the database `version` is 1, and the `exportSchema` = `false` argument prevents [Room]
 * from exporting the database schema into a folder.
 */
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    /**
     * Connects the database to the DAO. This is essentially an abstract method that has 0 arguments
     * and returns the class that is annotated with `@Dao`.
     */
    abstract val sleepDatabaseDao: SleepDatabaseDao

    /**
     * Define a companion object, this allows us to add functions on the SleepDatabase class.
     *
     * For example, clients can call `SleepDatabase.getInstance(context)` to instantiate
     * a new SleepDatabase.
     */
    companion object {
        /**
         * [INSTANCE] will keep a reference to any database returned via [getInstance].
         *
         * This will help us avoid repeatedly initializing the database, which is expensive.
         *
         *  The value of a volatile variable will never be cached, and all writes and
         *  reads will be done to and from the main memory. It means that changes made by one
         *  thread to shared data are visible to other threads.
         */
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        /**
         * Helper function to get the database. If a database has already been retrieved, the
         * previous database will be returned, otherwise create a new database. This function
         * is threadsafe, and callers should cache the result for multiple database calls to
         * avoid overhead.
         *
         * This is an example of a simple Singleton pattern that takes another Singleton as an
         * argument in Kotlin. To learn more about Singleton read the wikipedia article:
         * https://en.wikipedia.org/wiki/Singleton_pattern
         *
         * In a block synchronized on `this` we set our [SleepDatabase] variable `var instance`
         * to our cached [SleepDatabase] instance [INSTANCE]. If `instance` is `null` we need to
         * build (or load) an instance of [SleepDatabase] so we construct a [RoomDatabase.Builder]
         * using the context of the single, global Application object of the current process, using
         * [SleepDatabase] as the class, and the "sleep_history_database" as the name, we allow the
         * builder to destructively recreate database tables, then build the builder into a
         * [SleepDatabase] which we assign to `instance`. We cache `instance` in [INSTANCE].
         *
         * Having set `instance` either to a non-null [INSTANCE], or constructed a new [SleepDatabase]
         * for it, we return `instance` to the caller.
         *
         * @param context The application context Singleton, used to get access to the filesystem.
         * @return our singleton [SleepDatabase] instance.
         */
        fun getInstance(context: Context): SleepDatabase {
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
            synchronized(this) {

                // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                // Smart cast is only available to local variables.
                var instance = INSTANCE

                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java,
                        "sleep_history_database"
                    )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // Migration is not part of this lesson. You can learn more about
                        // migration with Room in this blog post:
                        // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                        .fallbackToDestructiveMigration(dropAllTables = true)
                        .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }

                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }
}
