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

@file:Suppress("TestFunctionName")

package com.google.samples.apps.nowinandroid.core.result

import app.cash.turbine.test
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [asResult].
 */
class ResultKtTest {

    /**
     * Tests that the [asResult] extension function correctly catches errors emitted by the flow
     * and wraps them in a [Result.Error] object.
     *
     * The test creates a flow that emits a value and then throws an exception.
     * It then uses the [asResult] extension function to convert the flow into a flow of [Result] objects.
     * Finally, it uses the `test` extension function from the Turbine library to assert that the
     * resulting flow emits the expected [Result] objects in the correct order:
     *  1. [Result.Loading]
     *  2. [Result.Success] with the emitted value
     *  3. [Result.Error] with the thrown exception
     *
     * TODO: Continue here.
     */
    @Test
    fun Result_catches_errors(): TestResult = runTest {
        flow {
            emit(value = 1)
            throw Exception("Test Done")
        }
            .asResult()
            .test {
                assertEquals(expected = Result.Loading, actual = awaitItem())
                assertEquals(expected = Result.Success(1), actual = awaitItem())

                when (val errorResult: Result<Int> = awaitItem()) {
                    is Result.Error -> assertEquals(
                        expected = "Test Done",
                        actual = errorResult.exception.message,
                    )

                    Result.Loading,
                    is Result.Success,
                        -> throw IllegalStateException(
                        "The flow should have emitted an Error Result",
                    )
                }

                awaitComplete()
            }
    }
}
