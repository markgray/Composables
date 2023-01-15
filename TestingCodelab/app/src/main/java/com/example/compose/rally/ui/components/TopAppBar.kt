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

package com.example.compose.rally.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.rally.RallyScreen
import com.example.compose.rally.RallyApp
import com.example.compose.rally.RallyActivity
import com.example.compose.rally.ui.theme.RallyTheme
import java.util.Locale

/**
 * This is used as the `topBar` argument (top app bar of the screen) of the [Scaffold] used in the
 * [RallyApp] Composable that [RallyActivity] sets as the apps content in its `onCreate` override.
 * Our root Composable is a [Surface] whose `modifier` argument is a [Modifier.height] that set the
 * preferred height of its content to be exactly [TabHeight] (56.dp) to which is chained a
 * [Modifier.fillMaxWidth] which causes it to use the entire incoming width constraint. Its `content`
 * consists of a [Row] whose `modifier` argument is a [Modifier.selectableGroup] which groups its
 * list of selectable [RallyTab] instances together for accessibility purposes. It uses the [forEach]
 * extension method on our [List] of [RallyScreen] parameter [allScreens] to compose a [RallyTab]
 * for each [RallyScreen] whose `text` argument is the [RallyScreen.name] property, whose `icon`
 * argument is the [RallyScreen.icon] property, whose `onSelected` argument is a lambda that calls
 * our [onTabSelected] parameter with the [RallyScreen] as its argument, and whose `selected` argument
 * is `true` iff the [RallyScreen] `screen` that the [RallyTab] is to contain is equal to our
 * [RallyScreen] parameter [currentScreen].
 *
 * @param allScreens a list of the all of the [RallyScreen.values] defined for the app. These are
 * [RallyScreen.Overview], [RallyScreen.Accounts], and [RallyScreen.Bills].
 * @param onTabSelected a lambda that we use as the `onSelected` argument of all of the [RallyTab]
 * Composables in our [RallyTopAppBar]. The [Scaffold] in [RallyApp] passes us a lambda which sets
 * its [RallyScreen] variable `currentScreen` to the argument passed to the lambda (making that
 * [RallyScreen] the currently selected tab.
 * @param currentScreen the currently selected [RallyScreen] tab. The [Scaffold] in [RallyApp] passes
 * us its [RallyScreen] variable `currentScreen`.
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
                    text = screen.name,
                    icon = screen.icon,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen
                )
            }
        }
    }
}

/**
 * This Composable is a selectable Tab that is used by [RallyTopAppBar] for each of the [RallyScreen]
 * enum values. We start by initializing our [Color] variable `val color` to the [Colors.onSurface]
 * color of [MaterialTheme.colors] (our [RallyTheme] custom [MaterialTheme] defines this to be
 * [Color.White]). We initialize our [Int] variable `val durationMillis` to [TabFadeInAnimationDuration]
 * (150ms) if [selected] is `true`, or to [TabFadeOutAnimationDuration] (100ms) if it is `false`. We
 * initialize and remember our [TweenSpec] of [Color] variable `val animSpec` using the [tween] method
 * with its `durationMillis` argument our durationMillis` variable, its `easing` argument [LinearEasing]
 * (It returns fraction unmodified which is useful as a default value for cases where a Easing is
 * required but no actual easing is desired), and its `delayMillis` argument [TabFadeInAnimationDelay]
 * (100ms). We initialize our [Color] variable `val tabTintColor` by [animateColorAsState] with its
 * `targetValue` if [selected] is `true` our [Color] variable `color` and if it is `false` a copy of
 * `color` with its `alpha` argument [InactiveTabOpacity] (0.60f)
 *
 * Our root Composable is then a [Row] whose `modifier` argument is a [Modifier.padding] that sets
 * the padding on all sides of the [Row] to 16.dp, to which is chained a [Modifier.animateContentSize]
 * (which causes the [Modifier] to animate its own size when its child modifier changes size). Next
 * in the chain is a [Modifier.height] that sets the [Row]'s `height` to [TabHeight] (56.dp) followed
 * by a [Modifier.selectable]
 *
 * @param text used as the title of this [RallyTab], it comes from the [RallyScreen.name] property
 * of the [RallyScreen] we are supposed to represent.
 * @param icon [ImageVector] that is used as the `imageVector` argument of our [Icon], it comes from
 * the [RallyScreen.icon] property of the [RallyScreen] we are supposed to represent.
 * @param onSelected a lambda that takes a [RallyScreen] as its argument.
 * @param selected `true` if this [RallyTab] is the currently selected one.
 */
@Composable
private fun RallyTab(
    text: String,
    icon: ImageVector,
    onSelected: () -> Unit,
    selected: Boolean
) {
    val color: Color = MaterialTheme.colors.onSurface
    val durationMillis: Int = if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpec: TweenSpec<Color> = remember {
        tween(
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
                indication = rememberRipple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )
            )
            .clearAndSetSemantics { contentDescription = text }
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tabTintColor)
        if (selected) {
            Spacer(Modifier.width(width = 12.dp))
            Text(text = text.uppercase(locale = Locale.getDefault()), color = tabTintColor)
        }
    }
}

/**
 * TODO: Add kdoc
 */
private val TabHeight = 56.dp

/**
 * TODO: Add kdoc
 */
private const val InactiveTabOpacity = 0.60f

/**
 * TODO: Add kdoc
 */
private const val TabFadeInAnimationDuration = 150

/**
 * TODO: Add kdoc
 */
private const val TabFadeInAnimationDelay = 100

/**
 * TODO: Add kdoc
 */
private const val TabFadeOutAnimationDuration = 100
