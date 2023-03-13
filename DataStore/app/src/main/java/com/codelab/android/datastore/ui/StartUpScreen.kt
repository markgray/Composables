package com.codelab.android.datastore.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

/**
 * This Composable just displays a splash screen for 2000 milliseconds then calls its [onTimeout]
 * lambda argument via the `val currentOnTimeout` remembered updated state.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us any so the empty, default, or starter [Modifier] that
 * contains no elements is used instead.
 * @param onTimeout a lambda that we should call when our timeup is over. Our caller passes us a
 * lambda that sets a [Boolean] flag to `false` in order to compose the main screen.
 */
@Composable
fun StartUpScreen(
    modifier: Modifier = Modifier,
    onTimeout: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val currentOnTimeout: () -> Unit by rememberUpdatedState(onTimeout)
        LaunchedEffect(true) {
            delay(timeMillis = 2000)
            currentOnTimeout()
        }
        CircularProgressIndicator()
    }
}