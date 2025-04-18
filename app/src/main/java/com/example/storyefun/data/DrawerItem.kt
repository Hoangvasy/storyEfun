package com.example.storyefun.data

import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(

    val title : String,
    val route :String,
    val icon : ImageVector,
    val badgeCount: Int = 0
)