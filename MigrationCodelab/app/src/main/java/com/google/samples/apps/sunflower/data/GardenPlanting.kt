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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

/**
 * [GardenPlanting] represents when a user adds a [Plant] to their garden, with useful metadata.
 * Properties such as [lastWateringDate] are used for notifications (such as when to water the
 * plant). Declaring the column info allows for the renaming of variables without implementing a
 * database migration, as the column name would not change.
 *
 * The `@Entity` annotation Marks the class as an entity. This class will have a mapping SQLite table
 * in the database. The arguments of the `@Entity` annotation are:
 *  - `tableName` The table name in the SQLite database. If not set, defaults to the class name. We
 *  use "garden_plantings" as our table name.
 *  - `foreignKeys` List of [ForeignKey] constraints on this entity. Our only [ForeignKey] constraint
 *  is one for the [Plant] `entity` (table name "plants") with the `parentColumns` a list containing
 *  the [String] "id" (The list of column names in the parent Entity - which is us), and the
 *  `childColumns` is a list containing the [String] "plant_id" which is the column name we use to
 *  store references to [Plant] instances in the "plants" table.
 *  TODO: verify the above from https://sqlite.org/foreignkeys.html (I think it is confused a bit)
 *  Attempting to insert a row into the "garden_plantings" table that does not correspond to any row
 *  in the "plants" table will fail, as will attempting to delete a row from the "plants" table when
 *  there exist dependent rows in the "garden_plantings" table.
 */
@Entity(
    tableName = "garden_plantings",
    foreignKeys = [
        ForeignKey(entity = Plant::class, parentColumns = ["id"], childColumns = ["plant_id"])
    ],
    indices = [Index("plant_id")]
)
data class GardenPlanting(
    /**
     * TODO: Add kdoc
     */
    @ColumnInfo(name = "plant_id") val plantId: String,

    /**
     * Indicates when the [Plant] was planted. Used for showing notification when it's time
     * to harvest the plant.
     */
    @ColumnInfo(name = "plant_date") val plantDate: Calendar = Calendar.getInstance(),

    /**
     * Indicates when the [Plant] was last watered. Used for showing notification when it's
     * time to water the plant.
     */
    @ColumnInfo(name = "last_watering_date")
    val lastWateringDate: Calendar = Calendar.getInstance()
) {
    /**
     * TODO: Add kdoc
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var gardenPlantingId: Long = 0
}
