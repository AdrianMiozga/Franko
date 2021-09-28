package org.wentura.franko.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wentura.franko.Constants
import org.wentura.franko.data.Activity
import org.wentura.franko.data.ActivityRepository
import javax.inject.Inject

@HiltViewModel
class ActivityListViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    companion object {
        val TAG = ActivityListViewModel::class.simpleName
    }

    private val activities = MutableLiveData<ArrayList<Activity>>()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun getCurrentActivities(activityTypes: ArrayList<String>): LiveData<ArrayList<Activity>> {
        if (activityTypes.isEmpty()) {
            activities.value = ArrayList()
            return activities
        }

        activityRepository
            .getActivities(uid)
            .whereIn(Constants.ACTIVITY, activityTypes)
            .orderBy(Constants.END_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                activities.value = ArrayList(querySnapshot.toObjects())
            }

        return activities
    }
}
