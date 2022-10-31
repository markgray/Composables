/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.reply.ui.utils

import android.graphics.Rect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.example.reply.data.Email
import com.example.reply.ui.ReplyBottomNavigationBar
import com.example.reply.ui.ReplyListAndDetailContent
import com.example.reply.ui.ReplyListOnlyContent
import com.example.reply.ui.ReplyNavigationRail
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Information about the posture of the device
 */
sealed interface DevicePosture {
    /**
     * The default [DevicePosture] when both [isBookPosture] and [isSeparating] return `false` for
     * the [FoldingFeature] that is found in the [WindowLayoutInfo] that the [WindowInfoTracker]
     * emits for the device we are running on (ie. it a normal device without a fold or the ability
     * to be separated).
     */
    object NormalPosture : DevicePosture

    /**
     * To be this type of [DevicePosture] the [FoldingFeature] that is found in the [WindowLayoutInfo]
     * that the [WindowInfoTracker] emits for the device we are running on has the [FoldingFeature.state]
     * property [FoldingFeature.State.HALF_OPENED] and the [FoldingFeature.orientation] property
     * [FoldingFeature.Orientation.VERTICAL] (The foldable device's hinge is in an intermediate
     * position between opened and closed state, there is a non-flat angle between parts of the
     * flexible screen or between physical screen panels, and the height of the [FoldingFeature] is
     * greater than or equal to the width).
     */
    data class BookPosture(
        /**
         * This is the [FoldingFeature.bounds] property for this device, the bounding rectangle of
         * the feature within the application window in the window coordinate space.
         */
        val hingePosition: Rect
    ) : DevicePosture

    /**
     * To be this type of [DevicePosture] the [FoldingFeature] that is found in the [WindowLayoutInfo]
     * that the [WindowInfoTracker] emits for the device we are running on has the [FoldingFeature.state]
     * [FoldingFeature.State.FLAT] and [FoldingFeature.isSeparating] is `true` (the foldable device
     * is completely open, the screen space that is presented to the user is flat, and the [FoldingFeature]
     * should be thought of as splitting the window into multiple physical areas that can be seen by
     * users as logically separate).
     */
    data class Separating(
        /**
         * This is the [FoldingFeature.bounds] property for this device, the bounding rectangle of
         * the feature within the application window in the window coordinate space.
         */
        val hingePosition: Rect,
        /**
         * This is the [FoldingFeature.orientation] property for this device,
         * [FoldingFeature.Orientation.HORIZONTAL] if the width is greater than the height,
         * [FoldingFeature.Orientation.VERTICAL] otherwise.
         */
        var orientation: FoldingFeature.Orientation
    ) : DevicePosture
}

/**
 * Returns `true` if its [FoldingFeature] parameter [foldFeature] indicates that the [DevicePosture]
 * of the device is [DevicePosture.BookPosture]. This is so if the [FoldingFeature.state] property
 * of [foldFeature] is [FoldingFeature.State.HALF_OPENED] (The foldable device's hinge is in an
 * intermediate position between opened and closed state, there is a non-flat angle between parts of
 * the flexible screen or between physical screen panels) and its [FoldingFeature.orientation] property
 * is [FoldingFeature.Orientation.VERTICAL] (the height of the [FoldingFeature] is greater than or
 * equal to the width).
 */
@OptIn(ExperimentalContracts::class)
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
        foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

/**
 * Returns `true` if its [FoldingFeature] parameter [foldFeature] indicates that the [DevicePosture]
 * of the device is [DevicePosture.Separating]. This is so if the [FoldingFeature.state] property
 * of [foldFeature] is [FoldingFeature.State.FLAT] (the foldable device is completely open, the screen
 * space that is presented to the user is flat) and its [FoldingFeature.isSeparating] property is
 * `true` (the [FoldingFeature] should be thought of as splitting the window into multiple physical
 * areas that can be seen by users as logically separate).
 */
@OptIn(ExperimentalContracts::class)
fun isSeparating(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}

/**
 * Different type of navigation supported by app depending on size and state.
 */
enum class ReplyNavigationType {
    /**
     * This type of navigation uses [ReplyBottomNavigationBar] which uses the Material Design bottom
     * navigation bar [NavigationBar] to navigate between screens (which are yet to be written, so the
     * `onClick` of the [NavigationBarItem]'s do nothing yet). This type of navigation is chosen if
     * [WindowWidthSizeClass] of the device is [WindowWidthSizeClass.Compact] (Represents the majority
     * of phones in portrait) or is not a known [WindowWidthSizeClass].
     */
    BOTTOM_NAVIGATION,

    /**
     * This type of navigation uses [ReplyNavigationRail] which uses the Material Design bottom
     * navigation rail [NavigationRail] to navigate between screens (which are yet to be written, so the
     * `onClick` of the [NavigationRailItem]'s do nothing yet). This type of navigation is chosen if
     * [WindowWidthSizeClass] of the device is [WindowWidthSizeClass.Medium] (Represents the majority
     * of tablets in portrait and large unfolded inner displays in portrait) or [WindowWidthSizeClass.Expanded]
     * (Represents the majority of tablets in landscape and large unfolded inner displays in landscape)
     * and the [DevicePosture] of the device is [DevicePosture.BookPosture] (The foldable device's hinge
     * is in an intermediate position between opened and closed state, there is a non-flat angle between
     * parts of the flexible screen or between physical screen panels, and the height of its [FoldingFeature]
     * is greater than or equal to the width).
     */
    NAVIGATION_RAIL,

    /**
     * This type of navigation uses [PermanentNavigationDrawer] (the Material Design navigation
     * permanent drawer, which is always visible and usually used for frequently switching
     * destinations). This type of navigation is chosen if [WindowWidthSizeClass] of the device is
     * [WindowWidthSizeClass.Expanded] (Represents the majority of tablets in landscape and large
     * unfolded inner displays in landscape) but the [DevicePosture] of the device is NOT
     * [DevicePosture.BookPosture].
     */
    PERMANENT_NAVIGATION_DRAWER
}

/**
 * Content shown depending on size and state of device.
 */
enum class ReplyContentType {
    /**
     * This content type uses [ReplyListOnlyContent] to display [Email] objects with only 2 lines
     * of the [Email.body] of the [Email]. It is chosen if the [WindowWidthSizeClass] of the device
     * is [WindowWidthSizeClass.Compact] (Represents the majority of phones in portrait) or
     * [WindowWidthSizeClass.Medium] (Represents the majority of tablets in portrait and large
     * unfolded inner displays in portrait) and the [DevicePosture] is neither [DevicePosture.BookPosture]
     * nor [DevicePosture.Separating], or is not a known [WindowWidthSizeClass].
     */
    LIST_ONLY,

    /**
     * This content type uses [ReplyListAndDetailContent] to display [Email] objects with only 2 lines
     * of the [Email.body] of the [Email], along with a separate [LazyColumn] which displays all the
     * [Email.threads] associated with the selected [Email]. It is chosen if the [WindowWidthSizeClass]
     * of the device is [WindowWidthSizeClass.Medium] and the [DevicePosture] of the device is either
     * [DevicePosture.BookPosture] or [DevicePosture.Separating], or the [WindowWidthSizeClass] of
     * the device is [WindowWidthSizeClass.Expanded].
     */
    LIST_AND_DETAIL
}