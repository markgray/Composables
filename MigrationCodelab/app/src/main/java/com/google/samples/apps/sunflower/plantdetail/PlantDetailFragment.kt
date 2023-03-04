/*
 * Copyright 2020 Google LLC
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

package com.google.samples.apps.sunflower.plantdetail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ShareCompat
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
import com.google.accompanist.themeadapter.material.MdcTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.samples.apps.sunflower.HomeViewPagerFragmentDirections
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.databinding.FragmentPlantDetailBinding
import com.google.samples.apps.sunflower.utilities.InjectorUtils
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel
import com.google.samples.apps.sunflower.adapters.PlantAdapter
import com.google.samples.apps.sunflower.adapters.PlantAdapter.PlantViewHolder
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.PlantRepository
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModelFactory

/**
 * A fragment representing a single Plant detail screen.
 */
class PlantDetailFragment : Fragment() {
    /**
     * This contains the "plantId" [String] that the `navigateToPlant` method of the [PlantViewHolder]
     * of [PlantAdapter] fetches from the [Plant.plantId] field of the [Plant] to be viewed and then
     * uses as the argument to the generated method
     * [HomeViewPagerFragmentDirections.actionViewPagerFragmentToPlantDetailFragment] in order to
     * construct the [NavDirections] that it uses to navigate to this fragment. That [String] is
     * contained in its [PlantDetailFragmentArgs.plantId] property. The [navArgs] method returns a
     * Lazy delegate to access the [Fragment]'s arguments as an [PlantDetailFragmentArgs] instance.
     */
    private val args: PlantDetailFragmentArgs by navArgs()

    /**
     * This is the [PlantDetailViewModel] that we use to retrieve data from the [PlantRepository]
     * and [GardenPlantingRepository] and, if the user clicks the [FloatingActionButton] in our
     * layout file, to add the [Plant] we are displaying to the [GardenPlantingRepository]. The
     * method [InjectorUtils.providePlantDetailViewModelFactory] returns an instance of
     * [PlantDetailViewModelFactory] constructed to use the singleton [PlantRepository] and singleton
     * [GardenPlantingRepository] as well as the [Plant] whose [Plant.plantId] was passed us in our
     * [PlantDetailFragmentArgs] field [args].
     */
    private val plantDetailViewModel: PlantDetailViewModel by viewModels {
        InjectorUtils.providePlantDetailViewModelFactory(
            context = requireActivity(),
            plantId = args.plantId
        )
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onViewCreated]. It is recommended to only inflate the layout in this method
     * and move logic that operates on the returned View to [onViewCreated]. We initialize our
     * [FragmentPlantDetailBinding] variable `val binding` to the value returned by the method
     * [DataBindingUtil.inflate] when it uses our [LayoutInflater] parameter [inflater] to inflate
     * the layout file with resource ID [R.layout.fragment_plant_detail] with our [ViewGroup] parameter
     * [container] supplying the LayoutParams without attaching to it. We then use the [apply]
     * extension function on the [FragmentPlantDetailBinding] to:
     *  - set the `viewModel` variable in the binding to our [PlantDetailViewModel] variable
     *  `plantDetailViewModel`
     *  - set the [LifecycleOwner] that should be used for observing changes of [LiveData] in the
     *  binding to the a [LifecycleOwner] that represents the [Fragment]'s [View] lifecycle
     *  (our `viewLifecycleOwner` property).
     *  - set the `callback` variable in the binding to an anonymous [Callback] instance that
     *  overrides the [Callback.add] method which if its [Plant] parameter `plant` is not `null`
     *  calls our [hideAppBarFab] method with the [FloatingActionButton] whose ID is [R.id.fab]
     *  to hide the [FloatingActionButton], then calls the [PlantDetailViewModel.addPlantToGarden]
     *  method of our [plantDetailViewModel] to have it add the [Plant] we are displaying to the
     *  [GardenPlantingRepository], and finally calls the [Snackbar.make] method to show the
     *  [String] with resource ID [R.string.added_plant_to_garden] ("Added plant to garden").
     *  - initializes our [Boolean] variable `var isToolbarShown` to `false`.
     *  - sets the [NestedScrollView.OnScrollChangeListener] of the `plantDetailScrollview` in the
     *  binding to an anonymous instance whose [NestedScrollView.OnScrollChangeListener.onScrollChange]
     *  override will decide whether the toolbar should be shown based on how far the
     *  [NestedScrollView] has been scrolled. To do this it initializes its [Boolean] variable
     *  `val shouldShowToolbar` to `true` if the current vertical scroll origin (`scrollY`) is
     *  greater than the `height` of the [Toolbar] whose binding address in `binding` is
     *  [FragmentPlantDetailBinding.toolbar]. Then if `isToolbarShown` is not equal to our variable
     *  `shouldShowToolbar` we set `isToolbarShown` to `shouldShowToolbar` and then set the
     *  [AppBarLayout.isActivated] property of the [FragmentPlantDetailBinding.appbar] of `binding`
     *  to `shouldShowToolbar` (this will use shadow animator to add elevation if toolbar is shown),
     *  and set the [CollapsingToolbarLayout.isTitleEnabled] property of `binding`s
     *  [FragmentPlantDetailBinding.toolbarLayout] to `shouldShowToolbar` (shows the plant name if
     *  toolbar is shown).
     *  - we set a lambda as a listener to respond to navigation events on the [Toolbar] in `binding`
     *  at binding address [FragmentPlantDetailBinding.toolbar] which calls the [View.findNavController]
     *  method of the [View] passed the lambda to locate the [NavController] associated with the [View]
     *  and then calls its [NavController.navigateUp] method to navigate up in the navigation hierarchy.
     *  - we set the [Toolbar.OnMenuItemClickListener] of [FragmentPlantDetailBinding.toolbar] in
     *  `binding` to a lambda which when the [MenuItem.getItemId] (kotlin `itemId` property) has
     *  resource ID [R.id.action_share] calls [createShareIntent] to create a share [Intent] and
     *  launch an activity to share the [Plant.name] of our [Plant] then returns `true` to consume
     *  the click. If it is not resource ID [R.id.action_share] we return `false` to allow normal
     *  system handling of the [MenuItem].
     *  - Next we configure the [ComposeView] in our layout file that is bound to the resource ID
     *  [FragmentPlantDetailBinding.composeView] by using the [apply] extension function on it and
     *  in the [apply] `block` we call its [ComposeView.setViewCompositionStrategy] method to set
     *  its [ViewCompositionStrategy] to [ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed]
     *  in order to dispose the Composition when the Fragment view lifecycle is destroyed, instead
     *  of when the [ComposeView] is detached from the window. We then call [ComposeView.setContent]
     *  to set the Jetpack Compose content of this [View] to an [MdcTheme] wrapped call to
     *  the [PlantDetailDescription] screen, with [plantDetailViewModel] as its `plantDetailViewModel`
     *  argument.
     *
     * Finally we return the [View] returned by the  [FragmentPlantDetailBinding.getRoot] method of
     * `binding` (kotlin `root` property) to the caller as the [View] for our fragment's UI (`root`
     * is the outermost [View] in the layout file associated with the Binding).
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate any views.
     * @param container If non-`null`, this is the parent view that the fragment's UI will be
     * attached to. The fragment should not add the view itself, but this can be used to generate
     * the LayoutParams of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentPlantDetailBinding>(
            inflater, R.layout.fragment_plant_detail, container, false
        ).apply {
            viewModel = plantDetailViewModel
            lifecycleOwner = viewLifecycleOwner
            callback = object : Callback {
                override fun add(plant: Plant?) {
                    plant?.let {
                        hideAppBarFab(fab = fab)
                        plantDetailViewModel.addPlantToGarden()
                        Snackbar.make(root, R.string.added_plant_to_garden, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }

            var isToolbarShown = false

            // scroll change listener begins at Y = 0 when image is fully collapsed
            plantDetailScrollview.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->

                    // User scrolled past image to height of toolbar and the title text is
                    // underneath the toolbar, so the toolbar should be shown.
                    val shouldShowToolbar: Boolean = scrollY > toolbar.height

                    // The new state of the toolbar differs from the previous state; update
                    // appbar and toolbar attributes.
                    if (isToolbarShown != shouldShowToolbar) {
                        isToolbarShown = shouldShowToolbar

                        // Use shadow animator to add elevation if toolbar is shown
                        appbar.isActivated = shouldShowToolbar

                        // Show the plant name if toolbar is shown
                        toolbarLayout.isTitleEnabled = shouldShowToolbar
                    }
                }
            )

            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            toolbar.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_share -> {
                        createShareIntent()
                        true
                    }
                    else -> false
                }
            }

            composeView.apply {
                // By default, the Composition is disposed when ComposeView is detached
                // from the window. This causes problems during transitions as the ComposeView
                // will still be visible on the screen after it's detached from the window.
                // Instead, to dispose the Composition when the Fragment view lifecycle is
                // destroyed, we set the DisposeOnViewTreeLifecycleDestroyed strategy as the
                // ViewCompositionStrategy for this ComposeView
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )

                // Add Jetpack Compose content to this View
                setContent {
                    MdcTheme {
                        PlantDetailDescription(plantDetailViewModel = plantDetailViewModel)
                    }
                }
            }
        }
        return binding.root
    }

    /**
     * Helper function for calling a share functionality. Should be used when user presses a share
     * button/menu item. We initialize our [String] variable `val shareText` by retrieving the
     * value of the [PlantDetailViewModel.plant] property of our [plantDetailViewModel] field, then
     * use the [let] extension function to apply an `if` expression to the `plant` which returns
     * the empty string if `plant` is `null` or the [String] that the [getString] method returns
     * when it formats the [Plant.name] property of the `plant` using the format string with
     * resource ID [R.string.share_text_plant] ("Check out the %s plant in the Android Sunflower app").
     * Then we initialize our [Intent] variable `val shareIntent` by constructing an instance of
     * [ShareCompat.IntentBuilder], and then applying the [ShareCompat.IntentBuilder.setText] method
     * to it to set the literal text data to be sent as part of the share to `shareText`, and then
     * chaining a [ShareCompat.IntentBuilder.setType] method to that to set mime type to "text/plain",
     * and finally chaining a [ShareCompat.IntentBuilder.createChooserIntent] to the end of the
     * builder to create an [Intent] to which we apply the [Intent.addFlags] method to it to set its
     * flags to [Intent.FLAG_ACTIVITY_NEW_DOCUMENT] (open a document into a new task rooted at the
     * activity launched by this [Intent]) bitwise or'ed with [Intent.FLAG_ACTIVITY_MULTIPLE_TASK]
     * (create a new task and launch an activity into it). Finally the [createShareIntent] method
     * calls the [startActivity] method with `shareIntent` as its argument to have the user choose
     * how to share the content of the [Intent].
     */
    private fun createShareIntent() {
        val shareText: String = plantDetailViewModel.plant.value.let { plant: Plant? ->
            if (plant == null) {
                ""
            } else {
                getString(R.string.share_text_plant, plant.name)
            }
        }
        val shareIntent: Intent = ShareCompat.IntentBuilder(requireActivity())
            .setText(shareText)
            .setType("text/plain")
            .createChooserIntent()
            .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        startActivity(shareIntent)
    }

    /**
     * FloatingActionButtons anchored to AppBarLayouts have their visibility controlled by the scroll
     * position. We want to turn this behavior off to hide the FAB when it is clicked.
     *
     * This is adapted from Chris Banes' Stack Overflow answer: https://stackoverflow.com/a/41442923
     *
     * First we initialize our [CoordinatorLayout.LayoutParams] variable `val params` to the layout
     * params of our [FloatingActionButton] parameter [fab]. Then we initialize our
     * [FloatingActionButton.Behavior] variable `val behavior` to the value returned by the
     * [CoordinatorLayout.LayoutParams.getBehavior] method of `params` (kotlin `behavior` property),
     * and then we set the [FloatingActionButton.Behavior.isAutoHideEnabled] property of `behavior`
     * to `false` and call the [FloatingActionButton.hide] method of [fab] to animate the button
     * hide if the view has already been laid out.
     *
     * @param fab the [FloatingActionButton] whose scroll position visibility control we should
     * disable.
     */
    private fun hideAppBarFab(fab: FloatingActionButton) {
        val params = fab.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as FloatingActionButton.Behavior
        behavior.isAutoHideEnabled = false
        fab.hide()
    }

    /**
     * Interface used for the "callback" binding variable in layout/fragment_plant_detail.xml, its
     * [add] method is called with the [Plant] that the [PlantDetailFragment] is displaying when the
     * [FloatingActionButton] is clicked.
     */
    interface Callback {
        /**
         * Method called when the user clicks the [FloatingActionButton]. The `onCreateView` overrride
         * of [PlantDetailFragment] creates an anonymous instance of [Callback] which it sets the
         * "callback" binding variable in layout/fragment_plant_detail.xml to. In that instance the
         * [add] method implementation checks to make sure [plant] is not `null`, and if not it calls
         * [hideAppBarFab] with the [FloatingActionButton] bound as [FragmentPlantDetailBinding.fab]
         * to hide the [FloatingActionButton] and disable its auto hide property. Then we call the
         * [PlantDetailViewModel.addPlantToGarden] method of our [plantDetailViewModel] field to add
         * the [Plant] we are displaying to the [GardenPlantingRepository] (our "garden"). Finally it
         * shows a [Snackbar] displaying the message "Added plant to garden".
         *
         * @param plant the [Plant] that we are displaying (our [PlantDetailViewModel] already knows
         * which [Plant] so in the implementation added in `onCreateView` we only check to make sure
         * it is not `null`).
         */
        fun add(plant: Plant?)
    }
}
