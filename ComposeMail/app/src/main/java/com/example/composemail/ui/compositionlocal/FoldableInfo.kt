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

package com.example.composemail.ui.compositionlocal

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/**
 * A [ProvidableCompositionLocal] that holds information about the foldable state of the device,
 * such as whether it's in a half-opened (tabletop) posture. This allows components deep in the
 * composition hierarchy to react to changes in the device's fold state without needing to pass
 * the information down explicitly.
 */
val LocalFoldableInfo: ProvidableCompositionLocal<FoldableInfo> =
    compositionLocalOf { FoldableInfo.Default }

/**
 * Contains information about the foldable state of the device.
 *
 * @property isHalfOpen `true` if the device is in a half-opened state (e.g., tabletop or
 * book posture), `false` otherwise.
 */
data class FoldableInfo(
    val isHalfOpen: Boolean
) {
    companion object {
        /**
         * The default [FoldableInfo] state, representing a device that is not in a half-opened
         * posture (e.g., fully open or a standard non-foldable device).
         */
        val Default: FoldableInfo = FoldableInfo(isHalfOpen = false)
    }
}