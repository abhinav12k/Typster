package utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*

object KeyboardHelper {
    @OptIn(ExperimentalComposeUiApi::class)
    fun isGamePauseTriggered(keyEvent: KeyEvent) =
        keyEvent.key == Key.Escape && keyEvent.type == KeyEventType.KeyDown

    fun isValidKeyboardInput(keyEvent: KeyEvent): Boolean {
        return map.contains(keyEvent.key) && keyEvent.type == KeyEventType.KeyDown
    }

    fun getLetterFromKeyboardInput(keyEvent: KeyEvent): String = map.getValue(keyEvent.key)

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
        Key.Apostrophe to "'",
    )
}