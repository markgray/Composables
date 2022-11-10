/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui

import androidx.compose.material.DrawerState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.ui.MainDestinations.ARTICLE_ID_KEY
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.interests.InterestsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Destinations used in the ([JetnewsApp]).
 */
object MainDestinations {
    /**
     * Route of the [composable] which launches the [HomeScreen] Composable
     */
    const val HOME_ROUTE: String = "home"

    /**
     * Route of the [composable] which launches the [InterestsScreen] Composable
     */
    const val INTERESTS_ROUTE: String = "interests"

    /**
     * Route of the [composable] which launches the [ArticleScreen] Composable
     */
    const val ARTICLE_ROUTE: String = "post"

    /**
     * Key under which the [Post.id] string of the article to be displayed is stored that the
     * [ArticleScreen] Composable should display when the [ARTICLE_ROUTE] is navigated to.
     */
    const val ARTICLE_ID_KEY: String = "postId"
}

/**
 * This Composable is used as the `content` of the [Scaffold] of the [JetnewsApp] Composable and
 * contains the [NavHost] which is used to navigate between the [HomeScreen], [InterestsScreen],
 * and [ArticleScreen] Composables. We initialize and remember our [MainActions] variable
 * `val actions` to an instance constructed to use our [NavHostController] parameter [navController]
 * to navigate between screens (we use [navController] as the key when we call [remember] which will
 * cause `actions` to be reinitialized if [navController] is not equal to the value it held during
 * the previous composition). We initialize and remember our [CoroutineScope] variable
 * `val coroutineScope` to a [CoroutineScope] bound to this point in the composition using the
 * [rememberCoroutineScope] method. We initialize our variable `val openDrawer` to a suspend lambda
 * which uses the [CoroutineScope.launch] method of `coroutineScope` to launch a coroutine which
 * calls the [DrawerState.open] method of the [ScaffoldState.drawerState] of [scaffoldState] to
 * open the drawer of the [Scaffold]
 *
 * Our root Composable is a [NavHost] whose `navController` argument is our [navController] parameter,
 * and whose `startDestination` argument is our [startDestination] parameter. The [NavHost] holds
 * three [composable] whose `route` arguments are:
 *  - [MainDestinations.HOME_ROUTE], its `content` is the [HomeScreen] Composable, whose [PostsRepository]
 *  argument `postsRepository` is the [AppContainer.postsRepository] field of our [appContainer]
 *  parameter, whose `navigateToArticle` argument is the [MainActions.navigateToArticle] method of
 *  `actions`, and whose `openDrawer` argument is our `openDrawer` suspend lambda.
 *  - [MainDestinations.INTERESTS_ROUTE], its `content` is the [InterestsScreen] Composable, whose
 *  [InterestsRepository] argument `interestsRepository` is the [AppContainer.interestsRepository]
 *  field of our [appContainer] parameter, and whose `openDrawer` argument is our `openDrawer`
 *  suspend lambda.
 *  - [MainDestinations.ARTICLE_ROUTE] (with [ARTICLE_ID_KEY] spliced to the end of that [String]
 *  with a '/' character separating them to specify the key under which the article ID will be stored
 *  in the [NavBackStackEntry.arguments] bundle)
 */
@Composable
fun JetnewsNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    startDestination: String = MainDestinations.HOME_ROUTE
) {
    val actions: MainActions = remember(navController) { MainActions(navController) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = MainDestinations.HOME_ROUTE) {
            HomeScreen(
                postsRepository = appContainer.postsRepository,
                navigateToArticle = actions.navigateToArticle,
                openDrawer = openDrawer
            )
        }
        composable(route = MainDestinations.INTERESTS_ROUTE) {
            InterestsScreen(
                interestsRepository = appContainer.interestsRepository,
                openDrawer = openDrawer
            )
        }
        composable(route = "${MainDestinations.ARTICLE_ROUTE}/{$ARTICLE_ID_KEY}") { backStackEntry: NavBackStackEntry ->
            ArticleScreen(
                postId = backStackEntry.arguments?.getString(ARTICLE_ID_KEY),
                onBack = actions.upPress,
                postsRepository = appContainer.postsRepository
            )
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    /**
     *
     */
    val navigateToArticle: (String) -> Unit = { postId: String ->
        navController.navigate(route = "${MainDestinations.ARTICLE_ROUTE}/$postId")
    }

    /**
     *
     */
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}
