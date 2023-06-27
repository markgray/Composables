/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composeanimateddraganddrop

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.Ref
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

/**
 * A ConstraintLayout that animates based on changes made to [constraintSet]. This is similar to
 * using `ConstraintLayout(constraintSet, animateChanges = true)`, but this will always animate
 * forward. Making it animate always forward allows for consistency with start/end concepts. Meaning
 * that the given [constraintSet] is always the end state when animating. We start by initializing
 * and rememembering our [ConstraintSet] variable `var startConstraint` to a [MutableState] of our
 * [ConstraintSet] parameter [constraintSet], and initializing and rememembering our [ConstraintSet]
 * variable `var endConstraint` to a [MutableState] of our [ConstraintSet] parameter [constraintSet].
 * We initialize and remember our [Animatable] of [Float] to [AnimationVector1D] variable `val progress`
 * to an instance whose initial value is 0.0f ([AnimationVector1D] is a 1D vector, it contains only
 * one Float value which is animated when the [Animatable.animateTo] method of `progress` is called).
 * We initialize and remember our [Channel] of [ConstraintSet] variable `val channel` to an instance
 * whose `capacity` is [Channel.CONFLATED] ([Channel] that buffers at most one element and conflates
 * all subsequent `send` and `trySend` invocations, so that the receiver always gets the most recently
 * sent element. Back-to-send sent elements are _conflated_ -- only the most recently sent element is
 * received, while previously sent elements **are lost**). We initialize and remember our [Ref] of
 * [Boolean] variable `val hasAnimated` to an instance whose initial `value` is `false` (a [Ref] is
 * a value holder general purpose class).
 *
 * We use [SideEffect] to schedule a lambda to run after every recomposition, and in that lambd we
 * call the [Channel.trySend] method of `channel` to add our [ConstraintSet] parameter [constraintSet]
 * to the [Channel]. Next we use a [LaunchedEffect] whose `key1` is `channel` (it will be cancelled
 * and re-launched when `channel` changes value) whose [CoroutineScope] lambda loops for all the
 * [ConstraintSet] variable `constraints` contained in `channnel`, initializing its [ConstraintSet]
 * variable `val newConstraints` to either the [ConstraintSet] returned by the [Channel.tryReceive]
 * method of `channel` or if that is `null` to the current value of the `constraints` variable.
 * Then it initializes its [ConstraintSet] variable `val currentConstraints` to `startConstraint` if
 * the [Ref.value] of `hasAnimated` is `false` or to `endConstraint` if it is `true`. Then if the
 * `newConstraints` we received from [Channel] `channel` is not equal to `currentConstraints` we set
 * `endConstraint` to `newConstraints`, then call the [Animatable.snapTo] method of `progress` to set
 * its current value `0f` without any animation, then call its [Animatable.animateTo] method to start
 * an animation to animate from its current value to the `targetValue` of `1.0f` using our
 * [AnimationSpec] parameter [animationSpec] as its `animationSpec`.
 *
 * Having set things up we then use a [MotionLayout] Composable to animate our [content] Composable
 * as its `content` using `startConstraint` as its `start` [ConstraintSet], `endConstraint` as its
 * `end` [ConstraintSet], the [Animatable.value] of `progress` as the interpolated position of the
 * layout between the [ConstraintSet]'s, and our [Modifier] parameter [modifier] as the [Modifier]
 * to apply to its layout node.
 *
 * @param constraintSet The [ConstraintSet] that we are to animate to.
 * @param modifier a [Modifier] our caller can use to modify our looks and behavior. Our only caller
 * [FlowDragAndDropExample] passes us a [ColumnScope] `Modifier.fillMaxWidth` to which is chained a
 * `Modifier.background` that draws a [Color.LightGray] colored [RectangleShape] behind our content,
 * to which is chained a `Modifier.onGloballyPositioned` whose `onGloballyPositioned` lambda callback
 * will be invoked with the [LayoutCoordinates] of the element when the global position of the content
 * may have changed. Note that it will be called after a composition when the coordinates are finalized.
 * The lambda updates the bounding box of its `listBounds` and `windowBounds` based on the new
 * [LayoutCoordinates]. And finally a [Modifier.dragAndDrop] is chained to the end which sets the
 * [LayoutDragHandler] used to to enable the DragAndDrop functionality of our receiving Composable.
 * @param animationSpec the [AnimationSpec] to use to animate our [ConstraintSet] parameter
 * [constraintSet]. Our caller does not pass one so the default [tween] is used with the default
 * aguments of [tween] used: `durationMillis` of [DefaultDurationMillis] (300ms) (duration of the
 * animation spec), `delayMillis` of 0 (the amount of time in milliseconds that animation waits before
 * starting), and `easing` of [FastOutSlowInEasing] (the easing curve that will be used to interpolate
 * between start and end, [FastOutSlowInEasing] (speeds up quickly and slows down gradually, in order
 * to emphasize the end of the transition).
 * @param content a Composable that runs in [MotionLayoutScope], and is best understood reading the
 * kdoc for [FlowDragAndDropExample].
 */
@Composable
fun AnimatedConstraintLayout(
    constraintSet: ConstraintSet,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = tween(),
    content: @Composable MotionLayoutScope.() -> Unit
) {
    var startConstraint: ConstraintSet by remember { mutableStateOf(constraintSet) }
    var endConstraint: ConstraintSet by remember { mutableStateOf(constraintSet) }
    val progress: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0.0f) }
    val channel: Channel<ConstraintSet> = remember { Channel(capacity = Channel.CONFLATED) }
    val hasAnimated: Ref<Boolean> = remember { Ref<Boolean>().apply { value = false } }

    SideEffect {
        channel.trySend(constraintSet)
    }

    LaunchedEffect(key1 = channel) {
        for (constraints: ConstraintSet in channel) {
            val newConstraints: ConstraintSet = channel.tryReceive().getOrNull() ?: constraints
            val currentConstraints: ConstraintSet =
                if (hasAnimated.value == false) startConstraint else endConstraint
            if (newConstraints != currentConstraints) {
                startConstraint = currentConstraints
                endConstraint = newConstraints
                progress.snapTo(targetValue = 0f)
                progress.animateTo(targetValue = 1f, animationSpec = animationSpec)
                hasAnimated.value = true
            }
        }
    }
    MotionLayout(
        start = startConstraint,
        end = endConstraint,
        progress = progress.value,
        modifier = modifier,
        content = content
    )
}