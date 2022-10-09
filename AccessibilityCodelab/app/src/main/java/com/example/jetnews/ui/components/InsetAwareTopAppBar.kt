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

package com.example.jetnews.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Colors
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.theme.Red700

/**
 * A wrapper around [TopAppBar] which uses [Modifier.statusBarsPadding] to shift the app bar's
 * contents down, but still draws the background behind the status bar too. Our root Composable is
 * a [Surface] whose `color` argument is our [Color] parameter [backgroundColor] (our users do not
 * pass us one so this is the default `primarySurface` [Color] of [MaterialTheme.colors] and which
 * is the `primary` [Color] in light mode ([Red700] in [JetnewsTheme]) or the `surface` [Color] in
 * dark mode ([JetnewsTheme] does not specify one, so the default [Color.White] of [MaterialTheme.colors]
 * is used), whose `elevation` argument is our [elevation] parameter (our callers do not pass one so
 * the default 4.dp is used), and whose `modifier` argument is our [modifier] parameter (our callers
 * do not pass one so the the empty, default, or starter [Modifier] that contains no elements is used).
 * The `content` of the [Surface] is a [TopAppBar] Composable whose `title` argument is our [title]
 * parameter, whose `navigationIcon` argument is our [navigationIcon] parameter, whose `actions`
 * argument is our [actions] parameter, whose `backgroundColor` argument is [Color.Transparent], whose
 * `contentColor` is our [contentColor] parameter, whose `elevation` argument is 0.dp, and whose
 * `modifier` argument is a [Modifier.statusBarsPadding] (adds padding to accommodate the status
 * bars insets).
 *
 * @param title The `title` to be displayed in the center of the [TopAppBar]
 * @param modifier a [Modifier] instance that our callers can use to modify our appearance and/or
 * behavior. Our callers do not pass us one so the the empty, default, or starter Modifier that
 * contains no elements is used.
 * @param navigationIcon The navigation icon displayed at the start of the [TopAppBar]. Our three
 * callers all pass an [IconButton] whose `onClick` opens the drawer of the [Scaffold] we are the
 * `topBar` argument of.
 * @param actions The actions displayed at the end of the [TopAppBar]. None of our callers pass us
 * one so our default "do nothing" lambda is used instead.
 * @param backgroundColor the `color` argument (background color) of our root [Surface] Composable.
 * Our callers do not pass us one so we use the default `primarySurface` [Color] of [MaterialTheme.colors]
 * which is the `primary` [Color] in light mode ([Red700] in [JetnewsTheme]) or the `surface` [Color] in
 * dark mode ([JetnewsTheme] does not specify one, so the default [Color.White] of [MaterialTheme.colors]
 * is used)
 * @param contentColor preferred content color provided by [TopAppBar] to its children. Our callers
 * do not pass us one so we use the default of the [Color] that the [contentColorFor] method returns
 * when passed our [backgroundColor] parameter. The [contentColorFor] function tries to match the
 * provided `backgroundColor` to a 'background' color in [MaterialTheme.colors], and then will return
 * the corresponding color used for content. For example, when `backgroundColor` is the `primary`
 * [Color] of [Colors], this will return the `onPrimary` [Color] of [Colors].
 * @param elevation the `elevation` argument of our [Surface], which is the size of the shadow below
 * the surface. Our callers do not pass us one so we use the default of 4.dp
 */
@Composable
fun InsetAwareTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 4.dp
) {
    Surface(
        color = backgroundColor,
        elevation = elevation,
        modifier = modifier
    ) {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp,
            modifier = Modifier.statusBarsPadding()
        )
    }
}
