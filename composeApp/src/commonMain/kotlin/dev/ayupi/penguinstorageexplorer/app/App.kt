package dev.ayupi.penguinstorageexplorer.app

import androidx.compose.runtime.Composable
import dev.ayupi.penguinstorageexplorer.di.getCommonModule
import dev.ayupi.penguinstorageexplorer.di.platformModule
import dev.ayupi.penguinstorageexplorer.navigation.PSENavHost
import dev.ayupi.penguinstorageexplorer.ui.theme.PSETheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = {
        modules(getCommonModule(), platformModule())
    }) {
        PSETheme {
            val appState = rememberPSEAppState()
            PSEApp(appState = appState)
        }
    }
}
