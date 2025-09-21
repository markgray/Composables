/*
 * Copyright (C) 2020 The Android Open Source Project
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
@file:Suppress(
    "UNUSED_PARAMETER",
    "ReplaceNotNullAssertionWithElvisReturn",
    "MemberVisibilityCanBePrivate"
)

package android.support.composegraph3d.lib

import java.util.Arrays
import kotlin.math.sqrt

/**
 * This renders 3Dimensional Objects.
 */
class Scene3D {
    /**
     * The matrix used to transform the scene from world space to screen space.
     */
    var mMatrix: ViewMatrix = ViewMatrix()

    /**
     * The inverse of the matrix used to transform the scene. This is used to transform the scene
     * from world space to camera space.
     */
    var mInverse: Matrix? = Matrix()

    /**
     * The main 3D object to be rendered.
     */
    var mObject3D: Object3D? = null

    /**
     * A list of objects that are rendered before the main object.
     */
    var mPreObjects: ArrayList<Object3D> = arrayListOf()

    /**
     * An ArrayList that holds the 3D objects that are rendered after the
     * main object [mObject3D] has been rendered.
     */
    var mPostObjects: ArrayList<Object3D> = ArrayList()

    /**
     * The z-buffer is used to store the depth of each pixel in the scene.
     * This is used to determine which objects are in front of other objects.
     */
    var zBuff: FloatArray? = null

    /**
     * The array of integers representing the image data.
     * Each integer in the array corresponds to the color of a pixel in the image.
     */
    lateinit var img: IntArray

    /**
     * The direction of the light source. It's an array of three floats representing the x, y,
     * and z components of the light direction vector.
     */
    private val light = floatArrayOf(0f, 0f, 1f) // The direction of the light source

    /**
     * The direction of the light source, transformed by the current camera view.
     * This is an array of three floats representing the x, y, and z components of the
     * transformed light direction vector. This is used in lighting calculations.
     */
    @JvmField
    var mTransformedLight: FloatArray =
        floatArrayOf(0f, 1f, 1f) // The direction of the light source

    /**
     * Flag indicating whether the light source should move with the camera.
     *  - If `true`, the light's direction will be transformed by the camera's view matrix.
     *  - If `false`, the light's direction remains fixed in world space.
     */
    var mLightMovesWithCamera: Boolean = false

    /**
     * The width of the scene, in pixels.
     */
    var width: Int = 0

    /**
     * The height of the scene, in pixels.
     */
    var height: Int = 0

    /**
     * A temporary [FloatArray] used for vector calculations. It has a size of 3 to store x, y,
     * and z components of a vector.
     */
    @JvmField
    var tmpVec: FloatArray = FloatArray(size = 3)

    /**
     * The color used for drawing lines in the scene.
     * This is an integer value representing an ARGB color.
     * The default value is -0x1000000 (opaque black).
     */
    var lineColor: Int = -0x1000000

    /**
     * A small epsilon value used to prevent division by zero or other floating-point inaccuracies
     * when performing calculations along the x-axis.
     */
    private val epslonX = 0.000005232f

    /**
     * A small epsilon value used to prevent division by zero or other floating-point inaccuracies
     * when performing calculations along the y-axis.
     */
    private val epslonY = 0.00000898f

    /**
     * An unused field of type [Function].
     */
    private val mFunction: Function? = null

    /**
     * The zoom level of the scene.
     * This value is used to calculate the size of the 3D object in the scene.
     * A higher zoom value will make the object appear larger, while a lower value will
     * make it appear smaller.
     * The default value is 1f.
     */
    var zoom: Float = 1f

    /**
     * The background color of the scene.
     * This is an integer value representing an ARGB color.
     * The default value is 0 (transparent).
     */
    var background: Int = 0

    /**
     * Represents a 3D box defined by its vertices and edges.
     * This class is used to draw the bounding box of a 3D object.
     */
    internal inner class Box {
        /**
         * A 2D array of floats representing the coordinates of the box.
         * The first dimension represents the two corners of the box (min and max).
         * The second dimension represents the x, y, and z coordinates of each corner.
         * For example, `mBox[0][0]` would be the x-coordinate of the minimum corner,
         * and `mBox[1][2]` would be the z-coordinate of the maximum corner.
         */
        var mBox = arrayOf(floatArrayOf(1f, 1f, 1f), floatArrayOf(2f, 3f, 2f))

        /**
         * An array of integers that defines the x-coordinates of the starting points
         * of the edges of a 3D box. Each element in this array corresponds to an
         * edge of the box, and its value is an index into the [mBox] array,
         * indicating which x-coordinate to use for the starting point of that edge.
         */
        var mX1 = intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0)

        /**
         * An array of integers that defines the y-coordinates of the starting points
         * of the edges of a 3D box. Each element in this array corresponds to an
         * edge of the box, and its value is an index into the [mBox] array,
         * indicating which y-coordinate to use for the starting point of that edge.
         */
        var mY1 = intArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1)

        /**
         * An array of integers that defines the z-coordinates of the starting points
         * of the edges of a 3D box. Each element in this array corresponds to an
         * edge of the box, and its value is an index into the [mBox] array,
         * indicating which z-coordinate to use for the starting point of that edge.
         */
        var mZ1 = intArrayOf(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1)

        /**
         * An array of integers that defines the x-coordinates of the ending points
         * of the edges of a 3D box. Each element in this array corresponds to an
         * edge of the box, and its value is an index into the [mBox] array,
         * indicating which x-coordinate to use for the ending point of that edge.
         */
        var mX2 = intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1)

        /**
         * An array of integers that defines the y-coordinates of the ending points
         * of the edges of a 3D box. Each element in this array corresponds to an
         * edge of the box, and its value is an index into the [mBox] array,
         * indicating which y-coordinate to use for the ending point of that edge.
         */
        var mY2 = intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1)

        /**
         * An array of integers that defines the z-coordinates of the ending points
         * of the edges of a 3D box. Each element in this array corresponds to an
         * edge of the box, and its value is an index into the [mBox] array,
         * indicating which z-coordinate to use for the ending point of that edge.
         */
        var mZ2 = intArrayOf(1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1)

        /**
         * A temporary [FloatArray] of size 3 used to store the 3D coordinates (x, y, z)
         * of the first point of an edge when drawing the bounding box. This point is
         * derived from the [mBox] array using indices from [mX1], [mY1], and [mZ1].
         */
        var mPoint1 = FloatArray(size = 3)

        /**
         * A temporary [FloatArray] of size 3 used to store the 3D coordinates (x, y, z)
         * of the second point of an edge when drawing the bounding box. This point is
         * derived from the [mBox] array using indices from [mX2], [mY2], and [mZ2].
         */
        var mPoint2 = FloatArray(size = 3)

        /**
         * A temporary [FloatArray] of size 3 used to store the screen-space coordinates
         * (x, y, z) of the first point of an edge after transformation by the inverse
         * view matrix [mInverse]. This is used for drawing the bounding box.
         */
        var mDraw1 = FloatArray(size = 3)

        /**
         * A temporary [FloatArray] of size 3 used to store the screen-space coordinates (x, y, z)
         * of the second transformed point of an edge when drawing the bounding box.
         * This point is the result of transforming [mPoint2] by the inverse view matrix [mInverse].
         */
        var mDraw2 = FloatArray(size = 3)

        /**
         * Draws the lines of the bounding box.
         * This function iterates through the 12 edges of the box, defined by
         * [mX1], [mY1], [mZ1] and [mX2], [mY2], [mZ2].
         * For each edge, it retrieves the 3D coordinates of the start and end points
         * from [mBox], transforms these points using the [mInverse] matrix,
         * and then calls the [LineRender.draw] method to render the line in 2D.
         *
         * @param r The [LineRender] interface used to draw the lines.
         */
        fun drawLines(r: LineRender) {
            for (i in 0..11) {
                mPoint1[0] = mBox[mX1[i]][0]
                mPoint1[1] = mBox[mY1[i]][1]
                mPoint1[2] = mBox[mZ1[i]][2]
                mPoint2[0] = mBox[mX2[i]][0]
                mPoint2[1] = mBox[mY2[i]][1]
                mPoint2[2] = mBox[mZ2[i]][2]
                mInverse!!.mult3(src = mPoint1, dest = mDraw1)
                mInverse!!.mult3(src = mPoint2, dest = mDraw2)
                r.draw(
                    x1 = mDraw1[0].toInt(),
                    y1 = mDraw1[1].toInt(),
                    x2 = mDraw2[0].toInt(),
                    y2 = mDraw2[1].toInt()
                )
            }
        }
    }

    /**
     * Our init just normalizes the [light] vector to make it a unit vector.
     */
    init {
        VectorUtil.normalize(a = light)
    }

    /**
     * Transforms the triangles of the main 3D object and any pre/post objects by calling [transform].
     */
    fun transformTriangles() {
        transform()
    }

    /**
     * Transforms the scene objects using the inverse view matrix.
     *
     * This function applies the inverse view matrix ([mInverse]) to the main 3D object ([mObject3D]),
     * as well as to all pre-objects ([mPreObjects]) and post-objects ([mPostObjects]).
     *
     * If [mLightMovesWithCamera] is true, it also transforms the light direction vector ([light])
     * by the view matrix ([mMatrix]) and normalizes it, storing the result in [mTransformedLight].
     * Otherwise, it copies the original light direction to [mTransformedLight].
     */
    fun transform() {
        val m = mInverse
        if (mLightMovesWithCamera) {
            mMatrix.mult3v(src = light, dest = mTransformedLight)
            VectorUtil.normalize(a = mTransformedLight)
        } else {
            System.arraycopy(light, 0, mTransformedLight, 0, 3)
        }
        mObject3D!!.transform(m = m)
        for (obj in mPreObjects) {
            obj.transform(m = m)
        }
        for (obj in mPostObjects) {
            obj.transform(m = m)
        }
    }

    /**
     * The width of the screen in world coordinates.
     * This value determines how much of the 3D scene is visible horizontally.
     * Setting this property will:
     *  1. Update the screen width in the underlying [ViewMatrix].
     *  2. Recalculate the view matrix.
     *  3. Calculate the inverse of the view matrix.
     *  4. Transform all scene objects based on the new matrix.
     */
    var screenWidth: Double
        get() = mMatrix.screenWidth
        set(sw) {
            mMatrix.screenWidth = sw
            mMatrix.calcMatrix()
            mMatrix.invers(mInverse!!)
            transform()
        }

    /**
     * Handles the initial touch down event for trackball rotation.
     * This method records the starting position of the touch (x, y)
     * in the view matrix ([mMatrix]) and then recalculates the
     * inverse view matrix ([mInverse]).
     *
     * @param x The x-coordinate of the touch down event.
     * @param y The y-coordinate of the touch down event.
     */
    fun trackBallDown(x: Float, y: Float) {
        mMatrix.trackBallDown(x = x, y = y)
        mMatrix.invers(ret = mInverse!!)
    }

    /**
     * Handles the trackball move event.
     * This method updates the trackball position in the view matrix ([mMatrix])
     * based on the current touch coordinates (x, y).
     * After updating the trackball, it recalculates the inverse view matrix ([mInverse])
     * and then calls [transform] to apply the updated transformation to all scene objects.
     *
     * @param x The current x-coordinate of the touch during the move event.
     * @param y The current y-coordinate of the touch during the move event.
     */
    fun trackBallMove(x: Float, y: Float) {
        mMatrix.trackBallMove(x = x, y = y)
        mMatrix.invers(ret = mInverse!!)
        transform()
    }

    /**
     * Handles the end of a trackball interaction (touch up event).
     * This method signals the view matrix ([mMatrix]) that the trackball
     * interaction has ended at the given screen coordinates (x, y).
     * It then recalculates the inverse view matrix ([mInverse]).
     * Note: Unlike [trackBallMove], this method does not immediately trigger a scene transform.
     * The transform is usually handled separately after the interaction sequence is complete.
     *
     * @param x The x-coordinate where the trackball interaction ended.
     * @param y The y-coordinate where the trackball interaction ended.
     */
    fun trackBallUP(x: Float, y: Float) {
        mMatrix.trackBallUP(x = x, y = y)
        mMatrix.invers(ret = mInverse!!)
    }

    /**
     * Updates the scene by recalculating the inverse view matrix and then re-transforming all scene
     * objects. This method should be called whenever the view matrix ([mMatrix]) has been modified
     * directly (e.g., through direct manipulation of its properties, not through trackball or pan
     * methods which handle this internally). It ensures that the [mInverse] matrix is up-to-date
     * and that all scene objects are correctly transformed based on the current view.
     */
    fun update() {
        mMatrix.invers(ret = mInverse!!)
        transform()
    }

    /**
     * Initiates a pan operation at the given screen coordinates.
     * This method records the starting position of the pan (x, y)
     * in the view matrix ([mMatrix]) and then recalculates the
     * inverse view matrix ([mInverse]).
     *
     * @param x The x-coordinate of the pan start.
     * @param y The y-coordinate of the pan start.
     */
    fun panDown(x: Float, y: Float) {
        mMatrix.panDown(x, y)
        mMatrix.invers(mInverse!!)
    }

    /**
     * Handles a pan move event.
     * This method updates the pan position in the view matrix ([mMatrix])
     * based on the current touch coordinates (x, y).
     * After updating the pan, it recalculates the inverse view matrix ([mInverse])
     * and then calls [transform] to apply the updated transformation to all scene objects.
     *
     * @param x The current x-coordinate of the pan.
     * @param y The current y-coordinate of the pan.
     */
    fun panMove(x: Float, y: Float) {
        mMatrix.panMove(x = x, y = y)
        mMatrix.invers(ret = mInverse!!)
        transform()
    }

    /**
     * Finalizes the pan operation.
     * This method signals to the view matrix ([mMatrix]) that the pan gesture has ended.
     * It typically corresponds to a touch "up" event in a pan gesture.
     * Unlike [panMove], this method does not immediately trigger a scene transform or
     * recalculate the inverse matrix. The final state of the matrix after the pan
     * should have been set by the preceding [panMove] calls.
     */
    fun panUP() {
        mMatrix.panUP()
    }

    /**
     * Gets the current "look at" point of the camera in the 3D scene.
     * The look point is the 3D coordinate (x, y, z) that the camera is directed towards.
     * This property returns a string representation of the look point array (e.g., "[x, y, z]").
     */
    val lookPoint: String
        get() = mMatrix.lookPoint.contentToString()

    /**
     * Sets the screen dimensions and related buffers for the scene.
     * This function initializes or updates the screen width and height, the image buffer,
     * and the background color. It also sets up the necessary buffers (like the z-buffer)
     * and the view matrix based on these dimensions. Finally, it applies the transformation
     * to the scene objects.
     *
     * @param width The new width of the screen in pixels.
     * @param height The new height of the screen in pixels.
     * @param img The integer array representing the image buffer where the scene will be rendered.
     * @param background The ARGB integer color value to be used as the background color.
     */
    fun setScreenDim(width: Int, height: Int, img: IntArray, background: Int) {
        mMatrix.setScreenDim(x = width, y = height)
        setupBuffers(w = width, h = height, img = img, background = background)
        setUpMatrix(width = width, height = height)
        transform()
    }

    /**
     * Sets up the view matrix based on the current 3D object and screen dimensions.
     * This version of the function does not reset the camera's orientation.
     * It calculates the look point, diagonal size of the object, and screen width
     * to configure the view matrix. The inverse of this matrix is then calculated.
     *
     * @param width The width of the screen in pixels.
     * @param height The height of the screen in pixels.
     * @see setUpMatrix
     */
    fun setUpMatrix(width: Int, height: Int) {
        setUpMatrix(width = width, height = height, resetOrientation = false)
    }

    /**
     * Sets up the view matrix for the scene.
     * This function calculates and configures the view matrix ([mMatrix]) based on the
     * main 3D object ([mObject3D]), the specified screen dimensions, and the current zoom level.
     * It sets the look point to the center of the main object.
     * If [resetOrientation] is true, it also resets the camera's eye point and up vector
     * to a default orientation (looking down the z-axis, with y-axis as up, and positioned
     * based on the object's size and zoom).
     * If [resetOrientation] is false, it attempts to fix the up point of the existing matrix.
     * It then calculates the screen width in world coordinates based on the object's diagonal
     * and zoom, sets the screen dimensions in the matrix, calculates the main view matrix,
     * and finally computes its inverse.
     *
     * @param width The width of the viewport in pixels.
     * @param height The height of the viewport in pixels.
     * @param resetOrientation If true, the camera's eye point and up vector are reset to default
     * values. If false, the existing orientation is maintained or adjusted.
     */
    fun setUpMatrix(width: Int, height: Int, resetOrientation: Boolean) {
        val lookPoint = mObject3D!!.center()
        val diagonal = mObject3D!!.size() * zoom
        mMatrix.lookPoint = lookPoint
        if (resetOrientation) {
            val eyePoint = doubleArrayOf(
                lookPoint[0] - diagonal,
                lookPoint[1] - diagonal,
                lookPoint[2] + diagonal
            )
            mMatrix.eyePoint = eyePoint
            val upVector = doubleArrayOf(0.0, 0.0, 1.0)
            mMatrix.upVector = upVector
        } else {
            mMatrix.fixUpPoint()
        }
        val screenWidth = diagonal * 2
        mMatrix.screenWidth = screenWidth
        mMatrix.setScreenDim(x = width, y = height)
        mMatrix.calcMatrix()
        mMatrix.invers(ret = mInverse!!)
    }

    /**
     * Checks if the scene is not yet set up.
     * The scene is considered not set up if the inverse matrix ([mInverse]) is null.
     * This typically means that [setUpMatrix] or a similar initialization method
     * that calculates the inverse matrix has not been called or has failed.
     *
     * @return `true` if the inverse matrix is null, indicating the scene is not set up;
     * `false` otherwise.
     */
    fun notSetUp(): Boolean {
        return mInverse == null
    }

    /**
     * Interface for rendering lines.
     * Implementations of this interface are responsible for drawing a line segment
     * between two given 2D points (`x1`, `y1`) and (`x2`, `y2`).
     */
    interface LineRender {
        /**
         * Draws a line segment between two 2D points ([x1], [y1]) and ([x2], [y2]).
         *
         * @param x1 The x-coordinate of the starting point of the line.
         * @param y1 The y-coordinate of the starting point of the line.
         * @param x2 The x-coordinate of the ending point of the line.
         * @param y2 The y-coordinate of the ending point of the line.
         */
        fun draw(x1: Int, y1: Int, x2: Int, y2: Int)
    }

    /**
     * Draws a bounding box around the main 3D object ([mObject3D]).
     *
     * This function creates an internal [Box] object, sets its dimensions
     * to the min and max coordinates of the [mObject3D], and then calls
     * the [Box.drawLines] method to render the edges of the box using
     * the provided [LineRender] interface.
     *
     * If the [g] parameter ([LineRender]) is null, or if the main object
     * [mObject3D] is null, this function does nothing.
     *
     * @param g The [LineRender] implementation used to draw the lines of the box.
     * f null, no box will be drawn.
     */
    fun drawBox(g: LineRender?) {}

    /**
     * Defines an interface for a mathematical function that takes two float arguments (x and y)
     * and returns a float value. This is typically used to represent a surface in 3D space
     * where the output of the function is the z-coordinate for a given (x, y) pair.
     */
    interface Function {
        /**
         * Evaluates a function at the given 2D coordinates ([x], [y]).
         * This method is intended to be implemented by classes that represent
         * mathematical functions or surfaces, allowing them to provide a
         * value (typically a z-coordinate or a function output) for any
         * given [x] and [y] input.
         *
         * @param x The x-coordinate at which to evaluate the function.
         * @param y The y-coordinate at which to evaluate the function.
         * @return The result of evaluating the function at the specified (x, y) point.
         */
        fun eval(x: Float, y: Float): Float
    }

    /**
     * Adds an [Object3D] to the list of objects that are rendered before the main object.
     * These "pre-objects" are transformed and rendered in the order they are added,
     * prior to the rendering of the primary [mObject3D].
     *
     * @param obj The [Object3D] to be added to the pre-rendering list.
     */
    fun addPreObject(obj: Object3D) {
        mPreObjects.add(obj)
    }

    /**
     * Sets the main 3D object to be rendered in the scene.
     * This replaces any existing main object.
     *
     * @param obj The [Object3D] to set as the main object.
     */
    fun setObject(obj: Object3D) {
        mObject3D = obj
    }

    /**
     * Adds an [Object3D] to the list of objects that are rendered after the main object.
     * These objects are stored in the [mPostObjects] list.
     * Post-objects are transformed and rendered after the main [mObject3D] has been processed.
     *
     * @param obj The [Object3D] to be added to the post-rendering list.
     */
    fun addPostObject(obj: Object3D) {
        mPostObjects.add(obj)
    }

    /**
     * Resets the camera to its default position and orientation, looking at the main object.
     * This function calls [setUpMatrix] with the current screen dimensions and the
     * `resetOrientation` flag set to `true`. This effectively re-centers the camera on the
     * main 3D object and sets its orientation to a default view (typically looking down the z-axis).
     * After resetting the view matrix, it calls [transform] to apply the changes to all scene
     * objects.
     *
     * @see setUpMatrix
     * @see transform
     */
    fun resetCamera() {
        setUpMatrix(width = width, height = height, resetOrientation = true)
        transform()
    }

    /**
     * Initializes or updates the rendering buffers and related properties.
     * This function sets the [width] and [height] of the rendering area,
     * the [background] color, and the image buffer ([img]).
     * It also creates and initializes the z-buffer ([zBuff]) to [Float.MAX_VALUE]
     * for all pixels, and fills the image buffer with the specified background color.
     *
     * @param w The width of the rendering area in pixels.
     * @param h The height of the rendering area in pixels.
     * @param img The integer array that will serve as the image buffer for rendering.
     * This array will be filled with the background color initially.
     * @param background The ARGB integer color value to be used as the background.
     */
    fun setupBuffers(w: Int, h: Int, img: IntArray, background: Int) {
        width = w
        height = h
        this.background = background
        zBuff = FloatArray(size = w * h)
        this.img = img
        Arrays.fill(zBuff!!, Float.MAX_VALUE)
        Arrays.fill(img, background)
    }

    /**
     * Renders the scene.
     * This method clears the z-buffer and image buffer, then renders all objects in the scene.
     * It first renders the pre-objects, then the main object, and finally the post-objects.
     * If the z-buffer is not initialized, the method returns without rendering.
     *
     * @param type An integer parameter that is currently unused in the rendering process.
     * It might be reserved for future use to specify different rendering modes or types.
     */
    fun render(type: Int) {
        if (zBuff == null) {
            return
        }
        Arrays.fill(zBuff!!, Float.MAX_VALUE)
        Arrays.fill(img, background)
        for (mPreObject in mPreObjects) {
            mPreObject.render(s = this, zbuff = zBuff!!, img = img, width = width, height = height)
        }

        mObject3D!!.render(s = this, zbuff = zBuff!!, img = img, width = width, height = height)
        for (mPreObject in mPostObjects) {
            mPreObject.render(s = this, zbuff = zBuff!!, img = img, width = width, height = height)
        }
    }

    companion object {
        /**
         * A tag used for logging and debugging purposes within the Scene3D class.
         * The value "SurfaceGen" suggests it might be related to surface generation or rendering.
         */
        private const val TAG = "SurfaceGen"

        /**
         * Returns the minimum of three integer values.
         *
         * @param x1 The first integer.
         * @param x2 The second integer.
         * @param x3 The third integer.
         * @return The smallest of the three input integers.
         */
        private fun min(x1: Int, x2: Int, x3: Int): Int {
            return if (x1 > x2) (if (x2 > x3) x3 else x2) else if (x1 > x3) x3 else x1
        }

        /**
         * Calculates the maximum of three integer values.
         *
         * @param x1 The first integer.
         * @param x2 The second integer.
         * @param x3 The third integer.
         * @return The largest integer among x1, x2, and x3.
         */
        private fun max(x1: Int, x2: Int, x3: Int): Int {
            return if (x1 < x2) (if (x2 < x3) x3 else x2) else if (x1 < x3) x3 else x1
        }

        /**
         * Converts HSV (Hue, Saturation, Value) color values to an RGB integer representation.
         * This version uses a `when` statement for determining the RGB components based on hue.
         * The alpha component is set to opaque (-0x1000000 or 0xFF000000).
         *
         * @param hue The hue component of the color, expected to be in the range [0, 1).
         * @param saturation The saturation component of the color, in the range [0, 1].
         * @param value The value (brightness) component of the color, in the range [0, 1].
         * @return An integer representing the ARGB color. If the hue calculation results in an
         * unexpected value, returns 0 (transparent black).
         */
        fun hsvtorgbSlow(hue: Float, saturation: Float, value: Float): Int {
            val h: Int = (hue * 6).toInt()
            val f: Float = hue * 6 - h
            val p: Int = (0.5f + 255 * value * (1 - saturation)).toInt()
            val q: Int = (0.5f + 255 * value * (1 - f * saturation)).toInt()
            val t: Int = (0.5f + 255 * value * (1 - (1 - f) * saturation)).toInt()
            val v: Int = (0.5f + 255 * value).toInt()
            when (h) {
                0 -> return -0x1000000 or (v shl 16) + (t shl 8) + p
                1 -> return -0x1000000 or (q shl 16) + (v shl 8) + p
                2 -> return -0x1000000 or (p shl 16) + (v shl 8) + t
                3 -> return -0x1000000 or (p shl 16) + (q shl 8) + v
                4 -> return -0x1000000 or (t shl 16) + (p shl 8) + v
                5 -> return -0x1000000 or (v shl 16) + (p shl 8) + q
            }
            return 0
        }

        /**
         * Converts HSV (Hue, Saturation, Value) color values to an RGB integer representation.
         * The alpha component is set to opaque (-0x1000000 or 0xFF000000).
         * This function uses a series of if statements to determine the RGB components
         * based on the hue.
         *
         * @param hue The hue component of the color, expected to be in the range [0, 1).
         * A hue of 0 or 1 corresponds to red.
         * @param saturation The saturation component of the color, in the range [0, 1].
         * A saturation of 0 results in a grayscale color.
         * @param value The value (brightness) component of the color, in the range [0, 1].
         * A value of 0 results in black.
         * @return An integer representing the ARGB color. If the hue calculation results in an
         * unexpected value (outside the 0-5 range for `h`), it returns 0 (transparent black).
         */
        fun hsvToRgb(hue: Float, saturation: Float, value: Float): Int {
            val h: Int = (hue * 6).toInt()
            val f: Float = hue * 6 - h
            val p: Int = (0.5f + 255 * value * (1 - saturation)).toInt()
            val q: Int = (0.5f + 255 * value * (1 - f * saturation)).toInt()
            val t: Int = (0.5f + 255 * value * (1 - (1 - f) * saturation)).toInt()
            val v: Int = (0.5f + 255 * value).toInt()
            if (h == 0) {
                return -0x1000000 or (v shl 16) + (t shl 8) + p
            }
            if (h == 1) {
                return -0x1000000 or (q shl 16) + (v shl 8) + p
            }
            if (h == 2) {
                return -0x1000000 or (p shl 16) + (v shl 8) + t
            }
            if (h == 3) {
                return -0x1000000 or (p shl 16) + (q shl 8) + v
            }
            if (h == 4) {
                return -0x1000000 or (t shl 16) + (p shl 8) + v
            }
            if (h == 5) {
                return -0x1000000 or (v shl 16) + (p shl 8) + q
            }

            return 0
        }

        /**
         * Draws a line segment in 3D space, performing z-buffering.
         * The line is drawn between two points ([fx1], [fy1], [fz1]) and ([fx2], [fy2], [fz2]).
         * Each pixel along the line is checked against the z-buffer. If the new pixel's
         * z-value is closer (smaller) than the existing value in the z-buffer (with a small
         * tolerance), the pixel is drawn with the specified color and the z-buffer is updated.
         * Pixels outside the screen boundaries ([w], [h]) are skipped.
         *
         * @param zbuff The z-buffer array, storing depth values for each pixel.
         * @param img The image buffer array where the line will be drawn.
         * @param color The ARGB integer color of the line.
         * @param w The width of the image buffer in pixels.
         * @param h The height of the image buffer in pixels.
         * @param fx1 The x-coordinate of the starting point of the line.
         * @param fy1 The y-coordinate of the starting point of the line.
         * @param fz1 The z-coordinate (depth) of the starting point of the line.
         * @param fx2 The x-coordinate of the ending point of the line.
         * @param fy2 The y-coordinate of the ending point of the line.
         * @param fz2 The z-coordinate (depth) of the ending point of the line.
         */
        @JvmStatic
        fun drawline(
            zbuff: FloatArray, img: IntArray, color: Int, w: Int, h: Int,
            fx1: Float, fy1: Float, fz1: Float,
            fx2: Float, fy2: Float, fz2: Float
        ) {
            val dx: Float = fx2 - fx1
            val dy: Float = fy2 - fy1
            val dz: Float = fz2 - fz1
            val zang: Float = sqrt(x = dy * dy + dz * dz)
            val steps: Float = sqrt(x = dx * dx + zang * zang)
            var t = 0f
            while (t < 1) {
                val px: Float = fx1 + t * dx
                val py: Float = fy1 + t * dy
                val pz: Float = fz1 + t * dz
                val ipx: Int = px.toInt()
                val ipy: Int = py.toInt()
                if (ipx < 0 || ipx >= w || ipy < 0 || ipy >= h) {
                    t += 1 / steps
                    continue
                }
                val point: Int = ipx + w * ipy
                if (zbuff[point] >= pz - 2) {
                    img[point] = color
                }
                t += 1 / steps
            }
        }

        /**
         * Determines if a 2D triangle defined by three points is a back-face.
         * This is typically used in 2D screen space after 3D projection.
         * A triangle is considered a back-face if its vertices are ordered clockwise
         * in screen space. This function calculates the signed area of the triangle
         * (or a value proportional to it). A negative area indicates a clockwise winding order.
         *
         * The formula used is based on the 2D cross product:
         * ```
         * (x1 - x2) * (y3 - y2) - (y1 - y2) * (x3 - x2)
         * ```
         *
         * @param fx3 The x-coordinate of the third vertex of the triangle.
         * @param fy3 The y-coordinate of the third vertex of the triangle.
         * @param fz3 The z-coordinate of the third vertex (unused in this 2D back-face check).
         * @param fx2 The x-coordinate of the second vertex of the triangle.
         * @param fy2 The y-coordinate of the second vertex of the triangle.
         * @param fz2 The z-coordinate of the second vertex (unused in this 2D back-face check).
         * @param fx1 The x-coordinate of the first vertex of the triangle.
         * @param fy1 The y-coordinate of the first vertex of the triangle.
         * @param fz1 The z-coordinate of the first vertex (unused in this 2D back-face check).
         * @return `true` if the triangle is a back-face (vertices are clockwise), `false` otherwise.
         */
        @JvmStatic
        fun isBackface(
            fx3: Float, fy3: Float, fz3: Float,
            fx2: Float, fy2: Float, fz2: Float,
            fx1: Float, fy1: Float, fz1: Float
        ): Boolean {
            return (fx1 - fx2) * (fy3 - fy2) - (fy1 - fy2) * (fx3 - fx2) < 0
        }

        /**
         * Renders a flat-shaded triangle onto the screen.
         *
         * This function implements a scanline rasterization algorithm to fill a triangle
         * defined by three vertices (fx1, fy1, fz1), (fx2, fy2, fz2), and (fx3, fy3, fz3).
         * It performs z-buffering to ensure correct depth occlusion.
         * The triangle is colored with a single specified `color`.
         *
         * The process involves:
         *  1. Ensuring the triangle vertices are ordered in a counter-clockwise direction
         *  on the screen to simplify edge calculations.
         *  2. Calculating the plane equation of the triangle in screen space (dx, dy, zoff)
         *  to interpolate z-values across the triangle.
         *  3. Converting floating-point vertex coordinates to 28.4 fixed-point format
         *  for efficient integer-based scanline traversal.
         *  4. Determining the bounding box of the triangle on the screen and clamping it
         *  to the screen dimensions ([w], [h]).
         *  5. Iterating through each scanline (y-coordinate) within the triangle's
         *  bounding box.
         *  6. For each scanline, iterating through pixels (x-coordinate) within the
         *  triangle's horizontal extent for that scanline.
         *  7. Using edge functions (`lCX1`, `lCX2`, `lCX3`) to determine if a pixel (`x`, `y`)
         *  is inside the triangle.
         *  8. If the pixel is inside, calculating its z-value using the interpolated plane
         *  equation.
         *  9. Performing a z-buffer test: if the calculated z-value is less than the
         *  current value in [zbuff] for that pixel, the pixel is updated with the
         *  new [color] and its z-value is updated in [zbuff].
         *
         * @param zbuff The z-buffer array, storing the depth value for each pixel.
         * @param img The image buffer array, storing the color value for each pixel.
         * @param color The ARGB integer color to fill the triangle with.
         * @param w The width of the image buffer in pixels.
         * @param h The height of the image buffer in pixels.
         * @param fx3 The x-coordinate of the third vertex of the triangle.
         * @param fy3 The y-coordinate of the third vertex of the triangle.
         * @param fz3 The z-coordinate of the third vertex of the triangle.
         * @param fx2 The x-coordinate of the second vertex of the triangle.
         * @param fy2 The y-coordinate of the second vertex of the triangle.
         * @param fz2 The z-coordinate of the second vertex of the triangle.
         * @param fx1 The x-coordinate of the first vertex of the triangle.
         * @param fy1 The y-coordinate of the first vertex of the triangle.
         */
        @JvmStatic
        fun triangle(
            zbuff: FloatArray, img: IntArray, color: Int, w: Int, h: Int,
            fx3: Float, fy3: Float, fz3: Float,
            fx2: Float, fy2: Float, fz2: Float,
            fx1: Float, fy1: Float, fz1: Float
        ) {
            var fx2Local: Float = fx2
            var fy2Local: Float = fy2
            var fz2Local: Float = fz2
            var fx1Local: Float = fx1
            var fy1Local: Float = fy1
            var fz1Local: Float = fz1
            if ((fx1Local - fx2Local) * (fy3 - fy2Local) - (fy1Local - fy2Local) * (fx3 - fx2Local) < 0) {
                val tmpx: Float = fx1Local
                val tmpy: Float = fy1Local
                val tmpz: Float = fz1Local
                fx1Local = fx2Local
                fy1Local = fy2Local
                fz1Local = fz2Local
                fx2Local = tmpx
                fy2Local = tmpy
                fz2Local = tmpz
            }
            // using maxmima
            // string(solve([x1*dx+y1*dy+zoff=z1,x2*dx+y2*dy+zoff=z2,x3*dx+y3*dy+zoff=z3],[dx,dy,zoff]));
            val d: Double =
                (fx1Local * (fy3 - fy2Local) - fx2Local * fy3 + fx3 * fy2Local + ((fx2Local - fx3)
                    * fy1Local)).toDouble()
            if (d == 0.0) {
                return
            }
            val dx: Float =
                (-(fy1Local * (fz3 - fz2Local) - fy2Local * fz3 + fy3 * fz2Local + ((fy2Local - fy3)
                    * fz1Local)) / d).toFloat()
            val dy: Float =
                ((fx1Local * (fz3 - fz2Local) - fx2Local * fz3 + fx3 * fz2Local + ((fx2Local - fx3)
                    * fz1Local)) / d).toFloat()
            val zoff: Float = (fx1Local * (fy3 * fz2Local - fy2Local * fz3) + (fy1Local
                * (fx2Local * fz3 - fx3 * fz2Local)) + (fx3 * fy2Local - fx2Local * fy3) * fz1Local / d).toFloat()

            // 28.4 fixed-point coordinates
            val y1: Int = (16.0f * fy1Local + .5f).toInt()
            val y2: Int = (16.0f * fy2Local + .5f).toInt()
            val y3: Int = (16.0f * fy3 + .5f).toInt()
            val x1: Int = (16.0f * fx1Local + .5f).toInt()
            val x2: Int = (16.0f * fx2Local + .5f).toInt()
            val x3: Int = (16.0f * fx3 + .5f).toInt()
            val dX12: Int = x1 - x2
            val dX23: Int = x2 - x3
            val dX31: Int = x3 - x1
            val dY12: Int = y1 - y2
            val dY23: Int = y2 - y3
            val dY31: Int = y3 - y1
            val fDX12: Int = dX12 shl 4
            val fDX23: Int = dX23 shl 4
            val fDX31: Int = dX31 shl 4
            val fDY12: Int = dY12 shl 4
            val fDY23: Int = dY23 shl 4
            val fDY31: Int = dY31 shl 4
            var minx: Int = min(x1, x2, x3) + 0xF shr 4
            var maxx: Int = max(x1, x2, x3) + 0xF shr 4
            var miny: Int = min(x1 = y1, x2 = y2, x3 = y3) + 0xF shr 4
            var maxy: Int = max(x1 = y1, x2 = y2, x3 = y3) + 0xF shr 4
            if (miny < 0) {
                miny = 0
            }
            if (minx < 0) {
                minx = 0
            }
            if (maxx > w) {
                maxx = w
            }
            if (maxy > h) {
                maxy = h
            }
            var off: Int = miny * w
            var c1: Int = dY12 * x1 - dX12 * y1
            var c2: Int = dY23 * x2 - dX23 * y2
            var c3: Int = dY31 * x3 - dX31 * y3
            if (dY12 < 0 || dY12 == 0 && dX12 > 0) {
                c1++
            }
            if (dY23 < 0 || dY23 == 0 && dX23 > 0) {
                c2++
            }
            if ((dY31 < 0 || dY31 == 0) && dX31 > 0) {
                c3++
            }
            var cY1: Int = c1 + dX12 * (miny shl 4) - dY12 * (minx shl 4)
            var cY2: Int = c2 + dX23 * (miny shl 4) - dY23 * (minx shl 4)
            var cY3: Int = c3 + dX31 * (miny shl 4) - dY31 * (minx shl 4)
            for (y in miny until maxy) {
                var lCX1: Int = cY1
                var lCX2: Int = cY2
                var lCX3: Int = cY3
                val p: Float = zoff + dy * y
                for (x in minx until maxx) {
                    if (lCX1 > 0 && lCX2 > 0 && lCX3 > 0) {
                        val point: Int = x + off
                        val zval: Float = p + dx * x
                        if (zbuff[point] > zval) {
                            zbuff[point] = zval
                            img[point] = color
                        }
                    }
                    lCX1 -= fDY12
                    lCX2 -= fDY23
                    lCX3 -= fDY31
                }
                cY1 += fDX12
                cY2 += fDX23
                cY3 += fDX31
                off += w
            }
        }

        /**
         * Renders a triangle with Phong shading (interpolated color) onto the screen.
         *
         * This function implements a scanline rasterization algorithm to fill a triangle
         * defined by three vertices (fx1, fy1, fz1), (fx2, fy2, fz2), and (fx3, fy3, fz3).
         * It performs z-buffering for depth occlusion and interpolates hue and brightness
         * values across the triangle to achieve a smooth shading effect. The saturation
         * (`sat`) is constant for the entire triangle. The interpolated HSV values are
         * then converted to RGB for display.
         *
         * The process involves:
         *  1. Ensuring the triangle vertices are ordered in a counter-clockwise direction
         *  on the screen to simplify edge calculations. If not, the vertices (and their
         *  corresponding hue/brightness attributes) are swapped.
         *  2. Calculating the plane equations for z-depth (dx, dy, zoff), hue (dhx, dhy, hoff),
         *  and brightness (dbx, dby, boff) to enable interpolation across the triangle.
         *  This is done by solving a system of linear equations. If the determinant `d` is
         *  zero, the triangle is degenerate and rendering is skipped.
         *  3. Converting floating-point vertex coordinates to 28.4 fixed-point format
         *  for efficient integer-based scanline traversal.
         *  4. Determining the bounding box of the triangle on the screen and clamping it
         *  to the screen dimensions (w, h).
         *  5. Iterating through each scanline (y-coordinate) within the triangle's
         *  bounding box.
         *  6. For each scanline, iterating through pixels (x-coordinate) within the
         *  triangle's horizontal extent for that scanline.
         *  7. Using edge functions (lCX1, lCX2, lCX3) to determine if a pixel (x, y)
         *  is inside the triangle.
         *  8. If the pixel is inside:
         *      a. Interpolate its z-value, hue, and brightness using the calculated
         *      plane equations.
         *      b. Convert the interpolated HSV values to RGB.
         *      c. Perform a z-buffer test: if the calculated z-value is less than the
         *      current value in [zbuff] for that pixel, the pixel is updated with the
         *      new [color] and its z-value is updated in [zbuff].
         *  9. If the pixel is outside the triangle, continue to the next pixel.
         *
         * @param zbuff The z-buffer array, storing the depth value for each pixel.
         * @param img The image buffer array, storing the color value for each pixel.
         * @param h3 The hue value of the third vertex of the triangle.
         * @param b3 The brightness value of the third vertex of the triangle.
         * @param h2 The hue value of the second vertex of the triangle.
         * @param b2 The brightness value of the second vertex of the triangle.
         * @param h1 The hue value of the first vertex of the triangle.
         * @param b1 The brightness value of the first vertex of the triangle.
         * @param sat The constant saturation value for the entire triangle.
         * @param w The width of the image buffer in pixels.
         * @param h The height of the image buffer in pixels.
         * @param fx3 The x-coordinate of the third vertex of the triangle.
         * @param fy3 The y-coordinate of the third vertex of the triangle.
         * @param fz3 The z-coordinate of the third vertex of the triangle.
         * @param fx2 The x-coordinate of the second vertex of the triangle.
         * @param fy2 The y-coordinate of the second vertex of the triangle.
         * @param fz2 The z-coordinate of the second vertex of the triangle.
         * @param fx1 The x-coordinate of the first vertex of the triangle.
         * @param fy1 The y-coordinate of the first vertex of the triangle.
         * @param fz1 The z-coordinate of the first vertex of the triangle.
         */
        fun trianglePhong(
            zbuff: FloatArray, img: IntArray,
            h3: Float, b3: Float,
            h2: Float, b2: Float,
            h1: Float, b1: Float,
            sat: Float,
            w: Int, h: Int,
            fx3: Float, fy3: Float, fz3: Float,
            fx2: Float, fy2: Float, fz2: Float,
            fx1: Float, fy1: Float, fz1: Float
        ) {
            var h2Local: Float = h2
            var b2Local: Float = b2
            var h1Local: Float = h1
            var b1Local: Float = b1
            var fx2Local: Float = fx2
            var fy2Local: Float = fy2
            var fz2Local: Float = fz2
            var fx1Local: Float = fx1
            var fy1Local: Float = fy1
            var fz1Local: Float = fz1
            if ((fx1Local - fx2Local) * (fy3 - fy2Local) - (fy1Local - fy2Local) * (fx3 - fx2Local) < 0) {
                val tmpx: Float = fx1Local
                val tmpy: Float = fy1Local
                val tmpz: Float = fz1Local
                fx1Local = fx2Local
                fy1Local = fy2Local
                fz1Local = fz2Local
                fx2Local = tmpx
                fy2Local = tmpy
                fz2Local = tmpz
                val tmph: Float = h1Local
                val tmpb: Float = b1Local
                h1Local = h2Local
                b1Local = b2Local
                h2Local = tmph
                b2Local = tmpb
            }
            // using maxmima
            // string(solve([x1*dx+y1*dy+zoff=z1,x2*dx+y2*dy+zoff=z2,x3*dx+y3*dy+zoff=z3],[dx,dy,zoff]));
            val d: Float =
                (fx1Local * (fy3 - fy2Local) - fx2Local * fy3 + fx3 * fy2Local + ((fx2Local - fx3)
                    * fy1Local))
            if (d == 0.0f) {
                return
            }
            val dx: Float =
                (-(fy1Local * (fz3 - fz2Local) - fy2Local * fz3 + fy3 * fz2Local + ((fy2Local - fy3)
                    * fz1Local)) / d)
            val dy: Float =
                ((fx1Local * (fz3 - fz2Local) - fx2Local * fz3 + fx3 * fz2Local + ((fx2Local - fx3)
                    * fz1Local)) / d)
            val zoff: Float = ((fx1Local * (fy3 * fz2Local - fy2Local * fz3) + (fy1Local
                * (fx2Local * fz3 - fx3 * fz2Local)) + (fx3 * fy2Local - fx2Local * fy3) * fz1Local) / d)
            val dhx: Float =
                (-(fy1Local * (h3 - h2Local) - fy2Local * h3 + fy3 * h2Local + ((fy2Local - fy3)
                    * h1Local)) / d)
            val dhy: Float =
                ((fx1Local * (h3 - h2Local) - fx2Local * h3 + fx3 * h2Local + ((fx2Local - fx3)
                    * h1Local)) / d)
            val hoff: Float = ((fx1Local * (fy3 * h2Local - fy2Local * h3) + (fy1Local
                * (fx2Local * h3 - fx3 * h2Local)) + (fx3 * fy2Local - fx2Local * fy3) * h1Local) / d)
            val dbx: Float =
                (-(fy1Local * (b3 - b2Local) - fy2Local * b3 + fy3 * b2Local + ((fy2Local - fy3)
                    * b1Local)) / d)
            val dby: Float =
                ((fx1Local * (b3 - b2Local) - fx2Local * b3 + fx3 * b2Local + ((fx2Local - fx3)
                    * b1Local)) / d)
            val boff: Float = ((fx1Local * (fy3 * b2Local - fy2Local * b3) + (fy1Local
                * (fx2Local * b3 - fx3 * b2Local)) + (fx3 * fy2Local - fx2Local * fy3) * b1Local) / d)

            // 28.4 fixed-point coordinates
            val lY1: Int = (16.0f * fy1Local + .5f).toInt()
            val lY2: Int = (16.0f * fy2Local + .5f).toInt()
            val lY3: Int = (16.0f * fy3 + .5f).toInt()
            val lX1: Int = (16.0f * fx1Local + .5f).toInt()
            val lX2: Int = (16.0f * fx2Local + .5f).toInt()
            val lX3: Int = (16.0f * fx3 + .5f).toInt()
            val lDX12: Int = lX1 - lX2
            val lDX23: Int = lX2 - lX3
            val lDX31: Int = lX3 - lX1
            val lDY12: Int = lY1 - lY2
            val lDY23: Int = lY2 - lY3
            val lDY31: Int = lY3 - lY1
            val lFDX12: Int = lDX12 shl 4
            val lFDX23: Int = lDX23 shl 4
            val lFDX31: Int = lDX31 shl 4
            val lFDY12: Int = lDY12 shl 4
            val lFDY23: Int = lDY23 shl 4
            val lFDY31: Int = lDY31 shl 4
            var minx: Int = min(x1 = lX1, x2 = lX2, x3 = lX3) + 0xF shr 4
            var maxx: Int = max(x1 = lX1, x2 = lX2, x3 = lX3) + 0xF shr 4
            var miny: Int = min(x1 = lY1, x2 = lY2, x3 = lY3) + 0xF shr 4
            var maxy: Int = max(x1 = lY1, x2 = lY2, x3 = lY3) + 0xF shr 4
            if (miny < 0) {
                miny = 0
            }
            if (minx < 0) {
                minx = 0
            }
            if (maxx > w) {
                maxx = w
            }
            if (maxy > h) {
                maxy = h
            }
            var off: Int = miny * w
            var lC1: Int = lDY12 * lX1 - lDX12 * lY1
            var lC2: Int = lDY23 * lX2 - lDX23 * lY2
            var lC3: Int = lDY31 * lX3 - lDX31 * lY3
            if (lDY12 < 0 || lDY12 == 0 && lDX12 > 0) {
                lC1++
            }
            if (lDY23 < 0 || lDY23 == 0 && lDX23 > 0) {
                lC2++
            }
            if (lDY31 < 0 || lDY31 == 0 && lDX31 > 0) {
                lC3++
            }
            var lCY1: Int = lC1 + lDX12 * (miny shl 4) - lDY12 * (minx shl 4)
            var lCY2: Int = lC2 + lDX23 * (miny shl 4) - lDY23 * (minx shl 4)
            var lCY3: Int = lC3 + lDX31 * (miny shl 4) - lDY31 * (minx shl 4)
            for (y in miny until maxy) {
                var lCX1: Int = lCY1
                var lCX2: Int = lCY2
                var lCX3: Int = lCY3
                val p: Float = zoff + dy * y
                val ph: Float = hoff + dhy * y
                val pb: Float = boff + dby * y
                for (x in minx until maxx) {
                    if (lCX1 > 0 && lCX2 > 0 && lCX3 > 0) {
                        val point: Int = x + off
                        val zval: Float = p + dx * x
                        val hue: Float = ph + dhx * x
                        val bright: Float = pb + dbx * x
                        if (zbuff[point] > zval) {
                            zbuff[point] = zval
                            img[point] = hsvToRgb(hue = hue, saturation = sat, value = bright)
                        }
                    }
                    lCX1 -= lFDY12
                    lCX2 -= lFDY23
                    lCX3 -= lFDY31
                }
                lCY1 += lFDX12
                lCY2 += lFDX23
                lCY3 += lFDX31
                off += w
            }
        }

    }
}