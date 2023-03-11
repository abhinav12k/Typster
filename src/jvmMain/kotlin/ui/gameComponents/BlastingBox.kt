package ui.gameComponents

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun BlastingBox(xOffset: Dp, yOffset: Dp) {
    val explosionProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        explosionProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
    }

    val boxColor = Color.White.copy(.86f)
    val explosionColor = Color.Red.copy(.56f)

    val boxSize = 80.dp

    Box(
        modifier = Modifier
            .offset(xOffset, yOffset)
            .size(boxSize)
            .clip(CircleShape)
            .background(boxColor.copy(alpha = 1 - explosionProgress.value)),
        contentAlignment = Alignment.Center
    ) {
        val explosionSize = (boxSize * 2 * explosionProgress.value)
        Canvas(modifier = Modifier.size(explosionSize)) {
            drawCircle(
                color = explosionColor.copy(alpha = 1 - explosionProgress.value),
                radius = size.minDimension / 2,
                center = Offset(size.width / 2, size.height / 2)
            )
        }
    }
}