package com.chrisrich.duckit.ui.screens.auth

import com.chrisrich.duckit.domain.model.AuthResponse

/**
 * Represents the UI state for the authentication screen.
 *
 * This data class holds all necessary state variables related to user authentication.
 * It is used by the `AuthViewModel` to manage and update UI elements in the authentication flow.
 *
 * ## Properties:
 * - `email`: The current email input value.
 * - `password`: The current password input value.
 * - `isLoading`: Indicates whether an authentication request is in progress.
 * - `authResponse`: Holds the authentication response after a successful login or sign-up.
 * - `error`: Contains an error message if authentication fails.
 * - `isSignUp`: Determines whether the user is in sign-up mode (`true`) or login mode (`false`).
 * - `isEmailError`: Tracks whether the entered email is invalid.
 * - `showEmailError`: Ensures the email error message is only shown when appropriate.
 * - `isEmailFocused`: Tracks whether the email input field is currently focused.
 * - `isAuthenticated`: Indicates whether the user has been successfully authenticated.
 *
 * This state is observed in `AuthScreen` to dynamically update the UI.
 */
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
//    /**
//     * Determines whether the email error message should be displayed.
//     *
//     * This function checks multiple conditions to decide if the email error should be shown:
//     * - `isEmailError`: Indicates if the email is invalid.
//     * - `!isEmailFocused`: Ensures the error is shown only when the email input field is not focused.
//     * - `showEmailError`: Prevents showing the error until validation has been triggered (e.g., after losing focus).
//     *
//     * @return `true` if the email error should be displayed, otherwise `false`.
//     */
//    fun shouldShowEmailError(): Boolean {
//        return isEmailError && !isEmailFocused && showEmailError
//    }
}