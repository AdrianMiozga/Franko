package org.wentura.franko.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.wentura.franko.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun getUser(): DocumentReference {
        return db.collection(Constants.USERS)
            .document(uid)
    }

    fun updateUser(updates: HashMap<String, Any>) {
        db.collection(Constants.USERS)
            .document(uid)
            .update(updates)
    }

    fun removeProfilePicture() {
        db.collection(Constants.USERS)
            .document(uid)
            .update(Constants.PHOTO_URL, FieldValue.delete())

        val imageDirectory = Firebase.storage.reference.child(Constants.IMAGES)
        val profilePicture = imageDirectory.child("$uid.png")

        profilePicture.delete()
    }
}
