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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.CraneDrawer
import androidx.compose.samples.crane.base.CraneTabBar
import androidx.compose.samples.crane.base.CraneTabs
import androidx.compose.samples.crane.base.ExploreSection
import androidx.compose.samples.crane.calendar.CalendarScreen
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.details.DetailsActivity
import androidx.compose.samples.crane.details.launchDetailsActivity
import androidx.compose.samples.crane.ui.BottomSheetShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.maps.android.compose.GoogleMap
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
 * Our app's main composable, its parent [MainContent] just adds some animated padding above it
 * (100dp to 0dp) to "dramatize" the transition from [LandingScreen] to  [CraneHome]. We start by
 * initializing and remembering our [ScaffoldState] variable `val scaffoldState` to a new instance.
 * Then our root composable is a [Scaffold] whose `scaffoldState` argument is our [ScaffoldState]
 * variable `scaffoldState`, whose `modifier` argument is a [Modifier.statusBarsPadding] to have it
 * add padding to avoid the status bars, and whose `drawerContent` is a lambda which composes our
 * [CraneDrawer] composable. In the `content` argument of the [Scaffold] it passes [PaddingValues]
 * in the variable `contentPadding` to the lambda which initializes and remembers its [CoroutineScope]
 * variable `val scope`, then composes a [CraneHomeContent] composable. The `modifier` argument of
 * [CraneHomeContent] chains a [Modifier.padding] to our [Modifier] parameter [modifier] which adds
 * the [PaddingValues] passed the lambda in the `contentPadding` variable, the `widthSize` argument
 * is our [WindowWidthSizeClass] parameter [widthSize], the `onExploreItemClicked` argument is our
 * [OnExploreItemClicked] parameter [onExploreItemClicked], the `onDateSelectionClicked` argument is
 * our lambda parameter [onDateSelectionClicked], the `openDrawer` argument is a lambda which uses
 * the [CoroutineScope.launch] method of `scope` to launch a coroutine that calls the
 * [DrawerState.open] method of the [ScaffoldState.drawerState] of [ScaffoldState] variable
 * `scaffoldState` to open the drawer of the [Scaffold], and the `viewModel` argument is our
 * [MainViewModel] parameter [viewModel].
 *
 * @param widthSize the [WindowWidthSizeClass] of the device we are running on, one of
 * [WindowWidthSizeClass.Compact], [WindowWidthSizeClass.Medium], or [WindowWidthSizeClass.Expanded]
 * @param onExploreItemClicked a lambda that we should call with the [ExploreModel] of the item that
 * the user has clicked. It traces up the hierarchy to a lambda created in the `onCreate`  override
 * of [MainActivity] that calls the [launchDetailsActivity] method to launch the [DetailsActivity]
 * to display a [GoogleMap] that corresponds to the [ExploreModel] it is called with.
 * @param onDateSelectionClicked a lambda that should be called when the user indicates that they
 * wish to select dates. It traces back to the `onCreate` override to be a lambda that calls the
 * [NavController.navigate] method to navigate to the route [Routes.Calendar.route] which displays
 * the [CalendarScreen] Composable.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [MainContent] passes the [Modifier] that is passed it by its parent
 * [MainScreen] which is a [Modifier.alpha] using an animated `alpha` argument which animates from
 * 0f to 1f with a duration of 300ms as the [LandingScreen] fades out.
 * @param viewModel the [MainViewModel] for the app. It is injected using [hiltViewModel] in
 * the `onCreate` override.
 */
@Composable
fun CraneHome(
    widthSize: WindowWidthSizeClass,
    onExploreItemClicked: OnExploreItemClicked,
    onDateSelectionClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.statusBarsPadding(),
        drawerContent = {
            CraneDrawer()
        }
    ) { contentPadding: PaddingValues ->
        val scope: CoroutineScope = rememberCoroutineScope()
        CraneHomeContent(
            modifier = modifier.padding(paddingValues = contentPadding),
            widthSize = widthSize,
            onExploreItemClicked = onExploreItemClicked,
            onDateSelectionClicked = onDateSelectionClicked,
            openDrawer = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            },
            viewModel = viewModel
        )
    }
}

/**
 *
 */
@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun CraneHomeContent(
    widthSize: WindowWidthSizeClass,
    onExploreItemClicked: OnExploreItemClicked,
    onDateSelectionClicked: () -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val suggestedDestinations: List<ExploreModel>? by viewModel.suggestedDestinations.observeAsState()

    val onPeopleChanged: (Int) -> Unit = { viewModel.updatePeople(it) }
    val craneScreenValues: Array<CraneScreen> = CraneScreen.values()
    val pagerState: PagerState =
        rememberPagerState(initialPage = CraneScreen.Fly.ordinal) { craneScreenValues.size }

    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed),
        frontLayerShape = BottomSheetShape,
        frontLayerScrimColor = Color.Unspecified,
        appBar = {
            HomeTabBar(
                openDrawer = openDrawer,
                tabSelected = craneScreenValues[pagerState.currentPage],
                onTabSelected = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = it.ordinal,
                            animationSpec = tween(
                                TAB_SWITCH_ANIM_DURATION
                            )
                        )
                    }
                })
        },
        backLayerContent = {
            SearchContent(
                widthSize = widthSize,
                tabSelected = craneScreenValues[pagerState.currentPage],
                viewModel = viewModel,
                onPeopleChanged = onPeopleChanged,
                onDateSelectionClicked = onDateSelectionClicked,
                onExploreItemClicked = onExploreItemClicked
            )
        },
        frontLayerContent = {
            HorizontalPager(state = pagerState) { page: Int ->
                when (craneScreenValues[page]) {
                    CraneScreen.Fly -> {
                        suggestedDestinations?.let { destinations: List<ExploreModel> ->
                            ExploreSection(
                                widthSize = widthSize,
                                title = stringResource(id = R.string.explore_flights_by_destination),
                                exploreList = destinations,
                                onItemClicked = onExploreItemClicked
                            )
                        }
                    }

                    CraneScreen.Sleep -> {
                        ExploreSection(
                            widthSize = widthSize,
                            title = stringResource(id = R.string.explore_properties_by_destination),
                            exploreList = viewModel.hotels,
                            onItemClicked = onExploreItemClicked
                        )
                    }

                    CraneScreen.Eat -> {
                        ExploreSection(
                            widthSize = widthSize,
                            title = stringResource(id = R.string.explore_restaurants_by_destination),
                            exploreList = viewModel.restaurants,
                            onItemClicked = onExploreItemClicked
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun HomeTabBar(
    openDrawer: () -> Unit,
    tabSelected: CraneScreen,
    onTabSelected: (CraneScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    CraneTabBar(
        modifier = modifier
            .wrapContentWidth()
            .sizeIn(maxWidth = 500.dp),
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

private const val TAB_SWITCH_ANIM_DURATION = 300

@Composable
private fun SearchContent(
    widthSize: WindowWidthSizeClass,
    tabSelected: CraneScreen,
    viewModel: MainViewModel,
    onPeopleChanged: (Int) -> Unit,
    onDateSelectionClicked: () -> Unit,
    onExploreItemClicked: OnExploreItemClicked
) {
    // Reading datesSelected State from here instead of passing the String from the ViewModel
    // to cause a recomposition when the dates change.
    val selectedDates = viewModel.calendarState.calendarUiState.value.selectedDatesFormatted
    AnimatedContent(
        targetState = tabSelected,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(TAB_SWITCH_ANIM_DURATION, easing = EaseIn)
            ).togetherWith(
                fadeOut(
                    animationSpec = tween(TAB_SWITCH_ANIM_DURATION, easing = EaseOut)
                )
            ).using(
                SizeTransform(
                    sizeAnimationSpec = { _, _ ->
                        tween(TAB_SWITCH_ANIM_DURATION, easing = EaseInOut)
                    }
                )
            )
        },
        label = "SearchContent"
    ) { targetState ->
        when (targetState) {
            CraneScreen.Fly -> FlySearchContent(
                widthSize = widthSize,
                datesSelected = selectedDates,
                searchUpdates = FlySearchContentUpdates(
                    onPeopleChanged = onPeopleChanged,
                    onToDestinationChanged = { viewModel.toDestinationChanged(it) },
                    onDateSelectionClicked = onDateSelectionClicked,
                    onExploreItemClicked = onExploreItemClicked
                )
            )

            CraneScreen.Sleep -> SleepSearchContent(
                widthSize = widthSize,
                datesSelected = selectedDates,
                sleepUpdates = SleepSearchContentUpdates(
                    onPeopleChanged = onPeopleChanged,
                    onDateSelectionClicked = onDateSelectionClicked,
                    onExploreItemClicked = onExploreItemClicked
                )
            )

            CraneScreen.Eat -> EatSearchContent(
                widthSize = widthSize,
                datesSelected = selectedDates,
                eatUpdates = EatSearchContentUpdates(
                    onPeopleChanged = onPeopleChanged,
                    onDateSelectionClicked = onDateSelectionClicked,
                    onExploreItemClicked = onExploreItemClicked
                )
            )
        }
    }
}

data class FlySearchContentUpdates(
    val onPeopleChanged: (Int) -> Unit,
    val onToDestinationChanged: (String) -> Unit,
    val onDateSelectionClicked: () -> Unit,
    val onExploreItemClicked: OnExploreItemClicked
)

data class SleepSearchContentUpdates(
    val onPeopleChanged: (Int) -> Unit,
    val onDateSelectionClicked: () -> Unit,
    val onExploreItemClicked: OnExploreItemClicked
)

data class EatSearchContentUpdates(
    val onPeopleChanged: (Int) -> Unit,
    val onDateSelectionClicked: () -> Unit,
    val onExploreItemClicked: OnExploreItemClicked
)
