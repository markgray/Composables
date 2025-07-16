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

package com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.ThumbState.Active
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.ThumbState.Dormant
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.ThumbState.Inactive
import kotlinx.coroutines.delay

/**
 * The time period for showing the scrollbar thumb after interacting with it, before it fades away
 */
private const val SCROLLBAR_INACTIVE_TO_DORMANT_TIME_IN_MS = 2_000L

/**
 * A [Scrollbar] that allows for fast scrolling of content by dragging its thumb.
 * Its thumb disappears when the scrolling container is dormant.
 *
 * We start by initializing and remembering our [MutableInteractionSource] variable
 * `interactionSource` to a new instance. Then our root composable is a [Scrollbar]
 * whose arguments are:
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `orientation`: is our [Orientation] parameter [orientation].
 *  - `interactionSource`: is our [MutableInteractionSource] variable `interactionSource`.
 *  - `state`: is our [ScrollbarState] parameter [state].
 *  - `thumb`: is a lambda which composes our [DraggableScrollbarThumb] composable with its
 *  `interactionSource` argument our [MutableInteractionSource] variable `interactionSource` and
 *  its `orientation` argument our [Orientation] parameter [orientation].
 *  - `onThumbMoved`: is our lambda parameter [onThumbMoved].
 *
 * @param state the driving state for the [Scrollbar]
 * @param orientation the orientation of the scrollbar
 * @param onThumbMoved the fast scroll implementation
 * @param modifier a [Modifier] instance that our caller can use to modify the appearance and/or
 * behavior of our [Scrollbar].
 */
@Composable
fun ScrollableState.DraggableScrollbar(
    state: ScrollbarState,
    orientation: Orientation,
    onThumbMoved: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    Scrollbar(
        modifier = modifier,
        orientation = orientation,
        interactionSource = interactionSource,
        state = state,
        thumb = {
            DraggableScrollbarThumb(
                interactionSource = interactionSource,
                orientation = orientation,
            )
        },
        onThumbMoved = onThumbMoved,
    )
}

/**
 * A simple [Scrollbar]. Its thumb disappears when the scrolling container is dormant.
 *
 * We start by initializing and remembering our [MutableInteractionSource] variable `interactionSource`
 * to a new instance. Then our root composable is a [Scrollbar] whose arguments are:
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `orientation`: is our [Orientation] parameter [orientation].
 *  - `interactionSource`: is our [MutableInteractionSource] variable `interactionSource`.
 *  - `state`: is our [ScrollbarState] parameter [state].
 *  - `thumb`: is a lambda which composes our [DecorativeScrollbarThumb] composable with its
 *  `interactionSource` argument our [MutableInteractionSource] variable `interactionSource` and
 *  its `orientation` argument our [Orientation] parameter [orientation].
 *
 * @param state the driving state for the [Scrollbar]
 * @param orientation the orientation of the scrollbar
 * @param modifier a [Modifier] instance that our caller can use to modify the appearance and/or
 * behavior of our [Scrollbar].
 */
@Composable
fun ScrollableState.DecorativeScrollbar(
    state: ScrollbarState,
    orientation: Orientation,
    modifier: Modifier = Modifier,
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    Scrollbar(
        modifier = modifier,
        orientation = orientation,
        interactionSource = interactionSource,
        state = state,
        thumb = {
            DecorativeScrollbarThumb(
                interactionSource = interactionSource,
                orientation = orientation,
            )
        },
    )
}

/**
 * A scrollbar thumb that is intended to also be a touch target for fast scrolling.
 * This composable is a [Box] whose `modifier` argument is a [Modifier] that configures its `width`
 * to be 12.dp and to fill its `height` if our [Orientation] parameter [orientation] is [Vertical]
 * or configures its `height` to be 12.dp and to fill its `width` if our [Orientation] parameter
 * [orientation] is [Horizontal], and this is followed in the chain by the [Modifier.scrollThumb]
 * extension function with its `scrollableState` the `this` [ScrollableState] that this is an
 * extension function for, and its `interactionSource` our [InteractionSource] parameter
 * [interactionSource].
 *
 * @param interactionSource the [InteractionSource] for the scrollbar thumb.
 * @param orientation the [Orientation] of the scrollbar.
 */
@Composable
private fun ScrollableState.DraggableScrollbarThumb(
    interactionSource: InteractionSource,
    orientation: Orientation,
) {
    Box(
        modifier = Modifier
            .run {
                when (orientation) {
                    Vertical -> width(width = 12.dp).fillMaxHeight()
                    Horizontal -> height(height = 12.dp).fillMaxWidth()
                }
            }
            .scrollThumb(scrollableState = this, interactionSource = interactionSource),
    )
}

/**
 * A decorative scrollbar thumb used solely for communicating a user's position in a list.
 *
 * This composable is a private helper function used by [DecorativeScrollbar].
 * It displays a simple visual indicator of the scroll position. Our root composable is a [Box]
 * whose `modifier` argument is a [Modifier] that configures its `width` to be `2.dp` and to fill
 * its `height` if our [Orientation] parameter [orientation] is [Vertical] or configures its
 * `height` to be `2.dp` and to fill its `width` if our [Orientation] parameter [orientation] is
 * [Horizontal], and this is followed in the chain by the [Modifier.scrollThumb] extension function
 * with its `scrollableState` the `this` [ScrollableState] that this is an extension function for,
 * and its `interactionSource` our [InteractionSource] parameter [interactionSource].
 *
 * @param interactionSource The [InteractionSource] for tracking interactions like hover or press.
 * This is used to potentially change the thumb's appearance based on user interaction, though in
 * this decorative version, it primarily influences visibility timing.
 * @param orientation The [Orientation] of the scrollbar (Vertical or Horizontal). This determines
 * the dimension (width or height) and fill direction of the thumb.
 */
@Composable
private fun ScrollableState.DecorativeScrollbarThumb(
    interactionSource: InteractionSource,
    orientation: Orientation,
) {
    Box(
        modifier = Modifier
            .run {
                when (orientation) {
                    Vertical -> width(width = 2.dp).fillMaxHeight()
                    Horizontal -> height(height = 2.dp).fillMaxWidth()
                }
            }
            .scrollThumb(scrollableState = this, interactionSource = interactionSource),
    )
}

/**
 * A [Modifier.Node] that draws a scrollbar thumb. We start by initializing our [State] of [Color]
 * variable `colorState` to a [scrollbarThumbColor] whose `scrollableState` argument is our
 * [ScrollableState] parameter [scrollableState] and whose `interactionSource` argument is our
 * [InteractionSource] parameter [interactionSource]. We use the [Modifier.then] extension function
 * of `this` [Modifier] to chain a [ModifierNodeElement]<[ScrollThumbNode]> [ScrollThumbElement]
 * whose `colorProducer` argument is the [State.value] of our [State] of [Color] variable `colorState`.
 *
 * @param scrollableState the [ScrollableState] of the scrollable container.
 * @param interactionSource the [InteractionSource] of the scrollable container.
 */
@SuppressLint("ComposableModifierFactory")
@Composable
private fun Modifier.scrollThumb(
    scrollableState: ScrollableState,
    interactionSource: InteractionSource,
): Modifier {
    val colorState: State<Color> = scrollbarThumbColor(
        scrollableState = scrollableState,
        interactionSource = interactionSource,
    )
    return this then ScrollThumbElement { colorState.value }
}

/**
 * A [ModifierNodeElement] that creates a [ScrollThumbNode].
 *
 * @param colorProducer a lambda that returns the [Color] to use to draw the thumb.
 */
@SuppressLint("ModifierNodeInspectableProperties")
private data class ScrollThumbElement(val colorProducer: ColorProducer) :
    ModifierNodeElement<ScrollThumbNode>() {
    /**
     * Creates a new instance of [ScrollThumbNode].
     * This function is called when the modifier element is first applied.
     * It initializes the [ScrollThumbNode] with the provided [colorProducer].
     *
     * @return A new [ScrollThumbNode] instance.
     */
    override fun create(): ScrollThumbNode = ScrollThumbNode(colorProducer)

    /**
     * Updates the existing [ScrollThumbNode] with new properties.
     * This function is called when the modifier element is recomposed with different inputs.
     * It updates the [colorProducer] of the [node] and invalidates its drawing
     * so it can be redrawn with the new color.
     *
     * @param node The [ScrollThumbNode] to update.
     */
    override fun update(node: ScrollThumbNode) {
        node.colorProducer = colorProducer
        node.invalidateDraw()
    }
}

/**
 * A [DrawModifierNode] which draws a scrollbar thumb.
 * It is added to the [Modifier] of a scrollbar thumb [Box] by the [ScrollThumbElement.create]
 * override, and its `colorProducer` field is updated with the latest color by the
 * [ScrollThumbElement.update] override.
 *
 * @param colorProducer a lambda which returns the [Color] to be used to draw the scrollbar thumb.
 */
private class ScrollThumbNode(var colorProducer: ColorProducer) : DrawModifierNode,
    Modifier.Node() {
    /**
     * The shape of the scrollbar thumb.
     */
    private val shape = RoundedCornerShape(size = 16.dp)

    /**
     * The last [Size] that an [Outline] was created for. Used to avoid recomposing when the [Size]
     * has not changed.
     */
    private var lastSize: Size? = null

    /**
     * The [LayoutDirection] used for the last outline calculation. This is used to avoid
     * recalculating the [Outline] if the [LayoutDirection] has not changed.
     */
    private var lastLayoutDirection: LayoutDirection? = null

    /**
     * The last [Outline] that was created. This is cached to avoid recomposing the [Outline] if the
     * drawing parameters ([Size], [LayoutDirection]) have not changed.
     */
    private var lastOutline: Outline? = null

    /**
     * Draws the scrollbar thumb.
     * We initialize our [Color] variable `val color` to the [Color] returned by our [colorProducer]
     * lambda parameter. We initialize our [Outline] variable `val outline` to:
     *  - If the current [ContentDrawScope.size] of the content is equal to our [lastSize] field AND
     *  the current [ContentDrawScope.layoutDirection] is equal to our [lastLayoutDirection] field
     *  we set `outline` to our [lastOutline] field.
     *  - Else we set `outline` to the [Outline] that the [shape] field creates for our current
     *  [ContentDrawScope.size], [ContentDrawScope.layoutDirection] and `this` [ContentDrawScope]
     *  as its `density` argument.
     *
     * If `color` is not equal to [Color.Unspecified] we call the [ContentDrawScope.drawOutline]
     * method with our `outline` variable and `color` variable.
     *
     * Finally we cache `outline` in our [lastOutline] field, the current [ContentDrawScope.size]
     * in our [lastSize] field, and the current [ContentDrawScope.layoutDirection] in our
     * [lastLayoutDirection] field.
     */
    override fun ContentDrawScope.draw() {
        val color: Color = colorProducer()
        val outline: Outline =
            if (size == lastSize && layoutDirection == lastLayoutDirection) {
                lastOutline!!
            } else {
                shape.createOutline(size = size, layoutDirection = layoutDirection, density = this)
            }
        if (color != Color.Unspecified) drawOutline(outline = outline, color = color)

        lastOutline = outline
        lastSize = size
        lastLayoutDirection = layoutDirection
    }
}

/**
 * Computes the color of the scrollbar thumb based on its interaction state.
 *
 * The thumb can be in one of three states: [ThumbState.Dormant], [ThumbState.Inactive], or
 * [ThumbState.Active].
 *
 * It transitions to [ThumbState.Active] when the scrollbar is interacted with (pressed, hovered,
 * dragged, or scrolling is in progress) and it can scroll in either direction.
 *
 * It transitions to [ThumbState.Inactive] from [ThumbState.Active] when interaction stops.
 * After a delay ([SCROLLBAR_INACTIVE_TO_DORMANT_TIME_IN_MS]), it transitions to
 * [ThumbState.Dormant].
 *
 * The color is animated using [animateColorAsState] with a [SpringSpec] for smooth transitions.
 * The `targetValue` of the [animateColorAsState] is:
 *  - [ThumbState.Active]: a copy of [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme]
 *  with 50% alpha.
 *  - [ThumbState.Inactive]: a copy of [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme]
 *  with 20% alpha.
 *  - [ThumbState.Dormant]: [Color.Transparent].
 *
 * @param scrollableState The [ScrollableState] of the scrollable container. Used to determine if
 * scrolling is possible and if scrolling is in progress.
 * @param interactionSource The [InteractionSource] for the scrollbar. Used to track press, hover,
 * and drag states.
 * @return A [State] object holding the current color of the scrollbar thumb.
 */
@Composable
private fun scrollbarThumbColor(
    scrollableState: ScrollableState,
    interactionSource: InteractionSource,
): State<Color> {
    /**
     * The current [MutableState] wrapped [ThumbState] of the scrollbar thumb.
     */
    var state: ThumbState by remember { mutableStateOf(value = Dormant) }

    /**
     * The [State] wrapped [Boolean] indicating if the scrollbar thumb is pressed or not which will
     * be updated by [interactionSource] as the user interacts with the scrollbar.
     */
    val pressed: Boolean by interactionSource.collectIsPressedAsState()

    /**
     * The [State] wrapped [Boolean] indicating if the scrollbar thumb is hovered or not which will
     * be updated by [interactionSource] as the user interacts with the scrollbar.
     */
    val hovered: Boolean by interactionSource.collectIsHoveredAsState()

    /**
     * The [State] wrapped [Boolean] indicating if the scrollbar thumb is dragged or not which will
     * be updated by [interactionSource] as the user interacts with the scrollbar.
     */
    val dragged: Boolean by interactionSource.collectIsDraggedAsState()

    /**
     * [Boolean] indicating if the scrollbar thumb is active, it is `true` if the scrollbar thumb
     * can scroll in either direction and the user is interacting with the scrollbar, ie. one of
     * `pressed`, `hovered`, or `dragged`, is `true` or scrolling is in progress.
     */
    val active: Boolean = (scrollableState.canScrollForward || scrollableState.canScrollBackward) &&
        (pressed || hovered || dragged || scrollableState.isScrollInProgress)

    /**
     * The animated [State] wrapped [Color] of the scrollbar thumb.
     * The color is animated using [animateColorAsState] with a [SpringSpec] for smooth transitions.
     * The `targetValue` of the [animateColorAsState] is:
     *  - [ThumbState.Active]: a copy of [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme]
     *  with 50% alpha.
     *  - [ThumbState.Inactive]: a copy of [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme]
     *  with 20% alpha.
     *  - [ThumbState.Dormant]: [Color.Transparent].
     *
     * The `label` of the [animateColorAsState] is "Scrollbar thumb color".
     */
    val color: State<Color> = animateColorAsState(
        targetValue = when (state) {
            Active -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            Inactive -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            Dormant -> Color.Transparent
        },
        animationSpec = SpringSpec(
            stiffness = Spring.StiffnessLow,
        ),
        label = "Scrollbar thumb color",
    )
    /**
     * This [LaunchedEffect] block is executed when the [Boolean] variable `active` changes value.
     * Its purpose is to update the [ThumbState] `state` variable based on the `active` variable.
     */
    LaunchedEffect(key1 = active) {
        when (active) {
            true -> state = Active
            false -> if (state == Active) {
                state = Inactive
                delay(timeMillis = SCROLLBAR_INACTIVE_TO_DORMANT_TIME_IN_MS)
                state = Dormant
            }
        }
    }

    return color
}

/**
 * The state of the scrollbar thumb.
 * The thumb can be [Active] (fully visible), [Inactive] (partially visible),
 * or [Dormant] (invisible).
 */
private enum class ThumbState {
    Active,
    Inactive,
    Dormant,
}
