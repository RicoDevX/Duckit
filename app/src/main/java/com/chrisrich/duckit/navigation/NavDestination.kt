package com.chrisrich.duckit.navigation

sealed class NavDestination(val route: String) {
    data object PostGalleryScreen : NavDestination("postGalleryScreen")
    data object AuthScreen : NavDestination("authScreen")
    data object NewPostScreen : NavDestination("newPostScreen")
}
