package com.example.storyefun.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.storyefun.R

// Define App Colors
data class AppColors(
    val background: @Composable () -> Painter,  // ✅ Đổi thành lambda @Composable
    val textPrimary: Color,
    val textSecondary: Color,
    val buttonBackground: Color,
    val buttonText: Color,
    val backgroundColor : Color,
    val tagColor : Color,
    val backgroundContrast1 : Color,
    val backgroundContrast2 : Color

)

// Light Theme
val LightColors = AppColors(
    background = { painterResource(id = R.drawable.background) },  // ✅ Gọi trong @Composable lambda
    textPrimary = Color.Black,
    textSecondary = Color.Gray,
    buttonBackground = Color.Red,
    buttonText = Color.White,
    backgroundColor = Color.White,
    tagColor = Color.DarkGray,
    backgroundContrast1 = Color.Black,
    backgroundContrast2 = Color.DarkGray


)

// Dark Theme
val DarkColors = AppColors(
    background = { painterResource(id = R.drawable.darkbackground) }, // ✅ Gọi trong @Composable lambda
    textPrimary = Color.White,
    textSecondary = Color.Gray,
    buttonBackground = Color.Red,
    buttonText = Color.Black,
    backgroundColor = Color.Black,
    tagColor = Color.Gray,
    backgroundContrast1 = Color.White,
    backgroundContrast2 = Color.Gray
)

// Create a Local variable to store colors
val LocalAppColors = staticCompositionLocalOf { LightColors }

// Function to apply theme
@Composable
fun AppTheme(
    darkTheme: Boolean,  // Biến này quyết định dùng Light hay Dark theme
    content: @Composable () -> Unit
) {
    val colors = remember(darkTheme) {
        if (darkTheme) DarkColors else LightColors
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            content = content
        )
    }
}