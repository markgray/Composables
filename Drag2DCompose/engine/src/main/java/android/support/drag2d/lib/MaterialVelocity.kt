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
    "ReplaceNotNullAssertionWithElvisReturn", "ReplacePrintlnWithLogging",
    "MemberVisibilityCanBePrivate"
)

package android.support.drag2d.lib

import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * This class calculates the velocity and position of an object over time,
 * following a "material" motion profile.
 *
 * The motion is typically divided into several stages:
 *  1.  Acceleration: The object speeds up.
 *  2.  Cruise: The object moves at a constant velocity.
 *  3.  Deceleration: The object slows down to a stop at the destination.
 *
 * This class can calculate the necessary motion profile based on constraints like
 * maximum velocity, maximum acceleration, and a maximum time to reach the destination.
 * It can also incorporate a custom Easing function for the final stage of motion.
 *
 * The primary methods are `config()` to set up the motion profile, and `getPos(t)`
 * and `getV(t)` to get the position and velocity at a given time `t`.
 */
open class MaterialVelocity {
    /**
     * The starting position of the animation. This is typically set to the current position of the
     * object being animated when the animation is configured.
     */
    open var startPos: Float = 0f

    /**
     * The starting velocity of the object.
     */
    open var startV: Float = 0f

    /**
     * The final position of the motion.
     * This is typically the destination or target position for the object. It's configured
     * in the `config` method and used to calculate the motion profile.
     */
    open var endPos: Float = 0f

    /**
     * The duration of the motion in seconds.
     * This is computed by the `config` method.
     */
    open var mDuration: Float = 0f

    /**
     * An array of `Stage` objects that define the motion profile.
     * The motion is broken down into a sequence of stages (acceleration, cruise, deceleration).
     * Each stage defines the motion over a specific time interval. The number of
     * stages used is determined by the `config` method and stored in `mNumberOfStages`.
     * This array is pre-allocated with 3 stages, which is the maximum number typically used.
     */
    open val mStage: Array<Stage> = arrayOf<Stage>(Stage(n = 1), Stage(n = 2), Stage(n = 3))

    /**
     * The number of stages in the motion profile.
     * This is computed by the `config` method and can be 1, 2, or 3, depending
     * on the motion profile calculated (e.g., ramp-down only, ramp-up then ramp-down, etc.).
     */
    open var mNumberOfStages: Int = 0

    /**
     * An optional Easing function to be applied to the motion.
     * If provided, this easing function will be used to calculate the position
     * and velocity for the final stage of the motion, replacing the default
     * linear deceleration. The `config` method sets up an adapter to ensure
     * the easing function starts with the correct initial velocity and covers
     * the required distance.
     */
    protected open var mEasing: Easing? = null

    /**
     * The total distance to be covered during the easing phase of the motion.
     *
     * When an `Easing` function is used, the final stage of the motion profile is adapted
     * to smoothly transition from the previous stage's velocity into the easing curve.
     * This property stores the total displacement that occurs during this final easing stage.
     * It's calculated in `configureEasingAdapter` and used by `getEasing` to scale the
     * output of the easing function to the correct distance.
     *
     * @see configureEasingAdapter
     * @see getEasing
     */
    protected open var mEasingAdapterDistance: Double = 0.0

    /**
     * The quadratic coefficient for the time-warping function applied to the easing curve.
     *
     * When an `Easing` function is used, a time-warping function `g(t) = At^2 + Bt` is
     * applied to the input of the easing function to match the initial velocity of the
     * motion's final stage. This ensures a smooth transition into the easing curve.
     * `mEasingAdapterA` represents the 'A' coefficient in this quadratic equation.
     * It is calculated in `configureEasingAdapter` to ensure that `g(t)` transitions
     * from the correct starting velocity and covers the full range 0.0 to 1.0 over the
     * adapted duration of the easing stage.
     *
     * @see configureEasingAdapter
     * @see mEasingAdapterB
     */
    protected open var mEasingAdapterA: Double = 0.0

    /**
     * The linear coefficient for the time-warping function applied to the easing curve.
     *
     * When an `Easing` function is used, a time-warping function `g(t) = At^2 + Bt` is
     * applied to the input of the easing function to match the initial velocity of the
     * motion's final stage. This ensures a smooth transition into the easing curve.
     * `mEasingAdapterB` represents the 'B' coefficient in this quadratic equation.
     * It is calculated in `configureEasingAdapter` to ensure that `g'(0)` matches the
     * desired initial velocity for the easing portion of the animation.
     *
     * @see configureEasingAdapter
     * @see mEasingAdapterA
     */
    protected open var mEasingAdapterB: Double = 0.0

    /**
     * A flag that indicates if the motion should be constrained to one dimension,
     * allowing for more complex motion profiles.
     *
     * When `true`, the `config` method will attempt to find a suitable motion profile
     * using various strategies (e.g., `cruseThenRampDown`, `rampUpRampDown`, etc.).
     *
     * If set to `false`, the system will only use the simplest `rampDown` profile,
     * which may not be suitable for all scenarios but can be useful for simpler
     * or more controlled animations. This can also prevent the motion from
     * temporarily moving in the opposite direction of the target.
     */
    protected open var oneDimension: Boolean = true

    /**
     * The total duration of the animation when an easing function is applied.
     *
     * This value is calculated in `configureEasingAdapter` and represents the sum of the
     * durations of the initial motion stages plus the adapted duration of the final
     * easing stage. The easing stage's duration may be adjusted by a time-warping
     * function to ensure a smooth velocity transition, so this property stores the
     * final, potentially altered, total time.
     *
     * @see configureEasingAdapter
     * @see duration
     */
    private var mTotalEasingDuration = 0f

    /**
     * The total duration of the motion in seconds.
     *
     * This value is computed by the `config` method based on the calculated motion profile.
     * If a custom `Easing` function is provided (`mEasing` is not null), the duration might be
     * adjusted to account for the time-warping applied to the easing curve. In this case,
     * this property will return the adjusted total duration (`mTotalEasingDuration`). Otherwise, it
     * returns the duration calculated for the standard multi-stage motion profile (`mDuration`).
     *
     * @see mDuration
     * @see mTotalEasingDuration
     * @see config
     */
    val duration: Float
        get() {
            if (mEasing != null) {
                return mTotalEasingDuration
            }
            return mDuration
        }

    /**
     * Represents a single stage of motion within the overall profile.
     * A motion profile is typically composed of one or more stages, such as
     * acceleration, cruise, and deceleration. Each stage defines a segment
     * of motion with a constant acceleration (which can be zero).
     *
     * This class stores the start and end conditions (time, position, velocity)
     * for its segment and provides methods to calculate the interpolated position
     * and velocity at any given time `t` within that stage.
     *
     * @param n An identifier for the stage (e.g., 1, 2, or 3).
     */
    class Stage(val n: Int) {
        /**
         * The initial velocity at the beginning of this motion stage.
         * It's set in the `setUp` method and is used to calculate the velocity
         * and position at any point within this stage's time interval.
         */
        var mStartV: Float = 0f

        /**
         * The initial position at the beginning of this motion stage.
         * It's set in the `setUp` method and serves as the starting point
         * for position calculations within this stage's time interval.
         */
        var mStartPos: Float = 0f

        /**
         * The start time of this stage in seconds, relative to the beginning of the entire motion.
         * It's set in the `setUp` method and marks the point in time when this stage begins.
         */
        var mStartTime: Float = 0f

        /**
         * The final velocity at the end of this motion stage.
         * It's set in the `setUp` method and marks the target velocity for this
         * stage's time interval.
         */
        var mEndV: Float = 0f

        /**
         * The position at the end of this motion stage.
         * It's set in the `setUp` method and marks the target position
         * that the object will reach at the `endTime` of this stage.
         */
        var mEndPos: Float = 0f

        /**
         * The end time of this stage in seconds, relative to the beginning of the entire motion.
         * It's set in the `setUp` method and marks the point in time when this stage ends.
         */
        var endTime: Float = 0f

        /**
         * The change in velocity over the duration of this stage.
         * It is calculated as `mEndV - mStartV` in the `setUp` method.
         */
        var mDeltaV: Float = 0f

        /**
         * The duration of this stage in seconds (`endTime` - `mStartTime`).
         * This is calculated in the `setUp` method and is used in position and
         * velocity calculations to determine the progress (`pt`) within the stage.
         */
        var mDeltaT: Float = 0f

        /**
         * Sets up the parameters for this motion stage.
         *
         * This function initializes the start and end conditions (position, velocity, and time)
         * for a specific segment of the overall motion profile. It also pre-calculates the
         * change in velocity and time (`mDeltaV`, `mDeltaT`) for use in interpolation.
         * TODO: Continue here.
         * @param startV The velocity at the beginning of the stage.
         * @param startPos The position at the beginning of the stage.
         * @param startTime The time (relative to the total animation) when this stage begins.
         * @param endV The velocity at the end of the stage.
         * @param endPos The position at the end of the stage.
         * @param endTime The time (relative to the total animation) when this stage ends.
         */
        fun setUp(
            startV: Float,
            startPos: Float,
            startTime: Float,
            endV: Float,
            endPos: Float,
            endTime: Float
        ) {
            mStartV = startV
            mStartPos = startPos
            mStartTime = startTime
            mEndV = endV
            this.endTime = endTime
            mEndPos = endPos
            mDeltaV = mEndV - mStartV
            mDeltaT = this.endTime - mStartTime
        }

        fun getPos(t: Float): Float {
            val dt: Float = t - mStartTime
            val pt: Float = dt / (mDeltaT)
            val v: Float = mStartV + (mDeltaV) * pt
            return dt * (mStartV + v) / 2 + mStartPos
        }

        fun getVel(t: Float): Float {
            val dt: Float = t - mStartTime
            val pt: Float = dt / (this.endTime - mStartTime)
            return mStartV + (mDeltaV) * pt
        }
    }

    fun getV(t: Float): Float {
        if (mEasing == null) {
            for (i in 0..<mNumberOfStages) {
                if (mStage[i].endTime > t) {
                    return mStage[i].getVel(t)
                }
            }
            return 0f
        }
        val lastStages: Int = mNumberOfStages - 1
        for (i in 0..<lastStages) {
            if (mStage[i].endTime > t) {
                return mStage[i].getVel(t)
            }
        }
        return getEasingDiff(t = (t - mStage[lastStages].mStartTime).toDouble()).toFloat()
    }

    fun getPos(t: Float): Float {
        if (mEasing == null) {
            for (i in 0..<mNumberOfStages) {
                if (mStage[i].endTime > t) {
                    return mStage[i].getPos(t)
                }
            }
            return this.endPos
        }

        val lastStages: Int = mNumberOfStages - 1
        for (i in 0..<lastStages) {
            if (mStage[i].endTime > t) {
                return mStage[i].getPos(t)
            }
        }

        var ret: Float = getEasing((t - mStage[lastStages].mStartTime).toDouble()).toFloat()
        ret += mStage[lastStages].mStartPos
        return ret
    }

    override fun toString(): String {
        var s = " "
        for (i in 0..<mNumberOfStages) {
            val stage: Stage = mStage[i]
            s += " $i $stage"
        }
        return s
    }

    interface Easing {
        fun get(t: Double): Double

        fun getDiff(t: Double): Double

        fun clone(): Easing?
    }

    fun config(
        currentPos: Float, destination: Float, currentVelocity: Float,
        maxTime: Float, maxAcceleration: Float, maxVelocity: Float, easing: Easing?
    ) {
        var currentPos: Float = currentPos
        var currentVelocity: Float = currentVelocity
        if (currentPos == destination) {
            currentPos += 1f
        }
        this.startPos = currentPos
        this.endPos = destination

        mEasing = easing?.clone()

        val dir: Float = (destination - currentPos) / abs(destination - currentPos)
        val maxV: Float = maxVelocity * dir
        val maxA: Float = maxAcceleration * dir

        if (currentVelocity.toDouble() == 0.0) {
            currentVelocity = 0.0001f * dir
        }

        this.startV = currentVelocity

        if (!rampDown(currentPos, destination, currentVelocity, maxTime)) {
            if (!(oneDimension && cruseThenRampDown(
                    currentPos = currentPos,
                    destination = destination,
                    currentVelocity = currentVelocity,
                    maxTime = maxTime,
                    maxA = maxA,
                    maxV = maxV
                ))
            ) {
                if (!rampUpRampDown(
                        currentPos = currentPos,
                        destination = destination,
                        currentVelocity = currentVelocity,
                        maxA = maxA,
                        maxVelocity = maxV,
                        maxTime = maxTime
                    )
                ) {
                    rampUpCruseRampDown(
                        currentPos = currentPos,
                        destination = destination,
                        currentVelocity = currentVelocity,
                        maxA = maxA,
                        maxV = maxV,
                        maxTime = maxTime
                    )
                }
            }
        }
        if (oneDimension) {
            configureEasingAdapter()
        }
    }

    private fun rampDown(
        currentPos: Float, destination: Float, currentVelocity: Float,
        maxTime: Float
    ): Boolean {
        val timeToDestination: Float = 2 * ((destination - currentPos) / currentVelocity)
        if (timeToDestination > 0 && timeToDestination <= maxTime) { // hit the brakes
            mNumberOfStages = 1
            mStage[0].setUp(currentVelocity, currentPos, 0f, 0f, destination, timeToDestination)
            mDuration = timeToDestination
            return true
        }
        return false
    }

    @Suppress("unused")
    private fun cruseThenRampDown(
        currentPos: Float, destination: Float, currentVelocity: Float,
        maxTime: Float, maxA: Float, maxV: Float
    ): Boolean {
        val timeToBreak: Float = currentVelocity / maxA
        val brakeDist: Float = currentVelocity * timeToBreak / 2
        val cruseDist: Float = (destination - currentPos) - brakeDist
        val cruseTime: Float = cruseDist / currentVelocity
        val totalTime: Float = cruseTime + timeToBreak

        if (totalTime > 0 && totalTime < maxTime) {
            mNumberOfStages = 2
            mStage[0].setUp(
                startV = currentVelocity,
                startPos = currentPos,
                startTime = 0f,
                endV = currentVelocity,
                endPos = cruseDist,
                endTime = cruseTime
            )
            mStage[1].setUp(
                startV = currentVelocity,
                startPos = currentPos + cruseDist,
                startTime = cruseTime,
                endV = 0f,
                endPos = destination,
                endTime = cruseTime + timeToBreak
            )
            mDuration = cruseTime + timeToBreak
            return true
        }
        return false
    }

    private fun rampUpRampDown(
        currentPos: Float, destination: Float, currentVelocity: Float,
        maxA: Float, maxVelocity: Float, maxTime: Float
    ): Boolean {
        var peakV: Float =
            sign(x = maxA) * sqrt(x = maxA * (destination - currentPos) + currentVelocity * currentVelocity / 2)
        println(">>>>>>>>>  peak $peakV $maxVelocity")
        if (maxVelocity / peakV > 1) {
            var t1: Float = (peakV - currentVelocity) / maxA
            var d1: Float = (peakV + currentVelocity) * t1 / 2 + currentPos
            var t2: Float = peakV / maxA
            mNumberOfStages = 2
            mStage[0].setUp(
                startV = currentVelocity,
                startPos = currentPos,
                startTime = 0f,
                endV = peakV,
                endPos = d1,
                endTime = t1
            )
            mStage[1].setUp(
                startV = peakV,
                startPos = d1,
                startTime = t1,
                endV = 0f,
                endPos = destination,
                endTime = t2 + t1
            )
            mDuration = t2 + t1
            if (mDuration > maxTime) {
                return false
            }
            println(">>>>>>>>>  rampUpRampDown $mDuration  $maxTime")

            if (mDuration < maxTime / 2) {
                t1 = mDuration / 2
                t2 = t1
                peakV = (2 * (destination - currentPos) / t1 - currentVelocity) / 2
                d1 = (peakV + currentVelocity) * t1 / 2 + currentPos

                mNumberOfStages = 2
                mStage[0].setUp(
                    startV = currentVelocity,
                    startPos = currentPos,
                    startTime = 0f,
                    endV = peakV,
                    endPos = d1,
                    endTime = t1
                )
                mStage[1].setUp(
                    startV = peakV,
                    startPos = d1,
                    startTime = t1,
                    endV = 0f,
                    endPos = destination,
                    endTime = t2 + t1
                )
                mDuration = t2 + t1
                println(">>>>>>>>>f rampUpRampDown $mDuration  $maxTime")
                println(">>>>>>>>>f           peak $peakV $maxVelocity")

                if (mDuration > maxTime) {
                    println(" fail ")
                    return false
                }
            }
            return true
        }
        return false
    }

    @Suppress("unused")
    private fun rampUpCruseRampDown(
        currentPos: Float, destination: Float, currentVelocity: Float,
        maxA: Float, maxV: Float, maxTime: Float
    ) {
//        float t1 = (maxV - currentVelocity) / maxA;
//        float d1 = (maxV + currentVelocity) * t1 / 2 + currentPos;
//        float t3 = maxV / maxA;
//        float d3 = (maxV) * t3 / 2;
//        float d2 = destination - d1 - d3;
//        float t2 = d2 / maxV;
//
//        mNumberOfStages = 3;
//        mStage[0].setUp(currentVelocity, currentPos, 0, maxV, d1, t1);
//        mStage[1].setUp(maxV, d1, t1, maxV, d2 + d1, t2 + t1);
//        mStage[2].setUp(maxV, d1 + d2, t1 + t2, 0, destination, t2 + t1 + t3);
//        mDuration = t3 + t2 + t1;
        val t1: Float = maxTime / 3
        val t2: Float = t1 * 2
        val distance: Float = destination - currentPos
        val dt2: Float = t2 - t1
        val dt3: Float = maxTime - t2
        val v1: Float = (2 * distance - currentVelocity * t1) / (t1 + 2 * dt2 + dt3)
        mDuration = maxTime
        val d1: Float = (currentVelocity + v1) * t1 / 2
        val d2: Float = (v1 + v1) * (t2 - t1) / 2
        mNumberOfStages = 3
        val acc: Float = (v1 - currentVelocity) / t1
        val dec: Float = (v1) / dt3
        println(" >>>>>> $acc /  $v1 \\ $dec")

        mStage[0].setUp(
            startV = currentVelocity,
            startPos = currentPos,
            startTime = 0f,
            endV = v1,
            endPos = currentPos + d1,
            endTime = t1
        )
        mStage[1].setUp(
            startV = v1,
            startPos = currentPos + d1,
            startTime = t1,
            endV = v1,
            endPos = currentPos + d1 + d2,
            endTime = t2
        )
        mStage[2].setUp(
            startV = v1,
            startPos = currentPos + d1 + d2,
            startTime = t2,
            endV = 0f,
            endPos = destination,
            endTime = maxTime
        )
        mDuration = maxTime
    }

    fun getEasing(t: Double): Double {
        val gx: Double = t * t * mEasingAdapterA + t * mEasingAdapterB
        if (gx > 1) {
            return mEasingAdapterDistance
        }
        return mEasing!!.get(t = gx) * mEasingAdapterDistance
    }

    private fun getEasingDiff(t: Double): Double {
        val gx: Double = t * t * mEasingAdapterA + t * mEasingAdapterB
        if (gx > 1) {
            return 0.0
        }
        return mEasing!!.getDiff(t = gx) * mEasingAdapterDistance * ((t * mEasingAdapterA) + mEasingAdapterB)
    }

    protected fun configureEasingAdapter() {
        if (mEasing == null) {
            return
        }
        val last: Int = mNumberOfStages - 1
        val initialVelocity: Float = mStage[last].mStartV
        val distance: Float = mStage[last].mEndPos - mStage[last].mStartPos

        @Suppress("UnusedVariable", "unused")
        val duration: Float = mStage[last].endTime - mStage[last].mStartTime
        val baseVel: Double = mEasing!!.getDiff(0.0)

        mEasingAdapterB = initialVelocity / (baseVel * distance)
        mEasingAdapterA = 1 - mEasingAdapterB
        mEasingAdapterDistance = distance.toDouble()
        val easingDuration: Double =
            (sqrt(x = 4 * mEasingAdapterA + mEasingAdapterB * mEasingAdapterB) - mEasingAdapterB) /
                (2 * mEasingAdapterA)
        mTotalEasingDuration = (easingDuration + mStage[last].mStartTime).toFloat()
    }
}
