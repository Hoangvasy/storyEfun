package com.example.storyefun

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*

import com.example.storyefun.navigation.AppNavigation
import com.example.storyefun.ui.components.BottomBar
import com.example.storyefun.ui.theme.AppTheme
import com.example.storyefun.viewModel.ThemeViewModel
import com.google.firebase.FirebaseApp
import androidx.compose.material3.Scaffold

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Add this line
        setContent {

            val navController = rememberNavController()
            val themeViewModel: ThemeViewModel = viewModel()  // Tạo ViewModel

            // Observe the isDarkTheme state
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            // Lấy route hiện tại từ navController
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            // Apply the theme dynamically based on the collected state
            AppTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBar(
                            navController = navController,
                            currentRoute = currentRoute ?: "" // Dự phòng khi currentRoute là null
                        )
                    }
                ) { innerPadding ->
                    // Wrap the entire app content in AppNavigation
                    AppNavigation(
                        navController = navController,
                        themeViewModel = themeViewModel,
                    )
                }
            }
        }
    }
}
