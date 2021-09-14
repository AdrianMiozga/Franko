package org.wentura.franko.data

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

    fun getPaths(): CollectionReference {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
    }

    suspend fun getPath(pathId: String): DocumentSnapshot {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .document(pathId)
            .get()
            .await()
    }
}
