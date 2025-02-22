package com.chrisrich.duckit.ui.screens.postgallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisrich.duckit.domain.usecase.postgallery.DownvotePostUseCase
import com.chrisrich.duckit.domain.usecase.postgallery.GetPostsUseCase
import com.chrisrich.duckit.domain.usecase.postgallery.UpvotePostUseCase
import com.chrisrich.duckit.navigation.NavDestination
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostGalleryViewModel(
    private val getPostsUseCase: GetPostsUseCase,
    private val upvotePostUseCase: UpvotePostUseCase,
    private val downvotePostUseCase: DownvotePostUseCase,
    private val sessionManager: SessionManager,
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val _state = MutableStateFlow(
        PostGalleryViewState(
            isLoggedIn = sessionManager.getAuthToken() != null
        )
    )
    val state: StateFlow<PostGalleryViewState> = _state.asStateFlow()

    init {
        getPostList()
    }

    fun onEvent(event: PostGalleryEvent) {
        when (event) {
            is PostGalleryEvent.RefreshPostList -> getPostList()
            is PostGalleryEvent.FabClicked -> onFabClicked()
            is PostGalleryEvent.LogOut -> logOut()
            is PostGalleryEvent.NavigateToSignIn -> navigateToSignIn()
            is PostGalleryEvent.DismissLoginPrompt -> _state.update { it.copy(showLoginPrompt = false) }
            is PostGalleryEvent.RemovePost -> removePost(event.postId)
            is PostGalleryEvent.VotePost -> onVoteClicked(event.postId, event.isUpvote)
            is PostGalleryEvent.ShowPostDialog -> _state.update {
                it.copy(
                    selectedPostId = event.postId,
                    showPostDialog = true
                )
            }

            is PostGalleryEvent.DismissPostDialog -> _state.update {
                it.copy(
                    selectedPostId = null,
                    showPostDialog = false
                )
            }
        }
    }

    private fun getPostList() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            _state.update { it.copy(isLoggedIn = token != null, isLoading = true, error = null) }

            getPostsUseCase(token).collectLatest { result ->
                result.fold(
                    onSuccess = { response ->
                        _state.update { it.copy(isLoading = false, posts = response.posts) }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to load posts"
                            )
                        }
                    }
                )
            }
        }
    }

    private fun removePost(postId: String) {
        _state.update { it.copy(posts = it.posts.filterNot { post -> post.id == postId }) }
    }

    private fun updatePostState(
        postId: String,
        votes: Int? = null
    ) {
        _state.update { state ->
            val updatedPosts = state.posts.map {
                if (it.id == postId) {
                    it.copy(
                        upvotes = votes ?: it.upvotes,
                    )
                } else it
            }
            state.copy(posts = updatedPosts)
        }
    }

    private fun onFabClicked() {
        _state.update { it.copy(showPostDialog = false, selectedPostId = null) }

        if (state.value.isLoggedIn) {
            navigationManager.navigate(NavDestination.NewPostScreen)
        } else {
            _state.update { it.copy(showLoginPrompt = true) }
        }
    }

    private fun onVoteClicked(postId: String, isUpvote: Boolean) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrEmpty()) {
            _state.update {
                it.copy(
                    showLoginPrompt = true
                )
            }
            return
        }

        viewModelScope.launch {
            updatePostState(postId)

            val voteResult =
                if (isUpvote) upvotePostUseCase(postId, token) else downvotePostUseCase(
                    postId,
                    token
                )

            voteResult.collect { result ->
                result.fold(
                    onSuccess = { response ->
                        updatePostState(postId, votes = response.votes)
                    },
                    onFailure = {
                        updatePostState(
                            postId//Fail Silently - No need to update votes
                        )
                    }
                )
            }
        }
    }

    private fun navigateToSignIn() {
        _state.update {
            it.copy(
                showLoginPrompt = false,
                showPostDialog = false,
                selectedPostId = null
            )
        }
        navigationManager.navigate(NavDestination.AuthScreen)
    }

    fun logOut() {
        sessionManager.clearAuthToken()
        _state.update { it.copy(isLoggedIn = false) }
        getPostList()
    }
}
