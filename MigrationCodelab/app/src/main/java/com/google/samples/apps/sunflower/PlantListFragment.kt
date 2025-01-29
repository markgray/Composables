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

package com.google.samples.apps.sunflower

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.sunflower.adapters.PlantAdapter
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantDao
import com.google.samples.apps.sunflower.data.PlantRepository
import com.google.samples.apps.sunflower.databinding.FragmentPlantListBinding
import com.google.samples.apps.sunflower.utilities.InjectorUtils
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel

/**
 * This fragment displays all of the [Plant]'s that are found in the "plants" table in the database.
 */
class PlantListFragment : Fragment() {

    /**
     * This is the [ViewModel] we use to observe and read the [PlantListViewModel.plants] field, a
     * [LiveData] wrapped [List] of [Plant] instances which [PlantListViewModel] reads from the
     * [PlantRepository], and which [PlantRepository] reads from the "plants" table of the ROOM
     * database using [PlantDao].
     */
    private val viewModel: PlantListViewModel by viewModels {
        InjectorUtils.providePlantListViewModelFactory(this)
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onViewCreated]. It is recommended to only inflate the layout in this method
     * and move logic that operates on the returned View to [onViewCreated]. First we initialize
     * our [FragmentPlantListBinding] variable `val binding` by having the method
     * [FragmentPlantListBinding.inflate] use our [LayoutInflater] parameter inflate its associated
     * layout file (the file with resource ID `R.layout.fragment_plant_list`) with our [ViewGroup]
     * parameter [container] supplying LayoutParams without being attached to. If the the [Context]
     * this fragment is currently associated with is `null` we just return the outermost [View]
     * of the layout file associated with `binding` to the caller ([FragmentPlantListBinding.getRoot]
     * aka kotlin `root` property).
     *
     * Otherwise we initialize our [PlantAdapter] variable `val adapter` to a new instance, and
     * set the [RecyclerView.Adapter] of the [RecyclerView] in our layout file that is accessed
     * using the [FragmentPlantListBinding.plantList] property of `binding` to `adapter`, then
     * call our method [subscribeUi] with `adapter` as the argument to have it begin to observe
     * the [PlantListViewModel.plants] field of [viewModel], submitting the [LiveData] wrapped
     * [List] of [Plant] objects to `adapter` to be diffed, and displayed whenever it changes
     * value.
     *
     * We initialize our [MenuHost] variable `val menuHost` to to the `FragmentActivity` this
     * fragment is currently associated with, then call its [MenuHost.addMenuProvider] method to
     * add our [MenuProvider] field [menuProvider] to the [MenuHost]. Finally we return the
     * outermost [View] in the layout file associated with `binding` to the caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment.
     * @param container If non-`null`, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or `null`.
     */
    @Suppress("RedundantNullableReturnType") // The method we override returns nullable.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPlantListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = PlantAdapter()
        binding.plantList.adapter = adapter
        subscribeUi(adapter)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner)
        return binding.root
    }

    /**
     * Our  [MenuProvider]
     */
    private val menuProvider: MenuProvider = object : MenuProvider {
        /**
         * Initialize the contents of the Fragment host's standard options menu. You should place
         * your menu items in the [Menu] parameter [menu]. We just use our [MenuInflater] parameter
         * [menuInflater] to inflate our menu layout file into our [Menu] parameter [menu] (the file
         * with resource ID `R.menu.menu_plant_list`).
         *
         * @param menu The options menu in which you place your items.
         * @param menuInflater a [MenuInflater] you can use to inflate an XML menu file with.
         */
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_plant_list, menu)
        }

        /**
         * This hook is called whenever an item in your options menu is selected. If the value
         * returned by [MenuItem.getItemId] (kotlin `itemId` property) is `R.id.filter_zone` we call
         * our [updateData] method to "toggle" the grow zone filtering that our [PlantListViewModel]
         * performs (if the [PlantListViewModel.isFiltered] method returns `true` it calls the method
         * [PlantListViewModel.clearGrowZoneNumber] to clear, and if it is `false` it call the method
         * [PlantListViewModel.setGrowZoneNumber] to set the grow zone to "9"). If the `itemId` is not
         * `R.id.filter_zone` we return `false`
         *
         * @param menuItem the menu item that was selected
         * @return `true` if the given menu item is handled by this menu provider, `false` otherwise.
         */
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.filter_zone -> {
                    updateData()
                    true
                }

                else -> false
            }
        }
    }

    /**
     * This method is called to add an observer to the [LiveData] wrapped [List] of [Plant] instances
     * field [PlantListViewModel.plants] of [viewModel] which submits the list to be diffed, and
     * displayed by our [PlantAdapter] parameter [adapter] whenever the value changes.
     *
     * @param adapter the [PlantAdapter] for the [RecyclerView] in our UI that displays a [List] of
     * [Plant] instances.
     */
    private fun subscribeUi(adapter: PlantAdapter) {
        viewModel.plants.observe(viewLifecycleOwner) { plants: List<Plant> ->
            adapter.submitList(plants)
        }
    }

    /**
     * This method is called to toggle the grow zone filtering performed by [PlantRepository] on
     * the [LiveData] wrapped [List] of [Plant] instances that it supplies to [PlantListViewModel]
     * for its [PlantListViewModel.plants] field. If [PlantListViewModel.isFiltered] returns `true`
     * we call [PlantListViewModel.clearGrowZoneNumber] to clear the grow zone filtering, and if it
     * returns `false` we call [PlantListViewModel.setGrowZoneNumber] to set the grow zone filter to
     * 9 (when the grow zone is cleared (ie. `NO_GROW_ZONE`) the [PlantListViewModel.plants] field
     * uses the [PlantRepository.getPlants] method to retrieve its [List], otherwise if uses the
     * [PlantRepository.getPlantsWithGrowZoneNumber] method passing it the current grow zone number.
     */
    private fun updateData() {
        with(viewModel) {
            if (isFiltered()) {
                clearGrowZoneNumber()
            } else {
                setGrowZoneNumber(9)
            }
        }
    }
}
