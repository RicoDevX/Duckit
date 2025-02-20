package com.chrisrich.duckit.screens.auth.ui

import com.chrisrich.duckit.ui.screens.auth.AuthViewState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test for [AuthViewState].
 *
 * This test ensures that the default state is correctly initialized,
 * modifications to the state behave as expected, and the `shouldShowEmailError`
 * function correctly determines when an email error should be displayed.
 */
class AuthViewStateTest {

    @Test
    fun `Default state is initialized correctly`() {
        val state = AuthViewState()

        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoading)
        assertNull(state.authResponse)
        assertNull(state.error)
        assertFalse(state.isSignUp)
        assertFalse(state.isEmailError)
        assertFalse(state.showEmailError)
        assertFalse(state.isEmailFocused)
    }

    @Test
    fun `Modifying email and password updates state`() {
        val state = AuthViewState(email = "test@example.com", password = "securePass123")

        assertEquals("test@example.com", state.email)
        assertEquals("securePass123", state.password)
    }

    @Test
    fun `shouldShowEmailError returns true when conditions are met`() {
        val state = AuthViewState(
            isEmailError = true,
            isEmailFocused = false,
            showEmailError = true
        )

        assertTrue(state.shouldShowEmailError())
    }

    @Test
    fun `shouldShowEmailError returns false when email is valid`() {
        val state = AuthViewState(
            isEmailError = false,
            isEmailFocused = false,
            showEmailError = true
        )

        assertFalse(state.shouldShowEmailError())
    }

    @Test
    fun `shouldShowEmailError returns false when email is focused`() {
        val state = AuthViewState(
            isEmailError = true,
            isEmailFocused = true,
            showEmailError = true
        )

        assertFalse(state.shouldShowEmailError())
    }

    @Test
    fun `shouldShowEmailError returns false when showEmailError is false`() {
        val state = AuthViewState(
            isEmailError = true,
            isEmailFocused = false,
            showEmailError = false
        )

        assertFalse(state.shouldShowEmailError())
    }
}
