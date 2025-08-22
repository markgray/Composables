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

package androidx.compose.samples.crane.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.BackdropValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.base.CraneDrawer
import androidx.compose.samples.crane.base.CraneTabBar
import androidx.compose.samples.crane.base.CraneTabs
import androidx.compose.samples.crane.base.ExploreSection
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.details.launchDetailsActivity
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * This `typealias` for a function which takes an [ExploreModel] and returns [Unit] probably exists
 * because [OnExploreItemClicked] is easier to type, but then again it does make its purpose clearer.
 */
typealias OnExploreItemClicked = (ExploreModel) -> Unit

/**
 * This `enum` is used to identify which [Tab] in the [TabRow] of [CraneTabs] has been selected by
 * the user. It is then used to determine which Composable is rendered and where the data it uses
 * comes from (see the `frontLayerContent` argument of the [BackdropScaffold] in [CraneHomeContent]
 * for the different Composables and datasets used depending on which [Tab] has been selected).
 */
enum class CraneScreen {
    /**
     * When the [Tab] using this `enum` value as its index is clicked the user is able to find out
     * flight information for the various cities he can fly to.
     */
    Fly,

    /**
     * When the [Tab] using this `enum` value as its index is clicked the user is able to find out
     * hotel information for the various cities.
     */
    Sleep,

    /**
     * When the [Tab] using this `enum` value as its index is clicked the user is able to find out
     * restaurant information for the various cities.
     */
    Eat
}

/**
 * This Composable is called by the Composable `MainScreen` main screen of [MainActivity] after it
 * briefly shows [LandingScreen] when starting up, and its children produce the UI of [MainActivity].
 * Its root Composable is a [Scaffold] whose `scaffoldState` argument is our remembered [ScaffoldState]
 * variable, whose `modifier` argument is a [Modifier.statusBarsPadding] (adds padding to accommodate
 * the status bars insets), and whose `drawerContent` argument is a [CraneDrawer] Composable. In the
 * `content` of the [Scaffold] we first remember our [CoroutineScope] variable `val scope` then call
 * the [CraneHomeContent] Composable with its `modifier` argument our [Modifier] parameter [modifier]
 * with a [Modifier.padding] of the [PaddingValues] the [Scaffold] passes its `content` in the `padding`
 * variable, with our [OnExploreItemClicked] parameter [onExploreItemClicked] as its `onExploreItemClicked`
 * argument (which traces back to the `onCreate` override of [MainActivity] and is a lambda which will call
 * the method [launchDetailsActivity] with the [ExploreModel] that was clicked), and as the `openDrawer`
 * argument of [CraneHomeContent] we pass a lambda which launches a coroutine using the `scope`
 * [CoroutineScope] which calls the `open` method of the [ScaffoldState.drawerState] of our [ScaffoldState]
 * variable `scaffoldState`.
 *
 * @param onExploreItemClicked the [OnExploreItemClicked] method that should be called with the
 * [ExploreModel] that was clicked. In our case this traces back to the `onCreate` override of
 * [MainActivity] and is a lambda which will call the method [launchDetailsActivity] with the
 * [ExploreModel] that was clicked
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller ([MainScreen]) does not pass any so the empty, default, or starter [Modifier]
 * that contains no elements is used instead.
 */
@Composable
fun CraneHome(
    onExploreItemClicked: OnExploreItemClicked,
    modifier: Modifier = Modifier,
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.statusBarsPadding(),
        drawerContent = {
            CraneDrawer()
        }
    ) { padding: PaddingValues ->
        val scope: CoroutineScope = rememberCoroutineScope()
        CraneHomeContent(
            modifier = modifier.padding(padding),
            onExploreItemClicked = onExploreItemClicked,
            openDrawer = {
                // TODO Codelab: rememberCoroutineScope step - open the navigation drawer DONE
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        )
    }
}

/**
 * This Composable is called to be the `content` of the [Scaffold] of [CraneHome]. We initialize
 * our [List] of [ExploreModel] variable `val suggestedDestinations` as a [State] using the
 * [StateFlow.collectAsState] method on the [StateFlow] of [List] of [ExploreModel] property
 * [MainViewModel.suggestedDestinations] of our [viewModel] parameter (which has been injected by
 * Hilt btw) so that every time a new value is posted into the [StateFlow] the [State] will be
 * updated causing recomposition of every Composable that uses `suggestedDestinations`. We initialize
 * our function reference variable `val onPeopleChanged` to a lambda which calls the method
 * [MainViewModel.updatePeople] of our [viewModel] parameter with the [Int] passed the function
 * reference (returning [Unit]). We remember our [CraneScreen] variable `var tabSelected` as a
 * [MutableState] whose initial value is [CraneScreen.Fly]. We then call on [BackdropScaffold] to be
 * our root Composable content with the arguments:
 *  - `modifier` we pass it our [Modifier] parameter [modifier]. Our [CraneHome] caller passes us its
 *  `modifier` parameter with an added [Modifier.padding] of the [PaddingValues] that its [Scaffold]
 *  passes its `content`. The [MainScreen] Composable does not pass [CraneHome] a `modifier` argument
 *  so the `modifier` parameter of [CraneHome] is the empty, default, or starter [Modifier] that
 *  contains no elements, so our [modifier] parameter is just the [Modifier.padding] of the
 *  [PaddingValues] that [Scaffold] of [CraneHome] passes its `content`.
 *  - `scaffoldState` we pass the [BackdropScaffoldState] that the [rememberBackdropScaffoldState]
 *  creates and remembers with the initial value [BackdropValue.Revealed].
 *  - `frontLayerScrimColor` we pass it [Color.Unspecified] (this represents an unset value without
 *  having to box the [Color]. It will be treated as `Transparent` when drawn).
 *  - `appBar` we pass the Composable [HomeTabBar] whose `openDrawer` argument is our [openDrawer]
 *  lambda parameter, whose `tabSelected` argument is our `tabSelected` variable, and whose
 *  `onTabSelected` argument is a lambda which sets `tabSelected` to the [CraneScreen] of the [Tab]
 *  in the [HomeTabBar] that was clicked (selected) by the user.
 *  - `backLayerContent` we pass the Composable [SearchContent] whose `tabSelected` argument is our
 *  `tabSelected` [CraneScreen] variable, whose [MainViewModel] argument `viewModel` is our [viewModel]
 *  parameter, and whose `onPeopleChanged` lambda argument is our `onPeopleChanged` lambda variable.
 *  - `frontLayerContent` we pass a block whose Composable depends on the value of `tabSelected`:
 *      - [CraneScreen.Fly] we call the Composable [ExploreSection] with its `title` argument "Explore
 *      Flights by Destination", its `exploreList` argument our `suggestedDestinations` variable and
 *      its `onItemClicked` argument our [onExploreItemClicked] parameter.
 *      - [CraneScreen.Sleep] we call the Composable [ExploreSection] with its `title` argument
 *      "Explore Properties by Destination", its `exploreList` argument the [MainViewModel.hotels]
 *      field of our [viewModel] parameter and its `onItemClicked` argument our [onExploreItemClicked]
 *      parameter.
 *      - [CraneScreen.Eat] we call the Composable [ExploreSection] with its `title` argument
 *      "Explore Restaurants by Destination", its `exploreList` argument the [MainViewModel.restaurants]
 *      field of our [viewModel] parameter and its `onItemClicked` argument our [onExploreItemClicked]
 *      parameter.
 *
 * @param onExploreItemClicked the [OnExploreItemClicked] lambda that we should pass to [ExploreSection]
 * @param openDrawer a lambda that [HomeTabBar] can call to open the drawer of the [Scaffold] used
 * by the [CraneHome] Composable.
 * @param modifier a [Modifier] instance that our user can use to modify our appearance and/or behavior.
 * Our [CraneHome] caller passes us a [Modifier.padding] of the [PaddingValues] that its [Scaffold]
 * passes its `content`.
 * @param viewModel our singleton [MainViewModel] (injected by Hilt).
 */
@Composable
fun CraneHomeContent(
    onExploreItemClicked: OnExploreItemClicked,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
) {
    // TODO Codelab: collectAsState step - consume stream of data from the ViewModel DONE
    val suggestedDestinations: List<ExploreModel> by viewModel.suggestedDestinations.collectAsState()

    val onPeopleChanged: (Int) -> Unit = { viewModel.updatePeople(it) }
    var tabSelected: CraneScreen by remember { mutableStateOf(CraneScreen.Fly) }

    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
        frontLayerScrimColor = Color.Unspecified,
        appBar = {
            HomeTabBar(
                openDrawer = openDrawer,
                tabSelected = tabSelected,
                onTabSelected = { tabSelected = it }
            )
        },
        backLayerContent = {
            SearchContent(
                tabSelected = tabSelected,
                viewModel = viewModel,
                onPeopleChanged = onPeopleChanged
            )
        },
        frontLayerContent = {
            when (tabSelected) {
                CraneScreen.Fly -> {
                    ExploreSection(
                        title = "Explore Flights by Destination",
                        exploreList = suggestedDestinations,
                        onItemClicked = onExploreItemClicked
                    )
                }

                CraneScreen.Sleep -> {
                    ExploreSection(
                        title = "Explore Properties by Destination",
                        exploreList = viewModel.hotels,
                        onItemClicked = onExploreItemClicked
                    )
                }

                CraneScreen.Eat -> {
                    ExploreSection(
                        title = "Explore Restaurants by Destination",
                        exploreList = viewModel.restaurants,
                        onItemClicked = onExploreItemClicked
                    )
                }
            }
        }
    )
}

/**
 * This is used as the App bar for the back layer of the [BackdropScaffold] of [CraneHomeContent]
 * (its `appBar` argument). Our root Composable is a [CraneTabBar] whose `modifier` argument is our
 * [modifier] parameter, and whose `onMenuClicked` lambda argument is our [openDrawer] parameter.
 * Its `children` argument is a [CraneTabs] Composable whose `modifier` argument is the [Modifier]
 * passed it by [CraneTabBar] (which is a `RowScope` `Modifier.weight` of 1f with a `Modifier.align`
 * of [Alignment.CenterVertically] (this causes the [CraneTabs] to occupy the entire width of the
 * incoming measurement constraints, and to center itself and its children vertically). The `titles`
 * argument of the [CraneTabs] is a the [List] of [String] that is created from the [CraneScreen.name]
 * of all of the [CraneScreen.values] in the [CraneScreen] enum. Its `tabSelected` argument is our
 * [tabSelected] parameter, and its `onTabSelected` lambda argument is a lambda which calls our
 * [onTabSelected] parameter with the [CraneScreen] that it is called with (which is the [CraneScreen]
 * of the [Tab] that has been selected of course).
 *
 * @param openDrawer a that [CraneTabBar] can call to open the drawer of the [Scaffold] used
 * by the [CraneHome] Composable. We pass it as the `onMenuClicked` argument of [CraneTabBar].
 * @param tabSelected the [CraneScreen] of the [Tab] that has been selected by the user.
 * @param onTabSelected a lambda that we can call with the [CraneScreen] of the [Tab] when the user
 * selects a different [Tab].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [CraneHomeContent] caller does not specify on so the empty, default, or starter
 * [Modifier] that contains no elements is used.
 */
@Composable
private fun HomeTabBar(
    openDrawer: () -> Unit,
    tabSelected: CraneScreen,
    onTabSelected: (CraneScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    CraneTabBar(
        modifier = modifier,
        onMenuClicked = openDrawer
    ) { tabBarModifier ->
        CraneTabs(
            modifier = tabBarModifier,
            titles = CraneScreen.entries.map { it.name },
            tabSelected = tabSelected,
            onTabSelected = { tab: CraneScreen -> onTabSelected(CraneScreen.entries[tab.ordinal]) }
        )
    }
}

/**
 * This Composable is used as the `backLayerContent` argument (the content of the back layer) of the
 * [BackdropScaffold] in [CraneHomeContent]. It displays one of three Composables depending on the
 * value of its [CraneScreen] parameter [tabSelected]:
 *  - [CraneScreen.Fly] it displays a [FlySearchContent] whose `onPeopleChanged` lambda argument is
 *  our [onPeopleChanged] parameter, and whose `onToDestinationChanged` is a lambda which calls the
 *  [MainViewModel.toDestinationChanged] method of our [viewModel] parameter with the [City.nameToDisplay]
 *  of the new destination.
 *  - [CraneScreen.Sleep] it displays a [SleepSearchContent] whose `onPeopleChanged` lambda argument
 *  is our [onPeopleChanged] parameter.
 *  - [CraneScreen.Eat] it displays a [EatSearchContent] whose `onPeopleChanged` lambda argument is
 *  our [onPeopleChanged] parameter.
 *
 * @param tabSelected the [CraneScreen] of the [Tab] selected in the [HomeTabBar] used as the `appBar`
 * argument of the [BackdropScaffold] of [CraneHomeContent]. One of [CraneScreen.Fly], [CraneScreen.Eat]
 * or [CraneScreen.Sleep].
 * @param viewModel the [MainViewModel] used by [MainActivity]. It is injected by Hilt as the default
 * value of the `viewModel` parameter of [CraneHomeContent].
 * @param onPeopleChanged a lambda which takes an [Int] and returns [Unit], [CraneHomeContent] assigns
 * a lambda which calls the [MainViewModel.updatePeople] method of [viewModel] with the new number of
 * people traveling.
 */
@Composable
private fun SearchContent(
    tabSelected: CraneScreen,
    viewModel: MainViewModel,
    onPeopleChanged: (Int) -> Unit
) {
    when (tabSelected) {
        CraneScreen.Fly -> FlySearchContent(
            onPeopleChanged = onPeopleChanged,
            onToDestinationChanged = { viewModel.toDestinationChanged(it) }
        )

        CraneScreen.Sleep -> SleepSearchContent(
            onPeopleChanged = onPeopleChanged
        )

        CraneScreen.Eat -> EatSearchContent(
            onPeopleChanged = onPeopleChanged
        )
    }
}
