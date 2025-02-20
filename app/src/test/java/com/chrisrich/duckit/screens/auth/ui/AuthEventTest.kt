package com.chrisrich.duckit.screens.auth.ui

import com.chrisrich.duckit.ui.screens.auth.AuthEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Unit tests for the [AuthEvent] sealed class.
 *
 * These tests ensure that event instances are correctly created and behave as expected.
 */
class AuthEventTest {

    @Test
    fun `UpdateEmail event holds correct email`() {
        val event = AuthEvent.UpdateEmail("test@example.com")

        assertEquals("test@example.com", event.email)
    }

    @Test
    fun `UpdatePassword event holds correct password`() {
        val event = AuthEvent.UpdatePassword("securePass123")

        assertEquals("securePass123", event.password)
    }

    @Test
    fun `ToggleAuthMode event is singleton`() {
        val event1 = AuthEvent.ToggleAuthMode
        val event2 = AuthEvent.ToggleAuthMode

        assertSame(event1, event2)
    }

    @Test
    fun `LogIn event is singleton`() {
        val event1 = AuthEvent.LogIn
        val event2 = AuthEvent.LogIn

        assertSame(event1, event2)
    }

    @Test
    fun `SignUp event is singleton`() {
        val event1 = AuthEvent.SignUp
        val event2 = AuthEvent.SignUp

        assertSame(event1, event2)
    }

    @Test
    fun `EmailLostFocus event is singleton`() {
        val event1 = AuthEvent.EmailLostFocus
        val event2 = AuthEvent.EmailLostFocus

        assertSame(event1, event2)
    }

    @Test
    fun `NavigateBack event is singleton`() {
        val event1 = AuthEvent.NavigateBack
        val event2 = AuthEvent.NavigateBack

        assertSame(event1, event2)
    }

    @Test
    fun `UpdateEmail with same values are equal`() {
        val event1 = AuthEvent.UpdateEmail("test@example.com")
        val event2 = AuthEvent.UpdateEmail("test@example.com")

        assertEquals(event1, event2)
    }

    @Test
    fun `UpdatePassword with same values are equal`() {
        val event1 = AuthEvent.UpdatePassword("securePass123")
        val event2 = AuthEvent.UpdatePassword("securePass123")

        assertEquals(event1, event2)
    }

    @Test
    fun `UpdateEmail with different values are not equal`() {
        val event1 = AuthEvent.UpdateEmail("test1@example.com")
        val event2 = AuthEvent.UpdateEmail("test2@example.com")

        assertNotEquals(event1, event2)
    }

    @Test
    fun `UpdatePassword with different values are not equal`() {
        val event1 = AuthEvent.UpdatePassword("password1")
        val event2 = AuthEvent.UpdatePassword("password2")

        assertNotEquals(event1, event2)
    }
}
