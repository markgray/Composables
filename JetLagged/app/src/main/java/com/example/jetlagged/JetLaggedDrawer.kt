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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This is used as the root Composable of [MainActivity]
 */
@Composable
fun HomeScreenDrawer() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        var drawerState: DrawerState by remember {
            mutableStateOf(value = DrawerState.Closed)
        }
        var screenState: Screen by remember {
            mutableStateOf(value = Screen.Home)
        }

        val translationX: Animatable<Float, AnimationVector1D> = remember {
            Animatable(initialValue = 0f)
        }

        val drawerWidth: Float = with(LocalDensity.current) {
            DrawerWidth.toPx()
        }
        translationX.updateBounds(lowerBound = 0f, upperBound = drawerWidth)

        val coroutineScope: CoroutineScope = rememberCoroutineScope()

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

        HomeScreenDrawerContents(
            selectedScreen = screenState,
            onScreenSelected = { screen: Screen ->
                screenState = screen
            }
        )

        val draggableState: DraggableState = rememberDraggableState(onDelta = { dragAmount: Float ->
            coroutineScope.launch {
                translationX.snapTo(targetValue = translationX.value + dragAmount)
            }
        })
        val decay = rememberSplineBasedDecay<Float>()
        ScreenContents(
            selectedScreen = screenState,
            onDrawerClicked = ::toggleDrawerState,
            modifier = Modifier
                .graphicsLayer {
                    this.translationX = translationX.value
                    val scale: Float = lerp(1f, 0.8f, translationX.value / drawerWidth)
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

private enum class DrawerState {
    Open,
    Closed
}

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
