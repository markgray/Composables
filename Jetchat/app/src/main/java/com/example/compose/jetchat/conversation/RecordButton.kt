/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat.conversation

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import com.example.compose.jetchat.R
import kotlinx.coroutines.CoroutineScope
import kotlin.math.abs
import kotlinx.coroutines.launch

/**
 * This is used as the microphone button at the end of the [UserInputText] Composable. When long
 * clicked it "pretends" to be recording. We start by initializing our [Transition] of [Boolean]
 * variable `val transition` by using the [updateTransition] method to set up a [Transition] and
 * have it update with the `targetState` our [Boolean] parameter [recording]. When [recording]
 * changes [Transition] will run all or its child animations towards their target values specified
 * for the new targetState. Next we initialize our [State] of [Float] variable `val scale` to the
 * value returned by the [Transition.animateFloat] method of `transition` which creates a [Float]
 * animation as a part of the [Transition] (this means the states of this animation will be managed
 * by the [Transition]). The `transitionSpec` argument of the call to [Transition.animateFloat] is
 * a [spring] whose `dampingRatio` is [Spring.DampingRatioMediumBouncy] (Damping ratio for a medium
 * bouncy spring), and whose `targetValueByState` argument is a lambda which returns 2f if the
 * [Boolean] passed it is `true` or 1f if it is `false` (`targetValueByState` is used as a mapping
 * from a target state to the target value of this animation. [Transition] will be using this mapping
 * to determine what value to target this animation towards). Next we initialize our [State] of [Float]
 * variable `val containerAlpha` to the value returned by the [Transition.animateFloat] method of
 * `transition` which creates another [Float] animation as a part of the [Transition]. The `transitionSpec`
 * argument of the call to [Transition.animateFloat] is a [tween] whose `durationMillis` argument is
 * 2,000, and whose `targetValueByState` argument is a lambda which returns 1f if the [Boolean] passed
 * it is `true` or 0f if it is `false`. Finally we initialize our [State] of [Color] variable
 * `val iconColor` to the value returned by the [Transition.animateColor] method of `transition`
 * which creates a [Color] animation as a part of the [Transition]. The `transitionSpec` argument of
 * the call to [Transition.animateColor] is a [tween] whose `durationMillis` argument is 200, and
 * whose `targetValueByState` argument is a lambda which returns the [Color] returned by the
 * [contentColorFor] method when passed the `backgroundColor` of the `current` [LocalContentColor]
 * (ie. the corresponding color used by our [ColorScheme] for content of a Composable whose
 * `background` is the `current` [LocalContentColor]) if the [Boolean] passed the lambda is `true`
 * and the `current` [LocalContentColor] itself if it is `false`.
 *
 * Our root Composable is a [Box] holding another [Box] (used as the Background during recording)
 * whose `modifier` argument is a [BoxScope.matchParentSize] which sizes the element to match the
 * size of the outer [Box] to which chained an [Modifier.aspectRatio] which sizes the content to
 * match a 1f aspect ratio, and to this is chained a [Modifier.graphicsLayer] which applies an
 * `alpha` of the current value of our animated [State] of [Float] variable `containerAlpha` to the
 * content, and applies a `scaleX` and `scaleY` of the current value of our animated [State] of
 * [Float] variable `scale` to the content. After the [Modifier.graphicsLayer] comes a
 * [Modifier.clip] which clips the `shape` to a [CircleShape], and the last in the chain is a
 * [Modifier.background] which sets the background `color` of the content to the `current` value of
 * [LocalContentColor].
 *
 * A [TooltipBox] also occupies the outer [Box]. Before calling it we initialize and remember our
 * [CoroutineScope] variable `val scope`, and we initialize and remember our [TooltipState] variable
 * `val tooltipState` to a new instance. The `positionProvider` argument of the [TooltipBox] is
 * the remembered [PopupPositionProvider] returned by the [TooltipDefaults.rememberRichTooltipPositionProvider]
 * method, the `tooltip` argument is a lambda that composes a [RichTooltip] whose `text` lambda argument
 * is a [Text] whose `text` is the [String] with resource ID [R.string.touch_and_hold_to_record]
 * ("Touch and hold to record"), and its `state` argument is our [TooltipState] variable
 * `tooltipState`. The `content` of the [TooltipBox] is an [Icon] whose `imageVector` is the
 * [ImageVector] drawn by [Icons.Filled.Mic], whose `contentDescription` argument is the [String]
 * with resource ID [R.string.record_message] ("Record voice message"), whose `tint` is the current
 * value of our animated [State] of [Color] variable `iconColor`, and whose `modifier` argument uses
 * the [Modifier] parameter of [RecordButton] then chains a [Modifier.sizeIn] to it that sets its
 * `minWidth` to 56.dp and its `minHeight` to 6.dp, followed by a chain to a [Modifier.padding] that
 * adds 16.dp to all sides, followed by a chain to a [Modifier.clickable] that uses a do nothing
 * lambda, with our [Modifier.voiceRecordingGesture] at the tail end. The arguments of the
 * [Modifier.voiceRecordingGesture] are:
 *  - `horizontalSwipeProgress` we pass the [swipeOffset] lambda parameter of [RecordButton]. Our
 *  caller [UserInputText] passes us a lambda for this which returns the [MutableFloatState.floatValue]
 *  of a [MutableFloatState] variable which keeps track of the current `swipeOffset` which our
 *  [onSwipeOffsetChange] lambda parameter updates.
 *  - `onSwipeProgressChanged` we pass the [onSwipeOffsetChange] lambda parameter of [RecordButton].
 *  Our caller [UserInputText] passes us a lambda for this which updates the [MutableFloatState]
 *  variable which keeps track of the current `swipeOffset` and which our [swipeOffset] lambda
 *  parameter reads.
 *  - `onClick` we pass a lambda which calls the [CoroutineScope.launch] method of our [CoroutineScope]
 *  variable `scope` to launch a coroutine which calls the [TooltipState.show] method of our
 *  [TooltipState] variable `tooltipState` to show the tooltip associated with [TooltipBox].
 *  - `onStartRecording` we pass the [onStartRecording] lambda parameter of [RecordButton]. Our caller
 *  [UserInputText] passes us a lambda which sets its [Boolean] variable `val consumed` to the inverse
 *  of the current value of its [State] wrapped [Boolean] variable `isRecordingMessage`, sets
 *  `isRecordingMessage` to `true` and returns `consumed` to the caller of the lambda.
 *  - `onFinishRecording` we pass the [onFinishRecording] lambda parameter of [RecordButton]. Our
 *  caller [UserInputText] passes us a lambda which sets its [State] wrapped [Boolean] variable
 *  `isRecordingMessage` to `false`
 *  - `onCancelRecording` we pass the [onCancelRecording] lambda parameter of [RecordButton]. Our
 *  caller [UserInputText] passes us a lambda which sets its [State] wrapped [Boolean] variable
 *  `isRecordingMessage` to `false`.
 *
 * @param recording if `true` we are currently "recording", and our UI should reflect that fact. Our
 * caller passes us the current value of its [State] wrapped [Boolean] variable `isRecordingMessage`.
 * @param swipeOffset used to keep track of how far the user has horizontally dragged the mic.
 * @param onSwipeOffsetChange called to update the value of [swipeOffset].
 * @param onStartRecording called by [detectDragGesturesAfterLongPress] when the user long presses
 * the microphone. Our caller [UserInputText] passes us a lambda which sets its [Boolean] variable
 * `val consumed` to the inverse of the current value of its [State] wrapped [Boolean] variable
 * `isRecordingMessage`, sets `isRecordingMessage` to `true` and returns `consumed` to the caller of
 * the lambda
 * @param onFinishRecording called by [detectDragGesturesAfterLongPress] after all pointers are up.
 * Our caller [UserInputText] passes us a lambda which sets its [State] wrapped [Boolean] variable
 * `isRecordingMessage` to `false`
 * @param onCancelRecording called by [detectDragGesturesAfterLongPress] if another gesture has
 * consumed pointer input. Our caller [UserInputText] passes us a lambda which sets its [State]
 * wrapped [Boolean] variable `isRecordingMessage` to `false`
 * @param modifier a [Modifier] instance that our user can use to modify our appearance and/or
 * behavior. Our caller [UserInputText] passes us a [Modifier.fillMaxHeight] causing us to occupy
 * our entire incoming vertical constraint.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordButton(
    recording: Boolean,
    swipeOffset: () -> Float,
    onSwipeOffsetChange: (Float) -> Unit,
    onStartRecording: () -> Boolean,
    onFinishRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition: Transition<Boolean> = updateTransition(targetState = recording, label = "record")
    val scale: State<Float> = transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "record-scale",
        targetValueByState = { rec: Boolean -> if (rec) 2f else 1f }
    )
    val containerAlpha: State<Float> = transition.animateFloat(
        transitionSpec = { tween(durationMillis = 2000) },
        label = "record-scale",
        targetValueByState = { rec: Boolean -> if (rec) 1f else 0f }
    )
    val iconColor: State<Color> = transition.animateColor(
        transitionSpec = { tween(durationMillis = 200) },
        label = "record-scale",
        targetValueByState = { rec: Boolean ->
            if (rec) contentColorFor(backgroundColor = LocalContentColor.current)
            else LocalContentColor.current
        }
    )

    Box {
        // Background during recording
        Box(
            modifier = Modifier
                .matchParentSize()
                .aspectRatio(ratio = 1f)
                .graphicsLayer {
                    alpha = containerAlpha.value
                    scaleX = scale.value; scaleY = scale.value
                }
                .clip(shape = CircleShape)
                .background(color = LocalContentColor.current)
        )
        val scope: CoroutineScope = rememberCoroutineScope()
        val tooltipState = remember { TooltipState() }
        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            tooltip = {
                RichTooltip {
                    Text(stringResource(R.string.touch_and_hold_to_record))
                }
            },
            state = tooltipState
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = stringResource(id = R.string.record_message),
                tint = iconColor.value,
                modifier = modifier
                    .sizeIn(minWidth = 56.dp, minHeight = 6.dp)
                    .padding(all = 18.dp)
                    .clickable { }
                    .voiceRecordingGesture(
                        horizontalSwipeProgress = swipeOffset,
                        onSwipeProgressChanged = onSwipeOffsetChange,
                        onClick = { scope.launch { tooltipState.show() } },
                        onStartRecording = onStartRecording,
                        onFinishRecording = onFinishRecording,
                        onCancelRecording = onCancelRecording,
                    )
            )
        }
    }
}

/**
 * This custom [Modifier] is used to process pointer input within the region of its modified element,
 * interpreting them and then calling the lambda parameter which is appropriate for the type of
 * gesture it detects. We start by chaining a [Modifier.pointerInput] to our [Modifier] receiver whose
 * [PointerInputScope] lambda block calls the [detectTapGestures] method with its `onTap` lambda
 * argument our [onClick] lambda parameter when it detects a tap gesture. We then chain to that
 * [Modifier] another [Modifier.pointerInput] whose [PointerInputScope] lambda block initializes some
 * variables:
 *  - `var offsetY` [Float] is initialized to 0. Used to keep track of how much the pointer has been
 *  dragged in the Y direction, it is updated by the `onDrag` lambda argument of the
 *  [detectDragGesturesAfterLongPress] method.
 *  - `var dragging` [Boolean] is initialized to `false`. Used to keep track of whether the pointer
 *  is being dragged. It is set to `true` by the `onDragStart` lambda argument of the
 *  [detectDragGesturesAfterLongPress], and set to `false` by its `onDragCancel`, `onDragEnd`, and
 *  `onDrag` lambda arguments.
 *  - `val swipeToCancelThresholdPx` [Float] Number of pixels in the X direction that indicates that
 *  the user has decided to cancel the dragging.
 *  - `val verticalThresholdPx` [Float] Number of pixels in the Y direction that indicates that the
 *  user has decided to cancel the dragging.
 *
 * Then the [PointerInputScope] block calls the [detectDragGesturesAfterLongPress] method with its:
 *  - `onDragStart` argument a lambda that calls our [onSwipeProgressChanged] lambda parameter with
 *  0f, sets `offsetY` to 0f, sets `dragging` to `true`, and then calls our [onStartRecording] lambda
 *  parameter.
 *  - `onDragCancel` argument a lambda that calls our [onCancelRecording] lambda parameter, and sets
 *  `dragging` to `false`.
 *  - `onDragEnd` argument a lambda that calls our [onFinishRecording] lambda parameter if `dragging`
 *  is currently `true`, and then sets `dragging` to `false`.
 *  - `onDrag` argument a lambda which is called with a [PointerInputChange] parameter `change` and
 *  an [Offset] parameter `dragAmount`. In the lambda, if `dragging` is `false` we do nothing, and
 *  if it is `true` we call our [onSwipeProgressChanged] lambda parameter with the value returned by
 *  our [horizontalSwipeProgress] lambda parameter plus the [Offset.x] of the [Offset] passed our
 *  lambda. We then add [Offset.x] of the [Offset] passed the lambda to our [Float] variable `offsetY`.
 *  We initialize our [Float] variable `val offsetX` to the value returned by our [horizontalSwipeProgress]
 *  lambda parameter. Then if `offsetX` is less than 0, and the absolute value of `offsetX` is greater
 *  than or equal to `swipeToCancelThresholdPx` and the absolute value of `offsetY` is less than or
 *  equal to `verticalThresholdPx` we call our [onCancelRecording] lambda parameter and set `dragging`
 *  to `false`.
 *
 * @param horizontalSwipeProgress a lambda which returns the current accumulated horizontal swipe
 * progress.
 * @param onSwipeProgressChanged a lambda we should call to update the current accumulated horizontal
 * swipe progress.
 * @param onClick A lambda for us to call when the [detectTapGestures] method of our first
 * [Modifier.pointerInput] detects a `tap`. Our caller passes us a lambda which calls the
 * [CoroutineScope.launch] method of its [CoroutineScope] variable `scope` to launch a coroutine
 * which calls the [TooltipState.show] method of its [TooltipState] variable `tooltipState`
 * to show the tooltip associated with its [TooltipBox]).
 * @param onStartRecording a lambda we should call when we detect that the user wants to start recording.
 * @param onFinishRecording a lambda we should call when we detect that the user wants to stop recording.
 * @param onCancelRecording a lambda we should call when we detect that the user wants to cancel recording.
 * @param swipeToCancelThreshold how far in the X direction the user needs to drag us to indicate that
 * he wishes to cancel the recording.
 * @param verticalThreshold how far in the Y direction the user needs to drag us to indicate that he
 * does not want to cancel the recording.
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
private fun Modifier.voiceRecordingGesture(
    horizontalSwipeProgress: () -> Float,
    onSwipeProgressChanged: (Float) -> Unit,
    onClick: () -> Unit = {},
    onStartRecording: () -> Boolean = { false },
    onFinishRecording: () -> Unit = {},
    onCancelRecording: () -> Unit = {},
    swipeToCancelThreshold: Dp = 200.dp,
    verticalThreshold: Dp = 80.dp,
): Modifier = this
    .pointerInput(key1 = Unit) { detectTapGestures(onTap = { onClick() }) }
    .pointerInput(key1 = Unit) {
        var offsetY = 0f
        var dragging = false
        val swipeToCancelThresholdPx: Float = swipeToCancelThreshold.toPx()
        val verticalThresholdPx: Float = verticalThreshold.toPx()

        detectDragGesturesAfterLongPress(
            onDragStart = {
                onSwipeProgressChanged(0f)
                offsetY = 0f
                dragging = true
                onStartRecording()
            },
            onDragCancel = {
                onCancelRecording()
                dragging = false
            },
            onDragEnd = {
                if (dragging) {
                    onFinishRecording()
                }
                dragging = false
            },
            onDrag = { change: PointerInputChange, dragAmount: Offset ->
                if (dragging) {
                    onSwipeProgressChanged(horizontalSwipeProgress() + dragAmount.x)
                    offsetY += dragAmount.y
                    val offsetX: Float = horizontalSwipeProgress()
                    if (
                        offsetX < 0 &&
                        abs(offsetX) >= swipeToCancelThresholdPx &&
                        abs(offsetY) <= verticalThresholdPx
                    ) {
                        onCancelRecording()
                        dragging = false
                    }
                }
            }
        )
    }
