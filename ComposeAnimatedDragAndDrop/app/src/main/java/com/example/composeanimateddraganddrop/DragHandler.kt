/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composeanimateddraganddrop

import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.node.Ref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Used as the `percentFromCenter` argument to the [LayoutDragHandler.containsCloseToCenter] function
 * which we call to check for collisions by checking our [Offset] against all of the [Rect] in the
 * [LayoutDragHandler.boundsById] map looking for one that our [Offset] is within the given distance
 * percent from the center of the [Rect].
 */
private const val CONTAINS_FROM_CENTER_PERCENT = 0.75f

/**
 * This [Modifier] appends a [Modifier.pointerInput] which uses its [LayoutDragHandler] parameter
 * [dragHandler] when it uses the [LayoutDragHandler.detectDragAndDrop] extension function in the
 * [PointerInputScope] block to enable the DragAndDrop functionality on the receiving Composable.
 * It is used in the [Modifier] passed to the [AnimatedConstraintLayout] of the Composable
 * [FlowDragAndDropExample]. All the magic is done by the call to [detectDragGesturesAfterLongPress]
 * in [LayoutDragHandler.detectDragAndDrop], its callbacks use the methods and data found in the
 * [LayoutDragHandler] that the [Modifier.dragAndDrop] is called with.
 *
 * @param dragHandler the [LayoutDragHandler] to be used for DragAndDrop functionality.
 */
fun Modifier.dragAndDrop(dragHandler: LayoutDragHandler): Modifier =
    with(dragHandler) {
        pointerInput(Unit) {
            detectDragAndDrop()
        }
    }


/**
 * This class encapsulates all of the data needed to handle the drag of one of the [Item] in our
 * layout (which it does).
 *
 * @param boundsById a map from the ID (index number) of all of the [Item]s to the [Rect] that
 * defines the [Item]'s size and [Offset].
 * @param orderedIds a [List] of the [Item] ID's in the order in which they should be composed.
 * @param listBounds a [Ref] of a [Rect] that defines the full bounds of the ConstraintLayout-based
 * list of [Item].
 * @param windowBounds a [Ref] of a [Rect] that defines the Clipped (window) bounds of the list.
 * @param scrollState this is the [ScrollState] that we can use to monitor and control the scrolling
 * of the [Column] in the [FlowDragAndDropExample] Composable which holds both the controls and the
 * [AnimatedConstraintLayout] displaying the [Item]s we drag around (it is the `state` argument to a
 * `Modifier.verticalScroll` that is applied to the [Column]).
 * @param scope a [CoroutineScope] we can use to launch jobs in response to callback events such as
 * clicks or other user interaction where the response to that event needs to unfold over time and
 * be cancelled if the composable managing that process leaves the composition. [FlowDragAndDropExample]
 * creates it using [rememberCoroutineScope] so it is tied to the point in the composition where it
 * is called and will be cancelled when the call leaves the composition.
 * @param onMove a lambda to be called when the dragged [Item] is over another [Item] causing it to
 * need to be moved.
 */
class LayoutDragHandler(
    private val boundsById: Map<Int, Rect>,
    private val orderedIds: SnapshotStateList<Int>,
    private val listBounds: Ref<Rect>,
    private val windowBounds: Ref<Rect>,
    private val scrollState: ScrollState?,
    private val scope: CoroutineScope,
    private val onMove: (from: Int, to: Int) -> Unit
) {
    /**
     * Ignore the bounds of the currently dragged item. To avoid "replacing" with self.
     */
    private var ignoreBounds: Rect = Rect.Zero

    /**
     * The index of the dragged [Item]
     */
    private var draggedIndex: Int = -1

    /**
     * Used in the [onDrag] override to hold the old value of [scrollPosition] which it will use
     * the next time it is called to calculate the `scrollChange` [Offset] which it adds to its
     * `dragAmount` [Offset] parameter and the `old` [Offset] of [draggedOffsetFlow] when it calls
     * the [MutableStateFlow.update] method of [draggedOffsetFlow].
     */
    private var lastScrollPosition: Int = 0

    /**
     * Flow to handle the dragging offset, since it may need to check for "collision" across
     * multiple items it's best to make it non-blocking for the dragging event.
     */
    private val draggedOffsetFlow = MutableStateFlow(Offset.Unspecified)

    /**
     * A [Rect] surrounding the area of the layout that is currently visible on the screen
     */
    private fun viewportBounds(): Rect = windowBounds.value ?: Rect.Zero

    /**
     * The top Y coordinate of the ConstraintLayout-based list
     */
    private val contentOffset: Float
        get() = listBounds.value?.top ?: 0f

    /**
     * The current scroll position value in pixels of the [Column] holding our UI.
     */
    private val scrollPosition: Int
        get() = scrollState?.value ?: 0

    /**
     * A [Channel] of [Float] that is used to send scrolling values to a coroutine started in our
     * `init` block which calls the [ScrollState.scrollBy] method of [scrollState] to have it jump
     * instantly by the value in pixels that it receives from the [Channel]. It is constructed using
     * [Channel.CONFLATED] so it holds only the latest value sent it. The receiving coroutine runs
     * as long the [CoroutineContext.isActive] method of the scope it is launched in returns `true`.
     */
    private val scrollChannel = Channel<Float>(Channel.CONFLATED)

    /**
     * The ID of the dragged [Item], it is set by our [onStartDrag] method.
     */
    var draggedId: Int by mutableIntStateOf(-1)
        private set

    /**
     * The current size of the dragged [Item], or [Size.Zero] if the [boundsById] map entry for
     * [draggedId] is `null`. It is used as the `Modifier.size` of the [Modifier] used for the
     * [DraggablePlaceholder] that shows the dragged [Item] as it is dragged.
     */
    val draggedSize: Size
        get() = boundsById[draggedId]?.size ?: Size.Zero

    /**
     * Offset of the dragged item with respect to the root bounds.
     */
    val placeholderOffset: Animatable<Offset, AnimationVector2D> =
        Animatable(Offset.Zero, Offset.VectorConverter)

    init {
        scope.launch {
            // Check and process collisions
            draggedOffsetFlow.collect { offset: Offset ->
                if (offset.isUnspecified) {
                    // Reset when unspecified
                    ignoreBounds = Rect.Zero
                    draggedIndex = -1
                    draggedId = -1
                    return@collect
                }
                if (ignoreBounds.contains(offset)) {
                    return@collect
                }

                // If we are hovering over a different item, obtain its index
                val targetIndex = boundsById.firstNotNullOfOrNull { (id: Int, bounds: Rect) ->
                    // Consider some extra padding when checking for a "collision" to avoid possibly
                    // undesired layout changes
                    if (draggedId != id && bounds.containsCloseToCenter(offset, CONTAINS_FROM_CENTER_PERCENT)) {
                        ignoreBounds = bounds
                        id
                    } else {
                        null
                    }
                }?.let { id: Int ->
                    orderedIds.indexOf(id)
                }

                if (targetIndex != null) {
                    // Item index change, trigger callback
                    val initialIndex = draggedIndex
                    draggedIndex = targetIndex
                    onMove(initialIndex, targetIndex)
                }

                // Scroll the viewport if needed to fit the current dragged item, this is to allow
                // scrolling when hovering over the edge of the viewport
                val scrollInto = Rect(placeholderOffset.value, ignoreBounds.size)
                scope.launch {
                    scrollIntoIfNeeded(scrollInto)
                }
            }
        }
        scope.launch {
            // Consume scroll
            while (coroutineContext.isActive) {
                for (scrollInto: Float in scrollChannel) {
                    val diff: Float = scrollChannel.tryReceive().getOrNull() ?: scrollInto
                    scrollState?.scrollBy(diff)
                }
            }
        }
    }

    /**
     * Extension function to enable the DragAndDrop functionality on the receiving Composable. We
     * just call the [PointerInputScope.detectDragGesturesAfterLongPress] method with [onStartDrag]
     * as its `onDragStart` argument (called when a long press is detected with an [Offset]
     * representing the last known pointer position relative to the containing element), with
     * [onEndDrag] as its `onDragEnd` argument (called after all pointers are up and `onDragCancel`
     * is called if another gesture has consumed pointer input, canceling this gesture), with
     * [onEndDrag] as its `onDragCancel` argument. The `onDrag` argument is a lambda that is called
     * with the [PointerInputChange] as its `change` parameter (describes a change that has occurred
     * for a particular pointer, as well as how much of the change has been consumed) and the [Offset]
     * as its `dragAmount` parameter (distance that the pointer has moved on the screen minus any
     * distance that has been consumed) and the lambda is called for every change in position until
     * the pointer is raised. Inside the lambda we call the [PointerInputChange.consume] method of
     * the `change` parameter and our [onDrag] method with the `dragAmount` parameter.
     */
    suspend fun PointerInputScope.detectDragAndDrop() {
        detectDragGesturesAfterLongPress(
            onDragStart = ::onStartDrag,
            onDragEnd = ::onEndDrag,
            onDragCancel = ::onEndDrag,
            onDrag = { change: PointerInputChange, dragAmount: Offset ->
                change.consume()
                onDrag(dragAmount = dragAmount)
            },
        )
    }

    /**
     * Find dragged item, initialize placeholder offset. Called by [detectDragGesturesAfterLongPress]
     * when a long press is detected with its [Offset] parameter [offset] representing the last known
     * pointer position relative to the containing element. We initialize our [Float] variable
     * `val currContentOffset` to our [Float] field [contentOffset] (the top Y coordinate of the
     * ConstraintLayout-based list), then we loop through all of the entries in the [Map] of [Int]
     * to [Rect] field [boundsById] (contains the current bounds [Rect] of each of the [Item] objects
     * indexed by their ID) with the index assigned to our `id` variable and its [Rect] assigned to
     * our `bounds` variable. Inside the loop we check if the [Rect.contains] method of `bounds`
     * returns `true` for our [Offset] parameter [offset], and if it returns `false` we just loop
     * around to check the next `id` and `bounds`. If it returns `true` however we have found the
     * dragged [Item] so we:
     *  * We initialize our [Offset] variable `val topLeft` to the [Rect.topLeft] value of `bounds`.
     *
     *  * we call the [CoroutineScope.launch] method of our [scope] field to launch a new coroutine
     *  which calls the [Animatable.snapTo] method our our [placeholderOffset] to have it set its
     *  current value to the [Offset] formed when `currContentOffset` is added to `topLeft`
     *
     *  * we set our [Int] field [draggedIndex] to the index of the first occurrence of the `id` in
     *  our [SnapshotStateList] of [Int] field [orderedIds]
     *
     *  * we set our [Rect] field [ignoreBounds] to `bounds`
     *
     *  * we set our [Int] field [draggedId] to `id`
     *
     *  * we set the [MutableStateFlow.value] of our [MutableStateFlow] of [Offset] field
     *  [draggedOffsetFlow] to our [Offset] parameter [offset].
     *
     *  * we return.
     *
     * @param offset the current [Offset] of the [Item] that is to be dragged.
     */
    private fun onStartDrag(offset: Offset) {
        val currContentOffset: Float = contentOffset
        lastScrollPosition = scrollPosition
        boundsById.forEach { (id: Int, bounds: Rect) ->
            if (bounds.contains(offset)) {
                val topLeft: Offset = bounds.topLeft
                scope.launch {
                    // The offset of the dragged item should account for the content offset
                    placeholderOffset.snapTo(targetValue = topLeft + Offset(0f, currContentOffset))
                }
                draggedIndex = orderedIds.indexOf(id)
                ignoreBounds = bounds
                draggedId = id
                draggedOffsetFlow.value = offset
                return
            }
        }
    }

    /**
     * Update placeholder offset.
     *
     * Update accumulated offset flow so that it's handled separately.
     */
    private fun onDrag(dragAmount: Offset) {
        if (draggedIndex == -1 || draggedId == -1) {
            Log.i("RowDndDemo", "onDrag: Unspecified dragged element or offset")
            return
        }
        scope.launch {
            placeholderOffset.snapTo(placeholderOffset.value + dragAmount)
        }
        val scrollChange = Offset(0f, (scrollPosition - lastScrollPosition).toFloat())
        lastScrollPosition = scrollPosition

        // Accumulate the total offset for the Flow, this is necessary since it's possible for a
        // slow collector to miss intermediate updates
        draggedOffsetFlow.update { old: Offset ->
            old + dragAmount + scrollChange
        }
    }

    /**
     * End drag by animating the placeholder towards its origin, note that the origin position may
     * have changed if a dragged was performed over a different item.
     */
    private fun onEndDrag() {
        if (draggedId != -1) {
            val currContentOffset: Float = contentOffset
            boundsById[draggedId]?.topLeft?.let { targetOffset ->
                scope.launch {
                    placeholderOffset.animateTo(targetOffset + Offset(0f, currContentOffset))

                    // Reset after animation is done
                    draggedOffsetFlow.value = Offset.Unspecified
                    placeholderOffset.snapTo(Offset.Zero)
                }
            }
        }
    }

    /**
     * Whether the given [offset] is within the given distance percent from the center.
     */
    private fun Rect.containsCloseToCenter(
        offset: Offset,
        @FloatRange(0.0, 1.0)
        percentFromCenter: Float = 0.5f
    ): Boolean {
        if (offset.x < this.left || offset.x > this.right) {
            return false
        }
        if (offset.y < this.top || offset.y > this.bottom) {
            return false
        }

        val horDistance = width * 0.5f * percentFromCenter
        val center = this.center
        if (offset.x < (center.x - horDistance) || offset.x > (center.x + horDistance)) {
            return false
        }

        val verDistance = height * 0.5f * percentFromCenter
        if (offset.y < (center.y - verDistance) || offset.y > (center.y + verDistance)) {
            return false
        }
        return true
    }

    /**
     * Scroll into the given [bounds] in case they are located outside the viewport.
     *
     * Note that nothing may happen if the given [bounds] are outside the bounds of the original
     * content held by the viewport.
     */
    private suspend fun scrollIntoIfNeeded(bounds: Rect) {
        val visibleBounds = viewportBounds()
        if (bounds.top - visibleBounds.top < 0f) {
            scrollChannel.send(bounds.top - visibleBounds.top)
        } else if (bounds.bottom - visibleBounds.bottom > 0f) {
            scrollChannel.send(bounds.bottom - visibleBounds.bottom)
        }
    }
}