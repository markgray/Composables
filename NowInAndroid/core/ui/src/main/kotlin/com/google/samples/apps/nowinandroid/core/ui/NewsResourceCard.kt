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

package com.google.samples.apps.nowinandroid.core.ui

import android.content.ClipData
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.samples.apps.nowinandroid.core.designsystem.R.drawable
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaIconToggleButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTopicTag
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * An expanded news resource card with a header image, byline, date, title, description, and topics.
 * [NewsResource] card used on the following screens: "For You", "Saved".
 *
 * Our root Composable is a [Card] whose arguments are:
 *  - `onClick`: is our [onClick] lambda parameter.
 *  - `shape`: is a [RoundedCornerShape] whose size is `16.dp`.
 *  - `colors`: is a [CardDefaults.cardColors] whose container color is the [ColorScheme.surface] of
 *  our custom [MaterialTheme.colorScheme].
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.semantics] in whose
 *  [SemanticsPropertyReceiver] `properties` lambda argument we add the property
 *  [SemanticsPropertyReceiver.onClick] whose `label` is our [String] variable `clickActionLabel`
 *  and whose `action` is `null`, chained to a [Modifier.testTag] whose `tag` argument is the
 *  [String] "newsResourceCard:${userNewsResource.id}".
 *
 * In the [ColumnScope] `content` composable lamba argument of the [Card] we compose a [Column] in
 * whose [ColumnScope] `content` composable lambda argument if the [UserNewsResource.headerImageUrl]
 * of our [UserNewsResource] parameter [userNewsResource] is not `null` we compose a [Row] in whose
 * [RowScope] `content` composable lambda argument we compose a [NewsResourceHeaderImage] whose
 * `headerImageUrl` argument is the [UserNewsResource.headerImageUrl] of our [UserNewsResource]
 * parameter [userNewsResource].
 *
 * In any case we then compose a [Box] whose `modifier` argument is a [Modifier.padding] that adds
 * `16.dp` to all sides, and in whose [BoxScope] `content` composable lambda argument we compose a
 * [Column] in whose [ColumnScope] `content` composable lambda argument we compose:
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` argument is `12.dp`.
 *
 * This is followed by a [Row] in whose [RowScope] `content` composable lambda argument we compose:
 *  - a [NewsResourceTitle] whose `newsResourceTitle` argument is the [UserNewsResource.title] of
 *  our [UserNewsResource] parameter [userNewsResource], and whose `modifier` argument is a
 *  [Modifier.fillMaxWidth] whose `fraction` argument is `.8f`, chained to a [Modifier.dragAndDropSource]
 *  in whose `transferData` lambda argument we return a [DragAndDropTransferData] whose `clipData`
 *  argument is a [ClipData.newPlainText] whose `label` argument is the string variable `sharingLabel`
 *  and whose `text` argument is the string variable `sharingContent`. The `flags` argument of the
 *  [DragAndDropTransferData] is our [Int] variable `dragAndDropFlags`.
 *  - a [Spacer] whose `modifier` argument is a [RowScope.weight] whose `weight` argument is `1f`.
 *  - a [BookmarkButton] whose `isBookmarked` argument is our [Boolean] parameter [isBookmarked],
 *  and whose `onClick` argument is our lambda parameter [onToggleBookmark].
 *
 * Next in the [Column] is a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height`
 * argument is `14.dp`, then comes a [Row] whose `verticalAlignment` argument is
 * [Alignment.CenterVertically] and in whose [RowScope] `content` composable lambda argument we compose:
 *  - if our [Boolean] parameter [hasBeenViewed] is `false` we compose a [NotificationDot] whose
 *  `color` argument is the [ColorScheme.tertiary] of our [MaterialTheme.colorScheme], and whose
 *  `modifier` argument is a [Modifier.size] whose `size` argument is `8.dp`, followed by a [Spacer]
 *  whose `modifier` argument is a [Modifier.size] whose `size` argument is `6.dp`.
 *  - a [NewsResourceMetaData] whose `publishDate` argument is the [UserNewsResource.publishDate] of
 *  our [UserNewsResource] parameter [userNewsResource], and whose `resourceType` argument is the
 *  [UserNewsResource.type] of our [UserNewsResource] parameter [userNewsResource].
 *
 * Next in the [Column] is a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height`
 * argument is `14.dp`, then comes a [NewsResourceShortDescription] whose `newsResourceShortDescription`
 * argument is the [UserNewsResource.content] of our [UserNewsResource] parameter [userNewsResource].
 * This is followed by a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height`
 * argument is `12.dp`, then comes a [NewsResourceTopics] whose `topics` argument is the
 * [UserNewsResource.followableTopics] of our [UserNewsResource] parameter [userNewsResource],
 * and whose `onTopicClick` argument is our lambda parameter [onTopicClick].
 *
 * @param userNewsResource The [UserNewsResource] to show.
 * @param isBookmarked Whether the resource is bookmarked.
 * @param hasBeenViewed Whether the resource has been viewed.
 * @param onToggleBookmark Callback for when the bookmark button is toggled.
 * @param onClick Callback for when the card is clicked.
 * @param onTopicClick Callback for when a topic is clicked.
 * @param modifier Modifier to be applied to the card.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsResourceCardExpanded(
    userNewsResource: UserNewsResource,
    isBookmarked: Boolean,
    hasBeenViewed: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    /**
     * Used as the custom label for accessibility services to communicate button's action to user
     * ("Open Resource Link") it is applied to the semantics of the root composable [Card].
     */
    val clickActionLabel: String = stringResource(id = R.string.core_ui_card_tap_action)

    /**
     * Used as the label of the [ClipData] used to share news resources by drag and drop of the
     * [NewsResourceTitle].
     */
    val sharingLabel: String = stringResource(id = R.string.core_ui_feed_sharing)

    /**
     * Used as the `text` of the [ClipData] used to share news resources by drag and drop of the
     * [NewsResourceTitle].
     */
    val sharingContent: String = stringResource(
        id = R.string.core_ui_feed_sharing_data,
        userNewsResource.title,
        userNewsResource.url,
    )

    /**
     * Used to enable global drag and drop of the [NewsResourceTitle] if the version of Android
     * running on the device is 24 ("N") or higher.
     */
    val dragAndDropFlags: Int = if (VERSION.SDK_INT >= VERSION_CODES.N) {
        View.DRAG_FLAG_GLOBAL
    } else {
        0
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        // Use custom label for accessibility services to communicate button's action to user.
        // Pass null for action to only override the label and not the actual action.
        modifier = modifier
            .semantics {
                onClick(label = clickActionLabel, action = null)
            }
            .testTag(tag = "newsResourceCard:${userNewsResource.id}"),
    ) {
        Column {
            if (!userNewsResource.headerImageUrl.isNullOrEmpty()) {
                Row {
                    NewsResourceHeaderImage(headerImageUrl = userNewsResource.headerImageUrl)
                }
            }
            Box(
                modifier = Modifier.padding(all = 16.dp),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(height = 12.dp))
                    Row {
                        NewsResourceTitle(
                            newsResourceTitle = userNewsResource.title,
                            modifier = Modifier
                                .fillMaxWidth(fraction = .8f)
                                .dragAndDropSource { _ ->
                                    DragAndDropTransferData(
                                        clipData = ClipData.newPlainText(
                                            sharingLabel,
                                            sharingContent,
                                        ),
                                        flags = dragAndDropFlags,
                                    )
                                },
                        )
                        Spacer(modifier = Modifier.weight(weight = 1f))
                        BookmarkButton(isBookmarked = isBookmarked, onClick = onToggleBookmark)
                    }
                    Spacer(modifier = Modifier.height(height = 14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!hasBeenViewed) {
                            NotificationDot(
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(size = 8.dp),
                            )
                            Spacer(modifier = Modifier.size(size = 6.dp))
                        }
                        NewsResourceMetaData(
                            publishDate = userNewsResource.publishDate,
                            resourceType = userNewsResource.type,
                        )
                    }
                    Spacer(modifier = Modifier.height(height = 14.dp))
                    NewsResourceShortDescription(newsResourceShortDescription = userNewsResource.content)
                    Spacer(modifier = Modifier.height(height = 12.dp))
                    NewsResourceTopics(
                        topics = userNewsResource.followableTopics,
                        onTopicClick = onTopicClick,
                    )
                }
            }
        }
    }
}

/**
 * Displays an image resource in a [Box].
 *
 * It uses the [AsyncImagePainter] returned by [rememberAsyncImagePainter] to load and display the
 * image specified by the [String] parameter [headerImageUrl]. While the image is loading, it
 * displays a [CircularProgressIndicator]. If the image fails to load, it displays a placeholder
 * image. If the Composable is being viewed in inspection mode, the placeholder image is displayed.
 *
 * The root composable is a [Box] whose `modifier` argument is a [Modifier.fillMaxWidth] chained to
 * a [Modifier.height] of `180.dp`, and whose `contentAlignment` argument is [Alignment.Center].
 *
 * The [BoxScope] `content` lambda argument of the [Box] is:
 *  - if our [MutableState] wrapped [Boolean] variable `isLoading` is `true` we compose a
 *  [CircularProgressIndicator] whose `modifier` argument is a [BoxScope.align] whose `alignment`
 *  argument is [Alignment.Center], chained to a [Modifier.size] of `80.dp`. The `color` argument is
 *  the [ColorScheme.tertiary] color of our custom [MaterialTheme.colorScheme].
 *  - an [Image] whose `modifier` argument is a [Modifier.fillMaxWidth] chained to a [Modifier.height]
 *  of `180.dp`, whose `contentScale` argument is [ContentScale.Crop]. Its `painter` argument is our
 *  [AsyncImagePainter] variable `imageLoader` if our [Boolean] variable `isError` is `false` and our
 *  [Boolean] variable `isLocalInspection` is `false`, otherwise it is the [painterResource] whose
 *  ID is `drawable.core_designsystem_ic_placeholder_default` (a stylized Android image). Its
 *  `contentDescription` argument is `null`.
 *
 * @param headerImageUrl The URL of the image to load.
 */
@Composable
fun NewsResourceHeaderImage(
    headerImageUrl: String?,
) {
    /**
     * `true` if the image is currently loading.
     */
    var isLoading: Boolean by remember { mutableStateOf(value = true) }

    /**
     * `true` if the image failed to load.
     */
    var isError: Boolean by remember { mutableStateOf(value = false) }

    /**
     * The [AsyncImagePainter] used to load the image.
     */
    val imageLoader: AsyncImagePainter = rememberAsyncImagePainter(
        model = headerImageUrl,
        onState = { state: AsyncImagePainter.State ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        },
    )

    /**
     * True if the composition is composed inside a Inspectable component.
     */
    val isLocalInspection: Boolean = LocalInspectionMode.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 180.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .size(size = 80.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 180.dp),
            contentScale = ContentScale.Crop,
            painter = if (isError.not() && !isLocalInspection) {
                imageLoader
            } else {
                painterResource(id = drawable.core_designsystem_ic_placeholder_default)
            },
            // TODO b/226661685: Investigate using alt text of image to populate content description
            // decorative image,
            contentDescription = null,
        )
    }
}

/**
 * Composable that displays the title of a news resource.
 *
 * It renders a [Text] whose `text` argument is our [String] parameter [newsResourceTitle], whose
 * [TextStyle] `style` argument is the [Typography.headlineSmall] of our custom
 * [MaterialTheme.typography], and whose `modifier` argument is our [Modifier] parameter [modifier].
 *
 * @param newsResourceTitle The title of the news resource.
 * @param modifier The [Modifier] to be applied to this composable.
 */
@Composable
fun NewsResourceTitle(
    newsResourceTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = newsResourceTitle,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier,
    )
}

/**
 * An icon button that can be used to bookmark an item.
 *
 * It is a [NiaIconToggleButton] whose `checked` argument is our [Boolean] parameter [isBookmarked],
 * whose `onCheckedChange` argument is a lambda that calls our lambda parameter [onClick]. Its
 * `modifier` argument is our [Modifier] parameter [modifier]. Its `icon` argument (the icon used
 * when `checked` is `false`) is an [Icon] whose `imageVector` is [NiaIcons.BookmarkBorder] and
 * whose `contentDescription` is the string "Bookmark". Its `checkedIcon` argument (the icon used
 * when `checked` is `true`) is an [Icon] whose `imageVector` is [NiaIcons.Bookmark] and
 * whose `contentDescription` is the string "Unbookmark".
 *
 * @param isBookmarked Whether the item is bookmarked.
 * @param onClick Called when the user clicks the button.
 * @param modifier Modifier to be applied to the button.
 */
@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NiaIconToggleButton(
        checked = isBookmarked,
        onCheckedChange = { onClick() },
        modifier = modifier,
        icon = {
            Icon(
                imageVector = NiaIcons.BookmarkBorder,
                contentDescription = stringResource(id = R.string.core_ui_bookmark),
            )
        },
        checkedIcon = {
            Icon(
                imageVector = NiaIcons.Bookmark,
                contentDescription = stringResource(id = R.string.core_ui_unbookmark),
            )
        },
    )
}

/**
 * A composable that draws a simple circle of a specified [Color].
 *
 * It is used by [NewsResourceCardExpanded] to indicate that a news resource has not been viewed
 * yet. Our root composable is a [Canvas] whose `modifier` argument is our [Modifier] parameter
 * [modifier] chained to a [Modifier.semantics] whose `properties` lambda argument sets the
 * [SemanticsPropertyReceiver.contentDescription] to the [String] "Unread". In the [DrawScope]
 * `onDraw` lambda argument of the [Canvas] we call the [DrawScope.drawCircle] method with its
 * `color` argument our [Color] parameter [color] and its `radius` the `minDimension` of the
 * [Canvas] divided by 2.
 *
 * @param color the [Color] to draw the circle with.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior.
 */
@Composable
fun NotificationDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val description: String =
        stringResource(id = R.string.core_ui_unread_resource_dot_content_description)
    Canvas(
        modifier = modifier
            .semantics { contentDescription = description },
        onDraw = {
            drawCircle(
                color = color,
                radius = size.minDimension / 2,
            )
        },
    )
}

/**
 * Formats the [Instant] parameter [publishDate] to a [String] in the format of [FormatStyle.MEDIUM]
 * using the default [Locale] and the current time zone.
 *
 * It creates a [DateTimeFormatter] by calling the [DateTimeFormatter.ofLocalizedDate] method with
 * [FormatStyle.MEDIUM] as its `dateStyle` argument, then chains a call to the
 * [DateTimeFormatter.withLocale] method with the value returned by [Locale.getDefault] for its
 * `locale` argument, then chains a call to the [DateTimeFormatter.withZone] method with the
 * [java.time.ZoneId] returned by converting the [kotlinx.datetime.TimeZone] returned by the
 * `current` [LocalTimeZone] to a [java.time.ZoneId] by calling its
 * [kotlinx.datetime.TimeZone.toJavaZoneId] method, then it calls the [DateTimeFormatter.format]
 * method with the [java.time.Instant] returned by converting our [Instant] parameter [publishDate]
 * to a [java.time.Instant] by calling its [Instant.toJavaInstant] method. The [String] returned by
 * the `format` method is returned by us.
 *
 * @param publishDate the [Instant] to be formatted.
 * @return a [String] representation of our [Instant] parameter [publishDate] formatted using
 * [FormatStyle.MEDIUM] for the current time zone using the default [Locale].
 */
@Composable
fun dateFormatted(publishDate: Instant): String = DateTimeFormatter
    .ofLocalizedDate(FormatStyle.MEDIUM)
    .withLocale(Locale.getDefault())
    .withZone(LocalTimeZone.current.toJavaZoneId())
    .format(publishDate.toJavaInstant())

/**
 * A composable that displays the publish date and type of a news resource.
 *
 * It uses the [dateFormatted] method to format our [Instant] parameter [publishDate] to produce
 * a [String] variable `val formattedDate`. This is then used in a [Text] composable as either the
 * entire `text` argument if our [String] parameter [resourceType] is blank, or as the first
 * formatting argument to the string whose resource ID is `R.string.core_ui_card_meta_data_text`
 * ("%1$s · %2$s") with our [String] parameter [resourceType] as the second argument if it is not
 * blank. The `style` argument of the [Text] is the [Typography.labelSmall] of our custom
 * [MaterialTheme.typography].
 *
 * @param publishDate the [Instant] to be displayed.
 * @param resourceType the type of the resource to be displayed. If it is blank only the
 * [publishDate] will be displayed.
 */
@Composable
fun NewsResourceMetaData(
    publishDate: Instant,
    resourceType: String,
) {
    val formattedDate: String = dateFormatted(publishDate = publishDate)
    Text(
        text = if (resourceType.isNotBlank()) {
            stringResource(R.string.core_ui_card_meta_data_text, formattedDate, resourceType)
        } else {
            formattedDate
        },
        style = MaterialTheme.typography.labelSmall,
    )
}

/**
 * Composable that displays the short description of a news resource.
 *
 * It renders a [Text] whose `text` argument is our [String] parameter [newsResourceShortDescription],
 * and whose [TextStyle] `style` argument is the [Typography.bodyLarge] of our custom
 * [MaterialTheme.typography].
 *
 * @param newsResourceShortDescription the short description to display.
 */
@Composable
fun NewsResourceShortDescription(
    newsResourceShortDescription: String,
) {
    Text(text = newsResourceShortDescription, style = MaterialTheme.typography.bodyLarge)
}

/**
 * Displays a list of topics associated with a news resource.
 *
 * Our root composable is a [Row] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.horizontalScroll] whose `state` argument is a [rememberScrollState], and
 * the `horizontalArrangement` argument of the [Row] is [Arrangement.spacedBy] whose `space`
 * argument is `4.dp`. In the [RowScope] `content` composable lambda argument of the [Row] we
 * loop through our [List] of [FollowableTopic] parameter [topics] using variable `followableTopic`
 * for each [FollowableTopic]. We then compose a [NiaTopicTag] whose arguments are:
 *  - `followed`: is the [Boolean] property [FollowableTopic.isFollowed] of `followableTopic`.
 *  - `onClick`: is a lambda that calls our lambda parameter [onTopicClick] with the [String]
 *  property [Topic.id] of the [FollowableTopic.topic] property of `followableTopic`.
 *  - `text`: is a lambda that initializes its [String] variable `val contentDescription` to the
 *  [String] "... is followed" if the [Boolean] property [FollowableTopic.isFollowed] of
 *  `followableTopic` is `true`, or the [String] "... is not followed" if the [Boolean] property
 *  [FollowableTopic.isFollowed] of `followableTopic` is `false`. It then composes a [Text] whose
 *  `text` argument is the uppercase [String] property [Topic.name] of the [FollowableTopic.topic],
 *  and whose `modifier` argument is a [Modifier.semantics] whose `contentDescription` argument
 *  is our [String] variable `contentDescription`, and to this is chained a [Modifier.testTag] whose
 *  `tag` argument is the [String] "topicTag:${FollowableTopic.topic.id}".
 *
 * @param topics the [List] of [FollowableTopic] to display.
 * @param onTopicClick called when a topic is clicked.
 * @param modifier the [Modifier] to be applied to the root composable.
 */
@Composable
fun NewsResourceTopics(
    topics: List<FollowableTopic>,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        // causes narrow chips
        modifier = modifier.horizontalScroll(state = rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
    ) {
        for (followableTopic: FollowableTopic in topics) {
            NiaTopicTag(
                followed = followableTopic.isFollowed,
                onClick = { onTopicClick(followableTopic.topic.id) },
                text = {
                    val contentDescription: String = if (followableTopic.isFollowed) {
                        stringResource(
                            R.string.core_ui_topic_chip_content_description_when_followed,
                            followableTopic.topic.name,
                        )
                    } else {
                        stringResource(
                            R.string.core_ui_topic_chip_content_description_when_not_followed,
                            followableTopic.topic.name,
                        )
                    }
                    Text(
                        text = followableTopic.topic.name.uppercase(Locale.getDefault()),
                        modifier = Modifier
                            .semantics {
                                this.contentDescription = contentDescription
                            }
                            .testTag(tag = "topicTag:${followableTopic.topic.id}"),
                    )
                },
            )
        }
    }
}

/**
 * This is a Composable function that is used to preview the [BookmarkButton] Composable. It renders
 * a [NiaTheme] wrapped [Surface] which contains a [BookmarkButton] whose `isBookmarked` argument
 * is `false` and whose `onClick` argument is a do nothing lambda. The `@Preview` annotation names
 * this preview "Bookmark Button".
 */
@Preview(name = "Bookmark Button")
@Composable
private fun BookmarkButtonPreview() {
    NiaTheme {
        Surface {
            BookmarkButton(isBookmarked = false, onClick = { })
        }
    }
}

/**
 * This is a Composable function that is used to preview the [BookmarkButton] Composable. It renders
 * a [NiaTheme] wrapped [Surface] which contains a [BookmarkButton] whose `isBookmarked` argument
 * is `true` and whose `onClick` argument is a do nothing lambda. The `@Preview` annotation names
 * this preview "Bookmark Button Bookmarked".
 */
@Preview(name = "Bookmark Button Bookmarked")
@Composable
private fun BookmarkButtonBookmarkedPreview() {
    NiaTheme {
        Surface {
            BookmarkButton(isBookmarked = true, onClick = { })
        }
    }
}

/**
 * Preview function for the [NewsResourceCardExpanded] composable. It renders the first of the
 * [List] of [UserNewsResource] objects provided by our [UserNewsResourcePreviewParameterProvider]
 * parameter [userNewsResources].
 *
 * It provides a [LocalInspectionMode] with the value `true` to its children.
 *
 * Then in a [NiaTheme] wrapped [Surface] it composes a [NewsResourceCardExpanded] whose
 * `userNewsResource` argument is the [UserNewsResource] at index 0 of our [List] of
 * [UserNewsResource] parameter [userNewsResources], whose `isBookmarked` argument is `true`,
 * whose `hasBeenViewed` argument is `false`, and whose `onToggleBookmark`, `onClick` and
 * `onTopicClick` arguments are do nothing lambdas.
 *
 * @param userNewsResources a [List] of [UserNewsResource] objects provided by our
 * [UserNewsResourcePreviewParameterProvider].
 */
@Preview(name = "NewsResourceCardExpanded")
@Composable
private fun ExpandedNewsResourcePreview(
    @PreviewParameter(provider = UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    CompositionLocalProvider(
        value = LocalInspectionMode provides true,
    ) {
        NiaTheme {
            Surface {
                NewsResourceCardExpanded(
                    userNewsResource = userNewsResources[0],
                    isBookmarked = true,
                    hasBeenViewed = false,
                    onToggleBookmark = {},
                    onClick = {},
                    onTopicClick = {},
                )
            }
        }
    }
}
