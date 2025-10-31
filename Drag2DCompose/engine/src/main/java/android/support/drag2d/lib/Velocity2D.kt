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

import java.util.Arrays
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

class Velocity2D {
    val mvX: MaterialVelocity2D = MaterialVelocity2D()
    val mvY: MaterialVelocity2D = MaterialVelocity2D()

    fun configure(
        posX: Float,
        posY: Float,
        velocityX: Float,
        velocityY: Float,
        destinationX: Float,
        destinationY: Float,
        duration: Float,
        maxV: Float,
        maxA: Float, easing: MaterialVelocity.Easing?
    ) {
        var velocityX = velocityX
        var velocityY = velocityY
        val speed: Double = hypot(velocityX, velocityY).toDouble()
        if (speed > maxV) {
            velocityX *= (maxV / speed).toFloat()
            velocityY *= (maxV / speed).toFloat()
        }
        mvX.config(posX, destinationX, velocityX, duration, maxA, maxV, easing)
        mvY.config(posY, destinationY, velocityY, duration, maxA, maxV, easing)

        mvX.sync(mvY)
    }

    @Suppress("unused")
    private fun checkCurves() {
        println(" --------x-------")
        dump(mvX)
        println(" -------y--------")

        dump(mvY)
        println("  ")
    }

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
            val t1 = mv.mStage[i].mStartTime + 0.001f
            val t2 = mv.mStage[i].endTime - 0.001f
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
            val dist = mv.mStage[i].mEndPos - mv.mStage[i].mStartPos
            val dist2 =
                (mv.mStage[i].mStartV + mv.mStage[i].mEndV) * (mv.mStage[i].endTime - mv.mStage[i].mStartTime) / 2
            print(" | $dist  == $dist2")
        }
        println()
    }

    fun getX(t: Float): Float {
        return mvX.getPos(t)
    }

    fun getY(t: Float): Float {
        return mvY.getPos(t)
    }

    fun getVX(t: Float): Float {
        return mvX.getV(t)
    }

    fun getVY(t: Float): Float {
        return mvY.getV(t)
    }

    @Suppress("unused")
    fun isStillMoving(t: Float): Boolean {
        return mvX.duration > t || mvY.duration > t
    }

    val duration: Float
        get() = max(mvX.duration, mvY.mDuration)

    @Suppress("unused")
    fun getPointOffsetX(len: Int, fraction: Float): Int {
        val lines = (len - 5 * 4) / 8
        var off = (((len - 20).toFloat() / 8) * fraction).toInt()

        if (off >= lines) {
            off = lines - 2
        }
        return 20 + 4 * off
    }

    @Suppress("unused")
    fun getPointOffsetY(len: Int, fraction: Float): Int {
        val lines = (len - 5 * 4) / 8
        var off = (((len - 20).toFloat() / 8) * fraction).toInt()

        if (off >= lines) {
            off = lines - 2
        }
        return 20 + 4 * (lines + off)
    }

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
     * This builds a curves that can be displayed on the screen for debugging
     *
     * @param points        in the form (x1,y1,x2,y2),... as supported by canvas.drawLines()
     * @param w
     * @param h
     * @param velocityMode
     */
    @Suppress("unused")
    fun getCurves(points: FloatArray, w: Int, h: Int, velocityMode: Boolean) {
        val len = points.size
        val duration = this.duration
        val lines = (len - 5 * 4) / 8
        var p = 0

        val inset = 40
        val regionW = w - inset * 2
        val regionH = h - inset * 2
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
            val startX = mvX.startV
            val startY = mvY.startV
            val endX = 0f
            val endY = 0f
            for (i in 0..<lines) {
                val t = i * duration / lines
                v = (mvY.getV(t) - startY) / (endY - startY)
                min = min(v, min)
                max = max(v, max)
                v = (mvX.getV(t) - startX) / (endX - startX)
                min = min(v, min)
                max = max(v, max)
            }

            var y0 = inset + regionH - regionH * ((0.0f - min) / (max - min))
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
                val t = i * duration / lines
                val t2 = (i + 1) * duration / lines
                val xp1 = i / lines.toFloat()
                val xp2 = (i + 1) / lines.toFloat()
                points[p++] = inset + regionW * (xp1)
                points[p++] =
                    inset + regionH - regionH * ((mvY.getV(t) - startY) / (endY - startY) - min) / (max - min)
                points[p++] = inset + regionW * (xp2)
                points[p++] =
                    inset + regionH - regionH * ((mvY.getV(t2) - startY) / (endY - startY) - min) / (max - min)
            }
            for (i in 0..<lines) {
                val t = i * duration / lines
                val t2 = (i + 1) * duration / lines
                val xp1 = i / lines.toFloat()
                val xp2 = (i + 1) / lines.toFloat()
                points[p++] = inset + regionW * (xp1)
                points[p++] =
                    inset + regionH - regionH * ((mvX.getV(t) - startX) / (endX - startX) - min) / (max - min)
                points[p++] = inset + regionW * (xp2)
                points[p++] =
                    inset + regionH - regionH * ((mvX.getV(t2) - startX) / (endX - startX) - min) / (max - min)
            }
        } else {
            val startX = mvX.startPos
            val startY = mvY.startPos
            val endX = mvX.endPos
            val endY = mvY.endPos
            for (i in 0..<lines) {
                val t = i * duration / lines
                v = (mvY.getPos(t) - startY) / (endY - startY)
                min = min(v, min)
                max = max(v, max)
                v = (mvX.getPos(t) - startX) / (endX - startX)
                min = min(v, min)
                max = max(v, max)
            }

            var y0 = inset + regionH - regionH * ((0.0f - min) / (max - min))
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
                val t = i * duration / lines
                val t2 = (i + 1) * duration / lines
                val xp1 = i / lines.toFloat()
                val xp2 = (i + 1) / lines.toFloat()
                points[p++] = inset + regionW * (xp1)
                points[p++] =
                    inset + regionH - regionH * ((mvY.getPos(t) - startY) / (endY - startY) - min) / (max - min)
                points[p++] = inset + regionW * (xp2)
                points[p++] =
                    inset + regionH - regionH * ((mvY.getPos(t2) - startY) / (endY - startY) - min) / (max - min)
            }
            for (i in 0..<lines) {
                val t = i * duration / lines
                val t2 = (i + 1) * duration / lines
                val xp1 = i / lines.toFloat()
                val xp2 = (i + 1) / lines.toFloat()
                points[p++] = inset + regionW * (xp1)
                points[p++] =
                    inset + regionH - regionH * ((mvX.getPos(t) - startX) / (endX - startX) - min) / (max - min)
                points[p++] = inset + regionW * (xp2)
                points[p++] =
                    inset + regionH - regionH * ((mvX.getPos(t2) - startX) / (endX - startX) - min) / (max - min)
            }
        }
    }
}
