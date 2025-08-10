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
@file:Suppress("unused", "UNUSED_PARAMETER", "ReplaceNotNullAssertionWithElvisReturn", "ReplaceJavaStaticMethodWithKotlinAnalog", "MemberVisibilityCanBePrivate",
    "RedundantSuppression"
)

package android.support.composegraph3d.lib

import android.support.composegraph3d.lib.Scene3D.Companion.trianglePhong

/**
 * This represents 3d Object in this system.
 */
open class Object3D {
    /**
     * TODO: Add kdoc
     */
    lateinit var vert: FloatArray

    /**
     * TODO: Add kdoc
     */
    lateinit var normal: FloatArray

    /**
     * TODO: Add kdoc
     */
    lateinit var index: IntArray

    /**
     * TODO: Add kdoc
     */
    lateinit var tVert  : FloatArray

    /**
     * TODO: Add kdoc
     */
    protected var mMinX: Float = 0f

    /**
     * TODO: Add kdoc
     */
    protected var mMaxX: Float = 0f

    /**
     * TODO: Add kdoc
     */
    protected var mMinY: Float = 0f

    /**
     * TODO: Add kdoc
     */
    protected var mMaxY: Float = 0f

    /**
     * TODO: Add kdoc
     */
     var mMinZ: Float = 0f

    /**
     * TODO: Add kdoc
     */
     var mMaxZ: Float = 0f

// bounds in x,y & z

    /**
     * TODO: Add kdoc
     */
    var type: Int = 4

    /**
     * TODO: Add kdoc
     */
    var mAmbient: Float = 0.3f

    /**
     * TODO: Add kdoc
     */
    var mDefuse: Float = 0.7f

    /**
     * TODO: Add kdoc
     */
    var mSaturation: Float = 0.6f

    /**
     * TODO: Add kdoc
     */
    fun makeVert(n: Int) {
        vert = FloatArray(n * 3)
        tVert = FloatArray(n * 3)
        normal = FloatArray(n * 3)
    }

    /**
     * TODO: Add kdoc
     */
    fun makeIndexes(n: Int) {
        index = IntArray(n * 3)
    }

    /**
     * TODO: Add kdoc
     */
    fun transform(m: Matrix?) {
        var i = 0
        while (i < vert.size) {
            m!!.mult3(vert, i, tVert, i)
            i += 3
        }
    }

    /**
     * TODO: Add kdoc
     */
    open fun render(s: Scene3D, zbuff: FloatArray, img: IntArray, width: Int, height: Int) {
        when (type) {
            0 -> rasterHeight(s, zbuff, img, width, height)
            1 -> rasterOutline(s, zbuff, img, width, height)
            2 -> rasterColor(s, zbuff, img, width, height)
            3 -> rasterLines(s, zbuff, img, width, height)
            4 -> rasterPhong(this,s, zbuff, img, width, height)
        }
    }

    /**
     * TODO: Add kdoc
     */
    private fun rasterLines(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            val height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            val `val` = (255 * Math.abs(height)).toInt()
            Scene3D.triangle(
                zbuff, img, 0x10001 * `val` + 0x100 * (255 - `val`), w, h, tVert[p1], tVert[p1 + 1],
                tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                tVert[p3 + 2]
            )
            Scene3D.drawline(
                zbuff, img, s.lineColor, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f
            )
            Scene3D.drawline(
                zbuff, img, s.lineColor, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f
            )
            i += 3
        }
    }

    /**
     * TODO: Add kdoc
     */
    fun rasterHeight(s: Scene3D?, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            var height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            height = (height - mMinZ) / (mMaxZ - mMinZ)
            val col: Int = Scene3D.hsvToRgb(
                height,
                Math.abs(2 * (height - 0.5f)),
                Math.sqrt(height.toDouble()).toFloat()
            )
            Scene3D.triangle(
                zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                tVert[p3 + 2]
            )
            i += 3
        }
    }

    // float mSpec = 0.2f;
    /**
     * TODO: Add kdoc
     */
    open fun rasterColor(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec)
            val defuse = VectorUtil.dot(s.tmpVec, s.mTransformedLight)
            var height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            height = (height - mMinZ) / (mMaxZ - mMinZ)
            val bright = Math.min(1f, Math.max(0f, mDefuse * defuse + mAmbient))
            val hue = (height - Math.floor(height.toDouble())).toFloat()
            val sat = 0.8f
            val col: Int = Scene3D.hsvToRgb(hue, sat, bright)
            Scene3D.triangle(
                zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                tVert[p3 + 2]
            )
            i += 3
        }
    }

    /**
     * TODO: Add kdoc
     */
    private fun color(hue: Float, sat: Float, bright: Float): Int {
        var hueLocal = hue
        var brightLocal = bright
        hueLocal = hue(hueLocal)
        brightLocal = bright(brightLocal)
        return Scene3D.hsvToRgb(hueLocal, sat, brightLocal)
    }

    private fun hue(hue: Float): Float {
        return (hue - Math.floor(hue.toDouble())).toFloat()
    }

    private fun bright(bright: Float): Float {
        return Math.min(1f, Math.max(0f, bright))
    }

    private fun defuse(normals: FloatArray, off: Int, light: FloatArray?): Float {
        // s.mMatrix.mult3v(normal,off,s.tmpVec);
        return Math.abs(VectorUtil.dot(normal, off, light))
    }

    /**
     * TODO: Add kdoc
     */
    fun rasterPhong1(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        @Suppress("ReplacePrintlnWithLogging")
        println(" render ")

        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            //    VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec);


//            float defuse1 = VectorUtil.dot(normal, p1, s.mTransformedLight);
//            float defuse2 = VectorUtil.dot(normal, p2, s.mTransformedLight);
//            float defuse3 = VectorUtil.dot(normal, p3, s.mTransformedLight);
            val defuse1 = defuse(normal, p1, s.mTransformedLight)
            val defuse2 = defuse(normal, p2, s.mTransformedLight)
            val defuse3 = defuse(normal, p3, s.mTransformedLight)
            val col1Hue = hue((vert[p1 + 2] - mMinZ) / (mMaxZ - mMinZ))
            val col2Hue = hue((vert[p2 + 2] - mMinZ) / (mMaxZ - mMinZ))
            val col3Hue = hue((vert[p3 + 2] - mMinZ) / (mMaxZ - mMinZ))
            val col1Bright =  bright(mDefuse * defuse1 + mAmbient)
            val col2Bright =  bright(mDefuse * defuse2 + mAmbient)
            val col3Bright =  bright(mDefuse * defuse3 + mAmbient)
            trianglePhong(
                zbuff, img,
                col1Hue, col1Bright,
                col2Hue, col2Bright,
                col3Hue, col3Bright,
                mSaturation,
                w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2]
            )
            i += 3
        }
    }

    /**
     * TODO: Add kdoc
     */
    fun rasterPhong(mSurface: Object3D, s: Scene3D, zbuff: FloatArray?, img: IntArray?, w: Int, h: Int) {
        var i = 0
        while (i < mSurface.index.size) {
            val p1: Int = mSurface.index[i]
            val p2: Int = mSurface.index[i + 1]
            val p3: Int = mSurface.index[i + 2]

            //    VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec);


//            float defuse1 = VectorUtil.dot(normal, p1, s.mTransformedLight);
//            float defuse2 = VectorUtil.dot(normal, p2, s.mTransformedLight);
//            float defuse3 = VectorUtil.dot(normal, p3, s.mTransformedLight);
            val defuse1 = defuse(mSurface.normal, p1, s.mTransformedLight)
            val defuse2 = defuse(mSurface.normal, p2, s.mTransformedLight)
            val defuse3 = defuse(mSurface.normal, p3, s.mTransformedLight)
            val col1Hue =
                hue((mSurface.vert[p1 + 2] - mSurface.mMinZ) / (mSurface.mMaxZ - mSurface.mMinZ))
            val col2Hue =
                hue((mSurface.vert[p2 + 2] - mSurface.mMinZ) / (mSurface.mMaxZ - mSurface.mMinZ))
            val col3Hue =
                hue((mSurface.vert[p3 + 2] - mSurface.mMinZ) / (mSurface.mMaxZ - mSurface.mMinZ))
            val col1Bright = bright(mDefuse * defuse1 + mAmbient)
            val col2Bright = bright(mDefuse * defuse2 + mAmbient)
            val col3Bright = bright(mDefuse * defuse3 + mAmbient)
            trianglePhong(
                zbuff!!, img!!,
                col1Hue, col1Bright,
                col2Hue, col2Bright,
                col3Hue, col3Bright,
                0.6f,
                w, h,
                mSurface.tVert[p1], mSurface.tVert[p1 + 1], mSurface.tVert[p1 + 2],
                mSurface.tVert[p2], mSurface.tVert[p2 + 1], mSurface.tVert[p2 + 2],
                mSurface.tVert[p3], mSurface.tVert[p3 + 1], mSurface.tVert[p3 + 2]
            )
            i += 3
        }
    }

    /**
     * TODO: Add kdoc
     */
    fun rasterOutline(s: Scene3D, zBuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            Scene3D.triangle(
                zBuff, img, s.background, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2]
            )
            Scene3D.drawline(
                zBuff, img, s.lineColor, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2]
            )
            Scene3D.drawline(
                zBuff, img, s.lineColor, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2]
            )
            i += 3
        }
    }

    /**
     * TODO: Add kdoc
     */
    fun center(): DoubleArray {
        return doubleArrayOf(
            (
                    (mMinX + mMaxX) / 2).toDouble(),
            ((mMinY + mMaxY) / 2).toDouble(),
            ((mMinZ + mMaxZ) / 2
                    ).toDouble()
        )
    }

    /**
     * TODO: Add kdoc
     */
    fun centerX(): Float {
        return (mMaxX + mMinX) / 2
    }

    /**
     * TODO: Add kdoc
     */
    fun centerY(): Float {
        return (mMaxY + mMinY) / 2
    }

    /**
     * TODO: Add kdoc
     */
    fun rangeX(): Float {
        return (mMaxX - mMinX) / 2
    }

    /**
     * TODO: Add kdoc
     */
    fun rangeY(): Float {
        return (mMaxY - mMinY) / 2
    }

    /**
     * TODO: Add kdoc
     */
    fun size(): Double {
        return Math.hypot(
            (mMaxX - mMinX).toDouble(),
            Math.hypot((mMaxY - mMinY).toDouble(), (mMaxZ - mMinZ).toDouble())
        ) / 2
    }
}