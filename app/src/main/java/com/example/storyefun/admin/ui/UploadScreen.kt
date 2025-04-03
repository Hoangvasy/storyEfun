package com.example.storyefun.admin.ui

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.storyefun.admin.viewModel.BookViewModel
import com.example.storyefun.data.Book
import com.example.storyefun.data.CloudnaryRepository
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@Composable
fun AdminUploadScreen(navController: NavController,  viewModel: BookViewModel = viewModel()) {
    val context = LocalContext.current
    // Mutable state for input fields
    var bookName by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.observeAsState(false)

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
                Log.d("Success","Click")

                uploadBookToFirebase(

                    bookName, authorName, description, context, navController, imageUri, posterUri, viewModel
                )
            },
            enabled = bookName.isNotBlank() && authorName.isNotBlank() && description.isNotBlank()
                    && imageUri != null && posterUri != null
        ) {
            Text("Upload Book")
        }
        // 🔄 Centered Loading Overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Transparent overlay
                    .wrapContentSize(Alignment.Center) // Centers the loading indicator
            ) {
                CircularProgressIndicator(color = Color.White)
            }
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
    posterUri: Uri?,
    viewModel: BookViewModel
) {
    val bookId = UUID.randomUUID().toString()
    val book = Book(
        id = bookId,
        name = name,
        author = author,
        description = description,
    )
    if (imageUri != null && posterUri != null)
    {
        Log.d("Success","uri is not null!")

        viewModel.addBook(book, imageUri, posterUri)
    } else {
        Toast.makeText(context, "Please select both images", Toast.LENGTH_SHORT).show()
    }
}