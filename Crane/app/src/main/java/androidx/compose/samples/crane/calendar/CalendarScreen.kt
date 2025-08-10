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

@file:Suppress("UnusedImport")

package androidx.compose.samples.crane.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.calendar.model.CalendarState
import androidx.compose.samples.crane.calendar.model.CalendarUiState
import androidx.compose.samples.crane.calendar.model.Month
import androidx.compose.samples.crane.home.MainActivity
import androidx.compose.samples.crane.home.MainViewModel
import androidx.compose.samples.crane.home.Routes
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import java.time.LocalDate

/**
 * This is the composable that is used for the [Routes.Calendar.route] route of the [NavHost] that
 * is created in the `onCreate` override of [MainActivity]. It holds a [CalendarContent] which holds
 * a [Calendar] which renders all of the weeks of all of the months in our 24 month calendar in a
 * [LazyColumn]. We start by initializing and remembering our [CalendarState] variable
 * `val calendarState` to the value returned by the [MainViewModel.calendarState] property of our
 * [MainViewModel] parameter [mainViewModel] (it contains the [List] of 24 [Month]'s that represent
 * our calendar as well as information about the date range that the user has selected (if any) as
 * well as methods to query and modify the [CalendarState]). We then compose a [CalendarContent]
 * whose `calendarState` argument is our [CalendarState] variable `calendarState`, whose `onDayClicked`
 * lambda argument is a lambda which accepts the [LocalDate] passed the lambda in its `dateClicked`
 * variable then calls the [MainViewModel.onDaySelected] method of [MainViewModel] parameter
 * [mainViewModel] with `dateClicked`, and its `onBackPressed` argument is our lambda parameter
 * [onBackPressed].
 *
 * @param onBackPressed a lambda that should be called when the user clicks the back button. Our
 * caller (the [NavHost] in the `onCreate` override of [MainActivity]) passes us a lambda that calls
 * the [NavHostController.popBackStack] method of the `navController` of the [NavHost]).
 * @param mainViewModel the [MainViewModel] that is injected by the [hiltViewModel] method for the
 * [MainActivity] that is found in the [NavBackStackEntry] for the route [Routes.Home.route].
 */
@Composable
fun CalendarScreen(
    onBackPressed: () -> Unit,
    mainViewModel: MainViewModel
) {
    val calendarState: CalendarState = remember {
        mainViewModel.calendarState
    }

    CalendarContent(
        calendarState = calendarState,
        onDayClicked = { dateClicked: LocalDate ->
            mainViewModel.onDaySelected(daySelected = dateClicked)
        },
        onBackPressed = onBackPressed
    )
}

/**
 * Holds a [Scaffold] with a [CalendarTopAppBar] as its `topBar` and a [Calendar] as its `content`.
 * The arguments of the [Scaffold] are;
 *  - `modifier` is a [Modifier.windowInsetsPadding] that adds padding so that its content does not
 *  enter the [WindowInsetsSides.Start] and [WindowInsetsSides.End] of the [WindowInsets] of the
 *  navigation bars.
 *  - `backgroundColor` is the [Colors.primary] of our [CraneTheme] custom [MaterialTheme.colors].
 *  - `topBar` is a lambda that composes a [CalendarTopAppBar] whose `calendarState` argument is our
 *  [CalendarState] parameter [calendarState], and whose `onBackPressed` argument is our lambda
 *  parameter [onBackPressed].
 *
 * The `content` lambda parameter is passed [PaddingValues] in the `contentPadding` variable and
 * composes a [Calendar] whose `calendarState` argument is our [CalendarState] parameter
 * [calendarState], whose onDayClicked argument is our lambda parameter [onDayClicked], and whose
 * `contentPadding` argument is the [PaddingValues] that [Scaffold] passes its `content` in the
 * `contentPadding` variable.
 *
 * @param calendarState the [CalendarState] describing the calendar we should render. Our caller
 * [CalendarScreen] calls us with the remembered [CalendarState] that it reads from the
 * [MainViewModel.calendarState] property.
 * @param onDayClicked a lambda that should be called when the user clicks one of the days in the
 * calendar. Our caller [CalendarScreen] calls us with a lambda which accepts the [LocalDate] passed
 * the lambda in its `dateClicked` variable then calls the [MainViewModel.onDaySelected] method of
 * [MainViewModel] with `dateClicked`.
 * @param onBackPressed a lambda that should be called when the user clicks the back button. Our
 * caller [CalendarScreen] calls us with its `onBackPressed` parameter, and its caller (the [NavHost]
 * in the `onCreate` override of [MainActivity]) calls it with a lambda that calls the
 * [NavHostController.popBackStack] method of the `navController` of the [NavHost]).
 */
@Composable
private fun CalendarContent(
    calendarState: CalendarState,
    onDayClicked: (LocalDate) -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.navigationBars.only(sides = WindowInsetsSides.Start + WindowInsetsSides.End)
        ),
        backgroundColor = MaterialTheme.colors.primary,
        topBar = {
            CalendarTopAppBar(calendarState = calendarState, onBackPressed = onBackPressed)
        }
    ) { contentPadding: PaddingValues ->
        Calendar(
            calendarState = calendarState,
            onDayClicked = onDayClicked,
            contentPadding = contentPadding
        )
    }
}

/**
 * This is the `topBar` used by the [Scaffold] in [CalendarContent]. We start by initializing our
 * [CalendarUiState] variable `val calendarUiState` to the [MutableState.value] of the
 * [CalendarState.calendarUiState] property of our [CalendarState] parameter [calendarState]. Then
 * our root Composable is a [Column] which holds:
 *  - a [Spacer] whose `modifier` argument is a [Modifier.windowInsetsTopHeight] to set its top
 *  [WindowInsets] to the `insets` of [WindowInsets.Companion.statusBars], and to this is chained
 *  a [Modifier.fillMaxWidth] to have the [Spacer] occupy its entire incoming width constrain, and
 *  to this is chained a [Modifier.background] to set its [Color] to the [Colors.primaryVariant]
 *  of the [CraneTheme] custom [MaterialTheme.colors].
 *
 * Below this is a [TopAppBar] whose arguments are:
 *  - `title` is a lambda that composes a [Text] whose `text` is the [String] with resource ID
 *  `R.string.calendar_select_dates_title` ("Select Dates") if the [CalendarUiState.hasSelectedDates]
 *  property of [CalendarUiState] variable `calendarUiState` returns `false` or if it returns `true`
 *  the [String] returned by the [CalendarUiState.selectedDatesFormatted] property of
 *  `calendarUiState`.
 *  - `navigationIcon` is an [IconButton] whose `onClick` lambda argument is a lambda which calls
 *  our lambda parameter [onBackPressed], and its `content` Composable lambda argument is an [Icon]
 *  whose `imageVector` argument is the [ImageVector] drawn by [Icons.AutoMirrored.Filled.ArrowBack],
 *  whose `contentDescription` is the [String] with resource ID `R.string.cd_back` ("Back"), and
 *  whose `tint` [Color] is the [Colors.onSurface] of the [CraneTheme] custom [MaterialTheme.colors].
 *  - `backgroundColor` is the [Colors.primaryVariant] of the [CraneTheme] custom
 *  [MaterialTheme.colors].
 *  - `elevation` is 0.dp
 *
 * @param calendarState the current [CalendarState] of our [Calendar]. It traces back to
 * [CalendarScreen] where it is the remembered [CalendarState] that it reads from the
 * [MainViewModel.calendarState] property.
 * @param onBackPressed a lambda to call when the user presses the back button. It traces back to
 * the [NavHost] in the `onCreate` override of [MainActivity] where it is a lambda that calls the
 * [NavHostController.popBackStack] method of the `navController` of the [NavHost].
 */
@Composable
private fun CalendarTopAppBar(calendarState: CalendarState, onBackPressed: () -> Unit) {
    val calendarUiState: CalendarUiState = calendarState.calendarUiState.value
    Column {
        Spacer(
            modifier = Modifier
                .windowInsetsTopHeight(insets = WindowInsets.statusBars)
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.primaryVariant)
        )
        TopAppBar(
            title = {
                Text(
                    text = if (!calendarUiState.hasSelectedDates) {
                        stringResource(id = R.string.calendar_select_dates_title)
                    } else {
                        calendarUiState.selectedDatesFormatted
                    }
                )
            },
            navigationIcon = {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.cd_back),
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            },
            backgroundColor = MaterialTheme.colors.primaryVariant,
            elevation = 0.dp
        )
    }
}
