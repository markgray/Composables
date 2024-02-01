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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.RichTooltipState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.R
import kotlinx.coroutines.CoroutineScope
import kotlin.math.abs
import kotlinx.coroutines.launch

/**
 * This is used as the microphone button at the end of the [UserInputText] Composable. When long
 * clicked it "pretends" to be recording.
 *
 * @param recording if `true` we are currently "recording", and our UI should reflect that fact.
 * @param swipeOffset used to keep track of how far the user has horizontally dragged the mic.
 * @param onSwipeOffsetChange called to update the value of [swipeOffset].
 * @param onStartRecording called by [detectDragGesturesAfterLongPress] when the user long presses
 * the microphone.
 * @param onFinishRecording called by [detectDragGesturesAfterLongPress] after all pointers are up.
 * @param onCancelRecording called by [detectDragGesturesAfterLongPress] if another gesture has
 * consumed pointer input.
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
        targetValueByState = { rec -> if (rec) 2f else 1f }
    )
    val containerAlpha: State<Float> = transition.animateFloat(
        transitionSpec = { tween(durationMillis = 2000) },
        label = "record-scale",
        targetValueByState = { rec -> if (rec) 1f else 0f }
    )
    val iconColor: State<Color> = transition.animateColor(
        transitionSpec = { tween(durationMillis = 200) },
        label = "record-scale",
        targetValueByState = { rec ->
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
        val tooltipState: RichTooltipState = remember { RichTooltipState() }
        RichTooltipBox(
            text = { Text(text = stringResource(id = R.string.touch_and_hold_to_record)) },
            tooltipState = tooltipState
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
    .pointerInput(key1 = Unit) { detectTapGestures { onClick() } }
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
            onDrag = { change, dragAmount ->
                if (dragging) {
                    onSwipeProgressChanged(horizontalSwipeProgress() + dragAmount.x)
                    offsetY += dragAmount.y
                    val offsetX = horizontalSwipeProgress()
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
