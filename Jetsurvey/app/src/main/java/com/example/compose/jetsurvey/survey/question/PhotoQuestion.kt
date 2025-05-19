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

package com.example.compose.jetsurvey.survey.question

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.QuestionWrapper
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import androidx.core.net.toUri

/**
 * A composable function that displays a photo question. It allows the user to take a photo.
 * We start by initializing our [Boolean] variable `hasPhoto` to `true` if our [Uri] parameter
 * [imageUri] is not `null`, then we initialize our [ImageVector] variable `iconResource` with
 * the [ImageVector] drawn bu [Icons.Filled.SwapHoriz] if `hasPhoto` is `true`, or the [ImageVector]
 * drawn by [Icons.Filled.AddAPhoto] if it is `false`. Next we initialize our [Uri] variable
 * `newImageUri` with the [Uri] returned by our lambda parameter [getNewImageUri]. We initialize and
 * remember our [ManagedActivityResultLauncher] variable `cameraLauncher` using the
 * [rememberLauncherForActivityResult] method for the `contract` argument of
 * [ActivityResultContracts.TakePicture] (An [ActivityResultContract] that takes a picture saving it
 * into the [Uri] passed the [ManagedActivityResultLauncher.launch] method as its `input` argument),
 * and the `onResult` argument is a lambda that calls our lambda parameter [onPhotoTaken] with the
 * [Uri] passed the [ManagedActivityResultLauncher.launch] method as its `input` argument if the
 * [Boolean] passed the lambda in variable `success` is `true`.
 *
 * Our root composable is a [QuestionWrapper] whose `titleResourceId` argument is our [Int] parameter
 * [titleResourceId], and whose `modifier` argument is our [Modifier] parameter [modifier]. Inside the
 * `content` composable lambda argument of the [QuestionWrapper] we compose a [OutlinedButton] whose
 * arguments are:
 *  - `onClick`: is a lambda that sets [Uri] variable `newImageUri` to the [Uri] returned by our
 *  lambda parameter [getNewImageUri], and then calls the [ManagedActivityResultLauncher.launch]
 *  method of our [ManagedActivityResultLauncher] variable `cameraLauncher` with the [Uri] variable
 *  `newImageUri` as its `input` argument.
 *  - `shape`: is the [Shapes.small] of our custom [MaterialTheme.shapes].
 *  TODO: Continue here.
 *
 * @param titleResourceId The resource ID of the question title.
 * @param imageUri The URI of the currently selected image, or null if no image is selected.
 * @param getNewImageUri A function that returns a new URI for storing a captured photo.
 * @param onPhotoTaken A callback that is invoked when a photo is taken or selected.
 * It provides the URI of the captured or selected photo.
 * @param modifier The modifier to apply to this composable.
 */
@Composable
fun PhotoQuestion(
    @StringRes titleResourceId: Int,
    imageUri: Uri?,
    getNewImageUri: () -> Uri,
    onPhotoTaken: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasPhoto: Boolean = imageUri != null
    val iconResource: ImageVector = if (hasPhoto) {
        Icons.Filled.SwapHoriz
    } else {
        Icons.Filled.AddAPhoto
    }
    var newImageUri: Uri? = getNewImageUri()

    val cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success: Boolean ->
                if (success) {
                    onPhotoTaken(newImageUri!!)
                }
            }
        )

    QuestionWrapper(
        titleResourceId = titleResourceId,
        modifier = modifier,
    ) {

        OutlinedButton(
            onClick = {
                newImageUri = getNewImageUri()
                cameraLauncher.launch(input = newImageUri)
            },
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues()
        ) {
            Column {
                if (hasPhoto) {
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(data = imageUri)
                            .crossfade(enable = true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 96.dp)
                            .aspectRatio(ratio = 4 / 3f)
                    )
                } else {
                    PhotoDefaultImage(
                        modifier = Modifier.padding(
                            horizontal = 86.dp,
                            vertical = 74.dp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.BottomCenter)
                        .padding(vertical = 26.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = iconResource, contentDescription = null)
                    Spacer(modifier = Modifier.width(width = 8.dp))
                    Text(
                        text = stringResource(
                            id = if (hasPhoto) {
                                R.string.retake_photo
                            } else {
                                R.string.add_photo
                            }
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoDefaultImage(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId: Int = if (lightTheme) {
        R.drawable.ic_selfie_light
    } else {
        R.drawable.ic_selfie_dark
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
}

/**
 * Two previews of the [PhotoQuestion] composable:
 *  - One with the light theme
 *  - One with the dark theme
 */
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PhotoQuestionPreview() {
    JetsurveyTheme {
        Surface {
            PhotoQuestion(
                titleResourceId = R.string.selfie_skills,
                imageUri = "https://example.bogus/wow".toUri(),
                getNewImageUri = { Uri.EMPTY },
                onPhotoTaken = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
