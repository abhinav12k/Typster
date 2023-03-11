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
import androidx.compose.ui.unit.dp
import game.BlastingBoxData
import utils.xOffset
import utils.yOffset


@Composable
fun BlastingBox(blastingBoxData: BlastingBoxData) {
    val explosionProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        explosionProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
        blastingBoxData.isBlastShown = true
    }

    val boxColor = Color.White.copy(.86f)
    val explosionColor = Color.Red.copy(.56f)

    Box(
        modifier = Modifier
            .offset(blastingBoxData.xOffset, blastingBoxData.yOffset)
            .size(blastingBoxData.size.dp)
            .clip(CircleShape)
            .background(boxColor.copy(alpha = 1 - explosionProgress.value)),
        contentAlignment = Alignment.Center
    ) {
        val explosionSize = (blastingBoxData.size.dp * 2 * explosionProgress.value)
        Canvas(modifier = Modifier.size(explosionSize)) {
            drawCircle(
                color = explosionColor.copy(alpha = 1 - explosionProgress.value),
                radius = size.minDimension / 2,
                center = Offset(size.width / 2, size.height / 2)
            )
        }
    }
}