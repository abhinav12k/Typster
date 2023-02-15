import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.openrndr.math.Vector2
import java.util.UUID
import kotlin.math.atan2
import kotlin.random.Random

enum class GameState {
    STOPPED, RUNNING, PAUSED
}

fun Vector2.angle(): Double {
    val radian = atan2(y = this.y, x = this.x)
    return (radian / Math.PI) * 180
}


val windowWidth = 700.dp
val windowHeight = 900.dp

class Game {
    private var prevTime = 0L   // storing the last time game was updated
    var userShip = UserShipData()

    var targetLocation by mutableStateOf(DpOffset.Zero)

    var gameObjects = mutableStateListOf<GameObject>()
    var gameState by mutableStateOf(GameState.RUNNING)
    var gameStatus by mutableStateOf("Let's begin!")

    private var wordsPerRound = 3
    private val wordsList = textArray
    private var currentTargetWord: String? = null
    private var currentTargetEnemyObject: GameObject? = null
    private var idxUnderValidation = -1

    private var currentWordIdx = 0

    private val currentWord: String?
        get() {
            if (currentWordIdx == wordsList.size) return null
            val tmp = wordsList[currentWordIdx]
            currentWordIdx = currentWordIdx.inc()
            return tmp
        }

    fun startGame() {
        gameObjects.clear()
        currentWordIdx = 0

        idxUnderValidation = 0
        currentTargetEnemyObject = null
        currentTargetWord = null

        userShip.position = Vector2(width.value / 2.0, height.value - 40.0)
        userShip.movementVector = Vector2.ZERO
        gameObjects.add(userShip)

        repeat(wordsPerRound) {
            gameObjects.add(EnemyBulletData().apply {
                position = Vector2(Random.nextDouble() * width.value, Random.nextDouble() * 80.0)
                angle = (userShip.position - position).angle()
                speed = 2.0
                word = currentWord as String
            })
        }

        gameState = GameState.RUNNING
        gameStatus = "Good luck!"
    }

    fun update(time: Long) {
        val delta = time - prevTime
        val floatDelta = (delta / 1e8).toFloat() //because time is in nano seconds
        prevTime = time

        if (gameState == GameState.STOPPED || gameState == GameState.PAUSED) return

        val currentTargetWordVector = Vector2(targetLocation.x.value.toDouble(), targetLocation.y.value.toDouble())
        val shipToTargetWord = currentTargetWordVector - userShip.position

        userShip.visualAngle = shipToTargetWord.angle()

        val enemyBullets = gameObjects.filterIsInstance<EnemyBulletData>()

        val bullets = gameObjects.filterIsInstance<BulletData>()

        enemyBullets.forEach { enemyBulletData ->
            bullets.firstOrNull { it.overlapsWith(enemyBulletData) }?.let {
                //todo: introduce drag in enemy bullets
                gameObjects.remove(it)
            }
        }

        for (gameObject in gameObjects) {
            gameObject.update(floatDelta, this)
        }

        if (enemyBullets.any { enemyBullet -> userShip.overlapsWith(enemyBullet) }) {
            endGame()
        }

        if (enemyBullets.isEmpty()) {
            winGame()
        }
    }

    fun onKeyboardInput(currentTypedLetter: String) {
        val enemyBullets = gameObjects.filterIsInstance<EnemyBulletData>()

        if (currentTypedLetter.isNotEmpty()) {
            currentTargetEnemyObject = currentTargetEnemyObject ?: enemyBullets.firstOrNull {
                it.word.first().lowercase() == currentTypedLetter.lowercase()
            }?.apply {
                currentTargetWord = word
                isUnderAttack = true
                targetLocation = DpOffset(
                    this.position.x.dp,
                    this.position.y.dp
                )
            }
            if (!currentTargetWord.isNullOrEmpty()) {
                if (currentTargetWord?.get(idxUnderValidation)?.lowercase() == currentTypedLetter.lowercase()) {
                    idxUnderValidation = idxUnderValidation.inc()

                    userShip.fire(this)

                    currentTargetWord?.substring(idxUnderValidation)?.let {
                        (currentTargetEnemyObject as? EnemyBulletData)?.word = it
                    }

                    if (idxUnderValidation == currentTargetWord?.length) {
                        gameObjects.remove(currentTargetEnemyObject)

                        gameObjects.removeAll(gameObjects.filterIsInstance<BulletData>())

                        idxUnderValidation = 0
                        currentTargetEnemyObject = null
                        currentTargetWord = null
                    }
                }
            }
        }

        if (enemyBullets.size <= 2) {
            addMoreEnemyBullets(enemyBulletSpeed)
            enemyBulletSpeed += 0.1
        }
    }

    private var enemyBulletSpeed = 2.0
    private fun addMoreEnemyBullets(speedD: Double) {
        if (currentWord == null) return
        repeat(wordsPerRound) {
            gameObjects.add(EnemyBulletData().apply {
                position = Vector2(Random.nextDouble() * width.value, Random.nextDouble() * 80.0)
                angle = (userShip.position - position).angle()
                speed = speedD
                word = currentWord as String
            })
        }
    }

    fun resumeGame() {
        gameState = GameState.RUNNING
        gameStatus = "Good luck!"
    }

    fun pauseGame() {
        gameState = GameState.PAUSED
        gameStatus = "Game Paused :("
    }

    private fun endGame() {
        gameObjects.remove(userShip)
        gameState = GameState.STOPPED
        gameStatus = "Better luck next time!"
    }

    private fun winGame() {
        gameState = GameState.STOPPED
        gameStatus = "Congratulations!"
    }

    var width by mutableStateOf(0.dp)
    var height by mutableStateOf(0.dp)
}