/*
 * Copyright 2023 Google LLC
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

package com.google.samples.apps.sunflower.compose.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.compose.garden.GardenScreen
import com.google.samples.apps.sunflower.compose.plantlist.PlantListScreen
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import com.google.samples.apps.sunflower.ui.SunflowerTheme
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Used to represent the pages available in the Home screen.
 *
 * @param titleResId The string resource id for the title of this page.
 * @param drawableResId The drawable resource id for the icon of this page.
 */
enum class SunflowerPage(
    @param:StringRes val titleResId: Int,
    @param:DrawableRes val drawableResId: Int
) {
    MY_GARDEN(
        titleResId = R.string.my_garden_title,
        drawableResId = R.drawable.ic_my_garden_active
    ),
    PLANT_LIST(
        titleResId = R.string.plant_list_title,
        drawableResId = R.drawable.ic_plant_list_active
    )
}

/**
 * Composable function that represents the home screen of the Sunflower app.
 *
 * This screen displays a pager with two tabs: "My Garden" and "Plant List".
 * The "My Garden" tab shows the plants that the user has added to their garden.
 * The "Plant List" tab shows a list of all available plants.
 *
 * The user can navigate between the tabs by swiping or tapping on the tabs.
 *
 * We start by initializing and remembering our [PagerState] variable `pagerState` with a `pageCount`
 * of the [Array.size] of our [Array] of [SunflowerPage] parameter [pages], and initializing our
 * [TopAppBarScrollBehavior] variable `scrollBehavior` to an instance of
 * [TopAppBarDefaults.enterAlwaysScrollBehavior].
 *
 * Our root composable is a [Scaffold] whose `modifier` argument chained to our [Modifier] parameter
 * [modifier] a [Modifier.nestedScroll] with the [TopAppBarScrollBehavior.nestedScrollConnection]
 * of our [TopAppBarScrollBehavior] variable `scrollBehavior` as its `connection`, and the `topBar`
 * argument is a lambda that composes a [HomeTopAppBar] whose `pagerState` argument is our [PagerState]
 * variable `pagerState`, `onFilterClick` argument is a lambda that calls the
 * [PlantListViewModel.updateData] method of our [PlantListViewModel] parameter [viewModel], and the
 * `scrollBehavior` argument is our [TopAppBarScrollBehavior] variable `scrollBehavior`.
 *
 * In the `content` composable lambda argument of the [Scaffold] we accept the [PaddingValues] passed
 * the lambda in variable `contentPadding` then compose a [HomePagerScreen] whose arguments are:
 *  - `onPlantClick`: is our [onPlantClick] lambda parameter.
 *  - `pagerState`: is our [PagerState] variable `pagerState`.
 *  - `pages`: is our [Array] of [SunflowerPage] parameter [pages].
 *  - `modifier`: is a [Modifier.padding] with the `top` argument set to the value returned by the
 *  [PaddingValues.calculateTopPadding] method of our [PaddingValues] variable `contentPadding`.
 *
 * @param modifier The modifier to be applied to the HomeScreen.
 * @param onPlantClick A callback that is invoked when a plant is clicked.
 * @param viewModel The ViewModel that provides data for the screen.
 * @param pages An array of SunflowerPage objects that represent the tabs in the pager.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onPlantClick: (Plant) -> Unit = {},
    viewModel: PlantListViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(value = LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        }, key = null
    ),
    pages: Array<SunflowerPage> = SunflowerPage.entries.toTypedArray()
) {
    val pagerState: PagerState = rememberPagerState(pageCount = { pages.size })
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                pagerState = pagerState,
                onFilterClick = { viewModel.updateData() },
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding: PaddingValues ->
        HomePagerScreen(
            onPlantClick = onPlantClick,
            pagerState = pagerState,
            pages = pages,
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding())
        )
    }
}

/**
 * A composable function that displays a horizontal pager with tabs for navigating between different
 * sections of the app.
 *
 * Our root composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we first initialize and
 * remember our [CoroutineScope] variable `coroutineScope` using the [rememberCoroutineScope] method.
 * Then we compose a [PrimaryTabRow] whose `selectedTabIndex` argument is the [PagerState.currentPage]
 * of our [PagerState] parameter [pagerState]. In the `tabs` composable lambda argument of the
 * [PrimaryTabRow] we use the [Array.forEachIndexed] method to iterate over each [SunflowerPage] in
 * our [Array] of [SunflowerPage] parameter [pages]. In the lambda body of the [Array.forEachIndexed]
 * we capture the [Int] passed the lambda in variable `index` and the [SunflowerPage] passed the
 * lambda in variable `page`. Then we initialize our [String] variable `title` to the [stringResource]
 * whose `id` argument is the [SunflowerPage.titleResId] of our [SunflowerPage] variable `page`.
 * Then we compose a [Tab] whose arguments are:
 *  - `selected`: is `true` if the [Int] variable `index` is equal to the [PagerState.currentPage]
 *  of our [PagerState] parameter [pager.
 *  - `onClick`: is a lambda that calls the [CoroutineScope.launch] method of our [CoroutineScope]
 *  variable `coroutineScope` to launch a coroutine that calls the [PagerState.animateScrollToPage]
 *  method of our [PagerState] parameter [pagerState] with the [Int] variable `index` as its `page`
 *  argument to scroll animate to the page corresponding to the [Tab].
 *  - `text`: is a lambda that composes a [Text] whose `text` argument is our [String] variable
 *  `title`.
 *  - `icon`: is a lambda that composes an [Icon] whose `painter` argument is a [painterResource]
 *  whose `id` is the [SunflowerPage.drawableResId] of our [SunflowerPage] variable `page`, and
 *  whose `contentDescription` argument is our [String] variable `title`.
 *  - `unselectedContentColor`: is the [ColorScheme.secondary] of our custom [MaterialTheme.colorScheme]
 *
 * Next we compose a [HorizontalPager] whose arguments are:
 *  - `modifier`: is a [Modifier.background] with the [ColorScheme.background] of our custom
 *  [MaterialTheme.colorScheme] as its `color`.
 *  - `state`: is our [PagerState] parameter [pagerState].
 *  - `verticalAlignment`: is [Alignment.Top].
 *
 * In the [PagerScope] composable lambda argument of the [HorizontalPager] we accept the [Int] passed
 * the lambda in variable `index` then switch on the [SunflowerPage] at `index` in the [pages]
 * array of [SunflowerPage] parameter [pages]:
 *
 * **[SunflowerPage.MY_GARDEN]** we compose a [GardenScreen] whose arguments are:
 *  - `modifier`: is a [Modifier.fillMaxSize].
 *  - `onAddPlantClick`: is a lambda that calls the [CoroutineScope.launch] method of our
 *  [CoroutineScope] variable `coroutineScope` to launch a coroutine that calls the
 *  [PagerState.scrollToPage] method of our [PagerState] parameter [pagerState] with the
 *  `page` argument set to the [SunflowerPage.ordinal] of [SunflowerPage.PLANT_LIST]
 *  - `onPlantClick`: is a lambda that accepts the [PlantAndGardenPlantings] passed the lambda in
 *  variable `plantings` then calls our [onPlantClick] lambda parameter [onPlantClick] with the
 *  [PlantAndGardenPlantings.plant] of our [PlantAndGardenPlantings] variable `plantings` as its
 *  argument.
 *
 * **[SunflowerPage.PLANT_LIST]** we compose a [PlantListScreen] whose arguments are:
 *  - `onPlantClick`: is our lambda parameter [onPlantClick].
 *  - `modifier`: is a [Modifier.fillMaxSize].
 *
 * @param onPlantClick A callback that is invoked when a plant is clicked.
 * @param pagerState The state of the pager, used to control the current page and scrolling.
 * @param pages An array of [SunflowerPage] objects representing the pages to display.
 * @param modifier A [Modifier] to apply to the pager.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePagerScreen(
    onPlantClick: (Plant) -> Unit,
    pagerState: PagerState,
    pages: Array<SunflowerPage>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val coroutineScope: CoroutineScope = rememberCoroutineScope()

        // Tab Row
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            pages.forEachIndexed { index: Int, page: SunflowerPage ->
                val title: String = stringResource(id = page.titleResId)
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = index)
                        }
                    },
                    text = { Text(text = title) },
                    icon = {
                        Icon(
                            painter = painterResource(id = page.drawableResId),
                            contentDescription = title
                        )
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Pages
        HorizontalPager(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { index: Int ->
            when (pages[index]) {
                SunflowerPage.MY_GARDEN -> {
                    GardenScreen(
                        modifier = Modifier.fillMaxSize(),
                        onAddPlantClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(page = SunflowerPage.PLANT_LIST.ordinal)
                            }
                        },
                        onPlantClick = { plantings: PlantAndGardenPlantings ->
                            onPlantClick(plantings.plant)
                        })
                }

                SunflowerPage.PLANT_LIST -> {
                    PlantListScreen(
                        onPlantClick = onPlantClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

/**
 * This is the [CenterAlignedTopAppBar] that is used as the `topBar` argument of the [Scaffold] used
 * by the [HomeScreen] composable. Its `title` argument is a [Text] that displays the `text` whose
 * resource ID is `R.string.app_name` ("Sunflower"), using the `style` [Typography.headlineSmall] of
 * our custom [MaterialTheme.typography]. Its `modifier` argument is our [Modifier] parameter
 * [modifier]. Its `actions` argument is a lambda which composes an [IconButton] if the
 * [PagerState.currentPage] property of our [PagerState] parameter [pagerState] is equal to the
 * [SunflowerPage.ordinal] of [SunflowerPage.PLANT_LIST] value (the second page of the [HorizontalPager]
 * which displays all the plants in the app). The `onClick` argument of this [IconButton] is our
 * lambda parameter [onFilterClick], and its `content` is an [Icon] whose `painter` argument draws
 * the drawable with resource ID `R.drawable.ic_filter_list_24dp` (a stylized list with check marks
 * next to the items), and its `contentDescription` is the string with resource ID
 * `R.string.menu_filter_by_grow_zone` ("Filter by grow zone"). The `scrollBehavior` argument of the
 * [CenterAlignedTopAppBar] is our [TopAppBarScrollBehavior] parameter [scrollBehavior].
 *
 * @param pagerState the [PagerState] that is used by the [HorizontalPager] of the [HomeScreen].
 * @param onFilterClick a lambda that the [IconButton] of our `actions` should call when it is clicked.
 * @param scrollBehavior the [TopAppBarScrollBehavior] that the [CenterAlignedTopAppBar] should use.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or behavior.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    pagerState: PagerState,
    onFilterClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        modifier = modifier,
        actions = {
            if (pagerState.currentPage == SunflowerPage.PLANT_LIST.ordinal) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter_list_24dp),
                        contentDescription = stringResource(
                            id = R.string.menu_filter_by_grow_zone
                        )
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

/**
 * This is a Preview of our [HomePagerScreen] composable. We initialize our [Array] of [SunflowerPage]
 * variable `val pages` to the [Array] that the [Collection.toTypedArray] method creates from the
 * [List] of [SunflowerPage] that the [SunflowerPage.entries] property returns. Then wrapped in our
 * [SunflowerTheme] custom [MaterialTheme] we compose a [HomePagerScreen] whose `onPlantClick`
 * argument is an empty lambda, whose `pagerState` argument is the [PagerState] returned by
 * [rememberPagerState] when its `pageCount` argument is a lambda returning the [Array.size] of
 * `pages`, and whose `pages` argument is our `pages` variable.
 */
@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun HomeScreenPreview() {
    SunflowerTheme {
        val pages: Array<SunflowerPage> = SunflowerPage.entries.toTypedArray()
        HomePagerScreen(
            onPlantClick = {},
            pagerState = rememberPagerState(pageCount = { pages.size }),
            pages = pages
        )
    }
}
