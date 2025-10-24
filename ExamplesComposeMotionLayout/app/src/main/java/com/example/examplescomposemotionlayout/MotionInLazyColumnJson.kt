package com.example.examplescomposemotionlayout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

/**
 * A demo of using MotionLayout in a LazyColumn where each item can be expanded/collapsed.
 * The state of each item is preserved when scrolling. The [MotionScene] is defined using JSON5.
 *
 * This demo is identical to the [MotionInLazyColumnDsl] demo in the file `MotionInLazyColumnDsl.kt`
 * apart from the use of JSON5 for defining the [MotionScene] instead of kotlin code so I won't
 * bother to comment it.
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:width=480dp,height=800dp,dpi=440")
@Composable
fun MotionInLazyColumn() {

    val scene = """
      {
        ConstraintSets: {
          start: {
            title: {
              centerVertically: 'icon', 
              start: [ 'icon','end', 16],
              },
            image: {
              width: 40,
              height: 40,
               centerVertically: 'icon', 
              end: ['parent', 'end', 8],
            },
            icon: {
              top: ['parent', 'top', 8],
              start: [ 'parent','start', 9],
              bottom: ['parent', 'bottom', 8],
              alpha: 1,
            },
          },
          
          end: {
            title: {
              bottom: ['image', 'bottom', 0],
              start: ['image', 'start', 0],
              scaleX: 0.7,
              scaleY: 0.7,
            },
            image: {
              width: 'parent',
              height: 200,
              centerVertically: 'parent',
            },
            icon: {
              top: ['parent', 'top', 16],
              start: [ 'parent','start', 16],
               alpha: 1,
            },
          },
        },
        Transitions: {
          default: {
            from: 'start',
            to: 'end',
          },
        },
      }
      """

    val model: BooleanArray = remember { BooleanArray(size = 100) }

    LazyColumn {
        items(count = 100) {
            // Text(text = "item $it", modifier = Modifier.padding(4.dp))
            Box(modifier = Modifier.padding(all = 3.dp)) {
                var animateToEnd: Boolean by remember { mutableStateOf(value = model[it]) }
                val progress: Animatable<Float, AnimationVector1D> = remember {
                    Animatable(
                        initialValue = 0f
                    )
                }

                LaunchedEffect(key1 = animateToEnd) {
                    progress.animateTo(
                        targetValue = if (animateToEnd) 1f else 0f,
                        animationSpec = tween(700)
                    )
                }

                MotionLayout(
                    modifier = Modifier
                        .background(color = Color(color = 0xFF331B1B))
                        .fillMaxWidth()
                        .padding(all = 1.dp),
                    motionScene = MotionScene(content = scene),
                    progress = progress.value
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
                                model[it] = animateToEnd
                            },
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.layoutId(layoutId = "title"),
                        text = "San Francisco $it",
                        fontSize = 30.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
