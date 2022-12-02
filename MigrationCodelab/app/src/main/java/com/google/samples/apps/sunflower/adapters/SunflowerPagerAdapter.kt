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
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        MY_GARDEN_PAGE_INDEX to { GardenFragment() },
        PLANT_LIST_PAGE_INDEX to { PlantListFragment() }
    )

    /**
     * TODO: Add kdoc
     */
    override fun getItemCount(): Int = tabFragmentsCreators.size

    /**
     * TODO: Add kdoc
     */
    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
