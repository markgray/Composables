package com.example.examplescomposemotionlayout

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

/**
 * A demo of a collapsing toolbar created with [MotionLayout], with the scroll driven by a
 * [LazyColumn].
 *
 * The [MotionScene] is defined using a JSON string. The animation progress is manually controlled
 * by listening to scroll events from the [LazyColumn] through a [NestedScrollConnection].
 * The height of the toolbar is adjusted based on the scroll delta, and this height change is
 * then converted into a progress value (from 0.0 to 1.0) to drive the [MotionLayout] transition.
 * TODO: Continue here.
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun ToolBarLazyExample() {
    @Suppress("UNUSED_VARIABLE", "unused")
    val scroll: ScrollState = rememberScrollState(initial = 0)

    val scene = """
      {
        ConstraintSets: {
          start: {
            title: {
              bottom: ['image', 'bottom', 16],                
              start: [ 'image','start', 16],
              },
            image: {
              width: 'parent',
              height: 250,
              top: ['parent', 'top', 0],
              custom: {
                cover: '#000000FF'
              }
            },
            icon: {
              top: ['image', 'top', 16],
              start: [ 'image','start', 16],
              alpha: 0,
            },
          },
          end: {
            title: {
              centerVertically: 'image',
              start: ['icon', 'end', 0],
              scaleX: 0.7,
              scaleY: 0.7,
            },
            image: {
              width: 'parent',
              height: 50,
              top: ['parent', 'top', 0],
              custom: {
                cover: '#FF0000FF'
              }
            },
            icon: {
              top: ['image', 'top', 16],
              start: [ 'image','start', 16],
            },
          },
        },
        Transitions: {
          default: {
            from: 'start',
            to: 'end',
            pathMotionArc: 'startHorizontal',
          },
        },
      }
      """

    val maxPx: Float = with(receiver = LocalDensity.current) { 250.dp.roundToPx().toFloat() }
    val minPx: Float = with(receiver = LocalDensity.current) { 50.dp.roundToPx().toFloat() }
    val toolbarHeight: MutableFloatState = remember { mutableFloatStateOf(value = maxPx) }

    val nestedScrollConnection: NestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val height = toolbarHeight.floatValue

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
            modifier = Modifier.background(color = Color.Green),
            motionScene = MotionScene(content = scene),
            progress = progress
        ) {
            Image(
                modifier = Modifier.layoutId(layoutId = "image"),
                painter = painterResource(id = R.drawable.bridge),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .layoutId(layoutId = "image")
                    .background(color = customProperties(id = "image").color(name = "cover"))
            ) {
            }
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(connection = nestedScrollConnection)
        ) {
            LazyColumn {
                items(count = 100) {
                    Text(text = "item $it", modifier = Modifier.padding(all = 4.dp))
                }
            }
        }
    }
}
