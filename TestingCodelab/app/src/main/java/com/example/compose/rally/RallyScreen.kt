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

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.compose.rally.data.Account
import com.example.compose.rally.data.Bill
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.bills.BillsBody
import com.example.compose.rally.ui.components.RallyTopAppBar
import com.example.compose.rally.ui.overview.AccountsCard
import com.example.compose.rally.ui.overview.BillsCard
import com.example.compose.rally.ui.overview.OverviewBody
import com.example.compose.rally.ui.overview.OverviewScreenCard
import com.example.compose.rally.ui.overview.SeeAllButton

/**
 * Screen state for Rally. Navigation is kept simple until a proper mechanism is available. Back
 * navigation is not supported.
 *
 * @param icon the [ImageVector] to be used as the `icon` argument of the `RallyTab` that is composed
 * for this [RallyScreen] by the [RallyTopAppBar] that is used as the `topBar` argument (top app bar
 * of the screen) of the [Scaffold] used in the [RallyApp] Composable.
 * @param body a lambda Composable that will be called by the [Content] method of [RallyScreen] to
 * have it compose a screen to be displayed as the `content` of the [Scaffold] used in the [RallyApp]
 * Composable. When called from an [RallyScreen.Overview] it will compose an [OverviewBody], when
 * called from an [RallyScreen.Accounts] it will compose an [AccountsBody], and when called from an
 * [RallyScreen.Bills] it will compose an [BillsBody].
 */
enum class RallyScreen(
    val icon: ImageVector,
    private val body: @Composable ((RallyScreen) -> Unit) -> Unit
) {
    /**
     * This [RallyScreen] will compose the [OverviewBody] Composable when its [body] lambda is called
     * by the [Content] method. Its [icon] is the [ImageVector] drawn by [Icons.Filled.PieChart]
     * which is a stylized "Pie Chart" (three slices of a circle). Its [body] lambda passes the
     * lambda that is passed to the lambda to the Composable [OverviewBody] as its `onScreenChange`
     * argument. It is the "Starting" [RallyScreen] of the app.
     */
    Overview(
        icon = Icons.Filled.PieChart,
        body = { onScreenChange: (RallyScreen) -> Unit -> OverviewBody(onScreenChange = onScreenChange) }
    ),

    /**
     * This [RallyScreen] will compose the [AccountsBody] Composable when its [body] lambda is called
     * by the [Content] method. Its [icon] is the [ImageVector] drawn from [Icons.Filled.AttachMoney]
     * which is a stylized "dollar sign". Its [body] lambda composes the Composable [AccountsBody]
     * with its `accounts` argument the [UserData.accounts] list of [Account] instances.
     */
    Accounts(
        icon = Icons.Filled.AttachMoney,
        body = { AccountsBody(accounts = UserData.accounts) }
    ),

    /**
     * This [RallyScreen] will compose the [BillsBody] Composable when its [body] lambda is called
     * by the [Content] method. Its [icon] is the [ImageVector] drawn from [Icons.Filled.MoneyOff]
     * which is a stylized "dollar sign" with a slash across it. Its [body] lambda composes the
     * Composable [AccountsBody] with its `accounts` argument the [UserData.bills] list of [Bill]
     * instances.
     */
    Bills(
        icon = Icons.Filled.MoneyOff,
        body = { BillsBody(bills = UserData.bills) }
    );

    /**
     * This Composable is called using a [RallyScreen] receiver and calls the [RallyScreen.body]
     * method of that receiver to have it compose a screen to be displayed in the [Box] which is
     * the `content` of the [Scaffold] used in the [RallyApp] Composable.
     *
     * @param onScreenChange a lambda which takes a [RallyScreen] as its parameter, it is only used
     * by the [RallyScreen.body] method of a [RallyScreen.Overview] to supply the `onScreenChange`
     * argument of the [OverviewBody] that is composed. It is then used as the `onScreenChange`
     * argument of both the [AccountsCard] and [BillsCard] that [OverviewBody] contains, and they
     * use it as the `onClickSeeAll` argument of the [OverviewScreenCard] they use, which uses it as
     * the `onClick` argument of the [SeeAllButton] it contains. [RallyApp] passes [Content] a lambda
     * which sets its [MutableState] wrapped `currentScreen` variable to the [RallyScreen] the lambda
     * is passed thereby causing a recomposition of all the Composables which use `currentScreen` as
     * an argument: [RallyTopAppBar] and the current `content` of its [Scaffold].
     */
    @Composable
    fun Content(onScreenChange: (RallyScreen) -> Unit) {
        body(onScreenChange)
    }
}
