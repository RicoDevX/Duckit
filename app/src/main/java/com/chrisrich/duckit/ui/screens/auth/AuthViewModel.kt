package com.chrisrich.duckit.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisrich.duckit.domain.model.AuthRequest
import com.chrisrich.duckit.domain.model.AuthResponse
import com.chrisrich.duckit.domain.usecase.auth.LogInUseCase
import com.chrisrich.duckit.domain.usecase.auth.SignUpUseCase
import com.chrisrich.duckit.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val authResponse: AuthResponse? = null,
    val error: String? = null
)

class AuthViewModel(
    private val logInUseCase: LogInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun logIn(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val request = AuthRequest(email, password)

            logInUseCase(request).collectLatest { result ->
                result.fold(
                    onSuccess = { response ->
                        sessionManager.saveAuthToken(response.token) // Save token on success
                        _state.update { it.copy(isLoading = false, authResponse = response) }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false, error = error.message) }
                    }
                )
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val request = AuthRequest(email, password)

            signUpUseCase(request).collectLatest { result ->
                result.fold(
                    onSuccess = { response ->
                        sessionManager.saveAuthToken(response.token) // Save token on success
                        _state.update { it.copy(isLoading = false, authResponse = response) }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false, error = error.message) }
                    }
                )
            }
        }
    }
}
