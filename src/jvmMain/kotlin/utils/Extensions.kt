package utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import game.GameObject
import org.openrndr.math.Vector2
import kotlin.math.atan2

fun Vector2.angle(): Double {
    val radian = atan2(y = this.y, x = this.x)
    return (radian / Math.PI) * 180
}


val GameObject.xOffset: Dp get() = position.x.dp - (size.dp / 2)
val GameObject.yOffset: Dp get() = position.y.dp - (size.dp / 2)