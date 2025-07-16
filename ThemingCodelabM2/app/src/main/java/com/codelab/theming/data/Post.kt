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

package com.codelab.theming.data

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Immutable
import com.codelab.theming.R

/**
 * Data type that holds the data needed to display a single "Post".
 *
 * @param id a [Long] number used to uniquely identify the [Post].
 * @param title The title of the [Post].
 * @param subtitle the subtitle of the [Post].
 * @param url a https url string to access the post.
 * @param metadata the [Metadata] for the [Post], contains `author`, `date` and `readTimeMinutes`
 * values.
 * @param imageId the resource ID of a drawable to draw as a header [Image] at its top when the
 * [Post] is displayed. It is used in the `FeaturedPost` Composable (file ui/start/Home.kt and
 * ui/finish/Home.kt).
 * @param imageThumbId the resource ID of a drawable to draw a thumbnail [Image] for the [Post]. It
 * is used in the `PostItem` Composable (file ui/start/Home.kt and ui/finish/Home.kt)
 * @param tags a [Set] of [String]'s describing the subject categories that the [Post] falls under.
 * It is used in the `PostMetadata` Composable (file ui/start/Home.kt and ui/finish/Home.kt)
 */
@Immutable
data class Post(
    val id: Long,
    val title: String,
    val subtitle: String? = null,
    val url: String,
    val metadata: Metadata,
    @param:DrawableRes val imageId: Int,
    @param:DrawableRes val imageThumbId: Int,
    val tags: Set<String>
)

/**
 * Contains some "trivia" about the [Post].
 *
 * @param author a [PostAuthor] instance naming the author of the [Post] in its [PostAuthor.name]
 * field, and a URL in its [PostAuthor.url] field that links to a page on "medium.com" which has
 * a list of all or the articles that author has posted to "medium.com" (not used by app).
 * @param date the date the [Post] was published.
 * @param readTimeMinutes an estimate of the number of minutes it will take to read the [Post].
 */
@Immutable
data class Metadata(
    val author: PostAuthor,
    val date: String,
    val readTimeMinutes: Int
)

/**
 * Contains the name of the author and a URL that links to a "medium.com" web page listing all of
 * the articles that the author has posted to "medium.com"
 *
 * @param name the name of the author.
 * @param url a URL that links to a "medium.com" web page listing all of the articles that the
 * author has posted to "medium.com"
 */
@Immutable
data class PostAuthor(
    val name: String,
    val url: String? = null
)

/**
 * A fake repo returning sample data
 */
object PostRepo {
    /**
     * Returns our [List] of fake [Post] objects field [posts]
     */
    fun getPosts(): List<Post> = posts

    /**
     * Returns a random [Post] from our [List] of fake [Post] objects field [posts]
     */
    fun getFeaturedPost(): Post = posts.random()
}

/**
 * Sample Data
 */

private val pietro = PostAuthor("Pietro Maggi", "https://medium.com/@pmaggi")
private val manuel = PostAuthor("Manuel Vivo", "https://medium.com/@manuelvicnt")
private val florina = PostAuthor("Florina Muntenescu", "https://medium.com/@florina.muntenescu")
private val jose = PostAuthor("Jose Alcérreca", "https://medium.com/@JoseAlcerreca")

private val post1 = Post(
    id = 1L,
    title = "A Little Thing about Android Module Paths",
    subtitle = "How to configure your module paths, instead of using Gradle’s default.",
    url = "https://medium.com/androiddevelopers/gradle-path-configuration-dc523f0ed25c",
    metadata = Metadata(
        author = pietro,
        date = "August 02",
        readTimeMinutes = 1
    ),
    imageId = R.drawable.post_1,
    imageThumbId = R.drawable.post_1_thumb,
    tags = setOf("Modularization", "Gradle")
)

private val post2 = Post(
    id = 2L,
    title = "Dagger in Kotlin: Gotchas and Optimizations",
    subtitle = "Use Dagger in Kotlin! This article includes best practices to optimize your build time and gotchas you might encounter.",
    url = "https://medium.com/androiddevelopers/dagger-in-kotlin-gotchas-and-optimizations-7446d8dfd7dc",
    metadata = Metadata(
        author = manuel,
        date = "July 30",
        readTimeMinutes = 3
    ),
    imageId = R.drawable.post_2,
    imageThumbId = R.drawable.post_2_thumb,
    tags = setOf("Dagger", "Kotlin")
)

private val post3 = Post(
    id = 3L,
    title = "From Java Programming Language to Kotlin — the idiomatic way",
    subtitle = "Learn how to get started converting Java Programming Language code to Kotlin, making it more idiomatic and avoid common pitfalls, by…",
    url = "https://medium.com/androiddevelopers/from-java-programming-language-to-kotlin-the-idiomatic-way-ac552dcc1741",
    metadata = Metadata(
        author = florina,
        date = "July 09",
        readTimeMinutes = 1
    ),
    imageId = R.drawable.post_3,
    imageThumbId = R.drawable.post_3_thumb,
    tags = setOf("Kotlin")
)

private val post4 = Post(
    id = 4L,
    title = "Locale changes and the AndroidViewModel antipattern",
    subtitle = "TL;DR: Expose resource IDs from ViewModels to avoid showing obsolete data.",
    url = "https://medium.com/androiddevelopers/locale-changes-and-the-androidviewmodel-antipattern-84eb677660d9",
    metadata = Metadata(
        author = jose,
        date = "April 02",
        readTimeMinutes = 1
    ),
    imageId = R.drawable.post_4,
    imageThumbId = R.drawable.post_4_thumb,
    tags = setOf("ViewModel", "Locale")
)

private val post5 = Post(
    id = 5L,
    title = "Collections and sequences in Kotlin",
    subtitle = "Working with collections is a common task and the Kotlin Standard Library offers many great utility functions. It also offers two ways of…",
    url = "https://medium.com/androiddevelopers/collections-and-sequences-in-kotlin-55db18283aca",
    metadata = Metadata(
        author = florina,
        date = "July 24",
        readTimeMinutes = 4
    ),
    imageId = R.drawable.post_5,
    imageThumbId = R.drawable.post_5_thumb,
    tags = setOf("Kotlin", "Collections", "Sequences")
)

private val posts = listOf(
    post1,
    post2,
    post3,
    post4,
    post5,
    post1.copy(id = 6L),
    post2.copy(id = 7L),
    post3.copy(id = 8L),
    post4.copy(id = 9L),
    post5.copy(id = 10L)
)
