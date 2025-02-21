package com.chrisrich.duckit.ui.screens.newpost

data class NewPostViewState(
    val headline: String = "",
    val imageUrl: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)
