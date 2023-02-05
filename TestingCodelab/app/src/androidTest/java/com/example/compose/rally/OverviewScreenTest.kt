package com.example.compose.rally

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.compose.rally.ui.overview.AlertHeader
import com.example.compose.rally.ui.overview.AlertCard
import com.example.compose.rally.ui.overview.OverviewBody
import org.junit.Rule
import org.junit.Test

class OverviewScreenTest {

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
     * This test tests whether the "Alerts" Composable is displayed when the [OverviewBody] is on
     * screen (the [AlertHeader] in the [AlertCard] of [OverviewBody] displays the text "Alerts").
     * We start by calling the method [ComposeContentTestRule.setContent] of our [composeTestRule]
     * field to have it set the [OverviewBody] composable as the content of the current screen. Then
     * we call the [ComposeContentTestRule.onNodeWithText] method to find a semantics node with the
     * text "Alerts", and then we chain a call to the [SemanticsNodeInteraction.assertIsDisplayed]
     * on the [SemanticsNodeInteraction] returned to assert that the semantics node is displayed on
     * screen (throws [AssertionError] if the node is not displayed).
     */
    @Test
    fun overviewScreen_alertsDisplayed() {
        composeTestRule.setContent {
            OverviewBody()
        }

        composeTestRule
            .onNodeWithText("Alerts")
            .assertIsDisplayed()
    }
}
