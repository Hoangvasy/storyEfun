package com.example.storyefun.ui.theme

import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.example.storyefun.R

// Define App Colors
data class AppColors(
    val background: Color,  // ✅ Đổi thành lambda @Composable
    val textPrimary: Color,
    val textSecondary: Color,
    val buttonBackground: Color,
    val buttonText: Color,
    val backgroundColor: Color,
    val tagColor: Color,
    val backgroundContrast1: Color,
    val backgroundContrast2: Color,
    val header: Color,
    val buttonOrange: Color

)

// Light Theme
val LightColors = AppColors(
//    background = { painterResource(id = R.drawable.background) },  // ✅ Gọi trong @Composable lambda
    background = Color.White,
    header = Color(0xFFF4A261),
    textPrimary = Color.Black,
    textSecondary = Color.Gray,
    buttonBackground = Color.Red,
    buttonText = Color.White,
    backgroundColor = Color.White,
    tagColor = Color.DarkGray,
    backgroundContrast1 = Color.Black,
    backgroundContrast2 = Color.DarkGray,
    buttonOrange = Color(0xFFFFA500)

)

// Dark Theme
val DarkColors = AppColors(
//    background = { painterResource(id = R.drawable.darkbackground) }, // ✅ Gọi trong @Composable lambda
    background = Color.Black,
    header = Color.Gray,
    textPrimary = Color.White,
    textSecondary = Color.Gray,
    buttonBackground = Color.Red,
    buttonText = Color.Black,
    backgroundColor = Color.Black,
    tagColor = Color.Gray,
    backgroundContrast1 = Color.White,
    backgroundContrast2 = Color.Gray,
    buttonOrange = Color(0xFFFFA500)
)

// Create a Local variable to store colors
val LocalAppColors = staticCompositionLocalOf { LightColors }

// Function to apply theme
@Composable
fun AppTheme(
    darkTheme: Boolean,  // Biến này quyết định dùng Light hay Dark theme
    content: @Composable () -> Unit
) {
    Log.d("App them upadte", " update")
    val colors = remember(darkTheme) {
        if (darkTheme) DarkColors else LightColors
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            content = content
        )
    }
}



private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ZalopayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}