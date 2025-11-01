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
@file:Suppress("MemberVisibilityCanBePrivate")

package android.support.drag2d.lib

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin

/**
 * Provides easing functions for animations, primarily based on cubic Bézier curves.
 * This class allows for the creation of custom easing curves or the use of several predefined
 * standard easing functions.
 *
 * Easing functions specify the rate of change of a parameter over time.
 * They are typically used in animations to make movement appear more natural.
 * This implementation uses a cubic Bézier curve defined by two control points (x1, y1) and
 * (x2, y2). The curve starts at (0, 0) and ends at (1, 1).
 *
 * It also includes specialized easing functions like elastic and bounce, which are not based on
 * Bézier curves.
 *
 * The class provides methods to get the interpolated value (`get(t)`) and its derivative
 * (`getDiff(t)`) for a given time `t` (from 0.0 to 1.0).
 *
 * Example usage with a custom curve:
 * `val customEasing = MaterialEasing(0.25, 0.1, 0.25, 1.0)`
 *
 * Example usage with a predefined curve:
 * `val decelerateEasing = MaterialEasing.DECELERATE`
 *
 * The `companion object` contains several predefined easing instances like `DECELERATE`, `LINEAR`,
 * `OVERSHOOT`, and various "ease-out" types.
 */
@Suppress("unused")
class MaterialEasing : MaterialVelocity.Easing {

    /**
     * Provides an elastic easing out function.
     * The curve starts off fast and then overshoots the target, oscillating with decreasing
     * amplitude until it settles at the final value (1.0).
     * This creates a bouncy, rubber-band like effect at the end of the animation.
     *
     * The easing function is defined by the formula:
     * `f(t) = 2^(-10 * t) * sin((t * 10 - 0.75) * (2 * PI / 3)) + 1`
     * for t in the range [0, 1].
     */
    internal class EaseOutElastic : MaterialVelocity.Easing {
        /**
         * Constant value representing 2 * PI.
         */
        val c4: Double = (2 * Math.PI) / 3

        /**
         * Constant value representing the natural logarithm of 8.
         */
        val log8: Double = ln(x = 8.0)

        /**
         * Calculates the easing value for a given time point `t`.
         *
         * This function takes a value `t` representing the fraction of time elapsed (from 0.0 to 1.0)
         * and returns the corresponding progress value (also typically from 0.0 to 1.0)
         * based on the cubic Bézier curve defined for this easing instance.
         *
         * It uses a binary search algorithm to find the parameter `time` (internal to the Bézier
         * curve calculation) that produces an x-coordinate matching the input `t`. It then calculates
         * the corresponding y-coordinate on the curve. For efficiency and to handle potential
         * floating-point inaccuracies, it performs a final linear interpolation within a small
         * range around the found point.
         *
         * @param t The input value, representing the fraction of time elapsed, clamped to the range
         * 0.0 to 1.0.
         * @return The interpolated progress value according to the easing curve. Returns 0.0 for
         * t <= 0.0 and 1.0 for t >= 1.0.
         */
        override fun get(t: Double): Double {
            if (t <= 0) {
                return 0.0
            }
            if (t >= 1) {
                return 1.0
            }
            return 2.0.pow(x = -10 * t) * sin(x = (t * 10 - 0.75) * c4) + 1
        }

        /**
         * Calculates the instantaneous velocity (the derivative) of the elastic easing curve
         * at a given time `t`. This represents the rate of change of the animation's progress.
         *
         * The derivative is calculated from the easing function `f(t)`.
         * The function returns 0.0 if `t` is outside the typical animation progress range of
         * 0.0 to 1.0
         *
         * @param t The time progress, typically from 0.0 (start) to 1.0 (end).
         * @return The velocity of the easing curve at time `t`.
         */
        override fun getDiff(t: Double): Double {
            @Suppress("UnusedVariable")
            val c4: Double = (2 * Math.PI) / 3
            if (t !in 0.0..1.0) {
                return 0.0
            }

            val v: Double = 5 * 2.0.pow(1 - 10 * t) *
                (log8 * cos((TWENTY_PI * t) / 3) + 2 * Math.PI * sin((TWENTY_PI * t) / 3)) / 3
            return v
        }

        /**
         * Creates and returns a copy of this `EaseOutElastic` object.
         * Since `EaseOutElastic` is stateless, this simply returns a new instance.
         *
         * @return A new `EaseOutElastic` instance.
         */
        override fun clone(): EaseOutElastic {
            return EaseOutElastic()
        }

        companion object {
            /**
             * Constant value representing 20 * PI.
             */
            const val TWENTY_PI: Double = 20 * Math.PI
        }
    }

    /**
     * Provides a bounce easing out function.
     * The animation "bounces" at the end, decelerating with a series of bounces
     * that decrease in amplitude until settling at the final value (1.0).
     * This mimics the behavior of an object dropping and bouncing to a stop.
     *
     * The function is defined piecewise to create the bouncing effect.
     */
    internal class EaseOutBounce : MaterialVelocity.Easing {
        /**
         * Constants representing the parameters of the bounce easing function.
         */
        val n1: Double = 7.5625
        val d1: Double = 2.75

        /**
         * Calculates the interpolated value for a given time `t` (from 0.0 to 1.0).
         * This function provides an "ease-out bounce" effect. The animation starts fast and
         * then "bounces" to a stop, simulating an object hitting a surface and bouncing with
         * decreasing height until it settles at the final position.
         *
         * The implementation uses a series of parabolic equations to model the bouncing behavior.
         *
         * @param t The input value, representing the normalized time of the animation, typically
         * in the range 0.0 to 1.0
         * @return The interpolated value, representing the animation's progress at time `t`.
         * The output is also clamped to the 0.0 to 1.0 range.
         */
        override fun get(t: Double): Double {
            var t: Double = t
            if (t < 0) {
                return 0.0
            }
            if (t < 1 / d1) {
                return (1 / (1 + 1 / d1)) * (n1 * t * t + t)
            } else if (t < 2 / d1) {
                return n1 * (1.5 / d1.let { t -= it; t }) * t + 0.75
            } else if (t < 2.5 / d1) {
                return n1 * (2.25 / d1.let { t -= it; t }) * t + 0.9375
            } else if (t <= 1) {
                return n1 * (2.625 / d1.let { t -= it; t }) * t + 0.984375
            }
            return 1.0
        }

        /**
         * Calculates the derivative (velocity) of the bounce easing curve at a given time `t`.
         *
         * This function provides the instantaneous rate of change of the bounce animation.
         * The bounce effect is defined as a piecewise function, and this method computes
         * the derivative for each segment of that function.
         *
         * The derivative is calculated as follows:
         *  - For `t < 0`, the velocity is 0.
         *  - For `0 <= t < 1/d1`, it follows the derivative of the first quadratic segment.
         *  - For subsequent segments (`1/d1 <= t < 2/d1`, `2/d1 <= t < 2.5/d1`, etc.),
         *  it calculates the linear velocity within each bounce arc.
         * - For `t > 1`, the velocity is 0, as the animation has settled.
         *
         * @param t The time input, typically in the range 0.0 to 1.0
         * @return The value of the derivative at time `t`.
         */
        override fun getDiff(t: Double): Double {
            @Suppress("UnusedVariable", "unused")
            var result: Double
            if (t < 0) {
                return 0.0
            }
            if (t < 1 / d1) {
                return 2 * n1 * (t) / (1 + 1 / d1) + 1 / (1 + 1 / d1)
            } else if (t < 2 / d1) {
                return 2 * n1 * (t - 1.5 / d1)
            } else if (t < 2.5 / d1) {
                return 2 * n1 * (t - 2.25 / d1)
            } else if (t <= 1) {
                return 2 * n1 * (t - 2.625 / d1)
            }
            return 0.0
        }

        /**
         * Creates and returns a copy of this `EaseOutBounce` object.
         * Since `EaseOutBounce` is stateless (it has no mutable properties that change
         * its behavior), this method simply returns a new instance of the class.
         *
         * @return A new `EaseOutBounce` instance.
         */
        override fun clone(): EaseOutBounce {
            return EaseOutBounce()
        }
    }

    /**
     * A string representation of the easing curve, used for efficient initialization.
     * The format is typically "name(x1, y1, x2, y2)", e.g., "standard(0.4, 0.0, 0.2, 1.0)".
     * This allows the curve parameters to be parsed directly from a string, which can be
     * useful for configuration or serialization purposes. It is primarily used by the
     * internal constructor that takes a string argument.
     */
    private var mConfigString: String? = null

    /**
     * The x-coordinate of the first control point (P1) of the cubic Bézier curve.
     * The curve starts at P0(0, 0) and ends at P3(1, 1). This value, along with
     * [mY1], defines the handle that influences the curve's shape near the start.
     */
    var mX1: Double = 0.0

    /**
     * The y-coordinate of the first control point (P1) of the cubic Bézier curve.
     * The curve starts at P0(0, 0) and ends at P3(1, 1). This value, along with
     * [mX1], defines the handle that influences the curve's shape near the start.
     */
    var mY1: Double = 0.0

    /**
     * The x-coordinate of the second control point (P2) of the cubic Bézier curve.
     * This value, along with [mY2], defines the handle that influences the
     * curve's shape near the end (P3 at (1, 1)).
     */
    var mX2: Double = 0.0

    /**
     * The y-coordinate of the second control point (P2) of the cubic Bézier curve.
     * This value, along with [mX2], defines the handle that influences the
     * curve's shape near the end (P3 at (1, 1)).
     */
    var mY2: Double = 0.0

    /**
     * Initializes a new [MaterialEasing] instance from a string representation.
     * This constructor is used for deserialization.
     */
    internal constructor(configString: String) {
        // done this way for efficiency
        mConfigString = configString
        val start: Int = configString.indexOf('(')
        val off1: Int = configString.indexOf(',', start)
        mX1 = configString.substring(start + 1, off1).trim { it <= ' ' }.toDouble()
        val off2: Int = configString.indexOf(',', off1 + 1)
        mY1 = configString.substring(off1 + 1, off2).trim { it <= ' ' }.toDouble()
        val off3: Int = configString.indexOf(',', off2 + 1)
        mX2 = configString.substring(off2 + 1, off3).trim { it <= ' ' }.toDouble()
        val end: Int = configString.indexOf(')', off3 + 1)
        mY2 = configString.substring(off3 + 1, end).trim { it <= ' ' }.toDouble()
    }

    /**
     * Initializes a new [MaterialEasing] instance from an array of float values.
     * This constructor is used for deserialization.
     */
    internal constructor(c: FloatArray) : this(
        x1 = c[0].toDouble(),
        y1 = c[1].toDouble(),
        x2 = c[2].toDouble(),
        y2 = c[3].toDouble()
    )

    /*
     * Initializes a new [MaterialEasing] instance with specified control points.
     */
    internal constructor(x1: Double, y1: Double, x2: Double, y2: Double) {
        setup(x1 = x1, y1 = y1, x2 = x2, y2 = y2)
    }

    /**
     * Creates and returns a copy of this [MaterialEasing] object.
     * The new instance will have the same cubic Bézier control points (`x1`, `y1`, `x2`, `y2`)
     * as the original.
     *
     * @return A new [MaterialEasing] instance that is a clone of this one.
     */
    override fun clone(): MaterialEasing {
        return MaterialEasing(x1 = mX1, y1 = mY1, x2 = mX2, y2 = mY2)
    }

    /**
     * Sets the control points for the cubic Bézier curve.
     * The curve is defined by two control points, P1 and P2. The curve starts
     * at P0(0, 0) and ends at P3(1, 1), and these points are implicit.
     *
     * @param x1 The x-coordinate of the first control point (P1).
     * @param y1 The y-coordinate of the first control point (P1).
     * @param x2 The x-coordinate of the second control point (P2).
     * @param y2 The y-coordinate of the second control point (P2).
     */
    fun setup(x1: Double, y1: Double, x2: Double, y2: Double) {
        this.mX1 = x1
        this.mY1 = y1
        this.mX2 = x2
        this.mY2 = y2
    }

    /**
     * Calculates the x-coordinate on the cubic Bézier curve for a given parameter `t`.
     * The Bézier curve is defined by the control points P0(0,0), P1(mX1, mY1),
     * P2(mX2, mY2), and P3(1,1). This function computes the x-component of the
     * point on the curve.
     *
     * The formula for the x-coordinate is:
     * `X(t) = (1-t)³*P0x + 3(1-t)²*t*P1x + 3(1-t)*t²*P2x + t³*P3x`
     * Since P0x is 0 and P3x is 1, the formula simplifies to:
     * `X(t) = 3(1-t)²*t*mX1 + 3(1-t)*t²*mX2 + t³`
     *
     * @param t The curve parameter, typically in the range 0.0 to 1.0
     * @return The x-coordinate on the curve for the given parameter `t`.
     */
    private fun getX(t: Double): Double {
        val t1: Double = 1 - t
        // no need for because start at 0,0 double f0 = (1 - t) * (1 - t) * (1 - t);
        val f1: Double = 3 * t1 * t1 * t
        val f2: Double = 3 * t1 * t * t
        val f3: Double = t * t * t
        return mX1 * f1 + mX2 * f2 + f3
    }

    /**
     * Calculates the y-coordinate on the cubic Bézier curve for a given parameter `t`.
     * The Bézier curve is defined by the control points P0(0,0), P1(mX1, mY1),
     * P2(mX2, mY2), and P3(1,1). This function computes the y-component of the
     * point on the curve, which represents the animation's progress.
     *
     * The formula for the y-coordinate is:
     * `Y(t) = (1-t)³*P0y + 3(1-t)²*t*P1y + 3(1-t)*t²*P2y + t³*P3y`
     * Since P0y is 0 and P3y is 1, the formula simplifies to:
     * `Y(t) = 3(1-t)²*t*mY1 + 3(1-t)*t²*mY2 + t³`
     *
     * @param t The curve parameter, typically in the range 0.0 to 1.0.
     * @return The y-coordinate on the curve for the given parameter `t`.
     */
    private fun getY(t: Double): Double {
        val t1: Double = 1 - t
        // no need for because start at 0,0 double f0 = (1 - t) * (1 - t) * (1 - t);
        val f1: Double = 3 * t1 * t1 * t
        val f2: Double = 3 * t1 * t * t
        val f3: Double = t * t * t
        return (mY1 * f1) + (mY2 * f2) + f3
    }

    /**
     * Calculates the derivative of the x-coordinate (dX/dt) of the cubic Bézier curve
     * with respect to the parameter `t`.
     *
     * The original Bézier curve for x is `X(t) = 3(1-t)²*t*mX1 + 3(1-t)*t²*mX2 + t³`.
     * This function computes the derivative of that expression.
     * The derivative is used to find the slope of the curve (`dy/dx`), which is calculated as
     * `(dY/dt) / (dX/dt)`.
     *
     * @param t The curve parameter, typically in the range 0.0 to 1.0.
     * @return The value of the derivative of the x-component at parameter `t`.
     */
    private fun getDiffX(t: Double): Double {
        val t1: Double = 1 - t
        return (3 * t1 * t1 * mX1) + (6 * t1 * t * (mX2 - mX1)) + (3 * t * t * (1 - mX2))
    }

    /**
     * Calculates the derivative of the y-coordinate (dY/dt) on the cubic Bézier curve for a given
     * parameter `t`. This represents the vertical component of the curve's velocity with respect
     * to the parameter `t`.
     *
     * The y-coordinate of the curve is given by the Bézier formula:
     * `Y(t) = 3(1-t)²*t*mY1 + 3(1-t)*t²*mY2 + t³*1`
     * This function computes the derivative of that formula.
     *
     * @param t The curve parameter, typically in the range 0.0 to 1.0.
     * @return The value of the derivative dY/dt at parameter `t`.
     */
    private fun getDiffY(t: Double): Double {
        val t1: Double = 1 - t
        return (3 * t1 * t1 * mY1) + (6 * t1 * t * (mY2 - mY1)) + (3 * t * t * (1 - mY2))
    }

    /**
     * Calculates the derivative (velocity) of the easing curve at a given time `t`.
     * This represents the rate of change of the animation's progress.
     *
     * It approximates the derivative by finding the parameter on the Bézier curve that
     * corresponds to the input time `t` using a binary search. It then calculates the
     * slope of a small line segment on the curve around that point. This provides a
     * numerical approximation of the instantaneous velocity.
     *
     * The process involves:
     *  1. A binary search to find the internal curve parameter (`timeParameter`) that
     *  produces an x-coordinate (`tx`) close to the input time `t`.
     *  2. Defining a small interval around this `timeParameter`.
     *  3. Calculating the (x, y) coordinates at the start and end of this interval.
     *  4. Returning the slope (dy/dx) between these two points.
     *
     * @param t The time progress, typically from 0.0 (start) to 1.0 (end).
     * @return The approximate derivative of the easing curve at time `t`.
     */
    override fun getDiff(t: Double): Double {
        var timeParameter = 0.5
        var range = 0.5
        while (range > SD_ERROR) {
            val tx = getX(t = timeParameter)
            range *= 0.5
            if (tx < t) {
                timeParameter += range
            } else {
                timeParameter -= range
            }
        }

        val x1: Double = getX(t = timeParameter - range)
        val x2: Double = getX(t = timeParameter + range)
        val y1: Double = getY(t = timeParameter - range)
        val y2: Double = getY(t = timeParameter + range)
        return (y2 - y1) / (x2 - x1)
    }

    /**
     * Calculates the easing value for a given time `t`.
     *
     * This function takes a value `t` representing the fraction of time elapsed (from 0.0 to 1.0)
     * and returns the corresponding progress value (also typically from 0.0 to 1.0)
     * based on the cubic Bézier curve defined for this easing instance.
     *
     * For a given time `t`, there is no closed-form solution to find the corresponding
     * y-value on a cubic Bézier curve. This implementation uses a numerical approach:
     *  1. A binary search is performed to find the curve's internal parameter (`time`)
     *  that produces an x-coordinate on the curve (`tx`) that is very close to the input `t`.
     *  2. Once a small region is identified, a linear interpolation is performed between the
     *  y-values at the boundaries of that region to get a precise result.
     *
     * @param t The input value, representing the fraction of time elapsed. The function will
     *clamp this to the range 0.0 to 1.0
     * @return The interpolated progress value according to the easing curve. Returns 0.0 for
     * `t <= 0.0` and 1.0 for `t >= 1.0`.
     */
    override fun get(t: Double): Double {
        if (t <= 0.0) {
            return 0.0
        }
        if (t >= 1.0) {
            return 1.0
        }
        var time = 0.5
        var range = 0.5
        while (range > S_ERROR) {
            val tx: Double = getX(t = time)
            range *= 0.5
            if (tx < t) {
                time += range
            } else {
                time -= range
            }
        }

        val x1: Double = getX(t = time - range)
        val x2: Double = getX(t = time + range)
        val y1: Double = getY(t = time - range)
        val y2: Double = getY(t = time + range)

        return (y2 - y1) * (t - x1) / (x2 - x1) + y1
    }

    companion object {
        /**
         * Unused, but apparently intended for a "standard" curve.
         */
        private val STANDARD_COEFFICIENTS = floatArrayOf(0.4f, 0.0f, 0.2f, 1f)

        /**
         * Unused, but apparently intended for an "accelerate" curve.
         */
        private val ACCELERATE_COEFFICIENTS = floatArrayOf(0.4f, 0.05f, 0.8f, 0.7f)

        /**
         * Used for the "decelerate" curve.
         */
        private val DECELERATE_COEFFICIENTS = floatArrayOf(0.0f, 0.0f, 0.2f, 0.95f)

        /**
         * Used for the "linear" curve.
         */
        private val LINEAR_COEFFICIENTS = floatArrayOf(1f, 1f, 0f, 0f)

        /**
         * Unused, but apparently intended for an "anticipate" curve.
         */
        private val ANTICIPATE_COEFFICIENTS = floatArrayOf(0.36f, 0f, 0.66f, -0.56f)

        /**
         * Used for the "overshoot" curve.
         */
        private val OVERSHOOT_COEFFICIENTS = floatArrayOf(0.34f, 1.56f, 0.64f, 1f)

        /**
         * Unused.
         */
        const val DECELERATE_NAME: String = "decelerate"

        /**
         * Unused.
         */
        const val ACCELERATE_NAME: String = "accelerate"

        /**
         * Unused.
         */
        const val STANDARD_NAME: String = "standard"

        /**
         * Unused.
         */
        const val LINEAR_NAME: String = "linear"

        /**
         * Unused.
         */
        const val ANTICIPATE_NAME: String = "anticipate"

        /**
         * Unused.
         */
        const val OVERSHOOT_NAME: String = "overshoot"

        // public static final CubicEasing STANDARD = new CubicEasing(STANDARD_COEFFICIENTS);
        // public static final CubicEasing ACCELERATE = new CubicEasing(ACCELERATE_COEFFICIENTS);

        /**
         * A pre-defined easing curve that starts fast and decelerates towards the end.
         * The animation will begin with high velocity and gradually slow down as it approaches
         * the final state, creating a smooth "ease-out" effect.
         *
         * This corresponds to a cubic-bezier curve with control points (0.0, 0.0) and (0.2, 0.95).
         */
        val DECELERATE: MaterialEasing = MaterialEasing(c = DECELERATE_COEFFICIENTS)

        /**
         * A linear easing curve, which provides a constant rate of change.
         *
         * The animation progresses uniformly from start to end without any acceleration or
         * deceleration. Its graph is a straight line from (0, 0) to (1, 1). This is equivalent
         * to an animation with no easing effect applied.
         */
        val LINEAR: MaterialEasing = MaterialEasing(c = LINEAR_COEFFICIENTS)

        // public static final CubicEasing ANTICIPATE = new CubicEasing(ANTICIPATE_COEFFICIENTS);

        /**
         * An easing curve that simulates an "overshoot" effect.
         *
         * The animation starts by moving quickly towards the target, goes past it, and then
         * smoothly returns to settle at the final position. This creates a sense of momentum
         * and exaggeration.
         *
         * It is defined by the cubic Bézier curve with control points (0.34, 1.56) and (0.64, 1.0).
         * Note that the Y-coordinate of the first control point (1.56) is greater than 1.0,
         * which causes the curve to "overshoot" the final value before coming back.
         */
        val OVERSHOOT: MaterialEasing = MaterialEasing(c = OVERSHOOT_COEFFICIENTS)

        /**
         * An "ease-out" easing that starts fast and decelerates using a sinusoidal curve.
         * This creates a very smooth and gentle deceleration effect. The animation begins at
         * full speed and gradually slows to a stop.
         *
         * This easing is defined by the cubic-bezier curve with control points
         * P1(0.61, 1) and P2(0.88, 1).
         */
        val EASE_OUT_SINE: MaterialEasing = MaterialEasing(c = floatArrayOf(0.61f, 1f, 0.88f, 1f))

        /**
         * An "ease-out" easing that starts fast and decelerates using a cubic curve.
         * This provides a standard and widely used deceleration effect, starting at full
         * speed and smoothly slowing down to a stop.
         *
         * This easing is defined by the cubic-bezier curve with control points
         * P1(0.33, 1) and P2(0.68, 1).
         */
        val EASE_OUT_CUBIC: MaterialEasing = MaterialEasing(c = floatArrayOf(0.33f, 1f, 0.68f, 1f))

        /**
         * An "ease-out" easing that starts fast and decelerates using a quintic (power of 5) curve.
         * This provides a more pronounced deceleration effect compared to `EASE_OUT_CUBIC` or
         * `EASE_OUT_QUART`. The animation begins very quickly and slows down sharply towards the end.
         *
         * This easing is defined by the cubic-bezier curve with control points
         * P1(0.22, 1) and P2(0.36, 1).
         */
        val EASE_OUT_QUINT: MaterialEasing = MaterialEasing(c = floatArrayOf(0.22f, 1f, 0.36f, 1f))

        /**
         * An "ease-out" easing that starts fast and decelerates following the curve of a
         * circular arc. This creates a visually smooth and natural-looking slowdown. The
         * animation begins at full speed and its rate of change decreases as if moving along
         * the circumference of a circle.
         *
         * This easing is defined by the cubic-bezier curve with control points
         * P1(0.02, 0.55) and P2(0.45, 1).
         */
        val EASE_OUT_CIRC: MaterialEasing =
            MaterialEasing(c = floatArrayOf(0.02f, 0.55f, 0.45f, 1f))

        /**
         * An "ease-out" easing that starts fast and decelerates using a quadratic (power of 2) curve.
         * This provides a simple and effective deceleration, starting at full speed and smoothly
         * slowing to a stop. It's less pronounced than `EASE_OUT_CUBIC`.
         *
         * This easing is defined by the cubic-bezier curve with control points
         * P1(0.5, 1) and P2(0.89, 1).
         */
        val EASE_OUT_QUAD: MaterialEasing = MaterialEasing(c = floatArrayOf(0.5f, 1f, 0.89f, 1f))

        /**
         * An "ease-out" easing that starts fast and decelerates using a quartic (power of 4) curve.
         * This provides a stronger deceleration effect than `EASE_OUT_CUBIC` but is less
         * abrupt than `EASE_OUT_QUINT`. The animation begins at high velocity and slows
         * down noticeably as it approaches the end.
         *
         * This easing is defined by the cubic-bezier curve with control points
         * P1(0.25, 1) and P2(0.5, 1).
         */
        val EASE_OUT_QUART: MaterialEasing = MaterialEasing(c = floatArrayOf(0.25f, 1f, 0.5f, 1f))

        /**
         * An "ease-out" easing that starts fast and decelerates using an exponential curve.
         * This creates a very dramatic deceleration effect, where the animation starts
         * extremely fast and then slows down very abruptly at the end. It's one of the most
         * pronounced "ease-out" effects.
         *
         * This easing is defined by the cubic-bezier curve with control points
         * P1(0.16, 1) and P2(0.3, 1).
         */
        val EASE_OUT_EXPO: MaterialEasing = MaterialEasing(c = floatArrayOf(0.16f, 1f, 0.3f, 1f))

        /**
         * An "ease-out" easing that simulates an object being pulled back slightly before
         * moving forward. The animation starts by moving in the opposite direction, then
         * overshoots the target, and finally settles at the end position. This creates a
         * spring-like, anticipatory effect.
         *
         * It is defined by the cubic Bézier curve with control points (0.34, 1.56) and (0.64, 1.0).
         * The y-coordinate of the first control point being greater than 1 causes the overshoot.
         * The combination of control points creates the initial backward motion.
         */
        val EASE_OUT_BACK: MaterialEasing =
            MaterialEasing(c = floatArrayOf(0.34f, 1.56f, 0.64f, 1f))

        /**
         * An "ease-out" easing that simulates an elastic, spring-like effect.
         * The animation starts fast, overshoots the target value, then oscillates back and forth
         * with decreasing amplitude until it comes to rest at the final position. This creates
         * a bouncy, rubber-band-like motion at the end of the animation.
         *
         * This easing is not based on a cubic-Bézier curve but is implemented using a
         * damped sine wave function.
         */
        val EASE_OUT_ELASTIC: MaterialVelocity.Easing = EaseOutElastic()

        /**
         * An "ease-out" easing that simulates a bouncing effect.
         * The animation starts fast and then "bounces" to a stop as it reaches the end.
         * This mimics the behavior of an object being dropped, bouncing several times with
         * decreasing height until it comes to rest.
         *
         * This easing is not based on a cubic-Bézier curve but is implemented with a
         * piecewise function to create the distinct bounces.
         */
        val EASE_OUT_BOUNCE: MaterialVelocity.Easing = EaseOutBounce()

        /**
         * The error tolerance for the binary search in the `get(t)` method.
         * This value determines the precision of the search for the curve parameter `t`
         * that corresponds to the input time. A smaller value increases accuracy but may
         * slightly increase computation time. The search stops when the range of uncertainty
         * is smaller than this value.
         */
        private const val S_ERROR = 0.001

        /**
         * The error tolerance for the binary search in the `getDiff(t)` method.
         * This value determines the precision of the search when approximating the
         * derivative of the easing curve. The search for the correct time parameter
         * on the curve stops when the search interval is smaller than this threshold.
         * A smaller value leads to a more accurate derivative calculation at the cost
         * of potentially more iterations.
         */
        private const val SD_ERROR = 0.0001
    }
}
