package org.wentura.franko.activities

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.wentura.franko.Constants
import javax.inject.Inject

class ActivityRepository @Inject constructor() {

    private val db = Firebase.firestore

    suspend fun getPaths(): QuerySnapshot {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .get()
            .await()
    }
}
