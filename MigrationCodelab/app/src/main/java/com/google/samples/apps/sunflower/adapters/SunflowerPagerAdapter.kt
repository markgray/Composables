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

package com.google.samples.apps.sunflower.adapters

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.samples.apps.sunflower.GardenFragment
import com.google.samples.apps.sunflower.HomeViewPagerFragment
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.R

/**
 * This is the ViewPager page index for the [GardenFragment] and is used by our override of
 * [FragmentStateAdapter.createFragment] to select that fragment from the `tabFragmentsCreators`
 * [Map] as the return value for the override.
 */
const val MY_GARDEN_PAGE_INDEX: Int = 0

/**
 * This is the ViewPager page index for the [PlantListFragment] and is used by our override of
 * [FragmentStateAdapter.createFragment] to select that fragment from the `tabFragmentsCreators`
 * [Map] as the return value for the override.
 */
const val PLANT_LIST_PAGE_INDEX: Int = 1

/**
 * This is the [RecyclerView.Adapter] used by the [ViewPager2] with resource ID [R.id.view_pager] in
 * the layout file layout/fragment_view_pager.xml (resource ID [R.layout.fragment_view_pager]) which
 * is used as the View for the fragment's UI by [HomeViewPagerFragment].
 */
class SunflowerPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to a lambda that calls the constructor of their
     * respective [Fragment]'s (when the lambda value of an item is [invoke]'d it returns a new
     * instance of the [Fragment]).
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        MY_GARDEN_PAGE_INDEX to { GardenFragment() },
        PLANT_LIST_PAGE_INDEX to { PlantListFragment() }
    )

    /**
     * Returns the total number of items in the data set held by the adapter. We just return the
     * `size` of our [tabFragmentsCreators] field.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = tabFragmentsCreators.size

    /**
     * Provide a new Fragment associated with the specified position. The adapter will be
     * responsible for the Fragment lifecycle:
     *  - The Fragment will be used to display an item.
     *  - The Fragment will be destroyed when it gets too far from the viewport, and its state
     *  will be saved. When the item is close to the viewport again, a new Fragment will be
     *  requested, and a previously saved state will be used to initialize it.
     *
     * We retrieve the lambda in the [Map] of [Int] to lambda with the key [position] and if it is
     * not `null` we [invoke] it and return the [Fragment] instance it returns to the caller. If it
     * is `null` we throw an [IndexOutOfBoundsException].
     *
     * @param position the position in our adapter's dataset of the fragment producing lambda that is
     * required.
     * @return a [Fragment] instance of the type requested by our [position] parameter.
     */
    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
