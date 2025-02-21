import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.chrisrich.duckit.R
import com.chrisrich.duckit.domain.model.Post
import com.chrisrich.duckit.ui.screens.postlist.PostListViewModel
import com.chrisrich.duckit.utils.SessionManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun PostItem(post: Post, onShowLoginPrompt: () -> Unit, onImageLoadFailure: (String) -> Unit) {
    val sessionManager: SessionManager = koinInject()
    val viewModel: PostListViewModel = koinInject()
    val coroutineScope = rememberCoroutineScope()

    var localUpvotes by remember { mutableIntStateOf(post.upvotes) }
    var showDialog by remember { mutableStateOf(false) }
    var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
    var imageAspectRatio by remember { mutableFloatStateOf(1f) } // Default aspect ratio

    // Thumbnail for Gallery
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clickable { showDialog = true } // Opens dialog on click
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(post.image)
                .crossfade(true)
                .listener(
                    onSuccess = { _, result ->
                        val width = result.drawable.intrinsicWidth
                        val height = result.drawable.intrinsicHeight
                        if (width > 0 && height > 0) {
                            imageAspectRatio = width.toFloat() / height.toFloat()
                        }
                    },
                    onError = { _, _ ->
                        onImageLoadFailure(post.id) // Notify ViewModel on failure
                    }
                )
                .build(),
            contentDescription = stringResource(R.string.duck_image),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(imageAspectRatio), // Uses original aspect ratio
            onState = { state -> imageState = state }
        )

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = { },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title Bar with Close Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = post.headline, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                Text(text = stringResource(R.string.by, post.author), fontSize = 14.sp, color = Color.Gray)
                            }
                            IconButton(onClick = { showDialog = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close),
                                    tint = Color.Black
                                )
                            }
                        }

                        // Enlarged Image
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(post.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(R.string.full_screen_duck_image),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(imageAspectRatio)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Upvote/Downvote Buttons with Vote Count in the Middle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Upvote Button
                            IconButton(
                                onClick = {
                                    val authToken = sessionManager.getAuthToken()
                                    if (authToken != null) {
                                        localUpvotes += 1 // Optimistically update UI
                                        coroutineScope.launch {
                                            viewModel.upvotePost(post.id) { updatedVotes ->
                                                localUpvotes = updatedVotes
                                            }
                                        }
                                    } else {
                                        onShowLoginPrompt()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = stringResource(R.string.upvote),
                                    tint = Color.Black
                                )
                            }

                            // Vote Count Display (Centered)
                            Text(
                                text = "$localUpvotes",
                                fontSize = 18.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 16.dp) // Ensures proper spacing
                            )

                            // Downvote Button
                            IconButton(
                                onClick = {
                                    val authToken = sessionManager.getAuthToken()
                                    if (authToken != null) {
                                        localUpvotes -= 1 // Optimistically update UI
                                        coroutineScope.launch {
                                            viewModel.downvotePost(post.id) { updatedVotes ->
                                                localUpvotes = updatedVotes
                                            }
                                        }
                                    } else {
                                        onShowLoginPrompt()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = stringResource(R.string.down_vote),
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}
