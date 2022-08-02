package com.example.androidtriviacompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidtriviacompose.about.AboutScreen
import com.example.androidtriviacompose.game.GameScreen
import com.example.androidtriviacompose.gameover.GameOverScreen
import com.example.androidtriviacompose.gamewon.GameWonScreen
import com.example.androidtriviacompose.rules.RulesScreen
import com.example.androidtriviacompose.title.TitleScreen
import com.example.androidtriviacompose.ui.theme.AndroidTriviaComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This is the launch activity of the Android Trivia game.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call the [setContent] method to have it compose the composable passed in its `content`
     * lambda into the our activity. The content will become the root view of the activity. That
     * composable is our [AndroidTriviaApp] composable which is wrapped in the custom [MaterialTheme]
     * defined by [AndroidTriviaComposeTheme].
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so we ignore it.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTriviaComposeTheme {
                AndroidTriviaApp()
            }
        }
    }
}

/**
 * This class defines the navigation routes that can be requested of the [NavHost] in our [NavGraph]
 * composable.
 *
 * @param route the [String] that is used as the route argument of the [NavGraphBuilder.composable]
 * method in the [NavHost] composable of [NavGraph] for the screen which is the composable for the
 * destination given in the [NavGraphBuilder.composable] `content` lambda argument.
 */
sealed class Routes(val route: String) {
    /**
     * Used to navigate to the [AboutScreen] composable.
     */
    object About : Routes("about")

    /**
     * Used to navigate to the [GameScreen] composable.
     */
    object Game : Routes("game")

    /**
     * Used to navigate to the [GameOverScreen] composable.
     */
    object GameOver : Routes("gameover")

    /**
     * Used to navigate to the [GameWonScreen] composable.
     */
    object GameWon : Routes("gamewon")

    /**
     * Used to navigate to the [RulesScreen] composable.
     */
    object Rules : Routes("rules")

    /**
     * Used to navigate to the [TitleScreen] composable.
     */
    object Title : Routes("title")
}

/**
 * This Composable holds the [NavHost] used to navigate between screens and is the `content`
 * argument of the [Scaffold] Composable used in our [MainScaffold] Composable.
 *
 * @param modifier a [Modifier] instance that we might find useful to pass as the head of a [Modifier]
 * chain to the Composables we contain (but do not do at present). Defaults to the empty, default,
 * or starter [Modifier] that contains no elements.
 * @param finishActivity a lambda parameter we lazily copied from some sample code (unused).
 * @param navController the [NavHostController] that will be used by our [NavHost].
 * @param startDestination the route for the start destination that will be used by our [NavHost].
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    finishActivity: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Title.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.About.route) {
            AboutScreen(navController = navController)
        }
        composable(Routes.Game.route) {
            GameScreen(navController = navController)
        }
        composable(Routes.GameOver.route) {
            GameOverScreen(navController = navController)
        }
        composable(Routes.GameWon.route) {
            GameWonScreen(navController = navController)
        }
        composable(Routes.Rules.route) {
            RulesScreen(navController = navController)
        }
        composable(Routes.Title.route) {
            TitleScreen(navController = navController)
        }
    }
}

/**
 * This Composable exists solely to pass to our [MainScaffold] Composable a [Modifier] configured by
 * [Modifier.fillMaxSize] to have its content fill the Constraints.maxWidth and Constraints.maxHeight
 * of the incoming measurement constraints, and by [Modifier.wrapContentSize] to align the content to
 * the center of the incoming measurement constraints.
 */
@Composable
fun AndroidTriviaApp() {
    MainScaffold(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}

/**
 * This Composable is used as the `drawerContent` argument of the [Scaffold] used in [MainScaffold].
 * (content of the Drawer sheet that can be pulled from the left side or right for RTL).
 *
 * @param navController the [NavHostController] that can be used to navigate to the different screens
 * controlled by the [NavHost] in our [NavGraph] Composable.
 * @param scaffoldState the [ScaffoldState] of the scaffold widget whose `drawerContent` we are. It
 * contains the state of the screen, e.g. variables to provide manual control over the drawer
 * behavior, sizes of components, etc
 * @param scope a [CoroutineScope] we can use to launch a background process to close the drawer.
 */
@Composable
fun DrawerContent(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            scope.launch { scaffoldState.drawerState.close() }
            navController.navigate(Routes.Rules.route)
        }) {
        Image(
            painter = painterResource(id = R.drawable.rules),
            contentDescription = stringResource(id = R.string.rules)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = stringResource(id = R.string.rules))
    }
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            scope.launch { scaffoldState.drawerState.close() }
            navController.navigate(Routes.About.route)
        }) {
        Image(
            painter = painterResource(id = R.drawable.about_button),
            contentDescription = stringResource(id = R.string.about)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = stringResource(id = R.string.about))
    }
}

/**
 * This Composable exists in order to hold a [Scaffold] Composable and pass it memoized instances of
 * [ScaffoldState] (our variable `val scaffoldState`), [CoroutineScope] (our variable `val scope`)
 * and [NavHostController] (our variable `val navController`) that will be stable across re-composition.
 * The arguments we pass to [Scaffold] are:
 *  - `modifier` - we just pass our own `modifier` parameter. The [Modifier] we are passed is
 *  configured with [Modifier.fillMaxSize] to have us fill our entire incoming measurement constraints,
 *  and [Modifier.wrapContentSize] to have us align to the center of our canvas
 *  - `scaffoldState` - we pass our "remembered" variable `val scaffoldState` as the [ScaffoldState]
 *  that contains the state of the widget, e.g. variables that provide manual control over the drawer
 *  behavior, sizes of components, etc. Note that we pass `scaffoldState` to our [DrawerContent] so
 *  it can use it to close the drawer when one of its buttons is clicked, and also to the [TopAppBar]
 *  composable we use for the `topBar` argument so it can use it to open the drawer when the [IconButton]
 *  used for its `navigationIcon` parameter is clicked.
 *  - `drawerContent` - we pass an instance of our [DrawerContent] Composable constructed to use our
 *  remembered [NavHostController] variable `navController` to navigate, our [ScaffoldState] variable
 *  `scaffoldState` to use to close the drawer when one of its buttons is clicked, and our
 *  [CoroutineScope] variable `scope` to use to close the drawer in a background process. [Scaffold]
 *  will use this as the content of the Drawer sheet that can be pulled from the left side (right for
 *  RTL).
 *  - `topBar` - we use an instance of [TopAppBar] as top app bar of the screen. Its `title` is the
 *  string "Android Trivia", and its `navigationIcon` is an instance of [IconButton] whose `onClick`
 *  parameter launches a coroutine using our remembered [CoroutineScope] variable `scope` which
 *  calls the `open` method of the [ScaffoldState.drawerState] of our `scaffoldState` variable to
 *  have it open the drawer. The [Icon] used as the content of the [IconButton] uses the system icon
 *  [Icons.Filled.Menu].
 *  - `content` - the content for the [Scaffold] is the [NavHost] which is wrapped by our [NavGraph]
 *  composable, with the `modifier` passed it consisting of a [Modifier.padding] constructed to use
 *  the [PaddingValues] passed as the argument to the lambda by [Scaffold] as its padding, and with
 *  the `navController` parameter of [NavGraph] our remembered [NavHostController] variable
 *  `navController`
 *
 * @param modifier the [Modifier] that we should pass to [Scaffold] for the root of the [Scaffold].
 */
@Preview(showBackground = true)
@Composable
fun MainScaffold(
    modifier: Modifier = Modifier
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope: CoroutineScope = rememberCoroutineScope()
    val navController: NavHostController = rememberNavController()
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        drawerContent = {
            DrawerContent(
                navController = navController,
                scaffoldState = scaffoldState,
                scope = scope
            )
        },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.android_trivia)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch { scaffoldState.drawerState.open() }
                        }
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                    }
                }
            )
        },
        content = { innerPadding: PaddingValues ->
            NavGraph(
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }
    )
}