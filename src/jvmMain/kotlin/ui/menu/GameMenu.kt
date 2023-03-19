/**
 * @author abhinav12k
 */

package ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import game.Game
import game.GameState
import utils.GAME_LOST_IMAGE_PATH
import utils.GAME_TITLE_IMAGE_PATH
import utils.GAME_WON_IMAGE_PATH

@Composable
fun GameMenu(game: Game) {
    var isDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        NavSection()
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
        if (game.gameState == GameState.WON || game.gameState == GameState.LOST) {
            GameStats(
                modifier = Modifier
                    .padding(vertical = 24.dp, horizontal = 36.dp)
                    .fillMaxWidth(),
                game.calculateGameStats()
            )
        }
        HeroSection(game) {
            isDialogOpen = true
        }
        Spacer(modifier = Modifier.fillMaxWidth().heightIn(24.dp))
        FooterText(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.fillMaxWidth().heightIn(24.dp))
    }

    if (isDialogOpen) {
        InputTextDialog(onClose = {
            isDialogOpen = false
        }) {
            game.startGame(it)
        }
    }

}