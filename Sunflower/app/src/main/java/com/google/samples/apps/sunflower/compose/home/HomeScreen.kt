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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
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
 * TODO: Continue here.
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
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(page = index) } },
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
