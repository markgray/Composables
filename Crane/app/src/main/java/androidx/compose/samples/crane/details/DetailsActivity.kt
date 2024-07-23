/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.samples.crane.details

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.samples.crane.base.Result
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.home.MainActivity
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The key under which the city name is stored as an extra in the [Intent] used to launch
 * [DetailsActivity], and which is used by Hilt when it stores the city name in the
 * [SavedStateHandle] passed to [DetailsViewModel].
 */
internal const val KEY_ARG_DETAILS_CITY_NAME = "KEY_ARG_DETAILS_CITY_NAME"

/**
 * Launches the [Intent] created by [createDetailsActivityIntent] to launch the [DetailsActivity]
 * activity. It just uses the [Context.startActivity] of its [Context] parameter [context] to launch
 * the [Intent] created by [createDetailsActivityIntent] when passed our [context] parameter and our
 * [item] parameter.
 *
 * @param context the [Context] of our apps [MainActivity].
 * @param item the [ExploreModel] of the [City] whose map the user wants to see.
 */
fun launchDetailsActivity(context: Context, item: ExploreModel) {
    context.startActivity(createDetailsActivityIntent(context = context, item = item))
}

/**
 * Creates an [Intent] to launch [DetailsActivity], adds an extra that stores the [City.name] of the
 * [ExploreModel.city] of its [ExploreModel] parameter [item] under the key [KEY_ARG_DETAILS_CITY_NAME]
 * and returns the [Intent] to the caller.
 *
 * @param context the [Context] of our apps [MainActivity].
 * @param item the [ExploreModel] of the [City] whose map the user wants to see.
 */
@VisibleForTesting
fun createDetailsActivityIntent(context: Context, item: ExploreModel): Intent {
    val intent = Intent(context, DetailsActivity::class.java)
    intent.putExtra(KEY_ARG_DETAILS_CITY_NAME, item.city.name)
    return intent
}

/**
 * This activity displays the "details" of a city clicked in [MainActivity]. The AndroidEntryPoint
 * annotation Marks an Android component class to be setup for injection with the standard Hilt
 * Dagger Android components. This will generate a base class that the annotated class should extend,
 * either directly or via the Hilt Gradle Plugin (as we do). This base class will take care of
 * injecting members into the Android class as well as handling instantiating the proper Hilt
 * components at the right point in the lifecycle. The name of the base class will be
 * "Hilt_DetailsActivity.java".
 */
@AndroidEntryPoint
class DetailsActivity : ComponentActivity() {

    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge-to-edge
     * display with the `statusBarStyle` argument a [SystemBarStyle.dark] whose `scrim` is
     * [Color.TRANSPARENT] (This lets the color of our app show through the system bar), then we
     * call our super's implementation of `onCreate`. Finally we call the [setContent] method to have
     * it compose the composable lambda it is passed into our activity. The content will become the
     * root view of our activity. That lambda uses our [CraneTheme] custom [MaterialTheme] to wrap
     * a [Surface] whose `content` is a [DetailsScreen] whose `onErrorLoading` argument is a lambda
     * which logs the error message "Error loading screen", then calls [finish] to close this activity,
     * and whose `modifier` argument uses [Modifier.statusBarsPadding] to add padding to accommodate
     * the system status bars insets, and [Modifier.navigationBarsPadding] to add padding to
     * accommodate the navigation bars insets.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)

        setContent {
            CraneTheme {
                Surface {
                    DetailsScreen(
                        onErrorLoading = {
                            Log.e("DetailsActivity", "Error loading screen")
                            finish()
                        },
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    )
                }
            }
        }
    }
}

/**
 * This data class it used by [DetailsScreen] to determine whether it is currently "loading" the
 * [City] it needs for its [city] field ([isLoading] is `true`), whether it has successfully loaded
 * its [City] ([city] is not equal to `null`), or whether an error has occurred ([throwError] is
 * `true`).
 *
 * @param city the [City] retrieved from the [DetailsViewModel.cityDetails] property when it returns
 * a [Result.Success] of a [City].
 * @param isLoading if `true` we are waiting for a [Result] to be returned from the
 * [DetailsViewModel.cityDetails] property.
 * @param throwError `true` if [DetailsViewModel.cityDetails] returns a [Result.Error].
 */
private data class DetailsScreenUiState(
    val city: City? = null,
    val isLoading: Boolean = false,
    val throwError: Boolean = false
)

/**
 * This is the main screen of [DetailsActivity] and is composed into the activity by the
 * [setContent] method in the [ComponentActivity.onCreate] override of [DetailsActivity].
 * It uses a [DetailsScreenUiState] observable snapshot State that is created by the
 * [produceState] method (which it stores in its varible `val uiState`) to decide what it
 * needs to do during the current recomposition. The `key1` argument of [produceState] is our
 * [DetailsViewModel] parameter [viewModel] (any change to [viewModel] will cause the
 * [LaunchedEffect] in [produceState] to be relaunched), the `initialValue` is a
 * [DetailsScreenUiState] whose [DetailsScreenUiState.isLoading] property is `true`, and in
 * the `producer` lambda argument of [produceState] we initialize our [Result] of [City] variable
 * `val cityDetailsResult` to the value returned by the [DetailsViewModel.cityDetails] property
 * of our [DetailsViewModel] parameter [viewModel] then we set the [MutableState.value] of
 * `uiState` to a [DetailsScreenUiState] whose [DetailsScreenUiState.city] is the
 * [Result.Success.data] of `cityDetailsResult` if `cityDetailsResult` is a [Result.Success] of
 * [City], or to a [DetailsScreenUiState] whose [DetailsScreenUiState.throwError] property is
 * `true` if it is not a [Result.Success].
 *
 * @param onErrorLoading a lambda we should call when there is an error loading the [City] from
 * [DetailsViewModel.cityDetails]. Our caller, the `onCreate` override of [DetailsActivity], passes
 * us a lambda that logs the error "Error loading screen" then calls [DetailsActivity.finish] to
 * close the activity.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller, the `onCreate` override of [DetailsActivity], passes us a
 * [Modifier.statusBarsPadding] to have us add padding to accommodate the status bars insets, with
 * a [Modifier.navigationBarsPadding] chained to that to have us add padding to accommodate the
 * navigation bars insets.
 * @param viewModel the [DetailsViewModel] that we should use. Our caller does not pass any, so we
 * use the [viewModel] default value of the parameter, which is the [DetailsViewModel] injected by
 * Hilt.
 */
@Composable
fun DetailsScreen(
    onErrorLoading: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = viewModel()
) {
    // The `produceState` API is used as an _alternative_ to model the
    // UiState in the ViewModel and expose it in a stream of data.
    val uiState: DetailsScreenUiState by produceState(
        key1 = viewModel,
        initialValue = DetailsScreenUiState(isLoading = true)
    ) {
        val cityDetailsResult: Result<City> = viewModel.cityDetails
        value = if (cityDetailsResult is Result.Success<City>) {
            DetailsScreenUiState(city = cityDetailsResult.data)
        } else {
            DetailsScreenUiState(throwError = true)
        }
    }

    @Suppress("Destructure")
    Crossfade(
        targetState = uiState,
        modifier = modifier,
        label = "Crossfade"
    ) { currentUiState: DetailsScreenUiState ->
        when {
            currentUiState.city != null -> {
                DetailsContent(currentUiState.city, Modifier.fillMaxSize())
            }
            currentUiState.isLoading -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            else -> {
                onErrorLoading()
            }
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
fun DetailsContent(
    city: City,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Spacer(Modifier.height(32.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = city.nameToDisplay,
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        CityMapView(city.latitude, city.longitude)
    }
}

/**
 * CityMapView
 * A composable that shows a map centered on a location with a marker.
 */
@Composable
fun CityMapView(
    latitude: String,
    longitude: String,
    onMapLoadedWithCameraState: ((CameraPositionState) -> Unit)? = null, // Exposed for use in tests
    onZoomChanged: (() -> Unit)? = null
) {
    val cityLocation = remember(latitude, longitude) {
        LatLng(latitude.toDouble(), longitude.toDouble())
    }

    val cameraPositionState = rememberCameraPositionState(cityLocation.toString()) {
        position = CameraPosition.fromLatLngZoom(
            cityLocation,
            InitialZoom
        )
    }

    MapViewContainer(
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            onMapLoadedWithCameraState?.invoke(cameraPositionState)
        },
        onZoomChanged = onZoomChanged
    ) {
        Marker(state = MarkerState(position = cityLocation))
    }
}

/**
 * MapViewContainer
 * A MapView styled with custom zoom controls.
 */
@Composable
fun MapViewContainer(
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit = {},
    onZoomChanged: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    val mapProperties = remember {
        MapProperties(
            maxZoomPreference = MaxZoom,
            minZoomPreference = MinZoom,
        )
    }

    val mapUiSettings = remember {
        // We are providing our own zoom controls so disable the built-in ones.
        MapUiSettings(zoomControlsEnabled = false)
    }

    val animationScope = rememberCoroutineScope()
    Column {
        ZoomControls(
            onZoomIn = {
                animationScope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.zoomIn())
                    onZoomChanged?.invoke()
                }
            },
            onZoomOut = {
                animationScope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.zoomOut())
                    onZoomChanged?.invoke()
                }
            }
        )

        GoogleMap(
            properties = mapProperties,
            cameraPositionState = cameraPositionState,
            uiSettings = mapUiSettings,
            onMapLoaded = onMapLoaded,
            content = content
        )
    }
}

@Composable
private fun ZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        ZoomButton(text = "-", onClick = onZoomOut)
        ZoomButton(text = "+", onClick = onZoomIn)
    }
}

@Composable
private fun ZoomButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(all = 8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary
        ),
        onClick = onClick
    ) {
        Text(text = text, style = MaterialTheme.typography.h5)
    }
}

/**
 * TODO: Add kdoc
 */
private const val InitialZoom = 5f

/**
 * TODO: Add kdoc
 */
const val MinZoom: Float = 2f

/**
 * TODO: Add kdoc
 */
const val MaxZoom: Float = 20f
