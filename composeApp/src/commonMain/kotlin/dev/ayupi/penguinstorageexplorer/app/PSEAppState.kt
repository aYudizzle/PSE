package dev.ayupi.penguinstorageexplorer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberPSEAppState(
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope()
): PSEAppState {
    return remember(
        navController,
        scope
    ) {
        PSEAppState(
            navController,
            scope
        )
    }
}

data class PSEAppState(
    val navController: NavHostController,
    val scope: CoroutineScope,
) {
    val currentDestionation: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination
}