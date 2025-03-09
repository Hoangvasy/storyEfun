package com.example.storyefun.admin.ui

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@Composable
fun AdminUploadScreen(navController: NavController) {
    val context = LocalContext.current

    // Mutable state for input fields
    var bookName by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var posterUri by remember { mutableStateOf<Uri?>(null) }

    // Pick image and poster
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }
    val posterPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        posterUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        navController.let {
            IconButton(onClick = { it.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
        Text(text = "Upload a Book", style = MaterialTheme.typography.headlineSmall)

        // Name Input
        OutlinedTextField(
            value = bookName,
            onValueChange = { bookName = it },
            label = { Text("Book Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // Author Input
        OutlinedTextField(
            value = authorName,
            onValueChange = { authorName = it },
            label = { Text("Author Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // Description Input
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        // Pick Book Image
        Box(
            modifier = Modifier
                .size(150.dp)
                .clickable { imagePicker.launch("image/*") }
        ) {
            imageUri?.let {
                Image(painter = rememberAsyncImagePainter(it), contentDescription = "Book Image")
            } ?: Text("Choose Book Image", modifier = Modifier.align(Alignment.Center))
        }

        // Pick Poster Image
        Box(
            modifier = Modifier
                .size(150.dp)
                .clickable { posterPicker.launch("image/*") }
        ) {
            posterUri?.let {
                Image(painter = rememberAsyncImagePainter(it), contentDescription = "Poster Image")
            } ?: Text("Choose Poster Image", modifier = Modifier.align(Alignment.Center))
        }

        // Upload Button
        Button(
            onClick = {
                uploadBookToFirebase(
                    bookName, authorName, description, context, navController, imageUri, posterUri
                )
            },
            enabled = bookName.isNotBlank() && authorName.isNotBlank() && description.isNotBlank()
                    && imageUri != null && posterUri != null
        ) {
            Text("Upload Book")
        }
    }
}


fun uploadBookToFirebase(
    name: String,
    author: String,
    description: String,
    context: Context,
    navController: NavController,
    imageUri: Uri?,
    posterUri: Uri?
) {
    val firestore = FirebaseFirestore.getInstance()
    val bookId = UUID.randomUUID().toString()

    // Upload images to Cloudinary
    if (imageUri != null && posterUri != null) {
        uploadImageToCloudinary(imageUri, "book_covers") { imageUrl ->
            uploadImageToCloudinary(posterUri, "book_posters") { posterUrl ->
                // Save book data with image URLs to Firestore
                val book = hashMapOf(
                    "id" to bookId,
                    "name" to name,
                    "author" to author,
                    "description" to description,
                    "imageUrl" to imageUrl,
                    "posterUrl" to posterUrl,
                    "likes" to 0,
                    "views" to 0,
                    "follows" to 0,
                    "type" to "novel"
                )

                firestore.collection("books").document(bookId)
                    .set(book)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Book uploaded successfully!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseUpload", "Failed to save book: ${e.message}")
                        Toast.makeText(context, "Failed to save book", Toast.LENGTH_LONG).show()
                    }
            }
        }
    } else {
        Toast.makeText(context, "Please select both images", Toast.LENGTH_SHORT).show()
    }
}
fun uploadImageToCloudinary(uri: Uri, folder: String, onSuccess: (String) -> Unit) {
    val requestId = MediaManager.get().upload(uri)
        .option("folder", folder) // Optional: Specify a folder
        .callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                // Upload started
                println("Upload started: $requestId")
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                // Upload progress
                println("Upload progress: $bytes/$totalBytes")
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                // Upload success
                var imageUrl = resultData["url"] as String

                // Ensure the URL uses HTTPS
                if (imageUrl.startsWith("http://")) {
                    imageUrl = imageUrl.replace("http://", "https://")
                }

                println("Upload success: $imageUrl")
                onSuccess(imageUrl)
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                // Upload error
                println("Upload error: ${error.description}")
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                // Upload rescheduled
                println("Upload rescheduled: ${error.description}")
            }
        })
        .dispatch()
}