package org.wentura.franko

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.wentura.franko.profileedit.ProfilePictureObserver
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object Utilities {
    /**
     * Extract Google profile picture in original quality.
     *
     * @param [url] to extract
     * @return extracted url
     */
    fun extractGoogleProfilePicture(url: String): String {
        return url.removeSuffix("=s96-c")
    }

    fun extractPhotoUrl(user: FirebaseUser): String {
        var photoUrl = user.photoUrl.toString()

        for (profile in user.providerData) {
            if (profile.providerId == Constants.PROVIDER_GOOGLE) {
                photoUrl = extractGoogleProfilePicture(photoUrl)
            }
        }

        return photoUrl
    }

    fun InputStream.copyToFile(outputFile: File) {
        this.use { input ->
            val outputStream = FileOutputStream(outputFile)

            outputStream.use { output ->
                val bufferSize = ByteArray(4 * 1024)

                while (true) {
                    val byteCount = input.read(bufferSize)

                    if (byteCount < 0) break

                    output.write(bufferSize, 0, byteCount)
                }

                output.flush()
            }
        }
    }

    fun File.getUri(context: Context): Uri {
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            this
        )
    }

    suspend fun createTmpFile(prefix: String, suffix: String, directory: File): File {
        val file: File

        withContext(Dispatchers.IO) {
            Log.d(ProfilePictureObserver.TAG, "createTmpFile: ")

            @Suppress("BlockingMethodInNonBlockingContext")
            file = File.createTempFile(
                prefix,
                suffix,
                directory
            ).apply {
                createNewFile()
                deleteOnExit()
            }
        }

        return file
    }

    fun closeKeyboard(view: View) {
        val inputMethodManager = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isLocationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun loadProfilePicture(photoUrl: String?, imageView: ImageView) {
        if (photoUrl.isNullOrBlank()) {
            imageView.load(R.drawable.ic_profile_picture_placeholder) {
                transformations(CircleCropTransformation())
            }
        } else {
            imageView.load(photoUrl) {
                transformations(CircleCropTransformation())
            }
        }
    }
}
