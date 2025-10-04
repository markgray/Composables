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

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.samples.apps.sunflower.utilities.DATABASE_NAME
import com.google.samples.apps.sunflower.utilities.PLANT_DATA_FILENAME
import com.google.samples.apps.sunflower.workers.SeedDatabaseWorker
import com.google.samples.apps.sunflower.workers.SeedDatabaseWorker.Companion.KEY_FILENAME
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * The Room database for this app.
 *
 * This database stores the [Plant] and [GardenPlanting] information.
 * It is pre-populated with plant data from `assets/plants.json`.
 *
 * The @[Database] annotation Marks the class as a [RoomDatabase]. The @[Database.entities] property
 * of the annotation is the list of "entities" included in the database. Each entity turns into a
 * `table` in the database. The [GardenPlanting] and [Plant] classes are `data class`'es which are
 * annotated with @[Entity], with the arguments of their annotation defining the table name, the
 * indices into thetable as well as the relationship of the table to other tables in the database
 * (ie. any [ForeignKey]'s associated with the table). The @[TypeConverters] annotation specifies a
 * class which contains [TypeConverter]'s to allow Room to reference complex data types, in our
 * case [Converters.calendarToDatestamp] and [Converters.datestampToCalendar]
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
         * the new [AppDatabase].         *
         */
        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Returns our [AppDatabase] field [instance] if it is not `null` or if it is `null` calls
         * our [buildDatabase] method to build an [AppDatabase] instance, caches it in [instance]
         * and returns it.
         *
         * @param context the [Context] of the single, global [ApplicationContext] object of the
         * current process.
         * @return our singleton instance of [AppDatabase].
         */
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(lock = this) {
                instance ?: buildDatabase(context = context).also { instance = it }
            }
        }

        /**
         * Create and pre-populate the database. See this article for more details:
         * https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1
         *
         * We construct an instance of [RoomDatabase.Builder] by calling [Room.databaseBuilder] with
         * the arguments:
         *  - `context`: is our [Context] parameter [context].
         *  - `klass`: is the java [Class] of [AppDatabase].
         *  - `name`: is our [String] constant [DATABASE_NAME] ("sunflower-db")
         *
         * We chain to this [RoomDatabase.Builder], a [RoomDatabase.Builder.addCallback] whose
         * `callback` argument is a [Callback] object which overrides its [Callback.onCreate]
         * method that calls its super's implementation of `onCreate` with the [SupportSQLiteDatabase]
         * passed the method then initializes its `request` variable with a [OneTimeWorkRequest] for
         * [SeedDatabaseWorker] whose `inputData` is the [Pair] of [KEY_FILENAME]
         * ("PLANT_DATA_FILENAME") to [PLANT_DATA_FILENAME] ("plants.json") which will cause it to
         * parse the json in assets file "plants.json" and insert the data in the "plants" table of
         * the database. It then has [WorkManager] enqueue the `request`. Finally the [buildDatabase]
         * method uses the [RoomDatabase.Builder.build] method to build the [AppDatabase] and returns
         * it to the caller.
         *
         * @param context the [Context] of the single, global [ApplicationContext] object of the
         * current process.
         * @return a new instance of [AppDatabase]
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = DATABASE_NAME
            )
                .addCallback(
                    callback = object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db = db)
                            val request: OneTimeWorkRequest =
                                OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                                    .setInputData(inputData = workDataOf(KEY_FILENAME to PLANT_DATA_FILENAME))
                                    .build()
                            WorkManager.getInstance(context = context).enqueue(request = request)
                        }
                    }
                )
                .build()
        }
    }
}
