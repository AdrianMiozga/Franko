package org.wentura.franko.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.wentura.franko.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepository @Inject constructor() {

    private val db = Firebase.firestore

    companion object {
        val TAG = ActivityRepository::class.simpleName
    }

    fun addActivity(activity: Activity) {
        db.collection(Constants.ACTIVITIES)
            .add(activity)
    }

    fun getActivities(uid: String): Query {
        return db.collection(Constants.ACTIVITIES)
            .whereEqualTo(Constants.UID, uid)
    }

    fun getActivities(uid: ArrayList<String>): Query {
        return db.collection(Constants.ACTIVITIES)
            .whereIn(Constants.UID, uid)
    }

    fun getActivity(activityId: String): DocumentReference {
        return db.collection(Constants.ACTIVITIES)
            .document(activityId)
    }

    fun deleteActivity(activityId: String): Task<Void> {
        return db.collection(Constants.ACTIVITIES)
            .document(activityId)
            .delete()
    }
}
