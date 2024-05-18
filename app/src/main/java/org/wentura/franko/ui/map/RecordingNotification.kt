package org.wentura.franko.ui.map

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import org.wentura.franko.Constants
import org.wentura.franko.MainActivity
import org.wentura.franko.R

class RecordingNotification(
    context: Context
) : NotificationCompat.Builder(
    context,
    Constants.ACTIVITY_RECORDING_NOTIFICATION_CHANNEL_ID
) {

    private val pendingIntent = NavDeepLinkBuilder(context)
        .setComponentName(MainActivity::class.java)
        .setGraph(R.navigation.navigation)
        .setDestination(R.id.map_fragment)
        .createPendingIntent()

    init {
        setSmallIcon(R.mipmap.ic_launcher)
        setContentIntent(pendingIntent)
        priority = NotificationCompat.PRIORITY_LOW
        setShowWhen(false)
        setCategory(NotificationCompat.CATEGORY_NAVIGATION)
        setOngoing(true)
    }
}
