/**
 * @author abhinav12k
 */

package ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import game.Game
import game.GameState
import ui.theme.MenuOptionBackgroundColor
import ui.theme.MenuOptionBorderColor
import utils.GAME_LOST_IMAGE_PATH
import utils.GAME_TITLE_IMAGE_PATH
import utils.GAME_WON_IMAGE_PATH

@Composable
fun ColumnScope.HeroSection(game: Game, onCustomTextInputClicked: () -> Unit) {

    Image(
        painter = painterResource(
            when (game.gameState) {
                GameState.INITIALIZED -> GAME_TITLE_IMAGE_PATH
                GameState.LOST -> GAME_LOST_IMAGE_PATH
                GameState.WON -> GAME_WON_IMAGE_PATH
                else -> GAME_TITLE_IMAGE_PATH
            }
        ),
        contentDescription = "Game Status",
        modifier = Modifier
            .padding(16.dp)
            .size(400.dp)
            .aspectRatio(1f)
            .shadow(8.dp)
            .align(Alignment.CenterHorizontally)
    )
    Row(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f),
        horizontalArrangement = Arrangement.Center
    ) {
        HeroSectionMenuButton(
            optionLabel = when (game.gameState) {
                GameState.INITIALIZED -> "Play"
                GameState.PAUSED -> "Resume"
                GameState.STARTED, GameState.RESUMED -> "Pause"
                GameState.LOST, GameState.WON -> "Restart"
            },
            onClick = {
                when (game.gameState) {
                    GameState.INITIALIZED, GameState.LOST, GameState.WON -> {
                        game.startGame()
                    }
                    GameState.STARTED, GameState.RESUMED -> game.pauseGame()
                    GameState.PAUSED -> game.resumeGame()
                }
            },
            defaultBgColor = Color.White,
            defaultTextColor = MenuOptionBackgroundColor
        )

        Spacer(modifier = Modifier.padding(16.dp))

        if (game.gameState != GameState.INITIALIZED && game.gameState != GameState.LOST && game.gameState != GameState.WON) {
            HeroSectionMenuButton(
                "Restart",
                onClick = { game.startGame() }
            )
            Spacer(modifier = Modifier.padding(16.dp))
        }

        HeroSectionMenuButton(
            "Load your own text",
            onClick = { onCustomTextInputClicked.invoke() }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HeroSectionMenuButton(
    optionLabel: String,
    onClick: () -> Unit,
    defaultBgColor: Color? = null,
    defaultTextColor: Color? = null
) {
    var isOptionActive by remember { mutableStateOf(false) }
    Card(
        shape = CircleShape,
        elevation = 10.dp,
        modifier = Modifier
            .onPointerEvent(PointerEventType.Enter) { isOptionActive = true }
            .onPointerEvent(PointerEventType.Exit) { isOptionActive = false }
            .clickable { onClick.invoke() },
        backgroundColor = defaultBgColor ?: if (isOptionActive) Color.White else MenuOptionBackgroundColor,
        border = if (isOptionActive) null else BorderStroke(1.dp, MenuOptionBorderColor)
    ) {
        Box {
            Text(
                text = optionLabel,
                modifier = Modifier.padding(all = 16.dp).align(Alignment.Center),
                color = defaultTextColor
                    ?: if (isOptionActive) MenuOptionBackgroundColor else Color.White.copy(alpha = .6f),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}