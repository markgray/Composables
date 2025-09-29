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

package com.google.samples.apps.sunflower.compose.garden

import android.app.Activity
import androidx.activity.compose.ReportDrawn
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridLayoutInfo
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.GardenPlanting
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import com.google.samples.apps.sunflower.ui.SunflowerTheme
import com.google.samples.apps.sunflower.viewmodels.GardenPlantingListViewModel
import com.google.samples.apps.sunflower.viewmodels.PlantAndGardenPlantingsViewModel
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

/**
 * Stateful [GardenScreen] that retrieves the garden plants from the [GardenPlantingListViewModel]
 * and displays them in our stateless [GardenScreen] overload. We initialize our [List] of
 * [PlantAndGardenPlantings] variable `gardenPlants` using the [collectAsStateWithLifecycle] method
 * of the [StateFlow] of [List] of [PlantAndGardenPlantings] property
 * [GardenPlantingListViewModel.plantAndGardenPlantings] of our [GardenPlantingListViewModel] parameter
 * [viewModel]. Then we compose our stateless [GardenScreen] overload with the arguments:
 *  - `gardenPlants`: our list of [PlantAndGardenPlantings] variable `gardenPlants`
 *  - `modifier`: our [Modifier] parameter [modifier].
 *  - `onAddPlantClick`: our [onAddPlantClick] lambda parameter.
 *  - `onPlantClick`: our [onPlantClick] lambda parameter.
 *
 * @param modifier Modifier to be applied to the composable.
 * @param viewModel [GardenPlantingListViewModel] that contains the list of [PlantAndGardenPlantings].
 * @param onAddPlantClick Callback that is invoked when the "Add plant" button is clicked.
 * @param onPlantClick Callback that is invoked when a plant in the garden is clicked.
 */
@Composable
fun GardenScreen(
    modifier: Modifier = Modifier,
    viewModel: GardenPlantingListViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(value = LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        }, key = null
    ),
    onAddPlantClick: () -> Unit,
    onPlantClick: (PlantAndGardenPlantings) -> Unit
) {

    val gardenPlants: List<PlantAndGardenPlantings>
        by viewModel.plantAndGardenPlantings.collectAsStateWithLifecycle()

    GardenScreen(
        gardenPlants = gardenPlants,
        modifier = modifier,
        onAddPlantClick = onAddPlantClick,
        onPlantClick = onPlantClick
    )
}

/**
 * Displays the garden screen.
 *
 * This composable function is responsible for rendering the garden screen. It takes a list of
 * [PlantAndGardenPlantings] and composes a [GardenList] to display them in a grid. If the list is
 * empty, it composes an [EmptyGarden] to inform the user that their garden is empty and displays a
 * message inviting the user to add plants to their garden.
 *
 * If our [List] of [PlantAndGardenPlantings] parameter [gardenPlants] is empty, we compose an
 * [EmptyGarden] with the arguments:
 *  - `onAddPlantClick`: our [onAddPlantClick] lambda parameter.
 *  - `modifier`: our [Modifier] parameter [modifier].
 *
 * If our [List] of [PlantAndGardenPlantings] parameter [gardenPlants] is not empty, we compose a
 * [GardenList] with the arguments:
 *  - `gardenPlants`: our [List] of [PlantAndGardenPlantings] parameter [gardenPlants]
 *  - `onPlantClick`: our [onPlantClick] lambda parameter.
 *  - `modifier`: our [Modifier] parameter [modifier].
 *
 * @param gardenPlants A list of [PlantAndGardenPlantings] to display in the garden.
 * @param modifier A [Modifier] to apply to the composable.
 * @param onAddPlantClick A lambda function to be invoked when the "Add plant" button is clicked.
 * @param onPlantClick A lambda function to be invoked when a plant in the garden is clicked. It
 * receives the [PlantAndGardenPlantings] that was clicked as a parameter.
 */
@Composable
fun GardenScreen(
    gardenPlants: List<PlantAndGardenPlantings>,
    modifier: Modifier = Modifier,
    onAddPlantClick: () -> Unit = {},
    onPlantClick: (PlantAndGardenPlantings) -> Unit = {}
) {
    if (gardenPlants.isEmpty()) {
        EmptyGarden(onAddPlantClick = onAddPlantClick, modifier = modifier)
    } else {
        GardenList(gardenPlants = gardenPlants, onPlantClick = onPlantClick, modifier = modifier)
    }
}

/**
 * Composable that displays a list of [PlantAndGardenPlantings] in a [LazyVerticalGrid].
 *
 * We start by initializing and remembering our [LazyGridState] variable `gridState` using the
 * [rememberLazyGridState] method. Then we call the [ReportDrawnWhen] method to add the condition
 * that it should wait to call [Activity.reportFullyDrawn] until the
 * [LazyGridLayoutInfo.totalItemsCount] of the [LazyGridState.layoutInfo] of [LazyGridState]
 * variable `gridState` is greater than 0 (our [LazyVerticalGrid] has been rendered).
 *
 * Our root Composable is a [LazyVerticalGrid] whose arguments are:
 *  - `columns`: a [GridCells.Fixed] whose `count` argument is `2` (Defines a grid with `2` columns).
 *  - `modifier`: a [Modifier.imePadding] chained to our [Modifier] parameter [modifier] to add
 *  padding to accommodate the ime insets.
 *  - `state`: our [LazyGridState] variable `gridState`.
 *  - `contentPadding`: a [PaddingValues] that adds `horizontal` padding of `12.dp` (the
 *  [dimensionResource] whose id is `R.dimen.card_side_margin`) and a `vertical`
 *  padding of `16.dp` (the [dimensionResource] whose id is `R.dimen.margin_normal`).
 *
 * In the [LazyGridScope] `content` composable lambda argument of our [LazyVerticalGrid], we compose
 * a [LazyGridScope.items] whose `items` argument is our [List] of [PlantAndGardenPlantings]
 * parameter [gardenPlants] and whose `key` is the value of the [Plant.plantId] of the current
 * [PlantAndGardenPlantings.plant]. In the [LazyGridItemScope] `itemContent` composable lambda
 * argument of the [LazyGridScope.items] we accept the [PlantAndGardenPlantings] passed the lambda
 * in variable `planting` and compose a [GardenListItem] whose `plant` argument is the
 * [PlantAndGardenPlantings] variable `planting` and whose `onPlantClick` argument is our
 * [onPlantClick] lambda parameter.
 *
 * @param gardenPlants the [List] of [PlantAndGardenPlantings] that are to be displayed.
 * @param onPlantClick a lambda that will be called with the [PlantAndGardenPlantings] that was
 * clicked when a [PlantAndGardenPlantings] is clicked.
 */
@Composable
private fun GardenList(
    gardenPlants: List<PlantAndGardenPlantings>,
    onPlantClick: (PlantAndGardenPlantings) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Call reportFullyDrawn when the garden list has been rendered
    val gridState: LazyGridState = rememberLazyGridState()
    ReportDrawnWhen { gridState.layoutInfo.totalItemsCount > 0 }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.imePadding(),
        state = gridState,
        contentPadding = PaddingValues(
            horizontal = dimensionResource(id = R.dimen.card_side_margin),
            vertical = dimensionResource(id = R.dimen.margin_normal)
        )
    ) {
        items(
            items = gardenPlants,
            key = { planting: PlantAndGardenPlantings -> planting.plant.plantId }
        ) { planting: PlantAndGardenPlantings ->
            GardenListItem(plant = planting, onPlantClick = onPlantClick)
        }
    }
}

/**
 * This composable function is responsible for rendering a single item in the garden list.
 * It displays information about a plant, including its image, name, planted date, last watered date,
 * and watering interval.
 *
 * It starts by initializing its [PlantAndGardenPlantingsViewModel] variable `vm` to a new
 * instance constructed to hold our [PlantAndGardenPlantings] parameter [plant]. It initializes
 * its [Dp] variable `cardSideMargin` to the [dimensionResource] whose id is `R.dimen.card_side_margin`
 * (12.dp). It initializes its [Dp] variable `marginNormal` to the [dimensionResource] whose id is
 * `R.dimen.margin_normal` (16.dp).
 *
 * The root Composable is an [ElevatedCard]. Its `onClick` argument is a lambda that calls our
 * [onPlantClick] lambda parameter with our [PlantAndGardenPlantings] parameter [plant]. Its
 * `modifier` argument is a `Modifier.padding` whose `start` and `end` padding are `cardSideMargin`
 * (12.dp) and whose `bottom` padding is the dimension with resource ID `R.dimen.card_bottom_margin`
 * (26.dp). Its `colors` argument is a [CardDefaults.cardColors] whose `containerColor` is the
 * [ColorScheme.secondaryContainer] of the [MaterialTheme.colorScheme] of our custom [MaterialTheme].
 *
 * In the [ColumnScope] `content` composable lambda argument of the [ElevatedCard], we compose a
 * [Column] whose `modifier` argument is a [Modifier.fillMaxWidth]. In the [ColumnScope] `content`
 * composable lambda argument of the [Column], we compose:
 *
 * **First**: a [GlideImage] whose arguments are:
 *  - `model`: the [PlantAndGardenPlantingsViewModel.imageUrl] of our [PlantAndGardenPlantingsViewModel]
 *  variable `vm`.
 *  - `contentDescription`: the [Plant.description] of our [PlantAndGardenPlantings] parameter
 *  [plant].
 *  - `modifier`: a [Modifier.fillMaxWidth] chained to a [Modifier.height] whose `height` is the
 *  [dimensionResource] whose id is `R.dimen.plant_item_image_height` (95.dp).
 *  - `contentScale`: [ContentScale.Crop].
 *
 * **Second**: a [Text] whose arguments are:
 *  - `text`: the [PlantAndGardenPlantingsViewModel.plantName] of our [PlantAndGardenPlantingsViewModel]
 *  variable `vm`.
 *  - `modifier`: a [Modifier.padding] whose `vertical` padding is `marginNormal` (16.dp) chained to
 *  a [ColumnScope.align] whose `alignment` is [Alignment.CenterHorizontally].
 *  - `style`: the [Typography.titleMedium] of the [MaterialTheme.typography] of our custom
 *  [MaterialTheme].
 *
 * **Third**: a [Text] whose arguments are:
 *  - `text`: the string resource with ID `R.string.plant_date_header` ("Planted").
 *  - `modifier`: a [ColumnScope.align] whose `alignment` is [Alignment.CenterHorizontally].
 *  - `style`: the [Typography.titleSmall] of the [MaterialTheme.typography] of our custom
 *  [MaterialTheme].
 *
 * **Fourth**: a [Text] whose arguments are:
 *  - `text`: the [PlantAndGardenPlantingsViewModel.plantDateString] of our
 *  [PlantAndGardenPlantingsViewModel] variable `vm`.
 *  - `modifier`: a [ColumnScope.align] whose `alignment` is [Alignment.CenterHorizontally].
 *  - `style`: the [Typography.labelSmall] of the [MaterialTheme.typography] of our custom
 *  [MaterialTheme].
 *
 * **Fifth**: a [Text] whose arguments are:
 *  - `text`: the string resource with ID `R.string.watered_date_header` ("Last watered").
 *  - `modifier`: a [ColumnScope.align] whose `alignment` is [Alignment.CenterHorizontally] chained
 *  to a [Modifier.padding] whose `top` padding is `marginNormal` (16.dp).
 *  - `style`: the [Typography.titleSmall] of the [MaterialTheme.typography] of our custom
 *  [MaterialTheme].
 *
 * **Sixth**: a [Text] whose arguments are:
 *  - `text`: the [PlantAndGardenPlantingsViewModel.waterDateString] of our
 *  [PlantAndGardenPlantingsViewModel] variable `vm`.
 *  - `modifier`: a [ColumnScope.align] whose `alignment` is [Alignment.CenterHorizontally].
 *  - `style`: the [Typography.labelSmall] of the [MaterialTheme.typography] of our custom
 *  [MaterialTheme].
 *
 * **Seventh**: a [Text] whose arguments are:
 *  - `text`: the plural string resource with `id` `R.plurals.watering_next`, with the `count`
 *  the [PlantAndGardenPlantingsViewModel.wateringInterval] of our [PlantAndGardenPlantingsViewModel]
 *  variable `vm` and whose `formatArgs` argument is the
 *  [PlantAndGardenPlantingsViewModel.wateringInterval] of our [PlantAndGardenPlantingsViewModel]
 *  variable `vm`.
 *  - `modifier`: a [ColumnScope.align] whose `alignment` is [Alignment.CenterHorizontally] chained
 *  to a [Modifier.padding] whose `bottom` padding is `marginNormal` (16.dp).
 *  - `style`: the [Typography.labelSmall] of the [MaterialTheme.typography] of our custom
 *  [MaterialTheme].
 *
 * @param plant The [PlantAndGardenPlantings] to display in the card.
 * @param onPlantClick A lambda function to be invoked when the card is clicked. It receives the
 * [PlantAndGardenPlantings] that was clicked as a parameter.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun GardenListItem(
    plant: PlantAndGardenPlantings,
    onPlantClick: (PlantAndGardenPlantings) -> Unit
) {
    val vm = PlantAndGardenPlantingsViewModel(plantings = plant)

    // Dimensions
    val cardSideMargin: Dp = dimensionResource(id = R.dimen.card_side_margin)
    val marginNormal: Dp = dimensionResource(id = R.dimen.margin_normal)

    ElevatedCard(
        onClick = { onPlantClick(plant) },
        modifier = Modifier.padding(
            start = cardSideMargin,
            end = cardSideMargin,
            bottom = dimensionResource(id = R.dimen.card_bottom_margin)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(Modifier.fillMaxWidth()) {
            GlideImage(
                model = vm.imageUrl,
                contentDescription = plant.plant.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = dimensionResource(id = R.dimen.plant_item_image_height)),
                contentScale = ContentScale.Crop,
            )

            // Plant name
            Text(
                text = vm.plantName,
                modifier = Modifier
                    .padding(vertical = marginNormal)
                    .align(alignment = Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleMedium,
            )

            // Planted date
            Text(
                text = stringResource(id = R.string.plant_date_header),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = vm.plantDateString,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                style = MaterialTheme.typography.labelSmall
            )

            // Last Watered
            Text(
                text = stringResource(id = R.string.watered_date_header),
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(top = marginNormal),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = vm.waterDateString,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = pluralStringResource(
                    id = R.plurals.watering_next,
                    count = vm.wateringInterval,
                    vm.wateringInterval
                ),
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(bottom = marginNormal),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

/**
 * Composable that displays a screen when the garden is empty.
 *
 * We call the [ReportDrawn] method to have the system call [Activity.reportFullyDrawn] when this
 * Composable is composed. Our root Composable is a [Column] whose `modifier` argument is our
 * [Modifier] parameter [modifier], whose `horizontalAlignment` argument centers its children
 * horizontally, and whose `verticalArrangement` argument centers its children vertically.
 *
 * In the [ColumnScope] `content` Composable lambda argument of the [Column] we compose:
 *  - A [Text] whose `text` is the string "Your garden is empty" (the [stringResource] whose ID is
 *  `R.string.garden_empty`), and whose `style` argument is the [Typography.headlineSmall] of our
 *  custom [MaterialTheme.typography].
 *  - A [Button] whose `shape` argument is the [Shapes.medium] of the [MaterialTheme.shapes] of our
 *  custom [MaterialTheme], and whose `onClick` argument is our [onAddPlantClick]
 *  lambda parameter. In its [RowScope] `content` Composable lambda it composes a [Text] whose
 *  `text` is the string "Add plant" (the [stringResource] whose ID is `R.string.add_plant`), and
 *  whose `style` argument is the [Typography.titleSmall] of our custom [MaterialTheme.typography].
 *
 * @param onAddPlantClick A lambda function to be invoked when the "Add plant" button is clicked.
 * @param modifier A [Modifier] to apply to the composable.
 */
@Composable
private fun EmptyGarden(onAddPlantClick: () -> Unit, modifier: Modifier = Modifier) {
    // Calls reportFullyDrawn when this composable is composed.
    ReportDrawn()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.garden_empty),
            style = MaterialTheme.typography.headlineSmall
        )
        Button(
            shape = MaterialTheme.shapes.medium,
            onClick = onAddPlantClick
        ) {
            Text(
                text = stringResource(id = R.string.add_plant),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

/**
 * Preview for the [GardenScreen] Composable. It uses a [SunflowerTheme] wrapped [GardenScreen]
 * whose `gardenPlants` argument is our [List] of [PlantAndGardenPlantings] parameter [gardenPlants].
 * The [PreviewParameter] annotation that precedes this parameter causes the values supplied by the
 * [GardenScreenPreviewParamProvider] that it specifies as its `provider` to be used for the preview,
 * which in our case is an empty list, and a list of one [PlantAndGardenPlantings].
 *
 * @param gardenPlants a [List] of [PlantAndGardenPlantings] which will be supplied by our custom
 * [PreviewParameterProvider] class [GardenScreenPreviewParamProvider].
 */
@Preview
@Composable
private fun GardenScreenPreview(
    @PreviewParameter(GardenScreenPreviewParamProvider::class) gardenPlants: List<PlantAndGardenPlantings>
) {
    SunflowerTheme {
        GardenScreen(gardenPlants)
    }
}

/**
 * This is a [PreviewParameterProvider] for the [GardenScreenPreview] Composable. It provides
 * a [Sequence] of two [List] of [PlantAndGardenPlantings] to the `gardenPlants` parameter of
 * [GardenScreenPreview]:
 *  - an empty list.
 *  - a list containing a single [PlantAndGardenPlantings] whose [Plant] is named "Apple", and
 *  whose [GardenPlanting] has its `plantDate` and `lastWateringDate` set to the current date.
 */
private class GardenScreenPreviewParamProvider :
    PreviewParameterProvider<List<PlantAndGardenPlantings>> {
    override val values: Sequence<List<PlantAndGardenPlantings>> =
        sequenceOf(
            emptyList(),
            listOf(
                PlantAndGardenPlantings(
                    plant = Plant(
                        plantId = "1",
                        name = "Apple",
                        description = "An apple.",
                        growZoneNumber = 1,
                        wateringInterval = 2,
                        imageUrl = "https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max",
                    ),
                    gardenPlantings = listOf(
                        GardenPlanting(
                            plantId = "1",
                            plantDate = Calendar.getInstance(),
                            lastWateringDate = Calendar.getInstance()
                        )
                    )
                )
            )
        )
}
