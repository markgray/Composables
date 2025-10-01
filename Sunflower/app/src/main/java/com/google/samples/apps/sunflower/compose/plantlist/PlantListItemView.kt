/*
 * Copyright 2022 Google LLC
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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.UnsplashPhoto

/**
 * Composable that displays a single [ImageListItem] displaying the name and the image of the
 * [Plant] parameter [plant].
 *
 * @param plant The plant to display.
 * @param onClick A lambda that will be called when the card is clicked.
 */
@Composable
fun PlantListItem(plant: Plant, onClick: () -> Unit) {
    ImageListItem(name = plant.name, imageUrl = plant.imageUrl, onClick = onClick)
}

/**
 * Composable that displays a single [ImageListItem] displaying the name and the image of the
 * [UnsplashPhoto.user] responsible for the photo.
 *
 * @param photo The photo to display.
 * @param onClick A lambda that will be called when the card is clicked.
 */
@Composable
fun PhotoListItem(photo: UnsplashPhoto, onClick: () -> Unit) {
    ImageListItem(name = photo.user.name, imageUrl = photo.urls.small, onClick = onClick)
}

/**
 * A [Card] that displays an image and a name. Our root composable is a [Card] whose `onClick`
 * argument is our [onClick] lambda parameter, whose `colors` argument is [CardDefaults.cardColors]
 * with its `containerColor` argument set to the [ColorScheme.secondaryContainer] of our custom
 * [MaterialTheme.colorScheme], and whose `modifier` argument is [Modifier.padding] that sets the
 * `horizontal` padding to the value returned by the [dimensionResource] function for the resource
 * ID `R.dimen.card_side_margin` (12.dp) chained to a [Modifier.padding] that sets the `bottom`
 * padding to the value returned by the [dimensionResource] function for the resource ID
 * `R.dimen.card_bottom_margin` (26.dp). In the [ColumnScope] `content` composable lambda argument
 * of the [Card] we compose a [Column] whose `modifier` argument is [Modifier.fillMaxWidth], and
 * in the [ColumnScope] `content` composable lambda argument of the [Column] we compose:
 *
 * **First**, we compose a [GlideImage] whose arguments are:
 *  - `model`: the [String] parameter [imageUrl].
 *  - `contentDescription`: the string resource with ID `R.string.a11y_plant_item_image`
 *  ("Picture of plant")
 *  - `modifier`: a [Modifier.fillMaxWidth] chained to a [Modifier.height] whose `height` is
 *  the value returned by the [dimensionResource] function for the resource ID
 *  `R.dimen.plant_item_image_height` (95.dp).
 *  - `contentScale`: [ContentScale.Crop]
 *
 * **Second**, we compose a [Text] whose arguments are:
 *  - `text`: the [String] parameter [name].
 *  - `textAlign`: [TextAlign.Center]
 *  - `maxLines`: 1
 *  - `style`: the [Typography.titleMedium] of our custom [MaterialTheme.typography]
 *  - `modifier`: a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that sets the
 *  `vertical` padding to the value returned by the [dimensionResource] function for the
 *  resource ID `R.dimen.margin_normal` (16.dp) chained to a [Modifier.wrapContentWidth] whose
 *  `align` argument is [Alignment.CenterHorizontally].
 *
 * @param name The name to display.
 * @param imageUrl The URL of the image to display.
 * @param onClick A lambda that will be called when the card is clicked.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageListItem(name: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.card_side_margin))
            .padding(bottom = dimensionResource(id = R.dimen.card_bottom_margin))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            GlideImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.a11y_plant_item_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = dimensionResource(id = R.dimen.plant_item_image_height)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.margin_normal))
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            )
        }
    }
}