@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
package com.example.storyefun.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController : NavController) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Categories",
                                fontSize = 18.sp,
                                color = Color(0xFF8B0000),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* Handle menu click */ }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
            }
        },
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                HeroSection()
            }
            item {
                FilterBar(modifier = Modifier, filterViewModel = viewModel())
            }

        }
    }
}

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://i.pinimg.com/736x/87/1c/59/871c59a95205840c0b884d7a425b7481.jpg"
                ),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

class FilterViewModel : ViewModel() {
    private val _selectedIndex = mutableStateOf(-1)
    val selectedIndex: State<Int> = _selectedIndex

    private val _selectedCategory = mutableStateOf("")
    val selectedCategory: State<String> = _selectedCategory

    fun updateSelectedCategory(index: Int, category: String) {
        _selectedIndex.value = index
        _selectedCategory.value = category
    }
}

val categories = listOf("Tiểu thuyết", "Truyện ngắn", "Kinh dị", "Hành động", "Lãng mạn", "Hài hước- vui")
val categoryStories = mapOf(
    "Tiểu thuyết" to listOf(
        Story1("Story1", "Author A", "https://link-to-image.com/cover3.jpg"),
        Story1("Story2", "Author B", "https://i.pinimg.com/736x/87/1c/59/871c59a95205840c0b884d7a425b7481.jpg"),
        Story1("Story3", "Author C", "https://link-to-image.com/cover3.jpg")
    ),
    "Truyện ngắn" to listOf(
        Story1("Story1 4", "Author D", "https://link-to-image.com/cover4.jpg"),
        Story1("Story1 5", "Author E", "https://link-to-image.com/cover5.jpg")
    ),
    "Kinh dị" to listOf(
        Story1("Story1 6", "Author F", "https://link-to-image.com/cover6.jpg"),
        Story1("Story1 7", "Author G", "https://link-to-image.com/cover7.jpg"),
        Story1("Story1 8", "Author H", "https://link-to-image.com/cover8.jpg")
    ),
    "Hành động" to listOf(
        Story1("Story1 9", "Author I", "https://link-to-image.com/cover9.jpg"),
        Story1("Story1 10", "Author J", "https://link-to-image.com/cover10.jpg")
    ),
    "Lãng mạn" to listOf(
        Story1("Story1 11", "Author K", "https://link-to-image.com/cover11.jpg"),
        Story1("Story1 12", "Author L", "https://link-to-image.com/cover12.jpg")
    ),
    "Hài hước- vui" to listOf(
        Story1("Story1 13", "Author M", "https://link-to-image.com/cover13.jpg"),
        Story1("Story1 14", "Author N", "https://link-to-image.com/cover14.jpg")
    ),
)

@Composable
fun FetchCategories(): List<String> {
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        try {
            val snapshot = firestore.collection("categories").get().await()
            categories = snapshot.documents.mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return categories
}

@Composable
fun FilterBar(
    modifier: Modifier = Modifier,
    filterViewModel: FilterViewModel = viewModel()
) {
    val selectedIndex by filterViewModel.selectedIndex
    val selectedCategory by filterViewModel.selectedCategory

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEachIndexed { index, label ->
                TextButton(
                    onClick = {
                        filterViewModel.updateSelectedCategory(index, label)
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (selectedIndex == index) Color.Blue else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    if (selectedCategory.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
//            Text("Danh sách truyện cho $selectedCategory:", style = MaterialTheme.typography.titleMedium)
            categoryStories[selectedCategory]?.forEach { story ->
                StoryItem(story)
            }
        }
    }
}

@Composable
fun StoryItem(story: Story1) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter(story.coverImageUrl),
                contentDescription = story.title,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(story.title, style = MaterialTheme.typography.headlineSmall)
                Text(story.author, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

data class Story1(
    val title: String,
    val author: String,
    val coverImageUrl: String
)