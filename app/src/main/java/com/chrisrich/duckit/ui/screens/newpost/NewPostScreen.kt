import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrisrich.duckit.ui.screens.AppTopBar
import com.chrisrich.duckit.ui.screens.newpost.NewPostEvent
import com.chrisrich.duckit.ui.screens.newpost.NewPostViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewPostScreen(
    viewModel: NewPostViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            AppTopBar(
                showBackButton = true,
                onBackClick = { viewModel.onEvent(NewPostEvent.NavigateBack) })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.postSuccess && (uiState.postedPost != null)) {
                PostSuccessScreen(
                    post = uiState.postedPost!!,
                    onDismiss = { viewModel.onEvent(NewPostEvent.NavigateBack) },
                    onPostAnother = { viewModel.onEvent(NewPostEvent.ResetForm) }
                )
            } else {
                PostCreationForm(uiState, viewModel, snackbarHostState)
            }
        }
    }
}