package org.wentura.physicalapplication.map

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.wentura.physicalapplication.Constants
import org.wentura.physicalapplication.MainActivity
import org.wentura.physicalapplication.R

class LocationUpdatesService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification = NotificationCompat.Builder(this, Constants.ACTIVITY_RECORDING_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.activity_recording_notification_title))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
            .build()

        startForeground(Constants.ACTIVITY_TRACKING_NOTIFICATION_ID, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
