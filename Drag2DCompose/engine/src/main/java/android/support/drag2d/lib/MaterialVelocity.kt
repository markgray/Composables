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

open class MaterialVelocity {
    open var startPos: Float = 0f
    open var startV: Float = 0f
    open var endPos: Float = 0f
    open var mDuration: Float = 0f
    open val mStage: Array<Stage> = arrayOf<Stage>(Stage(1), Stage(2), Stage(3))
    open var mNumberOfStages: Int = 0
    protected open var mEasing: Easing? = null
    protected open var mEasingAdapterDistance: Double = 0.0
    protected open var mEasingAdapterA: Double = 0.0
    protected open var mEasingAdapterB: Double = 0.0
    protected open var oneDimension: Boolean = true
    private var mTotalEasingDuration = 0f

    @Suppress("unused")
    val duration: Float
        get() {
            if (mEasing != null) {
                return mTotalEasingDuration
            }
            return mDuration
        }

    @Suppress("unused")
    class Stage(val n: Int) {
        var mStartV: Float = 0f
        var mStartPos: Float = 0f
        var mStartTime: Float = 0f
        var mEndV: Float = 0f
        var mEndPos: Float = 0f
        var endTime: Float = 0f
        var mDeltaV: Float = 0f
        var mDeltaT: Float = 0f

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
