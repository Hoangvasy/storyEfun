package com.example.storyefun.admin.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.viewModel.CategoryViewModel
import com.example.storyefun.viewModel.UploadViewModel

@Composable
fun AdminUploadScreen(
    navController: NavController
) {
    val uploadViewModel: UploadViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val context = LocalContext.current
    val theme = LocalAppColors.current

    // Lấy danh sách categories và trạng thái loading từ CategoryViewModel
    val categories by categoryViewModel.categories.observeAsState(emptyList())
    val isLoadingCategories by categoryViewModel.isLoading.observeAsState(false)
    // Lấy selectedCategories từ CategoryViewModel bằng toán tử by
    val selectedCategories by categoryViewModel.selectedCategories

    // Đồng bộ selectedCategories từ CategoryViewModel với selectedCategory của UploadViewModel
    LaunchedEffect(selectedCategories) {
        uploadViewModel.selectedCategory = selectedCategories
    }

    // Gọi hàm lấy categories khi màn hình được hiển thị
    LaunchedEffect(Unit) {
        categoryViewModel.fetchCategories()
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        uploadViewModel.imageUri = it
    }
    val posterPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        uploadViewModel.posterUri = it
    }
    val selectedItem = remember { mutableStateOf("uploadBook") } // Đặt đúng là "uploadBook"
    val snackbarHostState = remember { SnackbarHostState() }

    AdminDrawer(
        navController = navController,
        drawerState = rememberDrawerState(DrawerValue.Closed),
        selectedItem = selectedItem
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header
                        Box(modifier = Modifier.fillMaxWidth()) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                            Text(
                                text = "Upload Book",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uploadViewModel.bookName,
                            onValueChange = { uploadViewModel.bookName = it },
                            label = { Text("\uD83D\uDCDA Book Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = uploadViewModel.authorName,
                            onValueChange = { uploadViewModel.authorName = it },
                            label = { Text("👤 Author Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = uploadViewModel.description,
                            onValueChange = { uploadViewModel.description = it },
                            label = { Text("📝 Description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Hiển thị danh sách categories
                        Text(
                            text = "📚 Select Categories",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (isLoadingCategories) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        } else {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { category ->
                                    FilterChip(
                                        selected = selectedCategories.contains(category.id),
                                        onClick = { categoryViewModel.toggleCategorySelection(category.id) },
                                        label = { Text(category.name) },
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }

                        // Hiển thị số category được chọn
                        Text(
                            text = "Selected ${selectedCategories.size} categories",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ImagePickerBox(uploadViewModel.imageUri, "Book Image") {
                                imagePicker.launch("image/*")
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            ImagePickerBox(uploadViewModel.posterUri, "Poster Image") {
                                posterPicker.launch("image/*")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { uploadViewModel.uploadBook(context, navController) },
                            enabled = !uploadViewModel.isUploading &&
                                    uploadViewModel.bookName.isNotBlank() &&
                                    uploadViewModel.authorName.isNotBlank() &&
                                    uploadViewModel.description.isNotBlank() &&
                                    uploadViewModel.imageUri != null &&
                                    uploadViewModel.posterUri != null &&
                                    selectedCategories.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = theme.buttonOrange)
                        ) {
                            Text("☁️ Upload Book", color = theme.textPrimary)
                        }
                    }
                }
            }
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