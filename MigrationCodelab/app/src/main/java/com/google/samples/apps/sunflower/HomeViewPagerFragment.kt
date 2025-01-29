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

package com.google.samples.apps.sunflower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.samples.apps.sunflower.adapters.MY_GARDEN_PAGE_INDEX
import com.google.samples.apps.sunflower.adapters.PLANT_LIST_PAGE_INDEX
import com.google.samples.apps.sunflower.adapters.SunflowerPagerAdapter
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.databinding.FragmentViewPagerBinding

/**
 * This is the start destination of our navigation graph. It displays all of the [Plant]'s that are
 * currently in our garden (ie. are found in the "garden_plantings" table of our database).
 */
class HomeViewPagerFragment : Fragment() {

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onViewCreated]. It is recommended to only inflate the layout in this method
     * and move logic that operates on the returned [View] to [onViewCreated].
     *
     * We initialize our [FragmentViewPagerBinding] variable `val binding` by having the method
     * [FragmentViewPagerBinding.inflate] use our [LayoutInflater] parameter [inflater] to inflate
     * its associated layout file (the file with resource ID `R.layout.fragment_view_pager`), with
     * our [ViewGroup] parameter [container] supplying the LayoutParams without attaching to it.
     * Then we initialize our [TabLayout] variable to the [FragmentViewPagerBinding.tabs] view in
     * `binding`, and our [ViewPager2] variable `val viewPager` to the [FragmentViewPagerBinding.viewPager]
     * view in `binding`. We set the adapter of `viewPager` to a new instance of [SunflowerPagerAdapter].
     * We construct a [TabLayoutMediator] instance to have it configure the icons and labels of the
     * two [TabLayout.Tab]'s in our [TabLayout] variable `tabLayout` then call its
     * [TabLayoutMediator.attach] to link the [TabLayout] and the [ViewPager2] variable `viewPager`
     * together. Note: The mediator will synchronize the [ViewPager2]'s position with the selected
     * `tab` when a `tab` is selected, and the [TabLayout]'s scroll position when the user drags the
     * [ViewPager2]. [TabLayoutMediator] will listen to [ViewPager2]'s `OnPageChangeCallback` to
     * adjust `tab` when [ViewPager2] moves. [TabLayoutMediator] listens to [TabLayout]'s
     * `OnTabSelectedListener` to adjust [ViewPager2] when `tab` moves. [TabLayoutMediator] listens
     * to [RecyclerView]'s `AdapterDataObserver` to `recreate` tab content when dataset changes.
     *
     * Next we call the [AppCompatActivity.setSupportActionBar] method of our [FragmentActivity]
     * to have it set the [Toolbar] to act as the `ActionBar` for our `Activity` window to the
     * [FragmentViewPagerBinding.toolbar] of `binding`, and finally we return the outermost view in
     * the layout file associated with `binding` to have serve as our fragment's UI.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment.
     * @param container If non-`null`, this is the parent view that the fragment's
     * UI should be attached to. The fragment should not add the view itself,
     * but this can be used to generate the  of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType") // The method we override returns nullable.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val tabLayout: TabLayout = binding.tabs
        val viewPager: ViewPager2 = binding.viewPager

        viewPager.adapter = SunflowerPagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

    /**
     * Returns the resource ID of the drawable that is to be used as the icon for the [TabLayout.Tab]
     * whose position is our [Int] parameter [position]. The `tab` in position [MY_GARDEN_PAGE_INDEX]
     * (0) gets `R.drawable.garden_tab_selector` and the `tab` in position [MY_GARDEN_PAGE_INDEX]
     * (1) gets `R.drawable.plant_list_tab_selector`.
     *
     * @param position the position of the [TabLayout.Tab] in the [TabLayout].
     * @return the resource ID of a drawable to use as the icon for the [TabLayout.Tab] in the
     * [TabLayout] in position [position].
     */
    private fun getTabIcon(position: Int): Int {
        return when (position) {
            MY_GARDEN_PAGE_INDEX -> R.drawable.garden_tab_selector
            PLANT_LIST_PAGE_INDEX -> R.drawable.plant_list_tab_selector
            else -> throw IndexOutOfBoundsException()
        }
    }

    /**
     * Returns the [String] that is to be used as the [TabLayout.Tab.text] (title)  for the
     * [TabLayout.Tab] whose position is our [Int] parameter [position]. The `tab` in position
     * [MY_GARDEN_PAGE_INDEX] (0) gets the [String] whose resource ID is `R.string.my_garden_title`
     * ("My garden") and the `tab` in position [MY_GARDEN_PAGE_INDEX] (1) gets the [String] whose
     * resource ID is  `R.string.plant_list_title` ("Plant list").
     *
     * @param position the position of the [TabLayout.Tab] in the [TabLayout].
     * @return a [String] to use as the title of the [TabLayout.Tab] in the [TabLayout] in position
     * [position].
     */
    private fun getTabTitle(position: Int): String? {
        return when (position) {
            MY_GARDEN_PAGE_INDEX -> getString(R.string.my_garden_title)
            PLANT_LIST_PAGE_INDEX -> getString(R.string.plant_list_title)
            else -> null
        }
    }
}
