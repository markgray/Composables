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
import androidx.compose.ui.graphics.drawscope.DrawScope
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
 * We start by initializing our [Int] variable `grid` to `5`, and initializing our [Int] variable
 * `blocks` to `grid` times `grid`. We initialize and remember our [MutableState] wrapped [Array] of
 * [Int] variable `data` to an [Array] of `size` [Int] variable `blocks` with each entry initialized
 * to its index in the [Array]. We initialize and remember our [MutableState] wrapped [Boolean]
 * variable `toggle` to an initial `value` of `true`. We initialize and remember keyed on `data` our
 * [List] of [String] variable `refId` to the [List] of [String] produced by the [Array.map] method
 * of `data` with its `transform` lambda generating a [String] formed by concatenating the string
 * value of the entry in `data` to the character "w".
 *
 * We initialize and remember keyed on `data` our [ConstraintSet] variable `set` to a new instance in
 * whose [ConstraintSetScope] `description` lambda argument we:
 *  - initialize our [Array] of [ConstrainedLayoutReference] variable `ref` to the [Array] of
 *  [ConstrainedLayoutReference] created by the [Iterable.map] method of `refId` called with a
 *  `transform` lamdba argument that calls [ConstraintSetScope.createRefFor] with each entry in
 *  `refId` feeding the resulting [List] of [ConstrainedLayoutReference] to the
 *  [Collection.toTypedArray] method to transform the [List] into an [Array].
 *  - initialize our [ConstrainedLayoutReference] variable `flow` to the [ConstrainedLayoutReference]
 *  created by the [ConstraintSetScope.createFlow] method called with the `elements` argument our
 *  [Array] of [ConstrainedLayoutReference] variable `ref`, the `maxElement` argument set to `grid`
 *  (the number of elements in a row), and the `wrapMode` argument set to [Wrap.Aligned] (each
 *  element in a column is aligned to the element in the row above and the row below).
 *  - we call the [ConstraintSetScope.constrain] method with the `ref` argument set to `flow` and
 *  in its [ConstrainScope] `constrainBlock` we [ConstrainScope.centerTo] the `other` its `parent`
 *  and set its [ConstrainScope.height] to the `ratio` "1:1".
 *  - we use the [Array.forEach] method of [Array] of [ConstrainedLayoutReference] variable `ref` to
 *  loop though its entries capturing the current [ConstrainedLayoutReference] in the variable
 *  `layoutItem`, then we call the [ConstraintSetScope.constrain] method to constrain each
 *  `layoutItem` to hqve a [ConstrainScope.width] of the `percent` `1f` divided by `grid`, and the
 *  [ConstrainScope.height] of the `ratio` "1:1".
 *
 * Having created our [ConstraintSet] we compose a [ConstraintLayout] with the arguments:
 *  - `constraintSet`: is our [ConstraintSet] variable `set`.
 *  - `animateChanges`: is `true`.
 *  - `animationSpec`: is a [tween] whose `durationMillis` argument is set to `1000`.
 *  - `modifier`: is a [Modifier.background] whose `color` is [Color.Red] chained to a
 *  [Modifier.clickable] in whose `onClick` lambda we set the [MutableState] wrapped [Array] of [Int]
 *  variable `data` to a `clone` of itself and if the [MutableState] wrapped [Boolean] variable
 *  `toggle` is `true` we call the [Array.shuffle] method of `data` to shuffle its entries, otherwise
 *  we call the [Array.sort] method of `data` to sort its entries, and then we invert the value of
 *  `toggle`.
 *
 * In the `content` Composable lambda argument of the [ConstraintLayout] we initialize our [Painter]
 * variable `painter` to the instance created by [painterResource] from the jpg with resource ID
 * `R.drawable.pepper`. Then we use the [Array.forEachIndexed] method of [Array] of [Int] variable
 * `data` to loop through its entries capturing the index in [Int] variable `i`, and the entry in
 * [Int] variable `id`, then we compose a [PuzzlePiece] whose arguments are:
 *  - `x`: is `i` modulo `grid`.
 *  - `y`: is `i` divided by `grid`.
 *  - `gridSize`: is `grid`.
 *  - `painter`: is our [Painter] variable `painter`.
 *  - `modifier`: is a [Modifier.layoutId] whose `id` is the [String] at index `id` in our [List]
 *  of [String] variable `refId`.
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
 * Shows how to animate moving pieces of a puzzle using [MotionLayout].
 *
 * This version of the puzzle uses [MotionLayout] to animate the shuffling and reordering of the
 * puzzle pieces. It defines two [ConstraintSet]s within a [MotionScene]: one for the ordered
 * state and one for the shuffled state.
 *
 * The transition between these two sets is defined in the `transition` block of the [MotionScene].
 * The animation's progress is driven by an animated float, which is toggled when the layout is
 * clicked.
 *
 * When clicked, the `animateToEnd` boolean state is toggled, and the array of indices is
 * re-shuffled. This triggers a recomposition where a new [MotionScene] is created with the new
 * shuffled order, and `animateFloatAsState` animates the `progress` of the [MotionLayout],
 * resulting in a smooth transition between the ordered and shuffled states.
 *
 * Keyframes (`keyAttributes`) are used to add effects like rotation and scaling to the pieces
 * during the transition, making the shuffle animation more dynamic.
 *
 * We start by initializing our [Int] variable `grid` to `5`, and initializing our [Int] variable
 * `blocks` to `grid` times `grid`. We initialize and remember our [MutableState] wrapped [Boolean]
 * variable `animateToEnd` to an initial `value` of `true`. We initialize and remember our [Array]
 * of [Int] variable `index` to an [Array] of `size` [Int] variable `blocks` with each entry
 * initialized to its index in the [Array] to which we use the [apply] extension function to shuffle
 * its entries. We initialize and remember our [Array] of [String] variable `refId` to an [Array] of
 * `size` [Int] variable `blocks` with each entry initialized to the [String] formed by concatenating
 * the string value of the index of the entry to the character "W".
 *
 * We initialize and remember keyed on `animateToEnd` our [MotionScene] variable `scene` to a new
 * instance in whose [MotionSceneScope] `motionSceneContent` lambda argument we:
 *  - initialize our [Array] of [ConstrainedLayoutReference] variable `ordered` to the [Array] of
 *  [ConstrainedLayoutReference] created by using the [Array.map] method of `refId` with a `transform`
 *  lambda argument that calls [ConstraintSetScope.createRefFor] with each entry in `refId` feeding
 *  the resulting [List] of [ConstrainedLayoutReference] to the [Collection.toTypedArray] method to
 *  transform the [List] into an [Array].
 *  - initialize our [Array] of [ConstrainedLayoutReference] variable `shuffle` to the [Array] of
 *  [ConstrainedLayoutReference] created by using the [Array.map] method of `index` with a `transform`
 *  lambda argument that returns the [ConstrainedLayoutReference] whose entry in `ordered` has the
 *  index of the entry in `index` feeding the resulting [List] of [ConstrainedLayoutReference] to the
 *  [Collection.toTypedArray] method to transform the [List] into an [Array] (this results in a
 *  shuffled version of `ordered` because `index` is a shuffled array of its indices).
 *
 * Next we initialize our [ConstraintSetRef] variable `set1` to a new instance in whose
 * [ConstraintSetScope] `constraintSetContent` lambda argument we:
 *  - initialize our [ConstrainedLayoutReference] variable `flow` to the [ConstrainedLayoutReference]
 *  created by the [ConstraintSetScope.createFlow] method called with the `elements` argument our
 *  [Array] of [ConstrainedLayoutReference] variable `ordered`, the `maxElement` argument our [Int]
 *  variable `grid`, and the `wrapMode` argument set to [Wrap.Aligned].
 *  - [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `flow` to
 *  [ConstrainScope.centerTo] the `other` its `parent` and set its [ConstrainScope.width] to the
 *  `ratio` "1:1" and its [ConstrainScope.height] to the `ratio` "1:1".
 *  - we then use the [Array.forEach] method of [Array] of [ConstrainedLayoutReference] variable
 *  `ordered` to loop through its entries capturing the current [ConstrainedLayoutReference] in the
 *  variable `itemRef`, then we call the [ConstraintSetScope.constrain] method to constrain each
 *  `itemRef` to have a [ConstrainScope.width] of `percent` `1f` divided by `grid` and a
 *  [ConstrainScope.height] of the `ratio` "1:1".
 *
 *.Next we initialize our [ConstraintSetRef] variable `set2` to a new instance in whose
 * [ConstraintSetScope] `constraintSetContent` lambda argument we:
 *  - initialize our [ConstrainedLayoutReference] variable `flow` to the [ConstrainedLayoutReference]
 *  created by the [ConstraintSetScope.createFlow] method called with the `elements` argument our
 *  [Array] of [ConstrainedLayoutReference] variable `shuffle`, the `maxElement` argument our [Int]
 *  variable `grid`, and the `wrapMode` argument set to [Wrap.Aligned].
 *  - [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `flow` to
 *  [ConstrainScope.centerTo] the `other` its `parent` and set its [ConstrainScope.width] to the
 *  `ratio` "1:1" and its [ConstrainScope.height] to the `ratio` "1:1".
 *  - we then use the [Array.forEach] method of [Array] of [ConstrainedLayoutReference] variable
 *  `ordered` to loop through its entries capturing the current [ConstrainedLayoutReference] in the
 *  variable `itemRef`, then we call the [ConstraintSetScope.constrain] method to constrain each
 *  `itemRef` to have a [ConstrainScope.width] of `percent` `1f` divided by `grid` and a
 *  [ConstrainScope.height] of the `ratio` "1:1".
 *
 *  Next we define a [MotionSceneScope.transition] `from` [ConstraintSetRef] variable `set1` to
 *  [ConstraintSetRef] variable `set2` with the `name` "default" in whose [TransitionScope]
 *  `transitionContent` lambda argument we:
 *   - set the [TransitionScope.motionArc] to [Arc.StartHorizontal].
 *   - call [TransitionScope.keyAttributes] with a `vararg` of all the [ConstrainedLayoutReference]
 *   in [Array] of [ConstrainedLayoutReference] variable `ordered` and in its [KeyAttributesScope]
 *   `keyAttributesContent` lambda argument we use [KeyAttributesScope.frame] at `frame` `40`
 *   to set its [KeyAttributeScope.rotationZ] to -90f, its [KeyAttributeScope.scaleX] to 0.1f, and
 *   its [KeyAttributeScope.scaleY] to 0.1f. Then we use [KeyAttributesScope.frame] at `frame` `70`
 *   to set its [KeyAttributeScope.rotationZ] to 90f, its [KeyAttributeScope.scaleX] to 0.1f, and
 *   its [KeyAttributeScope.scaleY] to 0.1f.
 *
 * Having defined our [MotionScene] variable `scene` we initialize our [State] wrapped animated
 * [Float] variable `progress` by using [animateFloatAsState] with a `targetValue` of `1f` if
 * `animateToEnd` is `true` or `0f` if it is `false`, with an `animationSpec` of a [tween] with a
 * `durationMillis` of `800`, and a `label` of an empty string.
 *
 *  Our root composable is a [MotionLayout] whose arguments are:
 *   - `motionScene`: is our [MotionScene] variable `scene`.
 *   - `modifier`: is a [Modifier.clickable] in whose `onClick` lambda we set `animateToEnd` to
 *   its inverse, then call the [Array.shuffle] method of `index` to shuffle its contents, chained
 *   to a [Modifier.background] whose `color` is [Color.Red], chained to a [Modifier.fillMaxSize].
 *   - `progress`: is our [State] wrapped animated [Float] variable `progress`.
 *
 * In the [MotionLayoutScope] `content` Composable lambda argument of the [MotionLayout] we initialize
 * our [Painter] variable `painter` to the [Painter] created by [painterResource] from the jpg with
 * resource ID `R.drawable.pepper`. Then we use the [Array.forEachIndexed] method of [Array] of [Int]
 *  variable `index` to loop through its entries capturing the index in [Int] variable `i`, and the
 *  entry in [Int] variable `id`, then we compose a [PuzzlePiece] whose arguments are:
 *   - `x`: is `i` modulo `grid`.
 *   - `y`: is `i` divided by `grid`.
 *   - `gridSize`: is `grid`.
 *   - `painter`: is our [Painter] variable `painter`.
 *   - `modifier`: is a [Modifier.layoutId] whose `id` is the [String] at index `id` in our [Array]
 *   of [String] variable `refId`.
 *
 * @see PuzzlePiece for the composable that draws each individual piece.
 * @see Puzzle for an alternative implementation using `ConstraintLayout(animateChanges = true)`.
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
                refId.map { createRefFor(id = it) }.toTypedArray()
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
                ordered.forEach { itemRef: ConstrainedLayoutReference ->
                    constrain(ref = itemRef) {
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
 * A Composable that displays a single piece of a puzzle.
 *
 * This function takes a [painter] which represents the full image of the puzzle. It then calculates
 * which fragment of the image to display based on its coordinates ([x], [y]) within a grid of a
 * given [gridSize].
 *
 * The implementation uses a [Canvas]. It translates the canvas by a negative offset corresponding
 * to the piece's position and then draws the full painter, which is scaled to the size of the entire
 * grid. A `clipRect` ensures that only the portion of the painter corresponding to the current
 * Composable's bounds is visible, effectively creating a single puzzle piece.
 *
 * Our root composable is a [Canvas] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.fillMaxSize]. In the [DrawScope] `onDraw` lambda argument of the [Canvas]
 * we use [DrawScope.clipRect] with the default arguments to clip the drawing to the bounds of the
 * Composable. Then we use [DrawScope.translate] with a `left` of `-x` times the width of the [Canvas] and a `top`
 * of `-y` times the height of the [Canvas] to translate the canvas by a negative offset corresponding
 * to the piece's position. Then in the [DrawScope] `block` lambda argument of the [DrawScope.translate]
 * we use `with` with a `receiver` of our [Painter] parameter [painter] to call [Painter.draw] with a
 * `size` of the [Canvas] times the [gridSize] of the grid (only the clipped and translated portion
 * of the painter will be visible).
 *
 * @param x The horizontal position (column index) of the piece in the grid, starting from 0.
 * @param y The vertical position (row index) of the piece in the grid, starting from 0.
 * @param gridSize The total number of columns (and rows) in the square puzzle grid.
 * @param painter The [Painter] for the full puzzle image.
 * @param modifier The [Modifier] to be applied to this composable.
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