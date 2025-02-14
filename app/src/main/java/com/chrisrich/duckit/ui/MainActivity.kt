package com.chrisrich.duckit.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.chrisrich.duckit.navigation.NavDestination
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.navigation.SetupNavGraph
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navigationManager: NavigationManager = koinViewModel()
            val currentDestination by navigationManager.currentDestination.collectAsState()

            SetupNavGraph(navController)

            // Observe navigation changes
            LaunchedEffect(currentDestination) {
                navController.navigate(currentDestination.route) {
                    popUpTo(NavDestination.PostListScreen.route) { inclusive = false }
                }
            }
        }
    }
}
