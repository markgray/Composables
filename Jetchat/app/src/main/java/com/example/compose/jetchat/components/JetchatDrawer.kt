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

package com.example.compose.jetchat.components

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose.jetchat.NavActivity
import com.example.compose.jetchat.R
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.data.meProfile
import com.example.compose.jetchat.profile.ProfileFragment
import com.example.compose.jetchat.profile.ProfileScreenState
import com.example.compose.jetchat.theme.JetchatTheme

/**
 * This is used by [JetchatDrawer] as the `content` of the [ModalDrawerSheet] that is used as the
 * the Content inside of the [ModalNavigationDrawer] modal navigation drawer (wheels within wheels!)
 * Our root Composable is a [Column] whose `modifier` argument is a [Modifier.fillMaxSize] to have
 * it occupy its entire incoming size constraints, with a [Modifier.background] that sets its
 * background color to the [ColorScheme.background] color of our [MaterialTheme.colorScheme]. The
 * content of the [Column] is a [Spacer] whose `modifier` argument is a [Modifier.windowInsetsTopHeight]
 * which sets the height to that of the insets at the top of the screen with its `insets` argument
 * [WindowInsets.Companion.statusBars] (this pushes the drawer content below the status bar). This
 * is followed by a [DrawerHeader] which displays in a [Row] the  [JetchatIcon] and an [Image] that
 * draws the `jetchat` logo in resource ID [R.drawable.jetchat_logo]. After this is a [DividerItem]
 * Composable which draws a [Divider] whose `color` is a copy of the [ColorScheme.onSurface] with
 * its alpha set to 0.12f. This is followed by a [DrawerItemHeader] which displays the [Text] "Chats",
 * and this is followed by a [ChatItem] displaying the [Text] "composers" and a [ChatItem] displaying
 * the [Text] "droidcon-nyc". The `onChatClicked` lambda argumet of both [ChatItem]'s is a lambda
 * that calls our [onChatClicked] parameter with the text that they are displaying. This is followed
 * another [DividerItem] whose `modifier` argument is a [Modifier.padding] that sets the padding
 * on each end of the [DividerItem] to 28.dp. This is followed by a [DrawerItemHeader] which displays
 * the [Text] "Recent Profiles". This is followed by a [ProfileItem] displaying the `text`
 * "Ali Conors (you)", the `profilePic` [ProfileScreenState.photo] resource ID of [meProfile], with
 * its `onProfileClicked` argument a lambda which calls our [onProfileClicked] parameter with the
 * [ProfileScreenState.userId] of [meProfile]. This is followed by a [ProfileItem] displaying the
 * `text` "Taylor Brooks", the `profilePic` [ProfileScreenState.photo] resource ID of
 * [colleagueProfile] with its `onProfileClicked` argument a lambda which calls our [onProfileClicked]
 * parameter with the [ProfileScreenState.userId] of [colleagueProfile].
 *
 * @param onProfileClicked lambda to be called with the [ProfileScreenState.userId] of the profile
 * depicted when a [ProfileItem] is clicked. In the `onCreate` override of [NavActivity] this is a
 * lambda which creates a [Bundle] of the [String]'s "userId" to the [String] the lambda is called
 * with then calls the [NavController.navigate] method with the resource ID [R.id.nav_profile]
 * (the [ProfileFragment] destination in the navigation xml file) and the [Bundle] created (this
 * displays the information about [ProfileScreenState] of the [ProfileItem] clicked either [meProfile]
 * or [colleagueProfile]).
 * @param onChatClicked lambda to be called with the name of the chat when a [ChatItem] is clicked.
 * In the `onCreate` override of [NavActivity] this is a lambda which pop's the BackStack back to
 * [R.id.nav_home] (the "#composers" main screen) and closes the drawer (ie. does nothing).
 */
@Composable
fun JetchatDrawerContent(
    onProfileClicked: (String) -> Unit,
    onChatClicked: (String) -> Unit
) {
    // Use windowInsetsTopHeight() to add a spacer which pushes the drawer content
    // below the status bar (y-axis)
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(insets = WindowInsets.statusBars))
        DrawerHeader()
        DividerItem()
        DrawerItemHeader(text = "Chats")
        ChatItem(text = "composers", selected = true) { onChatClicked("composers") }
        ChatItem(text = "droidcon-nyc", selected = false) { onChatClicked("droidcon-nyc") }
        DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
        DrawerItemHeader(text = "Recent Profiles")
        ProfileItem(text = "Ali Conors (you)", profilePic = meProfile.photo) {
            onProfileClicked(meProfile.userId)
        }
        ProfileItem(text = "Taylor Brooks", profilePic = colleagueProfile.photo) {
            onProfileClicked(colleagueProfile.userId)
        }
    }
}

/**
 *
 */
@Composable
private fun DrawerHeader() {
    Row(modifier = Modifier.padding(all = 16.dp), verticalAlignment = CenterVertically) {
        JetchatIcon(
            contentDescription = null,
            modifier = Modifier.size(size = 24.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.jetchat_logo),
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
@Composable
private fun DrawerItemHeader(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = CenterStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ChatItem(text: String, selected: Boolean, onChatClicked: () -> Unit) {
    val background = if (selected) {
        Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(height = 56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(shape = CircleShape)
            .then(other = background)
            .clickable(onClick = onChatClicked),
        verticalAlignment = CenterVertically
    ) {
        val iconTint = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_jetchat),
            tint = iconTint,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentDescription = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun ProfileItem(text: String, @DrawableRes profilePic: Int?, onProfileClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .height(height = 56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(shape = CircleShape)
            .clickable(onClick = onProfileClicked),
        verticalAlignment = CenterVertically
    ) {
        val paddingSizeModifier = Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            .size(size = 24.dp)
        if (profilePic != null) {
            Image(
                painter = painterResource(id = profilePic),
                modifier = paddingSizeModifier.then(Modifier.clip(shape = CircleShape)),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        } else {
            Spacer(modifier = paddingSizeModifier)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

/**
 *
 */
@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

/**
 *
 */
@Composable
@Preview
fun DrawerPreview() {
    JetchatTheme {
        Surface {
            Column {
                JetchatDrawerContent({}, {})
            }
        }
    }
}
/**
 *
 */
@Composable
@Preview
fun DrawerPreviewDark() {
    JetchatTheme(isDarkTheme = true) {
        Surface {
            Column {
                JetchatDrawerContent({}, {})
            }
        }
    }
}
