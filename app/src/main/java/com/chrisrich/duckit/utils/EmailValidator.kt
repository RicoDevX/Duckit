package com.chrisrich.duckit.utils

interface EmailValidator {
    fun isValid(email: String): Boolean
}

object DefaultEmailValidator : EmailValidator {
    override fun isValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
