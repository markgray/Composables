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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.Ref
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.FlowStyle
import androidx.constraintlayout.compose.HorizontalAlign
import androidx.constraintlayout.compose.Wrap
import androidx.constraintlayout.compose.layoutId
import kotlinx.coroutines.CoroutineScope

/**
 * The number of [Item] objects we create for our [AnimatedConstraintLayout].
 */
private const val ITEM_COUNT = 40

/**
 * The initial unexpanded height and width of an [Item] in [dp]. When expanded its width increase to
 * twice this value.
 */
private const val BASE_ITEM_SIZE = 80

/**
 * The initial value for the number of columns used as the `maxElement` argument of the call to
 * `createFlow` that creates the [ConstrainedLayoutReference] used in the [ConstraintSet]. It is
 * the maximum number of elements in a row.
 */
private const val INITIAL_COLUMN_COUNT = 4

/**
 * This is Composable which handles our UI. It is used by [MainActivity] in its call to `setContent`.
 */
@Preview
@Composable
internal fun FlowDragAndDropExample() {
    /**
     * The number of [Item] Composable widgets we create for the demo.
     */
    val itemCount: Int = ITEM_COUNT

    /**
     * The number of columns per row from 1 to 4, it is used as the `maxElement` argument of the
     * [ConstrainedLayoutReference] flow that is used in the [ConstraintSet] the is used as the
     * `constraintSet` argument of our call of [AnimatedConstraintLayout]. Its value is incremented
     * round robin by the [Button] labeled "Columns" in our UI (which also displays the latest value.
     */
    var columnCount: Int by remember { mutableIntStateOf(INITIAL_COLUMN_COUNT) }

    /**
     * Our [List] of [ItemState] for each of the [Item] that we display. An [ItemState] contains two
     * [MutableState] wrapped [Boolean]'s: [ItemState.isHorizontallyExpanded] and
     * [ItemState.isVerticallyExpanded] which define whether the [Item] they are for is horizontally
     * and/or vertically expanded. Only [ItemState.isHorizontallyExpanded] is used by us, and is
     * toggled when an [Item] is clicked. The [Item] toggles between a width of [BASE_ITEM_SIZE] and
     * two times [BASE_ITEM_SIZE] (80.dp and 160.dp).
     */
    val itemModel: List<ItemState> = remember {
        List(ITEM_COUNT) { ItemState() }
    }

    /**
     * Observable mutable list, holds the desired item order for the Flow layout. Note that as this
     * List is modified, recompositions are triggered.
     */
    val itemOrderByIndex: SnapshotStateList<Int> =
        remember { mutableStateListOf(elements = List(itemCount) { it }.toTypedArray()) }

    /**
     * This is the [ConstraintSet] that is fed to our [AnimatedConstraintLayout] as its `constraintSet`
     * argument.
     */
    val constraintSet = ConstraintSet {
        /**
         * Create the references for ConstraintLayout
         */
        val itemRefs: List<ConstrainedLayoutReference> = List(itemCount) { createRefFor(id = "item$it") }

        /**
         * Provide the flow with the references in the order that reflects the current layout
         * Since the list is observable, changes on it will cause the [ConstraintSet] to be recreated
         * on recomposition, triggering the layout animation in [AnimatedConstraintLayout]
         *
         *  + `elements` a `vararg` of [ConstraintLayout] items, in our case our variable `itemRefs`
         *  [List] of [ConstrainedLayoutReference] converted to an [Array].
         *  + `flowVertically` when set to `true` arranges the Composables from top to bottom, when
         *  `false` like here they are arranged from left to right.
         *  + `maxElement` defines the maximum elements on a row, we use the `columnCount` chosen by
         *  the user using the "Columns" [Button].
         *  + `horizontalStyle` sets the style of the horizontal chain ([FlowStyle.Spread],
         *  [FlowStyle.Packed], or [FlowStyle.SpreadInside]) we use [FlowStyle.Spread] which spreads
         *  them to occupy the width of their row.
         *  + `horizontalFlowBias` sets the way elements are aligned vertically, Center is default,
         *  we use 0.5f
         *  + `horizontalAlign` set the way elements are aligned horizontally, we use
         *  [HorizontalAlign.Center]
         *  + wrapMode sets the way reach maxElements is handled [Wrap.None] (default) -- no wrap
         *  behavior, [Wrap.Chain] - create additional chains which is what we use.
         */
        val flow: ConstrainedLayoutReference = createFlow(
            elements = itemOrderByIndex.map { itemRefs[it] }.toTypedArray(),
            flowVertically = false,
            maxElement = columnCount,
            horizontalStyle = FlowStyle.Spread,
            horizontalFlowBias = 0.5f, // centerVertically
            horizontalAlign = HorizontalAlign.Center,
            wrapMode = Wrap.Chain
        )
        /**
         * Here we specify the constraints associated to the layout identified with ref. In the
         * `constrainBlock` lambda we constrain the `width` to be [Dimension.fillToConstraints]
         * (A Dimension that spreads to match constraints), we link the `top` of our `flow` to the
         * `parent.top`, and `centerHorizontallyTo` our `parent` to add start and end links towards
         * the corresponding anchors of `parent``. This will center horizontally the current layout
         * inside or around (depending on size) `parent`.
         */
        constrain(ref = flow) {
            width = Dimension.fillToConstraints
            top.linkTo(parent.top)
            centerHorizontallyTo(parent)
        }

        itemRefs.forEachIndexed { index: Int, itemRef: ConstrainedLayoutReference ->
            /**
             * Define the items dimensions, note that since `isHorizontallyExpanded` is an
             * observable State, changes to it will cause the ConstraintSet to be recreated and so
             * the change wil be animated by AnimatedConstraintLayout
             */
            val widthDp: Int = if (itemModel[index].isHorizontallyExpanded) {
                BASE_ITEM_SIZE * 2
            } else {
                BASE_ITEM_SIZE
            }
            constrain(ref = itemRef) {
                height = BASE_ITEM_SIZE.dp.asDimension()
                width = widthDp.dp.asDimension()
            }
        }
    }

    /**
     * This is used as the `state` argument of the `Modifier.verticalScroll` that is used as part of
     * the `modifier` argument of the [Column] that holds our UI. It is passed as the `scrollState`
     * argument of the [LayoutDragHandler] variable `dragHandler` in order to allow it to scroll the
     * [Column] if it needs to.
     */
    val scrollState: ScrollState = rememberScrollState()

    /**
     * Full bounds of the ConstraintLayout-based list
     */
    val listBounds: Ref<Rect> = remember { Ref<Rect>().apply { value = Rect.Zero } }

    /**
     * Clipped (window) bounds of the ConstraintLayout-based list
     */
    val windowBounds: Ref<Rect> = remember { Ref<Rect>().apply { value = Rect.Zero } }

    /**
     * [CoroutineScope] that is passed as the `scope` argument of our [LayoutDragHandler], it uses
     * it to launch jobs in response to callback events such as clicks or other user interaction
     * where the response to that event needs to unfold over time and be cancelled if the composable
     * managing that process leaves the composition.
     */
    val scope: CoroutineScope = rememberCoroutineScope()

    /**
     * This contains the [Rect] defining the position and size of each [Item] indexed by their ID.
     * It is used as the `boundsById` argument of the [LayoutDragHandler], and it is updated whenever
     * an [Item] moves by the [MotionLayoutScope] `Modifier.onStartEndBoundsChanged` applied to the
     * [Box] containing it to the `endBounds` of the animated movement of the [Box].
     */
    val boundsById: MutableMap<Int, Rect> = remember { mutableMapOf() }

    /**
     * This is used as the `onMove` argument of our [LayoutDragHandler] and it is triggered whenever
     * the dragged [Item] is over a new "target" [Item]. It removes the [Item] at its `from` parameter
     * (the old index of the dragged [Item]) and adds it in the `to` parameter position of the [List]
     * of [Int] variable `itemOrderByIndex` thereby swapping them when the [ConstraintLayout] based
     * list is recomposed to reflect the change.
     */
    val onMove: (Int, Int) -> Unit = { from: Int, to: Int ->
        // TODO: Implement a way that moves items directionally (moving up pushes items down) instead of in a Flow
        itemOrderByIndex.add(to, itemOrderByIndex.removeAt(from))
    }

    /**
     * Our [LayoutDragHandler]. It is used as the `dragHandler` argument of the [Modifier.dragAndDrop]
     * that is appended to the `modifier` of our [AnimatedConstraintLayout] and enables DragAndDrop
     * functionality on the receiving Composable, with its data and callbacks used to do the magic
     * required. It is also used as the `dragHandler` argument of the [DraggablePlaceholder]. The
     * parameters of its constructor are:
     *  + `boundsById` our [MutableMap] of [Int] to [Rect] variable `boundsById` which contains the
     *  [Rect] defining the position and size of each [Item] indexed by their ID.
     *  + `orderedIds` [SnapshotStateList] of [Int] variable `itemOrderByIndex` which holds the
     *  desired item order for the Flow layout.
     *  + `listBounds` our [Ref] or a [Rect] variable `listBounds` which contains the full bounds of
     *  the ConstraintLayout-based list
     *  + `windowBounds` our [Ref] or a [Rect] variable `windowBounds` which contains the clipped
     *  (window) bounds of the ConstraintLayout-based list
     *  + `scrollState` our [ScrollState] variable `scrollState` which is used as the `state`
     *  argument of the `Modifier.verticalScroll` modifier used for the [Column] holding our UI and
     *  which allows the [LayoutDragHandler] to observe and control the scrolling of the [Column] as
     *  needed.
     *  + `scope` our [CoroutineScope] variable `scope` which [LayoutDragHandler] can use to launch
     *  any background work it may need to do.
     *  + `onMove` our lambda variable `onMove(from: Int, to: Int)` which removes the [Item] at index `
     *  `from` from the [List] of [Int] `itemOrderByIndex` and inserts it in position `to` of the
     *  [List], and it is called by [LayoutDragHandler] when it determines that the dragged [Item]
     *  has moved over another [Item] in the layout.
     */
    val dragHandler: LayoutDragHandler = remember {
        LayoutDragHandler(
            boundsById = boundsById,
            orderedIds = itemOrderByIndex,
            listBounds = listBounds,
            windowBounds = windowBounds,
            scrollState = scrollState,
            scope = scope,
            onMove = onMove
        )
    }

    // Common Container for the placeholder and the actual layout
    Box(modifier = Modifier.fillMaxWidth()) {
        // Composables that represent the actual content of each Item, since the content may be
        // handed-off to the DraggablePlaceholder we need to define it before-hand, that way it may
        // be emitted in the ConstraintLayout node or the DraggablePlaceholder node as it's needed
        val movableItems = remember {
            List(itemCount) { it }.map { id: Int ->
                movableContentOf {
                    Item(
                        text = "item$id",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState, enabled = true)
        ) {
            // Controls and layout
            Column(
                modifier = Modifier.align(Alignment.End),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        columnCount = INITIAL_COLUMN_COUNT
                        itemOrderByIndex.sort()
                        itemModel.forEach {
                            it.isHorizontallyExpanded = false
                            it.isVerticallyExpanded = false
                        }
                    }) {
                        Text(text = "Reset")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { itemOrderByIndex.shuffle() }) {
                        Text(text = "Shuffle")
                    }
                    Button(onClick = {
                        var newColumnCount = columnCount + 1
                        if (newColumnCount > 4) {
                            newColumnCount = 1
                        }
                        columnCount = newColumnCount
                    }) {
                        Text(text = "Columns ($columnCount)")
                    }
                }

                AnimatedConstraintLayout(
                    constraintSet = constraintSet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .onGloballyPositioned {
                            // Full bounds of the ConstraintLayout-based list
                            listBounds.value =
                                it
                                    .findRootCoordinates()
                                    .localBoundingBoxOf(it, false)
                            // Clipped (window) bounds of the list
                            windowBounds.value =
                                it
                                    .findRootCoordinates()
                                    .localBoundingBoxOf(it, true)
                        }
                        .dragAndDrop(dragHandler = dragHandler)
                ) {
                    for (i in 0 until itemCount) {
                        val name = "item$i"
                        Box(
                            modifier = Modifier
                                .layoutId(layoutId = name)
                                .onStartEndBoundsChanged(layoutId = name) { _, endBounds: Rect ->
                                    // We need bounds that will always represent the static state of
                                    // the layout, we use the End bounds since
                                    // AnimatedConstraintLayout always animates towards the End.
                                    boundsById[i] = endBounds
                                }
                                .clickable {
                                    itemModel[i].isHorizontallyExpanded =
                                        !itemModel[i].isHorizontallyExpanded
                                }
                        ) {
                            if (i != dragHandler.draggedId) {
                                // Show content when not dragging
                                movableItems[i]()
                            } else {
                                // Leave a border so that it's clear where the Item will end up when
                                // the drag interaction finishes
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = CardDefaults.shape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
        // Placeholder should be sibling of ConstraintLayout, if it's a child, even if it's on
        // a fixed position, the animated relayout will cause instability with the drag interaction
        DraggablePlaceholder(
            modifier = Modifier,
            dragHandler = dragHandler
        ) {
            // TODO: Create placeholder dynamically, since dragStart might overlap with dragEnd
            if (dragHandler.draggedId != -1) {
                movableItems[dragHandler.draggedId]()
            }
        }
    }
}

/**
 * The current expanded or un-expanded state of an [Item] in the horizonal and vertical directions.
 */
class ItemState {
    /**
     * TODO: Add kdoc
     */
    var isHorizontallyExpanded: Boolean by mutableStateOf(false)

    /**
     * TODO: Add kdoc
     */
    var isVerticallyExpanded: Boolean by mutableStateOf(false)
}

/**
 * TODO: Add kdoc
 */
@Composable
fun Item(
    text: String,
    modifier: Modifier = Modifier
) {
    val color = remember { Color.hsv(IntRange(0, 360).random().toFloat(), 0.5f, 0.8f) }
    Box(
        modifier = modifier.background(color, CardDefaults.shape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}