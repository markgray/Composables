/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents one night's sleep through start, end times, and the sleep quality. The `@Entity`
 * annotation marks this class as an entity. This class will have a mapping SQLite table in the
 * database, the `tableName` parameter to the annotation sets the table name in the SQLite database
 * to "daily_sleep_quality_table".
 */
@Entity(tableName = "daily_sleep_quality_table")
data class SleepNight(
    /**
     * The `PrimaryKey` of our table, we let SQLite `autoGenerate` the unique id starting at "1"
     * (Insert methods treat 0 as not-set while inserting the item)>
     */
    @PrimaryKey(autoGenerate = true)
    var nightId: Long = 0L,

    /**
     * The start time in milliseconds of our night's sleep. The `@ColumnInfo` annotation sets
     * the name of the column in the database to "start_time_milli". Defaults to the field name
     * if not set.
     */
    @ColumnInfo(name = "start_time_milli")
    var startTimeMilli: Long = System.currentTimeMillis(),

    /**
     * The end time in milliseconds of our night's sleep. The `@ColumnInfo` annotation sets
     * the name of the column in the database to "end_time_milli". Defaults to the field name
     * if not set.
     */
    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = startTimeMilli,

    /**
     * The user's subjective rating of his night's sleep. The `@ColumnInfo` annotation sets
     * the name of the column in the database to "quality_rating". Defaults to the field name
     * if not set.
     */
    @ColumnInfo(name = "quality_rating")
    var sleepQuality: Int = -1
)
