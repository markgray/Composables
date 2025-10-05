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
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.Calendar.DAY_OF_YEAR

/**
 * This data class defines the structure of a row in the "plants" table of the database. The @[Entity]
 * annotation Marks this class as an entity, and the `tableName` argument is the table name in the
 * SQLite database.
 */
@Entity(tableName = "plants")
data class Plant(
    /**
     * This is the [PrimaryKey] for a row in the "plants" table of the database. It comes from the
     * "value" of the "plantId" name in the JSON file assets/plants.json and is unique for all of
     * the plants. The @[PrimaryKey] annotation Marks a field in an [Entity] as the primary key.
     * The @[ColumnInfo] annotation specifies the column `name` for the field to be "id" (instead
     * of the default `plantId`).
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val plantId: String,
    /**
     * This is the `name` of the [Plant] and comes from the "value" of the "name" name in the JSON
     * file assets/plants.json.
     */
    val name: String,
    /**
     * This is a long-winded description of the [Plant] and comes from the "value" of the "description"
     * name in the JSON file assets/plants.json.
     */
    val description: String,
    /**
     * This is the Grow Zone Number of the plant and comes from the "value" of the "growZoneNumber"
     * name in the JSON file assets/plants.json. The lower the number is, the lower the temperatures
     * in that zone. Each zone represents ten degrees of temperature difference. Each zone is also
     * divided into “a” and “b” segments. These represent five degrees of temperature difference, but
     * our JSON file only uses the integer so that is what we use here. For example, zone 4 represents
     * minimum temperatures between -30 to -20 degrees F.
     */
    val growZoneNumber: Int,
    /**
     * How often the plant should be watered in days, it comes from the "value" of the "wateringInterval"
     * name in the JSON file assets/plants.json.
     */
    val wateringInterval: Int = 7, // how often the plant should be watered, in days
    /**
     * This is the URL for a picture of the [Plant] that has uploaded to upload.wikimedia.org, it
     * comes from the "value" of the "imageUrl" name in the JSON file assets/plants.json.
     */
    val imageUrl: String = ""
) {

    /**
     * Determines if the plant should be watered.  Returns `true` if [since]'s date > date of last
     * watering + watering Interval; `false` otherwise. If is only used by the `test_shouldBeWatered`
     * `unitTest`.
     *
     * @param since the [Calendar] for the current date and time.
     * @param lastWateringDate the [Calendar] for the last date it was watered on.
     * @return `true` if [since] is greater than [lastWateringDate] plus the value of our field
     * [wateringInterval]
     */
    fun shouldBeWatered(since: Calendar, lastWateringDate: Calendar): Boolean =
        since > lastWateringDate.apply { add(DAY_OF_YEAR, wateringInterval) }

    /**
     * Simply returns our [name] field as the [String] version of our class.
     */
    override fun toString(): String = name
}
