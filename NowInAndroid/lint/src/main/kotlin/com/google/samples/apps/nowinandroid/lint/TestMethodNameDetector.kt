/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.lint

import com.android.tools.lint.detector.api.AnnotationInfo
import com.android.tools.lint.detector.api.AnnotationUsageInfo
import com.android.tools.lint.detector.api.Category.Companion.TESTING
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope.JAVA_FILE
import com.android.tools.lint.detector.api.Scope.TEST_SOURCES
import com.android.tools.lint.detector.api.Severity.WARNING
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat.RAW
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UElement
import java.util.EnumSet
import kotlin.io.path.Path

/**
 * A detector that checks for common patterns in naming the test methods:
 *  - [detectPrefix] removes unnecessary "test" prefix in all unit test.
 *  - [detectFormat] Checks the `given_when_then` format of Android instrumented tests
 *  (backticks are not supported).
 */
class TestMethodNameDetector : Detector(), SourceCodeScanner {

    /**
     * This detector only needs to analyze elements that are annotated with "org.junit.Test".
     *
     * @return a list of fully qualified annotation names.
     */
    override fun applicableAnnotations(): List<String> = listOf("org.junit.Test")

    /**
     * Called whenever the given element references a referenced element that has been annotated with
     * one of the annotations returned from applicableAnnotations, pointed to by annotationInfo.
     * The element itself may not be annotated; it can also be a member in a class which has been
     * annotated, or within an outer class which has been annotated, or in a file that has been
     * annotated with an annotation, or in a package, and so on. The usageInfo data provides
     * additional context; most importantly, it will include all relevant annotations in the
     * hierarchy, in scope order, and an index pointing to which specific annotation the callback
     * is pointing to. This can be used to handle scoping when there are multiple related
     * annotations. For example, let's say you have two annotations, @Mutable and @Immutable.
     * When you're visiting an @Immutable annotation, that annotation could be coming from an
     * outer class where a closer class or immediate method annotation is marked @Mutable.
     * In this case, you'll want to visit the usageInfo and make sure that none of the annotations
     * leading up to the AnnotationUsageInfo.index are the @Mutable annotation which would override
     * the @Immutable annotation on the class. You don't need to do this for repeated occurrences
     * of the same annotation; lint will already skip any later or outer scope usages of the same
     * annotation since it's almost always the case that the closer annotation is a redefinition
     * which overrides the outer one, and leaving this up to detectors to worry about would probably
     * lead to subtle bugs. Note that these annotations are included in the
     * AnnotationUsageInfo.annotations list, so you can look for them in the callback to the
     * innermost one if you do want to consider outer occurrences of the same annotation.
     * For more information, see the annotations chapter of the lint api guide.
     *
     * @param context the context in which the element is being processed.
     * @param element the element that was annotated.
     * @param annotationInfo the annotation that was used to annotate the element.
     * @param usageInfo the usage of the annotation in the element.
     */
    override fun visitAnnotationUsage(
        context: JavaContext,
        element: UElement,
        annotationInfo: AnnotationInfo,
        usageInfo: AnnotationUsageInfo,
    ) {
        val method = usageInfo.referenced as? PsiMethod ?: return

        method.detectPrefix(context, usageInfo)
        method.detectFormat(context, usageInfo)
    }

    /**
     * Returns true if the test is an Android UI test.
     */
    private fun JavaContext.isAndroidTest() = Path(path = "androidTest") in file.toPath()

    /**
     * Detects if the test method name starts with "test" and reports an issue if it does.
     * It also provides a quick fix to remove the prefix. This check is applied to all test methods.
     *
     * @param context the context in which the element is being processed.
     * @param usageInfo the usage of the annotation in the element.
     */
    private fun PsiMethod.detectPrefix(
        context: JavaContext,
        usageInfo: AnnotationUsageInfo,
    ) {
        if (!name.startsWith(prefix = "test")) return
        context.report(
            issue = PREFIX,
            scope = usageInfo.usage,
            location = context.getNameLocation(this),
            message = PREFIX.getBriefDescription(RAW),
            quickfixData = LintFix.create()
                .name(displayName = "Remove prefix")
                .replace().pattern(oldPattern = """test[\s_]*""")
                .with(newText = "")
                .autoFix()
                .build(),
        )
    }

    /**
     * Detects if the test method name follows the `given_when_then` or `when_then` format and
     * reports an issue if it doesn't. This check is applied only to Android UI tests.
     *
     * @param context the context in which the element is being processed.
     * @param usageInfo the usage of the annotation in the element.
     */
    private fun PsiMethod.detectFormat(
        context: JavaContext,
        usageInfo: AnnotationUsageInfo,
    ) {
        if (!context.isAndroidTest()) return
        if ("""[^\W_]+(_[^\W_]+){1,2}""".toRegex().matches(input = name)) return
        context.report(
            issue = FORMAT,
            scope = usageInfo.usage,
            location = context.getNameLocation(element = this),
            message = FORMAT.getBriefDescription(format = RAW),
        )
    }

    companion object {

        /**
         * Creates an [Issue] with the given parameters.
         *
         * @param id the id of the issue.
         * @param briefDescription the brief description of the issue.
         * @param explanation the explanation of the issue.
         * @return an [Issue] with the given parameters.
         */
        private fun issue(
            id: String,
            briefDescription: String,
            explanation: String,
        ): Issue = Issue.create(
            id = id,
            briefDescription = briefDescription,
            explanation = explanation,
            category = TESTING,
            priority = 5,
            severity = WARNING,
            implementation = Implementation(
                TestMethodNameDetector::class.java,
                EnumSet.of(JAVA_FILE, TEST_SOURCES),
            ),
        )

        /**
         * An issue that is reported when a test method starts with `test`.
         */
        @JvmField
        val PREFIX: Issue = issue(
            id = "TestMethodPrefix",
            briefDescription = "Test method starts with `test`",
            explanation = "Test method should not start with `test`.",
        )

        /**
         * An issue that is reported when a test method does not follow the `given_when_then`
         * or `when_then` format. This check is applied only to Android UI tests.
         */
        @JvmField
        val FORMAT: Issue = issue(
            id = "TestMethodFormat",
            briefDescription = "Test method does not follow the `given_when_then` or `when_then` format",
            explanation = "Test method should follow the `given_when_then` or `when_then` format.",
        )
    }
}
