import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import game.*
import kotlinx.coroutines.launch
import ui.gameComponents.Bullet
import ui.gameComponents.EnemyBullet
import ui.gameComponents.Ship
import ui.menu.GameMenu
import ui.theme.GameBackgroundColor
import utils.*

@Composable
@Preview
fun App(game: Game) {

    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                game.update(it)
            }
        }
    }

    Box(
        modifier = Modifier
            .background(GameBackgroundColor)
            .fillMaxHeight()
    ) {
        if (game.isGameMenuVisible) {
            GameMenu(game)
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clipToBounds()
            .onSizeChanged {
                with(density) {
                    game.width = it.width.toDp()
                    game.height = it.height.toDp()
                }
            }) {
            game.gameObjects.forEach {
                when (it) {
                    is ShipData -> Ship(it)
                    is BulletData -> Bullet(it)
                    is EnemyBulletData -> EnemyBullet(it)
                }
            }
        }
    }

}

fun main() = application {

    val appIcon = useResource("drawable/typing.svg") {
        loadSvgPainter(inputStream = it, density = LocalDensity.current)
    }
    var windowVisible by remember { mutableStateOf(true) }
    val game = remember { Game() }
    val coroutineScope = rememberCoroutineScope()

//    var isResizable by remember { mutableStateOf(true) }
//    isResizable = game.gameState == GameState.INITIALIZED

    MaterialTheme {
        val trayState = rememberTrayState()
        Tray(
            state = trayState,
            icon = appIcon,
            onAction = { windowVisible = true },
            menu = {
                Item(
                    text = "Stop music",
                    onClick = { AudioPlayer.stop() }
                )
                Item(
                    text = "Replay music",
                    onClick = {
                        coroutineScope.launch {
                            AudioPlayer.play()
                        }
                    }
                )
                Item(
                    text = "Hide game",
                    onClick = { windowVisible = false }
                )
                Item(
                    text = "Show game",
                    onClick = { windowVisible = true }
                )
                Item(
                    text = "Close",
                    onClick = {
                        AudioPlayer.stop()
                        exitApplication()
                    }
                )
            }
        )

        val windowState = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(windowWidth, windowHeight)
        )

        Window(
            state = windowState,
            onCloseRequest = {
                windowVisible = false
            },
            title = APP_NAME,
            visible = windowVisible,
            onKeyEvent = {
                if (game.gameState == GameState.STOPPED || game.gameState == GameState.PAUSED) {
                    false
                } else if (game.gameState != GameState.INITIALIZED && KeyboardHelper.isGamePauseTriggered(it)) {
                    game.pauseGame()
                    true
                } else if (KeyboardHelper.isValidKeyboardInput(it)) {
                    game.onKeyboardInput(KeyboardHelper.getLetterFromKeyboardInput(it))
                    true
                } else false
            },
            resizable = true //todo: find fix for disabling full screen mode
        ) {
            App(game)
        }
    }
}