package com.chrisrich.duckit.ui.screens.postgallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisrich.duckit.domain.model.Post
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

sealed class PostGalleryEvent {
    data object RefreshPostList : PostGalleryEvent()
    data object FabClicked : PostGalleryEvent()
    data object LogOut : PostGalleryEvent()
    data object NavigateToSignIn : PostGalleryEvent()
    data object DismissLoginPrompt : PostGalleryEvent()
    data class RemovePost(val postId: String) : PostGalleryEvent()
    data class VotePost(val postId: String, val isUpvote: Boolean) : PostGalleryEvent()
    data class ShowPostDialog(val postId: String) : PostGalleryEvent()
    data object DismissPostDialog : PostGalleryEvent()
    data object ResetVoteError : PostGalleryEvent()
}

data class PostGalleryViewState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val showLoginPrompt: Boolean = false,
    val loginReason: Int = com.chrisrich.duckit.R.string.you_need_to_log_in_to_post_a_duck,
    val showPostDialog: Boolean = false,
    val selectedPostId: String? = null,
    val showVoteError: Boolean = false,
)

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

            PostGalleryEvent.ResetVoteError -> _state.update {
                it.copy(
                    showVoteError = false
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
            _state.update { it.copy(showLoginPrompt = true) }
            return
        }

        viewModelScope.launch {
            val voteResult =
                if (isUpvote) upvotePostUseCase(postId, "user_token") else downvotePostUseCase(
                    postId,
                    "user_token"
                )//TODO: Shows 403 when trying to use real token

            voteResult.collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _state.update { state ->
                            val updatedPosts = state.posts.map { post ->
                                if (post.id == postId) {
                                    post.copy(upvotes = response.votes)
                                } else post
                            }
                            state.copy(posts = updatedPosts)
                        }
                    },
                    onFailure = {
                        _state.update { state ->
                            state.copy(
                                showVoteError = true,
                            )
                        }
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
