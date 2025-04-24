package com.example.storyefun.ui.screens

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.storyefun.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var active by remember { mutableStateOf(false) }
    val db = FirebaseFirestore.getInstance()


    SearchBar(
        query = query,
        onQueryChange = { newQuery ->
            query = newQuery
            if (newQuery.isNotEmpty()) {
                performSearch(db, newQuery) { results ->
                    searchResults = results
                }
            } else {
                searchResults = emptyList()
            }
        },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Search for books...") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
        },
        trailingIcon = if (active) {
            {
                IconButton(onClick = { if (query.isNotEmpty()) query = "" else active = false }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }
        } else null
    ) {
        searchResults.forEach { (name, id) ->
            ListItem(
                modifier = Modifier.clickable {
                    getBookDetails(db, id) { book ->
                        selectedBook = book
                    }
                    active = false
                },
                headlineContent = {
                    Text(text = name)
                },
            )
        }

    }

    selectedBook?.let { book ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp) // Thêm khoảng cách giữa text và ảnh
                ) {
                    Text(text = "Book Name: ${book.id}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Author: ${book.name}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Description: ${book.author}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                AsyncImage(
                    model = book.description, // Thay thành book.imageUrl nếu đó là URL ảnh
                    contentDescription = "Book Cover",
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp), // Đảm bảo chiều cao cố định
                    contentScale = ContentScale.Crop // Cắt ảnh nếu không vừa
                )
            }
        }
    }
}

fun performSearch(db: FirebaseFirestore, query: String, onResult: (List<Pair<String, String>>) -> Unit) {
    db.collection("books")
        .whereGreaterThanOrEqualTo("name", query)
        .whereLessThan("name", query + '\uf8ff')
        .get()
        .addOnSuccessListener { documents ->
            val results = documents.mapNotNull { doc ->
                val name = doc.getString("name")
                val id = doc.id
                if (name != null) name to id else null
            }
            val name = documents.mapNotNull { it.getString("name") }
            println("Search results: $name")

            println("Search results: $results")
            onResult(results)
        }
        .addOnFailureListener { exception ->
            println("Error: ${exception.message}")
            onResult(emptyList())
        }
}

fun getBookDetails(db: FirebaseFirestore, bookId: String, onResult: (Book?) -> Unit) {
    db.collection("books")
        .document(bookId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val name = document.getString("name") ?: "No Name"
                val author = document.getString("author") ?: "No Author"
                val description = document.getString("description") ?: "No Description"
                val imageUrl = document.getString("imageUrl") ?: ""
                onResult(Book(name, author, description, imageUrl))
            } else {
                println("No document found for ID: $bookId")
                onResult(null)
            }
        }
        .addOnFailureListener { exception ->
            println("Error fetching book details: ${exception.message}")
            onResult(null)
        }
}

