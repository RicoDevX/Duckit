import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chrisrich.duckit.domain.model.NewPostRequest
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun PostSuccessScreen(
    post: NewPostRequest,
    onDismiss: () -> Unit,
    onPostAnother: () -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }
    val showConfetti by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = isVisible, enter = fadeIn()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Your Duck Has Been Posted!",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(post.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Posted Duck",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(post.headline, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = onPostAnother) { Text("Post Another") }
                        Button(onClick = onDismiss) { Text("Dismiss") }
                    }
                }
            }
        }

        if (showConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        emitter = Emitter(duration = Long.MAX_VALUE, TimeUnit.MILLISECONDS)
                            .perSecond(50),
                        position = Position.Relative(0.5, 0.0)
                    )
                )
            )
        }
    }
}



