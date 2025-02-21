package com.chrisrich.duckit.ui.screens.newpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisrich.duckit.domain.model.NewPostRequest
import com.chrisrich.duckit.domain.usecase.newpost.NewPostUseCase
import com.chrisrich.duckit.navigation.NavDestination
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewPostViewModel(
    private val useCase: NewPostUseCase,
    private val sessionManager: SessionManager,
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val _state = MutableStateFlow(NewPostViewState())
    val state: StateFlow<NewPostViewState> = _state.asStateFlow()

    fun updateHeadline(headline: String) {
        _state.update { it.copy(headline = headline) }
    }

    fun updateImageUrl(imageUrl: String) {
        _state.update { it.copy(imageUrl = imageUrl) }
    }

    fun submitPost() {

        val currentState = _state.value
        if (currentState.headline.isBlank() || currentState.imageUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Headline and Image URL cannot be empty") }
            return
        }

        _state.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                sessionManager.getAuthToken()?.let { token ->
                    useCase.invoke(
                        token,
                        NewPostRequest(currentState.headline, currentState.imageUrl)
                    )
                    _state.update { it.copy(isSubmitting = false) }

                    navigationManager.navigate(NavDestination.PostGalleryScreen)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isSubmitting = false, errorMessage = e.message ?: "Failed to post")
                }
            }
        }
    }
}
