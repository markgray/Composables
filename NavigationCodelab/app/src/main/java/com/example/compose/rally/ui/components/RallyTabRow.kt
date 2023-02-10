/*
 * Copyright 2022 The Android Open Source Project
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
import com.example.compose.rally.RallyDestination
import com.example.compose.rally.RallyApp
import java.util.Locale

/**
 * This Composable is used as the `topBar` argument of the [Scaffold] used by [RallyApp]. It contains
 * a [Row] of [RallyTab]'s and when one of them is selected it will navigate to the [RallyDestination]
 * route associated with the [RallyTab]. Our root Composable is a [Surface] whose `modifier` argument
 * is a [Modifier.height] whose `height` argument sets the height of the [Surface] to [TabHeight]
 * (56.dp), and a [Modifier.fillMaxWidth] is chained to that to have the [Surface] fill the entire
 * incoming horizontal constraint. The `content` of the [Surface] is a [Row] whose `modifier` argument
 * is a [Modifier.selectableGroup] which groups its list of selectable items together for accessibility
 * purposes). For its `content` we loop over all of the [RallyDestination]'s in our [List] of
 * [RallyDestination] parameter [allScreens] composing a [RallyTab] whose `text` argument is the
 * [RallyDestination.route] property of the [RallyDestination], whose `icon` argument is the
 * [RallyDestination.icon] property of the [RallyDestination], whose `onSelected` argument is a
 * lambda which calls our [onTabSelected] lambda parameter with the [RallyDestination], and whose
 * `selected` argument is `true` if our [currentScreen] parameter is equal to the [RallyDestination].
 *
 * @param allScreens a [List] of [RallyDestination]'s, each of which a [RallyTab] in our [Row] will
 * be associated with when they are composed.
 * @param onTabSelected a lambda which takes a [RallyDestination], it will be called when the
 * [RallyDestination] of one of the [RallyTab]'s is the selected [currentScreen] (it is used as
 * the `onSelected` argument of the [RallyTab] and called with the [RallyDestination] associated
 * with the [RallyTab] when it is clicked).
 * @param currentScreen the currently selected [RallyDestination].
 */
@Composable
fun RallyTabRow(
    allScreens: List<RallyDestination>,
    onTabSelected: (RallyDestination) -> Unit,
    currentScreen: RallyDestination
) {
    Surface(
        modifier = Modifier
            .height(height = TabHeight)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.selectableGroup()) {
            allScreens.forEach { screen: RallyDestination ->
                RallyTab(
                    text = screen.route,
                    icon = screen.icon,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen
                )
            }
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
private fun RallyTab(
    text: String,
    icon: ImageVector,
    onSelected: () -> Unit,
    selected: Boolean
) {
    val color = MaterialTheme.colors.onSurface
    val durationMillis = if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpec = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = TabFadeInAnimationDelay
        )
    }
    val tabTintColor by animateColorAsState(
        targetValue = if (selected) color else color.copy(alpha = InactiveTabOpacity),
        animationSpec = animSpec
    )
    Row(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize()
            .height(TabHeight)
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
        Icon(imageVector = icon, contentDescription = text, tint = tabTintColor)
        if (selected) {
            Spacer(Modifier.width(12.dp))
            Text(text.uppercase(Locale.getDefault()), color = tabTintColor)
        }
    }
}

private val TabHeight = 56.dp
private const val InactiveTabOpacity = 0.60f

private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100
