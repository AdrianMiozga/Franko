package org.wentura.franko.map

import android.app.Service
import android.content.Intent
import android.os.Binder
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
class RecordingService : Service() {

    companion object {
        val TAG = RecordingService::class.simpleName
    }

    @Inject
    lateinit var recordingRepository: RecordingRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var timer: Timer
    private val initialTime = SystemClock.elapsedRealtime()

    private lateinit var notification: NotificationCompat.Builder

    private val binder: IBinder = LocalBinder()

    private val locationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(10)
//        fastestInterval = TimeUnit.SECONDS.toMillis(5)
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
            recordingRepository,
            Looper.getMainLooper()
        )

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        recordingRepository.recordingTime.value = 0L
        recordingRepository.points.value = ArrayList()

        stopUpdates()
    }

    private fun startTimer() {
        timer = Timer()

        timer.scheduleAtFixedRate(timerTask {
            val elapsedTime = SystemClock.elapsedRealtime() - initialTime

            recordingRepository.startTime = System.currentTimeMillis()
            recordingRepository.recordingTime.postValue(elapsedTime)

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

    fun stopUpdates() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }

        fusedLocationClient.removeLocationUpdates(recordingRepository)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {

        fun getService(): RecordingService = this@RecordingService
    }
}
