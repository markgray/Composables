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

package com.google.samples.apps.nowinandroid.core.datastore

import androidx.datastore.core.CorruptionException
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

/**
 * Unit tests for [UserPreferencesSerializer].
 */
class UserPreferencesSerializerTest {
    /**
     * Serializer instance for [UserPreferences] that we will test.
     */
    private val userPreferencesSerializer = UserPreferencesSerializer()

    /**
     * Tests that the default value of the [UserPreferencesSerializer] is an empty [UserPreferences]
     * object.
     */
    @Test
    fun defaultUserPreferences_isEmpty() {
        assertEquals(
            expected = userPreferences {
                // Default value
            },
            actual = userPreferencesSerializer.defaultValue,
        )
    }

    /**
     * Tests that the [UserPreferencesSerializer] can write and read [UserPreferences] from a
     * [ByteArrayOutputStream] and [ByteArrayInputStream] respectively.
     *
     * We start by initializing our [UserPreferences] variable `userPreferences` with a new instance
     * whose [Map] of [String] to [Boolean] property [UserPreferences.followedTopicIds] contains
     * two members `0` and `1` with values `true`. We initialize our [ByteArrayOutputStream] variable
     * `outputStream` with a new instance and call the `writeTo` method of [UserPreferences] variable
     * `userPreferences` with `outputStream` as its `output`  argument. We then initialize our
     * [ByteArrayInputStream] variable `inputStream` with the `toByteArray` method of
     * [ByteArrayOutputStream] variable `outputStream` as its `buf` argument. We initialize our
     * [UserPreferences] variable `actualUserPreferences` with the `readFrom` method of
     * [UserPreferencesSerializer] variable `userPreferencesSerializer` with `inputStream` as its
     * `input` argument. Finally, we assert that the [UserPreferences] variable `actualUserPreferences`
     * is equal to the [UserPreferences] variable `expectedUserPreferences` with the `assertEquals`
     * method.
     */
    @Test
    fun writingAndReadingUserPreferences_outputsCorrectValue(): TestResult = runTest {
        val expectedUserPreferences: UserPreferences = userPreferences {
            followedTopicIds.put("0", true)
            followedTopicIds.put("1", true)
        }

        val outputStream = ByteArrayOutputStream()

        expectedUserPreferences.writeTo(outputStream)

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())

        val actualUserPreferences: UserPreferences =
            userPreferencesSerializer.readFrom(input = inputStream)

        assertEquals(
            expected = expectedUserPreferences,
            actual = actualUserPreferences,
        )
    }

    /**
     * Tests that a [CorruptionException] is thrown when the [UserPreferencesSerializer] attempts
     * to read an invalid [UserPreferences] from a [ByteArrayInputStream].
     *
     * We initialize our [ByteArrayInputStream] variable `inputStream` with a new instance whose
     * `buf` argument is a [ByteArray] consisting of a single byte with the value `0`. We then
     * call the `readFrom` method of our [UserPreferencesSerializer] field [userPreferencesSerializer]
     * with `input` = `inputStream` which should throw a [CorruptionException] since the `input`
     * is not a valid [UserPreferences] object.
     */
    @Test(expected = CorruptionException::class)
    fun readingInvalidUserPreferences_throwsCorruptionException(): TestResult = runTest {
        userPreferencesSerializer.readFrom(input = ByteArrayInputStream(byteArrayOf(0)))
    }
}
