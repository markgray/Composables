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
    "ReplaceNotNullAssertionWithElvisReturn",
    "KotlinConstantConditions",
    "JoinDeclarationAndAssignment",
    "ReplaceJavaStaticMethodWithKotlinAnalog",
    "MemberVisibilityCanBePrivate"
)

package android.support.composegraph3d.lib

import java.text.DecimalFormat
import java.util.*

/**
 * Matrix math class.  (For the purposes of this application it is more efficient as has no JNI)
 */
open class Matrix {
    /**
     * The 16 elements of the 4x4 matrix, stored in a flat array.
     * The matrix elements are stored in column-major order, which is the standard
     * for graphics libraries like OpenGL.
     *
     * The layout is as follows:
     *
     * ```
     * [ m[0]  m[4]  m[8]  m[12] ]
     * [ m[1]  m[5]  m[9]  m[13] ]
     * [ m[2]  m[6]  m[10] m[14] ]
     * [ m[3]  m[7]  m[11] m[15] ]
     * ```
     *
     * Where `m[12], m[13], m[14]` represent the translation components.
     */
    var m: DoubleArray

    /**
     * Normalizes the upper 3x3 submatrix to create a pure rotation matrix.
     * This is achieved by normalizing each of the first three column vectors
     * (representing the X, Y, and Z axes of the rotation).
     * This operation removes any scaling factors from the rotation part of the matrix,
     * ensuring that the basis vectors are of unit length. It does not affect the
     * translation or perspective components of the matrix.
     */
    fun makeRotation() {
        run {
            val v = doubleArrayOf(m[0], m[4], m[8])
            VectorUtil.normalize(a = v)
            m[0] = v[0]
            m[4] = v[1]
            m[8] = v[2]
        }
        run {
            val v = doubleArrayOf(m[1], m[5], m[9])
            VectorUtil.normalize(a = v)
            m[1] = v[0]
            m[5] = v[1]
            m[9] = v[2]
        }
        run {
            val v = doubleArrayOf(m[2], m[6], m[10])
            VectorUtil.normalize(a = v)
            m[2] = v[0]
            m[6] = v[1]
            m[10] = v[2]
        }
    }

    /**
     * Prints the contents of this 4x4 matrix to standard output for debugging purposes.
     * The matrix is formatted in a human-readable, row-major layout, with each value
     * formatted to three decimal places.
     *
     * Example output:
     * ```
     * [   1.000 ,    0.000 ,    0.000 ,    0.000 ]
     * [   0.000 ,    1.000 ,    0.000 ,    0.000 ]
     * [   0.000 ,    0.000 ,    1.000 ,    0.000 ]
     * [   0.000 ,    0.000 ,    0.000 ,    1.000 ]
     * ```
     * Note: This method prints in row-major order for readability, even though the
     * underlying `m` array is stored in column-major order.
     */
    @Suppress("ReplacePrintlnWithLogging")
    open fun print() {
        val df = DecimalFormat("      ##0.000")
        for (i in 0..3) {
            for (j in 0..3) {
                print(
                    (if (j == 0) "[ " else " , ") + trim(
                        s = df.format(
                            m[i * 4 + j]
                        )
                    )
                )
            }
            println("]")
        }
    }

    /**
     * Zero argument constructor.
     */
    constructor() {
        m = DoubleArray(4 * 4)
        setToUnit()
    }

    /**
     * Constructor from a [Matrix].
     */
    constructor(matrix: Matrix) : this(m = Arrays.copyOf(matrix.m, matrix.m.size))

    /**
     * Constructor from an array of doubles.
     */
    protected constructor(m: DoubleArray) {
        this.m = m
    }

    /**
     * Resets this matrix to the 4x4 identity matrix.
     *
     * The identity matrix is a special matrix that, when multiplied by another
     * matrix, results in the original matrix. It has `1.0` on its main diagonal
     * and `0.0` everywhere else. In a transformation context, it represents
     * "no transformation" (no rotation, no translation, no scaling).
     *
     * The resulting matrix will be:
     * ```
     * [ 1.0  0.0  0.0  0.0 ]
     * [ 0.0  1.0  0.0  0.0 ]
     * [ 0.0  0.0  1.0  0.0 ]
     * [ 0.0  0.0  0.0  1.0 ]
     * ```
     */
    fun setToUnit() {
        for (i in 1 until m.size) {
            m[i] = 0.0
        }
        m[0] = 1.0
        m[5] = 1.0
        m[10] = 1.0
        m[15] = 1.0
    }

    /**
     * Multiplies this matrix by a 4-element column vector.
     * This performs a standard 4x4 matrix-vector multiplication.
     * The result is stored in the 4-element `dest` array.
     * The operation performed is: [dest] = `this` * [src].
     *
     * @param src The 4-element source column vector to be multiplied.
     * @param dest The 4-element destination vector where the result is stored.
     */
    fun mult4(src: FloatArray, dest: FloatArray) {
        for (i in 0..3) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..3) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat()
        }
    }

    /**
     * Multiplies this matrix by a 3-element column vector, treating it as a point in 3D space
     * by assuming a `w` coordinate of 1.0. This is an affine transformation.
     *
     * This is equivalent to performing a full 4x4 matrix multiplication with a 4-element
     * vector `[src[0], src[1], src[2], 1.0]`. The result is a 3-element vector,
     * effectively ignoring the `w` component of the transformed point.
     *
     * The operation is `dest = M * [src[0], src[1], src[2], 1.0]`, where `M` is this matrix.
     * The `dest` will contain the transformed `x, y, z` coordinates.
     *
     * @param src The 3-element source point vector (x, y, z) to be transformed.
     * @param dest The 3-element destination vector where the transformed point is stored.
     */
    fun mult3(src: FloatArray, dest: FloatArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = m[col + 3]
            for (j in 0..2) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat()
        }
    }

    /**
     * Multiplies the 3x3 upper-left submatrix of this matrix by a 3-element column vector,
     * treating it as a direction vector in 3D space. This is typically used for transforming
     * normals or other direction vectors that should not be affected by translation.
     *
     * This is equivalent to performing a 4x4 matrix multiplication with a 4-element
     * vector `[src[0], src[1], src[2], 0.0]`, which effectively ignores the translation
     * part of the matrix. The result is a 3-element vector.
     *
     * The operation is `dest = M_rot * src`, where `M_rot` is the 3x3 rotation/scaling
     * part of this matrix.
     *
     * @param src The 3-element source direction vector (x, y, z) to be transformed.
     * @param dest The 3-element destination vector where the transformed direction is stored.
     */
    fun mult3v(src: FloatArray, dest: FloatArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..2) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat()
        }
    }

    /**
     * Multiplies the 3x3 upper-left submatrix of this matrix by a 3-element column vector
     * from a source array with an offset, treating it as a direction vector. This is typically
     * used for transforming normals or other direction vectors that should not be affected by
     * translation.
     *
     * This performs the same operation as [mult3v], but reads the source vector
     * from `src` starting at the index `off`.
     *
     * The operation is `dest = M_rot * [src[off], src[off+1], src[off+2]]`, where `M_rot`
     * is the 3x3 rotation/scaling part of this matrix.
     *
     * @param src The source array containing the 3-element direction vector.
     * @param off The starting offset within the `src` array.
     * @param dest The 3-element destination array where the transformed direction is stored.
     */
    fun mult3v(src: FloatArray, off: Int, dest: FloatArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..2) {
                sum += m[col + j] * src[off + j]
            }
            dest[i] = sum.toFloat()
        }
    }

    /**
     * Multiplies this matrix by a 4-element column vector using double-precision floating-point numbers.
     * This performs a standard 4x4 matrix-vector multiplication.
     * The result is stored in the 4-element `dest` array.
     * The operation performed is: [dest] = `this` * [src].
     *
     * Note: The final result for each element is converted to a `Float` and then back to a `Double`
     * before being stored in the `dest` array, which may result in a loss of precision.
     *
     * @param src The 4-element source column vector (x, y, z, w) to be multiplied.
     * @param dest The 4-element destination vector where the result is stored.
     */
    fun mult4(src: DoubleArray, dest: DoubleArray) {
        for (i in 0..3) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..3) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat().toDouble()
        }
    }

    /**
     * Multiplies this matrix by a 3-element column vector, treating it as a point in 3D space
     * by assuming a `w` coordinate of 1.0. This is an affine transformation.
     *
     * This is an overload of [mult3] that operates on `DoubleArray`s.
     * It is equivalent to performing a full 4x4 matrix multiplication with a 4-element
     * vector `[src[0], src[1], src[2], 1.0]`. The result is a 3-element vector,
     * effectively ignoring the `w` component of the transformed point.
     *
     * The operation is `dest = M * [src[0], src[1], src[2], 1.0]`, where `M` is this matrix.
     * The `dest` will contain the transformed `x, y, z` coordinates.
     *
     * @param src The 3-element source point vector (x, y, z) to be transformed.
     * @param dest The 3-element destination vector where the transformed point is stored.
     */
    fun mult3(src: DoubleArray, dest: DoubleArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = m[col + 3]
            for (j in 0..2) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat().toDouble()
        }
    }

    /**
     * Multiplies the 3x3 upper-left submatrix of this matrix by a 3-element column vector,
     * treating it as a direction vector in 3D space. This is typically used for transforming
     * normals or other direction vectors that should not be affected by translation.
     *
     * This is the double-precision version of [mult3v] for `FloatArray`.
     *
     * The operation is `dest = M_rot * src`, where `M_rot` is the 3x3 rotation/scaling
     * part of this matrix. This is equivalent to performing a 4x4 matrix multiplication with a
     * 4-element vector `[src[0], src[1], src[2], 0.0]`.
     *
     * @param src The 3-element source direction vector (x, y, z) to be transformed.
     * @param dest The 3-element destination vector where the transformed direction is stored.
     */
    fun mult3v(src: DoubleArray?, dest: DoubleArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..2) {
                sum += m[col + j] * src!![j]
            }
            dest[i] = sum.toFloat().toDouble()
        }
    }

    /**
     * Multiplies the upper-left 3x3 submatrix of this matrix by a 3-element column vector,
     * treating it as a direction vector. This is a convenience method that allocates a new
     * array for the result.
     *
     * This method is typically used for transforming direction vectors (like normals) that
     * should not be affected by translation. It calls [mult3v] internally.
     *
     * @param src The 3-element source direction vector (x, y, z) to be transformed.
     * @return A new [DoubleArray] of size 3 containing the transformed direction vector.
     */
    fun vecmult(src: DoubleArray?): DoubleArray {
        val ret = DoubleArray(size = 3)
        mult3v(src = src, dest = ret)
        return ret
    }

    /**
     * Multiplies this matrix by a 3-element column vector from a source array with an offset,
     * treating it as a point in 3D space by assuming a `w` coordinate of 1.0. This is an
     * affine transformation. The result is stored in a destination array with an offset.
     *
     * This is an overload of [mult3] that allows operating on sub-sections of float arrays.
     * The operation is `dest[off2..off2+2] = M * [src[off1], src[off1+1], src[off1+2], 1.0]`,
     * where `M` is this matrix. The `w` component of the result is discarded.
     *
     * @param src The source array containing the 3-element point vector (x, y, z).
     * @param off1 The starting offset within the `src` array to read the vector from.
     * @param dest The destination array where the transformed 3-element point is stored.
     * @param off2 The starting offset within the `dest` array to write the result to.
     */
    fun mult3(src: FloatArray, off1: Int, dest: FloatArray, off2: Int) {
        var col = 0 * 4
        var sum = m[col + 3]
        for (j in 0..2) {
            sum += m[col + j] * src[j + off1]
        }
        val v0 = sum.toFloat()
        col = 1 * 4
        sum = m[col + 3]
        for (j in 0..2) {
            sum += m[col + j] * src[j + off1]
        }
        val v1 = sum.toFloat()
        col = 2 * 4
        sum = m[col + 3]
        for (j in 0..2) {
            sum += m[col + j] * src[j + off1]
        }
        val v2 = sum.toFloat()
        dest[off2] = v0
        dest[1 + off2] = v1
        dest[2 + off2] = v2
    }

    /**
     * Computes the inverse of its [Matrix] parameter [ret] 4x4 matrix.
     *
     * This method calculates the inverse using the analytic solution for a 4x4 matrix, which
     * involves calculating the adjugate matrix and dividing by the determinant.
     *
     * The result of the inversion is stored in the provided [ret] matrix. This is done to avoid
     * unnecessary object allocations. The method modifies the [ret] matrix directly.
     *
     * If the matrix is singular (i.e., its determinant is zero), it cannot be inverted.
     * In this case, the method will return `null`.
     *
     * @param ret A `Matrix` object where the resulting inverse matrix will be stored.
     * The contents of this matrix will be overwritten.
     * @return The `ret` matrix containing the inverse, or `null` if the matrix is not invertible.
     */
    fun invers(ret: Matrix): Matrix? {
        val inv: DoubleArray = ret.m
        inv[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14] -
            m[9] * m[6] * m[15] + m[9] * m[7] * m[14] +
            m[13] * m[6] * m[11] - m[13] * m[7] * m[10]
        inv[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] +
            m[8] * m[6] * m[15] - m[8] * m[7] * m[14] -
            m[12] * m[6] * m[11] + m[12] * m[7] * m[10]
        inv[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13] -
            m[8] * m[5] * m[15] + m[8] * m[7] * m[13] +
            m[12] * m[5] * m[11] - m[12] * m[7] * m[9]
        inv[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] +
            m[8] * m[5] * m[14] - m[8] * m[6] * m[13] -
            m[12] * m[5] * m[10] + m[12] * m[6] * m[9]
        inv[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] +
            m[9] * m[2] * m[15] - m[9] * m[3] * m[14] -
            m[13] * m[2] * m[11] + m[13] * m[3] * m[10]
        inv[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14] -
            m[8] * m[2] * m[15] + m[8] * m[3] * m[14] +
            m[12] * m[2] * m[11] - m[12] * m[3] * m[10]
        inv[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] +
            m[8] * m[1] * m[15] - m[8] * m[3] * m[13] -
            m[12] * m[1] * m[11] + m[12] * m[3] * m[9]
        inv[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13] -
            m[8] * m[1] * m[14] + m[8] * m[2] * m[13] +
            m[12] * m[1] * m[10] - m[12] * m[2] * m[9]
        inv[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14] -
            m[5] * m[2] * m[15] + m[5] * m[3] * m[14] +
            m[13] * m[2] * m[7] - m[13] * m[3] * m[6]
        inv[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] +
            m[4] * m[2] * m[15] - m[4] * m[3] * m[14] -
            m[12] * m[2] * m[7] + m[12] * m[3] * m[6]
        inv[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13] -
            m[4] * m[1] * m[15] + m[4] * m[3] * m[13] +
            m[12] * m[1] * m[7] - m[12] * m[3] * m[5]
        inv[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] +
            m[4] * m[1] * m[14] - m[4] * m[2] * m[13] -
            m[12] * m[1] * m[6] + m[12] * m[2] * m[5]
        inv[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] +
            m[5] * m[2] * m[11] - m[5] * m[3] * m[10] -
            m[9] * m[2] * m[7] + m[9] * m[3] * m[6]
        inv[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] -
            m[4] * m[2] * m[11] + m[4] * m[3] * m[10] +
            m[8] * m[2] * m[7] - m[8] * m[3] * m[6]
        inv[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] +
            m[4] * m[1] * m[11] - m[4] * m[3] * m[9] -
            m[8] * m[1] * m[7] + m[8] * m[3] * m[5]
        inv[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] -
            m[4] * m[1] * m[10] + m[4] * m[2] * m[9] +
            m[8] * m[1] * m[6] - m[8] * m[2] * m[5]

        var det: Double
        det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12]
        if (det == 0.0) {
            return null
        }
        det = 1.0 / det
        for (i in 0..15) {
            inv[i] = inv[i] * det
        }
        return ret
    }

    /**
     * Multiplies `this` matrix by another matrix [b] and returns the result as a new [Matrix].
     *
     * This performs a post-multiplication, which means the operation is: `this` * [b].
     * The order of multiplication is important in matrix math as it is not commutative
     * (`A * B != B * A`). This operation transforms a point first by matrix `b`, and
     * then by `this` matrix.
     *
     * @param b The [Matrix] to multiply this matrix by (the right-hand side of the multiplication).
     * @return A new [Matrix] object containing the result of the multiplication.
     */
    fun mult(b: Matrix?): Matrix {
        return Matrix(
            m = multiply(
                a = m, b = b!!.m
            )
        )
    }

    /**
     * Pre-multiplies this matrix by another matrix [b] and returns the result as a new [Matrix].
     *
     * This performs a pre-multiplication, which means the operation is: [b] * `this`.
     * The order of multiplication is important in matrix math as it is not commutative
     * (`A * B != B * A`). This operation transforms a point first by `this` matrix,
     * and then by matrix `b`.
     *
     * @param b The [Matrix] to pre-multiply this matrix by (the left-hand side of the multiplication).
     * @return A new [Matrix] object containing the result of the multiplication.
     */
    fun premult(b: Matrix): Matrix {
        return Matrix(m = multiply(a = b.m, b = m))
    }

    companion object {
        /**
         * Trims a formatted number string to a fixed width of 7 characters from the end.
         * This is a helper function for [print] to ensure consistent column alignment
         * when printing the matrix, by taking the rightmost characters of a string
         * that has been padded with leading spaces.
         *
         * @param s The input string, typically a number formatted by [DecimalFormat].
         * @return The last 7 characters of the input string.
         */
        private fun trim(s: String): String {
            return s.substring(startIndex = s.length - 7)
        }

        /**
         * Multiplies two 4x4 matrices, represented as 16-element [DoubleArray]s,
         * and returns the result in a new array. The matrices are assumed to be stored
         * in column-major order.
         *
         * This performs the standard matrix multiplication `C = A * B`, where `C` is the
         * resultant matrix.
         *
         * The element `C(i, j)` (at row `i`, column `j`) of the resultant matrix is
         * calculated as the dot product of the i-th row of matrix `A` and the j-th
         * column of matrix `B`.
         *
         * Given the column-major storage `array[row + 4 * column]`:
         * `resultant[i + 4 * j] = sum(k=0..3) of a[i + 4 * k] * b[k + 4 * j]`
         *
         * @param a A 16-element [DoubleArray] representing the first 4x4 matrix (left-hand side).
         * @param b A 16-element [DoubleArray] representing the second 4x4 matrix (right-hand side).
         * @return A new 16-element [DoubleArray] containing the result of the multiplication.
         */
        private fun multiply(a: DoubleArray, b: DoubleArray): DoubleArray {
            val resultant = DoubleArray(size = 16)
            for (i in 0..3) {
                for (j in 0..3) {
                    for (k in 0..3) {
                        resultant[i + 4 * j] += a[i + 4 * k] * b[k + 4 * j]
                    }
                }
            }
            return resultant
        }

        /**
         * A simple demonstration and test of the Matrix class functionality.
         * This main function creates a sample matrix, calculates its inverse,
         * and then multiplies the original matrix by its inverse. The expected
         * result of this multiplication is the identity matrix. The state of
         * the matrix at each step (original, inverse, and product) is printed
         * to the console for verification.
         *
         * @param args Command-line arguments (not used).
         */
        @Suppress("ReplacePrintlnWithLogging")
        @JvmStatic
        fun main(args: Array<String>) {
            val m = Matrix()
            val inv = Matrix()
            m.m[0] = 100.0
            m.m[5] = 12.0
            m.m[10] = 63.0
            m.m[3] = 12.0
            m.m[7] = 34.0
            m.m[11] = 17.0
            println(" matrix ")
            m.print()
            println(" inv ")
            m.invers(ret = inv)!!.print()
            println(" inv*matrix ")
            m.mult(b = m.invers(ret = inv)).print()
        }
    }
}