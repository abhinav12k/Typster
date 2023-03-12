/**
 * @author abhinav12k
 */

package ui.gameComponents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import game.StarData

@Composable
fun StarrySky(starsData: List<StarData>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        starsData.forEach {
            drawCircle(color = it.color, radius = it.radius, center = it.position, style = Fill)
        }
    }
}