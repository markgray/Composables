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

@file:Suppress("UNUSED_PARAMETER", "unused") // Suggested changes would make class less reusable

package com.codelab.basiclayouts

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
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
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.codelab.basiclayouts.ui.theme.gray900
import com.codelab.basiclayouts.ui.theme.taupe100
import java.util.Locale

/**
 * This is the main activity of the solution of the "Basic Layouts in Compose Codelab"
 * https://developer.android.com/codelabs/jetpack-compose-layouts
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call [setContent] to have it Compose the composable [MySootheApp] into our activity.
     * The content will become the root view of the activity.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MySootheApp() }
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
 *  in different states. We use the defaults produced by [TextFieldDefaults.textFieldColors] except
 *  for the `backgroundColor` which we replace with the `surface` color of [MaterialTheme.colors]
 *  ([Color.White] with an alpha of 0.85 for the `LightColorPalette`, and [Color.White] with an alpha
 *  of 0.15 for the `DarkColorPalette`)
 *  - `placeholder` - placeholder to be displayed when the text field is in focus and the input text
 *  is empty, we use the string with resource ID [R.string.placeholder_search] ("Search").
 *  - `modifier` - the [Modifier] for the [TextField], we use our parameter [modifier] as a starter
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
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
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
 * `style` argument of the `h3` font of [MaterialTheme.typography]. The `h3` font in our custom
 * [Typography] uses [FontWeight.Bold] as the `fontWeight`, 14.sp as the `fontSize`, a `letterSpacing`
 * of 0.sp and the default `defaultFontFamily` of `fontFamilyLato`, so the [Font] is loaded from the
 * resource ID [R.font.lato_bold] (which is the file "font/lato_bold.ttf"). The `modifier` argument
 * of the [Text] is [Modifier.paddingFromBaseline] with the padding from the top of the layout to
 * the baseline of the first line of text in the content given by `top` = 24.dp, and the distance
 * from the baseline of the last line of text in the content to the bottom of the layout given by
 * `bottom` = 8.dp.
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
            painter = painterResource(drawable),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
        )
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.h3,
            modifier = Modifier.paddingFromBaseline(
                top = 24.dp, bottom = 8.dp
            )
        )
    }
}

/**
 * Step: Favorite collection card - Material Surface. Our root Composable is a [Surface] which uses
 * our [Modifier] parameter [modifier] as its `modifier` argument, and the `small` [RoundedCornerShape]
 * of [MaterialTheme.shapes] (in our case a [RoundedCornerShape] with 4.dp corners). The contents of
 * the [Surface] is a [Row] with a [Modifier.width] `modifier` of 192.dp, and whose `verticalAlignment`
 * is [Alignment.CenterVertically] (its children are vertically centered in it). The [Row] holds an
 * [Image] that displays the [Drawable] whose resource ID is our [drawable] parameter, with its size
 * set to 56.dp by the [Modifier.size] used as its `modifier` argument and whose `contentScale`
 * argument setting the aspect ratio scaling to be [ContentScale.Crop] (scales the source uniformly,
 * maintaining the source's aspect ratio, so that both width and height dimensions of the source will
 * be equal to or larger than the corresponding dimension of the destination). This [Image] is then
 * followed by a [Text] Composable that displays the string whose resource ID is our [text] parameter,
 * using the `h3` [TextStyle] of [MaterialTheme.typography] (in our case the [FontFamily] whose
 * [FontWeight.Bold] is provided by the [Font] with ID [R.font.lato_bold] (the file lato_bold.ttf)
 * whose `fontSize` is 14.sp, and whose `letterSpacing` is 0.sp), and as its `modifier` argument it
 * uses a [Modifier.padding] whose `horizontal` padding is 16.dp
 *
 * @param drawable the resource ID of the [Drawable] that we are supposed to display in our [Image]
 * @param text the resource ID of the [String] that we are supposed to use as the text or our [Text]
 * @param modifier a [Modifier] that our caller can use to modify our behavior and appearance. We
 * use it as the `modifier` argument of our root [Surface] Composable. The [FavoriteCollectionCardPreview]
 * preview uses a [Modifier.padding] of 8.dp, and [FavoriteCollectionsGrid] uses a [Modifier.height]
 * of 56.dp to have the preferred height of our content to be exactly 56.dp
 */
@Composable
fun FavoriteCollectionCard(
    @DrawableRes drawable: Int,
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(192.dp)
        ) {
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp)
            )
            Text(
                text = stringResource(text),
                style = MaterialTheme.typography.h3,
                modifier = Modifier.padding(horizontal = 16.dp)
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        items(alignYourBodyData) { (drawable, text): DrawableStringPair ->
            AlignYourBodyElement(drawable = drawable, text = text)
        }
    }
}

/**
 * Step: Favorite collections grid - LazyGrid. Our root Composable is a [LazyHorizontalGrid] that
 * uses a [GridCells.Fixed] of 2 for its `rows` argument to specify that it is a grid with 2 rows,
 * uses a [PaddingValues] of 16.dp `horizontal` for its `contentPadding` to put 16.dp padding at the
 * ends of its content, uses [Arrangement.spacedBy] of 8.dp for both its `horizontalArrangement` and
 * its `verticalArrangement` to put 8.dp between its children cells, and adds a [Modifier.height]
 * of 120.dp to our [Modifier] parameter [modifier] to set its height to be 120.dp. The `content` of
 * the [LazyHorizontalGrid] are [items] of [FavoriteCollectionCard] that are created to display the
 * [List] of [DrawableStringPair] `drawable` and `text` resource IDs in our [favoriteCollectionsData]
 * field with a [Modifier.height] of 56.dp setting the height of each cell to be 56.dp
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
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(120.dp)
    ) {
        items(favoriteCollectionsData) { (drawable, text): DrawableStringPair ->
            FavoriteCollectionCard(
                drawable = drawable,
                text = text,
                modifier = Modifier.height(56.dp)
            )
        }
    }
}

/**
 * Step: Home section - Slot APIs. This Composable holds a [Text] that displays a title string whose
 * resource ID is our [title] and a Composable [content] in a [Column]. [HomeScreen] uses it for the
 * [AlignYourBodyRow] and [FavoriteCollectionsGrid] Composables. Our root Composable is a [Column]
 * that we pass our [Modifier] parameter [modifier] to as its ``modifier` argument. The `content` of
 * the [Column] is a [Text] displaying an uppercase version of the string with resource ID [title]
 * using the `h2` [TextStyle] of our custom [MaterialTheme.typography] which is a `fontFamilyKulim`
 * [FontFamily] that uses the [Font] with resource ID [R.font.lato_regular] (the lato_regular.ttf
 * font file), with a `fontSize` of 15.sp and `letterSpacing` of (1.15).sp, and its `modifier`
 * argument is a [Modifier.paddingFromBaseline] whose `top` padding is 40.dp, and whose `bottom`
 * padding is 8.dp, and a [Modifier.padding] whose `horizontal` argument is 16.dp adds 16.dp to each
 * end of the [Text]. Below this title [Text] in our [Column] is our [content] Composable.
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
            text = stringResource(title).uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.h2,
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
                .padding(horizontal = 16.dp)
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
 *  - a [HomeSection] whose `title` is the string with resource ID [R.string.align_your_body]
 *  ("Align your body") and a `content` Composable of [AlignYourBodyRow]
 *  - a [HomeSection] whose `title` is the string with resource ID [R.string.favorite_collections]
 *  ("Favorite Collections") and a `content` Composable of [FavoriteCollectionsGrid]
 *  - a [Spacer] with its `modifier` argument a [Modifier.height] of 16.dp
 *
 * @param modifier a [Modifier] that can be used by our caller to modify our appearance and behavior,
 * the [MySootheApp] Composable passes us a [Modifier.padding] that uses the [PaddingValues] that
 * [Scaffold] passes to its `content` Composable lambda as the `padding` to use along each edge of
 * our content's left, top, right and bottom. We in turn use [modifier] for our [Column], chaining
 * a [Modifier.verticalScroll] with a [rememberScrollState] as its `state` argument onto it to make
 * the [Column] scrollable.
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
 * [BottomNavigation] whose `backgroundColor` is the `background` [Color] of [MaterialTheme.colors]
 * ([taupe100] for the `LightColorPalette` and [gray900] for the `DarkColorPalette`), and whose
 * `modifier` argument is our [Modifier] parameter [modifier]. Its children Composables are two
 * [BottomNavigationItem]:
 *  - First one uses as the `label` the string with resource ID [R.string.bottom_navigation_home]
 *  ("HOME"), and its [Icon] argument `icon` is the [ImageVector] drawn by [Icons.Filled.Spa]
 *  ([Icons.Default] is an alias for [Icons.Filled])
 *  - Second one uses as the `label` the string with resource ID [R.string.bottom_navigation_profile]
 *  ("PROFILE"), and its [Icon] argument `icon` is the [ImageVector] drawn by
 *  [Icons.Filled.AccountCircle] ([Icons.Default] is an alias for [Icons.Filled])
 *
 * The `selected` argument of the "HOME" [BottomNavigationItem] is hard coded to be `true`, and the
 * `selected` argument of the "PROFILE" [BottomNavigationItem] is hard coded to be `false`, and the
 * `onClick` argument of both is an empty lambda.
 *
 * @param modifier a [Modifier] that can be used by our caller to modify our appearance and behavior,
 * the preview [BottomNavigationPreview] uses a [Modifier.padding] whose `top` is 24.dp, and the
 * [Scaffold] in the [MySootheApp] Composable does not specify a value so the empty, default, or
 * starter [Modifier] that contains no elements is used instead.
 */
@Composable
private fun SootheBottomNavigation(modifier: Modifier = Modifier) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_home))
            },
            selected = true,
            onClick = {}
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_profile))
            },
            selected = false,
            onClick = {}
        )
    }
}

/**
 * Step: MySoothe App - Scaffold. This is the Composable that we use as the root view of our activity.
 * Wrapped in our [MySootheTheme] custom [MaterialTheme] is a [Scaffold] whose `bottomBar` argument
 * (the bottom bar of the screen) is our [SootheBottomNavigation] Composable (it contains a
 * [BottomNavigation] with two [BottomNavigationItem] children, "HOME" and "PROFILE"). Our `content`
 * is a [HomeScreen] Composable whose `modifier` argument is a [Modifier.padding] with its
 * `paddingValues` argument the [PaddingValues] that are necessary to properly offset top and bottom
 * bars.
 */
@Composable
fun MySootheApp() {
    MySootheTheme {
        Scaffold(
            bottomBar = { SootheBottomNavigation() }
        ) { padding: PaddingValues ->
            HomeScreen(modifier = Modifier.padding(paddingValues = padding))
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
private val favoriteCollectionsData: List<DrawableStringPair> = listOf(
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
    @DrawableRes val drawable: Int,
    @StringRes val text: Int
)

/**
 * The Preview for our [SearchBar] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun SearchBarPreview() {
    MySootheTheme { SearchBar(Modifier.padding(8.dp)) }
}

/**
 * The Preview for our [AlignYourBodyElement] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
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
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
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
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun FavoriteCollectionsGridPreview() {
    MySootheTheme { FavoriteCollectionsGrid() }
}

/**
 * The Preview for our [AlignYourBodyRow] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun AlignYourBodyRowPreview() {
    MySootheTheme { AlignYourBodyRow() }
}

/**
 * The Preview for our [HomeSection] Composable with an [AlignYourBodyRow] in its slot.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun HomeSectionPreview() {
    MySootheTheme {
        HomeSection(R.string.align_your_body) {
            AlignYourBodyRow()
        }
    }
}

/**
 * The Preview for our [HomeScreen] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun ScreenContentPreview() {
    MySootheTheme { HomeScreen() }
}

/**
 * The Preview for our [SootheBottomNavigation] Composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun BottomNavigationPreview() {
    MySootheTheme { SootheBottomNavigation(Modifier.padding(top = 24.dp)) }
}

/**
 * The Preview for our [MySootheApp] Composable.
 */
@Preview(widthDp = 360, heightDp = 640)
@Composable
fun MySoothePreview() {
    MySootheApp()
}
