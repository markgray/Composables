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

package com.example.baselineprofiles_codelab.ui.home

import android.content.res.Configuration
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.Snack
import com.example.baselineprofiles_codelab.ui.components.JetsnackSurface
import com.example.baselineprofiles_codelab.ui.home.cart.Cart
import com.example.baselineprofiles_codelab.ui.home.search.Search
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import java.util.Locale

/**
 * Adds the home navigation graph to the NavGraphBuilder.
 *
 * This [NavGraphBuilder] extension function defines the navigation structure for the main sections
 * of the application, including Feed, Search, Cart, and Profile. Each section is represented by a
 * composable destination within the navigation graph.
 *
 * This adds [composable] routes for all the home screens: [Feed], [Search], [Cart], and [Profile].
 * Our content block just consists of 4 calls to the [NavGraphBuilder.composable] method to add
 * the following routes to our [NavGraphBuilder]:
 *  - `route` = the [HomeSections.route] of [HomeSections.FEED] composes a [Feed] whose `onSnackClick`
 *  lambda argument calls our [onSnackSelected] lambda parameter passing it the [Long] that the
 *  `onSnackClick` lambda is called with and the [NavBackStackEntry] passed the `content` Composable
 *  lambda argument of the [composable] method, and whose [Modifier] `modifier` argument is our
 *  [Modifier] parameter [modifier].
 *  - `route` = the [HomeSections.route] of [HomeSections.SEARCH] composes a [Search] whose
 *  `onSnackClick` lambda argument calls our [onSnackSelected] lambda parameter passing it the
 *  [Long] that the `onSnackClick` lambda is called with and the [NavBackStackEntry] passed the
 *  `content` Composable lambda argument of the [composable] method, and whose [Modifier] `modifier`
 *  argument is our [Modifier] parameter [modifier].
 *  - `route` = the [HomeSections.route] of [HomeSections.CART] composes a [Cart] whose `onSnackClick`
 *  lambda argument calls our [onSnackSelected] lambda parameter passing it the [Long] that the
 *  `onSnackClick` lambda is called with and the [NavBackStackEntry] passed the content Composable
 *  lambda argument of the [composable] method, and whose [Modifier] `modifier` argument is our
 *  [Modifier] parameter [modifier].
 *  - `route` = the [HomeSections.route] of [HomeSections.PROFILE] composes a [Profile] whose
 *  [Modifier] `modifier` argument is our [Modifier] parameter [modifier].
 *
 * @param onSnackSelected A callback function that is invoked when a snack item is selected
 * in the Feed, Search, or Cart sections. It is passed the [Snack.id] of the selected snack and
 * the [NavBackStackEntry] from which the selection occurred.
 * @param modifier The [Modifier] to be applied to the composables in this graph.
 */
fun NavGraphBuilder.addHomeGraph(
    onSnackSelected: (Long, NavBackStackEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    composable(HomeSections.FEED.route) { from: NavBackStackEntry ->
        Feed(onSnackClick = { id: Long -> onSnackSelected(id, from) }, modifier = modifier)
    }
    composable(HomeSections.SEARCH.route) { from: NavBackStackEntry ->
        Search(onSnackClick = { id: Long -> onSnackSelected(id, from) }, modifier = modifier)
    }
    composable(HomeSections.CART.route) { from: NavBackStackEntry ->
        Cart(onSnackClick = { id: Long -> onSnackSelected(id, from) }, modifier = modifier)
    }
    composable(HomeSections.PROFILE.route) {
        Profile(modifier = modifier)
    }
}

/**
 * Represents the different sections or destinations available within the home screen.
 *
 * Each section is defined by:
 *  - A title (represented by a string resource ID).
 *  - An icon (represented by an [ImageVector]).
 *  - A navigation route (represented by a [String]).
 *
 * This enum is typically used to populate a navigation bar or similar UI component,
 * allowing users to easily navigate between different parts of the home screen.
 *
 * @property title The string resource ID for the title of the section. This will be displayed
 * to the user to identify the section.
 * @property icon The [ImageVector] representing the icon for the section. This will be displayed
 * alongside the title in the navigation UI.
 * @property route The navigation route associated with this section. This is used by the
 * navigation system to navigate to the correct destination when the user selects this section.
 */
enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    /**
     * Represents the feed section of the home screen.
     */
    FEED(R.string.home_feed, Icons.Outlined.Home, "home/feed"),

    /**
     * Represents the search section of the home screen.
     */
    SEARCH(R.string.home_search, Icons.Outlined.Search, "home/search"),

    /**
     * Represents the cart section of the home screen.
     */
    CART(R.string.home_cart, Icons.Outlined.ShoppingCart, "home/cart"),

    /**
     * Represents the profile section of the home screen.
     */
    PROFILE(R.string.home_profile, Icons.Outlined.AccountCircle, "home/profile")
}

/**
 * Jetsnack bottom navigation bar with support for the [HomeSections].
 *
 * TODO: Continue here.
 *
 * @param tabs The [Array] of [HomeSections] to be displayed.
 * @param currentRoute The route of the currently selected tab.
 * @param navigateToRoute Callback to trigger navigation to a specific route.
 * @param color The background color of the bottom navigation bar. Defaults to the
 * [JetsnackColors.iconPrimary] of our custom [JetsnackTheme.colors].
 * @param contentColor The color of the icons and text within the bottom navigation bar. Defaults to
 * The [JetsnackColors.iconInteractive] of our custom [JetsnackTheme.colors].
 */
@Composable
fun JetsnackBottomBar(
    tabs: Array<HomeSections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    color: Color = JetsnackTheme.colors.iconPrimary,
    contentColor: Color = JetsnackTheme.colors.iconInteractive
) {
    val routes: List<String> = remember { tabs.map { it.route } }
    val currentSection: HomeSections = tabs.first { it.route == currentRoute }

    JetsnackSurface(
        color = color,
        contentColor = contentColor
    ) {
        val springSpec = SpringSpec<Float>(
            // Determined experimentally
            stiffness = 800f,
            dampingRatio = 0.8f
        )
        JetsnackBottomNavLayout(
            selectedIndex = currentSection.ordinal,
            itemCount = routes.size,
            indicator = { JetsnackBottomNavIndicator() },
            animSpec = springSpec,
            modifier = Modifier.navigationBarsPadding()
        ) {
            val configuration: Configuration = LocalConfiguration.current
            val currentLocale: Locale =
                ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()

            tabs.forEach { section: HomeSections ->
                val selected: Boolean = section == currentSection
                val tint: Color by animateColorAsState(
                    if (selected) {
                        JetsnackTheme.colors.iconInteractive
                    } else {
                        JetsnackTheme.colors.iconInteractiveInactive
                    }
                )

                val text: String = stringResource(section.title).uppercase(currentLocale)

                JetsnackBottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = section.icon,
                            tint = tint,
                            contentDescription = text
                        )
                    },
                    text = {
                        Text(
                            text = text,
                            color = tint,
                            style = MaterialTheme.typography.button,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onSelected = { navigateToRoute(section.route) },
                    animSpec = springSpec,
                    modifier = BottomNavigationItemPadding
                        .clip(BottomNavIndicatorShape)
                )
            }
        }
    }
}

@Composable
private fun JetsnackBottomNavLayout(
    selectedIndex: Int,
    itemCount: Int,
    animSpec: AnimationSpec<Float>,
    indicator: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Track how "selected" each item is [0, 1]
    val selectionFractions: List<Animatable<Float, AnimationVector1D>> = remember(itemCount) {
        List(itemCount) { i: Int ->
            Animatable(if (i == selectedIndex) 1f else 0f)
        }
    }
    selectionFractions.forEachIndexed { index: Int, selectionFraction: Animatable<Float, AnimationVector1D> ->
        val target: Float = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(key1 = target, key2 = animSpec) {
            selectionFraction.animateTo(targetValue = target, animationSpec = animSpec)
        }
    }

    // Animate the position of the indicator
    val indicatorIndex: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }
    val targetIndicatorIndex: Float = selectedIndex.toFloat()
    LaunchedEffect(key1 = targetIndicatorIndex) {
        indicatorIndex.animateTo(targetValue = targetIndicatorIndex, animationSpec = animSpec)
    }

    Layout(
        modifier = modifier.height(height = BottomNavHeight),
        content = {
            content()
            Box(modifier = Modifier.layoutId(layoutId = "indicator"), content = indicator)
        }
    ) { measurables: List<Measurable>, constraints: Constraints ->
        check(itemCount == (measurables.size - 1)) // account for indicator

        // Divide the width into n+1 slots and give the selected item 2 slots
        val unselectedWidth: Int = constraints.maxWidth / (itemCount + 1)
        val selectedWidth: Int = 2 * unselectedWidth
        val indicatorMeasurable: Measurable = measurables.first { it.layoutId == "indicator" }

        val itemPlaceables: List<Placeable> = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index: Int, measurable: Measurable ->
                // Animate item's width based upon the selection amount
                val width: Int = lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
                measurable.measure(
                    constraints.copy(
                        minWidth = width,
                        maxWidth = width
                    )
                )
            }
        val indicatorPlaceable: Placeable = indicatorMeasurable.measure(
            constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth
            )
        )

        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
        ) {
            val indicatorLeft: Float = indicatorIndex.value * unselectedWidth
            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable: Placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
fun JetsnackBottomNavigationItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    selected: Boolean,
    onSelected: () -> Unit,
    animSpec: AnimationSpec<Float>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.selectable(selected = selected, onClick = onSelected),
        contentAlignment = Alignment.Center
    ) {
        // Animate the icon/text positions within the item based on selection
        val animationProgress: Float by animateFloatAsState(if (selected) 1f else 0f, animSpec)
        JetsnackBottomNavItemLayout(
            icon = icon,
            text = text,
            animationProgress = animationProgress
        )
    }
}

@Composable
private fun JetsnackBottomNavItemLayout(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
) {
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .layoutId(layoutId = "icon")
                    .padding(horizontal = TextIconSpacing),
                content = icon
            )
            val scale = lerp(0.6f, 1f, animationProgress)
            Box(
                modifier = Modifier
                    .layoutId(layoutId = "text")
                    .padding(horizontal = TextIconSpacing)
                    .graphicsLayer {
                        alpha = animationProgress
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = BottomNavLabelTransformOrigin
                    },
                content = text
            )
        }
    ) { measurables: List<Measurable>, constraints: Constraints ->
        val iconPlaceable: Placeable = measurables.first { it.layoutId == "icon" }.measure(constraints)
        val textPlaceable: Placeable = measurables.first { it.layoutId == "text" }.measure(constraints)

        placeTextAndIcon(
            textPlaceable = textPlaceable,
            iconPlaceable = iconPlaceable,
            width = constraints.maxWidth,
            height = constraints.maxHeight,
            animationProgress = animationProgress
        )
    }
}

private fun MeasureScope.placeTextAndIcon(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
    val iconY: Int = (height - iconPlaceable.height) / 2
    val textY: Int = (height - textPlaceable.height) / 2

    val textWidth: Float = textPlaceable.width * animationProgress
    val iconX: Float = (width - textWidth - iconPlaceable.width) / 2
    val textX: Float = iconX + iconPlaceable.width

    return layout(width = width, height = height) {
        iconPlaceable.placeRelative(iconX.toInt(), iconY)
        if (animationProgress != 0f) {
            textPlaceable.placeRelative(textX.toInt(), textY)
        }
    }
}

@Composable
private fun JetsnackBottomNavIndicator(
    strokeWidth: Dp = 2.dp,
    color: Color = JetsnackTheme.colors.iconInteractive,
    shape: Shape = BottomNavIndicatorShape
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .then(BottomNavigationItemPadding)
            .border(width = strokeWidth, color = color, shape = shape)
    )
}

private val TextIconSpacing = 2.dp
private val BottomNavHeight = 56.dp
private val BottomNavLabelTransformOrigin = TransformOrigin(0f, 0.5f)
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)
private val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

@Preview
@Composable
private fun JetsnackBottomNavPreview() {
    JetsnackTheme {
        JetsnackBottomBar(
            tabs = HomeSections.entries.toTypedArray(),
            currentRoute = "home/feed",
            navigateToRoute = { }
        )
    }
}
