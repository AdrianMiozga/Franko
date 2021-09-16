package org.wentura.franko

import android.graphics.Color
import java.util.concurrent.TimeUnit

object Constants {

    // Firestore Paths
    const val USERS = "users"
    const val PATHS = "paths"
    const val FOLLOWING = "following"

    // Firestore Fields
    const val UID = "uid"
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val BIO = "bio"
    const val LAST_ACTIVITY = "lastActivity"
    const val CITY = "city"
    const val PHOTO_URL = "photoUrl"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val ACTIVITY = "activity"
    const val END_TIME = "endTime"

    // Firestore Values
    const val IMPERIAL = "Imperial"
    const val METRIC = "Metric"
    const val EVERYONE = "Everyone"

    // Storage Paths
    const val IMAGES = "images"

    // Setting Keys
    const val DARK_MODE_KEY = "darkMode"
    const val UNITS_OF_MEASURE_KEY = "unitsOfMeasure"
    const val WHO_CAN_SEE_MY_PROFILE = "whoCanSeeMyProfile"
    const val WHO_CAN_SEE_MY_LOCATION = "whoCanSeeMyLocation"
    const val WHO_CAN_SEE_MY_FOLLOWING_COUNT = "whoCanSeeMyFollowingCount"
    const val ACTIVITY_TYPE_KEY = ACTIVITY
    const val WHO_CAN_SEE_THIS_ACTIVITY = "whoCanSeeThisActivity"

    // Map Settings
    const val DEFAULT_ZOOM = 17F
    const val LINE_WIDTH = 50F
    const val LINE_COLOR = Color.BLUE

    // Notification
    const val ACTIVITY_TRACKING_NOTIFICATION_ID = 1
    const val ACTIVITY_RECORDING_NOTIFICATION_CHANNEL_ID = "activity_recording_notification_channel"

    // Providers
    const val PROVIDER_GOOGLE = "google.com"

    // Intent Request Codes
    const val PERMISSIONS_REQUEST_LOCATION = 1

    // Result Registry Keys
    const val SELECT_IMAGE_KEY = "selectImageKey"
    const val TAKE_PICTURE_KEY = "takePictureKey"

    // Other
    /** Min activity time to save in milliseconds **/
    val MIN_ACTIVITY_TIME = TimeUnit.MINUTES.toMillis(1)

    /** Meters per second to miles per hour **/
    const val MPH = 2.23694

    /** Meters per second to kilometers per hour **/
    const val KMH = 3.6
}
