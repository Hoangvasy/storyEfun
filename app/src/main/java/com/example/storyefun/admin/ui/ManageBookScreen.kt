package com.example.storyefun.admin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.storyefun.R
import com.example.storyefun.data.models.Book
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.viewModel.BookViewModel

@Composable
fun ManageBooksScreen(navController: NavController, viewModel: BookViewModel = viewModel()) {
    val theme = LocalAppColors.current
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Newest") }
    val isLoading by viewModel.isLoading.observeAsState(false)
    val books by viewModel.books.observeAsState(emptyList())
    val selectedItem = remember { mutableStateOf("manageBooks")}
    val snackbarHostState = remember { SnackbarHostState() }

    AdminDrawer(
        navController = navController,
        drawerState = rememberDrawerState(DrawerValue.Closed),
        selectedItem = selectedItem

    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding) // Xử lý PaddingValues từ Scaffold
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

                    val filteredBooks =
                        books.filter { it.name.contains(searchQuery, ignoreCase = true) }
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
                            .weight(1f),
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
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Book",
                        tint = theme.textPrimary
                    )
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
    }
}

@Composable
fun BookAdminItem(book: Book, navController: NavController, viewModel: BookViewModel) {
    val theme = LocalAppColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp), // Tăng chiều cao để ảnh lớn hơn
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
                    .weight(4f) // Tăng weight để ảnh chiếm nhiều không gian hơn
                    .clip(RoundedCornerShape(8.dp))
                    .shadow(4.dp, RoundedCornerShape(8.dp)) // Thêm bóng
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF7043).copy(alpha = 0.1f),
                                Color(0xFFFFA726).copy(alpha = 0.1f)
                            )
                        )
                    ) // Gradient nền nhẹ
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp)) // Viền trắng nhẹ
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = book.imageUrl,
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.error)
                    ),
                    contentDescription = "Book Cover",
                    contentScale = ContentScale.Crop, // Ảnh lấp đầy khung
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
            }

            // Thông tin sách
            Column(modifier = Modifier.padding(top = 12.dp)) {
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

                Spacer(modifier = Modifier.height(8.dp))

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
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("addVolume/${book.id}") },
                    modifier = Modifier
                        .width(40.dp)
                        .height(28.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Icon(
                        Icons.Default.Add, contentDescription = null, tint = theme.textPrimary
                    )
                }
                Button(
                    onClick = { navController.navigate("editBook/${book.id}") },
                    modifier = Modifier
                        .width(40.dp)
                        .height(28.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Icon(
                        Icons.Default.Edit, contentDescription = null, tint = theme.textPrimary
                    )
                }
                Button(
                    onClick = { viewModel.deleteBook(book.id) },
                    modifier = Modifier
                        .width(40.dp)
                        .height(28.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Icon(
                        Icons.Default.Delete, contentDescription = null, tint = theme.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownMenuButton(currentSort: String, onSortSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Newest", "Most Viewed", "Most Liked")

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Sort Options")
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
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.error),
                        onError = { result ->
                            println("Image loading failed: ${result.result.throwable}")
                        },
                        onSuccess = {
                            println("Image loaded successfully")
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

            // Display Metadata
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