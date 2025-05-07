/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.baselineprofiles_codelab.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * Displays a placeholder profile screen indicating that the profile feature is under development.
 *
 * This composable shows an image and two text messages, informing the user that the profile
 * section is currently a work in progress and encouraging them to grab a beverage while they wait.
 *
 * Our root composable is a [Column] whose `horizontalAlignment` argument is set to
 * [Alignment.CenterHorizontally] and whose `modifier` argument is our [Modifier] parameter
 * [modifier], with a [Modifier.fillMaxSize] chained to it, a [Modifier.wrapContentSize] chained
 * to that, and a [Modifier.padding] chained to that which add `24.dp` to `all` sides. In its
 * [ColumnScope] `content` composable lambda argument we compose:
 *
 * **First** An [Image] whose `painter` argument is a [painterResource] whose `id` argument is
 * `R.drawable.empty_state_search`, and whose `contentDescription` argument is `null`.
 *
 * **Second** A [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` argument
 * is `24.dp`.
 *
 * **Third** A [Text] whose arguments are:
 *  - `text`: is the [String] whose resource ID is `R.string.work_in_progress` ("This is currently
 *  work in progress").
 *  - `style`: [TextStyle] is the [Typography.subtitle1] of our custom [MaterialTheme.typography].
 *  - `textAlign`: [TextAlign] is [TextAlign.Center].
 *  - `modifier`: [Modifier.fillMaxWidth].
 *
 * **Fourth** A [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` argument
 * is `16.dp`.
 *
 * **Fifth** A [Text] whose arguments are:
 *  - `text`: is the [String] whose resource ID is `R.string.grab_beverage` ("Grab a beverage and
 *  check back later!").
 *  - `style`: [TextStyle] is the [Typography.body2] of our custom [MaterialTheme.typography].
 *  - `textAlign`: [TextAlign] is [TextAlign.Center].
 *  - `modifier`: is a [Modifier.fillMaxWidth].
 *
 * @param modifier Modifier to be applied to the layout. Allows customization of size, padding,
 * and other layout aspects. Defaults to an empty Modifier.
 */
@Composable
fun Profile(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(all = 24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_state_search),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        Text(
            text = stringResource(id = R.string.work_in_progress),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        Text(
            text = stringResource(id = R.string.grab_beverage),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Three Previews of our [Profile] Composable using different configurations:
 *  - "default" - light theme
 *  - "dark theme" - dark theme
 *  - "large font" - large font size
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun ProfilePreview() {
    JetsnackTheme {
        Profile()
    }
}
