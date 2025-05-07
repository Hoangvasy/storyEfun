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


//import com.example.storyefun.ui.AddChapterScreen
import com.example.storyefun.viewModel.ThemeViewModel
import com.google.common.base.Objects
import com.google.firebase.auth.FirebaseAuth


sealed class UserScreen(val route: String) {
    object Home : Screen("home")
    object BookDetail : Screen("bookDetail/{bookId}")

    object ProductOrder : Screen("productOrder")


    object Login : Screen("login")
    object Upload : Screen("upload")
    object Reading : Screen("reading/{bookId}/{volumeOrder}/{chapterOrder}")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object Favourite : Screen("favourite")
    object Setting : Screen("setting")
    object CategoryList : Screen("category")
    object Search : Screen("search")
    object Desposite : Screen("desposite")
    object Coin : Screen("coin")
    object ManageUser : Screen("manageUser")
    object PaymentNotification : Screen("paymentNotification")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationUser(navController: NavHostController, themeViewModel: ThemeViewModel) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val start = if (currentUser != null) UserScreen.Home.route else UserScreen.Login.route

    NavHost(
        navController = navController,
        startDestination = start
    ) {
//        composable(Screen.Home.route) { HomeScreen(navController, themeViewModel) }
        composable(Screen.Home.route) { HomeBookScreen(navController) }
        composable("bookDetail/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "Unknown"
            BookDetailScreen(navController, bookId, themeViewModel)
        }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Reading.route) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "unknown"
            val volumeOrderStr = backStackEntry.arguments?.getString("volumeOrder") ?: "1"
            val chapterOrderStr = backStackEntry.arguments?.getString("chapterOrder") ?: "1"

            // Parse strings to Int
            val volumeOrder = volumeOrderStr.toLongOrNull() ?: 1
            val chapterOrder = chapterOrderStr.toLongOrNull() ?: 1

            //Log.d("chapter and voluume ", "chapter $chapterOrder  volume $volumeOrder")
            ReaderScreen(navController, bookId, volumeOrder, chapterOrder, themeViewModel)
        }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController, themeViewModel) }
        composable(Screen.Upload.route) { UploadScreen(navController) }
        composable(Screen.Favourite.route) { FavouriteScreen(themeViewModel, navController) }
        composable(Screen.Setting.route) { SettingScreen(navController, themeViewModel) }
        composable(Screen.CategoryList.route) { CategoriesScreen(navController) }
        composable(Screen.Desposite.route) { DespositeScreen() }
        composable(Screen.Coin.route) { CoinScreen(navController) }
        composable(Screen.Search.route) { SearchScreen(navController) }

        composable(Screen.ProductOrder.route) {
            ProductOrderScreen()
        }
        composable(Screen.Desposite.route) {
            DespositeScreen()
        }

    }
}