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
    "ReplaceJavaStaticMethodWithKotlinAnalog",
    "KotlinConstantConditions",
    "MemberVisibilityCanBePrivate"
)

package android.support.composegraph3d.lib.objects

import android.support.composegraph3d.lib.Object3D

/**
 * Plots a surface based on Z = f(X,Y)
 */
class Surface3D(private val mFunction: Function) : Object3D() {
    /**
     * Zoom factor in the Z axis (never changed).
     */
    private val mZoomZ = 1f

    /**
     * The resolution of the surface grid. This determines the number of vertices used to represent
     * the surface. The surface is rendered as a grid of `mSize` x `mSize` quads. A higher value
     * results in a smoother, more detailed surface at the cost of performance. The total number of
     * vertices in the surface will be (`mSize` + 1) * (`mSize` + 1).
     */
    var mSize: Int = 100

    /**
     * Sets the axis-aligned bounding box for the surface plot. This defines the domain
     * and range for the x, y, and z coordinates. After setting the range, the surface
     * is recomputed.
     *
     * @param minX The minimum value on the X-axis.
     * @param maxX The maximum value on the X-axis.
     * @param minY The minimum value on the Y-axis.
     * @param maxY The maximum value on the Y-axis.
     * @param minZ The minimum value on the Z-axis. If `Float.NaN` is provided, the z-range
     *             will be automatically calculated based on the function's output and scaled
     *             to fit the x/y range.
     * @param maxZ The maximum value on the Z-axis. If `Float.NaN` is provided, the z-range
     *             will be automatically calculated.
     */
    fun setRange(minX: Float, maxX: Float, minY: Float, maxY: Float, minZ: Float, maxZ: Float) {
        mMinX = minX
        mMaxX = maxX
        mMinY = minY
        mMaxY = maxY
        mMinZ = minZ
        mMaxZ = maxZ
        computeSurface(resetZ = java.lang.Float.isNaN(mMinZ))
    }

    /**
     * Sets the resolution of the surface grid.
     *
     * This determines the number of vertices used to represent the surface. The surface is rendered
     * as a grid of `size` x `size` quads. A higher value results in a smoother, more detailed
     * surface at the cost of increased computation. The total number of vertices will be
     * (`size` + 1) * (`size` + 1). After setting the new size, the surface is recomputed.
     *
     * @param size The new grid size. For a `size` of N, the grid will be N x N quads.
     * @see mSize
     */
    fun setArraySize(size: Int) {
        mSize = size
        computeSurface(resetZ = false)
    }

    /**
     * Defines a mathematical function of two variables, z = f(x, y), used to generate
     * the surface plot. Implement this interface to specify the shape of the 3D surface.
     */
    interface Function {
        /**
         * Defines the mathematical function `z = f(x, y)` to be plotted.
         * This function will be called for each point (x, y) on the grid
         * to determine its corresponding z-coordinate.
         *
         * @param x The x-coordinate.
         * @param y The y-coordinate.
         * @return The z-coordinate corresponding to the given (x, y) point.
         */
        fun eval(x: Float, y: Float): Float
    }

    /**
     * Recomputes the entire surface geometry.
     *
     * This function orchestrates the process of generating the 3D surface. It first allocates
     * the necessary memory for vertices and indices based on the current grid size (`mSize`). It
     * then calls `calcSurface` to populate these buffers with the actual coordinate data derived
     * from the mathematical function. This method should be called whenever a property that affects
     * the surface's geometry, such as the grid size or axis ranges, is changed.
     *
     * @param resetZ If true, the Z-axis range will be automatically recalculated based on the
     *               output of the function. If false, the existing `mMinZ` and `mMaxZ` values
     *               will be used.
     * @see setArraySize
     * @see setRange
     * @see calcSurface
     */
    fun computeSurface(resetZ: Boolean) {
        val n = (mSize + 1) * (mSize + 1)
        makeVert(n = n)
        makeIndexes(n = mSize * mSize * 2)
        calcSurface(resetZ = resetZ)
    }

    /**
     * Calculates the vertices, normals, and indices for the 3D surface mesh.
     *
     * This function generates the geometry of the surface based on the [Function] property
     * [mFunction]. It iterates over a grid defined by `mMinX`, `mMaxX`, `mMinY`, `mMaxY`, and
     * `mSize`. For each point (x, y) on the grid, it computes the z-coordinate using
     * `mFunction.eval(x, y)`.
     *
     * It also calculates the normal vector at each vertex using the central difference method to
     * approximate the partial derivatives of the function. This is essential for correct lighting.
     *
     * If [resetZ] is true, the function will automatically determine the min/max z-values from
     * the function's output and scale the z-coordinates to be proportional to the x/y range,
     * preventing the surface from looking too "flat" or "spiky".
     *
     * Finally, it populates the `index` array to define the triangles that form the surface mesh.
     *
     * @param resetZ If true, the z-range is automatically calculated and scaled to fit the x/y range.
     * If false, the existing `mMinZ` and `mMaxZ` values are used.
     */
    fun calcSurface(resetZ: Boolean) {
        val minX = mMinX
        val maxX = mMaxX
        val minY = mMinY
        val maxY = mMaxY
        var minZ = Float.MAX_VALUE
        var maxZ = -Float.MAX_VALUE
        var count = 0
        for (iy in 0..mSize) {
            val y = minY + iy * (maxY - minY) / mSize
            for (ix in 0..mSize) {
                val x = minX + ix * (maxX - minX) / mSize
                val delta = 0.001f
                var dx = (mFunction.eval(x + delta, y) - mFunction.eval(x - delta, y)) / (2 * delta)
                var dy = (mFunction.eval(x, y + delta) - mFunction.eval(x, y - delta)) / (2 * delta)
                var dz = 1f
                val norm = Math.sqrt((dz * dz + dx * dx + dy * dy).toDouble()).toFloat()
                dx /= norm
                dy /= norm
                dz /= norm
                normal[count] = dx
                vert[count++] = x
                normal[count] = dy
                vert[count++] = y
                normal[count] = -dz
                var z = mFunction.eval(x, y)
                if (java.lang.Float.isNaN(z) || java.lang.Float.isInfinite(z)) {
                    val epslonX = 0.000005232f
                    val epslonY = 0.00000898f
                    z = mFunction.eval(x + epslonX, y + epslonY)
                }
                vert[count++] = z
                if (java.lang.Float.isNaN(z)) {
                    continue
                }
                if (java.lang.Float.isInfinite(z)) {
                    continue
                }
                minZ = Math.min(z, minZ)
                maxZ = Math.max(z, maxZ)
            }
            if (resetZ) {
                mMinZ = minZ
                mMaxZ = maxZ
            }
        }
        // normalize range in z
        val xrange = mMaxX - mMinX
        val yrange = mMaxY - mMinY
        val zrange = mMaxZ - mMinZ
        if (zrange != 0f && resetZ) {
            val xyrange = (xrange + yrange) / 2
            val scalez = xyrange / zrange
            var i = 0
            while (i < vert.size) {
                var z = vert[i + 2]
                if (java.lang.Float.isNaN(z) || java.lang.Float.isInfinite(z)) {
                    z = if (i > 3) {
                        vert[i - 1]
                    } else {
                        vert[i + 5]
                    }
                }
                vert[i + 2] = z * scalez * mZoomZ
                i += 3
            }
            if (resetZ) {
                mMinZ *= scalez
                mMaxZ *= scalez
            }
        }
        count = 0
        for (iy in 0 until mSize) {
            for (ix in 0 until mSize) {
                val p1 = 3 * (ix + iy * (mSize + 1))
                val p2 = 3 * (1 + ix + iy * (mSize + 1))
                val p3 = 3 * (ix + (iy + 1) * (mSize + 1))
                val p4 = 3 * (1 + ix + (iy + 1) * (mSize + 1))
                index[count++] = p1
                index[count++] = p2
                index[count++] = p3
                index[count++] = p4
                index[count++] = p3
                index[count++] = p2
            }
        }
    }
}