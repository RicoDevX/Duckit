package com.chrisrich.duckit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
val isLoading: Boolean = false,
val posts: PostListResponse? = null,
val token: String? = null,
val error: String? = null,
)

class DuckitViewModel(private val repo: DuckitRepo, private val sessionManager: SessionManager) :
    ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        checkUserSession()
        getTheDucks()
    }
    /**
     * Checks if a token exists in SessionManager on initialization.
     * Updates the UI state accordingly.
     */
    private fun checkUserSession() {
        val token = sessionManager.getAuthToken()
        _state.update { it.copy(token = token) }
    }

    private fun getTheDucks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repo.getPosts()
                .collect { result ->
                    result.fold(
                        onSuccess = { posts ->
                            _state.update { it.copy(isLoading = false, posts = posts) }
                        },
                        onFailure = { error ->
                            _state.update { it.copy(isLoading = false, error = error.message) }
                        }
                    )
                }
        }
    }

    /**
     * Attempts to sign in the user with the given email and password.
     * If the sign-in fails due to the account not being found (404 error),
     * it will automatically attempt to sign up the user with the same credentials.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    fun logIn(email: String, password: String) {
        viewModelScope.launch {
            repo.logIn(email, password).collect { result ->
                result.fold(
                    onSuccess = { authResponse ->
                        sessionManager.saveAuthToken(authResponse.token)
                        _state.update { it.copy(token = authResponse.token) }
                    },
                    onFailure = { error ->
                        if (error.message?.contains("404") == true || error.message?.contains("account not found") == true) {
                            // If the account does not exist, attempt sign up
                            signUp(email, password)
                        } else {
                            _state.update { it.copy(error = error.message) }
                        }
                    }
                )
            }
        }
    }

    /**
     * Attempts to sign up a new user with the given email and password.
     * If the sign-up is successful, the authentication token is stored
     * and the user is marked as signed in.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            repo.signUp(email, password).collect { result ->
                result.fold(
                    onSuccess = { authResponse ->
                        sessionManager.saveAuthToken(authResponse.token)
                        _state.update { it.copy(token = authResponse.token) }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                )
            }
        }
    }

    fun logout() {
        sessionManager.clearAuthToken()
        _state.update { UiState(token = null) }
    }

    fun upvote(postId: String) {
        viewModelScope.launch {
            state.value.token?.let { token ->
                repo.upvote(postId, token).collect { result ->
                    result.fold(
                        onSuccess = { _ -> getTheDucks() },
                        onFailure = { error -> _state.update { it.copy(error = error.message) } }
                    )
                }
            }
        }
    }

    fun downvote(postId: String) {
        viewModelScope.launch {
            state.value.token?.let { token ->
                repo.downvote(postId, token).collect { result ->
                    result.fold(
                        onSuccess = { _ -> getTheDucks() },
                        onFailure = { error -> _state.update { it.copy(error = error.message) } }
                    )
                }
            }
        }
    }
}
