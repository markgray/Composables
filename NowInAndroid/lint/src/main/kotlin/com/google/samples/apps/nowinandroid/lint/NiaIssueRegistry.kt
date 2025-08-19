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

package com.google.samples.apps.nowinandroid.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.google.samples.apps.nowinandroid.lint.designsystem.DesignSystemDetector

/**
 * An issue registry that checks for common issues specific to Now in Android.
 */
class NiaIssueRegistry : IssueRegistry() {

    /**
     * The list of issues that will be checked when running lint.
     */
    override val issues: List<Issue> = listOf(
        DesignSystemDetector.ISSUE,
        TestMethodNameDetector.FORMAT,
        TestMethodNameDetector.PREFIX,
    )

    /**
     * The API version this issue registry is targeting.
     *
     * Generally, the Lint APIs are backwards compatible, so Issues can be developed with an older
     * version of Lint and run on a newer version. However, some APIs are newer and require a newer
     * version of Lint to run. When developing a custom Issue, you should set this value to
     * `com.android.tools.lint.detector.api.CURRENT_API`.
     *
     * If you are deliberately writing your Issue to be compatible with an older version of Lint,
     * you can set this value to a lower API level.
     *
     * @see com.android.tools.lint.detector.api.CURRENT_API
     * @see minApi
     */
    override val api: Int = CURRENT_API

    /**
     * The minimum API version this issue registry is targeting.
     *
     * Issues are backwards compatible, so when developing a custom Issue, you can specify this
     * value to a lower API level to make it compatible with an older version of Lint.
     *
     * @see api
     * @see com.android.tools.lint.detector.api.CURRENT_API
     */
    override val minApi: Int = 12

    /**
     * The vendor checking for issues.
     *
     * This is used to allow users to disable checks from a particular vendor.
     *
     * The vendor also defines a feedback URL and contact address which is used when printing
     * Lint reports, so that users can report false positives or other issues with the Lint checks.
     *
     * @see Vendor
     */
    override val vendor: Vendor = Vendor(
        vendorName = "Now in Android",
        feedbackUrl = "https://github.com/android/nowinandroid/issues",
        contact = "https://github.com/android/nowinandroid",
    )
}
