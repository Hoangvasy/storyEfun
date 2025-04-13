package com.example.storyefun.ui.screens


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.storyefun.R
import com.example.storyefun.data.Book
import com.example.storyefun.viewModel.BookViewModel
import com.example.storyefun.ui.components.*
import com.example.storyefun.ui.theme.AppColors
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.ui.theme.ThemeViewModel
import coil.compose.AsyncImage


@Composable
fun BookDetailScreen(navController: NavController, bookId : String,themeViewModel: ThemeViewModel = viewModel(),  viewModel: BookViewModel = viewModel()) {
    var theme = LocalAppColors.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val isDarkMode by themeViewModel.isDarkTheme.collectAsState()
    val book by viewModel.book.observeAsState()

    LaunchedEffect(bookId) {
        viewModel.fetchBook(bookId)
        Log.e("info of lauched book: ", book.toString())

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        if (!isDarkMode) {


        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(theme.backgroundColor)
            )
        }

        if (book != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    var text by remember { mutableStateOf("") }
                    var active by remember { mutableStateOf(false) }
                    Header(text, active, onQueryChange = { text = it }, onActiveChange = { active = it }, navController)
                }

                item { MangaInfo(theme, book!!) }  // safe to use !! here after null check

                item {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = theme.backgroundContrast2
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Thông tin", color = theme.backgroundColor) }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Chapter", color = theme.backgroundColor) }
                        )
                    }
                }

                item {
                    when (selectedTabIndex) {
                        0 -> InformationSection(navController, theme, book!!)
                        1 -> ChapterListSection(theme, book!!, navController)
                    }
                }
            }
        } else {
            // You can show loading or empty state here
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = theme.textPrimary)
            }
        }

    }
}



@Composable
fun MangaInfo(theme: AppColors, book: Book) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp)
    ) {
        // Box to hold both banner and poster (overlay)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f) // Tỷ lệ khung hình 16:9 cho banner
        ) {
            // Banner image (background)
            AsyncImage(
                model = book.posterUrl,
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.error),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)) // Bo góc cho banner
                    .background(Color.Gray.copy(alpha = 0.5f)) // Debug background
            )

            // Poster image (overlay on the left)
            AsyncImage(
                model = book.imageUrl,
                contentDescription = "Poster",
                contentScale = ContentScale.FillWidth,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.error),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(185.dp)
                    .fillMaxHeight()
                    .padding(start = 16.dp)
                    .clip(RoundedCornerShape(12.dp)) // Bo góc cho poster
            )
        }

        // Book details (below the images)
        Text(
            text = book.name,
            fontSize = 24.sp,
            color = theme.textPrimary,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )
        Text(
            text = book.author,
            fontSize = 16.sp,
            color = theme.textSecondary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )

        // Stats Row
        Row(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "👁️ ${book.views}  |  📅 ${book.follows}  |  ❤️ ${book.likes}K",
                fontSize = 14.sp,
                color = theme.textSecondary
            )
        }

        Divider(
            color = theme.textSecondary,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}

@Composable
fun GenreTag(text: String, theme: AppColors) {
    Text(
        text = text,
        maxLines = 1, // Ensure text stays on one line
        overflow = TextOverflow.Clip, // Clip text if it overflows (or use Ellipsis if desired)
        softWrap = false, // Prevent wrapping in the middle of a word
        modifier = Modifier
            .wrapContentWidth() // Let the width wrap content
            .padding(4.dp)
            .background(Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        color = theme.backgroundColor
    )
}



@Composable
fun InformationSection(navController: NavController, theme: AppColors, book: Book)
{
    val genres = book.category
    Column(modifier = Modifier.padding(8.dp)) {
        Box(modifier = Modifier.height(200.dp)) { // ✅ Fixed height to avoid infinite constraints
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 90.dp),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genres.size) { index ->
                    GenreTag(genres[index].name, theme)
                }
            }
        }

        Text(
            text = book.description,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp),
            color = theme.textPrimary
        )
    }

    Button(
        onClick = { /* Start Reading */ },
        colors = ButtonDefaults.buttonColors(Color.Red),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text("Bắt đầu đọc", fontSize = 18.sp, color = theme.textPrimary,
            modifier = Modifier.clickable { navController.navigate("reading/${book.id}/1/1") }

        )

    }
    CommentSection(theme)
}

@Composable
fun ChapterListSection(theme: AppColors, book: Book, navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Volumes ( ${book.volume.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp),
            color = theme.textPrimary
        )
        Text(
            text = "Cập nhật mới nhất 29/02/2024",
            fontSize = 14.sp,
            color = theme.textSecondary,
            modifier = Modifier.padding(bottom = 12.dp), // Space below the update info
        )

        // Sample chapters
        for (volume in book.volume) {
            Text(
                text = "Tập ${volume.name}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,

                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp) ,
                color = theme.textPrimary

            )
            for (chapter in volume.chapters) {
                Text(
                    text = "Chương ${chapter.title}",
                    fontSize = 14.sp,
                    color = theme.textSecondary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 6.dp) // Indent and space chapters
                        .clickable {
                            // Điều hướng đến màn hình đọc khi nhấp vào chapter
                            navController.navigate("reading/${book.id}/${volume.order}/${chapter.order}")
                        }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MangaDetailScreenPreview() {
    // stop preview when add parameter to class
//     BookDetailScreen()
}