package org.wentura.franko.map

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import org.wentura.franko.Constants
import org.wentura.franko.MainActivity
import org.wentura.franko.R

class RecordingNotification(
    context: Context
) : NotificationCompat.Builder(
    context,
    Constants.ACTIVITY_RECORDING_NOTIFICATION_CHANNEL_ID
) {

    private val pendingIntent: PendingIntent =
        Intent(context, MainActivity::class.java).let { notificationIntent ->
            val flagImmutable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }

            PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                flagImmutable
            )
        }

    init {
        this.setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
    }
}
