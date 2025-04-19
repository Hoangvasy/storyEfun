package com.example.storyefun

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue

import androidx.navigation.compose.*
import com.example.storyefun.data.models.Category
import com.example.storyefun.data.repository.CategoryFirebase
import com.example.storyefun.navigation.AppNavigation
import com.example.storyefun.ui.theme.AppTheme
import com.example.storyefun.viewModel.ThemeViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) //  Add this line
        setContent {

            val navController = rememberNavController()
            val themeViewModel: ThemeViewModel = viewModel()  // Tạo ViewModel

            // Apply the theme dynamically based on the collected state

            // Observe the isDarkTheme state
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            // Wrap the entire app in AppTheme with the latest isDarkTheme
            AppTheme(darkTheme = isDarkTheme) {
                AppNavigation(navController, themeViewModel)
            }
        }
//        val newCategory = Category(name = "Tiểu thuyết", description = "Belinski: \"Tiểu thuyết là sử thi của đời tư\"")
//        CategoryFirebase(newCategory) {
//                isSuccess ->
//            if (isSuccess) {
//                Log.d("Firestore", "Category added successfully!")
//            } else {
//                Log.e("Firestore", "Failed to add category.")
//            }
//        }
    }
}


