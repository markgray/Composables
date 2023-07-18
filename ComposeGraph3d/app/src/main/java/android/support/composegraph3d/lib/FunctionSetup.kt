@file:Suppress("unused", "UNUSED_PARAMETER", "ReplaceJavaStaticMethodWithKotlinAnalog", "ReplaceNotNullAssertionWithElvisReturn", "MemberVisibilityCanBePrivate")

package android.support.composegraph3d.lib

import android.support.composegraph3d.lib.objects.AxisBox
import android.support.composegraph3d.lib.objects.Surface3D
import android.support.composegraph3d.lib.objects.Surface3D.Function
import java.util.*
import kotlin.math.*

/**
 * TODO: Add kdoc
 *
 * @param mWidth width
 * @param mHeight height
 */
class FunctionSetup(var mWidth: Int, var mHeight: Int) {
    /**
     * TODO: Add kdoc
     */
    var mScene3D: Scene3D

    /**
     * TODO: Add kdoc
     */
    private var mImageBuff: IntArray

    /**
     * TODO: Add kdoc
     */
    var mGraphType: Int = 2

    /**
     * TODO: Add kdoc
     */
    private var mLastTouchX0 = Float.NaN

    /**
     * TODO: Add kdoc
     */
    private var mLastTouchY0 = 0f

    /**
     * TODO: Add kdoc
     */
    private var mLastTrackBallX = 0f

    /**
     * TODO: Add kdoc
     */
    private var mLastTrackBallY = 0f

    /**
     *TODO: Add kdoc
     */
    var mDownScreenWidth: Double = 0.0

    /**
     * TODO: Add kdoc
     */
    var mSurface: Surface3D? = null

    /**
     * TODO: Add kdoc
     */
    var mAxisBox: AxisBox? = null

    /**
     *TODO: Add kdoc
     */
    var range: Float = 20f

    /**
     * TODO: Add kdoc
     */
    var minZ: Float = -10f

    /**
     * TODO: Add kdoc
     */
    var maxZ: Float = 10f

    /**
     * TODO: Add kdoc
     */
    var mZoomFactor: Float = 1f

    /**
     * 
     */
    var animated: Boolean = false

    /**
     * TODO: Add kdoc
     */
    var zBuff: FloatArray = FloatArray(mWidth * mHeight)

    /**
     * TODO: Add kdoc
     */
    var nanoTime: Long = 0

    /**
     * TODO: Add kdoc
     */
    var time: Float = 0f

    /**
     * TODO: Add kdoc
     */
    fun buildSurface() {
        mSurface = Surface3D(mFunction = object : Function {
            override fun eval(x: Float, y: Float): Float {
                val d = Math.sqrt((x * x + y * y).toDouble())
                return 0.3f * (Math.cos(d) * (y * y - x * x) / (1 + d)).toFloat()
            }
        })
        mSurface!!.setRange(-range, range, -range, range, minZ, maxZ)
        mScene3D.setObject(mSurface!!)
        mScene3D.resetCamera()
        mAxisBox = AxisBox()
        mAxisBox!!.setRange(-range, range, -range, range, minZ, maxZ)
        mScene3D.addPostObject(mAxisBox!!)
        return buildAnimatedSurface()
    }


    init {
        mImageBuff = IntArray(mWidth * mHeight)
        // zBuff = new float[w*h];
        mScene3D = Scene3D()
        buildSurface()
        mScene3D.setUpMatrix(mWidth, mHeight)
        mScene3D.setScreenDim(mWidth, mHeight, mImageBuff, 0x00AAAAAA)
    }

    /**
     * TODO: Add kdoc
     */
    fun buildAnimatedSurface() {
        mSurface = Surface3D(object : Function {
            override fun eval(x: Float, y: Float): Float {
                val d = sqrt((x * x + y * y).toDouble()).toFloat()
                val d2 = (x * x + y * y).toDouble().pow(0.125).toFloat()
                val angle = atan2(y.toDouble(), x.toDouble()).toFloat()
                val s = sin((d + angle - time * 5).toDouble()).toFloat()
                val s2 = sin(time.toDouble()).toFloat()
                val c = cos((d + angle - time * 5).toDouble()).toFloat()
                return (s2 * s2 + 0.1f) * d2 * 5 * (s + c) / (1 + d * d / 20)
            }
        })
        nanoTime = System.nanoTime()
        mScene3D.setObject(mSurface!!)
        mSurface!!.setRange(-range, range, -range, range, minZ, maxZ)
    }

    /**
     * TODO: Add kdoc
     */
    fun tick(now: Long) {
        time += (now - nanoTime) * 1E-9f
        nanoTime = now
        mSurface!!.calcSurface(false)
        mScene3D.update()
    }

    /**
     * TODO: Add kdoc
     */
    fun onKeyTyped(c: Long) {
        println(c)
        //        switch ((char) c) {
//            case  ' ':
//                buildAnimatedSurface();
//        }
    }

    /**
     * TODO: Add kdoc
     */
    fun onMouseDown(x: Float, y: Float) {
        mDownScreenWidth = mScene3D.screenWidth
        mLastTouchX0 = x
        mLastTouchY0 = y
        mScene3D.trackBallDown(mLastTouchX0, mLastTouchY0)
        mLastTrackBallX = mLastTouchX0
        mLastTrackBallY = mLastTouchY0
    }

    /**
     * TODO: Add kdoc
     */
    fun onMouseDrag(x: Float, y: Float) {
        if (java.lang.Float.isNaN(mLastTouchX0)) {
            return
        }
        val moveX = mLastTrackBallX - x
        val moveY = mLastTrackBallY - y
        if (moveX * moveX + moveY * moveY < 4000f) {
            mScene3D.trackBallMove(x, y)
        }
        mLastTrackBallX = x
        mLastTrackBallY = y
    }

    /**
     * TODO: Add kdoc
     */
    fun onMouseUP() {
        mLastTouchX0 = Float.NaN
        mLastTouchY0 = Float.NaN
    }

    /**
     * TODO: Add kdoc
     */
    fun onMouseWheel(rotation: Float, ctlDown: Boolean) {
        if (ctlDown) {
            mZoomFactor *= 1.01.pow(rotation.toDouble()).toFloat()
            mScene3D.zoom = mZoomFactor
            mScene3D.setUpMatrix(mWidth, mHeight)
            mScene3D.update()
        } else {
            range *= 1.01.pow(rotation.toDouble()).toFloat()
            mSurface!!.setArraySize(Math.min(300, (range * 5).toInt()))
            mSurface!!.setRange(-range, range, -range, range, minZ, maxZ)
            mAxisBox!!.setRange(-range, range, -range, range, minZ, maxZ)
            mScene3D.update()
        }
    }

    /**
     * TODO: Add kdoc
     */
    fun getImageBuff(time: Long): IntArray {
        tick(time)
        if (mScene3D.notSetUp()) {
            mScene3D.setUpMatrix(mWidth, mHeight)
        }
        render(2)
        return mImageBuff
    }

    /**
     * TODO: Add kdoc
     */
    fun render(type: Int) {
        Arrays.fill(mImageBuff, -0x777778)
        mScene3D.render(2)

        //    Arrays.fill(mScene3D.getZBuff(),Float.MAX_VALUE);

        // mSurface.render(this, zBuff, mImageBuff, mWidth, mHeight);
        //  raster_phong(mSurface,mScene3D,zBuff,mImageBuff,mWidth,mHeight);
    }

    /**
     * TODO: Add kdoc
     */
    fun setSize(width: Int, height: Int) {
        if (mWidth == width && mHeight == height) {
            return
        }
        println("$width $height")
        mWidth = width
        mHeight = height
        mImageBuff = IntArray(mWidth * mHeight)
        buildSurface()
        mScene3D.setUpMatrix(mWidth, mHeight)
        mScene3D.setScreenDim(mWidth, mHeight, mImageBuff, 0x00AAAAAA)
    }
}