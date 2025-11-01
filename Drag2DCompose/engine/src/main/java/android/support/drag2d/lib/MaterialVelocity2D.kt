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

import kotlin.math.sign

class MaterialVelocity2D : MaterialVelocity() {

    fun fixed(slower: MaterialVelocity2D) {
        val currentPos: Float = startPos
        val destination: Float = endPos
        var currentVelocity: Float = startV
        val duration: Float = slower.mDuration
        val dir: Float = sign(x = destination - currentPos)
        mDuration = slower.mDuration
        mNumberOfStages = slower.mNumberOfStages
        var stages: Int = mNumberOfStages
        var t1: Float = mDuration / 2
        if (stages == 1) {
            mNumberOfStages = 2
            stages = mNumberOfStages
        } else {
            t1 = slower.mStage[0].endTime
        }
        if (currentVelocity.toDouble() == 0.0) { // so we do not need to div by 0 all over the place
            currentVelocity = 0.0001f * dir
        }
        when (stages) {
            1 -> {
                fixeRampDown(
                    currentPos = currentPos,
                    destination = destination,
                    currentVelocity = currentVelocity,
                    duration = duration
                )
            }

            2 -> {
                fixeRampUpRampDown(
                    currentPos = currentPos,
                    destination = destination,
                    currentVelocity = currentVelocity,
                    duration = duration,
                    t1 = t1
                )
            }

            3 -> {
                fixe3Ramp(
                    currentPos = currentPos,
                    destination = destination,
                    currentVelocity = currentVelocity,
                    duration = duration,
                    t1 = slower.mStage[0].endTime,
                    t2 = slower.mStage[1].endTime
                )
            }
        }
        slower.configureEasingAdapter()
        configureEasingAdapter()
    }

    private fun fixeRampDown(
        currentPos: Float, destination: Float, currentVelocity: Float,
        duration: Float
    ) {
        @Suppress("UnusedVariable", "unused")
        val distance: Float = 2 * duration / currentVelocity
        val timeToDestination: Float = 2 * ((destination - currentPos) / currentVelocity)
        mNumberOfStages = 1
        mStage[0].setUp(
            startV = currentVelocity,
            startPos = currentPos,
            startTime = 0f,
            endV = 0f,
            endPos = destination,
            endTime = timeToDestination
        )
        mDuration = timeToDestination
    }

    private fun fixeRampUpRampDown(
        currentPos: Float,
        destination: Float,
        currentVelocity: Float,
        duration: Float,
        t1: Float
    ) {
        val maxV: Float = ((destination - currentPos) * 2 - currentVelocity * t1) / duration
        val d1: Float = currentPos + (currentVelocity + maxV) * t1 / 2

        mNumberOfStages = 2
        mStage[0].setUp(
            startV = currentVelocity,
            startPos = currentPos,
            startTime = 0f,
            endV = maxV,
            endPos = d1,
            endTime = t1
        )
        mStage[1].setUp(
            startV = maxV,
            startPos = d1,
            startTime = t1,
            endV = 0f,
            endPos = destination,
            endTime = duration
        )
        mDuration = duration
    }

    fun sync(m: MaterialVelocity2D) {
        if (isDominant(m)) {
            fixed(slower = m)
        } else {
            m.fixed(slower = this)
        }
    }

    fun isDominant(m: MaterialVelocity2D): Boolean {
        if (m.mNumberOfStages == mNumberOfStages) {
            return mDuration < m.mDuration
        }
        return mNumberOfStages < m.mNumberOfStages
    }

    private fun fixe3Ramp(
        currentPos: Float, destination: Float, currentVelocity: Float,
        duration: Float, t1: Float, t2: Float
    ) {
        val distance: Float = destination - currentPos
        val dt2: Float = t2 - t1
        val dt3: Float = duration - t2
        val v1: Float = (2 * distance - currentVelocity * t1) / (t1 + 2 * dt2 + dt3)

        val d1: Float = (currentVelocity + v1) * t1 / 2
        val d2: Float = (v1 + v1) * (t2 - t1) / 2
        mNumberOfStages = 3
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
            endTime = duration
        )
        mDuration = duration
    }

    init {
        oneDimension = false
    }
}
