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
import com.example.storyefun.ui.components.BottomBar
import com.example.storyefun.ui.theme.AppTheme
import com.example.storyefun.viewModel.ThemeViewModel
import com.google.firebase.FirebaseApp
import androidx.compose.material3.Scaffold
import com.example.storyefun.navigation.AppNavigationAdmin
import com.example.storyefun.navigation.AppNavigationUser

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val navController = rememberNavController()
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val isAdmin = themeViewModel.isAdmin.collectAsState(initial = false).value
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            AppTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (!isAdmin) {
                            BottomBar(
                                navController = navController,
                                currentRoute = currentRoute ?: ""
                            )
                        }
                    }
                ) { innerPadding ->
                    if (isAdmin) {
                        AppNavigationAdmin(navController = navController, themeViewModel)
                    } else {
                        AppNavigationUser(navController = navController, themeViewModel)
                    }
                }
            }
        }
    }
}
