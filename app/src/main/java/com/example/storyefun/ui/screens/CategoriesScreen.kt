package com.example.storyefun.ui.screens

import android.R.attr.onClick
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.storyefun.R
import com.google.android.material.chip.Chip
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.storyefun.data.models.Book
import com.example.storyefun.data.models.Category
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberImagePainter


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CategoriesScreen(navController: NavController) {
    val firestore = Firebase.firestore
    val categories = remember { mutableStateListOf<Category>() }
    val books = remember { mutableStateListOf<Book>() }
    val selectedCategoryId = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        firestore.collection("categories").get()
            .addOnSuccessListener { result ->
                categories.clear()
                for (document in result) {
                    val category = document.toObject(Category::class.java)
                    categories.add(category)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load categories: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    LaunchedEffect(selectedCategoryId.value) {
        selectedCategoryId.value?.let { categoryId ->
            firestore.collection("books")
                .whereArrayContains("categoryIDs", categoryId)
                .get()
                .addOnSuccessListener { result ->
                    books.clear()
                    for (document in result) {
                        val book = document.toObject(Book::class.java)
                        books.add(book)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to load books: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategoryId.value == category.id

                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCategoryId.value = category.id },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color.LightGray else Color.Transparent
                        )
                    ) {
                        Text(
                            text = category.name,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.Gray
            )

            if (selectedCategoryId.value != null) {
                BookList1(navController, books = books)
            }
        }
    }
}

@Composable
fun BookList1(navController: NavController, books: List<Book>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        items(books) { book ->
            Card(
                modifier = Modifier
                    .width(120.dp)
                    .wrapContentHeight()
                    .clickable { navController.navigate("bookDetail/${book.id}") },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                ) {
                    Image(
                        painter = rememberImagePainter(book.imageUrl),
                        contentDescription = book.name,
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = book.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                    )
                    Text(
                        text = book.author,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    val navController = rememberNavController()
    CategoriesScreen(navController = navController)
}
