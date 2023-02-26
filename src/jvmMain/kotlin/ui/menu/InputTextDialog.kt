package ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputTextDialog(onClose: () -> Unit, playBtnClicked: (enteredText: String) -> Unit) {
    Dialog(
        title = "Enter text",
        onCloseRequest = { onClose.invoke() },
        onPreviewKeyEvent = {
            if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                onClose.invoke()
                true
            } else {
                false
            }
        }
    ) {
        var text by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { playBtnClicked.invoke(text) },
            ) {
                Text("Play!")
            }
        }


    }
}