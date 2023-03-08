package game

import data.text
import utils.DEFAULT_ENEMY_BULLET_SPEED
import utils.DEFAULT_STAR_SPEED
import utils.DEFAULT_WORDS_PER_LEVEL

class GameConfig {
    private val regex = Regex("[^a-zA-Z]+")
    private var wordsPerLevel = DEFAULT_WORDS_PER_LEVEL
    var wordList: List<String> = prepareWordList()
    private var level = 1
    var enemyBulletSpeed = DEFAULT_ENEMY_BULLET_SPEED
        private set
    var starSpeed = DEFAULT_STAR_SPEED
        private set

    fun resetGameConfig() {
        wordsPerLevel = DEFAULT_WORDS_PER_LEVEL
        level = 1
        enemyBulletSpeed = DEFAULT_ENEMY_BULLET_SPEED
        wordList = prepareWordList()
    }

    fun increaseEnemyBulletSpeed() {
        enemyBulletSpeed += 0.1
    }

    fun increaseStarSpeed() {
        starSpeed += 0.1
    }

    fun increaseLevel() {
        level = level.inc()
    }

    fun getWordsPerLevel(): Int {
        return when (level) {
            in 1..7 -> wordsPerLevel
            in 8..9 -> {
                wordsPerLevel = wordsPerLevel.inc()
                wordsPerLevel
            }

            else -> wordsPerLevel
        }
    }

    private fun prepareWordList(): List<String> {
        return regex.split(text)
    }

    fun updateWordList(enteredText: String) {
        wordList = regex.split(enteredText)
    }

    fun isBackgroundMusicEnabledOnStart() = false

}