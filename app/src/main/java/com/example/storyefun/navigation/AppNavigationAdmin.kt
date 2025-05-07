package com.example.storyefun.navigation

import UploadScreen
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.storyefun.ui.screens.CategoryScreen
import com.example.profileui.ProfileScreen
import com.example.storyefun.ui.screens.*
import com.example.storyefun.admin.ui.*
import com.example.storyefun.ui.UserManageScreen
import com.example.storyefun.viewModel.ThemeViewModel
import com.google.common.base.Objects
import com.google.firebase.auth.FirebaseAuth


sealed class AdminScreen(val route: String) {

    object AdminMenu : Screen("menuScreen")
    object AdminUpload : Screen("uploadBook")
    object ManageBook : Screen("manageBook")
    object EditBook : Screen("editBook/{bookId}")
    object AddVolume : Screen("addVolume/{bookId}")
    object ListChapter : Screen("listChapter/{bookId}/{volumeId}")
    object AddChapter : Screen("addChapter/{bookId}/{volumeId}")
    object AddCategory : Screen("addCategory")
    object Desposite : Screen("desposite")
    object ManageUser : Screen("manageUser")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationAdmin(navController: NavHostController, themeViewModel: ThemeViewModel) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val start = if (currentUser != null) AdminScreen.AdminMenu.route else UserScreen.Login.route

    NavHost(
        navController = navController,
        startDestination = start
    ) {
        composable(Screen.ManageUser.route) { UserManageScreen(navController) }
        composable(Screen.AdminMenu.route) {MenuScreen(navController)}
        composable(Screen.AdminUpload.route) {AdminUploadScreen(navController)}
        composable(Screen.ManageBook.route) { ManageBooksScreen(navController) }
        composable(Screen.AddCategory.route) { AddCategory(navController, onCategoryAdded = {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.AddCategory.route) { inclusive = true }
            }
        }) }

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


        composable(Screen.ProductOrder.route) {
            ProductOrderScreen()
        }
        composable(Screen.Desposite.route) {
            DespositeScreen()
        }

    }
}