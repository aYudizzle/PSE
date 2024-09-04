package dev.ayupi.penguinstorageexplorer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.ayupi.penguinstorageexplorer.app.PSEAppState

@Composable
fun PSENavHost(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    startDestination: String = LIST_ROUTE,
    appState: PSEAppState
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = LIST_ROUTE,
        modifier = modifier
    ) {
        listScreen(
            navigateToDetailsScreen = { id -> navController.navigateToDetails(id = id, navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()) },
            onShowSnackbar = onShowSnackbar
        )
        detailsScreen(
            navigateToListView = { navController.navigateToListScreen(navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()) },
            onShowSnackbar = onShowSnackbar
        )
    }
}

