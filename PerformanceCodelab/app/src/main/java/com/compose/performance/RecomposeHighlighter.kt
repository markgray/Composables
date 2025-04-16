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
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
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

    /**
     * Updates the [RecomposeHighlighterModifier] with the latest state.
     *
     * This function is called when the state of the node this modifier is attached to
     * has changed, and the modifier needs to be updated to reflect those changes.
     * In this specific implementation, it increments the composition count of the node,
     * effectively marking that a recomposition has occurred for this part of the UI.
     *
     * @param node The [RecomposeHighlighterModifier] to be updated. This object holds the state
     * related to recomposition highlighting, such as the number of times the composable has been
     * recomposed.
     */
    override fun update(node: RecomposeHighlighterModifier) {
        node.incrementCompositions()
    }
  
    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * This implementation always returns `false`, meaning no other object is considered equal to
     * this instance. This is so that every recomposition triggers the update function.
     *
     * @param other the reference object with which to compare.
     * @return `true` if this object is the same as the `other` argument; `false` otherwise. We
     * always return `false` so that every recomposition triggers the update function.
     */
    override fun equals(other: Any?): Boolean = false

    /**
     * Returns a hash code value for the object. This implementation delegates to [Objects.hash],
     * providing the object itself as the input.
     *
     * The hash code is calculated based on the object's identity (memory address) since it's the
     * sole argument provided to [Objects.hash]. This is equivalent to calling
     * [System.identityHashCode].
     *
     * It is important to ensure consistency: if two objects are equal according to the [equals]
     * method, they must return the same hash code. While this implementation provides a hash code,
     * if you override equals, you should override hashCode in a way that is consistent with it,
     * probably including the fields used in equals within the calculation. Otherwise, collections
     * like HashMap or HashSet may not work correctly.
     *
     * @return a hash code value for this object.
     *
     * @see Objects.hash
     * @see System.identityHashCode
     * @see equals
     */
    override fun hashCode(): Int = Objects.hash(this)
}

/**
 * A Modifier that highlights recompositions by drawing a colored border around the composable.
 *
 * This modifier is intended for debugging and performance analysis purposes. It visually indicates
 * when a composable is recomposed by drawing a colored border. The color and thickness of the
 * border change based on the number of recompositions within a timeout period (3 seconds).
 *
 * **Functionality:**
 * - Tracks the number of recompositions.
 * - Resets the count after a 3-second timeout.
 * - Draws a colored border based on the recomposition count:
 *     - Blue (1 recomposition)
 *     - Green (2 recompositions)
 *     - Yellow to Red (3+ recompositions, with color intensity increasing with count)
 * - The border width increases with each recomposition after the second one.
 * - If the recomposition border width is greater than the size of the element, it will fill
 * the element.
 *
 * **Usage:**
 * Apply this modifier to a composable to visualize its recomposition behavior.
 *
 * ```kotlin
 * @Composable
 * fun MyComposable() {
 *     Column(Modifier.recomposeHighlighter()) {
 *         // ... your composable content ...
 *     }
 * }
 * ```
 *
 * **Limitations:**
 * - This modifier is primarily for debugging and should not be used in production code.
 */
private class RecomposeHighlighterModifier : Modifier.Node(), DrawModifierNode {

    /**
     * A [Job] representing the currently running timer.
     *
     * This job is used to manage the lifecycle of a recurring task or a countdown timer.
     * It can be used to start, pause, resume, or cancel the timer.
     *
     * When the timer is started, a new [Job] is created and assigned to this property.
     * If a timer is already running, the existing [Job] will be cancelled before
     * creating a new one.
     *
     * When the timer is stopped or completed, this property should be set to `null`
     * to indicate that no timer is currently active.
     *
     * This property should be accessed and modified from the coroutine context.
     */
    private var timerJob: Job? = null

    /**
     * The total number of compositions that have occurred.
     *
     * This property tracks how many times the content of the component has been recomposed.
     * A recomposition happens when the state of the component changes, requiring the UI to
     * be redrawn.
     *
     * Each time this value is updated, it will trigger a restart of the internal timer
     * and invalidate the drawing, forcing a redraw of the component.
     *
     * The property will not update if the new value is equal to the existing value.
     *
     *  This can be useful for debugging and performance analysis to identify parts of the
     * UI that are recomposing more frequently than expected.
     */
    private var totalCompositions: Long = 0
        set(value) {
            if (field == value) return
            restartTimer()
            field = value
            invalidateDraw()
        }

    /**
     * Increments the total number of compositions.
     *
     * This function increases the value of the [totalCompositions] counter by 1.
     * It is used to track the number of times a composition-related operation or
     * event has occurred.
     *
     * @see totalCompositions
     */
    fun incrementCompositions() {
        totalCompositions++
    }

    /**
     * Called when the node is attached to a Layout which is part of the UI tree.
     *
     * When called, node is guaranteed to be non-null. You can call sideEffect, coroutineScope, etc.
     * This is not guaranteed to get called at a time where the rest of the Modifier Nodes in the
     * hierarchy are "up to date". For instance, at the time of calling onAttach for this node,
     * another node may be in the tree that will be detached by the time Compose has finished
     * applying changes. As a result, if you need to guarantee that the state of the tree is
     * "final" for this round of changes, you should use the sideEffect API to schedule the
     * calculation to be done at that time.
     *
     * First we call our super's implementation of `onAttach`. Then we call [restartTimer] to
     * restart the timeout that resets the [totalCompositions] counter.
     *
     * @see Modifier.Node.onAttach
     * @see restartTimer
     */
    override fun onAttach() {
        super.onAttach()
        restartTimer()
    }

    /**
     * Indicates whether the component should automatically invalidate its layout and redraw itself
     * when its properties change.
     *
     * When set to `true` (the default), the component will automatically trigger a redraw whenever
     * a property that affects its appearance or layout is modified. This is convenient for dynamic
     * updates but can be less efficient if many changes are made in quick succession.
     *
     * When set to `false` the component will not automatically redraw. The user is responsible for
     * calling an appropriate method (e.g., `invalidate`) to trigger a redraw when necessary. This
     * provides more control and can improve performance when many changes are made before a redraw
     * is needed.
     *
     * By default, this is set to `true`, we set it to `false` so that we don't automatically
     * redraw when the properties change.
     */
    override val shouldAutoInvalidate: Boolean = false

    /**
     * Called when the node is attached to a Layout which is not a part of the UI tree anymore.
     * Note that the node can be reattached again. This should be called right before the node
     * gets removed from the list, so you should still be able to traverse inside of this method.
     * Ideally we would not allow you to trigger side effects here.
     *
     * We just cancel the [timerJob] of the timer that resets the [totalCompositions] counter.
     */
    override fun onDetach() {
        timerJob?.cancel()
    }

    /*
     * Start the timeout, and reset everytime there's a recomposition.
     */
    
    /**
     * Restarts the timer responsible for resetting the composition count and invalidating the draw.
     *
     * This function performs the following actions:
     * 1. **Early Exit:** If the component is not attached ([isAttached] is false), it immediately
     * returns, doing nothing.
     * 2. **Cancel Existing Timer:** If a timer is already running ([timerJob] is not null), it
     * cancels the existing timer job to prevent multiple timers from running concurrently.
     * 3. **Start New Timer:** It launches a new coroutine using the [coroutineScope] provided by
     * [Modifier] to launch a timer coroutine which:
     *     - **Delay:** The timer waits for 3000 milliseconds (3 seconds) before executing
     *     its actions.
     *     - **Reset Composition Count:** After the delay, it resets the `totalCompositions`
     *     variable to 0.
     *     - **Invalidate Draw:** It calls `invalidateDraw()` to request a redraw of the component,
     *     reflecting the changes made to the composition count.
     *
     * This timer is used to automatically reset the composition count after a period of inactivity
     * and trigger a redraw of the component.
     *
     * The [coroutineScope] used here has a lifecycle that matches the [Modifier], ensuring that
     * the timer is canceled when the component is destroyed.
     *
     * @see [isAttached] Checks if the component is currently attached.
     * @see [timerJob] The coroutine job representing the currently running timer.
     * @see [coroutineScope] The coroutine scope used to launch the timer coroutine.
     * @see [totalCompositions] The variable that is reset by this timer.
     * @see [invalidateDraw] The function that is called to trigger a redraw.
     */
    private fun restartTimer() {
        if (!isAttached) return

        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            delay(timeMillis = 3000)
            totalCompositions = 0
            invalidateDraw()
        }
    }

    /**
     * This is part of the [DrawModifierNode] interface, which is a [Modifier.Node] that draws into
     * the space of the layout that the modifier is applied to. And this is the callback that is
     * reponsible for drawing for the modifier. First we call [ContentDrawScope.drawContent] to draw
     * the actual content of the layout.
     *
     * Next we initialize our [Boolean] variable `val hasValidBorderParams` to `true` if the
     * [Size.minDimension] of the layout is greater than 0, otherwise set it to `false`. Then
     * if `hasValidBorderParams` is `false` or `totalCompositions` is less than or equal to 0
     * we return immediately.
     *
     * Otherwise we initialize our [Color] variable `val color` to a color based on the value
     * of [totalCompositions] and our [Float] variable `val strokeWidthPx` to a pixel value based
     * on the value of [totalCompositions] using a [Pair]:
     *  - 1L -> [Color.Blue] to a [Float] pixel value of 1f.
     *  - 2L -> [Color.Green] to a [Float] pixel value of 2dp.
     *  - else -> [Color.Yellow] to [Color.Red] based on a linear interpolation between the minimum
     *  of 1f and [totalCompositions] minus 1 divided by 100f to a [Float] pixel value of
     *  [totalCompositions] converted to a [Dp] then converted to its [Float] pixel value.
     *
     * Next we initialize our [Float] variable `val halfStroke` to half the value of `strokeWidthPx`,
     * initialize our [Offset] variable `val topLeft` to `Offset(halfStroke, halfStroke)`,
     * initialize our [Size] variable `val borderSize` to a [Size] whose [Size.width] is equal to
     * the [Size.width] of the layout minus `strokeWidthPx` and whose [Size.height] is equal to
     * the [Size.height] of the layout minus `strokeWidthPx`, initialize our [Boolean] variable
     * `val fillArea` to `true` if `strokeWidthPx` times 2 is greater than the [Size.minDimension]
     * of the layout, otherwise setting it to `false`, initialize our [Offset] variable `val rectTopLeft`
     * to `Offset.Zero` if `fillArea` is `true`, otherwise we set it to `topLeft`, initialize
     * our [Size] variable `val size` to [ContentDrawScope.size] if `fillArea` is `true`, otherwise
     * we set `borderSize`, initialize our [DrawStyle] variable `val style` to [Fill] if `fillArea`
     * is `true`, otherwise we set it to [Stroke] with `strokeWidthPx` as the [Stroke.width].
     *
     * Finally we call [ContentDrawScope.drawRect] with the following arguments:
     *  - `brush = SolidColor(value = color)`: The color to fill the rectangle with.
     *  - `topLeft = rectTopLeft`: The top-left corner of the rectangle.
     *  - `size = size`: The size of the rectangle.
     *  - `style = style`: The style of the rectangle.
     */
    override fun ContentDrawScope.draw() {
        // Draw actual content.
        drawContent()

        // Below is to draw the highlight, if necessary. A lot of the logic is copied from Modifier.border

        val hasValidBorderParams: Boolean = size.minDimension > 0f
        if (!hasValidBorderParams || totalCompositions <= 0) {
            return
        }

        val (color: Color, strokeWidthPx: Float) =
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
                        start = Color.Yellow.copy(alpha = 0.8f),
                        stop = Color.Red.copy(alpha = 0.5f),
                        fraction = min(1f, (totalCompositions - 1).toFloat() / 100f)
                    ) to totalCompositions.toInt().dp.toPx()
                }
            }

        val halfStroke: Float = strokeWidthPx / 2
        val topLeft = Offset(halfStroke, halfStroke)
        val borderSize = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)

        val fillArea: Boolean = (strokeWidthPx * 2) > size.minDimension
        val rectTopLeft: Offset = if (fillArea) Offset.Zero else topLeft
        val size: Size = if (fillArea) size else borderSize
        val style: DrawStyle = if (fillArea) Fill else Stroke(strokeWidthPx)

        drawRect(
            brush = SolidColor(value = color),
            topLeft = rectTopLeft,
            size = size,
            style = style
        )
    }
}
