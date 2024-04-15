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

package com.example.jetnews.utils

/**
 * This extension function of a [Set] "toggles" the presence of an [element] in a [Set], ie. if
 * using new [MutableSet] created from our [Set] receiver using [Set.toMutableSet] we find that its
 * [MutableSet.add] method returns `false` (the element is already contained in the set) we call the
 * [MutableSet.remove] method to remove it. Having toggled the presence of an [element] in the
 * [MutableSet] we use the [MutableSet.toSet] method to convert the [MutableSet] to a [Set] and
 * return it to the caller.
 *
 * @param element the [E] whose presence in our receiver [Set] we wish to "toggle".
 * @return a copy of our receiver [Set] with our [E] parameter [element] added if it is not already
 * in the [Set] or removed if it is.
 */
internal fun <E> Set<E>.addOrRemove(element: E): Set<E> {
    return this.toMutableSet().apply {
        if (!add(element)) {
            remove(element)
        }
    }.toSet()
}
