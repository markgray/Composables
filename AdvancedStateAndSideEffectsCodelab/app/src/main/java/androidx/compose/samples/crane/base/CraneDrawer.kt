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

@file:Suppress("UnusedImport")

package androidx.compose.samples.crane.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.samples.crane.home.CraneHome
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight

/**
 * The list of titles for entries in our [CraneDrawer].
 */
private val screens = listOf("Find Trips", "My Trips", "Saved Trips", "Price Alerts", "My Account")

/**
 * This is the content of the Drawer sheet that can be pulled from the left side (right for RTL) of
 * the [Scaffold] in our [CraneHome] Composable. Our root Composable is a [Column] whose `modifier`
 * argument adds a [Modifier.fillMaxSize] to our [modifier] parameter to have its content fill its
 * incoming measurement constraints, and adds a [Modifier.padding] to that to add 24.dp padding to
 * the `start` and 48.dp to the `top` of the [Column]. The content of the [Column] is an [Image]
 * whose `painter` argument is the [Painter] created by [painterResource] for the drawable whose
 * resource ID is `R.drawable.ic_crane_drawer` (a stylized line drawing of a crane), and this is
 * followed by a [Text] widget for each of the strings in the [List] of [String] field [screens],
 * with the text drawn using the `h4` [TextStyle] of [MaterialTheme.typography] (this is defined
 * by our [CraneTheme] to be the [Font] whose resource ID is `R.font.raleway_semibold` (the file
 * raleway_semibold.ttf) with a `fontSize` of 34.sp, and a `fontWeight` of [FontWeight.W600]. There
 * is a 24.dp high [Spacer] above each of the [Text] widgets in the [Column].
 *
 * @param modifier a [Modifier] that our caller can use to modify our appearance or behavior. Our
 * caller does not pass one so the empty, default, or starter [Modifier] that contains no elements
 * is used instead.
 */
@Composable
fun CraneDrawer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 48.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_crane_drawer),
            contentDescription = stringResource(R.string.cd_drawer)
        )
        for (screen in screens) {
            Spacer(modifier = Modifier.height(height = 24.dp))
            Text(text = screen, style = MaterialTheme.typography.h4)
        }
    }
}

/**
 * This is the Preview of our [CraneDrawer] Composable wrapped in our [CraneTheme] custom [MaterialTheme].
 */
@Preview
@Composable
fun CraneDrawerPreview() {
    CraneTheme {
        CraneDrawer()
    }
}
