package com.chrisrich.duckit.ui.screens.newpost

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisrich.duckit.domain.model.NewPostRequest
import com.chrisrich.duckit.domain.usecase.newpost.NewPostUseCase
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class NewPostEvent {
    data class UpdateHeadline(val headline: String) : NewPostEvent()
    data class UpdateImageUrl(val imageUrl: String) : NewPostEvent()
    data class ImageUrlFocusChanged(val isFocused: Boolean) : NewPostEvent()
    data class UpdateImageLoadState(val isValid: Boolean) : NewPostEvent()
    data object SubmitPost : NewPostEvent()
    data object ResetForm : NewPostEvent()
    data object NavigateBack : NewPostEvent()
}

data class NewPostViewState(
    val headline: String = "",
    val imageUrl: String = "",
    val isSubmitting: Boolean = false,
    val isImageValid: Boolean = false,
    val imageUrlError: String? = null,
    val showImageUrlError: Boolean = false,
    val errorMessage: String? = null,
    val postSuccess: Boolean = false,
    val postedPost: NewPostRequest? = null
)

class NewPostViewModel(
    private val useCase: NewPostUseCase,
    private val sessionManager: SessionManager,
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val _state = MutableStateFlow(NewPostViewState())
    val state: StateFlow<NewPostViewState> = _state.asStateFlow()

    fun onEvent(event: NewPostEvent) {
        when (event) {
            is NewPostEvent.UpdateHeadline -> {
                _state.update { it.copy(headline = event.headline) }
            }

            is NewPostEvent.UpdateImageUrl -> {
                _state.update {
                    it.copy(
                        imageUrl = event.imageUrl,
                        isImageValid = false,
                        showImageUrlError = false
                    )
                }
                validateImageUrl(event.imageUrl)
            }

            is NewPostEvent.ImageUrlFocusChanged -> {
                _state.update { it.copy(showImageUrlError = !event.isFocused) }
            }

            is NewPostEvent.UpdateImageLoadState -> {
                _state.update { it.copy(isImageValid = event.isValid) }
            }

            is NewPostEvent.SubmitPost -> {
                submitPost()
            }

            is NewPostEvent.ResetForm -> {
                _state.update {
                    NewPostViewState()
                }
            }

            is NewPostEvent.NavigateBack -> navigateBack()
        }
    }

    private fun navigateBack() {
        navigationManager.navigateBack()
    }

    private val supportedImageExtensions = listOf("jpg", "jpeg", "png", "webp", "gif")


    private fun validateImageUrl(imageUrl: String) {
        if (imageUrl.isBlank()) {
            _state.update { it.copy(imageUrlError = "Image URL cannot be empty") }
            return
        }

        if (!Patterns.WEB_URL.matcher(imageUrl).matches()) {
            _state.update { it.copy(imageUrlError = "Invalid URL format") }
            return
        }

        val isSupported = supportedImageExtensions.any { ext -> imageUrl.endsWith(".$ext", ignoreCase = true) }
        if (!isSupported) {
            _state.update { it.copy(imageUrlError = "URL must end with a supported image format (.jpg, .png, etc.)") }
            return
        }

        _state.update { it.copy(imageUrlError = null) }
    }

    private fun submitPost() {
        val currentState = _state.value
        if (currentState.headline.isBlank() || currentState.imageUrl.isBlank() || !currentState.isImageValid) {
            _state.update {
                it.copy(errorMessage = "Ensure the headline is filled and the image has loaded successfully.")
            }
            return
        }

        _state.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                sessionManager.getAuthToken()?.let { token ->
                    val postRequest = NewPostRequest(currentState.headline, currentState.imageUrl)
                    useCase.invoke("user_token", postRequest)

                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            postSuccess = true,
                            postedPost = postRequest
                        )
                    }

                } ?: run {
                    _state.update { it.copy(isSubmitting = false, errorMessage = "Authentication required") }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isSubmitting = false, errorMessage = e.message ?: "Failed to post")
                }
            }
        }
    }
}
