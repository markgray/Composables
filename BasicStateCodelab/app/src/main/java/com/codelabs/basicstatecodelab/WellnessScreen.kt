package com.codelabs.basicstatecodelab

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codelabs.basicstatecodelab.ui.theme.BasicStateCodelabTheme

/**
 * This is the top level Composable of our app. Wrapped in a [Surface] container using the
 * 'background' color from the [BasicStateCodelabTheme] ([Color.White]) that the [Surface] is
 * wrapped in it used as the `content` that a call to [setContent] composes into the activity in
 * the `onCreate` override of [MainActivity]. The root Composable is a [Column] whose `modifier`
 * argument is our [modifier] parameter. The [Column] holds a [StatefulCounter] above a
 * [WellnessTasksList] whose `list` argument is the [WellnessViewModel.tasks] list of [WellnessTask]
 * in our [wellnessViewModel] parameter, whose `onCheckedTask` argument is a lambda that calls the
 * method [WellnessViewModel.changeTaskChecked] of our [wellnessViewModel] parameter with the
 * [WellnessTask] and its new `checked` [Boolean] value, and whose `onCloseTask` argument is a
 * lambda which calls the method [WellnessViewModel.remove] of our [wellnessViewModel] parameter
 * with the [WellnessTask] which is to be removed.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and
 * behavior. We are not called with a value so the empty, default, or starter [Modifier] that
 * contains no elements that is specified by the default value of the parameter is used.
 * @param wellnessViewModel the [WellnessViewModel] that holds the business logic and date of our
 * [WellnessTasksList]. Our caller does not specify a value so the [WellnessViewModel] returned as
 * the parameter's default value by [viewModel] is used instead. (This is a clever way to correctly
 * initialize a [ViewModel] field in my opinion.)
 */
@Composable
fun WellnessScreen(
    modifier: Modifier = Modifier,
    wellnessViewModel: WellnessViewModel = viewModel()
) {
    Column(modifier = modifier) {
        StatefulCounter()

        WellnessTasksList(
            list = wellnessViewModel.tasks,
            onCheckedTask = { task: WellnessTask, checked: Boolean ->
                wellnessViewModel.changeTaskChecked(task, checked)
            },
            onCloseTask = { task: WellnessTask ->
                wellnessViewModel.remove(task)
            }
        )
    }
}

