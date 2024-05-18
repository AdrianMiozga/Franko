package org.wentura.franko.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wentura.franko.data.*
import javax.inject.Inject

@HiltViewModel
class UserActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    activityRepository: ActivityRepository
) : ViewModel() {

    companion object {
        val TAG = UserActivityViewModel::class.simpleName
    }

    private val _userActivity = MutableLiveData<UserActivity>()
    val userActivity: LiveData<UserActivity> = _userActivity

    private val activityId: String = savedStateHandle["id"]
        ?: throw IllegalArgumentException("Missing uid")

    init {
        activityRepository
            .getActivity(activityId)
            .addSnapshotListener { activitySnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                if (activitySnapshot == null) return@addSnapshotListener

                val activity: Activity = activitySnapshot.toObject() ?: return@addSnapshotListener

                val uid = activity.uid ?: return@addSnapshotListener

                userRepository
                    .getUser(uid)
                    .get()
                    .addOnSuccessListener { userSnapshot ->
                        val user: User = userSnapshot.toObject() ?: return@addOnSuccessListener

                        _userActivity.value = UserActivity(user, activity)
                    }
            }
    }
}
