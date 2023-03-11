package ui.gameComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import game.EnemyBulletData
import utils.xOffset
import utils.yOffset

@Composable
fun EnemyBullet(enemyBulletData: EnemyBulletData, isWordFinished: @Composable () -> Unit) {
    val backgroundColor = if (enemyBulletData.isUnderAttack) Color.Red.copy(.56f) else Color.White.copy(.86f)
    val textColor = if (enemyBulletData.isUnderAttack) Color.White else Color.DarkGray
    if (enemyBulletData.isWordFinished) {
        isWordFinished.invoke()
    } else {
        Box(
            Modifier
                .offset(enemyBulletData.xOffset, enemyBulletData.yOffset)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Spacer(Modifier.size(enemyBulletData.size.dp))
            Text(
                enemyBulletData.word,
                color = textColor,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            )
        }
    }
}
