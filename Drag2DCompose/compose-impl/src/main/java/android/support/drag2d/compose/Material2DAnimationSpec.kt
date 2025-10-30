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

package android.support.drag2d.compose

import android.support.drag2d.lib.MaterialEasing
import android.support.drag2d.lib.MaterialVelocity
import android.support.drag2d.lib.Velocity2D
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.AnimationVector3D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedFiniteAnimationSpec
import kotlin.math.roundToLong

/**
 * Default maximum velocity.
 *
 * This value has been tuned to provide smooth animations.
 */
private const val DEFAULT_MAX_VELOCITY = 1000f

/**
 * Default maximum acceleration.
 *
 * This value has been tuned to provide smooth animations.
 */
private const val DEFAULT_MAX_ACCELERATION = 1000f

/**
 * Default easing used for animations.
 *
 * This value has been tuned to provide smooth animations.
 */
private val defaultEasing: MaterialVelocity.Easing by lazy(mode = LazyThreadSafetyMode.NONE) {
    MaterialEasing.EASE_OUT_BACK
}

/**
 * Creates a [Material2DAnimationSpec] that can be used to animate any value.
 *
 * This animation spec is great for animations that feel "natural" since it's based on
 * Material Design's motion principles. It's particularly well-suited for animations that are
 * driven by user input, such as dragging, where the animation needs to smoothly take over from the
 * user's velocity.
 *
 * The animation is powered by [Velocity2D], which calculates the animation path based on the
 * initial and target values, initial velocity, and the provided constraints. This version of
 * `materialVelocity2D` applies the same animation parameters to all dimensions of the animated
 * value.
 *
 * For animating values with more than two dimensions (e.g., `AnimationVector3D` or
 * `AnimationVector4D`), this spec uses two separate `Velocity2D` instances. The first instance
 * animates the first and second dimensions, and the second instance animates the third and fourth.
 *
 * @param durationMs The desired duration of the animation in milliseconds. The actual duration may
 * vary depending on the initial velocity and distance to the target.
 * @param maxVelocity The maximum velocity that the animation can reach. This constraint helps
 * prevent the animation from becoming too fast and jarring.
 * @param maxAcceleration The maximum acceleration to be applied. This limits how quickly the
 * velocity can change, contributing to a smoother motion.
 * @param easing The easing curve to be applied to the animation. This determines the rate of
 * change of the animation over time, for example, starting slow and speeding up.
 *
 * @return An [AnimationSpec] that can be used with Compose's animation APIs.
 *
 */
fun <T> materialVelocity2D(
    durationMs: Int = AnimationConstants.DefaultDurationMillis,
    maxVelocity: Float = DEFAULT_MAX_VELOCITY,
    maxAcceleration: Float = DEFAULT_MAX_ACCELERATION,
    easing: MaterialVelocity.Easing = defaultEasing
): AnimationSpec<T> {
    return Material2DAnimationSpec(
        desiredDurationMs = durationMs,
        maxVelocityA = maxVelocity,
        maxVelocityB = maxVelocity,
        maxAccelerationA = maxAcceleration,
        maxAccelerationB = maxAcceleration,
        materialEasingA = easing,
        materialEasingB = easing
    )
}

/**
 * Creates a [Material2DAnimationSpec] with distinct animation parameters for different
 * components of the animated value.
 *
 * This version of `materialVelocity2D` is useful for animations where different dimensions need
 * to behave differently. For example, you might want a different easing curve or velocity limit
 * for the x-axis compared to the y-axis.
 *
 * The animation is powered by two [Velocity2D] instances. The first instance (`A`) animates the
 * first two dimensions of the value (e.g., x and y), while the second instance (`B`) animates the
 * third and fourth dimensions (if they exist).
 *
 * This allows for fine-grained control over multi-dimensional animations, ensuring they feel
 * natural and responsive according to Material Design's motion principles.
 *
 * @param T The type of the value to be animated.
 * @param durationMs The desired duration of the animation in milliseconds. The actual duration
 * may vary depending on the initial velocities and distances to the target.
 * @param maxVelocityA The maximum velocity for the first animation component (dims 1 and 2).
 * @param maxAccelerationA The maximum acceleration for the first animation component.
 * @param easingA The easing curve for the first animation component.
 * @param maxVelocityB The maximum velocity for the second animation component (dims 3 and 4).
 * @param maxAccelerationB The maximum acceleration for the second animation component.
 * @param easingB The easing curve for the second animation component.
 *
 * @return An [AnimationSpec] that can be used with Compose's animation APIs.
 */
@Suppress("unused")
fun <T> materialVelocity2D(
    durationMs: Int,
    maxVelocityA: Float,
    maxAccelerationA: Float,
    easingA: MaterialVelocity.Easing,
    maxVelocityB: Float,
    maxAccelerationB: Float,
    easingB: MaterialVelocity.Easing
): AnimationSpec<T> {
    return Material2DAnimationSpec(
        desiredDurationMs = durationMs,
        maxVelocityA = maxVelocityA,
        maxVelocityB = maxVelocityB,
        maxAccelerationA = maxAccelerationA,
        maxAccelerationB = maxAccelerationB,
        materialEasingA = easingA,
        materialEasingB = easingB
    )
}

/**
 * An animation spec that uses one or two [Velocity2D] instances to animate values.
 *
 * This spec is designed to handle animations for values with up to four dimensions.
 * It uses one `Velocity2D` instance (`A`) for the first two dimensions and a second
 * instance (`B`) for the third and fourth dimensions. This allows for creating
 * complex, multi-dimensional animations with distinct physical properties (like velocity,
 * acceleration, and easing) for different components of the animated value.
 *
 * For values with one or two dimensions (e.g., `Float`, `Int`, `Dp`, `Offset`), only the
 * first `Velocity2D` instance (`A`) is used.
 *
 * For values with three or four dimensions (e.g., `Size`, `Rect`), both instances are used.
 *
 * This class is not typically used directly. Instead, create an instance through the
 * [materialVelocity2D] factory functions.
 *
 * @param T The type of the value to be animated.
 * @property desiredDurationMs The desired duration of the animation in milliseconds.
 * @property maxVelocityA The maximum velocity for the first animation component (dims 1-2).
 * @property maxVelocityB The maximum velocity for the second animation component (dims 3-4).
 * @property maxAccelerationA The maximum acceleration for the first animation component.
 * @property maxAccelerationB The maximum acceleration for the second animation component.
 * @property materialEasingA The easing curve for the first animation component.
 * @property materialEasingB The easing curve for the second animation component.
 */
class Material2DAnimationSpec<T>(
    private val desiredDurationMs: Int = AnimationConstants.DefaultDurationMillis,
    private val maxVelocityA: Float = DEFAULT_MAX_VELOCITY,
    private val maxVelocityB: Float = DEFAULT_MAX_VELOCITY,
    private val maxAccelerationA: Float = DEFAULT_MAX_ACCELERATION,
    private val maxAccelerationB: Float = DEFAULT_MAX_ACCELERATION,
    private val materialEasingA: MaterialVelocity.Easing = defaultEasing,
    private val materialEasingB: MaterialVelocity.Easing = defaultEasing,
) : FiniteAnimationSpec<T> {
    private val velocity2DA: Velocity2D = Velocity2D()
    private val velocity2DB: Velocity2D = Velocity2D()

    /**
     * Creates a [VectorizedFiniteAnimationSpec] for the given [AnimationVector] type.
     *
     * This method is called by the animation system to get a vectorized version of this animation
     * spec, which can then be used to animate different types of [AnimationVector]s (e.g.,
     * [AnimationVector1D], [AnimationVector2D], etc.).
     *
     * @param converter A [TwoWayConverter] that can convert the animation's data type `T` to and
     * from an [AnimationVector].
     * @return A [VectorizedMaterial2DAnimationSpec] instance that contains the core animation logic
     * and is configured with the parameters from this spec.
     */
    override fun <V : AnimationVector> vectorize(
        converter: TwoWayConverter<T, V>
    ): VectorizedFiniteAnimationSpec<V> =
        VectorizedMaterial2DAnimationSpec(
            velocity2DA = velocity2DA,
            velocity2DB = velocity2DB,
            desiredDurationMs = desiredDurationMs,
            maxVelocityA = maxVelocityA,
            maxVelocityB = maxVelocityB,
            maxAccelerationA = maxAccelerationA,
            maxAccelerationB = maxAccelerationB,
            materialEasingA = materialEasingA,
            materialEasingB = materialEasingB
        )
}

/**
 * A vectorized animation spec that uses one or two [Velocity2D] instances to drive the animation.
 * This class is the core implementation that handles the animation of [AnimationVector] types.
 *
 * It works by splitting the dimensions of an [AnimationVector] between two [Velocity2D] instances.
 *  - `velocity2DA` animates the first and second dimensions.
 *  - `velocity2DB` animates the third and fourth dimensions.
 *
 * This allows for independent control over different parts of a multi-dimensional animation. For
 * example, in an animation of a `Rect`, the position (x, y) can have different physics parameters
 * than its size (width, height).
 *
 * The `config` function is responsible for setting up the `Velocity2D` instances based on the
 * initial/target values and velocities. It caches the last used parameters to avoid redundant
 * re-configuration, improving performance.
 *
 * The total duration of the animation is determined by the longer of the two `Velocity2D`
 * animations, ensuring that the entire animation completes before it's considered finished.
 *
 * @param V The type of [AnimationVector] to be animated.
 * @property velocity2DA The [Velocity2D] instance for animating the first two dimensions.
 * @property velocity2DB The [Velocity2D] instance for animating the third and fourth dimensions.
 * @property desiredDurationMs The desired duration of the animation in milliseconds.
 * @property maxVelocityA The maximum velocity for the first animation component.
 * @property maxVelocityB The maximum velocity for the second animation component.
 * @property maxAccelerationA The maximum acceleration for the first animation component.
 */
@Suppress("UNCHECKED_CAST")
private class VectorizedMaterial2DAnimationSpec<V : AnimationVector>(
    private val velocity2DA: Velocity2D,
    private val velocity2DB: Velocity2D,
    private val desiredDurationMs: Int,
    private val maxVelocityA: Float,
    private val maxVelocityB: Float,
    private val maxAccelerationA: Float,
    private val maxAccelerationB: Float,
    private val materialEasingA: MaterialVelocity.Easing,
    private val materialEasingB: MaterialVelocity.Easing
) : VectorizedFiniteAnimationSpec<V> {
    private lateinit var lastInitial: FloatArray
    private lateinit var lastTarget: FloatArray
    private lateinit var lastVelocity: FloatArray

    /**
     * Actual duration of [velocity2DA], this is updated by the output of [Velocity2D.getDuration].
     *
     * It's necessary since [velocity2DA] and [velocity2DB] are not guaranteed to have the same
     * duration.
     */
    private var durationASecs = desiredDurationMs / 1000f

    /**
     * Actual duration of [velocity2DB], this is updated by the output of [Velocity2D.getDuration].
     *
     * It's necessary since [velocity2DA] and [velocity2DB] are not guaranteed to have the same
     * duration.
     */
    private var durationBSecs = desiredDurationMs / 1000f

    /**
     * Configures the underlying [Velocity2D] instances with the animation parameters.
     *
     * This function checks if the animation parameters have changed since the last call. If they
     * have, it re-configures `velocity2DA` and `velocity2DB` with the new start/end values,
     * velocities, and physical constraints. This caching mechanism prevents redundant
     * calculations, improving performance when the animation state is updated frequently.
     *
     * The dimensions of the input arrays are mapped as follows:
     *  - `velocity2DA` handles dimensions 0 and 1 (e.g., x and y).
     *  - `velocity2DB` handles dimensions 2 and 3 (e.g., width and height).
     *
     * If a dimension is not present in the input arrays, a default value of `0f` is used.
     *
     * @param initialValue The starting value of the animation, as a `FloatArray`.
     * @param targetValue The target value of the animation, as a `FloatArray`.
     * @param initialVelocity The starting velocity of the animation, as a `FloatArray`.
     */
    private fun config(
        initialValue: FloatArray,
        targetValue: FloatArray,
        initialVelocity: FloatArray
    ) {
        if (!::lastInitial.isInitialized || !::lastTarget.isInitialized ||
            !::lastVelocity.isInitialized || !lastInitial.contentEquals(other = initialValue) ||
            !lastTarget.contentEquals(other = targetValue) ||
            !lastVelocity.contentEquals(other = initialVelocity)
        ) {
            lastInitial = initialValue
            lastTarget = targetValue
            lastVelocity = initialVelocity
            val desiredDurationSecs = desiredDurationMs / 1000f

            when (initialValue.size) {
                1, 2, 3, 4 -> {
                    velocity2DA.configure(
                        /* posX = */ initialValue.getOrElse(0) { 0f },
                        /* posY = */ initialValue.getOrElse(1) { 0f },
                        /* velocityX = */ initialVelocity.getOrElse(0) { 0f },
                        /* velocityY = */ initialVelocity.getOrElse(1) { 0f },
                        /* destinationX = */ targetValue.getOrElse(0) { 0f },
                        /* destinationY = */ targetValue.getOrElse(1) { 0f },
                        /* duration = */ desiredDurationSecs,
                        /* maxV = */ maxVelocityA,
                        /* maxA = */ maxAccelerationA,
                        /* easing = */ materialEasingA
                    )
                    velocity2DB.configure(
                        /* posX = */ initialValue.getOrElse(2) { 0f },
                        /* posY = */ initialValue.getOrElse(3) { 0f },
                        /* velocityX = */ initialVelocity.getOrElse(2) { 0f },
                        /* velocityY = */ initialVelocity.getOrElse(3) { 0f },
                        /* destinationX = */ targetValue.getOrElse(2) { 0f },
                        /* destinationY = */ targetValue.getOrElse(3) { 0f },
                        /* duration = */ desiredDurationSecs,
                        /* maxV = */ maxVelocityB,
                        /* maxA = */ maxAccelerationB,
                        /* easing = */ materialEasingB
                    )
                }

                else -> {

                }
            }
        }
    }

    /**
     * TODO: Continue here.
     */
    override fun getDurationNanos(initialValue: V, targetValue: V, initialVelocity: V): Long {
        return getDurationNanos(
            initialValue = FloatArrayConverter.convertFromVector(initialValue),
            targetValue = FloatArrayConverter.convertFromVector(targetValue),
            initialVelocity = FloatArrayConverter.convertFromVector(initialVelocity),
        )
    }

    /**
     * The reported duration will correspond to the [Velocity2D] instance that last the longest.
     *
     * That way the animation will be considered finished when both instances are guaranteed to be
     * stopped.
     */
    private fun getDurationNanos(
        initialValue: FloatArray,
        targetValue: FloatArray,
        initialVelocity: FloatArray
    ): Long {
        config(
            initialValue = initialValue,
            targetValue = targetValue,
            initialVelocity = initialVelocity
        )

        durationASecs = velocity2DA.duration
        durationBSecs = velocity2DB.duration
        return (maxOf(durationASecs, durationBSecs) * 1_000_000_000f).roundToLong()
    }

    override fun getValueFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        return getValueFromNanos(
            playTimeNanos = playTimeNanos,
            initialValue = FloatArrayConverter.convertFromVector(initialValue),
            targetValue = FloatArrayConverter.convertFromVector(targetValue),
            initialVelocity = FloatArrayConverter.convertFromVector(initialVelocity),
        )
    }

    private fun getValueFromNanos(
        playTimeNanos: Long,
        initialValue: FloatArray,
        targetValue: FloatArray,
        initialVelocity: FloatArray
    ): V {
        config(
            initialValue = initialValue,
            targetValue = targetValue,
            initialVelocity = initialVelocity
        )
        val playTimeSecs = playTimeNanos / 1_000_000_000f
        val playTimeSecsA = playTimeSecs.coerceAtMost(durationASecs)
        val playTimeSecsB = playTimeSecs.coerceAtMost(durationBSecs)

        return FloatArrayConverter.convertToVector(
            when (initialValue.size) {
                1 -> {
                    floatArrayOf(velocity2DA.getX(playTimeSecsA))
                }

                2 -> {
                    floatArrayOf(
                        velocity2DA.getX(playTimeSecsA),
                        velocity2DA.getY(playTimeSecsA)
                    )
                }

                3 -> {
                    floatArrayOf(
                        velocity2DA.getX(playTimeSecsA),
                        velocity2DA.getY(playTimeSecsA),
                        velocity2DB.getX(playTimeSecsB)
                    )
                }

                4 -> {
                    floatArrayOf(
                        velocity2DA.getX(playTimeSecsA),
                        velocity2DA.getY(playTimeSecsA),
                        velocity2DB.getX(playTimeSecsB),
                        velocity2DB.getY(playTimeSecsB)
                    )
                }

                else -> {
                    floatArrayOf()
                }
            }
        ) as V
    }

    override fun getVelocityFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        return getVelocityFromNanos(
            playTimeNanos = playTimeNanos,
            initialValue = FloatArrayConverter.convertFromVector(initialValue),
            targetValue = FloatArrayConverter.convertFromVector(targetValue),
            initialVelocity = FloatArrayConverter.convertFromVector(initialVelocity),
        )
    }

    private fun getVelocityFromNanos(
        playTimeNanos: Long,
        initialValue: FloatArray,
        targetValue: FloatArray,
        initialVelocity: FloatArray
    ): V {
        config(
            initialValue = initialValue,
            targetValue = targetValue,
            initialVelocity = initialVelocity
        )

        val playTimeSecs = playTimeNanos / 1_000_000_000f
        val playTimeSecsA = playTimeSecs.coerceAtMost(durationASecs)
        val playTimeSecsB = playTimeSecs.coerceAtMost(durationBSecs)

        return FloatArrayConverter.convertToVector(
            when (initialValue.size) {
                1 -> {
                    floatArrayOf(velocity2DA.getVX(playTimeSecsA))
                }

                2 -> {
                    floatArrayOf(
                        velocity2DA.getVX(playTimeSecsA),
                        velocity2DA.getVY(playTimeSecsA)
                    )
                }

                3 -> {
                    floatArrayOf(
                        velocity2DA.getVX(playTimeSecsA),
                        velocity2DA.getVY(playTimeSecsA),
                        velocity2DB.getVX(playTimeSecsB)
                    )
                }

                4 -> {
                    floatArrayOf(
                        velocity2DA.getVX(playTimeSecsA),
                        velocity2DA.getVY(playTimeSecsA),
                        velocity2DB.getVX(playTimeSecsB),
                        velocity2DB.getVY(playTimeSecsB)
                    )
                }

                else -> {
                    floatArrayOf()
                }
            }
        ) as V
    }
}

private val FloatArrayConverter: TwoWayConverter<FloatArray, AnimationVector> = TwoWayConverter(
    convertToVector = {
        when (it.size) {
            1 -> {
                AnimationVector1D(it[0])
            }

            2 -> {
                AnimationVector2D(it[0], it[1])
            }

            3 -> {
                AnimationVector3D(it[0], it[1], it[2])
            }

            4 -> {
                AnimationVector4D(it[0], it[1], it[2], it[3])
            }

            else -> AnimationVector1D(0f)
        }
    },
    convertFromVector = {
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (it) {
            is AnimationVector1D -> {
                floatArrayOf(it.value)
            }

            is AnimationVector2D -> {
                floatArrayOf(it.v1, it.v2)
            }

            is AnimationVector3D -> {
                floatArrayOf(it.v1, it.v2, it.v3)
            }

            is AnimationVector4D -> {
                floatArrayOf(it.v1, it.v2, it.v3, it.v4)
            }

            else -> {
                floatArrayOf()
            }
        }
    }
)