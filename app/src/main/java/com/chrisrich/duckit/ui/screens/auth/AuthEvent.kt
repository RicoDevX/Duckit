package com.chrisrich.duckit.ui.screens.auth

/**
 * Represents various user interactions and state changes in the authentication flow.
 *
 * This sealed class defines different authentication-related events that the `AuthViewModel`
 * can handle to update the UI state and trigger navigation or authentication actions.
 *
 * ## Events:
 * - `UpdateEmail(email: String)`: Updates the email input field.
 * - `UpdatePassword(password: String)`: Updates the password input field.
 * - `ToggleAuthMode`: Switches between login and sign-up modes.
 * - `LogIn`: Initiates the login process with the current credentials.
 * - `SignUp`: Initiates the sign-up process with the current credentials.
 * - `EmailLostFocus`: Triggers validation when the email input field loses focus.
 * - `NavigateBack`: Handles user navigation back to the previous screen.
 *
 * These events are processed in `AuthViewModel` to modify `AuthViewState` or trigger authentication actions.
 */
sealed class AuthEvent {
    data class UpdateEmail(val email: String) : AuthEvent()
    data class UpdatePassword(val password: String) : AuthEvent()
    data object ToggleAuthMode : AuthEvent()
    data object LogIn : AuthEvent()
    data object SignUp : AuthEvent()
    data object EmailLostFocus : AuthEvent()
    data object NavigateBack : AuthEvent()
}