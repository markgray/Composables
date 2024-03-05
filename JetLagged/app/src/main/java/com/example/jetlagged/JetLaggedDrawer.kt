/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetlagged

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This is composed into the activity as the root view of [MainActivity]. Our root Composable is a
 * [Surface] whose `modifier` argument is a [Modifier.fillMaxSize] causing it to occupy the entire
 * incoming size constraints. Inside the `content` of the [Surface] we initialize and remember our
 * [MutableState] wrapped [DrawerState] variable `var drawerState` to [DrawerState.Closed], initialize
 * and remember our [MutableState] wrapped [Screen] variable `var screenState` to [Screen.Home],
 */
@Composable
fun HomeScreenDrawer() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        /**
         * Controls whether the [HomeScreenDrawerContents] is "Open" or "Closed". It does this by
         * controlling the `translationX` of the [Modifier.graphicsLayer] draw layer holding the
         * [ScreenContents] Composable which is rendered on top of [HomeScreenDrawerContents] with
         * the `translationX` animated to 0 to "Close" the drawer, or to [DrawerWidth] to open the
         * drawer.
         */
        var drawerState: DrawerState by remember {
            mutableStateOf(value = DrawerState.Closed)
        }

        /**
         * Controls which of the  "destination" Composables are displayed in the [ScreenContents]
         * Composable:
         *  - [Screen.Home] displays the [JetLaggedScreen] Composable.
         *  - [Screen.SleepDetails] displays an empty [Surface] ("work in progress")
         *  - [Screen.Leaderboard] displays an empty [Surface] ("work in progress")
         *  - [Screen.Settings] displays an empty [Surface] ("work in progress")
         */
        var screenState: Screen by remember {
            mutableStateOf(value = Screen.Home)
        }

        /**
         * This controls the `translationX` of the [Modifier.graphicsLayer] draw layer holding the
         * [ScreenContents] Composable and is used to "Open" or "Close" the [HomeScreenDrawerContents]
         * that is rendered underneath the [ScreenContents] Composable.
         */
        val translationX: Animatable<Float, AnimationVector1D> = remember {
            Animatable(initialValue = 0f)
        }

        /**
         * This is the pixel value of the [Dp] constant [DrawerWidth] and is the number of pixels
         * that the `translationX` of the [Modifier.graphicsLayer] draw layer holding the
         * [ScreenContents] Composable must be moved in order for the [HomeScreenDrawerContents]
         * (that is drawn under the [ScreenContents] Composable) to be fully "Open"
         */
        val drawerWidth: Float = with(LocalDensity.current) {
            DrawerWidth.toPx()
        }
        translationX.updateBounds(lowerBound = 0f, upperBound = drawerWidth)

        /**
         * This is the [CoroutineScope] that is used to launch coroutines that use our [Animatable]
         * variable `translationX` to animate the [GraphicsLayerScope.translationX] of the
         * [Modifier.graphicsLayer] that holds our [ScreenContents] Composable, thereby "opening"
         * and "closing" the drawer since the [HomeScreenDrawerContents] drawer is below the
         * [ScreenContents].
         */
        val coroutineScope: CoroutineScope = rememberCoroutineScope()

        /**
         * Toggles the [DrawerState] of our [MutableState] wrapped [DrawerState] variable `drawerState`
         * between [DrawerState.Open] and [DrawerState.Closed] and launches a coroutine that uses the
         * [Animatable] variable `translationX` to animate the [GraphicsLayerScope.translationX] of
         * the [Modifier.graphicsLayer] that holds our [ScreenContents] Composable to the value that
         * is appropriate for the new [DrawerState] (0f for transitioning from [DrawerState.Open] to
         * [DrawerState.Closed] and `drawerWidth` for transitioning from [DrawerState.Closed] to
         * [DrawerState.Open]
         */
        fun toggleDrawerState() {
            coroutineScope.launch {
                if (drawerState == DrawerState.Open) {
                    translationX.animateTo(targetValue = 0f)
                } else {
                    translationX.animateTo(targetValue = drawerWidth)
                }
                drawerState = if (drawerState == DrawerState.Open) {
                    DrawerState.Closed
                } else {
                    DrawerState.Open
                }
            }
        }

        /**
         * This is the "Navigation Drawer" of our app. It is drawn under the [ScreenContents] Composable
         * and controls which [Screen] is displayed by the [ScreenContents] Composable.
         *  - `selectedScreen` our [Screen] variable `screenState`
         *  - `onScreenSelected` a lambda which sets our [Screen] variable `screenState` to the
         *  [Screen] paramter passed it.
         */
        HomeScreenDrawerContents(
            selectedScreen = screenState,
            onScreenSelected = { screen: Screen ->
                screenState = screen
            }
        )

        /**
         * This [DraggableState] is used as the `state` argument of the [Modifier.draggable] that is
         * part of the `modifier` argument of our [ScreenContents] Composable. It is the State of the
         * draggable, and allows for a granular control of how deltas are consumed by the user as
         * well as allowing one to write custom drag methods using drag suspend function. The `onDelta`
         * lambda passed to [rememberDraggableState] will be invoked whenever drag happens (by gesture
         * input or a custom [DraggableState.drag] call) with the delta in pixels. In our lambda we
         * use our [CoroutineScope] variable `coroutineScope` to launch a coroutine which calls the
         * [Animatable.snapTo] method of `translationX` to have it set its current value to  its
         * current value plus the `dragAmount` parameter of the lambda without any animation (thereby
         * causing the [GraphicsLayerScope.translationX] of the [Modifier.graphicsLayer] that holds
         * our [ScreenContents] Composable to reflect the current drag position).
         */
        val draggableState: DraggableState = rememberDraggableState(onDelta = { dragAmount: Float ->
            coroutineScope.launch {
                translationX.snapTo(targetValue = translationX.value + dragAmount)
            }
        })

        /**
         * This is used as the `animationSpec` of a call to the [Animatable.animateDecay] method of
         * the [Animatable] variable `translationX` which is called in the `onDragStopped` lambda
         * argument of the [Modifier.draggable] when the drag velocity and current value of
         * `translationX` is enough for it to reach the taget value, and starts a decay animation
         * (i.e. an animation that slows down from the given `initialVelocity` starting at current
         * [Animatable.value] until the velocity reaches 0.
         */
        val decay: DecayAnimationSpec<Float> = rememberSplineBasedDecay()

        /**
         * This Composable draws whichever [Screen] is selected by its `selectedScreen` argument
         * into a [Modifier.graphicsLayer] draw layer. The [GraphicsLayerScope.translationX] of that
         * [Modifier.graphicsLayer] draw layer is animated by the [Animatable] variable `translationX`
         * allowing it to be moved to the right to expose the [HomeScreenDrawerContents] that is
         * rendered underneath it either by dragging or by calling the `toggleDrawerState` method.
         * The arguments are:
         *  - `selectedScreen` our [MutableState] wrapped [Screen] variable `screenState`
         *  - `onDrawerClicked` a reference to our `toggleDrawerState` method which "Toggles"
         *  `drawerState` between [DrawerState.Open] and [DrawerState.Closed] and animates the
         *  [Animatable] variable `translationX` appropriately.
         *  - `modifier` a [Modifier.graphicsLayer] that makes the `content` of [ScreenContents]
         *  draw into a draw layer whose `translationX` can be animated using the [Animatable]
         *  variable `translationX`. To this is chained a [Modifier.draggable] that allows the user
         *  to drag the draw layer of the [Modifier.graphicsLayer].
         */
        ScreenContents(
            selectedScreen = screenState,
            onDrawerClicked = ::toggleDrawerState,
            modifier = Modifier
                .graphicsLayer {
                    this.translationX = translationX.value
                    val scale: Float = lerp(start = 1f, stop = 0.8f, fraction = translationX.value / drawerWidth)
                    this.scaleX = scale
                    this.scaleY = scale
                    val roundedCorners: Float = lerp(start = 0f, stop = 32.dp.toPx(), fraction = translationX.value / drawerWidth)
                    this.shape = RoundedCornerShape(size = roundedCorners)
                    this.clip = true
                    this.shadowElevation = 32f
                }
                // This example is showing how to use draggable with custom logic on stop to snap to the edges
                // You can also use `anchoredDraggable()` to set up anchors and not need to worry about more calculations.
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        val targetOffsetX: Float = decay.calculateTargetValue(
                            translationX.value,
                            velocity
                        )
                        coroutineScope.launch {
                            val actualTargetX: Float = if (targetOffsetX > drawerWidth * 0.5) {
                                drawerWidth
                            } else {
                                0f
                            }
                            // checking if the difference between the target and actual is + or -
                            val targetDifference: Float = (actualTargetX - targetOffsetX)
                            val canReachTargetWithDecay: Boolean =
                                (
                                    targetOffsetX > actualTargetX && velocity > 0f &&
                                        targetDifference > 0f
                                    ) ||
                                    (
                                        targetOffsetX < actualTargetX && velocity < 0 &&
                                            targetDifference < 0f
                                        )
                            if (canReachTargetWithDecay) {
                                translationX.animateDecay(
                                    initialVelocity = velocity,
                                    animationSpec = decay
                                )
                            } else {
                                translationX.animateTo(targetValue = actualTargetX, initialVelocity = velocity)
                            }
                            drawerState = if (actualTargetX == drawerWidth) {
                                DrawerState.Open
                            } else {
                                DrawerState.Closed
                            }
                        }
                    }
                )
        )
    }
}

/**
 * This composable displays whichever [Screen] is specified by its [Screen] parameter [selectedScreen].
 * The only one which has a real implementation is [Screen.Home] which displays the [JetLaggedScreen]
 * Composable. Our root Composable is a [Box] whose `modifier` argument is our [Modifier] parameter
 * [modifier]. In the `content` of the [Box] we `when` branch on the value of our [Screen] parameter
 * [selectedScreen]:
 *  - [Screen.Home] we compose a [JetLaggedScreen] Composable with its `modifier` argument just the
 *  empty, default, or starter [Modifier] that contains no elements, and its `onDrawerClicked` argument
 *  our [onDrawerClicked] lambda parameter.
 *  - [Screen.SleepDetails] we compose an empty [Surface] whose `modifier` argument is a
 *  [Modifier.fillMaxSize] that causes it to take up its entire incoming size constraints.
 *  - [Screen.Leaderboard] we compose an empty [Surface] whose `modifier` argument is a
 *  [Modifier.fillMaxSize] that causes it to take up its entire incoming size constraints.
 *  - [Screen.Settings] we compose an empty [Surface] whose `modifier` argument is a
 *  [Modifier.fillMaxSize] that causes it to take up its entire incoming size constraints.
 *
 * @param selectedScreen the [Screen] whose Composable implementation we are to display.
 * @param onDrawerClicked a lambda which when called will "toggle" the [HomeScreenDrawerContents]
 * navigation drawer of [HomeScreenDrawer] by animating the `translationX` of the [Modifier.graphicsLayer]
 * draw layer into which we draw our content thereby exposing or hiding the [HomeScreenDrawer] which
 * is rendered underneath us.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [HomeScreenDrawer] passes us a [Modifier.graphicsLayer] that makes us draw
 * our `content` into a draw layer whose `translationX` can be animated using the [Animatable]
 * variable `translationX`, with a [Modifier.draggable] that allows the user to drag the draw layer
 * of the [Modifier.graphicsLayer].
 */
@Composable
private fun ScreenContents(
    selectedScreen: Screen,
    onDrawerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (selectedScreen) {
            Screen.Home ->
                JetLaggedScreen(
                    modifier = Modifier,
                    onDrawerClicked = onDrawerClicked
                )

            Screen.SleepDetails ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                }

            Screen.Leaderboard ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                }

            Screen.Settings ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                }
        }
    }
}

/**
 * This enum is used to control the `translationX` of the [Modifier.graphicsLayer] draw layer into
 * which [ScreenContents] draws its contents. [DrawerState.Open] animates the `translationX`
 * to [DrawerWidth] (300.dp), and [DrawerState.Closed] animates the `translationX` to 0.
 */
private enum class DrawerState {
    Open,
    Closed
}

/**
 * This is the "navigation drawer" which is rendered underneath the [ScreenContents] Composable.
 */
@Composable
private fun HomeScreenDrawerContents(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Screen.entries.forEach {
            NavigationDrawerItem(
                label = {
                    Text(text = it.text)
                },
                icon = {
                    Icon(imageVector = it.icon, contentDescription = it.text)
                },
                colors =
                NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.White),
                selected = selectedScreen == it,
                onClick = {
                    onScreenSelected(it)
                },
            )
        }
    }
}

private val DrawerWidth: Dp = 300.dp

private enum class Screen(val text: String, val icon: ImageVector) {
    Home(text = "Home", icon = Icons.Default.Home),
    SleepDetails(text = "Sleep", icon = Icons.Default.Bedtime),
    Leaderboard(text = "Leaderboard", icon = Icons.Default.Leaderboard),
    Settings(text = "Settings", icon = Icons.Default.Settings),
}
