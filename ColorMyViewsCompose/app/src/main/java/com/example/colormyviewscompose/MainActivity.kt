package com.example.colormyviewscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colormyviewscompose.ui.theme.ColorMyViewsComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ColorMyViewsComposeTheme {
                ColorMyViewApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorMyViewApp() {
    ColumnContent(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    )
}

@Composable
fun ColumnContent(modifier: Modifier = Modifier) {
    var boxOneColor by remember {
        mutableStateOf(Color.White)
    }
    var boxTwoColor by remember {
        mutableStateOf(Color.White)
    }
    var boxThreeColor by remember {
        mutableStateOf(Color.White)
    }
    var boxFourColor by remember {
        mutableStateOf(Color.White)
    }
    var boxFiveColor by remember {
        mutableStateOf(Color.White)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.box_one),
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(boxOneColor)
                .padding(16.dp)
                .clickable {
                    boxOneColor = Color.DarkGray
                }
        )
        Row {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.box_two),
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(130.dp)
                    .height(130.dp)
                    .background(boxTwoColor)
                    .clickable {
                        boxTwoColor = Color.Gray
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.box_three),
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(boxThreeColor)
                        .clickable {
                            boxThreeColor = Color.Blue
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.box_four),
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(boxFourColor)
                        .clickable {
                            boxFourColor = Color.Magenta
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.box_five),
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(boxFiveColor)
                        .clickable {
                            boxFiveColor = Color.Blue
                        }
                )
            }
        }
        Row {
            Text(
                text = stringResource(id = R.string.how_to_play),
                fontSize = 24.sp
            )
            Text(
                text = stringResource(id = R.string.tap_the_boxes_and_buttons),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1.0f, fill = true))
        Row {
            Button(onClick = { boxThreeColor = Color.Red }) {
                Text(
                    text = stringResource(id = R.string.button_red)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { boxFourColor =Color.Yellow }) {
                Text(
                    text = stringResource(id = R.string.button_yellow)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { boxFiveColor = Color.Green }) {
                Text(
                    text = stringResource(id = R.string.button_green)
                )
            }
        }
    }
}