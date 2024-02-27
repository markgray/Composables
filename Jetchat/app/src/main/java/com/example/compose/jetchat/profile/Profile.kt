/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.AnimatingFabContent
import com.example.compose.jetchat.components.baselineHeight
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.data.meProfile
import com.example.compose.jetchat.theme.JetchatTheme

/**
 * This Composable is used to display the [ProfileScreenState] for a particular `userId`, either
 * [meProfile] or [colleagueProfile]. It is used in a [ComposeView] found in the layout file with
 * resource ID [R.layout.fragment_profile] by [ProfileFragment]. We start by initializing and
 * remembering our [MutableState] wrapped [Boolean] variable `var functionalityNotAvailablePopupShown`
 * to `false`. Then is `functionalityNotAvailablePopupShown` has changed to `true` in a later
 * recomposition we call our [FunctionalityNotAvailablePopup] with its `onDismiss` lambda argument
 * a lambda that sets `functionalityNotAvailablePopupShown` to `false` (this pops up an [AlertDialog]
 * displaying the message: "Functionality not available", and the `confirmButton` argument of that
 * [AlertDialog] is a [TextButton] that calls the lambda we pass [FunctionalityNotAvailablePopup]
 * when the user clicks it. Next we initialize and remember our [ScrollState] variable
 * `val scrollState`
 *
 * Our root Composable is a [BoxWithConstraints] whose `modifier` argument is a [Modifier.fillMaxSize]
 * that causes it to take up its entire incoming size constraint, with a [Modifier.nestedScroll] whose
 * `connection` argument is our [NestedScrollConnection] parameter [nestedScrollInteropConnection]
 * chained to that (modifies the element to make it participate in the nested scrolling hierarchy),
 * followed by a [Modifier.systemBarsPadding] to add padding to accommodate the system bars insets.
 * The `content` of the [BoxWithConstraints] holds two Composables:
 *  - a [Surface] whose `content` is a [Column] whose `modifier` argument is a [Modifier.fillMaxSize]
 *  that causes it to take up its entire incoming size constraint, with a [Modifier.verticalScroll]
 *  whose `state` argument is our [ScrollState] variable `scrollState` chained to that to Modify the
 *  element to allow it to scroll vertically when the height of the content is bigger than max
 *  constraints allow. The `content` of the [Column] is a [ProfileHeader] whose `scrollState`
 *  argument is our [ScrollState] variable `scrollState`, whose `data` argument is our [ProfileScreenState]
 *  parameter [userData], and whose `containerHeight` is the [BoxWithConstraintsScope.maxHeight] property
 *  of the [BoxWithConstraints] holding us (maximum height in [Dp]). Below this in the [Column] is a
 *  [UserInfoFields] Composable whose `userData` argument is our [ProfileScreenState] parameter [userData],
 *  and whose `containerHeight` is also the [BoxWithConstraintsScope.maxHeight] property of the
 *  [BoxWithConstraints] holding us.
 *  - we initialize and remember a `DerivedSnapshotState` wrapped [Boolean] variable `val fabExtended`
 *  using the [derivedStateOf] method with the `calculation` argument of [derivedStateOf] `true` if
 *  the [ScrollState.value] of our [ScrollState] variable `scrollState` is equal to 0.
 *  - a [ProfileFab] is Composed at the bottom of the [BoxWithConstraints] whose `extended` argument
 *  is the `DerivedSnapshotState` wrapped [Boolean] variable `fabExtended` (which is `true` whenever
 *  the [ScrollState.value] of our [ScrollState] variable `scrollState` is equal to 0). Its `userIsMe`
 *  argument is the result of calling the [ProfileScreenState.isMe] method of our [ProfileScreenState]
 *  parameter [userData], its `modifier` argument is a [BoxScope.align] whose `alignment` argument
 *  is [Alignment.BottomEnd] to align the fab to the bottom end of the [BoxWithConstraints] and the
 *  `onFabClicked` argument is a lambda which sets our [MutableState] wrapped [Boolean] variable
 *  `functionalityNotAvailablePopupShown` to `true` (this will cause [FunctionalityNotAvailablePopup]
 *  to be called which will pop up an [AlertDialog] displaying the message: "Functionality not available").
 *
 * @param userData the [ProfileScreenState] whose information we are supposed to display.
 * @param nestedScrollInteropConnection the [NestedScrollConnection] that we pass to the
 * [Modifier.nestedScroll] modifier of our [BoxWithConstraints] to have it participate in the nested
 * scroll hierarchy and to receive nested scroll events when they are dispatched by its scrolling
 * child (scrolling child - the element that actually receives scrolling events and dispatches them
 * via [NestedScrollDispatcher]).
 */
@Composable
fun ProfileScreen(
    userData: ProfileScreenState,
    nestedScrollInteropConnection: NestedScrollConnection = rememberNestedScrollInteropConnection()
) {
    var functionalityNotAvailablePopupShown: Boolean by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }

    val scrollState: ScrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(connection = nestedScrollInteropConnection)
            .systemBarsPadding()
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = scrollState),
            ) {
                ProfileHeader(
                    scrollState = scrollState,
                    data = userData,
                    containerHeight = this@BoxWithConstraints.maxHeight
                )
                UserInfoFields(
                    userData = userData,
                    containerHeight = this@BoxWithConstraints.maxHeight
                )
            }
        }

        val fabExtended: Boolean by remember { derivedStateOf { scrollState.value == 0 } }
        ProfileFab(
            extended = fabExtended,
            userIsMe = userData.isMe(),
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                // Offsets the FAB to compensate for CoordinatorLayout collapsing behaviour
                .offset(y = ((-100).dp)),
            onFabClicked = { functionalityNotAvailablePopupShown = true }
        )
    }
}

/**
 * This Composable displays the fields in its [ProfileScreenState] parameter [userData] which provide
 * information about the user in a [Column]. The `content` of the [Column] consists of:
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] that sets its `height` to 8.dp
 *  - a [NameAndPosition] Composable whose `userData` argument is our [ProfileScreenState] parameter
 *  [userData] (which displays a [Name] Composable and a [Position] Composable in a column).
 *  - a [ProfileProperty] whose `label` argument is the [String] with resource ID [R.string.display_name]
 *  ("Display name"), and whose `value` argument is the [ProfileScreenState.displayName] field of our
 *  [ProfileScreenState] parameter [userData].
 *  - a [ProfileProperty] whose `label` argument is the [String] with resource ID [R.string.status]
 *  ("Status"), and whose `value` argument is the [ProfileScreenState.status] field of our
 *  [ProfileScreenState] parameter [userData].
 *  - a [ProfileProperty] whose `label` argument is the [String] with resource ID [R.string.twitter]
 *  ("Twitter"), and whose `value` argument is the [ProfileScreenState.twitter] field of our
 *  [ProfileScreenState] parameter [userData].
 *  - if the [ProfileScreenState.timeZone] field of [userData] is not `null`, a [ProfileProperty]
 *  whose `label` argument is the [String] with resource ID [R.string.timezone] ("Timezone"),
 *  and whose `value` argument is the [ProfileScreenState.timeZone] field of our [ProfileScreenState]
 *  parameter [userData].
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] that sets its `height` to our [Dp]
 *  parameter [containerHeight] minus 320.bp coerced to be at least 0.dp.
 *
 * @param userData the [ProfileScreenState] containing the information we are to disply.
 * @param containerHeight the maximum height contraint of the [BoxWithConstraints] that contains us.
 */
@Composable
private fun UserInfoFields(userData: ProfileScreenState, containerHeight: Dp) {
    Column {
        Spacer(modifier = Modifier.height(height = 8.dp))

        NameAndPosition(userData = userData)

        ProfileProperty(label = stringResource(R.string.display_name), value = userData.displayName)

        ProfileProperty(label = stringResource(R.string.status), value = userData.status)

        ProfileProperty(label = stringResource(R.string.twitter), value = userData.twitter, isLink = true)

        userData.timeZone?.let {
            ProfileProperty(label = stringResource(R.string.timezone), value = userData.timeZone)
        }

        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
        // in order to always leave some content at the top.
        Spacer(modifier = Modifier.height(height = (containerHeight - 320.dp).coerceAtLeast(0.dp)))
    }
}

/**
 * This Composable displays the [ProfileScreenState.name] and [ProfileScreenState.position] of its
 * [ProfileScreenState] parameter [userData]. Its root Composable is a [Column] whose `modifier`
 * argument is a [Modifier.padding] that adds 16.dp to both sides of the [Column], and the `content`
 * of the [Column] is:
 *  - a [Name] Composable whose `userData` argument is our [ProfileScreenState] parameter [userData],
 *  and whose `modifier` argument is a [Modifier.baselineHeight] whose `heightFromBaseline` argument
 *  will set the `height` of any [Text] using the [Modifier] to 32.dp measured from its [FirstBaseline]
 *  - a [Position] Composable whose `userData` argument is our [ProfileScreenState] parameter [userData],
 *  and whose `modifier` argument is a [Modifier.padding] that adds 20.dp padding to the bottom of any
 *  Composable using the [Modifier], and chained to that is a [Modifier.baselineHeight] whose
 *  `heightFromBaseline` argument will set the `height` of any [Text] using the [Modifier] to 24.dp
 *  measured from its [FirstBaseline]
 *
 * @param userData the [ProfileScreenState] whose [ProfileScreenState.name] and [ProfileScreenState.position]
 * fields we are to display (using [Name] and [Position] Composables).
 */
@Composable
private fun NameAndPosition(
    userData: ProfileScreenState
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Name(
            userData = userData,
            modifier = Modifier.baselineHeight(heightFromBaseline = 32.dp)
        )
        Position(
            userData = userData,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .baselineHeight(heightFromBaseline = 24.dp)
        )
    }
}

/**
 * Displays the [ProfileScreenState.name] field our our [ProfileScreenState] parameter [userData] in
 * a [Text]. Our root Composable is a [Text] whose `text` argument is the [ProfileScreenState.name]
 * field our our [ProfileScreenState] parameter [userData], whose `modifier` argument is our [Modifier]
 * parameter [modifier], and whose [TextStyle] `style` argument is the [Typography.headlineSmall]
 * of our custom [MaterialTheme.typography].
 *
 * @param userData the [ProfileScreenState] whose [ProfileScreenState.name] field we are to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [NameAndPosition] passes us a [Modifier.baselineHeight] whose `heightFromBaseline`
 * argument will set our `height` of our [Text] to 32.dp measured from its [FirstBaseline]
 */
@Composable
private fun Name(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    Text(
        text = userData.name,
        modifier = modifier,
        style = MaterialTheme.typography.headlineSmall
    )
}

/**
 * Displays the [ProfileScreenState.position] field our our [ProfileScreenState] parameter [userData]
 * in a [Text]. Our root Composable is a [Text] whose `text` argument is the [ProfileScreenState.position]
 * field our our [ProfileScreenState] parameter [userData], whose `modifier` argument is our [Modifier]
 * parameter [modifier], whose [TextStyle] `style` argument is the [Typography.bodyLarge] of our custom
 * [MaterialTheme.typography], and whose [Color] argument `color` is the [ColorScheme.onSurfaceVariant]
 * of our custom [MaterialTheme.colorScheme].
 *
 * @param userData the [ProfileScreenState] whose [ProfileScreenState.position] field we are to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [NameAndPosition] passes us a [Modifier.padding] that adds 20.dp padding to
 * the bottom of our [Text], and chained to that is a [Modifier.baselineHeight] whose `heightFromBaseline`
 * argument will set the `height` of our [Text] to 24.dp measured from its [FirstBaseline].
 */
@Composable
private fun Position(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    Text(
        text = userData.position,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * This Composable displays the png whose resource ID is in the [ProfileScreenState.photo] field of
 * our [ProfileScreenState] parameter [data] (if it is not `null`). We start by initializing our [Int]
 * variable `val offset` to half the [ScrollState.value] of our [ScrollState] parameter [scrollState].
 * Then we initialize our [Dp] variable `val offsetDp` to the [Dp] value of `offset` pixels given the
 * `current` [LocalDensity]. Then if the [ProfileScreenState.photo] field of our [ProfileScreenState]
 * parameter [data] is not `null` we use the [Int] value of that field when we compose an [Image] into
 * th UI whose `modifier` argument is a [Modifier.heightIn] whose `max` argument is one half of our
 * [Dp] parameter [containerHeight] (constrains our content to half of [containerHeight]), with a
 * [Modifier.fillMaxWidth] chained to that which causes us to occupy our entire incoming width
 * constraint, with a [Modifier.padding] that adds 16.dp to the `start`, `offsetDp` to the `top`,
 * and 16.dp to the `end`, with a [Modifier.clip] chained to that that clips the [Image] to the
 * `shape` [CircleShape]. The `painter` of the [Image] is a [Painter] for the drawable with the
 * the resource ID contained in the [ProfileScreenState.photo] field of [data], the `contentScale`
 * argument is [ContentScale.Crop] (scales the source uniformly (maintaining the source's aspect
 * ratio) so that both dimensions (width and height) of the source will be equal to or larger than
 * the corresponding dimension of the destination).
 *
 * @param scrollState the [ScrollState] of the [Modifier.verticalScroll] used by the [Column] that
 * contains us.
 * @param data the [ProfileScreenState] of the user whose [ProfileScreenState.photo] field resource
 * ID we are supposed to have our [Image].
 * @param containerHeight the [BoxWithConstraintsScope.maxHeight] (maximum height in [Dp]) of the
 * [BoxWithConstraintsScope] that holds the [Surface] that holds the [Column] that holds us.
 */
@Composable
private fun ProfileHeader(
    scrollState: ScrollState,
    data: ProfileScreenState,
    containerHeight: Dp
) {
    val offset: Int = (scrollState.value / 2)
    val offsetDp: Dp = with(LocalDensity.current) { offset.toDp() }

    data.photo?.let { resourceId: Int ->
        Image(
            modifier = Modifier
                .heightIn(max = containerHeight / 2)
                .fillMaxWidth()
                // TODO: Update to use offset to avoid recomposition
                .padding(
                    start = 16.dp,
                    top = offsetDp,
                    end = 16.dp
                )
                .clip(shape = CircleShape),
            painter = painterResource(id = resourceId),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}

/**
 * This Composable is used to display the two [String] parameters [label] and [value] in separate
 * [Text] Composables, and if our [Boolean] parameter [isLink] is `true` the [Color] of the `text`
 * that displays [value] will be the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme].
 * Our root Composable is a [Column] whose `modifier` argument is a [Modifier.padding] that adds
 * 16.dp to the `start`, 16.dp to the `end`, and 16.dp to the `bottom`. The `content` of the [Column]
 * is:
 *  - a [HorizontalDivider] with no arguments which causes it to use its defaults: `modifier` = [Modifier],
 *  `thickness` = 1.0.dp, and `color` = the [ColorScheme.outlineVariant] of our custom
 *  [MaterialTheme.colorScheme]
 *  - a [Text] whose `text` argument is our [String] parameter [label], whose `modifier` argument is
 *  a [Modifier.baselineHeight] that sets the distance between the `top` and the first baseline to
 *  24.dp, whose [TextStyle] `style` argument is the [Typography.bodySmall] of our custom
 *  [MaterialTheme.typography], whose [Color] `color` argument is the [ColorScheme.onSurfaceVariant]
 *  of our custom [MaterialTheme.colorScheme].
 *  - we initialize our [TextStyle] variable `val style` depending on the value of our [Boolean]
 *  parameter [isLink]: if [isLink] is `true` to a copy of the [Typography.bodyLarge] of our custom
 *  [MaterialTheme.typography] with its [Color] `color` property set to the [ColorScheme.primary]
 *  of our custom [MaterialTheme.colorScheme], and if [isLink] is `false` to the [Typography.bodyLarge]
 *  of our custom [MaterialTheme.typography] with none of its defaults overridden.
 *  - a [Text] whose `text` argument is our [String] parameter [value], whose `modifier` argument is
 *  a [Modifier.baselineHeight] that sets the distance between the `top` and the first baseline to
 *  24.dp, whose [TextStyle] `style` argument is our [TextStyle] variable `style`.
 *
 * @param label the [String] to use as the `text` of the first [Text] in our [Column]
 * @param value the [String] to use as the `text` of the second [Text] in our [Column]
 * @param isLink if `true` the [Color] of the text in the second [Text] in our [Column] is set to
 * the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme].
 */
@Composable
fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        HorizontalDivider()
        Text(
            text = label,
            modifier = Modifier.baselineHeight(heightFromBaseline = 24.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val style: TextStyle = if (isLink) {
            MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        } else {
            MaterialTheme.typography.bodyLarge
        }
        Text(
            text = value,
            modifier = Modifier.baselineHeight(heightFromBaseline = 24.dp),
            style = style
        )
    }
}

/**
 * [ProfileFragment] composes this into the UI instead of [ProfileScreen] if the [ProfileScreenState]
 * that it is supposed to display is `null`. We just display a [Text] whose `text` argument is the
 * [String] with resource ID [R.string.profile_error] ("There was an error loading the profile").
 */
@Composable
fun ProfileError() {
    Text(text = stringResource(id = R.string.profile_error))
}

/**
 * This is the custom [FloatingActionButton] that is displayed at the [Alignment.BottomEnd] of the
 * [BoxWithConstraints] used by [ProfileScreen]. Its width is animated based on our [Boolean] parameter
 * [extended] by the [AnimatingFabContent] in the `content` lambda of our [FloatingActionButton].
 * [extended] is `true` when the [ScrollState.value] of the [ScrollState] used to scroll the [Column]
 * used in [ProfileScreen] is 0 (the top of the content of the [Column] is at the top of the [Column])
 * and [AnimatingFabContent] will animate the width of the FAB it displays between its full width when
 * [extended] to only as wide as it its height when `false` whenever [extended] changes value.
 *
 * Within the `block` argument of a [key] whose `keys` is our [Boolean] parameter [userIsMe] (that is
 * used to prevent the execution of multiple invocations during composition) is a [FloatingActionButton]
 * whose `onClick` argument is our [onFabClicked] lambda parameter, whose `modifier` argument chains
 * to our [Modifier] parameter [modifier] a [Modifier.padding] that adds 16.dp to all sides, followed
 * by a [Modifier.navigationBarsPadding] to add padding to accommodate the navigation bars insets,
 * followed by a [Modifier.height] that sets its `height` to 48.dp, followed by a [Modifier.widthIn]
 * that sets its `min` width to 48.dp. Its `containerColor` [Color] argument is the
 * [ColorScheme.tertiaryContainer] of our custom [MaterialTheme.colorScheme].
 *
 * The `content` of the [FloatingActionButton] is an [AnimatingFabContent] whose `icon` argument is
 * an [Icon] whose `imageVector` is if our [Boolean] parameter [userIsMe] is `true` the [ImageVector]
 * drawn by [Icons.Outlined.Create] and if it is `false` the [ImageVector] drawn by [Icons.Outlined.Create],
 * and whose `contentDescription` argument is the [String] with resource ID [R.string.edit_profile]
 * ("Edit Profile") if [userIsMe] is `true` or the [String] with resource ID [R.string.message]
 * ("Message") if [userIsMe] is `false`. The `text` argument of the [AnimatingFabContent] is the
 * [String] with resource ID [R.string.edit_profile] ("Edit Profile") if [userIsMe] is `true` or the
 * [String] with resource ID [R.string.message] ("Message") if [userIsMe] is `false`, and the
 * `extended` argument is our [Boolean] parameter [extended].
 *
 * @param extended if `true` our [AnimatingFabContent] will be rendered at its full width (an [Icon]
 * and [Text]), and if `false` it will just be a 48.dp by 48.dp [Icon].
 * @param userIsMe if `true` the profile is for "Ali Conors (you)" and the [Text] of the FAB would
 * display the  [String] with resource ID [R.string.edit_profile] ("Edit Profile") and if `false`
 * the [Text] of the FAB would display the  [String] with resource ID [R.string.message] ("Message").
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [ProfileScreen] passes us a [BoxScope.align] whose `alignment` argument
 * is [Alignment.BottomEnd] to have us align our fab to the bottom end of the [BoxWithConstraints]
 * Composable it uses as its root Composable.
 * @param onFabClicked a lambda we should call when our [FloatingActionButton] is clicked. Our caller
 * [ProfileScreen] passes us a lambda which sets its [MutableState] wrapped [Boolean] variable
 * `functionalityNotAvailablePopupShown` to `true` which causes a call to the Composable
 * [FunctionalityNotAvailablePopup] which pops up an [AlertDialog] displaying the message:
 * "Functionality not available"
 */
@Composable
fun ProfileFab(
    extended: Boolean,
    userIsMe: Boolean,
    modifier: Modifier = Modifier,
    onFabClicked: () -> Unit = { }
) {
    key(userIsMe) { // Prevent multiple invocations to execute during composition
        FloatingActionButton(
            onClick = onFabClicked,
            modifier = modifier
                .padding(all = 16.dp)
                .navigationBarsPadding()
                .height(height = 48.dp)
                .widthIn(min = 48.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            AnimatingFabContent(
                icon = {
                    Icon(
                        imageVector = if (userIsMe) Icons.Outlined.Create else Icons.AutoMirrored.Outlined.Chat,
                        contentDescription = stringResource(
                            if (userIsMe) R.string.edit_profile else R.string.message
                        )
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = if (userIsMe) R.string.edit_profile else R.string.message
                        ),
                    )
                },
                extended = extended
            )
        }
    }
}

/**
 * This is a Preview of [ProfileScreen] displaying [meProfile] with a `widthDp` of 640 and a `heightDp` of 360
 */
@Preview(widthDp = 640, heightDp = 360)
@Composable
fun ConvPreviewLandscapeMeDefault() {
    JetchatTheme {
        ProfileScreen(meProfile)
    }
}

/**
 * This is a Preview of [ProfileScreen] displaying [meProfile] with a `widthDp` of 360 and a `heightDp` of 480
 */
@Preview(widthDp = 360, heightDp = 480)
@Composable
fun ConvPreviewPortraitMeDefault() {
    JetchatTheme {
        ProfileScreen(meProfile)
    }
}

/**
 * This is a Preview of [ProfileScreen] displaying [colleagueProfile] with a `widthDp` of 360 and a `heightDp` of 480
 */
@Preview(widthDp = 360, heightDp = 480)
@Composable
fun ConvPreviewPortraitOtherDefault() {
    JetchatTheme {
        ProfileScreen(colleagueProfile)
    }
}

/**
 * This is a Preview of [ProfileFab] with its `extended` argument `true` and its `userIsMe` argument
 * `false`.
 */
@Preview
@Composable
fun ProfileFabPreview() {
    JetchatTheme {
        ProfileFab(extended = true, userIsMe = false)
    }
}
