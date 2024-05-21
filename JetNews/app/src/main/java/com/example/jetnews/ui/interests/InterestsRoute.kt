/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui.interests

import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.flow.StateFlow

/**
 * Stateful composable that displays the Navigation route for the Interests screen. We start by
 * initializing and remembering  our [List] of [TabContent] variable `val tabContent` to the value
 * returned from calling [rememberTabContent] with its `interestsViewModel` argument our
 * [InterestsViewModel] parameter [interestsViewModel] (it is kept up to date by collecting the
 * [StateFlow] of [InterestsUiState] property [InterestsViewModel.uiState] as a [State] wrapped
 * [InterestsUiState] which causes its [List] of [TabContent] to be updated everytime a new
 * [InterestsUiState] is emitted). Next we use destructuring to initialize our [Sections] variable
 * `val currentSection` and lambda of [Sections] variable `val updateSection` to a [MutableState]
 * wrapped [TabContent] whose initial value is the [List.first] of our [List] of [TabContent] variable
 * `tabContent`. Finally our root Composable is an [InterestsScreen] whose `tabContent` argument is
 * our [List] of [TabContent] variable `tabContent`, whose `currentSection` argument is our [Sections]
 * variable `currentSection`, whose `isExpandedScreen` argument is our [Boolean] parameter [isExpandedScreen]
 * whose `onTabChange` argument is our lambda of [Sections] variable `updateSection`, whose `openDrawer`
 * argument is our lambda parameter [openDrawer], and whose `snackbarHostState` argument is our
 * [SnackbarHostState] parameter [snackbarHostState].
 *
 * @param interestsViewModel the [InterestsViewModel] ViewModel that handles the business logic of
 * this screen.
 * @param isExpandedScreen (state) `true` if the screen is expanded ie. its [WindowWidthSizeClass]
 * is [WindowWidthSizeClass.Expanded] (Represents the majority of tablets in landscape and large
 * unfolded inner displays in landscape).
 * @param openDrawer (event) request opening the [ModalNavigationDrawer] app drawer
 * @param snackbarHostState (state) [SnackbarHostState] state for screen snackbar host
 */
@Composable
fun InterestsRoute(
    interestsViewModel: InterestsViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val tabContent: List<TabContent> = rememberTabContent(interestsViewModel = interestsViewModel)
    val (currentSection: Sections, updateSection: (Sections) -> Unit) = rememberSaveable {
        mutableStateOf(value = tabContent.first().section)
    }

    InterestsScreen(
        tabContent = tabContent,
        currentSection = currentSection,
        isExpandedScreen = isExpandedScreen,
        onTabChange = updateSection,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState
    )
}
