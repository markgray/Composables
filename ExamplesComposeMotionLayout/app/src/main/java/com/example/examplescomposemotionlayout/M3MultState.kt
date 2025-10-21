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
import kotlinx.coroutines.CoroutineScope

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
 * We initialize our [MotionScene] variable `scene` to a new instance in whose [MotionSceneScope]
 * `motionSceneContent` lambda argument we first initialize our [ConstrainedLayoutReference]
 * variable `titleRef` to a new instance whose `id` is the [String] variable `titleId`.
 *
 * We initialize our [ConstraintSetRef] variable `a` to a new instance in whose [ConstraintSetScope]
 * `constraintSetContent` lambda argument we [ConstraintSetScope.constrain] the
 * [ConstrainedLayoutReference] variable `titleRef` to [ConstrainScope.centerHorizontallyTo] to the
 * other its `parent` with a `bias` of `0f`, and to [ConstrainScope.centerVerticallyTo] to the other
 * its `parent` with a `bias` of `0f`
 *
 * We initialize our [ConstraintSetRef] variable `b` to a new instance whose `extendConstraintSet`
 * argument is the [ConstraintSetRef] variable `a`, and in whose [ConstraintSetScope]
 * `constraintSetContent` lambda argument we [ConstraintSetScope.constrain] the
 * [ConstrainedLayoutReference] variable `titleRef` to have a [ConstrainScope.horizontalBias] of `1f`.
 *
 * We initialize our [ConstraintSetRef] variable `c` to a new instance whose `extendConstraintSet`
 * argument is the [ConstraintSetRef] variable `b`, and in whose [ConstraintSetScope]
 * `constraintSetContent` lambda argument we [ConstraintSetScope.constrain] the
 * [ConstrainedLayoutReference] variable `titleRef` to have a [ConstrainScope.verticalBias] of `1f`.
 *
 * We initialize our [ConstraintSetRef] variable `d` to a new instance whose `extendConstraintSet`
 * argument is the [ConstraintSetRef] variable `c`, and in whose [ConstraintSetScope]
 * `constraintSetContent` lambda argument we [ConstraintSetScope.constrain] the
 * [ConstrainedLayoutReference] variable `titleRef` to have a [ConstrainScope.horizontalBias] of `0f`.
 *
 * Next we use [MotionSceneScope.transition] to define a transition `from` our [ConstraintSetRef]
 * variable `a` to our [ConstraintSetRef] variable `b`, with a [String] `name` of "right", and in the
 * [TransitionScope] `transitionContent` lambda argument we use [TransitionScope.keyAttributes] to
 * modify the `titleRef` at 50% of the way through the animation to have its
 * [KeyAttributeScope.rotationY] set to `50f` degrees (a rotation around the `Y` axis).
 *
 * Next we use [MotionSceneScope.transition] to define a transition `from` our [ConstraintSetRef]
 * variable `b` to our [ConstraintSetRef] variable `c`, with a [String] `name` of "down", and in the
 * [TransitionScope] `transitionContent` lambda argument we use [TransitionScope.keyAttributes] to
 * modify the `titleRef` at 50% of the way through the animation to have its
 * [KeyAttributeScope.rotationZ]  set to `90f` degrees (a rotation around the `Z` axis).
 *
 * Next we use [MotionSceneScope.transition] to define a transition `from` our [ConstraintSetRef]
 * variable `c` to our [ConstraintSetRef] variable `d`, with a [String] `name` of "left", and in the
 * [TransitionScope] `transitionContent` lambda argument we use [TransitionScope.keyAttributes] to
 * modify the `titleRef` at 50% of the way through the animation to have its
 * [KeyAttributeScope.rotationX] set to `45f` degrees (a rotation around the `X` axis), and its
 * [KeyAttributeScope.scaleX] set to `2f`
 *
 * Having defined our [MotionScene] we initialize our [Painter] variable `painter` to the [Painter]
 * returned by [painterResource] for the jpg with resource ID `R.drawable.pepper` (but never use it),
 * we initialize and remember our [MutableState] wrapped [String] variable `transitionName` to the
 * [String] "right", we initialize and remember our [MutableState] wrapped [Boolean] variable
 * `animateToEnd` to `true`, and we initialize and remember our [Animatable] of [Float] variable
 * `progress` to an [Animatable] whose initial value is `0f`.
 *
 * We compose a [LaunchedEffect] whose `key1` is our [MutableState] wrapped [Boolean] variable
 * `animateToEnd` (it will be re-run whenever `animateToEnd` changes value). In the
 * [CoroutineScope] `block` argument we initialize our [AnimationResult] of [Float] variable
 * `result` to the value returned by the [Animatable.animateTo] method of [Animatable] of [Float]
 * variable `progress`, with a `targetValue` of `1f` if `animateToEnd` is `true`, and `0f` if
 * `animateToEnd` is `false`, and whose `animationSpec` is a [tween] with a `duration` of `3000`
 * milliseconds. When that suspend method completes we set our [MutableState] wrapped [String]
 * variable `transitionName` to "down", call the [Animatable.snapTo] method of [Animatable] of
 * [Float] variable `progress` with a `targetValue` of `0f` then we call the [Animatable.animateTo]
 * method of [Animatable] of [Float] variable `progress`, with a `targetValue` of `1f` if
 * `animateToEnd` is `true`, and `0f` if `animateToEnd` is `false` with an `animationSpec` of a
 * [tween] whose `duration` is `3000` milliseconds. When that suspend method completes we set our
 * [MutableState] wrapped [String] variable `transitionName` to "left", call the [Animatable.snapTo]
 * method of [Animatable] of [Float] variable `progress` with a `targetValue` of `0f` then we call
 * the [Animatable.animateTo] method of [Animatable] of [Float] variable `progress`, with a
 * `targetValue` of `1f` if `animateToEnd` is `true`, and `0f` if `animateToEnd` is `false` with an
 * `animationSpec` of a [tween] whose `duration` is `3000` milliseconds.
 *
 * Our root composable is a [MotionLayout] whose `modifier` argument is a [Modifier.background] whose
 * `color` is the [Color] with hex value `0xFF221010` chained to a [Modifier.fillMaxSize] chained to
 * a [Modifier.padding] that adds `1.dp` to `all` sides, whose `motionScene` argument is our
 * [MotionScene] variable `scene`, whose `transitionName` argument is our [MutableState] wrapped
 * [String] variable `transitionName`, and whose `progress` argument is our [Animatable] of [Float]
 * variable `progress`. In the [MotionLayoutScope] `content` composable lambda argument we compose
 * a [Text] whose arguments are:
 *  - `modifier`: a [Modifier.layoutId] whose `id` is the [String] variable `titleId`.
 *  - `text`: our [MutableState] wrapped [String] variable `transitionName`.
 *  - `fontSize`: `30.sp`.
 *  - `color`: [Color.White]
 *
 */
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "motion101")
@Composable
fun M3MultiState() {
    val titleId = "title"

    val scene = MotionScene {
        val titleRef: ConstrainedLayoutReference = createRefFor(id = titleId)
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
