/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UNUSED_VARIABLE", "unused")

package com.example.composemail.ui.newmail

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import com.example.composemail.ui.components.OneLineText

/**
 * A preview for the new mail motion layout. It provides buttons to transition between the
 * different states of the layout: [NewMailLayoutState.Fab], [NewMailLayoutState.Full], and
 * [NewMailLayoutState.Mini].
 *
 * This allows for easy testing and visualization of the animations and constraint sets defined for
 * the motion layout.
 */
@Preview
@Composable
fun NewMotionMessagePreview() {
    /**
     * The [NewMailState] is used to track the current state of the layout. It is used to
     * determine which constraint set to use for the motion layout of our [NewMailButton].
     */
    val newMailState: NewMailState =
        rememberNewMailState(initialLayoutState = NewMailLayoutState.Full)
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(space = 10.dp)) {
            Button(onClick = newMailState::setToFab) {
                Text(text = "Fab")
            }
            Button(onClick = newMailState::setToFull) {
                Text(text = "Full")
            }
            Button(onClick = newMailState::setToMini) {
                Text(text = "Mini")
            }
        }
        NewMailButton(
            modifier = Modifier.fillMaxSize(),
            state = newMailState
        )
    }
}

/**
 * Creates a [MotionScene] for the new mail layout, defining the constraints for the different
 * states ([NewMailLayoutState.Fab], [NewMailLayoutState.Full], and [NewMailLayoutState.Mini]) and
 * the transitions between them.
 *
 * This function uses a JSON-like string format to define the `ConstraintSets` for each state
 * and a default `Transition`. The constraints define the size, position, and custom properties
 * (like background color) for each composable within the [MotionLayout].
 *
 * The colors are dynamically retrieved from the current [MaterialTheme] to ensure the UI is
 * consistent with the app's theme.
 *
 * @param initialState The starting state of the motion layout. The default transition will be
 * configured to animate from this state to the next logical state.
 * @return A [MotionScene] instance configured for the new mail UI.
 */
@OptIn(ExperimentalMotionApi::class)
@Composable
private fun messageMotionScene(initialState: NewMailLayoutState): MotionScene {
    val startStateName: String = remember { initialState.name }
    val endStateName: String = remember {
        when (initialState) {
            NewMailLayoutState.Fab -> NewMailLayoutState.Full
            NewMailLayoutState.Mini -> NewMailLayoutState.Fab
            NewMailLayoutState.Full -> NewMailLayoutState.Fab
        }.name
    }
    val primary: String = MaterialTheme.colors.primary.toHexString()
    val primaryVariant: String = MaterialTheme.colors.primaryVariant.toHexString()
    val onPrimary: String = MaterialTheme.colors.onPrimary.toHexString()
    val secondary: String = MaterialTheme.colors.secondary.toHexString()
    val onSecondary: String = MaterialTheme.colors.onSecondary.toHexString()
    val surface: String = MaterialTheme.colors.surface.toHexString()
    val onSurface: String = MaterialTheme.colors.onSurface.toHexString()

    return MotionScene(
        content =
            """
        {
          ConstraintSets: {
            ${NewMailLayoutState.Fab.name}: {
              box: {
                width: 50, height: 50,
                end: ['parent', 'end', 12],
                bottom: ['parent', 'bottom', 12],
                custom: {
                  background: '#$secondary'
                }
              },
              minIcon: {
                width: 40, height: 40,
                end: ['editClose', 'start', 8],
                top: ['editClose', 'top', 0],
                visibility: 'gone',
                custom: {
                  content: '#$onPrimary'
                }
              },
              editClose: {
                width: 40, height: 40,
                centerHorizontally: 'box',
                centerVertically: 'box',
                custom: {
                  content: '#$onSecondary'
                }
              },
              title: {
                width: 'spread',
                top: ['box', 'top', 0],
                bottom: ['editClose', 'bottom', 0],
                start: ['box', 'start', 8],
                end: ['minIcon', 'start', 8],
                custom: {
                  content: '#$onPrimary'
                }
                
                visibility: 'gone'
              },
              content: {
                width: 'spread', height: 'spread',
                start: ['box', 'start', 8],
                end: ['box', 'end', 8],
                
                top: ['editClose', 'bottom', 8],
                bottom: ['box', 'bottom', 8],
                
                visibility: 'gone'
              }
            },
            ${NewMailLayoutState.Full.name}: {
              box: {
                width: 'spread', height: 'spread',
                start: ['parent', 'start', 12],
                end: ['parent', 'end', 12],
                bottom: ['parent', 'bottom', 12],
                top: ['parent', 'top', 40],
                custom: {
                  background: '#$surface'
                }
              },
              minIcon: {
                width: 40, height: 40,
                end: ['editClose', 'start', 8],
                top: ['editClose', 'top', 0],
                custom: {
                  content: '#$onSurface'
                }
              },
              editClose: {
                width: 40, height: 40,
                end: ['box', 'end', 4],
                top: ['box', 'top', 4],
                custom: {
                  content: '#$onSurface'
                }
              },
              title: {
                width: 'spread',
                top: ['box', 'top', 0],
                bottom: ['editClose', 'bottom', 0],
                start: ['box', 'start', 8],
                end: ['minIcon', 'start', 8],
                custom: {
                  content: '#$onSurface'
                }
              },
              content: {
                width: 'spread', height: 'spread',
                start: ['box', 'start', 8],
                end: ['box', 'end', 8],
                
                top: ['editClose', 'bottom', 8],
                bottom: ['box', 'bottom', 8]
              }
            },
            ${NewMailLayoutState.Mini.name}: {
              box: {
                width: 180, height: 50,
                bottom: ['parent', 'bottom', 12],
                end: ['parent', 'end', 12],
                custom: {
                  background: '#$primary'
                }
              },
              minIcon: {
                width: 40, height: 40,
                end: ['editClose', 'start', 8],
                top: ['editClose', 'top', 0],
                rotationZ: 180,
                custom: {
                  content: '#$onPrimary'
                }
              },
              editClose: {
                width: 40, height: 40,
                end: ['box', 'end', 4],
                top: ['box', 'top', 4],
                custom: {
                  content: '#$onPrimary'
                }
              },
              title: {
                width: 'spread',
                top: ['box', 'top', 0],
                bottom: ['editClose', 'bottom', 0],
                start: ['box', 'start', 8],
                end: ['minIcon', 'start', 8],
                custom: {
                  content: '#$onPrimary'
                }
              },
              content: {
                width: 'spread', height: 'spread',
                start: ['box', 'start', 8],
                end: ['box', 'end', 8],
                
                top: ['editClose', 'bottom', 8],
                bottom: ['box', 'bottom', 8],
                
                visibility: 'gone'
              }
            }
          },
          Transitions: {
            default: {
              from: '$startStateName',
              to: '$endStateName'
            }
          }
        }
    """.trimIndent()
    )
}

/**
 * A composable that defines the content of the new mail dialog within a [MotionLayoutScope]. It
 * arranges the UI elements like the title, icons, and the message composition area.
 *
 * The appearance and behavior of the elements change based on the current [NewMailLayoutState]
 * provided by the [state] parameter. For example, the "close" icon changes to an "edit" icon when
 * in the FAB state, and the dialog's title changes from "Message" to "Draft".
 *
 * The layout of these elements is controlled by the parent [MotionLayout] using the `layoutId`
 * modifier. Custom properties like color are also driven by the [MotionLayout]'s `ConstraintSet`s
 * via the [MotionLayoutScope.customColor] function.
 *
 * @param state The [NewMailState] that holds the current layout state and provides callbacks
 * to transition to other states.
 */
@OptIn(ExperimentalMotionApi::class)
@Composable
internal fun MotionLayoutScope.MotionMessageContent(
    state: NewMailState
) {
    val currentState: NewMailLayoutState = state.currentState
    val focusManager: FocusManager = LocalFocusManager.current
    val dialogName: String = remember(key1 = currentState) {
        when (currentState) {
            NewMailLayoutState.Mini -> "Draft"
            else -> "Message"
        }
    }
    Surface(
        modifier = Modifier.layoutId(layoutId = "box"),
        color = customColor(id = "box", name = "background"),
        elevation = 4.dp,
        shape = RoundedCornerShape(size = 8.dp)
    ) {}
    val iconColor: Color = customColor(id = "editClose", name = "content")
    Row(modifier = Modifier.layoutId(layoutId = "editClose")) {
        when (currentState) {
            NewMailLayoutState.Fab -> {
                ColorableIconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(weight = 1f, fill = true),
                    imageVector = Icons.Default.Edit,
                    color = iconColor,
                    enabled = true
                ) {
                    state.setToFull()
                }
            }

            else -> {
                ColorableIconButton(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.Close,
                    color = iconColor,
                    enabled = true
                ) {
                    state.setToFab()
                }
            }
        }
    }
    ColorableIconButton(
        modifier = Modifier.layoutId(layoutId = "minIcon"),
        imageVector = Icons.Default.KeyboardArrowDown,
        color = customColor(id = "minIcon", name = "content"),
        enabled = true
    ) {
        when (currentState) {
            NewMailLayoutState.Full -> state.setToMini()
            else -> state.setToFull()
        }
    }
    OneLineText(
        text = dialogName,
        modifier = Modifier.layoutId(layoutId = "title"),
        color = customColor(id = "title", name = "content"),
        style = MaterialTheme.typography.h6
    )
    MessageWidget(
        modifier = Modifier.layoutId(layoutId = "content"),
        onDelete = {
            focusManager.clearFocus()
            state.setToFab()
        }
    )
//            MessageWidgetCol(
//                modifier = Modifier
//                    .layoutId("content")
//                    .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
//            )
}

/**
 * A composable that acts as a container for the new mail UI, using a [MotionLayout] to animate
 * between different states. The appearance and behavior of this button are determined by the
 * provided [NewMailState].
 *
 * The layout can transition between a floating action button ([NewMailLayoutState.Fab]), a
 * full-screen dialog ([NewMailLayoutState.Full]), and a minimized view
 * ([NewMailLayoutState.Mini]).
 *
 * The animations and constraints for these states are defined in the [MotionScene] returned by
 * [messageMotionScene]. The content of the layout is provided by [MotionMessageContent].
 *
 * @param modifier The [Modifier] to be applied to the [MotionLayout].
 * @param state The [NewMailState] that controls the current layout state and transitions.
 */
@OptIn(ExperimentalMotionApi::class)
@Composable
fun NewMailButton(
    modifier: Modifier = Modifier,
    state: NewMailState
) {
    val currentStateName: String = state.currentState.name
    MotionLayout(
        motionScene = messageMotionScene(initialState = state.currentState),
        animationSpec = tween(durationMillis = 700),
        constraintSetName = currentStateName,
        modifier = modifier
    ) {
        MotionMessageContent(state = state)
    }
}

/**
 * A composable that displays an icon button with a customizable tint color.
 *
 * This button is built on top of [Surface] to allow for a transparent background and a
 * specific `contentColor` to be applied to the [Icon].
 *
 * @param modifier The [Modifier] to be applied to this icon button.
 * @param imageVector The [ImageVector] to be displayed inside the button.
 * @param color The tint color to be applied to the [imageVector].
 * @param enabled Controls the enabled state of the button. When `false`, this button will not
 * be clickable.
 * @param onClick The lambda to be executed when this button is clicked.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ColorableIconButton(
    modifier: Modifier,
    imageVector: ImageVector,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        contentColor = color,
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * A composable that displays the UI for composing a new email message, arranged in a vertical
 * [Column].
 *
 * This includes [TextField]s for recipients, subject, and the message body, along with "Send"
 * and "Delete" buttons at the bottom. This is an alternative implementation to [MessageWidget]
 * and is not currently used in the motion layout.
 *
 * @param modifier The [Modifier] to be applied to the layout.
 */
@Composable
internal fun MessageWidgetCol(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = {
                Text(text = "Recipients")
            }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = {
                Text(text = "Subject")
            }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 2.0f, fill = true),
            value = "",
            onValueChange = {},
            placeholder = {
                Text(text = "Message")
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(onClick = { /* Send Mail */ }) {
                Row {
                    Text(text = "Send")
                    Spacer(modifier = Modifier.width(width = 8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Mail",
                    )
                }
            }
            Button(onClick = { /* Delete Draft */ }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Draft",
                )
            }
        }
    }
}

/**
 * A preview composable for the [MessageWidget].
 *
 * This preview displays the message composition interface, which includes fields for "To",
 * "Subject", and "Message", along with "Send" and "Delete" buttons. It uses a [ConstraintLayout]
 * to arrange these elements, filling the maximum available size to demonstrate its responsiveness.
 */
@Preview
@Composable
private fun MessageWidgetPreview() {
    MessageWidget(modifier = Modifier.fillMaxSize())
}

/**
 * A composable that displays the UI for composing a new email message, using a [ConstraintLayout]
 * to arrange the elements.
 *
 * This widget includes [OutlinedTextField]s for the recipient ("To"), subject, and message body.
 * It also contains "Send" and "Delete" buttons at the bottom. The layout is defined using a
 * JSON-based [ConstraintSet], which positions the elements relative to each other and the parent.
 *
 * A horizontal guideline (`gl1`) is used to separate the main message area from the bottom action
 * buttons.
 *
 * @param modifier The [Modifier] to be applied to the [ConstraintLayout].
 * @param onDelete A lambda to be executed when the "Delete" button is clicked. This is also
 * temporarily used for the "Send" button.
 */
@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun MessageWidget(
    modifier: Modifier,
    noinline onDelete: () -> Unit = {}
) {
    val constraintSet = remember {
        ConstraintSet(
            """
                {
                    gl1: { type: 'hGuideline', end: 50 },
                    recipient: {
                      top: ['parent', 'top', 2],
                      width: 'spread',
                      centerHorizontally: 'parent',
                    },
                    subject: { 
                      top: ['recipient', 'bottom', 8],
                      width: 'spread',
                      centerHorizontally: 'parent',
                    },
                    message: {
                      height: 'spread',
                      width: 'spread',
                      centerHorizontally: 'parent',
                      top: ['subject', 'bottom', 8],
                      bottom: ['gl1', 'bottom', 4],
                    },
                    delete: {
                      height: 'spread',
                      top: ['gl1', 'bottom', 0],
                      bottom: ['parent', 'bottom', 4],
                      start: ['parent', 'start', 0]
                    },
                    send: {
                      height: 'spread',
                      top: ['gl1', 'bottom', 0],
                      bottom: ['parent', 'bottom', 4],
                      end: ['parent', 'end', 0]
                    }
                }
            """.trimIndent()
        )
    }
    ConstraintLayout(
        modifier = modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
        constraintSet = constraintSet
    ) {
        OutlinedTextField(
            modifier = Modifier.layoutId(layoutId = "recipient"),
            value = "",
            onValueChange = {},
            label = {
                OneLineText(text = "To")
            }
        )
        OutlinedTextField(
            modifier = Modifier.layoutId(layoutId = "subject"),
            value = "",
            onValueChange = {},
            label = {
                OneLineText(text = "Subject")
            }
        )
        OutlinedTextField(
            modifier = Modifier
                .layoutId(layoutId = "message")
                .fillMaxHeight(),
            value = "",
            onValueChange = {},
            label = {
                OneLineText(text = "Message")
            }
        )
        Button(
            modifier = Modifier.layoutId(layoutId = "send"),
            onClick = onDelete // Do something different for Send onClick
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                OneLineText(text = "Send")
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Mail",
                )
            }
        }
        Button(
            modifier = Modifier.layoutId(layoutId = "delete"),
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Draft",
            )
        }
    }
}

/**
 * Converts a [Color] into a hexadecimal string representation.
 *
 * The method first converts the [Color] to its ARGB integer representation using [toArgb],
 * then to an unsigned integer to handle the alpha channel correctly, and finally formats it
 * as a hexadecimal string. This is primarily used for embedding color values directly into the
 * [MotionScene] JSON string.
 *
 * @return A hexadecimal string representing the color (e.g., "ff0000ff" for opaque red).
 */
private fun Color.toHexString() = toArgb().toUInt().toString(radix = 16)