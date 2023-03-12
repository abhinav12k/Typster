/**
 * @author abhinav12k
 */

package ui.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import game.Game

@Composable
fun GameMenu(game: Game) {
    var isDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        NavSection()
        HeroSection(game) {
            isDialogOpen = true
        }
        FooterSection()
    }

    if (isDialogOpen) {
        InputTextDialog(onClose = {
            isDialogOpen = false
        }) {
            game.startGame(it)
        }
    }

}