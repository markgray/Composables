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

package com.google.samples.apps.sunflower.compose.plantlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel

/**
 * Displays a grid of plants (this is the stateful override that calls its stateless override to
 * do the actual work once it retrieves the plants to be displayed from the database). We initialize
 * our [State] wrapped [List] of [Plant] variable `plants` to the value returned by the
 * [LiveData.observeAsState] method of the [LiveData] of [List] of [Plant] property
 * [PlantListViewModel.plants] of our [PlantListViewModel] parameter [viewModel]. Then we call our
 * stateless override [PlantListScreen] with the arguments:
 *  - `plants`: our [State] wrapped [List] of [Plant] variable `plants`
 *  - `modifier`: our [Modifier] parameter [modifier]
 *  - `onPlantClick`: our lambda parameter [onPlantClick]
 *
 * @param onPlantClick lambda to be called with a [Plant] when the [Plant] is clicked
 * @param modifier Modifier to be applied to the layout.
 * @param viewModel the [PlantListViewModel] used to get the plants, injected via Hilt.
 */
@Composable
fun PlantListScreen(
    onPlantClick: (Plant) -> Unit,

    modifier: Modifier = Modifier,
    viewModel: PlantListViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(value = LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        }, key = null
    ),
) {
    val plants: List<Plant> by viewModel.plants.observeAsState(initial = emptyList())
    PlantListScreen(plants = plants, modifier = modifier, onPlantClick = onPlantClick)
}

/**
 * The main screen that shows a grid of all available plants (stateless override that is called by
 * our stateful override). Our root composable is a [LazyVerticalGrid] whose arguments are:
 *  - `columns`: the number of columns in the grid is [GridCells.Fixed] with a `count` of 2
 *  - `modifier`: chained to our [Modifier] parameter [modifier] is a [Modifier.testTag] with a
 *  value of "plant_list", chained to a [Modifier.imePadding] to add padding to accommodate the ime
 *  insets.
 *  - `contentPadding`: is a [PaddingValues] that adds the [dimensionResource] with the id
 *  `R.dimen.card_side_margin` (12.dp) to the `horizontal` sides, and the [dimensionResource] with
 *  the id `R.dimen.header_margin` (24.dp) to the `vertical` sides.
 *
 * In the [LazyGridScope] `content` composable lambda argument of the [LazyVerticalGrid] we compose
 * a [LazyGridScope.items] whose `items` argument is our [List] of [Plant] variable [plants] and
 * whose `key` if a lambda that accepts the current [Plant] passed the lambda in variable `plant`
 * and returns the [Plant.plantId] of the `plant`. In the [LazyGridItemScope] `itemContent` composable
 * lambda argument of the [LazyGridScope.items] we accept the [Plant] passed the lambda in variable
 * `plant` and compose a [PlantListItem] whose `plant` argument is the `plant` and `onClick` lambda
 * argument is our lambda parameter [onPlantClick].
 *
 * @param plants The list of plants to display.
 * @param modifier Modifier to be applied to the layout.
 * @param onPlantClick A lambda that is called with the [Plant] when the [Plant] is clicked.
 */
@Composable
fun PlantListScreen(
    plants: List<Plant>,
    modifier: Modifier = Modifier,
    onPlantClick: (Plant) -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 2),
        modifier = modifier
            .testTag(tag = "plant_list")
            .imePadding(),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(id = R.dimen.card_side_margin),
            vertical = dimensionResource(id = R.dimen.header_margin)
        )
    ) {
        items(
            items = plants,
            key = { plant: Plant -> plant.plantId }
        ) { plant: Plant ->
            PlantListItem(plant = plant) {
                onPlantClick(plant)
            }
        }
    }
}

/**
 * A preview of the [PlantListScreen] composable. It uses a [PlantListPreviewParamProvider]
 * to supply a sample list of [Plant] objects for display. This allows for visualizing
 * the screen with both an empty list and a list containing several plant items.
 *
 * @param plants A list of [Plant] objects provided by the [PreviewParameter] annotation,
 * used to populate the plant list for the preview.
 */
@Preview
@Composable
private fun PlantListScreenPreview(
    @PreviewParameter(provider = PlantListPreviewParamProvider::class) plants: List<Plant>
) {
    PlantListScreen(plants = plants)
}

/**
 * A [PreviewParameterProvider] for the [PlantListScreenPreview].
 *
 * It provides two different states for the preview:
 *  1. An empty list of plants.
 *  2. A list of four sample plants.
 *
 * This allows for testing and visualizing the [PlantListScreen] composable under different
 * data conditions.
 */
private class PlantListPreviewParamProvider : PreviewParameterProvider<List<Plant>> {
    override val values: Sequence<List<Plant>> =
        sequenceOf(
            emptyList(),
            listOf(
                Plant(plantId = "1", name = "Apple", description = "Apple", growZoneNumber = 1),
                Plant(plantId = "2", name = "Banana", description = "Banana", growZoneNumber = 2),
                Plant(plantId = "3", name = "Carrot", description = "Carrot", growZoneNumber = 3),
                Plant(plantId = "4", name = "Dill", description = "Dill", growZoneNumber = 3),
            )
        )
}
