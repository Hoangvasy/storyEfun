package com.example.storyefun.admin.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.storyefun.R
import com.example.storyefun.admin.data.Book
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
@Composable
fun ManageBooksScreen(navController: NavController) {
    val books = remember { mutableStateListOf<Book>() }
    val db = Firebase.firestore

    // Back button


    // Fetch books from Firestore
    LaunchedEffect(Unit) {
        db.collection("books")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val book = document.toObject(Book::class.java)
                    books.add(book.copy(id = document.id)) // Add document ID
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
                println("Error fetching books: $exception")
            }
    }

    // Display books in a list
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
            )
        }
        Text(
            text = "Manage Books",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (books.isEmpty()) {
            Text("No books found.")
        } else {
            LazyColumn {
                items(books) { book ->
                    BookItem(book = book)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

    }
    Spacer(modifier = Modifier.height(18.dp))

    IconButton(onClick = { navController.navigate("UploadBook") }) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add book",
        )
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