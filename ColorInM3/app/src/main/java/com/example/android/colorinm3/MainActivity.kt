package com.example.android.colorinm3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.android.colorinm3.ui.theme.ColorInM3Theme

/**
 *
 */
class MainActivity : ComponentActivity() {
    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var dynamicColor: Boolean by rememberSaveable {
                mutableStateOf(value = true)
            }
            var darkTheme: Boolean by rememberSaveable {
                mutableStateOf(value = true)
            }
            ColorInM3Theme(dynamicColor = dynamicColor, darkTheme = darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(
                        dynamicColor = dynamicColor,
                        darkTheme = darkTheme,
                        toggleDynamic = { dynamicColor = !dynamicColor },
                        toggleDarkTheme = { darkTheme = !darkTheme }
                    )
                }
            }
        }
    }
}

/**
 *
 */
@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    dynamicColor: Boolean,
    darkTheme: Boolean,
    toggleDynamic: () -> Unit,
    toggleDarkTheme: () -> Unit
) {
    Scaffold(
        bottomBar = {
            AppBottombar(
                modifier = modifier,
                dynamicColor = dynamicColor,
                darkTheme = darkTheme,
                toggleDynamic = { toggleDynamic() },
                toggleDarkTheme = { toggleDarkTheme() }
            )
        }
    ) { paddingValues: PaddingValues ->
        Greeting(
            modifier = modifier.padding(paddingValues = paddingValues)
        )
    }
}

/**
 *
 */
@Composable
fun AppBottombar(
    modifier: Modifier = Modifier,
    dynamicColor: Boolean,
    darkTheme: Boolean,
    toggleDynamic: () -> Unit,
    toggleDarkTheme: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { toggleDynamic() },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = if (dynamicColor) "Static" else "Dynamic")
        }
        Button(
            onClick = { toggleDarkTheme() },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = if (darkTheme) "Light Theme" else "Dark Theme")
        }
    }
}

/**
 *
 */
@Composable
fun Greeting(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(space = 6.dp)
    ) {
        Spacer(modifier = Modifier.height(height = 6.dp))
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "primary",
                modifier = modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "primaryContainer",
                modifier = modifier,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "secondary",
                modifier = modifier,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}
