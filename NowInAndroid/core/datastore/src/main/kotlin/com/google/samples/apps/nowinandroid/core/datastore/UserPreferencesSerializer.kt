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
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * An [androidx.datastore.core.Serializer] for the [UserPreferences] proto.
 *
 * This class is responsible for serializing and deserializing [UserPreferences] objects.
 * It uses the Protocol Buffer library to parse and write the data.
 */
class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {
    /**
     * The default value for the [UserPreferences] object.
     *
     * This value is used if the data store is empty or if there is an error reading the data.
     */
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    /**
     * Reads a [UserPreferences] object from the given [InputStream].
     *
     * This function is called by DataStore to deserialize the data from disk.
     * It uses [UserPreferences.parseFrom] to parse the [InputStream] into a [UserPreferences]
     * object. If the data is corrupted and cannot be parsed, it throws a [CorruptionException].
     *
     * @param input The [InputStream] to read from.
     * @return The deserialized [UserPreferences] object.
     * @throws CorruptionException If the data is corrupted and cannot be parsed.
     */
    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            // readFrom is already called on the data store background thread
            UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    /**
     * Writes the [UserPreferences] to the [output] stream.
     *
     * This method is called on the data store background thread, so it is safe to perform
     * blocking I/O operations. We just call the [UserPreferences.writeTo] method of our
     * [UserPreferences] parameter [t] to have it write its data to our [OutputStream] parameter
     * [output].
     *
     * @param t The [UserPreferences] to write.
     * @param output The [OutputStream] to write to.
     */
    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}
