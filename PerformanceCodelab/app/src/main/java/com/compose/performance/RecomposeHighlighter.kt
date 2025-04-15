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
package com.compose.performance

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.dp
import java.util.Objects
import kotlin.math.min
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A [Modifier] that draws a border around elements that are recomposing. The border increases in
 * size and interpolates from red to green as more recompositions occur before a timeout.
 */
@Stable
fun Modifier.recomposeHighlighter(): Modifier = this.then(RecomposeHighlighterElement())

/**
 * A [ModifierNodeElement] that represents a recompose highlighter.
 *
 * This element is designed to trigger visual highlighting of a composable
 * during recomposition. It achieves this by ensuring that the `update` function
 * is called on every recomposition, thereby triggering the highlighting logic within
 * the [RecomposeHighlighterModifier].
 *
 * This is primarily used for debugging purposes to visualize recompositions
 * and understand how often a composable is being recomposed.
 *
 * **Key Features:**
 *
 * - **Triggers on Every Recomposition:** The `equals` method always returns `false`.
 *   This ensures that the `update` function in [RecomposeHighlighterModifier] is
 *   called on every recomposition, even if the element's properties have not
 *   conceptually changed.
 * - **Debug Inspector Information:**  Provides the name "recomposeHighlighter" in the
 *   inspector for easy identification in debugging tools.
 * - **Increments Composition Count:** The `update` function increments an internal counter
 *   in the [RecomposeHighlighterModifier] every time it's called. This is directly
 *   used by the recomposition highlighting logic to determine the color intensity.
 * - **Stateless:** This Element is stateless as it doesn't hold any data.
 *
 * **Usage:**
 *
 * This class should not be used directly. Instead, use the
 * `Modifier.recomposeHighlighter()` extension function which creates and adds
 * this element to the Modifier chain.
 *
 * ```kotlin
 * Box(Modifier.recomposeHighlighter()) {
 *     // Content that will be highlighted on recomposition
 * }
 * ```
 *
 * **How it Works:**
 *
 * 1. When `Modifier.recomposeHighlighter()` is called, an instance of
 *    [RecomposeHighlighterElement] is added to the Modifier chain.
 * 2. During recomposition, the Compose runtime checks if the Modifier element
 *    is considered "changed" compared to the previous composition.
 */
private class RecomposeHighlighterElement : ModifierNodeElement<RecomposeHighlighterModifier>() {

    /**
     * Adds debug inspection information for the `recomposeHighlighter` modifier.
     *
     * This function is called by the Compose Inspector to provide information about the
     * properties of this modifier. It's used to display details about the modifier in
     * tools like the Layout Inspector.
     *
     * In this specific case, it adds a single property:
     *
     * - **name**:  Set to "recomposeHighlighter". This indicates the type or purpose
     *   of this modifier in the inspector UI.
     *
     * This metadata helps developers understand what a given composable modifier does when
     * inspecting their UI tree.
     *
     * This function is intended to be used within the `InspectorInfo.inspectableProperties`
     * lambda of a custom modifier.
     *
     * Example usage within a custom modifier:
     * ```kotlin
     * fun Modifier.myCustomModifier() = composed(
     *     inspectorInfo = debugInspectorInfo {
     *        name = "myCustomModifier"
     *        properties["someProperty"] = "someValue"
     *     }
     * ) {
     *    //Modifier logic
     * }
     * ```
     *
     * Note: This function should only be called within the context of `InspectorInfo.inspectableProperties`.
     */
    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo { name = "recomposeHighlighter" }
    }

    /**
     * Creates a new [RecomposeHighlighterModifier] instance.
     *
     * This function is responsible for instantiating the modifier that visually highlights
     * recompositions in the UI.  It returns a default instance of [RecomposeHighlighterModifier]
     * with no custom configuration.
     *
     * @return A new [RecomposeHighlighterModifier] instance.
     */
    override fun create(): RecomposeHighlighterModifier = RecomposeHighlighterModifier()

    override fun update(node: RecomposeHighlighterModifier) {
        node.incrementCompositions()
    }

    // It's never equal, so that every recomposition triggers the update function.
    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int = Objects.hash(this)
}

private class RecomposeHighlighterModifier : Modifier.Node(), DrawModifierNode {

    private var timerJob: Job? = null

    /**
     * The total number of compositions that have occurred.
     */
    private var totalCompositions: Long = 0
        set(value) {
            if (field == value) return
            restartTimer()
            field = value
            invalidateDraw()
        }

    fun incrementCompositions() {
        totalCompositions++
    }

    override fun onAttach() {
        super.onAttach()
        restartTimer()
    }

    override val shouldAutoInvalidate: Boolean = false

    override fun onDetach() {
        timerJob?.cancel()
    }

    /**
     * Start the timeout, and reset everytime there's a recomposition.
     */
    private fun restartTimer() {
        if (!isAttached) return

        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            delay(3000)
            totalCompositions = 0
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        // Draw actual content.
        drawContent()

        // Below is to draw the highlight, if necessary. A lot of the logic is copied from Modifier.border

        val hasValidBorderParams = size.minDimension > 0f
        if (!hasValidBorderParams || totalCompositions <= 0) {
            return
        }

        val (color, strokeWidthPx) =
            when (totalCompositions) {
                // We need at least one composition to draw, so draw the smallest border
                // color in blue.
                1L -> Color.Blue to 1f
                // 2 compositions is _probably_ okay.
                2L -> Color.Green to 2.dp.toPx()
                // 3 or more compositions before timeout may indicate an issue. lerp the
                // color from yellow to red, and continually increase the border size.
                else -> {
                    lerp(
                        Color.Yellow.copy(alpha = 0.8f),
                        Color.Red.copy(alpha = 0.5f),
                        min(1f, (totalCompositions - 1).toFloat() / 100f)
                    ) to totalCompositions.toInt().dp.toPx()
                }
            }

        val halfStroke = strokeWidthPx / 2
        val topLeft = Offset(halfStroke, halfStroke)
        val borderSize = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)

        val fillArea = (strokeWidthPx * 2) > size.minDimension
        val rectTopLeft = if (fillArea) Offset.Zero else topLeft
        val size = if (fillArea) size else borderSize
        val style = if (fillArea) Fill else Stroke(strokeWidthPx)

        drawRect(
            brush = SolidColor(color),
            topLeft = rectTopLeft,
            size = size,
            style = style
        )
    }
}
