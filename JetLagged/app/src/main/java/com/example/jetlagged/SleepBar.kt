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
import androidx.compose.ui.draw.CacheDrawScope
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
 * in a call to `CacheDrawScope.onDrawBehind` which then does all of the actual drawing. We start by
 * initializing and remembering our [TextMeasurer] variable `val textMeasurer` to a new instance. We
 * initialize our animated [Dp] variable `val height` using the [Transition.animateDp] method of our
 * [Transition] of [Boolean] parameter [transition] with its `label` argument the [String] "height",
 * its `transitionSpec` a [spring], and its `targetValueByState` lambda argument a lambda which if
 * its `targetValueByState` [Boolean] parameter is `true` returns 100.dp and if `false` returns 24.dp.
 * We initialize our animated [Float] variable `val animationProgress` using the [Transition.animateFloat]
 * method of our [Transition] of [Boolean] parameter [transition] with its `label` argument the [String]
 * "progress", its `transitionSpec` a [spring], and its `targetValueByState` lambda argument a lambda
 * which if its `target` [Boolean] parameter is `true` returns 1f and if `false` returns 0f.
 *
 * Our root Composable is a [Spacer] whose `modifier` argument is a [Modifier.drawWithCache] which
 * calculates the animated arguments needed by [drawSleepBar] to draw the sleep bar representing our
 * [SleepDayData] parameter [sleepData] then calls [CacheDrawScope.onDrawBehind] to have it issue
 * drawing commands produced by [drawSleepBar] before the layout content is drawn. Chained to that is
 * a [Modifier.height] which sets the height of the [Spacer] to our animated [Dp] variable `height`,
 * and chained to that is a [Modifier.fillMaxWidth] causes the [Spacer] to occupy its entire incoming
 * width constraint.
 *
 * The arguments passed to [drawSleepBar] in the lambda we pass to [CacheDrawScope.onDrawBehind] are:
 *  - `roundedRectPath` our [Path] variable `val roundedRectPath` which is a [RoundRect] whose `rect`
 *  argument is a [Rect] whose `size` argument is [Size] whose `width` is the `width` of the drawing
 *  environment of the [Modifier.drawWithCache] plus 2 times the pixel value of [lineThickness],
 *  and whose `height` is the `height` of the drawing environment of the [Modifier.drawWithCache]
 *  plus the pixel value of [lineThickness]. Its `cornerRadius` is a [CornerRadius] whose `x` is
 *  the `animationProgress` animated [lerp] between 2.dp at its start and 10.dp when collapsed.
 *  - `sleepGraphPath` our animated [Path] variable `val sleepGraphPath` which is generated by a
 *  call to our [generateSleepPath] method with its `canvasSize` argument the [Size] of the drawing
 *  environment of the [Modifier.drawWithCache], its `sleepData` argument our [SleepDayData] parameter
 *  [sleepData], its `width` argument the `width` of the drawing environment of the [Modifier.drawWithCache],
 *  its `barHeightPx` the pixel value of [barHeight], its `heightAnimation` our [Transition.animateFloat]
 *  variable `animationProgress`, and its `lineThicknessPx` the pixel value of [lineThickness] divided
 *  by 2f.
 *  - `gradientBrush` our [Brush.verticalGradient] variable `val gradientBrush` whose `colorStops`
 *  argument is the [Array] of [Pair] of [Float] to [Color] produced from [sleepGradientBarColorStops],
 *  whose `startY` argument is 0f and whose `endY` argument is the size of [SleepType.entries] times
 *  the pixel value of [barHeight].
 *  - `roundedCornerStroke` our animated [Stroke] variable `val roundedCornerStroke` (its `pathEffect`
 *  argument is a [PathEffect.cornerPathEffect] whose `radius` is the pixel value of 2.dp times our
 *  [Transition.animateFloat] variable `animationProgress`).
 *  - `animationProgress` our [Transition.animateFloat] variable `animationProgress`.
 *  - `textResult` our [TextLayoutResult] variable `val textResult` which is the [TextMeasurer.measure]
 *  pre-measured emoji of the [SleepDayData.sleepScoreEmoji] of our [SleepDayData] parameter [sleepData].
 *  - `cornerRadiusStartPx` pixel value of 2.dp.
 *
 * @param sleepData the [SleepDayData] whose "SleepBar" we are to render.
 * @param transition the [Transition] of [Boolean] that is used to animate the "SleepBar" between
 * "expanded" (`true`) and "not expanded" (`false`) states when the user clicks on our "SleepBar"
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
    val height: Dp by transition.animateDp(
        label = "height",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        }
    ) { targetExpanded: Boolean ->
        if (targetExpanded) 100.dp else 24.dp
    }

    /**
     * The fraction of the [Transition] of [Boolean] parameter [transition] that has occurred. It is
     * 1f when [transition] is `true` (expanded) and 0f when [transition] is `false`.
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
                    x = lerp(cornerRadiusStartPx, collapsedCornerRadiusPx, (1 - animationProgress))
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
 * This is called in the `block` lambda argument we pass to the [CacheDrawScope.onDrawBehind] method
 * in the [Modifier.drawWithCache] applied to the [Spacer] Composable contained in [SleepRoundedBar].
 * It does all the actual drawing of the "sleep bar".
 *
 * @param roundedRectPath amimated [Path] used as the [clipPath] when drawing our [Path] parameter
 * [sleepGraphPath], its height is animated by the [Modifier.height] chained to the [Modifier.drawWithCache]
 * that calls us and its width is set to the [Modifier.fillMaxWidth] that is chained to that. When
 * expanded the `height` is 100.dp and when not expanded it is 24.dp.
 * @param sleepGraphPath the animated [Path] that [generateSleepPath] creates for the [SleepDayData]
 * with each of the [SleepPeriod] in the [SleepDayData.sleepPeriods] list of [SleepPeriod] offset
 * by the [SleepType.heightSleepType] of its [SleepPeriod.type] animated by the currrent value of
 * [animationProgress] (which is 1f when expanded and 0f when unexpanded).
 * @param gradientBrush a nifty [Brush.verticalGradient] where the `colorStops` argument is the
 * [Array] of [Pair] of [Float] to [Color] created from the [sleepGradientBarColorStops] list of
 * [Pair] of [Float] to [Color] where each [Float] value is the offset of the [SleepPeriod.type],
 * and the [Color] is the [SleepType.color] of the [SleepType]. Using this as the [Brush] when we
 * when we call [DrawScope.drawPath] automatically draws the [SleepPeriod] with the correct [Color]
 * when the sleep bar is expanded or unexpanded.
 * @param roundedCornerStroke the animated [PathEffect.cornerPathEffect] rounded [Stroke] we use to
 * draw our [Path] parameter [sleepGraphPath], its radius is 2.dp times [animationProgress].
 *  @param animationProgress the animated [Transition.animateFloat] of our [Boolean] expanded
 *  (`true`) or unexpanded (`false`) state, it is 1f when the [Transition] is `true` (expanded)
 *  and 0f when it is `false`.
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
 * Generates the animated [Path] for the different sleep periods contained in its [SleepDayData]
 * parameter [sleepData], given the animation progress in [Float] parameter [heightAnimation]
 * (goes from 0f to 1f). We start by initializing our [Path] variable `val path` to a new instance,
 * and our [SleepPeriod] variable `var previousPeriod` to `null`. We use the [Path.moveTo] method
 * of `path` to move it to (0,0). Then [forEach] of the `period` [SleepPeriod]'s in the [List] of
 * [SleepPeriod] field [SleepDayData.sleepPeriods] of our [SleepDayData] parameter [sleepData] we:
 *
 *  - initialize our [Float] variable `val percentageOfTotal` to the value returned by the
 *  [SleepDayData.fractionOfTotalTime] method of [sleepData] for the `period` [SleepPeriod]
 *  - initialize our [Float] variable `val periodWidth` to `percentageOfTotal` times our [Float]
 *  parameter [width] (the `width` dimension of the current drawing environment of the [drawWithCache]
 *  modifier that calls us).
 *  - initialize our [Float] variable `val startOffsetPercentage` to the value returned by the
 *  [SleepDayData.minutesAfterSleepStart] method of [sleepData] for the `period` [SleepPeriod]
 *  divided by the [SleepDayData.totalTimeInBed] property of [sleepData] converted to [Float]
 *  minutes.
 *  - initialize our [Float] variable `val halfBarHeight` to the [Size.height] of our [Size]
 *  parameter [canvasSize] (height of the current drawing environment of the [Modifier.drawWithCache]
 *  that called us) divided by the `size` of [SleepType.entries] divided by 2f.
 *  - initialize our [Float] variable `val offset` to 0f if `previousPeriod` is `null` or to
 *  `halfBarHeight` if it is not.
 *  - initialize our [Float] variable `val offsetY` to the [lerp] Linearly interpolated value between
 *  `start` of 0f and `stop` of the value returned by [heightSleepType] for the [SleepPeriod.type] of
 *  [SleepPeriod] `period` times the [Size.height] of [Size] parameter [canvasSize] with `fraction`
 *  our [Float] parameter [heightAnimation] (the `animationProgress` of the [Transition] between
 *  expanded and unexpanded) between them.
 *  - step 1 - draw a line from previous sleep period to current: if `previousPeriod` is not `null`
 *  we use the [Path.lineTo] method of `path` to draw a line to `x` of `startOffsetPercentage` times
 *  our [Float] parameter [width] plus our [Float] parameter [lineThicknessPx], and `y` of `offsetY`
 *  plus `offset`
 *  - step 2 - add the current sleep period as rectangle to path: we call the [Path.addRect] method
 *  of `path` with its `rect` argument a [Rect] with its `offset` of the top left corner an [Offset]
 *  whose `x` is `startOffsetPercentage` times our [Float] parameter [width] plus our [Float] parameter
 *  [lineThicknessPx], and its `y` is `offsetY`. The `size` argument is the value of the [Size.copy]
 *  of [Size] parameter [canvasSize] with the `width` overridden by `periodWidth` and the `height`
 *  overridden by our [Float] parameter [barHeightPx].
 *  - step 3 - move to the middle of the current sleep period: we call the [Path.moveTo] method of
 *  `path` to move it to `x` coordinate `startOffsetPercentage` times [Float] parameter [width] plus
 *  `periodWidth` plus [Float] parameter [lineThicknessPx], and `y` coordinate `offsetY` plus
 *  `halfBarHeight`.
 *  - having finished building the [Path] for this [SleepPeriod] `period` we set `previousPeriod`
 *  to `period` and loop around for the next [SleepPeriod].
 *
 * When done with all of the [SleepPeriod]'s in our [SleepDayData] parameter [sleepData] we return
 * [Path] variable `path` to the caller.
 *
 * @param canvasSize the dimensions of the current drawing environment of the [Modifier.drawWithCache]
 * that is calling us.
 * @param sleepData the [SleepDayData] whose [List] of [SleepPeriod] field [SleepDayData.sleepPeriods]
 * we are to generate a [Path] for.
 * @param width the [Size.width] of the dimensions of the current drawing environment of the
 * [Modifier.drawWithCache] that is calling us.
 * @param barHeightPx the pixel value of our [Dp] constant [barHeight].
 * @param heightAnimation the fraction of the [Transition] of [Boolean] value `isExpanded` between
 * `true` or `false`.
 * @param lineThicknessPx the pixel value of the [Dp] constant [lineThickness] divided by 2f.
 * @return a [Path] that can be used to draw all of the [SleepPeriod]'s in the [List] of [SleepPeriod]
 * field [SleepDayData.sleepPeriods] of our [SleepDayData] parameter [sleepData] given the current
 * animation progress passed us in our [Float] parameter [heightAnimation].
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

    sleepData.sleepPeriods.forEach { period: SleepPeriod ->
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
 * Padding which is used to offset the `x` coordinate of the [Offset] used for the `topLeft` argument
 * of the [drawText] call which draws the [TextLayoutResult] holding the [SleepDayData.sleepScoreEmoji]
 * emoji at the beginning of the [SleepRoundedBar]
 */
private val textPadding = 4.dp

/**
 * Holds a [Pair] of [Float] and [Color] for each of the [SleepType.entries] in a [List]. It is
 * converted to a typed [Array] of [Pair] and used as the `colorStops` argument of a call to
 * [Brush.verticalGradient] that creates the [Brush] variable `val gradientBrush` which is used as
 * the `gradientBrush` argument of a call to [drawSleepBar]. The [Float] value of the [Pair] is
 * the offset that determines where the [Color] value is dispersed throughout the vertical gradient,
 * the [Color] value is the [SleepType.color] of the [SleepType]
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
 * Used by the [generateSleepPath] method to compute its [Float] variable `val offsetY` which is
 * used to position the [SleepType] as it is animated between its expanded and unexpanded positions.
 *
 * @return the relative position of the [SleepType] at its expanded position.
 */
private fun SleepType.heightSleepType(): Float {
    return when (this) {
        SleepType.Awake -> 0f
        SleepType.REM -> 0.25f
        SleepType.Light -> 0.5f
        SleepType.Deep -> 0.75f
    }
}
