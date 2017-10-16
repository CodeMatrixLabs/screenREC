package software.kanunnikoff.screenrec.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.view.Gravity
import android.widget.Toast
import software.kanunnikoff.screenrec.MainActivity
import software.kanunnikoff.screenrec.R

/**
 * Created by dmitry on 15/10/2017.
 */
object Core {
    const val APP_TAG = "screenREC"
    const val STOP_RECORDING_ACTION = "stop_recording"

    fun showToast(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun showNotification(context: Context, header: String, body: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(header)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        val stopRecordingAction = Intent(context, MainActivity::class.java)
        stopRecordingAction.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        stopRecordingAction.putExtra(Core.STOP_RECORDING_ACTION, true)
        notificationBuilder.addAction(0, context.resources.getString(R.string.stop_recording_action), PendingIntent.getActivity(context, 0, stopRecordingAction, 0))

        NotificationManagerCompat.from(context).notify(1, notificationBuilder.build())
    }

    fun hideNotifications(context: Context) = NotificationManagerCompat.from(context).cancelAll()
}