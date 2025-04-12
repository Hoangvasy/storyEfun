package com.example.storyefun.admin.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.storyefun.ui.theme.LocalAppColors
import java.util.*

@Composable
fun AdminUploadScreen(navController: NavController) {
    val context = LocalContext.current
    val theme = LocalAppColors.current

    var bookName by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<List<String>>(emptyList()) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var posterUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }
    val posterPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        posterUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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
                text = "Upload Book",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        OutlinedTextField(
            value = bookName,
            onValueChange = { bookName = it },
            label = { Text("\uD83D\uDCDA Book Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(5.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = authorName,
            onValueChange = { authorName = it },
            label = { Text(" \uD83D\uDC64 Author Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(5.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("\uD83D\uDCDD Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(5.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        CategoryBook(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                selectedCategory = if (selectedCategory.contains(category)) {
                    selectedCategory - category
                } else {
                    selectedCategory + category
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ImagePickerBox(imageUri, " Book Image") { imagePicker.launch("image/*") }
            ImagePickerBox(posterUri, "Poster Image") { posterPicker.launch("image/*") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                uploadBook(bookName, authorName, description, selectedCategory, context, navController, imageUri, posterUri)
            },
            enabled = bookName.isNotBlank() && authorName.isNotBlank() && description.isNotBlank() && imageUri != null && posterUri != null,
            colors = ButtonDefaults.buttonColors(containerColor = theme.buttonOrange), // màu cam đẹp
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("☁\uFE0F Upload Book", color = theme.textPrimary)
        }
    }
}

@Composable

fun ImagePickerBox(uri: Uri?, placeholder: String, onClick: () -> Unit) {
    val theme = LocalAppColors.current
    Card(
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = theme.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .size(150.dp)
            .clickable { onClick() }
    ) {
        if (uri != null) {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(placeholder, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


// Upload book function
fun uploadBook(
    name: String, author: String, desc: String,
    categories: List<String>,
    context: Context, navController: NavController,
    imgUri: Uri?, posterUri: Uri?
) {
    if (imgUri == null || posterUri == null) {
        Toast.makeText(context, "Please select both images", Toast.LENGTH_SHORT).show()
        return
    }

    val bookId = UUID.randomUUID().toString()
    val db = FirebaseFirestore.getInstance()

    uploadToCloudinary(imgUri, "book_covers") { imgUrl ->
        uploadToCloudinary(posterUri, "book_posters") { posterUrl ->
            val bookData = hashMapOf(
                "id" to bookId,
                "name" to name,
                "author" to author,
                "description" to desc,
                "categories" to categories,
                "imageUrl" to imgUrl,
                "posterUrl" to posterUrl,
                "likes" to 0,
                "views" to 0,
                "follows" to 0,
                "type" to "novel"
            )

            // Save book information to Firestore
            db.collection("books")
                .document(bookId)
                .set(bookData)
                .addOnSuccessListener {
                    // Create volume and chapters after book is uploaded
//                    createVolumeAndChapters(bookId, db) {
                    Toast.makeText(context, "Book uploaded successfully!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()  // Go back after upload
//                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}


// Upload image to Cloudinary
fun uploadToCloudinary(uri: Uri, folder: String, onSuccess: (String) -> Unit) {
    MediaManager.get().upload(uri)
        .option("folder", folder)
        .callback(object : UploadCallback {
            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                val secureUrl = resultData["secure_url"] as String
                onSuccess(secureUrl)
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                Log.e("Cloudinary", "Upload error: ${error.description}")
            }

            override fun onStart(requestId: String) {}
            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
            override fun onReschedule(requestId: String, error: ErrorInfo) {}
        })
        .dispatch()
}