package org.wentura.franko.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.wentura.franko.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    companion object {
        val TAG = ActivityRepository::class.simpleName
    }

    fun addActivity(activity: Path) {
        db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .add(activity)
    }

    fun getActivities(): CollectionReference {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
    }

    fun getActivity(activityId: String): DocumentReference {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .document(activityId)
    }

    fun deleteActivity(activityId: String): Task<Void> {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .document(activityId)
            .delete()
    }

    fun updateActivityType(activityId: String, activityType: String) {
        db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .document(activityId)
            .update(Constants.ACTIVITY, activityType)
    }
}
