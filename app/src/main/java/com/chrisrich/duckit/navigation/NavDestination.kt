package com.chrisrich.duckit.navigation

sealed class NavDestination(val route: String) {
    data object PostListScreen : NavDestination("postListScreen")
    data object AuthScreen : NavDestination("authScreen")
}
