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

package com.google.samples.apps.nowinandroid.lint.designsystem

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression

/**
 * This detector checks for incorrect usages of Compose Material APIs like `MaterialTheme`, `Button`,
 * or `Scaffold` when equivalents are available in the Now in Android design system module.
 *
 * It flags usages of Material components that should be replaced by their `Nia` counterparts
 * (e.g., `NiaTheme`, `NiaButton`, `NiaScaffold`) to ensure a consistent look and feel across the app.
 */
class DesignSystemDetector : Detector(), Detector.UastScanner {

    /**
     * This lint check is interested in visiting call expressions (method calls)
     * and qualified reference expressions (like `MaterialTheme.typography`)
     * in the UAST - the Universal Abstract Syntax Tree.
     *
     * @return a list of UAST element types that this check is interested in.
     */
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UCallExpression::class.java,
        UQualifiedReferenceExpression::class.java,
    )

    /**
     * This handler is called by Lint when it encounters a UAST element of a type that we're
     * interested in. The handler then needs to inspect the node to see if it matches our criteria.
     *
     * In this case, we're looking for call expressions that are named like Material components
     * (e.g., "Button", "Text", "Scaffold") or qualified reference expressions that are named
     * like Material objects (e.g., "MaterialTheme.typography", "MaterialTheme.shapes").
     *
     * If we find a match, we report an issue to Lint.
     *
     * @param context The context of the lint check.
     * @return A UElementHandler that will be called by Lint when it encounters a UAST element.
     */
    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            /**
             * This function is called when a `UCallExpression` (a method call in the UAST)
             * is visited.
             *
             * It checks if the method call's name is in the `METHOD_NAMES` map, which
             * contains mappings from Material components to their Nia equivalents.
             *
             * If a mapping is found, it means a Material component is being used instead
             * of its Nia counterpart. In this case, an issue is reported using `reportIssue`.
             *
             * @param node The `UCallExpression` node being visited.
             */
            override fun visitCallExpression(node: UCallExpression) {
                val name: String = node.methodName ?: return
                val preferredName: String = METHOD_NAMES[name] ?: return
                reportIssue(
                    context = context,
                    node = node,
                    name = name,
                    preferredName = preferredName,
                )
            }

            /**
             * This method is called when a qualified reference expression is visited.
             *
             * A qualified reference expression is a reference that is qualified by a receiver,
             * such as `MaterialTheme.typography` or `MaterialTheme.shapes`.
             *
             * This method checks if the receiver of the qualified reference expression
             * is a Material API that has an equivalent in the Now in Android design system.
             *
             * If it is, it reports an issue.
             *
             * @param node the qualified reference expression to visit.
             */
            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                val name: String = node.receiver.asRenderString()
                val preferredName: String = RECEIVER_NAMES[name] ?: return
                reportIssue(
                    context = context,
                    node = node,
                    name = name,
                    preferredName = preferredName,
                )
            }
        }

    companion object {
        /**
         * The issue detected by this lint check.
         *
         * It identifies usages of Compose Material components that have equivalents in the
         * Now in Android design system module and suggests replacing them.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "DesignSystem",
            briefDescription = "Design system",
            explanation = "This check highlights calls in code that use Compose Material " +
                "composables instead of equivalents from the Now in Android design system " +
                "module.",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                DesignSystemDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )

        // Unfortunately :lint is a Java module and thus can't depend on the :core-designsystem
        // Android module, so we can't use composable function references (eg. ::Button.name)
        // instead of hardcoded names.
        /**
         * A map of Material method names to their corresponding `Nia` equivalents.
         *
         * This map is used by the `DesignSystemDetector` to identify Material components
         * that should be replaced by their `Nia` counterparts. The keys of the map are
         * the names of the Material methods, and the values are the names of the
         * corresponding `Nia` methods.
         *
         * For example, the entry `"Button" to "NiaButton"` indicates that usages of
         * the Material `Button` composable should be replaced with the `NiaButton`
         * composable.
         */
        val METHOD_NAMES: Map<String, String> = mapOf(
            "MaterialTheme" to "NiaTheme",
            "Button" to "NiaButton",
            "OutlinedButton" to "NiaOutlinedButton",
            "TextButton" to "NiaTextButton",
            "FilterChip" to "NiaFilterChip",
            "ElevatedFilterChip" to "NiaFilterChip",
            "NavigationBar" to "NiaNavigationBar",
            "NavigationBarItem" to "NiaNavigationBarItem",
            "NavigationRail" to "NiaNavigationRail",
            "NavigationRailItem" to "NiaNavigationRailItem",
            "TabRow" to "NiaTabRow",
            "Tab" to "NiaTab",
            "IconToggleButton" to "NiaIconToggleButton",
            "FilledIconToggleButton" to "NiaIconToggleButton",
            "FilledTonalIconToggleButton" to "NiaIconToggleButton",
            "OutlinedIconToggleButton" to "NiaIconToggleButton",
            "CenterAlignedTopAppBar" to "NiaTopAppBar",
            "SmallTopAppBar" to "NiaTopAppBar",
            "MediumTopAppBar" to "NiaTopAppBar",
            "LargeTopAppBar" to "NiaTopAppBar",
        )

        /**
         * A map of Material API names to their Nia equivalents.
         *
         * This is used by the `visitQualifiedReferenceExpression` method to check if a
         * qualified reference expression is using a Material API that has an equivalent
         * in the Now in Android design system.
         *
         * For example, if the code contains `MaterialTheme.typography`, this map will
         * be used to find the Nia equivalent, which is `NiaTheme.typography`.
         */
        val RECEIVER_NAMES: Map<String, String> = mapOf(
            "Icons" to "NiaIcons",
        )

        /**
         * Reports an issue to Lint.
         *
         * @param context The context of the lint check.
         * @param node The UAST node that the issue is associated with.
         * @param name The name of the Material component that was used.
         * @param preferredName The name of the Nia component that should be used instead.
         */
        fun reportIssue(
            context: JavaContext,
            node: UElement,
            name: String,
            preferredName: String,
        ) {
            context.report(
                issue = ISSUE,
                scope = node,
                location = context.getLocation(element = node),
                message = "Using $name instead of $preferredName",
            )
        }
    }
}
