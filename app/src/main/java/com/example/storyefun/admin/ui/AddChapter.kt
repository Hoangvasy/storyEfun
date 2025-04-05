package com.example.storyefun.admin.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.MediaManager

@Composable
fun AddChapter(navController: NavController, bookId: String) {
    val db = FirebaseFirestore.getInstance()
    var chapters by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var title by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) } // Multiple image URIs
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) } // Image URLs after upload

    // Load danh sách chapter
    LaunchedEffect(bookId) {
        db.collection("books").document(bookId).collection("chapter")
            .orderBy("order")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    chapters = snapshot.documents.mapNotNull { it.data }
                }
            }
    }

    // Launcher for selecting multiple images
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        imageUris = uris ?: emptyList() // Update selected URIs
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Chapters for Book ID: $bookId", style = MaterialTheme.typography.titleLarge)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(chapters) { chapter ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = chapter["title"] as String, style = MaterialTheme.typography.titleMedium)
                        if (chapter["content"] is String) {
                            Image(
                                painter = rememberAsyncImagePainter(chapter["content"] as String),
                                contentDescription = "Chapter Image",
                                modifier = Modifier.fillMaxWidth().height(150.dp)
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Chapter Title") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        // Button to select images
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Select Images")
        }

        // Display selected images
        imageUris.forEach { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxWidth().height(150.dp).padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                imageUris.forEach { uri ->
                    uploadChapterToCloudinary(uri, "chapters") { url ->
                        imageUrls = imageUrls + url // Add URL to the list of uploaded image URLs
                        if (imageUrls.size == imageUris.size) {
                            uploadChapter(bookId, title, imageUrls) {
                                title = ""
                                imageUris = emptyList()
                                imageUrls = emptyList()
                            }
                        }
                    }
                }
            },
            enabled = title.isNotBlank() && imageUris.isNotEmpty(),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Upload Chapter")
        }
    }
}

// Upload images to Cloudinary
fun uploadChapterToCloudinary(uri: Uri, folder: String, onSuccess: (String) -> Unit) {
    MediaManager.get().upload(uri)
        .option("folder", folder)
        .callback(object : com.cloudinary.android.callback.UploadCallback {
            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                val secureUrl = resultData["secure_url"] as? String
                if (secureUrl != null) {
                    onSuccess(secureUrl)
                } else {
                    println("Upload error: Secure URL is null")
                }
            }

            override fun onError(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                println("Upload error: ${error.description}")
            }

            override fun onStart(requestId: String) {
                println("Upload started: $requestId")
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                println("Uploading: $bytes / $totalBytes")
            }

            override fun onReschedule(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                println("Upload rescheduled: ${error.description}")
            }
        })
        .dispatch()
}

// Upload chapter into Firestore
fun uploadChapter(bookId: String, title: String, imageUrls: List<String>, onComplete: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val chapterData = hashMapOf(
        "title" to title,
        "content" to imageUrls, // Store list of image URLs
        "order" to System.currentTimeMillis(),
        "createdAt" to System.currentTimeMillis()
    )

    db.collection("books").document(bookId).collection("chapter")
        .add(chapterData)
        .addOnSuccessListener { onComplete() }
        .addOnFailureListener { e -> println("Error: ${e.message}") }
}
