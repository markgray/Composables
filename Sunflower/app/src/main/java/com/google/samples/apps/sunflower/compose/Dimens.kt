/*
 * Copyright 2020 Google LLC
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

@file:Suppress("unused")

package com.google.samples.apps.sunflower.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.samples.apps.sunflower.R

/**
 * Class that captures dimens used in Compose code. The dimens that need to be consistent with the
 * View system use [dimensionResource] and are marked as composable.
 *
 * Disclaimer:
 * This approach doesn't consider multiple configurations. For that, an Ambient should be created.
 */
object Dimens {

    /**
     * A small padding value (8.dp). It is used for elements that should have a small amount of
     * spacing between them.
     */
    val PaddingSmall: Dp
        @Composable get() = dimensionResource(id = R.dimen.margin_small)

    /**
     * A standard padding value (16.dp). This is the most commonly used padding size for spacing
     * between UI elements.
     */
    val PaddingNormal: Dp
        @Composable get() = dimensionResource(id = R.dimen.margin_normal)

    /**
     * A large padding value (24.dp). It is used for elements that should have a large amount of
     * spacing between them, or for the main container padding.
     */
    val PaddingLarge: Dp = 24.dp

    /**
     * The height of the app bar on the plant detail screen (278.dp). This is a fixed value used to
     * size the top app bar correctly.
     */
    val PlantDetailAppBarHeight: Dp
        @Composable get() = dimensionResource(id = R.dimen.plant_detail_app_bar_height)

    /**
     * The padding around the icons in the toolbar (12.dp). This is used to ensure that the icons
     * are not too close to the edge of the screen or other UI elements.
     */
    val ToolbarIconPadding: Dp = 12.dp

    /**
     * The size of the icons used in the toolbar (32.dp). This is a fixed value used to ensure
     * that all toolbar icons have a consistent size.
     */
    val ToolbarIconSize: Dp = 32.dp
}
