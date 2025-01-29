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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.sunflower.GardenFragment
import com.google.samples.apps.sunflower.HomeViewPagerFragmentDirections
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import com.google.samples.apps.sunflower.databinding.ListItemGardenPlantingBinding
import com.google.samples.apps.sunflower.viewmodels.PlantAndGardenPlantingsViewModel
import com.google.samples.apps.sunflower.views.MaskedCardView

/**
 * This is the [ListAdapter] that is used for the [RecyclerView] with ID `R.id.garden_list` in the
 * file layout/fragment_garden.xml used by [GardenFragment].
 */
class GardenPlantingAdapter :
    ListAdapter<PlantAndGardenPlantings, GardenPlantingAdapter.ViewHolder>(
        GardenPlantDiffCallback()
    ) {

    /**
     * Called when [RecyclerView] needs a new [ViewHolder] of the given type to represent an item.
     * We return a [ViewHolder] whose `binding` argument is the [ListItemGardenPlantingBinding] that
     * the [DataBindingUtil.inflate] method inflates from the layout file with ID
     * `R.layout.list_item_garden_planting` using the [LayoutInflater] that the method
     * [LayoutInflater.from] returns for the [Context] of our [ViewGroup] parameter [parent], using
     * [parent] to provide the `LayoutParams` without attaching to it.
     *
     * @param parent   The [ViewGroup] into which the new [View] will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new [View].
     * @return A new [ViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_garden_planting,
                parent,
                false
            )
        )
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. We call the
     * [ViewHolder.bind] method of our [ViewHolder] parameter [holder] with the [PlantAndGardenPlantings]
     * instance that the [getItem] method returns for our [Int] parameter [position]. It will set
     * the `viewModel` variable of the [ViewHolder.binding] of our [holder] to a new instance of
     * [PlantAndGardenPlantingsViewModel] and call the [ListItemGardenPlantingBinding.executePendingBindings]
     * of the [ViewHolder.binding] to have it evaluate the pending bindings, updating any Views that
     * have expressions bound to the modified variable.
     *
     * @param holder The [ViewHolder] which should be updated to represent the contents of the item
     * at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(plantings = getItem(position))
    }

    /**
     * This is the [RecyclerView.ViewHolder] that is used to hold item views for the [RecyclerView]
     * with ID `R.id.garden_list` in the file `R.layout.fragment_garden`. It is constructed with a
     * [ListItemGardenPlantingBinding] binding that is inflated from the file with resource ID
     * `R.layout.list_item_garden_planting` as the [binding] argument.
     *
     * @param binding a [ListItemGardenPlantingBinding] that has been inflated from the file with
     * resource ID `R.layout.list_item_garden_planting`.
     */
    class ViewHolder(
        private val binding: ListItemGardenPlantingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Sets the "clickListener" variable of our `binding` field to a lambda that calls our
         * `navigateToPlant` method with the `plantId` field of the `PlantAndGardenPlantingsViewModel`
         * bound to the `viewModel` variable of `binding`. The `clickListener` variable is called by
         * the android:onClick attribute of the `MaskedCardView` in the layout/list_item_garden_planting.xml
         * file.
         */
        init {
            binding.setClickListener { view: View ->
                binding.viewModel?.plantId?.let { plantId: String ->
                    navigateToPlant(plantId = plantId, view = view)
                }
            }
        }

        /**
         * This is called by the lambda of the `clickListener` variable (that is initialized by our
         * init block) in the file layout/list_item_garden_planting.xml with the [String] parameter
         * [plantId] coming from the [PlantAndGardenPlantingsViewModel.plantId] of the `viewModel`
         * variable of our [binding] field, and the [View] of the [MaskedCardView] that was clicked.
         * We initialize our [NavDirections] variable `val direction` to the [NavDirections] that is
         * returned by the [HomeViewPagerFragmentDirections.actionViewPagerFragmentToPlantDetailFragment]
         * method for the `plantId` argument [plantId]. Then we call the [View.findNavController]
         * method of our [View] parameter [view] to get the view's [NavController] and call its
         * [NavController.navigate] method with the `directions` argument `direction`.
         *
         * @param plantId the [PlantAndGardenPlantingsViewModel.plantId] field of the `viewModel`
         * variable of [binding]. This is the [Plant.plantId] field of the [PlantAndGardenPlantings.plant]
         * field of the [PlantAndGardenPlantings] we were bound to by our [bind] method.
         * @param view the [View] that was clicked, the [MaskedCardView] in the file that [binding]
         * was inflated from: layout/list_item_garden_planting.xml
         */
        private fun navigateToPlant(plantId: String, view: View) {
            val direction: NavDirections = HomeViewPagerFragmentDirections
                .actionViewPagerFragmentToPlantDetailFragment(plantId = plantId)
            view.findNavController().navigate(directions = direction)
        }

        /**
         * This method is called from [onBindViewHolder] with the [PlantAndGardenPlantings] instance
         * that our [itemView] should display, and to do that we just call the method
         * [ListItemGardenPlantingBinding.setViewModel] of [binding] (in kotlin we set its `viewModel`
         * property) to a new instance of [PlantAndGardenPlantingsViewModel] constructed to use our
         * [PlantAndGardenPlantings] parameter [plantings] to populate its fields. Then we call the
         * [ListItemGardenPlantingBinding.executePendingBindings] method of [binding] to have it
         * evaluate the pending bindings, updating any [View]s that have expressions bound to
         * modified variables.
         *
         * @param plantings the [PlantAndGardenPlantings] instance of the plant that we are to
         * display.
         */
        fun bind(plantings: PlantAndGardenPlantings) {
            with(binding) {
                viewModel = PlantAndGardenPlantingsViewModel(plantings = plantings)
                executePendingBindings()
            }
        }
    }
}

/**
 * This is the [DiffUtil.ItemCallback] that the [ListAdapter] will use for calculating the diff
 * between two non-null items in a list to calculate optimal updates for its [RecyclerView].
 */
private class GardenPlantDiffCallback : DiffUtil.ItemCallback<PlantAndGardenPlantings>() {

    /**
     * Called to check whether two objects represent the same item, for example, if your items have
     * unique ids, this method should check their id equality. The [Plant.plantId] field of the
     * [PlantAndGardenPlantings.plant] field is the unique ID of the [PlantAndGardenPlantings] class,
     * so we return `true` if it is structurally equal in our two parameters, `false` if it is not.
     *
     * @param oldItem The [PlantAndGardenPlantings] instance in the old list.
     * @param newItem The [PlantAndGardenPlantings] instance in the new list.
     * @return `true` if the two items represent the same object or `false` if they are different.
     */
    override fun areItemsTheSame(
        oldItem: PlantAndGardenPlantings,
        newItem: PlantAndGardenPlantings
    ): Boolean {
        return oldItem.plant.plantId == newItem.plant.plantId
    }

    /**
     * Called to check whether two items have the same data. This information is used to detect if
     * the contents of an item have changed. We return `true` if the [Plant] field
     * [PlantAndGardenPlantings.plant] of our two [PlantAndGardenPlantings] parameters are
     * structurally equal, `false` if they are not.
     *
     * @param oldItem The [PlantAndGardenPlantings] instance in the old list.
     * @param newItem The [PlantAndGardenPlantings] instance in the new list.
     * @return `true` if the contents of the items are the same or `false` if they are different.
     */
    override fun areContentsTheSame(
        oldItem: PlantAndGardenPlantings,
        newItem: PlantAndGardenPlantings
    ): Boolean {
        return oldItem.plant == newItem.plant
    }
}
