import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.jerry.shapes.MainContent
import com.jerry.shapes.inject.appModule
import com.jerry.shapes.inject.cacheModule
import com.jerry.shapes.ui.theme.BoxesTheme
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(appModule, cacheModule)
    }
    Window(onCloseRequest = ::exitApplication, title = "Boxes") {
        BoxesTheme {
            MainContent(onBackPressed = {})
        }
    }
}
