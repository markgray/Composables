@file:Suppress("DEPRECATION", "UNUSED_PARAMETER")

package com.example.examplescomposemotionlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import kotlin.random.Random


/**
 * A demo of using MotionLayout in a com.google.accompanist.pager.HorizontalPager
 */
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun MotionPager() {
    @Suppress("UNUSED_VARIABLE")
    val rand = Random
    val count = 100
    val graphs = mutableListOf<Color>()
    for (i in 0..count) {
        graphs.add(Color.hsv((i * 142f) % 360, 0.5f, 0.6f))
    }

    val pagerState = com.google.accompanist.pager.rememberPagerState()

    HorizontalPager(count = graphs.size, state = pagerState) { page ->
        // Our page content
        val pageOffset = calculateCurrentOffsetForPage(page)
        DynamicPages(graphs[page], pagerProgress = pageOffset, pageNumber = page)
    }
}

/**
 * TODO: Add kdoc
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
        val box = createRefFor(boxId)
        val start1 = constraintSet {
            constrain(box) {

                width = Dimension.percent(.5f)
                height = Dimension.percent(.5f)
                rotationZ = 360f

                centerTo(parent)
            }
        }

        val end1 = constraintSet {

            constrain(box) {
                width = Dimension.percent(.5f)
                height = Dimension.percent(.5f)

                centerTo(parent)
            }
        }
        transition(start1, end1, "default") {
        }
    }
    MotionLayout(
        modifier = Modifier
            .background(Color(0xFF221010))
            .fillMaxWidth()
            .height(300.dp)
            .padding(1.dp),
        motionScene = scene,
        progress = if (pagerProgress < 0) 1 + pagerProgress else pagerProgress
    ) {

        Box(
            modifier = Modifier
                .layoutId(boxId)
                .clip(RoundedCornerShape(20.dp))
                .background(colorValue)
        ) {
            Text(
                text = "$pageNumber",
                fontSize = 32.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
