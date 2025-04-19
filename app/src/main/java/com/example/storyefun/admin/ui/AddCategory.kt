package com.example.storyefun.admin.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.storyefun.data.models.Category
import com.example.storyefun.data.repository.CategoryFirebase

@Composable
fun AddCategory(navController: NavHostController, onCategoryAdded: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Category Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (name.isNotBlank() && description.isNotBlank()) {
                    val category = Category(name = name, description = description)
                    CategoryFirebase(category) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, "Category added successfully", Toast.LENGTH_SHORT).show()
                            onCategoryAdded()
                        } else {
                            Toast.makeText(context, "Failed to add category", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Add Category")
        }
    }
}

//@Composable
//fun AddCategory(navController: NavHostController) {
//    Surface(modifier = Modifier.fillMaxSize()) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            TopBar()
//            MenuList()
//            AddButton()
//        }
//    }
//}

@Composable
fun TopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFFF3D4F)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Menu",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MenuList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .weight(1f)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
//        val items = listOf(
//            MenuItem("Pizza", "25 items", R.drawable.pizza),
//            MenuItem("Salads", "30 items", R.drawable.salad),
//            MenuItem("Desserts", "30 items", R.drawable.dessert),
//            MenuItem("Pasta", "40 items", R.drawable.pasta),
//            MenuItem("Beverages", "20 items", R.drawable.beverage)
//        )
//        items.forEach { item ->
//            MenuCard(item)
//        }
    }
}

@Composable
fun MenuCard(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = item.subtitle,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            IconButton(onClick = { /* TODO: Handle click */ }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_arrow_right),
//                    contentDescription = "Arrow Right",
//                    tint = Color.Gray
//                )
            }
        }
    }
}

@Composable
fun AddButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { /* TODO: Handle Add action */ },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D4F))
        ) {
            Text(
                text = "Add",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class MenuItem(val title: String, val subtitle: String, val imageRes: Int)

// Note: Replace R.drawable.[image_name] with your actual drawable resources.


//@Preview(name = "AddCategory")
//@Composable
//private fun PreviewAddCategory() {
//    AddCategory(navController)
//}