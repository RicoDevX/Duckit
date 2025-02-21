package com.chrisrich.duckit.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manages user authentication session using EncryptedSharedPreferences.
 *
 * The `SessionManager` class is responsible for securely storing, retrieving, and clearing
 * authentication tokens using EncryptedSharedPreferences, ensuring data is protected with encryption.
 *
 * ## Usage:
 * - Call [saveAuthToken] to securely store an authentication token.
 * - Call [getAuthToken] to retrieve the stored authentication token.
 * - Call [clearAuthToken] to remove the stored authentication token.
 *
 * **Important:** This class should be initialized with an application-level `Context`
 * to ensure that EncryptedSharedPreferences persists across activities.
 *
 * @param context The application context used to access EncryptedSharedPreferences.
 */
class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // EncryptedSharedPreferences instance for secure authentication token storage
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_user_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN" // Key for storing the authentication token
    }

    /**
     * Saves an authentication token in EncryptedSharedPreferences.
     *
     * @param token The authentication token to be securely stored.
     */
    fun saveAuthToken(token: String) {
        encryptedPrefs.edit().putString(AUTH_TOKEN, token).apply()
    }

    /**
     * Retrieves the stored authentication token from EncryptedSharedPreferences.
     *
     * @return The stored authentication token, or `null` if no token is found.
     */
    fun getAuthToken(): String? {
        return encryptedPrefs.getString(AUTH_TOKEN, null)
    }

    /**
     * Clears the stored authentication token from EncryptedSharedPreferences.
     *
     * This method removes the stored token, effectively logging the user out.
     */
    fun clearAuthToken() {
        encryptedPrefs.edit().remove(AUTH_TOKEN).apply()
    }
}
