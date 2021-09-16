package org.wentura.franko.profileedit

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import org.wentura.franko.Constants
import org.wentura.franko.Util.Companion.convertToByteArray

class ProfilePictureObserver(
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val imageDirectory = Firebase.storage.reference.child(Constants.IMAGES)
    private val profilePicture = imageDirectory.child("$uid.png")

    private lateinit var selectImage: ActivityResultLauncher<String>
    private lateinit var takeImage: ActivityResultLauncher<Void>

    override fun onCreate(owner: LifecycleOwner) {
        selectImage =
            registry.register(
                Constants.SELECT_IMAGE_KEY, owner, ActivityResultContracts.GetContent()
            ) { uri ->
                updateProfilePicture(uri)
            }

        takeImage = registry.register(
            Constants.TAKE_PICTURE_KEY, owner, ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            updateProfilePicture(bitmap)
        }
    }

    fun selectImage() {
        selectImage.launch("image/*")
    }

    fun takePicture() {
        takeImage.launch(null)
    }

    private fun updateProfilePicture(bitmap: Bitmap) {
        val uploadTask = profilePicture.putBytes(bitmap.convertToByteArray())
        result(uploadTask)
    }

    private fun updateProfilePicture(uri: Uri) {
        val uploadTask = profilePicture.putFile(uri)
        result(uploadTask)
    }

    private fun result(uploadTask: UploadTask) {
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            profilePicture.downloadUrl
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
