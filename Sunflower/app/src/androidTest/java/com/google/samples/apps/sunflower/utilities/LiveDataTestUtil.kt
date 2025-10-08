/*
 * Copyright 2018 Google LLC
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

package com.google.samples.apps.sunflower.utilities

import androidx.lifecycle.LiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 *
 * This is a helper method for testing LiveData objects, adapted from
 * https://github.com/googlesamples/android-architecture-components.
 *
 * It observes the [LiveData] until it receives a value, then it stops observing.
 * If the [LiveData] doesn't receive a value within 2 seconds, the test will likely fail
 * because the returned value will be `null`.
 *
 * @param T The type of data held by the [LiveData].
 * @param liveData The [LiveData] to get the value from.
 * @return The value of the [LiveData].
 * @throws InterruptedException If the thread is interrupted while waiting.
 */
@Throws(InterruptedException::class)
fun <T> getValue(liveData: LiveData<T>): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    liveData.observeForever { o: T? ->
        data[0] = o
        latch.countDown()
    }
    latch.await(2, TimeUnit.SECONDS)

    @Suppress("UNCHECKED_CAST")
    return data[0] as T
}
