package org.wentura.physicalapplication

object Constants {

    // Firestore Paths
    const val USERS = "users"
    const val PATHS = "paths"

    // Firestore Fields
    const val UID = "uid"
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val CITY = "city"
    const val PHOTO_URL = "photoUrl"

    // Storage Paths
    const val IMAGES = "images"

    // Setting Keys
    const val DARK_MODE_KEY = "darkMode"
    const val MILES_KEY = "miles"
    const val WHO_CAN_SEE_MY_PROFILE = "whoCanSeeMyProfile"

    // Providers
    const val PROVIDER_GOOGLE = "google.com"

    // Intent Request Codes
    const val PERMISSIONS_REQUEST_LOCATION = 1

    /** Meters per second to miles per hour **/
    const val MPH = 2.23694

    /** Meters per second to kilometers per hour **/
    const val KMH = 3.6
}
