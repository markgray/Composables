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

@file:Suppress("DEPRECATION")

package com.example.owl.ui.course

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
// TODO: Material's Swipeable has been replaced by Foundation's AnchoredDraggable
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.owl.R
import com.example.owl.model.Course
import com.example.owl.model.CourseRepo
import com.example.owl.model.Lesson
import com.example.owl.model.LessonsRepo
import com.example.owl.model.courses
import com.example.owl.ui.common.CourseListItem
import com.example.owl.ui.common.OutlinedAvatar
import com.example.owl.ui.theme.BlueTheme
import com.example.owl.ui.theme.PinkTheme
import com.example.owl.ui.theme.pink500
import com.example.owl.ui.utils.NetworkImage
import com.example.owl.ui.utils.lerp
import com.example.owl.ui.utils.scrim
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * The size of the FAB in [Dp]. It is used by the calculations of how the FAB should transition
 * into the full screen sheet of lessons, and where the FAB should be located in the closed
 * state.
 */
private val FabSize = 56.dp

/**
 * The alpha of the sheet when it is expanded.
 */
private const val ExpandedSheetAlpha = 0.96f

/**
 * This component shows the details of a [Course] when called with the [Course.id] of the [Course].
 *
 * We start by initializing and remembering our [Course] variable `course` to the [Course] that the
 * [CourseRepo.getCourse] method returns when its [courseId] argument is our [Long] parameter
 * [courseId]. Then we compose the [CourseDetails] overload whose arguments are:
 *  - `course`: is our [Course] parameter `course`.
 *  - `selectCourse`: is our lambda parameter [selectCourse].
 *  - `upPress`: is our lambda parameter [upPress].
 *
 * @param courseId the ID of the [Course] that we want to show the details of.
 * @param selectCourse (event) request to navigate to a course.
 * @param upPress (event) request to navigate up.
 */
@Composable
fun CourseDetails(
    courseId: Long,
    selectCourse: (Long) -> Unit,
    upPress: () -> Unit
) {
    // Simplified for the sample
    val course: Course = remember(key1 = courseId) { CourseRepo.getCourse(courseId = courseId) }
    // TODO: Show error if course not found.
    CourseDetails(course = course, selectCourse = selectCourse, upPress = upPress)
}

/**
 * This is the main screen for displaying the details of a [Course]. It is composed of a
 * [CourseDescription] a [LessonsSheet] that can be swiped up by the user, a [BackHandler]
 * that will close the [LessonsSheet] if it is open, or navigate up if it is closed, and a
 * [Box] that acts as a swipeable container for the [LessonsSheet]. The [Box] uses
 * [Modifier.swipeable] to control the [SwipeableState] variable `sheetState` which can be
 * either [SheetState.Closed] or [SheetState.Open]. The `openFraction` of the sheet (how much
 * of it is visible) is calculated based on the `offset` of `sheetState` and the `dragRange`
 * that the sheet can be dragged.
 *
 * Our root composable is a [BoxWithConstraints] wrapped in our [PinkTheme] custom [MaterialTheme].
 * In the [BoxWithConstraintsScope] `content` composable lambda argument, we start by initializing
 * and remembering our [SwipeableState] of [SheetState] variable `sheetState` to an initial value
 * of [SheetState.Closed]. We then initialize our [Float] variable `fabSize` to the pixel value of
 * our [Dp] constant [FabSize]. We then initialize our [Float] variable `dragRange` to the
 * [Constraints.maxHeight] of our [BoxWithConstraintsScope] `constraints` minus `fabSize`.
 * We initialize and remember our [CoroutineScope] variable `scope` to the instance returned by
 * [rememberCoroutineScope].
 *
 * Next we compose a [BackHandler] whose arguments are:
 *  - `enabled`: to `true` if `sheetState` is [SheetState.Open] and `false` otherwise.
 *  - `onBack`: is a lambda that calls the [CoroutineScope.launch] method of `scope` to launch a
 *  coroutine that calls the [SwipeableState.animateTo] method of `sheetState` to animate to the
 *  `targetValue` of [SheetState.Closed].
 *
 * Then our root composable is a [Box] whose `modifier` argument is a [Modifier.swipeable] whose
 * arguments are:
 *  - `state`: is our [SwipeableState] variable `sheetState`.
 *  - `anchors`: is a [mapOf] `0f` to [SheetState.Closed] and `-dragRange` to [SheetState.Open].
 *  - `thresholds`: is a lambda that returns a [FractionalThreshold] whose `fraction` argument is
 *  `0.5f`.
 *  - `orientation`: is [Vertical].
 *
 * In the [BoxScope] `content` composable lambda argument of the [Box], we start by initializing our
 * [Float] variable `openFraction` to `0f` if `sheetState.offset.value` is [Float.NaN] or minus
 * `sheetState.offset.value` divided by `dragRange` or `0f` if `sheetState.offset.value` is not
 * [Float.NaN]. Then we coerce the result to be between `0f` and `1f`.
 *
 * Then we compose a [CourseDescription] whose arguments are:
 *  - `course`: is our [Course] parameter [course].
 *  - `selectCourse`: is our lambda parameter [selectCourse].
 *  - `upPress`: is our lambda parameter [upPress].
 *
 * On top of that in the [BoxScope] `content` composable lambda argument, we compose a [LessonsSheet]
 * whose arguments are:
 *  - `course`: is our [Course] parameter [course].
 *  - `openFraction`: is our [Float] variable `openFraction`.
 *  - `width`: is the [Constraints.maxWidth] of our [BoxWithConstraintsScope] `constraints`.
 *  - `height`: is the [Constraints.maxHeight] of our [BoxWithConstraintsScope] `constraints`.
 *
 * In the `updateSheet` lambda argument of the [LessonsSheet], we accept the [SheetState] passed the
 * lambda in variable `state` and call the [CoroutineScope.launch] method of `scope` to launch a
 * coroutine that calls the [SwipeableState.animateTo] method of `sheetState` to animate to the
 * `targetValue` of `state`.
 *
 * @param course the [Course] whose details we are to display.
 * @param selectCourse a lambda that takes the ID of a [Course] as its Long parameter that the
 * [CourseDescription] composable can call to navigate to a related course.
 * @param upPress a lambda that the [CourseDescription] composable can call to navigate up from
 * this screen.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CourseDetails(
    course: Course,
    selectCourse: (Long) -> Unit,
    upPress: () -> Unit
) {
    PinkTheme {
        BoxWithConstraints {
            // TODO: Material's Swipeable has been replaced by Foundation's AnchoredDraggable
            val sheetState: SwipeableState<SheetState> = rememberSwipeableState(SheetState.Closed)
            val fabSize: Float = with(LocalDensity.current) { FabSize.toPx() }
            val dragRange: Float = constraints.maxHeight - fabSize
            val scope: CoroutineScope = rememberCoroutineScope()

            BackHandler(
                enabled = sheetState.currentValue == SheetState.Open,
                onBack = {
                    scope.launch {
                        sheetState.animateTo(targetValue = SheetState.Closed)
                    }
                }
            )

            Box(
                // The Lessons sheet is initially closed and appears as a FAB. Make it openable by
                // swiping or clicking the FAB.
                // TODO: Material's Swipeable has been replaced by Foundation's AnchoredDraggable
                modifier = Modifier.swipeable(
                    state = sheetState,
                    anchors = mapOf(
                        0f to SheetState.Closed,
                        -dragRange to SheetState.Open
                    ),
                    thresholds = { _, _ -> FractionalThreshold(fraction = 0.5f) },
                    orientation = Vertical
                )
            ) {
                val openFraction: Float = if (sheetState.offset.value.isNaN()) {
                    0f
                } else {
                    -sheetState.offset.value / dragRange
                }.coerceIn(minimumValue = 0f, maximumValue = 1f)
                CourseDescription(course = course, selectCourse = selectCourse, upPress = upPress)
                LessonsSheet(
                    course = course,
                    openFraction = openFraction,
                    width = this@BoxWithConstraints.constraints.maxWidth.toFloat(),
                    height = this@BoxWithConstraints.constraints.maxHeight.toFloat()
                ) { state: SheetState ->
                    scope.launch {
                        sheetState.animateTo(targetValue = state)
                    }
                }
            }
        }
    }
}

/**
 * This is the Course Description screen. It contains a header, a body, and a list of related
 * courses.
 *
 * Our root composable is a [Surface] whose `modifier` argument is [Modifier.fillMaxSize]. In its
 * `content` composable lambda argument, we compose a [LazyColumn] whose [LazyListScope] `content`
 * composable lambda argument contains:
 *
 * **First**: a [LazyListScope.item] whose [LazyItemScope] `content` composable lambda argument
 * composes a [CourseDescriptionHeader] whose arguments are:
 *  - `course`: is our [Course] parameter [course].
 *  - `upPress`: is our lambda parameter [upPress].
 *
 * **Second**: a [LazyListScope.item] whose [LazyItemScope] `content` composable lambda argument
 * composes a [CourseDescriptionBody] whose `course` argument is our [Course] parameter [course].
 *
 * **Third**: a [LazyListScope.item] whose [LazyItemScope] `content` composable lambda argument
 * composes a [RelatedCourses] whose arguments are:
 *  - `courseId`: is the [Course.id] of our [Course] parameter [course].
 *  - `selectCourse`: is our lambda parameter [selectCourse].
 *
 * @param course The [Course] to display.
 * @param selectCourse (event) A course has been selected from the related courses.
 * @param upPress (event) The user has pressed the "up" button.
 */
@Composable
private fun CourseDescription(
    course: Course,
    selectCourse: (Long) -> Unit,
    upPress: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            item { CourseDescriptionHeader(course = course, upPress = upPress) }
            item { CourseDescriptionBody(course = course) }
            item { RelatedCourses(courseId = course.id, selectCourse = selectCourse) }
        }
    }
}

/**
 * This is the header of the [CourseDescription].
 *
 * Our root composable is a [Box]. In the [BoxScope] `content` composable lambda argument of the [Box]
 * we compose:
 *
 * **First**: a [NetworkImage] whose arguments are:
 *  - `url`: is the [Course.thumbUrl] of our [Course] parameter [course].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.scrim] whose `colors` argument
 *  is a [listOf] `Color(0x80000000)` and `Color(0x33000000)` and that is chained to a
 *  [Modifier.aspectRatio] whose `ratio` argument is `4f / 3f`.
 *
 * **Second**: a [TopAppBar] whose arguments are:
 *  - `backgroundColor`: is [Color.Transparent].
 *  - `elevation`: is `0.dp`.
 *  - `contentColor`: is [Color.White].
 *  - `modifier`: is a [Modifier.statusBarsPadding].
 *
 * In the [RowScope] `content` composable lambda argument of the [TopAppBar], we compose:
 *
 * **First**:
 * An [IconButton] whose `onClick` argument is our lambda parameter [upPress], and whose `content`
 * composable lambda argument composes an [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.AutoMirrored.Rounded.ArrowBack].
 *  - `contentDescription`: is the string resource with id `R.string.label_back`. ("Back")
 *
 * **Second**:
 * An [Image] whose arguments are:
 *  - `painter`: is the [Painter] that [painterResource] creates for the drawable with resource id
 *  `R.drawable.ic_logo`.
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.padding] that adds `4.dp` to the `bottom`, chained to a
 *  [Modifier.size] whose `size` argument is `24.dp`, and that is chained to a [RowScope.align]
 *  whose `alignment` argument is [Alignment.CenterVertically].
 *
 * **Third**:
 * A [Spacer] whose `modifier` argument is a [RowScope.weight] whose `weight` argument is `1f`.
 *
 * On the very top of the composables in the [Box] we compose an [OutlinedAvatar] whose arguments are:
 *  - `url`: is the [Course.instructor] of our [Course] parameter [course].
 *  - `modifier`: is a [Modifier.size] whose `size` argument is `40.dp`, chained to a
 *  [RowScope.align] whose `alignment` argument is [Alignment.BottomCenter], and that is chained to
 *  a [Modifier.offset] whose `y` argument is `20.dp` (to overlap the bottom of the [Image]).
 *
 * @param course The [Course] to display.
 * @param upPress called when the "up" button is pressed.
 */
@Composable
private fun CourseDescriptionHeader(
    course: Course,
    upPress: () -> Unit
) {
    Box {
        NetworkImage(
            url = course.thumbUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .scrim(colors = listOf(Color(0x80000000), Color(0x33000000)))
                .aspectRatio(ratio = 4f / 3f)
        )
        TopAppBar(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            contentColor = Color.White, // always white as image has dark scrim
            modifier = Modifier.statusBarsPadding()
        ) {
            IconButton(onClick = upPress) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.label_back)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .size(size = 24.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
        }
        OutlinedAvatar(
            url = course.instructor,
            modifier = Modifier
                .size(size = 40.dp)
                .align(alignment = Alignment.BottomCenter)
                .offset(y = 20.dp) // overlap bottom of image
        )
    }
}

/**
 * Displays the body of the course description.
 *
 * This composable function displays the course subject in uppercase, the course name,
 * a course description, a divider, a "What you'll need" section, and the needs for the course.
 * All text elements are centered and styled according to the MaterialTheme.
 *
 * **First**: We compose a [Text] whose arguments are:
 *  - `text`: is the [Course.subject] of our [Course] parameter [course] with all characters
 *  uppercase.
 *  - `color`: is the [Colors.primary] of our custom [MaterialTheme.colors].
 *  - `style`: is the [TextStyle] of [Typography.body2] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.Center].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `16.dp` to
 *  the `start`, `36.dp` to the `top`, `16.dp` to the `end` and `16.dp` to the `bottom`.
 *
 * **Second**: We compose a [Text] whose arguments are:
 *  - `text`: is the [Course.name] of our [Course] parameter [course].
 *  - `style`: is the [TextStyle] of [Typography.h4] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.Center].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `16.dp`
 *  to the `horizontal` sides.
 *
 * **Third**: We compose a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height`
 * argument is `16.dp`.
 *
 * **Fourth**: We compose a [CompositionLocalProvider] that provides [ContentAlpha.medium] for the
 * [LocalContentAlpha] to its `content` composable lambda argument, a [Text] whose arguments are:
 *  - `text`: is the string resource with id `R.string.course_desc`. ("This video course introduces
 *  the photography of...")
 *  - `style`: is the [TextStyle] of [Typography.body1] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.Center].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `16.dp`
 *  to `all` sides.
 *
 * **Fifth**: We compose a [Divider] whose `modifier` argument is a [Modifier.padding] that adds
 * `16.dp` to `all` sides.
 *
 * **Sixth**: We compose a [Text] whose arguments are:
 *  - `text`: is the string resource with id `R.string.what_you_ll_need`. ("What you'll need")
 *  - `style`: is the [TextStyle] of [Typography.h6] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.Center].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `16.dp`
 *  to `all` sides.
 *
 * **Seventh**: We compose a [CompositionLocalProvider] that provides [ContentAlpha.medium] for
 * the [LocalContentAlpha] to its `content` composable lambda argument, a [Text] whose arguments are:
 *  - `text`: is the string resource with id `R.string.needs` ("• DSLR or manual camera • 24mm wide
 *  angle lens • Tripod")
 *  - `style`: is the [TextStyle] of [Typography.body1] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.Center].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `16.dp`
 *  padding to the `start`, `16.dp` padding to the `top`, `16.dp` padding to the `end`, and `32.dp`
 *  to the `bottom`.
 *
 * @param course The [Course] object containing the details to be displayed.
 */
@Composable
private fun CourseDescriptionBody(course: Course) {
    Text(
        text = course.subject.uppercase(locale = Locale.getDefault()),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.body2,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 36.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    )
    Text(
        text = course.name,
        style = MaterialTheme.typography.h4,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(height = 16.dp))
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = stringResource(id = R.string.course_desc),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        )
    }
    Divider(modifier = Modifier.padding(all = 16.dp))
    Text(
        text = stringResource(id = R.string.what_you_ll_need),
        style = MaterialTheme.typography.h6,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    )
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = stringResource(id = R.string.needs),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 32.dp
                )
        )
    }
}

/**
 * Displays a list of related courses.
 *
 * This composable function fetches a list of courses related to the given `courseId`
 * and displays them in a horizontal scrolling list. Each item in the list is a
 * [CourseListItem] that, when clicked, invokes the [selectCourse] callback with the
 * [Course.id] of the selected course.
 *
 * The UI is themed with [BlueTheme] and consists of a title "You'll also like"
 * followed by the [LazyRow] of related courses.
 *
 * First we initialize and remember (with `key1` our [Long] parameter [courseId]) our [List] of
 * [Course] variable `relatedCourses` to the [List] returned by the [CourseRepo.getRelated] method
 * when called with our [Long] parameter [courseId] as its `courseId` argument.
 *
 * Our root composable is a [Surface] wrapped in our [BlueTheme] custom [MaterialTheme]. The
 * [Surface]'s `color` argument is the [Colors.primarySurface] of our custom [MaterialTheme.colors],
 * and its `modifier` argument is a [Modifier.fillMaxWidth]. In its `content` composable lambda
 * argument we compose a [Column] whose `modifier` argument is a [Modifier.navigationBarsPadding]
 * to add padding to accommodate the navigation bars insets. In the [ColumnScope] `content` composable
 * lambda argument of the [Column] we compose:
 *
 * **First**: We compose a [Text] whose arguments are:
 *  - `text`: is the string resource with id `R.string.you_ll_also_like`. ("You'll also like")
 *  - `style`: is the [TextStyle] of [Typography.h6] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.Center].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `16.dp`
 *  to the `horizontal` sides and `24.dp` to the `vertical` sides.
 *
 * **Second**: We compose a [LazyRow] whose `contentPadding` argument is a [PaddingValues] that adds
 * `16.dp` padding to the `start`, `32.dp` padding to the `bottom`, and `FabSize + 8.dp` padding to
 * the `end`. In the [LazyListScope] `content` composable lambda argument of the [LazyRow] we compose
 * a [LazyListScope.items] whose `items` argument is our [List] of [Course] variable `relatedCourses`,
 * and whose `key` argument is the [Course.id] of each [Course] in the [List]. In the [LazyItemScope]
 * `content` composable lambda argument of the [items] we accept the [Course] passed the lambda in
 * variable `related` and then compose a [CourseListItem] whose arguments are:
 *  - `course`: is the [Course] variable `related`.
 *  - `onClick`: is a lambda that invokes our [selectCourse] lambda parameter with the [Course.id]
 *  of the [Course] variable `related`.
 *  - `titleStyle`: is the [TextStyle] of [Typography.body2] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] that adds `8.dp` padding to the `end`, chained to a
 *  [Modifier.size] whose `width` argument is `288.dp` and `height` argument is `80.dp`.
 *  - `iconSize`: is `14.dp`.
 *
 * @param courseId The ID of the current course, used to fetch related courses.
 * @param selectCourse A lambda function that is invoked when a related course is selected.
 * It receives the ID of the selected course as a [Long].
 */
@Composable
private fun RelatedCourses(
    courseId: Long,
    selectCourse: (Long) -> Unit
) {
    val relatedCourses: List<Course> =
        remember(key1 = courseId) { CourseRepo.getRelated(courseId = courseId) }
    BlueTheme {
        Surface(
            color = MaterialTheme.colors.primarySurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.navigationBarsPadding()) {
                Text(
                    text = stringResource(id = R.string.you_ll_also_like),
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 24.dp
                        )
                )
                LazyRow(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        bottom = 32.dp,
                        end = FabSize + 8.dp
                    )
                ) {
                    items(
                        items = relatedCourses,
                        key = { it.id }
                    ) { related: Course ->
                        CourseListItem(
                            course = related,
                            onClick = { selectCourse(related.id) },
                            titleStyle = MaterialTheme.typography.body2,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(width = 288.dp, height = 80.dp),
                            iconSize = 14.dp
                        )
                    }
                }
            }
        }
    }
}

/**
 * A sheet that displays a list of lessons for a [Course].
 *
 * This composable function creates a [Surface] that can transform from a FAB-like appearance
 * to a full-screen sheet. The transformation is driven by the [openFraction] parameter,
 * which represents how much of the sheet is open (0.0 for closed, 1.0 for fully open).
 *
 * The sheet's position, size, and corner radius are interpolated based on [openFraction]
 * to create a smooth animation. When the sheet is closed, it resembles a FAB. As it opens,
 * it expands to fill the screen, and its top-left corner becomes squared.
 *
 * The background color of the sheet also transitions from [pink500] (the FAB color) to
 * the `primarySurface` color of the current theme with a slight transparency.
 *
 * The actual content of the sheet (the list of lessons) is provided by the [Lessons] composable.
 *
 * We start by initializing our [Float] variable `fabSize` to the pixel value of [FabSize] for the
 * `current` [LocalDensity], then initialize our [Float] variable `fabSheetHeight` to the sum of
 * `fabSize` and the bottom insets of the [WindowInsets.Companion.systemBars] for the `current`
 * [LocalDensity].
 *
 * We then initialize our [Float] variable `offsetX` to the Linearly interpolated value the [lerp]
 * function returns for the following conditions:
 *  - `startValue`: is our [Float] parameter [width] minus our [Float] variable `fabSize`.
 *  - `endValue`: is 0.0.
 *  - `startFraction`: is 0.0.
 *  - `endFraction`: is 0.15.
 *  - `fraction`: is our [Float] parameter [openFraction].
 *
 * We then initialize our [Float] variable `offsetY` to the Linearly interpolated value the [lerp]
 * function returns for the following conditions:
 *  - `startValue`: is our [Float] parameter [height] minus `fabSheetHeight`
 *  - `endValue`: is 0.0.
 *  - `fraction`: is our [Float] parameter [openFraction].
 *
 * We then initialize our [Float] variable `tlCorner` to the Linearly interpolated value the [lerp]
 * function returns for the following conditions:
 *  - `startValue`: is our [Float] variable `fabSize`.
 *  - `endValue`: is 0.0.
 *  - `startFraction`: is 0.0.
 *  - `endFraction`: is 0.15.
 *  - `fraction`: is our [Float] parameter [openFraction].
 *
 * We then initialize our [Color] variable `surfaceColor` to the Linearly interpolated value the
 * [lerp] function returns for the following conditions:
 *  - `startColor`: is our global [Color] property [pink500].
 *  - `endColor`: is a copy of the [Colors.primarySurface] of our custom [MaterialTheme.colors] with
 *  its alpha set to [ExpandedSheetAlpha].
 *  - `startFraction`: is 0.0.
 *  - `endFraction`: is 0.3.
 *  - `fraction`: is our [Float] parameter [openFraction].
 *
 * Then our root composable is a [Surface] whose `color` argument is our animated [Color] variable
 * `surfaceColor`, its `contentColor` argument is the [Color] returned by the [contentColorFor]
 * function for our [Color] variable `surfaceColor`, its `shape` argument is a [RoundedCornerShape]
 * whose `topStart` argument is our [Float] variable `tlCorner`, and its `modifier` argument is a
 * [Modifier.graphicsLayer] that sets the `translationX` and `translationY` properties to our
 * [Float] variables `offsetX` and `offsetY`, respectively. In the `content` composable lambda
 * argument of the [Surface] we compose a [Lessons] composable whose arguments are:
 *  - `course`: is our [Course] parameter [course].
 *  - `openFraction`: is our [Float] parameter [openFraction].
 *  - `surfaceColor`: is our [Color] variable `surfaceColor`.
 *  - `updateSheet`: is our [updateSheet] lambda parameter.
 *
 * @param course The [Course] for which to display the lessons.
 * @param openFraction A float between 0.0 and 1.0 representing the fraction of the sheet
 * that is currently open. 0.0 means the sheet is closed (FAB-like), and 1.0 means it's fully open.
 * @param width The width of the parent container, used for calculating the sheet's position.
 * @param height The height of the parent container, used for calculating the sheet's position.
 * @param updateSheet A lambda function that will be called when the sheet's state needs to be
 * updated (e.g., when the user interacts with an element that should open or close the sheet).
 * It takes a [SheetState] as a parameter.
 */
@Composable
private fun LessonsSheet(
    course: Course,
    openFraction: Float,
    width: Float,
    height: Float,
    updateSheet: (SheetState) -> Unit
) {
    // Use the fraction that the sheet is open to drive the transformation from FAB -> Sheet
    val fabSize: Float = with(LocalDensity.current) { FabSize.toPx() }
    val fabSheetHeight: Float = fabSize + WindowInsets.systemBars.getBottom(LocalDensity.current)
    val offsetX: Float = lerp(
        startValue = width - fabSize,
        endValue = 0f,
        startFraction = 0f,
        endFraction = 0.15f,
        fraction = openFraction
    )
    val offsetY: Float = lerp(
        startValue = height - fabSheetHeight,
        endValue = 0f,
        fraction = openFraction
    )
    val tlCorner: Float = lerp(
        startValue = fabSize,
        endValue = 0f,
        startFraction = 0f,
        endFraction = 0.15f,
        fraction = openFraction
    )
    val surfaceColor = lerp(
        startColor = pink500,
        endColor = MaterialTheme.colors.primarySurface.copy(alpha = ExpandedSheetAlpha),
        startFraction = 0f,
        endFraction = 0.3f,
        fraction = openFraction
    )
    Surface(
        color = surfaceColor,
        contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.primarySurface),
        shape = RoundedCornerShape(topStart = tlCorner),
        modifier = Modifier.graphicsLayer {
            translationX = offsetX
            translationY = offsetY
        }
    ) {
        Lessons(
            course = course,
            openFraction = openFraction,
            surfaceColor = surfaceColor,
            updateSheet = updateSheet
        )
    }
}

/**
 * This composable displays a list of lessons for a [Course] or a FAB to open the list.
 *
 * When the sheet is closed ([openFraction] is 0f), a Floating Action Button (FAB) is shown,
 * which when clicked, calls `updateSheet(SheetState.Open)` to open the sheet.
 *
 * When the sheet is open ([openFraction] is 1f), a list of lessons is displayed.
 * This list includes a [TopAppBar] with the course name and an expand more icon to close
 * the sheet by calling `updateSheet(SheetState.Closed)`. The lessons are displayed in a
 * [LazyColumn], each represented by a [Lesson] composable.
 *
 * The transition between the FAB and the lessons list is animated based on [openFraction].
 * The alpha of the lessons list and the FAB are interpolated using [lerp] to create a smooth
 * fade-in/fade-out effect.
 *
 * We start by initializing and remembering (with `key1` the [Course.id] of our [Course] parameter
 * [course]) our [List] of [Lesson] variable `lessons` to the [List] returned by the
 * [LessonsRepo.getLessons] method when called with our [Course.id] of our [Course] parameter
 * [course] as its `courseId` argument.
 *
 * Then our root composable is a [Box] whose `modifier` argument is a [Modifier.fillMaxWidth]. In its
 * [BoxScope] `content` composable lambda argument we start by initializing our animated [Float]
 * variable `lessonsAlpha` to the Linearly interpolated value the [lerp] function returns for the
 * following conditions:
 *  - `startValue`: is 0f
 *  - `endValue`: is 1f
 *  - `endFraction`: is 08.f
 *  - `fraction`: is our [Float] parameter [openFraction]
 *
 * Then we compose a [Column] whose `modifier` argument is a [Modifier.fillMaxSize] chained to a
 * [Modifier.graphicsLayer] whose `alpha` argument is our animated [Float] variable `lessonsAlpha`
 * (adds a layer atop the [Column] whose `alpha` is animated), and at the end of the chain is a
 * [Modifier.statusBarsPadding] to add padding to accommodate the status bars insets.
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we start by initializing
 * and remembering our [LazyListState] variable `scroll` to the value returned by the
 * [rememberLazyListState] function. We initialize our animated [Dp] variable `appBarElevation` to
 * the value returned by the [animateDpAsState] function when its `targetValue` lambda argument
 * returns `4,dp` if the [LazyListState.isScrolled] property of [LazyListState] variable `scroll`
 * is true, or `0.dp` otherwise.
 *
 * We initialize our [Color] variable `appBarColor` to our [Color] parameter [surfaceColor] if
 * `appBarElevation` is greater than 0.dp, or [Color.Transparent] otherwise.
 *
 * We then compose a [TopAppBar] whose `backgroundColor` argument is our [Color] variable
 * `appBarColor`, and its `elevation` argument is our [Dp] variable `appBarElevation`.
 *
 * In the [RowScope] `content` composable lambda argument of the [TopAppBar] we compose:
 *
 * **First**: We compose a [Text] whose arguments are:
 *  - `text`: is the [Course.name] of our [Course] parameter [course].
 *  - `style`: is the [TextStyle] of [Typography.subtitle1] of our custom [MaterialTheme.typography].
 *  - `maxLines`: is `1`.
 *  - `overflow`: is [TextOverflow.Ellipsis].
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` padding to `all` sides, chained to a
 *  [RowScope.weight] whose `weight` argument is `1f`, chained to a [RowScope.align] whose
 *  `alignment` argument is [Alignment.CenterVertically].
 *
 * **Second**: We compose an [IconButton] whose `onClick` argument is a lambda that calls our lambda
 * parameter [updateSheet] with the [SheetState.Closed] value, and whose `modifier` argument is a
 * [RowScope.align] whose `alignment` argument is [Alignment.CenterVertically]. In the `content`
 * composable lambda argument of the [IconButton] we compose an [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Rounded.ExpandMore].
 *  - `contentDescription`: is the [String] with resource id `R.string.label_collapse_lessons`
 *  ("Collapse lessons sheet").
 *
 * Below the [TopAppBar] we compose a [LazyColumn] whose `state` argument is our [LazyListState]
 * variable `scroll`, and whose `contentPadding` argument is the [PaddingValues] of the
 * [WindowInsets.Companion.systemBars] for only the [WindowInsetsSides.Horizontal] and the
 * [WindowInsetsSides.Bottom] sides. In the [LazyListScope] `content` composable lambda argument of
 * the [LazyColumn] we compose a [LazyListScope.items] whose `items` argument is our [List] of
 * [Lesson] variable `lessons`, and whose `key` argument is the [Lesson.title] of each [Lesson].
 * In the [LazyItemScope] `content` composable lambda argument of the [items] we accept the [Lesson]
 * passed the lambda in variable `lesson` and then compose a [Lesson] whose `lesson` argument is
 * `lesson`, followed by a [Divider] whose `startIndent` argument is `128.dp`.
 *
 * When the sheet is closed, we show the FAB:
 * First we initialize our [Float] variable `fabAlpha` to the value returned by the [lerp] function
 * when the following conditions are met:
 *  - `startValue`: is 1f.
 *  - `endValue`: is 0f.
 *  - `startFraction`: is 0f.
 *  - `endFraction`: is 0.15f.
 *  - `fraction`: is our [Float] parameter [openFraction].
 *
 * Then we compose a [Box] whose `modifier` argument is a [Modifier.size] whose `size` argument
 * is our [FabSize], chained to a [Modifier.padding] that adds `16.dp` padding to the `start`,
 * and `8.dp` padding to the `top`, chained to a [Modifier.graphicsLayer] whose `alpha` argument is
 * our [Float] variable `fabAlpha`. In the [BoxScope] `content` composable lambda argument of the
 * [Box] we compose an [IconButton] whose `modifier` argument is a [RowScope.align] whose `alignment`
 * argument is [Alignment.Center], and whose `onClick` argument is a lambda that calls our lambda
 * parameter [updateSheet] with the [SheetState.Open] value. In the `content` composable lambda
 * argument of the [IconButton] we compose an [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.AutoMirrored.Rounded.PlaylistPlay].
 *  - `tint`: is the [Colors.onPrimary] of our custom [MaterialTheme.colors].
 *  - `contentDescription`: is the [String] with resource id `R.string.label_expand_lessons`
 *  ("Expand lessons sheet").
 *
 * @param course The [Course] for which to display the lessons.
 * @param openFraction A float value between 0.0 and 1.0 representing the fraction of the sheet that
 * is open. 0.0 means closed (FAB visible), 1.0 means fully open (lessons list visible).
 * @param surfaceColor The background color of the sheet when it's open. Defaults to [Colors.surface]
 * of our custom [MaterialTheme.colors].
 * @param updateSheet A lambda function that takes a [SheetState] and is called to update the state
 * of the sheet (open or closed).
 */
@Composable
private fun Lessons(
    course: Course,
    openFraction: Float,
    surfaceColor: Color = MaterialTheme.colors.surface,
    updateSheet: (SheetState) -> Unit
) {
    val lessons: List<Lesson> = remember(course.id) { LessonsRepo.getLessons(courseId = course.id) }

    Box(modifier = Modifier.fillMaxWidth()) {
        // When sheet open, show a list of the lessons
        val lessonsAlpha: Float = lerp(
            startValue = 0f,
            endValue = 1f,
            startFraction = 0.2f,
            endFraction = 0.8f,
            fraction = openFraction
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = lessonsAlpha }
                .statusBarsPadding()
        ) {
            val scroll: LazyListState = rememberLazyListState()
            val appBarElevation: Dp by animateDpAsState(if (scroll.isScrolled) 4.dp else 0.dp)
            val appBarColor: Color = if (appBarElevation > 0.dp) surfaceColor else Color.Transparent
            TopAppBar(
                backgroundColor = appBarColor,
                elevation = appBarElevation
            ) {
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .weight(weight = 1f)
                        .align(alignment = Alignment.CenterVertically)
                )
                IconButton(
                    onClick = { updateSheet(SheetState.Closed) },
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ExpandMore,
                        contentDescription = stringResource(id = R.string.label_collapse_lessons)
                    )
                }
            }
            LazyColumn(
                state = scroll,
                contentPadding = WindowInsets.systemBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                    .asPaddingValues()
            ) {
                items(
                    items = lessons,
                    key = { it.title }
                ) { lesson: Lesson ->
                    Lesson(lesson = lesson)
                    Divider(startIndent = 128.dp)
                }
            }
        }

        // When sheet closed, show the FAB
        val fabAlpha: Float = lerp(
            startValue = 1f,
            endValue = 0f,
            startFraction = 0f,
            endFraction = 0.15f,
            fraction = openFraction
        )
        Box(
            modifier = Modifier
                .size(size = FabSize)
                .padding(start = 16.dp, top = 8.dp) // visually center contents
                .graphicsLayer { alpha = fabAlpha }
        ) {
            IconButton(
                modifier = Modifier.align(alignment = Alignment.Center),
                onClick = { updateSheet(SheetState.Open) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.PlaylistPlay,
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = stringResource(id = R.string.label_expand_lessons)
                )
            }
        }
    }
}

/**
 * This composable function displays a single lesson item in a list.
 *
 * It shows the lesson's image, title, length, and formatted step number.
 * The lesson item is clickable, though the click action is currently a placeholder.
 *
 * Our root composable is a [Row] whose `modifier` argument is a [Modifier.clickable] whose
 * `onClick` argument is a do nothing lambda, chained to a [Modifier.padding] that adds `16.dp`
 * padding to the `vertical` sides. In the [RowScope] `content` composable lambda argument of the
 * [Row] we compose:
 *
 * **First**: We compose a [NetworkImage] whose arguments are:
 *  - `url`: is the [Lesson.imageUrl] of our [Lesson] parameter [lesson].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.size] whose `width` argument is `112.dp` and whose `height` argument
 *  is `64.dp`.
 *
 * **Second**: We compose a [Column] whose `modifier` argument is a [RowScope.weight] whose
 * `weight` argument is `1f`, chained to a [Modifier.padding] that adds `16.dp` padding to the
 * `start`. In the [ColumnScope] `content` composable lambda argument of the [Column] we compose
 * a [Text] whose arguments are:
 *  - `text`: is the [Lesson.title] of our [Lesson] parameter [lesson].
 *  - `style`: is the [TextStyle] of [Typography.subtitle2] of our custom [MaterialTheme.typography].
 *  - `maxLines`: is `2`.
 *  - `overflow`: is [TextOverflow.Ellipsis].
 *
 * Below the [Text] we compose a [CompositionLocalProvider] that provides [ContentAlpha.medium] as
 * the [LocalContentAlpha] to a [Row] whose `modifier` argument is a [Modifier.padding] that adds
 * `4.dp` padding to the `top`, and whose `verticalAlignment` argument is [Alignment.CenterVertically].
 * In the [RowScope] `content` composable lambda argument of the [Row] first we compose an [Icon]
 * whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Rounded.PlayCircleOutline].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.size] whose `size` argument is `16.dp`.
 *
 * Next in the [Row] we compose a [Text] whose arguments are:
 * - `modifier`: is a [Modifier.padding] that adds `4.dp` padding to the `start`.
 *  - `text`: is the [Lesson.length] of our [Lesson] parameter [lesson].
 *  - `style`: is the [TextStyle] of [Typography.caption] of our custom [MaterialTheme.typography].
 *
 * **Third**: Next in the root [Row] We compose a [Text] whose arguments are:
 *  - `text`: is the [Lesson.formattedStepNumber] of our [Lesson] parameter [lesson].
 *  - `style`: is the [TextStyle] of [Typography.subtitle2] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` to the `horizontal` sides
 *
 * @param lesson The [Lesson] to display.
 */
@Composable
private fun Lesson(lesson: Lesson) {
    Row(
        modifier = Modifier
            .clickable(onClick = { /* todo */ })
            .padding(vertical = 16.dp)
    ) {
        NetworkImage(
            url = lesson.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(width = 112.dp, height = 64.dp)
        )
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(size = 16.dp)
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = lesson.length,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
        Text(
            text = lesson.formattedStepNumber,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * Defines the state of the sheet.
 */
private enum class SheetState { Open, Closed }

/**
 * A [Boolean] property that is `true` if this [LazyListState] has been scrolled from its initial
 * position, ie. either the [LazyListState.firstVisibleItemIndex] is greater than 0, or the
 * [LazyListState.firstVisibleItemScrollOffset] is greater than 0.
 */
private val LazyListState.isScrolled: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0

/**
 * Preview of [CourseDetails]
 */
@Preview(name = "Course Details")
@Composable
private fun CourseDetailsPreview() {
    val courseId = courses.first().id
    CourseDetails(
        courseId = courseId,
        selectCourse = { },
        upPress = { }
    )
}

/**
 * Preview of [Lessons] in the "Closed" state
 */
@Preview(name = "Lessons Sheet — Closed")
@Composable
private fun LessonsSheetClosedPreview() {
    LessonsSheetPreview(openFraction = 0f)
}

/**
 * Preview of [Lessons] in the "Open" state
 */
@Preview(name = "Lessons Sheet — Open")
@Composable
private fun LessonsSheetOpenPreview() {
    LessonsSheetPreview(openFraction = 1f)
}

/**
 * Preview of [Lessons] in the "Open" state, in dark theme
 */
@Preview(name = "Lessons Sheet — Open – Dark")
@Composable
private fun LessonsSheetOpenDarkPreview() {
    LessonsSheetPreview(openFraction = 1f, darkTheme = true)
}

/**
 * Preview of [Lessons]
 *
 * @param openFraction The fraction that the sheet is open.
 * @param darkTheme Whether the theme is dark.
 */
@Composable
private fun LessonsSheetPreview(
    openFraction: Float,
    darkTheme: Boolean = false
) {
    PinkTheme(darkTheme = darkTheme) {
        val color: Color = MaterialTheme.colors.primarySurface
        Surface(color = color) {
            Lessons(
                course = courses.first(),
                openFraction = openFraction,
                surfaceColor = color,
                updateSheet = { }
            )
        }
    }
}

/**
 * Preview of [RelatedCourses]
 */
@Preview(name = "Related")
@Composable
private fun RelatedCoursesPreview() {
    val related: Course = courses.random()
    RelatedCourses(
        courseId = related.id,
        selectCourse = { }
    )
}
