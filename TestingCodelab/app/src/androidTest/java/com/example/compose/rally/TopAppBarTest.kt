package com.example.compose.rally

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.example.compose.rally.ui.components.RallyTopAppBar
import org.junit.Rule
import org.junit.Test

class TopAppBarTest {

    /**
     * The `@get` annotation tells kotlin to annotate the Java getter. The full list of supported
     * use-site targets is:
     *  - `file` the entire file
     *  - `property` (annotations with this target are not visible to Java)
     *  - `field`
     *  - `get` (property getter)
     *  - `set` (property setter)
     *  - `receiver` (receiver parameter of an extension function or property)
     *  - `param` (constructor parameter)
     *  - `setparam` (property setter parameter)
     *  - `delegate` (the field storing the delegate instance for a delegated property)
     *
     * The `Rule` annotation Annotates fields that reference rules or methods that return a rule.
     * A field must be public, not static, and a subtype of `TestRule` (preferred) or `MethodRule`.
     * A method must be public, not static, and must return a subtype of `TestRule` (preferred) or
     * `MethodRule`. The Statement passed to the `TestRule` will run any `Before` methods, then the
     * `Test` method, and finally any `After` methods, throwing an exception if any of these fail.
     * If there are multiple annotated `Rules` on a class, they will be applied in order of methods
     * first, then fields. However, if there are multiple fields (or methods) they will be applied
     * in an order that depends on your JVM's implementation of the reflection API, which is
     * undefined, in general. Rules defined by fields will always be applied after Rules defined by
     * methods, i.e. the Statements returned by the former will be executed around those returned by
     * the latter.
     *
     * A [ComposeContentTestRule] allows you to set content without the necessity to provide a host
     * for the content. The host, such as an `Activity`, will be created by the test rule. This is
     * the [ComposeContentTestRule] that we use for all the tests in this file. If we wanted to
     * start the app's main activity you can do this with `createAndroidComposeRule`:
     *  - @get:Rule
     *  - val composeTestRule = createAndroidComposeRule(RallyActivity::class.java)
     */
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    /**
     * The `@Test` annotation tells JUnit that the public void method to which it is attached can be
     * run as a test case. To run the method, JUnit first constructs a fresh instance of the class
     * then invokes the annotated method. Any exceptions thrown by the test will be reported by JUnit
     * as a failure. If no exceptions are thrown, the test is assumed to have succeeded. This test
     * checks to see if the [RallyScreen.Accounts] screen is selected as it should be. It does this
     * by first initializing its [List] of [RallyScreen] variable `val allScreens` to all of the
     * [RallyScreen.values] (needed to call [RallyTopAppBar]). It then calls the method
     * [ComposeContentTestRule.setContent] method of our [composeTestRule] field to have it set the
     * [RallyTopAppBar] composable as a content of the current screen (Use this in your tests
     * to setup the UI content to be tested. This should be called exactly once per test). The
     * `allScreens` argument of [RallyTopAppBar] is our `allScreens` variable, its `onTabSelected`
     * lambda argument is a do-nothing lambda, and its `currentScreen` argument is the screen
     * [RallyScreen.Accounts] (which should set the currently selected [RallyScreen] tab of the
     * [RallyTopAppBar] to it). Finally we call the [ComposeContentTestRule.onNodeWithContentDescription]
     * method of [composeTestRule] with the `label` argument the [RallyScreen.name] of
     * [RallyScreen.Accounts] to find a semantics node with the that `contentDescription`. Then
     * we use the [SemanticsNodeInteraction] returned to call its
     * [SemanticsNodeInteraction.assertIsSelected] method which asserts that the current semantics
     * node is selected (throws [AssertionError] if the node is unselected or not selectable).
     */
    @Test
    fun rallyTopAppBarTest_currentTabSelected() {
        val allScreens: List<RallyScreen> = RallyScreen.values().toList()
        composeTestRule.setContent {
            RallyTopAppBar(
                allScreens = allScreens,
                onTabSelected = { },
                currentScreen = RallyScreen.Accounts
            )
        }

        composeTestRule
            .onNodeWithContentDescription(label = RallyScreen.Accounts.name)
            .assertIsSelected()
    }

    /**
     * This test tests whether a node exists which contains text which matches the uppercase of
     * [RallyScreen.Accounts.name]. It does this by by first initializing its [List] of [RallyScreen]
     * variable `val allScreens` to all of the [RallyScreen.values] (needed to call [RallyTopAppBar]).
     * It then calls the method [ComposeContentTestRule.setContent] method of our [composeTestRule]
     * field to have it set the [RallyTopAppBar] composable as a content of the current screen (Use
     * this in your tests to setup the UI content to be tested. This should be called exactly once
     * per test). The `allScreens` argument of [RallyTopAppBar] is our `allScreens` variable, its
     * `onTabSelected` lambda argument is a do-nothing lambda, and its `currentScreen` argument is
     * the screen [RallyScreen.Accounts]. It then calls the [ComposeContentTestRule.onNode] method
     * of our field [composeTestRule], with its `matcher` argument the result of "and-ing" the
     * two [SemanticsMatcher]'s: [hasText] with a `text` argument of the uppercase version of the
     * [RallyScreen.name] of [RallyScreen.Accounts] (value to match), and a [hasParent] which matches
     * the [hasContentDescription] of [RallyScreen.Accounts.name]. The `useUnmergedTree` argument of
     * `onNode` is `true` to enable it to find within merged composables like `Button`. To the
     * `onNode` we chain an `assertExists` to assert that the component was found and is part of
     * the component tree.
     */
    @Test
    fun rallyTopAppBarTest_currentLabelExists() {
        val allScreens = RallyScreen.values().toList()
        composeTestRule.setContent {
            RallyTopAppBar(
                allScreens = allScreens,
                onTabSelected = { },
                currentScreen = RallyScreen.Accounts
            )
        }

        composeTestRule
            .onNode(
                matcher = hasText(text = RallyScreen.Accounts.name.uppercase()) and
                    hasParent(hasContentDescription(RallyScreen.Accounts.name)),
                useUnmergedTree = true
            )
            .assertExists()
    }

}
