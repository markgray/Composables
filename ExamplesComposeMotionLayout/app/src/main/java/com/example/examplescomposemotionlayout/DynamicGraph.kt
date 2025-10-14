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
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import kotlinx.coroutines.flow.Flow
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
 * our [Float] TODO: Continue here.
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
            names.map { createRefFor(it) }.toTypedArray()
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
                    .background(color = Color.hsv(
                        hue = i * 240f / count,
                        saturation = 0.6f,
                        value = 0.6f
                    ))
            )
        }
    }
}
