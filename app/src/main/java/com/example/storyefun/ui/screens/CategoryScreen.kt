package com.example.storyefun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.storyefun.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.storyefun.ui.components.BottomBar
import com.example.storyefun.ui.components.Header

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid as LazyVerticalGrid

@Composable
fun CategoryScreen(navController : NavController) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val tabs = listOf(
        "Popular", "Bestseller", "Newest", "Coming Soon",
        "Top Rated", "Trending", "Editor Picks", "Free Books", "Daily Deals"
    )
    var selectedTab by remember { mutableStateOf(tabs[0]) }

    // Dữ liệu mẫu cho mỗi tab
    val popularBooks = listOf(
        R.drawable.poster2, R.drawable.poster3, R.drawable.poster4
    )
    val bestsellerBooks = listOf(
        R.drawable.poster5, R.drawable.poster6, R.drawable.poster3
    )

    val books = listOf(
        Book("Things Fall Apart", "Chinua Achebe", 3.5f, "300 pages", R.drawable.poster6),
        Book("Jane Eyre", "Charlotte Bronte", 4.0f, "280 pages", R.drawable.poster3),
        Book("Lararium", "Paulo Coelho", 3.8f, "250 pages", R.drawable.poster2)
    )

    Scaffold(
        topBar = { Header(text, active, onQueryChange = { text = it }, onActiveChange = { active = it }, navController) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image
            Image(
                painter = painterResource(R.drawable.background),
                contentDescription = "background",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer(alpha = 0.5f)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Categories",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // Tabs popular, bestseller,...
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tabs) { tab ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (tab == selectedTab) MaterialTheme.colorScheme.primary else Color.Gray,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .clickable { selectedTab = tab } // Thay đổi selectedTab khi nhấn
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }

                // Danh sách poster
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val booksToShow = when (selectedTab) {
                            "Popular" -> popularBooks
                            "Bestseller" -> bestsellerBooks
                            else -> listOf() // Dữ liệu giả cho các tab khác
                        }
                        items(booksToShow) { bookRes ->
                            Image(
                                painter = painterResource(bookRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(200.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface,
                                        shape = MaterialTheme.shapes.medium
                                    )
                            )
                        }
                    }
                }

                // danh sách đề xuất
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Tiêu đề cho danh sách
                        Text(
                            text = "You might like",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Danh sách các sách
                        books.forEach { book ->
                            BookCard(book)
                            Divider(
                                color = Color.Gray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
//            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(book.imageRes),
                contentDescription = book.title,
                modifier = Modifier.size(60.dp)
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = book.title,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Author: ${book.author}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${book.rating} ★ | ${book.pages}",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Icon for adding a book
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

data class Book(
    val title: String,
    val author: String,
    val rating: Float,
    val pages: String,
    val imageRes: Int
)

@Preview
@Composable
fun PreviewCategory() {
    CategoryScreen(rememberNavController())
}

