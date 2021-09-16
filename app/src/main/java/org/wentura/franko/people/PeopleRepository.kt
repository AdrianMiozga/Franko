package org.wentura.franko.people

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.wentura.franko.Constants
import javax.inject.Inject

// TODO: 16.09.2021 User UserRepository?
class PeopleRepository @Inject constructor() {

    private val db = Firebase.firestore

    suspend fun getPeople(): QuerySnapshot {
        return db.collection(Constants.USERS)
            .whereEqualTo(
                Constants.WHO_CAN_SEE_MY_PROFILE,
                Constants.EVERYONE
            )
            .get()
            .await()
    }
}
