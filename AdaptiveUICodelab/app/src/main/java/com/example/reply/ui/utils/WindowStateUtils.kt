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
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
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
 *
 */
@OptIn(ExperimentalContracts::class)
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
        foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

/**
 *
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
     *
     */
    BOTTOM_NAVIGATION,

    /**
     *
     */
    NAVIGATION_RAIL,

    /**
     *
     */
    PERMANENT_NAVIGATION_DRAWER
}

/**
 * Content shown depending on size and state of device.
 */
enum class ReplyContentType {
    /**
     *
     */
    LIST_ONLY,

    /**
     *
     */
    LIST_AND_DETAIL
}