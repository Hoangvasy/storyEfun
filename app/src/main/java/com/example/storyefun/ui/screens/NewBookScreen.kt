package com.example.storyefun.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import androidx.compose.material3.Card
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.storyefun.data.models.Book
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

@Composable
fun NewBookScreen(navController: NavController) {
    val firestore = Firebase.firestore
    val books = remember { mutableStateListOf<Book>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        firestore.collection("books")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(4)
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New arrivals", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Select all",
                    color = Color(0xFF00897B),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { }
                )
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(books.size) { index ->
                    val book = books[index]
                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                navController.navigate("bookDetail/${book.id}")
                            },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Row(modifier = Modifier.background(Color(0xFFFFFFFF))) {
                            Image(
                                painter = rememberImagePainter(book.imageUrl),
                                contentDescription = book.name,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .height(150.dp)
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp)
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(book.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                                Text(book.author, color = Color.Gray, fontSize = 14.sp, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${book.follows} Followers", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${book.likes} Likes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}