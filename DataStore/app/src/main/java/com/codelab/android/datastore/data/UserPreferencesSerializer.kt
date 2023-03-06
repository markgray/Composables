package com.codelab.android.datastore.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.codelab.android.datastore.MainActivity
import com.codelab.android.datastore.UserPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/**
 * This is the [Serializer] of [UserPreferences] that is used by `SingleProcessDataStore` to serialize
 * [UserPreferences] when reading and writing to disk. It is used as the `serializer` parameter in
 * the call to the [DataStore] delegate `dataStore` used to create and build the Proto [DataStore]
 * of [UserPreferences] as the `userPreferencesStore` extension property of [Context] in our
 * [MainActivity] file.
 */
@Suppress("BlockingMethodInNonBlockingContext")
object UserPreferencesSerializer : Serializer<UserPreferences> {
    /**
     * The default instance of [UserPreferences] which `SingleProcessDataStore` will return when its
     * `readData` method is called if the file that is supposed to contain our Proto [DataStore] of
     * [UserPreferences] does not exist yet. We just use the [UserPreferences] instance returned by
     * the method [UserPreferences.getDefaultInstance].
     */
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    /**
     * Called by the `readData` method of `SingleProcessDataStore` to read the data from our
     * [InputStream] parameter [input] and parse that data into a [UserPreferences] instance.
     * We just return the [UserPreferences] instance returned by the [UserPreferences.parseFrom]
     * method when it reads from [input] (this is a method that is created from our protocol buffer
     * file "proto/user_prefs.proto"). We wrap the call in a `try` block intended to catch
     * [InvalidProtocolBufferException] and rethrow it as a [CorruptionException]
     *
     * @param input the [InputStream] created to read from the Proto [DataStore] file.
     * @return an instance of [UserPreferences] parsed from
     */
    override suspend fun readFrom(input: InputStream): UserPreferences {
        try {
            return UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Called by the `writeData` method of `SingleProcessDataStore` to serialize and write its
     * [UserPreferences] parameter [t] to its [OutputStream] parameter [output]. We just call
     * the [UserPreferences.writeTo] method of [t] with [output] to have it do all the work
     * (this is a method that is created from our protocol buffer file "proto/user_prefs.proto").
     *
     * @param t the [UserPreferences] instance we are to serialize and write to our [OutputStream]
     * parameter [output]
     * @param output the [OutputStream] we are to write to.
     */
    override suspend fun writeTo(t: UserPreferences, output: OutputStream): Unit = t.writeTo(output)
}
