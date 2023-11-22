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
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.Result
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.home.MainActivity
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.samples.crane.ui.craneTypography
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.maps.CameraUpdate
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * The key under which the city name is stored as an extra in the [Intent] used to launch
 * [DetailsActivity], and which is used by Hilt when it stores the city name in the [SavedStateHandle]
 * passed to [DetailsViewModel].
 */
internal const val KEY_ARG_DETAILS_CITY_NAME = "KEY_ARG_DETAILS_CITY_NAME"

/**
 * Launches the [Intent] created by [createDetailsActivityIntent] to launch the [DetailsActivity]
 * activity. It just uses the [Context.startActivity] of its [Context] parameter [context] to launch
 * the [Intent] created by [createDetailsActivityIntent] when passed our [context] parameter and our
 * [item] parameter.
 *
 * @param context the [Context] of [MainActivity].
 * @param item the [ExploreModel] displayed by the `ExploreItem` that was clicked (see the file
 * ExploreSection.kt)
 */
fun launchDetailsActivity(context: Context, item: ExploreModel) {
    context.startActivity(createDetailsActivityIntent(context = context, item = item))
}

/**
 * Creates an [Intent] to launch the [DetailsActivity] so the user can view the details of the
 * [ExploreModel] parameter [item]. The `name` property of the [ExploreModel.city] of [item] is
 * added is added as an extra under the key [KEY_ARG_DETAILS_CITY_NAME]. It will be placed in the
 * [SavedStateHandle] passed to [DetailsViewModel] under the same key by Hilt when it creates that
 * [ViewModel].
 *
 * @param context the [Context] of [MainActivity].
 * @param item the [ExploreModel] displayed by the `ExploreItem` that was clicked (see the file
 * ExploreSection.kt)
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
     * Called when the activity is starting. First we call our super's implementation of `onCreate`.
     * We then call the [WindowCompat.setDecorFitsSystemWindows] method with `false` so that the
     * framework will not fit the content view to the insets and will just pass through the
     * [WindowInsetsCompat] to the content view. Finally we call the [setContent] method to have
     * it compose the composable lambda it is passed into our activity. The content will become the
     * root view of our activity. That lambda uses our [CraneTheme] custom [MaterialTheme] to wrap
     * a [Surface] whose `content` is a [DetailsScreen] whose `onErrorLoading` argument is a lambda
     * which calls [finish] to close this activity, and whose `modifier` argument uses
     * [Modifier.systemBarsPadding] to add padding to accommodate the system bars insets, and
     * [Modifier.navigationBarsPadding] to add padding to accommodate the navigation bars insets.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CraneTheme {
                Surface {
                    DetailsScreen(
                        onErrorLoading = { finish() },
                        modifier = Modifier.systemBarsPadding()
                    )
                }
            }
        }
    }
}

/**
 * This data class it used by [DetailsScreen] to determine whether it is currently "loading" the
 * [ExploreModel] it needs ([isLoading] is `true`) in which case it should display a
 * [CircularProgressIndicator]; done loading ([cityDetails] is not `null`) in which it displays
 * the [ExploreModel] in a [DetailsContent] Composable; or if [cityDetails] is still `null` but
 * [isLoading] is `false` it will call its `onErrorLoading` lambda parameter. [DetailsScreen] uses
 * the [produceState] delegate to construct the [DetailsUiState] it uses which returns an observable
 * snapshot State that produces values over time without a defined data source with the initial value
 * of a [DetailsUiState] whose [isLoading] field is `true` and in the suspend lambda of [produceState]
 * it fetches a [Result] of [ExploreModel] from the [DetailsViewModel.cityDetails] property and then
 * assigns to the [DetailsUiState] `value` either a [DetailsUiState] whose [cityDetails] field is
 * the [Result.Success.data] of the [Result] if the [Result] was a [Result.Success] or else a
 * [DetailsUiState] whose [throwError] field is `true`.
 *
 * @param cityDetails the [ExploreModel] fetched from the [DetailsViewModel.cityDetails] property or
 * `null` if none was found.
 * @param isLoading `true` if we are waiting for [DetailsViewModel.cityDetails] to return a [Result]
 * @param throwError `true` is no [ExploreModel] was found by [DetailsViewModel.cityDetails].
 */
data class DetailsUiState(
    val cityDetails: ExploreModel? = null,
    val isLoading: Boolean = false,
    val throwError: Boolean = false
)

/**
 * This is the main screen of [DetailsActivity] and is composed into the activity by the [setContent]
 * method in the [ComponentActivity.onCreate] override of [DetailsActivity]. It uses a [DetailsUiState]
 * observable snapshot State that is created by the [produceState] method (which it stores in its
 * varible `val uiState`) to decide what it needs to do during the current recomposition:
 *  - If the [DetailsUiState.cityDetails] field is not `null` then [produceState] has succeeded in
 *  retrieving a [Result] of [ExploreModel] from the [DetailsViewModel.cityDetails] property of our
 *  [viewModel] and constructed a [DetailsUiState] whose [DetailsUiState.cityDetails] is the
 *  [ExploreModel] returned in a [Result.Success] so it calls the [DetailsContent] Composable with
 *  the `exploreModel` argument the [DetailsUiState.cityDetails] of `uiState` and the `modifier`
 *  argument our [modifier] parameter with a [Modifier.fillMaxSize] added on to have [DetailsContent]
 *  fill its incoming measurement constraints.
 *  - If the [DetailsUiState.isLoading] field of `uiState` is `true` [produceState] is still trying
 *  to retrieve a [Result] from [DetailsViewModel.cityDetails] property of our [viewModel] so we
 *  call [Box] with its `modifier` argument our [modifier] parameter with a [Modifier.fillMaxSize]
 *  added on to have [Box] fill its incoming measurement constraints, and for its `content` we
 *  call the [CircularProgressIndicator] Composable with its `color` argument the `onSurface` [Color]
 *  of [MaterialTheme.colors] (which in our [CraneTheme] is [Color.White]).
 *  - For any other condition we call our [onErrorLoading] lambda parameter.
 *
 * @param onErrorLoading a lambda to call if we are unable to retrieve an [ExploreModel] instance to
 * display from the [DetailsViewModel.cityDetails] property of our [viewModel]. Our caller (the
 * [setContent] method called in the [ComponentActivity.onCreate] override of [DetailsActivity])
 * passes us a lambda which calls [ComponentActivity.finish] to close the activity.
 * @param modifier a [Modifier] instance which our caller can use to modify our appearance and/or
 * behavior. Our caller uses a [Modifier.systemBarsPadding] (Adds padding to accommodate the system
 * bars insets) to which it adds a [Modifier.navigationBarsPadding] (adds padding to accommodate the
 * navigation bars insets).
 * @param viewModel the [DetailsViewModel] we should use. Our caller does not pass any, so we use
 * the [viewModel] default value of the parameter, which is the [DetailsViewModel] injected by Hilt.
 */
@Composable
fun DetailsScreen(
    onErrorLoading: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = viewModel()
) {
    // TODO Codelab: produceState step - Show loading screen while fetching city details DONE
    val uiState: DetailsUiState by produceState(initialValue = DetailsUiState(isLoading = true)) {
        val cityDetailsResult: Result<ExploreModel> = viewModel.cityDetails
        value = if (cityDetailsResult is Result.Success<ExploreModel>) {
            DetailsUiState(cityDetails = cityDetailsResult.data)
        } else {
            DetailsUiState(throwError = true)
        }
    }

    when {
        uiState.cityDetails != null -> {
            @Suppress("ReplaceNotNullAssertionWithElvisReturn") // Returning would only hide error
            DetailsContent(exploreModel = uiState.cityDetails!!, modifier = modifier.fillMaxSize())
        }

        uiState.isLoading -> {
            Box(modifier = modifier.fillMaxSize()) {
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

/**
 * This Composable displays the information contained in its [ExploreModel] parameter [exploreModel].
 * Our root Composable is a [Column], whose `modifier` argument is our [Modifier] parameter [modifier],
 * and whose `verticalArrangement` argument is [Arrangement.Center] which causes the [Column] to place
 * its children such that they are as close as possible to the middle of the main axis. The `content`
 * of the [Column] is:
 *  - a [Spacer] whose `modifier` argument specifies a height of 32.dp
 *  - a [Text] whose `modifier` argument is a `ColumnScope` `Modifier.align` whose `alignment` argument
 *  of [Alignment.CenterHorizontally] causes it to be centered in the [Column], whose `text` argument
 *  is the [City.nameToDisplay] field of the [ExploreModel.city] field of our [exploreModel] parameter,
 *  whose `style` parameter is the `h4` [TextStyle] of [MaterialTheme.typography] (our [CraneTheme]
 *  defines this to be the `craneFontFamily` [FontFamily] with a [FontWeight] of [FontWeight.W600]
 *  and a `fontSize` of 34.sp, which is the [Font] with resource ID [R.font.raleway_semibold] see the
 *  file ui/Typography.kt), and the `textAlign` argument of the [Text] is [TextAlign.Center] which
 *  aligns the text in the center of the container.
 *  - a [Text] whose `modifier` argument is a `ColumnScope` `Modifier.align` whose `alignment` argument
 *  of [Alignment.CenterHorizontally] causes it to be centered in the [Column], whose `text` argument
 *  is the [ExploreModel.description] field of our [exploreModel] parameter, whose `style` parameter
 *  is the `h6` [TextStyle] of [MaterialTheme.typography] (our [CraneTheme] defines this to be the
 *  `craneFontFamily` [FontFamily] with a [FontWeight] of [FontWeight.W400] and a `fontSize` of 20.sp,
 *  which is the [Font] with resource ID [R.font.raleway_regular] see the file ui/Typography.kt), and
 *  the `textAlign` argument of the [Text] is [TextAlign.Center] which aligns the text in the center
 *  of the container.
 *  - a [Spacer] whose `modifier` argument specifies a height of 16.dp
 *  - a [CityMapView] Composable whose `latitude` argument is the [City.latitude] field of the
 *  [ExploreModel.city] field of our parameter [exploreModel], and whose `longitude` argument is the
 *  [City.longitude] field of the [ExploreModel.city] field of our parameter [exploreModel] which
 *  will create a [MapView] and cause a [GoogleMap] at that `latitude` and `longitude` to be loaded
 *  into it.
 *
 * @param exploreModel the [ExploreModel] whose information we are supposed to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [DetailsScreen] caller adds a [Modifier.fillMaxSize] to its own `modifier` parameter,
 * which is the [Modifier.systemBarsPadding] chained to a [Modifier.navigationBarsPadding] that is
 * passed to [DetailsScreen] when it is called by the [ComponentActivity.setContent] method in the
 * [DetailsActivity.onCreate] override of [ComponentActivity.onCreate] in [DetailsActivity].
 */
@Composable
fun DetailsContent(
    exploreModel: ExploreModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            text = exploreModel.city.nameToDisplay,
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            text = exploreModel.description,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        CityMapView(latitude = exploreModel.city.latitude, longitude = exploreModel.city.longitude)
    }
}

/**
 * This Composable exists to handle the lifecycle of a [MapView] which it passes to [MapViewContainer]
 * as its `map` argument to have it load a [GoogleMap] into it. The updates to the [MapView] needed
 * when the "Zoom" buttons are clicked are handled by the [MapViewContainer] so that when an update
 * to the [MapView] happens, this composable won't recompose and the [MapView] won't need to be recreated.
 * We use our [rememberMapViewWithLifecycle] method to create and "remember" a [MapView] which we use
 * to initialize our variable `val mapView`. Then we call the [MapViewContainer] Composable with
 * `mapView` as its `map` [MapView] argument, our [String] parameter [latitude] as its `latitude`
 * argument, and our [String] parameter [longitude] as its `longitude` argument.
 *
 * @param latitude the [City.latitude] location of the city whose map we are to show.
 * @param longitude the [City.longitude] location of the city whose map we are to show.
 */
@Composable
private fun CityMapView(latitude: String, longitude: String) {
    // The MapView lifecycle is handled by this composable. As the MapView also needs to be updated
    // with input from Compose UI, those updates are encapsulated into the MapViewContainer
    // composable. In this way, when an update to the MapView happens, this composable won't
    // recompose and the MapView won't need to be recreated.
    val mapView: MapView = rememberMapViewWithLifecycle()
    MapViewContainer(map = mapView, latitude = latitude, longitude = longitude)
}

/**
 * This Composable holds an [AndroidView] that displays its [MapView] parameter [map] and the [Row]
 * of two [ZoomButton]'s rendered by the [ZoomControls] Composable. It starts by "remembering"
 * in its [LatLng] variable `val cameraPosition` the [LatLng] constructed from the double values
 * of its [String] parameters [latitude] and [longitude] using [latitude] and [longitude] as the
 * `key1` and `key2 arguments of remember (this will remember the value returned by its lambda
 * argument if `key1` and `key2` are equal to the previous composition, otherwise it will produce
 * and remember a new value by re-executing the lambda). Then it calls [LaunchedEffect] with [map]
 * as its `key1` (When [LaunchedEffect] enters the composition it will launch its lambda block into
 * the composition's [CoroutineContext]. The coroutine will be cancelled and re-launched when
 * [LaunchedEffect] is recomposed with a different `key1`. The coroutine will be cancelled when the
 * [LaunchedEffect] leaves the composition). The lambda `block` of [LaunchedEffect] initializes its
 * [GoogleMap] variable `val googleMap` to the [GoogleMap] that the [MapView.awaitMap] suspend function
 * of [map] returns. When resumed it calls the [GoogleMap.addMarker] method of `googleMap` with the
 * [MarkerOptions] returned by the [MarkerOptions.position] method when called with the [LatLng]
 * variable `cameraPosition`. The lambda then calls the [GoogleMap.moveCamera] method of `googleMap`
 * with the [CameraUpdate] returned by the [CameraUpdateFactory.newLatLng] method for `cameraPosition`.
 *
 * Next we call [rememberSaveable] with [map] as its `inputs` argument (when it changes it will cause
 * the state to reset and `init` lambda to be rerun) to initialize our [Float] variable `var zoom`
 * to a [MutableState] whose initial value is [InitialZoom] (5f). Then we call the [ZoomControls]
 * Composable with `zoom` as its `zoom` argument, and as its `onZoomChanged` lambda a lambda which
 * sets `zoom` to the [Float] argument of the lambda after "coercing" it to be between [MinZoom] (2f)
 * and [MaxZoom] (20f).
 *
 * We initialize and remember our [CoroutineScope] variable `val coroutineScope` to the [CoroutineScope]
 * bound to this point in the composition that [rememberCoroutineScope] returns (the same [CoroutineScope]
 * instance will be returned across recompositions).
 *
 * Finally we call the [AndroidView] Composable with a lambda returning our [MapView] parameter [map]
 * as its `factory` argument. The `update` lambda then has it as its [MapView] argument `mapView`.
 * the lambda sets its [Float] varible `val mapZoom` so that the [AndroidView] recomposes when `zoom`
 * changes, then it launches a new coroutine (without blocking the current thread) using [CoroutineScope]
 * `coroutineScope` and then in the lambda `block` of the launched coroutine it initializes its
 * [GoogleMap] variable `val googleMap` to the [GoogleMap] that the [MapView.awaitMap] suspend
 * function of [map] returns. On resuming it calls our [GoogleMap.setZoom] extension function on
 * `googleMap` with `mapZoom` as its `zoom` argument which does the magic needed to "zoom" the
 * [GoogleMap]. Finally the lambda calls the [GoogleMap.moveCamera] method of `googleMap` with the
 * [CameraUpdate] returned by the [CameraUpdateFactory.newLatLng] method for `cameraPosition` (it
 * moves the camera to the same place to trigger the zoom update).
 *
 * @param map the [MapView] instance that we are to have load a [GoogleMap] of the area around our
 * [latitude] and [longitude] parameters then display in an [AndroidView].
 * @param latitude the [String] value of the [latitude] of the location of interest.
 * @param longitude the [String] value of the [longitude] of the location of interest.
 */
@Composable
private fun MapViewContainer(
    map: MapView,
    latitude: String,
    longitude: String
) {
    val cameraPosition: LatLng = remember(latitude, longitude) {
        LatLng(latitude.toDouble(), longitude.toDouble())
    }

    LaunchedEffect(map) {
        val googleMap: GoogleMap = map.awaitMap()
        googleMap.addMarker { position(cameraPosition) }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition))
    }

    var zoom: Float by rememberSaveable(map) { mutableFloatStateOf(InitialZoom) }
    ZoomControls(zoom = zoom) {
        zoom = it.coerceIn(MinZoom, MaxZoom)
    }

    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    AndroidView(factory = { map }) { mapView: MapView ->
        // Reading zoom so that AndroidView recomposes when it changes. The getMapAsync lambda
        // is stored for later, Compose doesn't recognize state reads
        val mapZoom: Float = zoom
        coroutineScope.launch {
            val googleMap: GoogleMap = mapView.awaitMap()
            googleMap.setZoom(zoom = mapZoom)
            // Move camera to the same place to trigger the zoom update
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition))
        }
    }
}

/**
 * This Composable holds two [ZoomButton] Composables in a [Row] which allow the user to decrease or
 * increase the [zoom] that [MapViewContainer] applies to the [GoogleMap] displayed in its [MapView].
 * The `modifier` argument of the [Row] root Composable is a [Modifier.fillMaxWidth] to have its
 * content fill the maximum incoming measurement constraints, and its `horizontalArrangement` argument
 * is [Arrangement.Center] so that its children are placed as close as possible to the middle of the
 * main axis. The `content` of the [Row] is two [ZoomButton] Composables, the `text` argument of the
 * first one is the [String] "-" and its `onClick` argument is a lambda which calls our [onZoomChanged]
 * parameter with our [zoom] parameter multiplied by 0.8f. The `text` argument of the second one is
 * the [String] "+" and its `onClick` argument is a lambda which calls our [onZoomChanged] parameter
 * with our [zoom] parameter multiplied by 1.2f.
 *
 * @param zoom the current value of "zoom" that has been applied to the [GoogleMap] displayed in the
 * [MapView] used by the [MapViewContainer] Composable.
 * @param onZoomChanged a lambda which should be called with a new value for [zoom] when one of our
 * [ZoomButton] Composables is clicked.
 */
@Composable
private fun ZoomControls(
    zoom: Float,
    onZoomChanged: (Float) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        ZoomButton(text = "-", onClick = { onZoomChanged(zoom * 0.8f) })
        ZoomButton(text = "+", onClick = { onZoomChanged(zoom * 1.2f) })
    }
}

/**
 * This Composable holds a [Button] whose label is our [String] parameter [text] and whose `onClick`
 * argument calls our [onClick] lambda when the [Button] is clicked. The `modifier` argument of the
 * [Button] is a [Modifier.padding] that adds 8.dp padding to all sides of the [Button], the `colors`
 * argument is a [ButtonDefaults.buttonColors] whose `backgroundColor` (background color of the Button
 * when it is enabled) is the `onPrimary` [Color] of [MaterialTheme.colors] (our [CraneTheme] custom
 * [MaterialTheme] does not specify one, so the default [Color.White] is used), and whose `contentColor`
 * (content color of the [Button] when enabled) is the `primary` [Color] of [MaterialTheme.colors]
 * (`crane_purple_800` aka `Color(0xFF5D1049)` is specified by our [CraneTheme] custom [MaterialTheme]),
 * and the `onClick` argument of the [Button] is our [onClick] lambda parameter. The `content` of the
 * [Button] is a [Text] whose `text` argument is our [text] parameter, and whose `style` parameter is
 * the `h5` [TextStyle] of [MaterialTheme.typography] which our [CraneTheme] custom [MaterialTheme]
 * specifies in its [craneTypography] custom [Typography] to be the [FontFamily] `craneFontFamily`
 * with a [FontWeight] of [FontWeight.W600] (the [Font] with resource ID [R.font.raleway_semibold])
 * and with a `fontSize` of 24.sp
 *
 * @param text the label of the [Button] (ie. its [Text] `content` displays it).
 * @param onClick a lambda that the [Button] should call when it is clicked.
 */
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
 * The initial value of "zoom" that is applied to the [GoogleMap] contained in the [MapView] of the
 * [MapViewContainer] Composable.
 */
@Suppress("ConstPropertyName") // It is a Compose constant of sorts
private const val InitialZoom = 5f

/**
 * The minimum value of "zoom" that can be applied to the [GoogleMap] contained in the [MapView] of
 * the [MapViewContainer] Composable. The value that the user requests by clicking the [ZoomButton]
 * Composables is "coerced" to be between [MinZoom] and [MaxZoom].
 */
const val MinZoom: Float = 2f

/**
 * The maximum value of "zoom" that can be applied to the [GoogleMap] contained in the [MapView] of
 * the [MapViewContainer] Composable. The value that the user requests by clicking the [ZoomButton]
 * Composables is "coerced" to be between [MinZoom] and [MaxZoom].
 */
const val MaxZoom: Float = 20f
