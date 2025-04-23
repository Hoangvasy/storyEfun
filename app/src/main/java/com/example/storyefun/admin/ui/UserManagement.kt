package com.example.storyefun.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.storyefun.data.models.User
import com.example.storyefun.viewModel.UserViewModel

@Composable
fun UserManageScreen(navController: NavController) {
    val userViewModel: UserViewModel = viewModel()
    val users by userViewModel.usersList.collectAsState()
    val deleteStatus by userViewModel.deleteStatus.collectAsState()

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }

    // Xử lý trạng thái xóa
    LaunchedEffect(deleteStatus) {
        when (deleteStatus) {
            is UserViewModel.DeleteStatus.Success -> {
                snackbarHostState.showSnackbar(
                    message = (deleteStatus as UserViewModel.DeleteStatus.Success).message,
                    duration = SnackbarDuration.Short
                )
                userViewModel.resetDeleteStatus()
            }
            is UserViewModel.DeleteStatus.Error -> {
                snackbarHostState.showSnackbar(
                    message = (deleteStatus as UserViewModel.DeleteStatus.Error).message,
                    duration = SnackbarDuration.Long
                )
                userViewModel.resetDeleteStatus()
            }
            is UserViewModel.DeleteStatus.Idle -> {}
        }
    }

    // Tải danh sách người dùng
    LaunchedEffect(Unit) {
        userViewModel.getAllUsers()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navigate to add user screen */ },
                containerColor = Color(0xFFFF8C00), // Màu cam sáng
                contentColor = Color.White,
                modifier = Modifier
                    .shadow(12.dp, CircleShape)
                    .size(60.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add User",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = Color(0xFF2C2C2C),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(data.visuals.message, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        containerColor = Color(0xFFF5F7FA) // Nền sáng
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .background(Color(0xFFF5F7FA))
        ) {
            Text(
                text = "User Management",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color(0xFF1E88E5), // Xanh dương sáng
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (users.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No users found",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFF6B7280)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(users, key = { it.uid }) { user ->
                        UserRow(
                            user = user,
                            onDelete = { userViewModel.deleteUser(user.uid) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserRow(user: User, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { /* TODO: Navigate to user details */ }
            .animateContentSize(animationSpec = tween(300))
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFE3F2FD)) // Gradient trắng -> xanh nhạt
                )
            )
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email: ${user.email}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF4B5563)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Coin: ${user.coin}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF4B5563)
                )
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFFFCDD2)) // Nền đỏ nhạt
                    .size(44.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFD32F2F), // Đỏ đậm
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Confirm Delete",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1A1A1A)
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete ${user.name}?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4B5563)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Delete", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF1E88E5)
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}