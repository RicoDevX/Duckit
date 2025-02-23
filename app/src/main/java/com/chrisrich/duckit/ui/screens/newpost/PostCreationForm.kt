import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.chrisrich.duckit.R
import com.chrisrich.duckit.ui.screens.newpost.NewPostEvent
import com.chrisrich.duckit.ui.screens.newpost.NewPostViewModel
import com.chrisrich.duckit.ui.screens.newpost.NewPostViewState

@Composable
fun PostCreationForm(
    uiState: NewPostViewState,
    viewModel: NewPostViewModel,
    snackbarHostState: SnackbarHostState
) {
    var imageLoadSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create a New Post", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.headline,
            onValueChange = { viewModel.onEvent(NewPostEvent.UpdateHeadline(it)) },
            label = { Text("Headline") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    onState = { state ->
                        val isSuccess = state is AsyncImagePainter.State.Success
                        if (imageLoadSuccess != isSuccess
                        ) {
                            imageLoadSuccess = isSuccess
                            viewModel.onEvent(NewPostEvent.UpdateImageLoadState(isSuccess))
                        }
                    }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.img_duck_placeholder),
                    contentDescription = "Placeholder Image",
                    modifier = Modifier.fillMaxSize().align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.imageUrl,
            onValueChange = { viewModel.onEvent(NewPostEvent.UpdateImageUrl(it)) },
            label = { Text("Image URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.imageUrlError != null) {
            Text(
                text = uiState.imageUrlError,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isSubmitting) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    viewModel.onEvent(NewPostEvent.SubmitPost)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.headline.isNotBlank() && imageLoadSuccess
            ) {
                Text("Submit Post")
            }
        }
    }
}