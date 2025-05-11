package com.example.storyefun.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.storyefun.data.models.Book
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

@Composable
fun RecommendedBookScreen(navController: NavController) {
    val firestore = Firebase.firestore
    val books = remember { mutableStateListOf<Book>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        firestore.collection("books")
            .orderBy("views", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                books.clear()
                for (document in result) {
                    val book = document.toObject(Book::class.java)
                    if (book.views >= 100) {
                        books.add(book)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load books: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recommended", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Books List
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books.size) { index ->
                val book = books[index]
                BookCard(
                    title = book.name,
                    author = book.author,
                    imageUrl = book.imageUrl ?: "",
                    onClick = {
                        navController.navigate("bookDetail/${book.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun BookCard(title: String, author: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = title,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
//                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = author,
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 1,
//                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
