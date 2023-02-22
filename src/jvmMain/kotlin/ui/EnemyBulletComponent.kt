package ui

import game.EnemyBulletData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import utils.xOffset
import utils.yOffset

@Composable
fun EnemyBullet(enemyBulletData: EnemyBulletData) {
    val bulletSize = enemyBulletData.size.dp
    val backgroundColor = if (enemyBulletData.isUnderAttack) Color.Red.copy(.56f) else Color.White.copy(.86f)
    val textColor = if(enemyBulletData.isUnderAttack) Color.White else Color.DarkGray
    Box(
        Modifier
            .offset(enemyBulletData.xOffset, enemyBulletData.yOffset)
            .clip(CircleShape)
            .background(backgroundColor)
            .wrapContentSize()
    ) {
        Box(
            Modifier
                .size(bulletSize)
                .rotate(enemyBulletData.angle.toFloat())
        )
        Text(
            enemyBulletData.word,
            color = textColor,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 4.dp)
        )
    }
}