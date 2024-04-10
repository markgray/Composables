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

package com.example.jetnews.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.example.jetnews.R
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.ui.interests.InterestsRoute
import com.example.jetnews.ui.theme.JetnewsTheme

/**
 * This is used as the `drawerContent` argument of the [ModalNavigationDrawer] used in the [JetnewsApp]
 * composable. Its root Composable is a [ModalDrawerSheet] whose `modifier` argument is our [Modifier]
 * parameter [modifier], and whose `content` lambda argument holds;
 *  - a [JetNewsLogo] whose `modifier` argument is a [Modifier.padding] that adds 28.sp to each side and
 *  24.dp to its top and bottom.
 *  - a [NavigationDrawerItem] whose `label` argument is a lambda which renders a [Text] whose `text`
 *  is the [String] with resource ID [R.string.home_title] ("Home"), whose `icon` argument is a
 *  lambda which renders an [Icon] whose `imageVector` argument causes it to display the [ImageVector]
 *  drawn by [Icons.Filled.Home] (a stylized house), whose `selected` argument is `true` if our [String]
 *  parameter [currentRoute] is equal to [JetnewsDestinations.HOME_ROUTE] ("home"), whose `onClick`
 *  argument is a lambda which calls our [navigateToHome] lambda parameter, then calls our [closeDrawer]
 *  lambda parameter, and its `modifier` argument is a [Modifier.padding] whose `paddingValues` argument
 *  is the [PaddingValues] constant [NavigationDrawerItemDefaults.ItemPadding] (adds 12.dp to each
 *  side).
 *  - a [NavigationDrawerItem] whose `label` argument is a lambda which renders a [Text] whose `text`
 *  is the [String] with resource ID [R.string.interests_title] ("Interests"), whose `icon` argument
 *  is a lambda which renders an [Icon] whose `imageVector` argument causes it to display the
 *  [ImageVector] drawn by [Icons.AutoMirrored.Filled.ListAlt] (a stylized list), whose selected
 *  argument is true if our [String] parameter [currentRoute] is equal to [JetnewsDestinations.INTERESTS_ROUTE]
 *  ("interests"), whose `onClick` argument is a lambda which calls our [navigateToInterests] lambda
 *  parameter, then calls our [closeDrawer] lambda parameter, and its `modifier` argument is a
 *  [Modifier.padding] whose `paddingValues` argument is the [PaddingValues] constant
 *  [NavigationDrawerItemDefaults.ItemPadding] (adds 12.dp to each side).
 *
 * @param currentRoute this is the current [NavDestination.route] of the [NavBackStackEntry] returned
 * by the [NavHostController] method `currentBackStackEntryAsState`, defaulting to
 * [JetnewsDestinations.HOME_ROUTE] ("home") if `null`.
 * @param navigateToHome a lambda we should call if we want to navigate to [HomeRoute] whose `route`
 * is [JetnewsDestinations.HOME_ROUTE]
 * @param navigateToInterests a lambda we should call if we want to navigate to [InterestsRoute] whose
 * `route` is [JetnewsDestinations.INTERESTS_ROUTE]
 * @param closeDrawer a lambda we should call if we want to close the [ModalNavigationDrawer] that
 * we are in.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToInterests: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        JetNewsLogo(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(id = R.string.home_title)) },
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = null) },
            selected = currentRoute == JetnewsDestinations.HOME_ROUTE,
            onClick = { navigateToHome(); closeDrawer() },
            modifier = Modifier.padding(paddingValues = NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(id = R.string.interests_title)) },
            icon = { Icon(
                imageVector = Icons.AutoMirrored.Filled.ListAlt,
                contentDescription = null
            ) },
            selected = currentRoute == JetnewsDestinations.INTERESTS_ROUTE,
            onClick = { navigateToInterests(); closeDrawer() },
            modifier = Modifier.padding(paddingValues = NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

/**
 * This is our app logo, which is used as the top line of the [ModalDrawerSheet] used by our
 * [AppDrawer] Composable. Our root Composable is a [Row] whose `modifier` argument is our [Modifier]
 * parameter [modifier]. The `content` of the [Row] consists of an [Icon] which displays the `vector`
 * drawn by the drawable with resource ID [R.drawable.ic_jetnews_logo] (a greater than character ">"
 * followed by an underline character "_" in a "box"). The `tint` argument of the [Icon] is the
 * [ColorScheme.primary] of our custom [MaterialTheme.colorScheme] (a red in light mode and a pink
 * in dark mode if [Build.VERSION.SDK_INT] is less than [Build.VERSION_CODES.S]). The [Icon] is
 * followed by a [Spacer] whose `modifier` argument is a [Modifier.width] which sets its `width` to
 * 8.dp and this is followed by another [Icon] whose `painter` argument causes it to display the
 * `vector` drawn by the drawable with resource ID [R.drawable.ic_jetnews_wordmark] (the word
 * "jetnews" drawn in a modernistic sans-serif font), the `contentDescription` argument of the
 * [Icon] is the [String] with resource ID [R.string.app_name] ("Jetnews"), and the `tint` argument
 * is the [ColorScheme.onSurfaceVariant] of our custom [MaterialTheme.colorScheme] (a black in light
 * mode and a light pink in dark mode if [Build.VERSION.SDK_INT] is less than [Build.VERSION_CODES.S]).
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [AppDrawer] passes us a [Modifier.padding] that adds 28.dp padding to each
 * end, and 24.dp to our top and bottom.
 */
@Composable
private fun JetNewsLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_jetnews_logo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(width = 8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_jetnews_wordmark),
            contentDescription = stringResource(id = R.string.app_name),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Previews of our [AppDrawer] Composable, one in light mode, and one in dark mode.
 */
@Preview(name = "Drawer contents")
@Preview(name = "Drawer contents (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    JetnewsTheme {
        AppDrawer(
            currentRoute = JetnewsDestinations.HOME_ROUTE,
            navigateToHome = {},
            navigateToInterests = {},
            closeDrawer = { }
        )
    }
}
