package ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import game.Game
import kotlinx.coroutines.CoroutineScope
import ui.theme.MenuOptionBackgroundColor
import ui.theme.MenuOptionBorderColor
import java.util.*

@Composable
fun BoxScope.GameMenu(game: Game, coroutineScope: CoroutineScope) {
    var isDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        NavSection(game.isBackgroundMusicEnabledOnStart, coroutineScope)
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