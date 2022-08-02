package com.example.dessertclickercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.dessertclickercompose.ui.theme.DessertClickerComposeTheme

/**
 * This is the main activity of our DessertClicker app.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * and then we call the [setContent] method to Compose the composable given in its composable
     * lambda argument into the activity (the content will become the root view of the activity).
     * That composable lambda consists of our [DessertClickerComposeTheme] custom [MaterialTheme]
     * wrapping a [Surface] whose `modifier` is a [Modifier.fillMaxSize] to have it fill the space
     * allowed it, and whose background color is the 'background' color from our theme. The composable
     * lambda of the [Surface] consists of a call to our [MainScreen] composable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so ignore this.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DessertClickerComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

/**
 * This is the main screen of our app, it exists solely to feed our [ConstraintLayoutContent] a
 * `modifier` argument consisting of a [Modifier.fillMaxSize] to have its content fill the space
 * allowed it, and a [Modifier.wrapContentSize] to have it use [Alignment.TopCenter] to align its
 * content to the top center of its allowed space.
 */
@Preview(showBackground = true)
@Composable
fun MainScreen() {
    ConstraintLayoutContent(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    )
}

/**
 * This Composable holds our [ConstraintLayout]. Strictly speaking the [ConstraintLayout] could be
 * directly used as the content of the [Surface] used in our [MainActivity.onCreate] override, with
 * [MainScreen] and [ConstraintLayoutContent] totally superfluous, but the indirection gives one
 * flexibility when writing the code in case you need to hoist something (it also lessens the amount
 * of indentation used).
 *
 * We pass our [Modifier] parmeter [modifier] as the `modifier` parameter of our [ConstraintLayout]
 * content. Our [ConstraintLayout] first creates [ConstrainedLayoutReference] instances for the 6
 * widgets it is to contain as its content, and `remember`'s three [Int]'s that are to be found in
 * our [bakery] singleton instance of [Bakery]:
 *  - `dessertId` is the resource ID of the drawable for the current dessert: The [Dessert.imageId]
 *  field of the [Bakery.currentDessert] field of [bakery].
 *  - `dessertsSold` is the total number of desserts sold: the [Bakery.dessertsSold] field of [bakery]
 *  - `revenue` is the amount of money received: the [Bakery.revenue] field of [bakery].
 *
 * The widgets contained in the [ConstraintLayout] listed by the [ConstrainedLayoutReference] used
 * to constrain them:
 *  - `backgroundImage` is an [Image] displaying our background drawable [R.drawable.bakery_back],
 *  whose `top` is linked to the `top` of its parent, and its `absoluteLeft` and `absoluteRight`
 *  are linked to its parent's `absoluteLeft` and `absoluteRight` respectively.
 *  - `whiteBackground` is a 100.dp high white [Box] whose `bottom` is linked to its parent's `bottom`
 *  and whose `absoluteLeft` and `absoluteRight` are linked to its parent's `absoluteLeft` and
 *  `absoluteRight` respectively.
 *  - `dessertButton` is a clickable [Image] displaying the drawable of the current dessert whose
 *  resource ID is given by `dessertId`. The lambda parameter of its [Modifier.clickable] calls the
 *  [Bakery.onDessertClicked] method of [bakery] then updates `dessertId`, `dessertsSold`, and
 *  `revenue` to the new values that are now to be found in [bakery] for them. Its `top` is linked
 *  to its parent's `top`, its `absoluteLeft` and `absoluteRight` are linked to its parent's
 *  `absoluteLeft` and `absoluteRight` respectively, and its `bottom` is linked to the `top` of
 *  `whiteBackground` (the White [Box] at the bottom of the [ConstraintLayout]).
 *  - `revenueText` is a [Text] displaying the remembered `revenue` value, which is taken from the
 *  [Bakery.revenue] field of [bakery], and represents the amount of money charged the "dessert
 *  clicking" customer. Its `absoluteRight` is linked to the `absoluteRight` of its parent with a
 *  margin of 16.dp, and its `bottom` is linked to its parent's `bottom` with a margin of 16.dp
 *  - `dessertSoldText` is a [Text] displaying the static text "Desserts Sold". Its `absoluteLeft`
 *  is linked to the `absoluteLeft` of its parent with a margin of 16.dp, and its `top` is linked
 *  to the `top` of `whiteBackground` with a margin of 16.dp
 *  - `amountSoldText` is a [Text] displaying the remembered `dessertsSold` value, which is taken
 *  from the [Bakery.dessertsSold] field of [bakery], and represents the total number of times that
 *  the user has clicked the dessert icons displayed in the `dessertButton` [Image]. Its `absoluteRight`
 *  is linked to its parent's `absoluteRight` with a margin of 16.dp, and its `top` is linked to the
 *  `top` of `whiteBackground` with a margin of 16.dp
 */
@Composable
fun ConstraintLayoutContent(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier) {
        val backgroundImage: ConstrainedLayoutReference = createRef()
        val whiteBackground: ConstrainedLayoutReference = createRef()
        val dessertButton: ConstrainedLayoutReference = createRef()
        val revenueText: ConstrainedLayoutReference = createRef()
        val dessertSoldText: ConstrainedLayoutReference = createRef()
        val amountSoldText: ConstrainedLayoutReference = createRef()

        var dessertId: Int by remember {
            mutableStateOf(bakery.currentDessert.imageId)
        }
        var dessertsSold: Int by remember {
            mutableStateOf(bakery.dessertsSold)
        }
        var revenue: Int by remember {
            mutableStateOf(bakery.revenue)
        }

        Image(
            painter = painterResource(R.drawable.bakery_back),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs(backgroundImage) {
                    top.linkTo(parent.top)
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    absoluteRight.linkTo(parent.absoluteRight)
                }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White)
                .constrainAs(whiteBackground) {
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    absoluteRight.linkTo(parent.absoluteRight)
                    bottom.linkTo(parent.bottom)
                }
        )
        Image(
            painter = painterResource(id = dessertId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .constrainAs(dessertButton) {
                    top.linkTo(parent.top)
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    absoluteRight.linkTo(parent.absoluteRight)
                    bottom.linkTo(whiteBackground.top)
                }
                .clickable {
                    bakery.onDessertClicked()
                    dessertId = bakery.currentDessert.imageId
                    dessertsSold = bakery.dessertsSold
                    revenue = bakery.revenue
                }
        )
        Text(
            text = "$$revenue",
            fontSize = 33.sp,
            color = Color.Green,
            modifier = Modifier
                .constrainAs(revenueText) {
                    absoluteRight.linkTo(
                        parent.absoluteRight,
                        margin = 16.dp
                    )
                    bottom.linkTo(
                        parent.bottom,
                        margin = 16.dp
                    )
                }
        )
        Text(
            text = "Desserts Sold",
            fontSize = 20.sp,
            modifier = Modifier
                .constrainAs(dessertSoldText) {
                    absoluteLeft.linkTo(
                        parent.absoluteLeft,
                        margin = 16.dp
                    )
                    top.linkTo(
                        whiteBackground.top,
                        margin = 16.dp
                    )
                }
        )
        Text(
            text = "$dessertsSold",
            fontSize = 20.sp,
            modifier = Modifier
                .constrainAs(amountSoldText) {
                    absoluteRight.linkTo(
                        parent.absoluteRight,
                        margin = 16.dp
                    )
                    top.linkTo(
                        whiteBackground.top,
                        margin = 16.dp
                    )
                }
        )

    }
}
