/*
 * Copyright 2020 Google LLC
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

@file:Suppress("unused")

package com.google.samples.apps.sunflower.compose.plantdetail

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ScrollState
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

/**
 * A heuristic value used to determine when the toolbar should be shown.
 */
private val HeaderTransitionOffset = 190.dp

/**
 * Class that contains derived state for when the toolbar should be shown.
 *
 * This class is responsible for managing the state of the toolbar based on the scroll position
 * and the position of the plant name. It determines whether the toolbar should be hidden or shown,
 * and provides a transition state for animating the toolbar's appearance.
 *
 * @param scrollState The current scroll state of the content.
 * @param namePosition The position of the plant name in the layout.
 */
data class PlantDetailsScroller(
    private val scrollState: ScrollState,
    val namePosition: Float
) {
    /**
     * A [MutableTransitionState] indicating the current state of the toolbar.
     *
     * This state is used to animate the toolbar's appearance and disappearance
     * as the user scrolls. When the scroll position reaches a certain threshold
     * (determined by [namePosition] and [HeaderTransitionOffset]), the toolbar
     * transitions to the [ToolbarState.SHOWN] state, and vice versa.
     *
     * The initial state of the toolbar is [ToolbarState.HIDDEN].
     */
    val toolbarTransitionState: MutableTransitionState<ToolbarState> =
        MutableTransitionState(initialState = ToolbarState.HIDDEN)

    /**
     * This function is used to determine the state of the toolbar.
     * When the namePosition is placed correctly on the screen (position > 1f) and it's
     * position is close to the header, then show the toolbar by animating it to [ToolbarState.SHOWN],
     * and return [ToolbarState.SHOWN] to the caller. Otherwise, hide the toolbar by animating it to
     * [ToolbarState.HIDDEN], and return [ToolbarState.HIDDEN] to the caller.
     *
     * @param density The screen density to calculate the transition offset.
     * @return The current state of the toolbar (either [ToolbarState.SHOWN] or [ToolbarState.HIDDEN]).
     */
    fun getToolbarState(density: Density): ToolbarState {
        // When the namePosition is placed correctly on the screen (position > 1f) and it's
        // position is close to the header, then show the toolbar.
        return if (namePosition > 1f &&
            scrollState.value > (namePosition - getTransitionOffset(density))
        ) {
            toolbarTransitionState.targetState = ToolbarState.SHOWN
            ToolbarState.SHOWN
        } else {
            toolbarTransitionState.targetState = ToolbarState.HIDDEN
            ToolbarState.HIDDEN
        }
    }

    /**
     * Calculates the transition offset in pixels.
     *
     * This function converts the [HeaderTransitionOffset] (defined in dp)
     * to pixels using the provided [density]. This offset is used to determine
     * when the toolbar should transition between shown and hidden states.
     *
     * @param density The screen density used for the conversion.
     * @return The transition offset in pixels.
     */
    private fun getTransitionOffset(density: Density): Float = with(density) {
        HeaderTransitionOffset.toPx()
    }
}

/**
 * Sealed class that represents the state of the toolbar.
 *
 * The toolbar can be either hidden or shown. This class is used to manage the
 * visibility of the toolbar and to animate its appearance and disappearance.
 */
enum class ToolbarState { HIDDEN, SHOWN }

/**
 * Extension property on [ToolbarState] that returns true if the state is [ToolbarState.SHOWN].
 *
 * This property provides a convenient way to check if the toolbar is currently shown.
 *
 * @return `true` if the toolbar is shown, `false` otherwise.
 */
val ToolbarState.isShown: Boolean
    get() = this == ToolbarState.SHOWN
