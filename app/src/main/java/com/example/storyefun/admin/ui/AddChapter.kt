package com.example.storyefun.admin.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.MediaManager

@Composable
fun AddChapterScreen(navController: NavController, bookId: String, volumeId: String) {
    val db = FirebaseFirestore.getInstance()
    var chapters by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var title by remember { mutableStateOf("") }
    var chapterNumber by remember { mutableStateOf(1) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        imageUris = uris
    }

    // Load chapter list
    LaunchedEffect(volumeId) {
        db.collection("books").document(bookId).collection("volumes").document(volumeId).collection("chapters")
            .orderBy("order")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    chapters = snapshot.documents.mapNotNull { it.data }
                    val count = snapshot.documents.size
                    chapterNumber = count + 1
                    title = "Chapter $chapterNumber"
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
                text = "Add Chapter ",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        // ======= Danh sách chương đã có =======
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(chapters) { chapter ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = chapter["title"] as? String ?: "No Title",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val content = chapter["content"]
                        when (content) {
                            is String -> {
                                Image(
                                    painter = rememberAsyncImagePainter(content),
                                    contentDescription = "Chapter Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
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
                                                .height(180.dp)
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

        Divider(modifier = Modifier.padding(vertical = 12.dp))

        // ======= Phần thêm chương mới =======
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("📌 Chapter Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(" Chọn ảnh minh họa", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hiển thị các ảnh đã chọn
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(imageUris) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    imageUris.forEach { uri ->
                        uploadChapterToCloudinary(uri, "chapters") { url ->
                            imageUrls = imageUrls + url
                            if (imageUrls.size == imageUris.size) {
                                uploadChapter(bookId, volumeId, title, imageUrls) {
                                    title = ""
                                    imageUris = emptyList()
                                    imageUrls = emptyList()
                                }
                            }
                        }
                    }
                },
                enabled = title.isNotBlank() && imageUris.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⬆️ Tải lên chương mới")
            }
        }
    }
}



fun uploadChapter(
    bookId: String,
    volumeId: String,
    title: String,
    imageUrls: List<String>,
    onComplete: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val chaptersRef = db.collection("books").document(bookId)
        .collection("volumes").document(volumeId).collection("chapters")

    val chapterData = hashMapOf(
        "title" to title,
        "content" to imageUrls,
        "order" to System.currentTimeMillis(),
        "createdAt" to System.currentTimeMillis(),
        "locked" to false // default: không khóa
    )

    // Thêm chương mới
    chaptersRef.add(chapterData)
        .addOnSuccessListener {
            // Sau khi thêm thành công, lấy lại toàn bộ chương để kiểm tra số lượng
            chaptersRef.orderBy("order").get().addOnSuccessListener { snapshot ->
                val chapterDocs = snapshot.documents
                if (chapterDocs.size > 2) {
                    // Khóa các chương từ chương 3 trở đi
                    chapterDocs.drop(2).forEach { doc ->
                        chaptersRef.document(doc.id).update("locked", true)
                    }
                }
                onComplete()
            }
        }
        .addOnFailureListener { e ->
            println("Error: ${e.message}")
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
