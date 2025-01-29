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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.home.CraneScreen
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * This is used by the `HomeTabBar` Composable (see the file home/CraneHome.kt) which is used as the
 * `appBar` argument of the [BackdropScaffold] of `CraneHomeContent` (also in home/CraneHome.kt) which
 * is used as the `content` of the [Scaffold] of `CraneHome` (also in home/CraneHome.kt). It consists
 * of a [Row] holding a [Row] which holds a `clickable` [Image] displaying the `R.drawable.ic_menu`
 * "Menu" icon which calls our [onMenuClicked] parameter when clicked, an 8.dp wide [Spacer] then
 * another [Image] displaying the `R.drawable.ic_crane_logo` icon (our "Crane" logo). Following the
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
                .weight(weight = 1f)
                .align(alignment = Alignment.CenterVertically)
        )
    }
}

/**
 * This Composable is used as the `children` content of the [CraneTabBar] that is used by `HomeTabBar`
 * (see the file home/CraneHome.kt). It consists of a [TabRow] which holds a [Tab] for each of the
 * entries in its [List] of [String] parameter [titles]. It is called by `HomeTabBar` with the [List]
 * of [CraneScreen.name] strings of the enum [CraneScreen], and the `onClick` argument passed to each
 * [Tab] uses the fact that the `index` of each [String] in [titles] is the same as the `index` in
 * the [CraneScreen.entries] of the enum when it calls our [onTabSelected] parameter (cute, but I do
 * not like the smell). The arguments of the [TabRow] are:
 *  - `selectedTabIndex` we pass the [CraneScreen.ordinal] of our [CraneScreen] parameter [tabSelected].
 *  - `modifier` we pass our [modifier] parameter, which [CraneTabBar] passes us which is a `RowScope`
 *  `Modifier.weight` of 1f, and a `Modifier.align` of [Alignment.CenterVertically] to have the
 *  [TabRow] align its children in the vertical center of the allotted space.
 *  - `contentColor` the preferred content color provided by this [TabRow] to its children, we pass
 *  the `onSurface` [Color] of our [CraneTheme] custom [MaterialTheme.colors] which is `crane_white`
 *  aka [Color.White].
 *  - `indicator` the indicator that represents which tab is currently selected, we pass a lambda
 *  that composes a [Box] whose `modifier` argument is a `Modifier.tabIndicatorOffset` whose
 *  `currentTabPosition` argument is the [TabPosition] of the [CraneScreen.ordinal] of our
 *  [tabSelected] parameter (takes up all the available width inside the [TabRow], and then animates
 *  the offset of the indicator it is applied to, depending on the `currentTabPosition`), with a
 *  [Modifier.fillMaxSize] chained to that (causes the [Box] to fill the entire incoming size
 *  constraints), with a [Modifier.padding] that adds 4.dp to each side of the [Box], and last a
 *  [Modifier.border] is chained which adds a `border` of a [BorderStroke] of width 2.dp, `color`
 *  of [Color.White] whose `shape` is a [RoundedCornerShape] of `size` 16.dp.
 *  - `divider` the divider displayed at the bottom of the [TabRow], we pass an empty lambda.
 *
 * For the `tabs` Composable lambda argument of the [TabRow] we loop over the entries in our [List]
 * of [String] parameter [titles] using the [forEachIndexed] extension function and for each [Int]
 * `index` and [String] `title` in the [List] we:
 *  - Initialize our [Boolean] variable `val selected` to `true` if the `index` of the current entry
 *  in the [titles] list is equal to the [CraneScreen.ordinal] value of our [tabSelected] parameter.
 *  - Initialize our [Modifier] variable `var textModifier` to a [Modifier.padding] whose `vertical`
 *  padding is 8.dp and whose `horizontal` padding is 16.dp
 *  - We call the [Tab] Composable with its `modifier` argument a [Modifier.padding] that adds 4.dp
 *  to each side, and chained to that a [Modifier.clip] that clips its `shape` to a [RoundedCornerShape]
 *  of 16.dp `size`. The `selected` argument of the [Tab] is set to our `selected` variable, and
 *  the `onClick` argument set to a call to our [onTabSelected] parameter lambda with the [CraneScreen]
 *  in [CraneScreen.entries] that corresponds to the `index` of the current entry in the [titles] list.
 *  - The `content` of the [Tab] is a [Text] whose `modifier` argument is our `textModifier` variable,
 *  and whose `text` argument is the [String.uppercase] conversion of `title` using the current locale.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. `HomeTabBar` (file home/CraneHome.kt) uses us as the `children` Composable content of
 * [CraneTabBar] passing the [Modifier] that [CraneTabBar] uses as the argument of `children` which
 * is the `RowScope` `Modifier.weight` of 1f with a `Modifier.align` of [Alignment.CenterVertically]
 * added to it.
 * @param titles the [List] of [String] that is created from the [CraneScreen.name] of all of the
 * [CraneScreen.values] in the [CraneScreen] enum. This Composable assumes it can it can determine
 * the [CraneScreen] that each [String] refers to by the index of the [String] in the [List].
 * @param tabSelected the [CraneScreen] that the currently selected [Tab] represents.
 * @param onTabSelected a lambda that a [Tab] should call with the [CraneScreen] it represents when
 * it is clicked.
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
        indicator = { tabPositions: List<TabPosition> ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(currentTabPosition = tabPositions[tabSelected.ordinal])
                    .fillMaxSize()
                    .padding(horizontal = 4.dp)
                    .border(
                        border = BorderStroke(width = 2.dp, color = Color.White),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
            )
        },
        divider = { }
    ) {
        titles.forEachIndexed { index: Int, title: String ->
            val selected: Boolean = index == tabSelected.ordinal

            val textModifier: Modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)

            Tab(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(shape = RoundedCornerShape(size = 16.dp)),
                selected = selected,
                onClick = {
                    onTabSelected(CraneScreen.entries[index])
                }
            ) {
                Text(
                    modifier = textModifier,
                    text = title.uppercase()
                )
            }
        }
    }
}
