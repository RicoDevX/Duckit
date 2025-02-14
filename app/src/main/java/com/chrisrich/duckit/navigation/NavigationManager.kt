package com.chrisrich.duckit.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationManager : ViewModel() {
    private val _currentDestination = MutableStateFlow<NavDestination>(NavDestination.PostListScreen)
    val currentDestination: StateFlow<NavDestination> = _currentDestination

    fun navigate(destination: NavDestination) {
        _currentDestination.value = destination
    }
}
