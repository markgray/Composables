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

@file:Suppress("unused")

package com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * The delay between scrolls when a user long presses on the scrollbar track to initiate a scroll
 * instead of dragging the scrollbar thumb.
 */
private const val SCROLLBAR_PRESS_DELAY_MS = 10L

/**
 * The percentage displacement of the scrollbar when scrolled by long presses on the scrollbar
 * track.
 */
private const val SCROLLBAR_PRESS_DELTA_PCT = 0.02f

/**
 * A class that holds the state of a scrollbar.
 * This class is not thread-safe.
 *
 * @see [scrollbarStateValue]
 */
class ScrollbarState {
    /**
     * Stores the current scrollbar state as a packed value. The first float in the packed value
     * represents the thumb size as a percentage of the total track size. The second float
     * represents the distance the thumb has traveled as a percentage of the total track size.
     */
    private var packedValue: Long by mutableLongStateOf(value = 0L)

    /**
     * Updates the scrollbar state.
     *
     * This function should be called when the scrollable content that the scrollbar is attached to
     * is scrolled. We just set our [Long] property [packedValue] to the
     * [ScrollbarStateValue.packedValue] of our [ScrollbarStateValue] parameter [stateValue].
     *
     * @param stateValue An updated [ScrollbarStateValue] describing the scrollbar state.
     */
    internal fun onScroll(stateValue: ScrollbarStateValue) {
        packedValue = stateValue.packedValue
    }

    /**
     * Returns the thumb size of the scrollbar as a percentage of the total track size
     */
    val thumbSizePercent: Float
        get() = unpackFloat1(value = packedValue)

    /**
     * Returns the distance the thumb has traveled as a percentage of total track size
     */
    val thumbMovedPercent: Float
        get() = unpackFloat2(value = packedValue)

    /**
     * Returns the max distance the thumb can travel as a percentage of total track size
     */
    val thumbTrackSizePercent: Float
        get() = 1f - thumbSizePercent
}

/**
 * Returns the size of the scrollbar track in pixels
 */
private val ScrollbarTrack.size: Float
    get() = unpackFloat2(value = packedValue) - unpackFloat1(value = packedValue)

/**
 * Returns the position of the scrollbar thumb on the track as a percentage of the total track size.
 * TODO: Continue here.
 * @param dimension the position of the thumb on the track in pixels
 */
private fun ScrollbarTrack.thumbPosition(
    dimension: Float,
): Float = max(
    a = min(
        a = dimension / size,
        b = 1f,
    ),
    b = 0f,
)

/**
 * Class definition for the core properties of a scroll bar
 */
@Immutable
@JvmInline
value class ScrollbarStateValue internal constructor(
    internal val packedValue: Long,
)

/**
 * Class definition for the core properties of a scroll bar track
 */
@Immutable
@JvmInline
private value class ScrollbarTrack(
    val packedValue: Long,
) {
    constructor(
        max: Float,
        min: Float,
    ) : this(packedValue = packFloats(val1 = max, val2 = min))
}

/**
 * Creates a [ScrollbarStateValue] with the listed properties
 *
 * @param thumbSizePercent the thumb size of the scrollbar as a percentage of the total track size.
 * Refers to either the thumb width (for horizontal scrollbars) or height (for vertical scrollbars).
 * @param thumbMovedPercent the distance the thumb has traveled as a percentage of total track size.
 */
fun scrollbarStateValue(
    thumbSizePercent: Float,
    thumbMovedPercent: Float,
): ScrollbarStateValue = ScrollbarStateValue(
    packedValue = packFloats(
        val1 = thumbSizePercent,
        val2 = thumbMovedPercent,
    ),
)

/**
 * Returns the value of [offset] along the axis specified by [this]
 */
internal fun Orientation.valueOf(offset: Offset) = when (this) {
    Horizontal -> offset.x
    Vertical -> offset.y
}

/**
 * Returns the value of [intSize] along the axis specified by [this]
 */
internal fun Orientation.valueOf(intSize: IntSize) = when (this) {
    Horizontal -> intSize.width
    Vertical -> intSize.height
}

/**
 * Returns the value of [intOffset] along the axis specified by [this]
 */
internal fun Orientation.valueOf(intOffset: IntOffset) = when (this) {
    Horizontal -> intOffset.x
    Vertical -> intOffset.y
}

/**
 * A Composable for drawing a scrollbar
 *
 * @param orientation the scroll direction of the scrollbar
 * @param state the state describing the position of the scrollbar
 * @param modifier the modifier to apply to this layout.
 * @param interactionSource allows for observing the state of the scroll bar
 * @param minThumbSize the minimum size of the scrollbar thumb
 * @param onThumbMoved an function for reacting to scroll bar displacements caused by direct
 * interactions on the scrollbar thumb by the user, for example implementing a fast scroll
 * @param thumb a composable for drawing the scrollbar thumb
 */
@Composable
fun Scrollbar(
    orientation: Orientation,
    state: ScrollbarState,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    minThumbSize: Dp = 40.dp,
    onThumbMoved: ((Float) -> Unit)? = null,
    thumb: @Composable () -> Unit,
) {
    // Using Offset.Unspecified and Float.NaN instead of null
    // to prevent unnecessary boxing of primitives
    var pressedOffset: Offset by remember { mutableStateOf(value = Offset.Unspecified) }
    var draggedOffset: Offset by remember { mutableStateOf(value = Offset.Unspecified) }

    // Used to immediately show drag feedback in the UI while the scrolling implementation
    // catches up
    var interactionThumbTravelPercent: Float by remember { mutableFloatStateOf(value = Float.NaN) }

    var track: ScrollbarTrack by remember { mutableStateOf(value = ScrollbarTrack(packedValue = 0)) }

    // scrollbar track container
    Box(
        modifier = modifier
            .run {
                val withHover: Modifier = interactionSource?.let(block = ::hoverable) ?: this
                when (orientation) {
                    Vertical -> withHover.fillMaxHeight()
                    Horizontal -> withHover.fillMaxWidth()
                }
            }
            .onGloballyPositioned { coordinates: LayoutCoordinates ->
                val scrollbarStartCoordinate: Float =
                    orientation.valueOf(offset = coordinates.positionInRoot())
                track = ScrollbarTrack(
                    max = scrollbarStartCoordinate,
                    min = scrollbarStartCoordinate + orientation.valueOf(intSize = coordinates.size),
                )
            }
            // Process scrollbar presses
            .pointerInput(key1 = Unit) {
                detectTapGestures(
                    onPress = { offset: Offset ->
                        try {
                            // Wait for a long press before scrolling
                            withTimeout(timeMillis = viewConfiguration.longPressTimeoutMillis) {
                                tryAwaitRelease()
                            }
                        } catch (e: TimeoutCancellationException) {
                            // Start the press triggered scroll
                            val initialPress = PressInteraction.Press(pressPosition = offset)
                            interactionSource?.tryEmit(interaction = initialPress)

                            pressedOffset = offset
                            interactionSource?.tryEmit(
                                interaction = when {
                                    tryAwaitRelease() -> PressInteraction.Release(press = initialPress)
                                    else -> PressInteraction.Cancel(press = initialPress)
                                },
                            )

                            // End the press
                            pressedOffset = Offset.Unspecified
                        }
                    },
                )
            }
            // Process scrollbar drags
            .pointerInput(key1 = Unit) {
                var dragInteraction: DragInteraction.Start? = null
                val onDragStart: (Offset) -> Unit = { offset ->
                    val start: DragInteraction.Start = DragInteraction.Start()
                    dragInteraction = start
                    interactionSource?.tryEmit(interaction = start)
                    draggedOffset = offset
                }
                val onDragEnd: () -> Unit = {
                    dragInteraction?.let {
                        interactionSource?.tryEmit(interaction = DragInteraction.Stop(start = it))
                    }
                    draggedOffset = Offset.Unspecified
                }
                val onDragCancel: () -> Unit = {
                    dragInteraction?.let {
                        interactionSource?.tryEmit(interaction = DragInteraction.Cancel(start = it))
                    }
                    draggedOffset = Offset.Unspecified
                }
                val onDrag: (change: PointerInputChange, dragAmount: Float) -> Unit =
                    onDrag@{ _, delta ->
                        if (draggedOffset == Offset.Unspecified) return@onDrag
                        draggedOffset = when (orientation) {
                            Vertical -> draggedOffset.copy(
                                y = draggedOffset.y + delta,
                            )

                            Horizontal -> draggedOffset.copy(
                                x = draggedOffset.x + delta,
                            )
                        }
                    }

                when (orientation) {
                    Horizontal -> detectHorizontalDragGestures(
                        onDragStart = onDragStart,
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragCancel,
                        onHorizontalDrag = onDrag,
                    )

                    Vertical -> detectVerticalDragGestures(
                        onDragStart = onDragStart,
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragCancel,
                        onVerticalDrag = onDrag,
                    )
                }
            },
    ) {
        // scrollbar thumb container
        Layout(content = { thumb() }) {
                measurables: List<Measurable>,
                constraints: Constraints,
            ->

            val measurable: Measurable = measurables.first()

            val thumbSizePx: Float = max(
                a = state.thumbSizePercent * track.size,
                b = minThumbSize.toPx(),
            )

            val trackSizePx: Float = when (state.thumbTrackSizePercent) {
                0f -> track.size
                else -> (track.size - thumbSizePx) / state.thumbTrackSizePercent
            }

            val thumbTravelPercent: Float = max(
                a = min(
                    a = when {
                        interactionThumbTravelPercent.isNaN() -> state.thumbMovedPercent
                        else -> interactionThumbTravelPercent
                    },
                    b = state.thumbTrackSizePercent,
                ),
                b = 0f,
            )

            val thumbMovedPx: Float = trackSizePx * thumbTravelPercent

            val y: Int = when (orientation) {
                Horizontal -> 0
                Vertical -> thumbMovedPx.roundToInt()
            }
            val x: Int = when (orientation) {
                Horizontal -> thumbMovedPx.roundToInt()
                Vertical -> 0
            }

            val updatedConstraints: Constraints = when (orientation) {
                Horizontal -> {
                    constraints.copy(
                        minWidth = thumbSizePx.roundToInt(),
                        maxWidth = thumbSizePx.roundToInt(),
                    )
                }

                Vertical -> {
                    constraints.copy(
                        minHeight = thumbSizePx.roundToInt(),
                        maxHeight = thumbSizePx.roundToInt(),
                    )
                }
            }

            val placeable: Placeable = measurable.measure(updatedConstraints)
            layout(width = placeable.width, height = placeable.height) {
                placeable.place(x = x, y = y)
            }
        }
    }

    if (onThumbMoved == null) return

    // Process presses
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { pressedOffset }.collect { pressedOffset: Offset ->
            // Press ended, reset interactionThumbTravelPercent
            if (pressedOffset == Offset.Unspecified) {
                interactionThumbTravelPercent = Float.NaN
                return@collect
            }

            var currentThumbMovedPercent: Float = state.thumbMovedPercent
            val destinationThumbMovedPercent: Float = track.thumbPosition(
                dimension = orientation.valueOf(pressedOffset),
            )
            val isPositive: Boolean = currentThumbMovedPercent < destinationThumbMovedPercent
            val delta: Float = SCROLLBAR_PRESS_DELTA_PCT * if (isPositive) 1f else -1f

            while (currentThumbMovedPercent != destinationThumbMovedPercent) {
                currentThumbMovedPercent = when {
                    isPositive -> min(
                        a = currentThumbMovedPercent + delta,
                        b = destinationThumbMovedPercent,
                    )

                    else -> max(
                        a = currentThumbMovedPercent + delta,
                        b = destinationThumbMovedPercent,
                    )
                }
                onThumbMoved(currentThumbMovedPercent)
                interactionThumbTravelPercent = currentThumbMovedPercent
                delay(timeMillis = SCROLLBAR_PRESS_DELAY_MS)
            }
        }
    }

    // Process drags
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { draggedOffset }.collect { draggedOffset: Offset ->
            if (draggedOffset == Offset.Unspecified) {
                interactionThumbTravelPercent = Float.NaN
                return@collect
            }
            val currentTravel: Float = track.thumbPosition(
                dimension = orientation.valueOf(draggedOffset),
            )
            onThumbMoved(currentTravel)
            interactionThumbTravelPercent = currentTravel
        }
    }
}
