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

@file:Suppress("Destructure", "UnusedImport")

package com.codelab.basiclayouts

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.basiclayouts.ui.theme.MySootheTheme

/**
 * This is the main activity of the solution of the "Basic Layouts in Compose Codelab"
 * https://developer.android.com/codelabs/jetpack-compose-layouts
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge]
     * to enable edge to edge display, then we call our super's
     * implementation of `onCreate`. Next we call [setContent] to have it Compose its
     * composable `content` lambda argument into our activity wherein we
     * initialize our [WindowSizeClass] variable `val windowSizeClass` to the [WindowSizeClass]
     * calculated for our activity by the [calculateWindowSizeClass] method, then call our
     * [MySootheApp] Composable with `windowSizeClass` as its `windowSizeClass` argument.
     *
     * The [MySootheApp] composable is wrapped in a [Box] whose `modifier` argument is a
     * [Modifier.safeDrawingPadding] to add padding to accommodate the safe drawing insets
     * as kludge to adjust to the use of [enableEdgeToEdge].
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(activity = this)
            Box(modifier = Modifier.safeDrawingPadding()) {
                MySootheApp(windowSize = windowSizeClass)
            }
        }
    }
}

/**
 * Step: Search bar - Modifiers. This Composable is the top item in the [Column] of [HomeScreen].
 * If it were functional it would be usable to search for something or other as "Search bars" are
 * used in other apps, but it is just used here to demonstrate the use of [Modifier]. Our `content`
 * consists of a [TextField] whose arguments are:
 *  - `value` - an empty string. In a functional "Search bar" this would be a "remembered" string
 *  which is updated by the `onValueChange` lambda of the [TextField].
 *  - `onValueChange` - an empty lambda. In a functional "Search bar" this would be a lamdba which
 *  would update the remembered variable used for the `value` argument as well as doing things to
 *  assist the user in his construction or a search, such as offering suggested searches to use.
 *  - `leadingIcon` - an [Icon] to be displayed at the beginning of the text field container, in our
 *  case we use the `Search` [ImageVector] of [Icons.Default] (a magnifying glass).
 *  - `colors` - [TextFieldColors] that will be used to resolve color of the text, content (including
 *  label, placeholder, leading and trailing icons, indicator line) and background for the [TextField]
 *  in different states. We use the defaults produced by [TextFieldDefaults.colors] except
 *  for the `unfocusedContainerColor` and `focusedContainerColor` colors both of which we replace
 *  with the [ColorScheme.surface] color of our [MaterialTheme] (Color(0xFFFFFBFF) (White) for our
 *  `lightColorScheme`, and Color(0xFF1D1B1A) (Black) for our `darkColorScheme`.
 *  - `placeholder` - placeholder to be displayed when the text field is in focus and the input text
 *  is empty, we use the string with resource ID `R.string.placeholder_search` ("Search").
 *  - - `modifier` - the [Modifier] for the [TextField], we use our parameter [modifier] as a starter
 *  and chain a [Modifier.fillMaxWidth] (have the content fill its incoming measurement constraints,
 *  followed by a [Modifier.heightIn] specifying 56.dp as the minimum height of the [TextField].
 *
 * @param modifier a [Modifier] that the Composables which use this Composable can use to modify its
 * behavior and appearance, or a default, or starter [Modifier] that contains no elements if they do
 * not specify one. [HomeScreen] uses a [Modifier.padding] whose `horizontal` padding is 16.dp, and
 * the preview [SearchBarPreview] uses 8.dp for `all` padding.
 */
@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    TextField(
        value = "",
        onValueChange = {},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        placeholder = {
            Text(stringResource(id = R.string.placeholder_search))
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

/**
 * Step: Align your body - Alignment. Our root Composable is a [Column] which is invoked with our
 * [Modifier] parameter [modifier] as its `modifier` argument, and [Alignment.CenterHorizontally]
 * as its `horizontalAlignment` argument to have the horizontal alignment of its children be
 * horizontally centered. The children of the [Column] are an [Image] displaying the [Drawable]
 * whose resource ID is our [drawable] parameter, and a [Text] whose text is the [String] whose
 * resource ID is our [text] parameter. The [Image] uses [ContentScale.Crop] as its `contentScale`
 * argument to Scale the source uniformly (maintaining the source's aspect ratio) so that both
 * dimensions (width and height) of the source will be equal to or larger than the corresponding
 * dimension of the destination, and uses [Modifier.size] to size the [Image] to be 88.dp and a
 * [Modifier.clip] of [CircleShape] to clip the [Image] to be a circle shape. The [Text] uses a
 * `style` argument of the `bodyMedium` font of [MaterialTheme.typography]. The `bodyMedium` font in
 * our custom [Typography] uses 14.sp as the `fontSize`, 20.sp as the `lineHeight`. (0.25).sp as the
 * `letterSpacing` and the `fontFamily` is the regular `fontFamilyLato`, so the [Font] is loaded
 * from the resource ID `R.font.lato_regular` (which is the file "font/lato_regular.ttf"). The
 * `modifier` argument of the [Text] is [Modifier.paddingFromBaseline] with the padding from the top
 * of the layout to the baseline of the first line of text in the content given by `top` = 24.dp,
 * and the distance from the baseline of the last line of text in the content to the bottom of the
 * layout given by `bottom` = 8.dp.
 *
 * @param drawable the resource ID of a [Drawable] to use for our [Image]
 * @param text the resource ID of a [String] to use as the text of our [Text]
 * @param modifier a [Modifier] that our caller can use to modify our behavior or appearance (or the
 * default [Modifier] if they do not specify one). It is used as the `modifier` parameter of the
 * root [Column] of our Composable. Preview [AlignYourBodyElementPreview] passes a [Modifier.padding]
 * with 8.dp for all padding, and [AlignYourBodyRow] passes no [Modifier] so the default is used.
 */
@Composable
fun AlignYourBodyElement(
    @DrawableRes drawable: Int,
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size = 88.dp)
                .clip(shape = CircleShape)
        )
        Text(
            text = stringResource(id = text),
            modifier = Modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

/**
 * Step: Favorite collection card - Material Surface. Our root Composable is a [Surface] which uses
 * our [Modifier] parameter [modifier] as its `modifier` argument, and the `medium`
 * [RoundedCornerShape] of [MaterialTheme.shapes] (in our case a [RoundedCornerShape] with 16.dp
 * corners). Its `color` argument is the [ColorScheme.surfaceVariant] color of its [MaterialTheme]
 * (which in our case this is Color(0xFFE7E1DE) (a very light Gray) for `lightColorScheme` and
 * Color(0xFF494644) (a very dark Gray) for `darkColorScheme`. The contents of the [Surface] is a
 * [Row] with a [Modifier.width] `modifier` of 255.dp, and whose `verticalAlignment` is
 * [Alignment.CenterVertically] (its children are vertically centered in it). The [Row] holds an
 * [Image] that displays the [Drawable] whose resource ID is our [drawable] parameter, with its size
 * set to 80.dp by the [Modifier.size] used as its `modifier` argument and whose `contentScale`
 * argument setting the aspect ratio scaling to be [ContentScale.Crop] (scales the source uniformly,
 * maintaining the source's aspect ratio, so that both width and height dimensions of the source will
 * be equal to or larger than the corresponding dimension of the destination). This [Image] is then
 * followed by a [Text] Composable that displays the string whose resource ID is our [text] parameter,
 * using the `titleMedium` [TextStyle] of [MaterialTheme.typography] (in our case the [FontFamily]
 * whose [FontWeight.Bold] is provided by the [Font] with ID `R.font.lato_bold` (the file
 * lato_bold.ttf) whose `fontSize` is 16.sp, and whose `letterSpacing` is (0.15).sp, and whose
 * `lineHeight` is 24.sp), and as its `modifier` argument it uses a [Modifier.padding] whose
 * `horizontal` padding is 16.dp
 *
 * @param drawable the resource ID of the [Drawable] that we are supposed to display in our [Image]
 * @param text the resource ID of the [String] that we are supposed to use as the text of our [Text]
 * @param modifier a [Modifier] that our caller can use to modify our behavior and appearance. We
 * use it as the `modifier` argument of our root [Surface] Composable. The [FavoriteCollectionCardPreview]
 * preview uses a [Modifier.padding] of 8.dp, and [FavoriteCollectionsGrid] uses a [Modifier.height]
 * of 80.dp to have the preferred height of our content to be exactly 80.dp
 */
@Composable
fun FavoriteCollectionCard(
    @DrawableRes drawable: Int,
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(width = 255.dp)
        ) {
            Image(
                painter = painterResource(id = drawable),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size = 80.dp)
            )
            Text(
                text = stringResource(id = text),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

/**
 * Step: Align your body row - Arrangements. Our root Composable is a [LazyRow] that uses our [modifier]
 * parameter as its `modifier` argument, uses [Arrangement.spacedBy] as its `horizontalArrangement`
 * argument to space its children by 8.dp, and uses a `contentPadding` of 16.dp as the [PaddingValues]
 * for the ends of its content. The contents of the [LazyRow] is an [items] which uses the [List] of
 * [DrawableStringPair] in our [alignYourBodyData] field to provide the `drawable` resource ID and
 * `text` resource ID for a call to our [AlignYourBodyElement] for each [DrawableStringPair] in the
 * [List].
 *
 * @param modifier a [Modifier] that our caller can specify to modify our behavior or appearance. In
 * our case none of our callers specify one so the empty, default, or starter [Modifier] that
 * contains no elements is used instead.
 */
@Composable
fun AlignYourBodyRow(
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        items(alignYourBodyData) { item: DrawableStringPair ->
            AlignYourBodyElement(drawable = item.drawable, text = item.text)
        }
    }
}

/**
 * Step: Favorite collections grid - LazyGrid. Our root Composable is a [LazyHorizontalGrid] that
 * uses a [GridCells.Fixed] of 2 for its `rows` argument to specify that it is a grid with 2 rows,
 * uses a [PaddingValues] of 16.dp `horizontal` for its `contentPadding` to put 16.dp padding at the
 * ends of its content, uses [Arrangement.spacedBy] of 16.dp for both its `horizontalArrangement` and
 * its `verticalArrangement` to put 16.dp between its children cells, and adds a [Modifier.height]
 * of 168.dp to our [Modifier] parameter [modifier] to set its height to be 168.dp. The `content` of
 * the [LazyHorizontalGrid] are [items] of [FavoriteCollectionCard] that are created to display the
 * [List] of [DrawableStringPair] `drawable` and `text` resource IDs in our [favoriteCollectionsData]
 * field with a [Modifier.height] of 80.dp setting the height of each cell to be 80.dp
 *
 * @param modifier a [Modifier] that our caller can specify to modify our behavior or appearance. In
 * our case none of our callers specify one so the empty, default, or starter [Modifier] that
 * contains no elements is used instead.
 */
@Composable
fun FavoriteCollectionsGrid(
    modifier: Modifier = Modifier
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(count = 2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier.height(height = 168.dp)
    ) {
        items(favoriteCollectionsData) { item: DrawableStringPair ->
            FavoriteCollectionCard(
                drawable = item.drawable,
                text = item.text,
                modifier = Modifier.height(height = 80.dp)
            )
        }
    }
}

/**
 * Step: Home section - Slot APIs. This Composable holds a [Text] that displays a title string whose
 * resource ID is our [title] parameter and our Composable [content] parameter in a [Column].
 * [HomeScreen] uses it for the [AlignYourBodyRow] and [FavoriteCollectionsGrid] Composables. Our
 * root Composable is a [Column] that we pass our [Modifier] parameter [modifier] to as its
 * `modifier` argument. The `content` of the [Column] is a [Text] displaying the string with
 * resource ID [title], using the `titleMedium` [TextStyle] of [MaterialTheme.typography] (in our
 * case the [FontFamily] whose [FontWeight.Bold] is provided by the [Font] with ID `R.font.lato_bold`
 * (the file lato_bold.ttf) whose `fontSize` is 16.sp, and whose `letterSpacing` is (0.15).sp, and
 * whose `lineHeight` is 24.sp), and its `modifier` argument is a [Modifier.padding] whose
 * `horizontal` argument is 16.dp (adds 16.dp to each end of the [Text]), to which is chained a
 * [Modifier.paddingFromBaseline] whose `top` padding is 40.dp, and whose `bottom` padding is 16.dp.
 * Below this title [Text] in our [Column] is our [content] parameter Composable.
 *
 * @param title the resource ID of the title string that should be displayed in our [Text].
 * @param modifier a [Modifier] that can be used by our caller to modify our appearance and behavior,
 * none of our callers pass us one so the empty, default, or starter [Modifier] that contains no
 * elements is used instead.
 * @param content the Composable that we should use in our "slot".
 */
@Composable
fun HomeSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .paddingFromBaseline(top = 40.dp, bottom = 16.dp)
        )
        content()
    }
}

/**
 * Step: Home screen - Scrolling. Our root Composable is a [Column] whose `modifier` argument is
 * our [Modifier] parameter [modifier] with a [Modifier.verticalScroll] added to it that uses a
 * [rememberScrollState] as its `state` argument (this makes the [Column] vertically scrollable).
 * The children of the [Column] are
 *  - a [Spacer] with its `modifier` argument a [Modifier.height] of 16.dp
 *  - a [SearchBar] with its `modifier` argument a [Modifier.padding] of 16.dp `horizontal`
 *  - a [HomeSection] whose `title` is the string with resource ID `R.string.align_your_body`
 *  ("Align your body") and a `content` Composable of [AlignYourBodyRow]
 *  - a [HomeSection] whose `title` is the string with resource ID `R.string.favorite_collections`
 *  ("Favorite Collections") and a `content` Composable of [FavoriteCollectionsGrid]
 *  - a [Spacer] with its `modifier` argument a [Modifier.height] of 16.dp
 *
 * @param modifier a [Modifier] that can be used by our caller to modify our appearance and behavior,
 * the [MySootheAppPortrait] Composable passes us a [Modifier.padding] that uses the [PaddingValues]
 * that [Scaffold] passes to its `content` Composable lambda as the `padding` to use along each edge
 * of our content's left, top, right and bottom. We in turn use [modifier] for our [Column], chaining
 * a [Modifier.verticalScroll] with a [rememberScrollState] as its `state` argument onto it to make
 * the [Column] scrollable. [MySootheAppLandscape] and [ScreenContentPreview] pass us no [Modifier]
 * so a default, or starter [Modifier] that contains no elements is used instead.
 *
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(state = rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(height = 16.dp))
        SearchBar(modifier = Modifier.padding(horizontal = 16.dp))
        HomeSection(title = R.string.align_your_body) {
            AlignYourBodyRow()
        }
        HomeSection(title = R.string.favorite_collections) {
            FavoriteCollectionsGrid()
        }
        Spacer(modifier = Modifier.height(height = 16.dp))
    }
}

/**
 * Step: Bottom navigation - Material. We use this as the `bottomBar` argument (the bottom bar of
 * the screen) of the [Scaffold] in the [MySootheApp] Composable. Our root Composable is a
 * [NavigationBar] whose `containerColor` argument is the [ColorScheme.surfaceVariant] color of
 * [MaterialTheme.colorScheme] which is Color(0xFFE7E1DE) (Light gray) for [lightColorScheme] and
 * Color(0xFF494644) (Very dark gray) for [darkColorScheme], and whose modifier` argument is our
 * [Modifier] parameter [modifier]. Its children Composables are two [NavigationBarItem]:
 *  - First one uses as the `label` the string with resource ID `R.string.bottom_navigation_home`
 *  ("HOME"), and its [Icon] argument `icon` is the [ImageVector] drawn by [Icons.Filled.Spa]
 *  ([Icons.Default] is an alias for [Icons.Filled])
 *  - Second one uses as the `label` the string with resource ID `R.string.bottom_navigation_profile`
 *  ("PROFILE"), and its [Icon] argument `icon` is the [ImageVector] drawn by
 *  [Icons.Filled.AccountCircle] ([Icons.Default] is an alias for [Icons.Filled])
 *
 * The `selected` argument of the "HOME" [NavigationBarItem] is hard coded to be `true`, and the
 * `selected` argument of the "PROFILE" [NavigationBarItem] is hard coded to be `false`, and the
 * `onClick` argument of both is an empty lambda.
 *
 * @param modifier a [Modifier] that can be used by our caller to modify our appearance and behavior,
 * the preview [BottomNavigationPreview] uses a [Modifier.padding] whose `top` is 24.dp, and the
 * [Scaffold] in the [MySootheAppPortrait] Composable does not specify a value so the empty, default,
 * or starter [Modifier] that contains no elements is used instead.
 */
@Composable
private fun SootheBottomNavigation(modifier: Modifier = Modifier) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null
                )
            },
            label = {
                Text(text = stringResource(id = R.string.bottom_navigation_home))
            },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            },
            label = {
                Text(text = stringResource(id = R.string.bottom_navigation_profile))
            },
            selected = false,
            onClick = {}
        )
    }
}

/**
 * Step: Navigation Rail - Material. We use this in the [MySootheAppLandscape] Composable (which is
 * used by [MySootheApp] when the [WindowSizeClass] of the device is [WindowWidthSizeClass.Expanded]).
 * Our root Composable content is a [NavigationRail] containing a [Column] whose `modifier` argument
 * is our [modifier] parameter with a [Modifier.fillMaxHeight] chained to it (its content will fill
 * the entire incoming height constraints), whose `verticalArrangement` is [Arrangement.Center]
 * (place children such that they are as close as possible to the middle of the main axis), and whose
 * `horizontalAlignment` argument is [Alignment.CenterHorizontally] (centers children horizontally).
 * The [Column] contains two [NavigationRailItem] Composables with a [Spacer] between them that is
 * 8.dp high.
 *  - The first [NavigationRail] uses as its `icon` argument an [Icon] which draws
 *  [Icons.Filled.AccountCircle] ([Icons.Default] is an alias for [Icons.Filled]), and for its
 *  `label` argument it uses a [Text] displaying the string with resource ID
 *  `R.string.bottom_navigation_home` ("HOME").
 *  - The second [NavigationRail] uses as its `icon` argument an [Icon] which draws
 *  [Icons.Filled.AccountCircle] ([Icons.Default] is an alias for [Icons.Filled]), and for its
 *  `label` argument it uses a [Text] displaying the string with resource ID
 *  `R.string.bottom_navigation_profile` ("PROFILE").
 *
 * The `selected` argument of the "HOME" [NavigationRailItem] is hard coded to be `true`, and the
 * `selected` argument of the "PROFILE" [NavigationRailItem] is hard coded to be `false`, and the
 * `onClick` argument of both is an empty lambda.
 *
 * @param modifier a [Modifier] that can be used by our caller to modify our appearance and behavior,
 * neither of our callers ([MySootheAppLandscape] and [NavigationRailPreview]) pass us one so the
 * empty, default, or starter [Modifier] that contains no elements is used instead.
 */
@Composable
private fun SootheNavigationRail(modifier: Modifier = Modifier) {
    NavigationRail(
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(id = R.string.bottom_navigation_home))
                },
                selected = true,
                onClick = {}
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(id = R.string.bottom_navigation_profile))
                },
                selected = false,
                onClick = {}
            )
        }
    }
}

/**
 * Step: MySoothe App - Scaffold. This is the Composable that we use as the root view of our activity.
 * We `when` branch on the value of the [WindowSizeClass.widthSizeClass] field of our [WindowSizeClass]
 * parameter [windowSize]:
 *  - [WindowWidthSizeClass.Compact] (Represents the majority of phones in portrait) we compose our
 *  [MySootheAppPortrait] Composable into our app.
 *  - [WindowWidthSizeClass.Expanded] (Represents the majority of tablets in landscape and large
 *  unfolded inner displays in landscape) we compose our [MySootheAppLandscape] Composable into
 *  our app.
 */
@Composable
fun MySootheApp(windowSize: WindowSizeClass) {
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            MySootheAppPortrait()
        }

        WindowWidthSizeClass.Expanded -> {
            MySootheAppLandscape()
        }
    }
}

/**
 * This is the Composable that is used when the [WindowSizeClass.widthSizeClass] of the device is
 * [WindowWidthSizeClass.Compact] (Represents the majority of phones in portrait). Wrapped in our
 * [MySootheTheme] custom [MaterialTheme] is a [Scaffold] whose `bottomBar` argument (the bottom bar
 * of the screen) is our [SootheBottomNavigation] Composable (it contains a [NavigationBar] with two
 * [NavigationBarItem] children "HOME" and "PROFILE"). Our `content` is a [HomeScreen] Composable
 * whose `modifier` argument is a [Modifier.padding] with its `paddingValues` argument the
 * [PaddingValues] that are necessary to properly offset top and bottom bars which [Scaffold] passes
 * to its `content` lambd.
 */
@Composable
fun MySootheAppPortrait() {
    MySootheTheme {
        Scaffold(
            bottomBar = { SootheBottomNavigation() }
        ) { padding: PaddingValues ->
            HomeScreen(modifier = Modifier.padding(paddingValues = padding))
        }
    }
}

/**
 * This is the Composable that is used when the [WindowSizeClass.widthSizeClass] of the device is
 * [WindowWidthSizeClass.Expanded] (Represents the majority of tablets in landscape and large
 * unfolded inner displays in landscape). Wrapped in our  [MySootheTheme] custom [MaterialTheme] is
 * a [Surface] whose `color` argument (The background color) is the [ColorScheme.background] color
 * of our [MaterialTheme] (Color(0xFFF5F0EE) "White" for our [lightColorScheme] and Color(0xFF32302F)
 * "Black" for our [darkColorScheme]). The `content` of the [Surface] is a [Row] whose children our
 * our [SootheNavigationRail] Composable, and our [HomeScreen] Composable.
 */
@Composable
fun MySootheAppLandscape() {
    MySootheTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row {
                SootheNavigationRail()
                HomeScreen()
            }
        }
    }
}

/**
 * This [List] of [DrawableStringPair] is used to populate the [LazyRow] in the [AlignYourBodyRow]
 * Composable. The [DrawableStringPair.drawable] of each element in the [List] is the resource ID of
 * a [Drawable] to display in the [AlignYourBodyElement] created from the element, and the
 * [DrawableStringPair.text] of each element in the [List] is the resource ID of a [String] to
 * display in the [AlignYourBodyElement] created from the element.
 */
private val alignYourBodyData: List<DrawableStringPair> = listOf(
    R.drawable.ab1_inversions to R.string.ab1_inversions,
    R.drawable.ab2_quick_yoga to R.string.ab2_quick_yoga,
    R.drawable.ab3_stretching to R.string.ab3_stretching,
    R.drawable.ab4_tabata to R.string.ab4_tabata,
    R.drawable.ab5_hiit to R.string.ab5_hiit,
    R.drawable.ab6_pre_natal_yoga to R.string.ab6_pre_natal_yoga
).map { DrawableStringPair(it.first, it.second) }

/**
 * This [List] of [DrawableStringPair] is used to populate the [LazyHorizontalGrid] in the
 * [FavoriteCollectionsGrid] Composable. The [DrawableStringPair.drawable] of each element in the
 * [List] is the resource ID of a [Drawable] to display in the [FavoriteCollectionCard] created from
 * the element, and the [DrawableStringPair.text] of each element in the [List] is the resource ID
 * of a [String] to display in the [FavoriteCollectionCard] created from the element.
 */
private val favoriteCollectionsData = listOf(
    R.drawable.fc1_short_mantras to R.string.fc1_short_mantras,
    R.drawable.fc2_nature_meditations to R.string.fc2_nature_meditations,
    R.drawable.fc3_stress_and_anxiety to R.string.fc3_stress_and_anxiety,
    R.drawable.fc4_self_massage to R.string.fc4_self_massage,
    R.drawable.fc5_overwhelmed to R.string.fc5_overwhelmed,
    R.drawable.fc6_nightly_wind_down to R.string.fc6_nightly_wind_down
).map { DrawableStringPair(it.first, it.second) }

/**
 * Data class holding a [Drawable] resource ID, and a [String] resource ID in its [drawable] and
 * [text] fields respectively.
 *
 * @param drawable a [Drawable] resource ID
 * @param text a [String] resource ID
 */
private data class DrawableStringPair(
    @param:DrawableRes val drawable: Int,
    @param:StringRes val text: Int
)

/**
 * The Preview for our [SearchBar] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun SearchBarPreview() {
    MySootheTheme { SearchBar(Modifier.padding(8.dp)) }
}

/**
 * The Preview for our [AlignYourBodyElement] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun AlignYourBodyElementPreview() {
    MySootheTheme {
        AlignYourBodyElement(
            text = R.string.ab1_inversions,
            drawable = R.drawable.ab1_inversions,
            modifier = Modifier.padding(8.dp)
        )
    }
}

/**
 * The Preview for our [FavoriteCollectionCard] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun FavoriteCollectionCardPreview() {
    MySootheTheme {
        FavoriteCollectionCard(
            text = R.string.fc2_nature_meditations,
            drawable = R.drawable.fc2_nature_meditations,
            modifier = Modifier.padding(8.dp)
        )
    }
}

/**
 * The Preview for our [FavoriteCollectionsGrid] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun FavoriteCollectionsGridPreview() {
    MySootheTheme { FavoriteCollectionsGrid() }
}

/**
 * The Preview for our [AlignYourBodyRow] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun AlignYourBodyRowPreview() {
    MySootheTheme { AlignYourBodyRow() }
}

/**
 * The Preview for our [HomeSection] Composable holding a [AlignYourBodyRow] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun HomeSectionPreview() {
    MySootheTheme {
        HomeSection(R.string.align_your_body) {
            AlignYourBodyRow()
        }
    }
}

/**
 * The Preview for our [HomeScreen] Composable
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE, heightDp = 180)
@Composable
fun ScreenContentPreview() {
    MySootheTheme { HomeScreen() }
}

/**
 * The Preview for our [SootheBottomNavigation] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun BottomNavigationPreview() {
    MySootheTheme { SootheBottomNavigation(Modifier.padding(top = 24.dp)) }
}

/**
 * The Preview for our [SootheNavigationRail] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun NavigationRailPreview() {
    MySootheTheme { SootheNavigationRail() }
}

/**
 * The Preview for our [MySootheAppPortrait] Composable.
 */
@Preview(widthDp = 360, heightDp = 640)
@Composable
fun MySoothePortraitPreview() {
    MySootheAppPortrait()
}

/**
 * The Preview for our [MySootheAppLandscape] Composable.
 */
@Preview(widthDp = 640, heightDp = 360)
@Composable
fun MySootheLandscapePreview() {
    MySootheAppLandscape()
}
