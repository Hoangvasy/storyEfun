package com.example.storyefun.ui.components

import android.util.Log
import androidx.compose.foundation.Image
//import androidx.compose.foundation.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.storyefun.data.models.Book
import com.example.storyefun.ui.theme.LocalAppColors
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SearchBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.storyefun.ui.screens.SearchScreen


fun fetchBooks(query: String, searchType: String, callback: (List<Book>) -> Unit) {
    val db = Firebase.firestore
    val queryRef = if (searchType == "name") {
        db.collection("books").whereGreaterThanOrEqualTo("name", query)
    } else {
        db.collection("books").whereArrayContains("category", query)
    }

    queryRef.get()
        .addOnSuccessListener { documents ->
            val books = documents.map { document ->
                document.toObject(Book::class.java)
            }
            callback(books)
        }
        .addOnFailureListener { exception ->
            callback(emptyList())
            Log.e("Search", "Error fetching books", exception)
        }
}

data class Book(
    val name: String = "",
    val category: List<String> = emptyList()
)

@Composable
fun Header(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    var theme = LocalAppColors.current

    var searchQuery by remember { mutableStateOf("") }
    var searchType by remember { mutableStateOf("name") }
    var searchResults by remember { mutableStateOf<List<Book>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    fun performSearch(query: String) {
        isLoading = true
        fetchBooks(query, searchType) { books ->
            searchResults = books
            isLoading = false
        }
    }

//    Column(modifier = Modifier.fillMaxWidth()) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.White)
//        ) {
//            Image(
//                painter = rememberAsyncImagePainter("https://i.pinimg.com/736x/6b/e9/17/6be91716ac90da6cdbac6421d78c7534.jpg"),
//                contentDescription = "Background Image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                    ) {
                        Text(
                            text = "ストリエフン",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { navController.navigate("home") }
                        )
                        Text(
                            text = "STORYEFUN",
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable { navController.navigate("home") }
                        )
                    }

                    // Right
                    Row {
                        IconButton(onClick = { navController.navigate("search") }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "search",
                            )
                        }
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "person",
                            )
                        }
                        IconButton(onClick = { navController.navigate("setting") }) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "settings"
//
                            )
                        }
                    }
                }

            }

            }
