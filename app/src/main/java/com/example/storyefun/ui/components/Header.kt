package com.example.storyefun.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.storyefun.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Book(
    val name: String = "",
    val category: List<String> = emptyList()
)

fun fetchBooks(query: String, searchType: String, callback: (List<Book>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
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

@Composable
fun Header(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var coinBalance by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch user's coin balance
    LaunchedEffect(auth.currentUser?.uid) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect
        isLoading = true
        try {
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            coinBalance = (userDoc.getLong("coin")?.toInt() ?: userDoc.get("coin") as? Int ?: 0)
            Log.d("Header", "Fetched coin balance: $coinBalance for userId=$userId")
        } catch (e: Exception) {
            Log.e("Header", "Error fetching coin balance: ${e.message}", e)
            coinBalance = 0
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFFFF))
            .padding(top = 5.dp, bottom = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Logo and Name
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .clickable { navController.navigate("home") },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ストリエフン",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = "STORYEFUN",
                    fontSize = 15.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }

            // Right: Icons and Coin Balance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { navController.navigate("search") }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isLoading) "..." else coinBalance.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_coin),
                        contentDescription = "Coin Balance",
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.Top),
                        tint = Color.Gray
                    )
                    IconButton(
                        onClick = { navController.navigate("desposite") },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Deposit",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    Header(navController = rememberNavController())
}