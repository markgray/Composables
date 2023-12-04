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

package com.example.compose.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.bills.BillsBody
import com.example.compose.rally.ui.components.RallyTopAppBar
import com.example.compose.rally.ui.overview.OverviewBody
import com.example.compose.rally.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call the [setContent] method to have it compose [RallyApp] into the activity. [RallyApp]
     * will become the root view of the activity.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }
}

/**
 * This is the main screen of our app. We wrap our content in our [RallyTheme] custom [MaterialTheme].
 * We start by initializing our [List] of [RallyScreen] variable `val allScreens` to a [List] of all
 * the [RallyScreen.values] of the [RallyScreen] enum class. Then we initialize and [rememberSaveable]
 * our [RallyScreen] variable `var currentScreen` by a [MutableState] whose initial value is
 * [RallyScreen.Overview] (this is the currently selected [RallyScreen] of the [RallyTopAppBar] in
 * our [Scaffold] and controls the screen displayed in the [Box] `content` of the [Scaffold]).
 *
 * Our root Composable is a [Scaffold] whose `topBar` argument (top app bar of the screen) is a
 * [RallyTopAppBar] whose `allScreens` argument is our `allScreens` variable, whose `onTabSelected`
 * argument is a lambda which sets `currentScreen` to its [RallyScreen] parameter, and whose
 * [RallyScreen] `currentScreen` argument is our `currentScreen` variable (it will be recomposed
 * whenever `currentScreen` changes value). The `content` of the [Scaffold] is a [Box] which uses
 * the [PaddingValues] parameter passed to it by [Scaffold] to set its `modifier` argument to a
 * [Modifier.padding] of that padding. The `content` of the [Box] is the Composable returned by
 * the [RallyScreen.Content] method of `currentScreen` with its `onScreenChange` argument a lambda
 * that sets `currentScreen` to the [RallyScreen] passed to the lambda (and the screen displayed
 * in the [Box] will be recomposed whenever `currentScreen` changes value also). [RallyScreen.Content]
 * calls the [RallyScreen.body] lambda of the [RallyScreen] it is passed which will compose the
 * [OverviewBody] Composable when called on a [RallyScreen.Overview], compose the [AccountsBody]
 * Composable when called on a [RallyScreen.Accounts], and compose the [BillsBody] Composable when
 * called on a [RallyScreen.Bills].
 */
@Composable
fun RallyApp() {
    RallyTheme {
        val allScreens: List<RallyScreen> = RallyScreen.entries
        var currentScreen: RallyScreen by rememberSaveable { mutableStateOf(RallyScreen.Overview) }
        Scaffold(
            topBar = {
                RallyTopAppBar(
                    allScreens = allScreens,
                    onTabSelected = { screen: RallyScreen -> currentScreen = screen },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding: PaddingValues ->
            Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
                currentScreen.Content(onScreenChange = { screen: RallyScreen -> currentScreen = screen })
            }
        }
    }
}
