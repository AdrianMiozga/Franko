package org.wentura.franko.ui.home

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
import org.wentura.franko.data.*
import javax.inject.Inject

@HiltViewModel
class UserActivityListViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val TAG = UserActivityListViewModel::class.simpleName
    }

    private val _userActivities = MutableLiveData<List<UserActivity>>()
    val userActivities: LiveData<List<UserActivity>> = _userActivities

    init {
        viewModelScope.launch {
            // TODO: 15.10.2021 Listener for new followings?
            val following = userRepository
                .getFollowing(getCurrentUserUid())
                .get()
                .await()

            val followingIds = ArrayList<String>()

            following?.forEach { user ->
                followingIds.add(user.id)
            }

            if (followingIds.isEmpty()) {
                _userActivities.value = ArrayList()
                return@launch
            }

            activityRepository
                .getActivities(followingIds)
                .orderBy(Constants.END_TIME, Query.Direction.DESCENDING)
                .addSnapshotListener { activitiesSnapshot, exception ->
                    viewModelScope.launch {
                        if (exception != null) {
                            Log.w(TAG, exception)
                            return@launch
                        }

                        if (activitiesSnapshot == null) return@launch

                        val activities: List<Activity> = ArrayList(activitiesSnapshot.toObjects())
                        val result: ArrayList<UserActivity> = ArrayList(activities.size)

                        // TODO: 28.09.2021 Optimize
                        for (activity in activities) {
                            if (activity.uid == null) continue

                            val userSnapshot = userRepository.getUser(activity.uid)
                                .get()
                                .await()

                            val user: User = userSnapshot.toObject() ?: continue

                            result.add(UserActivity(user, activity))
                        }

                        _userActivities.value = result
                    }
                }
        }
    }
}
