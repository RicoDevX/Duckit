package com.chrisrich.duckit.ui.screens.postgallery

import com.chrisrich.duckit.ui.screens.postgallery.components.post.PostViewState

data class PostGalleryViewState(
    val isLoading: Boolean = false,
    val posts: List<PostViewState> = emptyList(),
    val error: String? = null
)
