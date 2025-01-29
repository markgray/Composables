/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.theme.JetnewsShapes
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.theme.JetnewsTypography
import com.example.jetnews.ui.theme.Red300
import com.example.jetnews.ui.theme.Red700

/**
 * This is used as the `drawerContent` argument of the [Scaffold] used by the [JetnewsApp] Composable
 * (content of the Drawer sheet that can be pulled from the left side or right for RTL). Our root
 * Composable is a [Column] whose `modifier` argument is a [Modifier.fillMaxSize] (has its `content`
 * fill the [Constraints.maxWidth] and [Constraints.maxHeight] of the incoming measurement constraints).
 * Its `content` consists of:
 *  - A [Spacer] whose `modifier` argument uses a [Modifier.height] to set its height to 24.dp
 *  - A [JetNewsLogo] whose `modifier` argument uses a [Modifier.padding] to set the padding on all
 *  sides to 16.dp
 *  - A [Divider] whose `color` argument sets its [Color] to a copy of the `onSurface` [Color] of
 *  [MaterialTheme.colors] with its `alpha` set to .2f (Since our [JetnewsTheme] custom [MaterialTheme]
 *  does not specify an `onSurface` [Color] this is the default [Color.Black] for LightThemeColors,
 *  and [Color.White] for DarkThemeColors.
 *  - A [DrawerButton] whose `icon` argument is [Icons.Filled.Home] (a stylized "house"), whose
 *  `label` argument is the [String] "Home", whose `isSelected` argument is `true` if our [String]
 *  parameter [currentRoute] is equal to [MainDestinations.HOME_ROUTE], and whose `action` argument
 *  is a lambda which calls our [navigateToHome] parameter then calls our [closeDrawer] parameter.
 *  - A [DrawerButton] whose `icon` argument is [Icons.AutoMirrored.Filled.ListAlt] (a stylized "list"), whose
 *  `label` argument is the [String] "Interests", whose `isSelected` argument is `true` if our
 *  [String] parameter [currentRoute] is equal to [MainDestinations.INTERESTS_ROUTE], and whose
 *  `action` argument is a lambda which calls our [navigateToInterests] parameter then calls our
 *  [closeDrawer] parameter.
 */
@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToInterests: () -> Unit,
    closeDrawer: () -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(height = 24.dp))
        JetNewsLogo(modifier = Modifier.padding(all = 16.dp))
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
        DrawerButton(
            icon = Icons.Filled.Home,
            label = "Home",
            isSelected = currentRoute == MainDestinations.HOME_ROUTE,
            action = {
                navigateToHome()
                closeDrawer()
            }
        )

        DrawerButton(
            icon = Icons.AutoMirrored.Filled.ListAlt,
            label = "Interests",
            isSelected = currentRoute == MainDestinations.INTERESTS_ROUTE,
            action = {
                navigateToInterests()
                closeDrawer()
            }
        )
    }
}

/**
 * This Composable displays two [Image]'s in a [Row], and is used as the top "line" in the [Column]
 * root Composable of [AppDrawer]. Our root Composable is a [Row] whose `modifier` argument is our
 * [modifier] parameter. The `content` of the [Row] is an [Image] whose `painter` draws the drawable
 * with resource ID `R.drawable.ic_jetnews_logo` (a "greater than" character followed by an underline),
 * with a `colorFilter` argument is a [ColorFilter.tint] whose `color` argument is the `primary` color
 * of [MaterialTheme.colors] ([Red700] for `LightThemeColors` and [Red300] for `DarkThemeColors` is
 * specified by our [JetnewsTheme] custom [MaterialTheme]). This is followed by a [Spacer] whose
 * `width` is 8.dp, and that is followed by a second [Image] whose `painter` draws the drawable
 * with resource ID `R.drawable.ic_jetnews_wordmark` (which is the word "jetnews" written in a fancy
 * font), with a `colorFilter` argument is a [ColorFilter.tint] whose `color` argument is the
 * `onSurface` color of [MaterialTheme.colors] (our [JetnewsTheme] custom [MaterialTheme] does not
 * specify one so the default [Color.Black] is used for `LightThemeColors` and [Color.White] is used
 * for `DarkThemeColors`).
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [AppDrawer] caller uses a [Modifier.padding] that sets the padding on all sides
 * of our [Row] to 16.dp
 */
@Composable
private fun JetNewsLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.ic_jetnews_logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.primary)
        )
        Spacer(modifier = Modifier.width(width = 8.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_jetnews_wordmark),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onSurface)
        )
    }
}

/**
 * This Composable is used for each of the two navigation choices in the [AppDrawer]. It is just a
 * fancy wrapper for a [TextButton] which modifies the alpha and colors based on whether this
 * [DrawerButton] is the selected one ([isSelected] is `true`), surrounds the [TextButton] in a
 * [Surface] which has a `small` [MaterialTheme.shapes] as its `shape` argument (our [JetnewsShapes]
 * specifies a [RoundedCornerShape] with 4.dp corners), and passes arguments to the [TextButton]
 * that reflect the intent of the parameters passed to [DrawerButton]. We initialize our [Colors]
 * variable `val colors` to [MaterialTheme.colors] and initialize our [Float] variable `val imageAlpha`
 * to 1f if our [Boolean] parameter [isSelected] is `true` (`this` [DrawerButton] is the one that
 * is slected) or to 0.6f if [isSelected] is `false`. We set our [Color] variable `val textIconColor`
 * to [Colors.primary] if our [Boolean] parameter [isSelected] is `true`, or to a copy of
 * [Colors.onSurface] with an alpha of 0.6f if [isSelected] is `false`. We set our [Color] variable
 * `val backgroundColor` to a copy of [Colors.primary] with an alpha of 0.12f if our [Boolean]
 * parameter [isSelected] is `true`, or [Color.Transparent] if [isSelected] is `false`. We initialize
 * our [Modifier] variable `val surfaceModifier` to a [Modifier.padding] which sets the padding at
 * each size to 8.dp, and then chain a [Modifier.fillMaxWidth] to it so that the `content` of any
 * Composable will fill  the [Constraints.maxWidth] of the incoming measurement constraints.
 *
 * Having initialized we set our root Composable to a [Surface] with its `modifier` argument our
 * [Modifier] variable `surfaceModifier`, its `color` argument our [Color] variable `backgroundColor`,
 * and its `shape` argument the `small` [Shape] of [MaterialTheme.shapes] which our [JetnewsShapes]
 * custom [Shapes] defines to be a [RoundedCornerShape] with 4.dp corners. The `content` of the
 * [Surface] is a [TextButton] whose `onClick` argument is our [action] lambda parameter, and whose
 * `modifier` argument is a [Modifier.fillMaxWidth] to have it fill  the [Constraints.maxWidth] of
 * its incoming measurement constraints. Its `content` is a [Row] whose `horizontalArrangement`
 * argument is [Arrangement.Start] (places children horizontally such that they are as close as
 * possible to the beginning of the horizontal axis), whose `verticalAlignment` argument is
 * [Alignment.CenterVertically] to have its children centered verically, and whose `modifier`
 * argument is a [Modifier.fillMaxWidth] to have it fill  the [Constraints.maxWidth] of its incoming
 * measurement constraints. The `content` of the [Row] is a an [Image] whose `imageVector` argument
 * is our [ImageVector] parameter [icon] (the [ImageVector] it will draw), whose `contentDescription`
 * is `null`, whose `colorFilter` argument is a [ColorFilter.tint] whose `color` parameter is our
 * [Color] variable `textIconColor` (the tint that the painter should apply when drawing), and whose
 * `alpha` argument is our [Float] variable `imageAlpha` (opacity to be applied to the [ImageVector]
 * when it is rendered onscreen). The [Image] is followed by a [Spacer] whose `modifier` argument
 * uses a [Modifier.width] to set its `width` to 16.dp, and that is followed by a [Text] whose `text`
 * is our [String] parameter [label], whose `style` argument is the `body2` [TextStyle] of
 * [MaterialTheme.typography] which our [JetnewsTypography] custom [Typography] defines to be
 * `Montserrat` [FontFamily] with `fontWeight` = [FontWeight.Medium], `fontSize` = 14.sp, and
 * `letterSpacing` = 0.25.sp (the [Font] with resource ID `R.font.montserrat_medium`). The `color`
 * of the text is our [Color] variable `textIconColor`.
 *
 * @param icon the [ImageVector] that our [Image] Composable should draw.
 * @param label the text that our [Text] Composable should display.
 * @param isSelected if `true` our [DrawerButton] has been selected by the user.
 * @param action a lambda that the [TextButton] should call when the user clicks the button.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller ([AppDrawer]) does not pass us one so the empty, default, or starter
 * [Modifier] that contains no elements (the default value for our [modifier] parameter) is used
 * instead.
 */
@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors: Colors = MaterialTheme.colors
    val imageAlpha: Float = if (isSelected) {
        1f
    } else {
        0.6f
    }
    val textIconColor: Color = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor: Color = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val surfaceModifier: Modifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()
    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = textIconColor),
                    alpha = imageAlpha
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textIconColor
                )
            }
        }
    }
}

/**
 * These are two Previews of a [Surface] holding our [AppDrawer] Composable, all wrapped by our
 * [JetnewsTheme] custom [MaterialTheme]. The `name` of the first is "Drawer contents" and it uses
 * the default `LightThemeColors`, and the `name` of the second is "Drawer contents (dark)" and it
 * uses the [UI_MODE_NIGHT_YES] `uiMode` and it uses the `DarkThemeColors`. The arguments passed to
 * [AppDrawer] are:
 *  - `currentRoute`: [MainDestinations.HOME_ROUTE], the [JetnewsNavGraph] route to load the
 *  [HomeScreen] is passed
 *  - `navigateToHome`: an empty lambda.
 *  - `navigateToInterests`: an empty lambda.
 *  - `closeDrawer`: an empty lambda.
 */
@Preview(name = "Drawer contents")
@Preview(name = "Drawer contents (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    JetnewsTheme {
        Surface {
            AppDrawer(
                currentRoute = MainDestinations.HOME_ROUTE,
                navigateToHome = {},
                navigateToInterests = {},
                closeDrawer = { }
            )
        }
    }
}
