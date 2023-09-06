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

@file:Suppress("PrivatePropertyName")

package androidx.compose.samples.crane.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 * [Color] which is used by the `ExploreItem` Composable (file: base/ExploreSection.kt) for the [Text]
 * displaying the [ExploreModel.description] field of its `item` parameter and by the `ExploreSection`
 * Composable (same file) for the [Text] displaying its `title` parameter.
 * and
 */
val crane_caption: Color = Color.DarkGray

/**
 * [Color] used by the `ExploreList` Composable (file: base/ExploreSection.kt) for the [Divider] it
 * places between the `ExploreItem` items in the [LazyColumn] it uses to display the [ExploreModel]
 * objects in its [List] of [ExploreModel] parameter `exploreList`
 */
val crane_divider_color: Color = Color.LightGray

/**
 * Used as the `secondary` [Color] of the [lightColors] in our [Colors] field [craneColors].
 */
private val crane_red = Color(0xFFE30425)

/**
 * Used as the `onSurface` [Color] of the [lightColors] in our [Colors] field [craneColors].
 */
private val crane_white = Color.White

/**
 * Used as the `primaryVariant` [Color] of the [lightColors] in our [Colors] field [craneColors].
 */
private val crane_purple_700 = Color(0xFF720D5D)

/**
 * Used as the `primary` [Color] of the [lightColors] in our [Colors] field [craneColors].
 */
private val crane_purple_800 = Color(0xFF5D1049)

/**
 * Used as the `surface` [Color] of the [lightColors] in our [Colors] field [craneColors].
 */
private val crane_purple_900 = Color(0xFF4E0D3A)

/**
 * These are the custom colors used by our [CraneTheme] custom [MaterialTheme]. It is used as the
 * `colors` argument of [MaterialTheme] ("A complete definition of the Material Color theme for this
 * hierarchy").
 *  - primary: [crane_purple_800],
 *  - primaryVariant: [crane_purple_700],
 *  - secondary: [crane_red],
 *  - secondaryVariant: Defaults to Color(0xFF018786) a turquoise shade
 *  - background: Defaults to [Color.White]
 *  - surface: [crane_purple_900],
 *  - error: Defaults to Color(0xFFB00020) a dark red,
 *  - onPrimary: Defaults to [Color.White],
 *  - onSecondary: Defaults to [Color.Black],
 *  - onBackground: Defaults to [Color.Black],
 *  - onSurface: [crane_white],
 *  - onError: Defaults to [Color.White]
 */
val craneColors: Colors = lightColors(
    primary = crane_purple_800,
    secondary = crane_red,
    surface = crane_purple_900,
    onSurface = crane_white,
    primaryVariant = crane_purple_700
)

/**
 * Used as the `shape` argument of the [Surface] of the `ExploreSection` Composable (file:
 * base/ExploreSection.kt). The top two corners are rounded by 20.dp, and the bottom two
 * corners are not rounded.
 */
val BottomSheetShape: RoundedCornerShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/**
 * Our custom [MaterialTheme]. We pass [MaterialTheme] the arguments:
 *  - `colors` = [craneColors] - A complete definition of the Material Color theme for this
 *  hierarchy)
 *  - `typography` = [craneTypography] - A set of [TextStyle]s to be used as this hierarchy's
 *  typography system.
 *
 * And we leave its `shapes` argument to its default value.
 *
 * @param content the Composable block that we are supplying Material Theming to.
 */
@Composable
fun CraneTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = craneColors, typography = craneTypography) {
        content()
    }
}
