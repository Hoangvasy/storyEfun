package com.example.storyefun.navigation


import UploadScreen
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.profileui.ProfileScreen
import com.example.storyefun.ui.screens.*
import com.example.storyefun.ui.theme.ThemeViewModel

import com.example.storyefun.admin.ui.*


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object BookDetail : Screen("bookDetail")
    object Login : Screen("login")
    object Upload : Screen("upload")
    object Reader : Screen("reader")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object MyStory : Screen("mystory")
    object Setting : Screen("setting")
    object Category : Screen("category")

    object AdminMenu : Screen("menuScreen")
    object AdminUpload : Screen("uploadBook")
    object ManageBook : Screen("manageBook")
    object AddChapter : Screen("addCChapter/{bookId}")

}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(navController: NavHostController, themeViewModel: ThemeViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.ManageBook.route
//        startDestination = Screen.Upload.route
    ) {
        composable(Screen.Home.route) { HomeScreen(navController, themeViewModel) }
        composable(Screen.BookDetail.route) {BookDetailScreen(navController, themeViewModel)}
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Reader.route) { ReaderScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController, themeViewModel) }
        composable(Screen.Upload.route) { UploadScreen(navController) }
        composable(Screen.MyStory.route) { MyStoryScreen(navController) }
        composable(Screen.Setting.route) { SettingScreen(navController, themeViewModel) }
        composable(Screen.Category.route) { CategoryScreen(navController) }

        composable(Screen.AdminMenu.route) {MenuScreen(navController)}
        composable(Screen.AdminUpload.route) {AdminUploadScreen(navController)}
        composable(Screen.ManageBook.route) { ManageBooksScreen(navController) }
        composable("addChapter/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "Unknown"
            AddChapter(navController, bookId)
        }

    }
}
