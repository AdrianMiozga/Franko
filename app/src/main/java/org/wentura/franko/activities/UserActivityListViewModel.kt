package org.wentura.franko.activities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.wentura.franko.Constants
import org.wentura.franko.Utilities.getCurrentUserUid
import org.wentura.franko.data.*
import javax.inject.Inject

@HiltViewModel
class UserActivityListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    companion object {
        val TAG = UserActivityListViewModel::class.simpleName
    }

    private val _userActivities = MutableLiveData<List<UserActivity>>()
    val userActivites: LiveData<List<UserActivity>> = _userActivities

    init {
        getCurrentActivities(listOf())
    }

    fun getCurrentActivities(activityTypes: List<String>): LiveData<List<UserActivity>> {
        viewModelScope.launch {
            val userSnapshot = userRepository
                .getUser()
                .get()
                .await()

            val user: User = userSnapshot.toObject() ?: return@launch

            var query = activityRepository
                .getActivities(getCurrentUserUid())
                .orderBy(Constants.END_TIME, Query.Direction.DESCENDING)

            if (activityTypes.isNotEmpty()) {
                query = query.whereIn(Constants.ACTIVITY, activityTypes)
            }

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
