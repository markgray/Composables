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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

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
 * whose `initial` value is `0`. TODO: Continue here.
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