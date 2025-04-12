package com.example.storyefun.admin.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.storyefun.ui.theme.LocalAppColors
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import java.util.*

@Composable
fun AddVolumeScreen(navController: NavController, bookId: String) {
    val theme = LocalAppColors.current
    val context = LocalContext.current
    var volumeTitle by remember { mutableStateOf(TextFieldValue("")) }
    var volumeNumber by remember { mutableStateOf(1) }
    var volumeOrder by remember { mutableStateOf(1) }
    var volumes by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    val db = FirebaseFirestore.getInstance()

    // Load existing volumes
    LaunchedEffect(bookId) {
        db.collection("books").document(bookId).collection("volumes")
            .orderBy("order")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val count = snapshot.documents.size
                    volumeNumber = count + 1
                    volumeTitle = TextFieldValue("Volume $volumeNumber: ")
                    volumes = snapshot.documents.map { document ->
                        document.data?.plus("id" to document.id) ?: emptyMap()
                    }
                    volumeOrder = snapshot.size() + 1
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "Add Volume",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Volume Title Input
        OutlinedTextField(
            value = volumeTitle,
            onValueChange = { volumeTitle = it },
            label = { Text("Volume Title") },
            isError = volumeTitle.text.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // Add Button
        Button(
            onClick = {
                if (volumeTitle.text.isNotBlank()) {
                    addVolumeToFirestore(bookId, volumeTitle.text, volumeOrder, db, context, navController)
                } else {
                    Toast.makeText(context, "Please provide a title", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = theme.buttonOrange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Add Volume", color = Color.Black)
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

        Text("Existing Volumes", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(volumes) { volume ->
                val volumeId = volume["id"] as String
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("addChapter/$bookId/$volumeId")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = volume["title"] as String,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = theme.textPrimary
                        )
                    }
                }
            }
        }
    }
}


// Thêm Volume vào Firestore và điều hướng sau khi thêm thành công
fun addVolumeToFirestore(
    bookId: String,
    title: String,
    order: Int,
    db: FirebaseFirestore,
    context: Context,
    navController: NavController
) {
    // Tạo volume ID
    val volumeId = UUID.randomUUID().toString()

    // Dữ liệu Volume
    val volumeData = hashMapOf(
        "title" to title,
        "order" to order,
        "createdAt" to FieldValue.serverTimestamp() // Timestamp khi tạo volume
    )

    // Thêm volume vào Firestore
    db.collection("books")
        .document(bookId)
        .collection("volumes")
        .document(volumeId)
        .set(volumeData)
        .addOnSuccessListener {
            Toast.makeText(context, "Volume added successfully", Toast.LENGTH_SHORT).show()
            // Điều hướng đến màn hình quản lý volume hoặc màn hình AddChapter
            navController.navigate("addChapter/$bookId/$volumeId")
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Failed to add volume: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
