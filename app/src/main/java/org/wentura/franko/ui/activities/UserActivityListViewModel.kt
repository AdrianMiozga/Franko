package org.wentura.franko.ui.activities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.wentura.franko.Constants
import org.wentura.franko.Utilities.getCurrentUserUid
import org.wentura.franko.data.Activity
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.data.User
import org.wentura.franko.data.UserActivity
import org.wentura.franko.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserActivityListViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    companion object {
        val TAG = UserActivityListViewModel::class.simpleName
    }

    private val _userActivities = MutableLiveData<List<UserActivity>>()
    val userActivities: LiveData<List<UserActivity>> = _userActivities

    init {
        getCurrentActivities(Constants.ACTIVITY_TYPES)
    }

    fun getCurrentActivities(activityTypes: List<String>): LiveData<List<UserActivity>> {
        if (activityTypes.isEmpty()) {
            _userActivities.value = ArrayList()
            return _userActivities
        }

        viewModelScope.launch {
            val userSnapshot = userRepository.getUser().get().await()

            val user: User = userSnapshot.toObject() ?: return@launch

            val query =
                activityRepository
                    .getActivities(getCurrentUserUid())
                    .orderBy(Constants.END_TIME, Query.Direction.DESCENDING)
                    .whereIn(Constants.ACTIVITY, activityTypes)

            query.addSnapshotListener { activitiesSnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, exception)
                    return@addSnapshotListener
                }

                if (activitiesSnapshot == null) return@addSnapshotListener

                val activities: List<Activity> = ArrayList(activitiesSnapshot.toObjects())
                val result: ArrayList<UserActivity> = ArrayList(activities.size)

                // TODO: 28.09.2021 Optimize
                for (activity in activities) {
                    result.add(UserActivity(user, activity))
                }

                _userActivities.value = result
            }
        }

        return _userActivities
    }
}
