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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/**
 * The Data Access Object for the [GardenPlanting] class (aka "garden_plantings" table). The `@Dao`
 * annotation Marks the class as a Data Access Object. Data Access Objects are the main classes where
 * you define your database interactions. They can include a variety of query methods. The class
 * marked with `@Dao` should either be an interface or an abstract class. At compile time, Room will
 * generate an implementation of this class when it is referenced by a Database. An abstract `@Dao`
 * class can optionally have a constructor that takes a Database as its only parameter.
 */
@Dao
interface GardenPlantingDao {
    /**
     * Returns a [LiveData] wrapped [List] of all the [GardenPlanting] rows in the "garden_plantings"
     * table. The `@Query` annotation Marks a method in a Dao annotated class as a query method. The
     * `value` argument is a SQL query that will be run when this method is called. The "SELECT *"
     * clause specifies that the query should return all columns of the queried tables, and the
     * "FROM garden_plantings" clause indicates the table to retrieve data from to be the
     * "garden_plantings" table.
     *
     * @return a [LiveData] wrapped [List] of all the [GardenPlanting] rows in the "garden_plantings"
     * table.
     */
    @Query(value = "SELECT * FROM garden_plantings")
    fun getGardenPlantings(): LiveData<List<GardenPlanting>>

    /**
     * Returns a [LiveData] wrapped [Boolean] that is `true` if and only if one of the rows of the
     * "garden_plantings" table has a "plant_id" column containing the value of our [String] parameter
     * [plantId]. The "EXISTS" operator is used to test for the existence of any record in a subquery.
     * The "EXISTS" operator returns `true` if the subquery returns one or more records. The subquery
     * uses a "SELECT 1" clause to select 1 row "FROM" the "garden_plantings" table and the "WHERE"
     * clause specifies that the "plant_id" column of the row should equal our [String] parameter
     * [plantId] (the colon in `:plantId` tells ROOM to substitute the value of the parameter in the
     * query) and the "LIMIT 1" clause is used to specify the number of records (1) to return. The
     * "SELECT" clause of the outer query causes Room to infer the result contents from the method's
     * return type and generate the code that will automatically convert the query result into the
     * method's return type.
     *
     * @param plantId the "id" of the [Plant] row in the "plants" table that we are searching for in
     * the "garden_plantings" table. The `plant_id` column of the "garden_plantings" table holds the
     * references to [Plant] rows in the "plants" table.
     * @return a [LiveData] wrapped [Boolean] that is `true` if and only if one of the rows of the
     * "garden_plantings" table has a "plant_id" column containing the value of our [String] parameter
     * [plantId].
     */
    @Query(value = "SELECT EXISTS(SELECT 1 FROM garden_plantings WHERE plant_id = :plantId LIMIT 1)")
    fun isPlanted(plantId: String): LiveData<Boolean>

    /**
     * This query will tell Room to query both the [Plant] and [GardenPlanting] tables and handle
     * the object mapping. The `@Transaction` annotation marks this method as a transaction method.
     * A SQL transaction is a grouping of one or more SQL statements that interact with a database.
     * A transaction in its entirety can commit to a database as a single logical unit or rollback
     * (become undone) as a single logical unit. The (SELECT DISTINCT(plant_id) FROM garden_plantings)
     * subquery produces a list of all of the rows in the "garden_plantings" table with a DISTINCT
     * "plant_id" column, and the outer query selects all of the rows in the "plants" table whose
     * "id" column is in the list returned by the subquery. ROOM automagically populates instances
     * of [PlantAndGardenPlantings] with the [Plant] and [GardenPlanting] from the query into a
     * [LiveData] wrapped [List] of [PlantAndGardenPlantings]. This is due to a `@Relation` annotation
     * in [PlantAndGardenPlantings] which can be used in a POJO to automatically fetch relation entities.
     * When the POJO is returned from a query, all of its relations are also fetched by Room.
     *
     * @return a [LiveData] wrapped [List] of [PlantAndGardenPlantings] which captures the relationship
     * between a [Plant] and a user's [GardenPlanting].
     */
    @Transaction
    @Query("SELECT * FROM plants WHERE id IN (SELECT DISTINCT(plant_id) FROM garden_plantings)")
    fun getPlantedGardens(): LiveData<List<PlantAndGardenPlantings>>

    /**
     * TODO: Add kdoc
     */
    @Insert
    suspend fun insertGardenPlanting(gardenPlanting: GardenPlanting): Long

    /**
     * TODO: Add kdoc
     */
    @Delete
    suspend fun deleteGardenPlanting(gardenPlanting: GardenPlanting)
}
