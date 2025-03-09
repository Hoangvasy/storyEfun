package com.example.profileui

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.storyefun.R
import com.example.storyefun.ui.theme.ThemeViewModel

@Composable
fun ProfileScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    // Use shared dark mode state from ThemeViewModel
    val isDarkMode by themeViewModel.isDarkTheme.collectAsState()
    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Avatar + Name + Location
        ProfileHeader(textColor)

        Spacer(modifier = Modifier.height(16.dp))

        // Settings Section
        SettingsSection(
            navController = navController,
            darkMode = isDarkMode,
            textColor = textColor,
            onDarkModeToggle = { themeViewModel.toggleTheme() }
        ) { destination ->
            // Handle navigation based on destination
            when (destination) {
                "home" -> navController.navigate("login")
                "favourite" -> navController.navigate("login")
                "account" -> navController.navigate("login")
                "contact" -> navController.navigate("login")
                "out" -> println("Đăng xuất")
            }
        }
    }
}

@Composable
fun ProfileHeader(textColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ava),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Hoàng Văn Sỹ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(15.dp))
            ProfileStats(textColor)
        }
    }
}

@Composable
fun ProfileStats(textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem("122", "followers", textColor)
        StatItem("67", "following", textColor)
        StatItem("37K", "likes", textColor)
    }
}

@Composable
fun StatItem(number: String, label: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E2E5D)
        )
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun SettingsSection(
    navController: NavController,
    darkMode: Boolean,
    textColor: Color,
    onDarkModeToggle: (Boolean) -> Unit,
    onItemClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Settings",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
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
        SettingItem(
            title = "tai khoan",
            icon = Icons.Default.Person,
            darkMode = darkMode,
            textColor = textColor,
            onClick = { navController.navigate("login") }
        )
        SettingItem(
            title = "truyen yêu thích",
            icon = Icons.Default.Favorite,
            darkMode = darkMode,
            textColor = textColor,
            onClick = { onItemClick("login") }
        )
        SettingItem(
            title = "them truyen",
            icon = Icons.Default.Add,
            darkMode = darkMode,
            textColor = textColor,
            onClick = { onItemClick("login") }
        )
        SettingItem(
            title = "Lien he",
            icon = Icons.Default.Call,
            darkMode = darkMode,
            textColor = textColor,
            onClick = { onItemClick("login") }
        )
        SettingItem(
            title = "Dang xuat",
            icon = Icons.Default.ExitToApp,
            darkMode = darkMode,
            textColor = textColor,
            onClick = { onItemClick("login") }
        )
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(
                onClick = { onClick?.invoke() },
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            color = textColor
        )
        Spacer(modifier = Modifier.weight(1f))
        if (switch) {
            Switch(
                checked = darkMode,
                onCheckedChange = { onDarkModeToggle?.invoke(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = if (darkMode) Color.White else Color.Black,
                    checkedTrackColor = if (darkMode) Color.Gray else Color.LightGray
                )
            )
        }
    }
}
