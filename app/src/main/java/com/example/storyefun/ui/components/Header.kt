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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.storyefun.R
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.viewModel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun Header(
    modifier: Modifier = Modifier,
    navController: NavController,
    themeViewModel: ThemeViewModel
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var coinBalance by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val theme = LocalAppColors.current // Access theme colors

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
            .background(theme.header.copy(0.8f)) // Use updated theme.header
            .padding(top = 3.dp, bottom = 3.dp) // Reduced padding

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp), // Reduced horizontal padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Logo and Name
            Column(
                modifier = Modifier
                    .padding(4.dp) // Slightly reduced padding
                    .clickable { navController.navigate("home") },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ストリエフン",
                    fontSize = 16.sp, // Reduced font size
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    modifier = Modifier.padding(bottom = 1.dp) // Reduced bottom padding
                )
                Text(
                    text = "STORYEFUN",
                    fontSize = 13.sp, // Reduced font size
                    color = theme.textSecondary
                )
            }

            // Right: Icons and Coin Balance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp) // Reduced spacing
            ) {
                IconButton(onClick = { navController.navigate("search") }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = theme.textPrimary,
                        modifier = Modifier.size(20.dp) // Smaller icon
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp) // Reduced spacing
                ) {
                    Text(
                        text = if (isLoading) "..." else coinBalance.toString(),
                        fontSize = 18.sp, // Reduced font size
                        fontWeight = FontWeight.Medium,
                        color = theme.textPrimary
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_coin),
                        contentDescription = "Coin Balance",
                        modifier = Modifier
                            .size(12.dp) // Smaller icon
                            .align(Alignment.Top),
                        tint = theme.textSecondary
                            //    tint = Color(0xFF3A2DA3)
                    )
                    IconButton(
                        onClick = { navController.navigate("desposite") },
                        modifier = Modifier.size(18.dp) // Smaller button
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Deposit",
                            tint = theme.textPrimary,
                            modifier = Modifier.size(14.dp) // Smaller icon
                        )
                    }
                }

                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = theme.textPrimary,
                        modifier = Modifier.size(20.dp) // Smaller icon
                    )
                }
            }
        }
    }
}