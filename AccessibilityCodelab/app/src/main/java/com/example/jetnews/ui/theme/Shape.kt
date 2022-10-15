/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

/**
 * The [Shapes] used by the [JetnewsTheme] custom [MaterialTheme]. The `small` [RoundedCornerShape]
 * of 4.dp is used by Small components, the `medium` [RoundedCornerShape] of 4.dp is used by medium
 * components, and the `large` [RoundedCornerShape] of 8.dp is used by large components
 */
val JetnewsShapes: Shapes = Shapes(
    small = RoundedCornerShape(size = 4.dp),
    medium = RoundedCornerShape(size = 4.dp),
    large = RoundedCornerShape(size = 8.dp)
)
