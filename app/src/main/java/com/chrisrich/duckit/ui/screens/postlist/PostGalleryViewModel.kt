package com.chrisrich.duckit.ui.screens.postlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisrich.duckit.domain.model.Post
import com.chrisrich.duckit.domain.usecase.postlist.DownvotePostUseCase
import com.chrisrich.duckit.domain.usecase.postlist.GetPostsUseCase
import com.chrisrich.duckit.domain.usecase.postlist.UpvotePostUseCase
import com.chrisrich.duckit.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostState(
    val isLoading: Boolean = false,
    val posts: List<Post>? = null,
    val error: String? = null
)

class PostListViewModel(
    private val getPostsUseCase: GetPostsUseCase,
    private val upvotePostUseCase: UpvotePostUseCase,
    private val downvotePostUseCase: DownvotePostUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(PostState())
    val state: StateFlow<PostState> = _state

    init {
        getPostList()
    }

    fun getPostList() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            _state.update { it.copy(isLoading = true, error = null) }

            getPostsUseCase(token).collectLatest { result ->
                result.fold(
                    onSuccess = { response ->
                        _state.update { it.copy(isLoading = false, posts = response.posts) }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false, error = error.message) }
                    }
                )
            }
        }
    }

    fun upvotePost(postId: String, onVoteUpdated: (Int) -> Unit) {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token == null) {
                _state.update { it.copy(error = "User not logged in") }
                return@launch
            }

            upvotePostUseCase(postId, "token").collectLatest { result ->
                result.fold(
                    onSuccess = { response ->
                        onVoteUpdated(response.votes) // Return new vote count
                    },
                    onFailure = { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                )
            }
        }
    }

    fun downvotePost(postId: String, onVoteUpdated: (Int) -> Unit) {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token == null) {
                _state.update { it.copy(error = "User not logged in") }
                return@launch
            }

            downvotePostUseCase(postId, "token").collectLatest { result ->
                result.fold(
                    onSuccess = { response ->
                        onVoteUpdated(response.votes) // Return new vote count
                    },
                    onFailure = { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                )
            }
        }
    }

    fun removePost(postId: String) {
        _state.update { currentState ->
            val updatedPosts = currentState.posts?.filterNot { it.id == postId }
            currentState.copy(posts = updatedPosts)
        }
    }
}
