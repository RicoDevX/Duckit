package com.chrisrich.duckit.ui.screens.postgallery

import com.chrisrich.duckit.domain.model.Post

data class PostGalleryViewState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val showLoginPrompt: Boolean = false,
    val loginReason: Int = com.chrisrich.duckit.R.string.you_need_to_log_in_to_post_a_duck,
    val showPostDialog: Boolean = false,
    val selectedPostId: String? = null
)
