package software.kanunnikoff.screenrec.core

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
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
import android.preference.PreferenceManager
import android.util.Log


@SuppressLint("StaticFieldLeak")
/**
 * Created by dmitry on 15/10/2017.
 */
object Core {
    const val APP_TAG = "screenREC"
    const val STOP_RECORDING_ACTION = "stop_recording"
    private const val RECORD_NUMBER = "record_number"
    private const val COLUMN_FILE_NAME_PREFIX = "pref_key_file_name_prefix"
    private const val COLUMN_RECORD_TITLE_PREFIX = "pref_key_record_title_prefix"
    private const val COLUMN_OUTPUT_FORMAT = "pref_key_output_format"
    private const val COLUMN_AUDIO_ENCODER = "pref_key_audio_encoder"
    private const val COLUMN_AUDIO_ENCODING_BIT_RATE = "pref_key_audio_encoding_bit_rate"
    private const val COLUMN_AUDIO_SAMPLING_RATE = "pref_key_audio_sampling_rate"
    private const val COLUMN_AUDIO_CHANNELS = "pref_key_audio_channels"
    private const val COLUMN_VIDEO_ENCODER = "pref_key_video_encoder"
    private const val COLUMN_VIDEO_ENCODING_BIT_RATE = "pref_key_video_encoding_bit_rate"
    private const val COLUMN_VIDEO_FRAME_RATE = "pref_key_video_frame_rate"
    private const val COLUMN_VIDEO_SIZE_WIDTH = "pref_key_video_size_width"
    private const val COLUMN_VIDEO_SIZE_HEIGHT = "pref_key_video_size_height"

    const val DEFAULT_FILE_NAME = "screenrec"

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
        File(record.outputFile).delete()
    }

    fun renameRecord(record: Record, title: String) {
        sqliteStorage!!.renameRecord(record, title)
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

    fun getFileNamePrefix(): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_FILE_NAME_PREFIX, "")
    }

    fun getRecordTitlePrefix(): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_RECORD_TITLE_PREFIX, "")
    }

    fun getOutputFormat(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_OUTPUT_FORMAT, "-1").toInt()
    }

    fun getAudioEncoder(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_AUDIO_ENCODER, "-1").toInt()
    }

    fun getAudioEncodingBitRate(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_AUDIO_ENCODING_BIT_RATE, "-1").toInt()
    }

    fun getAudioSamplingRate(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_AUDIO_SAMPLING_RATE, "-1").toInt()
    }

    fun getAudioChannels(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_AUDIO_CHANNELS, "-1").toInt()
    }

    fun getVideoEncoder(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_VIDEO_ENCODER, "-1").toInt()
    }

    fun getVideoEncodingBitRate(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_VIDEO_ENCODING_BIT_RATE, "-1").toInt()
    }

    fun getVideoFrameRate(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_VIDEO_FRAME_RATE, "-1").toInt()
    }

    fun getVideoSizeWidth(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_VIDEO_SIZE_WIDTH, "-1").toInt()
    }

    fun getVideoSizeHeight(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COLUMN_VIDEO_SIZE_HEIGHT, "-1").toInt()
    }

    fun outputFormatString(): String {
        return when (getOutputFormat()) {
            MediaRecorder.OutputFormat.THREE_GPP -> "3GPP"
            MediaRecorder.OutputFormat.MPEG_4 -> "MPEG4"
            else -> "Default"
        }
    }

    fun audioEncoderString(): String {
        return when (getAudioEncoder()) {
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
        return when (getAudioChannels()) {
            MyMediaRecorder.AudioChannels.MONO -> context!!.resources.getString(R.string.audio_channels_mono)
            else -> context!!.resources.getString(R.string.audio_channels_stereo)
        }
    }

    fun videoEncoderString(): String {
        return when (getVideoEncoder()) {
            MediaRecorder.VideoEncoder.H263 -> "H263"
            MediaRecorder.VideoEncoder.H264 -> "H264"
            MediaRecorder.VideoEncoder.MPEG_4_SP -> "MPEG4 SP"
            MediaRecorder.VideoEncoder.VP8 -> "VP8"
            MediaRecorder.VideoEncoder.HEVC -> "HEVC"
            else -> "Default"
        }
    }

    fun formatDate(date: Long): String {
        var result = if (android.text.format.DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("dd, MMMM yyyy, HH:mm:ss", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("dd, MMMM yyyy, hh:mm:ss a", Locale.getDefault()).format(date)
        }

        for ((k, v) in rusMonths) {
            result = result.replace(k, v)
        }

        return result
    }

    private val rusMonths = mapOf(
            "января" to "Январь",
            "февраля" to "Февраль",
            "марта" to "Март",
            "апреля" to "Апрель",
            "мая" to "Май",
            "июня" to "Июнь",
            "июля" to "Июль",
            "августа" to "Август",
            "сентября" to "Сентябрь",
            "октября" to "Октябрь",
            "ноября" to "Ноябрь",
            "декабря" to "Декабрь"
    )

    fun formatDateForFileName(date: Date): String {
        return if (android.text.format.DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("_yyyyMMdd_HHmmss", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("_yyyyMMdd_hhmmss_a", Locale.getDefault()).format(date)
        }
    }

    fun formatDuration(duration: Long): String {
        val hour = duration / 3600
        val minute = duration % 3600 / 60
        val second = duration % 3600 % 60

        var result = ""

        if (hour > 0) {
            result += hour.toString() + context?.getString(R.string.h) + " "
        }

        if (minute > 0) {
            result += minute.toString() + context?.getString(R.string.m) + " "
        }

        result += second.toString() + context?.getString(R.string.s)

        return result
    }
}