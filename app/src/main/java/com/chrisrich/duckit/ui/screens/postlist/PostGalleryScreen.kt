package com.chrisrich.duckit.ui.screens.postlist

import PostGallery
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrisrich.duckit.R
import com.chrisrich.duckit.navigation.NavDestination
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.ui.screens.postlist.components.ErrorScreen
import com.chrisrich.duckit.ui.screens.postlist.components.LoadingScreen
import com.chrisrich.duckit.utils.SessionManager
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen() {
    val viewModel: PostListViewModel = koinViewModel()
    val navigationManager: NavigationManager = koinViewModel()
    val sessionManager: SessionManager = koinInject()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    // Use sessionManager to check if user is logged in
    var isLoggedIn by remember { mutableStateOf(sessionManager.getAuthToken() != null) }
    var showLoginPrompt by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_duckit_logo),
                        contentDescription = "Duck Logo",
                        modifier = Modifier.size(200.dp)
                    )
                },
                actions = {
                    TextButton(onClick = {
                        if (isLoggedIn) {
                            sessionManager.clearAuthToken() // Log out user
                            isLoggedIn = false
                            navigationManager.navigate(NavDestination.PostListScreen) // Refresh screen
                        } else {
                            navigationManager.navigate(NavDestination.AuthScreen) // Navigate to Sign Up
                        }
                    }) {
                        Text(
                            text = if (isLoggedIn) "Log Out" else "Sign In",
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF45C066),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState.isLoading -> LoadingScreen()
                uiState.error != null || uiState.posts.isNullOrEmpty() -> ErrorScreen(
                    uiState.error ?: "No posts found"
                )

                else -> PostGallery (
                    posts = uiState.posts ?: emptyList(),
                    onShowLoginPrompt = { showLoginPrompt = true }
                )
            }
        }
    }

    if (showLoginPrompt) {
        AlertDialog(
            onDismissRequest = { showLoginPrompt = false },
            title = { Text("Login Required") },
            text = { Text("You need to log in to vote on posts.") },
            confirmButton = {
                TextButton(onClick = {
                    showLoginPrompt = false
                    navigationManager.navigate(NavDestination.AuthScreen)
                }) {
                    Text("Log In")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginPrompt = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
