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
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.theme.JetnewsTheme

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
 *  - A [DrawerButton] whose `icon` argument is [Icons.Filled.ListAlt] (a stylized "list"), whose
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
            icon = Icons.Filled.ListAlt,
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
 *
 */
@Composable
private fun JetNewsLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.ic_jetnews_logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
        )
        Spacer(Modifier.width(8.dp))
        Image(
            painter = painterResource(R.drawable.ic_jetnews_wordmark),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
        )
    }
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colors
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }
    val textIconColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val surfaceModifier = modifier
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
                    colorFilter = ColorFilter.tint(textIconColor),
                    alpha = imageAlpha
                )
                Spacer(Modifier.width(16.dp))
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
