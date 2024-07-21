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

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BackdropScaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.samples.crane.base.ExploreSection
import androidx.compose.samples.crane.calendar.Calendar
import androidx.compose.samples.crane.calendar.model.CalendarState
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.di.DefaultDispatcher
import androidx.compose.samples.crane.di.DispatchersModule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * The maximum number of people that can travel
 */
const val MAX_PEOPLE: Int = 4

/**
 * This is the [ViewModel] used by [MainActivity]. It is injected by Hilt using the [hiltViewModel]
 * method in the `onCreate` override of [MainActivity]. The `HiltViewModel` annotation identifies a
 * [ViewModel] for construction injection. The [ViewModel] annotated with HiltViewModel will be
 * available for creation by the [HiltViewModelFactory] and can be retrieved by default in an
 * `Activity` or `Fragment` annotated with `AndroidEntryPoint`. The `HiltViewModel` containing a
 * constructor annotated with `Inject` will have its dependencies defined in the constructor
 * parameters injected by Dagger's Hilt. The generated java class `MainViewModel_Factory.java`
 * is used to provide instances of [MainViewModel].
 *
 * @param destinationsRepository the [DestinationsRepository] singleton injected by Hilt that we
 * should use in order to use the three [List] of [ExploreModel]'s it provides:
 * [DestinationsRepository.destinations], [DestinationsRepository.hotels] and
 * [DestinationsRepository.restaurants].
 * @param defaultDispatcher the [CoroutineDispatcher] that is injected by Hilt for the qualifier
 * `@DefaultDispatcher`, which is the [Dispatchers.Default] that is returned by the method
 * [DispatchersModule.provideDefaultDispatcher].
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val destinationsRepository: DestinationsRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    /**
     * The [MutableState] wrapped [SplashState] that is used to control whether [LandingScreen] or
     * [MainContent] is displayed. It is used as the `initialState` of a [MutableTransitionState]
     * that is animated from its initial value of [SplashState.Shown] to [SplashState.Completed]
     * driving the alpha of [LandingScreen] from 1f to 0f and the alpha of [MainContent] from
     * 0f to 1f, then the `onTimeout` lambda argument of [LandingScreen] sets [shownSplash] to
     * [SplashState.Completed]
     */
    val shownSplash: MutableState<SplashState> = mutableStateOf(SplashState.Shown)

    /**
     * The [List] of [ExploreModel] which is used as the dataset for the `frontLayerContent` of the
     * [BackdropScaffold] of [CraneHomeContent] when the [CraneScreen.Sleep] tab of [CraneHomeContent]
     * is selected (file home/CraneHome.kt). A when statement chooses which of three [ExploreSection]
     * Composables calls is executed, and [ExploreSection] then has its `ExploreList` Composable
     * display it in its [LazyColumn].
     */
    val hotels: List<ExploreModel> = destinationsRepository.hotels

    /**
     * The [List] of [ExploreModel] which is used as the dataset for the `frontLayerContent` of the
     * [BackdropScaffold] of [CraneHomeContent] when the [CraneScreen.Eat] tab of [CraneHomeContent]
     * is selected (file home/CraneHome.kt). A when statement chooses which of three [ExploreSection]
     * Composables calls is executed, and [ExploreSection] then has its `ExploreList` Composable
     * display it in its [LazyColumn].
     */
    val restaurants: List<ExploreModel> = destinationsRepository.restaurants

    /**
     * The [CalendarState] that contols the [Calendar] Composable.
     */
    val calendarState = CalendarState()

    /**
     * This private [MutableLiveData] of a [List] of [ExploreModel] objects is read using our
     * publicly accessible field [suggestedDestinations] to be used as the dataset for the
     * `frontLayerContent` of the [BackdropScaffold] of [CraneHomeContent] when the [CraneScreen.Fly]
     * tab of [CraneHomeContent] is selected (file home/CraneHome.kt). It is wrapped in a
     * [MutableLiveData] so that it can be observed using `observeAsState` It is set to a new value
     * by our [toDestinationChanged] method and our [updatePeople] method.
     */
    private val _suggestedDestinations = MutableLiveData<List<ExploreModel>>()

    /**
     * Public read-only access to our [MutableLiveData] of [List] of [ExploreModel] field
     * [_suggestedDestinations]. [CraneHomeContent] uses the `observeAsState` extension function
     * on this property to initialize its [List] of [ExploreModel] variable `suggestedDestinations`,
     * which is then used as the dataset for the `frontLayerContent` of the [BackdropScaffold] of
     * [CraneHomeContent] when the [CraneScreen.Fly] tab of [CraneHomeContent] is selected (file
     * home/CraneHome.kt). A when statement chooses which of three [ExploreSection] Composables
     * calls is executed, and [ExploreSection] then has its `ExploreList` Composable display it in
     * its [LazyColumn].
     */
    val suggestedDestinations: LiveData<List<ExploreModel>>
        get() = _suggestedDestinations

    init {
        _suggestedDestinations.value = destinationsRepository.destinations
    }

    /**
     * Called by the `onDayClicked` lambda parameter of `CalendarContent` with the [LocalDate] of the
     * day when the user clicks on a day in the [Calendar]. We use the [CoroutineScope] tied to this
     * [ViewModel] to launch a new coroutine and call the [CalendarState.setSelectedDay] of our
     * [CalendarState] field [calendarState] with our [LocalDate] parameter [daySelected] as its
     * `newDate` argument.
     *
     * @param daySelected the [LocalDate] of the day in the [Calendar] that the user clicked.
     */
    fun onDaySelected(daySelected: LocalDate) {
        viewModelScope.launch {
            calendarState.setSelectedDay(newDate = daySelected)
        }
    }

    /**
     * This method is called whenever the user increments the number of people traveling. We use the
     * [CoroutineScope] tied to this [ViewModel] to launch a new coroutine and if the new number of
     * people traveling is greater than [MAX_PEOPLE] (4) we set the value of our [List] of
     * [ExploreModel] field [_suggestedDestinations] to an [emptyList], otherwise we use our
     * [CoroutineDispatcher] field [defaultDispatcher] (that is injected by Hilt) as the
     * [CoroutineContext] for a coroutine which sets the [List] of [ExploreModel] variable
     * `val newDestinations` to a randomly shuffled copy of the [DestinationsRepository.destinations]
     * field of [destinationsRepository] with our [Int] parameter [people] used to create a random
     * seed for the shuffle. When the coroutine that is initializing `newDestinations` finishes we
     * set  the value of our [List] of [ExploreModel] field [_suggestedDestinations] to `newDestinations`
     * causing any Composable using a variable observed from [suggestedDestinations] as a [State] to
     * recompose.
     *
     * @param people the new number of people traveling.
     */
    fun updatePeople(people: Int) {
        viewModelScope.launch {
            if (people > MAX_PEOPLE) {
                _suggestedDestinations.value = emptyList()
            } else {
                val newDestinations = withContext(defaultDispatcher) {
                    destinationsRepository.destinations
                        .shuffled(Random(people * (1..100).shuffled().first()))
                }
                _suggestedDestinations.value = newDestinations
            }
        }
    }

    /**
     * This method is called by the [ToDestinationUserInput] Composable that is inside of the
     * [FlySearchContent] (which is called by the [SearchContent] Composable which is the
     * `backLayerContent` of the [BackdropScaffold] of [CraneHomeContent] when the [CraneScreen.Fly]
     * tab is selected) to filter the [DestinationsRepository.destinations] field of
     * [destinationsRepository] for [ExploreModel]'s which contain the [String] parameter
     * [newDestination] in their [City.nameToDisplay] property. We use the [CoroutineScope] tied to
     * this [ViewModel] to launch a new coroutine, and in that coroutine we use our
     * [CoroutineDispatcher] field [defaultDispatcher] (that is injected by Hilt) as the
     * [CoroutineContext] for a coroutine which sets the [List] of [ExploreModel] variable
     * `val newDestinations` to a [List] of [ExploreModel] created using the [List.filter] method of
     * the [DestinationsRepository.destinations] field of [destinationsRepository] to filter for only
     * those [ExploreModel]'s whose [City.nameToDisplay] property contains our [String] parameter
     * [newDestination]. When that coroutine finishes we set  the value of our [List] of [ExploreModel]
     * field [_suggestedDestinations] to `newDestinations` causing any Composable using a variable
     * observed from [suggestedDestinations] as a [State] to recompose.
     *
     * @param newDestination the [String] entered by the user that we will use to filter for
     * [ExploreModel]'s whose [City.nameToDisplay] property contains [newDestination].
     */
    fun toDestinationChanged(newDestination: String) {
        viewModelScope.launch {
            val newDestinations = withContext(defaultDispatcher) {
                destinationsRepository.destinations
                    .filter { it.city.nameToDisplay.contains(newDestination) }
            }
            _suggestedDestinations.value = newDestinations
        }
    }
}
