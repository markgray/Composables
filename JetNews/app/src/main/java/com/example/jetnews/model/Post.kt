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

package com.example.jetnews.model

import androidx.annotation.DrawableRes

/**
 * TODO: Add kdoc
 */
data class Post(
    /**
     * TODO: Add kdoc
     */
    val id: String,
    /**
     * TODO: Add kdoc
     */
    val title: String,
    /**
     * TODO: Add kdoc
     */
    val subtitle: String? = null,
    /**
     * TODO: Add kdoc
     */
    val url: String,
    /**
     * TODO: Add kdoc
     */
    val publication: Publication? = null,
    /**
     * TODO: Add kdoc
     */
    val metadata: Metadata,
    /**
     * TODO: Add kdoc
     */
    val paragraphs: List<Paragraph> = emptyList(),
    /**
     * TODO: Add kdoc
     */
    @DrawableRes val imageId: Int,
    /**
     * TODO: Add kdoc
     */
    @DrawableRes val imageThumbId: Int
)

/**
 * TODO: Add kdoc
 */
data class Metadata(
    /**
     * TODO: Add kdoc
     */
    val author: PostAuthor,
    /**
     * TODO: Add kdoc
     */
    val date: String,
    /**
     * TODO: Add kdoc
     */
    val readTimeMinutes: Int
)

/**
 * TODO: Add kdoc
 */
data class PostAuthor(
    /**
     * TODO: Add kdoc
     */
    val name: String,
    /**
     * TODO: Add kdoc
     */
    val url: String? = null
)

/**
 * TODO: Add kdoc
 */
data class Publication(
    /**
     * TODO: Add kdoc
     */
    val name: String,
    /**
     * TODO: Add kdoc
     */
    val logoUrl: String
)

/**
 * TODO: Add kdoc
 */
data class Paragraph(
    /**
     * TODO: Add kdoc
     */
    val type: ParagraphType,
    /**
     * TODO: Add kdoc
     */
    val text: String,
    /**
     * TODO: Add kdoc
     */
    val markups: List<Markup> = emptyList()
)

/**
 * TODO: Add kdoc
 */
data class Markup(
    /**
     * TODO: Add kdoc
     */
    val type: MarkupType,
    /**
     * TODO: Add kdoc
     */
    val start: Int,
    /**
     * TODO: Add kdoc
     */
    val end: Int,
    /**
     * TODO: Add kdoc
     */
    val href: String? = null
)

/**
 * TODO: Add kdoc
 */
enum class MarkupType {
    /**
     * TODO: Add kdoc
     */
    Link,
    /**
     * TODO: Add kdoc
     */
    Code,
    /**
     * TODO: Add kdoc
     */
    Italic,
    /**
     * TODO: Add kdoc
     */
    Bold,
}

/**
 * TODO: Add kdoc
 */
enum class ParagraphType {
    /**
     * TODO: Add kdoc
     */
    Title,
    /**
     * TODO: Add kdoc
     */
    Caption,
    /**
     * TODO: Add kdoc
     */
    Header,
    /**
     * TODO: Add kdoc
     */
    Subhead,
    /**
     * TODO: Add kdoc
     */
    Text,
    /**
     * TODO: Add kdoc
     */
    CodeBlock,
    /**
     * TODO: Add kdoc
     */
    Quote,
    /**
     * TODO: Add kdoc
     */
    Bullet,
}
