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

@file:Suppress("unused")

package com.google.samples.apps.sunflower.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.samples.apps.sunflower.data.AppDatabase
import com.google.samples.apps.sunflower.data.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.lang.reflect.Type

/**
 * A worker that seeds the database with a list of plants.
 *
 * This worker reads a JSON file from the assets folder, parses it into a list of [Plant] objects,
 * and then inserts them into the database. This is a one-time operation that should be triggered
 * when the application is first installed or when the database is created.
 *
 * The name of the JSON file to be read is passed as input data with the key [KEY_FILENAME].
 *
 * @param context The application context.
 * @param workerParams Parameters to setup the worker, including input data.
 */
class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext = context, params = workerParams) {

    /**
     * Reads a JSON file from the assets folder and inserts the [Plant] data into the database.
     *
     * This method is the main entry point for the worker's execution. It reads the filename
     * from the input data, opens the corresponding JSON file from the assets, parses it into
     * a list of [Plant] objects using Gson, and then inserts them into the database via the
     * [AppDatabase.plantDao] Dao object.
     *
     * The operation is performed on the [Dispatchers.IO] context to avoid blocking the main thread.
     *
     * @return [Result.success] if the database is seeded successfully.
     * @return [Result.failure] if the filename is not provided in the input data or if any
     * exception occurs during the file reading, parsing, or database insertion.
     */
    override suspend fun doWork(): Result = withContext(context = Dispatchers.IO) {
        try {
            val filename: String? = inputData.getString(key = KEY_FILENAME)
            if (filename != null) {
                applicationContext.assets.open(filename).use { inputStream: InputStream ->
                    JsonReader(inputStream.reader()).use { jsonReader: JsonReader ->
                        val plantType: Type? = object : TypeToken<List<Plant>>() {}.type
                        val plantList: List<Plant> = Gson().fromJson(jsonReader, plantType)

                        val database: AppDatabase =
                            AppDatabase.getInstance(context = applicationContext)
                        database.plantDao().upsertAll(plants = plantList)

                        Result.success()
                    }
                }
            } else {
                Log.e(TAG, "Error seeding database - no valid filename")
                Result.failure()
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        /**
         * The tag used for logging purposes.
         */
        private const val TAG = "SeedDatabaseWorker"

        /**
         * The key used to retrieve the filename from the input data.
         */
        const val KEY_FILENAME: String = "PLANT_DATA_FILENAME"
    }
}
