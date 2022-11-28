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
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.sunflower.HomeViewPagerFragmentDirections
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.databinding.ListItemPlantBinding
import com.google.samples.apps.sunflower.plantdetail.PlantDetailFragment
import com.google.samples.apps.sunflower.views.MaskedCardView

/**
 * This is the [ListAdapter] that is used for the [RecyclerView] with ID [R.id.plant_list] in the
 * file layout/fragment_plant_list.xml used by in [PlantListFragment].
 */
class PlantAdapter : ListAdapter<Plant, RecyclerView.ViewHolder>(PlantDiffCallback()) {

    /**
     * Called when [RecyclerView] needs a new [PlantViewHolder] of the given type to represent an
     * item. We return a [PlantViewHolder] whose `binding` argument is the [ListItemPlantBinding]
     * that the [ListItemPlantBinding.inflate] method inflates from the layout file with ID
     * [R.layout.list_item_plant] using the [LayoutInflater] that the method [LayoutInflater.from]
     * returns for the [Context] of our [ViewGroup] parameter [parent], using [parent] to provide
     * the `LayoutParams` without attaching to it.
     *
     * @param parent   The [ViewGroup] into which the new [View] will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new [View].
     * @return A new [PlantViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlantViewHolder(
            ListItemPlantBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. We call the
     * [PlantViewHolder.bind] method of our [RecyclerView.ViewHolder] [holder] with the
     * [Plant] instance that the [getItem] method returns for our [Int] parameter [position].
     * It will set the `plant` variable of the [PlantViewHolder.binding] of our [holder] to the
     * [Plant] and call the [ListItemPlantBinding.executeBindings] method of the
     * [PlantViewHolder.binding] to have it evaluate the pending bindings, updating any Views that
     * have expressions bound to the modified variable.
     *
     * @param holder The [RecyclerView.ViewHolder] which should be updated to represent the contents
     * of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val plant: Plant = getItem(position)
        (holder as PlantViewHolder).bind(plant)
    }

    /**
     * This is the [RecyclerView.ViewHolder] that is used to hold item views for the [RecyclerView]
     * with ID [R.id.plant_list] in the file [R.layout.fragment_plant_list]. It is constructed with
     * a [ListItemPlantBinding] binding that is inflated from the file with resource ID
     * [R.layout.list_item_plant] as the [binding] argument.
     *
     * @param binding a [ListItemPlantBinding] that has been inflated from the file with
     * resource ID [R.layout.list_item_plant].
     */
    class PlantViewHolder(
        private val binding: ListItemPlantBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Sets the "clickListener" variable of our `binding` field to a lambda that calls our
         * `navigateToPlant` method with the `plant` variable of `binding`. The `clickListener`
         * variable is called by the android:onClick attribute of the `MaskedCardView` in the
         * layout/list_item_plant.xml file.
         */
        init {
            binding.setClickListener { view: View ->
                binding.plant?.let { plant: Plant ->
                    navigateToPlant(plant = plant, view = view)
                }
            }
        }

        /**
         * This method is called by the lambda that the "clickListener" variable is set to by our
         * init block in our [ListItemPlantBinding] field [binding]. The "clickListener" variable
         * is called by the android:onClick attribute of the [MaskedCardView] in the file that
         * [binding] is inflated from: layout/list_item_plant.xml when the [MaskedCardView] is
         * clicked. The [Plant] parameter [plant] comes from the `plant` variable of the
         * [ListItemPlantBinding] we are constructed with, and the [View] parameter [view] is the
         * [View] of the [MaskedCardView] that was clicked. We initialize our [NavDirections] variable
         * `val direction` to the [NavDirections] that is returned by the method
         * [HomeViewPagerFragmentDirections.actionViewPagerFragmentToPlantDetailFragment] method for
         * the [Plant.plantId] field of [plant]. Then we call the [View.findNavController]
         * method of our [View] parameter [view] to get the view's [NavController] and call its
         * [NavController.navigate] method with the `directions` argument `direction`.
         *
         * @param plant the [Plant] that we want [PlantDetailFragment] to display, the lambda that
         * calls us retrieves it from the [ListItemPlantBinding.getPlant] method of [binding] (aka
         * kotlin property `plant`).@param view the [View] that was clicked, the [MaskedCardView]
         * in the file that [binding] was inflated from: layout/list_item_plant.xml
         */
        private fun navigateToPlant(
            plant: Plant,
            view: View
        ) {
            /**
             * The [NavDirections] required to have the [PlantDetailFragment] display our [Plant]
             * parameter [plant].
             */
            val direction: NavDirections =
                HomeViewPagerFragmentDirections.actionViewPagerFragmentToPlantDetailFragment(
                    plant.plantId
                )
            view.findNavController().navigate(direction)
        }

        /**
         * This method is called from [onBindViewHolder] with the [Plant] instance [item] that our
         * [itemView] should display, and to do that we just call the [ListItemPlantBinding.setPlant]
         * method of [binding] (in kotlin we set its `plant` property) to set the `plant` variable
         * to our [Plant] parameter [item], then call the [ListItemPlantBinding.executeBindings]
         * method of [binding] to have it evaluate the pending bindings, updating any [View]s that
         * have expressions bound to modified variables.
         *
         * @param item the [Plant] we are to display.
         */
        fun bind(item: Plant) {
            binding.apply {
                plant = item
                executePendingBindings()
            }
        }
    }
}

/**
 * This is the [DiffUtil.ItemCallback] that the [ListAdapter] will use for calculating the diff
 * between two non-null items in a list to calculate optimal updates for its [RecyclerView].
 */
private class PlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
    /**
     * Called to check whether two objects represent the same item, for example, if your items have
     * unique ids, this method should check their id equality. The [Plant.plantId] field of a
     * [Plant] is the unique ID of the class so we return `true` if it is structurally equal in our
     * two parameters, `false` if it is not.
     *
     * @param oldItem The [Plant] instance in the old list.
     * @param newItem The [Plant] instance in the new list.
     * @return `true` if the two items represent the same object or `false` if they are different.
     */
    override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return oldItem.plantId == newItem.plantId
    }

    /**
     * Called to check whether two items have the same data. This information is used to detect if
     * the contents of an item have changed. We return `true` if the two parameters are structurally
     * equal, `false` if they are not.
     *
     * @param oldItem The [Plant] instance in the old list.
     * @param newItem The [Plant] instance in the new list.
     * @return `true` if the contents of the items are the same or `false` if they are different.
     */
    override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return oldItem == newItem
    }
}
