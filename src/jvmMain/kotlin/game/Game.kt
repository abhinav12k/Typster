/**
 * @author abhinav12k
 */

package game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.openrndr.math.Vector2
import utils.AudioManager
import utils.EXPLOSION_SOUND_PATH
import utils.FIRING_SOUND_PATH
import utils.angle
import kotlin.random.Random

class Game {
    private var prevTime = 0L   // storing the last time game was updated
    private var userShip = UserShipData()

    private val gameConfig = GameConfig()

    val starsData by lazy {
        getStars(heightPx, widthPx)
    }

    var gameObjects = mutableStateListOf<GameObject>()
    var gameState by mutableStateOf(GameState.INITIALIZED)
        private set
    var isGameMenuVisible by mutableStateOf(true)
    val isBackgroundMusicEnabledOnStart = gameConfig.isBackgroundMusicEnabledOnStart()

    private var currentTargetWord: String? = null
    private var currentTargetEnemyObject: GameObject? = null
    private var idxUnderValidation = -1

    private var currentWordIdx = 0
    private var numberOfCorrectKeyStrokes = 0
    private var totalNumberOfKeyStrokes = 0
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var totalTimePaused: Long = 0
    private var isPaused = false
    private var pauseStartTime: Long = 0

    private val currentWord: String?
        get() {
            if (currentWordIdx == gameConfig.wordList.size) return null
            val tmp = gameConfig.wordList[currentWordIdx]
            currentWordIdx = currentWordIdx.inc()
            return tmp
        }

    fun startGame(wordsString: String? = null) {
        initGameVariables(wordsString)

        setupPlayerShip()

        addEnemyBullets(gameConfig.enemyBulletSpeed)

        updateGameState(GameState.STARTED)

        AudioManager.stopBackgroundMusic()
        AudioManager.play()

        startTime = System.currentTimeMillis()
    }

    private fun initGameVariables(wordsString: String?) {
        gameConfig.resetGameConfig()

        if (!wordsString.isNullOrEmpty()) {
            gameConfig.updateWordList(wordsString)
        }

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
                if (it.isEmpty()) return@let
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
                isGameMenuVisible = false
            }

            GameState.PAUSED -> {
                gameState = GameState.PAUSED
                isGameMenuVisible = true

                isPaused = true
                pauseStartTime = System.currentTimeMillis()
            }

            GameState.RESUMED -> {
                gameState = GameState.RESUMED
                isGameMenuVisible = false

                if (isPaused) {
                    isPaused = false
                    val pauseEndTime = System.currentTimeMillis()
                    totalTimePaused += pauseEndTime - pauseStartTime
                }
            }

            GameState.LOST -> {
                gameState = GameState.LOST
                isGameMenuVisible = true

                endTime = System.currentTimeMillis()
            }

            GameState.WON -> {
                gameState = GameState.WON
                isGameMenuVisible = true

                endTime = System.currentTimeMillis()
            }

            GameState.INITIALIZED -> {}
        }
    }

    fun update(time: Long) {
        val delta = time - prevTime
        val floatDelta = (delta / 1e8).toFloat() //because time is in nano seconds
        prevTime = time

        if (!isGameInRunningState()) return

        val enemyBullets = gameObjects.filterIsInstance<EnemyBulletData>()
        val bullets = gameObjects.filterIsInstance<BulletData>()
        val blastDataRemovalList = gameObjects.filterIsInstance<BlastingBoxData>().filter { it.isBlastShown }
        gameObjects.removeAll(blastDataRemovalList)

        enemyBullets.forEach { enemyBulletData ->
            if (enemyBulletData.isWordFinished) {
                gameObjects.remove(enemyBulletData)
                gameObjects.add(BlastingBoxData(80.0).apply {
                    position = enemyBulletData.position
                })
            } else {
                bullets.firstOrNull { it.overlapsWith(enemyBulletData) }?.let {
                    //todo: introduce drag in enemy bullets
//                enemyBulletData.position -= Vector2(0.0,4.0)

                    gameObjects.remove(it)
                }
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

        updateStarData()
    }

    private fun updateStarData() {
        starsData.forEach {
            var yPos = it.position.y + gameConfig.starSpeed.toFloat()
            if (yPos >= heightPx) {
                yPos = 0f
            }
            it.position = it.position.copy(y = yPos)
        }
    }

    private fun isGameInRunningState(): Boolean {
        return gameState == GameState.STARTED || gameState == GameState.RESUMED
    }

    private fun getTargetAngle(targetLocation: DpOffset): Double {
        val currentTargetWordVector = Vector2(targetLocation.x.value.toDouble(), targetLocation.y.value.toDouble())
        val shipToTargetWord = currentTargetWordVector - userShip.position
        return shipToTargetWord.angle()
    }

    fun onKeyboardInput(currentTypedLetter: String) {
        totalNumberOfKeyStrokes = totalNumberOfKeyStrokes.inc()
        val enemyBullets = gameObjects.filterIsInstance<EnemyBulletData>()

        if (currentTypedLetter.isNotEmpty()) {
            currentTargetEnemyObject = currentTargetEnemyObject ?: enemyBullets.firstOrNull {
                !it.isWordFinished && it.word.first().lowercase() == currentTypedLetter.lowercase()
            }?.apply {
                currentTargetWord = word
                isUnderAttack = true
                val targetLocation = DpOffset(
                    this.position.x.dp,
                    this.position.y.dp
                )
                userShip.visualAngle = getTargetAngle(targetLocation)
            }
            if (!currentTargetWord.isNullOrEmpty() && idxUnderValidation < (currentTargetWord?.length ?: 0)) {
                if (currentTargetWord?.get(idxUnderValidation)?.lowercase() == currentTypedLetter.lowercase()) {
                    numberOfCorrectKeyStrokes = numberOfCorrectKeyStrokes.inc()
                    idxUnderValidation = idxUnderValidation.inc()

                    AudioManager.play(FIRING_SOUND_PATH, false)
                    userShip.fire(this)

                    currentTargetWord?.substring(idxUnderValidation)?.let {
                        (currentTargetEnemyObject as? EnemyBulletData)?.word = it
                    }

                    if (idxUnderValidation == currentTargetWord?.length) {
                        AudioManager.play(EXPLOSION_SOUND_PATH, false)

                        (currentTargetEnemyObject as? EnemyBulletData)?.isWordFinished = true

                        gameObjects.removeAll(gameObjects.filterIsInstance<BulletData>())

                        idxUnderValidation = 0
                        currentTargetEnemyObject = null
                        currentTargetWord = null
                    }

                } else {
                    (currentTargetEnemyObject as? EnemyBulletData)?.isTypedCharacterMismatched = true
                }
            }
        }

        if (enemyBullets.size <= 2) {
            gameConfig.increaseLevel()
            gameConfig.increaseEnemyBulletSpeed()
            gameConfig.increaseStarSpeed()
            addEnemyBullets(gameConfig.enemyBulletSpeed)
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

    fun calculateGameStats(): GameStats {

        val totalTimeInMillis = endTime - startTime - totalTimePaused
        val totalTimeInSeconds = totalTimeInMillis / 1000

        val accuracy: Double = (numberOfCorrectKeyStrokes.toDouble() / totalNumberOfKeyStrokes) * 100
        val wpm: Double = (totalNumberOfKeyStrokes / 5.0) / (totalTimeInSeconds / 60.0)
        val errorRate: Double = ((totalNumberOfKeyStrokes - numberOfCorrectKeyStrokes).toDouble() / totalNumberOfKeyStrokes) * 100
        val timeTakenInMinutes: Double = totalTimeInSeconds / 60.0

        return GameStats(
            "%.2f%%".format(accuracy),
            "%.2f".format(wpm),
            "%.2f%%".format(errorRate),
            "%.2f minutes".format(timeTakenInMinutes)
        )
    }


    var width by mutableStateOf(0.dp)
    var height by mutableStateOf(0.dp)

    var widthPx = 0
    var heightPx = 0
}