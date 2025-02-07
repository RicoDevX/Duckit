package com.chrisrich.duckit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuckPostScreen(navController: NavController) {
    val viewModel: DuckitViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val isLoggedIn = uiState.token != null
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
                           viewModel.logout()
                            navController.navigate("duckPostScreen") // Refresh screen
                        } else {
                            navController.navigate("signUpScreen") // Navigate to Sign Up
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
                uiState.error != null -> ErrorScreen(uiState.error!!)
                else -> DuckPostList(posts = uiState.posts?.posts, viewModel, isLoggedIn, onShowLoginPrompt = { showLoginPrompt = true })
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
                    navController.navigate("signUpScreen")
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

@Composable
fun DuckPostList(posts: List<Post>?, viewModel: DuckitViewModel, isLoggedIn: Boolean, onShowLoginPrompt: () -> Unit) {
    if (posts.isNullOrEmpty()) {
        EmptyStateScreen()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts.size) { index ->
                DuckPostItem(post = posts[index], viewModel, isLoggedIn, onShowLoginPrompt)
            }
        }
    }
}

@Composable
fun EmptyStateScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "No Posts Found",
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No ducks found!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DuckPostItem(post: Post, viewModel: DuckitViewModel, isLoggedIn: Boolean, onShowLoginPrompt: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NetworkImage(url = post.image, contentDescription = "Duck Image")
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.headline,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "By ${post.author}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                    IconButton(onClick = {
                        if (isLoggedIn) viewModel.upvote(post.id) else onShowLoginPrompt()
                    }) {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Upvote", tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${post.upvotes}")
                    IconButton(onClick = {
                        if (isLoggedIn) viewModel.downvote(post.id) else onShowLoginPrompt()
                    }) {
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Downvote", tint = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkImage(url: String, contentDescription: String) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error: $errorMessage", color = Color.Red, fontSize = 18.sp)
    }
}
