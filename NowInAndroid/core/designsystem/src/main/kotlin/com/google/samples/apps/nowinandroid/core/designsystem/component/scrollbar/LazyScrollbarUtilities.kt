/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar

import androidx.compose.foundation.gestures.ScrollableState
import kotlin.math.abs

/**
 * Linearly interpolates the index for the first item in [visibleItems] for smooth scrollbar
 * progression.
 *
 * If our [List] of [LazyStateItem] parameter [visibleItems] is empty, we return `0f`. We initialize
 * our [LazyStateItem] variable `firstItem` to the first item in our [List] of [LazyStateItem] parameter
 * [visibleItems]. We initialize our [Int] variable `firstItemIndex` to the index of our [LazyStateItem]
 * variable `firstItem` using our [itemIndex] lambda parameter. If our [Int] variable `firstItemIndex`
 * is less than `0`, we return `Float.NaN`. We initialize our [Int] variable `firstItemSize` to the
 * size of our [LazyStateItem] variable `firstItem` using our [itemSize] lambda parameter. If our
 * [Int] variable `firstItemSize` is `0`, we return `Float.NaN`. We initialize our [Float] variable
 * `itemOffset` to the offset of our [LazyStateItem] variable `firstItem` using our [offset] lambda
 * parameter. We initialize our [Float] variable `offsetPercentage` to the absolute value of our
 * [Float] variable `itemOffset` divided by our [Int] variable `firstItemSize`. We initialize our
 * [LazyStateItem] variable `nextItem` to the next item in our [List] of [LazyStateItem] parameter
 * [visibleItems] using our [nextItemOnMainAxis] lambda parameter, but if that is `null` return our
 * [Int] variable `firstItemIndex` plus our [Float] variable `offsetPercentage`. We initialize our
 * [Int] variable `nextItemIndex` to the index of our [LazyStateItem] variable `nextItem` using our
 * [itemIndex] lambda parameter. We return our [Int] variable `firstItemIndex` plus the difference
 * between our [Int] variable `nextItemIndex` and our [Int] variable `firstItemIndex` times our
 * [Float] variable `offsetPercentage`.
 *
 * @param visibleItems a list of items currently visible in the layout.
 * @param itemSize a lookup function for the size of an item in the layout.
 * @param offset a lookup function for the offset of an item relative to the start of the view port.
 * @param nextItemOnMainAxis a lookup function for the next item on the main axis in the direction
 * of the scroll.
 * @param itemIndex a lookup function for index of an item in the layout relative to
 * the total amount of items available.
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition) where nextItemPosition
 * is the index of the consecutive item along the major axis.
 * */
internal inline fun <LazyState : ScrollableState, LazyStateItem> LazyState.interpolateFirstItemIndex(
    visibleItems: List<LazyStateItem>,
    crossinline itemSize: LazyState.(LazyStateItem) -> Int,
    crossinline offset: LazyState.(LazyStateItem) -> Int,
    crossinline nextItemOnMainAxis: LazyState.(LazyStateItem) -> LazyStateItem?,
    crossinline itemIndex: (LazyStateItem) -> Int,
): Float {
    if (visibleItems.isEmpty()) return 0f

    val firstItem: LazyStateItem = visibleItems.first()
    val firstItemIndex: Int = itemIndex(firstItem)

    if (firstItemIndex < 0) return Float.NaN

    val firstItemSize: Int = itemSize(firstItem)
    if (firstItemSize == 0) return Float.NaN

    val itemOffset: Float = offset(firstItem).toFloat()
    val offsetPercentage: Float = abs(x = itemOffset) / firstItemSize

    val nextItem: LazyStateItem =
        nextItemOnMainAxis(firstItem) ?: return firstItemIndex + offsetPercentage

    val nextItemIndex: Int = itemIndex(nextItem)

    return firstItemIndex + ((nextItemIndex - firstItemIndex) * offsetPercentage)
}

/**
 * Returns the percentage of an item that is currently visible in the view port.
 *
 * If our [Int] parameter [itemSize] is `0`, we return `0f`. We initialize our [Int] variable
 * `itemEnd` to our [Int] parameter [itemStartOffset] plus our [Int] parameter [itemSize]. We
 * initialize our [Int] variable `startOffset` to `0` if our [Int] parameter [viewportStartOffset]
 * is less than our [Int] parameter [itemStartOffset], otherwise we initialize our [Int] variable
 * `startOffset` to the absolute value of our [Int] parameter [viewportStartOffset] minus the
 * absolute value of our [Int] parameter [itemStartOffset]. We initialize our [Int] variable
 * `endOffset` to `0` if our [Int] variable `itemEnd` is less than our [Int] parameter
 * [viewportEndOffset], otherwise we initialize our [Int] variable `endOffset` to the absolute value
 * of our [Int] parameter [viewportEndOffset] minus the absolute value of our [Int] variable
 * `itemEnd`. We initialize our [Float] variable `size` to our [Int] parameter [itemSize]. We
 * return our [Float] variable `size` minus our [Float] variable `startOffset` minus our [Float]
 * variable `endOffset` that quantity divided by our [Float] variable `size`.
 *
 * @param itemSize the size of the item
 * @param itemStartOffset the start offset of the item relative to the view port start
 * @param viewportStartOffset the start offset of the view port
 * @param viewportEndOffset the end offset of the view port
 */
internal fun itemVisibilityPercentage(
    itemSize: Int,
    itemStartOffset: Int,
    viewportStartOffset: Int,
    viewportEndOffset: Int,
): Float {
    if (itemSize == 0) return 0f
    val itemEnd: Int = itemStartOffset + itemSize
    val startOffset: Int = when {
        itemStartOffset > viewportStartOffset -> 0
        else -> abs(n = abs(n = viewportStartOffset) - abs(n = itemStartOffset))
    }
    val endOffset: Int = when {
        itemEnd < viewportEndOffset -> 0
        else -> abs(n = abs(n = itemEnd) - abs(n = viewportEndOffset))
    }
    val size: Float = itemSize.toFloat()
    return (size - startOffset - endOffset) / size
}
