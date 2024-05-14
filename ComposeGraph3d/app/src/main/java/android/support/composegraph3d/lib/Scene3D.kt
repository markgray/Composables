/*
 * Copyright (C) 2020 The Android Open Source Project
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
@file:Suppress("unused", "UNUSED_PARAMETER", "ReplaceNotNullAssertionWithElvisReturn", "MemberVisibilityCanBePrivate")

package android.support.composegraph3d.lib

import java.util.Arrays
import kotlin.math.sqrt

/**
 * This renders 3Dimensional Objects.
 */
class Scene3D {
    /**
     * TODO: Add kdoc
     */
    var mMatrix: ViewMatrix = ViewMatrix()

    /**
     * TODO: Add kdoc
     */
    var mInverse: Matrix? = Matrix()

    /**
     * TODO: Add kdoc
     */
    var mObject3D: Object3D? = null

    /**
     * TODO: Add kdoc
     */
    var mPreObjects: ArrayList<Object3D> = arrayListOf()

    /**
     * TODO: Add kdoc
     */
    var mPostObjects: ArrayList<Object3D> = ArrayList()

    /**
     * TODO: Add kdoc
     */
    var zBuff: FloatArray? = null

    /**
     * TODO: Add kdoc
     */
    lateinit var img: IntArray

    /**
     * TODO: Add kdoc
     */
    private val light = floatArrayOf(0f, 0f, 1f) // The direction of the light source

    /**
     *
     */
    @JvmField
    var mTransformedLight: FloatArray = floatArrayOf(0f, 1f, 1f) // The direction of the light source

    /**
     * TODO: Add kdoc
     */
    var mLightMovesWithCamera: Boolean = false

    /**
     * TODO: Add kdoc
     */
    var width: Int = 0

    /**
     * TODO: Add kdoc
     */
    var height: Int = 0

    /**
     * TODO: Add kdoc
     */
    @JvmField
    var tmpVec: FloatArray = FloatArray(3)

    /**
     * TODO: Add kdoc
     */
    var lineColor: Int = -0x1000000

    /**
     * TODO: Add kdoc
     */
    private val epslonX = 0.000005232f

    /**
     * TODO: Add kdoc
     */
    private val epslonY = 0.00000898f

    /**
     * TODO: Add kdoc
     */
    private val mFunction: Function? = null

    /**
     * TODO: Add kdoc
     */
    var zoom: Float = 1f

    /**
     * TODO: Add kdoc
     */
    var background: Int = 0

    /**
     * TODO: Add kdoc
     */
    internal inner class Box {
        /**
         * TODO: Add kdoc
         */
        var mBox = arrayOf(floatArrayOf(1f, 1f, 1f), floatArrayOf(2f, 3f, 2f))

        /**
         * TODO: Add kdoc
         */
        var mX1 = intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0)

        /**
         * TODO: Add kdoc
         */
        var mY1 = intArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1)

        /**
         * TODO: Add kdoc
         */
        var mZ1 = intArrayOf(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1)

        /**
         * TODO: Add kdoc
         */
        var mX2 = intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1)

        /**
         * TODO: Add kdoc
         */
        var mY2 = intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1)

        /**
         * TODO: Add kdoc
         */
        var mZ2 = intArrayOf(1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1)

        /**
         * TODO: Add kdoc
         */
        var mPoint1 = FloatArray(3)

        /**
         * TODO: Add kdoc
         */
        var mPoint2 = FloatArray(3)

        /**
         * TODO: Add kdoc
         */
        var mDraw1 = FloatArray(3)

        /**
         * TODO: Add kdoc
         */
        var mDraw2 = FloatArray(3)

        /**
         * TODO: Add kdoc
         */
        fun drawLines(r: LineRender) {
            for (i in 0..11) {
                mPoint1[0] = mBox[mX1[i]][0]
                mPoint1[1] = mBox[mY1[i]][1]
                mPoint1[2] = mBox[mZ1[i]][2]
                mPoint2[0] = mBox[mX2[i]][0]
                mPoint2[1] = mBox[mY2[i]][1]
                mPoint2[2] = mBox[mZ2[i]][2]
                mInverse!!.mult3(mPoint1, mDraw1)
                mInverse!!.mult3(mPoint2, mDraw2)
                r.draw(
                    mDraw1[0].toInt(),
                    mDraw1[1].toInt(),
                    mDraw2[0].toInt(),
                    mDraw2[1].toInt()
                )
            }
        }
    }

    init {
        VectorUtil.normalize(light)
    }

    /**
     * TODO: Add kdoc
     */
    fun transformTriangles() {
        transform()
    }

    /**
     * TODO: Add kdoc
     */
    fun transform() {
        val m = mInverse
        if (mLightMovesWithCamera) {
            mMatrix.mult3v(light, mTransformedLight)
            VectorUtil.normalize(mTransformedLight)
        } else {
            System.arraycopy(light, 0, mTransformedLight, 0, 3)
        }
        mObject3D!!.transform(m)
        for (obj in mPreObjects) {
            obj.transform(m)
        }
        for (obj in mPostObjects) {
            obj.transform(m)
        }
    }

    /**
     * TODO: Add kdoc
     */
    var screenWidth: Double
        get() = mMatrix.screenWidth
        set(sw) {
            mMatrix.screenWidth = sw
            mMatrix.calcMatrix()
            mMatrix.invers(mInverse!!)
            transform()
        }

    /**
     * TODO: Add kdoc
     */
    fun trackBallDown(x: Float, y: Float) {
        mMatrix.trackBallDown(x, y)
        mMatrix.invers(mInverse!!)
    }

    /**
     * TODO: Add kdoc
     */
    fun trackBallMove(x: Float, y: Float) {
        mMatrix.trackBallMove(x, y)
        mMatrix.invers(mInverse!!)
        transform()
    }

    /**
     * TODO: Add kdoc
     */
    fun trackBallUP(x: Float, y: Float) {
        mMatrix.trackBallUP(x, y)
        mMatrix.invers(mInverse!!)
    }

    /**
     * TODO: Add kdoc
     */
    fun update() {
        mMatrix.invers(mInverse!!)
        transform()
    }

    /**
     * TODO: Add kdoc
     */
    fun panDown(x: Float, y: Float) {
        mMatrix.panDown(x, y)
        mMatrix.invers(mInverse!!)
    }

    /**
     * TODO: Add kdoc
     */
    fun panMove(x: Float, y: Float) {
        mMatrix.panMove(x, y)
        mMatrix.invers(mInverse!!)
        transform()
    }

    /**
     * TODO: Add kdoc
     */
    fun panUP() {
        mMatrix.panUP()
    }

    /**
     * TODO: Add kdoc
     */
    @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
    val lookPoint: String
        get() = Arrays.toString(mMatrix.lookPoint)

    /**
     * TODO: Add kdoc
     */
    fun setScreenDim(width: Int, height: Int, img: IntArray, background: Int) {
        mMatrix.setScreenDim(width, height)
        setupBuffers(width, height, img, background)
        setUpMatrix(width, height)
        transform()
    }

    /**
     * TODO: Add kdoc
     */
    fun setUpMatrix(width: Int, height: Int) {
        setUpMatrix(width, height, false)
    }

    /**
     * TODO: Add kdoc
     */
    fun setUpMatrix(width: Int, height: Int, resetOrientation: Boolean) {
        val lookPoint = mObject3D!!.center()
        val diagonal = mObject3D!!.size() * zoom
        mMatrix.lookPoint = lookPoint
        if (resetOrientation) {
            val eyePoint = doubleArrayOf(
                lookPoint[0] - diagonal,
                lookPoint[1] - diagonal,
                lookPoint[2] + diagonal
            )
            mMatrix.eyePoint = eyePoint
            val upVector = doubleArrayOf(0.0, 0.0, 1.0)
            mMatrix.upVector = upVector
        } else {
            mMatrix.fixUpPoint()
        }
        val screenWidth = diagonal * 2
        mMatrix.screenWidth = screenWidth
        mMatrix.setScreenDim(width, height)
        mMatrix.calcMatrix()
        mMatrix.invers(mInverse!!)
    }

    /**
     * TODO: Add kdoc
     */
    fun notSetUp(): Boolean {
        return mInverse == null
    }

    /**
     * TODO: Add kdoc
     */
    interface LineRender {
        /**
         * TODO: Add kdoc
         */
        fun draw(x1: Int, y1: Int, x2: Int, y2: Int)
    }

    /**
     * TODO: Add kdoc
     */
    fun drawBox(g: LineRender?) {}

    /**
     * TODO: Add kdoc
     */
    interface Function {
        /**
         * TODO: Add kdoc
         */
        fun eval(x: Float, y: Float): Float
    }

    /**
     * TODO: Add kdoc
     */
    fun addPreObject(obj: Object3D) {
        mPreObjects.add(obj)
    }

    /**
     * TODO: Add kdoc
     */
    fun setObject(obj: Object3D) {
        mObject3D = obj
    }

    /**
     * TODO: Add kdoc
     */
    fun addPostObject(obj: Object3D) {
        mPostObjects.add(obj)
    }

    /**
     * TODO: Add kdoc
     */
    fun resetCamera() {
        setUpMatrix(width, height, true)
        transform()
    }

    /**
     * TODO: Add kdoc
     */
    fun setupBuffers(w: Int, h: Int, img: IntArray, background: Int) {
        width = w
        height = h
        this.background = background
        zBuff = FloatArray(w * h)
        this.img = img
        Arrays.fill(zBuff!!, Float.MAX_VALUE)
        Arrays.fill(img, background)
    }

    /**
     * TODO: Add kdoc
     */
    fun render(type: Int) {
        if (zBuff == null) {
            return
        }
        Arrays.fill(zBuff!!, Float.MAX_VALUE)
        Arrays.fill(img, background)
        for (mPreObject in mPreObjects) {
            mPreObject.render(this, zBuff!!, img, width, height)
        }

        mObject3D!!.render(this, zBuff!!, img, width, height)
        for (mPreObject in mPostObjects) {
            mPreObject.render(this, zBuff!!, img, width, height)
        }
    }

    companion object {
        /**
         * TODO: Add kdoc
         */
        private const val TAG = "SurfaceGen"

        /**
         * TODO: Add kdoc
         */
        private fun min(x1: Int, x2: Int, x3: Int): Int {
            return if (x1 > x2) (if (x2 > x3) x3 else x2) else if (x1 > x3) x3 else x1
        }

        /**
         * TODO: Add kdoc
         */
        private fun max(x1: Int, x2: Int, x3: Int): Int {
            return if (x1 < x2) (if (x2 < x3) x3 else x2) else if (x1 < x3) x3 else x1
        }

        /**
         * TODO: Add kdoc
         */
        fun hsvtorgbSlow(hue: Float, saturation: Float, value: Float): Int {
            val h = (hue * 6).toInt()
            val f = hue * 6 - h
            val p = (0.5f + 255 * value * (1 - saturation)).toInt()
            val q = (0.5f + 255 * value * (1 - f * saturation)).toInt()
            val t = (0.5f + 255 * value * (1 - (1 - f) * saturation)).toInt()
            val v = (0.5f + 255 * value).toInt()
            when (h) {
                0 -> return -0x1000000 or (v shl 16) + (t shl 8) + p
                1 -> return -0x1000000 or (q shl 16) + (v shl 8) + p
                2 -> return -0x1000000 or (p shl 16) + (v shl 8) + t
                3 -> return -0x1000000 or (p shl 16) + (q shl 8) + v
                4 -> return -0x1000000 or (t shl 16) + (p shl 8) + v
                5 -> return -0x1000000 or (v shl 16) + (p shl 8) + q
            }
            return 0
        }

        /**
         * TODO: Add kdoc
         */
        fun hsvToRgb(hue: Float, saturation: Float, value: Float): Int {
            val h = (hue * 6).toInt()
            val f = hue * 6 - h
            val p = (0.5f + 255 * value * (1 - saturation)).toInt()
            val q = (0.5f + 255 * value * (1 - f * saturation)).toInt()
            val t = (0.5f + 255 * value * (1 - (1 - f) * saturation)).toInt()
            val v = (0.5f + 255 * value).toInt()
            if (h == 0) {
                return -0x1000000 or (v shl 16) + (t shl 8) + p
            }
            if (h == 1) {
                return -0x1000000 or (q shl 16) + (v shl 8) + p
            }
            if (h == 2) {
                return -0x1000000 or (p shl 16) + (v shl 8) + t
            }
            if (h == 3) {
                return -0x1000000 or (p shl 16) + (q shl 8) + v
            }
            if (h == 4) {
                return -0x1000000 or (t shl 16) + (p shl 8) + v
            }
            if (h == 5) {
                return -0x1000000 or (v shl 16) + (p shl 8) + q
            }

            return 0
        }

        /**
         * TODO: Add kdoc
         */
        @JvmStatic
        fun drawline(
            zbuff: FloatArray, img: IntArray, color: Int, w: Int, h: Int,
            fx1: Float, fy1: Float, fz1: Float,
            fx2: Float, fy2: Float, fz2: Float
        ) {
            val dx = fx2 - fx1
            val dy = fy2 - fy1
            val dz = fz2 - fz1
            val zang = sqrt(dy * dy + dz * dz)
            val steps = sqrt(dx * dx + zang * zang)
            var t = 0f
            while (t < 1) {
                val px = fx1 + t * dx
                val py = fy1 + t * dy
                val pz = fz1 + t * dz
                val ipx = px.toInt()
                val ipy = py.toInt()
                if (ipx < 0 || ipx >= w || ipy < 0 || ipy >= h) {
                    t += 1 / steps
                    continue
                }
                val point = ipx + w * ipy
                if (zbuff[point] >= pz - 2) {
                    img[point] = color
                }
                t += 1 / steps
            }
        }

        /**
         * TODO: Add kdoc
         */
        @JvmStatic
        fun isBackface(
            fx3: Float, fy3: Float, fz3: Float,
            fx2: Float, fy2: Float, fz2: Float,
            fx1: Float, fy1: Float, fz1: Float
        ): Boolean {
            return (fx1 - fx2) * (fy3 - fy2) - (fy1 - fy2) * (fx3 - fx2) < 0
        }

        /**
         * TODO: Add kdoc
         */
        @JvmStatic
        fun triangle(
            zbuff: FloatArray, img: IntArray, color: Int, w: Int, h: Int,
            fx3: Float, fy3: Float, fz3: Float,
            fx2: Float, fy2: Float, fz2: Float,
            fx1: Float, fy1: Float, fz1: Float
        ) {
            var fx2Local = fx2
            var fy2Local = fy2
            var fz2Local = fz2
            var fx1Local = fx1
            var fy1Local = fy1
            var fz1Local = fz1
            if ((fx1Local - fx2Local) * (fy3 - fy2Local) - (fy1Local - fy2Local) * (fx3 - fx2Local) < 0) {
                val tmpx = fx1Local
                val tmpy = fy1Local
                val tmpz = fz1Local
                fx1Local = fx2Local
                fy1Local = fy2Local
                fz1Local = fz2Local
                fx2Local = tmpx
                fy2Local = tmpy
                fz2Local = tmpz
            }
            // using maxmima
            // string(solve([x1*dx+y1*dy+zoff=z1,x2*dx+y2*dy+zoff=z2,x3*dx+y3*dy+zoff=z3],[dx,dy,zoff]));
            val d = (fx1Local * (fy3 - fy2Local) - fx2Local * fy3 + fx3 * fy2Local + ((fx2Local - fx3)
                    * fy1Local)).toDouble()
            if (d == 0.0) {
                return
            }
            val dx = (-(fy1Local * (fz3 - fz2Local) - fy2Local * fz3 + fy3 * fz2Local + ((fy2Local - fy3)
                    * fz1Local)) / d).toFloat()
            val dy = ((fx1Local * (fz3 - fz2Local) - fx2Local * fz3 + fx3 * fz2Local + ((fx2Local - fx3)
                    * fz1Local)) / d).toFloat()
            val zoff = (fx1Local * (fy3 * fz2Local - fy2Local * fz3) + (fy1Local
                    * (fx2Local * fz3 - fx3 * fz2Local)) + (fx3 * fy2Local - fx2Local * fy3) * fz1Local / d).toFloat()

            // 28.4 fixed-point coordinates
            val y1 = (16.0f * fy1Local + .5f).toInt()
            val y2 = (16.0f * fy2Local + .5f).toInt()
            val y3 = (16.0f * fy3 + .5f).toInt()
            val x1 = (16.0f * fx1Local + .5f).toInt()
            val x2 = (16.0f * fx2Local + .5f).toInt()
            val x3 = (16.0f * fx3 + .5f).toInt()
            val dX12 = x1 - x2
            val dX23 = x2 - x3
            val dX31 = x3 - x1
            val dY12 = y1 - y2
            val dY23 = y2 - y3
            val dY31 = y3 - y1
            val fDX12 = dX12 shl 4
            val fDX23 = dX23 shl 4
            val fDX31 = dX31 shl 4
            val fDY12 = dY12 shl 4
            val fDY23 = dY23 shl 4
            val fDY31 = dY31 shl 4
            var minx = min(x1, x2, x3) + 0xF shr 4
            var maxx = max(x1, x2, x3) + 0xF shr 4
            var miny = min(y1, y2, y3) + 0xF shr 4
            var maxy = max(y1, y2, y3) + 0xF shr 4
            if (miny < 0) {
                miny = 0
            }
            if (minx < 0) {
                minx = 0
            }
            if (maxx > w) {
                maxx = w
            }
            if (maxy > h) {
                maxy = h
            }
            var off = miny * w
            var c1 = dY12 * x1 - dX12 * y1
            var c2 = dY23 * x2 - dX23 * y2
            var c3 = dY31 * x3 - dX31 * y3
            if (dY12 < 0 || dY12 == 0 && dX12 > 0) {
                c1++
            }
            if (dY23 < 0 || dY23 == 0 && dX23 > 0) {
                c2++
            }
            if ((dY31 < 0 || dY31 == 0) && dX31 > 0) {
                c3++
            }
            var cY1 = c1 + dX12 * (miny shl 4) - dY12 * (minx shl 4)
            var cY2 = c2 + dX23 * (miny shl 4) - dY23 * (minx shl 4)
            var cY3 = c3 + dX31 * (miny shl 4) - dY31 * (minx shl 4)
            for (y in miny until maxy) {
                var lCX1 = cY1
                var lCX2 = cY2
                var lCX3 = cY3
                val p = zoff + dy * y
                for (x in minx until maxx) {
                    if (lCX1 > 0 && lCX2 > 0 && lCX3 > 0) {
                        val point = x + off
                        val zval = p + dx * x
                        if (zbuff[point] > zval) {
                            zbuff[point] = zval
                            img[point] = color
                        }
                    }
                    lCX1 -= fDY12
                    lCX2 -= fDY23
                    lCX3 -= fDY31
                }
                cY1 += fDX12
                cY2 += fDX23
                cY3 += fDX31
                off += w
            }
        }

        /**
         * TODO: Add kdoc
         */
        fun trianglePhong(
            zbuff: FloatArray, img: IntArray,
            h3: Float, b3: Float,
            h2: Float, b2: Float,
            h1: Float, b1: Float,
            sat: Float,
            w: Int, h: Int,
            fx3: Float, fy3: Float, fz3: Float,
            fx2: Float, fy2: Float, fz2: Float,
            fx1: Float, fy1: Float, fz1: Float
        ) {
            var h2Local = h2
            var b2Local = b2
            var h1Local = h1
            var b1Local = b1
            var fx2Local = fx2
            var fy2Local = fy2
            var fz2Local = fz2
            var fx1Local = fx1
            var fy1Local = fy1
            var fz1Local = fz1
            if ((fx1Local - fx2Local) * (fy3 - fy2Local) - (fy1Local - fy2Local) * (fx3 - fx2Local) < 0) {
                val tmpx = fx1Local
                val tmpy = fy1Local
                val tmpz = fz1Local
                fx1Local = fx2Local
                fy1Local = fy2Local
                fz1Local = fz2Local
                fx2Local = tmpx
                fy2Local = tmpy
                fz2Local = tmpz
                val tmph = h1Local
                val tmpb = b1Local
                h1Local = h2Local
                b1Local = b2Local
                h2Local = tmph
                b2Local = tmpb
            }
            // using maxmima
            // string(solve([x1*dx+y1*dy+zoff=z1,x2*dx+y2*dy+zoff=z2,x3*dx+y3*dy+zoff=z3],[dx,dy,zoff]));
            val d = (fx1Local * (fy3 - fy2Local) - fx2Local * fy3 + fx3 * fy2Local + ((fx2Local - fx3)
                    * fy1Local))
            if (d == 0.0f) {
                return
            }
            val dx = (-(fy1Local * (fz3 - fz2Local) - fy2Local * fz3 + fy3 * fz2Local + ((fy2Local - fy3)
                    * fz1Local)) / d)
            val dy = ((fx1Local * (fz3 - fz2Local) - fx2Local * fz3 + fx3 * fz2Local + ((fx2Local - fx3)
                    * fz1Local)) / d)
            val zoff = ((fx1Local * (fy3 * fz2Local - fy2Local * fz3) + (fy1Local
                    * (fx2Local * fz3 - fx3 * fz2Local)) + (fx3 * fy2Local - fx2Local * fy3) * fz1Local) / d)
            val dhx = (-(fy1Local * (h3 - h2Local) - fy2Local * h3 + fy3 * h2Local + ((fy2Local - fy3)
                    * h1Local)) / d)
            val dhy = ((fx1Local * (h3 - h2Local) - fx2Local * h3 + fx3 * h2Local + ((fx2Local - fx3)
                    * h1Local)) / d)
            val hoff = ((fx1Local * (fy3 * h2Local - fy2Local * h3) + (fy1Local
                    * (fx2Local * h3 - fx3 * h2Local)) + (fx3 * fy2Local - fx2Local * fy3) * h1Local) / d)
            val dbx = (-(fy1Local * (b3 - b2Local) - fy2Local * b3 + fy3 * b2Local + ((fy2Local - fy3)
                    * b1Local)) / d)
            val dby = ((fx1Local * (b3 - b2Local) - fx2Local * b3 + fx3 * b2Local + ((fx2Local - fx3)
                    * b1Local)) / d)
            val boff = ((fx1Local * (fy3 * b2Local - fy2Local * b3) + (fy1Local
                    * (fx2Local * b3 - fx3 * b2Local)) + (fx3 * fy2Local - fx2Local * fy3) * b1Local) / d)

            // 28.4 fixed-point coordinates
            val lY1 = (16.0f * fy1Local + .5f).toInt()
            val lY2 = (16.0f * fy2Local + .5f).toInt()
            val lY3 = (16.0f * fy3 + .5f).toInt()
            val lX1 = (16.0f * fx1Local + .5f).toInt()
            val lX2 = (16.0f * fx2Local + .5f).toInt()
            val lX3 = (16.0f * fx3 + .5f).toInt()
            val lDX12 = lX1 - lX2
            val lDX23 = lX2 - lX3
            val lDX31 = lX3 - lX1
            val lDY12 = lY1 - lY2
            val lDY23 = lY2 - lY3
            val lDY31 = lY3 - lY1
            val lFDX12 = lDX12 shl 4
            val lFDX23 = lDX23 shl 4
            val lFDX31 = lDX31 shl 4
            val lFDY12 = lDY12 shl 4
            val lFDY23 = lDY23 shl 4
            val lFDY31 = lDY31 shl 4
            var minx = min(lX1, lX2, lX3) + 0xF shr 4
            var maxx = max(lX1, lX2, lX3) + 0xF shr 4
            var miny = min(lY1, lY2, lY3) + 0xF shr 4
            var maxy = max(lY1, lY2, lY3) + 0xF shr 4
            if (miny < 0) {
                miny = 0
            }
            if (minx < 0) {
                minx = 0
            }
            if (maxx > w) {
                maxx = w
            }
            if (maxy > h) {
                maxy = h
            }
            var off = miny * w
            var lC1 = lDY12 * lX1 - lDX12 * lY1
            var lC2 = lDY23 * lX2 - lDX23 * lY2
            var lC3 = lDY31 * lX3 - lDX31 * lY3
            if (lDY12 < 0 || lDY12 == 0 && lDX12 > 0) {
                lC1++
            }
            if (lDY23 < 0 || lDY23 == 0 && lDX23 > 0) {
                lC2++
            }
            if (lDY31 < 0 || lDY31 == 0 && lDX31 > 0) {
                lC3++
            }
            var lCY1 = lC1 + lDX12 * (miny shl 4) - lDY12 * (minx shl 4)
            var lCY2 = lC2 + lDX23 * (miny shl 4) - lDY23 * (minx shl 4)
            var lCY3 = lC3 + lDX31 * (miny shl 4) - lDY31 * (minx shl 4)
            for (y in miny until maxy) {
                var lCX1 = lCY1
                var lCX2 = lCY2
                var lCX3 = lCY3
                val p = zoff + dy * y
                val ph = hoff + dhy * y
                val pb = boff + dby * y
                for (x in minx until maxx) {
                    if (lCX1 > 0 && lCX2 > 0 && lCX3 > 0) {
                        val point = x + off
                        val zval = p + dx * x
                        val hue = ph + dhx * x
                        val bright = pb + dbx * x
                        if (zbuff[point] > zval) {
                            zbuff[point] = zval
                            img[point] = hsvToRgb(hue, sat, bright)
                        }
                    }
                    lCX1 -= lFDY12
                    lCX2 -= lFDY23
                    lCX3 -= lFDY31
                }
                lCY1 += lFDX12
                lCY2 += lFDX23
                lCY3 += lFDX31
                off += w
            }
        }

    }
}