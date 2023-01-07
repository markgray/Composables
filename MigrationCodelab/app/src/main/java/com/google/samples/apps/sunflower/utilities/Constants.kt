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

package com.google.samples.apps.sunflower.utilities

import androidx.room.Room
import com.google.samples.apps.sunflower.data.AppDatabase
import com.google.samples.apps.sunflower.workers.SeedDatabaseWorker

/*
 * Constants used throughout the app.
 */

/**
 * This is the name of our ROOM database, which is used in the `buildDatabase` method of [AppDatabase]
 * as the `name` argument of the [Room.databaseBuilder] it uses to build the database.
 */
const val DATABASE_NAME: String = "sunflower-db"

/**
 * This is the name of the file in our app's assets which contains the JSON formatted data that is
 * read, parsed and loaded into our database by the [SeedDatabaseWorker.doWork] method.
 */
const val PLANT_DATA_FILENAME: String = "plants.json"
