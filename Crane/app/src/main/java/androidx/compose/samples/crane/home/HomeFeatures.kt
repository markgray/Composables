/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.samples.crane.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.SimpleUserInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * This Composable is loaded when the user selects the [CraneScreen.Fly] tab in the [SearchContent]
 * Composable. We start by initializing our [Int] variable `val columns` based on the value of our
 * [WindowWidthSizeClass] parameter [widthSize] setting it to 1 for [WindowWidthSizeClass.Compact],
 * 2 for [WindowWidthSizeClass.Medium], and 4 for [WindowWidthSizeClass.Expanded]. Then our root
 * Composable is a [CraneSearch] whose `columns` argument is our `columns` variable, and whose
 * [LazyGridScope] `content` argument is a lambda holding 4 [LazyGridScope.item] Composables which
 * hold:
 *  - a [PeopleUserInput] whose `titleSuffix` argument is the [String] ", Economy" and whose
 *  `onPeopleChanged` argument is the [FlySearchContentUpdates.onPeopleChanged] lambda from our
 *  [FlySearchContentUpdates] parameter [searchUpdates].
 *  - a [FromDestination] composable.
 *  - a [ToDestinationUserInput] Composable whose `onToDestinationChanged` argument is the
 *  [FlySearchContentUpdates.onToDestinationChanged] lambda from our [FlySearchContentUpdates]
 *  parameter [searchUpdates].
 *  - a [DatesUserInput] Composable whose `datesSelected` argument is our [String] parameter
 *  [datesSelected], and whose `onDateSelectionClicked` argument is the
 *  [FlySearchContentUpdates.onDateSelectionClicked] lambda from our [FlySearchContentUpdates]
 *  parameter [searchUpdates].
 *
 * @param widthSize the [WindowWidthSizeClass] of the device we are running on, one of
 * [WindowWidthSizeClass.Compact], [WindowWidthSizeClass.Medium], or [WindowWidthSizeClass.Expanded]
 * @param datesSelected a [String] representation of the currently selected date range.
 * @param searchUpdates a [FlySearchContentUpdates] data class instance containing lambda callbacks
 * for several different events that might occur.
 */
@Composable
fun FlySearchContent(
    widthSize: WindowWidthSizeClass,
    datesSelected: String,
    searchUpdates: FlySearchContentUpdates
) {

    val columns: Int = when (widthSize) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        WindowWidthSizeClass.Expanded -> 4
        else -> 1
    }

    CraneSearch(
        columns = columns,
        content = {
            item {
                PeopleUserInput(
                    titleSuffix = ", Economy",
                    onPeopleChanged = searchUpdates.onPeopleChanged
                )
            }
            item {
                FromDestination()
            }
            item {
                ToDestinationUserInput(
                    onToDestinationChanged = searchUpdates.onToDestinationChanged
                )
            }
            item {
                DatesUserInput(
                    datesSelected = datesSelected,
                    onDateSelectionClicked = searchUpdates.onDateSelectionClicked
                )
            }
        }
    )
}

/**
 * This Composable is loaded when the user selects the [CraneScreen.Sleep] tab in the [SearchContent]
 * Composable. We start by initializing our [Int] variable `val columns` based on the value of our
 * [WindowWidthSizeClass] parameter [widthSize] setting it to 1 for [WindowWidthSizeClass.Compact],
 * 2 for [WindowWidthSizeClass.Medium], and 4 for [WindowWidthSizeClass.Expanded]. Then our root
 * Composable is a [CraneSearch] whose `columns` argument is our `columns` variable, and whose
 * [LazyGridScope] `content` argument is a lambda holding 3 [LazyGridScope.item] Composables which
 * hold:
 *  - a [PeopleUserInput] whose `onPeopleChanged` argument is the
 *  [SleepSearchContentUpdates.onPeopleChanged] lambda from our [SleepSearchContentUpdates] parameter
 *  [sleepUpdates].
 *  - a [DatesUserInput] Composable whose `datesSelected` argument is our [String] parameter
 *  [datesSelected], and whose `onDateSelectionClicked` argument is the
 *  [SleepSearchContentUpdates.onDateSelectionClicked] lambda from our [SleepSearchContentUpdates]
 *  parameter [sleepUpdates].
 *  - a [SimpleUserInput] whose `caption` argument is the [String] with resource ID
 *  [R.string.input_select_location] ("Select Location"), and whose `vectorImageId` argument is
 *  the [Int] DrawableRes [R.drawable.ic_hotel] (a stylized "head in a bed")
 *
 * @param widthSize the [WindowWidthSizeClass] of the device we are running on, one of
 * [WindowWidthSizeClass.Compact], [WindowWidthSizeClass.Medium], or [WindowWidthSizeClass.Expanded]
 * @param datesSelected a [String] representation of the currently selected date range.
 * @param sleepUpdates a [SleepSearchContentUpdates] data class instance containing lambda callbacks
 * for several different events that might occur.
 */
@Composable
fun SleepSearchContent(
    widthSize: WindowWidthSizeClass,
    datesSelected: String,
    sleepUpdates: SleepSearchContentUpdates
) {
    val columns: Int = when (widthSize) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 3
        WindowWidthSizeClass.Expanded -> 3
        else -> 1
    }
    CraneSearch(
        columns = columns,
        content = {
            item {
                PeopleUserInput(onPeopleChanged = { sleepUpdates.onPeopleChanged })
            }
            item {
                DatesUserInput(
                    datesSelected = datesSelected,
                    onDateSelectionClicked = sleepUpdates.onDateSelectionClicked
                )
            }
            item {
                SimpleUserInput(
                    caption = stringResource(id = R.string.input_select_location),
                    vectorImageId = R.drawable.ic_hotel
                )
            }
        }
    )
}

/**
 * This Composable is loaded when the user selects the [CraneScreen.Eat] tab in the [SearchContent]
 * Composable. We start by initializing our [Int] variable `val columns` based on the value of our
 * [WindowWidthSizeClass] parameter [widthSize] setting it to 1 for [WindowWidthSizeClass.Compact],
 * 2 for [WindowWidthSizeClass.Medium], and 4 for [WindowWidthSizeClass.Expanded]. Then our root
 * Composable is a [CraneSearch] whose `columns` argument is our `columns` variable, and whose
 * [LazyGridScope] `content` argument is a lambda holding 4 [LazyGridScope.item] Composables which
 * hold:
 *  - a [PeopleUserInput] whose `onPeopleChanged` argument is the
 *  [EatSearchContentUpdates.onPeopleChanged] lambda from our [EatSearchContentUpdates] parameter
 *  [eatUpdates].
 *  - a [DatesUserInput] Composable whose `datesSelected` argument is our [String] parameter
 *  [datesSelected], and whose `onDateSelectionClicked` argument is the
 *  [EatSearchContentUpdates.onDateSelectionClicked] lambda from our [EatSearchContentUpdates]
 *  parameter [eatUpdates].
 *  - a [SimpleUserInput] whose `caption` argument is the [String] with resource ID
 *  [R.string.input_select_time] ("Select Time"), and whose `vectorImageId` argument is
 *  the [Int] DrawableRes [R.drawable.ic_time] (a stylized clock)
 *  - a [SimpleUserInput] whose `caption` argument is the [String] with resource ID
 *  [R.string.input_select_location] ("Select Location"), and whose `vectorImageId` argument is
 *  the [Int] DrawableRes [R.drawable.ic_restaurant] (a stylized crossed spoon and knife)
 *
 * @param widthSize the [WindowWidthSizeClass] of the device we are running on, one of
 * [WindowWidthSizeClass.Compact], [WindowWidthSizeClass.Medium], or [WindowWidthSizeClass.Expanded]
 * @param datesSelected a [String] representation of the currently selected date range.
 * @param eatUpdates a [EatSearchContentUpdates] data class instance containing lambda callbacks
 * for several different events that might occur.
 */
@Composable
fun EatSearchContent(
    widthSize: WindowWidthSizeClass,
    datesSelected: String,
    eatUpdates: EatSearchContentUpdates
) {
    val columns: Int = when (widthSize) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        WindowWidthSizeClass.Expanded -> 4
        else -> 1
    }
    CraneSearch(columns = columns) {
        item {
            PeopleUserInput(onPeopleChanged = eatUpdates.onPeopleChanged)
        }
        item {
            DatesUserInput(
                datesSelected = datesSelected,
                onDateSelectionClicked = eatUpdates.onDateSelectionClicked
            )
        }
        item {
            SimpleUserInput(
                caption = stringResource(id = R.string.input_select_time),
                vectorImageId = R.drawable.ic_time
            )
        }
        item {
            SimpleUserInput(
                caption = stringResource(id = R.string.input_select_location),
                vectorImageId = R.drawable.ic_restaurant
            )
        }
    }
}

/**
 * This is used as the root composable for the [FlySearchContent], [SleepSearchContent], and
 * [EatSearchContent] Composable. Our root Composable is a [LazyVerticalGrid] whose `modifier`
 * argument is a [Modifier.padding] that adds 24.dp padding to the `start`, 0.dp to the `top`, 24.dp
 * to the `end`, and 12.dp to the `bottom`, whose `columns` argument is a [GridCells.Fixed] whose
 * `count` is our [Int] parameter [columns], whose `horizontalArrangement` argument is a
 * [Arrangement.spacedBy] which spaces the content horizontally by 8.dp, whose `verticalArrangement`
 * is a [Arrangement.spacedBy] which spaces the content vertically by 8.dp, and the [LazyGridScope]
 * `content` argument is our [LazyGridScope] lambda argument [content].
 *
 * @param columns the number of columns in our [LazyVerticalGrid]. Our three caller pass us a value
 * based on the [WindowWidthSizeClass] of the device we are running on:
 *  - [WindowWidthSizeClass.Compact] -> 1
 *  - [WindowWidthSizeClass.Medium] -> 2
 *  - [WindowWidthSizeClass.Expanded] -> 4
 * @param content a lambda containing one or more [LazyGridScope.item] composables depending on the
 * needs of our callers.
 */
@Composable
private fun CraneSearch(
    columns: Int,
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(start = 24.dp, top = 0.dp, end = 24.dp, bottom = 12.dp),
        columns = GridCells.Fixed(count = columns),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        content = content
    )
}
