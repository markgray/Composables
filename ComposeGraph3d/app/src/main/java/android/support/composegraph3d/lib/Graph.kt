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
     * TODO: Continue here.
     */
    var graphFunctions: FunctionSetup = FunctionSetup(mWidth = w, mHeight = h)

    /**
     * TODO: Add kdoc
     */
    var bitmap: ImageBitmap = ImageBitmap(w, h, ImageBitmapConfig.Argb8888)

    /**
     * TODO: Add kdoc
     */
    fun setSize(width: Int, height: Int) {
        if (w == width / scale && h == height / scale) {
            return
        }
        w = width / scale
        h = height / scale
        graphFunctions.setSize(w, h)
        bitmap = ImageBitmap(w, h, ImageBitmapConfig.Argb8888)
        @Suppress("ReplacePrintlnWithLogging")
        println("$w x $h")
    }

    /**
     * TODO: Add kdoc
     */
    fun getImageForTime(nanoTime: Long): ImageBitmap {
        val pix = graphFunctions.getImageBuff(nanoTime)
        bitmap.asAndroidBitmap().setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    /**
     * TODO: Add kdoc
     */
    fun dragStart(down: Offset) {
        downX = down.x / scale
        downY = down.y / scale
        graphFunctions.onMouseDown(downX, downY)
    }

    /**
     * TODO: Add kdoc
     */
    fun dragStopped() {
        downX = 0.0f
        downY = 0.0f
    }

    /**
     * TODO: Add kdoc
     */
    @Suppress("UNUSED_PARAMETER") // Suggested change would make method less reusable
    fun drag(change: PointerInputChange, drag: Offset) {
        downX += drag.x / scale
        downY += drag.y / scale
        graphFunctions.onMouseDrag(downX, downY)

    }

}