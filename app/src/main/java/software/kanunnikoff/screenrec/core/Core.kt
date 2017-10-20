package software.kanunnikoff.screenrec.core

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import software.kanunnikoff.screenrec.ui.MainActivity
import software.kanunnikoff.screenrec.R
import software.kanunnikoff.screenrec.model.Record
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
/**
 * Created by dmitry on 15/10/2017.
 */
object Core {
    const val APP_TAG = "screenREC"
    const val STOP_RECORDING_ACTION = "stop_recording"
    private const val RECORD_NUMBER = "record_number"
    private const val COLUMN_OUTPUT_FORMAT = "output_format"
    private const val COLUMN_AUDIO_ENCODER = "audio_encoder"
    private const val COLUMN_AUDIO_ENCODING_BIT_RATE = "audio_encoding_bit_rate"
    private const val COLUMN_AUDIO_SAMPLING_RATE = "audio_sampling_rate"
    private const val COLUMN_AUDIO_CHANNELS = "audio_channels"
    private const val COLUMN_VIDEO_ENCODER = "video_encoder"
    private const val COLUMN_VIDEO_ENCODING_BIT_RATE = "video_encoding_bit_rate"
    private const val COLUMN_VIDEO_FRAME_RATE = "video_frame_rate"
    private const val COLUMN_VIDEO_SIZE_WIDTH = "video_size_width"
    private const val COLUMN_VIDEO_SIZE_HEIGHT = "video_size_height"

    var sqliteStorage: RecordsSqliteStorage? = null
    var context: Context? = null

    fun init(context: Context) {
        this.context = context
        sqliteStorage = RecordsSqliteStorage(context)
    }

    fun showToast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun showNotification(header: String, body: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(header)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setUsesChronometer(true)

        val stopRecordingAction = Intent(context, MainActivity::class.java)
        stopRecordingAction.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        stopRecordingAction.putExtra(Core.STOP_RECORDING_ACTION, true)
        notificationBuilder.addAction(0, context!!.resources.getString(R.string.stop_recording_action), PendingIntent.getActivity(context, 0, stopRecordingAction, 0))

        NotificationManagerCompat.from(context).notify(1, notificationBuilder.build())
    }

    fun hideNotifications() = NotificationManagerCompat.from(context).cancelAll()

    fun receiveAllRecords(start: Long, callback: Callback<Record>) {
        callback.onResult(sqliteStorage!!.getAllRecords(start))
    }

    fun receiveFavoredRecords(start: Long, callback: Callback<Record>) {
        callback.onResult(sqliteStorage!!.getFavoredRecords(start))
    }

    fun favoriteRecord(record: Record) {
        record.isFavored = 1
        sqliteStorage?.favoriteRecord(record)
    }

    fun unFavoriteRecord(record: Record) {
        record.isFavored = 0
        sqliteStorage?.favoriteRecord(record)
    }

    fun insertRecord(record: Record): Long {
        return sqliteStorage!!.insertRecord(record)
    }

    fun deleteRecord(record: Record) {
        sqliteStorage?.deleteRecord(record)

        if (true) {   // todo
            File(record.outputFile).delete()
        }
    }

    fun loadBitmap(url: String, imageView: ImageView) {
        Glide.with(context).load(url).into(imageView)
    }

    fun loadBitmap(bitmap: Bitmap, imageView: ImageView) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        Glide.with(context)
                .load(stream.toByteArray())
                .asBitmap()
                .into(imageView)
    }

    fun loadBitmap(thumbnail: ByteArray, imageView: ImageView) {
        Glide.with(context)
                .load(thumbnail)
                .asBitmap()
                .into(imageView)
    }

    var recordNumber: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(RECORD_NUMBER, 1)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(RECORD_NUMBER, value)
            editor.apply()
        }

    var outputFormat: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_OUTPUT_FORMAT, MediaRecorder.OutputFormat.MPEG_4)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_OUTPUT_FORMAT, value)
            editor.apply()
        }

    var audioEncoder: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_AUDIO_ENCODER, MediaRecorder.AudioEncoder.AMR_NB)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_AUDIO_ENCODER, value)
            editor.apply()
        }

    var audioEncodingBitRate: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_AUDIO_ENCODING_BIT_RATE, 16000)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_AUDIO_ENCODING_BIT_RATE, value)
            editor.apply()
        }

    var audioSamplingRate: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_AUDIO_SAMPLING_RATE, 96000)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_AUDIO_SAMPLING_RATE, value)
            editor.apply()
        }

    var audioChannels: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_AUDIO_CHANNELS, MyMediaRecorder.AudioChannels.STEREO)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_AUDIO_CHANNELS, value)
            editor.apply()
        }

    var videoEncoder: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_VIDEO_ENCODER, MediaRecorder.VideoEncoder.H264)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_VIDEO_ENCODER, value)
            editor.apply()
        }

    var videoEncodingBitRate: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_VIDEO_ENCODING_BIT_RATE, 3000000)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_VIDEO_ENCODING_BIT_RATE, value)
            editor.apply()
        }

    var videoFrameRate: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_VIDEO_FRAME_RATE, 30)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_VIDEO_FRAME_RATE, value)
            editor.apply()
        }

    var videoSizeWidth: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_VIDEO_SIZE_WIDTH, 480)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_VIDEO_SIZE_WIDTH, value)
            editor.apply()
        }

    var videoSizeHeight: Int
        get() = context!!.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE).getInt(COLUMN_VIDEO_SIZE_HEIGHT, 800)
        set(value) {
            val editor = context!!.getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE).edit()
            editor.putInt(COLUMN_VIDEO_SIZE_HEIGHT, value)
            editor.apply()
        }

    fun outputFormatString(): String {
        return when (outputFormat) {
            MediaRecorder.OutputFormat.THREE_GPP -> "3GPP"
            MediaRecorder.OutputFormat.MPEG_4 -> "MPEG4"
            else -> "Default"
        }
    }

    fun audioEncoderString(): String {
        return when (audioEncoder) {
            MediaRecorder.AudioEncoder.AMR_NB -> "AMR (Narrowband)"
            MediaRecorder.AudioEncoder.AMR_WB -> "AMR (Wideband)"
            MediaRecorder.AudioEncoder.AAC -> "AAC Low Complexity (AAC-LC)"
            MediaRecorder.AudioEncoder.HE_AAC -> "High Efficiency AAC (HE-AAC)"
            MediaRecorder.AudioEncoder.AAC_ELD -> "Enhanced Low Delay AAC (AAC-ELD)"
            MediaRecorder.AudioEncoder.VORBIS -> "Ogg Vorbis"
            else -> "Default"
        }
    }

    fun audioChannelsString(): String {
        return when (audioChannels) {
            MyMediaRecorder.AudioChannels.MONO -> "Mono"
            else -> "Stereo"
        }
    }

    fun videoEncoderString(): String {
        return when (videoEncoder) {
            MediaRecorder.VideoEncoder.H263 -> "H263"
            MediaRecorder.VideoEncoder.H264 -> "H264"
            MediaRecorder.VideoEncoder.MPEG_4_SP -> "MPEG4 SP"
            MediaRecorder.VideoEncoder.VP8 -> "VP8"
            MediaRecorder.VideoEncoder.HEVC -> "HEVC"
            else -> "Default"
        }
    }

    fun formatDate(date: Long): String {
        return if (android.text.format.DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("dd, MMMM yyyy, HH:mm:ss", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("dd, MMMM yyyy, hh:mm:ss a", Locale.getDefault()).format(date)
        }
    }

    fun formatDuration(duration: Long): String {
        val hour = duration / 3600
        val minute = duration % 3600 / 60
        val second = duration % 3600 % 60

        var result = ""

        if (hour > 0) {
            result += hour.toString() + "h "
        }

        if (minute > 0) {
            result += minute.toString() + "m "
        }

        result += second.toString() + "s"

        return result
    }
}