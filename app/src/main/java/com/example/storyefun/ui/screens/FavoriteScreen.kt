package com.example.storyefun.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.storyefun.R
import com.example.storyefun.ui.components.BottomBar
import com.example.storyefun.ui.components.Header

// Sample data class for Book
data class FavoriteScreen(
    val title: String,
    val coverImage: Int,
    val progress: Float,
    val author: String = "Unknown Author",
    val rating: Int = 0
)

val sampleBooks = listOf(
    FavoriteScreen("Book 1", R.drawable.poster4, 80f, "Author 1", 4),
    FavoriteScreen("Book 2", R.drawable.poster5, 50f, "Author 2", 3),
    FavoriteScreen("Book 3", R.drawable.poster6, 30f, "Author 3", 5)
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteScreen(navController: NavController) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
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
                    Recents()
                }
                item {
                    Favorite()
                }
            }
        }
    }
}

@Composable
fun Recents() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Title
        Text(
            text = "Recents",
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
        )

        // Horizontal list of books
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(sampleBooks) { book ->
                BookCard(book = book)
            }
            item {
                DiscoverMoreCard {
                    // Handle discover more action here
                }
            }
        }
    }
}

@Composable
fun BookCard(book: FavoriteScreen) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(200.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = book.coverImage),
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = book.progress / 100,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Text(
                text = "${book.progress.toInt()}%",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun DiscoverMoreCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "+ Discover More",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
val books = listOf(
    Book("Things Fall Apart", "Chinua Achebe", 3.5f, "300 pages", R.drawable.poster6),
    Book("Jane Eyre", "Charlotte Bronte", 4.0f, "280 pages", R.drawable.poster3),
    Book("Lararium", "Paulo Coelho", 3.8f, "250 pages", R.drawable.poster2)
)
@Composable
fun Favorite() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Tiêu đề
        Text(
            text = "Favorite",
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
        )
        // Danh sách sách
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
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