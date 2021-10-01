package org.wentura.franko.map

import android.app.Service
import android.content.Intent
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
import org.wentura.franko.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class LocationUpdatesService : Service() {

    companion object {
        val TAG = LocationUpdatesService::class.simpleName
    }

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var elapsedTimeRepository: ElapsedTimeRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var timer: Timer
    private val initialTime = SystemClock.elapsedRealtime()

    private lateinit var notification: NotificationCompat.Builder

    private val locationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(10)
        fastestInterval = TimeUnit.SECONDS.toMillis(5)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notification = RecordingNotification(this)

        startForeground(Constants.ACTIVITY_TRACKING_NOTIFICATION_ID, notification.build())

        startTimer()

        @Suppress("MissingPermission")
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationRepository,
            Looper.getMainLooper()
        )

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        elapsedTimeRepository.elapsedTime.value = 0L

        timer.cancel()
        fusedLocationClient.removeLocationUpdates(locationRepository)
    }

    private fun startTimer() {
        timer = Timer()

        timer.scheduleAtFixedRate(timerTask {
            val elapsedTime = SystemClock.elapsedRealtime() - initialTime

            elapsedTimeRepository.elapsedTime.postValue(elapsedTime)

            val time = SimpleDateFormat("mm:ss", Locale.US).format(elapsedTime)

            val title = getString(
                R.string.activity_recording_notification_title,
                time
            )

            notification.setContentTitle(title)

            NotificationManagerCompat
                .from(applicationContext)
                .notify(Constants.ACTIVITY_TRACKING_NOTIFICATION_ID, notification.build())

        }, 0, 1000)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
