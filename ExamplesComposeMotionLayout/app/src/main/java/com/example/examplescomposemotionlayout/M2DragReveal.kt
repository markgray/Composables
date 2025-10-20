package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

/**
 * A demo of a drag-to-reveal animation.
 *
 * The user can drag up on the text content ("words") to trigger the transition.
 *
 * In the start state:
 *  - An image is displayed prominently at the top.
 *  - A title is shown below the image.
 *  - A block of text is below the title.
 *
 * In the end state (after dragging up):
 *  - The image shrinks to a small thumbnail at the top.
 *  - The title moves to be vertically centered on the thumbnail.
 *  - The text block moves up to be positioned just below the thumbnail.
 *
 * The transition uses `onSwipe` to control the animation and includes `keyAttributes` to
 * modify the image's saturation and brightness mid-transition, and `keyPositions` to
 * curve the title's animation path.
 *
 * We start by initializing our [String] variable `imageId` to the [String] "image", our [String]
 * variable `titleId` to the [String] "title", and our [String] variable `wordsId` to the [String]
 * "words".
 *
 * We initialize our [MotionScene] variable `scene` to a new instance in whose [MotionSceneScope]
 * `motionSceneContent` lambda argument we initialize our [ConstrainedLayoutReference] variable
 * `imageRef` to a new instance whose `id` is the [String] variable `imageId`, we initialize our
 * [ConstrainedLayoutReference] variable `titleRef` to a new instance whose `id` is the [String]
 * variable `titleId`, and we initialize our [ConstrainedLayoutReference] variable `wordsRef` to a
 * new instance whose `id` is the [String] variable `wordsId`.
 *
 * We initialize our [ConstraintSetRef] variable `start1` to a new instance in whose
 * [ConstraintSetScope] `constraintSetContent` lambda argument we:
 *  - [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `imageRef` to have
 *  a [ConstrainScope.width] of [Dimension.fillToConstraints], a [ConstrainScope.height] of `400.dp`,
 *  a [ConstrainScope.alpha] of `1f`, to have it [ConstrainScope.centerHorizontallyTo] the `other`
 *  its `parent`, to link its `top` to the `top` of its `parent`, to declare a `customFloat` whose
 *  `name` is "sat", and whose `value` is `1f`, and to declare a `customFloat` whose `name` is
 *  "bright", and whose `value` is `1f`.
 *  - [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `titleRef` to have
 *  a [ConstrainScope.width] of [Dimension.wrapContent], a [ConstrainScope.height] of
 *  [Dimension.wrapContent], to have if [ConstrainScope.centerHorizontallyTo] the `other` its
 *  `parent`, and to link its `top` to the `bottom` of [ConstrainedLayoutReference] variable
 *  `imageRef` with a `margin` of `2.dp`.
 *  - [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `wordsRef` to have
 *  a [ConstrainScope.width] of [Dimension.fillToConstraints], a [ConstrainScope.height] of
 *  [Dimension.wrapContent], to have if [ConstrainScope.centerHorizontallyTo] the `other`
 *  its `parent`, to link its `top` to the `bottom` of [ConstrainedLayoutReference] variable
 *  `titleRef`.
 *
 * We initialize our [ConstraintSetRef] variable `end1` to a new instance in whose
 * [ConstraintSetScope] `constraintSetContent` lambda argument we:
 *  - [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `imageRef` to have
 *  a [ConstrainScope.width] of `80.dp`, a [ConstrainScope.height] of `80.dp`, to have it
 *  [ConstrainScope.centerHorizontallyTo] the `other` its `parent`, to link its `top` to the `top`
 *  of its `parent`, to declare a `customFloat` whose `name` is "sat", and whose `value` is `0.8f`,
 *  and to declare a `customFloat` whose `name` is "bright", and whose `value` is `0.8f`.
 *  - [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `titleRef` to have
 *  a [ConstrainScope.width] of [Dimension.wrapContent], a [ConstrainScope.height] of
 *  [Dimension.wrapContent], to have if [ConstrainScope.centerHorizontallyTo] the `other` its
 *  `parent`, and to link its `top` to the `bottom` of [ConstrainedLayoutReference] variable
 *  `imageRef`.
 *  - [ConstraintSetScope.constrain] the [ConstrainedLayoutReference] variable `wordsRef` to have
 *  a [ConstrainScope.width] of [Dimension.fillToConstraints], a [ConstrainScope.height] of
 *  [Dimension.wrapContent], to have if [ConstrainScope.centerHorizontallyTo] the `other`
 *  its `parent`, to link its `top` to the `bottom` of [ConstrainedLayoutReference] variable
 *  `imageRef`.
 *
 * Next we use [MotionSceneScope.transition] to define a transition `from` our [ConstraintSetRef]
 * variable `start1` `to` our [ConstraintSetRef] variable `end1` with the [String] "default" as its
 * `name`. In the [TransitionScope] `transitionContent` lambda argument we set the
 * [TransitionScope.onSwipe] property to a new instance of [OnSwipe] with the arguments:
 *  - `side`: is [SwipeSide.Top].
 *  - `direction`: is [SwipeDirection.Up].
 *  - `anchor`: is the [ConstrainedLayoutReference] variable `wordsRef`.
 *  - `mode`: is [SwipeMode.Spring] with a `damping` of `40f`.
 *
 * We use [TransitionScope.keyPositions] to modify the animation path of the `titleRef` at 40% of
 * the way through the animation to have its vertical position follow a curved path instead of a
 * straight line.
 *
 * We use [TransitionScope.keyAttributes] to modify the `imageRef` at 70% of the way through the
 * animation to have its saturation `sat` change to `0f` and havits brightness `bright` change to
 * `1.6f`.
 *
 * Having defined our [MotionScene] we initialize our [Painter] variable `painter` to a the [Painter]
 * returned by [painterResource] for the jpg with resource ID `R.drawable.pepper`.
 *
 * Our root composable is a [MotionLayout] whose `modifier` argument is a [Modifier.background]
 * whose `color` is the [Color] with hex value `0xFF221010`, chained to a [Modifier.fillMaxSize],
 * chained to a [Modifier.padding] that adds `1.dp` to all sides, and whose `motionScene` argument
 * is the [MotionScene] variable `scene`.
 *
 * In the [MotionLayoutScope] `content` composable lambda argument we:
 *
 * **First** we compose a [MotionImage] whose arguments are:
 *  - `painter`: is the [Painter] variable `painter`.
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is the [String] variable `imageId`
 *  - `brightness`: is the [Float] returned by [MotionLayoutScope.customFloat] for the `id` the
 *  [String] variable `imageId` and the `name` "bright".
 *  - `saturation`: is the [Float] returned by [MotionLayoutScope.customFloat] for the `id` the
 *  [String] variable `imageId` and the `name` "sat".
 *
 * **Second** we compose a [Text] whose arguments are:
 *  - `modifier`: is a [Modifier.layoutId] whose `layoutId` is the [String] variable `titleId`.
 *  - `text`: is the [String] "Pepper".
 *  - `fontSize`: is `30.sp`.
 *  - `color`: is [Color.White].
 *
 * **Third** we compose a [Box] whose `modifier` argument is a [Modifier.layoutId] whose `layoutId`
 * is the [String] variable `wordsId`, chained to a [Modifier.clip] whose `shape` is a
 * [RoundedCornerShape] of `size` `20.dp`. In the [BoxScope] `content` composable lambda argument we
 * compose a [Text] whose arguments are:
 *  - `text`: is the [String] returned by [LoremIpsum] with `words` `222`.
 *  - `modifier`: is a [Modifier.background] whose `color` is [Color.White], chained to a
 *  [Modifier.padding] that adds `8.dp` to all sides, chained to a [Modifier.layoutId] whose
 *  `layoutId` is the [String] variable `titleId`.
 */
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "motion101")
@Composable
fun M2DragReveal() {
    val imageId = "image"
    val titleId = "title"
    val wordsId = "words"

    val scene = MotionScene {
        val imageRef: ConstrainedLayoutReference = createRefFor(id = imageId)
        val titleRef: ConstrainedLayoutReference = createRefFor(id = titleId)
        val wordsRef: ConstrainedLayoutReference = createRefFor(id = wordsId)


        val start1: ConstraintSetRef = constraintSet {
            constrain(ref = imageRef) {
                width = Dimension.fillToConstraints
                height = Dimension.value(dp = 400.dp)
                alpha = 1f
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = parent.top)
                customFloat(name = "sat", value = 1f)
                customFloat(name = "bright", value = 1f)
            }
            constrain(ref = titleRef) {
                width =  Dimension.wrapContent
                height =  Dimension.wrapContent
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = imageRef.bottom, margin = 2.dp)
            }
            constrain(ref = wordsRef) {
                width =  Dimension.fillToConstraints
                height =  Dimension.wrapContent
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = titleRef.bottom)
            }
        }

        val end1: ConstraintSetRef = constraintSet {
            constrain(ref = imageRef) {
                width = Dimension.value(dp = 80.dp)
                height =Dimension.value(dp = 80.dp)
                centerHorizontallyTo(other = parent, bias = 0f)
                top.linkTo(anchor = parent.top)
                customFloat(name = "sat", value = 0.8f)
                customFloat(name = "bright", value = 0.8f)
            }
            constrain(ref = titleRef) {
                width =  Dimension.wrapContent
                height =  Dimension.wrapContent
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = imageRef.top)
                bottom.linkTo(anchor = imageRef.bottom)
            }
            constrain(ref = wordsRef) {
                width =  Dimension.fillToConstraints
                height =  Dimension.wrapContent
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = imageRef.bottom)
            }
        }
        transition(from = start1, to = end1, name = "default") {
             onSwipe =  OnSwipe(
                side = SwipeSide.Top,
                direction = SwipeDirection.Up,
                anchor = wordsRef,
                 mode = SwipeMode.Spring(damping = 40f),
            )
            keyPositions(titleRef){
                frame(frame = 40){
                    percentY = 0.3f
                    type = RelativePosition.Path
                }
            }
            keyAttributes(imageRef) {
                frame(frame = 70) {
                    customFloat(name = "sat", value = 0f)
                    customFloat(name = "bright", value = 1.6f)

                }
            }

        }
    }
    val painter: Painter = painterResource(id = R.drawable.pepper)

    MotionLayout(
        modifier = Modifier
            .background(color = Color(color = 0xFF221010))
            .fillMaxSize()
            .padding(all = 1.dp),
        motionScene = scene
    ) {
        MotionImage(
            painter = painter,
            modifier = Modifier.layoutId(layoutId = imageId),
            brightness = customFloat(id = imageId, name = "bright"),
            saturation = customFloat(id = imageId, name = "sat"),
        )
        Text(
            modifier = Modifier.layoutId(layoutId = titleId),
            text = "Pepper",
            fontSize = 30.sp,
            color = Color.White
        )
        Box(
            modifier = Modifier
                .layoutId(layoutId = wordsId)
                .clip(shape = RoundedCornerShape(size = 20.dp))
        ) {
            Text(
                text = LoremIpsum(words = 222).values.first(),
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(all = 8.dp)
                    .layoutId(layoutId = "title"),
            )
        }
    }
}
