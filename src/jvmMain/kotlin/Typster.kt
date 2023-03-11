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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import game.*
import ui.gameComponents.*
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
            .onSizeChanged {
                game.heightPx = it.height
                game.widthPx = it.width
                with(density) {
                    game.width = it.width.toDp()
                    game.height = it.height.toDp()
                }
            }
    ) {
        if (game.isGameMenuVisible) {
            GameMenu(game)
        } else {
            StarrySky(game.starsData)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clipToBounds()
            ) {
                game.gameObjects.forEach {
                    when (it) {
                        is ShipData -> Ship(it)
                        is BulletData -> Bullet(it)
                        is EnemyBulletData -> EnemyBullet(it)
                        is BlastingBoxData -> BlastingBox(it)
                    }
                }
            }
        }
    }

}

fun main() = application {

    val trayIcon = useResource(TRAY_ICON_PATH) {
        loadSvgPainter(inputStream = it, density = LocalDensity.current)
    }
    var windowVisible by remember { mutableStateOf(true) }
    val game = remember { Game() }

//    var isResizable by remember { mutableStateOf(true) }
//    isResizable = game.gameState == GameState.INITIALIZED

    if (game.isBackgroundMusicEnabledOnStart) {
        AudioManager.play()
    }

    MaterialTheme {
        val trayState = rememberTrayState()
        Tray(
            state = trayState,
            icon = trayIcon,
            onAction = { windowVisible = true },
            menu = {
                Item(
                    text = "Hide game window",
                    onClick = { windowVisible = false }
                )
                Item(
                    text = "Show game window",
                    onClick = { windowVisible = true }
                )
                Item(
                    text = "Exit",
                    onClick = {
                        AudioManager.stopBackgroundMusic()
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
                if (game.gameState == GameState.LOST || game.gameState == GameState.WON || game.gameState == GameState.PAUSED) {
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