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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [GardenPlanting] class (aka "garden_plantings" table). The @[Dao]
 * annotation Marks the class as a Data Access Object. Data Access Objects are the main classes where
 * you define your database interactions. They can include a variety of query methods. The class
 * marked with @[Dao] should either be an interface or an abstract class. At compile time, Room will
 * generate an implementation of this class when it is referenced by a Database. An abstract @[Dao]
 * class can optionally have a constructor that takes a Database as its only parameter.
 */
@Dao
interface GardenPlantingDao {
    /**
     * Returns a [Flow] of [List] of all the [GardenPlanting] in the `garden_plantings` table.
     * The list is automatically updated when the database changes.
     *
     * The @[Query] annotation Marks a method in a Dao annotated class as a query method. The
     * `value` argument is a SQL query that will be run when this method is called. The "SELECT *"
     * clause specifies that the query should return all columns of the queried tables, and the
     * "FROM garden_plantings" clause indicates the table to retrieve data from to be the
     * "garden_plantings" table.
     */
    @Query(value = "SELECT * FROM garden_plantings")
    fun getGardenPlantings(): Flow<List<GardenPlanting>>

    /**
     * Checks if a [Plant] is planted in the garden.
     *
     * The "EXISTS" operator is used to test for the existence of any record in a subquery. The
     * "EXISTS" operator returns `true` if the subquery returns one or more records. The subquery
     * uses a "SELECT 1" clause to select 1 row "FROM" the "garden_plantings" table and the "WHERE"
     * clause specifies that the "plant_id" column of the row should equal our [String] parameter
     * [plantId] (the colon in `:plantId` tells ROOM to substitute the value of the parameter in the
     * query) and the "LIMIT 1" clause is used to specify the number of records (1) to return. The
     * "SELECT" clause of the outer query causes Room to infer the result contents from the method's
     * return type and generate the code that will automatically convert the query result into the
     * method's return type.
     *
     * @param plantId The ID of the [Plant] to check.
     * @return A [Flow] that emits `true` if the plant is in the garden, `false` otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM garden_plantings WHERE plant_id = :plantId LIMIT 1)")
    fun isPlanted(plantId: String): Flow<Boolean>

    /**
     * This query will tell Room to query both the [Plant] and [GardenPlanting] tables and handle
     * the object mapping. The @[Transaction] annotation marks this method as a transaction method.
     * A SQL transaction is a grouping of one or more SQL statements that interact with a database.
     * A transaction in its entirety can commit to a database as a single logical unit or rollback
     * (become undone) as a single logical unit. The (SELECT DISTINCT(plant_id) FROM garden_plantings)
     * subquery produces a list of all of the rows in the "garden_plantings" table with a DISTINCT
     * "plant_id" column, and the outer query selects all of the rows in the "plants" table whose
     * "id" column is in the list returned by the subquery. ROOM automagically populates instances
     * of [PlantAndGardenPlantings] with the [Plant] and [GardenPlanting] from the query into a
     * [Flow] of [List] of [PlantAndGardenPlantings]. This is due to a @[Relation] annotation
     * in [PlantAndGardenPlantings] which can be used in a POJO to automatically fetch related
     * entities. When the POJO is returned from a query, all of its relations are also fetched
     * by Room.
     *
     * @return A [Flow] of [List] of [PlantAndGardenPlantings] containing all of the [Plant]s
     * planted in the garden.
     */
    @Transaction
    @Query("SELECT * FROM plants WHERE id IN (SELECT DISTINCT(plant_id) FROM garden_plantings)")
    fun getPlantedGardens(): Flow<List<PlantAndGardenPlantings>>

    /**
     * This method inserts its [GardenPlanting] argument [gardenPlanting] into the "garden_plantings"
     * table and returns the row id. The @[Insert] annotation Marks a method in a Dao annotated class
     * as an insert method. The implementation of the method will insert its parameters into the
     * database. All of the parameters of the Insert method must either be classes annotated with
     * @[Entity] or collections or arrays of it.
     *
     * @param gardenPlanting the [GardenPlanting] instance to be inserted in the "garden_plantings"
     * table.
     * @return The row id of the [GardenPlanting] that was inserted
     */
    @Insert
    suspend fun insertGardenPlanting(gardenPlanting: GardenPlanting): Long

    /**
     * Removes its [GardenPlanting] parameter [gardenPlanting] from the "garden_plantings" table of
     * the database. The @[Delete] annotation Marks a method in a Dao annotated class as a delete
     * method. The implementation of the method will delete its parameters from the database. All of
     * the parameters of the Delete method must either be classes annotated with @[Entity] or
     * collections or arrays of it.
     *
     * @param gardenPlanting the [GardenPlanting] that should be removed from the "garden_plantings"
     * table of the database.
     */
    @Delete
    suspend fun deleteGardenPlanting(gardenPlanting: GardenPlanting)
}
