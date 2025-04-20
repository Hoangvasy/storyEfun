@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.storyefun.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.storyefun.R
import com.example.storyefun.data.models.Book
import com.example.storyefun.viewModel.BookViewModel
import com.example.storyefun.ui.components.BottomBar
import com.example.storyefun.ui.components.Header
import com.example.storyefun.ui.theme.AppColors
import com.example.storyefun.ui.theme.AppTheme
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.viewModel.ThemeViewModel
import com.example.storyefun.viewModel.searchBooks
import kotlinx.coroutines.delay
import okhttp3.internal.http2.Header

@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, themeViewModel: ThemeViewModel ) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var books by remember { mutableStateOf<List<Book>>(emptyList())}
    val isDarkMode by themeViewModel.isDarkTheme.collectAsState()



    AppTheme(darkTheme = isDarkMode) {
        val colors = LocalAppColors.current

        Scaffold(
            topBar = {
                Header(
////                    text = text,
////                    active = active,
////                    onQueryChange = { text = it },
////                    onActiveChange = { active = it },
                    navController = navController
                )
            },
            bottomBar = { BottomBar(navController, "home") }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                // In light mode, use a background image; in dark mode, use theme background color
                if (!isDarkMode) {
//                    Image(
//                        painter = colors.background(),
//                        contentDescription = "background",
//                        contentScale = ContentScale.FillBounds,
//                        modifier = Modifier
//                            .matchParentSize()
//                            .graphicsLayer(alpha = 0.5f)
//                    )
                } else {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(colors.backgroundColor)
                    )
                }

                // Main content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    item {SearchBar(onSearch = it)}
//                    item { Banner() }
                    item { Channels(navController = navController, theme = colors) }
                    item { BookStory(navController = navController, theme = colors) }
//                    item { ContinueRead() }
                }
            }
        }
    }
}

@Composable
fun BookList(books: List<Book>) {
    if (books.isEmpty()) {
        Text("Không tìm thấy sách nào!")
    } else {
        LazyColumn {
            itemsIndexed(books) { index, book ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Tên: ${book.name}")
                        Text("Tác giả: ${book.author}")
                        Text("Mô tả: ${book.description}")
                    }
                }
            }
        }
    }
}

// BANNER
@Composable
fun Banner() {
    val images = listOf(
        rememberAsyncImagePainter("https://i.pinimg.com/736x/f1/ab/0d/f1ab0db3bae065ccb0d4af75e45f5072.jpg"),
        rememberAsyncImagePainter("https://i.pinimg.com/736x/2c/92/15/2c921587443963ef76414b6b26d37f7b.jpg"),
        rememberAsyncImagePainter("https://i.pinimg.com/736x/30/6f/1a/306f1ae0f32e02b286638e12d3ea2782.jpg"),
        rememberAsyncImagePainter("https://i.pinimg.com/736x/40/b8/97/40b89794a0abecb2801db9f251309ced.jpg"),
        rememberAsyncImagePainter("https://i.pinimg.com/736x/e3/1f/d2/e31fd247b18e988e36bbd5787b38af60.jpg")
    )

    val pagerState = rememberPagerState(pageCount = { images.size })

    // Chuyen dong
    LaunchedEffect(Unit) {
        while (true){
            delay(3000)
            val nextPage = (pagerState.currentPage + 1)%pagerState.pageCount
            pagerState.scrollToPage(nextPage)
        }
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box (modifier = Modifier.wrapContentSize()){
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.wrapContentSize()
                    .padding(3.dp)
            ) { currentPage ->
                Card (
                    modifier = Modifier.wrapContentSize()
                        .padding(3.dp),
                    elevation = CardDefaults.cardElevation(5.dp)
                ){
                    Image(
                        painter = images[currentPage],
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        PageIndicator(
            pageCount = images.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
        )
    }
}
// Dots
@Composable
fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier.Companion) {
    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
    ){
        repeat(pageCount){
            IndicatorDots(isSelected = it == currentPage, modifier = Modifier)
        }
    }
}
// Dots
@Composable
fun IndicatorDots(isSelected: Boolean, modifier:Modifier) {
    val size = animateDpAsState(targetValue = if(isSelected) 12.dp else 10.dp, label = "")
    Box(modifier = Modifier.padding(2.dp)
        .size(size.value)
        .clip(CircleShape)
        .background(if(isSelected) Color(0xFF780000) else Color(0xFFD3D3D3)))
}

@Composable
fun BookStory(
    navController: NavController,
    theme: AppColors,
    title: String = "Books",
    viewModel: BookViewModel = viewModel()
) {
    val books = viewModel.books.observeAsState(emptyList())

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 5.dp)) {

        HeaderRow(navController, title, theme)

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(books.value) { _, book ->
                Box(modifier = Modifier.width(150.dp).height(250.dp)) {
                    Card(
                        modifier = Modifier.wrapContentSize().padding(5.dp)
                            .clickable { navController.navigate("bookDetail/${book.id}") }
                        ,
                        elevation = CardDefaults.cardElevation(5.dp)
                    ) {
                        Column {
                            if (!book.imageUrl.isNullOrEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(book.imageUrl)
                                            .crossfade(true)
                                            .build()
                                    ),
                                    contentDescription = "Book Cover",
                                    modifier = Modifier.fillMaxWidth()
                                        .fillMaxHeight(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .background(Color.Gray)
                                ) {
                                    Text(
                                        text = "No Image Available",
                                        modifier = Modifier.align(Alignment.Center),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun HeaderRow(navController: NavController, title: String, theme: AppColors) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
            )
        )
        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {
                navController.navigate("category") },
            modifier = Modifier.width(80.dp).align(Alignment.CenterVertically)
        ) {
            Text(
                text = "Xem tất cả",
                style = TextStyle(fontStyle = FontStyle.Italic),
                color = theme.textSecondary
            )
        }
    }
}


@Composable
fun Channels(navController: NavController, theme: AppColors, title: String = "Hãng truyện") {
    val images = listOf(
        rememberAsyncImagePainter("https://i.pinimg.com/736x/5d/b6/37/5db6377d25a3a8955fddd92541282aa4.jpg"),
        rememberAsyncImagePainter("https://i.pinimg.com/736x/c6/f6/e0/c6f6e05b95ede8e55e06cba17e4507d4.jpg"),
        rememberAsyncImagePainter("https://i.pinimg.com/474x/2b/fa/33/2bfa33c6c330498792ffdb73a70bd1f8.jpg"),
        rememberAsyncImagePainter("https://i.pinimg.com/736x/0f/a9/0b/0fa90bd5c402250ed2157e09543cf971.jpg"),
        rememberAsyncImagePainter("https://i.pinimg.com/736x/9a/bd/c5/9abdc53ede4fab53d5cc75e324a37ef9.jpg")
    )

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)) {

        HeaderRow(navController, title, theme)

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(images) { _, imagePainter ->
                Box(modifier = Modifier.width(150.dp).height(80.dp)) {
                    Card(
                        modifier = Modifier.wrapContentSize().padding(5.dp),
                        elevation = CardDefaults.cardElevation(5.dp)
                    ) {
                        Image(
                            painter = imagePainter,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun ContinueRead() {
    var theme = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Continue Reading",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = theme.textPrimary

                )
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {},
                modifier = Modifier
                    .width(80.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Xem tất cả",
                    style = TextStyle(fontStyle = FontStyle.Italic),
                    color = theme.textSecondary
                )
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.bannerhome),
                contentDescription = "Banner",
                modifier = Modifier.fillMaxWidth()
            )
            Image(
                painter = painterResource(id = R.drawable.poster1),
                contentDescription = "Overlay Image",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.5f)
                    .height(250.dp)
            )
        }
    }
}

@Composable
fun Stories(navController: NavController) {
    val theme = LocalAppColors.current
    val backgroundImages = listOf(
        R.drawable.banner2,
        R.drawable.banner3,
        R.drawable.banner4,
        R.drawable.bannerhome,
        R.drawable.bannerhome
    )
    val overlayImages = listOf(
        R.drawable.poster2,
        R.drawable.poster3,
        R.drawable.poster6,
        R.drawable.poster5,
        R.drawable.poster4
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Truyện ngắn")
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {},
                modifier = Modifier
                    .width(80.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Xem tất cả",
                    style = TextStyle(fontStyle = FontStyle.Italic),
                    color = theme.textSecondary,
                    modifier = Modifier.clickable { navController.navigate("category") }

                )

            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("bookDetail") }
        ) {
            itemsIndexed(backgroundImages) { index, backgroundImage ->
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(150.dp)
                ) {
                    Image(
                        painter = painterResource(id = backgroundImage),
                        contentDescription = "Banner",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Image(
                        painter = painterResource(id = overlayImages[index]),
                        contentDescription = "Overlay",
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(0.5f)
                            .height(250.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    // For preview purposes, you can create a dummy ThemeViewModel and NavController if needed.
    HomeScreen(navController = rememberNavController(), themeViewModel = ThemeViewModel())
}