package com.chrisrich.duckit.ui.screens.postgallery.components.post

data class PostViewState(
    val id: String,
    val headline: String,
    val image: String,
    val author: String,
    val votes: Int,
    val isLoading: Boolean = false,
    val error: String? = null
)
