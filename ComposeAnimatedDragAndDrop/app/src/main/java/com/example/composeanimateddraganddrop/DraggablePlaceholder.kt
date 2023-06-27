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

package com.example.composeanimateddraganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex


/**
 * Placeholder that reflects the dragged item from a layout. This should be used alongside the
 * layout in which the Drag And Drop takes place. Think of it as an overlay that shows the dragged
 * item as it's dragged.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our only caller, [FlowDragAndDropExample], calls us with the empty, default, or starter
 * [Modifier] that contains no elements.
 * @param dragHandler that is handling the drag of our Composable.
 * @param content the Composable we should display in our [Box]. This is the [Item] in the
 * `movableItems` list of [Item] in the [FlowDragAndDropExample] Composable whose index is the
 * [LayoutDragHandler.draggedId] of [dragHandler], which is set to the `id` of the [Item] that is
 * determined to be chosen to be dragged in the `onStartDrag` method of [LayoutDragHandler] (or it
 * is -1 if none is being dragged).
 */
@Composable
fun DraggablePlaceholder(
    modifier: Modifier,
    dragHandler: LayoutDragHandler,
    content: @Composable (BoxScope.() -> Unit)
) {
    val draggedModifier = if (dragHandler.draggedId != -1) {
        with(LocalDensity.current) {
            Modifier
                .size(dragHandler.draggedSize.toDpSize())
                .zIndex(1f)
                .graphicsLayer {
                    val offset: Offset = dragHandler.placeholderOffset.value
                    translationX = offset.x
                    translationY = offset.y
                }
        }
    } else {
        Modifier
    }
    Box(
        modifier = modifier.then(draggedModifier),
        content = content
    )
}