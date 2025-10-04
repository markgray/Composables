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

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * This class captures the relationship between a [Plant] and a user's [GardenPlanting], which is
 * used by Room to fetch the related entities.
 */
data class PlantAndGardenPlantings(
    /**
     * This is the [Plant] that was planted in the [GardenPlanting]. The @[Embedded] annotation
     * Marks a field of a POJO to allow nested fields (i.e. fields of the annotated field's class)
     * to be referenced directly in the SQL queries. If the container is an [Entity], these sub fields
     * will be columns in the [Entity]'s database table.
     */
    @Embedded
    val plant: Plant,

    /**
     * This is the [GardenPlanting] that the [Plant] is "planted" in. The [List] will contain only
     * the one entry at index 0, the one for the associated [Plant], but it needs to be a [List]
     * because of the way ROOM automagically creates a [PlantAndGardenPlantings] from all of the
     * [GardenPlanting] instances in the "garden_plantings" table of the database. The @[Relation]
     * annotation is a convenience annotation which can be used in a POJO to automatically fetch
     * relation entities. When the POJO is returned from a query, all of its relations are also
     * fetched by Room. The `entityColumn` argument ("plant_id") is the column to match in the
     * [GardenPlanting], and the `parentColumn` argument ("id") is the column to match in the
     * [Plant] in order to fetch the [Plant] field [plant] from the "plants" table of the database.
     */
    @Relation(parentColumn = "id", entityColumn = "plant_id")
    val gardenPlantings: List<GardenPlanting> = emptyList()
)
