/**
 * @author abhinav12k
 */

package ui.gameComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import game.ShipData
import utils.SPACESHIP_PATH
import utils.xOffset
import utils.yOffset

@Composable
fun Ship(shipData: ShipData) {
    val shipSize = shipData.size.dp
    Box(
        Modifier.offset(shipData.xOffset, shipData.yOffset)
            .size(shipSize)
            .rotate((shipData.visualAngle-270.0).toFloat())
            .clip(CircleShape)
            .background(Color.Black)
    ) {
        val spaceship = useResource(SPACESHIP_PATH) {
            loadSvgPainter(inputStream = it, density = LocalDensity.current)
        }
        Image(painter = spaceship, contentDescription = "space ship")
    }
}