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

package com.example.jetlagged

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.jetlagged.ui.theme.LegendHeadingStyle

/**
 * Draws a graph of its [SleepDayData] parameter [sleepData] that can be expanded to display the
 * details of the different "types" of sleep contained in the [SleepDayData].
 *
 * This Composable is used by [JetLaggedTimeGraph] in the lambda that it uses as the `bar` argument
 * of its call to [TimeGraph]. That lambda calls us with the [SleepDayData] whose index in the [List]
 * of [SleepDayData] dataset [SleepGraphData.sleepDayData] is the [Int] that the lambda is called
 * by [TimeGraph] in a [repeat] loop to supply it with [SleepBar] Composables for all the [SleepDayData].
 *
 * We start by initializing and remembering our [MutableState] wrapped [Boolean] variable `var isExpanded`
 * to `false`. We initialize our [Transition] of [Boolean] variable `val transition` to an instance
 * whose `targetState` is `isExpanded` and whose `label` is "expanded".
 *
 * Our root Composable is a [Column] whose `modifier` argument chains a [Modifier.clickable] to our
 * [Modifier] parameter [modifier] whose `indication` argument is `null` (indication to be shown when
 * modified element is pressed, `null` to show no indication), and whose `interactionSource` is a
 * remembered [MutableInteractionSource] instance (represents a stream of [Interaction]'s present for
 * this component, allowing listening to [Interaction] changes), and its `onClick` lambda argument is
 * a lambda that sets [MutableState] wrapped [Boolean] variable `isExpanded` to its inverse.
 *
 * The `content` of the [Column] holds a [SleepRoundedBar] whose `sleepData` argument is our [SleepDayData]
 * parameter [sleepData], and whose `transition` argument is our [Transition] of [Boolean] variable
 * `transition`. Below this we use the [Transition.AnimatedVisibility] method of `transition` to
 * animate as the `content` our [DetailLegend] Composable (for each of the [SleepType] enums it displays
 * a [LegendItem] that displays a [CircleShape] that is the [Color] of the [SleepType.color] and a
 * [Text] the [String] whose resource ID is the [SleepType.title] of the [SleepType]). The `enter`
 * [EnterTransition] argument of the [Transition.AnimatedVisibility] is a [fadeIn] plus a
 * [expandVertically], the `exit` [ExitTransition] argument is a [fadeOut] plus a [shrinkVertically].
 * The `visible` argument is the current value of the [Boolean] that the [Transition] of [Boolean]
 * `transition` uses as its `targetState` (our [MutableState] wrapped [Boolean] variable `isExpanded`)
 * this defines whether the content should be visible.
 *
 * @param sleepData the [SleepDayData] that we should display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [JetLaggedTimeGraph] passes us a [Modifier.padding] that adds 8.dp to our
 * `bottom` edge with a [TimeGraphScope.timeGraphBar] chained to that whose `start` argument is the
 * [SleepDayData.firstSleepStart] of the [SleepDayData] that the `bar` lambda argument of [TimeGraph]
 * is calling us for, whose `end` argument is its [SleepDayData.lastSleepEnd], and whose `hours`
 * argument is the [List] of hours inclusive between the [SleepGraphData.earliestStartHour] of
 * the [SleepGraphData] dummy dataset and 23 and the [List] of hours inclusive between 0 and its
 * [SleepGraphData.latestEndHour]. (The [TimeGraphScope.timeGraphBar] appears to be used to offset
 * the [SleepBar] to align it with the [HoursHeader] at the top of the [JetLaggedTimeGraph]).
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SleepBar(
    sleepData: SleepDayData,
    modifier: Modifier = Modifier,
) {
    var isExpanded: Boolean by rememberSaveable {
        mutableStateOf(value = false)
    }

    val transition: Transition<Boolean> = updateTransition(targetState = isExpanded, label = "expanded")

    Column(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isExpanded = !isExpanded
            }
    ) {
        SleepRoundedBar(
            sleepData = sleepData,
            transition = transition
        )

        transition.AnimatedVisibility(
            enter = fadeIn(animationSpec = tween(animationDuration)) + expandVertically(
                animationSpec = tween(animationDuration)
            ),
            exit = fadeOut(animationSpec = tween(animationDuration)) + shrinkVertically(
                animationSpec = tween(animationDuration)
            ),
            content = {
                DetailLegend()
            },
            visible = { it }
        )
    }
}

/**
 * This does the rendering of the "SleepBar" for its [SleepDayData] parameter [sleepData] using the
 * [Modifier.drawWithCache] modifier of its [Spacer] root composable. The [Modifier.drawWithCache]
 * modifier contains code for the calculation of the parameters that are passed to [drawSleepBar]
 * in a call to `CacheDrawScope.onDrawBehind` which then does all of the actual drawing.
 *
 * @param sleepData the [SleepDayData] whose "SleepBar" we are to render.
 * @param transition the [Transition] of [Boolean] that is used to animate the "SleepBar" between
 * "expanded" and "not expanded" states when the user clicks on our "SleepBar"
 */
@Composable
private fun SleepRoundedBar(
    sleepData: SleepDayData,
    transition: Transition<Boolean>,
) {
    /**
     * Used to create a [TextLayoutResult] of the [SleepDayData.sleepScoreEmoji] for [drawSleepBar]
     * to draw using [drawText]
     */
    val textMeasurer: TextMeasurer = rememberTextMeasurer()

    /**
     * The animated height used for the [Modifier.height] of our fancy [Spacer] which does all of
     * our drawing using [Modifier.drawWithCache]
     */
    val height: Dp by transition.animateDp(label = "height", transitionSpec = {
        spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    }) { targetExpanded ->
        if (targetExpanded) 100.dp else 24.dp
    }

    /**
     * The fraction of the [Transition] of [Boolean] parameter [transition] that has occurred
     */
    val animationProgress: Float by transition.animateFloat(
        label = "progress",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        }) { target: Boolean ->
        if (target) 1f else 0f
    }

    Spacer(
        modifier = Modifier
            .drawWithCache {
                val width: Float = this.size.width
                val cornerRadiusStartPx: Float = 2.dp.toPx()
                val collapsedCornerRadiusPx: Float = 10.dp.toPx()
                val animatedCornerRadius = CornerRadius(
                    lerp(cornerRadiusStartPx, collapsedCornerRadiusPx, (1 - animationProgress))
                )

                val lineThicknessPx: Float = lineThickness.toPx()
                val roundedRectPath = Path()
                roundedRectPath.addRoundRect(
                    RoundRect(
                        rect = Rect(
                            offset = Offset(x = 0f, y = -lineThicknessPx / 2f),
                            size = Size(
                                width = this.size.width + lineThicknessPx * 2,
                                height = this.size.height + lineThicknessPx
                            )
                        ),
                        cornerRadius = animatedCornerRadius
                    )
                )
                val roundedCornerStroke = Stroke(
                    width = lineThicknessPx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.cornerPathEffect(
                        radius = cornerRadiusStartPx * animationProgress
                    )
                )
                val barHeightPx: Float = barHeight.toPx()

                val sleepGraphPath: Path = generateSleepPath(
                    canvasSize = this.size,
                    sleepData = sleepData,
                    width = width,
                    barHeightPx = barHeightPx,
                    heightAnimation = animationProgress,
                    lineThicknessPx = lineThickness.toPx() / 2f
                )
                val gradientBrush: Brush =
                    Brush.verticalGradient(
                        colorStops = sleepGradientBarColorStops.toTypedArray(),
                        startY = 0f,
                        endY = SleepType.entries.size * barHeightPx
                    )
                val textResult: TextLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(text = sleepData.sleepScoreEmoji)
                )

                onDrawBehind {
                    drawSleepBar(
                        roundedRectPath = roundedRectPath,
                        sleepGraphPath = sleepGraphPath,
                        gradientBrush = gradientBrush,
                        roundedCornerStroke = roundedCornerStroke,
                        animationProgress = animationProgress,
                        textResult = textResult,
                        cornerRadiusStartPx = cornerRadiusStartPx
                    )
                }
            }
            .height(height = height)
            .fillMaxWidth()
    )
}

/**
 *
 */
private fun DrawScope.drawSleepBar(
    roundedRectPath: Path,
    sleepGraphPath: Path,
    gradientBrush: Brush,
    roundedCornerStroke: Stroke,
    animationProgress: Float,
    textResult: TextLayoutResult,
    cornerRadiusStartPx: Float,
) {
    clipPath(path = roundedRectPath) {
        drawPath(path = sleepGraphPath, brush = gradientBrush)
        drawPath(
            path = sleepGraphPath,
            style = roundedCornerStroke,
            brush = gradientBrush
        )
    }

    translate(left = -animationProgress * (textResult.size.width + textPadding.toPx())) {
        drawText(
            textLayoutResult = textResult,
            topLeft = Offset(x = textPadding.toPx(), y = cornerRadiusStartPx)
        )
    }
}

/**
 * Generate the path for the different sleep periods.
 */
private fun generateSleepPath(
    canvasSize: Size,
    sleepData: SleepDayData,
    width: Float,
    barHeightPx: Float,
    heightAnimation: Float,
    lineThicknessPx: Float,
): Path {
    val path = Path()

    var previousPeriod: SleepPeriod? = null

    path.moveTo(x = 0f, y = 0f)

    sleepData.sleepPeriods.forEach { period ->
        val percentageOfTotal: Float = sleepData.fractionOfTotalTime(period)
        val periodWidth: Float = percentageOfTotal * width
        val startOffsetPercentage: Float = sleepData.minutesAfterSleepStart(period) /
            sleepData.totalTimeInBed.toMinutes().toFloat()
        val halfBarHeight: Float = canvasSize.height / SleepType.entries.size / 2f

        val offset: Float = if (previousPeriod == null) {
            0f
        } else {
            halfBarHeight
        }

        val offsetY: Float = lerp(
            start = 0f,
            stop = period.type.heightSleepType() * canvasSize.height,
            fraction = heightAnimation
        )
        // step 1 - draw a line from previous sleep period to current
        if (previousPeriod != null) {
            path.lineTo(
                x = startOffsetPercentage * width + lineThicknessPx,
                y = offsetY + offset
            )
        }

        // step 2 - add the current sleep period as rectangle to path
        path.addRect(
            rect = Rect(
                offset = Offset(x = startOffsetPercentage * width + lineThicknessPx, y = offsetY),
                size = canvasSize.copy(width = periodWidth, height = barHeightPx)
            )
        )
        // step 3 - move to the middle of the current sleep period
        path.moveTo(
            x = startOffsetPercentage * width + periodWidth + lineThicknessPx,
            y = offsetY + halfBarHeight
        )

        previousPeriod = period
    }
    return path
}

/**
 * Draws a legend explaining the color to type mapping of the different [SleepType] ranges displayed
 * when the [SleepBar] is expanded.
 */
@Preview
@Composable
private fun DetailLegend() {
    Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        SleepType.entries.forEach {
            LegendItem(sleepType = it)
        }
    }
}

/**
 * Draws a [Row] with a [Box] whose `modifier` argument is a [Modifier.size] that sets its size to
 * 10.dp, with a [Modifier.clip] that clips its `shape` to [CircleShape], and a [Modifier.background]
 * that sets its [Color] to the [SleepType.color] of our [SleepType] parameter [sleepType]. This is
 * followed by a [Text] that displays as its `text` the [String] whose resource ID is the
 * [SleepType.title] of our [SleepType] parameter [sleepType], whose `style` [TextStyle] is our
 * [LegendHeadingStyle] (the downloadable [GoogleFont] "Lato" with a `fontSize` of 10.sp and a
 * [FontWeight] of 600), and whose `modifier` argument is a [Modifier.padding] that adds 4.dp padding
 * to the start of the [Text]. It is used by [DetailLegend] to draw a legend explaining the color to
 * type mapping of the different [SleepType] ranges displayed when the [SleepBar] is expanded.
 *
 * @param sleepType the [SleepType] whose [Color] and [SleepType.title] we are to display.
 */
@Composable
fun LegendItem(sleepType: SleepType) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(size = 10.dp)
                .clip(shape = CircleShape)
                .background(color = sleepType.color)
        )
        Text(
            stringResource(id = sleepType.title),
            style = LegendHeadingStyle,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

/**
 * Preview of our SleepBar Composable
 */
@Preview
@Composable
fun SleepBarPreview() {
    SleepBar(sleepData = sleepData.sleepDayData.first())
}

/**
 * Converted to pixels and used as the line thickness when drawing [RoundRect]'s
 */
private val lineThickness = 2.dp

/**
 * Height of the "bars" representing each of the sleep period segements.
 */
private val barHeight = 24.dp

/**
 * Used as the animation duration when animating the [Transition.AnimatedVisibility] of the
 * [DetailLegend] below the [SleepBar] when the user clicks the [SleepBar] to toggle it between
 * expanded and not expanded.
 */
private const val animationDuration = 500

/**
 *
 */
private val textPadding = 4.dp

/**
 *
 */
private val sleepGradientBarColorStops: List<Pair<Float, Color>> = SleepType.entries.map {
    Pair(
        when (it) {
            SleepType.Awake -> 0f
            SleepType.REM -> 0.33f
            SleepType.Light -> 0.66f
            SleepType.Deep -> 1f
        },
        it.color
    )
}

/**
 *
 */
private fun SleepType.heightSleepType(): Float {
    return when (this) {
        SleepType.Awake -> 0f
        SleepType.REM -> 0.25f
        SleepType.Light -> 0.5f
        SleepType.Deep -> 0.75f
    }
}
