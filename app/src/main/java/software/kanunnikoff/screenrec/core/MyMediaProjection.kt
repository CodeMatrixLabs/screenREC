package software.kanunnikoff.screenrec.core

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.util.DisplayMetrics


/**
 * Created by dmitry on 15/10/2017.
 */
class MyMediaProjection {
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null

    fun init(context: Context, displayMetrics: DisplayMetrics, recorder: MyMediaRecorder, resultCode: Int, data: Intent?) {
        mediaProjection = (context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager).getMediaProjection(resultCode, data)

        virtualDisplay = mediaProjection?.createVirtualDisplay(Core.APP_TAG,
                displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                recorder.getSurface(), null, null)
    }

    fun isInited(): Boolean {
        return mediaProjection != null
    }

    fun stop() {
        mediaProjection?.stop()
        virtualDisplay?.release()

        mediaProjection = null
        virtualDisplay = null
    }
}