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

package com.google.samples.apps.sunflower.workers

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.samples.apps.sunflower.data.AppDatabase
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantDao
import com.google.samples.apps.sunflower.utilities.PLANT_DATA_FILENAME
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import java.io.InputStream
import java.io.Reader
import java.lang.reflect.Type

/**
 * This class is responsible for seeding our data base by parsing the Json data it finds in the file
 * [PLANT_DATA_FILENAME] ("plants.json"). It is called by the `onCreate` override of the `Callback`
 * added to the [Room.databaseBuilder] which builds the singleton [AppDatabase] used by the app.
 *
 * @param context The [Context] for the database, the Application context in our case.
 * @param workerParams Parameters to setup the internal state of this worker. Nothing that our app
 * contributes appears to be used in it.
 */
class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    /**
     * A suspending method to do our work. To specify which [CoroutineDispatcher] your work should
     * run on, use `withContext()` within `doWork()`. If there is no other dispatcher declared,
     * [Dispatchers.Default] will be used. A CoroutineWorker is given a maximum of ten minutes to
     * finish its execution and return a [ListenableWorker.Result]. After this time has expired,
     * the worker will be signalled to stop.
     *
     * We return the [Result] returned by the suspend block argument we pass to the [CoroutineScope]
     * created by the [coroutineScope] method. In this block we wrap our code in a `try` block that
     * is intended to `catch` any [Exception], log it and return [Result.failure] if an exception
     * occurs. The code in the `try` block we fetch the application [Context] and use it to fetch an
     * [AssetManager] instance for the application's package then call its [AssetManager.open] method
     * to open an [InputStream] to read the file in our `assets` whose name is [PLANT_DATA_FILENAME]
     * ("plants.json"), and then using the [use] extension function on the [InputStream] we construct
     * a [JsonReader] that uses the [Reader] returned by the [InputStream.reader] method of the
     * [InputStream]. Using this [JsonReader] we define our [Type] variable `val plantType` to be
     * the type literal that the [TypeToken] method creates for a [List] of [Plant]. We then initialize
     * our [List] of [Plant] variable `val plantList` by constructing a [Gson] object and using its
     * [Gson.fromJson] method to read JSON from our [JsonReader] and convert it to an object of type
     * `plantType`.
     *
     * Having read all of the contents of our JSON file we initialize our [AppDatabase] variable
     * `val database` to the singleton instance of [AppDatabase] using the [AppDatabase.getInstance]
     * method with the Application [Context] as the `context` argument. We retrieve the [PlantDao]
     * DAO for the [Plant] table using the [AppDatabase.plantDao] method of `database` and then call
     * its [PlantDao.insertAll] method to insert all the [Plant] entries in our [List] of [Plant]
     * variable `plantList`. Finally we return [Result.success] to indicate that the work completed
     * successfully.
     *
     * @return The [ListenableWorker.Result] of the result of the background work; note that
     * dependent work will not execute if you return [ListenableWorker.Result.failure]
     */
    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(PLANT_DATA_FILENAME).use { inputStream: InputStream ->
                JsonReader(inputStream.reader()).use { jsonReader: JsonReader ->
                    val plantType: Type = object : TypeToken<List<Plant>>() {}.type
                    val plantList: List<Plant> = Gson().fromJson(jsonReader, plantType)

                    val database = AppDatabase.getInstance(context = applicationContext)
                    database.plantDao().insertAll(plantList)

                    Result.success()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        /**
         * TAG used for logging.
         */
        private const val TAG = "SeedDatabaseWorker"
    }
}
