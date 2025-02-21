package com.chrisrich.duckit.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Stack

class NavigationManager : ViewModel() {
    private val _currentDestination = MutableStateFlow<NavDestination>(NavDestination.PostGalleryScreen)
    val currentDestination: StateFlow<NavDestination> get() = _currentDestination

    private val backstack = Stack<NavDestination>()

    fun navigate(destination: NavDestination) {
        if (_currentDestination.value != destination) {
            backstack.push(_currentDestination.value)
            _currentDestination.value = destination
        }
    }

    fun navigateBack() {
        if (backstack.isNotEmpty()) {
            _currentDestination.value = backstack.pop()
        } else {
            _currentDestination.value = NavDestination.PostGalleryScreen
        }
    }
}
