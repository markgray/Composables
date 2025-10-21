package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import androidx.constraintlayout.compose.ConstraintSetScope
import kotlinx.coroutines.CoroutineScope
import kotlin.math.abs

/**
 * Demonstrates a fly-in effect using [MotionLayout].
 *
 * This animation features a central image that grows, brightens, and rotates into place,
 * while several emoji characters fly in from off-screen, bounce, and form a horizontal chain
 * at the bottom.
 *
 * The animation showcases several features of [MotionLayout] in Compose:
 *  - [MotionScene]: Defining `start` and `end` [ConstraintSet]s.
 *  - [Transition]: Defining the animation path between states.
 *  - Custom Attributes: Using `customFloat` for properties like saturation, brightness, and rotation.
 *  - [TransitionScope.keyPositions] `KeyPositions`: Controlling the path of the emojis in a
 *  [KeyPositionsScope] lambda argument.
 *  - [TransitionScope.keyAttributes] `KeyAttributes`: Modifying attributes like scale at specific
 *  points in the animation in a [KeyAttributesScope] lambda argument.
 *  - [TransitionScope.keyCycles] `KeyCycles`: Creating an oscillating motion (wobble) for the
 *  central image in a [KeyCyclesScope] lambda argument.
 *
 * We start by initializing our [String] variable `imgId` with the [String] value `"image". We
 * initialize our [Array] of [String] variable `id` with the [String] values `"w1"`, `"w2"`,
 * `"w3"`, `"w4"`, `"w5"`, and `"w6"`. We also initialize [List] of [String] variable `emojis` with
 * the [String] values `"😀"`, `"🙂"`, `"🤨"`, `"😐"`, `"😒"`, and `"😬"`.
 *
 * We initialize our [MotionScene] variable `scene` with a new instance of [MotionScene] in whose
 * [MotionSceneScope] `motionSceneContent` lambda argument:
 *  - We initialize our [Array] of [ConstrainedLayoutReference] variable `refs` to instances whose
 *  `id`'s are in our [Array] of [String] variable `id`.
 *  - We initialize our [ConstrainedLayoutReference] variable `imgRef` to an instance whose `id`
 *  is "image".
 *  - We initialize our [ConstraintSetRef] variable `start1` to an instance in whose
 *  [ConstraintSetScope] `constraintSetContent` lambda argument we [ConstraintSetScope.constrain]
 *  the [ConstrainedLayoutReference] variable `imgRef` to have a [ConstrainScope.width] of `10.dp`,
 *  to have a [ConstrainScope.height] of `10.dp`, to have a [ConstrainScope.alpha] of `0f`, to
 *  [ConstrainScope.centerHorizontallyTo] to the `other` parent, to have its `top` linked to its
 *  `parent`'s `top`, to have its `bottom` linked to its `parent`'s `bottom` with a margin of
 *  `100.dp`, declare a [MotionSceneScope.customFloat] with the `name` "sat" and value `0f`,
 *  declare a [MotionSceneScope.customFloat] with the `name` "bright" and value `0f`, and declare a
 *  [MotionSceneScope.customFloat] with the `name` "rot" and `value` `-360f`.
 *  We loop over `i` for all the indices of our [Array] of [ConstrainedLayoutReference] variable
 *  `refs` and use [ConstraintSetScope.constrain] to constrain each entry in `refs` to have a
 *  [ConstrainScope.width] of `32.dp`, to have a [ConstrainScope.height] of `32.dp`, to link its
 *  `bottom` to its `parent`'s `bottom` with a `margin` in [Dp] that is the index of the entry in
 *  the [Array] of [ConstrainedLayoutReference] variable `ref` times `2` minus the size of `ref`
 *  plus `1` the quantity times `200`, and we [ConstrainScope.centerHorizontallyTo] to the `other`
 *  `parent`, with a bias that is the index of the entry in the [Array] of [ConstrainedLayoutReference]
 *  modulo `2` times 4 minus `2f`.
 *  - We initialize our [ConstraintSetRef] variable `end1` to an instance in whose
 *  [ConstraintSetScope] `constraintSetContent` lambda argument we [ConstraintSetScope.constrain]
 *  the [ConstrainedLayoutReference] variable `imgRef` to have a [ConstrainScope.width] of
 *  [Dimension.fillToConstraints], to have a [ConstrainScope.height] of [Dimension.fillToConstraints],
 *  to [ConstrainScope.centerHorizontallyTo] to the `other` parent, to have its `top` linked to its
 *  `parent`'s `top`, to have its `bottom` linked to its `parent`'s `bottom` with a margin of
 *  `100.dp`, declare a [MotionSceneScope.customFloat] with the `name` "sat" and `value` `1f` declare
 *  a [MotionSceneScope.customFloat] with the `name` `bright" and `value` `1f`, and declare a
 *  [MotionSceneScope.customFloat] with the name "rot" and `value` `0f`. We use the method
 *  [ConstraintSetScope.createHorizontalChain] to create a horizontal chain of the `elements` in
 *  [Array] of [ConstrainedLayoutReference] variable `refs`.
 *  We loop over `i` for all the indices of our [Array] of [ConstrainedLayoutReference] variable
 *  `refs` and use [ConstraintSetScope.constrain] to constrain each entry in `refs` to have a
 *  [ConstrainScope.width] of `32.dp`, to have a [ConstrainScope.height] of `32.dp`, to link its
 *  `bottom` to its `parent`'s `bottom` with a `margin` of `16.dp`.
 *  - Finally we use [MotionSceneScope.transition] to add a transition `from` the [ConstraintSetRef]
 *  variable `start1` to the [ConstraintSetRef] variable `end1` with the name "default", and in its
 *  [TransitionScope] `transitionContent` lambda argument we set [TransitionScope.motionArc] to
 *  [Arc.StartHorizontal] (this makes the elements move along a curved path instead of a straight
 *  line). We use [TransitionScope.keyPositions] for the `targets` in `refs` to have it at the
 *  50% mark of the animation (frame(50)), move the emojis up slightly (percentY = 0.1f), creating
 *  a bounce effect. We use [TransitionScope.keyAttributes] to at the 50% mark, scale the emojis
 *  up to 6 times their size (scaleX = 6f, scaleY = 6f) before shrinking back to their final size.
 *  We use [TransitionScope.keyAttributes] on the target `imgRef` to at the 70% mark, to have the
 *  image's brightness momentarily boosted to 1.6, creating a flash effect. We use
 *  [TransitionScope.keyCycles] on the target `imgRef` to create a wobble effect for the image. It
 *  defines a vertical translation (translationY) that follows a wave pattern, starting and ending
 *  at 0 but peaking at 200f halfway through.
 *
 * Having initialized our [MotionScene] variable `scene` we initialize our [Painter] variable
 * `painter` to a [painterResource] for the jpg with resource ID [R.drawable.pepper]. We initialize
 * and remember our [MutableState] wrapped [Boolean] variable `animateToEnd` to `true`. We initialize
 * and remember our [Animatable] of [Float] variable `progress` to an [Animatable] with an initial
 * value of `0f`.
 *
 * We compose a [LaunchedEffect] whose `key1` is our [MutableState] wrapped [Boolean] variable
 * `animateToEnd` (it will be re-run whenever `animateToEnd` changes value). In the
 * [CoroutineScope] `block` argument we use [Animatable.animateTo] to animate our [Animatable] of
 * [Float] variable `progress` to a value of `1f` if `animateToEnd` is `true`, or `0f` if
 * `animateToEnd` is `false`, with an `animationSpec` of [tween] with a duration of `5_000`
 * miliseconds.
 *
 * Then our root composable is a [MotionLayout] whose `modifier` argument is a [Modifier.background]
 * whose `color` is the hex value `0xFF221010`, chained to a [Modifier.fillMaxSize], chained to a
 * [Modifier.padding] that adds `1.dp` to all sides. The `motionScene` argument is our [MotionScene]
 * variable `scene`, and the `progress` argument is the current value of our [Animatable] of [Float]
 * variable `progress`.
 *
 * In the [MotionLayoutScope] `content` composable lambda argument of the [MotionLayout] we first
 * compose a [MotionImage] whose arguments are:
 *  - `painter`: is our [Painter] variable `painter`.
 *  - `brightness`: is the value of our [MotionLayoutScope.customFloat] whose `id` is `imgId`, and
 *  whose `name` is `"bright"`.
 *  - `saturation`: is the value of our [MotionLayoutScope.customFloat] whose `id` is `imgId`, and
 *  whose `name` is `"sat"`.
 *  - `rotate`: is the value of our [MotionLayoutScope.customFloat] whose `id` is `imgId`, and whose
 *  `name` is `"rot"`.
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is `imgId`.
 *
 * We then loop over `i` for all the indices of our [Array] of [String] variable `id` and compose a
 * [Box] whose `modifier` argument is a [Modifier.layoutId] whose `layoutId` is the value of the
 * entry in the [Array] of [String] variable `id` at index `i`, chained to a [Modifier.clip] whose
 * `shape` is a [RoundedCornerShape] with a `size` of `20.dp`. In the [BoxScope] `content` composable
 * lambda argument of the [Box] we compose a [Text] whose `text` is the [String] entry in [List] of
 * [String] variable `emojis` at index `i`.
 */
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "motion101")
@Composable
fun M1FlyIn() {
    val imgId = "image"
    val id: Array<String> = arrayOf("w1", "w2", "w3", "w4", "w5", "w6")
    val emojis: List<String> = "😀 🙂 🤨 😐 😒 😬".split(' ')

    val scene = MotionScene {
        val refs: Array<ConstrainedLayoutReference> =
            id.map { createRefFor(id = it) }.toTypedArray()
        val imgRef: ConstrainedLayoutReference = createRefFor(id = "image")
        val start1: ConstraintSetRef = constraintSet {
            constrain(ref = imgRef) {
                width = Dimension.value(dp = 10.dp)
                height = Dimension.value(dp = 10.dp)
                alpha = 0f
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = parent.top)
                bottom.linkTo(anchor = parent.bottom, margin = 100.dp)
                customFloat(name = "sat", value = 0f)
                customFloat(name = "bright", value = 0f)
                customFloat(name = "rot", value = -360f)
            }
            for (i in refs.indices) {
                constrain(ref = refs[i]) {
                    width = Dimension.value(dp = 32.dp)
                    height = Dimension.value(dp = 32.dp)
                    bottom.linkTo(
                        anchor = parent.bottom,
                        margin = (abs(n = i * 2 - refs.size + 1) * 200).dp
                    )
                    centerHorizontallyTo(other = parent, bias = (i % 2) * 4 - 2f)
                }
            }
        }

        val end1: ConstraintSetRef = constraintSet {
            constrain(ref = imgRef) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = parent.top)
                bottom.linkTo(anchor = parent.bottom, margin = 100.dp)
                customFloat(name = "sat", value = 1f)
                customFloat(name = "bright", value = 1f)
                customFloat(name = "rot", value = 0f)
            }
            createHorizontalChain(elements = refs)
            for (i in refs.indices) {
                constrain(ref = refs[i]) {
                    width = Dimension.value(dp = 32.dp)
                    height = Dimension.value(dp = 32.dp)
                    bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
                }
            }
        }
        transition(from = start1, to = end1, name = "default") {
            motionArc = Arc.StartHorizontal

            keyPositions(*refs) {
                frame(frame = 50) {
                    type = RelativePosition.Delta
                    percentY = 0.1f
                }
            }
            keyAttributes(*refs) {
                frame(frame = 50) {
                    scaleX = 6f
                    scaleY = 6f
                }
            }
            keyAttributes(imgRef) {
                frame(frame = 70) {
                    customFloat(name = "sat", value = 0f)
                    customFloat(name = "bright", value = 1.6f)

                }
            }
            keyCycles(imgRef) {
                frame(frame = 0) {
                    period = 0f
                    translationY = 0f
                }
                frame(frame = 50) {
                    period = 1f
                    translationY = 200f
                }
                frame(frame = 100) {
                    period = 0f
                    translationY = 0f
                }
            }
        }
    }

    val painter: Painter = painterResource(id = R.drawable.pepper)
    val animateToEnd: Boolean by remember { mutableStateOf(value = true) }
    val progress: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(key1 = animateToEnd) {
        progress.animateTo(
            targetValue = if (animateToEnd) 1f else 0f,
            animationSpec = tween(durationMillis = 5_000)
        )
    }

    MotionLayout(
        modifier = Modifier
            .background(color = Color(color = 0xFF221010))
            .fillMaxSize()
            .padding(all = 1.dp),
        motionScene = scene,
        progress = progress.value
    ) {
        MotionImage(
            painter = painter,
            brightness = customFloat(id = imgId, name = "bright"),
            saturation = customFloat(id = imgId, name = "sat"),
            rotate = customFloat(id = imgId, name = "rot"),
            modifier = Modifier.layoutId(layoutId = imgId)
        )
        for (i in id.indices) {
            Box(
                modifier = Modifier
                    .layoutId(layoutId = id[i])
                    .clip(shape = RoundedCornerShape(size = 20.dp))
            ) {
                Text(text = emojis[i])
            }
        }
    }
}

