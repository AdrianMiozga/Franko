package org.wentura.franko.map

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import org.wentura.franko.Constants
import org.wentura.franko.MainActivity
import org.wentura.franko.R

class LocationUpdatesService : Service() {

    companion object {
        val TAG = LocationUpdatesService::class.simpleName
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("MissingPermission")
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

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            LocationRepository,
            Looper.getMainLooper()
        )

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        fusedLocationClient.removeLocationUpdates(LocationRepository)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
