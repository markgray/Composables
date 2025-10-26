package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetRef
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import kotlinx.coroutines.flow.Flow

/**
 * A selector for different reactions, represented by emojis.
 *
 * When an emoji is selected, it animates the corresponding emoji name into view using MotionLayout.
 * The emoji name scales up and fades in, while the previously selected name fades out.
 *
 * This demonstrates a complex animation sequence where the start state is common,
 * but there are multiple end states (one for each emoji). The correct transition
 * is chosen dynamically based on user interaction.
 * TODO: Continue here.
 *
 * @see MotionLayout
 * @see MotionScene
 * @see LaunchedEffect
 */
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll")
@Composable
fun ReactionSelector() {
    var selected: Int by remember { mutableIntStateOf(value = 3) }
    val transitionName: MutableState<String> = remember { mutableStateOf(value = "transition1") }
    val emojis: List<String> = remember { "😀 🙂 🤨 😐 😒 😬".split(' ') }
    val emojiNames: List<String> = remember {
        listOf(
            "Grinning Face",
            "Slightly Smiling Face",
            "Face with Raised Eyebrow",
            "Neutral Face",
            "Unamused Face",
            "Grimacing Face"
        )
    }

    val scene = MotionScene {
        val emojiIds: Array<ConstrainedLayoutReference> =
            emojis.map { createRefFor(id = it) }.toTypedArray()
        val titleIds: Array<ConstrainedLayoutReference> =
            emojiNames.map { createRefFor(id = it) }.toTypedArray()

        val start1: ConstraintSetRef = constraintSet {
            createHorizontalChain(elements = emojiIds)
            emojiIds.forEach { layoutRef: ConstrainedLayoutReference ->
                constrain(ref = layoutRef) {
                    top.linkTo(anchor = parent.top, margin = 10.dp)
                }
            }
            titleIds.forEachIndexed { index: Int, title: ConstrainedLayoutReference ->
                constrain(ref = title) {
                    top.linkTo(anchor = emojiIds[0].bottom, margin = 10.dp)
                    start.linkTo(anchor = emojiIds[index].start)
                    end.linkTo(anchor = emojiIds[index].end)
                    bottom.linkTo(anchor = parent.bottom, margin = 10.dp)
                    scaleX = 0.1f
                    alpha = 0f
                }
            }
        }
        val ends: List<ConstraintSetRef> = titleIds.map { layoutRef: ConstrainedLayoutReference ->
            constraintSet(extendConstraintSet = start1) {
                constrain(ref = layoutRef) {
                    scaleX = 1f
                    alpha = 1f
                }
            }
        }
        ends.mapIndexed { index: Int, end: ConstraintSetRef ->
            transition(from = start1, to = end, name = "transition$index") {
            }
        }
    }
    val progress: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0f) }
    val selectedFlow: Flow<Int> = snapshotFlow { selected }

    LaunchedEffect(key1 = Unit) {
        selectedFlow.collect {
            progress.snapTo(targetValue = 0f)
            transitionName.value = "transition$it"
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }

    Column {
        MotionLayout(
            modifier = Modifier
                .background(color = Color(color = 0xff334433))
                .fillMaxWidth(),
            motionScene = scene,
            transitionName = transitionName.value,
            progress = progress.value
        ) {
            emojis.forEachIndexed { index: Int, icon: String ->
                Text(
                    text = icon,
                    modifier = Modifier
                        .layoutId(layoutId = icon)
                        .clickable {
                            selected = index
                        }
                )
            }
            emojiNames.forEach { name: String ->
                Text(
                    text = name,
                    color = Color.White,
                    modifier = Modifier.layoutId(layoutId = name)
                )
            }
        }
    }
}
