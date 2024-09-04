import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.ayupi.penguinstorageexplorer.app.App

fun main() = application {
    val icon = painterResource("pse.png")
    Window(
        onCloseRequest = ::exitApplication,
        title = "PenguinStorageExplorer",
        icon = icon,
    ) {
        App()
    }
}
