package org.wentura.franko

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseUser
import java.io.ByteArrayOutputStream

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

    fun Bitmap.convertToByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)

        val byteArray: ByteArray = stream.toByteArray()
        this.recycle()

        return byteArray
    }

    fun closeKeyboard(view: View) {
        val inputMethodManager = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isLocationPermissionGranted(context: Context?): Boolean {
        return if (context == null) {
            false
        } else
            ContextCompat.checkSelfPermission(
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
