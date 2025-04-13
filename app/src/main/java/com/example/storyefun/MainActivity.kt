package com.example.storyefun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue

import androidx.navigation.compose.*
import com.example.storyefun.navigation.AppNavigation
import com.example.storyefun.ui.theme.AppTheme
import com.example.storyefun.viewModel.ThemeViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // 👈 Add this line
        setContent {

            val navController = rememberNavController()
            val themeViewModel: ThemeViewModel = viewModel()  // Tạo ViewModel
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState() // Collect the theme state

            // Apply the theme dynamically based on the collected state
            AppTheme(darkTheme = isDarkTheme) {
                AppNavigation(navController)
            }
        }
    }
}


