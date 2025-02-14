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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrisrich.duckit.R
import com.chrisrich.duckit.navigation.NavDestination
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.ui.screens.auth.AuthViewModel
import com.chrisrich.duckit.utils.isValidEmail
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen() {
    val viewModel: AuthViewModel = koinViewModel()
    val navigationManager: NavigationManager = koinViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }

    // Determine if the button should be enabled
    val isButtonEnabled = !isEmailError && email.isNotBlank() && password.isNotBlank()

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
                    IconButton(onClick = { navigationManager.navigate(NavDestination.PostListScreen) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
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
            Text(text = stringResource(id = R.string.welcome_message), style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailError = !email.isValidEmail()
                },
                label = { Text(stringResource(id = R.string.email_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = isEmailError,
                supportingText = {
                    if (isEmailError) {
                        Text(
                            text = stringResource(id = R.string.invalid_email_error),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (isSignUp) {
                            viewModel.signUp(email, password)
                        } else {
                            viewModel.logIn(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isButtonEnabled // Disable button if criteria are not met
                ) {
                    Text(text = if (isSignUp) stringResource(id = R.string.sign_up) else stringResource(id = R.string.sign_in))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(text = if (isSignUp) stringResource(id = R.string.already_have_account) else stringResource(id = R.string.dont_have_account))
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.error_prefix) + (uiState.error ?: ""), color = Color.Red)
            }

            LaunchedEffect(uiState.authResponse) {
                if (uiState.authResponse != null) {
                    navigationManager.navigate(NavDestination.PostListScreen)
                }
            }
        }
    }
}
