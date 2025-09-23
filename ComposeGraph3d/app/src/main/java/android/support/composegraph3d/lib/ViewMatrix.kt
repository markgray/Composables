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
    "ReplaceJavaStaticMethodWithKotlinAnalog",
    "MemberVisibilityCanBePrivate"
)

package android.support.composegraph3d.lib

import java.text.DecimalFormat
import java.util.*

/**
 * This calculates the matrix that transforms triangles from world space to screen space.
 */
class ViewMatrix : Matrix() {
    /**
     * The point in 3D space that the camera is looking at.
     * This is a [DoubleArray] of 3 values representing the x, y, and z coordinates.
     * Modifying this value will change the direction the camera is pointing.
     */
    var lookPoint: DoubleArray? = null

    /**
     * The point in 3D space that the camera is located.
     * This is a [DoubleArray] of 3 values representing the x, y, and z coordinates.
     * Modifying this value will change the position of the camera.
     */
    var eyePoint: DoubleArray? = null

    /**
     * The direction that is considered "up" in the 3D world.
     * This is a [DoubleArray] of 3 values representing a vector in 3D space.
     * Modifying this value will change the orientation of the camera.
     */
    var upVector: DoubleArray? = null

    /**
     * The width of the screen in world coordinates.
     * This value is used to scale the view matrix to fit the screen dimensions.
     * Modifying this value will change the zoom level of the camera.
     */
    var screenWidth: Double = 0.0

    /**
     * The dimensions of the screen in pixels.
     * This is an [IntArray] of 2 values representing the width and height of the screen.
     * This value is used to scale the view matrix to fit the screen dimensions.
     * Modifying this value will change the aspect ratio of the camera view.
     */
    var mScreenDim: IntArray? = null

    /**
     * A temporary [DoubleArray] of 3 values used for vector calculations.
     * This array is reused in various calculations to avoid unnecessary memory allocations.
     * It typically stores intermediate results of vector operations.
     */
    var mTmp1: DoubleArray = DoubleArray(size = 3)

    /**
     * Prints the current state of the ViewMatrix, including the look point, eye point, up vector,
     * screen width, and screen dimensions. This is useful for debugging purposes to inspect the
     * current camera setup.
     */
    @Suppress("ReplacePrintlnWithLogging")
    override fun print() {
        println("mLookPoint  :" + toStr(d = lookPoint))
        println("mEyePoint   :" + toStr(d = eyePoint))
        println("mUpVector   :" + toStr(d = upVector))
        println("mScreenWidth:" + toStr(d = screenWidth))
        println("mScreenDim  :[" + mScreenDim!![0] + "," + mScreenDim!![1] + "]")
    }

    /**
     * Sets the dimensions of the screen in pixels.
     *
     * This method updates the internal representation of the screen's width and height which is
     * stored in [IntArray] property [mScreenDim]. These dimensions are used in various
     * calculations, particularly for transforming world coordinates to screen coordinates
     * and for handling user input like touch gestures.
     *
     * @param x The width of the screen in pixels.
     * @param y The height of the screen in pixels.
     */
    fun setScreenDim(x: Int, y: Int) {
        mScreenDim = intArrayOf(x, y)
    }

    /**
     * This would be a good function name for a function that resets the view matrix to an identity
     * matrix, effectively undoing any transformations (rotation, translation, scaling) that have
     * been applied, returning the camera to its default state and orientation. This is would be
     * useful for starting with a clean slate or resetting the view after a series of
     * transformations. Unimplemented and apparently unused.
     */
    fun makeUnit() {}

    /**
     * Adjusts the upVector to be orthogonal to the line of sight.
     * This ensures that the "up" direction is perpendicular to the direction
     * the camera is looking, which is crucial for maintaining a stable and
     * intuitive camera orientation.
     *
     * The process involves:
     *  1. Calculating the view vector (`zv`) from the eye point to the look point.
     *  2. Normalizing the view vector (`zv`).
     *  3. Calculating the right vector (`rv`) by taking the cross product of the view vector and
     *  the current up vector.
     *  4. Recalculating the up vector by taking the cross product of the view vector and the right
     *  vector. This ensures the new up vector is orthogonal to both the view and right vectors.
     *  5. Normalizing the new up vector.
     *  6. Inverting the up vector. This step might be specific to the coordinate system or desired
     *  camera behavior.
     */
    fun fixUpPoint() {
        val zv = doubleArrayOf(
            eyePoint!![0] - lookPoint!![0],
            eyePoint!![1] - lookPoint!![1],
            eyePoint!![2] - lookPoint!![2]
        )
        VectorUtil.normalize(a = zv)
        val rv = DoubleArray(size = 3)
        VectorUtil.cross(a = zv, b = upVector, out = rv)
        VectorUtil.cross(a = zv, b = rv, out = upVector)
        VectorUtil.normalize(a = upVector)
        VectorUtil.mult(a = upVector, b = -1.0, out = upVector)
    }

    /**
     * Calculates the view matrix based on the current camera settings.
     * This matrix transforms coordinates from world space to screen space.
     * It uses the [eyePoint], [lookPoint], [upVector], [screenWidth], and [mScreenDim]
     * to construct the transformation. If [mScreenDim] is not set, the function returns
     * without calculating the matrix.
     *
     * The process involves:
     *  1. Calculating the view direction vector (`zv`) from [eyePoint] to [lookPoint] and
     *  normalizing it.
     *  2. Calculating a scale factor based on [screenWidth] and the screen width in pixels.
     *  3. Constructing the columns of the view matrix:
     *      - The Z-axis of the camera (third column of the matrix) is based on the normalized
     *      view direction.
     *      - The X-axis of the camera (first column) is calculated as the cross product of the
     *      view direction and the [upVector].
     *      - The Y-axis of the camera (second column) is based on the [upVector] (negated).
     *  4. Calculating the translation part of the matrix (fourth column) to position the camera
     *  correctly relative to the screen's center and depth.
     *  5. Storing the resulting 4x4 matrix in the [ViewMatrix.m] property of `this` [ViewMatrix].
     */
    fun calcMatrix() {
        if (mScreenDim == null) {
            return
        }
        val scale: Double = screenWidth / mScreenDim!![0]
        val zv = doubleArrayOf(
            lookPoint!![0] - eyePoint!![0],
            lookPoint!![1] - eyePoint!![1],
            lookPoint!![2] - eyePoint!![2]
        )
        VectorUtil.normalize(a = zv)
        val m = DoubleArray(size = 16)
        m[2] = zv[0] * scale
        m[6] = zv[1] * scale
        m[10] = zv[2] * scale
        m[14] = 0.0
        calcRight(a = zv, b = upVector, out = zv)
        m[0] = zv[0] * scale
        m[4] = zv[1] * scale
        m[8] = zv[2] * scale
        m[12] = 0.0
        m[1] = -upVector!![0] * scale
        m[5] = -upVector!![1] * scale
        m[9] = -upVector!![2] * scale
        m[13] = 0.0
        val sw = mScreenDim!![0] / 2 - 0.5
        val sh = mScreenDim!![1] / 2 - 0.5
        val sz = -0.5
        m[3] = eyePoint!![0] - (m[0] * sw + m[1] * sh + m[2] * sz)
        m[7] = eyePoint!![1] - (m[4] * sw + m[5] * sh + m[6] * sz)
        m[11] = eyePoint!![2] - (m[8] * sw + m[9] * sh + m[10] * sz)
        m[15] = 1.0
        this.m = m
    }

    /**
     * Calculates the look point and screen width based on the provided 3D object and voxel
     * dimensions. This method determines the center of the object and the appropriate screen
     * width to encompass it.
     *
     * The process involves:
     *  1. Finding the minimum and maximum x, y, and z coordinates of the object's vertices.
     *  2. Calculating the center of the object by averaging the min and max coordinates for each
     *  axis, scaled by the corresponding voxel dimension. This center point becomes the [lookPoint].
     *  3. Calculating the [screenWidth] as twice the maximum dimension (width, height, or depth)
     *  of the object, scaled by the voxel dimensions. This ensures the entire object is visible.
     *
     * @param tri The 3D object ([Object3D]) whose vertices are used to determine the look point
     * and screen width.
     * @param voxelDim A [FloatArray] representing the dimensions of a voxel in x, y, and z
     * directions. These dimensions are used to scale the object's coordinates.
     * @param w The width of the screen in pixels. (Currently unused in this specific overload)
     * @param h The height of the screen in pixels. (Currently unused in this specific overload)
     */
    private fun calcLook(tri: Object3D, voxelDim: FloatArray, w: Int, h: Int) {
        var minx = Float.MAX_VALUE
        var miny = Float.MAX_VALUE
        var minz = Float.MAX_VALUE
        var maxx = -Float.MAX_VALUE
        var maxy = -Float.MAX_VALUE
        var maxz = -Float.MAX_VALUE
        var i = 0
        while (i < tri.vert.size) {
            maxx = Math.max(tri.vert[i], maxx)
            minx = Math.min(tri.vert[i], minx)
            maxy = Math.max(tri.vert[i + 1], maxy)
            miny = Math.min(tri.vert[i + 1], miny)
            maxz = Math.max(tri.vert[i + 2], maxz)
            minz = Math.min(tri.vert[i + 2], minz)
            i += 3
        }
        lookPoint = doubleArrayOf(
            (voxelDim[0] * (maxx + minx) / 2).toDouble(),
            (voxelDim[1] * (maxy + miny) / 2).toDouble(),
            (voxelDim[2] * (maxz + minz) / 2).toDouble()
        )
        screenWidth = (Math.max(
            voxelDim[0] * (maxx - minx),
            Math.max(voxelDim[1] * (maxy - miny), voxelDim[2] * (maxz - minz))
        ) * 2).toDouble()
    }

    /**
     * Calculates the look point and screen width based on the provided 3D object. This method
     * determines the center of the object and the appropriate screen width to encompass it,
     * assuming the object's coordinates are already in world space (not scaled by voxel
     * dimensions).
     *
     * The process involves:
     *  1. Finding the minimum and maximum x, y, and z coordinates of the object's vertices.
     *  2. Calculating the center of the object by averaging the min and max coordinates for
     *  each axis. This center point becomes the [lookPoint].
     *  3. Calculating the [screenWidth] as the maximum dimension (width, height, or depth)
     *  of the object. This ensures the entire object is visible.
     *
     * @param triW The 3D object ([Object3D]) whose vertices are used to determine the look point
     * and screen width.
     * @param w The width of the screen in pixels. (Currently unused in this specific overload)
     * @param h The height of the screen in pixels. (Currently unused in this specific overload)
     */
    private fun calcLook(triW: Object3D, w: Int, h: Int) {
        var minx = Float.MAX_VALUE
        var miny = Float.MAX_VALUE
        var minz = Float.MAX_VALUE
        var maxx = -Float.MAX_VALUE
        var maxy = -Float.MAX_VALUE
        var maxz = -Float.MAX_VALUE
        var i = 0
        while (i < triW.vert.size) {
            maxx = Math.max(triW.vert[i], maxx)
            minx = Math.min(triW.vert[i], minx)
            maxy = Math.max(triW.vert[i + 1], maxy)
            miny = Math.min(triW.vert[i + 1], miny)
            maxz = Math.max(triW.vert[i + 2], maxz)
            minz = Math.min(triW.vert[i + 2], minz)
            i += 3
        }
        lookPoint = doubleArrayOf(
            ((maxx + minx) / 2).toDouble(),
            ((maxy + miny) / 2).toDouble(),
            ((maxz + minz) / 2).toDouble()
        )
        screenWidth = Math.max(maxx - minx, Math.max(maxy - miny, maxz - minz)).toDouble()
    }

    /**
     * Positions the camera to look at a 3D object from a specified direction.
     * This method calculates the camera's eye point and up vector based on the object's
     * bounding box, the desired viewing direction, and screen dimensions.
     *
     * The process involves:
     *  1. Calculating the object's look point (center) and screen width using [calcLook].
     *  2. Determining the camera's direction vector (dx, dy, dz) from the [dir] character.
     *  The [dir] character encodes the direction components in its bits.
     *  3. Calculating the [eyePoint] by offsetting the [lookPoint] along the direction vector,
     *  scaled by twice the [screenWidth]. This places the camera at a distance from the object.
     *  4. Defining the view vector (`zv`) as the negative of the direction vector.
     *  5. Defining an initial right vector (`rv`) based on the direction vector.
     *  If `dx` is zero, `rv` is (1,0,0); otherwise, it's (0,1,0). This provides an initial
     *  orthogonal vector.
     *  6. Normalizing all calculated vectors (`zv`, `rv`, `up`).
     *  7. Calculating the [upVector] by taking the cross product of `zv` and `rv`, then `zv` and
     *  the new `up`, and finally `zv` and the new `rv`. This ensures the [upVector] is
     *  orthogonal to the view direction and the right vector.
     *  8. Setting the [mScreenDim] with the provided width and height.
     *  9. Calling [calcMatrix] to update the view matrix with the new camera parameters.
     *
     * @param dir A character encoding the viewing direction:
     * ```
     *  - Bits 0-3: dz component 0x001 for up (dz = +1), 0x002 for down (dz = -1)
     *  - Bits 4-7: dx component 0x010 for right (dz = +1), 0x020 for left (dx = -1)
     *  - Bits 8-11: dy component 0x100 for forward (dy = +1), 0x200 for behind (dy = -1)
     * ```
     * @param tri The 3D object ([Object3D]) whose vertices are used to determine the look point
     * and screen width.
     * @param voxelDim A [FloatArray] representing the dimensions of a voxel in x, y, and z
     * directions. These dimensions are used to scale the object's coordinates.
     * @param w The width of the screen in pixels.
     * @param h The height of the screen in pixels.
     */
    fun look(dir: Char, tri: Object3D, voxelDim: FloatArray?, w: Int, h: Int) {
        calcLook(triW = tri, w = w, h = h)
        var dx: Int = dir.code shr 4 and 0xF
        var dy: Int = dir.code shr 8 and 0xF
        var dz: Int = dir.code shr 0 and 0xF
        if (dx > 1) {
            dx = -1
        }
        if (dy > 1) {
            dy = -1
        }
        if (dz > 1) {
            dz = -1
        }
        eyePoint =  doubleArrayOf(
            lookPoint!![0] + 2 * screenWidth * dx,
            lookPoint!![1] + 2 * screenWidth * dy,
            lookPoint!![2] + 2 * screenWidth * dz
        )
        val zv = doubleArrayOf(-dx.toDouble(), -dy.toDouble(), -dz.toDouble())
        val rv = doubleArrayOf(if (dx == 0) 1.0 else 0.0, if (dx == 0) 0.0 else 1.0, 0.0)
        val up = DoubleArray(size = 3)
        VectorUtil.norm(a = zv)
        VectorUtil.norm(a = rv)
        VectorUtil.cross(a = zv, b = rv, out = up)
        VectorUtil.cross(a = zv, b = up, out = rv)
        VectorUtil.cross(a = zv, b = rv, out = up)
        upVector = up
        mScreenDim = intArrayOf(w, h)
        calcMatrix()
    }

    /**
     * Positions the camera to look at a 3D object from a default diagonal viewpoint.
     * This method calculates the camera's eye point and up vector based on the object's
     * bounding box, screen dimensions, and a fixed diagonal viewing direction.
     *
     * The process involves:
     *  1. Calculating the object's look point (center) and screen width using [calcLook],
     *  considering the [FloatArray] parameter [voxelDim] for scaling.
     *  2. Setting the [eyePoint] by offsetting the [lookPoint] along a fixed diagonal
     *  direction (1, 1, 1), scaled by the [screenWidth]. This places the camera at a
     *  distance from the object, looking towards it from a general diagonal perspective.
     *  3. Defining a fixed view vector (`zv`) as (-1, -1, -1), representing the direction
     *  from the [eyePoint] towards the [lookPoint].
     *  4. Defining an initial right vector (`rv`) as (1, 1, 0).
     *  5. Normalizing both `zv` and `rv`.
     *  6. Calculating the [upVector] by performing a series of cross products:
     *      - `up = zv x rv`
     *      - `rv = zv x up` (new rv is orthogonal to zv and the first up)
     *      - `up = zv x rv` (final up is orthogonal to zv and the new rv)
     *      This ensures the [upVector] is orthogonal to the view direction and the right vector,
     *      establishing a stable camera orientation.
     *  7. Setting the [mScreenDim] with the provided width and height.
     *  8. Calling [calcMatrix] to update the view matrix with the new camera parameters.
     *
     * @param tri The 3D object ([Object3D]) whose vertices are used to determine the look point
     * and screen width.
     * @param voxelDim A [FloatArray] representing the dimensions of a voxel in x, y, and z
     * directions. These dimensions are used to scale the object's coordinates when
     * calculating the `lookPoint` and `screenWidth`.
     * @param w The width of the screen in pixels.
     * @param h The height of the screen in pixels.
     */
    fun lookAt(tri: Object3D, voxelDim: FloatArray, w: Int, h: Int) {
        calcLook(tri = tri, voxelDim = voxelDim, w = w, h = h)
        eyePoint = doubleArrayOf(
            lookPoint!![0] + screenWidth,
            lookPoint!![1] + screenWidth,
            lookPoint!![2] + screenWidth
        )
        val zv = doubleArrayOf(-1.0, -1.0, -1.0)
        val rv = doubleArrayOf(1.0, 1.0, 0.0)
        val up = DoubleArray(size = 3)
        VectorUtil.norm(a = zv)
        VectorUtil.norm(a = rv)
        VectorUtil.cross(a = zv, b = rv, out = up)
        VectorUtil.cross(a = zv, b = up, out = rv)
        VectorUtil.cross(a = zv, b = rv, out = up)
        upVector = up
        mScreenDim = intArrayOf(w, h)
        calcMatrix()
    }

    /**
     * The starting x-coordinate of a touch gesture.
     * This value is typically set when a touch down event occurs (e.g., in [trackBallDown])
     * and is used to calculate the movement delta in subsequent touch move events
     * (e.g., in [trackBallMove]). It represents the initial horizontal position of the touch
     * on the screen.
     */
    var mStartx: Float = 0f

    /**
     * Stores the y-coordinate of the initial touch point when a trackball drag gesture begins.
     * This is used in conjunction with [mStartx] to calculate the drag distance and direction.
     * The value is updated in the [trackBallDown] method when a touch event starts.
     */
    var mStarty: Float = 0f

    /**
     * Stores the x-coordinate of the initial touch point when a pan gesture begins.
     * This value is used in conjunction with [mPanStartY] to calculate the displacement
     * of the pan gesture. It is set in [panDown] and updated in [panMove].
     * If its value is `Float.NaN`, it indicates that no pan gesture is currently active.
     */
    var mPanStartX: Float = Float.NaN

    /**
     * Stores the Y-coordinate of the initial touch point when a pan gesture begins.
     * This value is used in conjunction with [mPanStartX] and the current touch
     * coordinates to calculate the panning movement. It is set to [Float.NaN]
     * when no pan gesture is active.
     */
    var mPanStartY: Float = Float.NaN

    /**
     * Stores a copy of the view matrix at the beginning of a trackball rotation.
     * This is used as a reference point to calculate the rotation amount during
     * a [trackBallMove] operation. It's initialized in [trackBallDown] and
     * represents the camera's orientation before the user starts dragging.
     */
    var mStartMatrix: Matrix? = null

    /**
     * Stores the 3D vector representing the starting point of a trackball rotation.
     * This vector is calculated from the initial touch coordinates in [trackBallDown]
     * using the [ballToVec] method. It's used as a reference along with [mMoveToV]
     * to determine the axis and angle of rotation during a [trackBallMove] operation.
     * The values are in a normalized 3D space relative to a virtual trackball.
     */
    var mStartV: DoubleArray = DoubleArray(3)

    /**
     * Stores the 3D vector representing the current touch point during a trackball rotation.
     * This vector is calculated from the 2D touch coordinates (x, y) on the screen using
     * the [ballToVec] method. It's updated in [trackBallMove] as the user drags their
     * finger. This vector, along with [mStartV], is used to determine the axis and angle
     * of rotation for the trackball effect.
     */
    var mMoveToV: DoubleArray = DoubleArray(3)

    /**
     * Stores a copy of the camera's eye point at the beginning of a trackball rotation.
     * This is used as a reference point to calculate the new eye point after rotation
     * during a [trackBallMove] operation. It's initialized in [trackBallDown] and
     * represents the camera's position before the user starts dragging.
     * The array holds the x, y, and z coordinates of the eye point.
     */
    lateinit var mStartEyePoint: DoubleArray

    /**
     * Stores a copy of the camera's up vector at the beginning of a trackball rotation.
     * This is used in conjunction with [mStartEyePoint] and [mStartMatrix]
     * to correctly reorient the camera during a [trackBallMove] operation.
     * It's initialized in [trackBallDown] and represents the "up" direction
     * before the user starts dragging.
     */
    lateinit var mStartUpVector: DoubleArray

    /**
     * A [Quaternion] used to represent and apply rotations, particularly in trackball
     * operations. This quaternion is updated during [trackBallMove] to reflect the
     * rotation caused by the user's drag gesture. It is then used to rotate the
     * [eyePoint] and [upVector] to achieve the trackball rotation effect.
     * The quaternion is initialized to an identity rotation (0,0,0,0) which
     * typically corresponds to no rotation or a scalar part of 1 depending on convention.
     */
    var mQ: Quaternion = Quaternion(x0 = 0.0, x1 = 0.0, x2 = 0.0, x3 = 0.0)

    /**
     * Handles the "up" event of a trackball gesture (when the user lifts their finger).
     * This function is typically called when a touch interaction, interpreted as a
     * trackball rotation, ends.
     * Currently, this function is empty and does not perform any actions. It serves as a
     * placeholder for potential future functionality related to finalizing a trackball
     * rotation or cleaning up resources used during the gesture.
     *
     * @param x The x-coordinate of the touch release point on the screen.
     * @param y The y-coordinate of the touch release point on the screen.
     */
    fun trackBallUP(x: Float, y: Float) {}

    /**
     * Initializes the trackball rotation operation when a touch down event occurs.
     * This method captures the initial state required to calculate the rotation
     * during subsequent touch move events.
     *
     * The process involves:
     *  1. Storing the initial touch coordinates (x, y) in [mStartx] and [mStarty].
     *  2. Converting the initial 2D touch coordinates to a 3D vector on a virtual
     *  trackball using [ballToVec] and storing it in [mStartV].
     *  3. Creating copies of the current [eyePoint] and [upVector] and storing them
     *  in [mStartEyePoint] and [mStartUpVector] respectively. This preserves the
     *  camera's initial position and orientation.
     *  4. Creating a copy of the current view matrix and storing it in [mStartMatrix].
     *  5. Configuring [mStartMatrix] to represent only the rotation component of the
     *  current view, which will be used to transform the rotation axis.
     *
     * @param x The x-coordinate of the touch down event on the screen.
     * @param y The y-coordinate of the touch down event on the screen.
     */
    fun trackBallDown(x: Float, y: Float) {
        mStartx = x
        mStarty = y
        ballToVec(x = x, y = y, v = mStartV)
        mStartEyePoint = Arrays.copyOf(eyePoint!!, m.size)
        mStartUpVector = Arrays.copyOf(upVector!!, m.size)
        mStartMatrix = Matrix(matrix = this)
        mStartMatrix!!.makeRotation()
    }

    /**
     * Updates the camera's orientation and position based on a trackball drag gesture.
     * This function is called when the user moves their finger on the screen after initiating
     * a trackball rotation with [trackBallDown]. It calculates the rotation based on the
     * difference between the current touch point and the starting touch point, then applies
     * this rotation to the camera's eye point and up vector.
     *
     * The process involves:
     *  1. Checking if the touch position has actually changed. If not, the function returns.
     *  2. Converting the current 2D touch coordinates (x, y) to a 3D vector on the virtual
     *  trackball using [ballToVec], storing the result in [mMoveToV].
     *  3. Calculating the `angle` of rotation between the starting trackball vector ([mStartV])
     *  and the current trackball vector ([mMoveToV]) using [Quaternion.calcAngle].
     *  4. Calculating the `axis` of rotation between [mStartV] and [mMoveToV] using
     *  [Quaternion.calcAxis].
     *  5. Transforming the calculated rotation `axis` by the initial camera orientation
     *  ([mStartMatrix]) to ensure the rotation is applied in the correct coordinate space.
     *  6. Updating the internal quaternion ([mQ]) with the calculated `angle` and transformed `axis`.
     *  7. Calculating the initial vector from the look point to the eye point.
     *  8. Rotating this vector using the quaternion [mQ] to get the new direction from the
     *  look point to the eye point [eyePoint].
     *  9. Rotating the initial up vector ([mStartUpVector]) by the quaternion [mQ] to get the
     *  new up vector.
     *  10. Calculating the new [eyePoint] by subtracting the rotated direction vector from the
     *  [lookPoint].
     *  11. Calling [calcMatrix] to update the view matrix with the new camera parameters.
     *
     * @param x The current x-coordinate of the touch on the screen.
     * @param y The current y-coordinate of the touch on the screen.
     */
    fun trackBallMove(x: Float, y: Float) {
        if (mStartx == x && mStarty == y) {
            return
        }
        ballToVec(x = x, y = y, v = mMoveToV)
        val angle: Double = Quaternion.calcAngle(v1 = mStartV, v2 = mMoveToV)
        var axis: DoubleArray? = Quaternion.calcAxis(v1 = mStartV, v2 = mMoveToV)
        axis = mStartMatrix!!.vecmult(src = axis)
        mQ.set(angle = angle, axis = axis) // was: `mQ[angle] = axis` which is obscure to say the least
        VectorUtil.sub(a = lookPoint, b = mStartEyePoint, out = eyePoint)
        eyePoint = mQ.rotateVec(v = eyePoint)
        upVector = mQ.rotateVec(v = mStartUpVector)
        VectorUtil.sub(a = lookPoint, b = eyePoint, out = eyePoint)
        calcMatrix()
    }

    /**
     * Initializes a pan gesture by storing the starting screen coordinates.
     * This function is called when a touch "down" event occurs and is interpreted
     * as the beginning of a pan operation. The provided x and y coordinates
     * are stored in [mPanStartX] and [mPanStartY] respectively. These values
     * will be used in subsequent [panMove] calls to calculate the pan displacement.
     *
     * @param x The x-coordinate of the touch down event on the screen.
     * @param y The y-coordinate of the touch down event on the screen.
     */
    fun panDown(x: Float, y: Float) {
        mPanStartX = x
        mPanStartY = y
    }

    /**
     * Updates the camera's position and look point based on a pan gesture.
     * This function is called when the user moves their finger on the screen after initiating
     * a pan operation with [panDown]. It calculates the displacement based on the
     * difference between the current touch point and the starting touch point, then applies
     * this displacement to both the camera's [eyePoint] and [lookPoint].
     *
     * The process involves:
     *  1. Calculating a scale factor based on [screenWidth] and the screen width in pixels.
     *  This converts screen space movement to world space movement.
     *  2. If [mPanStartX] is `Float.NaN` (meaning a pan hasn't started or was interrupted),
     *  it initializes [mPanStartX] and [mPanStartY] with the current touch coordinates (x, y).
     *  3. Calculating the change in x (`dx`) and y (`dy`) in world units by scaling the
     *  difference between the current touch coordinates and the starting pan coordinates.
     *  4. Calculating the right vector of the camera:
     *      - Get the view direction vector by subtracting [lookPoint] from [eyePoint].
     *      - Normalize the view direction vector.
     *      - Calculate the cross product of the view direction and the [upVector] to get the
     *      right vector. This vector is stored in [mTmp1].
     *  5. Moving the [eyePoint] and [lookPoint] horizontally:
     *      - Add the scaled right vector ([mTmp1] scaled by `dx`) to the current [eyePoint].
     *      - Add the scaled right vector ([mTmp1] scaled by `dx`) to the current [lookPoint].
     *  6. Moving the [eyePoint] and [lookPoint] vertically:
     *      - Add the scaled [upVector] (scaled by `dy`) to the current [eyePoint].
     *      - Add the scaled [upVector] (scaled by `dy`) to the current [lookPoint].
     *  7. Updating [mPanStartY] and [mPanStartX] to the current touch coordinates (x, y)
     *  for the next pan move calculation.
     *  8. Calling [calcMatrix] to update the view matrix with the new camera parameters.
     *
     * @param x The current x-coordinate of the touch on the screen.
     * @param y The current y-coordinate of the touch on the screen.
     */
    fun panMove(x: Float, y: Float) {
        val scale: Double = screenWidth / mScreenDim!![0]
        if (java.lang.Float.isNaN(mPanStartX)) {
            mPanStartX = x
            mPanStartY = y
        }
        val dx: Double = scale * (x - mPanStartX)
        val dy: Double = scale * (y - mPanStartY)
        VectorUtil.sub(a = eyePoint, b = lookPoint, out = mTmp1)
        VectorUtil.normalize(a = mTmp1)
        VectorUtil.cross(a = mTmp1, b = upVector, out = mTmp1)
        VectorUtil.madd(a = mTmp1, x = dx, b = eyePoint, out = eyePoint)
        VectorUtil.madd(a = mTmp1, x = dx, b = lookPoint, out = lookPoint)
        VectorUtil.madd(a = upVector, x = dy, b = eyePoint, out = eyePoint)
        VectorUtil.madd(a = upVector, x = dy, b = lookPoint, out = lookPoint)
        mPanStartY = y
        mPanStartX = x
        calcMatrix()
    }

    /**
     * Finalizes a pan gesture by resetting the starting pan coordinates.
     * This function is typically called when a touch "up" event occurs, signaling
     * the end of a pan operation. It sets [mPanStartX] and [mPanStartY] to
     * [Float.NaN], indicating that no pan gesture is currently active.
     * This ensures that the next touch down event, if interpreted as a pan,
     * will correctly initialize its starting point.
     */
    fun panUP() {
        mPanStartX = Float.NaN
        mPanStartY = Float.NaN
    }

    /**
     * Converts 2D screen coordinates (x, y) to a 3D vector on a virtual trackball.
     * This function is used to map touch input on the screen to a point on a sphere,
     * which is then used to calculate rotations for trackball-like camera controls.
     *
     * The process involves:
     *  1. Defining a `ballRadius` as 40% of the smaller screen dimension. This defines
     *  the size of the virtual trackball.
     *  2. Calculating the center of the screen (`cx`, `cy`).
     *  3. Calculating the displacement (`dx`, `dy`) of the input coordinates ([x], [y])
     *  from the screen center, normalized by the `ballRadius`. `dx` is inverted
     *  (center - [x]) which might be specific to the desired trackball behavior.
     *  4. Calculating the squared distance (`scale`) of the normalized displacement
     *  from the center of the trackball's projection on the screen.
     *  5. If `scale` is greater than 1 (meaning the touch point is outside the projected
     *  circle of the trackball), the `dx` and `dy` components are scaled down so that
     *  the point lies on the edge of the circle. This ensures that points outside the
     *  trackball's radius are projected onto its surface.
     *  6. Calculating the z-component (`dz`) of the vector on the sphere using the
     *  Pythagorean theorem: `dz = sqrt(1 - (dx^2 + dy^2))`. The absolute value is
     *  taken inside the square root to handle potential floating-point inaccuracies
     *  that might result in a negative value for points very close to the edge.
     *  7. Storing the calculated `dx`, `dy`, and `dz` into the output vector [v].
     *  8. Normalizing the resulting vector [v] to ensure it's a unit vector.
     *
     * @param x The x-coordinate on the screen.
     * @param y The y-coordinate on the screen.
     * @param v A [DoubleArray] of at least 3 elements where the resulting 3D vector
     * will be stored.
     */
    fun ballToVec(x: Float, y: Float, v: DoubleArray) {
        val ballRadius: Float = Math.min(mScreenDim!![0], mScreenDim!![1]) * .4f
        val cx: Double = mScreenDim!![0] / 2.0
        val cy: Double = mScreenDim!![1] / 2.0
        var dx: Double = (cx - x) / ballRadius
        var dy: Double = (cy - y) / ballRadius
        var scale: Double = dx * dx + dy * dy
        if (scale > 1) {
            scale = Math.sqrt(scale)
            dx /= scale
            dy /= scale
        }
        val dz: Double = Math.sqrt(Math.abs(1 - (dx * dx + dy * dy)))
        v[0] = dx
        v[1] = dy
        v[2] = dz
        VectorUtil.normalize(a = v)
    }

    companion object {
        /**
         * Represents a camera viewing direction where the positive Z-axis is considered "up".
         * This constant is typically used with the [look] method to specify the camera's
         * orientation. The character value encodes the direction components, where the
         * lowest nibble (bits 0-3) being 1 signifies a positive Z direction.
         * The exact interpretation of "up" depends on the coordinate system conventions
         * being used.
         */
        const val UP_AT: Char = 0x001.toChar()

        /**
         * Represents a camera viewing direction where the negative Z-axis is considered "down".
         * This constant is typically used with the [look] method to specify the camera's
         * orientation. The character value encodes the direction components, where the
         * lowest nibble (bits 0-3) being 2 signifies a negative Z direction.
         * The exact interpretation of "down" depends on the coordinate system conventions
         * being used.
         */
        const val DOWN_AT: Char = 0x002.toChar()

        /**
         * Represents a camera viewing direction where the positive X-axis points to the "right".
         * This constant is typically used with the [look] method to specify the camera's
         * orientation. The character value encodes the direction components, where the
         * second nibble (bits 4-7) being 1 signifies a positive X direction, conventionally
         * associated with "right" in many 3D coordinate systems.
         */
        const val RIGHT_AT: Char = 0x010.toChar()

        /**
         * Represents a camera viewing direction where the negative X-axis points to the "left".
         * This constant is typically used with the [look] method to specify the camera's
         * orientation. The character value encodes the direction components, where the
         * second nibble (bits 4-7) being 2 signifies a negative X direction, conventionally
         * associated with "left" in many 3D coordinate systems.
         */
        const val LEFT_AT: Char = 0x020.toChar()

        /**
         * Represents a camera viewing direction where the positive Y-axis points "forward".
         * This constant is typically used with the [look] method to specify the camera's
         * orientation. The character value encodes the direction components, where the
         * third nibble (bits 8-11) being 1 signifies a positive Y direction, conventionally
         * associated with "forward" or "into the screen" in some 3D coordinate systems.
         */
        const val FORWARD_AT: Char = 0x100.toChar()

        /**
         * Represents a camera viewing direction where the negative Y-axis points "behind".
         * This constant is typically used with the [look] method to specify the camera's
         * orientation. The character value encodes the direction components, where the
         * third nibble (bits 8-11) being 2 signifies a negative Y direction, conventionally
         * associated with "behind" or "out of the screen" in some 3D coordinate systems.
         */
        const val BEHIND_AT: Char = 0x200.toChar()

        /**
         * Formats a double value into a string with a fixed width of 8 characters.
         * The double is formatted to three decimal places using [df]. The resulting
         * string is then padded with leading spaces if necessary to ensure it is
         * exactly 8 characters long. If the formatted string is longer than 8 characters,
         * it is truncated from the left to fit.
         *
         * This is primarily used for printing matrix or vector components in a consistent,
         * aligned format for debugging or display purposes.
         *
         * @param d The double value to format.
         * @return A string representation of the double, padded or truncated to 8 characters.
         */
        private fun toStr(d: Double): String {
            val s = "       " + df.format(d)
            return s.substring(startIndex = s.length - 8)
        }

        /**
         * Converts a [DoubleArray] to a string representation.
         * Each element of the array is converted to a string using the [toStr] method
         * for single [Double] values, and they are concatenated within square brackets.
         * Example: `[ 1.000 2.500 -3.142]`
         *
         * @param d The [DoubleArray] to convert. Can be null, but will result in a
         * NullPointerException if it is, as it's dereferenced with `!!`.
         * @return A string representation of the [DoubleArray].
         */
        private fun toStr(d: DoubleArray?): String {
            var s = "["
            for (i in d!!.indices) {
                s += toStr(d = d[i])
            }
            return "$s]"
        }

        /**
         * A [DecimalFormat] object used for formatting double values to strings
         * with a specific pattern ("##0.000"). This pattern ensures that numbers
         * are displayed with at least one digit before the decimal point (even if it's zero)
         * and exactly three digits after the decimal point, padding with zeros if necessary.
         * For example, 1.2 would be formatted as "1.200", and 0.5 as "0.500".
         * This is primarily used in the [toStr] methods for consistent formatting
         * of numerical output, often for debugging or display purposes where alignment
         * and precision are important.
         */
        private val df = DecimalFormat("##0.000")

        /**
         * Calculates the cross product of two 3D vectors [a] and [b], storing the result in [out].
         * This function is a simple wrapper around [VectorUtil.cross].
         * The cross product of two vectors results in a new vector that is perpendicular to both
         * input vectors. The direction of the resulting vector is determined by the right-hand rule.
         *
         * This function is often used to calculate orthogonal vectors, such as the "right" vector
         * in a camera system (cross product of view direction and up vector).
         *
         * @param a The first 3D vector as a [DoubleArray] (x, y, z).
         * @param b The second 3D vector as a [DoubleArray] (x, y, z). Can be null, but will likely
         * cause an issue in the underlying [VectorUtil.cross] if it is.
         * @param out A [DoubleArray] of at least 3 elements where the resulting cross product
         * vector (x, y, z) will be stored. Can be null, in which case the behavior
         * depends on the implementation of [VectorUtil.cross].
         */
        fun calcRight(a: DoubleArray, b: DoubleArray?, out: DoubleArray?) {
            VectorUtil.cross(a = a, b = b, out = out)
        }

        /**
         * Main function for testing the [ViewMatrix] class.
         * This function creates a [ViewMatrix], sets up a simple camera configuration,
         * and calculates the view matrix. It serves as a basic example of how to
         * initialize and use the [ViewMatrix].
         *
         * The camera is positioned at (-10, 0, 0), looking at the origin (0, 0, 0),
         * with the up vector pointing along the positive Z-axis (0, 0, 1).
         * The screen width in world coordinates is set to 10.0, and the screen
         * dimensions in pixels are set to 512x512.
         * Finally, it calls [calcMatrix] to compute the transformation matrix.
         *
         * @param args Command line arguments (not used).
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val up = doubleArrayOf(0.0, 0.0, 1.0)
            val look = doubleArrayOf(0.0, 0.0, 0.0)
            val eye = doubleArrayOf(-10.0, 0.0, 0.0)
            val v = ViewMatrix()
            v.eyePoint = eye
            v.lookPoint = look
            v.upVector = up
            v.screenWidth = 10.0
            v.setScreenDim(x = 512, y = 512)
            v.calcMatrix()
        }
    }
}