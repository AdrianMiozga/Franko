package org.wentura.franko.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.data.Path
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    companion object {
        val TAG = ActivityViewModel::class.simpleName
    }

    private val activityId: String = savedStateHandle["id"] ?: throw IllegalArgumentException("Missing uid")

    private val _activity = MutableLiveData<Path>()

    fun getCurrentActivity(): LiveData<Path> {
        activityRepository
            .getActivity(activityId)
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                if (documentSnapshot == null) return@addSnapshotListener

                _activity.value = documentSnapshot.toObject()
            }

        return _activity
    }
}
