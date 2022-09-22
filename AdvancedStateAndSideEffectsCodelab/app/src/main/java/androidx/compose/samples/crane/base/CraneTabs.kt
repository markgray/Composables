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

package androidx.compose.samples.crane.base

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.home.CraneScreen
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat

/**
 * This is used by the `HomeTabBar` Composable (see the file home/CraneHome.kt) which is used as the
 * `appBar` argument of the [BackdropScaffold] of `CraneHomeContent` (also in home/CraneHome.kt) which
 * is used as the `content` of the [Scaffold] of `CraneHome` (also in home/CraneHome.kt). It consists
 * of a [Row] holding a [Row] which holds a `clickable` [Image] displaying the [R.drawable.ic_menu]
 * "Menu" icon which calls our [onMenuClicked] parameter when clicked, an 8.dp wide [Spacer] then
 * another [Image] displaying the [R.drawable.ic_crane_logo] icon (our "Crane" logo). Following the
 * inner [Row] in the outer [Row] are all of the Composables in our [children] parameter (in our case
 * a [CraneTabs] which is a a [TabRow] holding a [Tab] that displays all of the [CraneScreen.name]
 * entries of the [CraneScreen] enum ("Fly", "Sleep", and "Eat") in a [Text]. The `onClick` argument
 * of each [Tab] calls the `onTabSelected` lambda with the index of the [CraneScreen] it represents
 * which will set the `tabSelected` state of `CraneHomeContent` causing it to recompose to display
 * the content appropriate for the new state.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us the same empty, default, or starter [Modifier] that contains no
 * elements that it was passed, and we pass this to our root [Row]. All of the other Composables
 * that we call are passed their own personal [Modifier] instances.
 * @param onMenuClicked this method will be called when the "Menu" [Image] is clicked. `HomeTabBar`
 * passes us its `openDrawer` lambda parameter which is the `openDrawer` lambda which `CraneHomeContent`
 * passes it, which is a lambda passed it by `CraneHome` which launches a coroutine which calls the
 * [DrawerState.open] method of the [DrawerState] of the [ScaffoldState] of its [Scaffold].
 * @param children the Composable we are to display, in our case it is a [CraneTabs] which will
 * display [Tab] Composables for each of the entries in the [CraneScreen] enum in a [TabRow].
 */
@Composable
fun CraneTabBar(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit,
    children: @Composable (Modifier) -> Unit
) {
    Row(modifier = modifier) {
        // Separate Row as the children shouldn't have the padding
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Image(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable(onClick = onMenuClicked),
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = stringResource(id = R.string.cd_menu)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_crane_logo),
                contentDescription = null
            )
        }
        children(
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
    }
}

/**
 * This Composable is used as the `children` content of the [CraneTabBar] that is used by `HomeTabBar`
 * (see the file home/CraneHome.kt). It consists of a [TabRow] which hold a [Tab] for all of the
 * entries in its [List] of [String] parameter [titles]. It is called by `HomeTabBar` with the [List]
 * of [CraneScreen.name] strings of the enum [CraneScreen], and the `onClick` argument passed to each
 * [Tab] uses the fact that the `index` of each [String] in [titles] is the same as the `index` in
 * the [CraneScreen.values] of the enum when it calls our [onTabSelected] parameter (cute, but I do
 * not like the smell). The arguments of the [TabRow] are:
 *  - `selectedTabIndex` we pass the [CraneScreen.ordinal] of our [CraneScreen] parameter [tabSelected].
 *  - `modifier` we pass our [modifier] parameter, which [CraneTabBar] passes us which is a `RowScope`
 *  `Modifier.weight` of 1f, and a `Modifier.align` of [Alignment.CenterVertically] to have the
 *  [TabRow] align its children in the vertical center of the allotted space.
 *  - `contentColor` the preferred content color provided by this [TabRow] to its children, we pass
 *  the `onSurface` [Color] of our [CraneTheme] custom [MaterialTheme.colors] which is `crane_white`
 *  aka [Color.White].
 *  - `indicator` the indicator that represents which tab is currently selected, we pass an empty lambda.
 *  - `divider` the divider displayed at the bottom of the [TabRow], we pass an empty lambda.
 *
 * For the `tabs` argument we loop over the entries in our [List] of [String] parameter [titles] using
 * the [forEachIndexed] extension function and for each [Int] `index` and [String] `title` in the
 * [List] we:
 */
@Composable
fun CraneTabs(
    modifier: Modifier = Modifier,
    titles: List<String>,
    tabSelected: CraneScreen,
    onTabSelected: (CraneScreen) -> Unit
) {
    TabRow(
        selectedTabIndex = tabSelected.ordinal,
        modifier = modifier,
        contentColor = MaterialTheme.colors.onSurface,
        indicator = { },
        divider = { }
    ) {
        titles.forEachIndexed { index, title ->
            val selected = index == tabSelected.ordinal

            var textModifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            if (selected) {
                textModifier =
                    Modifier
                        .border(BorderStroke(2.dp, Color.White), RoundedCornerShape(16.dp))
                        .then(textModifier)
            }

            Tab(
                selected = selected,
                onClick = { onTabSelected(CraneScreen.values()[index]) }
            ) {
                Text(
                    modifier = textModifier,
                    text = title.uppercase(
                        ConfigurationCompat.getLocales(LocalConfiguration.current)[0]!!
                    )
                )
            }
        }
    }
}
