package com.chrisrich.duckit.ui.screens.auth

sealed class AuthEvent {
    data class UpdateEmail(val email: String) : AuthEvent()
    data class UpdatePassword(val password: String) : AuthEvent()
    data object ToggleAuthMode : AuthEvent()
    data object LogIn : AuthEvent()
    data object SignUp : AuthEvent()
    data object EmailLostFocus : AuthEvent()
    data object NavigateBack : AuthEvent()
}