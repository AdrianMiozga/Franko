package org.wentura.franko.profileedit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.wentura.franko.Constants
import org.wentura.franko.Utilities
import org.wentura.franko.Utilities.copyToFile
import org.wentura.franko.Utilities.getCurrentUserUid
import org.wentura.franko.Utilities.getUri
import java.io.File
import java.io.FileInputStream

class ProfilePictureObserver(
    private val context: Context,
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    private val db = Firebase.firestore
    private val uid = getCurrentUserUid()

    private val imageDirectory = Firebase.storage.reference.child(Constants.IMAGES)

    private val fileName = "$uid.${Constants.PROFILE_PICTURE_FORMAT_EXTENSION}"
    private val profilePicture = imageDirectory.child(fileName)

    private lateinit var tmpFile: File

    private lateinit var selectImage: ActivityResultLauncher<String>
    private lateinit var takeImage: ActivityResultLauncher<Uri>

    private lateinit var owner: LifecycleOwner

    companion object {
        val TAG = ProfilePictureObserver::class.simpleName
    }

    override fun onCreate(owner: LifecycleOwner) {
        this.owner = owner

        selectImage =
            registry.register(
                Constants.SELECT_IMAGE_KEY,
                owner,
                ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri == null) return@register

                owner.lifecycleScope.launchWhenCreated {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            tmpFile = Utilities.createTmpFile(
                                Constants.TMP_IMAGE_PREFIX,
                                Constants.TMP_IMAGE_SUFFIX,
                                context.cacheDir
                            )

                            inputStream.copyToFile(tmpFile)

                            updateProfilePicture()
                        }
                    }
                }
            }

        takeImage = registry.register(
            Constants.TAKE_PICTURE_KEY,
            owner,
            ActivityResultContracts.TakePicture()
        ) { isSuccess ->
            if (!isSuccess) return@register

            owner.lifecycleScope.launchWhenCreated {
                updateProfilePicture()
            }
        }
    }

    fun selectImage() = selectImage.launch("image/*")

    fun takePicture() {
        owner.lifecycleScope.launchWhenCreated {
            tmpFile = Utilities.createTmpFile(
                Constants.TMP_IMAGE_PREFIX,
                Constants.TMP_IMAGE_SUFFIX,
                context.cacheDir
            )

            takeImage.launch(tmpFile.getUri(context))
        }
    }

    private suspend fun updateProfilePicture() {
        withContext(Dispatchers.IO) {
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSLESS
            } else {
                @Suppress("Deprecation")
                Bitmap.CompressFormat.WEBP
            }

            val compressedImageFile = Compressor.compress(context, tmpFile) {
                default(width = 200, height = 200, format = format, quality = 100)
            }

            @Suppress("BlockingMethodInNonBlockingContext")
            val photoUrl = profilePicture
                .putStream(FileInputStream(compressedImageFile))
                .continueWithTask { profilePicture.downloadUrl }
                .await()

            val updates: Map<String, Any> =
                hashMapOf(Constants.PHOTO_URL to photoUrl.toString())

            db.collection(Constants.USERS)
                .document(uid)
                .update(updates)
        }
    }
}
