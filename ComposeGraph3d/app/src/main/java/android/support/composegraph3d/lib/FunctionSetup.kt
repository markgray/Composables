@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
    "ReplaceJavaStaticMethodWithKotlinAnalog",
    "ReplaceNotNullAssertionWithElvisReturn",
    "MemberVisibilityCanBePrivate",
    "RedundantSuppression"
)

package android.support.composegraph3d.lib

import android.support.composegraph3d.lib.objects.AxisBox
import android.support.composegraph3d.lib.objects.Surface3D
import android.support.composegraph3d.lib.objects.Surface3D.Function
import java.util.*
import kotlin.math.*

/**
 * Manages the setup and rendering of a 3D function graph.
 *
 * This class orchestrates the components necessary to display a 3D surface plot,
 * including the 3D scene, the surface object representing the mathematical function,
 * and the surrounding axis box. It also handles user input for interaction,
 * such as rotating the view, zooming, and changing the plot's range. It supports
 * both static and animated surfaces.
 *
 * @param mWidth The initial width of the rendering viewport.
 * @param mHeight The initial height of the rendering viewport.
 */
class FunctionSetup(var mWidth: Int, var mHeight: Int) {
    /**
     * The main 3D scene object that holds all the visual elements (like the surface
     * and axis box), manages the camera, and handles the rendering pipeline.
     */
    var mScene3D: Scene3D

    /**
     * A buffer to store the pixel data of the rendered 3D scene.
     * This array holds the color of each pixel on the screen, representing the final image
     * that will be displayed. Its size is `mWidth * mHeight`.
     */
    private var mImageBuff: IntArray

    /**
     * An integer code representing the type of graph or rendering style to be used.
     * The specific meaning of the integer values (e.g., 0 for wireframe, 1 for solid, 2 for shaded)
     * is defined by the rendering logic.
     *
     * This property is currently initialized to 2 but its direct usage in the provided
     * code is not fully implemented, suggesting it is a placeholder for future
     * rendering options.
     */
    var mGraphType: Int = 2

    /**
     * Stores the x-coordinate of the last primary touch point (or the start of a drag gesture).
     *
     * This value is captured when a touch-down event occurs (`onMouseDown`) and is used as a
     * reference point for calculating drag movements. It is reset to `Float.NaN` when the
     * touch is released (`onMouseUP`) to indicate that no drag operation is in progress.
     */
    private var mLastTouchX0 = Float.NaN

    /**
     * Stores the Y-coordinate of the last touch down event. This is used in conjunction
     * with `mLastTouchX0` to track the starting point of a gesture, such as a drag
     * for rotating the 3D scene. It is set in `onMouseDown` and used to determine
     * the initial state of a touch interaction. Its value is reset to `Float.NaN` in
     * `onMouseUP` to indicate that no touch is currently active.
     */
    private var mLastTouchY0 = 0f

    /**
     * Stores the x-coordinate from the previous `onMouseDrag` event.
     *
     * This property is used to calculate the incremental movement of the user's finger or
     * mouse during a drag gesture. It is initialized in `onMouseDown` with the starting
     * x-coordinate and then continuously updated in `onMouseDrag` to reflect the
     * most recent position. This allows for smooth, continuous rotation of the 3D scene
     * by tracking the delta between the current and last known positions.
     *
     * @see onMouseDrag
     * @see onMouseDown
     */
    private var mLastTrackBallX = 0f

    /**
     * Stores the y-coordinate from the previous `onMouseDrag` event.
     *
     * This property works in tandem with `mLastTrackBallX` to calculate the incremental
     * vertical movement during a drag gesture. It is initialized in `onMouseDown` with
     * the starting y-coordinate and is continuously updated in `onMouseDrag`. This allows
     * for tracking the delta between the current and last known y-positions, which is
     * used to control the rotation of the 3D scene smoothly.
     *
     * @see onMouseDrag
     * @see onMouseDown
     */
    private var mLastTrackBallY = 0f

    /**
     * Stores the screen width of the 3D scene at the moment a touch-down event occurs.
     * This value is captured in `onMouseDown` and can be used to normalize or scale
     * subsequent drag movements, ensuring consistent interaction behavior even if the
     * screen dimensions change during a gesture (e.g., due to device rotation).
     *
     * @see onMouseDown
     */
    var mDownScreenWidth: Double = 0.0

    /**
     * Represents the 3D surface plot itself, generated from a mathematical function.
     *
     * This object holds the geometry and data for the surface, which is defined by an
     * implementation of `Surface3D.Function`. It can be initialized with a static function
     * or updated to create an animated surface. The `buildSurface` and `buildAnimatedSurface`
     * methods are responsible for creating and configuring this object.
     *
     * @see Surface3D
     * @see buildSurface
     * @see buildAnimatedSurface
     */
    var mSurface: Surface3D? = null

    /**
     * Represents the bounding box with labeled axes that encloses the 3D surface plot.
     *
     * This object provides a visual frame of reference for the graph, showing the
     * boundaries of the x, y, and z axes. It is initialized and added to the scene
     * in the `buildSurface` method, and its range is updated whenever the main
     * plot's range changes (e.g., through zooming).
     *
     * @see AxisBox
     * @see buildSurface
     */
    var mAxisBox: AxisBox? = null

    /**
     * Defines the symmetrical range for the x and y axes of the 3D plot.
     *
     * This value determines the boundaries of the function plot, where the x-axis will
     * span from `-range` to `+range`, and the y-axis will also span from `-range` to `+range`.
     * It is used to configure both the `Surface3D` object and the `AxisBox`. This property
     * can be modified, for example, by a zoom or pan gesture, to change the visible
     * area of the plot.
     *
     * @see onMouseWheel
     * @see buildSurface
     */
    var range: Float = 20f

    /**
     * The minimum value of the z-axis, defining the lower boundary of the 3D plot.
     *
     * This property, in conjunction with `maxZ`, sets the vertical range for the
     * surface plot and the surrounding axis box. Any function values that fall
     * below `minZ` may be clamped or clipped, depending on the rendering implementation.
     * It is used when configuring the `Surface3D` and `AxisBox` to establish the
     * visible vertical extent of the graph.
     *
     * @see maxZ
     * @see Surface3D.setRange
     * @see AxisBox.setRange
     */
    var minZ: Float = -10f

    /**
     * The maximum value of the z-axis, defining the upper boundary of the 3D plot.
     *
     * This property works with `minZ` to set the vertical range for the
     * surface plot and the surrounding axis box. Any function values that exceed
     * `maxZ` may be clamped or clipped, depending on the rendering implementation.
     * It is used when configuring the `Surface3D` and `AxisBox` to establish the
     * visible vertical extent of the graph.
     *
     * @see minZ
     * @see Surface3D.setRange
     * @see AxisBox.setRange
     */
    var maxZ: Float = 10f

    /**
     * Controls the overall zoom level of the camera in the 3D scene.
     *
     * This factor is applied to the camera's projection matrix to magnify or shrink
     * the entire view. It is modified by user input, specifically through a mouse wheel
     * scroll while the control key is pressed. A value of `1f` represents the default
     * zoom level. Values greater than 1 zoom in, and values less than 1 zoom out.
     *
     * @see onMouseWheel
     * @see Scene3D.zoom
     */
    var mZoomFactor: Float = 1f

    /**
     * A flag to control whether the surface plot is static or animated.
     *
     * When `true`, the surface is rendered using a time-dependent function, creating an
     * animation. The `tick()` method must be called repeatedly to update the animation frame.
     * When `false`, a static function is used to render the surface. The specific functions
     * for animated and static modes are defined in `buildAnimatedSurface` and `buildSurface`
     * respectively.
     *
     * @see buildAnimatedSurface
     * @see buildSurface
     * @see tick
     */
    var animated: Boolean = false

    /**
     * A depth buffer used in the rendering process to handle occlusion.
     *
     * Each element in this array corresponds to a pixel on the screen and stores the
     * z-coordinate (depth) of the object closest to the camera at that pixel. When rendering
     * a new pixel, its depth is compared to the value in the buffer. If the new pixel is
     * closer, it is drawn, and the buffer is updated with the new depth value. This ensures
     * that objects in the foreground correctly obscure objects behind them.
     *
     * The size of the buffer is `mWidth * mHeight`. Although initialized, its direct
     * usage is commented out in the current `render` method, suggesting it's part of a
     * rendering pipeline that is not fully active. (In fact it is not used.)
     */
    var zBuff: FloatArray = FloatArray(mWidth * mHeight)

    /**
     * Stores the timestamp from the last animation frame, measured in nanoseconds.
     *
     * This value is used to calculate the time elapsed between frames (the delta time)
     * in the `tick()` method. By comparing the current system time with this stored
     * timestamp, the animation can progress smoothly and independently of the frame rate.
     * It is updated in `tick()` and initialized in `buildAnimatedSurface()`.
     *
     * @see System.nanoTime
     * @see tick
     */
    var nanoTime: Long = 0

    /**
     * A time variable, in seconds, used to drive animations in the 3D plot.
     *
     * This value is continuously incremented in the `tick()` method based on the elapsed
     * time since the last frame, calculated from `System.nanoTime()`. It is then used
     * within the mathematical function defined in `buildAnimatedSurface` to create
     * dynamic, time-dependent behavior in the surface plot, resulting in an animation.
     *
     * @see tick
     * @see buildAnimatedSurface
     */
    var time: Float = 0f

    /**
     * Initializes and configures the 3D surface plot with a static mathematical function.
     *
     * This function performs the following steps:
     *  1. Creates a `Surface3D` object, defining its geometry using a specific mathematical
     *  function `z = f(x, y)`. The function implemented here is:
     *  `z = 0.3 * (cos(d) * (y^2 - x^2) / (1 + d))`, where `d = sqrt(x^2 + y^2)`.
     *  2. Sets the plotting range for the x, y, and z axes on the `Surface3D` object using the
     *  current `range`, `minZ`, and `maxZ` values.
     *  3. Assigns this `Surface3D` object as the primary object to be rendered in the `mScene3D`.
     *  4. Resets the camera to a default position and orientation to properly view the new object.
     *  5. Creates and configures an `AxisBox` to provide a visual frame of reference, setting
     *  its dimensions to match the surface's range.
     *  6. Adds the `AxisBox` to the scene as a "post object," meaning it's rendered after the
     *  main surface.
     *  7. Calls `buildAnimatedSurface()` to set up the alternative animated surface, which can
     *  be toggled later.
     */
    fun buildSurface() {
        mSurface = Surface3D(mFunction = object : Function {
            override fun eval(x: Float, y: Float): Float {
                val d = Math.sqrt((x * x + y * y).toDouble())
                return 0.3f * (Math.cos(d) * (y * y - x * x) / (1 + d)).toFloat()
            }
        })
        mSurface!!.setRange(
            minX = -range,
            maxX = range,
            minY = -range,
            maxY = range,
            minZ = minZ,
            maxZ = maxZ
        )
        mScene3D.setObject(obj = mSurface!!)
        mScene3D.resetCamera()
        mAxisBox = AxisBox()
        mAxisBox!!.setRange(
            minX = -range,
            maxX = range,
            minY = -range,
            maxY = range,
            minZ = minZ,
            maxZ = maxZ
        )
        mScene3D.addPostObject(obj = mAxisBox!!)
        return buildAnimatedSurface()
    }

    /**
     * Initializes our properties [mImageBuff] and [mScene3D].
     */
    init {
        mImageBuff = IntArray(size = mWidth * mHeight)
        // zBuff = new float[w*h];
        mScene3D = Scene3D()
        buildSurface()
        mScene3D.setUpMatrix(width = mWidth, height = mHeight)
        mScene3D.setScreenDim(
            width = mWidth,
            height = mHeight,
            img = mImageBuff,
            background = 0x00AAAAAA
        )
    }

    /**
     * Configures the `Surface3D` object with a time-dependent mathematical function,
     * creating a dynamic, animated surface.
     *
     * This function defines a complex wave-like surface that evolves over time. It calculates
     * the z-coordinate based on the (x, y) position and the `time` variable. The function
     * uses polar coordinates (distance `d` and `angle`) and combines sinusoidal functions
     * to create a visually interesting animation.
     *
     * It initializes the `nanoTime` to the current system time, which is used by the `tick()`
     * method to calculate the elapsed time for the animation. The newly created surface
     * is then set as the main object in the `mScene3D` and its rendering range is configured.
     *
     * @see tick
     * @see time
     * @see animated
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
        mScene3D.setObject(obj = mSurface!!)
        mSurface!!.setRange(
            minX = -range,
            maxX = range,
            minY = -range,
            maxY = range,
            minZ = minZ,
            maxZ = maxZ
        )
    }

    /**
     * Advances the animation by a single frame.
     *
     * This function is called on each frame to update the state of an animated surface. It
     * calculates the elapsed time since the last frame (delta time) and increments the
     * global `time` variable, which drives the animation. It then triggers the recalculation
     * of the surface geometry based on the new time and requests an update of the 3D scene
     * to reflect the changes. This method is only effective when `animated` is `true`.
     *
     * @param now The current system time in nanoseconds, typically from `System.nanoTime()`.
     * This is used to calculate the time delta for smooth, frame-rate-independent animation.
     *
     * @see buildAnimatedSurface
     * @see time
     * @see nanoTime
     */
    fun tick(now: Long) {
        time += (now - nanoTime) * 1E-9f
        nanoTime = now
        mSurface!!.calcSurface(resetZ = false)
        mScene3D.update()
    }

    /**
     * Handles key press events.
     *
     * This function is intended to respond to keyboard input. Currently, it only prints the
     * key code to the console. The commented-out code suggests it was planned to handle
     * specific key presses, such as the spacebar (' ') to switch to the animated surface,
     * but this functionality is not currently implemented.
     *
     * @param c The character code of the key that was typed.
     */
    @Suppress("ReplacePrintlnWithLogging")
    fun onKeyTyped(c: Long) {
        println(c)
        //        switch ((char) c) {
//            case  ' ':
//                buildAnimatedSurface();
//        }
    }

    /**
     * Handles the event when a mouse button is pressed or a touch-down occurs.
     *
     * This function initializes the state for a drag gesture, which is typically used for
     * rotating the 3D scene. It captures the initial screen coordinates (x, y) of the
     * touch/click and records the screen width at that moment. These values serve as the
     * starting point for subsequent drag calculations in `onMouseDrag`.
     *
     * Specifically, it:
     *  - Records the current screen width in `mDownScreenWidth`.
     *  - Stores the initial touch coordinates in `mLastTouchX0` and `mLastTouchY0`.
     *  - Notifies the `Scene3D`'s trackball that a new "down" event has occurred at these coordinates.
     *  - Initializes the "last" trackball position (`mLastTrackBallX`, `mLastTrackBallY`) to the starting coordinates.
     *
     * @param x The x-coordinate of the mouse-down or touch-down event.
     * @param y The y-coordinate of the mouse-down or touch-down event.
     *
     * @see onMouseDrag
     * @see onMouseUP
     */
    fun onMouseDown(x: Float, y: Float) {
        mDownScreenWidth = mScene3D.screenWidth
        mLastTouchX0 = x
        mLastTouchY0 = y
        mScene3D.trackBallDown(x = mLastTouchX0, y = mLastTouchY0)
        mLastTrackBallX = mLastTouchX0
        mLastTrackBallY = mLastTouchY0
    }

    /**
     * Handles the drag gesture to rotate the 3D scene.
     *
     * This function is called repeatedly as the user drags their finger or mouse across the
     * screen. It calculates the displacement from the last known position and uses this
     * information to update the scene's rotation via the trackball mechanism in `Scene3D`.
     *
     * A check is included to prevent excessively large or erratic movements from being
     * processed, which can happen if the drag event coordinates jump unexpectedly. The
     * drag is only processed if the squared distance of the move is less than a threshold (4000f).
     *
     * This method relies on `onMouseDown` to initialize the starting coordinates and will
     * do nothing if a drag has not been properly initiated (i.e., `mLastTouchX0` is `NaN`).
     *
     * @param x The current x-coordinate of the mouse or touch point.
     * @param y The current y-coordinate of the mouse or touch point.
     *
     * @see onMouseDown
     * @see onMouseUP
     * @see Scene3D.trackBallMove
     */
    fun onMouseDrag(x: Float, y: Float) {
        if (java.lang.Float.isNaN(mLastTouchX0)) {
            return
        }
        val moveX: Float = mLastTrackBallX - x
        val moveY: Float = mLastTrackBallY - y
        if (moveX * moveX + moveY * moveY < 4000f) {
            mScene3D.trackBallMove(x = x, y = y)
        }
        mLastTrackBallX = x
        mLastTrackBallY = y
    }

    /**
     * Handles the event when a mouse button is released or a touch gesture ends.
     *
     * This function marks the end of a drag operation by resetting the tracking variables
     * `mLastTouchX0` and `mLastTouchY0` to `Float.NaN`. This indicates that no touch
     * or drag gesture is currently in progress. Subsequent calls to `onMouseDrag` will
     * be ignored until a new `onMouseDown` event occurs.
     *
     * @see onMouseDown
     * @see onMouseDrag
     */
    fun onMouseUP() {
        mLastTouchX0 = Float.NaN
        mLastTouchY0 = Float.NaN
    }

    /**
     * Handles mouse wheel scroll events to control zooming.
     *
     * This function provides two types of zooming based on whether the Control key is pressed:
     *  1. **Scene Zoom (with Control key):** When `ctlDown` is `true`, it adjusts the camera's
     *  overall zoom level (`mZoomFactor`). This magnifies or shrinks the entire 3D scene,
     *  including the surface and the axis box, without changing the plot's coordinate range.
     *  It's like using a magnifying glass.
     *  2. **Range Zoom (without Control key):** When `ctlDown` is `false`, it changes the
     *  plotting range (`range`) of the x and y axes. This effectively zooms into or out of
     *  the mathematical function itself, causing the surface to be re-evaluated over a
     *  smaller or larger domain. The resolution of the surface (`arraySize`) is also
     *  adjusted to maintain detail as the range changes.
     *
     * After either type of zoom, the 3D scene is updated to reflect the changes.
     *
     * @param rotation The amount and direction of the mouse wheel rotation.
     * @param ctlDown A boolean flag that is `true` if the Control key was held down during the scroll.
     */
    fun onMouseWheel(rotation: Float, ctlDown: Boolean) {
        if (ctlDown) {
            mZoomFactor *= 1.01.pow(x = rotation.toDouble()).toFloat()
            mScene3D.zoom = mZoomFactor
            mScene3D.setUpMatrix(mWidth, mHeight)
            mScene3D.update()
        } else {
            range *= 1.01.pow(x = rotation.toDouble()).toFloat()
            mSurface!!.setArraySize(size = Math.min(300, (range * 5).toInt()))
            mSurface!!.setRange(
                minX = -range,
                maxX = range,
                minY = -range,
                maxY = range,
                minZ = minZ,
                maxZ = maxZ
            )
            mAxisBox!!.setRange(
                minX = -range,
                maxX = range,
                minY = -range,
                maxY = range,
                minZ = minZ,
                maxZ = maxZ
            )
            mScene3D.update()
        }
    }

    /**
     * Renders a new frame of the 3D scene and returns the resulting image buffer.
     *
     * This function orchestrates the process of generating a single frame of the graph. It first
     * advances the animation state by calling `tick()` with the timestamp provided in its [Long]
     * parameter [time]. Then, it ensures the scene's projection matrix is correctly configured for
     * the current viewport dimensions. Finally, it triggers the `render()` process to draw the
     * 3D scene into the `mImageBuff` and returns this buffer. The returned `IntArray` contains the
     * pixel data (colors) for the rendered image, which can then be displayed on a screen.
     *
     * @param time The current system time in nanoseconds, used to update animations.
     * @return An `IntArray` representing the rendered image, where each integer is a pixel color.
     */
    fun getImageBuff(time: Long): IntArray {
        tick(now = time)
        if (mScene3D.notSetUp()) {
            mScene3D.setUpMatrix(width = mWidth, height = mHeight)
        }
        render(type = 2)
        return mImageBuff
    }

    /**
     * Renders the 3D scene into the image buffer.
     *
     * This function initiates the rendering process for the entire 3D scene.
     * It first clears the image buffer (`mImageBuff`) by filling it with a default
     * background color (a dark gray, `#888888`). It then calls the main `render`
     * method of the `Scene3D` object, which handles the drawing of all scene elements
     * (like the surface plot and axis box) into the buffer.
     *
     * The `type` parameter is currently unused in the function body, as `mScene3D.render(2)`
     * is hardcoded. This suggests the parameter is a placeholder for future rendering modes
     * (e.g., wireframe, solid, different shading models). The commented-out code indicates
     * that other rendering strategies, such as custom rasterization with a Z-buffer,
     * were considered or used previously.
     *
     * @param type An integer representing the desired rendering mode. This parameter is
     * currently ignored, and a default mode (2) is always used.
     */
    fun render(type: Int) {
        Arrays.fill(mImageBuff, -0x777778)
        mScene3D.render(type = 2)

        //    Arrays.fill(mScene3D.getZBuff(),Float.MAX_VALUE);

        // mSurface.render(this, zBuff, mImageBuff, mWidth, mHeight);
        //  raster_phong(mSurface,mScene3D,zBuff,mImageBuff,mWidth,mHeight);
    }

    /**
     * Resizes the rendering viewport and rebuilds the scene to fit the new dimensions.
     *
     * This function is called when the size of the drawing surface (e.g., the screen or a composable)
     * changes. It updates the internal width and height, reallocates the image buffer (`mImageBuff`)
     * to the new size, and then reinitializes the entire 3D scene by calling `buildSurface()`.
     * Finally, it updates the `Scene3D` object with the new dimensions for its camera projection
     * and screen mapping.
     *
     * An optimization is included to do nothing if the new dimensions are identical to the current ones,
     * preventing unnecessary re-rendering and object reconstruction.
     *
     * @param width The new width of the viewport in pixels.
     * @param height The new height of the viewport in pixels.
     */
    fun setSize(width: Int, height: Int) {
        if (mWidth == width && mHeight == height) {
            return
        }
        @Suppress("ReplacePrintlnWithLogging")
        println("$width $height")
        mWidth = width
        mHeight = height
        mImageBuff = IntArray(size = mWidth * mHeight)
        buildSurface()
        mScene3D.setUpMatrix(width = mWidth, height = mHeight)
        mScene3D.setScreenDim(
            width = mWidth,
            height = mHeight,
            img = mImageBuff,
            background = 0x00AAAAAA
        )
    }
}