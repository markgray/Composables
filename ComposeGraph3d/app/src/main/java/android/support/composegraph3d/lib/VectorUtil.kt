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
    "unused",
    "ReplaceJavaStaticMethodWithKotlinAnalog",
    "ReplaceNotNullAssertionWithElvisReturn",
    "MemberVisibilityCanBePrivate"
)

package android.support.composegraph3d.lib

import java.text.DecimalFormat

/**
 * A few utilities for vector calculations.
 */
object VectorUtil {
    /**
     * Subtracts vector [b] from vector [a] and stores the result in [out].
     *
     * @param a The first vector.
     * @param b The second vector.
     * @param out The vector to store the result in.
     */
    fun sub(a: DoubleArray?, b: DoubleArray?, out: DoubleArray?) {
        out!![0] = a!![0] - b!![0]
        out[1] = a[1] - b[1]
        out[2] = a[2] - b[2]
    }

    /**
     * Multiplies vector [a] by scalar [b] and stores the result in [out].
     *
     * @param a The vector.
     * @param b The scalar.
     * @param out The vector to store the result in.
     */
    fun mult(a: DoubleArray?, b: Double, out: DoubleArray?) {
        out!![0] = a!![0] * b
        out[1] = a[1] * b
        out[2] = a[2] * b
    }

    /**
     * Calculates the dot product of two vectors.
     *
     * @param a The first vector.
     * @param b The second vector.
     * @return The dot product of the two vectors.
     */
    fun dot(a: DoubleArray, b: DoubleArray): Double {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2]
    }

    /**
     * Calculates the norm (magnitude) of a [DoubleArray] 3D vector.
     *
     * @param a The vector.
     * @return The norm of the vector.
     */
    fun norm(a: DoubleArray?): Double {
        return Math.sqrt(a!![0] * a[0] + a[1] * a[1] + a[2] * a[2])
    }

    /**
     * Calculates the norm (magnitude) of a [FloatArray] 3D vector.
     *
     * @param a The vector.
     * @return The norm of the vector.
     */
    fun norm(a: FloatArray?): Double {
        return Math.sqrt((a!![0] * a[0] + a[1] * a[1] + a[2] * a[2]).toDouble())
    }

    /**
     * Calculates the cross product of two 3D vectors.
     *
     * @param a The first vector.
     * @param b The second vector.
     * @param out The vector to store the result in.
     */
    fun cross(a: DoubleArray, b: DoubleArray?, out: DoubleArray?) {
        val out0: Double = a[1] * b!![2] - b[1] * a[2]
        val out1: Double = a[2] * b[0] - b[2] * a[0]
        val out2: Double = a[0] * b[1] - b[0] * a[1]
        out!![0] = out0
        out[1] = out1
        out[2] = out2
    }

    /**
     * Normalizes a [DoubleArray] 3D vector.
     *
     * @param a The vector to normalize.
     */
    fun normalize(a: DoubleArray?) {
        val norm: Double = norm(a = a)
        a!![0] /= norm
        a[1] /= norm
        a[2] /= norm
    }

    /**
     * Normalizes a [FloatArray] 3D vector.
     *
     * @param a The vector to normalize.
     */
    fun normalize(a: FloatArray) {
        val norm: Float = norm(a).toFloat()
        a[0] /= norm
        a[1] /= norm
        a[2] /= norm
    }

    /**
     * Adds vector [b] to vector [a] and stores the result in [out].
     *
     * @param a The first vector.
     * @param b The second vector.
     * @param out The vector to store the result in.
     */
    fun add(
        a: DoubleArray, b: DoubleArray,
        out: DoubleArray
    ) {
        out[0] = a[0] + b[0]
        out[1] = a[1] + b[1]
        out[2] = a[2] + b[2]
    }

    /**
     * Multiply vector [a] by scalar [x] and add vector [b], store the result in [out].
     * ```
     * (out = x*a + b)
     * ```
     *
     * @param a The first vector.
     * @param x The scalar.
     * @param b The second vector.
     * @param out The vector to store the result in.
     */
    fun madd(
        a: DoubleArray?, x: Double, b: DoubleArray?,
        out: DoubleArray?
    ) {
        out!![0] = x * a!![0] + b!![0]
        out[1] = x * a[1] + b[1]
        out[2] = x * a[2] + b[2]
    }

    /**
     * Calculates the normal vector of a triangle defined by three vertices.
     * The vertices are specified by their indices in a [FloatArray].
     * The resulting normal vector is normalized.
     *
     * @param vert A [FloatArray] containing the vertex coordinates.
     * Each vertex is represented by three consecutive float values (x, y, z).
     * @param p1 The index of the first vertex of the triangle in the `vert` array.
     * @param p2 The index of the second vertex of the triangle in the `vert` array.
     * @param p3 The index of the third vertex of the triangle in the `vert` array.
     * @param norm A [FloatArray] of at least size 3 where the calculated normal vector will be stored.
     * If `null`, a new [FloatArray] will be created.
     */
    @JvmStatic
    fun triangleNormal(vert: FloatArray, p1: Int, p2: Int, p3: Int, norm: FloatArray?) {
        val x1: Float = vert[p2] - vert[p1]
        val y1: Float = vert[p2 + 1] - vert[p1 + 1]
        val z1: Float = vert[p2 + 2] - vert[p1 + 2]
        val x2: Float = vert[p3] - vert[p1]
        val y2: Float = vert[p3 + 1] - vert[p1 + 1]
        val z2: Float = vert[p3 + 2] - vert[p1 + 2]
        cross(a0 = x1, a1 = y1, a2 = z1, b0 = x2, b1 = y2, b2 = z2, out = norm)
        val n: Float = norm(a = norm).toFloat()
        norm!![0] /= n
        norm[1] /= n
        norm[2] /= n
    }

    /**
     * Calculates the dot product of two [FloatArray] 3D vectors.
     *
     * @param a The first vector.
     * @param b The second vector.
     * @return The dot product of the two vectors.
     */
    fun dot(a: FloatArray?, b: FloatArray?): Float {
        return a!![0] * b!![0] + a[1] * b[1] + a[2] * b[2]
    }

    /**
     * Calculates the dot product of two [FloatArray] 3D vectors.
     * The first vector is represented by a [FloatArray] and an offset,
     * while the second vector is represented by a [FloatArray].
     *
     * @param a The [FloatArray] containing the first vector.
     * @param offset The starting index of the first vector in the [a] array.
     * @param b The [FloatArray] containing the second vector.
     * @return The dot product of the two vectors.
     */
    fun dot(a: FloatArray, offset: Int, b: FloatArray?): Float {
        return a[offset] * b!![0] + a[1 + offset] * b[1] + a[2 + offset] * b[2]
    }

    /**
     * Calculates the cross product of two 3D vectors defined by their coordinates.
     * The result is stored in the [FloatArray] parameter [out].
     *
     * @param a0 The x-coordinate of the first vector.
     * @param a1 The y-coordinate of the first vector.
     * @param a2 The z-coordinate of the first vector.
     * @param b0 The x-coordinate of the second vector.
     * @param b1 The y-coordinate of the second vector.
     * @param b2 The z-coordinate of the second vector.
     * @param out A [FloatArray] of at least size 3 where the resulting cross product vector
     * will be stored.
     */
    fun cross(a0: Float, a1: Float, a2: Float, b0: Float, b1: Float, b2: Float, out: FloatArray?) {
        val out0 = a1 * b2 - b1 * a2
        val out1 = a2 * b0 - b2 * a0
        val out2 = a0 * b1 - b0 * a1
        out!![0] = out0
        out[1] = out1
        out[2] = out2
    }

    /**
     * Trims a string to its last 7 characters.
     *
     * @param s The string to trim.
     * @return The last 7 characters of the string.
     */
    private fun trim(s: String): String {
        return s.substring(startIndex = s.length - 7)
    }

    /**
     * Converts a 3D float vector to a string representation.
     * Each component of the vector is formatted to three decimal places.
     * If a component is NaN, it is represented as "NAN".
     * ```
     * Example: "[  1.234 ,   2.345 ,   3.456]"
     * ```
     *
     * @param light The [FloatArray] representing the 3D vector. It is assumed to have at
     * least 3 elements.
     * @return A string representation of the vector.
     */
    fun vecToString(light: FloatArray): String {
        val df = DecimalFormat("        ##0.000")
        var str = "["
        for (i in 0..2) {
            if (java.lang.Float.isNaN(light[i])) {
                str += (if (i == 0) "" else " , ") + trim(s = "           NAN")
                continue
            }
            str += (if (i == 0) "" else " , ") + trim(df.format(light[i].toDouble()))
        }
        return "$str]"
    }
}