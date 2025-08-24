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

import android.content.Context
import android.content.res.Configuration
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.widget.TextView
import android.content.res.Resources
import android.text.Spanned
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import com.google.accompanist.themeadapter.material.MdcTheme
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel
import androidx.compose.ui.platform.LocalResources

/**
 * This Composable exists in order to "hoist" the [State] wrapped [PlantDetailViewModel.plant] field
 * of our [plantDetailViewModel] parameter. This makes the [PlantDetailContent] which displays the
 * description of the [Plant] stateless. We start by initializing our [Plant] variable `val plant`
 * by the [State] wrapped [Plant] that the [observeAsState] method returns when we use it to observe
 * the [LiveData] wrapped [Plant] field [PlantDetailViewModel.plant] of our [plantDetailViewModel]
 * parameter. Then if `plant` is not `null` we call our [PlantDetailContent] Composable with `it` as
 * its `plant` argument.
 *
 * @param plantDetailViewModel the [PlantDetailViewModel]
 */
@Composable
fun PlantDetailDescription(plantDetailViewModel: PlantDetailViewModel) {
    // Observes values coming from the VM's LiveData<Plant> field as State<Plant?>
    val plant: Plant? by plantDetailViewModel.plant.observeAsState()

    // New emissions from plant will make PlantDetailDescription recompose as the state's read here
    plant?.let {
        // If plant is not null, display the content
        PlantDetailContent(it)
    }
}

/**
 * This Composable displays several fields from its [Plant] parameter [plant]. It wraps a [Column]
 * in a [Surface], with the [Column] using the [dimensionResource] method to fetch the value it uses
 * for its `modifier` argument (a [Modifier.padding] on all sides) from the resource value stored
 * under the ID `R.dimen.margin_normal` in the app's resources. The `content` of the [Column] is
 * three Composables:
 *  - [PlantName] to display the [Plant.name] field of our [Plant] parameter [plant].
 *  - [PlantWatering] to display the [Plant.wateringInterval] field of our [Plant] parameter [plant].
 *  - [PlantDescription] to display the [Plant.description] field of our [Plant] parameter [plant].
 *
 * @param plant the [Plant] whose fields we are to display.
 */
@Composable
fun PlantDetailContent(plant: Plant) {
    Surface {
        Column(Modifier.padding(all = dimensionResource(R.dimen.margin_normal))) {
            PlantName(plant.name)
            PlantWatering(plant.wateringInterval)
            PlantDescription(plant.description)
        }
    }
}

/**
 * This Composable displays its [String] parameter [name] in a [Text]. The `text` argument of the
 * [Text] is our parameter [name], and its `style` argument is the [TextStyle] to be found in
 * [MaterialTheme.typography] for the `h5` typeface (since we do not override the [MaterialTheme]
 * this will be Rotboto Regular, with a font size of 24sp). Its `modifier` argument is a
 * [Modifier.fillMaxWidth] to have it take all of the horizontal space of its incoming constraints,
 * to which is chained a [Modifier.padding] whose `horizontal` padding (the padding on each side of
 * the [Text] is the value returned by the [dimensionResource] method for the `R.dimen.margin_small`
 * resource ID, and at the end of the chain is a [Modifier.wrapContentWidth] whose `align` argument
 * is [Alignment.CenterHorizontally] to align the `text` displayed about the centerline of the [Text].
 *
 * @param name the [String] we are to display in our [Text]. It comes from the [Plant.name] field of
 * the [Plant] instance passed our [PlantDetailContent] caller as its parameter.
 */
@Composable
private fun PlantName(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_small))
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    )
}

/**
 * This Composable displays its [Int] parameter [wateringInterval] in a [Text], formatted with other
 * text intended to indicate that it represents the "watering interval" in days of the [Plant] in
 * question. Our root Composable is a [Column] whose `modifier` argument is a [Modifier.fillMaxWidth]
 * causing it to occupy its entire incoming width constraint. In its `content` we initialize our
 * [Modifier] variable `val centerWithPaddingModifier` with a [Modifier.padding] whose `horizontal`
 * padding is taken from the [dimensionResource] whose resource ID is `R.dimen.margin_small` (8.dp),
 * to which is chained a [ColumnScope] `Modifier.align` whose `alignment` argument is
 * [Alignment.CenterHorizontally] to have the "modified" Composable center its children horizontally
 * about its centerline (this [Modifier] is then used by both of the [Text]'s in the [Column]). Next
 * we initialize our [Dp] variable `val normalPadding` to the [dimensionResource] whose resource ID
 * is `R.dimen.margin_normal` (16.dp).
 *
 * Next we have a [Text] Composable displaying as its `text` argument the [String] whose resource ID
 * is `R.string.watering_needs_prefix` ("Watering needs"), with its `color` argument the [Color] that
 * is specified for [Colors.primaryVariant] in the default [MaterialTheme.colors] (we use [MdcTheme]
 * as our custom [MaterialTheme] which parses our `R.style.Base_Theme_Sunflower` and uses the [Color]
 * whose resource ID is `R.color.sunflower_green_700` (0x005d2b) for light mode and the [Color] whose
 * resource ID is `R.color.sunflower_green_200` (0x81ca9d) for night mode), for its `fontWeight`
 * argument we use [FontWeight.Bold], and for its `modifier` argument we chain a [Modifier.padding]
 * to our [Modifier] variable `centerWithPaddingModifier` that sets its `top` padding to our [Dp]
 * variable `normalPadding` (16dp).
 *
 * Next we initialize our [String] variable `val wateringIntervalText` to the [String] returned by
 * the current [Resources.getQuantityString] method using the String plural format specified by the
 * resource ID `R.plurals.watering_needs_suffix` and applying it to our parameter [wateringInterval].
 * We then use `wateringIntervalText` as the `text` argument of a [Text] and as its `modifier`
 * argument we chain a [Modifier.padding] to our [Modifier] variable `centerWithPaddingModifier`
 * that sets its `bottom` padding to our [Dp] variable `normalPadding` (16dp).
 *
 * @param wateringInterval the [String] we are to display in our [Text]. It comes from the
 * [Plant.wateringInterval] field of the [Plant] instance passed our [PlantDetailContent] caller
 * as its parameter.
 */
@Composable
private fun PlantWatering(wateringInterval: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Same modifier used by both Texts
        val centerWithPaddingModifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.margin_small))
            .align(alignment = Alignment.CenterHorizontally)

        val normalPadding: Dp = dimensionResource(R.dimen.margin_normal)

        Text(
            text = stringResource(R.string.watering_needs_prefix),
            color = MaterialTheme.colors.primaryVariant,
            fontWeight = FontWeight.Bold,
            modifier = centerWithPaddingModifier.padding(top = normalPadding)
        )

        val wateringIntervalText: String = LocalResources.current.getQuantityString(
            R.plurals.watering_needs_suffix, wateringInterval, wateringInterval
        )
        Text(
            text = wateringIntervalText,
            modifier = centerWithPaddingModifier.padding(bottom = normalPadding)
        )
    }
}

/**
 * This Composable uses [HtmlCompat.fromHtml] to convert its [String] parameter [description] from
 * HTML formatted text to a [Spanned] which it then displays in a [TextView] inside an [AndroidView].
 * First we initialize and remember our [Spanned] variable `val htmlDescription` using our [String]
 * parameter [description] as the `key1` argument of [remember] and with the `calculation` lambda
 * returning the [Spanned] that the [HtmlCompat.fromHtml] method produces from [description], with
 * the `flags` argument [HtmlCompat.FROM_HTML_MODE_COMPACT] (separates block-level elements with
 * line breaks (single newline character) in between). Our Composable content is an [AndroidView]
 * whose `factory` argument is a lambda constructs a [TextView] using the [Context] parameter passed
 * the lambda and then uses the [apply] extension function to set its [TextView.setMovementMethod]
 * property to the [MovementMethod] returned by the [LinkMovementMethod.getInstance] method (movement
 * method that traverses links in the text buffer and scrolls if necessary, supports clicking on
 * links with DPad Center or Enter). The `update` argument (callback to be invoked after the layout
 * is inflated, and whenever the [AndroidView] is recomposed) sets the `text` of the [TextView] to
 * our [Spanned] variable `htmlDescription`.
 *
 * @param description a HTML formatted [String] that contains the [Plant.description] field of the
 * [Plant] being displayed.
 */
@Composable
private fun PlantDescription(description: String) {
    // Remembers the HTML formatted description. Re-executes on a new description
    val htmlDescription: Spanned = remember(key1 = description) {
        HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    // Displays the TextView on the screen and updates with the HTML description when inflated
    // Updates to htmlDescription will make AndroidView recompose and update the text
    AndroidView(
        factory = { context: Context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = {
            it.text = htmlDescription
        }
    )
}

/**
 * This is a Preview of the [PlantDetailContent] Composable for a [Plant] that is constucted with
 * some dummy data.
 */
@Preview
@Composable
private fun PlantDetailContentPreview() {
    val plant = Plant(
        plantId = "id",
        name = "Apple",
        description = "HTML<br><br>description",
        growZoneNumber = 3,
        wateringInterval = 30,
        imageUrl = ""
    )
    MdcTheme {
        PlantDetailContent(plant)
    }
}

/**
 * This is a Preview of the [PlantDetailContent] Composable for a [Plant] that is constucted with
 * some dummy data with the `uiMode` argument to `@Preview` [Configuration.UI_MODE_NIGHT_YES] in
 * order to see what it looks like in night mode.
 */
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PlantDetailContentDarkPreview() {
    val plant = Plant(
        plantId = "id",
        name = "Apple",
        description = "HTML<br><br>description",
        growZoneNumber = 3,
        wateringInterval = 30,
        imageUrl = ""
    )
    MdcTheme {
        PlantDetailContent(plant)
    }
}

/**
 * This is a Preview of our [PlantName] Composable.
 */
@Preview
@Composable
private fun PlantNamePreview() {
    MdcTheme {
        PlantName("Apple")
    }
}

/**
 * This is a Preview of our [PlantWatering] Composable.
 */
@Preview
@Composable
private fun PlantWateringPreview() {
    MdcTheme {
        PlantWatering(7)
    }
}

/**
 * This is a Preview of our [PlantDescription] Composable.
 */
@Preview
@Composable
private fun PlantDescriptionPreview() {
    MdcTheme {
        PlantDescription("HTML<br><br>description")
    }
}
