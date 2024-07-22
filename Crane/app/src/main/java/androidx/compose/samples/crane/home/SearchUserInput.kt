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

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.CraneBaseUserInput
import androidx.compose.samples.crane.base.CraneEditableUserInput
import androidx.compose.samples.crane.base.CraneUserInput
import androidx.compose.samples.crane.home.PeopleUserInputAnimationState.Invalid
import androidx.compose.samples.crane.home.PeopleUserInputAnimationState.Valid
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController

/**
 * This enum is used to indicate whether the value of [PeopleUserInputState.people] (the number of
 * people traveling) is greater than [MAX_PEOPLE] (an [Invalid] value) or less than or equal to
 * [MAX_PEOPLE] (a [Valid] value).
 */
enum class PeopleUserInputAnimationState {
    /**
     * The number of people traveling is less than or equal to [MAX_PEOPLE]
     */
    Valid,

    /**
     * The number of people traveling is greater than [MAX_PEOPLE]
     */
    Invalid
}

/**
 * This class holds the current number of people traveling in its read-only [PeopleUserInputState.people]
 * property, and a [MutableTransitionState] of [PeopleUserInputAnimationState] in its
 * [PeopleUserInputState.animationState] property. It has a [PeopleUserInputState.addPerson] method
 * which adds 1 to [PeopleUserInputState.people] modulo [MAX_PEOPLE] plus 1 and a private
 * [PeopleUserInputState.updateAnimationState] method which [PeopleUserInputState.addPerson] calls
 * after each increment of [PeopleUserInputState.people] to see if the value of
 * [PeopleUserInputState.animationState] needs to be toggled between [Valid] and [Invalid] (or vice
 * versa). This [MutableTransitionState] is used by [tintPeopleUserInput] to animate the [State]
 * of [Color] it returns between the `onSurface` color of [MaterialTheme.colors] ([Color.White])
 * for [Valid] and the `secondary` color of [MaterialTheme.colors] (our [CraneTheme] custom
 * [MaterialTheme] specifies `crane_red` which is the [Color] 0xFFE30425, a blood red color) for
 * [Invalid].
 */
class PeopleUserInputState {
    /**
     * The number of people traveling. It has a private `set` method so that it can only be changed
     * by our [addPerson] method. Since it delegates to a [MutableState] any Composable reading it
     * will be recomposed when it changes value.
     */
    var people: Int by mutableIntStateOf(1)
        private set

    /**
     * This property indicates whether the current value of our [people] property is [Invalid]
     * ([people] greater than [MAX_PEOPLE]) or [Valid] ([people] less than or equal to [MAX_PEOPLE]).
     * It is updated by our [updateAnimationState] method (if it needs to be) every time that
     * [addPerson] increments [people]. It is a [MutableTransitionState] of the enum
     * [PeopleUserInputAnimationState] which contains two fields: `currentState` and `targetState`.
     * `currentState` is initialized to the provided `initialState` ([Valid]), and can only be mutated
     * by a [Transition]. `targetState` is also initialized to `initialState`. It can be mutated to
     * alter the course of a transition animation that is created from the [MutableTransitionState]
     * using [updateTransition]. Both `currentState` and `targetState` are backed by a [State] object.
     */
    val animationState: MutableTransitionState<PeopleUserInputAnimationState> =
        MutableTransitionState(Valid)

    /**
     * Adds 1 to our [people] property modulo [MAX_PEOPLE] plus 1, then calls our [updateAnimationState]
     * method to have it toggle the value of our [animationState] property if it needs to based on the
     * new value of [people] ([people] greater than [MAX_PEOPLE] is [Invalid], and less than or equal
     * to is [Valid]).
     */
    fun addPerson() {
        people = (people % (MAX_PEOPLE + 1)) + 1
        updateAnimationState()
    }

    /**
     * Updates the [MutableTransitionState.targetState] of our [animationState] field to [Valid] or
     * [Invalid] depending on whether our [people] property is greater than [MAX_PEOPLE] ([Invalid])
     * or less than or equal to [MAX_PEOPLE] ([Valid]) only if the new value is not equal to the
     * value of the [MutableTransitionState.currentState] of our [animationState] field.
     */
    private fun updateAnimationState() {
        val newState =
            if (people > MAX_PEOPLE) Invalid
            else Valid

        if (animationState.currentState != newState) animationState.targetState = newState
    }
}

/**
 * This Composable displays the currrent number of people traveling in a [CraneUserInput] Composable
 * and when the [MutableTransitionState.targetState] is [Invalid] it displays an additional [Text]
 * which displays the [String] "Error: We don't support more than $[MAX_PEOPLE] people". Our root
 * Composable is a [Column] wherein we `remember` a [MutableTransitionState] of
 * [PeopleUserInputAnimationState] variable `val transitionState` whose initial value is the current
 * value of the [PeopleUserInputState.animationState] property of our [peopleState] parameter, then
 * we initialize our [State] of [Color] variable `val tint` to the instance that the
 * [tintPeopleUserInput] method returns (the current value of the `tint` [Color] is used to "tint"
 * the [Icon] and the text of the `caption` [Text] of the [CraneBaseUserInput] that [CraneUserInput]
 * uses to display the number of people traveling (its [Color] is animated using the
 * [Transition.animateColor] method by [tintPeopleUserInput] between the `onSurface` [Color] of
 * [MaterialTheme.colors] ([Color.White]) and the `secondary` [Color] of [MaterialTheme.colors]
 * (which our [CraneTheme] custom [MaterialTheme] specifies to be `crane_red`: the [Color] 0xFFE30425,
 * a bright blood red). Next we initialize our [Int] variable `val people` to the
 * [PeopleUserInputState.people] property of our [peopleState] parameter.
 *
 * As long as the [MutableTransitionState.targetState] of `transitionState` is not [Invalid] the only
 * child of the [Column] is a [CraneUserInput] Composable whose `text` argument displays the value of
 * `people` followed by the [String] "Adult" (or "Adults" if `people` is greater than 1) followed by
 * our [titleSuffix] parameter (which is the empty string except when we are called to be part of the
 * [FlySearchContent] Composable in which case it is the string ", Economy"). The `vectorImageId`
 * argument is the resource ID [R.drawable.ic_person] (a stylized silhouette of the bust of a person).
 * The `tint` argument is the current [State.value] color of our [State] of [Color] variable `tint`.
 * The `onClick` argument is a lambda which calls the [PeopleUserInputState.addPerson] method of our
 * [peopleState] parameter to add another person to the current number of people traveling, followed
 * by a call to our [onPeopleChanged] lambda with the new value of the [PeopleUserInputState.people]
 * property of [peopleState].
 *
 * When the [MutableTransitionState.targetState] of `transitionState` is [Invalid] a [Text] is added
 * to the [Column] displaying the `text` "Error: We don't support more than $[MAX_PEOPLE] people"
 * using as its `style` argument a copy of the `body1` [TextStyle] of [MaterialTheme.typography]
 * (the `craneFontFamily` [FontFamily] with a `fontWeight` of [FontWeight.W600] (the [Font] with
 * resource ID [R.font.raleway_semibold]) and a `fontSize` of 16.dp) with the `color` of the [Font]
 * the `secondary` [Color] of [MaterialTheme.colors] (which our [CraneTheme] custom [MaterialTheme]
 * specifies to be `crane_red`: the [Color] 0xFFE30425, a bright blood red).
 *
 * @param titleSuffix a [String] to be added to the end of the text displayed by our [CraneUserInput]
 * which is the empty string except when we are called to be part of the [FlySearchContent] Composable,
 * in which case it is the string ", Economy"
 * @param onPeopleChanged a lambda to be called with the current number of people traveling (ie. the
 * value of the [PeopleUserInputState.people] property of our parameter [peopleState]) whenever it
 * changes value. [CraneHomeContent] passes [SearchContent] a lambda which calls the
 * [MainViewModel.updatePeople] method of its `viewModel` with the [Int] passed to [onPeopleChanged],
 * and [SearchContent] passes it to [EatSearchContent], [FlySearchContent] and [SleepSearchContent]
 * who pass it on to us.
 * @param peopleState the [PeopleUserInputState] instance that keeps track of the current number of
 * people traveling. Our callers do not pass us one, so the default `remember`'ed instance constructed
 * as our parameter default is always used instead (a new instance is created every time that the user
 * switches to a different tab, ie. the old values of [PeopleUserInputState.people] are forgotten).
 */
@Composable
fun PeopleUserInput(
    titleSuffix: String = "",
    onPeopleChanged: (Int) -> Unit,
    peopleState: PeopleUserInputState = remember { PeopleUserInputState() }
) {
    Column {
        val transitionState: MutableTransitionState<PeopleUserInputAnimationState> =
            remember { peopleState.animationState }
        val tint: State<Color> = tintPeopleUserInput(transitionState)

        val people: Int = peopleState.people
        CraneUserInput(
            text = pluralStringResource(
                id = R.plurals.number_adults_selected,
                count = people,
                people,
                titleSuffix
            ),
            vectorImageId = R.drawable.ic_person,
            tint = tint.value,
            onClick = {
                peopleState.addPerson()
                onPeopleChanged(peopleState.people)
            }
        )
        if (transitionState.targetState == Invalid) {
            Text(
                text = stringResource(
                    id = R.string.error_max_people,
                    MAX_PEOPLE
                ),
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.secondary)
            )
        }
    }
}

/**
 * This Composable is used by the [CraneSearch] Composable that is used by the [FlySearchContent]
 * that is displayed by the `SearchContent` that is used as the `backLayerContent` argument (the
 * content of the back layer) of the [BackdropScaffold] in [CraneHomeContent] when the [CraneScreen.Fly]
 * tab is selected. Its `content` is a [CraneUserInput] whose `text` argument is "Seoul, South Korea"
 * and whose `vectorImageId` argument is the drawable with resource ID [R.drawable.ic_location] (the
 * "inverted tear drop with a circular hole" that is used as a location marker on Google Maps and
 * elsewhere). The text is not modifiable, and no `onClick` argument is given so the Composable just
 * "ripples" when clicked.
 */
@Composable
fun FromDestination() {
    CraneUserInput(text = "Seoul, South Korea", vectorImageId = R.drawable.ic_location)
}

/**
 * This Composable is used by the [CraneSearch] Composable that is used by the [FlySearchContent]
 * that is displayed by the `SearchContent` that is used as the `backLayerContent` argument (the
 * content of the back layer) of the [BackdropScaffold] in [CraneHomeContent] when the
 * [CraneScreen.Fly] tab is selected.
 *
 * @param onToDestinationChanged a lambda we should call with the text entered by the user whenever
 * that changes value. The [CraneSearch] Composable passes us the `onToDestinationChanged` that is
 * passed to its [FlySearchContent] parent and [SearchContent] passes it a lambda which calls the
 * [MainViewModel.toDestinationChanged] method of its `viewModel` parameter with the new value of
 * text entered.
 */
@Composable
fun ToDestinationUserInput(onToDestinationChanged: (String) -> Unit) {
    CraneEditableUserInput(
        hint = stringResource(R.string.select_destination_hint),
        caption = stringResource(R.string.select_destination_to_caption),
        vectorImageId = R.drawable.ic_plane,
        onInputChanged = onToDestinationChanged
    )
}

/**
 * This Composable is used as part of the [CraneSearch] Composables used by [EatSearchContent],
 * [FlySearchContent] and [SleepSearchContent]. Its `content` is a [CraneUserInput] whose `caption`
 * argument is the [String] "Select Dates" if our [String] parameter [datesSelected] is empty or
 * `null` if it is not, whose `text` argument is our [String] parameter [datesSelected] and whose
 * `vectorImageId` argument is the drawable with resource ID [R.drawable.ic_calendar] (a stylized
 * picture of a hanging calendar). It `onClick` argument is our [onDateSelectionClicked] lambda
 * parameter lambda parameter which traces back up the hierarchy to a lambda that calls the
 * [NavHostController.navigate] method to navigate to the `route` [Routes.Calendar.route] when
 * it is clicked to allow the user to select a new date range for [datesSelected].
 *
 * @param datesSelected the [String] representing the date range the user has selected.
 * @param onDateSelectionClicked a lambda to be called when our [CraneUserInput] is clicked. It
 * traces back up the hierarchy to a lambda that calls the [NavHostController.navigate] method to
 * navigate to the `route` [Routes.Calendar.route] when called.
 */
@Composable
fun DatesUserInput(datesSelected: String, onDateSelectionClicked: () -> Unit) {
    CraneUserInput(
        onClick = onDateSelectionClicked,
        caption = if (datesSelected.isEmpty()) stringResource(R.string.select_dates) else null,
        text = datesSelected,
        vectorImageId = R.drawable.ic_calendar
    )
}

/**
 * This Composable animates the [Color] of a [State] of [Color] depending on the value of the
 * [PeopleUserInputAnimationState] ([Valid] or [Invalid]) of its [MutableTransitionState] of
 * [PeopleUserInputAnimationState] parameter [transitionState]. The [Color] for [Valid] (our variable
 * `val validColor`) is the `onSurface` [Color] of [MaterialTheme.colors] which our [CraneTheme]
 * custom [MaterialTheme] specifies to be `crane_white` ([Color.White]), and the [Color] for [Invalid]
 * (our variable `val invalidColor`) is the `secondary` [Color] of [MaterialTheme.colors] which our
 * [CraneTheme] custom [MaterialTheme] specifies to be `crane_red` (the [Color] 0xFFE30425 which is
 * a bright blood red). We use the [updateTransition] method to create a [Transition] and put it in
 * the [MutableTransitionState.currentState] of our [transitionState] parameter to initialize our
 * variable `val transition`. Whenever the [MutableTransitionState.targetState] of [transitionState]
 * changes, the [Transition] will animate to the new target state. Finally we return the [State] of
 * [Color] that the [Transition.animateColor] method of `transition` creates when its `transitionSpec`
 * argument is a [tween] whose `durationMillis` argument is 300 milliseconds, whose `label` argument
 * is "tintTransitionSpec" and whose `targetValueByState` lambda argument returns `validColor` if its
 * `state` argument is [Valid] or `invalidColor` if it is not.
 *
 * @param transitionState the [MutableTransitionState] created from the
 * [PeopleUserInputState.animationState] enum ([Valid] or [Invalid]) of the `peopleState`
 * [PeopleUserInputState] parameter of [PeopleUserInput]. A [MutableTransitionState] contains two
 * fields: [MutableTransitionState.currentState] and [MutableTransitionState.targetState]. Our
 * `currentState` is initialized to [Valid], and can only be mutated by a [Transition].
 * `targetState` is also initialized to [Valid]. It can be mutated to alter the course of a
 * transition animation that is created with the [MutableTransitionState] using [updateTransition].
 * Both `currentState` and `targetState` are backed by a [State] object.
 * @return a [State] object holding the current [Color] in its [State.value] field.
 */
@Composable
private fun tintPeopleUserInput(
    transitionState: MutableTransitionState<PeopleUserInputAnimationState>
): State<Color> {
    val validColor = MaterialTheme.colors.onSurface
    val invalidColor = MaterialTheme.colors.secondary

    val transition = updateTransition(transitionState = transitionState, label = "tintTransition")
    return transition.animateColor(
        transitionSpec = { tween(durationMillis = 300) }, label = "tintTransitionSpec"
    ) {
        if (it == Valid) validColor else invalidColor
    }
}

/**
 * A Preview of our [CraneTheme] wrapped [PeopleUserInput] Composable whose `onPeopleChanged` lambda
 * argument is an empty block.
 */
@Preview
@Composable
fun PeopleUserInputPreview() {
    CraneTheme {
        PeopleUserInput(onPeopleChanged = {})
    }
}
