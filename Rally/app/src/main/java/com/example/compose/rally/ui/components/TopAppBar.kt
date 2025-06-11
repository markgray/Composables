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

package com.example.compose.rally.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.rally.RallyScreen
import java.util.Locale

/**
 * A TopAppBar that displays all [RallyScreen]s, and allows the user to navigate between them by
 * selecting a [RallyScreen.name] from one of the [RallyTab] children in its [Row] Composable.
 * The currently selected [RallyScreen] passed in our [currentScreen] argument is highlighted by
 * having its [RallyTab] `selected` argument `true`.
 *
 * Our root composable is a [Surface] whose `modifier` argument is a [Modifier.height] of `56.dp`,
 * chained to a [Modifier.fillMaxWidth]. In its `content` composable lambda argument we compose
 * a [Row] whose `modifier` argument is a [Modifier.selectableGroup]. In its [RowScope] `content`
 * composable lambda argument we use the [Iterable.forEach] method of our [List] of [RallyScreen]
 * parameter [allScreens] to loop over its contents capturing the [RallyScreen] passed the lambda
 * in variable `screen`. We then compose a [RallyTab] for each of our [RallyScreen]s whose arguments
 * are:
 *  - `text`: is the [String] returned by the [RallyScreen.name] property of our [RallyScreen] variable
 *  `screen` converted to uppercase using the [Locale.getDefault] method of [Locale].
 *  - `icon`: is the [ImageVector] returned by the [RallyScreen.icon] property of our [RallyScreen]
 *  variable `screen`.
 *  - `onSelected`: is a lambda that calls our [onTabSelected] lambda parameter with our [RallyScreen]
 *  variable `screen` as its argument.
 *  - `selected`: is `true` if our [RallyScreen] variable `screen` is the same as our [currentScreen]
 *  parameter.
 *
 * @param allScreens the list of all [RallyScreen] that can be navigated to.
 * @param onTabSelected a callback that this Composable will call with the [RallyScreen] that
 * the user has selected. In our case this causes the `rallyApp` Composable to change the value
 * of `currentScreen` which causes us to be recomposed with that [RallyScreen] as our
 * [currentScreen] argument.
 * @param currentScreen the currently selected [RallyScreen].
 */
@Composable
fun RallyTopAppBar(
    allScreens: List<RallyScreen>,
    onTabSelected: (RallyScreen) -> Unit,
    currentScreen: RallyScreen
) {
    Surface(
        modifier = Modifier
            .height(height = TabHeight)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.selectableGroup()) {
            allScreens.forEach { screen: RallyScreen ->
                RallyTab(
                    text = screen.name.uppercase(Locale.getDefault()),
                    icon = screen.icon,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen
                )
            }
        }
    }
}

/**
 * A Composable that displays a Rally tab.
 *
 * We start by initializing our [Color] variable `val color` to the [Colors.onSurface] of our custom
 * [MaterialTheme.colors]. We initialize our [Int] variable `val durationMillis` to either
 * [TabFadeInAnimationDuration] (`150`) if our [selected] parameter is `true`, or to
 * [TabFadeOutAnimationDuration] (`100`) if it is `false`. We initialize and remember our [TweenSpec]
 * of [Color] variable `val animSpec` to a new instance whose `durationMillis` argument is our
 * [Int] variable `durationMillis`, whose `easing` argument is [LinearEasing], and whose
 * `delayMillis` argument is [TabFadeInAnimationDelay] (`100`). We initialize our animated [Color]
 * variable `val tabTintColor` to the value returned by the [animateColorAsState] method whose
 * `targetValue` argument is `color` if our [selected] parameter is `true` or a copy of `color`
 * whose `alpha` argument is [InactiveTabOpacity] (`0.60f`) if it is `false`, and the `animationSpec`
 * argument is our [TweenSpec] variable `animSpec`.
 *
 * Then our root composable is a [Row] whose `modifier` argument is a [Modifier.padding] that adds
 * `16.dp` padding to all sides, chained to a [Modifier.animateContentSize], chained to a
 * [Modifier.height] whose `height` argument is [TabHeight] (`56.dp`), chained to a
 * [Modifier.selectable] whose `selected` argument is our [Boolean] parameter [selected], whose
 * `onClick` argument is our [onSelected] lambda parameter, whose `role` argument is [Role.Tab],
 * whose `interactionSource` argument is a rmembered new instance of [MutableInteractionSource],
 * and whose `indication` argument is a [ripple] whose `bounded` argument is `false`, whose
 * `radius` argument is [Dp.Unspecified], and whose `color` argument is [Color.Unspecified].
 *
 * In the [RowScope] `content` composable lambda argument of the [Row] we first compose an [Icon]
 * whose arguments are:
 *  - `imageVector`: is our [ImageVector] parameter [icon].
 *  - `contentDescription`: is our [String] parameter [text].
 *  - `tint`: is our [Color] variable `tabTintColor`.
 *
 * If our [Boolean] parameter [selected] is `true` we compose a [Spacer] whose `modifier` argument
 * is a [Modifier.width] whose `width` argument is `12.dp`, and we compose a [Text] whose arguments
 * are:
 *  - `text`: is our [String] parameter [text].
 *  - `color`: is our [Color] variable `tabTintColor`.
 *  - `modifier`: is a [Modifier.clearAndSetSemantics] that clears any semantics applied to the [Text].
 *
 * @param text The text to display on the tab.
 * @param icon The icon to display on the tab.
 * @param onSelected A callback that is invoked when the tab is selected.
 * @param selected Whether the tab is currently selected.
 */
@Composable
private fun RallyTab(
    text: String,
    icon: ImageVector,
    onSelected: () -> Unit,
    selected: Boolean
) {
    val color: Color = MaterialTheme.colors.onSurface
    val durationMillis: Int =
        if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpec: TweenSpec<Color> = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = TabFadeInAnimationDelay
        )
    }
    val tabTintColor: Color by animateColorAsState(
        targetValue = if (selected) color else color.copy(alpha = InactiveTabOpacity),
        animationSpec = animSpec
    )
    Row(
        modifier = Modifier
            .padding(all = 16.dp)
            .animateContentSize()
            .height(height = TabHeight)
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )

            )
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = tabTintColor)
        if (selected) {
            Spacer(modifier = Modifier.width(width = 12.dp))
            Text(text = text, color = tabTintColor, modifier = Modifier.clearAndSetSemantics {})
        }
    }
}

/**
 * Height of the [RallyTopAppBar] and the [RallyTab]s it contains.
 */
private val TabHeight = 56.dp

/**
 * How opaque an inactive tab is.
 */
private const val InactiveTabOpacity = 0.60f

/**
 * How long it takes for a tab to fade in, in milliseconds.
 */
private const val TabFadeInAnimationDuration = 150

/**
 * Delay for the fade in animation of a tab.
 */
private const val TabFadeInAnimationDelay = 100

/**
 * How long it takes for a tab to fade out, in milliseconds.
 */
private const val TabFadeOutAnimationDuration = 100
