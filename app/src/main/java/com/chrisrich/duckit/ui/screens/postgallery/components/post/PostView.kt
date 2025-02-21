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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.chrisrich.duckit.R
import com.chrisrich.duckit.ui.screens.postgallery.components.post.PostViewState

@Composable
fun PostItem(
    post: PostViewState,
    viewModel: PostGalleryViewModel,
    onImageLoadFailure: (String) -> Unit
) {
    val showDialog by viewModel.showPostDialog.collectAsState()
    val selectedPost by viewModel.selectedPost.collectAsState()

    var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
    var imageAspectRatio by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .clickable { viewModel.showPostDialog(post) }
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
                        onImageLoadFailure(post.id)
                    }
                )
                .build(),
            contentDescription = stringResource(R.string.duck_image),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(imageAspectRatio),
            onState = { state -> imageState = state }
        )
    }

    if (showDialog && selectedPost == post) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPostDialog() },
            confirmButton = {},
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = post.headline,
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.by, post.author),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.dismissPostDialog() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.close),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.dismissPostDialog()
                                viewModel.onVoteClicked(post.id, true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = stringResource(R.string.upvote),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "${post.votes}",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        IconButton(
                            onClick = {
                                viewModel.dismissPostDialog()
                                viewModel.onVoteClicked(post.id, false)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.down_vote),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        )
    }
}
