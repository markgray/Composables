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

package com.example.compose.rally

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import java.io.FileOutputStream

/**
 * Simple on-device screenshot comparator that uses golden images present in
 * `androidTest/assets`. It's used to showcase the `AnimationClockTestRule` used in
 * [AnimatingCircleTests].
 *
 * Minimum SDK is O (Sdk 26). Densities between devices must match.
 *
 * Screenshots are saved on device in `/data/data/{package}/files`.
 *
 * @param goldenName The name of the golden image to compare against.
 * @param node The node to capture and compare.
 */
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
fun assertScreenshotMatchesGolden(
    goldenName: String,
    node: SemanticsNodeInteraction
) {
    val bitmap: Bitmap = node.captureToImage().asAndroidBitmap()

    // Save screenshot to file for debugging
    saveScreenshot(goldenName + System.currentTimeMillis().toString(), bitmap)
    val golden: Bitmap = InstrumentationRegistry.getInstrumentation()
        .context.resources.assets.open("$goldenName.png").use { BitmapFactory.decodeStream(it) }

    golden.compare(bitmap)
}

/**
 * Saves the bitmap to the external storage directory for debugging purposes.
 *
 * @param filename The name of the file to save the bitmap to.
 * @param bmp The bitmap to save.
 */
private fun saveScreenshot(filename: String, bmp: Bitmap) {
    val path = InstrumentationRegistry.getInstrumentation().targetContext.filesDir.canonicalPath
    FileOutputStream("$path/$filename.png").use { out ->
        bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    @Suppress("ReplacePrintlnWithLogging")
    println("Saved screenshot to $path/$filename.png")
}

/**
 * Compares two bitmaps pixel by pixel.
 *
 * @param other The bitmap to compare against.
 * @throws AssertionError if the bitmaps are not the same.
 */
private fun Bitmap.compare(other: Bitmap) {
    if (this.width != other.width || this.height != other.height) {
        throw AssertionError("Size of screenshot does not match golden file (check device density)")
    }
    // Compare row by row to save memory on device
    val row1 = IntArray(width)
    val row2 = IntArray(width)
    for (column in 0 until height) {
        // Read one row per bitmap and compare
        this.getRow(row1, column)
        other.getRow(row2, column)
        if (!row1.contentEquals(row2)) {
            throw AssertionError("Sizes match but bitmap content has differences")
        }
    }
}

/**
 * Gets a specific row of pixels from the bitmap.
 *
 * @param pixels The array to store the pixels in.
 * @param column The row number to get pixels from.
 */
private fun Bitmap.getRow(pixels: IntArray, column: Int) {
    this.getPixels(pixels, 0, width, 0, column, width, 1)
}
