package com.chrisrich.duckit.ui.screens.postgallery

import PostDetailFullScreenDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrisrich.duckit.R
import com.chrisrich.duckit.ui.screens.postgallery.components.ErrorScreen
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PostGalleryScreen(viewModel: PostGalleryViewModel = koinViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val topBarColor = Color(0xFF45C066)
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = { viewModel.onEvent(PostGalleryEvent.RefreshPostList) }
    )

    LaunchedEffect(uiState.showVoteError) {
        if (uiState.showVoteError) {
            delay(2000)
            viewModel.onEvent(PostGalleryEvent.ResetVoteError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_duckit_logo),
                        contentDescription = stringResource(R.string.duck_logo),
                        modifier = Modifier.size(200.dp)
                    )
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (uiState.isLoggedIn) viewModel.logOut() else viewModel.onEvent(
                                PostGalleryEvent.NavigateToSignIn
                            )
                        }
                    ) {
                        Text(
                            text = if (uiState.isLoggedIn) stringResource(R.string.log_out) else stringResource(
                                R.string.sign_in
                            ),
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarColor,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(PostGalleryEvent.FabClicked) },
                containerColor = topBarColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.submit))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            when {
                uiState.error != null -> {
                    ErrorScreen(uiState.error ?: stringResource(R.string.no_posts_found))
                }

                uiState.posts.isNotEmpty() -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(150.dp),
                        modifier = Modifier.fillMaxSize(),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(uiState.posts) { post ->
                            PostItem(
                                modifier = Modifier.animateItem(),
                                post = post,
                                onEvent = { event -> viewModel.onEvent(event) }
                            )
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    uiState.selectedPostId?.let { postId ->
        uiState.posts.find { it.id == postId }?.let { post ->
            PostDetailFullScreenDialog(
                post = post,
                currentVotes = post.upvotes,
                onDismiss = { viewModel.onEvent(PostGalleryEvent.DismissPostDialog) },
                onVote = { isUpvote ->
                    viewModel.onEvent(PostGalleryEvent.VotePost(post.id, isUpvote))
                },
                showVoteError = uiState.showVoteError,
            )
        }
    }

    if (uiState.showLoginPrompt) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(PostGalleryEvent.DismissLoginPrompt) },
            title = { Text(stringResource(R.string.login_required)) },
            text = { Text(stringResource(uiState.loginReason)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(PostGalleryEvent.DismissLoginPrompt)
                        viewModel.onEvent(PostGalleryEvent.NavigateToSignIn)
                    }
                ) {
                    Text(stringResource(R.string.sign_in))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(PostGalleryEvent.DismissLoginPrompt) }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
