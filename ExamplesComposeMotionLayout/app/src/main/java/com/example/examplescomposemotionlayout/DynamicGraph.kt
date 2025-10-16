package com.example.examplescomposemotionlayout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.MotionSceneScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlin.random.Random


/**
 * Shows how to use MotionLayout to have animated graphs in a LazyColumn.
 *
 * Each graph animates as it's revealed on screen.
 * It demonstrates how to dynamically create constraints based on input. See [DynamicGraph],
 * where constraints are created to lay out the given values into a single graph layout.
 *
 * We start by initializing our variable `graphs` to a [MutableList] of 100 [List]s of 10 random
 * [Float]s between 0f and 100f to use as data for our [DynamicGraph]'s. Then our root composable is
 * a [LazyColumn]. In the [LazyListScope] `content` composable lambda argument we compose a
 * [LazyListScope.items] whose `count` argument is 100. In the [LazyItemScope] `itemContent`
 * composable lambda argument of the `items` we accept the [Int] passed the lambda in variable
 * `index` then compose a [Box] whose `modifier` argument is a [Modifier.padding] that adds 3.dp to
 * all sides chained to a [Modifier.height] whose `height` argument is 200.dp. In the [BoxScope]
 * `content` lambda argument of the [Box] we compose a [DynamicGraph] whose `values` argument is the
 * [List] of [Float] at index `index` in our [MutableList] of [List] of [Float] variable `graphs`.
 */
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun ManyGraphs() {
    /**
     * A [MutableList] of 100 [List]s of 10 random [Float]s between 0f and 100f to use as data for
     * the graphs.
     */
    val graphs: MutableList<List<Float>> = remember {
        mutableListOf<List<Float>>().apply {
            (0..100).forEach { i: Int ->
                val values = FloatArray(size = 10) {
                    Random.nextInt(until = 100).toFloat() + 10f
                }.asList()
                add(values)
            }
        }
    }
    LazyColumn {
        items(count = 100) { index: Int ->
            Box(
                modifier = Modifier
                    .padding(all = 3.dp)
                    .height(height = 200.dp)
            ) {
                DynamicGraph(values = graphs[index])
            }
        }
    }
}

/**
 * A composable that displays a bar graph of the given [values].
 *
 * The graph is built using [MotionLayout], with the bars animating from a height of 1.dp to
 * their final height when the composable is first displayed. The animation is controlled by a
 * [MotionScene] that is dynamically generated based on the number of values provided.
 *
 * The bars are horizontally chained and their widths are calculated to fill the available space.
 * Their heights are proportional to the corresponding value in the list, scaled by the [max] value.
 *
 * Clicking on the graph will toggle the animation, causing the bars to grow or shrink.
 *
 * We start by initializing our [List] of [Float] variable `scale` to the contents of our [List]
 * of [Float] parameter [values] scaled by 0.8f divided by our [Int] parameter [max]. We initialize
 * our [Int] variable `count` to the size of our [List] of [Float] parameter [values]. We initialize
 * our [Float] variable `widthPercent` to 1 / (`count` * 2f). We initialize our [Array] of
 * [String] variable `tmpNames` to a new [Array] of [String] of size `count`. We loop over `i` for
 * all of the [Array.indices] of `tmpNames` and set the value at index `i` to the [String] formed by
 * "foo$i". We initialize our [List] of [String] variable `names` to the contents of `tmpNames` minus
 * any `null` values. We initialize our [MotionScene] variable `scene` to a [MotionScene] in whose
 * [MotionSceneScope] `motionSceneContent` lambda argument we:
 *  - Initialize our [Array] of [ConstrainedLayoutReference] variable `cols` by using the
 *  [Iterable.map] method of `names` to map each [String] in `names` to a [ConstrainedLayoutReference]
 *  whose `id` is the [String] and converting the [List] to a typed array using
 *  [Collection.toTypedArray] method.
 *  - Initialize our [ConstraintSetRef] variable `start1` to a [ConstraintSetRef] in whose
 *  [ConstraintSetScope] `constraintSet` lambda argument we create a horizontal chain of all of
 *  layouts in `cols` then loop over `i` for all of the [Array.indices] of `names` and specify the
 *  constraint for the `ref` at index `i` in `cols` to have a width of `widthPercent` and a height
 *  of 1.dp and a link from its `bottom` to the parent's `bottom` with a margin of 16.dp.
 *  - Initialize our [ConstraintSetRef] variable `end1` to a [ConstraintSetRef] in whose
 *  [ConstraintSetScope] `constraintSet` lambda argument we create a horizontal chain of all of
 *  layouts in `cols` then loop over `i` for all of the [Array.indices] of `names` and specify the
 *  constraint for the `ref` at index `i` in `cols` to have a width of `widthPercent` and a height
 *  of the value of the [Float] at index `i` in `scale`, and a link from its `bottom` to the parent's
 *  `bottom` with a `margin` of 16.dp.
 *  - Finally we use [MotionSceneScope.transition] to add a transition `from` the [ConstraintSetRef]
 *  variable `start1` to the [ConstraintSetRef] variable `end1` with the name "default".
 *
 * We initialize and remember our [MutableState] wrapped [Boolean] variable `animateToEnd` to `true`
 * and initialize and remember our [Flow] of [Boolean] variable `animateToEndFlow` to the [Flow]
 * returned by [snapshotFlow] whose `block` lambda argument is `animateToEnd`. Then we initialize
 * and remember our [Animatable] variable `progress` to an [Animatable] whose initial value is 0f.
 * We launch a [LaunchedEffect] in whose [CoroutineScope] `block` lambda argument we collect from
 * `animateToEndFlow` and in the [FlowCollector] `collector` argument we call the [Animatable.animateTo]
 * method of `progress` to animate to the target value of 1f if `animateToEnd` is `true` or 0f if
 * `animateToEnd` is `false` with an `animationSpec` of [tween] with a duration of 800 milliseconds.
 *
 * Our root composable is a [MotionLayout] whose arguments are:
 *  - `modifier`: is a [Modifier.background] whose `color` is a [Color] whose `color` is the hex
 *  value `0xFF221010` chained to a [Modifier.fillMaxSize], chained to a [Modifier.clickable] whose
 *  `onClick` lambda argument inverts the value of [Boolean] variable `animateToEnd`, and that is
 *  chained to a [Modifier.padding] that adds 1.dp to all sides.
 *  - `motionScene`: is the [MotionScene] variable `scene`.
 *  - `progress`: is the value of [Animatable] variable `progress`.
 *
 * In the [MotionLayoutScope] `content` lambda argument we loop over `i` from `0` until `count` and
 * compose a [Box] whose `modifier` argument is a [Modifier.layoutId] whose `layoutId` is the
 * [String] formed by "foo$i", chained to a [Modifier.clip] whose `shape` is a [RoundedCornerShape]
 * whose `size` is 20.dp, chained to a [Modifier.background] whose `color` is a [Color.hsv] whose
 * `hue` is `i` * 240f / `count`, `saturation` is 0.6f, and `value` is 0.6f.
 *
 * @param values The list of floats to be represented as bars in the graph.
 * @param max The maximum value that any item in [values] can have. This is used to scale the bars'
 * heights.
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun DynamicGraph(values: List<Float> = listOf(12f, 32f, 21f, 32f, 2f), max: Int = 100) {
    val scale: List<Float> = values.map { (it * 0.8f) / max }
    val count: Int = values.size
    val widthPercent: Float = 1 / (count * 2f)
    val tmpNames: Array<String?> = arrayOfNulls(size = count)
    for (i in tmpNames.indices) {
        tmpNames[i] = "foo$i"
    }
    val names: List<String> = tmpNames.filterNotNull()
    val scene = MotionScene {
        val cols: Array<ConstrainedLayoutReference> =
            names.map { createRefFor(id = it) }.toTypedArray()
        val start1: ConstraintSetRef = constraintSet {
            createHorizontalChain(elements = cols)
            for (i in names.indices) {
                constrain(ref = cols[i]) {
                    width = Dimension.percent(percent = widthPercent)
                    height = Dimension.value(dp = 1.dp)
                    bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
                }
            }
        }

        val end1: ConstraintSetRef = constraintSet {
            createHorizontalChain(elements = cols)
            for (i in names.indices) {
                constrain(ref = cols[i]) {
                    width = Dimension.percent(percent = widthPercent)
                    height = Dimension.percent(percent = scale[i])
                    bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
                }
            }
        }
        transition(from = start1, to = end1, name = "default") {
        }
    }
    var animateToEnd: Boolean by remember { mutableStateOf(value = true) }
    val animateToEndFlow: Flow<Boolean> = remember { snapshotFlow { animateToEnd } }
    val progress: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }

    // Animate on reveal
    LaunchedEffect(key1 = Unit) {
        animateToEndFlow.collect {
            progress.animateTo(
                targetValue = if (animateToEnd) 1f else 0f,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }

    MotionLayout(
        modifier = Modifier
            .background(color = Color(color = 0xFF221010))
            .fillMaxSize()
            .clickable { animateToEnd = !animateToEnd }
            .padding(all = 1.dp),
        motionScene = scene,
        progress = progress.value
    ) {
        for (i in 0..count) {
            Box(
                modifier = Modifier
                    .layoutId(layoutId = "foo$i")
                    .clip(shape = RoundedCornerShape(size = 20.dp))
                    .background(
                        color = Color.hsv(
                            hue = i * 240f / count,
                            saturation = 0.6f,
                            value = 0.6f
                        )
                    )
            )
        }
    }
}
