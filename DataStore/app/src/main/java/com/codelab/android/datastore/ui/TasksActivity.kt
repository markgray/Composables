package com.codelab.android.datastore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.codelab.android.datastore.UserPreferences
import com.codelab.android.datastore.data.TasksRepository
import com.codelab.android.datastore.data.UserPreferencesRepository

/**
 * The main activity of our app.
 */
class TasksActivity : AppCompatActivity() {
    /**
     * The view binding created for our layout file layout/activity_tasks.xml, it consists of a
     * `ConstraintLayout` root view holding a `RecyclerView` in which we display our [List] of
     * [Task] at the top followed by a `SwitchMaterial` that toggles "Show completed tasks" followed
     * by a `LinearLayout` holding a `ChipGroup` holding two `Chip`'s, one for toggling sort by
     * "Priority" and one for toggling sort by "Deadline".
     */
    private lateinit var binding: ActivityTasksBinding

    /**
     * The [TasksAdapter] that feeds views to our `RecyclerView`.
     */
    private val adapter = TasksAdapter()

    /**
     * The [TasksViewModel] we use to connect our [TasksRepository] and [UserPreferencesRepository]
     * with our UI.
     */
    private lateinit var viewModel: TasksViewModel

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we initialize our [ActivityTasksBinding] field [binding] to the binding returned by the
     * [ActivityTasksBinding.inflate] method when it uses the the [LayoutInflater] instance that
     * this `Window` retrieved from its [Context] to inflate the file "layout/activity_tasks.xml".
     * We then initialize our [ConstraintLayout] variable `val view` to the outermost [View] in the
     * layout file associated with our [ActivityTasksBinding] field [binding] and set our content
     * view to `view`. We intialize our [TasksViewModel] field [viewModel] by using an instance of
     * [ViewModelProvider] to create it (or return the existing singleton if it already exists) with
     * an instance of [TasksViewModelFactory] serving as the factory which will pass the constructor
     * of [TasksViewModel] our [TasksRepository] singleton object and an [UserPreferencesRepository]
     * constructed to use the [Context] extension property [userPreferencesStore] as its [DataStore]
     * of [UserPreferences].
     *
     * Next we call our method [setupRecyclerView] to have it add dividers between [RecyclerView]'s
     * row items and to set its adapter to our [TasksAdapter] field [adapter]. Finally we add an
     * observer to the [LiveData] wrapped [UserPreferences] field [TasksViewModel.initialSetupEvent]
     * of [viewModel] which will pass the first [UserPreferences] emitted from the [DataStore] to
     * a lambda which calls our [updateTaskFilters] method with the [UserPreferences.sortOrder_]
     * and [UserPreferences.showCompleted_] preferences to update the state of the widgets that
     * display and toggle these preferences. It then calls our [observePreferenceChanges] method
     * to add an observer to the [TasksViewModel.tasksUiModel] field of [viewModel] which will
     * submit the new list of `Tasks` to [adapter] when it changes and call [updateTaskFilters] to
     * update the state of the widgets that display and toggle our preferences values to reflect
     * the current [TasksUiModel.sortOrder] and [TasksUiModel.showCompleted] that was used to sort
     * the list.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksBinding.inflate(layoutInflater)
        val view: ConstraintLayout = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(
            this,
            TasksViewModelFactory(
                TasksRepository,
                UserPreferencesRepository(userPreferencesStore)
            )
        )[TasksViewModel::class.java]

        setupRecyclerView()

        viewModel.initialSetupEvent.observe(this) { initialSetupEvent: UserPreferences ->
            updateTaskFilters(initialSetupEvent.sortOrder, initialSetupEvent.showCompleted)
            setupOnCheckedChangeListeners()
            observePreferenceChanges()
        }
    }

    /**
     * Called to add an observer to the field [TasksViewModel.tasksUiModel] of [viewModel] which is
     * a [LiveData] wrapped [TasksUiModel] field that is converted to a [LiveData] from the [Flow]
     * of [TasksUiModel] created by combining the two [Flow]'s for the list of tasks returned from
     * our [TasksRepository] and the [UserPreferences] flow of the [DataStore] of [UserPreferences]
     * that our [UserPreferencesRepository] generates. The lambda of the observer submits the new
     * list of `Tasks` in the [TasksUiModel.tasks] field to [adapter] when it changes and then calls
     * [updateTaskFilters] to update the state of the widgets that display and toggle our preferences
     * values to reflect the current [TasksUiModel.sortOrder] and [TasksUiModel.showCompleted] that
     * was used to sort the list.
     */
    private fun observePreferenceChanges() {
        viewModel.tasksUiModel.observe(this) { tasksUiModel ->
            adapter.submitList(tasksUiModel.tasks)
            updateTaskFilters(tasksUiModel.sortOrder, tasksUiModel.showCompleted)
        }
    }

    /**
     * Called to add dividers between our [RecyclerView]'s row items and to set its adapter to our
     * [TasksAdapter] field [adapter].
     */
    private fun setupRecyclerView() {
        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        binding.list.adapter = adapter
    }

    /**
     * Called to set the `OnCheckedChangeListener`'s of the three widgets in our UI that are used to
     * to select the values of the preferences which are used when generating the list of tasks that
     * are displayed in our [RecyclerView]. The lambda of the [ActivityTasksBinding.sortDeadline]
     * chip `OnCheckedChangeListener` calls the [TasksViewModel.enableSortByDeadline] method with
     * the `checked` state of the chip, the lambda of the [ActivityTasksBinding.sortPriority]
     * chip `OnCheckedChangeListener` calls the [TasksViewModel.enableSortByPriority] method with
     * the `checked` state of the chip, and the lambda of the [ActivityTasksBinding.showCompletedSwitch]
     * `SwitchMaterial` calls the [TasksViewModel.showCompletedTasks] method with the `checked` state
     * of the `SwitchMaterial`.
     */
    private fun setupOnCheckedChangeListeners() {
        binding.sortDeadline.setOnCheckedChangeListener { _, checked ->
            viewModel.enableSortByDeadline(checked)
        }
        binding.sortPriority.setOnCheckedChangeListener { _, checked ->
            viewModel.enableSortByPriority(checked)
        }
        binding.showCompletedSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.showCompletedTasks(checked)
        }
    }

    /**
     * Called to update the state of the widgets that display and toggle our preferences values to
     * reflect our [SortOrder] parameter [sortOrder], and our [Boolean] parameter [showCompleted].
     * We use the [with] function to execute a function block with [binding] as its receiver which
     * sets the checked state of the [ActivityTasksBinding.showCompleted] `SwitchMaterial` to the
     * value of our [Boolean] parameter [showCompleted], sets the checked state of the
     * [ActivityTasksBinding.sortDeadline] `Chip` to "On" if our [SortOrder] parameter [sortOrder]
     * is [SortOrder.BY_DEADLINE] or [SortOrder.BY_DEADLINE_AND_PRIORITY] and set the checked state
     * of [ActivityTasksBinding.sortPriority] `Chip` to "On" if our [SortOrder] parameter [sortOrder]
     * is [SortOrder.BY_PRIORITY] or [SortOrder.BY_DEADLINE_AND_PRIORITY].
     *
     * @param sortOrder the [SortOrder] that is currently being used to sort our tasks
     * @param showCompleted if `true` the completed tasks are displayed, if `false` they are omitted
     */
    private fun updateTaskFilters(sortOrder: UserPreferences.SortOrder, showCompleted: Boolean) {
        with(binding) {
            showCompletedSwitch.isChecked = showCompleted
            sortDeadline.isChecked =
                sortOrder == SortOrder.BY_DEADLINE || sortOrder == SortOrder.BY_DEADLINE_AND_PRIORITY
            sortPriority.isChecked =
                sortOrder == SortOrder.BY_PRIORITY || sortOrder == SortOrder.BY_DEADLINE_AND_PRIORITY
        }
    }
}