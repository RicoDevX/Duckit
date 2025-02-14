package com.chrisrich.duckit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chrisrich.duckit.ui.screens.auth.AuthScreen
import com.chrisrich.duckit.ui.screens.postlist.PostListScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun SetupNavGraph(navController: NavHostController) {
    val navigationManager: NavigationManager = koinViewModel()
    val currentDestination by navigationManager.currentDestination.collectAsState()

    NavHost(navController = navController, startDestination = NavDestination.PostListScreen.route) {
        composable(NavDestination.PostListScreen.route) {
            PostListScreen()
        }
        composable(NavDestination.AuthScreen.route) {
            AuthScreen()
        }
    }

    // Automatically navigate when the currentDestination changes
    LaunchedEffect(currentDestination) {
        navController.navigate(currentDestination.route) {
            popUpTo(NavDestination.PostListScreen.route) { inclusive = false }
        }
    }
}
