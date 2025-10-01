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

package com.google.samples.apps.sunflower.compose.plantdetail

import android.graphics.drawable.Drawable
import android.text.method.LinkMovementMethod
import android.text.Spanned
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.core.text.HtmlCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.compose.Dimens
import com.google.samples.apps.sunflower.compose.utils.TextSnackbarContainer
import com.google.samples.apps.sunflower.compose.visible
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.databinding.ItemPlantDescriptionBinding
import com.google.samples.apps.sunflower.ui.SunflowerTheme
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel

/**
 * This class encapsulates all the necessary callbacks for the Plant Details screen.
 * It's used to avoid passing multiple individual lambda functions through several Composables,
 * which can lead to parameter naming conflicts and reduce code readability.
 *
 * @property onFabClick Callback invoked when the Floating Action Button (FAB) is clicked.
 * @property onBackClick Callback invoked when the back button is clicked.
 * @property onShareClick Callback invoked when the share button is clicked, called with the plant
 * name as a [String].
 * @property onGalleryClick Callback invoked when the gallery icon is clicked, called with the
 * [Plant] object.
 */
data class PlantDetailsCallbacks(
    val onFabClick: () -> Unit,
    val onBackClick: () -> Unit,
    val onShareClick: (String) -> Unit,
    val onGalleryClick: (Plant) -> Unit
)

/**
 * Composable function that represents the entire Plant Details screen.
 * This screen displays detailed information about a specific plant and allows users to perform
 * actions like adding the plant to their garden, sharing it, or viewing it in the gallery.
 *
 * We start by initializing our [State] wrapped [Plant] variable `plant` to the value returned by
 * the [LiveData.observeAsState] function of the [PlantDetailViewModel.plant] property of our
 * [PlantDetailViewModel] parameter [plantDetailsViewModel]. We initialize our [State] wrapped
 * [Boolean] variable `isPlanted` to the value returned by the [collectAsStateWithLifecycle] method
 * of the [PlantDetailViewModel.isPlanted] property of our [PlantDetailViewModel] parameter
 * [plantDetailsViewModel]. We initialize our [State] wrapped [Boolean] variable `showSnackbar`
 * to the value returned by the [LiveData.observeAsState] function of the
 * [PlantDetailViewModel.showSnackbar] property of our [PlantDetailViewModel] parameter
 * [plantDetailsViewModel].
 *
 * Then if both `plant` and `showSnackbar` are not null, we compose a [Surface] that holds a
 * [TextSnackbarContainer] whose `snackbarText` argument is the string resource with ID
 * `R.string.added_plant_to_garden` ("Added plant to garden"), whose `showSnackbar` argument is our
 * [Boolean] variable `showSnackbar`, and whose `onDismissSnackbar` argument is a lambda function
 * that calls the [PlantDetailViewModel.dismissSnackbar] method of our [PlantDetailViewModel]
 * parameter [plantDetailsViewModel]. In the `content` comoposable lambda argument of the
 * [TextSnackbarContainer] we compose a [PlantDetails] composable whose arguments are:
 *  - `plant`: is our [Plant] variable `plant`
 *  - `isPlanted`: is our [Boolean] variable `isPlanted`
 *  - `hasValidUnsplashKey`: is the value returned by the [PlantDetailViewModel.hasValidUnsplashKey]
 *  property of our [PlantDetailViewModel] parameter [plantDetailsViewModel].
 *  - `callbacks`: is a [PlantDetailsCallbacks] instance whose [PlantDetailsCallbacks.onBackClick]
 *  is our lambda parameter [onBackClick], whose [PlantDetailsCallbacks.onFabClick] is a lambda
 *  that calls the [PlantDetailViewModel.addPlantToGarden] method of our [PlantDetailViewModel]
 *  parameter [plantDetailsViewModel], whose [PlantDetailsCallbacks.onShareClick] is our lambda
 *  parameter [onShareClick], and whose [PlantDetailsCallbacks.onGalleryClick] is our lambda
 *  parameter [onGalleryClick].
 *
 * @param plantDetailsViewModel The ViewModel responsible for providing data and handling business
 * logic for this screen. It is injected using Hilt.
 * @param onBackClick A lambda function to be invoked when the user clicks the back button.
 * @param onShareClick A lambda function to be invoked when the user clicks the share button.
 * It is provided with the plant name as a [String].
 * @param onGalleryClick A lambda function to be invoked when the user clicks the gallery icon.
 * It is provided with the [Plant] object.
 */
@Composable
fun PlantDetailsScreen(
    plantDetailsViewModel: PlantDetailViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(
            value = LocalViewModelStoreOwner.current
        ) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        }, key = null
    ),
    onBackClick: () -> Unit,
    onShareClick: (String) -> Unit,
    onGalleryClick: (Plant) -> Unit,
) {
    val plant: Plant? = plantDetailsViewModel.plant.observeAsState().value
    val isPlanted: Boolean = plantDetailsViewModel.isPlanted.collectAsStateWithLifecycle().value
    val showSnackbar: Boolean? = plantDetailsViewModel.showSnackbar.observeAsState().value

    if (plant != null && showSnackbar != null) {
        Surface {
            TextSnackbarContainer(
                snackbarText = stringResource(id = R.string.added_plant_to_garden),
                showSnackbar = showSnackbar,
                onDismissSnackbar = { plantDetailsViewModel.dismissSnackbar() }
            ) {
                PlantDetails(
                    plant = plant,
                    isPlanted = isPlanted,
                    hasValidUnsplashKey = plantDetailsViewModel.hasValidUnsplashKey(),
                    callbacks = PlantDetailsCallbacks(
                        onBackClick = onBackClick,
                        onFabClick = {
                            plantDetailsViewModel.addPlantToGarden()
                        },
                        onShareClick = onShareClick,
                        onGalleryClick = onGalleryClick,
                    )
                )
            }
        }
    }
}

/**
 * The main composable for the plant details screen. It orchestrates the display of
 * plant information, a collapsing toolbar, and action buttons.
 *
 * This composable uses a [Box] to layer its children: [PlantDetailsContent] and [PlantToolbar].
 * It manages the scroll state and transitions to create a collapsing toolbar effect, where the
 * plant image and initial header fade out as the user scrolls, and a persistent toolbar fades in.
 *
 * We start by initializing and remembering our [ScrollState] variable `scrollState` to the value
 * returned by the [rememberScrollState] function. We initialize and remember our [MutableState]
 * wrapped [PlantDetailsScroller] variable `plantScroller` to an instance of [PlantDetailsScroller]
 * whose `scrollState` argument is our [ScrollState] variable `scrollState` and whose `namePosition`
 * argument is set to [Float.MIN_VALUE]. We initialize and remember our [MutableTransitionState] of
 * [ToolbarState] variable `transitionState` to the value returned by the [remember] function for the
 * `key1` argument of our [PlantDetailsScroller] variable `plantScroller` with an initial value of
 * the current value of the [PlantDetailsScroller.toolbarTransitionState] property of our
 * [PlantDetailsScroller] variable `plantScroller`. We initialize our [ToolbarState] variable
 * `toolbarState` to the value returned by the [PlantDetailsScroller.getToolbarState] method of our
 * [PlantDetailsScroller] variable `plantScroller` with the current [LocalDensity] as its
 * `density` argument. We initialize and remember our [Transition] of [ToolbarState] variable
 * `transition` to the value returned by the [rememberTransition] function for the `transitionState`
 * argument our [MutableTransitionState] of [ToolbarState] variable `transitionState` and the
 * `label` argument an empty string.
 *
 * We initialize our animated [State] of [Float] variable `toolbarAlpha` to the value returned by
 * the [Transition.animateFloat] function of our [Transition] of [ToolbarState] variable `transition`
 * with its `transitionSpec` argument set to the [spring] function with its `stiffness` argument
 * set to [Spring.StiffnessLow] and the `label` argument set to an empty string. In the
 * `targetValueByState` lambda argument of the [Transition.animateFloat] function we accept the
 * [ToolbarState] passed the lambda in variable `toolbarTransitionState` and return the value
 * `0f` if the [ToolbarState] is [ToolbarState.HIDDEN] and `1f` otherwise.
 *
 * We initialize our animated [State] of [Float] variable `contentAlpha` to the value returned by
 * the [Transition.animateFloat] function of our [Transition] of [ToolbarState] variable `transition`
 * with its `transitionSpec` argument set to the [spring] function with its `stiffness` argument
 * set to [Spring.StiffnessLow] and the `label` argument set to an empty string. In the
 * `targetValueByState` lambda argument of the [Transition.animateFloat] function we accept the
 * [ToolbarState] passed the lambda in variable `toolbarTransitionState` and return the value
 * `1f` if the [ToolbarState] is [ToolbarState.HIDDEN] and `0f` otherwise.
 *
 * Our root composable is a [Box] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.fillMaxSize]. In the [BoxScope] `content` composable lambda argument of
 * the [Box] we compose:
 *
 * **First** a [PlantDetailsContent] whose arguments are:
 *  - `scrollState`: is our [ScrollState] variable `scrollState`
 *  - `toolbarState`: is our [ToolbarState] variable `toolbarState`
 *  - `onNamePosition`: is a lambda function that accepts the [Float] passed the lambda in variable
 *  `newNamePosition` and if the [PlantDetailsScroller.namePosition] property of [PlantDetailsScroller]
 *  variable `plantScroller` is [Float.MIN_VALUE] it sets `plantScroller` to a copy of itself with
 *  its [PlantDetailsScroller.namePosition] property set to `newNamePosition`.
 *  - `plant`: is our [Plant] parameter [plant].
 *  - `isPlanted`: is our [Boolean] parameter [isPlanted].
 *  - `hasValidUnsplashKey`: is our [Boolean] parameter [hasValidUnsplashKey].
 *  - `imageHeight`: is uses the current [LocalDensity] as the receiver of a lambda function that
 *  sets its [Dp] variable `candidateHeight` to [Dimens.PlantDetailAppBarHeight] and returns the
 *  max of `candidateHeight` and `1.dp`.
 *  - `onFabClick`: is the [PlantDetailsCallbacks.onFabClick] lambda property of our
 *  [PlantDetailsCallbacks] parameter [callbacks].
 *  - `onGalleryClick`: is a lambda that calls the [PlantDetailsCallbacks.onGalleryClick] lambda
 *  property of our [PlantDetailsCallbacks] parameter [callbacks] with the [Plant] argument our
 *  [Plant] parameter [plant].
 *  - `contentAlpha`: is a lambda function that returns the value of our animated [State] of
 *  [Float] variable `contentAlpha`.
 *
 * **Second** a [PlantToolbar] whose arguments are:
 *  - `toolbarState`: is our [ToolbarState] variable `toolbarState`
 *  - `plantName`: is the [Plant.name] property of our [Plant] parameter [plant].
 *  - `callbacks`: is our [PlantDetailsCallbacks] parameter [callbacks].
 *  - `toolbarAlpha`: is a lambda function that returns the value of our animated [State] of
 *  [Float] variable `toolbarAlpha`.
 *  - `contentAlpha`: is a lambda function that returns the value of our animated [State] of
 *  [Float] variable `contentAlpha`.
 *
 * @param plant The [Plant] object to display details for.
 * @param isPlanted A boolean indicating whether the plant is currently in the user's garden.
 * @param hasValidUnsplashKey A boolean to determine if the gallery icon should be shown.
 * @param callbacks A data class holding all the necessary callbacks for user interactions,
 * such as back navigation, FAB click, sharing, and opening the gallery.
 * @param modifier A [Modifier] to be applied to the root container of this composable.
 */
@VisibleForTesting
@Composable
fun PlantDetails(
    plant: Plant,
    isPlanted: Boolean,
    hasValidUnsplashKey: Boolean,
    callbacks: PlantDetailsCallbacks,
    modifier: Modifier = Modifier
) {
    // PlantDetails owns the scrollerPosition to simulate CollapsingToolbarLayout's behavior
    val scrollState: ScrollState = rememberScrollState()
    var plantScroller: PlantDetailsScroller by remember {
        mutableStateOf(
            value = PlantDetailsScroller(
                scrollState = scrollState,
                namePosition = Float.MIN_VALUE
            )
        )
    }
    val transitionState: MutableTransitionState<ToolbarState> =
        remember(key1 = plantScroller) { plantScroller.toolbarTransitionState }

    val toolbarState: ToolbarState = plantScroller.getToolbarState(density = LocalDensity.current)

    // Transition that fades in/out the header with the image and the Toolbar
    val transition: Transition<ToolbarState> = rememberTransition(
        transitionState = transitionState,
        label = ""
    )

    val toolbarAlpha: State<Float> = transition.animateFloat(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = ""
    ) { toolbarTransitionState: ToolbarState ->
        if (toolbarTransitionState == ToolbarState.HIDDEN) 0f else 1f
    }

    val contentAlpha: State<Float> = transition.animateFloat(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = ""
    ) { toolbarTransitionState: ToolbarState ->
        if (toolbarTransitionState == ToolbarState.HIDDEN) 1f else 0f
    }

    Box(modifier = modifier.fillMaxSize()) {
        PlantDetailsContent(
            scrollState = scrollState,
            toolbarState = toolbarState,
            onNamePosition = { newNamePosition: Float ->
                // Comparing to Float.MIN_VALUE as we are just interested on the original
                // position of name on the screen
                if (plantScroller.namePosition == Float.MIN_VALUE) {
                    plantScroller = plantScroller.copy(namePosition = newNamePosition)
                }
            },
            plant = plant,
            isPlanted = isPlanted,
            hasValidUnsplashKey = hasValidUnsplashKey,
            imageHeight = with(receiver = LocalDensity.current) {
                val candidateHeight: Dp = Dimens.PlantDetailAppBarHeight
                // FIXME: Remove this workaround when https://github.com/bumptech/glide/issues/4952 is released
                maxOf(a = candidateHeight, b = 1.dp)
            },
            onFabClick = callbacks.onFabClick,
            onGalleryClick = { callbacks.onGalleryClick(plant) },
            contentAlpha = { contentAlpha.value }
        )
        PlantToolbar(
            toolbarState = toolbarState,
            plantName = plant.name,
            callbacks = callbacks,
            toolbarAlpha = { toolbarAlpha.value },
            contentAlpha = { contentAlpha.value }
        )
    }
}

/**
 * The main content of the plant details screen. This composable is responsible for displaying the
 * plant image, the floating action button (FAB) for adding the plant to the garden, and the
 * detailed plant information. The content is scrollable and fades out as the toolbar becomes
 * visible.
 *
 * Our root composable is a [Column] whose `modifier` argument is a [Modifier.verticalScroll] whose
 * `state` argument is our [ScrollState] parameter [scrollState]. In the [ColumnScope] `content`
 * composable lambda argument of the [Column] we compose a [ConstraintLayout]. In the
 * [ConstraintLayoutScope] `content` composable lambda argument of the [ConstraintLayout] we start
 * by initializing our [ConstrainedLayoutReference] variables `image`, `fab`, and `info` to the
 * values returned by the [ConstraintLayoutScope.createRefs] function. Then we compose:
 *
 * **First** a [PlantImage] whose arguments are:
 *  - `imageUrl`: is the [Plant.imageUrl] property of our [Plant] parameter [plant].
 *  - `imageHeight`: is our [Dp] parameter [imageHeight].
 *  - `modifier`: is a [ConstraintLayoutScope.constrainAs] whose `ref` argument is the
 *  [ConstrainedLayoutReference] variable `image` with the [ConstrainScope] `constrainBlock`
 *  lambda argument linking its `top` to the parent's `top`, and chained to that is a
 *  [Modifier.alpha] whose `alpha` argument is the value of our animated [State] of [Float]
 *  variable `contentAlpha`.
 *
 * **Second** if our [Boolean] parameter [isPlanted] is `false` we initialize our [Dp] variable
 * `fabEndMargin` to [Dimens.PaddingSmall] and compose a [PlantFab] whose arguments are:
 *  - `onFabClick`: is our [onFabClick] lambda parameter.
 *  - `modifier`: is a [ConstraintLayoutScope.constrainAs] whose `ref` argument is the
 *  [ConstrainedLayoutReference] variable `fab` with the [ConstrainScope] `constrainBlock`
 *  argument using the [ConstrainScope.centerAround] method to add top and bottom links towards the
 *  horizontal anchor of the `bottom` of [ConstrainedLayoutReference] variable `image`, and the
 *  [ConstrainScope.absoluteRight] linking it to the vertical anchor of the `absoluteRight` of the
 *  parent with a `margin` of `fabEndMargin`, and chained to that is a [Modifier.alpha] whose
 *  `alpha` argument is the value returned by our lambda parameter [contentAlpha].
 *
 * **Third** a [PlantInformation] whose arguments are:
 *  - `name`: is the [Plant.name] property of our [Plant] parameter [plant].
 *  - `wateringInterval`: is the [Plant.wateringInterval] property of our [Plant] parameter [plant].
 *  - `description`: is the [Plant.description] property of our [Plant] parameter [plant].
 *  - `hasValidUnsplashKey`: is our [Boolean] parameter [hasValidUnsplashKey].
 *  - `onNamePosition`: is a lambda function that accepts the [Float] passed the lambda in variable
 *  `namePosition` and calls our [onNamePosition] lambda parameter with `namePosition`.
 *  - `toolbarState`: is our [ToolbarState] parameter [toolbarState].
 *  - `onGalleryClick`: is our [onGalleryClick] lambda parameter.
 *  - `modifier`: is a [ConstraintLayoutScope.constrainAs] whose `ref` argument is the
 *  [ConstrainedLayoutReference] variable `info` with the [ConstrainScope] `constrainBlock`
 *  argument linking its `top` to the `bottom` of [ConstrainedLayoutReference] variable `image`.
 *
 * @param scrollState The [ScrollState] for the vertical scrolling behavior of the content.
 * @param toolbarState The current state of the toolbar, used to control the visibility of content.
 * @param plant The [Plant] object containing the details to be displayed.
 * @param isPlanted A boolean indicating if the plant is already in the user's garden. If `true`,
 * the FAB is not shown.
 * @param hasValidUnsplashKey A boolean indicating if a valid Unsplash API key is available,
 * which determines whether the gallery icon is shown and is functional.
 * @param imageHeight The height of the plant image.
 * @param onNamePosition A callback to use to report the vertical position of the plant name, used
 * for coordinating the collapsing toolbar animation.
 * @param onFabClick A lambda to be invoked when the "Add to Garden" FAB is clicked.
 * @param onGalleryClick A lambda to be invoked when the gallery icon is clicked.
 * @param contentAlpha A lambda that provides the alpha value for the content, used for the
 * fade-in/fade-out animation.
 */
@Composable
private fun PlantDetailsContent(
    scrollState: ScrollState,
    toolbarState: ToolbarState,
    plant: Plant,
    isPlanted: Boolean,
    hasValidUnsplashKey: Boolean,
    imageHeight: Dp,
    onNamePosition: (Float) -> Unit,
    onFabClick: () -> Unit,
    onGalleryClick: () -> Unit,
    contentAlpha: () -> Float,
) {
    Column(modifier = Modifier.verticalScroll(state = scrollState)) {
        ConstraintLayout {
            val (image: ConstrainedLayoutReference,
                fab: ConstrainedLayoutReference,
                info: ConstrainedLayoutReference
            ) = createRefs()

            PlantImage(
                imageUrl = plant.imageUrl,
                imageHeight = imageHeight,
                modifier = Modifier
                    .constrainAs(ref = image) { top.linkTo(anchor = parent.top) }
                    .alpha(alpha = contentAlpha())
            )

            if (!isPlanted) {
                val fabEndMargin: Dp = Dimens.PaddingSmall
                PlantFab(
                    onFabClick = onFabClick,
                    modifier = Modifier
                        .constrainAs(ref = fab) {
                            centerAround(anchor = image.bottom)
                            absoluteRight.linkTo(
                                anchor = parent.absoluteRight,
                                margin = fabEndMargin
                            )
                        }
                        .alpha(alpha = contentAlpha())
                )
            }

            PlantInformation(
                name = plant.name,
                wateringInterval = plant.wateringInterval,
                description = plant.description,
                hasValidUnsplashKey = hasValidUnsplashKey,
                onNamePosition = { namePosition: Float -> onNamePosition(namePosition) },
                toolbarState = toolbarState,
                onGalleryClick = onGalleryClick,
                modifier = Modifier.constrainAs(ref = info) {
                    top.linkTo(anchor = image.bottom)
                }
            )
        }
    }
}

/**
 * A composable that displays a plant image loaded from a URL.
 *
 * This function uses [GlideImage] to load and display the image from the given [imageUrl].
 * While the image is loading, it shows a placeholder with a specified color.
 * The loading state is managed internally to transition from the placeholder to the
 * loaded image. The function handles both successful loading and loading failures by
 * updating the loading state accordingly.
 *
 * We start by initializing and remembering our [MutableState] wrapped [Boolean] variable `isLoading`
 * to an initial value of `true`. Our root composable is a [Box] whose `modifier` argument chains
 * to our [Modifier] parameter [modifier] a [Modifier.fillMaxWidth] and a [Modifier.height] whose
 * `height` argument is our [Dp] parameter [imageHeight]. In the [BoxScope] `content` composable
 * lambda argument of the [Box] if `isLoading` is `true` we compose a [Box] whose `modifier`
 * argument is a [Modifier.fillMaxSize] chained to a [Modifier.background] whose `color` argument is
 * our [Color] parameter [placeholderColor]. In any case we compose a [GlideImage] whose arguments
 * are:
 *  - `model`: is our [String] parameter [imageUrl].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.fillMaxSize].
 *  - `contentScale`: is [ContentScale.Crop].
 *
 * In the lambda argument of the [GlideImage] we acccept the [RequestBuilder] of [Drawable] passed
 * the lambda in variable `builder` and use its [RequestBuilder.addListener] method to add a
 * [RequestListener] of [Drawable] object whose `onLoadFailed` override sets our [MutableState]
 * wrapped [Boolean] variable `isLoading` to `false` and returns `false` and whose `onResourceReady`
 * override sets our [MutableState] wrapped [Boolean] variable `isLoading` to `false` and returns
 * `false` (returning `false` allows Glide to call the [Target.onLoadFailed] method or the
 * [Target.onResourceReady] method respectively, while `true` prevents Glide from calling them).
 *
 * @param imageUrl The URL of the image to be displayed.
 * @param imageHeight The fixed height for the image container. The image will fill this height.
 * @param modifier A [Modifier] to be applied to the image container.
 * @param placeholderColor The color of the placeholder shown while the image is loading. Defaults
 * to a copy of the [ColorScheme.onSurface] color from the current [MaterialTheme] with its
 * `alpha` set to 0.2f.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PlantImage(
    imageUrl: String,
    imageHeight: Dp,
    modifier: Modifier = Modifier,
    placeholderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
) {
    var isLoading: Boolean by remember { mutableStateOf(value = true) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height = imageHeight)
    ) {
        if (isLoading) {
            // TODO: Update this implementation once Glide releases a version
            // that contains this feature: https://github.com/bumptech/glide/pull/4934
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = placeholderColor)
            )
        }
        GlideImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        ) { builder: RequestBuilder<Drawable> ->
            builder.addListener(object : RequestListener<Drawable> {
                /**
                 * Called when an exception occurs during a load, immediately before
                 * [Target.onLoadFailed]. Will only be called if we currently want to display an
                 * image for the given [model] in the given [target]. It is recommended to create
                 * a single instance per activity/fragment rather than instantiate a new object for
                 * each call to [GlideImage] to avoid object churn.
                 *
                 * @param e The maybe `null` exception containing information about why the request
                 * failed.
                 * @param model The model we were trying to load when the exception occurred.
                 * @param target The [Target] we were trying to load the image into.
                 * @param isFirstResource `true` if this exception is for the first resource to load.
                 * @return `true` to prevent [Target.onLoadFailed] from being called on [target],
                 * typically because the listener wants to update the [target] or the object the
                 * [target] wraps itself or `false` to allow [Target.onLoadFailed] to be called on
                 * [target].
                 */
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    isLoading = false
                    return false
                }

                /**
                 * Called when a load completes successfully, immediately before
                 * [Target.onResourceReady].
                 *
                 * @param resource The resource that was loaded for the target. Non-1 because a
                 * `null` resource will result in a call to [onLoadFailed] instead of this method.
                 * @param model The specific model that was used to load the image. Non-`null`
                 * because a `null` model will result in a call to [onLoadFailed] instead of
                 * this method.
                 * @param target The [Target] the model was loaded into.
                 * @param dataSource The [DataSource] the resource was loaded from.
                 * @param isFirstResource `true` if this is the first resource to in this load to be
                 * loaded into the target. For example when loading a thumbnail and a full-sized
                 * image, this will be `true` for the first image to load and `false` for the second.
                 * @return `true` to prevent [Target.onResourceReady] from being called on [target],
                 * typically because the listener wants to update the [target] or the object the
                 * [target] wraps itself or `false` to allow [Target.onResourceReady] to be called
                 * on [target].
                 */
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    isLoading = false
                    return false
                }
            })
        }
    }
}

/**
 * A floating action button (FAB) with an 'Add' icon, used to add the plant to the garden.
 *
 * We start by initializing our [String] variable `addPlantContentDescription` to the string with
 * resource ID `R.string.add_plant` ("Add plant") and compose a [FloatingActionButton] whose
 * `onClick` argument is our [onFabClick] lambda parameter, whose `shape` argument is the
 * [Shapes.small] of our custom [MaterialTheme.shapes], and whose `modifier` argument chains to
 * our [Modifier] parameter [modifier] a [Modifier.semantics] whose `contentDescription` is our
 * [String] variable `addPlantContentDescription`.
 *
 * In the `content` composable lambda argument of the [FloatingActionButton] we compose an [Icon]
 * whose `imageVector` argument is the [ImageVector] drawn by [Icons.Filled.Add] and whose
 * `contentDescription` argument is `null`.
 *
 * @param onFabClick A lambda to be invoked when the FAB is clicked.
 * @param modifier A [Modifier] to be applied to the FAB.
 */
@Composable
private fun PlantFab(
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val addPlantContentDescription: String = stringResource(id = R.string.add_plant)
    FloatingActionButton(
        onClick = onFabClick,
        shape = MaterialTheme.shapes.small,
        // Semantics in parent due to https://issuetracker.google.com/184825850
        modifier = modifier.semantics {
            contentDescription = addPlantContentDescription
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null
        )
    }
}

/**
 * A composable that switches between two different toolbar styles based on the
 * scrolling state of the screen. It can either display a prominent set of header
 * actions over the plant image or a more traditional, condensed TopAppBar.
 *
 * The transition between these two states is animated using alpha fades.
 *
 * We start by initializing our lambda variable `onShareClick` to a lambd which calls the
 * [PlantDetailsCallbacks.onShareClick] method of our [PlantDetailsCallbacks] parameter [callbacks]
 * with our [String] parameter [plantName]. If the [ToolbarState.isShown] property of our
 * [ToolbarState] parameter [toolbarState] is `true` we compose a [PlantDetailsToolbar] with the
 * arguments:
 *  - `plantName`: is our [String] parameter [plantName].
 *  - `onBackClick`: is the [PlantDetailsCallbacks.onBackClick] lambda property of our
 *  [PlantDetailsCallbacks] parameter [callbacks].
 *  - `onShareClick`: is our lambda variable `onShareClick`.
 *  - `modifier`: is a [Modifier.alpha] whose `alpha` argument is the value returned by our lambda
 *  parameter [toolbarAlpha].
 *
 * If the [ToolbarState.isShown] property of our [ToolbarState] parameter [toolbarState] is `false`
 * we compose a [PlantHeaderActions] with the arguments:
 *  - `onBackClick`: is the [PlantDetailsCallbacks.onBackClick] lambda property of our
 *  [PlantDetailsCallbacks] parameter [callbacks].
 *  - `onShareClick`: is our lambda variable `onShareClick`.
 *  - `modifier`: is a [Modifier.alpha] whose `alpha` argument is the value returned by our lambda
 *  parameter [contentAlpha].
 *
 * @param toolbarState The current state of the toolbar, which determines whether the full toolbar
 * ([PlantDetailsToolbar]) or just the header actions ([PlantHeaderActions]) should be shown.
 * @param plantName The name of the plant, to be displayed in the full toolbar.
 * @param callbacks A data class containing the callbacks for back and share actions.
 * @param toolbarAlpha A lambda that provides the alpha value for the [PlantDetailsToolbar] full
 * toolbar's fade-in animation.
 * @param contentAlpha A lambda that provides the alpha value for the [PlantHeaderActions] header
 * actions' fade-out animation.
 */
@Composable
private fun PlantToolbar(
    toolbarState: ToolbarState,
    plantName: String,
    callbacks: PlantDetailsCallbacks,
    toolbarAlpha: () -> Float,
    contentAlpha: () -> Float
) {
    val onShareClick = {
        callbacks.onShareClick(plantName)
    }
    if (toolbarState.isShown) {
        PlantDetailsToolbar(
            plantName = plantName,
            onBackClick = callbacks.onBackClick,
            onShareClick = onShareClick,
            modifier = Modifier.alpha(alpha = toolbarAlpha())
        )
    } else {
        PlantHeaderActions(
            onBackClick = callbacks.onBackClick,
            onShareClick = onShareClick,
            modifier = Modifier.alpha(alpha = contentAlpha())
        )
    }
}

/**
 * A toolbar that displays the plant name and action buttons.
 *
 * This composable is used when the user has scrolled past the initial header,
 * providing a persistent [TopAppBar] with navigation and sharing actions.
 *
 * It consists of a back button, the plant name centered, and a share button.
 * The entire toolbar is wrapped in a [Surface] and applies status bar padding
 * to avoid overlapping with system UI elements.
 *
 * The arguments of the [TopAppBar] are:
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.statusBarsPadding]
 *  chained to a [Modifier.background] whose `color` argument is the [ColorScheme.surface] of
 *  our custom [MaterialTheme.colorScheme].
 *  - `title`: is a lambda that composes a [Row] whose.
 *
 * The in the [RowScope] `content` composable lambda argument of the [Row] we compose:
 *
 * **First** an [IconButton] whose `onClick` argument is our [onBackClick] lambda parameter,
 * and whose `modifier` argument is a [RowScope.align] whose `alignment` argument is
 * [Alignment.CenterVertically]. In the `content` composable lambda argument of the [IconButton]
 * we compose an [Icon] whose `imageVector` argument is [Icons.AutoMirrored.Filled.ArrowBack],
 * and whose `contentDescription` argument the [stringResource] with resource ID `R.string.a11y_back`
 * ("Navigate up").
 *
 * **Second** a [Text] whose `text` argument is our [String] parameter [plantName], whose `style`
 * argument is the [Typography.titleLarge] of our custom [MaterialTheme.typography], and whose
 * `modifier` argument is a [RowScope.weight] whose `weight` is `1f`, chained to a
 * [Modifier.fillMaxSize] chained to a [Modifier.wrapContentSize] whose `align` argument is
 * [Alignment.Center].
 *
 * **Third** we initialize our [String] variable `shareContentDescription` to the string with the
 * resource ID `R.string.menu_item_share_plant` ("Share") then compose an [IconButton] whose
 * `onClick` argument is our [onShareClick] lambda parameter, and whose `modifier` argument is a
 * [RowScope.align] whose `alignment` argument is [Alignment.CenterVertically], chained to a
 * [Modifier.semantics] whose `contentDescription` argument is our [String] variable
 * `shareContentDescription`. In the `content` composable lambda argument of the [IconButton]
 * we compose an [Icon] whose `imageVector` argument is [Icons.Filled.Share], and whose
 * `contentDescription` argument is `null`.
 *
 * @param plantName The name of the plant to be displayed as the title of the toolbar.
 * @param onBackClick A lambda function to be invoked when the back button is clicked,
 * typically for navigating back to the previous screen.
 * @param onShareClick A lambda function to be invoked when the share button is clicked,
 * used to trigger a sharing action for the plant.
 * @param modifier A [Modifier] to be applied to the root [TopAppBar] container.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlantDetailsToolbar(
    plantName: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface {
        TopAppBar(
            modifier = modifier
                .statusBarsPadding()
                .background(color = MaterialTheme.colorScheme.surface),
            title = {
                Row {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.a11y_back)
                        )
                    }
                    Text(
                        text = plantName,
                        style = MaterialTheme.typography.titleLarge,
                        // As title in TopAppBar has extra inset on the left, need to do this: b/158829169
                        modifier = Modifier
                            .weight(weight = 1f)
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                    val shareContentDescription: String =
                        stringResource(id = R.string.menu_item_share_plant)
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            // Semantics in parent due to https://issuetracker.google.com/184825850
                            .semantics { contentDescription = shareContentDescription }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null
                        )
                    }
                }
            }
        )
    }
}

/**
 * A row of actions that are displayed over the plant image in the initial state of the
 * screen. It includes a back button and a share button.
 *
 * This composable is meant to be used when the main toolbar is not yet visible, providing
 * essential actions in a way that integrates with the large header image.
 * The icons are given a circular background to ensure they are visible against
 * the plant image.
 *
 * Our root composable is a [Row] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.fillMaxSize], chained to a [Modifier.systemBarsPadding] to avoid drawing
 * under the system bars, chained to a [Modifier.padding] that adds [Dimens.ToolbarIconPadding] to
 * its `top`. The `horizontalArrangement` argument of the [Row] is [Arrangement.SpaceBetween] to
 * place its children such that they are spaced evenly across the main axis, without free space
 * before the first child or after the last child.
 *
 * In the [RowScope] `content` composable lambda argument of the [Row] we start by initializing our
 * [Modifier] variable `iconModifier` to a [Modifier.sizeIn] whose `maxWidth` argument is
 * [Dimens.ToolbarIconSize] and whose `maxHeight` argument is [Dimens.ToolbarIconSize], chained
 * to a [Modifier.background] whose `color` argument is the [ColorScheme.surface] of our custom
 * [MaterialTheme.colorScheme], and whose `shape` argument is [CircleShape]. Then we compose:
 *
 * **First** an [IconButton] whose `onClick` argument is our [onBackClick] lambda parameter,
 * and whose `modifier` argument is a [Modifier.padding] that adds [Dimens.ToolbarIconPadding]
 * to its `start`, chained to a [Modifier.then] whose `other` argument is our [Modifier]
 * variable `iconModifier`. In the `content` composable lambda argument of the [IconButton] we
 * compose an [Icon] whose `imageVector` argument is [Icons.AutoMirrored.Filled.ArrowBack],
 * and whose `contentDescription` argument the [stringResource] with resource ID `R.string.a11y_back`
 * ("Navigate up").
 *
 * **Second** we initialize our [String] variable `shareContentDescription` to the string with
 * the resource ID `R.string.menu_item_share_plant` ("Share") then compose an [IconButton]
 * whose `onClick` argument is our [onShareClick] lambda parameter, and whose `modifier` argument
 * is a [Modifier.padding] that adds [Dimens.ToolbarIconPadding] to its `end`, chained to a
 * [Modifier.then] whose `other` argument is our [Modifier] variable `iconModifier`, chained to a
 * [Modifier.semantics] whose `contentDescription` argument is our [String] variable
 * `shareContentDescription`. In the `content` composable lambda argument of the [IconButton] we
 * compose an [Icon] whose `imageVector` argument is [Icons.Filled.Share], and whose
 * `contentDescription` argument is `null`.
 *
 * @param onBackClick A lambda function to be invoked when the back button is clicked.
 * @param onShareClick A lambda function to be invoked when the share button is clicked.
 * @param modifier A [Modifier] to be applied to the root [Row].
 */
@Composable
private fun PlantHeaderActions(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(top = Dimens.ToolbarIconPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val iconModifier = Modifier
            .sizeIn(
                maxWidth = Dimens.ToolbarIconSize,
                maxHeight = Dimens.ToolbarIconSize
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(start = Dimens.ToolbarIconPadding)
                .then(other = iconModifier)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.a11y_back)
            )
        }
        val shareContentDescription: String = stringResource(id = R.string.menu_item_share_plant)
        IconButton(
            onClick = onShareClick,
            modifier = Modifier
                .padding(end = Dimens.ToolbarIconPadding)
                .then(other = iconModifier)
                // Semantics in parent due to https://issuetracker.google.com/184825850
                .semantics {
                    contentDescription = shareContentDescription
                }
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = null
            )
        }
    }
}

/**
 * A composable function that displays detailed information about a plant.
 * This includes the plant's name, its watering needs, and a gallery icon if applicable.
 * The plant's name is only visible when the toolbar is hidden, and its position is reported
 * for coordinating animations.
 *
 * Our root composable is a [Column] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.padding] that adds [Dimens.PaddingLarge] to all sides. In the [ColumnScope]
 * `content` composable lambda argument of the [Column] we compose:
 *
 * **First** a [Text] whose `text` argument is our [String] parameter [name], whose `style` argument
 * is the [Typography.displaySmall] of our custom [MaterialTheme.typography], and whose `modifier`
 * argument is a [Modifier.padding] that adds [Dimens.PaddingSmall] to the `start` and `end` and
 * [Dimens.PaddingNormal] to the `bottom`, chained to a [ColumnScope.align] whose `alignment`
 * argument is [Alignment.CenterHorizontally], chained to a [Modifier.onGloballyPositioned] in whose
 * `onGloballyPositioned` lambda argument we accept the [LayoutCoordinates] passed the lambda in
 * variable `coord` then call our [onNamePosition] lambda parameter with the [Offset.y] of the
 * value returned by [LayoutCoordinates.positionInWindow] property of `coord`, and at the end of the
 * chain is a [Modifier.visible] whose `condition` argument is `true` if our [ToolbarState] parameter
 * [toolbarState] is [ToolbarState.HIDDEN].
 *
 * **Second** a [Box] whose `modifier` argument is a [ColumnScope.align] whose `alignment` argument
 * is [Alignment.CenterHorizontally], chained to a [Modifier.padding] that adds [Dimens.PaddingSmall]
 * to the `start`, and `end` sides and [Dimens.PaddingNormal] to the `bottom` side. In the [BoxScope]
 * `content` composable lambda argument of the [Box] we compose a [Column] whose `modifier` argument
 * is a [Modifier.fillMaxWidth]. In the [ColumnScope] `content` composable lambda argument of the
 * [Column] we compose a [Text] whose `text` argument is the string with resource ID
 * `R.string.watering_needs_prefix` ("Watering needs"), and whose `fontWeight` argument is
 * [FontWeight.Bold], and whose `modifier` argument is a [Modifier.padding] that adds
 * [Dimens.PaddingSmall] to the `horizontal` sides. We initialize our [String] variable
 * `wateringIntervalText` to the [pluralStringResource] whose `id` is `R.plurals.watering_needs_suffix`,
 * whose `count` is our [Int] parameter [wateringInterval] and whose `formatArgs` is our
 * [Int] parameter [wateringInterval] then we compose a [Text] whose `text` argument is our [String]
 * variable `wateringIntervalText`, and whose `modifier` argument is a [ColumnScope.align] whose
 * `alignment` argument is [Alignment.CenterHorizontally]. Then if our [Boolean] parameter
 * [hasValidUnsplashKey] is `true` we initialize and remember our [MutableInteractionSource] variable
 * `interactionSource` to a [MutableInteractionSource] instance and compose a [Image] whose arguments
 * are:
 *  - `painter`: is the [painterResource] with resource ID `R.drawable.ic_photo_library`,
 *  - `contentDescription`: is `null`,
 *  - `modifier`: is a [Modifier.clickable] whose `interactionSource` argument is our
 *  [MutableInteractionSource] variable `interactionSource` and whose `indication` argument is
 *  `null`, chained to a [ColumnScope.align] whose `alignment` argument is [Alignment.CenterEnd].
 *
 * **Third** underneath the [Box] we compose a [PlantDescription] whose `description` argument is
 * our [String] parameter [description].
 *
 * Then we compose a [Text] whose `text` argument
 *
 * @param name The name of the plant.
 * @param wateringInterval The interval at which the plant should be watered.
 * @param description A description of the plant.
 * @param hasValidUnsplashKey A flag indicating whether a valid Unsplash API key is available.
 * @param onNamePosition A callback to use to report the position of the plant name.
 * @param toolbarState The current state of the toolbar.
 * @param onGalleryClick A callback to use to handle the click on the gallery icon.
 * @param modifier The modifier to apply to this layout.
 */
@Composable
private fun PlantInformation(
    name: String,
    wateringInterval: Int,
    description: String,
    hasValidUnsplashKey: Boolean,
    onNamePosition: (Float) -> Unit,
    toolbarState: ToolbarState,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(all = Dimens.PaddingLarge)) {
        Text(
            text = name,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(
                    start = Dimens.PaddingSmall,
                    end = Dimens.PaddingSmall,
                    bottom = Dimens.PaddingNormal
                )
                .align(Alignment.CenterHorizontally)
                .onGloballyPositioned { coord: LayoutCoordinates ->
                    onNamePosition(coord.positionInWindow().y)
                }
                .visible { toolbarState == ToolbarState.HIDDEN }
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    start = Dimens.PaddingSmall,
                    end = Dimens.PaddingSmall,
                    bottom = Dimens.PaddingNormal
                )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.watering_needs_prefix),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = Dimens.PaddingSmall)
                        .align(alignment = Alignment.CenterHorizontally)
                )

                val wateringIntervalText: String = pluralStringResource(
                    id = R.plurals.watering_needs_suffix,
                    count = wateringInterval,
                    wateringInterval
                )

                Text(
                    text = wateringIntervalText,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                )
            }
            if (hasValidUnsplashKey) {
                val interactionSource: MutableInteractionSource =
                    remember { MutableInteractionSource() }
                Image(
                    painter = painterResource(id = R.drawable.ic_photo_library),
                    contentDescription = "Gallery Icon",
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onGalleryClick() }
                        .align(alignment = Alignment.CenterEnd)
                )
            }
        }
        PlantDescription(description = description)
    }
}

/**
 * A Composable that displays the plant's description.
 *
 * This function uses an [AndroidViewBinding] to embed a traditional Android [android.widget.TextView]
 * into the Compose UI. This is necessary because Compose's `Text` composable does not yet support
 * rendering HTML content, which is used for the plant description.
 *
 * We compose an [AndroidViewBinding] whose `factory` uses the [ItemPlantDescriptionBinding.inflate]
 * method to inflate the xml file `R.layout.item_plant_description.xml` into a
 * [ItemPlantDescriptionBinding] which it uses as the receiver of its `update` lambda argument in
 * which we set the `text` property of the `plantDescription` `TextView` (the `TextView` whose
 * resource ID is `R.id.plant_description`) to the [Spanned] returned by the [HtmlCompat.fromHtml]
 * method for the `source` our [String] parameter [description] and the `flags`
 * [HtmlCompat.FROM_HTML_MODE_COMPACT]. We also set the `movementMethod` property of the
 * `plantDescription` `TextView` to a [LinkMovementMethod] (movement method that traverses links in
 * the text buffer and scrolls if necessary) and set the `linksClickable` property of the
 * `plantDescription` `TextView` to `true`.
 *
 * @param description The HTML-formatted string describing the plant.
 */
@Composable
private fun PlantDescription(description: String) {
    // This remains using AndroidViewBinding because this feature is not in Compose yet
    AndroidViewBinding(factory = ItemPlantDescriptionBinding::inflate) {
        plantDescription.text = HtmlCompat.fromHtml(
            description,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        plantDescription.movementMethod = LinkMovementMethod.getInstance()
        plantDescription.linksClickable = true
    }
}

/**
 * A preview of the [PlantDetails] composable, which represents the entire plant details screen.
 * This preview is configured to display a sample "Tomato" plant that is already in the user's
 * garden (`isPlanted = true`), so the Floating Action Button for adding the plant will not be
 * visible. It also assumes a valid Unsplash key is present, so the gallery icon will be shown.
 * The callbacks for user interactions are provided as empty lambdas, as they are not active
 * in a static preview.
 */
@Preview
@Composable
private fun PlantDetailContentPreview() {
    SunflowerTheme {
        Surface {
            PlantDetails(
                plant = Plant(
                    plantId = "plantId",
                    name = "Tomato",
                    description = "HTML<br>description",
                    growZoneNumber = 6
                ),
                isPlanted = true,
                hasValidUnsplashKey = true,
                callbacks = PlantDetailsCallbacks(
                    onFabClick = { },
                    onBackClick = { },
                    onShareClick = { },
                    onGalleryClick = { })
            )
        }
    }
}
