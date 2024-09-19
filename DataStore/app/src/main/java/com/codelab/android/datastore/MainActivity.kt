package com.codelab.android.datastore

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.codelab.android.datastore.UserPreferences.SortOrder
import com.codelab.android.datastore.data.Task
import com.codelab.android.datastore.data.TasksRepository
import com.codelab.android.datastore.data.UserPreferencesRepository
import com.codelab.android.datastore.data.UserPreferencesSerializer
import com.codelab.android.datastore.ui.MainScreen
import com.codelab.android.datastore.ui.StartUpScreen
import com.codelab.android.datastore.ui.TasksUiModel
import com.codelab.android.datastore.ui.TasksViewModel
import com.codelab.android.datastore.ui.TasksViewModelFactory
import com.codelab.android.datastore.ui.theme.DataStoreTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * The name of the [SharedPreferences] file currently used (if any) whose contents we want to migrate
 * to Proto [DataStore].
 */
private const val USER_PREFERENCES_NAME = "user_preferences"

/**
 * The filename relative to `Context.applicationContext.filesDir` that [DataStore] acts on. The
 * File is obtained from dataStoreFile. It is created in the "/datastore" subdirectory.
 */
private const val DATA_STORE_FILE_NAME = "user_prefs.pb"

/**
 * The key used to save the [SortOrder] in the old [SharedPreferences] file.
 */
private const val SORT_ORDER_KEY = "sort_order"

/**
 * Build the [DataStore] and save a reference to it as an extension property of [Context]. We use
 * [DATA_STORE_FILE_NAME] as the filename relative to `Context.applicationContext.filesDir` that
 * [DataStore] acts on. The File is obtained from `dataStoreFile`. It is created in the "/datastore"
 * subdirectory. We use our [Serializer] of [UserPreferences] singleton [UserPreferencesSerializer]
 * as the `serializer`. For the `produceMigrations` argument to [dataStore] we use a [List] of
 * [DataMigration]'s holding a single instance of [SharedPreferencesMigration] whose `context`
 * argument is the [Context] passed the lambda of `produceMigrations`, whose `sharedPreferencesName`
 * argument is [USER_PREFERENCES_NAME] (the old shared preferences file name). The lambda argument
 * of [SharedPreferencesMigration] checks if the `sortOrder` property of its [UserPreferences]
 * argument `currentData` is [SortOrder.UNSPECIFIED] in which case it builds a [UserPreferences]
 * from the string stored under the key [SORT_ORDER_KEY] in its [SharedPreferencesView] argument
 * `sharedPrefs` which it then returns and if the `sortOrder` property is of its [UserPreferences]
 * argument `currentData` is not [SortOrder.UNSPECIFIED] it returns `currentData` as the migrated
 * data of the [SharedPreferencesMigration].
 */
private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserPreferencesSerializer,
    produceMigrations = { context: Context ->
        listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = USER_PREFERENCES_NAME
            ) { sharedPrefs: SharedPreferencesView, currentData: UserPreferences ->
                // Define the mapping from SharedPreferences to UserPreferences
                if (currentData.sortOrder == SortOrder.UNSPECIFIED) {
                    currentData.toBuilder().setSortOrder(
                        SortOrder.valueOf(
                            sharedPrefs.getString(SORT_ORDER_KEY, SortOrder.NONE.name)!!
                        )
                    ).build()
                } else {
                    currentData
                }
            }
        )
    }
)
/**
 * This is the main Activity of our app.
 */
class MainActivity : ComponentActivity() {
    /**
     * The [TasksViewModel] we use to connect our [TasksRepository] and [UserPreferencesRepository]
     * with our UI.
     */
    private lateinit var viewModel: TasksViewModel

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`.
     * Then we initialize our [TasksViewModel] field [viewModel] to the singleton instance that is
     * contructed using our [TasksViewModelFactory] from the [TasksRepository] singleton and the
     * [UserPreferencesRepository] constructed to use our apps [DataStore] of [UserPreferences]
     * extension property [userPreferencesStore]. Next we initialize our [TasksUiModel] variable
     * `var initialTasksUiModel` to an empty instance, then add an observer to the [LiveData] wrapped
     * [UserPreferences] property [TasksViewModel.initialSetupEvent] which will update the contents
     * of `initialTasksUiModel` when a new value is available.
     *
     * With the initial setup taken care of we call the [setContent] method to have it compose its
     * Composable `content` into the activity. The content will become the root view of the activity.
     * In the `content` we initialize and remember our [TasksUiModel] variable `var tasksUiModel`
     * with its initial value `initialTasksUiModel`. Then we wrap our UI in [DataStoreTheme], where
     * we add an observer to the [TasksViewModel.tasksUiModel] field of [viewModel] which updates
     * `tasksUiModel` to the new value when the field changes value. Our root Composable is a
     * [Surface] whose `modifier` argument is a [Modifier.fillMaxSize] that causes it to occupy the
     * entire incoming constraints and its `color` argument is the [Colors.background] color of the
     * [MaterialTheme.colors] (which is the default [Color.White] for light theme or `Color(0xFF121212)`
     * for dark theme since [DataStoreTheme] does not override them). Inside the [Surface] we
     * initialize our [Flow] of [List] of [Task] variable `val taskListFlow` using the [flow] function
     * with the block argument of [flow] calling `emit` with its `value` argument the
     * [TasksUiModel.tasks] field of our `tasksUiModel` variable. Next we initialize and remember our
     * [Boolean] variable `var showStartupScreen` using a [MutableState] whose initial value is `true`.
     * Then if `showStartupScreen` is `true` we compose a [StartUpScreen] whose `onTimeout` argument
     * is a lambda which sets `showStartupScreen` to `false` ([StartUpScreen] will call this lambda
     * after 2000 milliseconds causing us to be recomposed and the `else` branch of the `if` statement
     * will be taken instead). If `showStartupScreen` is `false` we compose a [MainScreen] whose
     * `viewModel` argument is our [viewModel] parameter, whose `tasksUiModel` argument is our
     * `tasksUiModel` variable and whose `tasks` argument is our `taskListFlow` variable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so we ignore this.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(
            this,
            TasksViewModelFactory(
                repository = TasksRepository,
                userPreferencesRepository = UserPreferencesRepository(userPreferencesStore)
            )
        )[TasksViewModel::class.java]

        var initialTasksUiModel = TasksUiModel(
            tasks = listOf(),
            showCompleted = false,
            sortOrder = SortOrder.UNSPECIFIED
        )

        viewModel.initialSetupEvent.observe(this) { initialSetupEvent: UserPreferences ->
            initialTasksUiModel = TasksUiModel(
                tasks = listOf(),
                showCompleted = initialSetupEvent.showCompleted,
                sortOrder = initialSetupEvent.sortOrder
            )
        }
        setContent {
            var tasksUiModel: TasksUiModel by remember {
                mutableStateOf(initialTasksUiModel)
            }
            DataStoreTheme {
                viewModel.tasksUiModel.observe(this) { newtasksUiModel: TasksUiModel ->
                    tasksUiModel = newtasksUiModel
                }
                // A surface container using the 'background' color from the theme
                Box(modifier = Modifier.safeDrawingPadding()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        val taskListFlow: Flow<List<Task>> = flow {
                            emit(value = tasksUiModel.tasks)
                        }
                        var showStartupScreen: Boolean by remember { mutableStateOf(true) }
                        if (showStartupScreen) {
                            StartUpScreen(onTimeout = { showStartupScreen = false })
                        } else {
                            MainScreen(
                                viewModel = viewModel,
                                tasksUiModel = tasksUiModel,
                                tasks = taskListFlow
                            )
                        }

                    }
                }
            }
        }
    }
}
