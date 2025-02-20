package com.chrisrich.duckit.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user authentication session using SharedPreferences.
 *
 * The `SessionManager` class is responsible for storing, retrieving, and clearing
 * authentication tokens in Android's SharedPreferences. This ensures that user sessions
 * persist across app launches.
 *
 * ## Usage:
 * - Call [saveAuthToken] to store an authentication token.
 * - Call [getAuthToken] to retrieve the stored authentication token.
 * - Call [clearAuthToken] to remove the stored authentication token.
 *
 * **Important:** This class should be initialized with an application-level `Context`
 * to ensure that SharedPreferences persists across activities.
 *
 * @param context The application context used to access SharedPreferences.
 */
class SessionManager(context: Context) {

    // SharedPreferences instance for storing authentication tokens
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN" // Key for storing the authentication token
    }

    /**
     * Saves an authentication token in SharedPreferences.
     *
     * This method securely stores the authentication token, allowing the user session
     * to persist even after the app is closed and reopened.
     *
     * @param token The authentication token to be stored.
     */
    fun saveAuthToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN, token).apply()
    }

    /**
     * Retrieves the stored authentication token from SharedPreferences.
     *
     * If no authentication token is found, this method returns `null`.
     *
     * @return The stored authentication token, or `null` if no token is found.
     */
    fun getAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null) // Returns null if no token is stored
    }

    /**
     * Clears the stored authentication token from SharedPreferences.
     *
     * This method removes the stored token, effectively logging the user out.
     */
    fun clearAuthToken() {
        prefs.edit().remove(AUTH_TOKEN).apply()
    }
}
