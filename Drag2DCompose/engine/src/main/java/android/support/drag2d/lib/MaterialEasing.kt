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
package android.support.drag2d.lib

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin

@Suppress("unused")
class MaterialEasing : MaterialVelocity.Easing {
    internal class EaseOutElastic : MaterialVelocity.Easing {
        val c4: Double = (2 * Math.PI) / 3
        val log8: Double = ln(8.0)

        override fun get(t: Double): Double {
            if (t <= 0) {
                return 0.0
            }
            if (t >= 1) {
                return 1.0
            }
            return 2.0.pow(-10 * t) * sin((t * 10 - 0.75) * c4) + 1
        }

        override fun getDiff(t: Double): Double {
            @Suppress("UnusedVariable")
            val c4 = (2 * Math.PI) / 3
            if (t !in 0.0..1.0) {
                return 0.0
            }

            val v: Double = 5 * 2.0.pow(1 - 10 * t) *
                (log8 * cos((TWENTY_PI * t) / 3) + 2 * Math.PI * sin((TWENTY_PI * t) / 3)) / 3
            return v
        }

        override fun clone(): EaseOutElastic {
            return EaseOutElastic()
        }

        companion object {
            const val TWENTY_PI: Double = 20 * Math.PI
        }
    }

    internal class EaseOutBounce : MaterialVelocity.Easing {
        val n1: Double = 7.5625
        val d1: Double = 2.75

        override fun get(t: Double): Double {
            var t = t
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

        override fun clone(): EaseOutBounce {
            return EaseOutBounce()
        }
    }


    private var mConfigString: String? = null
    var mX1: Double = 0.0
    var mY1: Double = 0.0
    var mX2: Double = 0.0
    var mY2: Double = 0.0

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

    internal constructor(c: FloatArray) : this(
        c[0].toDouble(),
        c[1].toDouble(),
        c[2].toDouble(),
        c[3].toDouble()
    )

    internal constructor(x1: Double, y1: Double, x2: Double, y2: Double) {
        setup(x1, y1, x2, y2)
    }


    override fun clone(): MaterialEasing {
        return MaterialEasing(mX1, mY1, mX2, mY2)
    }

    fun setup(x1: Double, y1: Double, x2: Double, y2: Double) {
        this.mX1 = x1
        this.mY1 = y1
        this.mX2 = x2
        this.mY2 = y2
    }

    private fun getX(t: Double): Double {
        val t1 = 1 - t
        // no need for because start at 0,0 double f0 = (1 - t) * (1 - t) * (1 - t);
        val f1 = 3 * t1 * t1 * t
        val f2 = 3 * t1 * t * t
        val f3 = t * t * t
        return mX1 * f1 + mX2 * f2 + f3
    }

    private fun getY(t: Double): Double {
        val t1 = 1 - t
        // no need for because start at 0,0 double f0 = (1 - t) * (1 - t) * (1 - t);
        val f1 = 3 * t1 * t1 * t
        val f2 = 3 * t1 * t * t
        val f3 = t * t * t
        return mY1 * f1 + mY2 * f2 + f3
    }


    private fun getDiffX(t: Double): Double {
        val t1 = 1 - t
        return 3 * t1 * t1 * mX1 + 6 * t1 * t * (mX2 - mX1) + 3 * t * t * (1 - mX2)
    }


    private fun getDiffY(t: Double): Double {
        val t1 = 1 - t
        return 3 * t1 * t1 * mY1 + 6 * t1 * t * (mY2 - mY1) + 3 * t * t * (1 - mY2)
    }

    /**
     * binary search for the region
     * and linear interpolate the answer
     */
    override fun getDiff(t: Double): Double {
        var timeParameter = 0.5
        var range = 0.5
        while (range > SD_ERROR) {
            val tx = getX(timeParameter)
            range *= 0.5
            if (tx < t) {
                timeParameter += range
            } else {
                timeParameter -= range
            }
        }

        val x1 = getX(timeParameter - range)
        val x2 = getX(timeParameter + range)
        val y1 = getY(timeParameter - range)
        val y2 = getY(timeParameter + range)
        return (y2 - y1) / (x2 - x1)
    }

    /**
     * binary search for the region
     * and linear interpolate the answer
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
            val tx = getX(time)
            range *= 0.5
            if (tx < t) {
                time += range
            } else {
                time -= range
            }
        }

        val x1 = getX(time - range)
        val x2 = getX(time + range)
        val y1 = getY(time - range)
        val y2 = getY(time + range)

        return (y2 - y1) * (t - x1) / (x2 - x1) + y1
    }

    companion object {
        private val STANDARD_COEFFICIENTS = floatArrayOf(0.4f, 0.0f, 0.2f, 1f)
        private val ACCELERATE_COEFFICIENTS = floatArrayOf(0.4f, 0.05f, 0.8f, 0.7f)
        private val DECELERATE_COEFFICIENTS = floatArrayOf(0.0f, 0.0f, 0.2f, 0.95f)
        private val LINEAR_COEFFICIENTS = floatArrayOf(1f, 1f, 0f, 0f)
        private val ANTICIPATE_COEFFICIENTS = floatArrayOf(0.36f, 0f, 0.66f, -0.56f)
        private val OVERSHOOT_COEFFICIENTS = floatArrayOf(0.34f, 1.56f, 0.64f, 1f)

        const val DECELERATE_NAME: String = "decelerate"
        const val ACCELERATE_NAME: String = "accelerate"
        const val STANDARD_NAME: String = "standard"
        const val LINEAR_NAME: String = "linear"
        const val ANTICIPATE_NAME: String = "anticipate"
        const val OVERSHOOT_NAME: String = "overshoot"

        // public static final CubicEasing STANDARD = new CubicEasing(STANDARD_COEFFICIENTS);
        // public static final CubicEasing ACCELERATE = new CubicEasing(ACCELERATE_COEFFICIENTS);
        val DECELERATE: MaterialEasing = MaterialEasing(DECELERATE_COEFFICIENTS)
        val LINEAR: MaterialEasing = MaterialEasing(LINEAR_COEFFICIENTS)

        // public static final CubicEasing ANTICIPATE = new CubicEasing(ANTICIPATE_COEFFICIENTS);
        val OVERSHOOT: MaterialEasing = MaterialEasing(OVERSHOOT_COEFFICIENTS)
        val EASE_OUT_SINE: MaterialEasing = MaterialEasing(floatArrayOf(0.61f, 1f, 0.88f, 1f))
        val EASE_OUT_CUBIC: MaterialEasing = MaterialEasing(floatArrayOf(0.33f, 1f, 0.68f, 1f))
        val EASE_OUT_QUINT: MaterialEasing = MaterialEasing(floatArrayOf(0.22f, 1f, 0.36f, 1f))
        val EASE_OUT_CIRC: MaterialEasing = MaterialEasing(floatArrayOf(0.02f, 0.55f, 0.45f, 1f))
        val EASE_OUT_QUAD: MaterialEasing = MaterialEasing(floatArrayOf(0.5f, 1f, 0.89f, 1f))
        val EASE_OUT_QUART: MaterialEasing = MaterialEasing(floatArrayOf(0.25f, 1f, 0.5f, 1f))
        val EASE_OUT_EXPO: MaterialEasing = MaterialEasing(floatArrayOf(0.16f, 1f, 0.3f, 1f))
        val EASE_OUT_BACK: MaterialEasing = MaterialEasing(floatArrayOf(0.34f, 1.56f, 0.64f, 1f))
        val EASE_OUT_ELASTIC: MaterialVelocity.Easing = EaseOutElastic()


        val EASE_OUT_BOUNCE: MaterialVelocity.Easing = EaseOutBounce()


        private const val S_ERROR = 0.001
        private const val SD_ERROR = 0.0001
    }
}
