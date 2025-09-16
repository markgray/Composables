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
    "UNUSED_VARIABLE",
    "unused",
    "ReplaceJavaStaticMethodWithKotlinAnalog",
    "ReplaceNotNullAssertionWithElvisReturn",
    "MemberVisibilityCanBePrivate",
    "RedundantSuppression"
)

package android.support.composegraph3d.lib.objects

import android.support.composegraph3d.lib.Object3D
import android.support.composegraph3d.lib.Scene3D
import android.support.composegraph3d.lib.Scene3D.Companion.drawline
import android.support.composegraph3d.lib.Scene3D.Companion.hsvToRgb
import android.support.composegraph3d.lib.Scene3D.Companion.isBackface
import android.support.composegraph3d.lib.Scene3D.Companion.triangle
import android.support.composegraph3d.lib.VectorUtil.dot
import android.support.composegraph3d.lib.VectorUtil.triangleNormal

/**
 * Draws box along the axis
 */
class AxisBox : Object3D() {
    
    /**
     * The color of the axis box's wireframe. The default is a light gray.
     */
    var color: Int = -0xefefdf
    
    /**
     * Sets the dimensions of the axis-aligned bounding box.
     * This defines the minimum and maximum coordinates for the box along the X, Y, and Z axes.
     * After setting the range, it rebuilds the box's geometry.
     *
     * @param minX The minimum X-coordinate of the box.
     * @param maxX The maximum X-coordinate of the box.
     * @param minY The minimum Y-coordinate of the box.
     * @param maxY The maximum Y-coordinate of the box.
     * @param minZ The minimum Z-coordinate of the box.
     * @param maxZ The maximum Z-coordinate of the box.
     */
    fun setRange(minX: Float, maxX: Float, minY: Float, maxY: Float, minZ: Float, maxZ: Float) {
        mMinX = minX
        mMaxX = maxX
        mMinY = minY
        mMaxY = maxY
        mMinZ = minZ
        mMaxZ = maxZ
        buildBox()
    }

    /**
     * Constructs the geometry of the axis-aligned bounding box.
     * This involves defining the 8 vertices of the cube based on the min/max
     * coordinate values and then defining the 12 triangles (2 for each of the 6 faces)
     * that make up the box's surface.
     *
     * The vertices are calculated using bitwise operations on an index `i` from 0 to 7.
     * - The first bit of `i` determines the X coordinate (mMinX or mMaxX).
     * - The second bit of `i` determines the Y coordinate (mMinY or mMaxY).
     * - The third bit of `i` determines the Z coordinate (mMinZ or mMaxZ).
     *
     * The [index] array stores the vertex indices for the triangles that form the box's wireframe.
     */
    fun buildBox() {
        vert = FloatArray(8 * 3) // cube 8 corners
        tVert = FloatArray(vert.size)
        for (i in 0..7) {
            vert[i * 3] = if (i and 1 == 0) mMinX else mMaxX // X
            vert[i * 3 + 1] = if (i shr 1 and 1 == 0) mMinY else mMaxY // Y
            vert[i * 3 + 2] = if (i shr 2 and 1 == 0) mMinZ else mMaxZ // Z
        }
        index = IntArray(6 * 2 * 3) // 6 sides x 2 triangles x 3 points per triangle
        val sides = intArrayOf( // pattern of clockwise triangles around cube
            0, 2, 1, 3, 1, 2,
            0, 1, 4, 5, 4, 1,
            0, 4, 2, 6, 2, 4,
            7, 6, 5, 4, 5, 6,
            7, 3, 6, 2, 6, 3,
            7, 5, 3, 1, 3, 5
        )
        index = IntArray(sides.size)
        for (i in sides.indices) {
            index[i] = sides[i] * 3
        }
    }

    /**
     * Renders a wireframe representation of the axis box. This is an older rendering method and
     * may be deprecated or used for specific legacy purposes. It iterates through the box's
     * triangles and draws the first edge of each triangle.
     *
     * This approach results in an incomplete wireframe, as it only draws one edge per triangle
     * (p1 to p2), missing the other two edges. It doesn't perform back-face culling, so all
     * specified edges are drawn regardless of their orientation to the camera.
     * The z-coordinate of the lines is slightly adjusted to prevent z-fighting with surfaces.
     *
     * @param s The 3D scene, which is unused in this function.
     * @param zbuff The z-buffer for depth testing.
     * @param img The integer array representing the image buffer to draw into.
     * @param w The width of the image buffer.
     * @param h The height of the image buffer.
     */
    fun renderOld(s: Scene3D?, zbuff: FloatArray?, img: IntArray?, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            val height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            val value = (255 * Math.abs(height)).toInt()
            drawline(
                zbuff = zbuff!!, img = img!!, color = color, w = w, h = h,
                fx1 = tVert[p1], fy1 = tVert[p1 + 1], fz1 = tVert[p1 + 2] - 0.01f,
                fx2 = tVert[p2], fy2 = tVert[p2 + 1], fz2 = tVert[p2 + 2] - 0.01f
            )
            i += 3
        }
    }

    /**
     * Renders the axis box as a wireframe with tick marks.
     *
     * This method iterates through the triangles that define the box's faces. It uses back-face
     * culling to determine the visibility of each face.
     *
     *  - For back-facing triangles (not visible from the camera's perspective), it draws only the
     *  first two edges, creating a simple wireframe for the far side of the box.
     *  - For front-facing triangles, it draws the first two edges and then adds tick marks along
     *  those edges. The tick marks are perpendicular to the edge and point towards the third
     *  vertex of the triangle, providing a sense of the surface grid.
     *
     * A small z-offset is applied to all lines to prevent z-fighting with other surfaces.
     *
     * @param s The current 3D scene, used for context.
     * @param zbuff The z-buffer for depth testing.
     * @param img The integer array representing the image buffer to draw into.
     * @param width The width of the image buffer.
     * @param height The height of the image buffer.
     */
    override fun render(s: Scene3D, zbuff: FloatArray, img: IntArray, width: Int, height: Int) {
        // raster_color(s, zbuff, img, w, h)
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            val front = isBackface(
                fx3 = tVert[p1], fy3 = tVert[p1 + 1], fz3 = tVert[p1 + 2],
                fx2 = tVert[p2], fy2 = tVert[p2 + 1], fz2 = tVert[p2 + 2],
                fx1 = tVert[p3], fy1 = tVert[p3 + 1], fz1 = tVert[p3 + 2]
            )
            if (front) {
                drawline(
                    zbuff = zbuff, img = img, color = color, w = width, h = height,
                    fx1 = tVert[p1], fy1 = tVert[p1 + 1], fz1 = tVert[p1 + 2] - 0.01f,
                    fx2 = tVert[p2], fy2 = tVert[p2 + 1], fz2 = tVert[p2 + 2] - 0.01f
                )
                drawline(
                    zbuff = zbuff, img = img, color = color, w = width, h = height,
                    fx1 = tVert[p1], fy1 = tVert[p1 + 1], fz1 = tVert[p1 + 2] - 0.01f,
                    fx2 = tVert[p3], fy2 = tVert[p3 + 1], fz2 = tVert[p3 + 2] - 0.01f
                )
                i += 3
                continue
            }
            drawline(
                zbuff = zbuff, img = img, color = color, w = width, h = height,
                fx1 = tVert[p1], fy1 = tVert[p1 + 1], fz1 = tVert[p1 + 2] - 0.01f,
                fx2 = tVert[p2], fy2 = tVert[p2 + 1], fz2 = tVert[p2 + 2] - 0.01f
            )
            drawTicks(
                zbuff = zbuff, img = img, color = color, w = width, h = height,
                p1x = tVert[p1], p1y = tVert[p1 + 1], p1z = tVert[p1 + 2] - 0.01f,
                p2x = tVert[p2], p2y = tVert[p2 + 1], p2z = tVert[p2 + 2] - 0.01f,
                nx = tVert[p3] - tVert[p1], ny = tVert[p3 + 1] - tVert[p1 + 1],
                nz = tVert[p3 + 2] - tVert[p1 + 2]
            )
            drawline(
                zbuff = zbuff, img = img, color = color, w = width, h = height,
                fx1 = tVert[p1], fy1 = tVert[p1 + 1], fz1 = tVert[p1 + 2] - 0.01f,
                fx2 = tVert[p3], fy2 = tVert[p3 + 1], fz2 = tVert[p3 + 2] - 0.01f
            )
            drawTicks(
                zbuff = zbuff, img = img, color = color, w = width, h = height,
                p1x = tVert[p1], p1y = tVert[p1 + 1], p1z = tVert[p1 + 2] - 0.01f,
                p2x = tVert[p3], p2y = tVert[p3 + 1], p2z = tVert[p3 + 2] - 0.01f,
                nx = tVert[p2] - tVert[p1], ny = tVert[p2 + 1] - tVert[p1 + 1],
                nz = tVert[p2 + 2] - tVert[p1 + 2]
            )
            i += 3
        }
    }

    /**
     * A vector representing the direction from the camera to the screen, used for lighting
     * calculations. It helps determine the amount of diffuse reflection from the box's surfaces.
     * The default value `{0, 0, -1}` points straight out from the camera along the negative Z-axis
     * in view space, which is standard for a forward-facing camera.
     */
    var screen: FloatArray = floatArrayOf(0f, 0f, -1f)

    init {
        type = 1
    }

    /**
     * Renders the axis box as a solid, shaded object.
     *
     * This method iterates through all the triangles that make up the box's surface.
     * For each triangle, it first performs back-face culling to avoid rendering
     * triangles that are not visible to the camera.
     *
     * For visible (front-facing) triangles, it calculates the surface normal and
     * then determines the color based on a simple lighting model that includes both
     * diffuse and ambient components. The final color is calculated in HSV and converted
     * to RGB before the triangle is rasterized and drawn to the image buffer, with
     * depth testing against the z-buffer.
     *
     * @param s The scene, providing access to lighting information and temporary vectors.
     * @param zbuff The z-buffer for depth testing.
     * @param img The integer array representing the image buffer to draw into.
     * @param w The width of the image buffer.
     * @param h The height of the image buffer.
     */
    override fun rasterColor(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            val back = isBackface(
                fx3 = tVert[p1], fy3 = tVert[p1 + 1], fz3 = tVert[p1 + 2],
                fx2 = tVert[p2], fy2 = tVert[p2 + 1], fz2 = tVert[p2 + 2],
                fx1 = tVert[p3], fy1 = tVert[p3 + 1], fz1 = tVert[p3 + 2]
            )
            if (back) {
                i += 3
                continue
            }
            triangleNormal(vert = tVert, p1 = p1, p2 = p3, p3 = p2, norm = s.tmpVec)
            val ss = dot(a = s.tmpVec, b = screen)
            val defuse = dot(a = s.tmpVec, b = s.mTransformedLight)
            val ambient = 0.5f
            val bright = Math.min(Math.max(0f, defuse + ambient), 1f)
            val hue = 0.4f
            val sat = 0.1f
            val col = hsvToRgb(hue = hue, saturation = sat, value = bright)
            triangle(
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

    companion object {

        /**
         * Draws a series of tick marks along a line segment in 3D space.
         *
         * The ticks are drawn perpendicular to the line segment defined by points p1 and p2.
         * The direction and length of the ticks are determined by a normal-like vector (nx, ny, nz).
         *
         * @param zbuff The z-buffer for depth testing.
         * @param img The integer array representing the image buffer to draw into.
         * @param color The color of the ticks.
         * @param w The width of the image buffer.
         * @param h The height of the image buffer.
         * @param p1x The x-coordinate of the starting point of the line segment.
         * @param p1y The y-coordinate of the starting point of the line segment.
         * @param p1z The z-coordinate of the starting point of the line segment.
         * @param p2x The x-coordinate of the ending point of the line segment.
         * @param p2y The y-coordinate of the ending point of the line segment.
         * @param p2z The z-coordinate of the ending point of the line segment.
         * @param nx The x-component of the vector defining the direction and length of the ticks.
         * @param ny The y-component of the vector defining the direction and length of the ticks.
         * @param nz The z-component of the vector defining the direction and length of the ticks.
         */
        fun drawTicks(
            zbuff: FloatArray?, img: IntArray?, color: Int, w: Int, h: Int,
            p1x: Float, p1y: Float, p1z: Float,
            p2x: Float, p2y: Float, p2z: Float,
            nx: Float, ny: Float, nz: Float
        ) {
            val tx = nx / 10
            val ty = ny / 10
            val tz = nz / 10
            var f = 0f
            while (f <= 1) {
                val px = p1x + f * (p2x - p1x)
                val py = p1y + f * (p2y - p1y)
                val pz = p1z + f * (p2z - p1z)
                drawline(
                    zbuff = zbuff!!, img = img!!, color = color, w = w, h = h,
                    fx1 = px, fy1 = py, fz1 = pz - 0.01f,
                    fx2 = px + tx, fy2 = py + ty, fz2 = pz + tz - 0.01f
                )
                f += 0.1f
            }
        }
    }
}