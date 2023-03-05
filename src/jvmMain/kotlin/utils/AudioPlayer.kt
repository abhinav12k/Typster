package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip


object AudioPlayer {

    private var audioClip: Clip? = null
    suspend fun play(filePath: String = BACKGROUND_MUSIC_2_PATH) {
        withContext(Dispatchers.IO) {
            try {
                stop()
                val bufferedIn = BufferedInputStream(
                    Thread.currentThread().contextClassLoader.getResourceAsStream(filePath)!!
                )
                val audioInputStream = AudioSystem.getAudioInputStream(bufferedIn)
                audioClip = AudioSystem.getClip()
                audioClip?.open(audioInputStream)
                audioClip?.loop(Clip.LOOP_CONTINUOUSLY)
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }

    fun stop() {
        audioClip?.stop()
        audioClip = null
    }

}