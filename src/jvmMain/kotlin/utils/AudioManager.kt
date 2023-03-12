/**
 * @author abhinav12k
 */

package utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip


object AudioManager {

    private val bufferedInputFileMap by lazy { HashMap<String, BufferedInputStream>() }
    private var backgroundMusic: Clip? = null
    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO) }
    fun play(filePath: String = BACKGROUND_MUSIC_2_PATH, shouldLoop: Boolean = true) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val bufferedIn = bufferedInputFileMap.getOrDefault(filePath,
                        BufferedInputStream(
                            Thread.currentThread().contextClassLoader.getResourceAsStream(filePath)!!
                        ).apply { bufferedInputFileMap[filePath] = this }
                    )

                    val audioClip = AudioSystem.getClip()
                    val audioInputStream = AudioSystem.getAudioInputStream(bufferedIn)

                    audioClip.open(audioInputStream)
                    if (shouldLoop) {
                        audioClip.loop(Clip.LOOP_CONTINUOUSLY)
                    }
                    audioClip?.start()

                    if (filePath == BACKGROUND_MUSIC_2_PATH) {
                        backgroundMusic = audioClip
                    }

                } catch (e: Exception) {
                    println("Error: $e")
                }
            }
        }
    }

    fun isDefaultBackgroundMusicPlaying() = backgroundMusic?.isRunning == true

    fun stopBackgroundMusic() {
        backgroundMusic?.close()
    }

}