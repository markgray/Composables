package com.example.examplescomposemotionlayout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.MotionSceneScope

/**
 * A demonstration of using [MotionLayout] to create animated, expandable items within a [LazyColumn].
 *
 * In this example, each item in the list is a [MotionLayout] instance that can be expanded or
 * collapsed by clicking an icon. The animation state (progress) is driven by `animateFloatAsState`,
 * which smoothly transitions between 0f (collapsed) and 1f (expanded).
 *
 * The collapsed and expanded states are defined programmatically using the [MotionScene] DSL. A
 * `remember`ed [BooleanArray] is used to persist the expanded/collapsed state of each item across
 * recompositions and scrolling.
 *
 * We start by initializing our [MotionScene] variable `scene` to a new instance in whose
 * [MotionSceneScope] `motionSceneContent` lambda argument we initialize our
 * [ConstrainedLayoutReference] variable `title` to a new instance whose `id` is the [String] "title",
 * ininitialize our [ConstrainedLayoutReference] variable `image` to a new instance whose `id` is
 * the [String] "image", and initialize our [ConstrainedLayoutReference] variable `icon` to a new
 * instance whose `id` is the [String] "icon".
 *
 * We initalize our [ConstraintSetRef] variable `start1` to a new instance in whose [ConstraintSetScope]
 * `constraintSetContent` lambda argument we:
 *  - [ConstraintSetScope.constrain] our [ConstrainedLayoutReference] variable `title` by using the
 *  [ConstrainScope.centerVerticallyTo] method to have it vertically center itself with respect to
 *  the [ConstrainedLayoutReference] variable `icon`, and by linking its [ConstrainScope.start] to
 *  the `end` of `icon` with a `margin` of `16.dp`.
 *  - [ConstraintSetScope.constrain] our [ConstrainedLayoutReference] variable `image` by setting its
 *  [ConstrainScope.width] to `40.dp`, its [ConstrainScope.height] to `40.dp`, using the method
 *  [ConstrainScope.centerVerticallyTo] to center it vertically to the [ConstrainedLayoutReference]
 *  variable `icon`, and by linking its [ConstrainScope.end] to the `end` of its `parent` with a
 *  margin of `8.dp`.
 *  - [ConstraintSetScope.constrain] our [ConstrainedLayoutReference] variable `icon` by linking its
 *  [ConstrainScope.top] to the `top` of its `parent` with a `margin` of `16.dp`, by linking its
 *  [ConstrainScope.bottom] to the `bottom` of its `parent` with a `margin` of `16.dp`, and by
 *  linking its [ConstrainScope.start] to the `start` of its `parent` with a `margin` of `16.dp`.
 *
 * We initalize our [ConstraintSetRef] variable `end1` to a new instance in whose [ConstraintSetScope]
 * `constraintSetContent` lambda argument we:
 *  - [ConstraintSetScope.constrain] our [ConstrainedLayoutReference] variable `title` by linking its
 *  [ConstrainScope.bottom] to the `bottom` of its `parent`, by linking its [ConstrainScope.start] to
 *  the `start` of its `parent`, and by setting its [ConstrainScope.scaleX] to `0.7f` and its
 *  [ConstrainScope.scaleY] to `0.7f`.
 *  - [ConstraintSetScope.constrain] our [ConstrainedLayoutReference] variable `image` by setting its
 *  [ConstrainScope.width] to [Dimension.matchParent], by setting its [ConstrainScope.height] to
 *  `200.dp`, and by using the method [ConstrainScope.centerVerticallyTo] to center it vertically to
 *  its `parent`.
 *  - [ConstraintSetScope.constrain] our [ConstrainedLayoutReference] variable `icon` by linking its
 *  [ConstrainScope.top] to the `top` of its `parent` with a `margin` of `16.dp`, and linking its
 *  [ConstrainScope.start] to the `start` of its `parent` with a `margin` of `16.dp`.
 *
 * We use [MotionSceneScope.transition] to create a transition `from` [ConstraintSetRef]
 * variable `start1` to [ConstraintSetRef] variable `end1` with the `name` "default"
 * ([MotionLayout] will animate between these two [ConstraintSetRef] based on the current value
 * of its `progress` argument).
 *
 * Having defined our [MotionScene], we initialize and remember [BooleanArray] variable `model` to
 * an instance whose `size` is `100`. Our root composable is a [LazyColumn] in whose [LazyListScope]
 * `content` lambda argument we compose a [LazyListScope.items] whose `count` is `100`.
 * TODO: Continue here.
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun MotionInLazyColumnDsl() {
    val scene = MotionScene {
        val title: ConstrainedLayoutReference = createRefFor(id = "title")
        val image: ConstrainedLayoutReference = createRefFor(id = "image")
        val icon: ConstrainedLayoutReference = createRefFor(id = "icon")

        val start1: ConstraintSetRef = constraintSet {
            constrain(ref = title) {
                centerVerticallyTo(other = icon)
                start.linkTo(anchor = icon.end, margin = 16.dp)
            }
            constrain(ref = image) {
                width = Dimension.value(dp = 40.dp)
                height = Dimension.value(dp = 40.dp)
                centerVerticallyTo(other = icon)
                end.linkTo(anchor = parent.end, margin = 8.dp)
            }
            constrain(ref = icon) {
                top.linkTo(anchor = parent.top, margin = 16.dp)
                bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
                start.linkTo(anchor = parent.start, margin = 16.dp)
            }
        }

        val end1: ConstraintSetRef = constraintSet {
            constrain(ref = title) {
                bottom.linkTo(anchor = parent.bottom)
                start.linkTo(anchor = parent.start)
                scaleX = 0.7f
                scaleY = 0.7f
            }
            constrain(ref = image) {
                width = Dimension.matchParent
                height = Dimension.value(dp = 200.dp)
                centerVerticallyTo(other = parent)
            }
            constrain(ref = icon) {
                top.linkTo(anchor = parent.top, margin = 16.dp)
                start.linkTo(anchor = parent.start, margin = 16.dp)
            }
        }
        transition(from = start1, to = end1, name = "default") {}
    }

    val model: BooleanArray = remember { BooleanArray(size = 100) }

    LazyColumn {
        items(count = 100) { index: Int ->
            Box(modifier = Modifier.padding(all = 3.dp)) {
                var animateToEnd: Boolean by remember { mutableStateOf(value = model[index]) }

                val progress: Float by animateFloatAsState(
                    targetValue = if (animateToEnd) 1f else 0f,
                    animationSpec = tween(durationMillis = 700),
                    label = ""
                )

                MotionLayout(
                    modifier = Modifier
                        .background(color = Color(color = 0xFF331B1B))
                        .fillMaxWidth()
                        .padding(all = 1.dp),
                    motionScene = scene,
                    progress = progress
                ) {
                    Image(
                        modifier = Modifier.layoutId(layoutId = "image"),
                        painter = painterResource(id = R.drawable.bridge),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Image(
                        modifier = Modifier
                            .layoutId(layoutId = "icon")
                            .clickable {
                                animateToEnd = !animateToEnd
                                model[index] = animateToEnd
                            },
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.layoutId(layoutId = "title"),
                        text = "San Francisco $index",
                        fontSize = 30.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
