
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrisrich.duckit.ui.screens.AppTopBar
import com.chrisrich.duckit.ui.screens.newpost.NewPostViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewPostScreen(
    onNavigateBack: () -> Unit,
) {
    val viewModel: NewPostViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(showBackButton = true, onBackClick = onNavigateBack) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Create a New Post", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = uiState.headline,
                onValueChange = { viewModel.updateHeadline(it) },
                label = { Text("Headline") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.imageUrl,
                onValueChange = { viewModel.updateImageUrl(it) },
                label = { Text("Image URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isSubmitting) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        viewModel.submitPost()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.headline.isNotBlank() && uiState.imageUrl.isNotBlank()
                ) {
                    Text("Post Duck")
                }
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage!!,
                    color = Color.Red
                )
            }
        }
    }
}
