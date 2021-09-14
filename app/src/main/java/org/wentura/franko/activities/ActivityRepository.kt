package org.wentura.franko.activities

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.wentura.franko.Constants
import javax.inject.Inject

class ActivityRepository @Inject constructor() {

    private val db = Firebase.firestore

    companion object {
        val TAG = ActivityRepository::class.simpleName
    }

    fun getPaths(): CollectionReference {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
    }
}
