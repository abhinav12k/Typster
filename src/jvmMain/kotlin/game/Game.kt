package game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.openrndr.math.Vector2
import utils.*
import kotlin.random.Random

class Game {
    private var prevTime = 0L   // storing the last time game was updated
    private var userShip = UserShipData()

    private var targetLocation by mutableStateOf(DpOffset.Zero)

    private val gameConfig = GameConfig()

    var gameObjects = mutableStateListOf<GameObject>()
    var gameState by mutableStateOf(GameState.INITIALIZED)
        private set
    var gameStatus by mutableStateOf(GAME_STATUS_STARTED)
    var isGameMenuVisible by mutableStateOf(true)

    private var currentTargetWord: String? = null
    private var currentTargetEnemyObject: GameObject? = null
    private var idxUnderValidation = -1

    private var currentWordIdx = 0

    private val currentWord: String?
        get() {
            if (currentWordIdx == gameConfig.wordList.size) return null
            val tmp = gameConfig.wordList[currentWordIdx]
            currentWordIdx = currentWordIdx.inc()
            return tmp
        }

    fun updateWordList(enteredText: String) {
        gameConfig.updateWordList(enteredText)
    }

    fun startGame() {
        initGameVariables()

        setupPlayerShip()

        addEnemyBullets(gameConfig.enemyBulletSpeed)

        updateGameState(GameState.STARTED)
    }

    private fun initGameVariables() {
        gameConfig.resetGameConfig()

        gameObjects.clear()
        currentWordIdx = 0

        idxUnderValidation = 0
        currentTargetEnemyObject = null
        currentTargetWord = null
    }

    private fun setupPlayerShip() {
        userShip.position = Vector2(width.value / 2.0, height.value - 40.0)
        userShip.movementVector = Vector2.ZERO
        gameObjects.add(userShip)
    }

    private fun addEnemyBullets(speedD: Double) {
        repeat(gameConfig.getWordsPerLevel()) {
            currentWord?.let {
                if(it.isEmpty()) return@let
                gameObjects.add(EnemyBulletData().apply {
                    position = Vector2(Random.nextDouble() * width.value, Random.nextDouble() * 80.0)
                    angle = (userShip.position - position).angle()
                    speed = speedD
                    word = it
                })
            } ?: return@repeat
        }
    }

    private fun updateGameState(gameStateArg: GameState) {
        when (gameStateArg) {
            GameState.STARTED -> {
                gameState = GameState.STARTED
                gameStatus = GAME_STATUS_RESUMED
                isGameMenuVisible = false
            }

            GameState.PAUSED -> {
                gameState = GameState.PAUSED
                gameStatus = GAME_STATUS_PAUSED
                isGameMenuVisible = true
            }

            GameState.RESUMED -> {
                gameState = GameState.RESUMED
                gameStatus = GAME_STATUS_RESUMED
                isGameMenuVisible = false
            }

            GameState.LOST -> {
                gameState = GameState.STOPPED
                gameStatus = GAME_STATUS_LOST
                isGameMenuVisible = true
            }

            GameState.WON -> {
                gameState = GameState.STOPPED
                gameStatus = GAME_STATUS_WON
                isGameMenuVisible = true
            }

            GameState.STOPPED, GameState.INITIALIZED -> {}
        }
    }

    fun update(time: Long) {
        val delta = time - prevTime
        val floatDelta = (delta / 1e8).toFloat() //because time is in nano seconds
        prevTime = time

        if(!isGameInRunningState()) return

        userShip.visualAngle = getTargetAngle()

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

    private fun isGameInRunningState(): Boolean {
        return gameState == GameState.STARTED || gameState == GameState.RESUMED
    }

    private fun getTargetAngle(): Double {
        val currentTargetWordVector = Vector2(targetLocation.x.value.toDouble(), targetLocation.y.value.toDouble())
        val shipToTargetWord = currentTargetWordVector - userShip.position
        return shipToTargetWord.angle()
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
            addEnemyBullets(gameConfig.enemyBulletSpeed)
            gameConfig.increaseEnemyBulletSpeed()
        }
    }

    fun resumeGame() {
        updateGameState(GameState.RESUMED)
    }

    fun pauseGame() {
        updateGameState(GameState.PAUSED)
    }

    private fun endGame() {
        gameObjects.remove(userShip)
        updateGameState(GameState.LOST)
    }

    private fun winGame() {
        updateGameState(GameState.WON)
    }

    var width by mutableStateOf(0.dp)
    var height by mutableStateOf(0.dp)
}