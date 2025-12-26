package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.MotionSceneScope

/**
 * A demo of using MotionLayout as a collapsing Toolbar using the DSL to define the MotionScene.
 *
 * This is based on using
 *
 * ```
 * Column(
 *  horizontalAlignment = Alignment.CenterHorizontally,
 *  modifier = Modifier.verticalScroll(scroll)
 * )
 * ```
 * The Column's modifier  Modifier.verticalScroll(scroll) will modify scroll.value as it scrolls.
 * We can use this value with a little math to calculate the appropriate progress.
 *
 * When the Column is at the start the MotionLayout sits on top of the Spacer. As the user scrolls
 * up the MotionLayout shrinks with the scrolling Spacer then, stops.
 *
 * We start by initializing and remembering our [ScrollState] variable `scroll` to a new instance
 * whose `initial` value is `0`. We initialize our [Dp] variable `big` to `250.dp` and `small` to
 * `50.dp`. We initialize our [MotionScene] variable `scene` to a new instance in whose
 * [MotionSceneScope] `motionSceneContent` lambda argument:
 *  - We initialize our [ConstrainedLayoutReference] variables `title`, `image`, and `icon` to
 *  the result of calling  `createRefsFor("title", "image", "icon")`.
 *  - We initialize our [ConstraintSetRef] variable `start1` to a new instance in whose
 *  [ConstraintSetScope] `constraintSetContent` lambda argument we [ConstraintSetScope.constrain]
 *  the [ConstrainedLayoutReference] variable `title` by linking its `bottom` to the `anchor` of the
 *  `bottom` of [ConstrainedLayoutReference] `image`, and linking its `start` to the `start` of
 *  [ConstrainedLayoutReference] `image`. We [ConstraintSetScope.constrain] the
 *  [ConstrainedLayoutReference] variable `image` by setting its `width` to [Dimension.matchParent],
 *  setting its `height` to the [Dimension.value] of `dp` equal to [Dp] variable `big`, linking its
 *  `top` to the `anchor` of its `parent`'s `top`, and setting its `customColor` to the `name`
 *  "cover" and `value` a [Color] whose `color` is the hex value `0x000000FF`. We
 *  [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `icon` by linking its
 *  `top` to the `anchor` of the `top` of [ConstrainedLayoutReference] `image` with a margin of
 *  `16.dp`, linking its `start` to the `start` of [ConstrainedLayoutReference] `image` with a
 *  margin of `16.dp`, and setting its `alpha` to `0f`.
 *  - We initialize our [ConstraintSetRef] variable `end1` to a new instance in whose
 *  [ConstraintSetScope] `constraintSetContent` lambda argument we [ConstraintSetScope.constrain]
 *  the [ConstrainedLayoutReference] variable `title` by linking its `bottom` to the `anchor` of
 *  the `bottom` of [ConstrainedLayoutReference] `image`, linking its `start` to the `anchor` of
 *  the `end` of [ConstrainedLayoutReference] `icon`, setting its `centerVerticallyTo` the `other`
 *  [ConstrainedLayoutReference] `image`, setting its `scaleX` to `0.7f`, and setting its `scaleY`
 *  to `0.7f`. We [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `image`
 *  by setting its `width` to [Dimension.matchParent], setting its `height` to the [Dimension.value]
 *  of `dp` equal to [Dp] variable `small`, linking its `top` to the `anchor` of its `parent`'s
 *  `top`, and setting its `customColor` to the `name` "cover" and `value` a [Color] whose `color`
 *  is the hex value `0xFF0000FF`. We [ConstraintSetScope.constrain] the [ConstrainedLayoutReference]
 *  variable `icon` by linking its `top` to the `anchor` of [ConstrainedLayoutReference] `image`
 *  with a margin of `16.dp`, and linking its `start` to the `start` of [ConstrainedLayoutReference]
 *  `image` with a margin of `16.dp`.
 *  - We use [MotionSceneScope.transition] to transition `from` [ConstraintSetRef] variable `start1`
 *  to [ConstraintSetRef] variable `end1` with the `name` "default".
 *
 * We compose a [Column] whose `horizontalAlignment` is [Alignment.CenterHorizontally] and whose
 * `modifier` is a [Modifier.verticalScroll] whose `state` is our [ScrollState] variable `scroll`.
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we compose a
 * [Spacer] whose `modifier` is a [Modifier.height] whose `height` is [Dp] variable `big`. Then we
 * [repeat] 5 times the composing of a [Text] whose `text` is 222 words of the nonsense produced by
 * [LoremIpsum], and whose `modifer` argument is a [Modifier.background] whose `color` is
 * [Color.White] chained to a [Modifier.padding] that adds `16.dp` to `all` sides.
 *
 * We initialize our [Float] variable `gap` to the difference of the pixel value of [Dp] variable
 * `big` and the pixel value of [Dp] variable `small`. We initialize our [Float] variable `progress`
 * to the minimum of `scroll.value` divided by `gap`, and `1f`.
 *
 * We compose a [MotionLayout] whose `modifier` is a [Modifier.fillMaxSize], whose `motionScene` is
 * our [MotionScene] variable `scene`, and whose `progress` is our [Float] variable `progress`.
 * In its [MotionLayoutScope] `content` composable lambda argument:
 *  - We compose an [Image] whose `modifier` is a [Modifier.layoutId] whose `layoutId` is the
 *  [ConstrainedLayoutReference] variable `image`, chained to a [Modifier.background] whose `color`
 *  is the [MotionLayoutScope.customColor] whose `id` is the [ConstrainedLayoutReference] "image"
 *  and whose `name` is "cover". The `painter` of the [Image] is the [painterResource] whose `id`
 *  is the `jpg` with resource ID `R.drawable.bridge`. The `contentDescription` of the [Image] is
 *  `null`. The `contentScale` of the [Image] is [ContentScale.Crop].
 *  - We compose an [Image] whose `modifier` is a [Modifier.layoutId] whose `layoutId` is the
 *  [ConstrainedLayoutReference] variable `icon`. The `painter` of the [Image] is the
 *  [painterResource] whose `id` is the `vector` drawn by resource ID `R.drawable.menu`. The
 *  `contentDescription` of the [Image] is `null`.
 *  - We compose a [Text] whose `modifier` is a [Modifier.layoutId] whose `layoutId` is the
 *  [ConstrainedLayoutReference] variable `title`, and whose `text` is "San Francisco", whose
 *  `fontSize` is `30.sp`, and whose `color` is [Color.White].
 */
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun ToolBarExampleDsl() {
    val scroll: ScrollState = rememberScrollState(initial = 0)
    val big = 250.dp
    val small = 50.dp
    val scene = MotionScene {
        val (title: ConstrainedLayoutReference,
            image: ConstrainedLayoutReference,
            icon: ConstrainedLayoutReference) =
            createRefsFor(
                "title",
                "image",
                "icon"
            )

        val start1: ConstraintSetRef = constraintSet {
            constrain(ref = title) {
                bottom.linkTo(anchor = image.bottom)
                start.linkTo(anchor = image.start)
            }
            constrain(ref = image) {
                width = Dimension.matchParent
                height = Dimension.value(dp = big)
                top.linkTo(anchor = parent.top)
                customColor(name = "cover", value = Color(color = 0x000000FF))
            }
            constrain(ref = icon) {
                top.linkTo(anchor = image.top, margin = 16.dp)
                start.linkTo(anchor = image.start, margin = 16.dp)
                alpha = 0f
            }
        }
        val end1: ConstraintSetRef = constraintSet {
            constrain(ref = title) {
                bottom.linkTo(anchor = image.bottom)
                start.linkTo(anchor = icon.end)
                centerVerticallyTo(other = image)
                scaleX = 0.7f
                scaleY = 0.7f
            }
            constrain(ref = image) {
                width = Dimension.matchParent
                height = Dimension.value(dp = small)
                top.linkTo(anchor = parent.top)
                customColor(name = "cover", value = Color(color = 0xFF0000FF))
            }
            constrain(ref = icon) {
                top.linkTo(anchor = image.top, margin = 16.dp)
                start.linkTo(anchor = image.start, margin = 16.dp)
            }
        }
        transition(from = start1, to = end1, name = "default") {}
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(state = scroll)
    ) {
        Spacer(modifier = Modifier.height(height = big))
        repeat(times = 5) {
            Text(
                text = LoremIpsum(words = 222).values.first(),
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(all = 16.dp)
            )
        }
    }
    val gap: Float = with(receiver = LocalDensity.current) { big.toPx() - small.toPx() }

    @SuppressLint("FrequentlyChangingValue")
    val progress: Float = minOf(a = scroll.value / gap, b = 1f)

    MotionLayout(
        modifier = Modifier.fillMaxSize(),
        motionScene = scene,
        progress = progress
    ) {
        Image(
            modifier = Modifier
                .layoutId(layoutId = "image")
                .background(color = customColor(id = "image", name = "cover")),
            painter = painterResource(id = R.drawable.bridge),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Image(
            modifier = Modifier.layoutId(layoutId = "icon"),
            painter = painterResource(id = R.drawable.menu),
            contentDescription = null
        )
        Text(
            modifier = Modifier.layoutId(layoutId = "title"),
            text = "San Francisco",
            fontSize = 30.sp,
            color = Color.White
        )
    }
}