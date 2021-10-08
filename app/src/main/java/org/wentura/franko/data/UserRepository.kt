package org.wentura.franko.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import org.wentura.franko.Constants
import org.wentura.franko.Utilities.getCurrentUserUid
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val myUid: String by lazy { getCurrentUserUid() }

    fun getUser(): DocumentReference {
        return db.collection(Constants.USERS)
            .document(myUid)
    }

    fun getUser(uid: String): DocumentReference {
        return db.collection(Constants.USERS)
            .document(uid)
    }

    suspend fun getPeople(): QuerySnapshot {
        return db.collection(Constants.USERS)
            .whereEqualTo(
                Constants.WHO_CAN_SEE_MY_PROFILE,
                Constants.EVERYONE
            )
            .get()
            .await()
    }

    fun getUsers(uidList: ArrayList<String>): Task<QuerySnapshot> {
        return db.collection(Constants.USERS)
            .whereIn(FieldPath.documentId(), uidList)
            .get()
    }

    fun getFollowing(uid: String): CollectionReference {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.FOLLOWING)
    }

    fun getFollowers(uid: String): CollectionReference {
        return db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.FOLLOWERS)
    }

    fun updateUser(updates: HashMap<String, Any>) {
        db.collection(Constants.USERS)
            .document(myUid)
            .update(updates)
    }

    fun addNewUser(values: HashMap<String, Any>) {
        db.collection(Constants.USERS)
            .document(myUid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) return@addOnSuccessListener

                db.collection(Constants.USERS)
                    .document(myUid)
                    .set(values)
            }
    }

    fun removeProfilePicture() {
        db.collection(Constants.USERS)
            .document(myUid)
            .update(Constants.PHOTO_URL, FieldValue.delete())

        val imageDirectory = Firebase.storage.reference.child(Constants.IMAGES)

        val imageName = "$myUid.${Constants.PROFILE_PICTURE_FORMAT_EXTENSION}"
        val profilePicture = imageDirectory.child(imageName)

        profilePicture.delete()
    }
}
