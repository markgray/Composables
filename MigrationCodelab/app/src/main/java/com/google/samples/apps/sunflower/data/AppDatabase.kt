/*
 * Copyright 2018 Google LLC
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

package com.google.samples.apps.sunflower.data

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.ForeignKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.samples.apps.sunflower.utilities.DATABASE_NAME
import com.google.samples.apps.sunflower.utilities.PLANT_DATA_FILENAME
import com.google.samples.apps.sunflower.workers.SeedDatabaseWorker

/**
 * The Room database for this app. The `@Database` annotation Marks the class as a RoomDatabase. The
 * The `entities` argument is the list of "entities" included in the database. Each entity turns
 * into a `table` in the database. [GardenPlanting] and [Plant] classes are `data class`'es which
 * are annotated with `@Entity`, with the arguments of their annotation defining the table name, the
 * indices into the table as well as the relationship of the table to other tables in the database
 * (ie. any [ForeignKey]'s associated with the table). The `@TypeConverters` annotation specified a
 * class which contains type converters to allow Room to reference complex data types:
 * [Converters.calendarToDatestamp] and [Converters.datestampToCalendar]
 */
@Database(entities = [GardenPlanting::class, Plant::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * This method returns the [GardenPlantingDao] DAO for the [GardenPlanting] table.
     */
    abstract fun gardenPlantingDao(): GardenPlantingDao

    /**
     * This method returns the [PlantDao] DAO for the [Plant] table.
     */
    abstract fun plantDao(): PlantDao

    companion object {

        /**
         * For Singleton instantiation, our [getInstance] method will return this [AppDatabase] if
         * it is not `null`, or use the [buildDatabase] method to build one, cache it here and return
         * the new [AppDatabase].
         */
        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Returns our [AppDatabase] field [instance] if it is not `null` or if it is `null` call
         * our [buildDatabase] to build an [AppDatabase] instance, cache it in [instance] and return
         * it.
         *
         * @param context the the [Context] of the single, global [Application] object of the
         * current process.
         * @return our singleton instance of [AppDatabase].
         */
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        /**
         * Create and pre-populate the database. See this article for more details:
         * https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
         *
         * We create a [RoomDatabase.Builder] for a persistent database using our [Context] parameter
         * [context] as the context for the database (the Application context in our case). With
         * [AppDatabase] the abstract class which is annotated with [Database] and extends [RoomDatabase],
         * and [DATABASE_NAME] ("sunflower-db") the name of the database file. We use its
         * [RoomDatabase.Builder.addCallback] method to add an anonymous [RoomDatabase.Callback] to
         * the database and override the [RoomDatabase.Callback.onCreate] method in order to have
         * our [SeedDatabaseWorker] "seed" the database with the initial data from the [PLANT_DATA_FILENAME]
         * ("plants.json") json file when the database is created for the first time. Finally we build
         * the [Room.databaseBuilder] and return the [AppDatabase] it builds.
         *
         * @param context The [Context] for the database, the Application context in our case.
         * @return the singleton [AppDatabase] for the app.
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : Callback() {
                    /**
                     * Called when the database is created for the first time. This is called after
                     * all the tables are created. First we call our super's implementation of
                     * `onCreate`, then we initialize our [OneTimeWorkRequest] variable `val request`
                     * with an instance that uses [SeedDatabaseWorker] as the [ListenableWorker] class
                     * that will perform work asynchronously in [WorkManager].
                     *
                     * @param db The database.
                     */
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val request: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })
                .build()
        }
    }
}
