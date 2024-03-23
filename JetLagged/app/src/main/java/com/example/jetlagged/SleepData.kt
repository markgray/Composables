/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.jetlagged

import androidx.compose.ui.graphics.Color
import com.example.jetlagged.ui.theme.Yellow_Awake
import com.example.jetlagged.ui.theme.Yellow_Deep
import com.example.jetlagged.ui.theme.Yellow_Light
import com.example.jetlagged.ui.theme.Yellow_Rem
import java.time.Duration
import java.time.LocalDateTime

/**
 * The data class used to hold our [SleepDayData].
 */
data class SleepGraphData(
    /**
     * Our dataset of [SleepDayData]
     */
    val sleepDayData: List<SleepDayData>,
) {
    /**
     * The hour of the earliest [SleepPeriod] in all of our [List] of [SleepDayData] field
     * [sleepDayData].
     */
    val earliestStartHour: Int by lazy {
        sleepDayData.minOf { it.firstSleepStart.hour }
    }

    /**
     * The hour of the latest [SleepPeriod] in all of our [List] of [SleepDayData] field
     * [sleepDayData].
     */
    val latestEndHour: Int by lazy {
        sleepDayData.maxOf { it.lastSleepEnd.hour }
    }
}

/**
 * Holds all of the data for one day of sleep.
 */
data class SleepDayData(
    /**
     * The start date of the day of sleep.
     */
    val startDate: LocalDateTime,
    /**
     * The [List] of [SleepPeriod] for this day of sleep.
     */
    val sleepPeriods: List<SleepPeriod>,
    /**
     * The subjective evaluation of the day of sleep. Used to select the emoji for the [sleepScoreEmoji]
     * property of this [SleepDayData].
     */
    val sleepScore: Int,
) {
    /**
     * Returns the earliest [SleepPeriod.startTime] in our [List] of [SleepPeriod] field [sleepPeriods]
     */
    val firstSleepStart: LocalDateTime by lazy {
        sleepPeriods.sortedBy(SleepPeriod::startTime).first().startTime
    }

    /**
     * Returns the last [SleepPeriod.endTime] in our [List] of [SleepPeriod] field [sleepPeriods]
     */
    val lastSleepEnd: LocalDateTime by lazy {
        sleepPeriods.sortedBy(SleepPeriod::startTime).last().endTime
    }

    /**
     * The total time spent in bed, it is the [Duration.between] our [firstSleepStart] and [lastSleepEnd]
     */
    val totalTimeInBed: Duration by lazy {
        Duration.between(firstSleepStart, lastSleepEnd)
    }

    /**
     * The emoji used to express the subjective evaluation of the day of sleep in [Int] field [sleepScore]
     */
    val sleepScoreEmoji: String by lazy {
        when (sleepScore) {
            in 0..40 -> "😖"
            in 41..60 -> "😏"
            in 60..70 -> "😴"
            in 71..100 -> "😃"
            else -> "🤷‍"
        }
    }

    /**
     * Returns the fraction of the [totalTimeInBed] that the [SleepPeriod] parameter [sleepPeriod]
     * represents.
     *
     * @param sleepPeriod the [SleepPeriod] whose fraction of [totalTimeInBed] we are to calculate.
     * @return the fraction of the [totalTimeInBed] that the [SleepPeriod] parameter [sleepPeriod]
     * represents.
     */
    fun fractionOfTotalTime(sleepPeriod: SleepPeriod): Float {
        return sleepPeriod.duration.toMinutes() / totalTimeInBed.toMinutes().toFloat()
    }

    /**
     * Returns the number of minutes after the beginning of the night's sleep that the [SleepPeriod]
     * parameter [sleepPeriod] started.
     *
     * @param sleepPeriod the [SleepPeriod] whose number of minutes after the beginning of the
     * night's sleep we want.
     * @return the number of minutes after the beginning of the night's sleep that the [SleepPeriod]
     * parameter [sleepPeriod] started.
     */
    fun minutesAfterSleepStart(sleepPeriod: SleepPeriod): Long {
        return Duration.between(
            firstSleepStart,
            sleepPeriod.startTime
        ).toMinutes()
    }
}

/**
 * Holds all the data for a single period of sleep
 */
data class SleepPeriod(
    /**
     * The start time of the [SleepPeriod]
     */
    val startTime: LocalDateTime,
    /**
     * The end time of the [SleepPeriod]
     */
    val endTime: LocalDateTime,
    /**
     * The type of sleep of the [SleepPeriod], one of [SleepType.Awake], [SleepType.REM],
     * [SleepType.Light], or [SleepType.Deep]
     */
    val type: SleepType,
) {

    /**
     * Returns the [Duration] between [LocalDateTime] field [startTime] and [LocalDateTime] field
     * [endTime].
     */
    val duration: Duration by lazy {
        Duration.between(startTime, endTime)
    }
}

/**
 * The type of sleep that occurred during the different [SleepPeriod]'s
 */
enum class SleepType(
    /**
     * Resource ID for a [String] describing the [SleepType]
     */
    val title: Int,
    /**
     * [Color] to use to draw the sections that this [SleepType] occupies in the sleep bar.
     */
    val color: Color
) {
    /**
     * The subject was wide awake.
     */
    Awake(R.string.sleep_type_awake, Yellow_Awake),

    /**
     * The subject was experiencing a REM (rapid eye movement) [SleepPeriod].
     */
    REM(R.string.sleep_type_rem, Yellow_Rem),

    /**
     * The subject was experiencing "Light" sleep.
     */
    Light(R.string.sleep_type_light, Yellow_Light),

    /**
     * The subject was experiencing "Deep" sleep.
     */
    Deep(R.string.sleep_type_deep, Yellow_Deep)
}
