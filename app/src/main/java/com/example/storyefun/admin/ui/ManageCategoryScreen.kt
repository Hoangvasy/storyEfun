package com.example.storyefun.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.storyefun.admin.viewModel.CategoryViewModel
import com.example.storyefun.data.Category

@Composable
fun ManageCategoryScreen(navController: NavController, viewModel: CategoryViewModel = viewModel()) {
    val categories by viewModel.categories.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 🏷️ Input Fields for New Category
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = categoryDescription,
                onValueChange = { categoryDescription = it },
                label = { Text("Category Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {
                if (categoryName.isNotBlank()) {
                    val newCategory = Category(id = categoryName.lowercase(), name = categoryName, description = categoryDescription)
                    viewModel.addCategory(newCategory)
                    categoryName = ""
                    categoryDescription = ""
                }
            }) {
                Text("Add Category")
            }
            Spacer(modifier = Modifier.height(12.dp))

            // 📜 Category List
            LazyColumn {
                items(categories) { category ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = category.name, style = MaterialTheme.typography.headlineSmall)
                            Text(text = category.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = { viewModel.deleteCategory(category) }) {
                                    Text("Delete")
                                }
                                Button(onClick = { /* Handle Edit */ }) {
                                    Text("Edit")
                                }
                            }
                        }
                    }
                }
            }
        }

        // 🔄 Loading Indicator (Overlay)
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).wrapContentSize(
                    Alignment.Center)
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}
