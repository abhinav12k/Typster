/**
 * @author abhinav12k
 */

package ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ui.theme.IconColor
import utils.AudioManager
import utils.EXIT_ICON_PATH
import utils.MUTE_ICON_PATH
import utils.UNMUTE_ICON_PATH
import kotlin.system.exitProcess

@Composable
fun NavSection() {

    var isMusicPlaying by remember { mutableStateOf(AudioManager.isDefaultBackgroundMusicPlaying()) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.End
    ) {

        Icon(
            painter = painterResource(if (isMusicPlaying) UNMUTE_ICON_PATH else MUTE_ICON_PATH),
            contentDescription = if (isMusicPlaying) "Music Un muted" else "Music Muted",
            tint = IconColor,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    if (isMusicPlaying) {
                        isMusicPlaying = false
                        AudioManager.stopBackgroundMusic()
                    } else {
                        isMusicPlaying = true
                        AudioManager.play()
                    }
                }
        )
        Spacer(Modifier.padding(horizontal = 8.dp))
        Icon(painter = painterResource(EXIT_ICON_PATH),
            contentDescription = "Exit",
            tint = IconColor,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    exitProcess(0)
                }
        )
    }
}