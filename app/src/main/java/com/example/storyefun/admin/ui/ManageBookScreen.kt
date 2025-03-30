package com.example.storyefun.admin.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.storyefun.R
import com.example.storyefun.admin.viewModel.BookViewModel
import com.example.storyefun.data.Book
import com.example.storyefun.data.BookRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
@Composable
fun ManageBooksScreen(navController: NavController, viewModel: BookViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Newest") }
    val isLoading by viewModel.isLoading.observeAsState(false)
    val books by viewModel.books.observeAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // 🔍 Search & Sorting Row
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search books...") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                DropdownMenuButton(sortOption) { selected ->
                    sortOption = selected
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val filteredBooks = books.filter { it.name.contains(searchQuery, ignoreCase = true) }
            val sortedBooks = when (sortOption) {
                "Most Viewed" -> filteredBooks.sortedByDescending { it.views }
                "Most Liked" -> filteredBooks.sortedByDescending { it.likes }
                else -> filteredBooks // Default is newest, assuming books are already ordered
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sortedBooks) { book ->
                    BookAdminItem(book, navController, viewModel)
                }
            }

            // ➕ Floating Add Button
            FloatingActionButton(
                onClick = { navController.navigate("uploadBook") },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }

        // 🔄 Centered Loading Overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Transparent overlay
                    .wrapContentSize(Alignment.Center) // Centers the loading indicator
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

// 🔽 Sorting Dropdown
@Composable
fun DropdownMenuButton(currentSort: String, onSortSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Newest", "Most Viewed", "Most Liked")

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(currentSort)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSortSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 📖 Book Item for Admin Panel
@Composable
fun BookAdminItem(book: Book, navController: NavController, viewModel: BookViewModel) {
    Card(
        modifier = Modifier.padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberAsyncImagePainter(book.imageUrl),
                contentDescription = "Book Cover",
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(book.name, style = MaterialTheme.typography.headlineSmall)
            Text("By ${book.author}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Views: ${book.views}", style = MaterialTheme.typography.bodySmall)
                Text("Likes: ${book.likes}", style = MaterialTheme.typography.bodySmall)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(
                    onClick = { viewModel.deleteBook(book.id) },
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book) {
    println("Book Image URL: ${book.imageUrl}") // Debugging

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Display Book Cover Image
            if (!book.imageUrl.isNullOrEmpty()) {
                println("Rendering image: ${book.imageUrl}") // Debugging
                Image(
                    painter = rememberAsyncImagePainter(
                        model = book.imageUrl,
                        placeholder = painterResource(R.drawable.placeholder), // Placeholder image
                        error = painterResource(R.drawable.error), // Error image
                        onError = { result ->
                            // Access the throwable using result.throwable
                            println("Image loading failed: ${result.result.throwable}") // Log the error
                        },
                        onSuccess = {
                            println("Image loaded successfully") // Log success
                        }
                    ),
                    contentDescription = "Book Cover",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                println("Image URL is null or empty") // Debugging
            }

            // Display Book Details
            Text(
                text = book.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "By ${book.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = book.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Display Metadata (Type, Follows, Likes, Views)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Type: ${book.type}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Follows: ${book.follows}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Likes: ${book.likes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Views: ${book.views}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}