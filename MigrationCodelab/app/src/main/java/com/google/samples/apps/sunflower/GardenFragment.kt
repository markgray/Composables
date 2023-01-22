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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.samples.apps.sunflower.adapters.GardenPlantingAdapter
import com.google.samples.apps.sunflower.adapters.PLANT_LIST_PAGE_INDEX
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import com.google.samples.apps.sunflower.databinding.FragmentGardenBinding
import com.google.samples.apps.sunflower.utilities.InjectorUtils
import com.google.samples.apps.sunflower.viewmodels.GardenPlantingListViewModel

/**
 * This is the [Fragment] which displays the plants that have been chosen for the garden.
 */
class GardenFragment : Fragment() {

    /**
     * The [FragmentGardenBinding] view binding that is inflated from the associated layout file with
     * resource ID [R.layout.fragment_garden] in our [onCreateView] override.
     */
    private lateinit var binding: FragmentGardenBinding

    /**
     * The [GardenPlantingListViewModel] that we use to access the [LiveData] wrapped [List] of
     * [PlantAndGardenPlantings] that the [GardenPlantingRepository.getPlantedGardens] method returns.
     * Our [subscribeUi] method adds an observer to [GardenPlantingListViewModel.plantAndGardenPlantings]
     * which updates the [FragmentGardenBinding.setHasPlantings] variable in our view binding, and
     * submits the [List] to the [GardenPlantingAdapter] that is feeding [PlantAndGardenPlantings]
     * objects to the [RecyclerView] in our UI.
     */
    private val viewModel: GardenPlantingListViewModel by viewModels {
        InjectorUtils.provideGardenPlantingListViewModelFactory(requireContext())
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onViewCreated]. It is recommended to only inflate the layout in this method
     * and move logic that operates on the returned [View] to [onViewCreated].
     *
     * We start by initializing our [FragmentGardenBinding] field [binding] by having the
     * [FragmentGardenBinding.inflate] method use our [LayoutInflater] parameter [inflater] to
     * inflate its associated layout file (resource ID [R.layout.fragment_garden]) using our
     * [ViewGroup] parameter [container] for its LayoutParams without attaching to the [ViewGroup].
     * We initialize our [GardenPlantingAdapter] variable `val adapter` to a new instance and set
     * the adapter of the [RecyclerView] with resource ID [R.id.garden_list] to it. Then we set the
     * [View.OnClickListener] of the [MaterialButton] with resource ID [R.id.add_plant] to a lambda
     * that calls our [navigateToPlantListPage] method. We call our [subscribeUi] method to have
     * it add an observer to the [GardenPlantingListViewModel.plantAndGardenPlantings] property
     * which will update the `hasPlantings` variable of [binding] and submit the [List] of
     * [PlantAndGardenPlantings] to the [GardenPlantingAdapter] whenever it changes value. Finally
     * we return the outer most [View] of our layout file ([FragmentGardenBinding.getRoot] aka
     * kotlin `root`) to be the [View] for our fragment's UI.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate any views in the
     * fragment,
     * @param container If non-`null`, this is the parent view that the fragment's UI will be
     * attached to. The fragment should not add the view itself, but this can be used to generate
     * the LayoutParams of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or `null`.
     */
    @Suppress("RedundantNullableReturnType") // The method we override returns nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGardenBinding.inflate(inflater, container, false)
        val adapter = GardenPlantingAdapter()
        binding.gardenList.adapter = adapter

        binding.addPlant.setOnClickListener {
            navigateToPlantListPage()
        }

        subscribeUi(adapter, binding)
        return binding.root
    }

    /**
     * This method adds an observer to the [GardenPlantingListViewModel.plantAndGardenPlantings]
     * [LiveData] wrapped [List] of [PlantAndGardenPlantings] object. The observer lambda sets the
     * "hasPlantings" variable of our [FragmentGardenBinding] parameter [binding] to `true` if the
     * [List] is not empty, and submits the [List] to our [GardenPlantingAdapter] parameter [adapter]
     * to be diffed, and displayed in the [RecyclerView] that the adapter is feeding.
     *
     * @param adapter the [GardenPlantingAdapter] that is feeding data to the [RecyclerView] in our UI.
     * @param binding the [FragmentGardenBinding] that is inflated from our layout file.
     */
    private fun subscribeUi(adapter: GardenPlantingAdapter, binding: FragmentGardenBinding) {
        viewModel.plantAndGardenPlantings.observe(viewLifecycleOwner) { result: List<PlantAndGardenPlantings> ->
            binding.hasPlantings = result.isNotEmpty()
            adapter.submitList(result)
        }
    }

    /**
     * Calls the [ViewPager2.setCurrentItem] method (kotlin `currentItem` property) of the view with
     * ID [R.id.view_pager] to set its item to [PLANT_LIST_PAGE_INDEX] which is the [ViewPager2] page
     * index for the [PlantListFragment] which displays all the [Plant] objects available for planting.
     */
    private fun navigateToPlantListPage() {
        requireActivity().findViewById<ViewPager2>(R.id.view_pager).currentItem =
            PLANT_LIST_PAGE_INDEX
    }
}
