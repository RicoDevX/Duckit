package com.chrisrich.duckit.screens.auth.ui

import EmailValidator
import com.chrisrich.duckit.domain.model.AuthRequest
import com.chrisrich.duckit.domain.model.AuthResponse
import com.chrisrich.duckit.domain.usecase.auth.LogInUseCase
import com.chrisrich.duckit.domain.usecase.auth.SignUpUseCase
import com.chrisrich.duckit.navigation.NavDestination
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.ui.screens.auth.AuthEvent
import com.chrisrich.duckit.ui.screens.auth.AuthViewModel
import com.chrisrich.duckit.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

/**
 * Unit tests for [AuthViewModel].
 *
 * This class tests all authentication events, including email validation,
 * login, sign-up, and navigation interactions.
 */
@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private lateinit var logInUseCase: LogInUseCase
    private lateinit var signUpUseCase: SignUpUseCase
    private lateinit var sessionManager: SessionManager
    private lateinit var navigationManager: NavigationManager

    private lateinit var viewModel: AuthViewModel

    // Standard Test Dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        logInUseCase = mock()
        signUpUseCase = mock()
        sessionManager = mock()
        navigationManager = mock()

        viewModel = AuthViewModel(
            logInUseCase = logInUseCase,
            signUpUseCase = signUpUseCase,
            sessionManager = sessionManager,
            navigationManager = navigationManager,
            emailValidator = object : EmailValidator {
                override fun isValid(email: String): Boolean = email == "test@example.com"
            }
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Cleanup main dispatcher after tests
    }

    @Test
    fun `UpdateEmail event updates email and sets focus`() = runTest(testDispatcher) {
        // Given an email input
        val email = "test@example.com"

        // When UpdateEmail is triggered
        viewModel.onEvent(AuthEvent.UpdateEmail(email))

        // Then ViewModel state should be updated
        val state = viewModel.state.first()
        assertEquals(email, state.email)
        assertTrue(state.isEmailFocused)
    }

    @Test
    fun `UpdatePassword event updates password`() = runTest(testDispatcher) {
        // Given a password input
        val password = "password123"

        // When UpdatePassword is triggered
        viewModel.onEvent(AuthEvent.UpdatePassword(password))

        // Then ViewModel state should be updated
        val state = viewModel.state.first()
        assertEquals(password, state.password)
    }

    @Test
    fun `ToggleAuthMode event switches between login and sign-up`() = runTest(testDispatcher) {
        // Initially, isSignUp should be false
        assertFalse(viewModel.state.first().isSignUp)

        // When ToggleAuthMode is triggered
        viewModel.onEvent(AuthEvent.ToggleAuthMode)

        // Then it should be true
        assertTrue(viewModel.state.first().isSignUp)

        // When ToggleAuthMode is triggered again
        viewModel.onEvent(AuthEvent.ToggleAuthMode)

        // Then it should be false again
        assertFalse(viewModel.state.first().isSignUp)
    }

    @Test
    fun `EmailLostFocus event validates email`() = runTest(testDispatcher) {
        // Given an invalid email input
        val invalidEmail = "invalid-email"
        viewModel.onEvent(AuthEvent.UpdateEmail(invalidEmail))

        // When EmailLostFocus event is triggered
        viewModel.onEvent(AuthEvent.EmailLostFocus)

        // Then state should show an error
        val state = viewModel.state.first()
        assertTrue(state.isEmailError)
        assertTrue(state.showEmailError)
    }

    @Test
    fun `LogIn event triggers authentication and navigation`() = runTest(testDispatcher) {
        // Given valid credentials
        val email = "test@example.com"
        val password = "password123"
        viewModel.onEvent(AuthEvent.UpdateEmail(email))
        viewModel.onEvent(AuthEvent.UpdatePassword(password))

        val mockResponse = AuthResponse(token = "fake_token")
        whenever(logInUseCase.invoke(any())).thenReturn(flow { emit(Result.success(mockResponse)) })

        // When LogIn event is triggered
        viewModel.onEvent(AuthEvent.LogIn)
        advanceUntilIdle() // Ensures coroutine execution completes

        // Then ViewModel should call LogInUseCase and store the token
        verify(logInUseCase).invoke(AuthRequest(email, password))
        verify(sessionManager).saveAuthToken(mockResponse.token)
        verify(navigationManager).navigate(NavDestination.PostListScreen)
    }

    @Test
    fun `SignUp event triggers authentication and navigation`() = runTest(testDispatcher) {
        // Given valid credentials
        val email = "test@example.com"
        val password = "password123"
        viewModel.onEvent(AuthEvent.UpdateEmail(email))
        viewModel.onEvent(AuthEvent.UpdatePassword(password))

        val mockResponse = AuthResponse(token = "fake_token")
        whenever(signUpUseCase.invoke(any())).thenReturn(flow { emit(Result.success(mockResponse)) })

        // When SignUp event is triggered
        viewModel.onEvent(AuthEvent.SignUp)
        advanceUntilIdle() // Ensures coroutine execution completes

        // Then ViewModel should call SignUpUseCase and store the token
        verify(signUpUseCase).invoke(AuthRequest(email, password))
        verify(sessionManager).saveAuthToken(mockResponse.token)
        verify(navigationManager).navigate(NavDestination.PostListScreen)
    }

    @Test
    fun `LogIn event handles authentication failure`() = runTest(testDispatcher) {
        // Given invalid credentials
        val email = "wrong@example.com"
        val password = "wrongpass"
        viewModel.onEvent(AuthEvent.UpdateEmail(email))
        viewModel.onEvent(AuthEvent.UpdatePassword(password))

        val errorMessage = "Invalid credentials"
        whenever(logInUseCase.invoke(any())).thenReturn(flow {
            emit(
                Result.failure(
                    Exception(
                        errorMessage
                    )
                )
            )
        })

        // When LogIn event is triggered
        viewModel.onEvent(AuthEvent.LogIn)
        advanceUntilIdle() // Ensures coroutine execution completes

        // Then ViewModel should update state with the error
        val state = viewModel.state.first()
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `NavigateBack event triggers navigation`() {
        // When NavigateBack is triggered
        viewModel.onEvent(AuthEvent.NavigateBack)

        // Then navigation should be called
        verify(navigationManager).navigate(NavDestination.PostListScreen)
    }
}
