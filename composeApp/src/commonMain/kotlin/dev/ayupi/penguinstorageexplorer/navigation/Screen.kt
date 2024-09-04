package dev.ayupi.penguinstorageexplorer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.ayupi.penguinstorageexplorer.presentation.detailsview.DetailsViewScreen
import dev.ayupi.penguinstorageexplorer.presentation.listview.ListViewScreen

internal const val LIST_ROUTE = "list_view"
internal const val DETAILS_ROUTE = "details_view"
internal const val idArg = "id"


fun NavController.navigateToDetails(id: Int, navOptions: NavOptions? = null) {
    this.navigate("$DETAILS_ROUTE/$id", navOptions)
}

fun NavController.navigateToListScreen(navOptions: NavOptions? = null) {
    this.navigate(LIST_ROUTE, navOptions)
}

// Detail Screen
fun NavGraphBuilder.detailsScreen(
    navigateToListView: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable(
        route = "$DETAILS_ROUTE/{$idArg}",
        arguments = listOf(
            navArgument(idArg) { type = NavType.IntType }
        ),
    ) {
        navBackStackEntry -> val arguments = requireNotNull(navBackStackEntry.arguments)
        val id = arguments.getInt(idArg)
        DetailsViewScreen(
            id = id,
            navigateToListView = navigateToListView,
            onShowSnackbar = onShowSnackbar
        )
    }
}

// List Screen
fun NavGraphBuilder.listScreen(
    navigateToDetailsScreen: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(
        route = LIST_ROUTE,
    ) {
        ListViewScreen(
            navigateToDetails = navigateToDetailsScreen,
            onShowSnackbar = onShowSnackbar
        )
    }
}