package com.example.storyefun.admin.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.storyefun.R
import com.example.storyefun.viewModel.BookViewModel
import com.example.storyefun.data.Book
import com.example.storyefun.data.BookRepository
import com.example.storyefun.ui.screens.HomeScreen
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.ui.theme.ThemeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun ManageBooksScreen(navController: NavController, viewModel: BookViewModel = viewModel()) {
    val theme = LocalAppColors.current
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Newest") }
    val isLoading by viewModel.isLoading.observeAsState(false)
    val books by viewModel.books.observeAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 🔍 Search & Sort Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search books...") },
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                DropdownMenuButton(sortOption) { selected -> sortOption = selected }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredBooks = books.filter { it.name.contains(searchQuery, ignoreCase = true) }
            val sortedBooks = when (sortOption) {
                "Most Viewed" -> filteredBooks.sortedByDescending { it.views }
                "Most Liked" -> filteredBooks.sortedByDescending { it.likes }
                else -> filteredBooks
            }

            // Scrollable list of books
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Ensure this takes up available space
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sortedBooks) { book ->
                    BookAdminItem(book, navController, viewModel)
                }
            }
        }

        // Floating action button
        FloatingActionButton(
            onClick = { navController.navigate("uploadBook") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = theme.buttonOrange,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Book", tint = theme.textPrimary)
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}


@Composable
fun BookAdminItem(book: Book, navController: NavController, viewModel: BookViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), // Giảm aspectRatio để card cao hơn, tạo không gian cho ảnh lớn hơn
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Ảnh bìa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f) // Tăng weight để ảnh to hơn, chiếm nhiều không gian hơn
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray) // Nền xám nhạt để thấy khung ảnh (có thể đổi thành Color.Transparent)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = book.imageUrl,
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.error)
                    ),
                    contentDescription = "Book Cover",
                    contentScale = ContentScale.Fit, // Hiển thị toàn bộ ảnh, không cắt
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
            }

            // Thông tin sách
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "By ${book.author}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "👁 ${book.views}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = Color.Gray
                    )
                    Text(
                        "❤️ ${book.likes}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = Color.Gray
                    )
                }
            }

            // Nút hành động
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("addVolume/${book.id}") },
                    modifier = Modifier
                        .width(40.dp)
                        .height(26.dp) // to ra
                    ,
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Icon(
                        Icons.Default.Add, contentDescription = null
                    )
                }
                Button(
                    onClick = { navController.navigate("editBook/${book.id}") },
                    modifier = Modifier
                        .width(40.dp)
                        .height(26.dp) // to ra

                    ,
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Icon(
                        Icons.Default.Edit, contentDescription = null
                    )
                }
                Button(
                    onClick = { viewModel.deleteBook(book.id) },
                    modifier = Modifier
                        .width(40.dp)
                        .height(26.dp) // to ra
                    ,
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Icon(
                        Icons.Default.Delete, contentDescription = null
                    )
                }
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