package com.example.profileui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.storyefun.R
import com.example.storyefun.viewModel.ThemeViewModel
import com.example.storyefun.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    val isDarkMode by themeViewModel.isDarkTheme.collectAsState()
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else Color.Black
    val userViewModel: UserViewModel = viewModel()
    val context = LocalContext.current

    // Firebase current user
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    // Bottom sheet state for avatar
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Dialog states for username and password
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf(firebaseUser?.displayName ?: "") }
    var newPassword by remember { mutableStateOf("") }

    // Image picker launchers
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            userViewModel.updateAvatar(
                imageUri = it,
                onSuccess = {
                    Toast.makeText(context, "Avatar updated successfully", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(context, "Failed to update avatar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            Toast.makeText(context, "Camera capture not implemented", Toast.LENGTH_SHORT).show()
        }
    }

    // Permission launcher for camera
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(Uri.EMPTY)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Username dialog
    if (showUsernameDialog) {
        AlertDialog(
            onDismissRequest = { showUsernameDialog = false },
            title = { Text("Change Username") },
            text = {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("New Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userViewModel.updateUsername(
                            newName = newUsername,
                            onSuccess = {
                                showUsernameDialog = false
                                Toast.makeText(context, "Username updated successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { e ->
                                showUsernameDialog = false
                                Toast.makeText(context, "Failed to update username: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    enabled = newUsername.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUsernameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Password dialog
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userViewModel.updatePassword(
                            newPassword = newPassword,
                            onSuccess = {
                                showPasswordDialog = false
                                Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { e ->
                                showPasswordDialog = false
                                Toast.makeText(context, "Failed to update password: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    enabled = newPassword.length >= 6
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Bottom sheet for avatar selection
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Avatar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                        showBottomSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick from Gallery")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val permission = Manifest.permission.CAMERA
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(Uri.EMPTY)
                        } else {
                            permissionLauncher.launch(permission)
                        }
                        showBottomSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take a Photo")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ProfileHeader(
            textColor = textColor,
            navController = navController,
            displayName = firebaseUser?.displayName ?: "Guest",
            email = firebaseUser?.email ?: "No email",
            photoUrl = firebaseUser?.photoUrl?.toString(),
            onEditAvatarClick = { showBottomSheet = true },
            onEditUsernameClick = { showUsernameDialog = true }
        )
        SettingsSection(
            navController = navController,
            darkMode = isDarkMode,
            textColor = textColor,
            onDarkModeToggle = { themeViewModel.toggleTheme() },
            onChangePasswordClick = { showPasswordDialog = true },
            onItemClick = { destination ->
                when (destination) {
                    "home" -> navController.navigate("login")
                    "favourite" -> navController.navigate("login")
                    "account" -> navController.navigate("login")
                    "contact" -> navController.navigate("login")
                    "out" -> {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ProfileHeader(
    textColor: Color,
    navController: NavController,
    displayName: String,
    email: String,
    photoUrl: String?,
    onEditAvatarClick: () -> Unit,
    onEditUsernameClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC5)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, 400f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = if (photoUrl != null) {
                        rememberAsyncImagePainter(
                            model = photoUrl,
                            placeholder = painterResource(id = R.drawable.ava),
                            error = painterResource(id = R.drawable.ava)
                        )
                    } else {
                        painterResource(id = R.drawable.ava)
                    },
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                        .shadow(8.dp, CircleShape)
                )
                IconButton(
                    onClick = onEditAvatarClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, Color(0xFF6200EE), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Avatar",
                        tint = Color(0xFF6200EE),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(
                    onClick = onEditUsernameClick,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Username",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = email,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileStats()
        }
    }
}

@Composable
fun ProfileStats() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem("122", "Followers")
        StatItem("67", "Following")
        StatItem("37K", "Likes")
    }
}

@Composable
fun StatItem(number: String, label: String) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SettingsSection(
    navController: NavController,
    darkMode: Boolean,
    textColor: Color,
    onDarkModeToggle: (Boolean) -> Unit,
    onChangePasswordClick: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (darkMode) Color(0xFF1F1F1F) else Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Settings",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingItem(
                title = "Chế độ tối",
                icon = Icons.Default.Face,
                switch = true,
                darkMode = darkMode,
                textColor = textColor,
                onDarkModeToggle = { onDarkModeToggle(it) }
            )
            Divider(color = textColor.copy(alpha = 0.1f))
            SettingItem(
                title = "Tài khoản",
                icon = Icons.Default.Person,
                darkMode = darkMode,
                textColor = textColor,
                onClick = { onItemClick("account") }
            )
            Divider(color = textColor.copy(alpha = 0.1f))
            SettingItem(
                title = "Đổi mật khẩu",
                icon = Icons.Default.Lock,
                darkMode = darkMode,
                textColor = textColor,
                onClick = onChangePasswordClick
            )
            Divider(color = textColor.copy(alpha = 0.1f))
            SettingItem(
                title = "Truyện yêu thích",
                icon = Icons.Default.Favorite,
                darkMode = darkMode,
                textColor = textColor,
                onClick = { navController.navigate("mystory") }
            )
            Divider(color = textColor.copy(alpha = 0.1f))
            SettingItem(
                title = "Truyện đã đăng",
                icon = Icons.Default.Add,
                darkMode = darkMode,
                textColor = textColor,
                onClick = { navController.navigate("mystory") }
            )
            Divider(color = textColor.copy(alpha = 0.1f))
            SettingItem(
                title = "Thêm truyện",
                icon = Icons.Default.Add,
                darkMode = darkMode,
                textColor = textColor,
                onClick = { navController.navigate("upload") }
            )
            Divider(color = textColor.copy(alpha = 0.1f))
            SettingItem(
                title = "Liên hệ",
                icon = Icons.Default.Call,
                darkMode = darkMode,
                textColor = textColor,
                onClick = { onItemClick("contact") }
            )
            Divider(color = textColor.copy(alpha = 0.1f))
            SettingItem(
                title = "Log out",
                icon = Icons.Default.ExitToApp,
                darkMode = darkMode,
                textColor = textColor,
                onClick = { onItemClick("out") }
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    icon: ImageVector,
    switch: Boolean = false,
    darkMode: Boolean = false,
    textColor: Color,
    onDarkModeToggle: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        if (isPressed) textColor.copy(alpha = 0.1f) else Color.Transparent
    )
    val elevation by animateDpAsState(if (isPressed) 4.dp else 0.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(
                onClick = { onClick?.invoke() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
        Spacer(modifier = Modifier.weight(1f))
        if (switch) {
            Switch(
                checked = darkMode,
                onCheckedChange = { onDarkModeToggle?.invoke(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF6200EE),
                    checkedTrackColor = Color(0xFFBB86FC),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }
    }
}