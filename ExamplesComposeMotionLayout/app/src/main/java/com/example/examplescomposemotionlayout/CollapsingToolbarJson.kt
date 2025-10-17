package com.example.examplescomposemotionlayout

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

/**
 * A demo of a collapsing toolbar effect built with [MotionLayout].
 *
 * This Composable demonstrates how to use a [MotionScene] defined in a JSON5 string
 * to transition between a fully expanded toolbar (with a large image and title)
 * and a collapsed one. The transition is driven by the scroll position of a
 * vertical scrolling [Column].
 *
 * The [MotionLayout] contains an image, a title, and an icon. The progress of the
 * transition is calculated based on the scroll state, causing the layout to
 * animate between its `start` and `end` constraint sets.
 *
 * @see MotionLayout
 * @see MotionScene
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun ToolBarExample() {
    val scroll = rememberScrollState(initial = 0)

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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(state = scroll)
    ) {
        Spacer(modifier = Modifier.height(height = 250.dp))
        repeat(times = 5) {
            Text(
                text = LoremIpsum(words = 222).values.first(),
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(all = 16.dp)
            )
        }
    }

    val progress = java.lang.Float.min(scroll.value / (3f * (250 - 50)), 1f)

    MotionLayout(
        modifier = Modifier.fillMaxSize(),
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
}
