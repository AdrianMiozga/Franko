package org.wentura.franko.ui.activityedit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wentura.franko.data.Activity
import org.wentura.franko.data.ActivityRepository
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    activityRepository: ActivityRepository,
) : ViewModel() {

    companion object {
        val TAG = ActivityViewModel::class.simpleName
    }

    private val activityId: String =
        savedStateHandle["id"] ?: throw IllegalArgumentException("Missing uid")

    private val _activity = MutableLiveData<Activity>()
    val activity: LiveData<Activity> = _activity

    init {
        activityRepository.getActivity(activityId).addSnapshotListener { documentSnapshot, exception
            ->
            if (exception != null) {
                Log.w(TAG, "Listen failed.", exception)
                return@addSnapshotListener
            }

            if (documentSnapshot == null) return@addSnapshotListener

            _activity.value = documentSnapshot.toObject()!!
        }
    }
}
