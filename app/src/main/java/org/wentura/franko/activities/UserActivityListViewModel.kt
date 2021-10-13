package org.wentura.franko.activities

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

    private val userActivities = MutableLiveData<List<UserActivity>>()

    fun getCurrentActivities(activityTypes: List<String>): LiveData<List<UserActivity>> {
        viewModelScope.launch {
            if (activityTypes.isEmpty()) {
                userActivities.value = listOf()
                return@launch
            }

            val userSnapshot = userRepository
                .getUser()
                .get()
                .await()

            val user: User = userSnapshot.toObject() ?: return@launch

            val activitiesSnapshot = activityRepository
                .getActivities(getCurrentUserUid())
                .whereIn(Constants.ACTIVITY, activityTypes)
                .orderBy(Constants.END_TIME, Query.Direction.DESCENDING)
                .get()
                .await()

            val activities: List<Activity> = ArrayList(activitiesSnapshot.toObjects())
            val result: ArrayList<UserActivity> = ArrayList(activities.size)

            // TODO: 28.09.2021 Optimize
            for (activity in activities) {
                result.add(UserActivity(user, activity))
            }

            userActivities.value = result
        }

        return userActivities
    }
}
