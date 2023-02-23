package utils

import javazoom.jl.player.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream

object AudioPlayer {

    private var audioPlayer: Player? = null
    private var localFilePath: String = BACKGROUND_MUSIC_PATH
    suspend fun play(filePath: String = BACKGROUND_MUSIC_PATH) {
        localFilePath = filePath
        withContext(Dispatchers.IO) {
            stop()
            val inputStream = FileInputStream(filePath)
            audioPlayer = Player(inputStream)
            audioPlayer?.play()
        }
    }

    fun stop() {
        audioPlayer?.close()
        audioPlayer = null
    }

}