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

/**
 * This class is used to calculate the motion of an object in 2D.
 *
 * It is used to synchronize the motion in the x and y directions.
 * One dimension (x or y) will be dominant and the other will be servient.
 * The dominant dimension will be the one that takes longer to complete its motion.
 * The servient dimension will be adjusted to take the same amount of time as the dominant
 * dimension.
 */
class MaterialVelocity2D : MaterialVelocity() {

    /**
     * Adjusts the velocity profile of this `MaterialVelocity2D` instance to match the duration
     * and number of stages of a "slower" animation.
     *
     * This function is typically called as part of a synchronization process (see [sync]) where one
     * animation (the "dominant" one) dictates the timing for another (the "slower" one).
     * This method modifies the current instance's animation parameters (like velocity peaks)
     * to ensure it reaches its destination in the exact same duration as the `slower` instance,
     * using the same number of acceleration/deceleration stages.
     *
     * It calculates the necessary velocities for 1, 2, or 3-stage ramps (ramp-down,
     * ramp-up/ramp-down, or ramp-up/coast/ramp-down) to fit the new time constraints.
     *
     * @param slower The `MaterialVelocity2D` instance that provides the target duration and stage
     * count. This instance's timing constraints will be imposed on the current instance.
     */
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

    /**
     * Calculates a single-stage ramp-down (constant deceleration) profile to reach the destination.
     * This is used when the animation needs to be synchronized with a dominant animation and the
     * profile consists of only deceleration.
     *
     * The method calculates the time required to decelerate from the `currentVelocity` to zero,
     * ending exactly at the `destination`. This calculated time then becomes the new duration
     * for this motion profile.
     *
     * Note: The `duration` parameter is passed but not directly used to set the final duration.
     * Instead, the time is recalculated based on the physics of constant deceleration. The `distance`
     * variable is also calculated but currently unused.
     *
     * @param currentPos The starting position of the object.
     * @param destination The target position of the object.
     * @param currentVelocity The initial velocity of the object.
     * @param duration The target duration from the dominant animation (currently unused in calculation).
     */
    private fun fixeRampDown(
        currentPos: Float,
        destination: Float,
        currentVelocity: Float,
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

    /**
     * Calculates and sets up a two-stage motion profile: acceleration followed by deceleration.
     *
     * This function is used to ensure the object reaches the specified `destination` in the
     * exact `duration` provided, by first ramping up the velocity and then ramping it down to zero.
     * The transition from acceleration to deceleration happens at time `t1`.
     *
     * The method calculates the peak velocity (`maxV`) required to cover the distance within the
     * given time constraints. It then configures two `Stage` objects:
     *  1.  An acceleration phase from the `currentVelocity` to `maxV`.
     *  2.  A deceleration phase from `maxV` to zero.
     *
     * @param currentPos The starting position of the object.
     * @param destination The target position of the object.
     * @param currentVelocity The initial velocity of the object.
     * @param duration The total time the motion should take.
     * @param t1 The time at which the first stage (acceleration) ends and the second stage
     *   (deceleration) begins.
     */
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

    /**
     * Synchronizes the motion profiles of two `MaterialVelocity2D` instances.
     *
     * This function determines which of the two animations (the current instance or the provided
     * instance `m`) is "dominant" based on its duration and number of motion stages. The dominant
     * animation is the one that takes longer to complete.
     *
     * The servient (non-dominant) animation's profile is then adjusted using the [fixed] method
     * to match the duration and stage count of the dominant one. This ensures that both animations
     * start and end at the same time, providing a synchronized 2D motion.
     *
     * @param m The other `MaterialVelocity2D` instance to synchronize with.
     * @see isDominant
     * @see fixed
     */
    fun sync(m: MaterialVelocity2D) {
        if (isDominant(m)) {
            fixed(slower = m)
        } else {
            m.fixed(slower = this)
        }
    }

    /**
     * Determines if the provided `MaterialVelocity2D` instance `m` is "dominant" compared to this
     * instance.
     *
     * In the context of synchronizing two animations (e.g., for X and Y motion), the dominant
     * animation is the one that takes longer to complete. This function establishes the dominance
     * hierarchy.
     *
     * The criteria for dominance are, in order of priority:
     *  1.  **Number of Stages:** The animation with more motion stages (e.g., a 3-stage
     *  ramp-up/coast/ramp-down vs. a 2-stage ramp-up/ramp-down) is considered dominant.
     *  2.  **Duration:** If both animations have the same number of stages, the one with the longer
     *  `mDuration` is considered dominant.
     *
     * This function returns `true` if `m` is dominant over the current instance, and `false`
     * otherwise.
     *
     * @param m The other `MaterialVelocity2D` instance to compare against.
     * @return `true` if `m` is the dominant animation, `false` otherwise.
     */
    fun isDominant(m: MaterialVelocity2D): Boolean {
        if (m.mNumberOfStages == mNumberOfStages) {
            return mDuration < m.mDuration
        }
        return mNumberOfStages < m.mNumberOfStages
    }

    /**
     * Calculates and sets up a three-stage motion profile: acceleration, constant velocity (coast),
     * and deceleration.
     *
     * This function is used to ensure the object reaches the specified `destination` in the exact
     * `duration` provided, fitting a profile with three distinct phases. The timing for these
     * phases is dictated by `t1` and `t2`.
     *
     * The method calculates the peak velocity (`v1`) required during the coasting phase to cover
     * the `distance` within the given time constraints. It then configures three `Stage` objects:
     *  1. An acceleration phase from `currentVelocity` to `v1`, ending at time `t1`.
     *  2. A coasting phase at a constant velocity `v1`, from time `t1` to `t2`.
     *  3. A deceleration phase from `v1` to zero, from time `t2` to `duration`.
     *
     * @param currentPos The starting position of the object.
     * @param destination The target position of the object.
     * @param currentVelocity The initial velocity of the object.
     * @param duration The total time the motion should take.
     * @param t1 The time at which the first stage (acceleration) ends.
     * @param t2 The time at which the second stage (coasting) ends.
     */
    private fun fixe3Ramp(
        currentPos: Float,
        destination: Float,
        currentVelocity: Float,
        duration: Float,
        t1: Float,
        t2: Float
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
