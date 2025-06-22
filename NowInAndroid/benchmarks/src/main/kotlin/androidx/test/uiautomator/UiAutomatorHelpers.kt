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

package androidx.test.uiautomator

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.HasChildrenOp.AT_LEAST
import androidx.test.uiautomator.HasChildrenOp.AT_MOST
import androidx.test.uiautomator.HasChildrenOp.EXACTLY

// These helpers need to be in the androidx.test.uiautomator package,
// because the abstract class has package local method that needs to be implemented.

/**
 * Returns a [Condition] that is satisfied when the element has the specified number of children.
 * Used by the [MacrobenchmarkScope] `forYouWaitForContent` extension function that is defined
 * in the file `ForYouActions.kt` which just uses the default values.
 *
 * @param childCount The number of children to check for. Defaults to 1.
 * @param op The comparison operator to use when checking the child count. Defaults to [AT_LEAST].
 * @return A [Condition] that is satisfied when the element has the specified number of children.
 */
fun untilHasChildren(
    childCount: Int = 1,
    op: HasChildrenOp = AT_LEAST,
): UiObject2Condition<Boolean> = object : UiObject2Condition<Boolean>() {
    override fun apply(element: UiObject2): Boolean = when (op) {
        AT_LEAST -> element.childCount >= childCount
        EXACTLY -> element.childCount == childCount
        AT_MOST -> element.childCount <= childCount
    }
}

/**
 * Enum for specifying the comparison operator to use when checking the child count.
 */
enum class HasChildrenOp {
    AT_LEAST,
    EXACTLY,
    AT_MOST,
}
