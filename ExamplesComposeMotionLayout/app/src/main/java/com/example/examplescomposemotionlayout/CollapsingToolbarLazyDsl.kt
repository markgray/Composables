package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
 * A demo using MotionLayout as a collapsing Toolbar using the DSL to define the MotionScene, where
 * the scrolling of the LazyColumn is obtained with a NestedScrollConnection.
 *
 * ```
 * LazyColumn(
 *   Modifier
 *     .fillMaxWidth()
 *     .nestedScroll(nestedScrollConnection)
 * ) {
 *   items(100) {
 *     Text(text = "item $it", modifier = Modifier.padding(4.dp))
 *   }
 * }
 * ```
 *
 * A NestedScrollConnection object is passed to a LazyColumn Composable via a modifier
 * (Modifier.nestedScroll(nestedScrollConnection)).
 *
 * When the [NestedScrollConnection.onPreScroll] override of the [NestedScrollConnection] is called
 * it returns the amount of "offset" to absorb and uses the offset to collapse the [MotionLayout].
 *
 * We start by initializing our [Dp] variable `big` to `250.dp` and [Dp] variable `small` to `50.dp`.
 * We initialize our [MotionScene] variable `scene` to a new instance in whose [MotionSceneScope]
 * `motionSceneContent` lambda argument:
 *  - We initialize our [ConstrainedLayoutReference] variable `title` to a new instance whose `id`
 *  is "title".
 *  - We initialize our [ConstrainedLayoutReference] variable `image` to a new instance whose `id`
 *  is "image".
 *  - We initialize our [ConstrainedLayoutReference] variable `icon` to a new instance whose `id`
 *  is "icon".
 *  - We initialize our [ConstraintSetRef] variable `start1` to a new instance in whose
 *  [ConstraintSetScope] `constraintSetContent` lambda argument we [ConstraintSetScope.constrain]
 *  the [ConstrainedLayoutReference] variable `title` by linking its `bottom` to the `bottom` of
 *  [ConstrainedLayoutReference] variable `image`, and by linking its `start` to the `start` of
 *  [ConstrainedLayoutReference] variable `image`. We [ConstraintSetScope.constrain] the
 *  [ConstrainedLayoutReference] variable `image` by setting its `width` to [Dimension.matchParent],
 *  setting its `height` to the [Dimension.value] of `dp` equal to [Dp] variable `big`, linking its
 *  `top` to the `anchor` of its `parent`'s `top`, and setting its `customColor` to the `name`
 *   "cover" and `value` a [Color] whose `color` is the hex value `0x000000FF`. We
 *   [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `icon` by linking its
 *   `top` to the `anchor` of the `top` of [ConstrainedLayoutReference] `image` with a margin of
 *   `16.dp`, linking its `start` to the `start` of [ConstrainedLayoutReference] `image` with a
 *   margin of `16.dp`, and setting its `alpha` to `0f`.
 *   - We initialize our [ConstraintSetRef] variable `end1` to a new instance in whose
 *   [ConstraintSetScope] `constraintSetContent` lambda argument we [ConstraintSetScope.constrain]
 *   the [ConstrainedLayoutReference] variable `title` by linking its `bottom` to the `anchor` of
 *   the `bottom` of [ConstrainedLayoutReference] `image`, linking its `start` to the `anchor` of
 *   the `end` of [ConstrainedLayoutReference] `icon`, setting its `centerVerticallyTo` the `other`
 *   [ConstrainedLayoutReference] `image`, setting its `scaleX` to `0.7f`, and setting its `scaleY`
 *   to `0.7f`. We [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `image`
 *   by setting its `width` to [Dimension.matchParent], setting its `height` to the [Dimension.value]
 *   of `dp` equal to [Dp] variable `small`, linking its `top` to the `anchor` of its `parent`'s
 *   `top`, and setting its `customColor` to the `name` "cover" and `value` a [Color] whose `color`
 *   is the hex value `0xFF0000FF`. We [ConstraintSetScope.constrain] the [ConstrainedLayoutReference]
 *   variable `icon` by linking its `top` to the `anchor` of [ConstrainedLayoutReference] `image`
 *   with a margin of `16.dp`, and linking its `start` to the `start` of [ConstrainedLayoutReference]
 *   `image` with a margin of `16.dp`.
 *   - We use [MotionSceneScope.transition] to create a transition `from` [ConstraintSetRef]
 *   variable `start1` to [ConstraintSetRef] variable `end1` with the `name` "default"
 *   ([MotionLayout] will animate between these two [ConstraintSetRef] based on the current value
 *   of its `progress` argument).
 *
 * We initialize our [Float] variable `maxPx` to the value of [Dp] variable `big` converted to pixels,
 * and initialize our [Float] variable `minPx` to the value of [Dp] variable `small` converted to
 * pixels. We initialize and remember our [MutableState] wrapped [Float] variable `toolbarHeight`
 * to an initial value of `maxPx`. We initialize and remember our [NestedScrollConnection] variable
 * `nestedScrollConnection` to an anonymous instance which overrides the method
 * [NestedScrollConnection.onPreScroll] to return an appropriate [Offset] for the current value of
 * `toolbarHeight`.
 *
 * We initialize our [Float] variable `progress` to `1` minus the current value of `toolbarHeight`
 * minus `minPx` divided by the quantity `maxPx` minus `minPx` (this is used as the `progress`
 * argument argument of our [MotionLayout] and represents the fraction of the space between `minPx`
 * and `maxPx` currently occupied by the height of the toolbar).
 *
 * Our root composable is a [Column] in whose [ColumnScope] `content` composable lambda argument
 * we compose a [MotionLayout] whose `motionScene` is our [MotionScene] variable `scene`, and whose
 * `progress` is our [Float] variable `progress`. In the [MotionLayoutScope] `content` composable
 * lambda argument we:
 *  - Compose an [Image] whose `modifier` argument is a [Modifier.layoutId] whose `layoutId` is
 *  "image", chained to a [Modifier.background] whose `color` is the [MotionLayoutScope.customColor]
 *  whose `id` is "image" and whose `name` is "cover". The `painter` is a [painterResource] for the
 *  jpg with resource ID `R.drawable.bridge`. The `contentDescription` is `null`. The `contentScale`
 *  is [ContentScale.Crop].
 *  - Compose an [Image] whose `modifier` argument is a [Modifier.layoutId] whose `layoutId is "icon",
 *  whose `painter` is a [painterResource] for the vector drawn by resource ID `R.drawable.menu`,
 *  and whose `contentDescription` is `null`.
 *  - Compose a [Text] whose `modifier` argument is a [Modifier.layoutId] whose `layoutId` is
 *  "title", whose `text` is "San Francisco", whose `fontSize` is `30.sp`, and whose `color` is
 *  [Color.White].
 *
 * Below the [MotionLayout] in the [Column] we compose a [LazyColumn] whose `modifier` argument
 * is a [Modifier.fillMaxWidth] chained to a [Modifier.nestedScroll] whose `connection` is our
 * [NestedScrollConnection] variable `nestedScrollConnection`. In the [LazyListScope] `content`
 * composable lambda argument of the [LazyColumn] we compose an [LazyListScope.items] whose `count`
 * is `100`. In its [LazyItemScope] `itemContent` [Composable] lambda argument we accept the [Int]
 * passed the lambda in variable `it` and compose a [Text] whose `text` is "item $it", and whose
 * `modifier` argument is a [Modifier.padding] that adds `4.dp` to all sides.
 */
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun ToolBarLazyExampleDsl() {
    val big: Dp = 250.dp
    val small: Dp = 50.dp
    val scene = MotionScene {
        val title: ConstrainedLayoutReference = createRefFor(id = "title")
        val image: ConstrainedLayoutReference = createRefFor(id = "image")
        val icon: ConstrainedLayoutReference = createRefFor(id = "icon")

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

    val maxPx: Float = with(LocalDensity.current) { big.roundToPx().toFloat() }
    val minPx: Float = with(LocalDensity.current) { small.roundToPx().toFloat() }
    val toolbarHeight: MutableFloatState = remember { mutableFloatStateOf(value = maxPx) }

    val nestedScrollConnection: NestedScrollConnection = remember {
        object : NestedScrollConnection {
            /**
             * Pre-scroll event handler. This is called before the scrolling container consumes the
             * scroll event. It allows us to "steal" the scroll delta to adjust the height of our
             * toolbar.
             *
             * We calculate the new potential height of the toolbar by adding the vertical scroll
             * delta (`available.y`).
             *
             *  - If the new height would exceed the maximum height (`maxPx`), we clamp the height
             *  to `maxPx` and consume only the delta required to reach that maximum.
             *  - If the new height would be less than the minimum height (`minPx`), we clamp the
             *  height to `minPx` and consume only the delta required to reach that minimum.
             *  - Otherwise, we adjust the toolbar height by the full vertical scroll delta and
             *  consume the entire delta, preventing the `LazyColumn` from scrolling.
             *
             * @param available The scroll delta available for consumption.
             * @param source The source of the nested scroll event.
             * @return The amount of scroll delta that was consumed by this connection. We only
             * fiddle with the vertical (y) component.
             */
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val height: Float = toolbarHeight.floatValue

                if (height + available.y > maxPx) {
                    toolbarHeight.floatValue = maxPx
                    return Offset(x = 0f, y = maxPx - height)
                }

                if (height + available.y < minPx) {
                    toolbarHeight.floatValue = minPx
                    return Offset(x = 0f, y = minPx - height)
                }

                toolbarHeight.floatValue += available.y
                return Offset(x = 0f, y = available.y)
            }
        }
    }

    val progress: Float = 1 - (toolbarHeight.floatValue - minPx) / (maxPx - minPx)

    Column {
        MotionLayout(
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
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .nestedScroll(connection = nestedScrollConnection)
        ) {
            items(count = 100) {
                Text(text = "item $it", modifier = Modifier.padding(all = 4.dp))
            }
        }
    }
}
