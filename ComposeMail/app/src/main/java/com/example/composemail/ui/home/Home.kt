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

@file:Suppress("LiftReturnOrAssignment")

package com.example.composemail.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.MotionSceneScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import com.example.composemail.model.ComposeMailModel
import com.example.composemail.model.data.MailInfoFull
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.ui.compositionlocal.FoldableInfo
import com.example.composemail.ui.compositionlocal.LocalFoldableInfo
import com.example.composemail.ui.compositionlocal.LocalWidthSizeClass
import com.example.composemail.ui.mails.MailList
import com.example.composemail.ui.mails.MailListState
import com.example.composemail.ui.newmail.NewMailButton
import com.example.composemail.ui.newmail.NewMailLayoutState
import com.example.composemail.ui.newmail.NewMailState
import com.example.composemail.ui.newmail.rememberNewMailState
import com.example.composemail.ui.viewer.MailToolbar
import com.example.composemail.ui.viewer.MailViewer
import kotlinx.coroutines.flow.Flow

/**
 * The different home states that are supported by the [MotionLayout] in [ComposeMailHome].
 *
 * Each state represents a [ConstraintSet] and has a unique [tag] that is used to identify that
 * [ConstraintSet] by the [ConstraintSetRef.name] property that points to it.
 *
 * The current state is resolved in [resolveConstraintSet] based on the screen size, fold state,
 * and whether a mail is open.
 */
private enum class HomeState(val tag: String) {
    /**
     * The default state where only the mail list is visible and the mail viewer is hidden.
     */
    ListOnly(tag = "listOnlyCompactAndExpanded"),

    /**
     * When a mail is opened on a compact screen, the mail viewer takes up the full screen, hiding
     * the mail list.
     */
    MailOpenCompact(tag = "mailOpenCompact"),

    /**
     * On larger screens, opening a mail results in a split-pane view, with the mail list on the
     * left and the mail viewer on the right.
     */
    MailOpenExpanded(tag = "mailOpenExpanded"),

    /**
     * On foldable devices in a half-opened (tabletop) posture, the mail viewer is on the top
     * half of the screen and the mail list is on the bottom half.
     */
    MailOpenHalf(tag = "mailOpenHalf")
}

/**
 * The [MotionScene] that models the animations and states of the home screen.
 *
 * It defines four states ([HomeState]):
 *  1. [HomeState.ListOnly]: The default state where only the mail list is visible.
 *  2. [HomeState.MailOpenCompact]: When a mail is opened on a compact screen, the viewer takes
 *  up the full screen.
 *  3. [HomeState.MailOpenExpanded]: On larger screens, opening a mail results in a split-pane view,
 *  with thelist on the left and the viewer on the right.
 *  4. [HomeState.MailOpenHalf]: On foldable devices in a half-opened (tabletop) posture, the mail
 *  viewer is on the top half of the screen and the mail list is on the bottom half.
 *
 * Transitions between these states are managed by the [MotionLayout] in [ComposeMailHome].
 */
@OptIn(ExperimentalMotionApi::class)
private val homeMotionScene = MotionScene {
    val (listRef: ConstrainedLayoutReference,
        toolbarRef: ConstrainedLayoutReference,
        viewerRef: ConstrainedLayoutReference,
        newMailButtonRef: ConstrainedLayoutReference,
        mailToolbarRef: ConstrainedLayoutReference
    ) = createRefsFor(
        "list",
        "toolbar",
        "viewer",
        "newMailButton",
        "mailToolbar",
    )

    /**
     * [ConstraintSetScope] extension method that sets the constraints for the `toolbarRef`
     * [ConstrainedLayoutReference]. It calls the [ConstraintSetScope.constrain] method of its
     * receiver to constrain `toolbarRef` and in the [ConstrainScope] `constrainBlock` lambda
     * argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.matchParent]
     *  - sets the [ConstrainScope.height] to `60.dp`
     *  - links its `top` to its parent's `top`
     *  - links its `start` to its parent's `start`
     */
    val setToolbarConstraints: ConstraintSetScope.() -> Unit = {
        constrain(ref = toolbarRef) {
            width = Dimension.matchParent
            // Toolbar has unstable vertical wrap content, so we pick a size that works for both
            // components supported in the toolbar
            height = Dimension.value(dp = 60.dp)
            top.linkTo(anchor = parent.top)
            start.linkTo(anchor = parent.start)
        }
    }

    /**
     * [ConstraintSetScope] extension method that sets the constraints for the `mailToolbarRef`
     * [ConstrainedLayoutReference]. (Mail toolbar constraints for whenever a Mail is open) It calls
     * the [ConstraintSetScope.constrain] method of its receiver to constrain `mailToolbarRef` and
     * in the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - links its `end` to its parent's `end` with a `margin` of `12.dp`
     *  - links its `bottom` to its parent's `bottom` with a `margin` of `16.dp`
     */
    val setVisibleMailToolbarConstraints: ConstraintSetScope.() -> Unit = {
        constrain(ref = mailToolbarRef) {
            end.linkTo(anchor = parent.end, margin = 12.dp)
            bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
        }
    }

    /**
     * [ConstraintSet] that defines the constraints for the [HomeState.ListOnly] state. We call the
     * [MotionSceneScope.constraintSet] method of the [MotionSceneScope] to create a new
     * [ConstraintSet] for the [ConstraintSetRef] variable `listOnlyCSet`. In the [ConstraintSetScope]
     * `constraintSetContent` lambda argument it:
     *
     * **First** Calls the [ConstraintSetScope.constrain] method to constrain both the `listRef`
     * and `viewerRef` [ConstrainedLayoutReference]s. In the [ConstrainScope] `constrainBlock`
     * lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.percent] with a `percent` of `1f`
     *  - sets the [ConstrainScope.height] to [Dimension.fillToConstraints]
     *  - links their `top` to the `toolbarRef`'s `bottom`
     *  - links their `bottom` to the parent's `bottom`
     *
     * **Second** Calls the [ConstraintSetScope.constrain] method to constrain the `listRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it
     * links its `start` to the parent's `start`
     *
     * **Third** Calls the [ConstraintSetScope.constrain] method to constrain the `viewerRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it
     * links its `start` to the parent's `end`
     *
     * **Fourth** Calls the [ConstraintSetScope.constrain] method to constrain the `mailToolbarRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - links its `top` to its parent's `bottom` with a `margin` of `16.dp`
     *  - links its `end` to its parent's `end`
     *
     * **Fifth** Calls the [ConstraintSetScope.constrain] method to constrain the `newMailButtonRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.matchParent]
     *  - sets the [ConstrainScope.height] to [Dimension.matchParent]
     *  - links its `top` to the parent's `top`
     *  - links its `start` to the parent's `start`
     *
     * **Sixth** calls the `setToolbarConstraints` method to set the constraints for the `toolbarRef`
     * [ConstrainedLayoutReference].
     */
    val listOnlyCSet: ConstraintSetRef = constraintSet(name = HomeState.ListOnly.tag) {
        constrain(listRef, viewerRef) {
            width = Dimension.percent(percent = 1f)
            height = Dimension.fillToConstraints
            top.linkTo(anchor = toolbarRef.bottom)
            bottom.linkTo(anchor = parent.bottom)
        }
        constrain(ref = listRef) {
            start.linkTo(parent.start)
        }
        constrain(ref = viewerRef) {
            start.linkTo(anchor = parent.end)
        }
        constrain(ref = mailToolbarRef) {
            top.linkTo(anchor = parent.bottom, margin = 16.dp)
            end.linkTo(anchor = parent.end)
        }
        constrain(ref = newMailButtonRef) {
            width = Dimension.matchParent
            height = Dimension.matchParent
            top.linkTo(anchor = parent.top)
            start.linkTo(anchor = parent.start)
        }
        setToolbarConstraints()
    }

    /**
     * [ConstraintSet] that defines the constraints for the [HomeState.MailOpenCompact] state.
     * (Mail open on a compact screen) We call the [MotionSceneScope.constraintSet] method of the
     * [MotionSceneScope] to create a new [ConstraintSet] for the [ConstraintSetRef] variable
     * `mailCompactCSet`. In the [ConstraintSetScope] `constraintSetContent` lambda argument it:
     *
     * **First** Calls the [ConstraintSetScope.constrain] method to constrain both the `listRef`
     * and `viewerRef` [ConstrainedLayoutReference]s. In the [ConstrainScope] `constrainBlock`
     * lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.percent] with a `percent` of `1f`
     *  - sets the [ConstrainScope.height] to [Dimension.fillToConstraints]
     *  - links their `top` to the `toolbarRef`'s `bottom`
     *  - links their `bottom` to the parent's `bottom`
     *
     * **Second** Calls the [ConstraintSetScope.constrain] method to constrain the `listRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it
     * links its `start` to the parent's `start`.
     *
     * **Third** Calls the [ConstraintSetScope.constrain] method to constrain the `viewerRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it
     * links its `start` to the parent's `start`.
     *
     * **Fourth** Calls the [ConstraintSetScope.constrain] method to constrain the `newMailButtonRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.matchParent]
     *  - sets the [ConstrainScope.height] to [Dimension.fillToConstraints]
     *  - links its `start` to the parent's `start`
     *  - links its `top` to the parent's `top`
     *  - links its `bottom` to the `mailToolbarRef`'s `top` with a `margin` of `8.dp`
     *
     * **Fifth** calls the `setToolbarConstraints` method to set the constraints for the `toolbarRef`
     * [ConstrainedLayoutReference].
     *
     * **Sixth** calls the `setVisibleMailToolbarConstraints` method to set the constraints for the
     * `mailToolbarRef` [ConstrainedLayoutReference].
     */
    val mailCompactCSet: ConstraintSetRef = constraintSet(name = HomeState.MailOpenCompact.tag) {
        constrain(listRef, viewerRef) {
            width = Dimension.percent(percent = 1f)
            height = Dimension.fillToConstraints
            top.linkTo(anchor = toolbarRef.bottom)
            bottom.linkTo(anchor = parent.bottom)
        }
        constrain(ref = listRef) {
            end.linkTo(anchor = parent.start)
        }
        constrain(ref = viewerRef) {
            start.linkTo(anchor = parent.start)
        }
        constrain(ref = newMailButtonRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints
            start.linkTo(anchor = parent.start)
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = mailToolbarRef.top, margin = 8.dp)
        }
        setToolbarConstraints()
        setVisibleMailToolbarConstraints()
    }

    /**
     * [ConstraintSet] that defines the constraints for the [HomeState.MailOpenExpanded] state.
     * (Mail open on a larger screen) In the [ConstraintSetScope] `constraintSetContent` lambda
     * argument it:
     *
     * **First** initializes its [ConstraintLayoutBaseScope.VerticalAnchor] variable `midGuideline`
     * using the [ConstraintLayoutBaseScope.createGuidelineFromAbsoluteLeft] method with a `fraction`
     * of `0.5f`.
     *
     * **Second** Calls the [ConstraintSetScope.constrain] method to constrain both the `listRef`
     * and `viewerRef` [ConstrainedLayoutReference]s. In the [ConstrainScope] `constrainBlock`
     * lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.fillToConstraints]
     *  - sets the [ConstrainScope.height] to [Dimension.fillToConstraints]
     *  - links their `top` to the `bottom` of [ConstrainedLayoutReference] `toolbarRef`
     *  - links their `bottom` to their parent's `bottom`
     *
     * **Third** Calls the [ConstraintSetScope.constrain] method to constrain the `listRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - links its `start` to the parent's `start`
     *  - links its `end` to the `midGuideline`
     *
     * **Fourth** Calls the [ConstraintSetScope.constrain] method to constrain the `viewerRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - links its `start` to the `midGuideline`
     *  - links its `end` to the parent's `end`
     *
     * **Fifth** Calls the [ConstraintSetScope.constrain] method to constrain the `newMailButtonRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.matchParent]
     *  - sets the [ConstrainScope.height] to [Dimension.fillToConstraints]
     *  - links its `start` to the parent's `start`
     *  - links its `top` to the parent's `top`
     *  - links its `bottom` to the `top` of [ConstrainedLayoutReference] `mailToolBarRef` with a
     *  `margin` of `8.dp'
     *
     * **Sixth** calls the `setToolbarConstraints` method to set the constraints for the `toolbarRef`
     * [ConstrainedLayoutReference].
     *
     * **Seventh** calls the `setVisibleMailToolbarConstraints` method to set the constraints for the
     * `mailToolbarRef` [ConstrainedLayoutReference].
     */
    constraintSet(name = HomeState.MailOpenExpanded.tag) {
        val midGuideline: ConstraintLayoutBaseScope.VerticalAnchor =
            createGuidelineFromAbsoluteLeft(fraction = 0.5f)

        constrain(listRef, viewerRef) {
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
            top.linkTo(anchor = toolbarRef.bottom)
            bottom.linkTo(anchor = parent.bottom)
        }
        constrain(ref = listRef) {
            start.linkTo(anchor = parent.start)
            end.linkTo(anchor = midGuideline)
        }
        constrain(ref = viewerRef) {
            start.linkTo(anchor = midGuideline)
            end.linkTo(anchor = parent.end)
        }
        constrain(ref = newMailButtonRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints
            start.linkTo(anchor = parent.start)
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = mailToolbarRef.top, margin = 8.dp)
        }
        setToolbarConstraints()
        setVisibleMailToolbarConstraints()
    }

    /**
     * [ConstraintSet] that defines the constraints for the [HomeState.MailOpenHalf] state.
     * (Mail open on a foldable device in a half-opened posture) In the [ConstraintSetScope]
     * `constraintSetContent` lambda argument it:
     *
     * **First** initializes its [ConstraintLayoutBaseScope.VerticalAnchor] variable `midGuideline`
     * using the [ConstraintLayoutBaseScope.createGuidelineFromAbsoluteLeft] method with a `fraction`
     * of `0.5f`.
     *
     * **Second** Calls the [ConstraintSetScope.constrain] method to constrain the `viewerRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.matchParent]
     *  - sets the [ConstrainScope.height] to [Dimension.fillToConstraints]
     *  - links its `start` to the parent's `start`
     *  - links its `top` to the parent's `top`
     *  - links its `bottom` to the `midGuideline`
     *
     * **Third** Calls the [ConstraintSetScope.constrain] method to constrain the `toolbarRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.matchParent]
     *  - sets the [ConstrainScope.height] to [Dimension.wrapContent]
     *  - links its `top` to the `midGuideline`
     *  - links its `start` to the parent's `start`
     *
     * **Fourth** Calls the [ConstraintSetScope.constrain] method to constrain the `listRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.matchParent]
     *  - sets the [ConstrainScope.height] to [Dimension.fillToConstraints]
     *  - links its `start` to the parent's `start`
     *  - links its `top` to the [ConstrainedLayoutReference] `toolbarRef` `bottom`
     *  - links its `bottom` to the parent's `bottom`
     *
     * **Fifth** Calls the [ConstraintSetScope.constrain] method to constrain the `newMailButtonRef`
     * [ConstrainedLayoutReference]. In the [ConstrainScope] `constrainBlock` lambda argument it:
     *  - sets the [ConstrainScope.width] to [Dimension.matchParent]
     *  - sets the [ConstrainScope.height] to [Dimension.fillToConstraints]
     *  - links its `start` to the parent's `start`
     *  - links its `top` to the parent's `top`
     *  - links its `bottom` to the `top` of [ConstrainedLayoutReference] `mailToolBarRef` with a
     *  `margin` of `8.dp'
     *
     * **Sixth** calls the `setToolbarConstraints` method to set the constraints for the `toolbarRef`
     * [ConstrainedLayoutReference].
     *
     * Having configured our [ConstraintSet]'s we call the [MotionSceneScope.defaultTransition]
     * method to set the default transition from the `listOnlyCSet` [ConstraintSetRef] to the
     * `mailCompactCSet` [ConstraintSetRef] to be a do-nothing lambda.
     */
    constraintSet(name = HomeState.MailOpenHalf.tag) {
        val midGuideline: ConstraintLayoutBaseScope.HorizontalAnchor =
            createGuidelineFromTop(fraction = 0.5f)

        constrain(ref = viewerRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints
            start.linkTo(anchor = parent.start)
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = midGuideline)
        }
        constrain(ref = toolbarRef) {
            width = Dimension.matchParent
            height = Dimension.wrapContent
            top.linkTo(anchor = midGuideline)
            start.linkTo(anchor = parent.start)
        }
        constrain(ref = listRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints
            start.linkTo(anchor = parent.start)
            top.linkTo(anchor = toolbarRef.bottom)
            bottom.linkTo(anchor = parent.bottom)
        }
        constrain(ref = newMailButtonRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints
            start.linkTo(anchor = parent.start)
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = mailToolbarRef.top, margin = 8.dp)
        }
        setVisibleMailToolbarConstraints()
    }
    defaultTransition(from = listOnlyCSet, to = mailCompactCSet) {
        // Do nothing
    }
}

/**
 * The main screen of the application.
 *
 * It uses a [MotionLayout] to animate between different states of the UI based on user
 * interaction, screen size, and device posture. The states are defined in [HomeState] and the
 * transitions and constraints are in [homeMotionScene].
 *
 * The layout is composed of:
 *  - [TopToolbar]: The top app bar.
 *  - [MailList]: The list of emails in the inbox.
 *  - [MailViewer]: The screen to view a single email.
 *  - [NewMailButton]: A floating action button to compose a new email.
 *  - [MailToolbar]: A toolbar with actions for an open email (like close).
 *
 * The current state is determined by [resolveConstraintSet], which considers whether an email is
 * open, whether the screen is compact, and whether the device is a foldable in tabletop mode.
 *
 * We start by initializing our [ComposeMailModel] variable `mailModel` to the existing (or newly
 * created if none exists yet) [ComposeMailModel] returned by the [viewModel] method. We initialize
 * and remember our [MailListState] variable `listState` to a new instance of [MailListState]. We
 * use the [rememberNewMailState] method to initialize and remember our [NewMailState] variable
 * `newMailState` to a new instance whose `initialLayoutState` is set to [NewMailLayoutState.Fab].
 * We initialize our [Boolean] variable `isCompact` to the result of checking if the current
 * [LocalWidthSizeClass] is [WindowWidthSizeClass.Compact], and initialize our [Boolean] variable
 * `isHalfOpen` to the result of checking if the current [LocalFoldableInfo] is
 * [FoldableInfo.isHalfOpen].
 *
 * We initialize our [HomeState] variable `currentConstraintSet` to the result of calling the
 * [resolveConstraintSet] method with the following arguments:
 *  - `isMailOpen`: the value of the [ComposeMailModel.isMailOpen] property of our [ComposeMailModel]
 *  variable `mailModel`
 *  - `isCompact`: the value of our [Boolean] variable `isCompact`
 *  - `isHalfOpen`: the value of our [Boolean] variable `isHalfOpen`
 *
 * Our root composable is a [MotionLayout] whose arguments are:
 *  - `motionScene`: our [MotionScene] property [homeMotionScene]
 *  - `constraintSetName`: the value of the [HomeState.tag] of our [HomeState] variable
 *  `currentConstraintSet`.
 *  - `animationSpec`: a [tween] with a duration of 400 milliseconds.
 *  - `modifier`: our [Modifier] parameter [modifier].
 *
 * In the [MotionLayoutScope] `content` composable lambda argument of the [MotionLayout] we:
 *
 * **First** Compose a [TopToolbar] whose arguments are:
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is `"toolbar"`
 *  - `selectionCountProvider`: function that returns the number of selected items in the [MailList]
 *  is a function reference to the [MailListState.selectedCount] method of our [MailListState]
 *  variable `listState`.
 *  - `onUnselectAll`: a function that unselects all items in the [MailList] is a function reference
 *  to the [MailListState.unselectAll] method of our [MailListState] variable `listState`
 *
 * **Second** Compose a [Column] whose `modifier argument is a [Modifier.layoutId] whose `layoutId`
 * is `"list"`. In the [ColumnScope] `content` composable lambda argument of the [Column] we:
 *  - Compose a [Text] whose `modifier` argument is a [Modifier.padding] that adds `4.dp` to the
 *  horizontal sides, whose `text` argument is the [String] "Inbox", and whose `style` argument is
 *  the [Typography.h6] of our custom [MaterialTheme.typography].
 *  - Compose a [MailList] whose `modifier` argument is a [Modifier.padding] that adds `4.dp` to the
 *  horizontal sides chained to a [ColumnScope.weight] whose `weight` argument is `1.0f` and whose
 *  `fill` argument is `true`, chained to a [Modifier.fillMaxWidth]. The `listState` argument is our
 *  [MailListState] variable `listState`. The `observableConversations` argument is the
 *  [Flow] of [PagingData] of [MailInfoPeek] property [ComposeMailModel.conversations] of our
 *  [ComposeMailModel] variable `mailModel`. The `onMailOpen` argument is a function reference to
 *  the [ComposeMailModel.openMail] method of our [ComposeMailModel] variable `mailModel`.
 *
 * **Third** Compose a [MailViewer] whose arguments are:
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is `"viewer"`, chained to a
 *  [Modifier.padding] that adds `8.dp` to all sides.
 *  - `mailInfoFull`: the [MailInfoFull] property [ComposeMailModel.openedMail] of our
 *  [ComposeMailModel] variable `mailModel` if that is not `null` or [MailInfoFull.Default] if it
 *  is `null`.
 *
 * **Fourth** Compose a [NewMailButton] whose arguments are:
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is `"newMailButton"`.
 *  - `state`: our [NewMailState] variable `newMailState`.
 *
 * **Fifth** Compose a [MailToolbar] whose arguments are:
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is `"mailToolbar"`.
 *  - `onCloseMail`: a function reference to the [ComposeMailModel.closeMail] method of our
 *  [ComposeMailModel] variable `mailModel`.
 *
 * @param modifier A [Modifier] for this composable that our caller can use to modify our appearance
 * and or behavior.
 */
@OptIn(ExperimentalMotionApi::class)
@Composable
fun ComposeMailHome(modifier: Modifier) {
    val mailModel: ComposeMailModel = viewModel()
    val listState: MailListState = remember { MailListState() }
    val newMailState: NewMailState =
        rememberNewMailState(initialLayoutState = NewMailLayoutState.Fab)

    val isCompact: Boolean = LocalWidthSizeClass.current == WindowWidthSizeClass.Compact
    val isHalfOpen: Boolean = LocalFoldableInfo.current.isHalfOpen

    val currentConstraintSet: HomeState = resolveConstraintSet(
        isMailOpen = mailModel.isMailOpen(),
        isCompact = isCompact,
        isHalfOpen = isHalfOpen
    )

    MotionLayout(
        motionScene = homeMotionScene,
        constraintSetName = currentConstraintSet.tag,
        animationSpec = tween(durationMillis = 400),
        modifier = modifier,
    ) {
        TopToolbar(
            modifier = Modifier.layoutId(layoutId = "toolbar"),
            selectionCountProvider = listState::selectedCount,
            onUnselectAll = listState::unselectAll
        )
        Column(modifier = Modifier.layoutId(layoutId = "list")) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = "Inbox",
                style = MaterialTheme.typography.h6
            )
            MailList(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(weight = 1.0f, fill = true)
                    .fillMaxWidth(),
                listState = listState,
                observableConversations = mailModel.conversations,
                onMailOpen = mailModel::openMail
            )
        }
        MailViewer(
            modifier = Modifier
                .layoutId(layoutId = "viewer")
                .padding(all = 8.dp),
            mailInfoFull = mailModel.openedMail ?: MailInfoFull.Default
        )
        NewMailButton(
            modifier = Modifier.layoutId(layoutId = "newMailButton"),
            state = newMailState
        )
        MailToolbar(
            modifier = Modifier.layoutId(layoutId = "mailToolbar"),
            onCloseMail = mailModel::closeMail
        )
    }
}

/**
 * Resolves the [HomeState] for the home screen based on the screen configuration and app state.
 *
 * The [HomeState] is used by the [MotionLayout] to determine which [ConstraintSet] to apply.
 *
 * If our [Boolean] parameter [isMailOpen] is `true`, we check if our [Boolean] parameter
 * [isCompact] is `true`. If it is, we return [HomeState.MailOpenCompact]. If it is `false`, we
 * check if our [Boolean] parameter [isHalfOpen] is `true`. If it is, we return
 * [HomeState.MailOpenHalf], and if it is not we return [HomeState.MailOpenExpanded]. If [isMailOpen]
 * is `false` we return [HomeState.ListOnly].
 *
 * @param isMailOpen Whether a mail is currently open in the viewer.
 * @param isCompact Whether the screen is of a compact width.
 * @param isHalfOpen Whether the device is a foldable in a half-opened (tabletop) posture.
 * @return The appropriate [HomeState] for the current context.
 */
@Composable
private fun resolveConstraintSet(
    isMailOpen: Boolean,
    isCompact: Boolean,
    isHalfOpen: Boolean
): HomeState {
    if (isMailOpen) {
        if (isCompact) {
            return HomeState.MailOpenCompact
        } else {
            if (isHalfOpen) {
                return HomeState.MailOpenHalf
            } else {
                return HomeState.MailOpenExpanded
            }
        }
    }
    return HomeState.ListOnly
}
