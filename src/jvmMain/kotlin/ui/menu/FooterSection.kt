/**
 * @author abhinav12k
 */

package ui.menu

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import utils.openInBrowser
import java.net.URI

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FooterText(
    modifier: Modifier
) {

    var isOptionActive by remember { mutableStateOf(false) }

    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color.White.copy(.7f), fontWeight = FontWeight.Bold
            )
        ) {
            append("Made with ❤️ by ")
        }

        pushStringAnnotation(
            tag = "Github", annotation = "https://github.com/abhinav12k"
        )
        withStyle(
            style = SpanStyle(
                color = if (isOptionActive) Color.White else Color.White.copy(.7f), fontWeight = FontWeight.Bold
            )
        ) {
            append("Abhinav")
        }

        pop()
    }

    ClickableText(
        text = annotatedText, onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = "Github", start = offset, end = offset
            ).firstOrNull()?.let { annotation ->
                openInBrowser(URI(annotation.item))
            }
        },
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter) { isOptionActive = true }
            .onPointerEvent(PointerEventType.Exit) { isOptionActive = false }
    )
}