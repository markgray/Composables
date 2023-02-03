/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import com.google.android.material.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.ShapeAppearancePathProvider

/**
 * A Card view that clips the content of any shape, this should be done upstream in card,
 * working around it for now.
 *
 * @param context The [Context] the view is running in, through which it can access the current theme,
 * resources, etc
 * @param attrs The attributes of the XML tag that is inflating the view.
 * @param defStyle An attribute in the current theme that contains a reference to a style resource
 * that supplies default values for the view. Can be 0 to not look for defaults.
 */
class MaskedCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyle) {
    /**
     * We use this to call its [ShapeAppearancePathProvider.calculatePath] method in our [onSizeChanged]
     * override in order to write the [ShapeAppearanceModel] field [shapeAppearance] into the [Path]
     * field [path] that we use to clip our [MaskedCardView] in our [onDraw] override.
     */
    @SuppressLint("RestrictedApi") // TODO: Keep your eye on this one, it may be changed!
    private val pathProvider: ShapeAppearancePathProvider = ShapeAppearancePathProvider()

    /**
     * [Path] that we use to clip our [MaskedCardView] in our [onDraw] override. It is written to
     * by the [ShapeAppearancePathProvider.calculatePath] method in our [onSizeChanged] override
     * using a [RectF] that is the new width by the new height as the bounds for the path.
     */
    private val path: Path = Path()

    /**
     * This is the [ShapeAppearanceModel] that we write to our [Path] field [path] in our [onSizeChanged]
     * override using the method [ShapeAppearancePathProvider.calculatePath].
     */
    private val shapeAppearance: ShapeAppearanceModel =
        ShapeAppearanceModel.builder(
            context, attrs, defStyle, R.style.Widget_MaterialComponents_CardView
        ).build()

    /**
     * This is the [RectF] that we use in our [onSizeChanged] override to provide the bounds for the
     * [Path] that [ShapeAppearancePathProvider.calculatePath] writes to our [Path] field [path]. Its
     * [RectF.right] is set to the new width of our view, and its [RectF.bottom] is set to the new
     * height of the view.
     */
    private val rectF = RectF(0f, 0f, 0f, 0f)

    /**
     * We implement this to do our drawing. We call the [Canvas.clipPath] method of our [canvas]
     * parameter to have it intersect its current clip with our [Path] field [path], then we call
     * our super's implementation of `onDraw` to have it draw our view on [canvas] clipped by the
     * new clip. [path] has had the MaterialComponents CardView [Shape] written to it in our
     * [onSizeChanged] override with the bounds dictated by the view's new width and height.
     *
     * @param canvas the [Canvas] on which the background will be drawn.
     */
    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(path)
        super.onDraw(canvas)
    }

    /**
     * This is called during layout when the size of this view has changed. If you were just added
     * to the view hierarchy, you're called with the old values of 0. We set the [RectF.right]
     * property of our [RectF] field [rectF] to the float value of our [w] parameter, and the
     * [RectF.bottom] property of our [RectF] field [rectF] to the float value of our [h] parameter.
     * Then we call the [ShapeAppearancePathProvider.calculatePath] method of our [pathProvider]
     * field to have it write our [ShapeAppearanceModel] field [shapeAppearance] to our [Path] field
     * [path] using [rectF] as the bounds for the [Path]. Finally we call our super's implementation
     * of `onSizeChanged`.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rectF.right = w.toFloat()
        rectF.bottom = h.toFloat()
        pathProvider.calculatePath(shapeAppearance, 1f, rectF, path)
        super.onSizeChanged(w, h, oldw, oldh)
    }
}
