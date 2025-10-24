package com.example.examplescomposemotionlayout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.*

/**
 * A composable that displays a picture puzzle that can be shuffled and reassembled.
 *
 * This example demonstrates how to animate the shuffling of puzzle pieces using the `animateChanges`
 * property of [ConstraintLayout].
 *
 * The puzzle pieces are arranged using [ConstraintLayoutBaseScope.createFlow]. When the layout
 * is clicked, the order of the pieces in the `data` array is either shuffled or sorted.
 * This change triggers a recomposition. A new [ConstraintSet] is created with the updated
 * order, and [ConstraintLayout] automatically animates the pieces to their new positions.
 *
 * TODO: Continue here.
 *
 * @see PuzzlePiece
 * @see MPuzzle for an alternative implementation using [MotionLayout].
 */
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun Puzzle() {
    val grid = 5
    val blocks: Int = grid * grid
    var data: Array<Int> by remember {
        val a: Array<Int> = Array(size = blocks) { it }
        mutableStateOf(value = a)
    }
    var toggle: Boolean by remember {
        mutableStateOf(value = true)
    }

    val refId: List<String> = remember(key1 = data) { data.map { "w$it" } }
    val set: ConstraintSet = remember(key1 = data) {
        ConstraintSet {
            val ref: Array<ConstrainedLayoutReference> =
                refId.map { createRefFor(id = it) }.toTypedArray()
            val flow: ConstrainedLayoutReference = createFlow(
                elements = ref,
                maxElement = grid,
                wrapMode = Wrap.Aligned,
            )
            constrain(ref = flow) {
                centerTo(other = parent)
                height = Dimension.ratio(ratio = "1:1")
            }
            ref.forEach { layoutItem: ConstrainedLayoutReference ->
                constrain(ref = layoutItem) {
                    width = Dimension.percent(percent = 1f / grid)
                    height = Dimension.ratio(ratio = "1:1")
                }
            }
        }
    }

    // Recreate ids for current Array order
    ConstraintLayout(
        constraintSet = set,
        animateChanges = true,
        animationSpec = tween(durationMillis = 1000),
        modifier = Modifier
            .background(color = Color.Red)
            .clickable {
                data = data.clone()
                if (toggle) {
                    data.shuffle()
                } else {
                    data.sort()
                }
                toggle = !toggle
            }
    ) {
        val painter: Painter = painterResource(id = R.drawable.pepper)
        data.forEachIndexed { i: Int, id: Int ->
            PuzzlePiece(
                x = i % grid,
                y = i / grid,
                gridSize = grid,
                painter = painter,
                modifier = Modifier.layoutId(layoutId = refId[id])
            )
        }
    }
}

/**
 * Shows how to animate moving pieces of a puzzle using MotionLayout.
 *
 * &nbsp;
 *
 * The [PuzzlePiece]s are laid out using the [ConstraintLayoutBaseScope.createFlow] helper.
 *
 * And the animation is achieved by creating two ConstraintSets. One providing ordered IDs to Flow,
 * and the other providing a shuffled list of the same IDs.
 *
 * @see PuzzlePiece
 */
@OptIn(ExperimentalMotionApi::class)
@Preview
@Composable
fun MPuzzle() {
    val grid = 5
    val blocks: Int = grid * grid

    var animateToEnd: Boolean by remember { mutableStateOf(value = true) }

    val index: Array<Int> = remember { Array(size = blocks) { it }.apply { shuffle() } }
    val refId: Array<String> = remember { Array(size = blocks) { "W$it" } }

    // Recreate scene when order changes (which is driven by toggling `animateToEnd`)
    val scene: MotionScene = remember(key1 = animateToEnd) {
        MotionScene {
            val ordered: Array<ConstrainedLayoutReference> =
                refId.map { createRefFor(it) }.toTypedArray()
            val shuffle: Array<ConstrainedLayoutReference> =
                index.map { ordered[it] }.toTypedArray()
            val set1: ConstraintSetRef = constraintSet {
                val flow: ConstrainedLayoutReference = createFlow(
                    elements = ordered,
                    maxElement = grid,
                    wrapMode = Wrap.Aligned,
                )
                constrain(ref = flow) {
                    centerTo(other = parent)
                    width = Dimension.ratio(ratio = "1:1")
                    height = Dimension.ratio(ratio = "1:1")
                }
                ordered.forEach { itemRef: ConstrainedLayoutReference ->
                    constrain(ref = itemRef) {
                        width = Dimension.percent(percent = 1f / grid)
                        height = Dimension.ratio(ratio = "1:1")
                    }
                }
            }
            val set2: ConstraintSetRef = constraintSet {
                val flow: ConstrainedLayoutReference = createFlow(
                    elements = shuffle,
                    maxElement = grid,
                    wrapMode = Wrap.Aligned,
                )
                constrain(ref = flow) {
                    centerTo(other = parent)
                    width = Dimension.ratio(ratio = "1:1")
                    height = Dimension.ratio(ratio = "1:1")
                }
                ordered.forEach { gridCell: ConstrainedLayoutReference ->
                    constrain(ref = gridCell) {
                        width = Dimension.percent(percent = 1f / grid)
                        height = Dimension.ratio(ratio = "1:1")
                    }
                }
            }
            transition(from = set1, to = set2, name = "default") {
                motionArc = Arc.StartHorizontal
                keyAttributes(*ordered) {
                    frame(frame = 40) {
                        // alpha = 0.0f
                        rotationZ = -90f
                        scaleX = 0.1f
                        scaleY = 0.1f
                    }
                    frame(frame = 70) {
                        rotationZ = 90f
                        scaleX = 0.1f
                        scaleY = 0.1f
                    }
                }
            }
        }
    }

    val progress: Float by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = ""
    )

    MotionLayout(
        motionScene = scene,
        modifier = Modifier
            .clickable {
                animateToEnd = !animateToEnd
                index.shuffle()
            }
            .background(color = Color.Red)
            .fillMaxSize(),
        progress = progress
    ) {
        val painter: Painter = painterResource(id = R.drawable.pepper)
        index.forEachIndexed { i: Int, id: Int ->
            PuzzlePiece(
                x = i % grid,
                y = i / grid,
                gridSize = grid,
                painter = painter,
                modifier = Modifier.layoutId(layoutId = refId[id])
            )
        }
    }
}

/**
 * Composable that displays a fragment of the given surface (provided through [painter]) based on
 * the given position ([x], [y]) of a square grid of size [gridSize].
 */
@Composable
fun PuzzlePiece(
    x: Int,
    y: Int,
    gridSize: Int,
    painter: Painter,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        clipRect {
            translate(
                left = -x * size.width,
                top = -y * size.height
            ) {
                with(receiver = painter) {
                    draw(size = size.times(operand = gridSize.toFloat()))
                }
            }
        }
    }
}