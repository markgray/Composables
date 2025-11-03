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
@file:Suppress("ReplacePrintlnWithLogging", "MemberVisibilityCanBePrivate")

package android.support.drag2d.lib

import java.util.Arrays
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * This class calculates the 2D motion of an object.
 * It is composed of two 1D motion calculations (one for X and one for Y),
 * which are then synchronized.
 *
 * This allows for creating complex 2D animations (e.g., for drag and fling gestures)
 * that adhere to physical constraints like maximum velocity and acceleration.
 * The motion profiles for each axis are calculated independently by [MaterialVelocity2D]
 * and then synchronized to ensure the total duration is consistent.
 *
 * It provides methods to get the position and velocity at any given time `t`
 * along the animation path.
 */
class Velocity2D {
    /**
     * The motion profile for the X-axis. This is a 1D motion calculation
     * that will be synchronized with [mvY].
     */
    val mvX: MaterialVelocity2D = MaterialVelocity2D()

    /**
     * The [MaterialVelocity2D] object responsible for calculating the motion along the Y-axis.
     * It computes the position and velocity for the vertical component of the 2D movement.
     * After configuration, it is synchronized with [mvX] to ensure a consistent
     * animation duration across both axes.
     */
    val mvY: MaterialVelocity2D = MaterialVelocity2D()

    /**
     * Configures the 2D motion profile.
     *
     * This function sets up the motion for both the X and Y axes based on the provided
     * initial conditions, destination, and physical constraints. It first ensures the initial
     * velocity does not exceed the specified maximum velocity ([maxV]). Then, it configures
     * the independent [MaterialVelocity2D] instances for each axis ([mvX] and [mvY]).
     * Finally, it synchronizes the two motion profiles to ensure they have the same total duration,
     * adjusting their acceleration/deceleration phases accordingly.
     *
     * @param posX The starting X position.
     * @param posY The starting Y position.
     * @param velocityX The initial velocity in the X direction.
     * @param velocityY The initial velocity in the Y direction.
     * @param destinationX The target X position.
     * @param destinationY The target Y position.
     * @param duration The desired duration of the motion in seconds. The actual duration might be
     * adjusted if the physical constraints cannot be met within this time.
     * @param maxV The maximum allowed velocity for the combined (hypotenuse) speed.
     * The individual axis velocities are scaled down if this is exceeded.
     * @param maxA The maximum allowed acceleration for each axis.
     * @param easing An optional easing function to apply to the motion. If `null`, a default
     * linear easing is used.
     */
    fun configure(
        posX: Float,
        posY: Float,
        velocityX: Float,
        velocityY: Float,
        destinationX: Float,
        destinationY: Float,
        duration: Float,
        maxV: Float,
        maxA: Float,
        easing: MaterialVelocity.Easing?
    ) {
        var velocityX: Float = velocityX
        var velocityY: Float = velocityY
        val speed: Double = hypot(velocityX, velocityY).toDouble()
        if (speed > maxV) {
            velocityX *= (maxV / speed).toFloat()
            velocityY *= (maxV / speed).toFloat()
        }
        mvX.config(
            currentPos = posX,
            destination = destinationX,
            currentVelocity = velocityX,
            maxTime = duration,
            maxAcceleration = maxA,
            maxVelocity = maxV,
            easing = easing
        )
        mvY.config(
            currentPos = posY,
            destination = destinationY,
            currentVelocity = velocityY,
            maxTime = duration,
            maxAcceleration = maxA,
            maxVelocity = maxV,
            easing = easing
        )
        mvX.sync(m = mvY)
    }

    /**
     * A private helper function for debugging purposes.
     * It prints detailed information about the motion profiles for both the X and Y axes
     * to the console. This includes duration, travel distance, and the specifics of each
     * motion stage (velocity, position, time).
     */
    @Suppress("unused")
    private fun checkCurves() {
        println(" --------x-------")
        dump(mv = mvX)
        println(" -------y--------")

        dump(mv = mvY)
        println("  ")
    }

    /**
     * Dumps the internal state of a [MaterialVelocity2D] object to the console for debugging.
     * This includes information about the total duration, start and end positions, and
     * a stage-by-stage breakdown of velocity, position, and time. It also calculates
     * and verifies the distance traveled in each stage.
     *
     * @param mv The [MaterialVelocity2D] instance to dump.
     */
    private fun dump(mv: MaterialVelocity2D) {
        println(" duration " + mv.duration)
        println(" travel " + mv.startPos + " -> " + mv.endPos)
        println(" NumberOfStages " + mv.mNumberOfStages)
        print("vel  ")

        for (i in 0..<mv.mNumberOfStages) {
            print(" | " + mv.mStage[i].mStartV + "  -> " + mv.mStage[i].mEndV)
        }
        println()
        print("pos  ")

        for (i in 0..<mv.mNumberOfStages) {
            print(" | " + mv.mStage[i].mStartPos + "  -> " + mv.mStage[i].mEndPos)
        }
        println()
        print("pos* ")
        for (i in 0..<mv.mNumberOfStages) {
            val t1: Float = mv.mStage[i].mStartTime + 0.001f
            val t2: Float = mv.mStage[i].endTime - 0.001f
            print(" | " + mv.getPos(t1) + "  -> " + mv.getPos(t2))
        }
        println()
        print("time ")

        for (i in 0..<mv.mNumberOfStages) {
            print(" | " + mv.mStage[i].mStartTime + "  -> " + mv.mStage[i].endTime)
        }
        println()
        print("dist ")

        for (i in 0..<mv.mNumberOfStages) {
            val dist: Float = mv.mStage[i].mEndPos - mv.mStage[i].mStartPos
            val dist2: Float =
                (mv.mStage[i].mStartV + mv.mStage[i].mEndV) *
                    (mv.mStage[i].endTime - mv.mStage[i].mStartTime) / 2
            print(" | $dist  == $dist2")
        }
        println()
    }

    /**
     * Gets the X position at a given time [t].
     * This function queries the motion profile for the X-axis ([mvX]) to determine
     * the object's horizontal position at the specified time in the animation.
     *
     * @param t The time in seconds from the start of the animation.
     * @return The X coordinate at time [t].
     */
    fun getX(t: Float): Float {
        return mvX.getPos(t = t)
    }

    /**
     * Gets the Y position at a given time [t].
     * This function queries the motion profile for the Y-axis ([mvY]) to determine
     * the object's vertical position at the specified time in the animation.
     *
     * @param t The time in seconds from the start of the animation.
     * @return The Y coordinate at time [t].
     */
    fun getY(t: Float): Float {
        return mvY.getPos(t = t)
    }

    /**
     * Retrieves the velocity along the X-axis at a specific time [t].
     * This function queries the underlying `MaterialVelocity2D` instance for the X-axis ([mvX])
     * to get the horizontal component of the velocity at the given moment in the animation.
     *
     * @param t The time in seconds from the start of the animation.
     * @return The velocity in the X direction at time [t].
     */
    fun getVX(t: Float): Float {
        return mvX.getV(t = t)
    }

    /**
     * Retrieves the velocity along the Y-axis at a specific time [t].
     * This function queries the underlying `MaterialVelocity2D` instance for the Y-axis ([mvY])
     * to get the vertical component of the velocity at the given moment in the animation.
     *
     * @param t The time in seconds from the start of the animation.
     * @return The velocity in the Y direction at time [t].
     */
    fun getVY(t: Float): Float {
        return mvY.getV(t = t)
    }

    /**
     * Checks if the animation is still in progress at a given time [t].
     * The animation is considered "still moving" if the given time [t] is less than the
     * total duration of either the X or Y component's motion profile.
     * Since both profiles are synchronized to the same maximum duration, this effectively
     * checks if [t] is within the bounds of the overall animation time.
     *
     * @param t The time in seconds to check, measured from the start of the animation.
     * @return `true` if the animation has not yet completed at time [t], `false` otherwise.
     */
    @Suppress("unused")
    fun isStillMoving(t: Float): Boolean {
        return mvX.duration > t || mvY.duration > t
    }

    /**
     * The total duration of the 2D motion in seconds.
     *
     * This is determined by taking the maximum of the individual durations calculated
     * for the X-axis (`mvX.duration`) and the Y-axis (`mvY.duration`). Since the
     * two motion profiles are synchronized, this ensures that the returned value
     * represents the time required for the entire 2D animation to complete.
     */
    val duration: Float
        get() = max(a = mvX.duration, b = mvY.mDuration)

    /**
     * A utility function, likely for debugging, that calculates an X-axis offset.
     * The calculation seems tailored for positioning elements within a graphical representation,
     * possibly a debug graph generated by [getCurves]. It takes a total length and a fraction
     * to compute a pixel offset, likely for plotting a point or a line segment.
     * The internal constants (e.g., 5*4, 8, 20) suggest it's designed for a specific
     * layout structure.
     *
     * @param len The total length or size of the container/array.
     * @param fraction A value, typically from 0.0 to 1.0, representing a position or progress.
     * @return An integer offset value calculated based on the inputs.
     */
    @Suppress("unused")
    fun getPointOffsetX(len: Int, fraction: Float): Int {
        val lines: Int = (len - 5 * 4) / 8
        var off: Int = (((len - 20).toFloat() / 8) * fraction).toInt()

        if (off >= lines) {
            off = lines - 2
        }
        return 20 + 4 * off
    }

    /**
     * A utility function for debugging purposes, likely used in conjunction with [getCurves]
     * to calculate a vertical offset for plotting points on a debug graph.
     * It seems to compute an offset based on a total length and a fraction, potentially
     * to position a point representing a specific stage or time in the motion profile.
     *
     * @param len The total length of the point buffer used for drawing.
     * @param fraction A value, likely between 0.0 and 1.0, representing a position
     * along the motion's duration.
     * @return The calculated Y-offset as an integer.
     */
    @Suppress("unused")
    fun getPointOffsetY(len: Int, fraction: Float): Int {
        val lines: Int = (len - 5 * 4) / 8
        var off: Int = (((len - 20).toFloat() / 8) * fraction).toInt()

        if (off >= lines) {
            off = lines - 2
        }
        return 20 + 4 * (lines + off)
    }

    /**
     * Populates two arrays with the start times of each motion stage for the X and Y axes.
     *
     * This function is typically used for debugging or visualization purposes to understand
     * the timing of the different acceleration and deceleration phases in the motion profiles.
     * It clears the provided arrays and then fills them with the start time of each stage
     * from [mvX] and [mvY]. Unused elements in the arrays are set to [Float.NaN].
     *
     * @param t1 An output array to be populated with the start times of the motion stages for the
     * X-axis ([mvX]). The size of this array should be sufficient to hold all stage times.
     * @param t2 An output array to be populated with the start times of the motion stages for the
     * Y-axis ([mvY]). The size of this array should be sufficient to hold all stage times.
     */
    @Suppress("unused")
    fun getCurvesSegments(t1: FloatArray, t2: FloatArray) {
        Arrays.fill(t1, Float.NaN)
        Arrays.fill(t2, Float.NaN)
        for (i in 0..<mvY.mNumberOfStages) {
            t2[i] = mvY.mStage[i].mStartTime
        }
        for (i in 0..<mvX.mNumberOfStages) {
            t1[i] = mvX.mStage[i].mStartTime
        }
    }

    /**
     * Generates a set of line segments to visualize the motion profiles for debugging.
     *
     * This function populates a [FloatArray] with (x, y) coordinates that represent the
     * position-time or velocity-time graphs for both the X and Y axes of the motion.
     * The output is formatted for direct use with `Canvas.drawLines()`. It plots two
     * curves: one for the X-axis motion and one for the Y-axis motion, scaled to fit
     * within a specified width and height.
     *
     * @param points An output array to be filled with line coordinates. The format is
     * `(x1, y1, x2, y2), (x3, y3, x4, y4), ...`. The size of this array determines the
     * resolution of the generated curves.
     * @param w The width of the drawing area (e.g., a Canvas) to scale the curves to.
     * @param h The height of the drawing area (e.g., a Canvas) to scale the curves to.
     * @param velocityMode If `true`, the function plots the velocity-over-time graphs.
     * If `false`, it plots the position-over-time graphs.
     */
    @Suppress("unused")
    fun getCurves(points: FloatArray, w: Int, h: Int, velocityMode: Boolean) {
        val len: Int = points.size
        val duration: Float = this.duration
        val lines: Int = (len - 5 * 4) / 8
        var p = 0

        val inset = 40
        val regionW: Int = w - inset * 2
        val regionH: Int = h - inset * 2
        points[p++] = inset.toFloat()
        points[p++] = inset.toFloat()
        points[p++] = inset.toFloat()
        points[p++] = (inset + regionH).toFloat()

        points[p++] = (inset + regionW).toFloat()
        points[p++] = inset.toFloat()
        points[p++] = (inset + regionW).toFloat()
        points[p++] = (inset + regionH).toFloat()

        points[p++] = inset.toFloat()
        points[p++] = (inset + regionH).toFloat()
        points[p++] = (inset + regionW).toFloat()
        points[p++] = (inset + regionH).toFloat()

        var min = 0f
        var max = 1f
        var v: Float
        if (velocityMode) {
            val startX: Float = mvX.startV
            val startY: Float = mvY.startV
            val endX = 0f
            val endY = 0f
            for (i in 0..<lines) {
                val t: Float = i * duration / lines
                v = (mvY.getV(t) - startY) / (endY - startY)
                min = min(v, min)
                max = max(v, max)
                v = (mvX.getV(t) - startX) / (endX - startX)
                min = min(v, min)
                max = max(v, max)
            }

            var y0: Float = inset + regionH - regionH * ((0.0f - min) / (max - min))
            points[p++] = inset.toFloat()
            points[p++] = y0
            points[p++] = (inset + regionW).toFloat()
            points[p++] = y0
            y0 = inset + regionH - regionH * ((1.0f - min) / (max - min))
            points[p++] = inset.toFloat()
            points[p++] = y0
            points[p++] = (inset + regionW).toFloat()
            points[p++] = y0

            for (i in 0..<lines) {
                val t: Float = i * duration / lines
                val t2: Float = (i + 1) * duration / lines
                val xp1: Float = i / lines.toFloat()
                val xp2: Float = (i + 1) / lines.toFloat()
                points[p++] = inset + regionW * (xp1)
                points[p++] = (inset + regionH) -
                    ((regionH * (((mvY.getV(t) - startY) / (endY - startY)) - min)) / (max - min))
                points[p++] = inset + regionW * (xp2)
                points[p++] = (inset + regionH) -
                    ((regionH * (((mvY.getV(t2) - startY) / (endY - startY)) - min)) / (max - min))
            }
            for (i in 0..<lines) {
                val t: Float = i * duration / lines
                val t2: Float = (i + 1) * duration / lines
                val xp1: Float = i / lines.toFloat()
                val xp2: Float = (i + 1) / lines.toFloat()
                points[p++] = inset + regionW * (xp1)
                points[p++] = (inset + regionH) -
                    ((regionH * (((mvX.getV(t) - startX) / (endX - startX)) - min)) / (max - min))
                points[p++] = inset + regionW * (xp2)
                points[p++] = (inset + regionH) -
                    ((regionH * (((mvX.getV(t2) - startX) / (endX - startX)) - min)) / (max - min))
            }
        } else {
            val startX: Float = mvX.startPos
            val startY: Float = mvY.startPos
            val endX: Float = mvX.endPos
            val endY: Float = mvY.endPos
            for (i in 0..<lines) {
                val t: Float = i * duration / lines
                v = (mvY.getPos(t) - startY) / (endY - startY)
                min = min(v, min)
                max = max(v, max)
                v = (mvX.getPos(t) - startX) / (endX - startX)
                min = min(v, min)
                max = max(v, max)
            }

            var y0: Float = inset + regionH - regionH * ((0.0f - min) / (max - min))
            points[p++] = inset.toFloat()
            points[p++] = y0
            points[p++] = (inset + regionW).toFloat()
            points[p++] = y0
            y0 = inset + regionH - regionH * ((1.0f - min) / (max - min))
            points[p++] = inset.toFloat()
            points[p++] = y0
            points[p++] = (inset + regionW).toFloat()
            points[p++] = y0

            for (i in 0..<lines) {
                val t: Float = i * duration / lines
                val t2: Float = (i + 1) * duration / lines
                val xp1: Float = i / lines.toFloat()
                val xp2: Float = (i + 1) / lines.toFloat()
                points[p++] = inset + regionW * (xp1)
                points[p++] = (inset + regionH) -
                    ((regionH * (((mvY.getPos(t) - startY) / (endY - startY)) - min)) / (max - min))
                points[p++] = inset + regionW * (xp2)
                points[p++] = (inset + regionH) -
                    ((regionH * (((mvY.getPos(t2) - startY) / (endY - startY)) - min)) / (max - min))
            }
            for (i in 0..<lines) {
                val t: Float = i * duration / lines
                val t2: Float = (i + 1) * duration / lines
                val xp1: Float = i / lines.toFloat()
                val xp2: Float = (i + 1) / lines.toFloat()
                points[p++] = inset + regionW * (xp1)
                points[p++] = (inset + regionH) -
                    ((regionH * (((mvX.getPos(t) - startX) / (endX - startX)) - min)) / (max - min))
                points[p++] = inset + regionW * (xp2)
                points[p++] = (inset + regionH) -
                    ((regionH * (((mvX.getPos(t2) - startX) / (endX - startX)) - min)) / (max - min))
            }
        }
    }
}
