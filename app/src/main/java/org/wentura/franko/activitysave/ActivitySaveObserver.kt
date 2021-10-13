package org.wentura.franko.activitysave

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.Utilities.getCurrentUserUid
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
        val path: ArrayList<HashMap<String, Double>> = ArrayList()

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

            path.add(element)
        }

        val uid = getCurrentUserUid()

        val binding = FragmentActivitySaveBinding.bind(view)

        val activitySaveActivityName = binding.activitySaveActivityName
        val activitySaveActivityTypeSpinner = binding.activitySaveActivityType
        val activitySaveActivityVisibilitySpinner = binding.activitySaveActivityVisibility

        var activityName = activitySaveActivityName.editText?.text.toString().trim()

        if (activityName.isBlank()) {
            activityName = context.resources.getString(R.string.activity_without_name)
        }

        val activityIndex = context.resources
            .getStringArray(R.array.activities_array)
            .indexOf(activitySaveActivityTypeSpinner.editText?.text.toString())

        val activityType = context.resources.getStringArray(R.array.activities_array_values)[activityIndex]

        val visibilityIndex = context.resources
            .getStringArray(R.array.who_can_see_activity)
            .indexOf(activitySaveActivityVisibilitySpinner.editText?.text.toString())

        val visibility = context.resources.getStringArray(R.array.who_can_see_activity_values)[visibilityIndex]

        val activity = Activity(
            uid,
            TimeUnit.MILLISECONDS.toSeconds(startTime),
            TimeUnit.MILLISECONDS.toSeconds(startTime + elapsedTime),
            path,
            activityType,
            activityName,
            visibility
        )

        activityRepository.addActivity(activity)
    }
}
