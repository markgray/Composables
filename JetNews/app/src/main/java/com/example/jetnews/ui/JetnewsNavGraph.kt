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

import android.app.Application
import android.os.Bundle
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.JetnewsApplication.Companion.JETNEWS_APP_URI
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.ui.home.HomeViewModel
import com.example.jetnews.ui.interests.InterestsRoute
import com.example.jetnews.ui.interests.InterestsViewModel

/**
 * This is the `name` field of the query [String] of the [NavDeepLink] for the route
 * [JetnewsDestinations.HOME_ROUTE] and when used it is the key to the [String] stored in the
 * [Bundle] that is used to set the `preSelectedPostId` argument of the [HomeViewModel] that
 * [HomeRoute] uses.
 */
const val POST_ID: String = "postId"

/**
 * This is the [NavHost] used by our app to navigate between displaying [HomeRoute] for the `route`
 * [JetnewsDestinations.HOME_ROUTE] and displaying [InterestsRoute] for the `route`
 * [JetnewsDestinations.INTERESTS_ROUTE]. The arguments to our [NavHost] root Composable are:
 *  - `navController` our [NavHostController] parameter [navController].
 *  - `startDestination` our [String] parameter [startDestination].
 *  - `modifier` our [Modifier] parameter [modifier].
 *
 * The `builder` [NavGraphBuilder] lamba argument of our [NavHost] holds two [composable] calls
 * which add their `content` Composable lambdas to the [NavGraphBuilder]. The arguments of the first
 * [composable] call are:
 *  - `route` is [JetnewsDestinations.HOME_ROUTE]
 *  - `deepLinks` is a [navDeepLink] with a query pattern of its `uriPattern` argument allowing the
 *  user of the deep link to preselect a particular post when the string "?postId=RequestedPostId"
 *  is appended to the [String] "https://developer.android.com/jetnews/home"
 *
 * In the `content` lambda argument of the first call of [composable] we initialize our [HomeViewModel]
 * variable `val homeViewModel` to the instance returned for a call to [HomeViewModel.provideFactory]
 * whose `postsRepository` argument is the [PostsRepository] contained in the [AppContainer.postsRepository]
 * field of our [AppContainer] parameter [appContainer], and whose `preSelectedPostId` argument is the
 * [String] stored under the key [POST_ID] ("postId") in the [Bundle] of the [NavBackStackEntry.arguments]
 * of the [NavBackStackEntry] that [NavHost] passes to its `builder` [NavGraphBuilder] lambda argument.
 * The Composable displayed by the first call to [composable] is [HomeRoute] with its `homeViewModel`
 * argument our [HomeViewModel] variable `homeViewModel`, whose `isExpandedScreen` argument is our
 * [Boolean] parameter [isExpandedScreen], and whose `openDrawer` argument is our lambda parameter
 * [openDrawer].
 *
 * The `route` argument of the second call to [composable] is [JetnewsDestinations.INTERESTS_ROUTE],
 * and in the `content` lambda argument of the second call of [composable] we initialize our
 * [InterestsViewModel] variable `val interestsViewModel` to the instance returned for a call to
 * [InterestsViewModel.provideFactory] with its `interestsRepository` argument is the
 * [InterestsRepository] contained in the [AppContainer.interestsRepository] field of our
 * [AppContainer] parameter [appContainer]. The Composable displayed by the second call to
 * [composable] is [InterestsRoute] with its `interestsViewModel` argument is our [InterestsViewModel]
 * variable `interestsViewModel`, whose `isExpandedScreen` argument is our [Boolean] parameter
 * [isExpandedScreen] and whose `openDrawer` argument is our lambda parameter [openDrawer].
 *
 * @param appContainer this is the [AppContainer] which is created in our [JetnewsApplication] custom
 * [Application] (it contains a reference to our singleton [PostsRepository] in [AppContainer.postsRepository]
 * and a reference to our singleton [InterestsRepository] in [AppContainer.interestsRepository]).
 * @param isExpandedScreen if `true` the [WindowWidthSizeClass] of the device we are running on is
 * [WindowWidthSizeClass.Expanded] (Represents the majority of tablets in landscape and large unfolded
 * inner displays in landscape).
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [JetnewsApp] does not pass us one so the empty, default, or starter [Modifier]
 * that contains no elements is used instead.
 * @param navController the [NavHostController] that our [NavHost] should use. Our caller [JetnewsApp]
 * passes us a remembered instance that it also uses to construct the [JetnewsNavigationActions] that
 * is used to have us navigate to [JetnewsDestinations.HOME_ROUTE] by calling its
 * [JetnewsNavigationActions.navigateToHome] method or to [JetnewsDestinations.INTERESTS_ROUTE] by
 * calling its [JetnewsNavigationActions.navigateToInterests] method. It is also used to access the
 * current [NavBackStackEntry] in order to determine the current [NavDestination.route] that we are
 * displaying.
 * @param openDrawer a lambda which we can call to open the [ModalNavigationDrawer] that we are the
 * `content` of.
 * @param startDestination this is the the route for the start destination of our [NavHost]. Our
 * caller [JetnewsApp] does not pass one, so the default value [JetnewsDestinations.HOME_ROUTE] is
 * used instead.
 */
@Composable
fun JetnewsNavGraph(
    appContainer: AppContainer,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = JetnewsDestinations.HOME_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = JetnewsDestinations.HOME_ROUTE,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "$JETNEWS_APP_URI/${JetnewsDestinations.HOME_ROUTE}?$POST_ID={$POST_ID}"
                }
            )
        ) { navBackStackEntry: NavBackStackEntry ->
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    postsRepository = appContainer.postsRepository,
                    preSelectedPostId = navBackStackEntry.arguments?.getString(POST_ID)
                )
            )
            HomeRoute(
                homeViewModel = homeViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
            )
        }
        composable(route = JetnewsDestinations.INTERESTS_ROUTE) {
            val interestsViewModel: InterestsViewModel = viewModel(
                factory = InterestsViewModel.provideFactory(
                    interestsRepository = appContainer.interestsRepository
                )
            )
            InterestsRoute(
                interestsViewModel = interestsViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer
            )
        }
    }
}
