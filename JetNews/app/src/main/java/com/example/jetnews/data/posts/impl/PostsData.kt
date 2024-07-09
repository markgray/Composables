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

@file:Suppress("ktlint:max-line-length") // String constants read better
package com.example.jetnews.data.posts.impl

import com.example.jetnews.R
import com.example.jetnews.model.Markup
import com.example.jetnews.model.MarkupType
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Paragraph
import com.example.jetnews.model.ParagraphType
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostAuthor
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.model.Publication

/**
 * Define hardcoded posts to avoid handling any non-ui operations.
 */

/**
 * The [PostAuthor] of [Post] sample [post1].
 */
val pietro: PostAuthor = PostAuthor(
    name = "Pietro Maggi",
    url = "https://medium.com/@pmaggi"
)

/**
 * The [PostAuthor] of [Post] sample [post2].
 */
val manuel: PostAuthor = PostAuthor(
    name = "Manuel Vivo",
    url = "https://medium.com/@manuelvicnt"
)

/**
 * The [PostAuthor] of [Post] sample [post3] and [post5].
 */
val florina: PostAuthor = PostAuthor(
    name = "Florina Muntenescu",
    url = "https://medium.com/@florina.muntenescu"
)

/**
 * The [PostAuthor] of [Post] sample [post4].
 */
val jose: PostAuthor = PostAuthor(
    name = "Jose Alcérreca",
    url = "https://medium.com/@JoseAlcerreca"
)

/**
 * The [PostAuthor] of [Post] sample [post6].
 */
val androidstudioteam =
    PostAuthor("Android Studio Team", "https://twitter.com/androidstudio")

/**
 * The [Publication] which published all five of our [Post].
 */
val publication: Publication = Publication(
    name = "Android Developers",
    logoUrl = "https://cdn-images-1.medium.com/max/258/1*u7oZc2_5mrkcFaxkXEyfYA@2x.png"
)

/**
 * These are the [Paragraph] used for [Post] sample [post1].
 */
val paragraphsPost1: List<Paragraph> = listOf(
    Paragraph(
        type = ParagraphType.Text,
        text = "Working to make our Android application more modular, I ended up with a sample that included a set of on-demand features grouped inside a folder:"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Pretty standard setup, all the on-demand modules, inside a “features” folder; clean."
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "These modules are included in the settings.gradle file as:"
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = "include ':app'\n" +
            "include ':features:module1'\n" +
            "include ':features:module2'\n" +
            "include ':features:module3'\n" +
            "include ':features:module4'"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "These setup works nicely with a single “minor” issue: an empty module named features in the Android view in Android Studio:"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "I can live with that, but I would much prefer to remove that empty module from my project!"
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "If you cannot remove it, just rename it!"
    ),

    Paragraph(
        type = ParagraphType.Text,
        text = "At I/O I was lucky enough to attend the “Android Studio: Tips and Tricks” talk where Ivan Gravilovic, from Google, shared some amazing tips. One of these was a possible solution for my problem: setting a custom path for my modules.",
        markups = listOf(
            Markup(
                MarkupType.Italic,
                41,
                72
            )
        )
    ),

    Paragraph(
        type = ParagraphType.Text,
        text = "In this particular case our settings.gradle becomes:",
        markups = listOf(Markup(type = MarkupType.Code, start = 28, end = 43))
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = """
                include ':app'
                include ':module1'
                include ':module1'
                include ':module1'
                include ':module1'
                """.trimIndent()
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = """
                // Set a custom path for the four features modules.
                // This avoid to have an empty "features" module in  Android Studio.
                project(":module1").projectDir=new File(rootDir, "features/module1")
                project(":module2").projectDir=new File(rootDir, "features/module2")
                project(":module3").projectDir=new File(rootDir, "features/module3")
                project(":module4").projectDir=new File(rootDir, "features/module4")
                """.trimIndent()
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "And the layout in Android Studio is now:"
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "Conclusion"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "As the title says, this is really a small thing, but it helps keep my project in order and it shows how a small Gradle configuration can help keep your project tidy."
    ),
    Paragraph(
        type = ParagraphType.Quote,
        text = "You can find this update in the latest version of the on-demand modules codelab.",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 54,
                end = 79,
                href = "https://codelabs.developers.google.com/codelabs/on-demand-dynamic-delivery/index.html"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "Resources"
    ),
    Paragraph(
        type = ParagraphType.Bullet,
        text = "Android Studio: Tips and Tricks (Google I/O’19)",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 0,
                end = 47,
                href = "https://www.youtube.com/watch?v=ihF-PwDfRZ4&list=PLWz5rJ2EKKc9FfSQIRXEWyWpHD6TtwxMM&index=32&t=0s"
            )
        )
    ),

    Paragraph(
        type = ParagraphType.Bullet,
        text = "On Demand module codelab",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 0,
                end = 24,
                href = "https://codelabs.developers.google.com/codelabs/on-demand-dynamic-delivery/index.html"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Bullet,
        text = "Patchwork Plaid — A modularization story",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 0,
                end = 40,
                href = "https://medium.com/androiddevelopers/a-patchwork-plaid-monolith-to-modularized-app-60235d9f212e"
            )
        )
    )
)

/**
 * These are the [Paragraph] used for [Post] sample [post2].
 */
val paragraphsPost2: List<Paragraph> = listOf(
    Paragraph(
        type = ParagraphType.Text,
        text = "Dagger is a popular Dependency Injection framework commonly used in Android. It provides fully static and compile-time dependencies addressing many of the development and performance issues that have reflection-based solutions.",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 0,
                end = 6,
                href = "https://dagger.dev/"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "This month, a new tutorial was released to help you better understand how it works. This article focuses on using Dagger with Kotlin, including best practices to optimize your build time and gotchas you might encounter.",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 14,
                end = 26,
                href = "https://dagger.dev/tutorial/"
            ),
            Markup(type = MarkupType.Bold, start = 114, end = 132),
            Markup(type = MarkupType.Bold, start = 144, end = 159),
            Markup(type = MarkupType.Bold, start = 191, end = 198)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Dagger is implemented using Java’s annotations model and annotations in Kotlin are not always directly parallel with how equivalent Java code would be written. This post will highlight areas where they differ and how you can use Dagger with Kotlin without having a headache."
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "This post was inspired by some of the suggestions in this Dagger issue that goes through best practices and pain points of Dagger in Kotlin. Thanks to all of the contributors that commented there!",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 58,
                end = 70,
                href = "https://github.com/google/dagger/issues/900"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "kapt build improvements"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "To improve your build time, Dagger added support for gradle’s incremental annotation processing in v2.18! This is enabled by default in Dagger v2.24. In case you’re using a lower version, you need to add a few lines of code (as shown below) if you want to benefit from it.",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 99,
                end = 104,
                href = "https://github.com/google/dagger/releases/tag/dagger-2.18"
            ),
            Markup(
                type = MarkupType.Link,
                start = 143,
                end = 148,
                href = "https://github.com/google/dagger/releases/tag/dagger-2.24"
            ),
            Markup(type = MarkupType.Bold, start = 53, end = 95)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Also, you can tell Dagger not to format the generated code. This option was added in Dagger v2.18 and it’s the default behavior (doesn’t generate formatted code) in v2.23. If you’re using a lower version, disable code formatting to improve your build time (see code below).",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 92,
                end = 97,
                href = "https://github.com/google/dagger/releases/tag/dagger-2.18"
            ),
            Markup(
                type = MarkupType.Link,
                start = 165,
                end = 170,
                href = "https://github.com/google/dagger/releases/tag/dagger-2.23"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Include these compiler arguments in your build.gradle file to make Dagger more performant at build time:",
        markups = listOf(Markup(MarkupType.Code, 41, 53))
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Alternatively, if you use Kotlin DSL script files, include them like this in the build.gradle.kts file of the modules that use Dagger:",
        markups = listOf(Markup(type = MarkupType.Code, start = 81, end = 97))
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Qualifiers for field attributes"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "",
        markups = listOf(Markup(type = MarkupType.Link, start = 0, end = 0))
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "When an annotation is placed on a property in Kotlin, it’s not clear whether Java will see that annotation on the field of the property or the method for that property. Setting the field: prefix on the annotation ensures that the qualifier ends up in the right place (See documentation for more details).",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 181, end = 187),
            Markup(
                type = MarkupType.Link,
                start = 268,
                end = 285,
                href = "http://frogermcs.github.io/dependency-injection-with-dagger-2-custom-scopes/"
            ),
            Markup(type = MarkupType.Italic, start = 114, end = 119),
            Markup(type = MarkupType.Italic, start = 143, end = 149)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "✅ The way to apply qualifiers on an injected field is:"
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = "@Inject @field:MinimumBalance lateinit var minimumBalance: BigDecimal",
        markups = listOf(Markup(type = MarkupType.Bold, start = 8, end = 29))
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "❌ As opposed to:"
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = """
                @Inject @MinimumBalance lateinit var minimumBalance: BigDecimal 
                // @MinimumBalance is ignored!
                """.trimIndent(),
        markups = listOf(Markup(type = MarkupType.Bold, start = 65, end = 95))
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Forgetting to add field: could lead to injecting the wrong object if there’s an unqualified instance of that type available in the Dagger graph.",
        markups = listOf(Markup(type = MarkupType.Code, start = 18, end = 24))
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "Static @Provides functions optimization"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Dagger’s generated code will be more performant if @Provides methods are static. To achieve this in Kotlin, use a Kotlin object instead of a class and annotate your methods with @JvmStatic. This is a best practice that you should follow as much as possible.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 51, end = 60),
            Markup(type = MarkupType.Code, start = 73, end = 79),
            Markup(type = MarkupType.Code, start = 121, end = 127),
            Markup(type = MarkupType.Code, start = 141, end = 146),
            Markup(type = MarkupType.Code, start = 178, end = 188),
            Markup(type = MarkupType.Bold, start = 200, end = 213),
            Markup(type = MarkupType.Italic, start = 200, end = 213)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "In case you need an abstract method, you’ll need to add the @JvmStatic method to a companion object and annotate it with @Module too.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 60, end = 70),
            Markup(type = MarkupType.Code, start = 121, end = 128)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Alternatively, you can extract the object module out and include it in the abstract one:"
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "Injecting Generics"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Kotlin compiles generics with wildcards to make Kotlin APIs work with Java. These are generated when a type appears as a parameter (more info here) or as fields. For example, a Kotlin List<Foo> parameter shows up as List<? super Foo> in Java.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 184, end = 193),
            Markup(type = MarkupType.Code, start = 216, end = 233),
            Markup(
                type = MarkupType.Link,
                start = 132,
                end = 146,
                href = "https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#variant-generics"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "This causes problems with Dagger because it expects an exact (aka invariant) type match. Using @JvmSuppressWildcards will ensure that Dagger sees the type without wildcards.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 95, end = 116),
            Markup(
                type = MarkupType.Link,
                start = 66,
                end = 75,
                href = "https://en.wikipedia.org/wiki/Class_invariant"
            ),
            Markup(
                type = MarkupType.Link,
                start = 96,
                end = 116,
                href = "https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-suppress-wildcards/index.html"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "This is a common issue when you inject collections using Dagger’s multibinding feature, for example:",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 57,
                end = 86,
                href = "https://dagger.dev/multibindings.html"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = """
                class MyVMFactory @Inject constructor(
                  private val vmMap: Map<String, @JvmSuppressWildcards Provider<ViewModel>>
                ) { 
                    ... 
                }
                """.trimIndent(),
        markups = listOf(Markup(type = MarkupType.Bold, start = 72, end = 93))
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "Inline method bodies"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Dagger determines the types that are configured by @Provides methods by inspecting the return type. Specifying the return type in Kotlin functions is optional and even the IDE sometimes encourages you to refactor your code to have inline method bodies that hide the return type declaration.",
        markups = listOf(Markup(type = MarkupType.Code, start = 51, end = 60))
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "This can lead to bugs if the inferred type is different from the one you meant. Let’s see some examples:"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "If you want to add a specific type to the graph, inlining works as expected. See the different ways to do the same in Kotlin:"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "If you want to provide an implementation of an interface, then you must explicitly specify the return type. Not doing it can lead to problems and bugs:"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Dagger mostly works with Kotlin out of the box. However, you have to watch out for a few things just to make sure you’re doing what you really mean to do: @field: for qualifiers on field attributes, inline method bodies, and @JvmSuppressWildcards when injecting collections.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 155, end = 162),
            Markup(type = MarkupType.Code, start = 225, end = 246)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Dagger optimizations come with no cost, add them and follow best practices to improve your build time: enabling incremental annotation processing, disabling formatting and using static @Provides methods in your Dagger modules.",
        markups = listOf(
            Markup(
                type = MarkupType.Code,
                start = 185,
                end = 194
            )
        )
    )
)

/**
 * These are the [Paragraph] used for [Post] sample [post3].
 */
val paragraphsPost3: List<Paragraph> = listOf(
    Paragraph(
        type = ParagraphType.Text,
        text = "Learn how to get started converting Java Programming Language code to Kotlin, making it more idiomatic and avoid common pitfalls, by following our new Refactoring to Kotlin codelab, available in English \uD83C\uDDEC\uD83C\uDDE7, Chinese \uD83C\uDDE8\uD83C\uDDF3 and Brazilian Portuguese \uD83C\uDDE7\uD83C\uDDF7.",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 151,
                end = 172,
                href = "https://codelabs.developers.google.com/codelabs/java-to-kotlin/#0"
            ),
            Markup(
                type = MarkupType.Link,
                start = 209,
                end = 216,
                href = "https://clmirror.storage.googleapis.com/codelabs/java-to-kotlin-zh/index.html#0"
            ),
            Markup(
                type = MarkupType.Link,
                start = 226,
                end = 246,
                href = "https://codelabs.developers.google.com/codelabs/java-to-kotlin-pt-br/#0"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "When you first get started writing Kotlin code, you tend to follow Java Programming Language idioms. The automatic converter, part of both Android Studio and Intellij IDEA, can do a pretty good job of automatically refactoring your code, but sometimes, it needs a little help. This is where our new Refactoring to Kotlin codelab comes in.",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 105,
                end = 124,
                href = "https://www.jetbrains.com/help/idea/converting-a-java-file-to-kotlin-file.html"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "We’ll take two classes (a User and a Repository) in Java Programming Language and convert them to Kotlin, check out what the automatic converter did and why. Then we go to the next level — make it idiomatic, teaching best practices and useful tips along the way.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 26, end = 30),
            Markup(type = MarkupType.Code, start = 37, end = 47)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "The Refactoring to Kotlin codelab starts with basic topics — understand how nullability is declared in Kotlin, what types of equality are defined or how to best handle classes whose role is just to hold data. We then continue with how to handle static fields and functions in Kotlin and how to apply the Singleton pattern, with the help of one handy keyword: object. We’ll see how Kotlin helps us model our classes better, how it differentiates between a property of a class and an action the class can do. Finally, we’ll learn how to execute code only in the context of a specific object with the scope functions.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 245, end = 251),
            Markup(type = MarkupType.Code, start = 359, end = 365),
            Markup(
                type = MarkupType.Link,
                start = 4,
                end = 25,
                href = "https://codelabs.developers.google.com/codelabs/java-to-kotlin/#0"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Thanks to Walmyr Carvalho and Nelson Glauber for translating the codelab in Brazilian Portuguese!",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 21,
                end = 42,
                href = "https://codelabs.developers.google.com/codelabs/java-to-kotlin/#0"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 76,
                end = 96,
                href = "https://codelabs.developers.google.com/codelabs/java-to-kotlin-pt-br/#0"
            )
        )
    )
)

/**
 * These are the [Paragraph] used for [Post] sample [post4].
 */
val paragraphsPost4: List<Paragraph> = listOf(
    Paragraph(
        type = ParagraphType.Text,
        text = "TL;DR: Expose resource IDs from ViewModels to avoid showing obsolete data."
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "In a ViewModel, if you’re exposing data coming from resources (strings, drawables, colors…), you have to take into account that ViewModel objects ignore configuration changes such as locale changes. When the user changes their locale, activities are recreated but the ViewModel objects are not.",
        markups = listOf(
            Markup(
                type = MarkupType.Bold,
                start = 183,
                end = 197
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "AndroidViewModel is a subclass of ViewModel that is aware of the Application context. However, having access to a context can be dangerous if you’re not observing or reacting to the lifecycle of that context. The recommended practice is to avoid dealing with objects that have a lifecycle in ViewModels.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 0, end = 16),
            Markup(type = MarkupType.Code, start = 34, end = 43),
            Markup(type = MarkupType.Bold, start = 209, end = 303)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Let’s look at an example based on this issue in the tracker: Updating ViewModel on system locale change.",
        markups = listOf(
            Markup(
                MarkupType.Link,
                61,
                103,
                "https://issuetracker.google.com/issues/111961971"
            ),
            Markup(MarkupType.Italic, 61, 104)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "The problem is that the string is resolved in the constructor only once. If there’s a locale change, the ViewModel won’t be recreated. This will result in our app showing obsolete data and therefore being only partially localized.",
        markups = listOf(Markup(type = MarkupType.Bold, start = 73, end = 133))
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "As Sergey points out in the comments to the issue, the recommended approach is to expose the ID of the resource you want to load and do so in the view. As the view (activity, fragment, etc.) is lifecycle-aware it will be recreated after a configuration change so the resource will be reloaded correctly.",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 3,
                end = 9,
                href = "https://twitter.com/ZelenetS"
            ),
            Markup(
                type = MarkupType.Link,
                start = 28,
                end = 36,
                href = "https://issuetracker.google.com/issues/111961971#comment2"
            ),
            Markup(type = MarkupType.Bold, start = 82, end = 150)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Even if you don’t plan to localize your app, it makes testing much easier and cleans up your ViewModel objects so there’s no reason not to future-proof."
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "We fixed this issue in the android-architecture repository in the Java and Kotlin branches and we offloaded resource loading to the Data Binding layout.",
        markups = listOf(
            Markup(
                type = MarkupType.Link,
                start = 66,
                end = 70,
                href = "https://github.com/googlesamples/android-architecture/pull/631"
            ),
            Markup(
                type = MarkupType.Link,
                start = 75,
                end = 81,
                href = "https://github.com/googlesamples/android-architecture/pull/635"
            ),
            Markup(
                type = MarkupType.Link,
                start = 128,
                end = 151,
                href = "https://github.com/googlesamples/android-architecture/pull/635/files#diff-7eb5d85ec3ea4e05ecddb7dc8ae20aa1R62"
            )
        )
    )
)

/**
 * These are the [Paragraph] used for [Post] sample [post5].
 */
val paragraphsPost5: List<Paragraph> = listOf(
    Paragraph(
        type = ParagraphType.Text,
        text = "Working with collections is a common task and the Kotlin Standard Library offers many great utility functions. It also offers two ways of working with collections based on how they’re evaluated: eagerly — with Collections, and lazily — with Sequences. Continue reading to find out what’s the difference between the two, which one you should use and when, and what the performance implications of each are.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 210, end = 220),
            Markup(type = MarkupType.Code, start = 241, end = 249),
            Markup(
                type = MarkupType.Link,
                start = 210,
                end = 221,
                href = "https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/index.html"
            ),
            Markup(
                type = MarkupType.Link,
                start = 241,
                end = 250,
                href = "https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/index.html"
            ),
            Markup(type = MarkupType.Bold, start = 130, end = 134),
            Markup(type = MarkupType.Bold, start = 195, end = 202),
            Markup(type = MarkupType.Bold, start = 227, end = 233),
            Markup(type = MarkupType.Italic, start = 130, end = 134)
        )
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "Collections vs sequences"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "The difference between eager and lazy evaluation lies in when each transformation on the collection is performed.",
        markups = listOf(
            Markup(
                type = MarkupType.Italic,
                start = 57,
                end = 61
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Collections are eagerly evaluated — each operation is performed when it’s called and the result of the operation is stored in a new collection. The transformations on collections are inline functions. For example, looking at how map is implemented, we can see that it’s an inline function, that creates a new ArrayList:",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 229, end = 232),
            Markup(type = MarkupType.Code, start = 273, end = 279),
            Markup(type = MarkupType.Code, start = 309, end = 318),
            Markup(
                type = MarkupType.Link,
                start = 183,
                end = 199,
                href = "https://kotlinlang.org/docs/reference/inline-functions.html"
            ),
            Markup(
                type = MarkupType.Link,
                start = 229,
                end = 232,
                href = "https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/common/src/generated/_Collections.kt#L1312"
            ),
            Markup(type = MarkupType.Bold, start = 0, end = 12),
            Markup(type = MarkupType.Italic, start = 16, end = 23)
        )
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = "public inline fun <T, R> Iterable<T>.map(transform: (T) -> R): List<R> {\n" +
            "  return mapTo(ArrayList<R>(collectionSizeOrDefault(10)), transform)\n" +
            "}",
        markups = listOf(
            Markup(type = MarkupType.Bold, start = 7, end = 13),
            Markup(type = MarkupType.Bold, start = 88, end = 97)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Sequences are lazily evaluated. They have two types of operations: intermediate and terminal. Intermediate operations are not performed on the spot; they’re just stored. Only when a terminal operation is called, the intermediate operations are triggered on each element in a row and finally, the terminal operation is applied. Intermediate operations (like map, distinct, groupBy etc) return another sequence whereas terminal operations (like first, toList, count etc) don’t.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 357, end = 360),
            Markup(type = MarkupType.Code, start = 362, end = 370),
            Markup(type = MarkupType.Code, start = 372, end = 379),
            Markup(type = MarkupType.Code, start = 443, end = 448),
            Markup(type = MarkupType.Code, start = 450, end = 456),
            Markup(type = MarkupType.Code, start = 458, end = 463),
            Markup(type = MarkupType.Bold, start = 0, end = 9),
            Markup(type = MarkupType.Bold, start = 67, end = 79),
            Markup(type = MarkupType.Bold, start = 84, end = 92),
            Markup(type = MarkupType.Bold, start = 254, end = 269),
            Markup(type = MarkupType.Italic, start = 14, end = 20)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Sequences don’t hold a reference to the items of the collection. They’re created based on the iterator of the original collection and keep a reference to all the intermediate operations that need to be performed."
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Unlike transformations on collections, intermediate transformations on sequences are not inline functions — inline functions cannot be stored and sequences need to store them. Looking at how an intermediate operation like map is implemented, we can see that the transform function is kept in a new instance of a Sequence:",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 222, end = 225),
            Markup(type = MarkupType.Code, start = 312, end = 320),
            Markup(
                type = MarkupType.Link,
                start = 222,
                end = 225,
                href = "https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/common/src/generated/_Sequences.kt#L860"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = "public fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R>{      \n" +
            "   return TransformingSequence(this, transform)\n" +
            "}",
        markups = listOf(Markup(type = MarkupType.Bold, start = 85, end = 105))
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "A terminal operation, like first, iterates through the elements of the sequence until the predicate condition is matched.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 27, end = 32),
            Markup(
                type = MarkupType.Link,
                start = 27,
                end = 32,
                href = "https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/common/src/generated/_Sequences.kt#L117"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = "public inline fun <T> Sequence<T>.first(predicate: (T) -> Boolean): T {\n" +
            "   for (element in this) if (predicate(element)) return element\n" +
            "   throw NoSuchElementException(“Sequence contains no element matching the predicate.”)\n" +
            "}"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "If we look at how a sequence like TransformingSequence (used in the map above) is implemented, we’ll see that when next is called on the sequence iterator, the transformation stored is also applied.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 34, end = 54),
            Markup(type = MarkupType.Code, start = 68, end = 71)
        )
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = "internal class TransformingIndexedSequence<T, R> \n" +
            "constructor(private val sequence: Sequence<T>, private val transformer: (Int, T) -> R) : Sequence<R> {",
        markups = listOf(
            Markup(
                type = MarkupType.Bold,
                start = 109,
                end = 120
            )
        )
    ),
    Paragraph(
        type = ParagraphType.CodeBlock,
        text = "override fun iterator(): Iterator<R> = object : Iterator<R> {\n" +
            "   …\n" +
            "   override fun next(): R {\n" +
            "     return transformer(checkIndexOverflow(index++), iterator.next())\n" +
            "   }\n" +
            "   …\n" +
            "}",
        markups = listOf(
            Markup(type = MarkupType.Bold, start = 83, end = 89),
            Markup(type = MarkupType.Bold, start = 107, end = 118)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Independent on whether you’re using collections or sequences, the Kotlin Standard Library offers quite a wide range of operations for both, like find, filter, groupBy and others. Make sure you check them out, before implementing your own version of these.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 145, end = 149),
            Markup(type = MarkupType.Code, start = 151, end = 157),
            Markup(type = MarkupType.Code, start = 159, end = 166),
            Markup(
                type = MarkupType.Link,
                start = 193,
                end = 207,
                href = "https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/#functions"
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "Collections and sequences"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Let’s say that we have a list of objects of different shapes. We want to make the shapes yellow and then take the first square shape."
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Let’s see how and when each operation is applied for collections and when for sequences"
    ),
    Paragraph(
        type = ParagraphType.Subhead,
        text = "Collections"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "map is called — a new ArrayList is created. We iterate through all items of the initial collection, transform it by copying the original object and changing the color, then add it to the new list.",
        markups = listOf(Markup(type = MarkupType.Code, start = 0, end = 3))
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "first is called — we iterate through each item until the first square is found",
        markups = listOf(Markup(type = MarkupType.Code, start = 0, end = 5))
    ),
    Paragraph(
        type = ParagraphType.Subhead,
        text = "Sequences"
    ),
    Paragraph(
        type = ParagraphType.Bullet,
        text = "asSequence — a sequence is created based on the Iterator of the original collection",
        markups = listOf(Markup(type = MarkupType.Code, start = 0, end = 10))
    ),
    Paragraph(
        type = ParagraphType.Bullet,
        text = "map is called — the transformation is added to the list of operations needed to be performed by the sequence but the operation is NOT performed",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 0, end = 3),
            Markup(type = MarkupType.Bold, start = 130, end = 133)
        )
    ),
    Paragraph(
        type = ParagraphType.Bullet,
        text = "first is called — this is a terminal operation, so, all the intermediate operations are triggered, on each element of the collection. We iterate through the initial collection applying map and then first on each of them. Since the condition from first is satisfied by the 2nd element, then we no longer apply the map on the rest of the collection.",
        markups = listOf(Markup(type = MarkupType.Code, start = 0, end = 5))
    ),

    Paragraph(
        type = ParagraphType.Text,
        text = "When working with sequences no intermediate collection is created and since items are evaluated one by one, map is only performed on some of the inputs."
    ),
    Paragraph(
        type = ParagraphType.Header,
        text = "Performance"
    ),
    Paragraph(
        type = ParagraphType.Subhead,
        text = "Order of transformations"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Independent of whether you’re using collections or sequences, the order of transformations matters. In the example above, first doesn’t need to happen after map since it’s not a consequence of the map transformation. If we reverse the order of our business logic and call first on the collection and then transform the result, then we only create one new object — the yellow square. When using sequences — we avoid creating 2 new objects, when using collections, we avoid creating an entire new list.",
        markups = listOf(
            Markup(type = MarkupType.Code, start = 122, end = 127),
            Markup(type = MarkupType.Code, start = 157, end = 160),
            Markup(type = MarkupType.Code, start = 197, end = 200)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Because terminal operations can finish processing early, and intermediate operations are evaluated lazily, sequences can, in some cases, help you avoid doing unnecessary work compared to collections. Make sure you always check the order of the transformations and the dependencies between them!"
    ),
    Paragraph(
        type = ParagraphType.Subhead,
        text = "Inlining and large data sets consequences"
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Collection operations use inline functions, so the bytecode of the operation, together with the bytecode of the lambda passed to it will be inlined. Sequences don’t use inline functions, therefore, new Function objects are created for each operation.",
        markups = listOf(
            Markup(
                type = MarkupType.Code,
                start = 202,
                end = 210
            )
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "On the other hand, collections create a new list for every transformation while sequences just keep a reference to the transformation function."
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "When working with small collections, with 1–2 operators, these differences don’t have big implications so working with collections should be ok. But, when working with large lists the intermediate collection creation can become expensive; in such cases, use sequences.",
        markups = listOf(
            Markup(type = MarkupType.Bold, start = 18, end = 35),
            Markup(type = MarkupType.Bold, start = 119, end = 130),
            Markup(type = MarkupType.Bold, start = 168, end = 179),
            Markup(type = MarkupType.Bold, start = 258, end = 267)
        )
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Unfortunately, I’m not aware of any benchmarking study done that would help us get a better understanding on how the performance of collections vs sequences is affected with different sizes of collections or operation chains."
    ),
    Paragraph(
        type = ParagraphType.Text,
        text = "Collections eagerly evaluate your data while sequences do so lazily. Depending on the size of your data, pick the one that fits best: collections — for small lists or sequences — for larger ones, and pay attention to the order of the transformations."
    )
)

/**
 * These are the [Paragraph] used for [Post] sample [post6].
 */
val paragraphsPost6 = listOf(
    Paragraph(
        ParagraphType.Text,
        "The Android Studio logo redesign caught the attention of the developer community since its sneak peek at the Android Developer Summit. We are thrilled to release the new Android Studio logo with the stable release of Flamingo. Now that the new logo is available to most Android Studio users, we can examine the design changes in greater detail and decode their meaning."
    ),
    Paragraph(
        ParagraphType.Text,
        "This case study offers a comprehensive overview of the design journey, from identifying the initial problem to the final outcome. It explores the critical brand elements that the team needed to consider and the tools used throughout the redesign process. This case study also delves into the various stages of design exploration, highlighting the efforts to create a modern logo while honoring the Android Studio brand's legacy."
    ),
    Paragraph(
        ParagraphType.Header,
        "Identifying the problem"
    ),
    Paragraph(
        ParagraphType.Text,
        "You told us the Android Studio logo looked a little weird and complicated. It doesn't shrink down well and it's way too similar to the emulator. We heard you!"
    ),
    Paragraph(
        ParagraphType.Text,
        "The Android Studio logo used between 2020 and 2022 was well-suited for print, but it posed challenges when used as an application icon. Its readability suffered when reduced to smaller sizes, and its similarity to the emulator caused confusion."
    ),
    Paragraph(
        ParagraphType.Text,
        "Additionally, the use of color alone to differentiate between Canary and Stable versions made it difficult for users with color vision deficiencies."
    ),
    Paragraph(
        ParagraphType.Text,
        "The redesign aimed to resolve these concerns by creating a logo that was easy to read, visually distinctive, and followed the OS guidelines when necessary, ensuring accessibility. The new design also maintained a connection with the Android logo family while honoring its legacy."
    ),
    Paragraph(
        ParagraphType.Text,
        "In this case study, we will delve into the version history and evolution of the Android Studio logo and how it has changed over the years."
    ),
    Paragraph(
        ParagraphType.Header,
        "A brief history of the Android Studio logo"
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2013: The original Android Studio logo was a 3D robot that highlighted the gears and interworking of the bugdroid. At this time, the Android Emulator was the bugdroid.",
        listOf(
            Markup(MarkupType.Bold, 0, 5)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2014: The Android Emulator merged to a flat mark but remained otherwise unchanged.",
        listOf(
            Markup(MarkupType.Bold, 0, 5)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2014-2019: An updated Android Studio logo was introduced featuring an \"A\" compass in front of a green circle.",
        listOf(
            Markup(MarkupType.Bold, 0, 10)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2019: In Canary 3.6, the color palette was updated to match Android 10.",
        listOf(
            Markup(MarkupType.Bold, 0, 5)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2020-2022: With the release of Android Studio 4.1 Canary, the \"A\" compass was reduced to an abstract form placed in front of a blueprint. The Android head was also added, peeking over the top.",
        listOf(
            Markup(MarkupType.Bold, 0, 10)
        )
    ),
    Paragraph(
        ParagraphType.Header,
        "Understanding the Android brand elements"
    ),
    Paragraph(
        ParagraphType.Text,
        "When redesigning a logo, it's important to consider brand elements that unify products within an ecosystem. For the Android Developer ecosystem, the \"robot head\" is a key brand element, alongside the primaryAndroid green color. The secondary colors blue and navy, and tertiary colors like orange, can also be utilized for support."
    ),
    Paragraph(
        ParagraphType.Header,
        "Key objectives"
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Iconography: use recognizable and appropriate symbols, such as compass \"A\" for Android Studio or a device for Android Emulator, to convey the purpose and functionality clearly and quickly.",
        listOf(
            Markup(MarkupType.Bold, 0, 12)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Enhance recognition and scalability: the Android Studio and Android Emulator should prioritize legibility and scalability, ensuring that they can be easily recognized and understood even at smaller sizes.",
        listOf(
            Markup(MarkupType.Bold, 0, 36)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Establish distinction: the Android Studio and Android Emulator need to be easily distinguishable, to avoid confusion.",
        listOf(
            Markup(MarkupType.Bold, 0, 22)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Maintain brand consistency: the Android Studio and Android Emulator designs should be consistent with the overall branding and visual identity of the Android family, while still being distinctive.",
        listOf(
            Markup(MarkupType.Bold, 0, 27)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Ensure accessibility: the logo should be accessible to all users, including those with visual impairments. This means using clear shapes, colors, and contrast.",
        listOf(
            Markup(MarkupType.Bold, 0, 21)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Follow OS guidelines: the updated application icon must align with the Android visual language and conform to the guidelines of macOS, Windows, and Linux operating systems, ensuring consistency and coherence across all platforms.",
        listOf(
            Markup(MarkupType.Bold, 0, 21)
        )
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Ensure versatility: the Android Studio logo should be versatile enough to work in a variety of sizes and contexts, such as on different devices and platforms.",
        listOf(
            Markup(MarkupType.Bold, 0, 20)
        )
    ),
    Paragraph(
        ParagraphType.Text,
        "Read More",
        listOf(
            Markup(
                MarkupType.Link,
                0,
                9,
                "https://android-developers.googleblog.com/2023/05/redesigning-android-studio-logo.html"
            )
        )
    )
)

/**
 * [Post] sample [post1].
 */
val post1: Post = Post(
    id = "dc523f0ed25c",
    title = "A Little Thing about Android Module Paths",
    subtitle = "How to configure your module paths, instead of using Gradle’s default.",
    url = "https://medium.com/androiddevelopers/gradle-path-configuration-dc523f0ed25c",
    publication = publication,
    metadata = Metadata(
        author = pietro,
        date = "August 02",
        readTimeMinutes = 1
    ),
    paragraphs = paragraphsPost1,
    imageId = R.drawable.post_1,
    imageThumbId = R.drawable.post_1_thumb
)

/**
 * [Post] sample [post2].
 */
val post2: Post = Post(
    id = "7446d8dfd7dc",
    title = "Dagger in Kotlin: Gotchas and Optimizations",
    subtitle = "Use Dagger in Kotlin! This article includes best practices to optimize your build time and gotchas you might encounter.",
    url = "https://medium.com/androiddevelopers/dagger-in-kotlin-gotchas-and-optimizations-7446d8dfd7dc",
    publication = publication,
    metadata = Metadata(
        author = manuel,
        date = "July 30",
        readTimeMinutes = 3
    ),
    paragraphs = paragraphsPost2,
    imageId = R.drawable.post_2,
    imageThumbId = R.drawable.post_2_thumb
)

/**
 * [Post] sample [post3].
 */
val post3: Post = Post(
    id = "ac552dcc1741",
    title = "From Java Programming Language to Kotlin — the idiomatic way",
    subtitle = "Learn how to get started converting Java Programming Language code to Kotlin, making it more idiomatic and avoid common pitfalls, by…",
    url = "https://medium.com/androiddevelopers/from-java-programming-language-to-kotlin-the-idiomatic-way-ac552dcc1741",
    publication = publication,
    metadata = Metadata(
        author = florina,
        date = "July 09",
        readTimeMinutes = 1
    ),
    paragraphs = paragraphsPost3,
    imageId = R.drawable.post_3,
    imageThumbId = R.drawable.post_3_thumb
)

/**
 * [Post] sample [post4].
 */
val post4: Post = Post(
    id = "84eb677660d9",
    title = "Locale changes and the AndroidViewModel antipattern",
    subtitle = "TL;DR: Expose resource IDs from ViewModels to avoid showing obsolete data.",
    url = "https://medium.com/androiddevelopers/locale-changes-and-the-androidviewmodel-antipattern-84eb677660d9",
    publication = publication,
    metadata = Metadata(
        author = jose,
        date = "April 02",
        readTimeMinutes = 1
    ),
    paragraphs = paragraphsPost4,
    imageId = R.drawable.post_4,
    imageThumbId = R.drawable.post_4_thumb
)

/**
 * [Post] sample [post5].
 */
val post5: Post = Post(
    id = "55db18283aca",
    title = "Collections and sequences in Kotlin",
    subtitle = "Working with collections is a common task and the Kotlin Standard Library offers many great utility functions. It also offers two ways of…",
    url = "https://medium.com/androiddevelopers/collections-and-sequences-in-kotlin-55db18283aca",
    publication = publication,
    metadata = Metadata(
        author = florina,
        date = "July 24",
        readTimeMinutes = 4
    ),
    paragraphs = paragraphsPost5,
    imageId = R.drawable.post_5,
    imageThumbId = R.drawable.post_5_thumb
)

/**
 * [Post] sample [post6].
 */
val post6 = Post(
    id = "55db18283ac0",
    title = "Redesigning the Android Studio Logo",
    subtitle = "A case study offering a comprehensive overview of the design journey of the Android Studio product logo.",
    url = "https://android-developers.googleblog.com/2023/05/redesigning-android-studio-logo.html",
    publication = publication,
    metadata = Metadata(
        author = androidstudioteam,
        date = "May 10",
        readTimeMinutes = 5
    ),
    paragraphs = paragraphsPost6,
    imageId = R.drawable.post_6,
    imageThumbId = R.drawable.post_6_thumb
)

/**
 * The global [PostsFeed] variable of dummy [Post] that this app uses.
 */
val posts: PostsFeed =
    PostsFeed(
        highlightedPost = post6,
        recommendedPosts = listOf(post1, post2, post3),
        popularPosts = listOf(
            post5,
            post1.copy(id = "post6"),
            post2.copy(id = "post7")
        ),
        recentPosts = listOf(
            post6,
            post3.copy(id = "post8"),
            post4.copy(id = "post9"),
            post5.copy(id = "post10")
        )
    )
