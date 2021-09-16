package org.wentura.franko.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
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

    fun getActivities(): CollectionReference {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
    }

    suspend fun getActivity(activityId: String): DocumentSnapshot {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .document(activityId)
            .get()
            .await()
    }

    fun deleteActivity(activityId: String): Task<Void> {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .document(activityId)
            .delete()
    }
}
