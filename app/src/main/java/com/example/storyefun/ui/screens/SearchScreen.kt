package com.example.storyefun.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.storyefun.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var selectedBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    var active by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = { newQuery ->
                query = newQuery
                if (newQuery.isNotEmpty()) {
                    performSearch(db, newQuery) { results ->
                        searchResults = results.sortedBy { it.first }
                    }
                } else {
                    searchResults = emptyList()
                }
            },
            onSearch = {
                active = false
                if (query.isNotEmpty()) {
                    scope.launch {
                        searchAndFetchBooks(db, query) { books ->
                            selectedBooks = books
                        }
                    }
                }
            },
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
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            searchResults.forEach { (name, id) ->
                ListItem(
                    modifier = Modifier.clickable {
                        scope.launch {
                            searchAndFetchBooks(db, query) { books ->
                                selectedBooks = books
                            }
                        }
                        navController.navigate("bookDetail/$id")
                    },
                    headlineContent = {
                        Text(text = name)
                    },
                )
            }
        }

        BookList(
            books = selectedBooks,
            onBookClick = { book ->
                navController.navigate("bookDetail/${book.id}")
            }
        )
    }
}

fun searchAndFetchBooks(
    db: FirebaseFirestore,
    query: String,
    onResult: (List<Book>) -> Unit
) {
    db.collection("books")
        .whereGreaterThanOrEqualTo("name", query)
        .whereLessThan("name", query + '\uf8ff')
        .get()
        .addOnSuccessListener { documents ->
            val books = documents.map { doc ->
                Book(
                    id = doc.id,
                    name = doc.getString("name") ?: "Unknown",
                    author = doc.getString("author") ?: "Unknown",
                    description = doc.getString("description") ?: "No Description",
                    imageUrl = doc.getString("imageUrl") ?: ""
                )
            }
            println("Books fetched: $books")
            onResult(books)
        }
        .addOnFailureListener { exception ->
            println("Error fetching books: ${exception.message}")
            onResult(emptyList())
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

@Composable
fun BookList(
    books: List<Book>,
    onBookClick: (Book) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(books) { book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { onBookClick(book) }
            ) {
                Row(modifier = Modifier.padding(10.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Book Name: ${book.name}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Author: ${book.author}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Description: ${book.description}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    AsyncImage(
                        model = book.imageUrl,
                        contentDescription = "Book Cover",
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}