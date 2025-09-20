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

/**
 * This is a class that represents a Quaternion
 * Used to implement a virtual trackball with no "gimbal lock"
 * see https://en.wikipedia.org/wiki/Quaternion
 */
class Quaternion(x0: Double, x1: Double, x2: Double, x3: Double) {
    /**
     * The components of the Quaternion are stored in a [DoubleArray] of size 4
     * The components are stored in the order of w,x,y,z
     * where w is the scalar component and x,y,z are the vector components
     */
    private val x = DoubleArray(size = 4) // w,x,y,z,

    /**
     * Sets the components of the Quaternion.
     * The components are stored in the order of [w], [x], [y], [z]
     * where [w] is the scalar component and [x], [y], [z] are the vector components.
     *
     * @param w the scalar component
     * @param x the first vector component
     * @param y the second vector component
     * @param z the third vector component
     */
    operator fun set(w: Double, x: Double, y: Double, z: Double) {
        this.x[0] = w
        this.x[1] = x
        this.x[2] = y
        this.x[3] = z
    }

    /**
     * Sets the quaternion to represent the rotation from vector [v1] to vector [v2].
     * The vectors are normalized, and the rotation axis is calculated as the cross product
     * of the normalized vectors. The angle of rotation is the angle between the normalized vectors.
     *
     * @param v1 The starting vector, represented as a [DoubleArray] of size 3 (x, y, z).
     * @param v2 The ending vector, represented as a [DoubleArray] of size 3 (x, y, z).
     */
    operator fun set(v1: DoubleArray, v2: DoubleArray) {
        val vec1 = normal(a = v1)
        val vec2 = normal(a = v2)
        val axis = normal(a = cross(a = vec1, b = vec2))
        val angle = Math.acos(dot(vec1, vec2))
        set(angle = angle, axis = axis)
    }

    /**
     * Sets the quaternion to represent a rotation by a given [angle] around a given [axis].
     * The [axis] vector is assumed to be normalized.
     *
     * @param angle The angle of rotation in radians.
     * @param axis The axis of rotation, represented as a normalized DoubleArray of size 3 (x, y, z).
     * This parameter can be null, but doing so will result in a NullPointerException.
     */
    operator fun set(angle: Double, axis: DoubleArray?) {
        x[0] = Math.cos(angle / 2)
        val sin = Math.sin(angle / 2)
        x[1] = axis!![0] * sin
        x[2] = axis[1] * sin
        x[3] = axis[2] * sin
    }

    /**
     * Sets the quaternion to the parameters of its constructor.
     */
    init {
        x[0] = x0
        x[1] = x1
        x[2] = x2
        x[3] = x3
    }

    /**
     * Calculates the conjugate of the quaternion.
     * The conjugate of a quaternion (w, x, y, z) is (w, -x, -y, -z).
     * This operation is equivalent to negating the vector part of the quaternion.
     *
     * @return A new [Quaternion] representing the conjugate of this quaternion.
     */
    fun conjugate(): Quaternion {
        return Quaternion(x0 = x[0], x1 = -x[1], x2 = -x[2], x3 = -x[3])
    }

    /**
     * Adds two quaternions.
     * This operation is performed component-wise.
     * For two quaternions a = (aw, ax, ay, az) and b = (bw, bx, by, bz),
     * the sum a + b is (aw + bw, ax + bx, ay + by, az + bz).
     *
     * @param b The quaternion to add to this quaternion.
     * @return A new Quaternion representing the sum of this quaternion and b.
     */
    operator fun plus(b: Quaternion): Quaternion {
        val a = this
        return Quaternion(a.x[0] + b.x[0], a.x[1] + b.x[1], a.x[2] + b.x[2], a.x[3] + b.x[3])
    }

    /**
     * Multiplies two quaternions.
     * The product of two quaternions a = (aw, ax, ay, az) and b = (bw, bx, by, bz)
     * is given by:
     *
     * ```
     * a * b = (aw*bw - ax*bx - ay*by - az*bz,  // w component
     *          aw*bx + ax*bw + ay*bz - az*by,  // x component
     *          aw*by - ax*bz + ay*bw + az*bx,  // y component
     *          aw*bz + ax*by - ay*bx + az*bw)  // z component
     * ```
     *
     * Note that quaternion multiplication is not commutative (a * b != b * a in general).
     *
     * @param b The quaternion to multiply this quaternion by.
     * @return A new [Quaternion] representing the product of this quaternion and [b].
     */
    operator fun times(b: Quaternion): Quaternion {
        val a = this
        val y0 = a.x[0] * b.x[0] - a.x[1] * b.x[1] - a.x[2] * b.x[2] - a.x[3] * b.x[3]
        val y1 = a.x[0] * b.x[1] + a.x[1] * b.x[0] + a.x[2] * b.x[3] - a.x[3] * b.x[2]
        val y2 = a.x[0] * b.x[2] - a.x[1] * b.x[3] + a.x[2] * b.x[0] + a.x[3] * b.x[1]
        val y3 = a.x[0] * b.x[3] + a.x[1] * b.x[2] - a.x[2] * b.x[1] + a.x[3] * b.x[0]
        return Quaternion(x0 = y0, x1 = y1, x2 = y2, x3 = y3)
    }

    /**
     * Calculates the inverse of the quaternion.
     * The inverse of a quaternion q = (w, x, y, z) is given by q* / |q|^2,
     * where q* is the conjugate of q and |q|^2 is the squared norm of q.
     * The squared norm is calculated as w^2 + x^2 + y^2 + z^2.
     * If the quaternion is a unit quaternion (norm = 1), its inverse is equal to its conjugate.
     *
     * @return A new [Quaternion] representing the inverse of this quaternion.
     */
    fun inverse(): Quaternion {
        val d = x[0] * x[0] + x[1] * x[1] + x[2] * x[2] + x[3] * x[3]
        return Quaternion(x0 = x[0] / d, x1 = -x[1] / d, x2 = -x[2] / d, x3 = -x[3] / d)
    }

    /**
     * Divides quaternion [b] by this quaternion.
     * This is equivalent to multiplying the inverse of this quaternion by quaternion [b].
     * The operation is a.inverse() * [b].
     * Note that quaternion division is not commutative.
     *
     * @param b The quaternion to be divided by this quaternion (the dividend).
     * @return A new [Quaternion] representing the result of the division b / a.
     */
    fun divides(b: Quaternion): Quaternion {
        val a = this
        return a.inverse().times(b)
    }

    /**
     * Rotates a 3D vector by this quaternion.
     * This method assumes the quaternion represents a pure rotation (i.e., it is a unit quaternion).
     * The rotation is performed using the formula: v' = q * v * q^(-1), where v is the vector
     * represented as a pure quaternion (0, vx, vy, vz), q is this quaternion, and q^(-1) is
     * its inverse (which is equal to its conjugate for unit quaternions).
     *
     * The formula can be expanded as:
     *
     * ```
     * Given q = (w, x, y, z) and v = (vx, vy, vz)
     *  Let s = x*vx + y*vy + z*vz
     *  Then the rotated vector components (v'x, v'y, v'z) are:
     *  v'x = 2 * (w * (vx*w - (y*vz - z*vy)) + s*x) - vx
     *  v'y = 2 * (w * (vy*w - (z*vx - x*vz)) + s*y) - vy
     *  v'z = 2 * (w * (vz*w - (x*vy - y*vx)) + s*z) - vz
     * ```
     *
     * @param v The 3D vector to rotate, represented as a [DoubleArray] of size 3 (x, y, z).
     * This parameter can be `null`, but doing so will result in a [NullPointerException].
     * @return A new DoubleArray of size 3 representing the rotated vector.
     */
    fun rotateVec(v: DoubleArray?): DoubleArray {
        val v0 = v!![0]
        val v1 = v[1]
        val v2 = v[2]
        val s = x[1] * v0 + x[2] * v1 + x[3] * v2
        val n0 = 2 * (x[0] * (v0 * x[0] - (x[2] * v2 - x[3] * v1)) + s * x[1]) - v0
        val n1 = 2 * (x[0] * (v1 * x[0] - (x[3] * v0 - x[1] * v2)) + s * x[2]) - v1
        val n2 = 2 * (x[0] * (v2 * x[0] - (x[1] * v1 - x[2] * v0)) + s * x[3]) - v2
        return doubleArrayOf(n0, n1, n2)
    }

    /**
     * Calculates the matrix representation of the quaternion, but does not return it. (Odd that)
     */
    fun matrix() {
        val xx = x[1] * x[1]
        val xy = x[1] * x[2]
        val xz = x[1] * x[3]
        val xw = x[1] * x[0]
        val yy = x[2] * x[2]
        val yz = x[2] * x[3]
        val yw = x[2] * x[0]
        val zz = x[3] * x[3]
        val zw = x[3] * x[0]
        val m = DoubleArray(size = 16)
        m[0] = 1 - 2 * (yy + zz)
        m[1] = 2 * (xy - zw)
        m[2] = 2 * (xz + yw)
        m[4] = 2 * (xy + zw)
        m[5] = 1 - 2 * (xx + zz)
        m[6] = 2 * (yz - xw)
        m[8] = 2 * (xz - yw)
        m[9] = 2 * (yz + xw)
        m[10] = 1 - 2 * (xx + yy)
        m[14] = 0.0
        m[13] = m[14]
        m[12] = m[13]
        m[11] = m[12]
        m[7] = m[11]
        m[3] = m[7]
        m[15] = 1.0
    }

    companion object {
        /**
         * Calculates the cross product of two 3D vectors.
         * The cross product of two vectors a = (ax, ay, az) and b = (bx, by, bz)
         * is a vector c = (ay*bz - az*by, az*bx - ax*bz, ax*by - ay*bx).
         * The resulting vector is orthogonal to both input vectors.
         *
         * @param a The first vector, represented as a [DoubleArray] of size 3 (x, y, z).
         * @param b The second vector, represented as a [DoubleArray] of size 3 (x, y, z).
         * @return A new [DoubleArray] of size 3 representing the cross product of [a] and [b].
         */
        private fun cross(a: DoubleArray, b: DoubleArray): DoubleArray {
            val out0 = a[1] * b[2] - b[1] * a[2]
            val out1 = a[2] * b[0] - b[2] * a[0]
            val out2 = a[0] * b[1] - b[0] * a[1]
            return doubleArrayOf(out0, out1, out2)
        }

        /**
         * Calculates the dot product of two 3D vectors.
         * The dot product of two vectors a = (ax, ay, az) and b = (bx, by, bz)
         * is a scalar value equal to ax*bx + ay*by + az*bz.
         * The dot product is related to the angle between the two vectors.
         *
         * @param a The first vector, represented as a [DoubleArray] of size 3 (x, y, z).
         * @param b The second vector, represented as a [DoubleArray] of size 3 (x, y, z).
         * @return The dot product of vectors a and b.
         */
        private fun dot(a: DoubleArray, b: DoubleArray): Double {
            return a[0] * b[0] + a[1] * b[1] + a[2] * b[2]
        }

        /**
         * Normalizes a 3D vector.
         *
         * The normalization is achieved by dividing each component of the vector by its magnitude
         * (norm). The magnitude is calculated as the square root of the dot product of the vector
         * with itself. If the input vector is the zero vector, this will result in division by zero
         * and produce NaN values.
         *
         * @param a The 3D vector to normalize, represented as a [DoubleArray] of size 3 (x, y, z).
         * @return A new [DoubleArray] of size 3 representing the normalized vector.
         */
        private fun normal(a: DoubleArray): DoubleArray {
            val norm = Math.sqrt(dot(a, a))
            return doubleArrayOf(a[0] / norm, a[1] / norm, a[2] / norm)
        }

        /**
         * Calculates the angle between two 3D vectors.
         *
         *  - The vectors are first normalized.
         *  - The angle is then calculated as the arc cosine of the dot product of the normalized
         *  vectors.
         *  - The result is in radians.
         *
         * @param v1 The first vector, represented as a DoubleArray of size 3 (x, y, z).
         * @param v2 The second vector, represented as a DoubleArray of size 3 (x, y, z).
         * @return The angle between the two vectors in radians.
         */
        fun calcAngle(v1: DoubleArray, v2: DoubleArray): Double {
            val vec1 = normal(a = v1)
            val vec2 = normal(a = v2)
            return Math.acos(dot(a = vec1, b = vec2))
        }

        /**
         * Calculates the axis of rotation between two 3D vectors.
         *
         *  - The input vectors [v1] and [v2] are first normalized to ensure they are unit vectors.
         *  - The axis of rotation is then calculated as the cross product of the normalized `vec1`
         *  and `vec2`.
         *  - The resulting axis vector is then normalized to ensure it is a unit vector.
         *
         * This axis represents the direction around which [v1] would need to rotate to align with
         * [v2].
         *
         * @param v1 The first vector, represented as a DoubleArray of size 3 (x, y, z).
         * @param v2 The second vector, represented as a DoubleArray of size 3 (x, y, z).
         * @return A new DoubleArray of size 3 representing the normalized axis of rotation
         * between `v1` and `v2`.
         */
        fun calcAxis(v1: DoubleArray, v2: DoubleArray): DoubleArray {
            val vec1 = normal(a = v1)
            val vec2 = normal(a = v2)
            return normal(a = cross(a = vec1, b = vec2))
        }
    }
}