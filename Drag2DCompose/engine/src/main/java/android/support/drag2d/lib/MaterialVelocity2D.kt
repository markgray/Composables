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

import kotlin.math.sign

class MaterialVelocity2D : MaterialVelocity() {

    fun fixed(slower: MaterialVelocity2D) {
        val currentPos = startPos
        val destination = endPos
        var currentVelocity = startV
        val duration = slower.mDuration
        val dir: Float = sign(destination - currentPos)
        mDuration = slower.mDuration
        mNumberOfStages = slower.mNumberOfStages
        var stages = mNumberOfStages
        var t1 = mDuration / 2
        if (stages == 1) {
            mNumberOfStages = 2
            stages = mNumberOfStages
        } else {
            t1 = slower.mStage[0].endTime
        }
        if (currentVelocity.toDouble() == 0.0) { // so we do not need to div by 0 all o ver the place
            currentVelocity = 0.0001f * dir
        }
        when (stages) {
            1 -> fixeRampDown(currentPos, destination, currentVelocity, duration)
            2 -> fixeRampUpRampDown(
                currentPos,
                destination,
                currentVelocity,
                duration,
                t1
            )

            3 -> fixe3Ramp(
                currentPos, destination, currentVelocity,
                duration,
                slower.mStage[0].endTime,
                slower.mStage[1].endTime
            )
        }

        slower.configureEasingAdapter()
        configureEasingAdapter()
    }

    private fun fixeRampDown(
        currentPos: Float, destination: Float, currentVelocity: Float,
        duration: Float
    ) {
        @Suppress("UnusedVariable", "unused")
        val distance = 2 * duration / currentVelocity
        val timeToDestination = 2 * ((destination - currentPos) / currentVelocity)
        mNumberOfStages = 1
        mStage[0].setUp(currentVelocity, currentPos, 0f, 0f, destination, timeToDestination)
        mDuration = timeToDestination
    }


    private fun fixeRampUpRampDown(
        currentPos: Float,
        destination: Float,
        currentVelocity: Float,
        duration: Float,
        t1: Float
    ) {
        val maxV = ((destination - currentPos) * 2 - currentVelocity * t1) / duration
        val d1 = currentPos + (currentVelocity + maxV) * t1 / 2

        mNumberOfStages = 2
        mStage[0].setUp(currentVelocity, currentPos, 0f, maxV, d1, t1)
        mStage[1].setUp(maxV, d1, t1, 0f, destination, duration)
        mDuration = duration
    }

    fun sync(m: MaterialVelocity2D) {
        if (isDominant(m)) {
            fixed(m)
        } else {
            m.fixed(this)
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
        val distance = destination - currentPos
        val dt2 = t2 - t1
        val dt3 = duration - t2
        val v1 = (2 * distance - currentVelocity * t1) / (t1 + 2 * dt2 + dt3)

        val d1 = (currentVelocity + v1) * t1 / 2
        val d2 = (v1 + v1) * (t2 - t1) / 2
        mNumberOfStages = 3
        mStage[0].setUp(currentVelocity, currentPos, 0f, v1, currentPos + d1, t1)
        mStage[1].setUp(v1, currentPos + d1, t1, v1, currentPos + d1 + d2, t2)
        mStage[2].setUp(v1, currentPos + d1 + d2, t2, 0f, destination, duration)
        mDuration = duration
    }

    init {
        oneDimension = false
    }
}
