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

import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.samples.apps.sunflower.R

/**
 * This `BindingAdapter` is called when a widget has an app:isGone="@{[Boolean]}" attribute with the
 * [View] of the widget in our [view] parameter and the [Boolean] value in our [isGone] parameter.
 * It is used twice in the layout/fragment_garden.xml (app:isGone="@{!hasPlantings}" for the
 * [RecyclerView] with ID `R.id.garden_list` and app:isGone="@{hasPlantings}" for the [LinearLayout]
 * that is displayed when the garden is empty) and once in layout/fragment_plant_detail.xml
 * (app:isGone="@{viewModel.isPlanted}" for the [FloatingActionButton] with ID `R.id.fab`).
 * When called it sets the `visibility` of [view] to [View.GONE] if [isGone] is `true` or to
 * [View.VISIBLE] it it is `false`.
 *
 * @param view the [View] of the widget that has an app:isGone attribute.
 * @param isGone if `true` we set the `visibility` of [view] to [View.GONE], and if `false` we set
 * it to [View.VISIBLE].
 */
@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}
