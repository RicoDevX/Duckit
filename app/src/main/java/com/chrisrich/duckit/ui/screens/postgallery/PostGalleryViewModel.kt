import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisrich.duckit.R
import com.chrisrich.duckit.domain.model.Post
import com.chrisrich.duckit.domain.usecase.postgallery.DownvotePostUseCase
import com.chrisrich.duckit.domain.usecase.postgallery.GetPostsUseCase
import com.chrisrich.duckit.domain.usecase.postgallery.UpvotePostUseCase
import com.chrisrich.duckit.navigation.NavDestination
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.ui.screens.postgallery.PostGalleryViewState
import com.chrisrich.duckit.ui.screens.postgallery.components.post.PostViewState
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

    private val _state = MutableStateFlow(PostGalleryViewState())
    val state: StateFlow<PostGalleryViewState> = _state.asStateFlow()

    private val _showLoginPrompt = MutableStateFlow(false)
    val showLoginPrompt: StateFlow<Boolean> = _showLoginPrompt.asStateFlow()

    private val _loginReason = MutableStateFlow(R.string.you_need_to_log_in_to_post_a_duck)
    val loginReason: StateFlow<Int> = _loginReason.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(sessionManager.getAuthToken() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _showPostDialog = MutableStateFlow(false)
    val showPostDialog: StateFlow<Boolean> = _showPostDialog.asStateFlow()

    private val _selectedPost = MutableStateFlow<PostViewState?>(null)
    val selectedPost: StateFlow<PostViewState?> = _selectedPost.asStateFlow()

    init {
        getPostList()
    }

    fun getPostList() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            _isLoggedIn.value = token != null
            _state.update { it.copy(isLoading = true, error = null) }

            getPostsUseCase(token).collectLatest { result ->
                result.fold(
                    onSuccess = { response ->
                        val postsWithState = response.posts.map { it.toViewState() }
                        _state.update { it.copy(isLoading = false, posts = postsWithState) }
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

    fun showPostDialog(post: PostViewState) {
        _selectedPost.value = post
        _showPostDialog.value = true
    }

    fun dismissPostDialog() {
        _showPostDialog.value = false
        _selectedPost.value = null
    }

    private fun upvote(postId: String, token: String) {
        viewModelScope.launch {
            updatePostState(postId, isLoading = true)

            upvotePostUseCase(postId, token).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        updatePostState(postId, votes = response.votes, isLoading = false)
                    },
                    onFailure = { error ->
                        updatePostState(
                            postId,
                            error = error.message ?: "Failed to upvote",
                            isLoading = false
                        )
                    }
                )
            }
        }
    }

    private fun downvote(postId: String, token: String) {
        viewModelScope.launch {
            updatePostState(postId, isLoading = true)

            downvotePostUseCase(postId, token).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        updatePostState(postId, votes = response.votes, isLoading = false)
                    },
                    onFailure = { error ->
                        updatePostState(
                            postId,
                            error = error.message ?: "Failed to downvote",
                            isLoading = false
                        )
                    }
                )
            }
        }
    }

    fun removePost(postId: String) {
        _state.update { currentState ->
            val updatedPosts = currentState.posts.filterNot { it.id == postId }
            currentState.copy(posts = updatedPosts)
        }
    }

    private fun updatePostState(
        postId: String,
        votes: Int? = null,
        isLoading: Boolean? = null,
        error: String? = null
    ) {
        _state.update { state ->
            state.copy(posts = state.posts.map {
                if (it.id == postId) {
                    it.copy(
                        votes = votes ?: it.votes,
                        isLoading = isLoading ?: it.isLoading,
                        error = error
                    )
                } else it
            })
        }
    }

    fun onFabClicked() {
        dismissPostDialog() // 🔥 Ensure post dialog is dismissed

        if (_isLoggedIn.value) {
            navigationManager.navigate(NavDestination.NewPostScreen)
        } else {
            _loginReason.value = R.string.you_need_to_log_in_to_post_a_duck
            _showLoginPrompt.update { true }
        }
    }

    fun onVoteClicked(postId: String, isUpvote: Boolean) {
        dismissPostDialog()

        val token = sessionManager.getAuthToken()
        _isLoggedIn.value = token != null

        if (token.isNullOrEmpty()) {
            _loginReason.value = R.string.you_need_to_log_in_to_vote_on_posts
            _showLoginPrompt.update { true }
            return
        }

        if (isUpvote) upvote(postId, token) else downvote(postId, token)
    }

    fun navigateToSignIn() {
        dismissLoginPrompt()
        navigationManager.navigate(NavDestination.AuthScreen)
    }

    fun logOut() {
        sessionManager.clearAuthToken()
        _isLoggedIn.value = false
        getPostList()
    }

    fun dismissLoginPrompt() {
        _showLoginPrompt.update { false }
    }

    private fun Post.toViewState() = PostViewState(
        id = this.id,
        headline = this.headline,
        image = this.image,
        author = this.author,
        votes = this.upvotes
    )
}
