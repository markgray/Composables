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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
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
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
 * TODO: Continue here.
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
    val scrollState = rememberScrollState()
    var plantScroller by remember {
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
                onNamePosition = { name: Float -> onNamePosition(name) },
                toolbarState = toolbarState,
                onGalleryClick = onGalleryClick,
                modifier = Modifier.constrainAs(ref = info) {
                    top.linkTo(anchor = image.bottom)
                }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PlantImage(
    imageUrl: String,
    imageHeight: Dp,
    modifier: Modifier = Modifier,
    placeholderColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.2f)
) {
    var isLoading: Boolean by remember { mutableStateOf(value = true) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(imageHeight)
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
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    isLoading = false
                    return false
                }

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
