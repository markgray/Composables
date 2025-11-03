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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemail.model.ComposeMailModel
import com.example.composemail.model.data.MailInfoFull
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

/**
 * The different home states that are supported by the [MotionLayout] in [ComposeMailHome].
 *
 * Each state is represented by a [ConstraintSet] and has a
 * unique [tag] that is used to identify it.
 *
 * The current state is resolved in [resolveConstraintSet] based on the screen size, fold state,
 * and whether a mail is open.
 */
private enum class HomeState(val tag: String) {
    ListOnly("listOnlyCompactAndExpanded"),
    MailOpenCompact("mailOpenCompact"),
    MailOpenExpanded("mailOpenExpanded"),
    MailOpenHalf("mailOpenHalf")
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
     * Sets the constraints for the `toolbarRef`
     * TODO: CONTINUE HERE.
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

    // Mail toolbar constraints for whenever a Mail is open
    val setVisibleMailToolbarConstraints: ConstraintSetScope.() -> Unit = {
        constrain(ref = mailToolbarRef) {
            end.linkTo(anchor = parent.end, margin = 12.dp)
            bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
        }
    }

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
 * TODO: Add kdoc
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
            modifier = Modifier
                .layoutId(layoutId = "toolbar"),
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
