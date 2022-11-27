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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.sunflower.HomeViewPagerFragmentDirections
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.databinding.ListItemPlantBinding

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
         * TODO: Add kdoc
         */
        private fun navigateToPlant(
            plant: Plant,
            view: View
        ) {
            val direction =
                HomeViewPagerFragmentDirections.actionViewPagerFragmentToPlantDetailFragment(
                    plant.plantId
                )
            view.findNavController().navigate(direction)
        }

        /**
         * TODO: Add kdoc
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
 * TODO: Add kdoc
 */
private class PlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
    /**
     * TODO: Add kdoc
     */
    override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return oldItem.plantId == newItem.plantId
    }

    /**
     * TODO: Add kdoc
     */
    override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return oldItem == newItem
    }
}
