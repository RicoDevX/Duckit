package com.chrisrich.duckit.ui.screens.postgallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.chrisrich.duckit.R
import com.chrisrich.duckit.domain.model.Post

@Composable
fun PostItem(
    modifier: Modifier = Modifier,
    post: Post,
    onEvent: (PostGalleryEvent) -> Unit
) {
    var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
    var imageAspectRatio by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .padding(4.dp)
            .clickable {
                onEvent(PostGalleryEvent.ShowPostDialog(post.id))
            }
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
                        onEvent(PostGalleryEvent.RemovePost(post.id))//If we can't load the image remove the post
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
}
