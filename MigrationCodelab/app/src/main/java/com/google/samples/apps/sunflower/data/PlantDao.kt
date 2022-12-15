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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * The Data Access Object for the Plant class (aka "plants" table in the database).
 */
@Dao
interface PlantDao {
    /**
     * Returns a [LiveData] wrapped [List] of all of the [Plant] instances in the "plants" table
     * sorted by its "name" column.
     *
     * @return a [LiveData] wrapped [List] of all of the [Plant] instances in the "plants" table
     * sorted by its "name" column.
     */
    @Query("SELECT * FROM plants ORDER BY name")
    fun getPlants(): LiveData<List<Plant>>

    /**
     * Returns a [LiveData] wrapped [List] of all of the [Plant] instances in the "plants" table
     * whose "growZoneNumber" column is equal to its [growZoneNumber] parameter, sorted by their
     * "name" column.
     *
     * @param growZoneNumber the grow zone number that we want to match the "growZoneNumber" column
     * of the [Plant]s selected to be returned.
     * @return a [LiveData] wrapped [List] of all of the [Plant] instances in the "plants" table
     * whose "growZoneNumber" column is equal to its [growZoneNumber] parameter, sorted by their
     * "name" column.
     */
    @Query("SELECT * FROM plants WHERE growZoneNumber = :growZoneNumber ORDER BY name")
    fun getPlantsWithGrowZoneNumber(growZoneNumber: Int): LiveData<List<Plant>>

    /**
     * Returns the [LiveData] wrapped [Plant] instance in the "plants" table whose "id" column is
     * equal to our [plantId] parameter.
     *
     * @param plantId the [String] that should match the "id" column of the [Plant] returned.
     * @return the [LiveData] wrapped [Plant] instance in the "plants" table whose "id" column is
     * equal to our [plantId] parameter.
     */
    @Query("SELECT * FROM plants WHERE id = :plantId")
    fun getPlant(plantId: String): LiveData<Plant>

    /**
     * Inserts all of the [Plant] instances in its [List] of [Plant] parameter [plants] into the
     * "plants" table of the database.
     *
     * @param plants the [List] of [Plant] instances that should be inserted into the "plants" table
     * of the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Plant>)
}
