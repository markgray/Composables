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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BackdropScaffold
import androidx.compose.runtime.State
import androidx.compose.samples.crane.base.ExploreSection
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.di.DefaultDispatcher
import androidx.compose.samples.crane.di.DispatchersModule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import javax.inject.Inject
import kotlin.random.Random

/**
 * The maximum number of people that can travel
 */
const val MAX_PEOPLE = 4

/**
 * This is the [ViewModel] used by [MainActivity]. It is injected as the default value of the
 * `viewModel` parameter of [CraneHomeContent] using the [viewModel] method. The `HiltViewModel`
 * annotation identifies a [ViewModel] for construction injection. The [ViewModel] annotated with
 * HiltViewModel will be available for creation by the [HiltViewModelFactory] and can be retrieved
 * by default in an `Activity` or `Fragment` annotated with `AndroidEntryPoint`. The `HiltViewModel`
 * containing a constructor annotated with `Inject` will have its dependencies defined in the
 * constructor parameters injected by Dagger's Hilt. The generated java class
 * `MainViewModel_Factory.java` is used to provide instances of [MainViewModel].
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
     * This private [MutableStateFlow] of a [List] of [ExploreModel] objects is read using our publicly
     * accessible field [suggestedDestinations] to be used as the dataset for the `frontLayerContent`
     * of the [BackdropScaffold] of [CraneHomeContent] when the [CraneScreen.Fly] tab of
     * [CraneHomeContent] is selected (file home/CraneHome.kt). It is wrapped in a [MutableStateFlow]
     * so that it will `emit` its [List] to every Composable which uses the `collectAsState` extension
     * function on [suggestedDestinations] whenever the `value` of [_suggestedDestinations] changes,
     * and the use of the collected [State] by a Composable will cause it to be recomposed. It is set
     * to a new value by our [toDestinationChanged] method and our [updatePeople] method.
     */
    private val _suggestedDestinations = MutableStateFlow<List<ExploreModel>>(emptyList())

    /**
     * Public read-only access to our [MutableStateFlow] of [List] of [ExploreModel] field
     * [_suggestedDestinations]. [CraneHomeContent] uses the `collectAsState` extension function
     * on this property to initialize its [List] of [ExploreModel] variable `suggestedDestinations`,
     * which is then used as the dataset for the `frontLayerContent` of the [BackdropScaffold] of
     * [CraneHomeContent] when the [CraneScreen.Fly] tab of [CraneHomeContent] is selected (file
     * home/CraneHome.kt). A when statement chooses which of three [ExploreSection] Composables
     * calls is executed, and [ExploreSection] then has its `ExploreList` Composable display it in
     * its [LazyColumn].
     */
    val suggestedDestinations: StateFlow<List<ExploreModel>>
        get() = _suggestedDestinations

    init {
        _suggestedDestinations.value = destinationsRepository.destinations
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
     * causing any Composable using a variable collected from [suggestedDestinations] as a [State] to
     * recompose.
     *
     * @param people the new number of people traveling.
     */
    fun updatePeople(people: Int) {
        viewModelScope.launch {
            if (people > MAX_PEOPLE) {
                _suggestedDestinations.value = emptyList()
            } else {
                val newDestinations: List<ExploreModel> = withContext(defaultDispatcher) {
                    destinationsRepository.destinations
                        .shuffled(random = Random(people * (1..100).shuffled().first()))
                }
                _suggestedDestinations.value = newDestinations
            }
        }
    }

    /**
     * This method is called by the [ToDestinationUserInput] Composable that is inside of the
     * [FlySearchContent] (which is called by the [SearchContent] Composable which is the `backLayerContent`
     * of the [BackdropScaffold] of [CraneHomeContent] when the [CraneScreen.Fly] tab is selected) to
     * filter the [DestinationsRepository.destinations] field of [destinationsRepository] for [ExploreModel]'s
     * which contain the [String] parameter [newDestination] in their [City.nameToDisplay] property.
     * We use the [CoroutineScope] tied to this [ViewModel] to launch a new coroutine, and in that
     * coroutine we use our [CoroutineDispatcher] field [defaultDispatcher] (that is injected by Hilt)
     * as the [CoroutineContext] for a coroutine which sets the [List] of [ExploreModel] variable
     * `val newDestinations` to a [List] of [ExploreModel] created using the [List.filter] method of
     * the [DestinationsRepository.destinations] field of [destinationsRepository] to filter for only
     * those [ExploreModel]'s whose [City.nameToDisplay] property contains our [String] parameter
     * [newDestination]. When that coroutine finishes we set  the value of our [List] of [ExploreModel]
     * field [_suggestedDestinations] to `newDestinations` causing any Composable using a variable
     * collected from [suggestedDestinations] as a [State] to recompose.
     *
     * @param newDestination the [String] entered by the user that we will use to filter for
     * [ExploreModel]'s whose [City.nameToDisplay] property contains [newDestination].
     */
    fun toDestinationChanged(newDestination: String) {
        viewModelScope.launch {
            val newDestinations: List<ExploreModel> = withContext(defaultDispatcher) {
                destinationsRepository.destinations
                    .filter { it.city.nameToDisplay.contains(newDestination) }
            }
            _suggestedDestinations.value = newDestinations
        }
    }
}
