package com.example.storyefun.admin.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ListChapterScreen(navController: NavController, bookId: String, volumeId: String) {
    val context = LocalContext.current
    var chapters by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) } // Danh sách các chương
    val db = FirebaseFirestore.getInstance()

    // Load chapters from Firestore
    LaunchedEffect(volumeId) {
        db.collection("books").document(bookId).collection("volumes")
            .document(volumeId).collection("chapters")
            .orderBy("order") // Sắp xếp theo thứ tự chương
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    chapters = snapshot.documents.mapNotNull { it.data }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Tiêu đề trang
        Text(
            text = "Chapters of Volume",

        )

        // Hiển thị danh sách các chương
        Spacer(modifier = Modifier.height(16.dp))
        Text("Existing Chapters")

        // Danh sách các chương
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(chapters) { chapter ->
                val chapterId = chapter["id"] as String
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            // Điều hướng đến màn hình chi tiết chương nếu cần
                            Toast.makeText(context, "Clicked on ${chapter["title"]}", Toast.LENGTH_SHORT).show()
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = chapter["title"] as String,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        // Nếu cần thêm thông tin, bạn có thể hiển thị thêm
                        Text(
                            text = "Order: ${chapter["order"]}",
                            style = TextStyle(color = Color.Gray, fontSize = MaterialTheme.typography.bodySmall.fontSize)
                        )
                    }
                }
            }
        }

        // Button thêm chương
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Điều hướng đến màn hình thêm chương
                navController.navigate("add_chapter_screen/$bookId/$volumeId")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Chapter")
        }
    }
}
