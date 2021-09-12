package org.wentura.franko

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseUser
import java.io.ByteArrayOutputStream


class Util {

    companion object {
        /**
         * Extract Google profile picture in original quality.
         *
         * @param url to extract
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
    }
}
