package org.wentura.franko.data

import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import org.wentura.franko.Constants
import org.wentura.franko.Utilities.getCurrentUserUid
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    private val db = Firebase.firestore

    fun getUser(): DocumentReference {
        return db.collection(Constants.USERS).document(getCurrentUserUid())
    }

    fun getUser(uid: String): DocumentReference {
        return db.collection(Constants.USERS).document(uid)
    }

    suspend fun getPeople(): QuerySnapshot {
        return db.collection(Constants.USERS)
            .whereEqualTo(Constants.WHO_CAN_SEE_MY_PROFILE, Constants.EVERYONE)
            .get()
            .await()
    }

    fun getUsers(uidList: List<String>): Task<QuerySnapshot> {
        return db.collection(Constants.USERS).whereIn(FieldPath.documentId(), uidList).get()
    }

    fun getFollowing(uid: String): CollectionReference {
        return db.collection(Constants.USERS).document(uid).collection(Constants.FOLLOWING)
    }

    fun getFollowers(uid: String): CollectionReference {
        return db.collection(Constants.USERS).document(uid).collection(Constants.FOLLOWERS)
    }

    fun updateUser(updates: Map<String, Any>) {
        db.collection(Constants.USERS).document(getCurrentUserUid()).update(updates)
    }

    fun addNewUser(values: Map<String, Any>) {
        db.collection(Constants.USERS).document(getCurrentUserUid()).get().addOnSuccessListener {
            document ->
            if (document.exists()) return@addOnSuccessListener

            db.collection(Constants.USERS).document(getCurrentUserUid()).set(values)
        }
    }

    fun removeProfilePicture() {
        db.collection(Constants.USERS)
            .document(getCurrentUserUid())
            .update(Constants.PHOTO_URL, FieldValue.delete())

        val imageDirectory = Firebase.storage.reference.child(Constants.IMAGES)

        val imageName = "${getCurrentUserUid()}.${Constants.PROFILE_PICTURE_FORMAT_EXTENSION}"
        val profilePicture = imageDirectory.child(imageName)

        profilePicture.delete()
    }
}
