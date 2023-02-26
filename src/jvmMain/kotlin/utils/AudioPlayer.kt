package utils

import javazoom.jl.player.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream

object AudioPlayer {

    private var audioPlayer: Player? = null
    private var localFilePath: String = BACKGROUND_MUSIC_PATH
    suspend fun play(filePath: String = BACKGROUND_MUSIC_PATH) {
        withContext(Dispatchers.IO) {
            try {
                stop()
                localFilePath = filePath
                val inputStream = FileInputStream(filePath)
                audioPlayer = Player(inputStream)
                audioPlayer?.play()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    fun stop() {
        audioPlayer?.close()
        audioPlayer = null
    }

}