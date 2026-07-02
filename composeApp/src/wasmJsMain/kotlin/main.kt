import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.jerry.shapes.MainContent
import com.jerry.shapes.inject.appModule
import com.jerry.shapes.inject.cacheModule
import com.jerry.shapes.ui.theme.BoxesTheme
import kotlinx.browser.document
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(appModule, cacheModule)
    }
    ComposeViewport(document.body!!) {
        BoxesTheme {
            MainContent(onBackPressed = {})
        }
    }
}
