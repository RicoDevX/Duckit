package com.chrisrich.duckit.ui.screens.auth

import DefaultEmailValidator
import EmailValidator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisrich.duckit.domain.model.AuthRequest
import com.chrisrich.duckit.domain.usecase.auth.LogInUseCase
import com.chrisrich.duckit.domain.usecase.auth.SignUpUseCase
import com.chrisrich.duckit.navigation.NavDestination
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling authentication logic.
 *
 * `AuthViewModel` manages the authentication state, processes user input events,
 * and interacts with use cases for logging in and signing up. It also handles
 * navigation within the authentication flow.
 *
 * ## Responsibilities:
 * - Manages `AuthViewState`, which represents the UI state for authentication.
 * - Processes authentication events (`AuthEvent`) and updates the state accordingly.
 * - Handles login and sign-up requests by invoking `LogInUseCase` and `SignUpUseCase`.
 * - Validates email input and ensures proper error handling.
 * - Controls navigation via `NavigationManager`.
 *
 * ## Dependencies:
 * - `LogInUseCase`: Executes login logic.
 * - `SignUpUseCase`: Executes sign-up logic.
 * - `SessionManager`: Manages user authentication tokens.
 * - `NavigationManager`: Handles app navigation.
 *
 * ## Authentication Flow:
 * 1. User inputs email and password.
 * 2. Events are triggered (`UpdateEmail`, `UpdatePassword`, `LogIn`, `SignUp`).
 * 3. ViewModel updates `AuthViewState` based on user input.
 * 4. Upon authentication success, stores token and navigates to `PostListScreen`.
 * 5. Handles authentication failures by displaying error messages.
 *
 * ## Event Handling:
 * - `UpdateEmail`: Updates the email state and manages focus.
 * - `UpdatePassword`: Updates the password state.
 * - `ToggleAuthMode`: Switches between login and sign-up modes.
 * - `LogIn`: Initiates the login process.
 * - `SignUp`: Initiates the sign-up process.
 * - `EmailLostFocus`: Validates email and displays errors only if non-empty.
 * - `NavigateBack`: Navigates back to `PostListScreen`.
 *
 * @see AuthViewState
 * @see AuthEvent
 */
class AuthViewModel(
    private val logInUseCase: LogInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val sessionManager: SessionManager,
    private val navigationManager: NavigationManager,
    private val emailValidator: EmailValidator = DefaultEmailValidator
) : ViewModel() {

    private val _state = MutableStateFlow(AuthViewState())
    val state: StateFlow<AuthViewState> = _state

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.UpdateEmail -> {
                _state.update { it.copy(email = event.email, isEmailFocused = true) }
            }

            is AuthEvent.UpdatePassword -> {
                _state.update { it.copy(password = event.password) }
            }

            AuthEvent.ToggleAuthMode -> {
                _state.update { it.copy(isSignUp = !it.isSignUp) }
            }

            AuthEvent.LogIn -> {
                logIn()
            }

            AuthEvent.SignUp -> {
                signUp()
            }

            AuthEvent.EmailLostFocus -> {
                val isEmailError = !emailValidator.isValid(_state.value.email)
                val isEmpty =
                    _state.value.email.isNotEmpty()//Don't show the error if the field is empty
                _state.update {
                    it.copy(
                        isEmailFocused = false,
                        isEmailError = isEmailError,
                        showEmailError = isEmpty
                    )
                }
            }

            AuthEvent.NavigateBack -> {
                navigationManager.navigate(NavDestination.PostListScreen)
            }
        }
    }

    private fun logIn() {
        viewModelScope.launch {
            val currentState = _state.value
            _state.update { it.copy(isLoading = true, error = null) }
            val request = AuthRequest(currentState.email, currentState.password)

            logInUseCase(request).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        sessionManager.saveAuthToken(response.token)
                        _state.update { it.copy(isLoading = false, authResponse = response) }
                        navigationManager.navigate(NavDestination.PostListScreen)
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false, error = error.message) }
                    }
                )
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            val currentState = _state.value
            _state.update { it.copy(isLoading = true, error = null) }
            val request = AuthRequest(currentState.email, currentState.password)

            signUpUseCase(request).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        sessionManager.saveAuthToken(response.token)
                        _state.update { it.copy(isLoading = false, authResponse = response) }
                        navigationManager.navigate(NavDestination.PostListScreen)
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false, error = error.message) }
                    }
                )
            }
        }
    }
}