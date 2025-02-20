package com.chrisrich.duckit.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrisrich.duckit.R
import org.koin.androidx.compose.koinViewModel

/**
 * Authentication screen for user login and sign-up.
 *
 * This Composable function displays the authentication UI, handling both login and sign-up flows.
 * It interacts with `AuthViewModel` to manage state updates and user events.
 *
 * ## Features:
 * - Displays input fields for email and password.
 * - Handles authentication mode toggling between sign-up and login.
 * - Provides validation for email input (error displayed on loss of focus).
 * - Displays a loading indicator while authentication is in progress.
 * - Shows error messages if authentication fails.
 * - Allows navigation back to the previous screen.
 *
 * ## State Management:
 * - Observes `AuthViewModel.state` using `collectAsStateWithLifecycle()`.
 * - Updates UI based on `AuthViewState`, including error handling and loading states.
 *
 * ## UI Components:
 * - **TopAppBar**: Displays the app logo and a back navigation button.
 * - **OutlinedTextField (Email & Password)**: Allows users to enter credentials.
 * - **Button (Sign In / Sign Up)**: Triggers authentication based on the current mode.
 * - **TextButton (Toggle Mode)**: Switches between sign-up and login.
 * - **Error Handling**: Shows validation errors for email and failed authentication attempts.
 *
 * @see AuthViewModel
 * @see AuthEvent
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen() {
    val viewModel: AuthViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_duckit_logo),
                        contentDescription = stringResource(id = R.string.duckit_logo_content_description),
                        modifier = Modifier.size(200.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(AuthEvent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF45C066),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.welcome_message),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEvent(AuthEvent.UpdateEmail(it)) },
                label = { Text(stringResource(id = R.string.email_label)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next
                ),
                singleLine = true,
                isError = uiState.shouldShowEmailError(),
                supportingText = {
                    if (uiState.shouldShowEmailError()) {
                        Text(
                            text = stringResource(id = R.string.invalid_email_error),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) viewModel.onEvent(AuthEvent.EmailLostFocus)
                    }
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onEvent(AuthEvent.UpdatePassword(it)) },
                label = { Text(stringResource(id = R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        viewModel.onEvent(if (uiState.isSignUp) AuthEvent.SignUp else AuthEvent.LogIn)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isEmailError && uiState.email.isNotBlank() && uiState.password.isNotBlank()
                ) {
                    Text(
                        text = if (uiState.isSignUp) stringResource(id = R.string.sign_up) else stringResource(
                            id = R.string.sign_in
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { viewModel.onEvent(AuthEvent.ToggleAuthMode) }) {
                Text(
                    text = if (uiState.isSignUp) stringResource(id = R.string.already_have_account) else stringResource(
                        id = R.string.dont_have_account
                    )
                )
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.error_prefix) + (uiState.error ?: ""),
                    color = Color.Red
                )
            }
        }
    }
}