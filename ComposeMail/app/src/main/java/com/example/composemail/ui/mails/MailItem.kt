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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
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
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.MotionSceneScope
import androidx.constraintlayout.compose.TransitionScope
import com.example.composemail.model.data.Contact
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.ui.components.ContactImage
import com.example.composemail.ui.components.OneLineText
import com.example.composemail.ui.theme.Selection
import com.example.composemail.ui.utils.toHourMinutes
import java.time.Instant

/**
 * A Composable that displays a mail entry item.
 *
 * This Composable is stateful. It manages the animated transition between the loading, normal,
 * and selected states. When [info] is null, it displays a loading indicator. When the [info]
 * is provided, it animates the transition to show the mail content. The item's appearance
 * also changes when it is selected via the [MailItemState] parameter [state].
 *
 * We start by initializing our [MotionMailState] using a when expression:
 *  - if our [MailInfoPeek] parameter [info] is `null` there is no info to display, so we show a
 *  Loading state, ie. [MotionMailState.Loading]
 *  - if the [MailItemState.isSelected] property of our [MailItemState] parameter [state] is `true`,
 *  we show the selected state, ie. [MotionMailState.Selected]
 *  - otherwise, we show the normal state, ie. [MotionMailState.Normal]
 *
 * Our root composable is a [MotionLayoutMail] whose arguments are:
 *  - `modifier`: is our [Modifier] parameter [modifier]
 *  - `info`: is our [MailInfoPeek] parameter [info] if it is not `null` or [MailInfoPeek.Default]
 *  if it is `null`.
 *  - `targetState`: is our [MotionMailState] variable `targetState`
 *  - `onToggledMail`: is a lambda that calls the [MailItemState.setSelected] method of our
 *  [MailItemState] parameter [state] with the negation of the value returned by its
 *  [MailItemState.isSelected] method.
 *  - `onOpenedMail`: is our lambda parameter [onMailOpen]
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
 * The duration of the animations in the [MailItem] composable, in milliseconds.
 *
 * This value is used in the `tween` specifications for animations like the background color change
 * and the [MotionLayout] transitions between different [MotionMailState]s.
 */
const val ANIMATION_DURATION: Int = 400

/**
 * An enum that represents the different layout states of the [MailItem] Composable.
 *
 * Each state corresponds to a [ConstraintSet] in the [MotionScene] used by the [MotionLayoutMail]
 * Composable, which is responsible for the animations between states.
 *
 * @param tag The [String] identifier used as the `name` for the corresponding `constraintSet` in
 * the [MotionScene].
 */
enum class MotionMailState(val tag: String) {
    /**
     * The state where the `MailItem` is still loading its content. In this state, a
     * placeholder UI is shown.
     */
    Loading(tag = "empty"),

    /**
     * The state that displays the mail's information, such as the sender, a preview of the
     * content, and the timestamp. This is the default state when the mail item is not being
     * loaded and is not selected.
     */
    Normal(tag = "normal"),

    /**
     * The state where a mail item is selected, usually triggered by a long press or by tapping
     * the contact's picture. This state is visually distinct, for example by changing its
     * background color and showing a checkmark instead of the contact's picture.
     */
    Selected(tag = "flipped")
}

/**
 * A composable that displays a mail item with animations between states using [MotionLayout].
 *
 * This composable defines the layout and transitions for a single mail item. It uses a
 * [MotionScene] built with the Compose DSL to describe the constraints for three different
 * states:
 *  - [MotionMailState.Loading]: A loading indicator is shown in the center.
 *  - [MotionMailState.Normal]: The default state, showing the sender's picture and mail content.
 *  - [MotionMailState.Selected]: The sender's picture flips to reveal a checkmark icon, and the
 *  background color changes to indicate selection.
 *
 * The transition between these states is animated based on the [targetState] parameter.
 *
 * We start by initializing our [State] wrapped animated [Color] variable `backgroundColor` to the
 * value returned by [animateColorAsState] when called with the following arguments:
 *  - `targetValue`: when our [MotionMailState] parameter [targetState] is [MotionMailState.Selected]
 *  we pass [Selection.backgroundColor], otherwise we pass [Colors.background] of our custom
 *  [MaterialTheme.colors]
 *  - `animationSpec`: we pass a [tween] with a duration of [ANIMATION_DURATION] (400 miliseconds).
 *  - `label`: we pass an empty [String].
 *
 * We initialize and remember our [MotionMailState] variable `initialStart` with the initial value
 * of our [MotionMailState] parameter [targetState].
 *
 * We initialize and remember our [MotionMailState] variable `initialEnd` using a when expression:
 *  - if our [MotionMailState] variable is [MotionMailState.Loading] we initialize it to
 *  [MotionMailState.Normal]
 *  - otherwise we initialize it to [MotionMailState.Loading]
 *
 * We initialize and remember our [MotionScene] variable `motionScene` to a new instance in whose
 * [MotionSceneScope] `motionSceneContent` lambda argument:
 *
 * **First** we initialize our [ConstrainedLayoutReference] variables `pictureRef`, `checkRef`,
 * `contentRef` and `loadingRef` to the [ConstrainedLayoutReference]'s returned by
 * [MotionSceneScope.createRefFor] for the strings "picture", "check", "content" and "loading"
 * respectively.
 *
 * **Second** we initialize our [ConstraintSetRef] variable `normalCSet` to refer to the
 * [ConstraintSet] created by [MotionSceneScope.constraintSet] for the `name` [MotionMailState.tag]
 * of [MotionMailState.Normal]. In the [ConstraintSetScope] `constraintSetContent` lambda argument
 * of the [MotionSceneScope.constraintSet]:
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `pictureRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.width] to `60.dp`
 *  - set the [ConstrainScope.height] to `60.dp`
 *  - call [ConstrainScope.centerVerticallyTo] to center it vertically to the parent
 *  - set the [ConstrainScope.start] to the start of the parent
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `checkRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.width] to `60.dp`
 *  - set the [ConstrainScope.height] to `60.dp`
 *  - call [ConstrainScope.centerVerticallyTo] to center it vertically to the parent
 *  - link its `start` to the `start` of the parent
 *  - set its [ConstrainScope.rotationY] to `180f`
 *  - set its [ConstrainScope.alpha] to `0.0f`
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `contentRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.width] to [Dimension.fillToConstraints]
 *  - set the [ConstrainScope.height] to `60.dp`
 *  - link its `top` to the `top` of the [ConstrainedLayoutReference] variable `pictureRef`
 *  - link its `start` to the `end` of the [ConstrainedLayoutReference] variable `pictureRef`
 *  with a `margin` of `8.dp`
 *  - link its `end` to the `end` of the parent with a `margin` of `8.dp`
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `loadingRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.width] to `60.dp`
 *  - set the [ConstrainScope.height] to `60.dp`
 *  - call [ConstrainScope.centerVerticallyTo] to center the [ConstrainedLayoutReference]
 *  variable `loadingRef` vertically to the parent
 *  - link its `end` to the `start` of its parent with a margin of `32.dp`
 *
 * **Third** we initialize our [ConstraintSetRef] variable `selectedCSet` to the  [ConstraintSet]
 * created by [MotionSceneScope.constraintSet] for the `name` [MotionMailState.tag] of
 * [MotionMailState.Selected], with its `extendConstraintSet` argument our [ConstraintSetRef]
 * variable `normalCSet`. In the [ConstraintSetScope] `constraintSetContent` lambda argument
 * of [MotionSceneScope.constraintSet]:
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `pictureRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.rotationY] to `-180f`
 *  - set the [ConstrainScope.alpha] to `0.0f`
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `checkRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.rotationY] to `0f`
 *  - set the [ConstrainScope.alpha] to `1f`
 *
 * **Fourth** we initialize our [ConstraintSetRef] variable `loadingCSet` to refer to the
 * [ConstraintSet] created by [MotionSceneScope.constraintSet] for the `name` [MotionMailState.tag]
 * of [MotionMailState.Loading]. In the [ConstraintSetScope] `constraintSetContent` lambda argument
 * of [MotionSceneScope.constraintSet]:
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `pictureRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.width] to `60.dp`
 *  - set the [ConstrainScope.height] to `60.dp`
 *  - link its `top` to the `top` of the [ConstrainedLayoutReference] variable `contentRef`
 *  - link its `start` to the `end` of its parent with a `margin` of `8.dp`
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `checkRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.width] to `60.dp`
 *  - set the [ConstrainScope.height] to `60.dp`
 *  - link its `top` to the `top` of the [ConstrainedLayoutReference] variable `contentRef`
 *  - link its `start` to the `end` of its parent with a `margin` of `8.dp`
 *  - set the [ConstrainScope.rotationY] to `180f`
 *  - set the [ConstrainScope.alpha] to `0.0f`
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `contentRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.width] to `120.dp`
 *  - set the [ConstrainScope.height] to `60.dp`
 *  - call [ConstrainScope.centerVerticallyTo] to center it vertically to the parent
 *  - link its `start` to the `end` of the [ConstrainedLayoutReference] variable `pictureRef`
 *  with a `margin` of `32.dp`
 *
 * We call the [ConstraintSetScope.constrain] method to constrain the [ConstrainedLayoutReference]
 * variable `loadingRef` and in the [ConstrainScope] `constrainBlock` lambda argument, we:
 *  - set the [ConstrainScope.width] to `60.dp`
 *  - set the [ConstrainScope.height] to `60.dp`
 *  - call [ConstrainScope.centerTo] to center the [ConstrainedLayoutReference] variable `loadingRef`
 *  to the parent
 *
 * **Fifth** we initialize our [ConstraintSetRef] variable `initialStartCSet` using a when expression:
 *  - if our [MotionMailState] variable `initialStart` is [MotionMailState.Normal] we initialize
 *  it to [ConstraintSetRef] variable `normalCSet`
 *  - if our [MotionMailState] variable `initialStart` is [MotionMailState.Loading] we initialize
 *  it to [ConstraintSetRef] variable `loadingCSet`
 *  - if our [MotionMailState] variable `initialStart` is [MotionMailState.Selected] we initialize
 *  it to [ConstraintSetRef] variable `selectedCSet`
 *
 * **Sixth** we initialize our [ConstraintSetRef] variable `initialEndCSet` using a when expression:
 *  - if our [MotionMailState] variable `initialStart` is [MotionMailState.Normal] we initialize
 *  it to [ConstraintSetRef] variable `normalCSet`
 *  - if our [MotionMailState] variable `initialStart` is [MotionMailState.Loading] we initialize
 *  it to [ConstraintSetRef] variable `loadingCSet`
 *  - if our [MotionMailState] variable `initialStart` is [MotionMailState.Selected] we initialize
 *  it to [ConstraintSetRef] variable `selectedCSet`
 *
 * **Seventh** we call the [MotionSceneScope.defaultTransition] method to define the default
 * transition from `initialStartCSet` to `initialEndCSet` passing a do-nothing lambda as its
 * [TransitionScope] `transitionContent` lambda argument.
 *
 * Having defined our [MotionScene] we initialize and remember our [MutableInteractionSource] variable
 * `interactionSource` to a new instance.
 *
 * Our root composable is a [MotionLayout] whose arguments are:
 *  - `modifier`: is our [Modifier] parameter [modifier] chained to a [Modifier.fillMaxSize] chained
 *  to a [Modifier.clip] whose `shape` is a [RoundedCornerShape] with a `size` of `8.dp`, chained to
 *  a [Modifier.background] whose `color` is our [State] wrapped animated [Color] variable
 *  `backgroundColor`, chained to a [Modifier.indication] whose `interactionSource` is our
 *  [MutableInteractionSource] variable `interactionSource` and whose `indication` is a [ripple]
 *  with a `bounded` argument of `true`, chained to a [Modifier.padding] that adds `8.dp` padding
 *  to all sides.
 *  - `constraintSetName`: is the [MotionMailState.tag] of our [MotionMailState] parameter [targetState]
 *  - `animationSpec`: is a [tween] with a duration of [ANIMATION_DURATION] (400 miliseconds)
 *  - `motionScene`: is our [MotionScene] variable `motionScene`.
 *
 * In the [MotionLayoutScope] `content` composable lambda argument of the [MotionLayout] we:
 *
 * **First** Compose a [ContactImage] whose arguments are:
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is "picture"
 *  - `uri`: is the [Contact.profilePic] of the [MailInfoPeek.from] property of our [MailInfoPeek]
 *  parameter `info`.
 *  - `onClick`: is a lambda that calls our [onToggledMail] lambda parameter with the [MailInfoPeek.id]
 *  property of our [MailInfoPeek] parameter [info].
 *
 * **Second** Compose a [Image] whose arguments are:
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is "check", chained to a [Modifier.clip]
 *  whose `shape` is a [RoundedCornerShape] with a `size` of `10.dp`, chained to a [Modifier.background]
 *  whose `color` is the [Colors.secondary] of our custom [MaterialTheme.colors]
 *  - `imageVector`: is an [Icons.Filled.Check]
 *  - `colorFilter`: is a [ColorFilter.tint] whose `color` is the [Colors.onSecondary] of our custom
 *  [MaterialTheme.colors]
 *  - `contentDescription`: is `null`
 *
 * **Third** Compose a [MailContent] whose arguments are:
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is "content", chained to a [Modifier.clickable]
 *  whose `interactionSource` is our [MutableInteractionSource] variable `interactionSource`, whose
 *  `indication` is `null`, and whose `onClick` argument is a lambda that calls our [onOpenedMail]
 *  lambda parameter with the [MailInfoPeek.id] property of our [MailInfoPeek] parameter [info]
 *  - `info`: is our [MailInfoPeek] parameter [info]
 *
 * **Fourth** Compose a [Box] whose arguments are:
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is "loading".
 *  - `contentAlignment`: is [Alignment.Center]
 *
 * In the [BoxScope] `content` lambda argument of the [Box] if our [MotionMailState] parameter
 * [targetState] is [MotionMailState.Loading] we compose a [CircularProgressIndicator] whose
 * `modifier` is a [Modifier.size] whose `size` is `40.dp`.
 *
 * @param modifier The modifier to be applied to the [MotionLayout].
 * @param info The [MailInfoPeek] data to be displayed in the mail item.
 * @param targetState The target [MotionMailState] to which the layout should animate.
 * @param onToggledMail A callback invoked with the mail ID when the sender's image is clicked,
 * used to toggle the selection state.
 * @param onOpenedMail A callback invoked with the mail ID when the main content area is clicked,
 * used to open the mail.
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
 * A Composable that displays the main content of a mail item, including the sender, timestamp,
 * and a short preview of the content.
 *
 * This layout consists of a `Column` containing two rows:
 *  1. A `Row` that displays the sender's name on the left (truncated with an ellipsis if too long)
 *  and the mail's timestamp on the right.
 *  2. A `OneLineText` that shows a short preview of the mail's content, also truncated with an
 *  ellipsis if it exceeds the available space.
 *
 * Our root composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier]
 * chained to a [Modifier.padding] that adds `4.dp` to the vertical sides, and whose `verticalArrangement`
 * is [Arrangement.SpaceBetween]. In the [ColumnScope] `content` composable lambda argument of the
 * [Column] we:
 *
 * **First** compose a [Row] whose `horizontalArrangement` argument is [Arrangement.SpaceBetween].
 * In the [RowScope] `content` composable lambda argument of the [Row] we:
 *
 * Compose a [OneLineText] whose arguments are:
 *  - `modifier`: is a [RowScope.weight] whose `weight` is `1.0f` and whose `fill` is `true`.
 *  - `text`: is the [Contact.name] of our [MailInfoPeek] parameter [info].
 *  - `style`: is the [Typography.body1] of our custom [MaterialTheme.typography].
 *  - `overflow`: is [TextOverflow.Ellipsis].
 *
 * Compose a second [OneLineText] whose arguments are:
 *  - `text`: is the [MailInfoPeek.timestamp] of our [MailInfoPeek] parameter [info] converted to
 *  a string using [Instant.toHourMinutes]
 *  - `style`: is the [Typography.body2] of our custom [MaterialTheme.typography].
 *
 * **Second** compose a [OneLineText] whose arguments are:
 *  - `text`: is the [MailInfoPeek.shortContent] of our [MailInfoPeek] parameter [info].
 *  - `style`: is the [Typography.body2] of our custom [MaterialTheme.typography].
 *  - `overflow`: is [TextOverflow.Ellipsis].
 *
 * @param modifier The modifier to be applied to the `Column` that holds the content.
 * @param info The [MailInfoPeek] object containing the data to display.
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

/**
 * A preview Composable for demonstrating the transition of a [MailItem] from its loading state
 * to the content-displayed state.
 *
 * This preview sets up a simple UI with a "Run" button and a [MailItem]. Initially, the
 * [MailItem] is in a loading state because the `info` passed to it is `null`. When the
 * "Run" button is clicked, the state is updated with default mail information, triggering
 * the animated transition within the [MailItem] to show the mail content.
 *
 * @see MailItem
 */
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

/**
 * A preview of the [MailItem] Composable in its default, "normal" state.
 *
 * This preview displays a `MailItem` using the default `MailInfoPeek` data, representing a
 * typical, unselected mail item in a list. The `onMailOpen` callback is a no-op for preview
 * purposes.
 */
@Preview
@Composable
private fun PreviewConversation() {
    MailItem(
        info = MailInfoPeek.Default,
        onMailOpen = { /* Do nothing */ }
    )
}