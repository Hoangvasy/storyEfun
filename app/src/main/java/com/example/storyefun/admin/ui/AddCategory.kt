package com.example.storyefun.admin.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun AddCategory(navController: NavHostController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar()
            MenuList()
            AddButton()
        }
    }
}

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
//            IconButton(onClick = { /* TODO: Handle click */ }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_arrow_right),
//                    contentDescription = "Arrow Right",
//                    tint = Color.Gray
//                )
//            }
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