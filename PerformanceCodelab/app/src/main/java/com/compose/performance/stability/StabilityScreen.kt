/*
 * Copyright 2024 The Android Open Source Project
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
package com.compose.performance.stability

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tracing.trace
import com.compose.performance.R
import com.compose.performance.recomposeHighlighter
import java.time.LocalDate

/**
 * A Composable function that displays a [List] of [StabilityItem] items to demonstrate the benifits
 * of making the items stable.
 *
 * @param viewModel The [StabilityViewModel] associated with this screen.
 * This ViewModel manages the data and logic for the stability items.
 */
@Composable
fun StabilityScreen(viewModel: StabilityViewModel = viewModel()) {
    // TASK Codelab task: Make items stable with strong skipping mode and annotation to prevent recomposing DONE
    val items: List<StabilityItem> by viewModel.items.collectAsState()

    Box {
        Column {
            // TASK Codelab task: make LocalDate stable to prevent recomposing with each change DONE
            LatestChange(today = viewModel.latestDateChange)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .recomposeHighlighter(),
                contentPadding = PaddingValues(bottom = 72.dp)
            ) {
                items(items = items, key = { it.id }) { item: StabilityItem ->
                    StabilityItemRow(
                        item = item,
                        onChecked = { viewModel.checkItem(id = item.id, checked = it) },
                        onRemoveClicked = { viewModel.removeItem(id = item.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.addItem() },
            modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.BottomEnd)
                .testTag(tag = "fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_item)
            )
        }
    }
}

/**
 * A Composable function that displays a [Text] with the latest change date.
 *
 * @param today The [LocalDate] representing the latest change date.
 */
@Composable
fun LatestChange(today: LocalDate): Unit = trace(label = "latest_change") {
    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier.recomposeHighlighter()
    ) {
        Text(
            text = stringResource(R.string.latest_change_was, today),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * A Composable function that displays a [ListItem] representing a [StabilityItem].
 *
 * @param item The [StabilityItem] to display.
 * @param modifier The [Modifier] to apply to this layout node.
 * @param onChecked A function to be invoked when the checkbox state changes.
 * @param onRemoveClicked A function to be invoked when the remove button is clicked.
 */
@Composable
fun StabilityItemRow(
    item: StabilityItem,
    modifier: Modifier = Modifier,
    onChecked: (checked: Boolean) -> Unit,
    onRemoveClicked: () -> Unit
): Unit = trace(label = "item_row") {
    Box(modifier = modifier.recomposeHighlighter()) {
        val (rowTonalElevation: Dp, iconBg: Color) = when (item.type) {
            StabilityItemType.REFERENCE -> 4.dp to MaterialTheme.colorScheme.primary
            StabilityItemType.EQUALITY -> 0.dp to MaterialTheme.colorScheme.tertiary
        }

        ListItem(
            tonalElevation = rowTonalElevation,
            headlineContent = { Text(text = item.name) },
            leadingContent = {
                Text(
                    text = item.type.name.take(n = 3),
                    modifier = Modifier
                        .size(size = 40.dp)
                        .background(color = iconBg, shape = CircleShape)
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            overlineContent = {
                Text(text = "instance ${System.identityHashCode(item)}")
            },
            trailingContent = {
                Row {
                    Checkbox(checked = item.checked, onCheckedChange = onChecked)
                    IconButton(onClick = onRemoveClicked) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Default.Delete),
                            contentDescription = stringResource(id = R.string.remove)
                        )
                    }
                }
            }
        )

        if (item.checked) {
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.CenterStart)
            )
        }
    }
}
