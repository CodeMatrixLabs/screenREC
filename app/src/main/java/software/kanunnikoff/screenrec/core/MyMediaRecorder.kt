package software.kanunnikoff.screenrec.core

import android.annotation.TargetApi
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import software.kanunnikoff.screenrec.core.Core.APP_TAG
import java.io.File

/**
 * Created by dmitry on 15/10/2017.
 */
class MyMediaRecorder {
    private var recorder: MediaRecorder? = null
    private var dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), APP_TAG)
    private val ORIENTATIONS = SparseIntArray()
    var outputFileAbsolutePath: String = ""
    var duration: Long = 0L
    var date: Long = 0L

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    fun init(
            outputFormat: Int = MediaRecorder.OutputFormat.MPEG_4,
            outputFile: String,
            audioEncoder: Int = MediaRecorder.AudioEncoder.AMR_NB,
            audioEncodingBitRate: Int = 16000,
            audioSamplingRate: Int = 96000,   // 96 kHz
            audioChannels: Int = AudioChannels.MONO,
            videoEncoder: Int = MediaRecorder.VideoEncoder.H264,
            videoEncodingBitRate: Int = 3000000,
            videoFrameRate: Int = 30,
            videoSizeWidth: Int = 480,
            videoSizeHeight: Int = 800,
            maxDuration: Int = -1,
            maxFileSize: Long = -1,
            rotation: Int = 0) {

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d(APP_TAG, "failed to create '$APP_TAG' directory")
                return
            }
        }

        recorder = MediaRecorder()

        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)

        recorder?.setOutputFormat(outputFormat)
        outputFileAbsolutePath = dir.absolutePath + File.separator + outputFile
        recorder?.setOutputFile(outputFileAbsolutePath)

        recorder?.setAudioEncoder(audioEncoder)
        recorder?.setAudioEncodingBitRate(audioEncodingBitRate)
        recorder?.setAudioSamplingRate(audioSamplingRate)
        recorder?.setAudioChannels(audioChannels)

        recorder?.setVideoEncoder(videoEncoder)
        recorder?.setVideoEncodingBitRate(videoEncodingBitRate)
        recorder?.setVideoFrameRate(videoFrameRate)
        recorder?.setVideoSize(videoSizeWidth, videoSizeHeight)

        recorder?.setMaxDuration(maxDuration)
        recorder?.setMaxFileSize(maxFileSize)

        recorder?.setOrientationHint(ORIENTATIONS.get(rotation + 90))

        try {
            recorder?.prepare()
        } catch (cause: Throwable) {
            Log.e(Core.APP_TAG, "can't prepare media recorder: ${cause.localizedMessage}")
        }
    }

    fun start() {
        date = System.currentTimeMillis()
        recorder?.start()
    }

    fun stop() {
        recorder?.stop()
        recorder?.release()
        duration = System.currentTimeMillis() - date
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun pause() {
        recorder?.pause()
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun resume() {
        recorder?.resume()
    }

    fun getSurface(): Surface {
        return recorder!!.surface
    }

    object AudioChannels {
        const val MONO = 1
        const val STEREO = 2
    }
}