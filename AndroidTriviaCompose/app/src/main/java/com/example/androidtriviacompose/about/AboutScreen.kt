package com.example.androidtriviacompose.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.androidtriviacompose.MainScaffold
import com.example.androidtriviacompose.R
import com.example.androidtriviacompose.Routes

/**
 * This is the screen that displays the "About" text for the AndroidTriviaCompose app. It just
 * consists of an [Image] composable and a [Text] composable in a [Column], which uses the
 * [Modifier.verticalScroll] modifier to allow the user to scroll the [Column] if it is too large
 * to display. This screen is displayed when the user clicks the "About" button in the drawer of
 * the [MainScaffold] composable found in the MainActivity.kt file. The route to this screen is
 * defined by the [Routes.About] object and is the [String] "about".
 *
 * @param modifier a [Modifier] instance that our caller could use to modify our Composables (but
 * they don't do so).
 * @param navController a [NavHostController] we could use to navigate to other screens (but don't
 * use).
 */
@Preview(showBackground = true)
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    AboutScreenContent(modifier)
}

/**
 * This is the content displayed by the [AboutScreen], a level of indirection added for flexibility
 * when writing the code but not necessary it turned out. Our `content` consists of a [Column] whose
 * `modifier` adds 8 dp to the padding of the `modifier` parameter passed to [AboutScreenContent]
 * and modifies the [Column] to allow to scroll it to scroll vertically. Its `horizontalAlignment`
 * parameter (horizontal alignment of the layout's children) is [Alignment.CenterHorizontally] (the
 * children are centered horizontally). The `content` of the [Column] is an [Image], followed by a
 * [Spacer] of 20dp, and a [Text] containing the text describing the "Android Trivia" game.
 *
 * @param modifier a [Modifier] instance that our caller could use to modify our Composables (but
 * they don't do so, so the default [Modifier] is used instead).
 */
@Composable
fun AboutScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.about_android_trivia),
            contentDescription = null
        )
        Spacer(modifier = modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.about_text),
            fontSize = 20.sp
        )
    }
}