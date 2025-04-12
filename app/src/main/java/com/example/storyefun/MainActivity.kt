package com.example.storyefun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.storyefun.navigation.AppNavigation
import com.example.storyefun.ui.screens.*
import com.example.storyefun.ui.theme.AppTheme
import com.example.storyefun.ui.theme.ThemeViewModel
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // 👈 Add this line
        setContent {

            val navController = rememberNavController()
            val themeViewModel: ThemeViewModel = viewModel()  // Tạo ViewModel
            AppTheme(darkTheme = themeViewModel.isDarkTheme.collectAsState().value) {
                AppNavigation(navController)
            }
        }
    }
}

