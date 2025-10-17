package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
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
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

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
 * When the onPreScroll of the NestedScrollConnection is called It returns the amount of "offset" to
 * absorb and uses the offset to collapse the MotionLayout.
 *
 * We start by initializing our [Dp] variable `big` to `250.dp` and [Dp] variable `small` to `50.dp`.
 * TODO: Continue here.
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
