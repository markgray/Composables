@file:Suppress("MemberVisibilityCanBePrivate")

package android.support.composegraph3d.lib

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.PointerInputChange

/**
 * TODO: Add kdoc
 */
class Graph {
    /**
     * TODO: Add kdoc
     */
    var w: Int = 512
    /**
     * TODO: Add kdoc
     */
    var h: Int = 512
    /**
     * TODO: Add kdoc
     */
    val scale: Int = 2
    /**
     * TODO: Add kdoc
     */
    var downX: Float = 0.0f
    /**
     * TODO: Add kdoc
     */
    var downY: Float = 0.0f
    /**
     * TODO: Add kdoc
     */
    var graphFunctions: FunctionSetup = FunctionSetup(w, h)
    /**
     * TODO: Add kdoc
     */
    var bitmap: ImageBitmap = ImageBitmap(w, h, ImageBitmapConfig.Argb8888)
    /**
     * TODO: Add kdoc
     */
    fun setSize(width: Int, height: Int) {
        if (w == width/scale && h == height/scale) {
            return
        }
        w = width/scale
        h = height/scale
        graphFunctions.setSize(w, h)
        bitmap = ImageBitmap(w, h, ImageBitmapConfig.Argb8888)
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
        downX = down.x/scale
        downY = down.y/scale
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
    @Suppress("UNUSED_PARAMETER")
    fun drag(change: PointerInputChange, drag: Offset) {
        downX += drag.x/scale
        downY += drag.y/scale
        graphFunctions.onMouseDrag(downX, downY)

    }

}