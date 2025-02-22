package com.chrisrich.duckit.ui.screens.postgallery

sealed class PostGalleryEvent {
    data object RefreshPostList : PostGalleryEvent()
    data object FabClicked : PostGalleryEvent()
    data object LogOut : PostGalleryEvent()
    data object NavigateToSignIn : PostGalleryEvent()
    data object DismissLoginPrompt : PostGalleryEvent()
    data class RemovePost(val postId: String) : PostGalleryEvent()
    data class VotePost(val postId: String, val isUpvote: Boolean) : PostGalleryEvent()
    data class ShowPostDialog(val postId: String) : PostGalleryEvent()
    data object DismissPostDialog : PostGalleryEvent()
}
