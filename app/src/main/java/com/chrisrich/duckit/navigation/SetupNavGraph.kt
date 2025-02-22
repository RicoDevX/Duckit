
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chrisrich.duckit.navigation.NavDestination
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.ui.screens.postgallery.PostGalleryScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun SetupNavGraph(navController: NavHostController) {
    val navigationManager: NavigationManager = koinViewModel()
    val currentDestination by navigationManager.currentDestination.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NavDestination.PostGalleryScreen.route
    ) {
        composable(NavDestination.PostGalleryScreen.route) {
            PostGalleryScreen()
        }

        composable(NavDestination.AuthScreen.route) {
            AuthScreen()
        }

        composable(NavDestination.NewPostScreen.route) {
            NewPostScreen()
        }
    }

    LaunchedEffect(currentDestination) {
        navController.navigate(currentDestination.route) {
            popUpTo(NavDestination.PostGalleryScreen.route) { inclusive = false }
        }
    }
}
