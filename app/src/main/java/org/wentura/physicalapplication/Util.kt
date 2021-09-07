package org.wentura.physicalapplication

import com.google.firebase.auth.FirebaseUser

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
    }
}
