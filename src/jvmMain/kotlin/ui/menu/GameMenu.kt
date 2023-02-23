package ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import game.Game
import game.GameState
import kotlinx.coroutines.launch
import ui.theme.MenuOptionBackgroundColor
import ui.theme.MenuOptionBorderColor
import utils.AudioPlayer
import kotlin.system.exitProcess

@Composable
fun BoxScope.GameMenu(game: Game) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.align(Alignment.Center),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // show resume / pause / play option only when game is played
        if (game.gameState != GameState.STOPPED) {
            MenuOption(
                optionLabel = when (game.gameState) {
                    GameState.INITIALIZED -> "Play"
                    GameState.PAUSED -> "Resume"
                    GameState.STARTED, GameState.RESUMED -> "Pause"
                    else -> "Play"
                },
                onClick = {
                    when (game.gameState) {
                        GameState.INITIALIZED -> {
                            game.startGame()
                            coroutineScope.launch {
                                AudioPlayer.play()
                            }
                        }

                        GameState.STARTED, GameState.RESUMED -> game.pauseGame()
                        GameState.PAUSED -> game.resumeGame()
                        else -> game.startGame()
                    }
                }
            )
        }

        // only show restart option when the game is at least in started state
        if (game.gameState != GameState.INITIALIZED) {
            MenuOption(
                optionLabel = "Restart",
                onClick = {
                    game.startGame()
                }
            )
        }

        MenuOption(
            optionLabel = "Stop Background Music",
            onClick = {
                AudioPlayer.stop()
            }
        )

        MenuOption(
            optionLabel = "Replay Background Music",
            onClick = {
                coroutineScope.launch {
                    AudioPlayer.replay()
                }
            }
        )

        MenuOption(
            optionLabel = "Load your text",
            onClick = {
                //todo: open up a new text window here to load text into memory

            }
        )

        MenuOption(
            optionLabel = "Exit",
            onClick = {
                exitProcess(0)
            }
        )

        /******* GAME STATUS ******/
        Spacer(
            modifier = Modifier
                .padding(vertical = 12.dp)
        )

        Text(
            game.gameStatus,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MenuOption(optionLabel: String, onClick: () -> Unit) {
    var isOptionActive by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .width(280.dp)
            .onPointerEvent(PointerEventType.Enter) { isOptionActive = true }
            .onPointerEvent(PointerEventType.Exit) { isOptionActive = false }
            .clickable { onClick.invoke() },
        backgroundColor = if (isOptionActive) Color.White else MenuOptionBackgroundColor,
        border = if (isOptionActive) null else BorderStroke(1.dp, MenuOptionBorderColor)
    ) {
        Text(
            text = optionLabel,
            modifier = Modifier.padding(all = 8.dp),
            color = if (isOptionActive) MenuOptionBackgroundColor else Color.White.copy(alpha = .6f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}