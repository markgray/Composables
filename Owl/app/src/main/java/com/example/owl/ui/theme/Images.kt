/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.owl.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Images that can vary by theme.
 */
@Immutable
data class Images(@DrawableRes val lockupLogo: Int)

/**
 * CompositionLocal used to pass [Images] down the tree.
 *
 * Setting the value here is typically done as part of [OwlTheme], which will automatically
 * handle computing the light/dark values.
 */
internal val LocalImages = staticCompositionLocalOf<Images> {
    error("No LocalImages specified")
}
