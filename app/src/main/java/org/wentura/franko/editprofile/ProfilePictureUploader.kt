package org.wentura.franko.editprofile

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import org.wentura.franko.Constants
import org.wentura.franko.Util.Companion.convertToByteArray

class ProfilePictureUploader {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val imagesRef = Firebase.storage.reference.child(Constants.IMAGES)
    private val thisImageRef = imagesRef.child("$uid.png")

    private val db = Firebase.firestore

    companion object {
        val TAG = ProfilePictureUploader::class.simpleName
    }

    fun updateProfilePicture(bitmap: Bitmap) {
        val uploadTask = thisImageRef.putBytes(bitmap.convertToByteArray())
        result(uploadTask)
    }

    fun updateProfilePicture(uri: Uri) {
        val uploadTask = thisImageRef.putFile(uri)
        result(uploadTask)
    }

    private fun result(uploadTask: UploadTask) {
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            thisImageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updates: Map<String, Any> =
                    hashMapOf(Constants.PHOTO_URL to task.result.toString())

                db.collection(Constants.USERS)
                    .document(uid)
                    .update(updates)
            }
        }
    }
}
