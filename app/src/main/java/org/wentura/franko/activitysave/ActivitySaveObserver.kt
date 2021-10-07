package org.wentura.franko.activitysave

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import com.google.firebase.auth.FirebaseAuth
import org.wentura.franko.Constants
import org.wentura.franko.data.Activity
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.databinding.FragmentActivitySaveBinding
import org.wentura.franko.map.RecordingRepository
import org.wentura.franko.map.RecordingService
import java.util.concurrent.TimeUnit

class ActivitySaveObserver(
    private val recordingRepository: RecordingRepository,
    private val activityRepository: ActivityRepository,
    private val context: Context,
    private val view: View
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

        val binding = FragmentActivitySaveBinding.bind(view)

        val activitySaveActivityName = binding.activitySaveActivityName
        val activitySaveActivityTypeSpinner = binding.activitySaveActivityTypeSpinner
        val activitySaveActivityVisibilitySpinner = binding.activitySaveActivityVisibilitySpinner

        val activity = Activity(
            uid,
            TimeUnit.MILLISECONDS.toSeconds(startTime),
            TimeUnit.MILLISECONDS.toSeconds(startTime + elapsedTime),
            array,
            activitySaveActivityTypeSpinner.selectedItem.toString(),
            activitySaveActivityName.text.toString().trim(),
            activitySaveActivityVisibilitySpinner.selectedItem.toString()
        )

        activityRepository.addActivity(activity)
    }
}
