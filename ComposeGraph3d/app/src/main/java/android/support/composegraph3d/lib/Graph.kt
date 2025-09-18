@file:Suppress("MemberVisibilityCanBePrivate", "RedundantSuppression", "RedundantSuppression")

package android.support.composegraph3d.lib

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.PointerInputChange

/**
 * Manages the state, rendering, and user interaction for a 3D graph.
 *
 * This class encapsulates the logic for creating and displaying a 3D function plot.
 * It handles canvas dimensions, scaling, and processes drag gestures to rotate the graph.
 * It uses a [FunctionSetup] instance to perform the actual rendering calculations
 * and provides an [ImageBitmap] for display in a Composable.
 */
class Graph {
    /**
     * The width of the drawing canvas for the graph, in pixels.
     * This is derived from the screen width and adjusted by the [scale] factor.
     */
    var w: Int = 512

    /**
     * The height of the drawing canvas for the graph, in pixels.
     * This is derived from the screen height and adjusted by the [scale] factor.
     */
    var h: Int = 512

    /**
     * The scaling factor applied to the canvas dimensions. This is used to reduce the
     * resolution of the underlying bitmap for performance reasons, while allowing the
     * display canvas to be larger. For example, a scale of 2 means the internal
     * bitmap for calculations will be half the width and height of the displayed component.
     */
    val scale: Int = 2

    /**
     * The x-coordinate of the last recorded pointer event (e.g., a touch or click),
     * scaled according to the [scale] factor. This is used as a reference point for
     * calculating drag distances to rotate the graph. It is updated on drag start
     * and during drag movements.
     */
    var downX: Float = 0.0f

    /**
     * The y-coordinate of the last recorded pointer event (e.g., a touch or click),
     * scaled according to the [scale] factor. This is used as a reference point for
     * calculating drag distances to rotate the graph. It is updated on drag start
     * and during drag movements.
     */
    var downY: Float = 0.0f

    /**
     * An instance of [FunctionSetup] that manages the core logic for calculating and
     * rendering the 3D function plot.
     *
     * This object is responsible for:
     *  - Storing the mathematical functions to be plotted.
     *  - Calculating the z-values (height) for each (x, y) point on the grid.
     *  - Handling the 3D projection and rotation transformations.
     *  - Generating the pixel buffer (`IntArray`) that represents the rendered image of the graph.
     *
     * It is initialized with the scaled dimensions of the graph canvas.
     */
    var graphFunctions: FunctionSetup = FunctionSetup(mWidth = w, mHeight = h)

    /**
     * An [ImageBitmap] that serves as the canvas for the rendered 3D graph.
     *
     * This bitmap is updated with the latest pixel data from [FunctionSetup] in each frame.
     * It is initialized with the scaled dimensions ([w], [h]) of the graph and is the object
     * that gets ultimately drawn on the screen by a Composable.
     */
    var bitmap: ImageBitmap = ImageBitmap(
        width = w,
        height = h,
        config = ImageBitmapConfig.Argb8888
    )

    /**
     * Updates the dimensions of the graph canvas.
     *
     * This function is called when the size of the composable displaying the graph changes.
     * It recalculates the internal width (`w`) and height (`h`) based on the provided
     * dimensions and the [scale] factor. If the new scaled dimensions are the same as the
     * current ones, the function returns early to avoid unnecessary reallocation.
     *
     * Otherwise, it updates the dimensions of the underlying [FunctionSetup] and recreates
     * the [bitmap] with the new size to match the drawing area.
     *
     * @param width The new width of the component, in pixels.
     * @param height The new height of the component, in pixels.
     */
    fun setSize(width: Int, height: Int) {
        if (w == width / scale && h == height / scale) {
            return
        }
        w = width / scale
        h = height / scale
        graphFunctions.setSize(width = w, height = h)
        bitmap = ImageBitmap(width = w, height = h, config = ImageBitmapConfig.Argb8888)
        @Suppress("ReplacePrintlnWithLogging")
        println("$w x $h")
    }

    /**
     * Generates and returns an `ImageBitmap` of the graph for a specific point in time.
     *
     * This function orchestrates the rendering of a single frame of the graph animation.
     * It retrieves the pixel buffer (an `IntArray`) for the given `nanoTime` from the
     * [graphFunctions] object, which performs the heavy lifting of calculating the 3D projection
     * and shading. The resulting pixel data is then copied into the class's [bitmap] instance.
     *
     * The `nanoTime` parameter is typically provided by a `Choreographer` or a similar
     * animation framework, allowing for smooth, time-based animations (like rotation).
     *
     * @param nanoTime The system time in nanoseconds, used to calculate the current state
     * of any time-dependent animations (e.g., continuous rotation).
     * @return The updated [ImageBitmap] containing the rendered graph for the specified time.
     */
    fun getImageForTime(nanoTime: Long): ImageBitmap {
        val pix: IntArray = graphFunctions.getImageBuff(time = nanoTime)
        bitmap.asAndroidBitmap()
            .setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    /**
     * Initiates a drag gesture for rotating the graph.
     *
     * This function is called when a drag gesture begins (e.g., on a pointer down event).
     * It records the initial `x` and `y` coordinates of the pointer, scaling them down
     * according to the [scale] factor. These coordinates are stored in [downX] and [downY]
     * and serve as the starting point for subsequent drag calculations.
     *
     * It then notifies the underlying [graphFunctions] instance that a drag has started
     * by calling its `onMouseDown` method with the scaled coordinates.
     *
     * @param down The initial position of the pointer event as an [Offset].
     */
    fun dragStart(down: Offset) {
        downX = down.x / scale
        downY = down.y / scale
        graphFunctions.onMouseDown(x = downX, y = downY)
    }

    /**
     * Finalizes a drag gesture.
     *
     * This function is called when a drag gesture ends (e.g., on a pointer up event).
     * It resets the reference drag coordinates, [downX] and [downY], to zero.
     * This ensures that the next drag gesture starts from a clean state.
     */
    fun dragStopped() {
        downX = 0.0f
        downY = 0.0f
    }

    /**
     * Processes a drag gesture to rotate the 3D graph.
     *
     * This function is called continuously during a drag gesture. It updates the
     * cumulative drag coordinates ([downX], [downY]) by adding the latest drag
     * amount, scaled by the [scale] factor. These updated coordinates are then
     * passed to the [graphFunctions] object's `onMouseDrag` method, which
     * recalculates the graph's rotation angles based on the total drag distance
     * from the start of the gesture.
     *
     * The `change` parameter is currently unused but is included to maintain a
     * consistent signature with common pointer input handlers, making the function
     * more adaptable for future enhancements (e.g., handling multi-touch or
     * pressure).
     *
     * @param change The detailed information about the pointer event that caused this drag update.
     * @param drag The amount of distance the pointer has moved since the previous drag event, as an [Offset].
     */
    @Suppress("UNUSED_PARAMETER") // Suggested change would make method less reusable
    fun drag(change: PointerInputChange, drag: Offset) {
        downX += drag.x / scale
        downY += drag.y / scale
        graphFunctions.onMouseDrag(x = downX, y = downY)

    }

}