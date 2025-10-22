@file:Suppress(
    "LocalVariableName",
    "JoinDeclarationAndAssignment",
    "ReplaceJavaStaticMethodWithKotlinAnalog"
)

package com.example.examplescomposemotionlayout


import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.constraintlayout.compose.MotionLayout


/**
 * A Composable that displays an image with various transformations and color adjustments,
 * suitable for use within [MotionLayout]. This overload accepts a drawable resource ID and
 * calls the overload that accepts a [Painter] created from that drawable resource.
 *
 * The image can be panned, zoomed, and rotated. Additionally, its contrast, brightness,
 * saturation, and warmth can be adjusted. These parameters are typically driven by the
 * progress of a [MotionLayout] transition.
 *
 * @param panX The horizontal panning of the image, where 0.0f is the left edge and 1.0f is the
 * right edge.
 * @param panY The vertical panning of the image, where 0.0f is the top edge and 1.0f is the
 * bottom edge.
 * @param zoom The zoom level of the image. 1.0f is the default, fitting the image within the
 * bounds while preserving aspect ratio. Values greater than 1.0f zoom in.
 * @param rotate The rotation of the image in degrees.
 * @param contrast The contrast of the image. 1.0f is the default (no change).
 * @param brightness The brightness of the image. 1.0f is the default (no change).
 * @param saturation The saturation of the image. 1.0f is the default (no change).
 * 0.0f is grayscale.
 * @param warmth The color temperature warmth of the image. 1.0f is the default (no change).
 * @param id The drawable resource ID for the image to be displayed.
 * @param modifier The modifier to be applied to the canvas that draws the image.
 */
@Suppress("unused")
@Composable
fun MotionImage(
    panX: Float = 0.0f,
    panY: Float = 0.0f,
    zoom: Float = 1f,
    rotate: Float = 0f,
    contrast: Float = 1f,
    brightness: Float = 1f,
    saturation: Float = 1f,
    warmth: Float = 1f,
    @DrawableRes id: Int,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.fillMaxSize()
) {
    MotionImage(
        panX = panX,
        panY = panY,
        zoom = zoom,
        rotate = rotate,
        contrast = contrast,
        brightness = brightness,
        saturation = saturation,
        warmth = warmth,
        painter = painterResource(id = id),
        modifier = modifier
    )
}

/**
 * A Composable that displays an image with various transformations applied.
 * This allows for dynamic control over the image's position, scale, rotation,
 * and color properties, making it suitable for animations and interactive views.
 * The image is drawn on a `Canvas` and clipped to the bounds of the composable.
 * This overload accepts a [Painter] instead of a drawable resource ID.
 * TODO: Continue here.
 *
 * @param panX Horizontal pan of the image. A value of 0.0 aligns the left edge of the image
 * with the left edge of the view, while 1.0 aligns the right edges.
 * @param panY Vertical pan of the image. A value of 0.0 aligns the top edge of the image
 * with the top edge of the view, while 1.0 aligns the bottom edges.
 * @param zoom The zoom level of the image. A value of 1.0 means the image is scaled to fit
 * within the view bounds while maintaining its aspect ratio. Values greater than 1.0 zoom in.
 * @param rotate The rotation of the image in degrees.
 * @param contrast The contrast of the image. A value of 1.0 means no change.
 * @param brightness The brightness of the image. A value of 1.0 means no change.
 * @param saturation The saturation of the image. A value of 1.0 means no change, while 0.0
 * results in a grayscale image.
 * @param warmth The color temperature of the image. A value of 1.0 means no change.
 * Values greater than 1.0 make the image appear warmer (more orange/red), while values
 * less than 1.0 make it appear cooler (more blue).
 * @param painter The [Painter] to draw.
 * @param modifier The modifier to be applied to the `Canvas`.
 */
@Composable
fun MotionImage(
    panX: Float = 0.0f,
    panY: Float = 0.0f,
    zoom: Float = 1f,
    rotate: Float = 0f,
    contrast: Float = 1f,
    brightness: Float = 1f,
    saturation: Float = 1f,
    warmth: Float = 1f,
    painter: Painter,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.fillMaxSize()
) {

    Canvas(modifier = modifier) {
        clipRect {
            val iw: Float = size.width
            val ih: Float = size.height
            val sw: Float = painter.intrinsicSize.width
            val sh: Float = painter.intrinsicSize.height

            val scale: Float = (if (iw * sh < ih * sw) sw / iw else sh / ih)
            val sx: Float = zoom * sw / iw / scale
            val sy: Float = zoom * sh / ih / scale
            val tx: Float = (sw - sx * iw) * panX
            val ty: Float = (sh - sy * ih) * panY
            @Suppress("ReplacePrintlnWithLogging")
            println("pan = $tx,$ty")
            val cf = ColorMatrix()
            updateMatrix(
                out = cf,
                brightness = brightness,
                saturation = saturation,
                contrast = contrast,
                warmth = warmth
            )
            with(receiver = painter) {
                withTransform(transformBlock = {
                    rotate(degrees = rotate)
                    translate(left = tx, top = ty)
                    scale(scaleX = sx, scaleY = sy)
                }) {
                    draw(size = size, colorFilter = ColorFilter.colorMatrix(colorMatrix = cf))
                }
            }
        }
    }

}

private fun saturation(mMatrix: FloatArray, saturationStrength: Float) {
    val Rf = 0.2999f
    val Gf = 0.587f
    val Bf = 0.114f
    val ms = 1.0f - saturationStrength
    val Rt: Float = Rf * ms
    val Gt: Float = Gf * ms
    val Bt: Float = Bf * ms
    mMatrix[0] = Rt + saturationStrength
    mMatrix[1] = Gt
    mMatrix[2] = Bt
    mMatrix[3] = 0f
    mMatrix[4] = 0f
    mMatrix[5] = Rt
    mMatrix[6] = Gt + saturationStrength
    mMatrix[7] = Bt
    mMatrix[8] = 0f
    mMatrix[9] = 0f
    mMatrix[10] = Rt
    mMatrix[11] = Gt
    mMatrix[12] = Bt + saturationStrength
    mMatrix[13] = 0f
    mMatrix[14] = 0f
    mMatrix[15] = 0f
    mMatrix[16] = 0f
    mMatrix[17] = 0f
    mMatrix[18] = 1f
    mMatrix[19] = 0f
}

private fun warmth(matrix: FloatArray, warmth: Float) {
    var warmthVar: Float = warmth
    val baseTemperature = 5000f
    if (warmthVar <= 0) warmthVar = .01f
    var tmpColor_r: Float
    var tmpColor_g: Float
    var tmpColor_b: Float
    var kelvin: Float = baseTemperature / warmthVar
    run {
        // simulate a black body radiation
        val centiKelvin: Float = kelvin / 100
        val colorR: Float
        val colorG: Float
        val colorB: Float
        if (centiKelvin > 66) {
            val tmp: Float = centiKelvin - 60f
            // Original statements (all decimal values)
            // colorR = (329.698727446f * (float) Math.pow(tmp, -0.1332047592f))
            // colorG = (288.1221695283f * (float) Math.pow(tmp, 0.0755148492f))
            colorR = 329.69873f * Math.pow(tmp.toDouble(), -0.13320476).toFloat()
            colorG = 288.12216f * Math.pow(tmp.toDouble(), 0.07551485).toFloat()
        } else {
            // Original statements (all decimal values)
            // colorG = (99.4708025861f * (float) Math.log(centiKelvin) - 161.1195681661f);
            colorG = 99.4708f * Math.log(centiKelvin.toDouble()).toFloat() - 161.11957f
            colorR = 255f
        }
        colorB = if (centiKelvin < 66) {
            if (centiKelvin > 19) {
                // Original statements (all decimal values)
                // 138.5177312231f * (float) Math.log(centiKelvin - 10) - 305.0447927307f);
                (138.51773f * Math.log((centiKelvin - 10).toDouble()).toFloat() - 305.0448f)
            } else {
                0f
            }
        } else {
            255f
        }
        tmpColor_r = Math.min(255f, Math.max(colorR, 0f))
        tmpColor_g = Math.min(255f, Math.max(colorG, 0f))
        tmpColor_b = Math.min(255f, Math.max(colorB, 0f))
    }
    var color_r: Float = tmpColor_r
    var color_g: Float = tmpColor_g
    var color_b: Float = tmpColor_b
    kelvin = baseTemperature

    // simulate a black body radiation
    val centiKelvin: Float = kelvin / 100
    val colorR: Float
    val colorG: Float
    val colorB: Float
    if (centiKelvin > 66) {
        val tmp: Float = centiKelvin - 60f
        // Original statements (all decimal values)
        //  colorR = (329.698727446f * (float) Math.pow(tmp, -0.1332047592f));
        //  colorG = (288.1221695283f * (float) Math.pow(tmp, 0.0755148492f));
        colorR = 329.69873f * Math.pow(tmp.toDouble(), -0.13320476).toFloat()
        colorG = 288.12216f * Math.pow(tmp.toDouble(), 0.07551485).toFloat()
    } else {
        // Original statements (all decimal values)
        //float of (99.4708025861f * (float) Math.log(centiKelvin) - 161.1195681661f);
        colorG = 99.4708f * Math.log(centiKelvin.toDouble()).toFloat() - 161.11957f
        colorR = 255f
    }
    colorB = if (centiKelvin < 66) {
        if (centiKelvin > 19) {
            // Original statements (all decimal values)
            //float of (138.5177312231 * Math.log(centiKelvin - 10) - 305.0447927307);
            138.51773f * Math.log((centiKelvin - 10).toDouble()).toFloat() - 305.0448f
        } else {
            0f
        }
    } else {
        255f
    }
    tmpColor_r = Math.min(255f, Math.max(colorR, 0f))
    tmpColor_g = Math.min(255f, Math.max(colorG, 0f))
    tmpColor_b = Math.min(255f, Math.max(colorB, 0f))

    color_r /= tmpColor_r
    color_g /= tmpColor_g
    color_b /= tmpColor_b
    matrix[0] = color_r
    matrix[1] = 0f
    matrix[2] = 0f
    matrix[3] = 0f
    matrix[4] = 0f
    matrix[5] = 0f
    matrix[6] = color_g
    matrix[7] = 0f
    matrix[8] = 0f
    matrix[9] = 0f
    matrix[10] = 0f
    matrix[11] = 0f
    matrix[12] = color_b
    matrix[13] = 0f
    matrix[14] = 0f
    matrix[15] = 0f
    matrix[16] = 0f
    matrix[17] = 0f
    matrix[18] = 1f
    matrix[19] = 0f
}

private fun brightness(matrix: FloatArray, brightness: Float) {
    matrix[0] = brightness
    matrix[1] = 0f
    matrix[2] = 0f
    matrix[3] = 0f
    matrix[4] = 0f
    matrix[5] = 0f
    matrix[6] = brightness
    matrix[7] = 0f
    matrix[8] = 0f
    matrix[9] = 0f
    matrix[10] = 0f
    matrix[11] = 0f
    matrix[12] = brightness
    matrix[13] = 0f
    matrix[14] = 0f
    matrix[15] = 0f
    matrix[16] = 0f
    matrix[17] = 0f
    matrix[18] = 1f
    matrix[19] = 0f
}

/**
 * TODO: Add kdoc
 */
fun updateMatrix(
    out: ColorMatrix,
    brightness: Float = 1f,
    saturation: Float = 1f,
    contrast: Float = 1f,
    warmth: Float = 1f
) {
    var used = false
    val tmp = ColorMatrix()
    out.reset()
    if (saturation != 1.0f) {
        saturation(mMatrix = tmp.values, saturationStrength = saturation)
        tmp.values.copyInto(destination = out.values)
        used = true
    }
    if (contrast != 1.0f) {
        if (!used) {
            out.setToScale(
                redScale = contrast,
                greenScale = contrast,
                blueScale = contrast,
                alphaScale = 1f
            )
        } else {
            tmp.setToScale(
                redScale = contrast,
                greenScale = contrast,
                blueScale = contrast,
                alphaScale = 1f
            )
            out.timesAssign(colorMatrix = tmp)
        }
        used = true
    }
    if (warmth != 1.0f) {
        if (!used) {
            warmth(matrix = out.values, warmth = warmth)
        } else {
            warmth(matrix = tmp.values, warmth = warmth)
            out.timesAssign(colorMatrix = tmp)
        }
        used = true
    }
    if (brightness != 1.0f) {

        if (!used) {
            brightness(matrix = out.values, brightness = brightness)
        } else {
            brightness(tmp.values, brightness)
            out.timesAssign(tmp)
        }
        @Suppress("UNUSED_VALUE", "AssignedValueIsNeverRead")
        used = true
    }

}
