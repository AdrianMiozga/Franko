package org.wentura.franko.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.wentura.franko.Constants
import javax.inject.Inject

class ProfileRepository @Inject constructor() {

    private val myUid = FirebaseAuth.getInstance().currentUser?.uid
    private val db = Firebase.firestore

    suspend fun getProfile(uid: String): DocumentSnapshot {
        return db.collection(Constants.USERS)
            .document(uid)
            .get()
            .await()
    }

    suspend fun getFollowing(): QuerySnapshot? {
        return if (myUid == null) null else db.collection(Constants.USERS)
            .document(myUid)
            .collection(Constants.FOLLOWING)
            .get()
            .await()
    }
}
