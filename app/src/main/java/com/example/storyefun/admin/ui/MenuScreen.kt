package com.example.storyefun.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MenuScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // List of buttons
        AdminButton("Manage Users") {
            // Add navigation later
        }
        Spacer(modifier = Modifier.height(16.dp))
        AdminButton("Manage Books") {
            // Add navigation late
            navController.navigate("manageBook")
        }
        Spacer(modifier = Modifier.height(16.dp))
        AdminButton("Manage Categories") {
            // Add navigation later
        }
        Spacer(modifier = Modifier.height(16.dp))
        AdminButton("Violation Reports") {
            // Add navigation later
        }
    }
}

@Composable
fun AdminButton(
    title: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 25.sp
        )
    }
}
