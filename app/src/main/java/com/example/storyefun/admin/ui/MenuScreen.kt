package com.example.storyefun.admin.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.storyefun.R
import com.example.storyefun.data.DrawerItem
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MenuScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedItem = remember { mutableStateOf("manageUsers") }

    val drawerItems = listOf(
        DrawerItem("Manage Users", "manageUsers", Icons.Default.Menu, 3),
        DrawerItem("Manage Books", "ManageBook", Icons.Default.Menu, 12),
        DrawerItem("Upload Book", "uploadBook", Icons.Default.Menu),
        DrawerItem("Manage Categories", "manageCategories", Icons.Default.Menu),
        DrawerItem("Statistics", "statistics", Icons.Default.Menu),
        DrawerItem("Settings", "settings", Icons.Default.Settings)
    )

    // Gradient màu cam đẹp
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFF7043),  // Cam đậm
            Color(0xFFFFA726)   // Cam nhạt
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .background(gradient)
                    .padding(vertical = 24.dp)
            ) {
                // Header với avatar và thông tin
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar với hiệu ứng shadow
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://randomuser.me/api/portraits/women/45.jpg")
                            .crossfade(true)
                            .build(),
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .shadow(8.dp, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Anita Cruz",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Admin",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Danh sách menu items
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(drawerItems) { item ->
                        val isSelected = selectedItem.value == item.route
                        val elevation = animateDpAsState(if (isSelected) 8.dp else 0.dp)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
//                                .shadow(elevation.value, RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color.White.copy(alpha = 0.2f)
                                    else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedItem.value = item.route
                                    scope.launch { drawerState.close() }
                                    navController.navigate(item.route)
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                .animateItemPlacement()
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = item.title,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f),
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )

                            if (item.badgeCount > 0) {
                                Spacer(modifier = Modifier.weight(1f))
                                Badge(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFFFF7043)
                                ) {
                                    Text(item.badgeCount.toString())
                                }
                            }
                        }
                    }
                }

                // Footer
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Divider(
                        color = Color.White.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Handle logout */ }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Logout",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Logout",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Admin Panel",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier
                                .clip(CircleShape) // Đảm bảo viền bo tròn gọn gàng
                                .background(Color(0xFFFF7043).copy(alpha = 0.9f)) // Có thể làm màu nhạt bớt
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    }
                    ,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://cdn-icons-png.flaticon.com/512/2232/2232688.png")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Admin Illustration",
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Welcome to Admin Panel",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Select an option from the menu",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}