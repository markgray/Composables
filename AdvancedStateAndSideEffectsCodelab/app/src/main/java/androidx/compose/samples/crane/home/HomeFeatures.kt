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

package androidx.compose.samples.crane.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BackdropScaffold
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.SimpleUserInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.Tab
import androidx.compose.runtime.MutableState
import androidx.compose.samples.crane.base.CraneEditableUserInput
import androidx.compose.samples.crane.base.CraneUserInput

/**
 * This Composable is displayed by the [SearchContent] Composable when the [Tab] selected in the
 * [HomeTabBar] that is used as the `appBar` argument of the [BackdropScaffold] of [CraneHomeContent]
 * is the one for [CraneScreen.Fly] (the one labeled "Fly"). Our root Composable is a [CraneSearch]
 * whose content Composable argument (which it displays in a [Column]) is:
 *  - A [PeopleUserInput] whose `titleSuffix` argument is the [String] ", Economy" and whose
 *  `onPeopleChanged` argument is our [onPeopleChanged] parameter. It displays the current number of
 *  people traveling followed by the `titleSuffix` in its [CraneUserInput], and when the [CraneUserInput]
 *  is clicked it increments the [PeopleUserInputState.people] field by calling the
 *  [PeopleUserInputState.addPerson] method then calls [onPeopleChanged] with the new value of
 *  [PeopleUserInputState.people] (which is a [MutableState] of [Int] (whose initial value is 1) so
 *  Compose is notified of the change and recomposes all Composables which use that value.
 *  - A [Spacer] whose `height` is 8.dp
 *  - A [FromDestination] Composable which displays the `text` "Seoul, South Korea" in a [CraneUserInput]
 *  but does not allow the user to change its value.
 *  - A [Spacer] whose `height` is 8.dp
 *  - A [ToDestinationUserInput] Composable whose `onToDestinationChanged` argument is our parameter
 *  [onToDestinationChanged]. Its `content` is a [CraneEditableUserInput] which allows the user to
 *  type into it, but its logic is rather broken so [onToDestinationChanged] is never called.
 *  - A [Spacer] whose `height` is 8.dp
 *  - A [DatesUserInput] Composable (which contains a [CraneUserInput] Composable whose `caption` is
 *  "Select Dates", but does nothing when clicked).
 *
 * @param onPeopleChanged a lambda that our [PeopleUserInput] Composable can call with the new number
 * of people traveling when the user clicks on the [CraneUserInput] it contains. [CraneHomeContent]
 * passes [SearchContent] a lambda which calls the [MainViewModel.updatePeople] method of its `viewModel`
 * with the [Int] passed to [onPeopleChanged], and [SearchContent] passes us that lambda so that we
 * can pass it on to [PeopleUserInput].
 * @param onToDestinationChanged a lambda that our [ToDestinationUserInput] Composable can call with
 * the new destination city, when the user enters one in its [CraneEditableUserInput] Composable.
 * [SearchContent] passes us a lambda that calls the [MainViewModel.toDestinationChanged] method of
 * its `viewModel` with the [String] that the user has entered.
 */
@Composable
fun FlySearchContent(
    onPeopleChanged: (Int) -> Unit,
    onToDestinationChanged: (String) -> Unit
) {
    CraneSearch {
        PeopleUserInput(
            titleSuffix = ", Economy",
            onPeopleChanged = onPeopleChanged
        )
        Spacer(modifier = Modifier.height(height = 8.dp))
        FromDestination()
        Spacer(modifier = Modifier.height(height = 8.dp))
        ToDestinationUserInput(onToDestinationChanged = onToDestinationChanged)
        Spacer(modifier = Modifier.height(height = 8.dp))
        DatesUserInput()
    }
}

/**
 * This Composable is displayed by the [SearchContent] Composable when the [Tab] selected in the
 * [HomeTabBar] that is used as the `appBar` argument of the [BackdropScaffold] of [CraneHomeContent]
 * is the one for [CraneScreen.Sleep] (the one labeled "Sleep"). Our root Composable is a [CraneSearch]
 * whose content Composable argument (which it displays in a [Column]) is:
 *  - A [PeopleUserInput] whose `onPeopleChanged` argument is our [onPeopleChanged] parameter. It
 *  displays the current number of people traveling in its [CraneUserInput], and when the [CraneUserInput]
 *  is clicked it increments the [PeopleUserInputState.people] field by calling the
 *  [PeopleUserInputState.addPerson] method then calls [onPeopleChanged] with the new value of
 *  [PeopleUserInputState.people] (which is a [MutableState] of [Int] (whose initial value is 1) so
 *  Compose is notified of the change and recomposes all Composables which use that value.
 *  - A [Spacer] whose `height` is 8.dp
 *  - A [DatesUserInput] Composable (which contains a [CraneUserInput] Composable whose `caption` is
 *  "Select Dates", but does nothing when clicked).
 *  - A [Spacer] whose `height` is 8.dp
 *  - A [SimpleUserInput] whose `caption` argument is "Select Location", and whose `vectorImageId`
 *  is the drawable with resource ID `R.drawable.ic_hotel` (a stylized picture of someone sleeping
 *  in a bed). It also does nothing when clicked.
 *
 * @param onPeopleChanged a lambda that our [PeopleUserInput] Composable can call with the new number
 * of people traveling when the user clicks on the [CraneUserInput] it contains. [CraneHomeContent]
 * passes [SearchContent] a lambda which calls the [MainViewModel.updatePeople] method of its `viewModel`
 * with the [Int] passed to [onPeopleChanged], and [SearchContent] passes us that lambda so that we
 * can pass it on to [PeopleUserInput].
 */
@Composable
fun SleepSearchContent(onPeopleChanged: (Int) -> Unit) {
    CraneSearch {
        PeopleUserInput(onPeopleChanged = onPeopleChanged)
        Spacer(modifier = Modifier.height(height = 8.dp))
        DatesUserInput()
        Spacer(modifier = Modifier.height(height = 8.dp))
        SimpleUserInput(caption = "Select Location", vectorImageId = R.drawable.ic_hotel)
    }
}

/**
 * This Composable is displayed by the [SearchContent] Composable when the [Tab] selected in the
 * [HomeTabBar] that is used as the `appBar` argument of the [BackdropScaffold] of [CraneHomeContent]
 * is the one for [CraneScreen.Eat] (the one labeled "Eat"). Our root Composable is a [CraneSearch]
 * whose content Composable argument (which it displays in a [Column]) is:
 *  - A [PeopleUserInput] whose `onPeopleChanged` argument is our [onPeopleChanged] parameter. It
 *  displays the current number of people traveling in its [CraneUserInput], and when the [CraneUserInput]
 *  is clicked it increments the [PeopleUserInputState.people] field by calling the
 *  [PeopleUserInputState.addPerson] method then calls [onPeopleChanged] with the new value of
 *  [PeopleUserInputState.people] (which is a [MutableState] of [Int] (whose initial value is 1) so
 *  Compose is notified of the change and recomposes all Composables which use that value.
 *  - A [Spacer] whose `height` is 8.dp
 *  - A [DatesUserInput] Composable (which contains a [CraneUserInput] Composable whose `caption` is
 *  "Select Dates", but does nothing when clicked).
 *  - A [Spacer] whose `height` is 8.dp
 *  - A [SimpleUserInput] whose `caption` argument is "Select Time". It does nothing when clicked.
 *  - A [Spacer] whose `height` is 8.dp
 *  - A [SimpleUserInput] whose `caption` argument is "Select Location". It does nothing when clicked.
 *
 * @param onPeopleChanged a lambda that our [PeopleUserInput] Composable can call with the new number
 * of people traveling when the user clicks on the [CraneUserInput] it contains. [CraneHomeContent]
 * passes [SearchContent] a lambda which calls the [MainViewModel.updatePeople] method of its `viewModel`
 * with the [Int] passed to [onPeopleChanged], and [SearchContent] passes us that lambda so that we
 * can pass it on to [PeopleUserInput].
 */
@Composable
fun EatSearchContent(onPeopleChanged: (Int) -> Unit) {
    CraneSearch {
        PeopleUserInput(onPeopleChanged = onPeopleChanged)
        Spacer(modifier = Modifier.height(height = 8.dp))
        DatesUserInput()
        Spacer(modifier = Modifier.height(height = 8.dp))
        SimpleUserInput(caption = "Select Time", vectorImageId = R.drawable.ic_time)
        Spacer(modifier = Modifier.height(height = 8.dp))
        SimpleUserInput(caption = "Select Location", vectorImageId = R.drawable.ic_restaurant)
    }
}

/**
 * This is basically just a convenience function that provides [Column] with a [Modifier.padding]
 * whose `start` is 24.dp, `top` is 0.dp, `end` is 24.dp, and `bottom` is 12.dp
 *
 * @param content the Composables that we should pass as the `content` argument of our [Column].
 */
@Composable
private fun CraneSearch(content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(start = 24.dp, top = 0.dp, end = 24.dp, bottom = 12.dp)) {
        content()
    }
}
