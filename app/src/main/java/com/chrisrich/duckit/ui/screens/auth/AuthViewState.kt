package com.chrisrich.duckit.ui.screens.auth

import com.chrisrich.duckit.domain.model.AuthResponse

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