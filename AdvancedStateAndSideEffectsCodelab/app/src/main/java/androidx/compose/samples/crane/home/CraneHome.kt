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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
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
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.details.launchDetailsActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
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
 * This Composable is called to be the `content` of the [Scaffold] of [CraneHome].
 */
@OptIn(ExperimentalMaterialApi::class)
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
    var tabSelected by remember { mutableStateOf(CraneScreen.Fly) }

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
            titles = CraneScreen.values().map { it.name },
            tabSelected = tabSelected,
            onTabSelected = { newTab -> onTabSelected(CraneScreen.values()[newTab.ordinal]) }
        )
    }
}

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
