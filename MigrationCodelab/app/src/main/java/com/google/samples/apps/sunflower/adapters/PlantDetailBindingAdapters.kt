/*
 * Copyright 2018 Google LLC
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

package com.google.samples.apps.sunflower.adapters

import android.text.method.LinkMovementMethod
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.samples.apps.sunflower.R

/**
 * This method is called when an [ImageView] has an app:imageFromUrl attribute with the parameter
 * [view] the [ImageView] and the parameter [imageUrl] the value of the binding expression it is
 * set to. It is used by the [ImageView] with resource ID [R.id.detail_image] in the layout file
 * with resource ID [R.layout.fragment_plant_detail] to load the URL in "@{viewModel.plant.imageUrl}",
 * by the [ImageView] with resource ID [R.id.imageView] in the layout file with resource ID
 * [R.layout.list_item_garden_planting] to load the URL in "@{viewModel.imageUrl}", and by the
 * [ImageView] with resource ID [R.id.plant_item_image] in the layout file with resource ID
 * [R.layout.list_item_plant] to load the URL in "@{plant.imageUrl}". First we check to make sure
 * that our [String] parameter [imageUrl] is not `null` or empty and return having done nothing if
 * it is. Otherwise we use the [Glide.with] to begin a load with Glide by passing in context of our
 * [view] parameter then we call the [RequestManager.load] method to create a request builder to
 * load our [String] parameter [imageUrl], then chain a [RequestBuilder.transition] call to the
 * [RequestBuilder] returned with its `transitionOptions` argument [DrawableTransitionOptions.withCrossFade],
 * and then chain a [RequestBuilder.into] on the [RequestBuilder] returned to set the [ImageView]
 * that the image will be loaded into (cancelling any existing loads into the view, and freeing any
 * resources Glide may have previously loaded into the view so they may be reused).
 *
 * @param view the [ImageView] that has an app:imageFromUrl attribute.
 * @param imageUrl the [String] assigned to the app:imageFromUrl attribute, an URL for an image.
 */
@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

/**
 * This `BindingAdapter` is called when a [FloatingActionButton] widget has an app:isGone="@{[Boolean]}"
 * attribute with the [FloatingActionButton] of the widget in our [view] parameter and the [Boolean]
 * value in our [isGone] parameter. It is used by the [FloatingActionButton] with ID [R.id.fab] in
 * the layout file [R.layout.fragment_plant_detail] with the [Boolean] value of the binding expression
 * assigned to the attribute "@{viewModel.isPlanted}". If our [Boolean] parameter [isGone] is `null`
 * or `true` we call the [FloatingActionButton.hide] method of [view] to hide the button, otherwise
 * we call the [FloatingActionButton.show] method to show the button.
 *
 * @param view the [FloatingActionButton] that has the app:isGone attribute.
 * @param isGone the value of the binding expression assigned to the attribute.
 */
@BindingAdapter("isGone")
fun bindIsGone(view: FloatingActionButton, isGone: Boolean?) {
    if (isGone == null || isGone) {
        view.hide()
    } else {
        view.show()
    }
}

/**
 * This `BindingAdapter` would be called if a [TextView] had the attribute app:renderHtml but none
 * of the [TextView]'s in this app use it.
 *
 * @param view the [TextView] with the attribute app:renderHtml
 * @param description the [String] value of the binding expression assigned to app:renderHtml
 */
@Suppress("unused") // Unused but instructional.
@BindingAdapter("renderHtml")
fun bindRenderHtml(view: TextView, description: String?) {
    if (description != null) {
        view.text = HtmlCompat.fromHtml(description, FROM_HTML_MODE_COMPACT)
        view.movementMethod = LinkMovementMethod.getInstance()
    } else {
        view.text = ""
    }
}

/**
 * TODO: Add kdoc
 */
@Suppress("unused")
@BindingAdapter("wateringText")
fun bindWateringText(textView: TextView, wateringInterval: Int) {
    val resources = textView.context.resources
    val quantityString = resources.getQuantityString(
        R.plurals.watering_needs_suffix,
        wateringInterval, wateringInterval
    )

    textView.text = quantityString
}
