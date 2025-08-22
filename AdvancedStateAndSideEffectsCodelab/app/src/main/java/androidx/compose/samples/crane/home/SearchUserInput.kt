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

@file:Suppress("UnusedImport")

package androidx.compose.samples.crane.home

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.rememberTransition
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
import androidx.compose.samples.crane.base.EditableUserInputState
import androidx.compose.samples.crane.base.rememberEditableUserInputState
import androidx.compose.samples.crane.home.PeopleUserInputAnimationState.Invalid
import androidx.compose.samples.crane.home.PeopleUserInputAnimationState.Valid
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext

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
        MutableTransitionState(initialState = Valid)

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
        val newState: PeopleUserInputAnimationState =
            if (people > MAX_PEOPLE) Invalid
            else Valid

        if (animationState.currentState != newState) animationState.targetState = newState
    }
}

/**
 * This Composable displays the currrent number of people traveling in a [CraneUserInput] Composable
 * and when the [MutableTransitionState.targetState] is [Invalid] it displays an additional [Text]
 * which displays the [String] "Error: We don't support more than $[MAX_PEOPLE] people". Our root
 * Composable is a [Column] wherein we `remember` a [MutableTransitionState] variable `val transitionState`
 * whose initial value is the current value of the [PeopleUserInputState.animationState] property of
 * our [peopleState] parameter, then we initialize our [State] of [Color] variable `val tint` to the
 * instance that the [tintPeopleUserInput] method returns (the current value of the `tint` [Color]
 * is used to "tint" the [Icon] and the text of the `caption` [Text] of the [CraneBaseUserInput] that
 * [CraneUserInput] uses to display the number of people traveling (its [Color] is animated using the
 * [Transition.animateColor] method by [tintPeopleUserInput] between the `onSurface` [Color] of
 * [MaterialTheme.colors] ([Color.White]) and the `secondary` [Color] of [MaterialTheme.colors] (which
 * our [CraneTheme] custom [MaterialTheme] specifies to be `crane_red`: the [Color] 0xFFE30425, a
 * bright blood red). Next we initialize our [Int] variable `val people` to the [PeopleUserInputState.people]
 * property of our [peopleState] parameter.
 *
 * As long as the [MutableTransitionState.targetState] of `transitionState` is not [Invalid] the only
 * child of the [Column] is a [CraneUserInput] Composable whose `text` argument displays the value of
 * `people` followed by the [String] "Adult" (or "Adults" if `people` is greater than 1) followed by
 * our [titleSuffix] parameter (which is the empty string except when we are called to be part of the
 * [FlySearchContent] Composable in which case it is the string ", Economy"). The `vectorImageId`
 * argument is the resource ID `R.drawable.ic_person` (a stylized silhouette of the bust of a person).
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
 * resource ID `R.font.raleway_semibold`) and a `fontSize` of 16.dp) with the `color` of the [Font]
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
    titleSuffix: String? = "",
    onPeopleChanged: (Int) -> Unit,
    peopleState: PeopleUserInputState = remember { PeopleUserInputState() }
) {
    Column {
        val transitionState: MutableTransitionState<PeopleUserInputAnimationState> =
            remember { peopleState.animationState }
        val tint: State<Color> = tintPeopleUserInput(transitionState)

        val people: Int = peopleState.people
        CraneUserInput(
            text = if (people == 1) "$people Adult$titleSuffix" else "$people Adults$titleSuffix",
            vectorImageId = R.drawable.ic_person,
            tint = tint.value,
            onClick = {
                peopleState.addPerson()
                onPeopleChanged(peopleState.people)
            }
        )
        if (transitionState.targetState == Invalid) {
            Text(
                text = "Error: We don't support more than $MAX_PEOPLE people",
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
 * and whose `vectorImageId` argument is the drawable with resource ID `R.drawable.ic_location` (the
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
 * content of the back layer) of the [BackdropScaffold] in [CraneHomeContent] when the [CraneScreen.Fly]
 * tab is selected. It allows the user to enter a [String] which it feeds to its [onToDestinationChanged]
 * parameter whenever the text entered changes. It starts by "remembering" its [EditableUserInputState]
 * variable `val editableUserInputState` using the [rememberEditableUserInputState] method with an
 * initial `hint` value of "Choose Destination". The [EditableUserInputState] has two fields:
 * [EditableUserInputState.text] which holds the current text, and [EditableUserInputState.hint]
 * which holds the `hint` passed to its constructor, as well as a [EditableUserInputState.isHint]
 * property which returns `true` if the two fields are still equal. Its companion object also has
 * a [Saver] which allows [rememberEditableUserInputState] to use [rememberSaveable] to remember the
 * instance of [EditableUserInputState] it constructs across Activity recreation. The [EditableUserInputState]
 * returned has both the `text` and the `hint` fields set to the `hint` argument passed to
 * [rememberEditableUserInputState]. Our Composable `content` consists of a [CraneEditableUserInput]
 * whose `state` argument is our [EditableUserInputState] variable `editableUserInputState` (its `text`
 * field will be updated with the text entered by the user), whose `caption` argument is the [String]
 * "To", and whose `vectorImageId` is the drawable with resource ID `R.drawable.ic_plane` (a stylized
 * airplane).
 *
 * We next use the [rememberUpdatedState] method to initialize and remember our lambda variable
 * `val currentOnDestinationChanged` to our [onToDestinationChanged] parameter (this function
 * remembers a `mutableStateOf` [onToDestinationChanged] and updates the value of
 * `currentOnDestinationChanged` to the new Value of [onToDestinationChanged] on each recomposition
 * of the [rememberUpdatedState] call. [rememberUpdatedState] should be used when parameters or values
 * computed during composition are referenced by a long-lived lambda or object expression.
 * Recomposition will update the resulting State without recreating the long-lived lambda or object,
 * allowing that object to persist without cancelling and resubscribing, or relaunching a long-lived
 * operation that may be expensive or prohibitive to recreate and restart. This may be common when
 * working with [LaunchedEffect] for example). Next we use [LaunchedEffect] with a `key1` of
 * `editableUserInputState` to launch its lambda `block` argument into the composition's
 * [CoroutineContext]. The coroutine will be cancelled when the [LaunchedEffect] leaves the composition.
 * The lambda block uses the [snapshotFlow] method to create a [Flow] that runs its lambda block when
 * collected and emits the result (the current value of the [EditableUserInputState.text] field of
 * `editableUserInputState`, recording any snapshot state that was accessed. While collection continues,
 * if a new Snapshot is applied that changes state accessed by block, the flow will run block again,
 * re-recording the snapshot state that was accessed. If the result of block is not equal to the
 * previous result, the flow will emit that new result. A [Flow.filter] is applied to the [Flow] which
 * will return a flow containing only values of the original flow where the [EditableUserInputState.isHint]
 * property is `false` (the user has altered the original text), and [Flow.collect] will be used on
 * this [Flow] in order to pass the new value of the [EditableUserInputState.text] field of
 * `editableUserInputState` to our `currentOnDestinationChanged` lambda (which is being kept up to
 * date with our [onToDestinationChanged] lambda parameter by [rememberUpdatedState] recall).
 *
 * @param onToDestinationChanged a lambda we should call with the text entered by the user whenever
 * that changes value. The [CraneSearch] Composable passes us the `onToDestinationChanged` that is
 * passed to its [FlySearchContent] parent and [SearchContent] passes it a lambda which calls the
 * [MainViewModel.toDestinationChanged] method of its `viewModel` parameter with the new value of
 * text entered. (The `viewModel` is the one that Hilt injects as the default value of the `viewModel`
 * parameter of [CraneHomeContent] BTW).
 */
@Composable
fun ToDestinationUserInput(onToDestinationChanged: (String) -> Unit) {
    val editableUserInputState: EditableUserInputState = rememberEditableUserInputState(hint = "Choose Destination")
    CraneEditableUserInput(
        state = editableUserInputState,
        caption = "To",
        vectorImageId = R.drawable.ic_plane
    )

    val currentOnDestinationChanged: (String) -> Unit by rememberUpdatedState(onToDestinationChanged)
    LaunchedEffect(editableUserInputState) {
        snapshotFlow { editableUserInputState.text }
            .filter { !editableUserInputState.isHint }
            .collect {
                currentOnDestinationChanged(editableUserInputState.text)
            }
    }
}

/**
 * This Composable is used as part of the [CraneSearch] Composables used by [EatSearchContent],
 * [FlySearchContent] and [SleepSearchContent]. It does nothing except ripple when clicked. Its
 * `content` is a [CraneUserInput] whose `caption` argument is the [String] "Select Dates", whose
 * `text` argument is the empty [String] and whose `vectorImageId` argument is the drawable with
 * resource ID `R.drawable.ic_calendar` (a stylized picture of a hanging calendar).
 */
@Composable
fun DatesUserInput() {
    CraneUserInput(
        caption = "Select Dates",
        text = "",
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
 * is the empty [String] and whose `targetValueByState` lambda argument returns `validColor` if its
 * `state` argument is [Valid] or `invalidColor` if it is not.
 *
 * @param transitionState the [MutableTransitionState] created from the [PeopleUserInputState.animationState]
 * enum ([Valid] or [Invalid]) of the `peopleState` [PeopleUserInputState] parameter of [PeopleUserInput].
 * A [MutableTransitionState] contains two fields: [MutableTransitionState.currentState] and
 * [MutableTransitionState.targetState]. Our `currentState` is initialized to [Valid], and can only
 * be mutated by a [Transition]. `targetState` is also initialized to [Valid]. It can be mutated
 * to alter the course of a transition animation that is created with the [MutableTransitionState]
 * using [updateTransition]. Both `currentState` and `targetState` are backed by a [State] object.
 * @return a [State] object holding the current [Color] in its [State.value] field.
 */
@Composable
private fun tintPeopleUserInput(
    transitionState: MutableTransitionState<PeopleUserInputAnimationState>
): State<Color> {
    val validColor: Color = MaterialTheme.colors.onSurface
    val invalidColor: Color = MaterialTheme.colors.secondary

    val transition: Transition<PeopleUserInputAnimationState> = rememberTransition(transitionState, label = "")
    return transition.animateColor(
        transitionSpec = { tween(durationMillis = 300) }, label = ""
    ) { state: PeopleUserInputAnimationState ->
        if (state == Valid) validColor else invalidColor
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
