@file:Suppress("UNUSED_PARAMETER", "RedundantSuppression")

package com.example.examplescomposemotionlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
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
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.MotionSceneScope
import kotlin.random.Random


/**
 * A demonstration of integrating a [MotionLayout] within a [HorizontalPager].
 *
 * This composable sets up a [HorizontalPager] with a fixed number of pages. Each page
 * contains a [DynamicPages] composable, which uses [MotionLayout]. The motion of the
 * [DynamicPages] is driven by the pager's scroll progress (a rotatation around the
 * `Z` axis), creating a dynamic, interactive transition as the user swipes between pages.
 *
 * A list of random colors is generated to give each page a unique background for its
 * animated element.
 *
 * We start by initializing our [Random.Default] variable `rand` to a new instance of [Random]
 * (but never use it). We initialize our [Int] variable `count` to a value of `100`, and initialize
 * our [MutableList] of [Color] variable `graphs` to an empty mutable list. We loop over `i` from
 * 0 to `count` using the [MutableList.add] method of `graphs` to add a [Color] object to the list
 * whose `hue` is calculated using the formula `i` times `142f` modulo `360`, its `saturation` is
 * `0.5`, and its `value` is `0.6f`. We initialize and remember our [PagerState] variable `pagerState`
 * to an instance whose `pageCount` argument is a lambda returning our [Int] variable `count`.
 *
 * Our root composable is a [HorizontalPager] whose `state` argument is our [PagerState] variable
 * `pagerState`. In the [PagerScope] `pageContent` Composable lambda argument we accept the [Int]
 * passed the lambda in variable `page`, and initialize our [Float] variable `pageOffset` to the
 * [PagerState.currentPage] of our [PagerState] variable `pagerState` minus the [Int] variable `page`
 * plus the [PagerState.currentPageOffsetFraction] of `pagerState`. Then we compose a [DynamicPages]
 * whose arguments are:
 *  - `colorValue`: is the [Color] at index `page` in our [MutableList] of [Color] variable `graphs`
 *  - `pagerProgress`: is `pageOffset`
 *  - `pageNumber`: is `page`
 */
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun MotionPager() {
    @Suppress("UNUSED_VARIABLE")
    val rand: Random.Default = Random
    val count = 100
    val graphs: MutableList<Color> = mutableListOf()
    for (i in 0..count) {
        graphs.add(element = Color.hsv(hue = (i * 142f) % 360, saturation = 0.5f, value = 0.6f))
    }

    val pagerState: PagerState = rememberPagerState(pageCount = { count })

    HorizontalPager(state = pagerState) { page: Int ->
        // Our page content
        val pageOffset: Float =
            pagerState.currentPage - page + pagerState.currentPageOffsetFraction
        DynamicPages(
            colorValue = graphs[page],
            pagerProgress = pageOffset,
            pageNumber = page
        )
    }
}

/**
 * A composable representing a single page within a [HorizontalPager].
 *
 * This function uses [MotionLayout] to create an animation that is controlled by the pager's
 * scroll progress. The animation consists of a colored [Box] that rotates 360 degrees as the
 * user swipes to the next or previous page.
 *
 * The animation is defined by a [MotionScene] with a `start` and `end` [ConstraintSetRef].
 * In the `start` set, the box has a `rotationZ` of 360 degrees. In the `end` set, the
 * `rotationZ` is 0 (the default). The `progress` of the [MotionLayout] is driven by the
 * `pagerProgress` parameter, which links the animation state directly to the scroll position
 * of the pager.
 *
 * We start by initializing our [String] variable `boxId` to the string "box". We initialize our
 * [MotionScene] variable `scene` with a new instance in whose [MotionSceneScope] `motionSceneContent`
 * lambda argument we initialize our [ConstrainedLayoutReference] variable `box` to a new instance
 * whose `id` argument is our [String] variable `boxId`. We initialize our [ConstraintSetRef]
 * variable `start1` to a new instance in whose [ConstraintSetScope] `constraintSetContent` lambda
 * argument we [ConstraintSetScope.constrain] the `box` [ConstrainedLayoutReference] to have a
 * [ConstrainScope.width] of `.5f` `percent`, a [ConstrainScope.height] of `.5f` `percent`, a
 * [ConstrainScope.rotationZ] of `360f`, and a [ConstrainScope.centerTo] the `parent`. We initialize
 * our [ConstraintSetRef] variable `end1` to a new instance in whose [ConstraintSetScope]
 * `constraintSetContent` lambda argument we [ConstraintSetScope.constrain] the `box`
 * [ConstrainedLayoutReference] to have a [ConstrainScope.width] of `.5f` `percent`, a
 * [ConstrainScope.height] of `.5f, and to have a [ConstrainScope.centerTo] the `parent`. We then
 * add a transition to our [MotionScene] variable `scene` `from` the `start1` [ConstraintSetRef] to
 * the `end1` [ConstraintSetRef] with the name "default".
 *
 * Our root composable is a [MotionLayout] whose arguments are:
 *  - `modifier`: is a [Modifier.background] whose `color` is the [Color] whose hex value `0xFF221010`,
 *  chained to a [Modifier.fillMaxWidth], chained to a [Modifier.height] whose `height` is `300.dp`,
 *  chained to a [Modifier.padding] that adds `1.dp` to `all` sides.
 *  - `motionScene`: is our [MotionScene] variable `scene`.
 *  - `progress`: is our [Float] parameter [pagerProgress].
 *
 * In the [MotionLayoutScope] `content` Composable lambda argument of the [MotionLayout] we compose
 * a [Box] whose `modifier` argument is a [Modifier.layoutId] whose `layoutId` argument is our
 * [String] variable `boxId`, chained to a [Modifier.clip] whose `shape` is a [RoundedCornerShape]
 * whose `size` is `20.dp`, chained to a [Modifier.background] whose `color` is our [Color] parameter
 * [colorValue]. In the [BoxScope] `content` Composable lambda argument of the [Box] we compose a
 * [Text] whose arguments are:
 *  - `text`: is our [Int] parameter [pageNumber] converted to a [String].
 *  - `fontSize`: is `32.sp`.
 *  - `modifier`: is a [BoxScope.align] whose `alignment` is [Alignment.Center].
 *
 * @param colorValue The background color of the animated [Box].
 * @param max The maximum number of pages, although this parameter is not used in the current
 * implementation.
 * @param pagerProgress The scroll progress of the pager, typically ranging from -1.0 to 1.0.
 * This value is used to drive the [MotionLayout] animation.
 * @param pageNumber The index of the current page, which is displayed as text inside the [Box].
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun DynamicPages(
    colorValue: Color = Color.Green,
    max: Int = 100,
    pagerProgress: Float = 1f,
    pageNumber: Int = 1
) {
    val boxId = "box"
    val scene = MotionScene {
        val box: ConstrainedLayoutReference = createRefFor(id = boxId)
        val start1: ConstraintSetRef = constraintSet {
            constrain(ref = box) {
                width = Dimension.percent(percent = .5f)
                height = Dimension.percent(percent = .5f)
                rotationZ = 360f
                centerTo(other = parent)
            }
        }

        val end1 = constraintSet {
            constrain(ref = box) {
                width = Dimension.percent(percent = .5f)
                height = Dimension.percent(percent = .5f)
                centerTo(other = parent)
            }
        }

        transition(from = start1, to = end1, name = "default") {
        }
    }
    MotionLayout(
        modifier = Modifier
            .background(color = Color(color = 0xFF221010))
            .fillMaxWidth()
            .height(height = 300.dp)
            .padding(all = 1.dp),
        motionScene = scene,
        progress = if (pagerProgress < 0) 1 + pagerProgress else pagerProgress
    ) {

        Box(
            modifier = Modifier
                .layoutId(layoutId = boxId)
                .clip(shape = RoundedCornerShape(size = 20.dp))
                .background(color = colorValue)
        ) {
            Text(
                text = "$pageNumber",
                fontSize = 32.sp,
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
    }
}
