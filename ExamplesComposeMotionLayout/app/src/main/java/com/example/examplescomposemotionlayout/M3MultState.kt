package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

/**
 * A demonstration of a [MotionLayout] with multiple transitions chained together.
 *
 * This Composable showcases how to define several [ConstraintSet]s (a, b, c, d) and
 * transitions between them ("right", "down", "left"). A [LaunchedEffect] is used to
 * programmatically trigger these transitions in sequence by updating the `transitionName`
 * and animating the `progress`.
 *
 * The text element moves from the center to the right, then down, then left, with different
 * keyframe animations ([KeyAttributeScope.rotationY], [KeyAttributeScope.rotationZ],
 * [KeyAttributeScope.rotationX], [KeyAttributeScope.scaleX]) applied during each transition.
 *
 * We start by initializing our [String] variable `titleId` to the [String] "title".
 * TODO: Continue here.
 */
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "motion101")
@Composable
fun M3MultiState() {
    val titleId = "title"

    val scene = MotionScene {
        val titleRef: ConstrainedLayoutReference = createRefFor(titleId)
        val a: ConstraintSetRef = constraintSet {
            constrain(ref = titleRef) {
                centerHorizontallyTo(other = parent, bias = 0f)
                centerVerticallyTo(other = parent, bias = 0f)
            }
        }

        val b: ConstraintSetRef = constraintSet(extendConstraintSet = a) {
            constrain(ref = titleRef) {
                horizontalBias = 1f
            }
        }
        val c: ConstraintSetRef = constraintSet(extendConstraintSet = b) {
            constrain(ref = titleRef) {
                verticalBias = 1f
            }
        }
        val d: ConstraintSetRef = constraintSet(extendConstraintSet = c) {
            constrain(ref = titleRef) {
                horizontalBias = 0f
            }
        }
        transition(from = a, to = b, name = "right") {
            keyAttributes(titleRef) {
                frame(frame = 50) {
                    rotationY = 50f
                }
            }
        }
        transition(from = b, to = c, name = "down") {
            keyAttributes(titleRef) {
                frame(frame = 50) {
                    rotationZ = 90f
                }
            }
        }
        transition(from = c, to = d, name = "left") {
            keyAttributes(titleRef) {
                frame(frame = 50) {
                    rotationX = 45f
                    scaleX = 2f
                }
            }
        }
    }
    @Suppress("UNUSED_VARIABLE", "unused")
    val painter: Painter = painterResource(id = R.drawable.pepper)

    var transitionName: String by remember {
        mutableStateOf(value = "right")
    }
    val animateToEnd: Boolean by remember { mutableStateOf(value = true) }
    val progress: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0f) }
    LaunchedEffect(key1 = animateToEnd) {
        @Suppress("UNUSED_VARIABLE", "unused")
        val result: AnimationResult<Float, AnimationVector1D> = progress.animateTo(
            targetValue = if (animateToEnd) 1f else 0f,
            animationSpec = tween(durationMillis = 3000)
        )
        transitionName = "down"
        progress.snapTo(targetValue = 0f)
        progress.animateTo(
            targetValue = if (animateToEnd) 1f else 0f,
            animationSpec = tween(durationMillis = 3000)
        )
        transitionName = "left"
        progress.snapTo(targetValue = 0f)
        progress.animateTo(
            targetValue = if (animateToEnd) 1f else 0f,
            animationSpec = tween(durationMillis = 3000)
        )
    }
    MotionLayout(
        modifier = Modifier
            .background(color = Color(color = 0xFF221010))
            .fillMaxSize()
            .padding(all = 1.dp),
        motionScene = scene,
        transitionName = transitionName,
        progress = progress.value
    ) {

        Text(
            modifier = Modifier.layoutId(layoutId = titleId),
            text = transitionName,
            fontSize = 30.sp,
            color = Color.White
        )

    }
}
