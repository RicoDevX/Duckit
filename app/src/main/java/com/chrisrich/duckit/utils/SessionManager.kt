package com.chrisrich.duckit.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null) // Returns null if no token is stored
    }

    fun clearAuthToken() {
        prefs.edit().remove(AUTH_TOKEN).apply()
    }
}
