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

import android.content.res.Configuration
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import com.google.accompanist.themeadapter.material.MdcTheme
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel

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
 * under the ID [R.dimen.margin_normal] in the app's resources. The `content` of the [Column] is
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
 * the [Text] is the value returned by the [dimensionResource] method for the [R.dimen.margin_small]
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
 * TODO: add kdoc
 */
@Composable
private fun PlantWatering(wateringInterval: Int) {
    Column(Modifier.fillMaxWidth()) {
        // Same modifier used by both Texts
        val centerWithPaddingModifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.margin_small))
            .align(Alignment.CenterHorizontally)

        val normalPadding = dimensionResource(R.dimen.margin_normal)

        Text(
            text = stringResource(R.string.watering_needs_prefix),
            color = MaterialTheme.colors.primaryVariant,
            fontWeight = FontWeight.Bold,
            modifier = centerWithPaddingModifier.padding(top = normalPadding)
        )

        val wateringIntervalText = LocalContext.current.resources.getQuantityString(
            R.plurals.watering_needs_suffix, wateringInterval, wateringInterval
        )
        Text(
            text = wateringIntervalText,
            modifier = centerWithPaddingModifier.padding(bottom = normalPadding)
        )
    }
}

/**
 * TODO: add kdoc
 */
@Composable
private fun PlantDescription(description: String) {
    // Remembers the HTML formatted description. Re-executes on a new description
    val htmlDescription = remember(description) {
        HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    // Displays the TextView on the screen and updates with the HTML description when inflated
    // Updates to htmlDescription will make AndroidView recompose and update the text
    AndroidView(
        factory = { context ->
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
 * TODO: add kdoc
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
 * TODO: add kdoc
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
 * TODO: add kdoc
 */
@Preview
@Composable
private fun PlantNamePreview() {
    MdcTheme {
        PlantName("Apple")
    }
}

/**
 * TODO: add kdoc
 */
@Preview
@Composable
private fun PlantWateringPreview() {
    MdcTheme {
        PlantWatering(7)
    }
}

/**
 * TODO: add kdoc
 */
@Preview
@Composable
private fun PlantDescriptionPreview() {
    MdcTheme {
        PlantDescription("HTML<br><br>description")
    }
}
