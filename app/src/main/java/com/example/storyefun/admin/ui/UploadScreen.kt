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
import java.util.*

@Composable
fun AdminUploadScreen(navController: NavController) {
    val context = LocalContext.current

    var bookName by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var posterUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }
    val posterPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        posterUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Text(text = "Upload a Book", style = MaterialTheme.typography.headlineSmall)

        // Input Fields
        OutlinedTextField(value = bookName, onValueChange = { bookName = it }, label = { Text("Book Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = authorName, onValueChange = { authorName = it }, label = { Text("Author Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

        // Pick Images
        ImagePickerBox(imageUri, "Choose Book Image") { imagePicker.launch("image/*") }
        ImagePickerBox(posterUri, "Choose Poster Image") { posterPicker.launch("image/*") }

        // Upload Button
        Button(
            onClick = {
                uploadBook(bookName, authorName, description, context, navController, imageUri, posterUri)
            },
            enabled = bookName.isNotBlank() && authorName.isNotBlank() && description.isNotBlank() && imageUri != null && posterUri != null
        ) {
            Text("Upload Book")
        }
    }
}

@Composable
fun ImagePickerBox(uri: Uri?, placeholder: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Button(onClick = onClick) {
                Text(placeholder)
            }
        }
    }
}

// Upload book function
fun uploadBook(
    name: String, author: String, desc: String,
    context: Context, navController: NavController,
    imgUri: Uri?, posterUri: Uri?
) {
    if (imgUri == null || posterUri == null) {
        Toast.makeText(context, "Please select both images", Toast.LENGTH_SHORT).show()
        return
    }

    val bookId = UUID.randomUUID().toString()

    uploadToCloudinary(imgUri, "book_covers") { imgUrl ->
        uploadToCloudinary(posterUri, "book_posters") { posterUrl ->
            val bookData = hashMapOf(
                "id" to bookId,
                "name" to name,
                "author" to author,
                "description" to desc,
                "imageUrl" to imgUrl,
                "posterUrl" to posterUrl,
                "likes" to 0,
                "views" to 0,
                "follows" to 0,
                "type" to "novel"
            )

            FirebaseFirestore.getInstance().collection("books")
                .document(bookId)
                .set(bookData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Book uploaded successfully!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
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
