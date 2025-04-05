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
    var chapterNumber  by remember { mutableStateOf(1) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

    // Load chapters
    LaunchedEffect(bookId) {
        db.collection("books").document(bookId).collection("chapter")
            .orderBy("order")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    chapters = snapshot.documents.mapNotNull { it.data }
                    val count = snapshot.documents.size
                    chapterNumber = count + 1;
                    title = "Chapter $chapterNumber"
                }
            }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        imageUris = uris ?: emptyList()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Chapters for Book ID: $bookId", style = MaterialTheme.typography.titleLarge)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(chapters) { chapter ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = chapter["title"] as String, style = MaterialTheme.typography.titleMedium)

                        val content = chapter["content"]
                        when (content) {
                            is String -> {
                                Image(
                                    painter = rememberAsyncImagePainter(content),
                                    contentDescription = "Chapter Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                )
                            }

                            is List<*> -> {
                                content.forEach { url ->
                                    if (url is String) {
                                        Image(
                                            painter = rememberAsyncImagePainter(url),
                                            contentDescription = "Chapter Image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp)
                                                .padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- CHUYỂN FORM NHẬP CHAPTER VÀ UPLOAD VÀO 1 LazyColumn để có thể scroll ---
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Chapter Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            item {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Select Images")
                }
            }

            items(imageUris) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 8.dp)
                )
            }

            item {
                Button(
                    onClick = {
                        imageUris.forEach { uri ->
                            uploadChapterToCloudinary(uri, "chapters") { url ->
                                imageUrls = imageUrls + url
                                if (imageUrls.size == imageUris.size) {
                                    uploadChapter(bookId, title, imageUrls) {
                                        title = ""
                                        imageUris = emptyList()
                                        imageUrls = emptyList()
                                        title = "chapter $chapterNumber"
                                    }
                                }
                            }
                        }
                    },
                    enabled = title.isNotBlank() && imageUris.isNotEmpty(),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text("Upload Chapter")
                }
            }
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
