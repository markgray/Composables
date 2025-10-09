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

package com.google.samples.apps.nowinandroid.feature.foryou

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.rememberPermissionState
import com.google.samples.apps.nowinandroid.core.designsystem.component.DynamicAsyncImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaIconToggleButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaOverlayLoadingWheel
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DecorativeScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.ScrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.DevicePreviews
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import com.google.samples.apps.nowinandroid.core.ui.TrackScreenViewEvent
import com.google.samples.apps.nowinandroid.core.ui.TrackScrollJank
import com.google.samples.apps.nowinandroid.core.ui.UserNewsResourcePreviewParameterProvider
import com.google.samples.apps.nowinandroid.core.ui.launchCustomChromeTab
import com.google.samples.apps.nowinandroid.core.ui.newsFeed
import kotlinx.coroutines.flow.StateFlow

/**
 * The For You screen. The stateless override it calls is composed of two main parts:
 *  * A list of topics that the user can choose to follow.
 *  * A list of news resources that are relevant to the user's followed topics.
 *
 * The screen also shows a loading indicator when the data is loading.
 * The screen is also responsible for handling deep links to news resources.
 *
 * We start by initializing our [State] wrapped [OnboardingUiState] variable `onboardingUiState`
 * using the [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of [OnboardingUiState]
 * property [ForYouViewModel.onboardingUiState] of our [ForYouViewModel] parameter [viewModel]. We
 * initialize our [State] wrapped [NewsFeedUiState] variable `feedState` using the
 * [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of [NewsFeedUiState] property
 * [ForYouViewModel.feedState] of our [ForYouViewModel] parameter [viewModel]. We initialize our
 * [State] wrapped [Boolean] variable `isSyncing` using the [StateFlow.collectAsStateWithLifecycle]
 * method of the [StateFlow] of [Boolean] property [ForYouViewModel.isSyncing] of our
 * [ForYouViewModel] parameter [viewModel]. We initialize our [State] wrapped [UserNewsResource]
 * variable `deepLinkedUserNewsResource` using the [StateFlow.collectAsStateWithLifecycle] method
 * of the [StateFlow] of [UserNewsResource] property [ForYouViewModel.deepLinkedNewsResource] of
 * our [ForYouViewModel] parameter [viewModel].
 *
 * Then we compose a stateless [ForYouScreen] with the arguments:
 *  - `isSyncing`: is our [State] wrapped [Boolean] variable `isSyncing`.
 *  - `onboardingUiState`: is our [State] wrapped [OnboardingUiState] variable `onboardingUiState`.
 *  - `feedState`: is our [State] wrapped [NewsFeedUiState] variable `feedState`.
 *  - `deepLinkedUserNewsResource`: is our [State] wrapped [UserNewsResource] variable
 *  `deepLinkedUserNewsResource`.
 *  - `onTopicCheckedChanged`: is a reference to the [ForYouViewModel.updateTopicSelection] method
 *  of our [ForYouViewModel] parameter [viewModel].
 *  - `onDeepLinkOpened`: is a reference to the [ForYouViewModel.onDeepLinkOpened] method of our
 *  [ForYouViewModel] parameter [viewModel].
 *  - `onTopicClick`: is our lambda parameter [onTopicClick].
 *  - `saveFollowedTopics`: is a reference to the [ForYouViewModel.dismissOnboarding] method of our
 *  [ForYouViewModel] parameter [viewModel].
 *  - `onNewsResourcesCheckedChanged`: is a reference to the [ForYouViewModel.updateNewsResourceSaved]
 *  method of our [ForYouViewModel] parameter [viewModel].
 *  - `onNewsResourceViewed`: is a lambda that acccepts the [String] it is passed in variable
 *  `newsResourceId` then calls the [ForYouViewModel.setNewsResourceViewed] method of our
 *  [ForYouViewModel] parameter [viewModel] with the `newsResourceId` argument our [String] variable
 *  `newsResourceId` and the `viewed` argument `true`.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *
 * @param onTopicClick The callback to be invoked when a topic is clicked.
 * @param modifier The modifier to be applied to the screen.
 * @param viewModel The view model for the screen.
 */
@Composable
internal fun ForYouScreen(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForYouViewModel = hiltViewModel(
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
        null,
    ),
) {
    val onboardingUiState: OnboardingUiState by viewModel.onboardingUiState.collectAsStateWithLifecycle()
    val feedState: NewsFeedUiState by viewModel.feedState.collectAsStateWithLifecycle()
    val isSyncing: Boolean by viewModel.isSyncing.collectAsStateWithLifecycle()
    val deepLinkedUserNewsResource: UserNewsResource? by viewModel.deepLinkedNewsResource.collectAsStateWithLifecycle()

    ForYouScreen(
        isSyncing = isSyncing,
        onboardingUiState = onboardingUiState,
        feedState = feedState,
        deepLinkedUserNewsResource = deepLinkedUserNewsResource,
        onTopicCheckedChanged = viewModel::updateTopicSelection,
        onDeepLinkOpened = viewModel::onDeepLinkOpened,
        onTopicClick = onTopicClick,
        saveFollowedTopics = viewModel::dismissOnboarding,
        onNewsResourcesCheckedChanged = viewModel::updateNewsResourceSaved,
        onNewsResourceViewed = { newsResourceId: String ->
            viewModel.setNewsResourceViewed(
                newsResourceId = newsResourceId,
                viewed = true,
            )
        },
        modifier = modifier,
    )
}

/**
 * The Stateless For You screen that is called by its Stateful [ForYouScreen] overload.
 *
 * We start by initializing our [Boolean] variable `isOnboardingLoading` to `true` if our
 * [OnboardingUiState] parameter [onboardingUiState] is of type [OnboardingUiState.Loading], and
 * initializing our [Boolean] variable `isFeedLoading` to `true` if our [NewsFeedUiState] parameter
 * [feedState] is of type [NewsFeedUiState.Loading].
 *
 * We call the [ReportDrawnWhen] composable to add the predicate that requires that [isSyncing] is
 * `false`, `isOnboardingLoading` is `false`, and `isFeedLoading` is `false` before the method
 * [Activity.reportFullyDrawn] is called.
 *
 * Next we initialize our [Int] variable `itemsAvailable` to the result of calling the [feedItemsSize]
 * method with its `feedState` argument our [NewsFeedUiState] parameter [feedState] and its
 * `onboardingUiState` argument our [OnboardingUiState] parameter [onboardingUiState].
 *
 * We initialize and remember our [LazyStaggeredGridState] variable `state` using the
 * [rememberLazyStaggeredGridState] method. We initialize our [ScrollbarState] variable
 * `scrollbarState` to the value returned by the [LazyStaggeredGridState.scrollbarState] method of
 * our [LazyStaggeredGridState] variable `state` with its `itemsAvailable` argument our [Int]
 * variable `itemsAvailable`.
 *
 * We call the [TrackScrollJank] composable with its `scrollableState` argument our
 * [LazyStaggeredGridState] variable `state` and its `stateName` argument the string "forYou:feed"
 * to have its track jank while scrolling anything that's scrollable.
 *
 * Our root composable is a [Box] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.fillMaxSize]. In its [BoxScope] `content` composable lambda argument we
 * compose a [LazyVerticalStaggeredGrid] whose arguments are:
 *  - `columns`: is a [StaggeredGridCells.Adaptive] whose `minSize` argument is 300.dp.
 *  - `contentPadding`: is a [PaddingValues] whose `all` argument is 16.dp.
 *  - `horizontalArrangement`: is a [Arrangement.spacedBy] whose `space` argument is 16.dp.
 *  - `verticalItemSpacing`: is 24.dp.
 *  - `modifier`: is a [Modifier.testTag] whose `tag` argument is "forYou:feed".
 *  - `state`: is our [LazyStaggeredGridState] variable `state`.
 *
 * In the [LazyStaggeredGridScope] `content` composable lambda argument we call the [onboarding]
 * extension method of our [LazyStaggeredGridScope] receiver with the arguments:
 *  - `onboardingUiState`: is our [OnboardingUiState] parameter [onboardingUiState].
 *  - `onTopicCheckedChanged`: is our lambda parameter [onTopicCheckedChanged].
 *  - `saveFollowedTopics`: is our lambda parameter [saveFollowedTopics].
 *  - `interestsItemModifier`: is a custom [Modifier.layout] that removes the enforced parent
 *  16.dp contentPadding from the LazyVerticalGrid and enables edge-to-edge scrolling for the
 *  [onboarding] section.
 *
 * Next in the [LazyStaggeredGridScope] `content` composable lambda argument we call the [newsFeed]
 * extension method of our [LazyStaggeredGridScope] receiver with the arguments:
 *  - `feedState`: is our [NewsFeedUiState] parameter [feedState].
 *  - `onNewsResourcesCheckedChanged`: is our lambda parameter [onNewsResourcesCheckedChanged].
 *  - `onNewsResourceViewed`: is our lambda parameter [onNewsResourceViewed].
 *  - `onTopicClick`: is our lambda parameter [onTopicClick].
 *
 * At the bottom of the [LazyStaggeredGridScope] `content` composable lambda argument we compose
 * a [LazyStaggeredGridScope.item] whose `span` argument is [StaggeredGridItemSpan.FullLine] and
 * whose `contentType` argument is "bottomSpacing". In its [LazyStaggeredGridItemScope] `content`
 * lambda argument we compose a [Column] which contains a [Spacer] whose `modifier` argument
 * is a [Modifier.height] whose `height` argument is 8.dp, and a second [Spacer] whose `modifier`
 * argument is a [Modifier.windowInsetsBottomHeight] whose `insets` argument is
 * [WindowInsets.Companion.safeDrawing].
 *
 * Next in the [Box] we compose an [AnimatedVisibility] whose `visible` argument is `true` if
 * `isSyncing` is `true`, `isOnboardingLoading` is `true`, or `isFeedLoading` is `true`, whose
 * `enter` argument is a [slideInVertically] whose `initialOffsetY` argument is a lambda that
 * accepts the [Int] passed the lambda in variable `fullHeight` and returns minus `fullHeight` with
 * a [fadeIn] added to the [slideInVertically]. And whose `exit` argument is a [slideOutVertically]
 * whose `targetOffsetY` argument is a lambda that accepts the [Int] passed the lambda in variable
 * `fullHeight` and returns minus `fullHeight` with a [fadeOut] added to the [slideOutVertically].
 * In the [AnimatedVisibilityScope] `content` composable lambda argument we initialize our [String]
 * variable `loadingContentDescription` to the string resource with the id
 * `R.string.feature_foryou_loading` ("Loading for you…"), then compose a [Box] whose `modifier`
 * argument is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `8.dp` to the top.
 * In its [BoxScope] `content` composable lambda argument we compose a [NiaOverlayLoadingWheel] whose
 * `modifier` argument is a [BoxScope.align] whose `alignment` argument is [Alignment.Center], and
 * whose `contentDesc` argument is our [String] variable `loadingContentDescription`.
 *
 * Next in the outer [Box] holding the [LazyVerticalStaggeredGrid] we compose a [DraggableScrollbar]
 * whose receiver is our [LazyStaggeredGridState] variable `state`, whose `modifier` argument is a
 * [Modifier.fillMaxHeight] chained to a [Modifier.windowInsetsPadding] whose `insets` argument is
 * [WindowInsets.Companion.systemBars], chained to a [Modifier.padding] whose `horizontal` argument
 * is 2.dp, and chained to a [BoxScope.align] whose `alignment` argument is [Alignment.CenterEnd].
 * The `state` argument is our [ScrollbarState] variable `scrollbarState`, the `orientation` argument
 * is [Orientation.Vertical], and the `onThumbMoved` argument is the remembered lambda returned by
 * the method [LazyStaggeredGridState.rememberDraggableScroller] when called with its `itemsAvailable`
 * argument our [Int] variable `itemsAvailable`.
 *
 * Outside of the [Box] now we call the [TrackScreenViewEvent] composable with its `screenName`
 * argument the string "forYou". Then we call [NotificationPermissionEffect] to have it ask
 * for permission to send notifications if necessary. Finally we call the [DeepLinkEffect] composable
 * with its `userNewsResource` argument our [UserNewsResource] variable `deepLinkedUserNewsResource`,
 * and its `onDeepLinkOpened` argument our lambda parameter [onDeepLinkOpened].
 *
 * @param isSyncing `true` if the app is currently syncing data.
 * @param onboardingUiState The [OnboardingUiState] state of the onboarding UI.
 * @param feedState The [NewsFeedUiState] state of the news feed.
 * @param deepLinkedUserNewsResource The [UserNewsResource] that was deep linked to, if any.
 * @param onTopicCheckedChanged The callback to be invoked when a topic is checked or unchecked.
 * @param onTopicClick The callback to be invoked when a topic is clicked.
 * @param onDeepLinkOpened The callback to be invoked when a deep link is opened.
 * @param saveFollowedTopics The callback to be invoked when the user saves their followed topics.
 * @param onNewsResourcesCheckedChanged The callback to be invoked when a news resource is checked or unchecked.
 * @param onNewsResourceViewed The callback to be invoked when a news resource is viewed.
 * @param modifier The [Modifier] to be applied to the screen.
 */
@Composable
internal fun ForYouScreen(
    isSyncing: Boolean,
    onboardingUiState: OnboardingUiState,
    feedState: NewsFeedUiState,
    deepLinkedUserNewsResource: UserNewsResource?,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    onDeepLinkOpened: (String) -> Unit,
    saveFollowedTopics: () -> Unit,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOnboardingLoading: Boolean = onboardingUiState is OnboardingUiState.Loading
    val isFeedLoading: Boolean = feedState is NewsFeedUiState.Loading

    // This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isOnboardingLoading && !isFeedLoading }

    val itemsAvailable: Int = feedItemsSize(
        feedState = feedState,
        onboardingUiState = onboardingUiState,
    )

    val state: LazyStaggeredGridState = rememberLazyStaggeredGridState()
    val scrollbarState: ScrollbarState = state.scrollbarState(
        itemsAvailable = itemsAvailable,
    )
    TrackScrollJank(scrollableState = state, stateName = "forYou:feed")

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(all = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .testTag(tag = "forYou:feed"),
            state = state,
        ) {
            onboarding(
                onboardingUiState = onboardingUiState,
                onTopicCheckedChanged = onTopicCheckedChanged,
                saveFollowedTopics = saveFollowedTopics,
                // Custom LayoutModifier to remove the enforced parent 16.dp contentPadding
                // from the LazyVerticalGrid and enable edge-to-edge scrolling for this section
                interestsItemModifier = Modifier.layout {
                        measurable: Measurable,
                        constraints: Constraints,
                    ->
                    val placeable: Placeable = measurable.measure(
                        constraints = constraints.copy(
                            maxWidth = constraints.maxWidth + 32.dp.roundToPx(),
                        ),
                    )
                    layout(width = placeable.width, height = placeable.height) {
                        placeable.place(x = 0, y = 0)
                    }
                },
            )

            newsFeed(
                feedState = feedState,
                onNewsResourcesCheckedChanged = onNewsResourcesCheckedChanged,
                onNewsResourceViewed = onNewsResourceViewed,
                onTopicClick = onTopicClick,
            )

            item(span = StaggeredGridItemSpan.FullLine, contentType = "bottomSpacing") {
                Column {
                    Spacer(modifier = Modifier.height(height = 8.dp))
                    // Add space for the content to clear the "offline" snackbar.
                    // TODO: Check that the Scaffold handles this correctly in NiaApp
                    // if (isOffline) Spacer(modifier = Modifier.height(48.dp))
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(insets = WindowInsets.safeDrawing))
                }
            }
        }
        AnimatedVisibility(
            visible = isSyncing || isFeedLoading || isOnboardingLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight: Int -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight: Int -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription: String =
                stringResource(id = R.string.feature_foryou_loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                NiaOverlayLoadingWheel(
                    modifier = Modifier
                        .align(alignment = Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(insets = WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(alignment = Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
    TrackScreenViewEvent(screenName = "ForYou")
    NotificationPermissionEffect()
    DeepLinkEffect(
        userNewsResource = deepLinkedUserNewsResource,
        onDeepLinkOpened = onDeepLinkOpened,
    )
}

/**
 * An extension on [LazyStaggeredGridScope] defining the onboarding portion of the for you screen.
 * Depending on the [onboardingUiState], this might emit no items.
 *
 * We branch on the value of our [OnboardingUiState] parameter [onboardingUiState] doing nothing if
 * it is [OnboardingUiState.Loading], [OnboardingUiState.LoadFailed], or [OnboardingUiState.NotShown].
 * If it is [OnboardingUiState.Shown] we compose a [LazyStaggeredGridScope.item] whose `span`
 * argument is [StaggeredGridItemSpan.FullLine] and whose `contentType` argument is "onboarding". In
 * its [LazyStaggeredGridItemScope] `content` composable lambda argument we compose a [Column]
 * whose `modifier` argument is our [Modifier] parameter [interestsItemModifier]. In the [ColumnScope]
 * `content` composable lambda argument we compose:
 *
 * A [Text] whose arguments are:
 *  - `text`: the string resource with the id `R.string.feature_foryou_onboarding_guidance_title`
 *  ("What are you interested in?")
 *  - `textAlign`: [TextAlign.Center] (the text will be centered)
 *  - `modifier`: a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `24.dp` to the
 *  `top`.
 *  - `style`: the [TextStyle] is [Typography.titleMedium] of our custom [MaterialTheme.typography]
 *
 * A [Text] whose arguments are:
 *  - `text`: is the [String] whose resource ID is `R.string.feature_foryou_onboarding_guidance_subtitle`
 *  ("Updates from topics you follow will appear here. Follow some things to get started.").
 *  `modifier`: a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `8.dp` to the
 *  `top`, `24.dp` to the `start`, and `24.dp` to the `end`.
 *  - `textAlign`: [TextAlign.Center] (the text will be centered)
 *  - `style`: the [TextStyle] is [Typography.bodyMedium] of our custom [MaterialTheme.typography]
 *
 * A [TopicSelection] whose arguments are:
 *  - `onboardingUiState`: is our [OnboardingUiState] parameter [onboardingUiState].
 *  - `onTopicCheckedChanged`: is our lambda parameter [onTopicCheckedChanged].
 *  - `modifier`: is a [Modifier.padding] that adds `8.dp` to the `bottom`.
 *
 * A [Row] whose `horizontalArrangement` argument is [Arrangement.Center] and whose `modifier`
 * argument is a [Modifier.fillMaxWidth]. In its [RowScope] `content` composable lambda argument we
 * compose a [NiaButton] whose arguments are:
 *  - `onClick`: our lambda parameter [saveFollowedTopics].
 *  - `enabled`: is `true` if the [OnboardingUiState.Shown.isDismissable] property of our
 *  [OnboardingUiState] parameter [onboardingUiState] is `true`
 *  - `modifier`: is a [Modifier.padding] that adds `24.dp` to the `horizontal` sides, chained to a
 *  [Modifier.widthIn] that sets the `min` width to `364.dp`, chained to a [Modifier.fillMaxWidth].
 *
 *  In the [RowScope] `content` composable lambda argument of the [NiaButton] we compose a [Text]
 *  whose `text` is the [String] with resource ID `R.string.feature_foryou_done` ("Done").
 *
 * @param onboardingUiState the onboarding state of the for you screen, one of
 * [OnboardingUiState.Loading], [OnboardingUiState.LoadFailed], [OnboardingUiState.NotShown], or
 * [OnboardingUiState.Shown].
 * @param onTopicCheckedChanged a function to be invoked when a topic is checked or unchecked.
 * @param saveFollowedTopics a function to be invoked when the user saves their followed topics.
 * @param interestsItemModifier a [Modifier] to be applied to the interests item.
 */
private fun LazyStaggeredGridScope.onboarding(
    onboardingUiState: OnboardingUiState,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    saveFollowedTopics: () -> Unit,
    interestsItemModifier: Modifier = Modifier,
) {
    when (onboardingUiState) {
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        OnboardingUiState.NotShown,
            -> Unit

        is OnboardingUiState.Shown -> {
            item(span = StaggeredGridItemSpan.FullLine, contentType = "onboarding") {
                Column(modifier = interestsItemModifier) {
                    Text(
                        text = stringResource(id = R.string.feature_foryou_onboarding_guidance_title),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(id = R.string.feature_foryou_onboarding_guidance_subtitle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TopicSelection(
                        onboardingUiState = onboardingUiState,
                        onTopicCheckedChanged = onTopicCheckedChanged,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    // Done button
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        NiaButton(
                            onClick = saveFollowedTopics,
                            enabled = onboardingUiState.isDismissable,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .widthIn(min = 364.dp)
                                .fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(id = R.string.feature_foryou_done),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Displays a list of topics for the user to select from.
 *
 * We start by initializing and remembering our [LazyGridState] variable `lazyGridState` to the
 * the instance returned by the [rememberLazyGridState] composable function. We also initialize
 * our [String] variable `topicSelectionTestTag` to the string "forYou:topicSelection".
 *
 * We call the [TrackScrollJank] composable with its `scrollableState` argument our [LazyGridState]
 * variable `lazyGridState`, and its `stateName` argument our [String] variable `topicSelectionTestTag`
 * to have it track jank while scrolling anything that's scrollable.
 *
 * Our root composable is a [Box] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.fillMaxWidth]. In the [BoxScope] `content` composable lambda argument we
 * we first compose a [LazyHorizontalGrid] whose arguments are:
 *  - `state`: our [LazyGridState] variable `lazyGridState`.
 *  - `rows`: a [GridCells.Fixed] whose `count` argument is `3`.
 *  - `horizontalArrangement`: a [Arrangement.spacedBy] whose `space` argument is `12.dp`.
 *  - `verticalArrangement`: a [Arrangement.spacedBy] whose `space` argument is `12.dp`.
 *  - `contentPadding`: a [PaddingValues] whose `all` argument is `24.dp`.
 *  - `modifier`: a [Modifier.heightIn] whose `max` argument is the maximum of `240.dp` and the
 *  [Dp] equivalent of `240.sp`, chained to a [Modifier.fillMaxWidth] chained to a [Modifier.testTag]
 *  whose `tag` argument is our [String] variable `topicSelectionTestTag`.
 *
 * In the [LazyGridScope] `content` composable lambda argument we compose a [LazyGridScope.items]
 * whose `items` argument is the [List] of [FollowableTopic] of the [OnboardingUiState.Shown.topics]
 * property of our [OnboardingUiState] parameter [onboardingUiState]. The `key` argument is the
 * [String] property [Topic.id] of the current [FollowableTopic].  In the [LazyGridItemScope]
 * `itemContent` composable lambda argument of the [LazyGridScope.items] we compose a
 * [SingleTopicButton] whose arguments are:
 *  - `name`: is the [String] property [Topic.name] of the current [FollowableTopic].
 *  - `topicId`: is the [String] property [Topic.id] of the current [FollowableTopic].
 *  - `imageUrl`: is the [String] property [Topic.imageUrl] of the current [FollowableTopic].
 *  - `isSelected`: is the [Boolean] property [FollowableTopic.isFollowed] of the current
 *  [FollowableTopic].
 *  - `onClick`: is our lambda parameter [onTopicCheckedChanged].
 *
 * Alongside the [LazyHorizontalGrid] in the [Box] we compose a [DecorativeScrollbar] using our
 * [LazyGridState] variable `lazyGridState` as its receiver with the arguments:
 *  - `modifier`: a [Modifier.fillMaxWidth] chained to a [Modifier.padding] whose `horizontal`
 *  argument is `12.dp`, chained to a [BoxScope.align] whose `alignment` argument is
 *  [Alignment.BottomStart].
 *  - `state`: the [ScrollbarState] returned by the [LazyGridState.scrollbarState] method of our
 *  [LazyGridState] variable `lazyGridState` with its `itemsAvailable` argument the size of the
 *  [OnboardingUiState.Shown.topics] property of our [OnboardingUiState] parameter
 *  [onboardingUiState].
 *  - `orientation`: is [Orientation.Horizontal].
 *
 * @param onboardingUiState The current [OnboardingUiState] state of the onboarding UI.
 * @param onTopicCheckedChanged A callback that is invoked when the user checks or unchecks a topic.
 * @param modifier A [Modifier] that is applied to the [Box] that contains the grid and scrollbar.
 */
@Composable
private fun TopicSelection(
    onboardingUiState: OnboardingUiState.Shown,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyGridState: LazyGridState = rememberLazyGridState()
    val topicSelectionTestTag = "forYou:topicSelection"

    TrackScrollJank(scrollableState = lazyGridState, stateName = topicSelectionTestTag)

    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        LazyHorizontalGrid(
            state = lazyGridState,
            rows = GridCells.Fixed(count = 3),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
            contentPadding = PaddingValues(all = 24.dp),
            modifier = Modifier
                // LazyHorizontalGrid has to be constrained in height.
                // However, we can't set a fixed height because the horizontal grid contains
                // vertical text that can be rescaled.
                // When the fontScale is at most 1, we know that the horizontal grid will be at most
                // 240dp tall, so this is an upper bound for when the font scale is at most 1.
                // When the fontScale is greater than 1, the height required by the text inside the
                // horizontal grid will increase by at most the same factor, so 240sp is a valid
                // upper bound for how much space we need in that case.
                // The maximum of these two bounds is therefore a valid upper bound in all cases.
                .heightIn(
                    max = max(
                        a = 240.dp,
                        b = with(receiver = LocalDensity.current) { 240.sp.toDp() },
                    ),
                )
                .fillMaxWidth()
                .testTag(tag = topicSelectionTestTag),
        ) {
            items(
                items = onboardingUiState.topics,
                key = { it.topic.id },
            ) {
                SingleTopicButton(
                    name = it.topic.name,
                    topicId = it.topic.id,
                    imageUrl = it.topic.imageUrl,
                    isSelected = it.isFollowed,
                    onClick = onTopicCheckedChanged,
                )
            }
        }
        lazyGridState.DecorativeScrollbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .align(Alignment.BottomStart),
            state = lazyGridState.scrollbarState(itemsAvailable = onboardingUiState.topics.size),
            orientation = Orientation.Horizontal,
        )
    }
}

/**
 * A button that displays a single [Topic].
 *
 * Our root composable is a [Surface] whose arguments are:
 *  - `modifier`: is a [Modifier.width] whose `width` argument is `312.dp`, chained to a
 *  [Modifier.heightIn] whsoe `min` is `56.dp`
 *  - `shape`: is a [RoundedCornerShape] whose `corner` argument is a [CornerSize] whose `size`
 *  is `8.dp`.
 *  - `color`: is the [ColorScheme.surface] of our custom [MaterialTheme.colorScheme].
 *  - `selected`: is our [Boolean] parameter [isSelected].
 *  - `onClick`: is a lambda that calls our lambda parameter [onClick] with our [String] parameter
 *  [topicId] and the negation of our [Boolean] parameter [isSelected].
 *
 * In the `content` composable lambda argument of the [Surface] we compose a [Row] whose
 * `verticalAlignment` argument is [Alignment.CenterVertically] and whose `modifier` argument is a
 * [Modifier.padding] that adds `12.dp` to the `start` and `8.dp` to the `end`. In the [RowScope]
 * `content` composable lambda argument we compose:
 *
 * A [TopicIcon] whose `imageUrl` argument is our [String] parameter [imageUrl].
 *
 * A [Text] whose arguments are:
 *  - `text`: is our [String] parameter [name].
 *  - `style`: [TextStyle] is the [Typography.titleSmall] of our custom [MaterialTheme.typography].
 *  - `modifier`: a [Modifier.padding] that adds `12.dp` to the `horizontal` sides, chained to
 *  [RowScope.weight] whose `weight` is `1f`.
 *  - `color`: is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *
 * A [NiaIconToggleButton] whose arguments are:
 *  - `checked`: is our [Boolean] parameter [isSelected].
 *  - `onCheckedChange`: is a lambda that calls our lambda parameter [onClick] with our [String]
 *  parameter [topicId] and the [Boolean] passed the lambda in variable `checked`.
 *  - `icon`: is a lambda that composes an [Icon] whose `imageVector` argument is [NiaIcons.Add]
 *  and whose `contentDescription` argument is our [String] parameter [name].
 *  - `checkedIcon`: is a lambda that composes an [Icon] whose `imageVector` argument is
 *  [NiaIcons.Check] and whose `contentDescription` argument is our [String] parameter [name].
 *
 * @param name the name of the [Topic].
 * @param topicId the [Topic.id] of the [Topic].
 * @param imageUrl the [Topic.imageUrl] of the [Topic].
 * @param isSelected whether or not the [Topic] is currently selected.
 * @param onClick a function to be invoked when the button is clicked.
 */
@Composable
private fun SingleTopicButton(
    name: String,
    topicId: String,
    imageUrl: String,
    isSelected: Boolean,
    onClick: (String, Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(width = 312.dp)
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(corner = CornerSize(size = 8.dp)),
        color = MaterialTheme.colorScheme.surface,
        selected = isSelected,
        onClick = {
            onClick(topicId, !isSelected)
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 8.dp),
        ) {
            TopicIcon(
                imageUrl = imageUrl,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(weight = 1f),
                color = MaterialTheme.colorScheme.onSurface,
            )
            NiaIconToggleButton(
                checked = isSelected,
                onCheckedChange = { checked: Boolean -> onClick(topicId, checked) },
                icon = {
                    Icon(
                        imageVector = NiaIcons.Add,
                        contentDescription = name,
                    )
                },
                checkedIcon = {
                    Icon(
                        imageVector = NiaIcons.Check,
                        contentDescription = name,
                    )
                },
            )
        }
    }
}

/**
 * An composable that displays the icon for a [Topic].
 *
 * We compose a [DynamicAsyncImage] whose arguments are:
 *  - `placeholder`: is a drawable with resource ID [R.drawable.feature_foryou_ic_icon_placeholder]
 *  (a stylized circle containing the text "Nia").
 *  - `imageUrl`: is our [String] parameter [imageUrl].
 *  - `contentDescription`: is `null` (it is a decorative image so needs no content description).
 *  - `modifier`: is our [Modifier] parameter [modifier] chained to a [Modifier.padding] that adds
 *  `10.dp` to all sides, chained to a [Modifier.size] that sets its `size` to `32.dp`.
 *
 * @param imageUrl the url of the image to display.
 * @param modifier a [Modifier] to be applied to the [DynamicAsyncImage].
 */
@Composable
fun TopicIcon(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    DynamicAsyncImage(
        placeholder = painterResource(id = R.drawable.feature_foryou_ic_icon_placeholder),
        imageUrl = imageUrl,
        // decorative
        contentDescription = null,
        modifier = modifier
            .padding(all = 10.dp)
            .size(size = 32.dp),
    )
}

/**
 * Effect that requests notification permission, if it shouldn't show rationale.
 *
 * We first use the [LocalInspectionMode] CompositionLocal to check if we are in a preview, and if
 * so we return early. If our Build version is less than [VERSION_CODES.TIRAMISU] we return early.
 * We initialize and remember our [PermissionState] variable `notificationsPermissionState` using the
 * `accompanist-permissions` library's [rememberPermissionState] method with the `permission` argument
 * [Manifest.permission.POST_NOTIFICATIONS].
 *
 * We then use a [LaunchedEffect] whose `key1` is `notificationsPermissionState` to launch its lambda
 * block whenever `notificationsPermissionState` changes. In this block we initialize our
 * [PermissionStatus] variable `status` to the [PermissionState.status] of
 * `notificationsPermissionState`. Then if `status` is of type [Denied] and its
 * [Denied.shouldShowRationale] property is `false` we call the
 * [PermissionState.launchPermissionRequest] method of `notificationsPermissionState`.
 */
@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {
    // Permission requests should only be made from an Activity Context, which is not present
    // in previews
    if (LocalInspectionMode.current) return
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return
    val notificationsPermissionState: PermissionState = rememberPermissionState(
        permission = Manifest.permission.POST_NOTIFICATIONS,
    )
    LaunchedEffect(key1 = notificationsPermissionState) {
        val status: PermissionStatus = notificationsPermissionState.status
        if (status is Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}


/**
 * A side effect that handles deep links to news resources.
 *
 * It uses the [LocalContext] CompositionLocal to initialize its [Context] variable `context` to
 * the current [Context]. It initializes its [Int] variable `backgroundColor` to the
 * [ColorScheme.background] color of our custom [MaterialTheme.colorScheme] converted to an ARGB [Int].
 *
 * It then uses a [LaunchedEffect] whose `key1` is its [UserNewsResource] parameter [userNewsResource]
 * to launch its lambda block whenever [userNewsResource] changes. In this block it first checks if
 * [userNewsResource] is `null` and if it is it returns from the [LaunchedEffect]. If the
 * [UserNewsResource.hasBeenViewed] property of [userNewsResource] is `false` it calls our lambda
 * parameter [onDeepLinkOpened] with the [UserNewsResource.id] property of [userNewsResource].
 *
 * Finally it calls the [launchCustomChromeTab] method with its `context` argument our [Context]
 * variable `context`, its `uri` argument the [Uri] parsed from the [UserNewsResource.url] property
 * of [userNewsResource], and its `toolbarColor` argument our [Int] variable `backgroundColor`.
 *
 * @param userNewsResource The [UserNewsResource] to deep link to. If this is `null`, nothing happens.
 * @param onDeepLinkOpened The callback to be invoked when the deep link is opened.
 */
@Composable
private fun DeepLinkEffect(
    userNewsResource: UserNewsResource?,
    onDeepLinkOpened: (String) -> Unit,
) {
    val context: Context = LocalContext.current
    val backgroundColor: Int = MaterialTheme.colorScheme.background.toArgb()

    LaunchedEffect(key1 = userNewsResource) {
        if (userNewsResource == null) return@LaunchedEffect
        if (!userNewsResource.hasBeenViewed) onDeepLinkOpened(userNewsResource.id)

        @SuppressLint("UseKtx")
        launchCustomChromeTab(
            context = context,
            uri = Uri.parse(userNewsResource.url),
            toolbarColor = backgroundColor,
        )
    }
}

/**
 * Returns the size of the feed items, which is the sum of the feed size and the onboarding size.
 * If the feed is loading, the feed size is 0, if the [NewsFeedUiState] is [NewsFeedUiState.Success]
 * the feed size is the size of the [NewsFeedUiState.Success.feed] list of our [NewsFeedUiState]
 * parameter [feedState]. If the onboarding is loading, failed, or not shown, the onboarding size is
 * 0. If the onboarding is shown, the onboarding size is 1.
 *
 * @param feedState The [NewsFeedUiState] state of the news feed.
 * @param onboardingUiState The [OnboardingUiState] state of the onboarding UI.
 * @return The size of the feed items.
 */
private fun feedItemsSize(
    feedState: NewsFeedUiState,
    onboardingUiState: OnboardingUiState,
): Int {
    val feedSize: Int = when (feedState) {
        NewsFeedUiState.Loading -> 0
        is NewsFeedUiState.Success -> feedState.feed.size
    }
    val onboardingSize: Int = when (onboardingUiState) {
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        OnboardingUiState.NotShown,
            -> 0

        is OnboardingUiState.Shown -> 1
    }
    return feedSize + onboardingSize
}

/**
 * Preview of the "For You" screen with a populated feed.
 *
 * It is composed wrapped in our [NiaTheme] custom [MaterialTheme].
 * It calls the stateless [ForYouScreen] composable with the arguments:
 *  - `isSyncing`: `false`.
 *  - `onboardingUiState`: [OnboardingUiState.NotShown].
 *  - `feedState`: [NewsFeedUiState.Success] whose `feed` argument is our [List] of [UserNewsResource]
 *  parameter [userNewsResources].
 *  - `deepLinkedUserNewsResource`: `null`.
 *  - `onTopicCheckedChanged`: a no-op lambda.
 *  - `saveFollowedTopics`: a no-op lambda.
 *  - `onNewsResourcesCheckedChanged`: a no-op lambda.
 *  - `onNewsResourceViewed`: a no-op lambda.
 *  - `onTopicClick`: a no-op lambda.
 *  - `onDeepLinkOpened`: a no-op lambda.
 *
 * @param userNewsResources the [List] of [UserNewsResource] to display, provided by our
 * [UserNewsResourcePreviewParameterProvider] custom [PreviewParameterProvider].
 */
@DevicePreviews
@Composable
fun ForYouScreenPopulatedFeed(
    @PreviewParameter(provider = UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

/**
 * Preview of the "For You" screen with a populated feed and the device offline.
 *
 * It is composed wrapped in our [NiaTheme] custom [MaterialTheme].
 * It calls the stateless [ForYouScreen] composable with the arguments:
 *  - `isSyncing`: `false`.
 *  - `onboardingUiState`: [OnboardingUiState.NotShown].
 *  - `feedState`: [NewsFeedUiState.Success] whose `feed` argument is our [List] of [UserNewsResource]
 *  parameter [userNewsResources].
 *  - `deepLinkedUserNewsResource`: `null`.
 *  - `onTopicCheckedChanged`: a no-op lambda.
 *  - `saveFollowedTopics`: a no-op lambda.
 *  - `onNewsResourcesCheckedChanged`: a no-op lambda.
 *  - `onNewsResourceViewed`: a no-op lambda.
 *  - `onTopicClick`: a no-op lambda.
 *  - `onDeepLinkOpened`: a no-op lambda.
 *
 * @param userNewsResources the [List] of [UserNewsResource] to display, provided by our
 * [UserNewsResourcePreviewParameterProvider] custom [PreviewParameterProvider].
 */
@DevicePreviews
@Composable
fun ForYouScreenOfflinePopulatedFeed(
    @PreviewParameter(provider = UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

/**
 * Preview of the "For You" screen with topic selection.
 *
 * It is composed wrapped in our [NiaTheme] custom [MaterialTheme].
 * It calls the stateless [ForYouScreen] composable with the arguments:
 *  - `isSyncing`: `false`.
 *  - `onboardingUiState`: [OnboardingUiState.Shown] whose `topics` argument is created by
 *  flat-mapping the [UserNewsResource.followableTopics] property of the [UserNewsResource] in
 *  our [List] of [UserNewsResource] parameter [userNewsResources] then removing the duplicates.
 *  - `feedState`: [NewsFeedUiState.Success] whose `feed` argument is our [List] of [UserNewsResource]
 *  parameter [userNewsResources].
 *  - `deepLinkedUserNewsResource`: `null`.
 *  - `onTopicCheckedChanged`: a no-op lambda.
 *  - `saveFollowedTopics`: a no-op lambda.
 *  - `onNewsResourcesCheckedChanged`: a no-op lambda.
 *  - `onNewsResourceViewed`: a no-op lambda.
 *  - `onTopicClick`: a no-op lambda.
 *  - `onDeepLinkOpened`: a no-op lambda.
 *
 * @param userNewsResources the [List] of [UserNewsResource] to display, provided by our
 * [UserNewsResourcePreviewParameterProvider] custom [PreviewParameterProvider].
 */
@DevicePreviews
@Composable
fun ForYouScreenTopicSelection(
    @PreviewParameter(provider = UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.Shown(
                topics = userNewsResources.flatMap { news: UserNewsResource -> news.followableTopics }
                    .distinctBy { it.topic.id },
            ),
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

/**
 * Preview of the "For You" screen when the feed is loading.
 *
 * It is composed wrapped in our [NiaTheme] custom [MaterialTheme].
 * It calls the stateless [ForYouScreen] composable with the arguments:
 *  - `isSyncing`: `false`.
 *  - `onboardingUiState`: [OnboardingUiState.Loading].
 *  - `feedState`: [NewsFeedUiState.Loading].
 *  - `deepLinkedUserNewsResource`: `null`.
 *  - `onTopicCheckedChanged`: a no-op lambda.
 *  - `saveFollowedTopics`: a no-op lambda.
 *  - `onNewsResourcesCheckedChanged`: a no-op lambda.
 *  - `onNewsResourceViewed`: a no-op lambda.
 *  - `onTopicClick`: a no-op lambda.
 *  - `onDeepLinkOpened`: a no-op lambda.
 */
@DevicePreviews
@Composable
fun ForYouScreenLoading() {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = NewsFeedUiState.Loading,
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

/**
 * Preview of the "For You" screen with a populated feed and the data is loading.
 *
 * It is composed wrapped in our [NiaTheme] custom [MaterialTheme].
 * It calls the stateless [ForYouScreen] composable with the arguments:
 *  - `isSyncing`: `true` (a loading indicator will be shown).
 *  - `onboardingUiState`: [OnboardingUiState.Loading].
 *  - `feedState`: [NewsFeedUiState.Success] whose `feed` argument is our [List] of [UserNewsResource]
 *  parameter [userNewsResources].
 *  - `deepLinkedUserNewsResource`: `null`.
 *  - `onTopicCheckedChanged`: a no-op lambda.
 *  - `saveFollowedTopics`: a no-op lambda.
 *  - `onNewsResourcesCheckedChanged`: a no-op lambda.
 *  - `onNewsResourceViewed`: a no-op lambda.
 *  - `onTopicClick`: a no-op lambda.
 *  - `onDeepLinkOpened`: a no-op lambda.
 *
 * @param userNewsResources the [List] of [UserNewsResource] to display, provided by our
 * [UserNewsResourcePreviewParameterProvider] custom [PreviewParameterProvider].
 */
@DevicePreviews
@Composable
fun ForYouScreenPopulatedAndLoading(
    @PreviewParameter(provider = UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = true,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}
