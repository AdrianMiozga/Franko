package org.wentura.franko.activitysave

import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.data.Activity
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.data.User
import org.wentura.franko.data.UserRepository
import org.wentura.franko.map.RecordingRepository
import org.wentura.franko.map.RecordingService
import java.util.concurrent.TimeUnit

class ActivitySaveObserver(
    private val recordingRepository: RecordingRepository,
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val context: Context
) : DefaultLifecycleObserver {

    fun save() {
        val array: MutableList<HashMap<String, Double>> = ArrayList()

        val points = recordingRepository.points.value
        val startTime = recordingRepository.startTime
        val elapsedTime = recordingRepository.recordingTime.value

        context.stopService(Intent(context, RecordingService::class.java))

        if (points == null) return
        if (startTime == 0L) return
        if (elapsedTime == null) return

        points.forEach { point ->
            val element = hashMapOf(
                Constants.LATITUDE to point.latitude,
                Constants.LONGITUDE to point.longitude
            )

            array.add(element)
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userRepository
            .getUser()
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user: User = documentSnapshot.toObject() ?: return@addOnSuccessListener

                val activity = Activity(
                    uid,
                    // TODO: 01.10.2021 Store in milliseconds?
                    TimeUnit.MILLISECONDS.toSeconds(startTime),
                    TimeUnit.MILLISECONDS.toSeconds(startTime + elapsedTime),
                    array,
                    user.lastActivity,
                    context.getString(R.string.activity_without_name),
                    user.whoCanSeeActivityDefault
                )

                activityRepository.addActivity(activity)
            }
    }
}
