package com.chrisrich.duckit.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisrich.duckit.domain.model.AuthRequest
import com.chrisrich.duckit.domain.model.AuthResponse
import com.chrisrich.duckit.domain.usecase.auth.LogInUseCase
import com.chrisrich.duckit.domain.usecase.auth.SignUpUseCase
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.utils.EmailValidator
import com.chrisrich.duckit.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AuthEvent {
    data class UpdateEmail(val email: String) : AuthEvent()
    data class UpdatePassword(val password: String) : AuthEvent()
    data class Authenticate(val isSignUp: Boolean) : AuthEvent()
    data object ToggleAuthMode : AuthEvent()
    data object EmailLostFocus : AuthEvent()
    data object NavigateBack : AuthEvent()
}

data class AuthViewState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val authResponse: AuthResponse? = null,
    val error: String? = null,
    val isSignUp: Boolean = false,
    val isEmailError: Boolean = false,
    val showEmailError: Boolean = false,
    val isEmailFocused: Boolean = false,
    val isAuthenticated: Boolean = false
) {
    fun shouldShowEmailError(): Boolean {
        return isEmailError && !isEmailFocused && showEmailError
    }
}

class AuthViewModel(
    private val logInUseCase: LogInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val sessionManager: SessionManager,
    private val navigationManager: NavigationManager,
    private val emailValidator: EmailValidator
) : ViewModel() {

    private val _state = MutableStateFlow(AuthViewState())
    val state: StateFlow<AuthViewState> = _state.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.UpdateEmail -> {
                _state.update {
                    it.copy(
                        email = event.email,
                        isEmailFocused = true,
                        showEmailError = false
                    )
                }
            }

            is AuthEvent.EmailLostFocus -> {
                val email = _state.value.email
                val isEmailValid = email.isEmpty() || emailValidator.isValid(email)
                _state.update {
                    it.copy(
                        isEmailFocused = false,
                        isEmailError = email.isNotEmpty() && !isEmailValid,
                        showEmailError = email.isNotEmpty() && !isEmailValid
                    )
                }
            }

            is AuthEvent.UpdatePassword -> _state.update { it.copy(password = event.password) }
            is AuthEvent.ToggleAuthMode -> _state.update { it.copy(isSignUp = !it.isSignUp) }
            is AuthEvent.Authenticate -> authenticateUser(event.isSignUp)
            is AuthEvent.NavigateBack -> navigationManager.navigateBack()
        }
    }

    private fun authenticateUser(isSignUp: Boolean) {
        val currentState = _state.value
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(error = "Email and password cannot be empty") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val request = AuthRequest(currentState.email, currentState.password)
            val authFlow = if (isSignUp) signUpUseCase(request) else logInUseCase(request)

            authFlow.collect { result ->
                result.fold(
                    onSuccess = { response ->
                        sessionManager.saveAuthToken(response.token)
                        _state.update { it.copy(isLoading = false, isAuthenticated = true) }

                        navigationManager.navigateBack()
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false, error = error.message ?: "Authentication failed") }
                    }
                )
            }
        }
    }
}
