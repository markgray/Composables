/*
 * Copyright (C) 2021 The Android Open Source Project
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

@file:Suppress("UNUSED_VARIABLE")

package com.example.composemail.ui.mails

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.ui.components.ContactImage
import com.example.composemail.ui.components.OneLineText
import com.example.composemail.ui.theme.Selection
import com.example.composemail.ui.utils.toHourMinutes

/**
 * A Composable that displays a mail entry item.
 *
 * This Composable is stateful. It manages the animated transition between the loading, normal,
 * and selected states. When [info] is null, it displays a loading indicator. When the [info]
 * is provided, it animates the transition to show the mail content. The item's appearance
 * also changes when it is selected via the [state].
 *
 * TODO: Continue here.
 *
 * @param modifier The modifier to be applied to the `MailItem`.
 * @param state The state of the `MailItem`, which includes whether it is selected.
 * @param info The information to display in the mail item. If null, a loading indicator is shown.
 * @param onMailOpen A callback invoked when the mail item is clicked.
 *
 * @see MotionLayoutMail where the layout and animations are defined.
 * @see PreviewConversationLoading for a preview of the loading state transition.
 */
@Composable
fun MailItem(
    modifier: Modifier = Modifier,
    state: MailItemState = MailItemState(id = -1) { _, _ -> },
    info: MailInfoPeek?,
    onMailOpen: (id: Int) -> Unit
) {
    // The layout (as a ConstraintSet ID) we want the Composable to take,
    // MotionLayout will animate the transition to that layout
    val targetState: MotionMailState =
        when {
            // No info to display, show a Loading state
            info == null -> MotionMailState.Loading
            // The item is selected, show as selected
            state.isSelected -> MotionMailState.Selected
            // The 'normal' state that just displays the given info
            else -> MotionMailState.Normal
        }
    MotionLayoutMail(
        modifier = modifier,
        info = info ?: MailInfoPeek.Default,
        targetState = targetState,
        onToggledMail = {
            // Toggle selection
            state.setSelected(!state.isSelected)
        },
        onOpenedMail = onMailOpen
    )
}

/**
 * TODO: Add kdoc
 */
const val ANIMATION_DURATION: Int = 400

/**
 * An enum that represents the different layout states of the Composable.
 *
 * Each corresponds to a ConstraintSet in the MotionScene.
 *
 * @param tag [String] to be used as the `name` of the `constraintSet`
 */
enum class MotionMailState(val tag: String) {
    /**
     * TODO: Add kdoc
     */
    Loading(tag = "empty"),

    /**
     * TODO: Add kdoc
     */
    Normal(tag = "normal"),

    /**
     * TODO: Add kdoc
     */
    Selected(tag = "flipped")
}

/**
 * TODO: Add kdoc
 */
@OptIn(ExperimentalMotionApi::class)
@Suppress("EXPERIMENTAL_API_USAGE")
@Composable
fun MotionLayoutMail(
    modifier: Modifier = Modifier,
    info: MailInfoPeek,
    targetState: MotionMailState,
    onToggledMail: (id: Int) -> Unit,
    onOpenedMail: (id: Int) -> Unit
) {
    val backgroundColor: Color by animateColorAsState(
        targetValue = when (targetState) {
            MotionMailState.Selected -> Selection.backgroundColor
            else -> MaterialTheme.colors.background
        },
        animationSpec = tween(durationMillis = ANIMATION_DURATION),
        label = ""
    )
    val initialStart: MotionMailState = remember { targetState }

    @Suppress("unused")
    val initialEnd: MotionMailState = remember {
        when (initialStart) {
            MotionMailState.Loading -> MotionMailState.Normal
            else -> MotionMailState.Loading
        }
    }
    val motionScene: MotionScene = remember {
        MotionScene {
            val (pictureRef: ConstrainedLayoutReference,
                checkRef: ConstrainedLayoutReference,
                contentRef: ConstrainedLayoutReference,
                loadingRef: ConstrainedLayoutReference
            ) = createRefsFor(
                "picture",
                "check",
                "content",
                "loading"
            )
            val normalCSet: ConstraintSetRef = constraintSet(name = MotionMailState.Normal.tag) {
                constrain(ref = pictureRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    centerVerticallyTo(other = parent)
                    start.linkTo(anchor = parent.start)
                }
                constrain(ref = checkRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start)

                    rotationY = 180f
                    @SuppressLint("Range")
                    alpha = 0.0f
                }
                constrain(ref = contentRef) {
                    width = Dimension.fillToConstraints
                    height = 60.dp.asDimension
                    top.linkTo(anchor = pictureRef.top)
                    start.linkTo(anchor = pictureRef.end, margin = 8.dp)
                    end.linkTo(anchor = parent.end, margin = 8.dp)
                }
                constrain(ref = loadingRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    centerVerticallyTo(other = parent)
                    end.linkTo(anchor = parent.start, margin = 32.dp)
                }
            }
            val selectedCSet: ConstraintSetRef = constraintSet(
                name = MotionMailState.Selected.tag,
                extendConstraintSet = normalCSet
            ) {
                constrain(ref = pictureRef) {
                    rotationY = -180f
                    @SuppressLint("Range")
                    alpha = 0.0f
                }
                constrain(ref = checkRef) {
                    rotationY = 0f
                    @SuppressLint("Range")
                    alpha = 1f
                }
            }
            val loadingCSet: ConstraintSetRef = constraintSet(name = MotionMailState.Loading.tag) {
                constrain(ref = pictureRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    top.linkTo(anchor = contentRef.top)
                    start.linkTo(anchor = parent.end, margin = 8.dp)
                }
                constrain(ref = checkRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    top.linkTo(anchor = contentRef.top)
                    start.linkTo(anchor = parent.end, margin = 8.dp)
                    rotationY = 180f
                    @SuppressLint("Range")
                    alpha = 0.0f
                }
                constrain(ref = contentRef) {
                    width = 120.dp.asDimension
                    height = 60.dp.asDimension
                    centerVerticallyTo(other = parent)
                    start.linkTo(anchor = pictureRef.end, margin = 32.dp)
                }
                constrain(ref = loadingRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    centerTo(other = parent)
                }
            }
            val initialStartCSet: ConstraintSetRef = when (initialStart) {
                MotionMailState.Normal -> normalCSet
                MotionMailState.Loading -> loadingCSet
                MotionMailState.Selected -> selectedCSet
            }
            val initialEndCSet: ConstraintSetRef = when (initialStart) {
                MotionMailState.Normal -> normalCSet
                MotionMailState.Loading -> loadingCSet
                MotionMailState.Selected -> selectedCSet
            }
            defaultTransition(from = initialStartCSet, to = initialEndCSet) {
                // Do nothing
            }
        }
    }

    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    MotionLayout(
        modifier = modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(size = 8.dp))
            .background(color = backgroundColor)
            .indication(
                interactionSource = interactionSource, // Consume MailContent's interactions
                indication = ripple(bounded = true)
            )
            .padding(all = 8.dp),
        constraintSetName = targetState.tag,
        animationSpec = tween(durationMillis = ANIMATION_DURATION),
        motionScene = motionScene
    ) {
        ContactImage(
            modifier = Modifier.layoutId(layoutId = "picture"),
            uri = info.from.profilePic,
            onClick = { onToggledMail(info.id) }
        )
        Image(
            modifier = Modifier
                .layoutId(layoutId = "check")
                .clip(shape = RoundedCornerShape(size = 10.dp))
                .background(color = MaterialTheme.colors.secondary),
            imageVector = Icons.Default.Check,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onSecondary),
            contentDescription = null
        )
        MailContent(
            modifier = Modifier
                .layoutId(layoutId = "content")
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Show no indication, delegate it to the parent
                    onClick = { onOpenedMail(info.id) }
                ),
            info = info
        )
        Box(
            modifier = Modifier.layoutId(layoutId = "loading"),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Consider leaving it until the transition from Loading to anything else finishes
            if (targetState == MotionMailState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 40.dp)
                )
            }
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
fun MailContent(
    modifier: Modifier = Modifier,
    info: MailInfoPeek
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            OneLineText(
                modifier = Modifier.weight(weight = 1.0f, fill = true),
                text = info.from.name,
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis
            )
            OneLineText(
                text = info.timestamp.toHourMinutes(),
                style = MaterialTheme.typography.body2,
            )
        }
        OneLineText(
            text = info.shortContent,
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun PreviewConversationLoading() {
    var info: MailInfoPeek? by remember { mutableStateOf(value = null) }
    Column(
        modifier = Modifier.size(width = 300.dp, height = 200.dp)
    ) {
        Button(onClick = { info = MailInfoPeek.Default }) {
            Text(text = "Run")
        }
        MailItem(
            info = info,
            onMailOpen = { /* Do nothing */ }
        )
    }
}

@Preview
@Composable
private fun PreviewConversation() {
    MailItem(
        info = MailInfoPeek.Default,
        onMailOpen = { /* Do nothing */ }
    )
}