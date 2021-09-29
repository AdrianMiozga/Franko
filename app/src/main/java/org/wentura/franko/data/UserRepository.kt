package org.wentura.franko.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.wentura.franko.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val myUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun getUser(): DocumentReference {
        return db.collection(Constants.USERS)
            .document(myUid)
    }

    fun getUser(uid: String): DocumentReference {
        return db.collection(Constants.USERS)
            .document(uid)
    }

    fun getUsers(uids: ArrayList<String>): Task<QuerySnapshot> {
        return db.collection(Constants.USERS)
            .whereIn(FieldPath.documentId(), uids)
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
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        db.collection(Constants.USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) return@addOnSuccessListener

                db.collection(Constants.USERS)
                    .document(uid)
                    .set(values)
            }
    }

    fun removeProfilePicture() {
        db.collection(Constants.USERS)
            .document(myUid)
            .update(Constants.PHOTO_URL, FieldValue.delete())

        val imageDirectory = Firebase.storage.reference.child(Constants.IMAGES)
        val profilePicture = imageDirectory.child("$myUid.png")

        profilePicture.delete()
    }
}
