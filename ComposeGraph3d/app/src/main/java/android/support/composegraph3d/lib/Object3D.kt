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
@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
    "ReplaceNotNullAssertionWithElvisReturn",
    "ReplaceJavaStaticMethodWithKotlinAnalog",
    "MemberVisibilityCanBePrivate",
    "RedundantSuppression"
)

package android.support.composegraph3d.lib

import android.support.composegraph3d.lib.Scene3D.Companion.trianglePhong

/**
 * This represents 3d Object in this system.
 */
open class Object3D {
    /**
     * A FloatArray that stores the vertices of the 3D object.
     *
     * Each vertex is represented by three consecutive float values for its x, y, and z coordinates.
     * For example, the first vertex is at indices 0, 1, 2, the second at 3, 4, 5, and so on.
     */
    lateinit var vert: FloatArray

    /**
     * A FloatArray that stores the normal vectors for each vertex of the 3D object.
     *
     * Normal vectors are used for lighting calculations to determine how a surface reflects light.
     * Each normal vector is represented by three consecutive float values for its x, y, and z
     * components, corresponding to a vertex in the `vert` array. For example, the normal for the
     * first vertex is at indices 0, 1, 2, the second at 3, 4, 5, and so on.
     */
    lateinit var normal: FloatArray

    /**
     * An IntArray that defines the faces of the 3D object as a list of triangles.
     *
     * Each triangle is represented by three consecutive integer values, which are indices
     * into the `vert` and `normal` arrays. For example, the first triangle is formed by the
     * vertices at `vert[index[0]]`, `vert[index[1]]`, and `vert[index[2]]`.
     */
    lateinit var index: IntArray

    /**
     * A FloatArray that stores the transformed vertices of the 3D object.
     *
     * This array holds the vertex data from `vert` after a transformation matrix has been applied,
     * typically moving the vertices from object space to world or screen space. This is used for
     * rendering. Like [vert], each vertex is represented by three consecutive float values (x, y, z).
     */
    lateinit var tVert: FloatArray

    /**
     * The minimum x-coordinate of the object's bounding box. This is calculated from the
     * vertices in the [vert] array and represents the extent of the object along the x-axis.
     */
    protected var mMinX: Float = 0f

    /**
     * The maximum x-coordinate of the object's bounding box. This is calculated from the
     * vertices in the [vert] array and represents the extent of the object along the x-axis.
     */
    protected var mMaxX: Float = 0f

    /**
     * The minimum y-coordinate of the object's bounding box. This is calculated from the
     * vertices in the [vert] array and represents the extent of the object along the y-axis.
     */
    protected var mMinY: Float = 0f

    /**
     * The maximum y-coordinate of the object's bounding box. This is calculated from the
     * vertices in the [vert] array and represents the extent of the object along the y-axis.
     */
    protected var mMaxY: Float = 0f

    /**
     * The minimum z-coordinate of the object's bounding box. This is calculated from the
     * vertices in the [vert] array and represents the extent of the object along the z-axis.
     */
    var mMinZ: Float = 0f

    /**
     * The maximum z-coordinate of the object's bounding box. This is calculated from the
     * vertices in the [vert] array and represents the extent of the object along the z-axis.
     */
    var mMaxZ: Float = 0f

// bounds in x,y & z

    /**
     * An integer that specifies the rendering mode for the object. The [render] function
     * uses this type to determine which rasterization method to call.
     *
     * Supported values and their corresponding rendering styles:
     *  - 0: `rasterHeight` - Colors the object based on the height (z-coordinate) of its vertices.
     *  - 1: `rasterOutline` - Renders the object as a filled shape with a wireframe outline.
     *  - 2: `rasterColor` - Renders the object with colors based on lighting (defuse) and height (hue).
     *  - 3: `rasterLines` - Renders the object as a wireframe with filled triangles colored by height.
     *  - 4: `rasterPhong` - (Default) Renders the object with Phong shading for a smooth, realistic look.
     *
     * @see render
     */
    var type: Int = 4

    /**
     * The ambient light component for the object's material.
     *
     * This value determines the base level of brightness for the object, representing light that
     * is scattered in the scene and illuminates the object indirectly, independent of the main
     * light source's direction. It is combined with the diffuse lighting component to calculate
     * the final color of a surface.
     *
     * A value of 0.0 means no ambient light, while 1.0 represents full ambient light.
     * The default is 0.3f.
     *
     * @see mDefuse
     * @see rasterColor
     * @see rasterPhong
     */
    var mAmbient: Float = 0.3f

    /**
     * The diffuse light component for the object's material.
     *
     * This value represents how much the object reflects light from a direct light source. It is
     * influenced by the angle between the surface normal and the light direction. A surface facing
     * the light source directly will be brighter, while a surface at a steep angle will be darker.
     * This property acts as a multiplier for the calculated diffuse reflection.
     *
     * A value of 0.0 means no diffuse reflection, while 1.0 represents full diffuse reflection.
     * The default is 0.7f. It is combined with the ambient light component to determine the final
     * surface brightness.
     *
     * @see mAmbient
     * @see rasterColor
     * @see rasterPhong
     */
    var mDefuse: Float = 0.7f

    /**
     * The saturation component for the object's material color.
     *
     * This value controls the intensity of the color when rendering, particularly when using
     * lighting models like Phong shading. It is used in conjunction with the hue (derived from
     * vertex height) and brightness (calculated from [mAmbient] and [mDefuse] components) to
     * determine the final color of a pixel.
     *
     * A value of 0.0 results in a grayscale (desaturated) appearance, while 1.0 represents full,
     * vibrant color. The default is 0.6f.
     *
     * @see rasterPhong
     * @see Scene3D.hsvToRgb
     */
    var mSaturation: Float = 0.6f

    /**
     * Initializes the vertex, transformed vertex, and normal arrays for the 3D object.
     *
     * This function allocates memory for the arrays that store vertex positions, transformed vertex
     * positions, and vertex normals. Each vertex and its corresponding normal requires three float
     * values (x, y, z), so the total size of each array will be `n * 3`.
     *
     * @param n The number of vertices to allocate space for.
     */
    fun makeVert(n: Int) {
        vert = FloatArray(size = n * 3)
        tVert = FloatArray(size = n * 3)
        normal = FloatArray(size = n * 3)
    }

    /**
     * Initializes the index array for the 3D object.
     *
     * This function allocates memory for the [index] array, which defines the object's faces
     * (triangles). Each triangle requires three integer indices to reference vertices in the
     * [vert] array, so the total size of the array will be `n * 3`.
     *
     * @param n The number of triangles to allocate space for.
     */
    fun makeIndexes(n: Int) {
        index = IntArray(size = n * 3)
    }

    /**
     * Transforms the object's vertices using the provided matrix.
     *
     * This function iterates through each vertex in the [vert] array, applies the transformation
     * defined by the [Matrix] parameter [m], and stores the result in the [tVert] array. This is
     * typically used to move the object from its local coordinate system (object space) to the
     * scene's coordinate system (world space) or the screen's coordinate system (screen space)
     * for rendering.
     *
     * @param m The transformation matrix to apply to each vertex. This matrix should not be null.
     */
    fun transform(m: Matrix?) {
        var i = 0
        while (i < vert.size) {
            m!!.mult3(src = vert, off1 = i, dest = tVert, off2 = i)
            i += 3
        }
    }

    /**
     * Renders the 3D object to a 2D image buffer based on the specified rendering `type`.
     *
     * This function acts as a dispatcher, calling the appropriate rasterization method
     * (e.g., `rasterPhong`, `rasterHeight`) to draw the object's triangles. The chosen
     * method determines the visual style, such as flat shading, wireframe, or smooth
     * Phong shading.
     *
     * @param s The [Scene3D] object containing scene information like lighting and camera settings.
     * @param zbuff The z-buffer used for depth testing to ensure correct occlusion of objects.
     * @param img The integer array representing the image buffer where the object will be rendered.
     * @param width The width of the image buffer.
     * @param height The height of the image buffer.
     * @see type
     */
    open fun render(s: Scene3D, zbuff: FloatArray, img: IntArray, width: Int, height: Int) {
        when (type) {
            0 -> rasterHeight(s = s, zbuff = zbuff, img = img, w = width, h = height)
            1 -> rasterOutline(s = s, zBuff = zbuff, img = img, w = width, h = height)
            2 -> rasterColor(s = s, zbuff = zbuff, img = img, w = width, h = height)
            3 -> rasterLines(s = s, zbuff = zbuff, img = img, w = width, h = height)
            4 -> rasterPhong(
                mSurface = this,
                s = s,
                zbuff = zbuff,
                img = img,
                w = width,
                h = height
            )
        }
    }

    /**
     * Renders the object's triangles filled with a color based on their height, and then draws
     * a wireframe outline over them.
     *
     * This method iterates through each triangle of the object. For each triangle, it first
     * calculates an average height from its vertices. This height is used to determine a fill
     * color (a mix of blue and green, where higher vertices are greener).
     *
     * After filling the triangle, it draws the edges of the triangle using the scene's line color.
     * The lines are drawn slightly in front of the filled triangle (by subtracting 0.01f from the
     * z-coordinate) to ensure they are visible and not obscured by the fill.
     *
     * @param s The [Scene3D] object containing scene information, such as the line color.
     * @param zbuff The z-buffer used for depth testing.
     * @param img The integer array representing the image buffer to draw into.
     * @param w The width of the image buffer.
     * @param h The height of the image buffer.
     */
    private fun rasterLines(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            val height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            val `val` = (255 * Math.abs(height)).toInt()
            Scene3D.triangle(
                zbuff = zbuff,
                img = img,
                color = 0x10001 * `val` + 0x100 * (255 - `val`),
                w = w,
                h = h,
                fx3 = tVert[p1],
                fy3 = tVert[p1 + 1],
                fz3 = tVert[p1 + 2],
                fx2 = tVert[p2],
                fy2 = tVert[p2 + 1],
                fz2 = tVert[p2 + 2],
                fx1 = tVert[p3],
                fy1 = tVert[p3 + 1],
                fz1 = tVert[p3 + 2]
            )
            Scene3D.drawline(
                zbuff = zbuff, img = img, color = s.lineColor, w = w, h = h,
                fx1 = tVert[p1], fy1 = tVert[p1 + 1], fz1 = tVert[p1 + 2] - 0.01f,
                fx2 = tVert[p2], fy2 = tVert[p2 + 1], fz2 = tVert[p2 + 2] - 0.01f
            )
            Scene3D.drawline(
                zbuff = zbuff, img = img, color = s.lineColor, w = w, h = h,
                fx1 = tVert[p1], fy1 = tVert[p1 + 1], fz1 = tVert[p1 + 2] - 0.01f,
                fx2 = tVert[p3], fy2 = tVert[p3 + 1], fz2 = tVert[p3 + 2] - 0.01f
            )
            i += 3
        }
    }

    /**
     * Renders the object by coloring its triangles based on their height (z-coordinate).
     *
     * This method iterates through each triangle of the object. For each triangle, it calculates
     * the average height of its three vertices. This height is then normalized to a 0-1 range using
     * the object's minimum and maximum Z bounds (`mMinZ`, `mMaxZ`).
     *
     * The normalized height is used to determine a color from a hue-saturation-value (HSV) model:
     * - **Hue** is set to the normalized height, creating a color gradient along the z-axis.
     * - **Saturation** is calculated to be highest at the top and bottom and lowest in the middle.
     * - **Value (Brightness)** is based on the square root of the height, making lower areas darker.
     *
     * The resulting color is then used to fill the triangle.
     *
     * @param s The [Scene3D] object, which is unused in this specific rendering mode but required
     * by the interface.
     * @param zbuff The z-buffer used for depth testing to ensure correct occlusion.
     * @param img The integer array representing the image buffer to draw into.
     * @param w The width of the image buffer.
     * @param h The height of the image buffer.
     */
    fun rasterHeight(s: Scene3D?, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            var height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            height = (height - mMinZ) / (mMaxZ - mMinZ)
            val col: Int = Scene3D.hsvToRgb(
                hue = height,
                saturation = Math.abs(2 * (height - 0.5f)),
                value = Math.sqrt(height.toDouble()).toFloat()
            )
            Scene3D.triangle(
                zbuff = zbuff,
                img = img,
                color = col,
                w = w,
                h = h,
                fx3 = tVert[p1],
                fy3 = tVert[p1 + 1],
                fz3 = tVert[p1 + 2],
                fx2 = tVert[p2],
                fy2 = tVert[p2 + 1],
                fz2 = tVert[p2 + 2],
                fx1 = tVert[p3],
                fy1 = tVert[p3 + 1],
                fz1 = tVert[p3 + 2]
            )
            i += 3
        }
    }

    // float mSpec = 0.2f;

    /**
     * Renders the object using flat shading, where each triangle is colored based on a combination
     * of its orientation to the light source and its height.
     *
     * This method iterates through each triangle of the object and calculates a single color for
     * the entire triangle. The color is determined using the HSV (Hue, Saturation, Value) model:
     *
     *  - **Hue**: The hue is derived from the triangle's average height (z-coordinate), normalized
     *  between the object's min and max Z bounds. This creates a color gradient that changes with
     *  elevation.
     *  - **Saturation**: A fixed saturation value (`0.8f`) is used, providing a consistently vibrant
     *  color.
     *  - **Value (Brightness)**: The brightness is calculated based on the angle between the triangle's
     *  normal vector and the direction of the light source. This simulates diffuse lighting, where
     *  faces pointing towards the light appear brighter. The final brightness is a blend of this
     *  diffuse component and the object's ambient light property (`mAmbient`).
     *
     * The resulting color is then used to fill the triangle on the screen.
     *
     * @param s The [Scene3D] object, providing access to lighting information and temporary vectors.
     * @param zbuff The z-buffer for depth testing, ensuring correct pixel occlusion.
     * @param img The integer array representing the image buffer to render into.
     * @param w The width of the image buffer.
     * @param h The height of the image buffer.
     */
    open fun rasterColor(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            VectorUtil.triangleNormal(vert = tVert, p1 = p1, p2 = p2, p3 = p3, norm = s.tmpVec)
            val defuse = VectorUtil.dot(a = s.tmpVec, b = s.mTransformedLight)
            var height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            height = (height - mMinZ) / (mMaxZ - mMinZ)
            val bright = Math.min(1f, Math.max(0f, mDefuse * defuse + mAmbient))
            val hue = (height - Math.floor(height.toDouble())).toFloat()
            val sat = 0.8f
            val col: Int = Scene3D.hsvToRgb(hue = hue, saturation = sat, value = bright)
            Scene3D.triangle(
                zbuff = zbuff,
                img = img,
                color = col,
                w = w,
                h = h,
                fx3 = tVert[p1],
                fy3 = tVert[p1 + 1],
                fz3 = tVert[p1 + 2],
                fx2 = tVert[p2],
                fy2 = tVert[p2 + 1],
                fz2 = tVert[p2 + 2],
                fx1 = tVert[p3],
                fy1 = tVert[p3 + 1],
                fz1 = tVert[p3 + 2]
            )
            i += 3
        }
    }

    /**
     * Converts HSV (Hue, Saturation, Value) color components to an RGB integer color.
     *
     * This is a convenience function that preprocesses the hue and brightness values
     * before converting them to RGB.
     *  - The `hue` is normalized to the range [0, 1) to ensure it wraps around correctly.
     *  - The `bright` (value) is clamped to the range [0, 1].
     *
     * It then calls [Scene3D.hsvToRgb] to perform the final conversion.
     *
     * @param hue The hue component of the color, typically in the range [0, 1].
     * @param sat The saturation component of the color, in the range [0, 1].
     * @param bright The brightness (value) component of the color, in the range [0, 1].
     * @return The integer representation of the RGB color, suitable for use in image buffers.
     * @see hue
     * @see bright
     * @see Scene3D.hsvToRgb
     */
    private fun color(hue: Float, sat: Float, bright: Float): Int {
        var hueLocal = hue
        var brightLocal = bright
        hueLocal = hue(hue = hueLocal)
        brightLocal = bright(bright = brightLocal)
        return Scene3D.hsvToRgb(hue = hueLocal, saturation = sat, value = brightLocal)
    }

    /**
     * Normalizes a hue value to the range [0.0, 1.0).
     *
     * This function takes a hue value and wraps it to ensure it falls within the standard
     * 0-to-1 range. For example, a hue of 1.3 becomes 0.3, and -0.2 becomes 0.8. This is
     * useful for creating continuous or repeating color gradients where the hue value might
     * exceed the standard bounds.
     *
     * @param hue The input hue value, which can be any floating-point number.
     * @return The hue value wrapped to the range [0.0, 1.0).
     */
    private fun hue(hue: Float): Float {
        return (hue - Math.floor(hue.toDouble())).toFloat()
    }

    /**
     * Clamps a brightness value to the range [0.0, 1.0].
     *
     * This function ensures that the input brightness value falls within the valid
     * 0-to-1 range. Values less than 0 are set to 0, and values greater than 1 are set to 1.
     * This is typically used to ensure that brightness values used in color calculations
     * do not exceed the displayable range.
     *
     * @param bright The input brightness value.
     * @return The brightness value clamped to the range [0.0, 1.0].
     */
    private fun bright(bright: Float): Float {
        return Math.min(1f, Math.max(0f, bright))
    }

    /**
     * Calculates the diffuse reflection component for a given normal vector and light direction.
     *
     * The diffuse reflection is determined by the dot product of the surface normal vector and
     * the light vector. This value represents how much light the surface reflects based on its
     * orientation to the light source. The absolute value is taken to ensure the result is
     * non-negative, as diffuse light cannot be negative.
     *
     * @param normals A [FloatArray] containing the normal vectors.
     * @param off The starting offset in the [normals] array for the specific normal vector to use.
     * This normal vector is assumed to be 3 floats (x, y, z) starting at this offset.
     * @param light A [FloatArray] representing the light direction vector (x, y, z). Can be null,
     * in which case the behavior of [VectorUtil.dot] will determine the outcome.
     * @return The absolute value of the dot product of the specified normal and light vector,
     * representing the diffuse reflection intensity. This value is typically between 0 and 1.
     */
    private fun defuse(normals: FloatArray, off: Int, light: FloatArray?): Float {
        // s.mMatrix.mult3v(normal,off,s.tmpVec);
        return Math.abs(VectorUtil.dot(normal, off, light))
    }

    /**
     * Renders the object using Phong shading, calculating color per vertex and interpolating
     * across each triangle for smooth lighting effects.
     *
     * This method iterates through each triangle defined in the [index] array. For each vertex
     * of the triangle, it calculates the following:
     *  - **Diffuse Lighting**: The dot product between the vertex normal and the transformed light
     *  direction ([Scene3D.mTransformedLight]) determines how much light the vertex surface
     *  receives directly.
     *  - **Hue**: The hue component of the vertex color is determined by its original z-coordinate
     *  ([vert]), normalized using the object's minimum and maximum Z bounds ([mMinZ], [mMaxZ]).
     *  This creates a color gradient based on height.
     *  - **Brightness (Value)**: The brightness is calculated by combining the diffuse lighting
     *  (scaled by [mDefuse]) and the object's ambient light component ([mAmbient]).
     *
     * These per-vertex hue and brightness values, along with the object's overall [mSaturation],
     * are then passed to the [Scene3D.trianglePhong] function. This function rasterizes the
     * triangle, interpolating the HSV color components across its surface to produce a smooth,
     * shaded appearance.
     *
     * @param s The [Scene3D] object containing scene information, particularly the transformed
     * light direction.
     * @param zbuff The z-buffer used for depth testing to ensure correct occlusion.
     * @param img The integer array representing the image buffer to draw into.
     * @param w The width of the image buffer.
     * @param h The height of the image buffer.
     * @see Scene3D.trianglePhong
     * @see hue
     * @see bright
     * @see defuse
     */
    fun rasterPhong1(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        @Suppress("ReplacePrintlnWithLogging")
        println(" render ")

        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            //    VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec);


//            float defuse1 = VectorUtil.dot(normal, p1, s.mTransformedLight);
//            float defuse2 = VectorUtil.dot(normal, p2, s.mTransformedLight);
//            float defuse3 = VectorUtil.dot(normal, p3, s.mTransformedLight);
            val defuse1 = defuse(normals = normal, off = p1, light = s.mTransformedLight)
            val defuse2 = defuse(normals = normal, off = p2, light = s.mTransformedLight)
            val defuse3 = defuse(normals = normal, off = p3, light = s.mTransformedLight)
            val col1Hue = hue(hue = (vert[p1 + 2] - mMinZ) / (mMaxZ - mMinZ))
            val col2Hue = hue(hue = (vert[p2 + 2] - mMinZ) / (mMaxZ - mMinZ))
            val col3Hue = hue(hue = (vert[p3 + 2] - mMinZ) / (mMaxZ - mMinZ))
            val col1Bright = bright(bright = mDefuse * defuse1 + mAmbient)
            val col2Bright = bright(bright = mDefuse * defuse2 + mAmbient)
            val col3Bright = bright(bright = mDefuse * defuse3 + mAmbient)
            trianglePhong(
                zbuff = zbuff, img = img,
                h3 = col1Hue, b3 = col1Bright,
                h2 = col2Hue, b2 = col2Bright,
                h1 = col3Hue, b1 = col3Bright,
                sat = mSaturation,
                w = w, h = h,
                fx3 = tVert[p1], fy3 = tVert[p1 + 1], fz3 = tVert[p1 + 2],
                fx2 = tVert[p2], fy2 = tVert[p2 + 1], fz2 = tVert[p2 + 2],
                fx1 = tVert[p3], fy1 = tVert[p3 + 1], fz1 = tVert[p3 + 2]
            )
            i += 3
        }
    }

    /**
     * Renders the object using Phong shading for smooth, realistic lighting effects.
     *
     * This method iterates through each triangle of the object and renders it using the
     * [Scene3D.trianglePhong] function. For each vertex of the triangle, it calculates:
     *  - **Diffuse Lighting**: The amount of light reflected from the surface, based on the dot
     *  product of the vertex normal and the light direction. This uses the [defuse] helper
     *  function.
     *  - **Hue**: Determined by the vertex's z-coordinate (height), normalized between the object's
     *  min and max Z bounds (`mMinZ`, `mMaxZ`). This creates a color gradient that changes with
     *  elevation. The [hue] helper function normalizes this value.
     *  - **Brightness (Value)**: Calculated by combining the diffuse lighting component (scaled by
     *  `mDefuse`) with the object's ambient light property (`mAmbient`). The [bright] helper
     *  function clamps this value to the [0, 1] range.
     *
     * These per-vertex color components (hue and brightness) are then passed to
     * [Scene3D.trianglePhong], along with a fixed saturation value (0.6f in this implementation,
     * though ideally it should use `mSurface.mSaturation`), which interpolates them across the
     * triangle's surface to produce a smooth shading effect.
     *
     * @param mSurface The [Object3D] to be rendered. This parameter provides access to the
     * object's geometry (vertices, normals, indices) and material properties.
     * @param s The [Scene3D] object, providing access to scene-wide information like the
     * transformed light direction.
     * @param zbuff The z-buffer (FloatArray) used for depth testing. Can be null if depth testing
     * is not required, but this is generally not recommended for correct rendering.
     * @param img The integer array representing the image buffer to render into. Can be `null` if
     * rendering to an off-screen buffer or not at all, though this is uncommon for this function.
     * @param w The width of the image buffer.
     */
    fun rasterPhong(
        mSurface: Object3D,
        s: Scene3D,
        zbuff: FloatArray?,
        img: IntArray?,
        w: Int,
        h: Int
    ) {
        var i = 0
        while (i < mSurface.index.size) {
            val p1: Int = mSurface.index[i]
            val p2: Int = mSurface.index[i + 1]
            val p3: Int = mSurface.index[i + 2]

            //    VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec);


//            float defuse1 = VectorUtil.dot(normal, p1, s.mTransformedLight);
//            float defuse2 = VectorUtil.dot(normal, p2, s.mTransformedLight);
//            float defuse3 = VectorUtil.dot(normal, p3, s.mTransformedLight);
            val defuse1 = defuse(normals = mSurface.normal, off = p1, light = s.mTransformedLight)
            val defuse2 = defuse(normals = mSurface.normal, off = p2, light = s.mTransformedLight)
            val defuse3 = defuse(normals = mSurface.normal, off = p3, light = s.mTransformedLight)
            val col1Hue =
                hue(hue = (mSurface.vert[p1 + 2] - mSurface.mMinZ) / (mSurface.mMaxZ - mSurface.mMinZ))
            val col2Hue =
                hue(hue = (mSurface.vert[p2 + 2] - mSurface.mMinZ) / (mSurface.mMaxZ - mSurface.mMinZ))
            val col3Hue =
                hue(hue = (mSurface.vert[p3 + 2] - mSurface.mMinZ) / (mSurface.mMaxZ - mSurface.mMinZ))
            val col1Bright = bright(bright = mDefuse * defuse1 + mAmbient)
            val col2Bright = bright(bright = mDefuse * defuse2 + mAmbient)
            val col3Bright = bright(bright = mDefuse * defuse3 + mAmbient)
            trianglePhong(
                zbuff = zbuff!!,
                img = img!!,
                h3 = col1Hue,
                b3 = col1Bright,
                h2 = col2Hue,
                b2 = col2Bright,
                h1 = col3Hue,
                b1 = col3Bright,
                sat = 0.6f,
                w = w,
                h = h,
                fx3 = mSurface.tVert[p1],
                fy3 = mSurface.tVert[p1 + 1],
                fz3 = mSurface.tVert[p1 + 2],
                fx2 = mSurface.tVert[p2],
                fy2 = mSurface.tVert[p2 + 1],
                fz2 = mSurface.tVert[p2 + 2],
                fx1 = mSurface.tVert[p3],
                fy1 = mSurface.tVert[p3 + 1],
                fz1 = mSurface.tVert[p3 + 2]
            )
            i += 3
        }
    }

    /**
     * Renders the object as a series of triangles filled with the scene's background color,
     * effectively creating a silhouette or filled outline. It then draws the edges of these
     * triangles using the scene's line color.
     *
     * This method iterates through each triangle of the object:
     *  1. It first fills the triangle using [Scene3D.triangle] with the `s.background` color.
     *  This creates the solid shape of the triangle.
     *  2. Then, it draws the two edges of the triangle that are connected to the first vertex (p1)
     *  using [Scene3D.drawline] with `s.lineColor`. Specifically, it draws lines from p1 to p2,
     *  and from p1 to p3. Note: The edge between p2 and p3 is not explicitly drawn in this loop,
     *  which might be an intentional optimization if triangles share edges or an oversight if
     *  a complete wireframe is desired for each individual triangle.
     *
     * The lines are drawn on top of the filled triangles, using the same z-values, so they
     * should be visible unless the line color is the same as the background color.
     *
     * @param s The [Scene3D] object, providing access to the background color and line color.
     * @param zBuff The z-buffer (FloatArray) used for depth testing by the triangle and line
     * drawing functions.
     * @param img The integer array representing the image buffer to render into.
     * @param w The width of the image buffer.
     * @param h The height of the image buffer.
     */
    fun rasterOutline(s: Scene3D, zBuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            Scene3D.triangle(
                zbuff = zBuff, img = img, color = s.background, w = w, h = h,
                fx3 = tVert[p1], fy3 = tVert[p1 + 1], fz3 = tVert[p1 + 2],
                fx2 = tVert[p2], fy2 = tVert[p2 + 1], fz2 = tVert[p2 + 2],
                fx1 = tVert[p3], fy1 = tVert[p3 + 1], fz1 = tVert[p3 + 2]
            )
            Scene3D.drawline(
                zbuff = zBuff, img = img, color = s.lineColor, w = w, h = h,
                fx1 = tVert[p1], fy1 = tVert[p1 + 1], fz1 = tVert[p1 + 2],
                fx2 = tVert[p2], fy2 = tVert[p2 + 1], fz2 = tVert[p2 + 2]
            )
            Scene3D.drawline(
                zbuff = zBuff, img = img, color = s.lineColor, w = w, h = h,
                fx1 = tVert[p1], fy1 = tVert[p1 + 1], fz1 = tVert[p1 + 2],
                fx2 = tVert[p3], fy2 = tVert[p3 + 1], fz2 = tVert[p3 + 2]
            )
            i += 3
        }
    }

    /**
     * Calculates the geometric center of the object's bounding box.
     *
     * This function returns the midpoint of the object's extents along the x, y, and z axes,
     * based on its minimum and maximum coordinate values ([mMinX], [mMaxX], etc.). The result
     * is a `DoubleArray` containing the x, y, and z coordinates of the center point.
     *
     * @return A `DoubleArray` of size 3, where:
     * - `[0]` is the x-coordinate of the center.
     * - `[1]` is the y-coordinate of the center.
     * - `[2]` is the z-coordinate of the center.
     * @see centerX
     * @see centerY
     * @see mMinX
     * @see mMaxX
     * @see mMinY
     * @see mMaxY
     * @see mMinZ
     * @see mMaxZ
     */
    fun center(): DoubleArray {
        return doubleArrayOf(
            ((mMinX + mMaxX) / 2).toDouble(),
            ((mMinY + mMaxY) / 2).toDouble(),
            ((mMinZ + mMaxZ) / 2).toDouble()
        )
    }

    /**
     * Calculates the x-coordinate of the center of the object's bounding box.
     *
     * This is determined by averaging the minimum ([mMinX]) and maximum ([mMaxX])
     * x-coordinates of the object's vertices.
     *
     * @return The x-coordinate of the bounding box center.
     * @see mMinX
     * @see mMaxX
     * @see center
     */
    fun centerX(): Float {
        return (mMaxX + mMinX) / 2
    }

    /**
     * Calculates the y-coordinate of the center of the object's bounding box.
     *
     * This is determined by averaging the minimum ([mMinY]) and maximum ([mMaxY])
     * y-coordinates of the object's vertices.
     *
     * @return The y-coordinate of the bounding box center.
     * @see mMinY
     * @see mMaxY
     * @see center
     */
    fun centerY(): Float {
        return (mMaxY + mMinY) / 2
    }

    /**
     * Calculates half of the range (extent) of the object's bounding box along the x-axis.
     *
     * This value represents the distance from the center of the bounding box to its
     * minimum or maximum x-coordinate. It is computed as `(mMaxX - mMinX) / 2`.
     * This can be useful for scaling or positioning operations.
     *
     * @return Half the width of the object's bounding box.
     * @see mMinX
     * @see mMaxX
     * @see rangeY
     * @see size
     */
    fun rangeX(): Float {
        return (mMaxX - mMinX) / 2
    }

    /**
     * Calculates half the extent of the object's bounding box along the y-axis.
     *
     * This represents the "radius" of the object in the y-dimension if it were centered at [centerY].
     * It is computed as half the difference between the maximum ([mMaxY]) and minimum ([mMinY])
     * y-coordinates of the object's vertices.
     *
     * @return Half the range of y-coordinates spanned by the object.
     * @see mMinY
     * @see mMaxY
     * @see centerY
     * @see size
     */
    fun rangeY(): Float {
        return (mMaxY - mMinY) / 2
    }

    /**
     * Calculates a characteristic size of the object's bounding box, specifically half of its
     * space diagonal.
     *
     * This method first computes the dimensions of the bounding box along each axis:
     *  - `dx = mMaxX - mMinX`
     *  - `dy = mMaxY - mMinY`
     *  - `dz = mMaxZ - mMinZ`
     *
     * It then calculates the length of the space diagonal of this bounding box using the
     * 3D Pythagorean theorem: `diagonal = sqrt(dx^2 + dy^2 + dz^2)`.
     * The function returns half of this diagonal length. This value can be interpreted as a
     * kind of "radius" of a sphere that would enclose the bounding box.
     *
     * @return Half the length of the space diagonal of the object's bounding box.
     * @see mMinX
     * @see mMaxX
     * @see mMinY
     * @see mMaxY
     * @see mMinZ
     * @see mMaxZ
     * @see rangeX
     * @see rangeY
     */
    fun size(): Double {
        return Math.hypot(
            (mMaxX - mMinX).toDouble(),
            Math.hypot(
                (mMaxY - mMinY).toDouble(),
                (mMaxZ - mMinZ).toDouble()
            )
        ) / 2
    }
}