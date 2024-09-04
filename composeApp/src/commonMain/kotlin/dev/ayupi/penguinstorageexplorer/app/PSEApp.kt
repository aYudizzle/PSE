package dev.ayupi.penguinstorageexplorer.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.ayupi.penguinstorageexplorer.navigation.PSENavHost

@Composable
fun PSEApp(
    appState: PSEAppState
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState)}
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = padding.calculateTopPadding())
        ) {
            PSENavHost(
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = SnackbarDuration.Short
                    ) == SnackbarResult.ActionPerformed
                },
                appState = appState,
                modifier = Modifier.padding(padding)
            )
        }

    }
}