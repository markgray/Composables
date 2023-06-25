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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.channels.Channel

/**
 * A ConstraintLayout that animates based on changes made to [constraintSet]. This is similar to
 * using `ConstraintLayout(constraintSet, animateChanges = true)`, but this will always animate
 * forward. Making it animate always forward allows for consistency with start/end concepts. Meaning
 * that the given [constraintSet] is always the end state when animating.
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
    var startConstraint by remember { mutableStateOf(constraintSet) }
    var endConstraint by remember { mutableStateOf(constraintSet) }
    val progress = remember { Animatable(0.0f) }
    val channel = remember { Channel<ConstraintSet>(Channel.CONFLATED) }
    val hasAnimated = remember { Ref<Boolean>().apply { value = false } }

    SideEffect {
        channel.trySend(constraintSet)
    }

    LaunchedEffect(channel) {
        for (constraints in channel) {
            val newConstraints = channel.tryReceive().getOrNull() ?: constraints
            val currentConstraints =
                if (hasAnimated.value == false) startConstraint else endConstraint
            if (newConstraints != currentConstraints) {
                startConstraint = currentConstraints
                endConstraint = newConstraints
                progress.snapTo(0f)
                progress.animateTo(1f, animationSpec)
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