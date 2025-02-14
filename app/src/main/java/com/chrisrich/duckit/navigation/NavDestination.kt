package com.chrisrich.duckit.navigation

sealed class NavDestination(val route: String) {
    data object PostListScreen : NavDestination("postListScreen")
    data object AuthScreen : NavDestination("authScreen")

//    // Example if you need a dynamic route with parameters
//    object PostDetailScreen : NavDestination("postDetailScreen/{postId}") {
//        fun createRoute(postId: String) = "postDetailScreen/$postId"
//
//        val arguments = listOf(navArgument("postId") { type = NavType.StringType })
//    }
}
