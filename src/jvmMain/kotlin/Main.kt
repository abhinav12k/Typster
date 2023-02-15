import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application


@Composable
@Preview
fun App() {

    val game = remember { Game() }
    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                game.update(it)
            }
        }
    }

    Column(modifier = Modifier
        .background(
            Color(51, 153, 255)
        )
        .fillMaxHeight()
        .onKeyEvent {
            if (game.gameState == GameState.STOPPED || game.gameState == GameState.PAUSED) {
                false
            } else if (isGamePauseTriggered(it)) {
                game.pauseGame()
                true
            } else if (isValidKeyboardInput(it)) {
                game.onKeyboardInput(getLetterFromKeyboardInput(it))
                true
            } else false
        }
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Button({
                when (game.gameState) {
                    GameState.PAUSED -> game.resumeGame()
                    GameState.RUNNING -> game.pauseGame()
                    else -> game.startGame()
                }
            }) {
                Text(
                    text = when (game.gameState) {
                        GameState.PAUSED -> "Resume"
                        GameState.RUNNING -> "Pause"
                        else -> "Play"
                    }
                )
            }
            Text(
                game.gameStatus,
                modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp),
                color = Color.White
            )
        }
        Box(
            modifier = Modifier
                .aspectRatio(1.0f)
                .background(Color(0, 0, 30))
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
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

}

fun main() = application {
    Window(
        state = WindowState(width = windowWidth, height = windowHeight),
        onCloseRequest = ::exitApplication,
        title = "Typing Game"
    ) {
        App()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun isGamePauseTriggered(keyEvent: KeyEvent) = keyEvent.key == Key.Escape && keyEvent.type == KeyEventType.KeyDown

private fun isValidKeyboardInput(keyEvent: KeyEvent): Boolean {
    return map.contains(keyEvent.key) && keyEvent.type == KeyEventType.KeyDown
}

private fun getLetterFromKeyboardInput(keyEvent: KeyEvent): String = map.getValue(keyEvent.key)

@OptIn(ExperimentalComposeUiApi::class)
private val map = mapOf(
    Key.A to "A",
    Key.B to "B",
    Key.C to "C",
    Key.D to "D",
    Key.E to "E",
    Key.F to "F",
    Key.G to "G",
    Key.H to "H",
    Key.I to "I",
    Key.J to "J",
    Key.K to "K",
    Key.L to "L",
    Key.M to "M",
    Key.N to "N",
    Key.O to "O",
    Key.P to "P",
    Key.Q to "Q",
    Key.R to "R",
    Key.S to "S",
    Key.T to "T",
    Key.U to "U",
    Key.V to "V",
    Key.W to "W",
    Key.X to "X",
    Key.Y to "Y",
    Key.Z to "Z",
    Key.Comma to ",",
    Key.Period to ".",
    Key.Grave to "`",
    Key.Slash to "/",
    Key.Semicolon to ";",
    Key.Backslash to "\\",
    Key.Apostrophe to "\"",
)