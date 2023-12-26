package com.example.aboutmecompose.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * These are the [Shapes] used by our [AboutMeComposeTheme] custom [MaterialTheme] (the set of
 * corner shapes to be used as this hierarchy's shape system).
 *  - [Shapes.extraSmall] A shape style with 4 same-sized corners whose size are bigger than
 *  RectangleShape and smaller than [Shapes.small]. By default autocomplete menu, select menu,
 *  snackbars, standard menu, and text fields use this shape.
 *  - [Shapes.small] A shape style with 4 same-sized corners whose size are bigger than
 *  [Shapes.extraSmall] and smaller than [Shapes.medium]. By default chips use this shape.
 *  - [Shapes.medium] A shape style with 4 same-sized corners whose size are bigger than
 *  [Shapes.small] and smaller than [Shapes.large]. By default cards and small FABs use this shape.
 *  - [Shapes.large] A shape style with 4 same-sized corners whose size are bigger than [Shapes.medium]
 *  and smaller than [Shapes.extraLarge]. By default extended FABs, FABs, and navigation drawers use
 *  this shape.
 *  - [Shapes.extraLarge] A shape style with 4 same-sized corners whose size are bigger than
 *  [Shapes.large] and smaller than [CircleShape]. By default large FABs use this shape.
 */
val shapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
