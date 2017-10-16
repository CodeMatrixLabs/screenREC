package software.kanunnikoff.screenrec.core

import android.media.MediaPlayer
import android.util.Log

/**
 * Created by dmitry on 15/10/2017.
 */
class MyMediaPlayer {
    private val player = MediaPlayer()

    fun init(file: String) {
        player.reset()
        player.setDataSource(file)
    }

    fun start() {
        try {
            player.prepare()
            player.start()
        } catch (cause: Throwable) {
            Log.e(Core.APP_TAG, "can't prepare media player: ${cause.localizedMessage}")
        }
    }

    fun stop() {
        player.release()
    }
}