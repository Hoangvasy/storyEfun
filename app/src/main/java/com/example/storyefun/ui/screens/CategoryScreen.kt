@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.storyefun.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.storyefun.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController) {
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
                )
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                HeroSection()
            }
            item {
                FilterBar(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://i.pinimg.com/736x/87/1c/59/871c59a95205840c0b884d7a425b7481.jpg"
                ),
                contentDescription = "Hero Image",
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

@Composable
fun FetchCategories(): List<String> {
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        try {
            val snapshot = firestore.collection("categories").get().await()
            categories = snapshot.documents.mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        // Optionally display a loading indicator
//        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }

    return categories
}

@Composable
fun FetchBooksByCategory(category: String): List<Book> {
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(category) {
        val firestore = FirebaseFirestore.getInstance()
        try {
            val snapshot = firestore.collection("books")
                .whereEqualTo("category", category)
                .get()
                .await()
            books = snapshot.documents.mapNotNull { doc ->
                Book(
                    name = doc.getString("title") ?: "",
                    author = doc.getString("author") ?: "",
                    imageUrl = doc.getString("coverImageUrl") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        // Optionally display a loading indicator
//        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }

    return books
}

@Composable
fun FilterBar(
    modifier: Modifier = Modifier,
    filterViewModel: FilterViewModel = viewModel()
) {
    val categories = FetchCategories()
    val selectedIndex by filterViewModel.selectedIndex
    val selectedCategory by filterViewModel.selectedCategory

    Column(modifier = modifier.fillMaxWidth().padding(8.dp)) {
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

        // Display books based on selected category
        if (selectedCategory.isNotEmpty()) {
            BookListForCategory(selectedCategory)
        }
    }
}

@Composable
fun BookListForCategory(category: String) {
    val books = FetchBooksByCategory(category)

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { book ->
            StoryItem(book)
        }
    }
}

@Composable
fun StoryItem(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter(book.imageUrl),
                contentDescription = book.name,
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
                Text(book.name, style = MaterialTheme.typography.headlineSmall)
                Text(book.author, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
