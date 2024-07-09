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

package com.example.jetnews.ui.interests

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.InterestSection
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.data.interests.impl.FakeInterestsRepository
import com.example.jetnews.ui.theme.JetnewsTheme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlin.math.max

/**
 * Used to provide a title [String] for the type of [Sections] that a [TabContent] displays.
 *
 * @param titleResId resource ID of a [String] to use as the title of a [Tab] in the
 * [InterestsTabRowContent] Composable
 */
enum class Sections(@StringRes val titleResId: Int) {
    /**
     * Used by the [TabContent] that displays the [List] of [InterestSection] of the
     * [InterestsUiState.topics], and the [Set] of [TopicSelection] collected as [State] from
     * the [StateFlow] of [Set] of [TopicSelection] of the [InterestsViewModel.selectedTopics]
     * property in a [TabWithSections]. The `titleResId` property is the resource ID
     * [R.string.interests_section_topics] ("Topics")
     */
    Topics(titleResId = R.string.interests_section_topics),

    /**
     * Used by the [TabContent] that displays the [List] of [String] of the
     * [InterestsUiState.people], and the [Set] of [String] collected as [State] from
     * the [StateFlow] of [Set] of [String] of the [InterestsViewModel.selectedPeople]
     * property in a [TabWithTopics]. The `titleResId` property is the resource ID
     * [R.string.interests_section_people] ("People")
     */
    People(titleResId = R.string.interests_section_people),

    /**
     * Used by the [TabContent] that displays the [List] of [String] of the
     * [InterestsUiState.publications], and the [Set] of [String] collected as [State] from
     * the [StateFlow] of [Set] of [String] of the [InterestsViewModel.selectedPublications]
     * property in a [TabWithTopics]. The `titleResId` property is the resource ID
     * [R.string.interests_section_publications] ("Publications")
     */
    Publications(titleResId = R.string.interests_section_publications)
}

/**
 * TabContent for a single tab of the screen. This is intended to encapsulate a tab & it's content
 * as a single object. It was added to avoid passing several parameters per-tab from the stateful
 * composable to the composable that displays the current tab.
 *
 * @param section the tab that this content is for
 * @param content content of the tab, a composable that describes the content
 */
class TabContent(val section: Sections, val content: @Composable () -> Unit)

/**
 * Stateless interest screen displays the tabs specified in [tabContent] adapting the UI to
 * different screen sizes. We start by initializing our [Context] variable `val context` to the
 * `current` [LocalContext]. Then our root Composable is a [Scaffold] whose arguments are:
 *  - `snackbarHost` is a lambda which composes a [SnackbarHost] whose `hostState` argument is our
 *  [SnackbarHostState] parameter [snackbarHostState].
 *  - `topBar` is a lambda that composes a [CenterAlignedTopAppBar] whose `title` argument is a
 *  lambda that composes a [Text] whose `text` argument is the [String] with resource ID
 *  [R.string.cd_interests] ("Interests"), using as its [TextStyle] `style` argument the
 *  [Typography.titleLarge] of our custom [MaterialTheme.typography] (`fontSize` = 22.sp, `lineHeight`
 *  = 28.sp, `letterSpacing` = 0.sp, and `lineBreak` = [LineBreak.Heading]), and as its `color`
 *  argument the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme]. The `navigationIcon`
 *  argument of the [CenterAlignedTopAppBar] is defined only is our [Boolean] parameter [isExpandedScreen]
 *  is `false` (a phone instead of a table) in which case it is an [IconButton] whose `onClick` argument
 *  is our lambda parameter [openDrawer], and whose `content` lambda argument composes an [Icon] whose
 *  [Painter] argument `painter` causes it to render the vector drawable whose resource ID is
 *  [R.drawable.ic_jetnews_logo] (a "greater than" character followed by an "underline" character).
 *  The `contentDescription` argument of the [Icon] is the [String] with resource ID
 *  [R.string.cd_open_navigation_drawer] ("Open navigation drawer"), and its [Color] `tint` argument
 *  is the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme]. The `actions` argument of
 *  the [CenterAlignedTopAppBar] is a [RowScope] lambda that composes an [IconButton] whose `onClick`
 *  argument is a lambda which pops ups a [Toast] with the message "Search is not yet implemented in
 *  this configuration". The `content` lambda argument of the [IconButton] composes an [Icon] whose
 *  `imageVector` argument is the [ImageVector] drawn by [Icons.Filled.Search] (a stylized magnifying
 *  glass), and its `contentDescription` argument is the [String] whose resource ID is
 *  [R.string.cd_search] ("Search").
 *
 * The `content` lambda argument of the [Scaffold] accepts the [PaddingValues] passed the lambda as
 * variable `val innerPadding` and uses it to initialize its [Modifier] variable `val screenModifier`
 * to a [Modifier.padding] whose `paddingValues` argument is that `innerPadding`. Its root Composable
 * is an [InterestScreenContent] whose arguments are:
 *  - `currentSection` is our [Sections] parameter [currentSection]
 *  - `isExpandedScreen` is our [Boolean] parameter [isExpandedScreen]
 *  - `updateSection` is our lambda taking a [Sections] parameter [onTabChange]
 *  - `tabContent` is our [List] of [TabContent] parameter [tabContent]
 *  - `modifier` is our [Modifier] variable `screenModifier`
 *
 * @param tabContent (slot) the tabs and their content to display on this screen, must be a
 * non-empty list, tabs are displayed in the order specified by this list
 * @param currentSection (state) the current tab to display, must be in [tabContent]
 * @param isExpandedScreen (state) true if the screen is expanded
 * @param onTabChange (event) request a change in [currentSection] to another tab from [tabContent]
 * @param openDrawer (event) request opening the app drawer
 * @param snackbarHostState (state) the state for the screen's [Scaffold]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsScreen(
    tabContent: List<TabContent>,
    currentSection: Sections,
    isExpandedScreen: Boolean,
    onTabChange: (Sections) -> Unit,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context: Context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.cd_interests),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    if (!isExpandedScreen) {
                        IconButton(onClick = openDrawer) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_jetnews_logo),
                                contentDescription = stringResource(
                                    id = R.string.cd_open_navigation_drawer
                                ),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                /* context = */ context,
                                /* text = */ "Search is not yet implemented in this configuration",
                                /* duration = */ Toast.LENGTH_LONG
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(id = R.string.cd_search)
                        )
                    }
                }
            )
        }
    ) { innerPadding: PaddingValues ->
        val screenModifier: Modifier = Modifier.padding(paddingValues = innerPadding)
        InterestScreenContent(
            currentSection = currentSection,
            isExpandedScreen = isExpandedScreen,
            updateSection = onTabChange,
            tabContent = tabContent,
            modifier = screenModifier
        )
    }
}

/**
 * Remembers the content for each tab on the Interests screen gathering application data from
 * [InterestsViewModel] parameter [interestsViewModel]. We start by initializing our [State] wrapped
 * [InterestsUiState] variable `val uiState` by using the [StateFlow.collectAsStateWithLifecycle]
 * extension function on the [StateFlow] wrapped [InterestsUiState] property [InterestsViewModel.uiState]
 * of our [InterestsViewModel] parameter [interestsViewModel] (it will get updated every time that
 * the [InterestsViewModel] emits a new value causing the recomposition of any Composable that uses
 * it). Then we proceed to the three [TabContent] that we return in a [List]:
 *  - `val topicsSection` is a [TabContent] whose `section` is [Sections.Topics] ("Topics") and in
 *  its `content` Composable lambda argument we initialize our [State] wrapped [Set] of
 *  [TopicSelection] variable `val selectedTopics` by using the [StateFlow.collectAsStateWithLifecycle]
 *  extension function on the [StateFlow] wrapped [Set] of [TopicSelection] property
 *  [InterestsViewModel.selectedTopics] of our [InterestsViewModel] parameter [interestsViewModel]
 *  (it will get updated every time that the [InterestsViewModel] emits a new value causing the
 *  recomposition of our [TabWithSections] Composable since it is used as its `selectedTopics`
 *  argument). Our root Composable is a [TabWithSections] whose `topics` argument is the
 *  [InterestsUiState.topics] property of our [State] wrapped [InterestsUiState] variable `uiState`,
 *  whose `selectedTopics` argument is our [State] wrapped [Set] of [TopicSelection] variable
 *  `selectedTopics`, and whose `onTopicSelect` is a lambda that calls the
 *  [InterestsViewModel.toggleTopicSelection] method of our [InterestsViewModel] parameter
 *  [interestsViewModel] with the [TopicSelection] passed it.
 *  - `val peopleSection` is a [TabContent] whose `section` is [Sections.People] ("People") and in
 *  its `content` Composable lambda argument we initialize our [State] wrapped [Set] of [String]
 *  variable `val selectedPeople` by using the [StateFlow.collectAsStateWithLifecycle] extension
 *  function on the [StateFlow] wrapped [Set] of [String] property [InterestsViewModel.selectedPeople]
 *  of our [InterestsViewModel] parameter [interestsViewModel] (it will get updated every time that
 *  the [InterestsViewModel] emits a new value causing the recomposition of our [TabWithTopics]
 *  Composable since it is used as its `selectedTopics` argument). Our root Composable is a [TabWithTopics]
 *  whose `topics` argument is the [InterestsUiState.people] property of our [State] wrapped
 *  [InterestsUiState] variable `uiState`, whose `selectedTopics` argument is our [State] wrapped
 *  [Set] of [String] variable `selectedPeople`, and whose `onTopicSelect` is a lambda that calls the
 *  [InterestsViewModel.togglePersonSelected] method of our [InterestsViewModel] parameter
 *  [interestsViewModel] with the [String] passed it.
 *  - `val publicationSection` is a [TabContent] whose `section` is [Sections.Publications]
 *  ("Publications") and in its `content` Composable lambda argument we initialize our [State]
 *  wrapped [Set] of [String] variable `val selectedPublications` by using the
 *  [StateFlow.collectAsStateWithLifecycle] extension function on the [StateFlow] wrapped [Set] of
 *  [String] property [InterestsViewModel.selectedPublications] of our [InterestsViewModel] parameter
 *  [interestsViewModel] (it will get updated every time that the [InterestsViewModel] emits a new
 *  value causing the recomposition of our [TabWithTopics]  Composable since it is used as its
 *  `selectedTopics` argument). Our root Composable is a [TabWithTopics] whose `topics` argument is
 *  the [InterestsUiState.publications] property of our [State] wrapped [InterestsUiState] variable
 *  `uiState`, whose `selectedTopics` argument is our [State] wrapped [Set] of [String] variable
 *  `selectedPublications`, and whose `onTopicSelect` is a lambda that calls the
 *  [InterestsViewModel.togglePublicationSelected] method of our [InterestsViewModel] parameter
 *  [interestsViewModel] with the [String] passed it.
 *
 * Finally we return a [List] of [TabContent] containing our variables `topicsSection`, `peopleSection`
 * and `publicationSection` to the caller.
 *
 * @param interestsViewModel the [InterestsViewModel] instance being used by the app
 * @return a [List] of the three [TabContent] (tab `section` names [Sections.Topics], [Sections.People]
 * and [Sections.Publications]) that we construct from data that we collect from the various fields
 * of our [InterestsViewModel] parameter [interestsViewModel] as they are emitted.
 */
@Composable
fun rememberTabContent(interestsViewModel: InterestsViewModel): List<TabContent> {
    // UiState of the InterestsScreen
    val uiState: InterestsUiState by interestsViewModel.uiState.collectAsStateWithLifecycle()

    // Describe the screen sections here since each section needs 2 states and 1 event.
    // Pass them to the stateless InterestsScreen using a tabContent.
    val topicsSection = TabContent(section = Sections.Topics) {
        val selectedTopics: Set<TopicSelection> by interestsViewModel.selectedTopics
            .collectAsStateWithLifecycle()
        TabWithSections(
            sections = uiState.topics,
            selectedTopics = selectedTopics,
            onTopicSelect = { interestsViewModel.toggleTopicSelection(it) }
        )
    }

    val peopleSection = TabContent(section = Sections.People) {
        val selectedPeople: Set<String> by interestsViewModel.selectedPeople
            .collectAsStateWithLifecycle()
        TabWithTopics(
            topics = uiState.people,
            selectedTopics = selectedPeople,
            onTopicSelect = { interestsViewModel.togglePersonSelected(it) }
        )
    }

    val publicationSection = TabContent(section = Sections.Publications) {
        val selectedPublications: Set<String> by interestsViewModel.selectedPublications
            .collectAsStateWithLifecycle()
        TabWithTopics(
            topics = uiState.publications,
            selectedTopics = selectedPublications,
            onTopicSelect = { interestsViewModel.togglePublicationSelected(it) }
        )
    }

    return listOf(topicsSection, peopleSection, publicationSection)
}

/**
 * Displays an [InterestsTabRow] tab row of all the [List] of [TabContent] parameter [tabContent]
 * with [Sections] parameter [currentSection] selected and the [TabContent.content] body of that
 * selected [TabContent]. We start by initializing our [Int] variable `val selectedTabIndex` by
 * finding the index of the first [TabContent] in the [List] of [TabContent] parameter [tabContent]
 * whose [TabContent.section] is equal to our [Sections] parameter [currentSection]. Then our root
 * Composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier]. In the
 * [ColumnScope] `content` lambda argument of the [Column] we compose:
 *  - an [InterestsTabRow] whose `selectedTabIndex` argument is our [Int] variable `selectedTabIndex`,
 *  whose `updateSection` argument is our lambda parameter [updateSection], whose `tabContent` argument
 *  is our [List] of [TabContent] parameter [tabContent], and whose `isExpandedScreen` argument is
 *  our [Boolean] parameter [isExpandedScreen].
 *  - a [HorizontalDivider] whose [Color] argument `color` is a copy of the [ColorScheme.onSurface]
 *  of our custom [MaterialTheme.colorScheme] with its `alpha` set to 0.1f
 *  -  a [Box] whose `modifier` argument is a [ColumnScope.weight] of `weight` 1f causing it to take
 *  up all remaining space once its unweighted siblings are measured and placed. The `content` of
 *  the [Box] composes the [TabContent.content] of the [TabContent] at index `selectedTabIndex` in
 *  our [List] of [TabContent] parameter [tabContent].
 *
 * @param currentSection (state) the tab that is currently selected
 * @param isExpandedScreen (state) whether or not the screen is expanded
 * @param updateSection (event) request a change in tab selection
 * @param tabContent (slot) tabs and their content to display, must be a non-empty list, tabs are
 * displayed in the order of this list
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us a [Modifier.padding] that adds the [PaddingValues] that [Scaffold]
 * passes its `content` lambda argument (us) to our padding.
 */
@Composable
private fun InterestScreenContent(
    currentSection: Sections,
    isExpandedScreen: Boolean,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex: Int = tabContent.indexOfFirst { it.section == currentSection }
    Column(modifier = modifier) {
        InterestsTabRow(
            selectedTabIndex = selectedTabIndex,
            updateSection = updateSection,
            tabContent = tabContent,
            isExpandedScreen = isExpandedScreen
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        Box(modifier = Modifier.weight(weight = 1f)) {
            // display the current tab content which is a @Composable () -> Unit
            tabContent[selectedTabIndex].content()
        }
    }
}

/**
 * [Modifier] for UI containers that show interests items. To a [Modifier.fillMaxWidth] that causes
 * the Composable using the [Modifier] to occupy the entire incoming width constraint, is chained a
 * [Modifier.wrapContentWidth] whose `align` argument of [Alignment.CenterHorizontally] causes the
 * content to measure at its desired width without regard for the incoming measurement minimum width
 * constraint centering it horizontally.
 */
private val tabContainerModifier: Modifier = Modifier
    .fillMaxWidth()
    .wrapContentWidth(align = Alignment.CenterHorizontally)

/**
 * Display a simple list of topics. Our root Composable is an [InterestsAdaptiveContentLayout] whose
 * `topPadding` argument is 16.dp (padding that [InterestsAdaptiveContentLayout] adds to the top y
 * position that it places its children at), whose `modifier` argument is a [Modifier.verticalScroll]
 * chained to our [Modifier] variable [tabContainerModifier] (modifies any element using the [Modifier]
 *  to allow it to scroll vertically when height of the content is bigger than max constraints allow).
 *  The `content` Composable lambda argument loops through each of the `var topic` [String] in our
 *  [List] of [String] parameter [topics] composing a [TopicItem] whose `itemTitle` argument is that
 *  `topic` [String], whose `selected` [Boolean] argument is `true` if that `topic` is in our [Set]
 *  of [String] parameter [selectedTopics], and whose `onToggle` argument is a lambda that calls our
 *  lambda parameter [onTopicSelect] with the `topic` [String].
 *
 * @param topics (state) topics to display
 * @param selectedTopics (state) currently selected topics
 * @param onTopicSelect (event) request a topic selection be changed
 */
@Composable
private fun TabWithTopics(
    topics: List<String>,
    selectedTopics: Set<String>,
    onTopicSelect: (String) -> Unit
) {
    InterestsAdaptiveContentLayout(
        topPadding = 16.dp,
        modifier = tabContainerModifier.verticalScroll(state = rememberScrollState())
    ) {
        topics.forEach { topic: String ->
            TopicItem(
                itemTitle = topic,
                selected = selectedTopics.contains(topic),
                onToggle = { onTopicSelect(topic) },
            )
        }
    }
}

/**
 * Display a sectioned list of topics. Our root Composable is a [Column] `modifier` argument is a
 * [Modifier.verticalScroll] chained to our [Modifier] variable [tabContainerModifier] (modifies it
 * to allow it to scroll vertically when height of the content is bigger than max constraints allow).
 * In the [ColumnScope] `content` lambda argument we loop though each of the [InterestSection] in
 * our [List] of [InterestSection] using destructuring to initialize [String] variable `section` to
 * the [InterestSection.title] and to initialize [List] of [String] variable `topics` to the its
 * [InterestSection.interests] property. We then compose a [Text] whose `text` argument is the current
 * value of `section`, whose `modifier` argument is a [Modifier.padding] that adds 16.dp to all sides,
 * with a [Modifier.semantics] chained to that which marks the [Text] as a heading for accessibility,
 * and whose [TextStyle] `style` argument is the [Typography.titleMedium] of our custom
 * [MaterialTheme.typography] (`fontSize` = 16.sp, `lineHeight` = 24.sp, `letterSpacing` = 0.15.sp,
 * `fontWeight` = [FontWeight.Medium], and `lineBreak` = [LineBreak.Heading]). This is followed by
 * an [InterestsAdaptiveContentLayout] in whose Composable `content` lambda we loop through each of
 * the `topic` [String] in the [InterestSection.interests] `topics` [List] of [String] composing an
 * [TopicItem] whose `itemTitle` is the current `topic` [String], whose `selected` argument is `true`
 * if our [Set] of [TopicSelection] parmeter [selectedTopics] contains a [TopicSelection] constructed
 * from the `section` [String] of the outside [forEach] loop and the `topic` [String] of the inside
 * [forEach] loop, and whose `onToggle` argument is a lambda which calls our [onTopicSelect] lambda
 * parameter with a [TopicSelection] constructed from the `section` [String] of the outside [forEach]
 * loop and the `topic` [String] of the inside [forEach] loop.
 *
 * @param sections (state) topics to display, grouped by sections
 * @param selectedTopics (state) currently selected topics
 * @param onTopicSelect (event) request a topic+section selection be changed
 */
@Composable
private fun TabWithSections(
    sections: List<InterestSection>,
    selectedTopics: Set<TopicSelection>,
    onTopicSelect: (TopicSelection) -> Unit
) {
    Column(modifier = tabContainerModifier.verticalScroll(state = rememberScrollState())) {
        sections.forEach { (section: String, topics: List<String>) ->
            Text(
                text = section,
                modifier = Modifier
                    .padding(all = 16.dp)
                    .semantics { heading() },
                style = MaterialTheme.typography.titleMedium
            )
            InterestsAdaptiveContentLayout {
                topics.forEach { topic: String ->
                    TopicItem(
                        itemTitle = topic,
                        selected = selectedTopics.contains(TopicSelection(section, topic)),
                        onToggle = { onTopicSelect(TopicSelection(section, topic)) },
                    )
                }
            }
        }
    }
}

/**
 * Display a full-width topic item. Our root Composable is a [Column] whose `modifier` argument is a
 * [Modifier.padding] that adds 16.dp to the `horizontal` sides of the [Column]. In its [ColumnScope]
 * `content` lambda argument we have a [Row] whose `modifier` argument chains a [Modifier.toggleable]
 * to our [Modifier] parameter [modifier] whose `value` argument is our [Boolean] parameter [selected],
 * and whose `onValueChange` argument is a lambda that calls our [onToggle] lambda parameter. The
 * `verticalAlignment` argument of the [Row] is [Alignment.CenterVertically] to center its children
 * vertically. In the [RowScope] `content` lambda argument of the [Row]:
 *  - we start by initializing our [Painter] variable `val image` to a [Painter] created to draw the
 *  drawable with resource ID [R.drawable.placeholder_1_1].
 *  - Then we compose an [Image] whose `painter` argument is our [Painter] variable `image`, and whose
 *  `modifier` argument is a [Modifier.size] that sets its size to 56.dp, with a [Modifier.clip] that
 *  clips the [Image] `shape` to a  [RoundedCornerShape] with `size` 4.dp
 *  - next in the [Row] is a [Text] whose `text` argument is our [String] parameter [itemTitle], whose
 *  `modifier` argument is a [Modifier.padding] that adds 16.dp padding to all sides with a
 *  [RowScope.weight] of `weight` 1f chained to that that causes the [Text] to occupy all remaining
 *  width of the [Row] once its siblings have been measured and placed, and whose [TextStyle] `style`
 *  argument is the [Typography.titleMedium] of our custom [MaterialTheme.typography] (`fontSize`
 *  = 16.sp, `lineHeight` = 24.sp, `letterSpacing` = 0.15.sp, `fontWeight` = [FontWeight.Medium], and
 *  `lineBreak` = [LineBreak.Heading])
 *  - next in the [Row] is a [Spacer] whose `modifier` argument is a [Modifier.width] that sets its
 *  `width` to 16.dp
 *  - last in the [Row] is a [SelectTopicButton] whose `selected` argument is our [Boolean] parameter
 *  [selected].
 *
 * Below the [Row] in the column is a [HorizontalDivider] whose `modifier` argument chains to our
 * [Modifier] parameter [modifier] a [Modifier.padding] that adds 72.dp padding to the `start`, 8.dp
 * to the `top` and 8.dp to the `bottom`. The [Color] `color` argument is a copy of the
 * [ColorScheme.onSurface] of our [MaterialTheme.colorScheme] with its `alpha` set to 0.1f
 *
 * @param itemTitle (state) topic title
 * @param selected (state) is topic currently selected
 * @param onToggle (event) toggle selection for topic
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Neither of our two callers ([TabWithSections] and [TabWithTopics]) pass us any so the
 * empty, default, or starter [Modifier] that contains no elements is used.
 */
@Composable
private fun TopicItem(
    itemTitle: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = modifier.toggleable(
                value = selected,
                onValueChange = { onToggle() }
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val image: Painter = painterResource(id = R.drawable.placeholder_1_1)
            Image(
                painter = image,
                contentDescription = null, // decorative
                modifier = Modifier
                    .size(size = 56.dp)
                    .clip(shape = RoundedCornerShape(size = 4.dp))
            )
            Text(
                text = itemTitle,
                modifier = Modifier
                    .padding(all = 16.dp)
                    .weight(weight = 1f), // Break line if the title is too long
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(width = 16.dp))
            SelectTopicButton(selected = selected)
        }
        HorizontalDivider(
            modifier = modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }
}

/**
 * [TabRow] for the [InterestsScreen]. We branch on the value of our [Boolean] parameter
 * [isExpandedScreen]:
 *  - `false` (we are running on a phone) we compose a [TabRow] whose `selectedTabIndex` argument is
 *  our [Int] parameter [selectedTabIndex], and whose [Color] `contentColor` argument is the
 *  [ColorScheme.primary] of our custom [MaterialTheme.colorScheme]. The `tabs` composable lambda
 *  argument of the [TabRow] is an [InterestsTabRowContent] whose `selectedTabIndex` argument is our
 *  [Int] parameter [selectedTabIndex], whose `updateSection` argument is our lambda of [Sections]
 *  parameter [updateSection], and whose `tabContent` argument is our [List] of [TabContent] parameter
 *  [tabContent]
 *  - `true` (we are running on a tablet) we compose a [ScrollableTabRow] whose `selectedTabIndex`
 *  argument is our [Int] parameter [selectedTabIndex], whose [Color] `contentColor` argument is the
 *  [ColorScheme.primary] of our custom [MaterialTheme.colorScheme], and whose `edgePadding` argument
 *  is 0.dp. The `tabs` composable lambda argument of the [TabRow] is an [InterestsTabRowContent]
 *  whose `selectedTabIndex` argument is our [Int] parameter [selectedTabIndex], whose `updateSection`
 *  argument is our lambda of [Sections] parameter [updateSection], whose `tabContent` argument is
 *  our [List] of [TabContent] parameter [tabContent], and whose `modifier` argument is a
 *  [Modifier.padding] that adds 8.dp to the `horizontal` sides of the [InterestsTabRowContent].
 *
 * @param selectedTabIndex the index of the currently selected tab
 * @param updateSection a lambda which will cause the UI display the [TabContent] of its [Sections]
 * argument when called.
 * @param tabContent the [List] of [TabContent] that is constructed and remembered by [rememberTabContent].
 * @param isExpandedScreen if `true` the device we are running on is a tablet.
 */
@Composable
private fun InterestsTabRow(
    selectedTabIndex: Int,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>,
    isExpandedScreen: Boolean
) {
    when (isExpandedScreen) {
        false -> {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                InterestsTabRowContent(
                    selectedTabIndex = selectedTabIndex,
                    updateSection = updateSection,
                    tabContent = tabContent
                )
            }
        }

        true -> {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp
            ) {
                InterestsTabRowContent(
                    selectedTabIndex = selectedTabIndex,
                    updateSection = updateSection,
                    tabContent = tabContent,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

/**
 * This composes the [Tab]'s that are displayed in the [InterestsTabRow]. We use [forEachIndexed] to
 * loop over each of the [TabContent] in our [List] of [TabContent] parameter [tabContent] setting
 * [Int] variable `index` to the index of the [TabContent] in the [List] and `content` to the
 * [TabContent]. In the [forEachIndexed] `action` lambda we start by initializing our [Color]
 * variable `val colorText` to the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme]
 * if our [Int] parameter [selectedTabIndex] is equal to the current `index` of the [forEachIndexed]
 * loop (the current [TabContent] `content` is the selected [TabContent]), and if it is not then
 * to a copy of the [ColorScheme.onSurface] with its `alpha` set to 0.8f. Then we compose a [Tab]
 * whose `selected` argument is `true` if our [Int] parameter [selectedTabIndex] is equal to the
 * current `index` of the [forEachIndexed], whose `onClick` is a lambda that calls our lambda
 * parameter [updateSection] with the [TabContent.section] property of the current [TabContent]
 * `content`, and whose `modifier` is a [Modifier.heightIn] that sets its minimum height to 48.dp.
 * In the [ColumnScope] `content` lambda argument of the [Tab] we compose a [Text] whose `text` is
 * the [String] whose resource `id` is the [Sections.titleResId] propery of the [TabContent.section]
 * of the current [TabContent] `content` of the [forEachIndexed] loop, whose [Color] `color` argument
 * is our [Color] variable `color`, whose [TextStyle] `style` argument is the [Typography.titleMedium]
 * of our custom [MaterialTheme.typography] (`fontSize` = 16.sp, `lineHeight` = 24.sp, `letterSpacing`
 * = 0.15.sp, `fontWeight` = [FontWeight.Medium], and `lineBreak` = [LineBreak.Heading]), and whose
 * `modifier` argument chains a [Modifier.paddingFromBaseline] to our [Modifier] parameter [modifier]
 * that positions the content in the layout such that the distance from the top of the layout to the
 * baseline of the first line of text in the content is 20.dp
 *
 * @param selectedTabIndex the index of the currently selected [TabContent] in our [List] of
 * [TabContent] parameter [tabContent].
 * @param updateSection lambda which when called with a [Sections] will change the currently selected
 * [TabContent] to the one for that [Sections].
 * @param tabContent the [List] of [TabContent] that is constructed and remembered by [rememberTabContent].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. When called by [InterestsTabRow] for a table we are passed a [Modifier.padding] that
 * adds 8.dp to our `horizontal` sides, and when called for a phone we are passed none so the empty,
 * default, or starter [Modifier] that contains no elements is used.
 */
@Composable
private fun InterestsTabRowContent(
    selectedTabIndex: Int,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>,
    modifier: Modifier = Modifier
) {
    tabContent.forEachIndexed { index: Int, content: TabContent ->
        val colorText: Color = if (selectedTabIndex == index) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        }
        Tab(
            selected = selectedTabIndex == index,
            onClick = { updateSection(content.section) },
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(
                text = stringResource(id = content.section.titleResId),
                color = colorText,
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier.paddingFromBaseline(top = 20.dp)
            )
        }
    }
}

/**
 * Custom layout for the Interests screen that places items on the screen given the available size.
 *
 * For example: Given a list of items (A, B, C, D, E) and a screen size that allows 2 columns,
 * the items will be displayed on the screen as follows:
 *  - A B
 *  - C D
 *  - E
 *
 * Our root composable is a [Layout] whose `modifier` argument is our [Modifier] parameter [modifier],
 * and whose `content` argument is our Composable lambda parameter [content]. In the [MeasurePolicy]
 * `measurePolicy` [MeasureScope] lambda argument we accept the [List] of [Measurable] passed the
 * lambda as variable `measurables` and the [Constraints] passed the lambda as `outerConstraints`.
 * We initialize our [Int] variable `val multipleColumnsBreakPointPx` to the pixel value of our [Dp]
 * parameter [multipleColumnsBreakPoint], initialize our [Int] variable `val topPaddingPx` to the
 * pixel value of our [Dp] parameter [topPadding], initialize our [Int] variable `val itemSpacingPx`
 * to the pixel value of our [Dp] parameter [itemSpacing], and initialize our [Int] variable
 * `val itemMaxWidthPx` to the pixel value of our [Dp] parameter [itemMaxWidth]. Next we set our
 * [Int] variable `val columns` to 1 if the [Constraints.maxWidth] of the [Constraints] passed the
 * lambda in variable `outerConstraints` is less than our variable `multipleColumnsBreakPointPx`,
 * or to 2 if it is not (we use this value for the number of columns to display on the screen).
 * We set our [Int] variable `val itemWidth` to the [Constraints.maxWidth] of the [Constraints] passed
 * the lambda in variable `outerConstraints` is `columns` is equal to 1 otherwise we initialize our
 * [Int] variable `val maxWidthWithSpaces` to the [Constraints.maxWidth] of `outerConstraints` minus
 * the quantity `columns` minus 1 times `itemSpacingPx`, and set `itemWidth` to `maxWidthWithSpaces`
 * divided by `columns` coerced to be between 0 and `itemMaxWidthPx`. We then initialize our
 * [Constraints] variable `val itemConstraints` to a copy of `outerConstraints` with its
 * [Constraints.maxWidth] set to `itemWidth`. We initialize our [IntArray] variable `val rowHeights`
 * to an [IntArray] whose `size` is the [List.size] of the [List] of [Measurable] passed the lambda
 * in `measurables` divided by `columns` with 1 added to it (we will use this [IntArray] to keep
 * track of the height of each row to calculate the layout's final size).
 *
 * Next we initialize our [List] of [Placeable] variable `val placeables` by using [mapIndexed] to
 * loop through all the [Measurable] in the [List] of [Measurable] in `measurables` capturing the
 * `index` that [mapIndexed] passes its `transform` lambda argument in [Int] variable `index` and
 * the [Measurable] passed in [Measurable] variable `measureable`. In the `tranform` lambda argument
 * we initialize our [Placeable] variable `val placeable` to the value that the [Measurable.measure]
 * method returns for `measureable` when called with its `constraints` argument our [Constraints]
 * variable `itemConstraints`. We calculate the [Int] `row` this [Placeable] will be in by dividing
 * `index` by `columns` and set the value stored at index `row` in [IntArray] `rowHeights` to the
 * [max] of its current value and the [Placeable.height] of `placeable`. Finally we return `placeable`
 * to be added to the [List] of [Placeable] variable `placeable` that [mapIndexed] is producing.
 *
 * We calculate the `maxHeight` of the Interests layout to initialize [Int] variable `val layoutHeight`
 * by summing the values in [IntArray] variable `rowHeights` and adding `topPaddingPx` to that. We
 * calulate the `maxWidth` of the Interests layout to initialize [Int] variable `val layoutWidth`
 * by multiplying `itemWidth` times `columns` and adding the quantity `itemSpacingPx` times `columns`
 * minus 1 to that value.
 *
 * Finally we call [MeasureScope.layout] with its `width` argument the [Constraints.constrainWidth]
 * method of `outerConstraints` called with its `width` argument `layoutWidth`, and its `height`
 * argument the [Constraints.constrainHeight] method of `outerConstraints` called with its `height`
 * argument `layoutHeight`. In its [Placeable.PlacementScope] lambda argument we:
 *  - initialize our [Int] variable `var yPosition` to `topPaddingPx`
 *  - we use the [List.chunked] extension function on our [List] of [Placeable] variable `placeables`
 *  to split it into `columns` [List] of [List]'s (ie. one [List] of [List] for 1 `columns` or two
 *  [List] of [List] of [Placeable] for 2 `columns`)
 *  - we then use [forEachIndexed] to loop through the one or two [List] of [List] or [Placeable]
 *  capturing the index passed the `action` lambda in [Int] variable `rowIndex`, and the [List] of
 *  [Placeable] in variable `row`.
 *  - in the `action` lambda of the [forEachIndexed] we initialize our [Int] variable `var xPosition`
 *  to 0.
 *  - we then use [forEach] to loop through all of the [Placeable] in [List] of [Placeable] `row`
 *  capturing the [Placeable] passed the `action` lambda in variable `placeable` and we then call
 *  the [Placeable.PlacementScope.placeRelative] method on `placeable` with `xPosition` as the `x`
 *  argument and `yPosition`, after which we add to `xPosition` the [Placeable.width] of `placeable`
 *  plus `itemSpacingPx`.
 *  - When done with the `row` [List] of [Placeable] we add to `yPosition` the value of the entry in
 *  `rowHeights` at index `rowIndex` and loop around for the next [List] of [Placeable] (if there is
 *  one).
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [TabWithTopics] passes us a [Modifier.verticalScroll] that permits us to
 * scroll if we need to, and our caller [TabWithSections] passes us none so the empty, default, or
 * starter Modifier that contains no elements is used.
 * @param topPadding the padding to add to the top of our [Layout]. Our caller [TabWithTopics] passes
 * us 16.dp, but our caller [TabWithSections] passes us none so the default 0.dp is used.
 * @param itemSpacing spacing to add between our children
 * @param itemMaxWidth maximum width of our children.
 * @param multipleColumnsBreakPoint the size of the available incoming [Constraints.maxWidth] of our
 * [Layout] at we switch from one column to two. This is hard coded to the default 600.dp
 * @param content a Composable lambda that provides the children composables to be laid out. These
 * will be [TopicItem] composed for each of the [String]'s in [List] of [String]'s in our case.
 */
@Composable
private fun InterestsAdaptiveContentLayout(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    itemSpacing: Dp = 4.dp,
    itemMaxWidth: Dp = 450.dp,
    multipleColumnsBreakPoint: Dp = 600.dp,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, outerConstraints: Constraints ->
        // Convert parameters to Px. Safe to do as `Layout` measure block runs in a `Density` scope
        val multipleColumnsBreakPointPx: Int = multipleColumnsBreakPoint.roundToPx()
        val topPaddingPx: Int = topPadding.roundToPx()
        val itemSpacingPx: Int = itemSpacing.roundToPx()
        val itemMaxWidthPx: Int = itemMaxWidth.roundToPx()

        // Number of columns to display on the screen. This is harcoded to 2 due to
        // the design mocks, but this logic could change in the future.
        val columns: Int = if (outerConstraints.maxWidth < multipleColumnsBreakPointPx) 1 else 2
        // Max width for each item taking into account available space, spacing and `itemMaxWidth`
        val itemWidth: Int = if (columns == 1) {
            outerConstraints.maxWidth
        } else {
            val maxWidthWithSpaces: Int = outerConstraints.maxWidth - (columns - 1) * itemSpacingPx
            (maxWidthWithSpaces / columns).coerceIn(minimumValue = 0, maximumValue = itemMaxWidthPx)
        }
        val itemConstraints: Constraints = outerConstraints.copy(maxWidth = itemWidth)

        // Keep track of the height of each row to calculate the layout's final size
        val rowHeights = IntArray(size = measurables.size / columns + 1)
        // Measure elements with their maximum width and keep track of the height
        val placeables: List<Placeable> = measurables.mapIndexed { index: Int, measureable: Measurable ->
            val placeable: Placeable = measureable.measure(constraints = itemConstraints)
            // Update the height for each row
            val row: Int = index.floorDiv(other = columns)
            rowHeights[row] = max(rowHeights[row], placeable.height)
            placeable
        }

        // Calculate maxHeight of the Interests layout. Heights of the row + top padding
        val layoutHeight: Int = topPaddingPx + rowHeights.sum()
        // Calculate maxWidth of the Interests layout
        val layoutWidth: Int = itemWidth * columns + (itemSpacingPx * (columns - 1))

        // Lay out given the max width and height
        layout(
            width = outerConstraints.constrainWidth(width = layoutWidth),
            height = outerConstraints.constrainHeight(height = layoutHeight)
        ) {
            // Track the y co-ord we have placed children up to
            var yPosition: Int = topPaddingPx
            // Split placeables in lists that don't exceed the number of columns
            // and place them taking into account their width and spacing
            placeables.chunked(size = columns).forEachIndexed { rowIndex: Int, row: List<Placeable> ->
                var xPosition = 0
                row.forEach { placeable: Placeable ->
                    placeable.placeRelative(x = xPosition, y = yPosition)
                    xPosition += placeable.width + itemSpacingPx
                }
                yPosition += rowHeights[rowIndex]
            }
        }
    }
}

/**
 * Previews of the [InterestsScreen] for `isExpandedScreen` argument `false` (a phone)
 */
@Preview(name = "Interests screen", group = "Interests")
@Preview(name = "Interests screen (dark)", group = "Interests", uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Interests screen (big font)", group = "Interests", fontScale = 1.5f)
@Composable
fun PreviewInterestsScreenDrawer() {
    JetnewsTheme {
        val tabContent: List<TabContent> = getFakeTabsContent()
        val (currentSection: Sections, updateSection: (Sections) -> Unit) = rememberSaveable {
            mutableStateOf(value = tabContent.first().section)
        }

        InterestsScreen(
            tabContent = tabContent,
            currentSection = currentSection,
            isExpandedScreen = false,
            onTabChange = updateSection,
            openDrawer = { },
            snackbarHostState = SnackbarHostState()
        )
    }
}

/**
 * Previews of the [InterestsScreen] for `isExpandedScreen` argument `true` (a tablet)
 */
@Preview(name = "Interests screen navrail", group = "Interests", device = Devices.PIXEL_C)
@Preview(
    name = "Interests screen navrail (dark)", group = "Interests",
    uiMode = UI_MODE_NIGHT_YES, device = Devices.PIXEL_C
)
@Preview(
    name = "Interests screen navrail (big font)", group = "Interests",
    fontScale = 1.5f, device = Devices.PIXEL_C
)
@Composable
private fun PreviewInterestsScreenNavRail() {
    JetnewsTheme {
        val tabContent: List<TabContent> = getFakeTabsContent()
        val (currentSection: Sections, updateSection: (Sections) -> Unit) = rememberSaveable {
            mutableStateOf(value = tabContent.first().section)
        }

        InterestsScreen(
            tabContent = tabContent,
            currentSection = currentSection,
            isExpandedScreen = true,
            onTabChange = updateSection,
            openDrawer = { },
            snackbarHostState = SnackbarHostState()
        )
    }
}

/**
 * Previews of [TabWithSections] for group "Topics"
 */
@Preview(name = "Interests screen topics tab", group = "Topics")
@Preview(name = "Interests screen topics tab (dark)", group = "Topics", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewTopicsTab() {
    val topics: List<InterestSection> = runBlocking {
        (FakeInterestsRepository().getTopics() as Result.Success).data
    }
    JetnewsTheme {
        Surface {
            TabWithSections(sections = topics, selectedTopics = setOf()) { }
        }
    }
}

/**
 * Previews of [TabWithSections] for group "People"
 */
@Preview(name = "Interests screen people tab", group = "People")
@Preview(name = "Interests screen people tab (dark)", group = "People", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPeopleTab() {
    val people: List<String> = runBlocking {
        (FakeInterestsRepository().getPeople() as Result.Success).data
    }
    JetnewsTheme {
        Surface {
            TabWithTopics(topics = people, selectedTopics = setOf()) { }
        }
    }
}

/**
 * Previews of [TabWithSections] for group "Publications"
 */
@Preview(name = "Interests screen publications tab", group = "Publications")
@Preview(name = "Interests screen publications tab (dark)", group = "Publications", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPublicationsTab() {
    val publications: List<String> = runBlocking {
        (FakeInterestsRepository().getPublications() as Result.Success).data
    }
    JetnewsTheme {
        Surface {
            TabWithTopics(topics = publications, selectedTopics = setOf()) { }
        }
    }
}

/**
 * Returns a fake [List] of [TabContent] to be used for previews.
 */
private fun getFakeTabsContent(): List<TabContent> {
    val interestsRepository = FakeInterestsRepository()
    val topicsSection = TabContent(Sections.Topics) {
        TabWithSections(
            sections = runBlocking { (interestsRepository.getTopics() as Result.Success).data },
            selectedTopics = emptySet()
        ) { }
    }
    val peopleSection = TabContent(Sections.People) {
        TabWithTopics(
            topics = runBlocking { (interestsRepository.getPeople() as Result.Success).data },
            selectedTopics = emptySet()
        ) { }
    }
    val publicationSection = TabContent(Sections.Publications) {
        TabWithTopics(
            topics = runBlocking { (interestsRepository.getPublications() as Result.Success).data },
            selectedTopics = emptySet()
        ) { }
    }

    return listOf(topicsSection, peopleSection, publicationSection)
}
