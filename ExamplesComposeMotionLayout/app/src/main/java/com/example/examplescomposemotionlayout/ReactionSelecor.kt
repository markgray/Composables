package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.MotionSceneScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * A selector for different reactions, represented by emojis.
 *
 * When an emoji is selected, it animates the corresponding emoji name into view using MotionLayout.
 * The emoji name scales up and fades in, while the previously selected name fades out.
 *
 * This demonstrates a complex animation sequence where the start state is common,
 * but there are multiple end states (one for each emoji). The correct transition
 * is chosen dynamically based on user interaction.
 *
 * We start by initializing and remembering our [MutableState] wrapped [Int] variable `selected` to
 * `3`, initializing and remembering our [MutableState] wrapped [String] variable `transitionName`
 * to `"transition1"`, initializing and remembering our [List] of [String] variable `emojis` to
 * the result of applying [String.split] to the [String] `"😀 🙂 🤨 😐 😒 😬"`, and initializing and
 * remembering our [List] of [String] variable `emojiNames` to a [List] of descriptions of the emoji
 * in [List] of [String] variable `emojis`.
 *
 * We initialize our [MotionScene] variable `scene` to a new instance in whose [MotionSceneScope]
 * `motionSceneContent` lambda argument we:
 *  - initialize our [Array] of [ConstrainedLayoutReference] variable `emojiIds` to the result of
 *  using the [Array.map] method of [Array] of [String] variable `emojis` with a `transform` lambda
 *  argument that calls [MotionSceneScope.createRefFor] on each [String] in the [Array] producing a
 *  [List] of [ConstrainedLayoutReference] that is fed to [Collection.toTypedArray] to convert it to
 *  an [Array] of [ConstrainedLayoutReference].
 *  - initialize our [Array] of [ConstrainedLayoutReference] variable `titleIds` to the result of
 *  using the [Array.map] method of [Array] of [String] variable `emojiNames` with a `transform`
 *  lambda argument that calls [MotionSceneScope.createRefFor] on each [String] in the [Array]
 *  producing a [List] of [ConstrainedLayoutReference] that is fed to [Collection.toTypedArray] to
 *  convert it to an [Array] of [ConstrainedLayoutReference].
 *
 * Next we initialize our [ConstraintSetRef] variable `start1` to a new instance in whose
 * [ConstraintSetScope] `constraintSetContent` lambda argument we call
 * [ConstraintSetScope.createHorizontalChain] to create a horizontal chain of the `elements` in [Array]
 * of [ConstrainedLayoutReference] variable `emojiIds`. We use the [Array.forEach] method of
 * [Array] of [ConstrainedLayoutReference] variable `emojiIds` to loop through its entries and in the
 * `action` lambda argument we accept the [ConstrainedLayoutReference] passed the lambda in variable
 * `layoutRef` then we call [ConstraintSetScope.constrain] to constrain `layoutRef` to have its
 * [ConstrainScope.top] linked to its `parent` `top with a `margin` of `10.dp`. We use the
 * [Array.forEachIndexed] method of [Array] of [ConstrainedLayoutReference] variable `titleIds` to
 * loop through its entries and in the `action` lambda argument we accept the index passed the lambda
 * in variable `index` and the [ConstrainedLayoutReference] passed the lambda in variable `title`
 * then we call [ConstraintSetScope.constrain] to constrain `title` to have its `top` linked to
 * the `bottom` of the `0th` entry in `emojiIds`, its `start` linked to the `start` of the `index`
 * entry in `emojiIds`, its `end` linked to the `end` of the `index` entry in `emojiIds`, its
 * `bottom` linked to its `parent` `bottom with a `margin` of `10.dp`, its [ConstrainScope.scaleX]
 * set to `0.1f`, and its [ConstrainScope.alpha] set to `0f`.
 *
 * Next we initialize our [List] of [ConstraintSetRef] variable `ends` by using the [Array.map] method
 * of [Array] of [ConstrainedLayoutReference] variable `titleIds` to loop through its entries and in
 * the `transform` lambda argument we accept the [ConstrainedLayoutReference] passed the lambda in
 * variable `layoutRef` then we use the [MotionSceneScope.constraintSet] method to create a
 * [ConstraintSetRef] for `layoutRef` that extends the [ConstraintSetRef] variable `start1` and in
 * its [ConstraintSetScope] `constraintSetContent` lambda argument we [ConstraintSetScope.constrain]
 * each `layoutRef` to have a [ConstrainScope.scaleX] of `1f` and a [ConstrainScope.alpha] of `1f`.
 *
 * To finish off creating the [MotionScene] variable `scene` we use the [Iterable.mapIndexed] method
 * of [List] of [ConstraintSetRef] variable `ends` to loop through its entries and in the `transform`
 * lambda argument we accept the index passed the lambda in variable `index` and the [ConstraintSetRef]
 * passed the lambda in variable `end`, then we call [MotionSceneScope.transition] to add a
 * [MotionSceneScope.transition] `from` the [ConstraintSetRef] variable `start1` to the
 * [ConstraintSetRef] variable `end` with a `name` formed by concatenating the [String] value of
 * `index` to the [String] "transition"
 *
 * Having created our [MotionScene] we initialize and remember our [Animatable] of [Float] variable
 * `progress` to a new instance with an `initialValue` of `0f`, and we initialize our [Flow] of [Int]
 * variable `selectedFlow` to the result of calling [snapshotFlow] with its `block` lambda argument
 * returning the current value of our [MutableState] wrapped [Int] variable `selected`.
 *
 * We compose a [LaunchedEffect] in whose [CoroutineScope] `block` suspend lambda argument we call
 * the [Flow.collect] method of [Flow] of [Int] variable `selectedFlow` to collect its entries and in
 * the `collector` lambda argument we accept the [Int] passed the lambda in variable `it` then call
 * the [Animatable.snapTo] method of [Animatable] of [Float] variable `progress` to have it snap to
 * the `targetValue` of `0f`. Then we set the value of [MutableState] wrapped [String] variable
 * `transitionName` to the [String] formed by concatenating the [String] "transition" to the [String]
 * value of `it`. Finally we call the [Animatable.animateTo] method of [Animatable] of [Float] variable
 * `progress` to have it animate to the `targetValue` of `1f` with an `animationSpec` of a [tween]
 * whose `durationMillis` is `800`.
 *
 * Our root composable is a [Column] in whose [ColumnScope] `content` composable lambda argument we
 * compose a [MotionLayout] whose arguments are:
 *  - `modifier`: is a [Modifier.background] whose `color` is the [Color] with hex value `0xff334433`
 *  chained to a [Modifier.fillMaxWidth]
 *  - `motionScene`: is our [MotionScene] variable `scene`.
 *  - `transitionName`: is the value of [MutableState] wrapped [String] variable `transitionName`.
 *  - `progress`: is the current value of [Animatable] of [Float] variable `progress`.
 *
 * In the [MotionLayoutScope] `content` composable lambda argument of the [MotionLayout] we use the
 * [Iterable.forEachIndexed] method of [List] of [String] variable `emojis` to loop through its
 * entries capturing the index in variable `index` and the [String] in variable `icon` and in the
 * `action` lambda argument we compose a [Text] whose arguments are:
 *  - `text`: is [String] variable `icon`.
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is the [String] variable `icon` chained
 *  to a [Modifier.clickable] in whose `onClick` lambda argument we set [MutableState] wrapped [Int]
 *  variable `selected` to [Int] variable `index`.
 *
 * Next we use the [Iterable.forEach] method of [List] of [String] variable `emojiNames` to loop
 * through its entries and in the `action` lambda argument we accept the [String] passed the lambda
 * in variable `name` then compose a [Text] whose arguments are:
 *  - `text`: is our [String] variable `name`
 *  - `color`: is [Color.White]
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is our [String] variable `name`.
 *
 * @see MotionLayout
 * @see MotionScene
 * @see LaunchedEffect
 */
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll")
@Composable
fun ReactionSelector() {
    var selected: Int by remember { mutableIntStateOf(value = 3) }
    val transitionName: MutableState<String> = remember { mutableStateOf(value = "transition1") }
    val emojis: List<String> = remember { "😀 🙂 🤨 😐 😒 😬".split(' ') }
    val emojiNames: List<String> = remember {
        listOf(
            "Grinning Face",
            "Slightly Smiling Face",
            "Face with Raised Eyebrow",
            "Neutral Face",
            "Unamused Face",
            "Grimacing Face"
        )
    }

    val scene = MotionScene {
        val emojiIds: Array<ConstrainedLayoutReference> =
            emojis.map { createRefFor(id = it) }.toTypedArray()
        val titleIds: Array<ConstrainedLayoutReference> =
            emojiNames.map { createRefFor(id = it) }.toTypedArray()

        val start1: ConstraintSetRef = constraintSet {
            createHorizontalChain(elements = emojiIds)
            emojiIds.forEach { layoutRef: ConstrainedLayoutReference ->
                constrain(ref = layoutRef) {
                    top.linkTo(anchor = parent.top, margin = 10.dp)
                }
            }
            titleIds.forEachIndexed { index: Int, title: ConstrainedLayoutReference ->
                constrain(ref = title) {
                    top.linkTo(anchor = emojiIds[0].bottom, margin = 10.dp)
                    start.linkTo(anchor = emojiIds[index].start)
                    end.linkTo(anchor = emojiIds[index].end)
                    bottom.linkTo(anchor = parent.bottom, margin = 10.dp)
                    scaleX = 0.1f
                    alpha = 0f
                }
            }
        }
        val ends: List<ConstraintSetRef> = titleIds.map { layoutRef: ConstrainedLayoutReference ->
            constraintSet(extendConstraintSet = start1) {
                constrain(ref = layoutRef) {
                    scaleX = 1f
                    alpha = 1f
                }
            }
        }
        ends.mapIndexed { index: Int, end: ConstraintSetRef ->
            transition(from = start1, to = end, name = "transition$index") {
            }
        }
    }
    val progress: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0f) }
    val selectedFlow: Flow<Int> = snapshotFlow { selected }

    LaunchedEffect(key1 = Unit) {
        selectedFlow.collect {
            progress.snapTo(targetValue = 0f)
            transitionName.value = "transition$it"
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }

    Column {
        MotionLayout(
            modifier = Modifier
                .background(color = Color(color = 0xff334433))
                .fillMaxWidth(),
            motionScene = scene,
            transitionName = transitionName.value,
            progress = progress.value
        ) {
            emojis.forEachIndexed { index: Int, icon: String ->
                Text(
                    text = icon,
                    modifier = Modifier
                        .layoutId(layoutId = icon)
                        .clickable {
                            selected = index
                        }
                )
            }
            emojiNames.forEach { name: String ->
                Text(
                    text = name,
                    color = Color.White,
                    modifier = Modifier.layoutId(layoutId = name)
                )
            }
        }
    }
}
