package org.wentura.franko.map

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.MainActivity
import org.wentura.franko.R
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class LocationUpdatesService : Service() {

    companion object {
        val TAG = LocationUpdatesService::class.simpleName
    }

    @Inject
    lateinit var locationRepository: LocationRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var timer: Timer
    private var initialTime = -1L

    private lateinit var notification: NotificationCompat.Builder

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        0
                    )
                }
            }

        notification = NotificationCompat
            .Builder(this, Constants.ACTIVITY_RECORDING_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.activity_recording_notification_title))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)

        startForeground(Constants.ACTIVITY_TRACKING_NOTIFICATION_ID, notification.build())

        startTimer()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationRepository,
            Looper.getMainLooper()
        )

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        timer.cancel()
        fusedLocationClient.removeLocationUpdates(locationRepository)
    }

    private fun startTimer() {
        initialTime = SystemClock.elapsedRealtime()

        timer = Timer()
        timer.scheduleAtFixedRate(timerTask {
            val elapsedSeconds = SystemClock.elapsedRealtime() - initialTime

            val title = getString(
                R.string.activity_recording_notification_title,
                SimpleDateFormat("mm:ss", Locale.US)
                    .format(elapsedSeconds)
            )

            notification.setContentTitle(title)

            NotificationManagerCompat
                .from(applicationContext)
                .notify(Constants.ACTIVITY_TRACKING_NOTIFICATION_ID, notification.build())

        }, 0, 1000)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
