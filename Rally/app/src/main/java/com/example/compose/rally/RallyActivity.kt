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

package com.example.compose.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.compose.rally.ui.components.RallyTopAppBar
import com.example.compose.rally.ui.theme.RallyTheme
import kotlin.enums.EnumEntries

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {

    /**
     * Called when the activity is first created. First we call our super's `onCreate` implementation,
     * then we set our content to a [RallyApp] composable.
     *
     * @param savedInstanceState We so not override [onSaveInstanceState] so do not use this.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState = savedInstanceState)
        setContent {
            RallyApp(modifier = Modifier.safeContentPadding())
        }
    }
}

/**
 * The main Composable function for the Rally app.
 *
 * Wrapped in our [RallyTheme] custom [MaterialTheme] we start by initializing our [EnumEntries]
 * of [RallyScreen] variable `val allScreens` to the [RallyScreen.entries] of the enum. We initialize
 * and [rememberSaveable] our [MutableState] wrapped [RallyScreen] variable `var currentScreen` to
 * an initial value of [RallyScreen.Overview]. Then our root composable is a [Scaffold] whose
 * `topBar` argument is a [RallyTopAppBar] whose `allScreens` argument is `allScreens`,
 * `onTabSelected` argument is a lambda that captures the [RallyScreen] passed the lambda in
 * variable `screen` then sets [MutableState] wrapped [RallyScreen] variable `currentScreen` to
 * the value of `screen`, and whose `currentScreen` argument is `currentScreen`.
 *
 * In the `content` composable lambda argument of the [Scaffold] we accept the [PaddingValues]
 * passed the lambda in variable `innerPadding` and compose a [Box] whose `modifier` argument is a
 * [Modifier.padding] that sets its padding to the value of `innerPadding`. In the [BoxScope]
 * `content` composable lambda argument of the [Box] we compose the [RallyScreen.ComposeBody] of
 * `currentScreen` with its `onScreenChange` argument set to a lambda that captures the
 * [RallyScreen] passed the lambda in variable `screen` then sets [MutableState] wrapped
 * [RallyScreen] variable `currentScreen` to the value of `screen`.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior.
 */
@Composable
fun RallyApp(modifier: Modifier = Modifier) {
    RallyTheme {
        val allScreens: EnumEntries<RallyScreen> = RallyScreen.entries
        var currentScreen: RallyScreen by rememberSaveable { mutableStateOf(RallyScreen.Overview) }
        Scaffold(
            modifier = modifier,
            topBar = {
                RallyTopAppBar(
                    allScreens = allScreens,
                    onTabSelected = { screen: RallyScreen -> currentScreen = screen },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding: PaddingValues ->
            Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
                currentScreen.ComposeBody(onScreenChange = { screen: RallyScreen ->
                    currentScreen = screen
                })
            }
        }
    }
}
