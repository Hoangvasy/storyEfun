package com.example.storyefun.navigation


import UploadScreen
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.storyefun.ui.screens.CategoryScreen
import com.example.profileui.ProfileScreen
import com.example.storyefun.ui.screens.*
import com.example.storyefun.ui.theme.ThemeViewModel

import com.example.storyefun.admin.ui.*


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object BookDetail : Screen("bookDetail/{bookId}")

    object Login : Screen("login")
    object Upload : Screen("upload")
    object Reading : Screen("reading/{bookId}/{volumeOrder}/{chapterOrder}")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object MyStory : Screen("mystory")
    object Setting : Screen("setting")
    object CategoryList : Screen("category")

    object AdminMenu : Screen("menuScreen")
    object AdminUpload : Screen("uploadBook")
    object ManageBook : Screen("manageBook")
    //    object AddChapter : Screen("addCChapter/{bookId}")
    object EditBook : Screen("editBook/{bookId}")
    object AddVolume : Screen("addVolume/{bookId}")
    object ListChapter : Screen("listChapter/{bookId}/{volumeId}")
    object AddChapter : Screen("addChapter/{bookId}/{volumeId}")

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
//        startDestination = Screen.Upload.route
    ) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable("bookDetail/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "Unknown"
            BookDetailScreen(navController, bookId)
        }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Reading.route) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "unknown"
            val volumeOrderStr = backStackEntry.arguments?.getString("volumeOrder") ?: "1"
            val chapterOrderStr = backStackEntry.arguments?.getString("chapterOrder") ?: "1"

            // Parse strings to Int
            val volumeOrder = volumeOrderStr.toIntOrNull() ?: 1
            val chapterOrder = chapterOrderStr.toIntOrNull() ?: 1

            //Log.d("chapter and voluume ", "chapter $chapterOrder  volume $volumeOrder")
            ReaderScreen(navController, bookId, volumeOrder, chapterOrder)
        }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Upload.route) { UploadScreen(navController) }
        composable(Screen.MyStory.route) { MyStoryScreen(navController) }
        composable(Screen.Setting.route) { SettingScreen(navController) }
        composable(Screen.CategoryList.route) { CategoryScreen(navController) }

        composable(Screen.AdminMenu.route) {MenuScreen(navController)}
        composable(Screen.AdminUpload.route) {AdminUploadScreen(navController)}
        composable(Screen.ManageBook.route) { ManageBooksScreen(navController) }

        composable("editBook/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "Unknown"
            EditBook(navController, bookId)
        }
        composable("addVolume/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "Unknown"
            AddVolumeScreen(navController, bookId)
        }
        composable("chapter_list_screen/{bookId}/{volumeId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "Unknown"
            val volumeId = backStackEntry.arguments?.getString("volumeId") ?: "Unknown"
            ListChapterScreen(navController, bookId, volumeId)
        }
        composable("addChapter/{bookId}/{volumeId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "Unknown"
            val volumeId = backStackEntry.arguments?.getString("volumeId") ?: "Unknown"
            AddChapterScreen(navController, bookId, volumeId)
        }


    }
}