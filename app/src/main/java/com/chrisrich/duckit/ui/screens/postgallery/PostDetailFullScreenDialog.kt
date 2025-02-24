import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chrisrich.duckit.R
import com.chrisrich.duckit.domain.model.Post
import com.chrisrich.duckit.utils.AngelicUpVoteMotivator
import kotlinx.coroutines.delay

@Composable
fun PostDetailFullScreenDialog(
    post: Post,
    currentVotes: Int,
    onDismiss: () -> Unit,
    onVote: (Boolean) -> Unit,
    showVoteError: Boolean
) {
    var showBackground by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    val motivator = AngelicUpVoteMotivator()
    val message =
        remember { mutableStateOf(motivator.generateUpVoteMessage(post.headline, currentVotes)) }
    val animationDuration = 500

    LaunchedEffect(Unit) {
        showBackground = true
        delay(250)
        showContent = true
    }

    LaunchedEffect(showContent) {
        if (!showContent) {
            delay(animationDuration.toLong())
            showBackground = false
            delay(animationDuration.toLong())
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = showBackground,
        enter = fadeIn(animationSpec = tween(animationDuration)),
        exit = fadeOut(animationSpec = tween(animationDuration))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable {
                    showContent = false
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = slideInHorizontally(
                    initialOffsetX = { it * 2 },
                    animationSpec = tween(animationDuration)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it * 2 },
                    animationSpec = tween(animationDuration)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                IconButton(
                                    onClick = { showContent = false },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.Transparent, CircleShape)
                                        .border(2.dp, Color.Black, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.Black
                                    )
                                }
                            }

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(post.image)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(R.string.full_screen_duck_image),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(post.headline, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(stringResource(R.string.by, post.author), fontSize = 14.sp)
                                Text(
                                    stringResource(R.string.has_votes, currentVotes),
                                    fontSize = 14.sp
                                )

                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(modifier = Modifier.height(24.dp)) {
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = showVoteError,
                                    enter = fadeIn(animationSpec = tween(animationDuration)),
                                    exit = fadeOut(animationSpec = tween(animationDuration))
                                ) {
                                    Text(
                                        stringResource(R.string.voting_error_please_try_again),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(animationDuration)
                    ) + fadeIn(animationSpec = tween(animationDuration)),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(animationDuration)
                    ) + fadeOut(animationSpec = tween(animationDuration))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = message.value,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 16.dp)
                            )

                            Column(
                                modifier = Modifier.wrapContentSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = { onVote(true) },
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Upvote",
                                        tint = Color.Green,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .graphicsLayer(
                                                scaleX = 2f,
                                                scaleY = 2f,
                                                rotationZ = -90f
                                            )
                                    )
                                }

                                IconButton(
                                    onClick = { onVote(false) },
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Downvote",
                                        tint = Color.Red,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .graphicsLayer(
                                                scaleX = 2f,
                                                scaleY = 2f,
                                                rotationZ = 90f
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}