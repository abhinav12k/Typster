package game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class StarData(
    var position: Offset,
    val radius: Float,
    val color: Color
)

fun getStars(heightPx: Int, widthPx: Int): List<StarData> {

    return List(40) {
        val x = Random.nextDouble() * widthPx
        val y = Random.nextDouble() * heightPx
        val radius = Random.nextDouble(1.0, 3.0)
        val alpha = Random.nextDouble(0.5, 1.0)
        val color = Color.White.copy(alpha = alpha.toFloat())
        StarData(position = Offset(x.toFloat(), y.toFloat()), radius.toFloat(), color)
    }
}