/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.baselineprofiles_codelab.ui.home.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.OrderLine
import com.example.baselineprofiles_codelab.model.Snack
import com.example.baselineprofiles_codelab.model.SnackRepo
import com.example.baselineprofiles_codelab.model.SnackbarManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * [CartViewModel]
 *
 * ViewModel responsible for managing the state of the shopping cart.
 * It handles adding, removing, and updating the quantity of snacks in the cart.
 * It also simulates occasional network errors for testing purposes.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 *
 * @property snackbarManager Manages the display of snackbar messages.
 * @property snackRepository Repository for accessing and managing snack data.
 */
class CartViewModel(
    private val snackbarManager: SnackbarManager,
    snackRepository: SnackRepo
) : ViewModel() {

    /**
     * [MutableStateFlow] of [List] of [OrderLine] representing the list of ordered items in the
     * shopping cart. It is observed by the UI layer to trigger state updates using our publicly
     * accessible [orderLines] flow. It is initialized with the current state of the cart from
     * [SnackRepo] property [snackRepository].
     */
    private val _orderLines: MutableStateFlow<List<OrderLine>> =
        MutableStateFlow(value = snackRepository.getCart())

    /**
     * Publicly accessible flow of [List] of [OrderLine] representing the current state of the
     * shopping cart that [_orderLines] flow provides. It is observed by the UI layer to trigger
     * state updates.
     */
    val orderLines: StateFlow<List<OrderLine>> get() = _orderLines

    /**
     * Counter that [shouldRandomlyFail] uses to simulate an error every 5 calls.
     */
    private var requestCount = 0

    /**
     * When `true` simulates an error (every 5 requests).
     */
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0

    /**
     * Increases the count of a specific snack in the order.
     *
     * This function attempts to increase the count of the snack identified by [snackId] in the
     * current order. It first checks if a random failure should occur using [shouldRandomlyFail].
     *  - If [shouldRandomlyFail] returns `false`, it searches for the [OrderLine] in our
     *  [MutableStateFlow] wrapped [List] of [OrderLine] property [_orderLines] whose
     *  [OrderLine.snack] has a [Snack.id] that matches our [Long] parameter [snackId] and
     *  initializes the [Int] variable `currentCount` with the value of its [OrderLine.count].
     *  Then it calls `updateSnackCount()` with [snackId] as its `snackId` argument and
     *  `currentCount + 1` as its `count` argument.
     *  - If [shouldRandomlyFail] returns `true`, it simulates an error and displays an error
     *  message to the user using the [SnackbarManager.showMessage] method of our [SnackbarManager]
     *  property [snackbarManager].
     *
     * @param snackId The ID of the snack to increase the count of.
     */
    fun increaseSnackCount(snackId: Long) {
        if (!shouldRandomlyFail()) {
            val currentCount: Int = _orderLines.value.first { order: OrderLine ->
                order.snack.id == snackId 
            }.count
            updateSnackCount(snackId = snackId, count = currentCount + 1)
        } else {
            snackbarManager.showMessage(messageTextId = R.string.cart_increase_error)
        }
    }

    /**
     * Decreases the count of a specific snack in the order lines.
     *
     * If the snack's current count is 1, it will be removed from the order.
     * If the snack's current count is greater than 1, its count will be decremented by 1.
     *
     * If [shouldRandomlyFail] returns true, it will simulate a failure and display an error
     * message to the user using the [SnackbarManager.showMessage] method of our [SnackbarManager]
     * property [snackbarManager].
     *
     * If [shouldRandomlyFail] returns `false`, it searches for the [OrderLine] in our
     * [MutableStateFlow] wrapped [List] of [OrderLine] property [_orderLines] whose
     * [OrderLine.snack] has a [Snack.id] that matches our [Long] parameter [snackId] and
     * initializes the [Int] variable `currentCount` with the value of its [OrderLine.count].
     * If `currentCount` is 1, it will call [removeSnack] with [snackId] as its argument.
     * Otherwise, it will call [updateSnackCount] with [snackId] as its `snackId` argument
     * and `currentCount - 1` as its `count` argument.
     *
     * @param snackId The ID of the snack to decrease the count of.
     * @throws NoSuchElementException if no order line with the given snackId exists.
     */
    fun decreaseSnackCount(snackId: Long) {
        if (!shouldRandomlyFail()) {
            val currentCount: Int = _orderLines.value.first { order: OrderLine ->
                order.snack.id == snackId
            }.count
            if (currentCount == 1) {
                // remove snack from cart
                removeSnack(snackId = snackId)
            } else {
                // update quantity in cart
                updateSnackCount(snackId = snackId, count = currentCount - 1)
            }
        } else {
            snackbarManager.showMessage(messageTextId = R.string.cart_decrease_error)
        }
    }

    /**
     * Removes a snack from the current order lines based on the provided snack ID.
     *
     * This function filters the [MutableStateFlow] wrapped [List] of [OrderLine] property
     * [_orderLines] removing any [OrderLine] whose [OrderLine.snack] has a [Snack.id] that
     * matches our [Long] parameter [snackId]. It then updates the [MutableStateFlow] wrapped
     * [List] of [OrderLine] property [_orderLines] with the filtered list.
     *
     * @param snackId The ID of the snack to be removed from the order lines.
     */
    fun removeSnack(snackId: Long) {
        _orderLines.value = _orderLines.value.filter { order: OrderLine ->
            order.snack.id != snackId
        }
    }

    /**
     * Updates the count of a specific snack in the order.
     *
     * This function iterates through the [OrderLine] objects in our [MutableStateFlow] of [List] of
     * [OrderLine] property [_orderLines] and finds the one whose [OrderLine.snack] has an [Snack.id]
     * equal to our [Long] parameter [snackId]. If a match is found, it creates a new [OrderLine]
     * with the updated [OrderLine.count] while keeping other properties the same. If no match is
     * found (i.e., the snack is not in the order), the list remains unchanged.
     *
     * The updated list of [OrderLine] objects is then emitted through the [_orderLines]
     * MutableStateFlow, triggering updates for any observers.
     *
     * @param snackId The ID of the snack to update.
     * @param count The new count for the specified snack.
     * @throws IllegalArgumentException if count is less than 0.
     */
    private fun updateSnackCount(snackId: Long, count: Int) {
        _orderLines.value = _orderLines.value.map { order: OrderLine ->
            if (order.snack.id == snackId) {
                order.copy(count = count)
            } else {
                order
            }
        }
    }

    /**
     * Factory for CartViewModel that takes [SnackbarManager] and [SnackRepo] as dependencies.
     */
    companion object {
        fun provideFactory(
            snackbarManager: SnackbarManager = SnackbarManager,
            snackRepository: SnackRepo = SnackRepo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(snackbarManager, snackRepository) as T
            }
        }
    }
}
